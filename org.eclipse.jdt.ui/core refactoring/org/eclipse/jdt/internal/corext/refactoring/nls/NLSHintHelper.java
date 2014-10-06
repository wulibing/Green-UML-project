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
package org.eclipse.jdt.internal.corext.refactoring.nls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jdt.internal.corext.dom.NodeFinder;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;

public class NLSHintHelper {

	private NLSHintHelper() {
	}

	/**
	 * Returns the accessor binding info or <code>null</code> if this element is not a nls'ed entry
	 */
	public static AccessorClassReference getAccessorClassReference(CompilationUnit astRoot, NLSElement nlsElement) {
		IRegion region= nlsElement.getPosition();
		return getAccessorClassReference(astRoot, region);
	}
	
	/**
	 * Returns the accessor binding info or <code>null</code> if this element is not a nls'ed entry
	 */
	public static AccessorClassReference getAccessorClassReference(CompilationUnit astRoot, IRegion region) {
		ASTNode nlsStringLiteral= NodeFinder.perform(astRoot, region.getOffset(), region.getLength());
		if (nlsStringLiteral == null) {
			return null; // not found
		}
		ASTNode parent= nlsStringLiteral.getParent();
		
		ITypeBinding accessorBinding= null;
		if (parent instanceof MethodInvocation) {
			MethodInvocation methodInvocation= (MethodInvocation) parent;
			List args= methodInvocation.arguments();
			if (args.indexOf(nlsStringLiteral) != 0) {
				return null; // must be first argument in lookup method
			}
			
			Expression firstArgument= (Expression)args.get(0);
			ITypeBinding argumentBinding= firstArgument.resolveTypeBinding();
			if (argumentBinding == null || !argumentBinding.getQualifiedName().equals("java.lang.String")) { //$NON-NLS-1$
				return null;
			}
			
			ITypeBinding typeBinding= methodInvocation.resolveTypeBinding();
			if (typeBinding == null || !typeBinding.getQualifiedName().equals("java.lang.String")) { //$NON-NLS-1$
				return null;
			}
			
			IMethodBinding methodBinding= methodInvocation.resolveMethodBinding();
			if (methodBinding == null || !Modifier.isStatic(methodBinding.getModifiers())) {
				return null; // only static methods qualify
			}
	
			accessorBinding= methodBinding.getDeclaringClass();
		} else if (parent instanceof QualifiedName) {
			QualifiedName name= (QualifiedName)parent;
			IBinding binding= name.resolveBinding();
			if (!(binding instanceof IVariableBinding))
				return null;
			
			IVariableBinding variableBinding= (IVariableBinding)binding;
			if (!Modifier.isStatic(variableBinding.getModifiers()))
				return null;
			
			accessorBinding= variableBinding.getDeclaringClass();
		} else if (parent instanceof VariableDeclarationFragment) {
			VariableDeclarationFragment decl= (VariableDeclarationFragment)parent;
			if (decl.getInitializer() != null)
				return null;
			
			IBinding binding= decl.resolveBinding();
			if (!(binding instanceof IVariableBinding))
				return null;
			
			IVariableBinding variableBinding= (IVariableBinding)binding;
			if (!Modifier.isStatic(variableBinding.getModifiers()))
				return null;
			
			if (!Modifier.isPublic(variableBinding.getModifiers()))
				return null;
			
			accessorBinding= variableBinding.getDeclaringClass();
		}
		if (accessorBinding == null)
			return null;
		
		String resourceBundleName;
		try {
			resourceBundleName= getResourceBundleName(accessorBinding);
		} catch (JavaModelException e) {
			return null;
		}
		
		if (resourceBundleName != null)
			return new AccessorClassReference(accessorBinding, resourceBundleName, new Region(parent.getStartPosition(), parent.getLength()));

		return null;
	}
	
	public static IPackageFragment getPackageOfAccessorClass(IJavaProject javaProject, ITypeBinding accessorBinding) throws JavaModelException {
		if (accessorBinding != null) {
			ICompilationUnit unit= Bindings.findCompilationUnit(accessorBinding, javaProject);
			if (unit != null) {
				return (IPackageFragment) unit.getParent();
			}
		}
		return null;
	}

	public static String getResourceBundleName(ITypeBinding accessorClassBinding) throws JavaModelException {
		IJavaElement je= accessorClassBinding.getJavaElement();
		if (je == null)
			return null;
		
		IOpenable openable= je.getOpenable();
		IJavaElement container= null;
		if (openable instanceof ICompilationUnit)
			container= (ICompilationUnit)openable;
		else if (openable instanceof IClassFile)
			container= (IClassFile)openable;
		else
			Assert.isLegal(false);
		CompilationUnit astRoot= JavaPlugin.getDefault().getASTProvider().getAST(container, ASTProvider.WAIT_YES, null);
	
		return getResourceBundleName(astRoot);
	}
	
