/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.template.java;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateTranslator;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.TemplateVariableType;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameMatch;

import org.eclipse.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.template.java.CompilationUnitCompletion.Variable;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Strings;
import org.eclipse.jdt.internal.corext.util.TypeNameMatchCollector;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.PreferenceConstants;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.text.correction.ASTResolving;
import org.eclipse.jdt.internal.ui.text.correction.SimilarElementsRequestor;
import org.eclipse.jdt.internal.ui.text.template.contentassist.MultiVariable;
import org.eclipse.jdt.internal.ui.text.template.contentassist.MultiVariableGuess;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;

/**
 * A context for java source.
 */
public class JavaContext extends CompilationUnitContext {

	/** A code completion requester for guessing local variable names. */
	private CompilationUnitCompletion fCompletion;
	/**
	 * The list of used local names.
	 * @since 3.3
	 */
	private Set fUsedNames= new HashSet();
	private Map fVariables= new HashMap();
	
	/**
	 * Creates a java template context.
	 * 
	 * @param type   the context type.
	 * @param document the document.
	 * @param completionOffset the completion offset within the document.
	 * @param completionLength the completion length.
	 * @param compilationUnit the compilation unit (may be <code>null</code>).
	 */
	public JavaContext(TemplateContextType type, IDocument document, int completionOffset, int completionLength, ICompilationUnit compilationUnit) {
		super(type, document, completionOffset, completionLength, compilationUnit);
	}
	
	/**
	 * Creates a java template context.
	 * 
	 * @param type   the context type.
	 * @param document the document.
	 * @param completionPosition the position defining the completion offset and length 
	 * @param compilationUnit the compilation unit (may be <code>null</code>).
	 * @since 3.2
	 */
	public JavaContext(TemplateContextType type, IDocument document, Position completionPosition, ICompilationUnit compilationUnit) {
		super(type, document, completionPosition, compilationUnit);
	}
	
	/**
	 * Returns the indentation level at the position of code completion.
	 * 
	 * @return the indentation level at the position of the code completion
	 */
	private int getIndentation() {
		int start= getStart();
		IDocument document= getDocument();
		try {
			IRegion region= document.getLineInformationOfOffset(start);
			String lineContent= document.get(region.getOffset(), region.getLength());
			IJavaProject project= getJavaProject();
			return Strings.computeIndentUnits(lineContent, project);
		} catch (BadLocationException e) {
			return 0;
		}
	}

	/*
	 * @see TemplateContext#evaluate(Template template)
	 */
	public TemplateBuffer evaluate(Template template) throws BadLocationException, TemplateException {
		clear();
		
		if (!canEvaluate(template))
			throw new TemplateException(JavaTemplateMessages.Context_error_cannot_evaluate); 
		
		TemplateTranslator translator= new TemplateTranslator() {
			protected TemplateVariable createVariable(TemplateVariableType type, String name, int[] offsets) {
//				TemplateVariableResolver resolver= getContextType().getResolver(type.getName());
//				return resolver.createVariable();
				
				MultiVariable variable= new JavaVariable(type, name, offsets);
				fVariables.put(name, variable);
				return variable;
			}
		};
		TemplateBuffer buffer= translator.translate(template);

		getContextType().resolve(buffer, this);

		IPreferenceStore prefs= JavaPlugin.getDefault().getPreferenceStore();
		boolean useCodeFormatter= prefs.getBoolean(PreferenceConstants.TEMPLATES_USE_CODEFORMATTER);

		IJavaProject project= getJavaProject();
		JavaFormatter formatter= new JavaFormatter(TextUtilities.getDefaultLineDelimiter(getDocument()), getIndentation(), useCodeFormatter, project);
		formatter.format(buffer, this);
		
		clear();

		return buffer;
	}

	private void clear() {
		fUsedNames.clear();
	}
	
	/*
	 * @see TemplateContext#canEvaluate(Template templates)
	 */
	public boolean canEvaluate(Template template) {
		if (fForceEvaluation)
			return true;

		String key= getKey();
		return
			template.matches(key, getContextType().getId()) &&
			key.length() != 0 && template.getName().toLowerCase().startsWith(key.toLowerCase());
	}

	/*
	 * @see DocumentTemplateContext#getCompletionPosition();
	 */
	public int getStart() {

		if (fIsManaged && getCompletionLength() > 0)
			return super.getStart();
		
		try {
			IDocument document= getDocument();

			int start= getCompletionOffset();
			int end= getCompletionOffset() + getCompletionLength();
			
			while (start != 0 && Character.isUnicodeIdentifierPart(document.getChar(start - 1)))
				start--;
			
			while (start != end && Character.isWhitespace(document.getChar(start)))
				start++;
			
			if (start == end)
				start= getCompletionOffset();	
			
				return start;	

		} catch (BadLocationException e) {
			return super.getStart();	
		}
	}