	public static String getResourceBundleName(ICompilationUnit unit) throws JavaModelException {
		return getResourceBundleName(JavaPlugin.getDefault().getASTProvider().getAST(unit, ASTProvider.WAIT_YES, null));
	}
	
	public static String getResourceBundleName(IClassFile classFile) throws JavaModelException {
		return getResourceBundleName(JavaPlugin.getDefault().getASTProvider().getAST(classFile, ASTProvider.WAIT_YES, null));
	}
	
	public static String getResourceBundleName(CompilationUnit astRoot) throws JavaModelException {

		if (astRoot == null)
			return null;
		
		final Map resultCollector= new HashMap(5);
		final Object RESULT_KEY= new Object();
		final Object FIELD_KEY= new Object();
		
		astRoot.accept(new ASTVisitor() {

			public boolean visit(MethodInvocation node) {
				IMethodBinding method= node.resolveMethodBinding();
				if (method == null)
					return true;

				String name= method.getDeclaringClass().getQualifiedName();
				if (!("java.util.ResourceBundle".equals(name) && "getBundle".equals(method.getName()) && node.arguments().size() > 0) && //old school //$NON-NLS-1$ //$NON-NLS-2$
						!("org.eclipse.osgi.util.NLS".equals(name) && "initializeMessages".equals(method.getName()) && node.arguments().size() == 2)) //Eclipse style //$NON-NLS-1$ //$NON-NLS-2$
					return true;

				Expression argument= (Expression)node.arguments().get(0);
				String bundleName= getBundleName(argument);
				if (bundleName != null)
					resultCollector.put(RESULT_KEY, bundleName);

				if (argument instanceof Name) {
					Object fieldNameBinding= ((Name)argument).resolveBinding();
					if (fieldNameBinding != null) 
						resultCollector.put(FIELD_KEY, fieldNameBinding);
				}

				return false;
			}

			public boolean visit(VariableDeclarationFragment node) {
				Expression initializer= node.getInitializer();
				String bundleName= getBundleName(initializer);
				if (bundleName != null) {
					Object fieldNameBinding= node.getName().resolveBinding();
					if (fieldNameBinding != null)
						resultCollector.put(fieldNameBinding, bundleName);
					return false;
				}
				return true;	
			}

			public boolean visit(Assignment node) {
				if (node.getLeftHandSide() instanceof Name) {
					String bundleName= getBundleName(node.getRightHandSide());
					if (bundleName != null) {
						Object fieldNameBinding= ((Name)node.getLeftHandSide()).resolveBinding();
						if (fieldNameBinding != null) {
							resultCollector.put(fieldNameBinding, bundleName);
							return false;
						}
					}
				}
				return true;
			}

			private String getBundleName(Expression initializer) {
				if (initializer instanceof StringLiteral)
					return ((StringLiteral)initializer).getLiteralValue();

				if (initializer instanceof MethodInvocation) {
					MethodInvocation methInvocation= (MethodInvocation)initializer;
					Expression exp= methInvocation.getExpression();
					if ((exp != null) && (exp instanceof TypeLiteral)) {
						SimpleType simple= (SimpleType)((TypeLiteral) exp).getType();
						ITypeBinding typeBinding= simple.resolveBinding();
						if (typeBinding != null)
							return typeBinding.getQualifiedName();
					}
				}
				return null;	
			}

		});

		
		Object fieldName;
		String result;
		
		// First try hard-coded bundle name String field names from NLS tooling:
		Iterator iter= resultCollector.keySet().iterator();
		while (iter.hasNext()) {
			Object o= iter.next();
			if (!(o instanceof IBinding))
				continue;
			IBinding binding= (IBinding)o;
			fieldName= binding.getName();
			if (fieldName.equals("BUNDLE_NAME") || fieldName.equals("RESOURCE_BUNDLE") || fieldName.equals("bundleName")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				result= (String)resultCollector.get(binding);
				if (result != null)
					return result;
			}
		}

		result= (String)resultCollector.get(RESULT_KEY);
		if (result != null)
			return result;

		fieldName= resultCollector.get(FIELD_KEY);
		if (fieldName != null)
			return (String)resultCollector.get(fieldName);

		return null;
	}

	public static IPackageFragment getResourceBundlePackage(IJavaProject javaProject, String packageName, String resourceName) throws JavaModelException {
		IPackageFragmentRoot[] allRoots= javaProject.getAllPackageFragmentRoots();
		for (int i= 0; i < allRoots.length; i++) {
			IPackageFragmentRoot root= allRoots[i];
			if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
				IPackageFragment packageFragment= root.getPackageFragment(packageName);
				if (packageFragment.exists()) {
					Object[] resources= packageFragment.isDefaultPackage() ? root.getNonJavaResources() : packageFragment.getNonJavaResources();
					for (int j= 0; j < resources.length; j++) {
						Object object= resources[j];
						if (object instanceof IFile) {
							IFile file= (IFile) object;
							if (file.getName().equals(resourceName)) {
								return packageFragment;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public static IStorage getResourceBundle(ICompilationUnit compilationUnit) throws JavaModelException {
		IJavaProject project= compilationUnit.getJavaProject();
		if (project == null)
			return null;
		
		String name= getResourceBundleName(compilationUnit);
		if (name == null)
			return null;
		
		String packName= Signature.getQualifier(name); 
		String resourceName= Signature.getSimpleName(name) + NLSRefactoring.PROPERTY_FILE_EXT;
		
		return getResourceBundle(project, packName, resourceName);
	}
	
	public static IStorage getResourceBundle(IJavaProject javaProject, String packageName, String resourceName) throws JavaModelException {
		IPackageFragmentRoot[] allRoots= javaProject.getAllPackageFragmentRoots();
		for (int i= 0; i < allRoots.length; i++) {
			IPackageFragmentRoot root= allRoots[i];
			if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
				IStorage storage= getResourceBundle(root, packageName, resourceName);
				if (storage != null)
					return storage;
			}
		}
		return null;
	}
	
	public static IStorage getResourceBundle(IPackageFragmentRoot root, String packageName, String resourceName) throws JavaModelException {
		IPackageFragment packageFragment= root.getPackageFragment(packageName);
		if (packageFragment.exists()) {
			Object[] resources= packageFragment.isDefaultPackage() ? root.getNonJavaResources() : packageFragment.getNonJavaResources();
			for (int j= 0; j < resources.length; j++) {
				Object object= resources[j];
				if (JavaModelUtil.isOpenableStorage(object)) {
					IStorage storage= (IStorage)object;
					if (storage.getName().equals(resourceName)) {
						return storage;
					}
				}
			}
		}
		return null;
	}

	public static IStorage getResourceBundle(IJavaProject javaProject, AccessorClassReference accessorClassReference) throws JavaModelException {
		String resourceBundle= accessorClassReference.getResourceBundleName();
		if (resourceBundle == null)
			return null;
		
		String resourceName= Signature.getSimpleName(resourceBundle) + NLSRefactoring.PROPERTY_FILE_EXT;
		String packName= Signature.getQualifier(resourceBundle);
		ITypeBinding accessorClass= accessorClassReference.getBinding();
		
		if (accessorClass.isFromSource())
			return getResourceBundle(javaProject, packName, resourceName);
		else if (accessorClass.getJavaElement() != null)
			return getResourceBundle((IPackageFragmentRoot)accessorClass.getJavaElement().getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT), packName, resourceName);
		
		return null;
	}
	
	/**
	 * Reads the properties from the given storage and
	 * returns it.
	 * 
	 * @param javaProject the Java project
	 * @param accessorClassReference the accessor class reference
	 * @return the properties or <code>null</code> if it was not successfully read
	 */
	public static Properties getProperties(IJavaProject javaProject, AccessorClassReference accessorClassReference) {
		try {
			IStorage storage= NLSHintHelper.getResourceBundle(javaProject, accessorClassReference);
			return getProperties(storage);
		} catch (JavaModelException ex) {
			// sorry no properties
			return null;
		}
	}
	
	/**
	 * Reads the properties from the given storage and
	 * returns it.
	 * 
	 * @param storage the storage
	 * @return the properties or <code>null</code> if it was not successfully read
	 */
	public static Properties getProperties(IStorage storage) {
		if (storage == null)
			return null;

		Properties props= new Properties();
		InputStream is= null;
		
		ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
		try {
			if (manager != null) {
				ITextFileBuffer buffer= manager.getTextFileBuffer(storage.getFullPath(), LocationKind.NORMALIZE);
				if (buffer != null) {
					IDocument document= buffer.getDocument();
					is= new ByteArrayInputStream(document.get().getBytes());
				}
			}
			
			// Fallback: read from storage
			if (is == null)
				is= storage.getContents();
			
			props.load(is);
			
		} catch (IOException e) {
			// sorry no properties
			return null;
		} catch (CoreException e) {
			// sorry no properties
			return null;
		} finally {
			if (is != null) try {
				is.close();
			} catch (IOException e) {
				// return properties anyway but log
				JavaPlugin.log(e);
			}
		}
		return props;
	}

}