	/*
	 * @see org.eclipse.jdt.internal.corext.template.DocumentTemplateContext#getEnd()
	 */
	public int getEnd() {
		
		if (fIsManaged || getCompletionLength() == 0)
			return super.getEnd();

		try {			
			IDocument document= getDocument();

			int start= getCompletionOffset();
			int end= getCompletionOffset() + getCompletionLength();
			
			while (start != end && Character.isWhitespace(document.getChar(end - 1)))
				end--;
			
			return end;	

		} catch (BadLocationException e) {
			return super.getEnd();
		}		
	}

	/*
	 * @see org.eclipse.jdt.internal.corext.template.DocumentTemplateContext#getKey()
	 */
	public String getKey() {

		if (getCompletionLength() == 0)		
			return super.getKey();

		try {
			IDocument document= getDocument();

			int start= getStart();
			int end= getCompletionOffset();
			return start <= end
				? document.get(start, end - start)
				: ""; //$NON-NLS-1$
			
		} catch (BadLocationException e) {
			return super.getKey();			
		}
	}

	/**
	 * Returns the character before the start position of the completion.
	 * 
	 * @return the character before the start position of the completion
	 */
	public char getCharacterBeforeStart() {
		int start= getStart();
		
		try {
			return start == 0
				? ' '
				: getDocument().getChar(start - 1);

		} catch (BadLocationException e) {
			return ' ';
		}
	}

	private static void handleException(Shell shell, Exception e) {
		String title= JavaTemplateMessages.JavaContext_error_title; 
		if (e instanceof CoreException)
			ExceptionHandler.handle((CoreException)e, shell, title, null);
		else if (e instanceof InvocationTargetException)
			ExceptionHandler.handle((InvocationTargetException)e, shell, title, null);
		else {
			JavaPlugin.log(e);
			MessageDialog.openError(shell, title, e.getMessage());
		}
	}	

	private CompilationUnitCompletion getCompletion() {
		ICompilationUnit compilationUnit= getCompilationUnit();
		if (fCompletion == null) {
			fCompletion= new CompilationUnitCompletion(compilationUnit);
			
			if (compilationUnit != null) {
				try {
					compilationUnit.codeComplete(getStart(), fCompletion);
				} catch (JavaModelException e) {
					// ignore
				}
			}
		}
		
		return fCompletion;
	}

	/**
	 * Returns the names of local arrays.
	 * 
	 * @return the names of local arrays
	 */
	public Variable[] getArrays() {
		Variable[] localArrays= getCompletion().findLocalArrays();
		arrange(localArrays);
		return localArrays;
	}
	
	/**
	 * Sorts already used locals behind any that are not yet used.
	 * 
	 * @param variables the variables to sort
	 * @since 3.3
	 */
	private void arrange(Variable[] variables) {
		Arrays.sort(variables, new Comparator() {
			public int compare(Object o1, Object o2) {
				return rank((Variable) o1) - rank((Variable) o2);
			}
			
			private int rank(Variable l) {
				return fUsedNames.contains(l.getName()) ? 1 : 0;
			}
		});
	}

	/**
	 * Returns the names of local variables matching <code>type</code>.
	 * 
	 * @return the names of local variables matching <code>type</code>
	 * @since 3.3
	 */
	public Variable[] getLocalVariables(String type) {
		Variable[] localVariables= getCompletion().findLocalVariables(type);
		arrange(localVariables);
		return localVariables;
	}
	
	/**
	 * Returns the names of fields matching <code>type</code>.
	 * 
	 * @return the names of fields matching <code>type</code>
	 * @since 3.3
	 */
	public Variable[] getFields(String type) {
		Variable[] fields= getCompletion().findFieldVariables(type);
		arrange(fields);
		return fields;
	}
	
	/**
	 * Returns the names of local iterables or arrays.
	 * 
	 * @return the names of local iterables or arrays
	 */
	public Variable[] getIterables() {
		Variable[] iterables= getCompletion().findLocalIterables();
		arrange(iterables);
		return iterables;
	}
	
	public void markAsUsed(String name) {
		fUsedNames.add(name);
	}

	public String[] suggestVariableNames(String type) throws IllegalArgumentException {
		String[] excludes= computeExcludes();
		// TODO erasure, arrays, etc.
		String[] result= suggestVariableName(type, excludes);
		return result;
	}
	
	private String[] computeExcludes() {
		String[] excludes= getCompletion().getLocalVariableNames();
		if (!fUsedNames.isEmpty()) {
			String[] allExcludes= new String[fUsedNames.size() + excludes.length];
			System.arraycopy(excludes, 0, allExcludes, 0, excludes.length);
			System.arraycopy(fUsedNames.toArray(), 0, allExcludes, 0, fUsedNames.size());
			excludes= allExcludes;
		}
		return excludes;
	}

	private String[] suggestVariableName(String type, String[] excludes) throws IllegalArgumentException {
		int dim=0;
		while (type.endsWith("[]")) {//$NON-NLS-1$
			dim++;
			type= type.substring(0, type.length() - 2);
		}
		
		IJavaProject project= getJavaProject();
		if (project != null)
			return StubUtility.getVariableNameSuggestions(StubUtility.LOCAL, project, type, dim, Arrays.asList(excludes), true);
		
		// fallback if we lack proper context: roll-our own lowercasing
		return new String[] {Signature.getSimpleName(type).toLowerCase()};
	}
	
	public void addImport(String type) {
		if (isReadOnly())
			return;
		
		ICompilationUnit cu= getCompilationUnit();
		if (cu == null)
			return;

		try {
			boolean qualified= type.indexOf('.') != -1;
			if (!qualified) {
				IJavaSearchScope searchScope= SearchEngine.createJavaSearchScope(new IJavaElement[] { cu.getJavaProject() });
				SimpleName nameNode= null;
				TypeNameMatch[] matches= findAllTypes(type, searchScope, nameNode, null, cu);
				if (matches.length != 1) // only add import if we have a single match
					return;
				type= matches[0].getFullyQualifiedName();
			}
			
			Position position= new Position(getCompletionOffset(), getCompletionLength());
			IDocument document= getDocument();
			final String category= "__template_position_importer" + System.currentTimeMillis(); //$NON-NLS-1$
			IPositionUpdater updater= new DefaultPositionUpdater(category);
			document.addPositionCategory(category);
			document.addPositionUpdater(updater);
			document.addPosition(position);

			try {
				
				ImportRewrite rewrite= StubUtility.createImportRewrite(cu, true);
				CompilationUnit root= getASTRoot(cu);
				ImportRewriteContext context;
				if (root == null)
					context= null;
				else
					context= new ContextSensitiveImportRewriteContext(root, getCompletionOffset(), rewrite);
				rewrite.addImport(type, context);
				JavaModelUtil.applyEdit(cu, rewrite.rewriteImports(null), false, null);
				
				setCompletionOffset(position.getOffset());
				setCompletionLength(position.getLength());
				
			} catch (CoreException e) {
				handleException(null, e);
			} finally {
				document.removePosition(position);
				document.removePositionUpdater(updater);
				document.removePositionCategory(category);
			}
			
		} catch (BadLocationException e) {
			handleException(null, e);
		} catch (BadPositionCategoryException e) {
			handleException(null, e);
		} catch (JavaModelException e) {
			handleException(null, e);
		}
	}
	
	private CompilationUnit getASTRoot(ICompilationUnit compilationUnit) {
		return JavaPlugin.getDefault().getASTProvider().getAST(compilationUnit, ASTProvider.WAIT_NO, new NullProgressMonitor());
	}
	
	/*
	 * Finds a type by the simple name. From AddImportsOperation
	 */
	private TypeNameMatch[] findAllTypes(String simpleTypeName, IJavaSearchScope searchScope, SimpleName nameNode, IProgressMonitor monitor, ICompilationUnit cu) throws JavaModelException {
		boolean is50OrHigher= JavaModelUtil.is50OrHigher(cu.getJavaProject());
		
		int typeKinds= SimilarElementsRequestor.ALL_TYPES;
		if (nameNode != null) {
			typeKinds= ASTResolving.getPossibleTypeKinds(nameNode, is50OrHigher);
		}
		
		ArrayList typeInfos= new ArrayList();
		TypeNameMatchCollector requestor= new TypeNameMatchCollector(typeInfos);
		new SearchEngine().searchAllTypeNames(null, 0, simpleTypeName.toCharArray(), SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE, getSearchForConstant(typeKinds), searchScope, requestor, IJavaSearchConstants.FORCE_IMMEDIATE_SEARCH, monitor);

		ArrayList typeRefsFound= new ArrayList(typeInfos.size());
		for (int i= 0, len= typeInfos.size(); i < len; i++) {
			TypeNameMatch curr= (TypeNameMatch) typeInfos.get(i);
			if (curr.getPackageName().length() > 0) { // do not suggest imports from the default package
				if (isOfKind(curr, typeKinds, is50OrHigher) && isVisible(curr, cu)) {
					typeRefsFound.add(curr);
				}
			}
		}
		return (TypeNameMatch[]) typeRefsFound.toArray(new TypeNameMatch[typeRefsFound.size()]);
	}
	
	private int getSearchForConstant(int typeKinds) {
		final int CLASSES= SimilarElementsRequestor.CLASSES;
		final int INTERFACES= SimilarElementsRequestor.INTERFACES;
		final int ENUMS= SimilarElementsRequestor.ENUMS;
		final int ANNOTATIONS= SimilarElementsRequestor.ANNOTATIONS;

		switch (typeKinds & (CLASSES | INTERFACES | ENUMS | ANNOTATIONS)) {
			case CLASSES: return IJavaSearchConstants.CLASS;
			case INTERFACES: return IJavaSearchConstants.INTERFACE;
			case ENUMS: return IJavaSearchConstants.ENUM;
			case ANNOTATIONS: return IJavaSearchConstants.ANNOTATION_TYPE;
			case CLASSES | INTERFACES: return IJavaSearchConstants.CLASS_AND_INTERFACE;
			case CLASSES | ENUMS: return IJavaSearchConstants.CLASS_AND_ENUM;
			default: return IJavaSearchConstants.TYPE;
		}
	}
	
	private boolean isOfKind(TypeNameMatch curr, int typeKinds, boolean is50OrHigher) {
		int flags= curr.getModifiers();
		if (Flags.isAnnotation(flags)) {
			return is50OrHigher && ((typeKinds & SimilarElementsRequestor.ANNOTATIONS) != 0);
		}
		if (Flags.isEnum(flags)) {
			return is50OrHigher && ((typeKinds & SimilarElementsRequestor.ENUMS) != 0);
		}
		if (Flags.isInterface(flags)) {
			return (typeKinds & SimilarElementsRequestor.INTERFACES) != 0;
		}
		return (typeKinds & SimilarElementsRequestor.CLASSES) != 0;
	}

	
	private boolean isVisible(TypeNameMatch curr, ICompilationUnit cu) {
		int flags= curr.getModifiers();
		if (Flags.isPrivate(flags)) {
			return false;
		}
		if (Flags.isPublic(flags) || Flags.isProtected(flags)) {
			return true;
		}
		return curr.getPackageName().equals(cu.getParent().getElementName());
	}
	
	/**
	 * Evaluates a 'java' template in the context of a compilation unit
	 * 
	 * @param template the template to be evaluated
	 * @param compilationUnit the compilation unit in which to evaluate the template
	 * @param position the position inside the compilation unit for which to evaluate the template
	 * @return the evaluated template
	 * @throws CoreException in case the template is of an unknown context type
	 * @throws BadLocationException in case the position is invalid in the compilation unit
	 * @throws TemplateException in case the evaluation fails
	 */
	public static String evaluateTemplate(Template template, ICompilationUnit compilationUnit, int position) throws CoreException, BadLocationException, TemplateException {

		TemplateContextType contextType= JavaPlugin.getDefault().getTemplateContextRegistry().getContextType(JavaContextType.NAME);
		if (contextType == null)
			throw new CoreException(new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IStatus.ERROR, JavaTemplateMessages.JavaContext_error_message, null)); 

		IDocument document= new Document();
		if (compilationUnit != null && compilationUnit.exists())
			document.set(compilationUnit.getSource());

		JavaContext context= new JavaContext(contextType, document, position, 0, compilationUnit);
		context.setForceEvaluation(true);

		TemplateBuffer buffer= context.evaluate(template);
		if (buffer == null)
			return null;
		return buffer.getString();
	}

	TemplateVariable getTemplateVariable(String name) {
		TemplateVariable variable= (TemplateVariable) fVariables.get(name);
		if (variable != null && !variable.isResolved())
			getContextType().resolve(variable, this);
		return variable;
	}

	/**
	 * Adds a multi-variable guess dependency.
	 * 
	 * @param master the master variable - <code>slave</code> needs to be updated when
	 *        <code>master</code> changes
	 * @param slave the dependent variable
	 * @since 3.3
	 */
	public void addDependency(MultiVariable master, MultiVariable slave) {
		MultiVariableGuess guess= getMultiVariableGuess();
		if (guess == null) {
			guess= new MultiVariableGuess();
			setMultiVariableGuess(guess);
		}
		
		guess.addDependency(master, slave);
	}

}
