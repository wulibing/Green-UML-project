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
package org.eclipse.jdt.internal.ui.fix;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.CompilationUnit;

import org.eclipse.jdt.internal.corext.fix.CleanUpConstants;
import org.eclipse.jdt.internal.corext.fix.IFix;
import org.eclipse.jdt.internal.corext.fix.Java50Fix;

import org.eclipse.jdt.ui.text.java.IProblemLocation;

/**
 * Create fixes which can transform pre Java50 code to Java50 code
 * @see org.eclipse.jdt.internal.corext.fix.Java50Fix
 *
 */
public class Java50CleanUp extends AbstractCleanUp {
		
	public Java50CleanUp(Map options) {
		super(options);
	}
	
	public Java50CleanUp() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean requireAST(ICompilationUnit unit) throws CoreException {
		boolean addAnotations= isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS);
		
		return addAnotations && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_OVERRIDE) || 
		       addAnotations && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_DEPRECATED) || 
		       isEnabled(CleanUpConstants.VARIABLE_DECLARATION_USE_TYPE_ARGUMENTS_FOR_RAW_TYPE_REFERENCES);
	}

	public IFix createFix(CompilationUnit compilationUnit) throws CoreException {
		if (compilationUnit == null)
			return null;
		
		boolean addAnotations= isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS);
		return Java50Fix.createCleanUp(compilationUnit, 
				addAnotations && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_OVERRIDE), 
				addAnotations && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_DEPRECATED), 
				isEnabled(CleanUpConstants.VARIABLE_DECLARATION_USE_TYPE_ARGUMENTS_FOR_RAW_TYPE_REFERENCES));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IFix createFix(CompilationUnit compilationUnit, IProblemLocation[] problems) throws CoreException {
		if (compilationUnit == null)
			return null;
		
		return Java50Fix.createCleanUp(compilationUnit, problems,
				isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS) && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_OVERRIDE), 
				isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS) && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_DEPRECATED),
				isEnabled(CleanUpConstants.VARIABLE_DECLARATION_USE_TYPE_ARGUMENTS_FOR_RAW_TYPE_REFERENCES));
	}

	public Map getRequiredOptions() {
		Map options= new Hashtable();
		if (isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS) && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_OVERRIDE))
			options.put(JavaCore.COMPILER_PB_MISSING_OVERRIDE_ANNOTATION, JavaCore.WARNING);
		
		if (isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS) && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_DEPRECATED))
			options.put(JavaCore.COMPILER_PB_MISSING_DEPRECATED_ANNOTATION, JavaCore.WARNING);
		
		if (isEnabled(CleanUpConstants.VARIABLE_DECLARATION_USE_TYPE_ARGUMENTS_FOR_RAW_TYPE_REFERENCES))
			options.put(JavaCore.COMPILER_PB_RAW_TYPE_REFERENCE, JavaCore.WARNING);
				
		return options;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String[] getDescriptions() {
		List result= new ArrayList();
		if (isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS) && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_OVERRIDE))
			result.add(MultiFixMessages.Java50MultiFix_AddMissingOverride_description);
		if (isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS) && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_DEPRECATED))
			result.add(MultiFixMessages.Java50MultiFix_AddMissingDeprecated_description);
		if (isEnabled(CleanUpConstants.VARIABLE_DECLARATION_USE_TYPE_ARGUMENTS_FOR_RAW_TYPE_REFERENCES))
			result.add(MultiFixMessages.Java50CleanUp_AddTypeParameters_description);
		return (String[])result.toArray(new String[result.size()]);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getPreview() {
		StringBuffer buf= new StringBuffer();
		
		buf.append("class E {\n"); //$NON-NLS-1$
		buf.append("    /**\n"); //$NON-NLS-1$
		buf.append("     * @deprecated\n"); //$NON-NLS-1$
		buf.append("     */\n"); //$NON-NLS-1$
		if (isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS) && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_DEPRECATED)) {
			buf.append("    @Deprecated\n"); //$NON-NLS-1$
		}
		buf.append("    public void foo() {}\n"); //$NON-NLS-1$
		buf.append("}\n"); //$NON-NLS-1$
		buf.append("class ESub extends E {\n"); //$NON-NLS-1$
		if (isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS) && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_OVERRIDE)) {
			buf.append("    @Override\n"); //$NON-NLS-1$
		}
		buf.append("    public void foo() {}\n"); //$NON-NLS-1$
		buf.append("}\n"); //$NON-NLS-1$
		
		return buf.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canFix(CompilationUnit compilationUnit, IProblemLocation problem) throws CoreException {
		if (isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS) && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_OVERRIDE)) {
			Java50Fix fix= Java50Fix.createAddOverrideAnnotationFix(compilationUnit, problem);
			if (fix != null)
				return true;
		}
		if (isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS) && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_DEPRECATED)) {
			Java50Fix fix= Java50Fix.createAddDeprectatedAnnotation(compilationUnit, problem);
			if (fix != null)
				return true;
		}
		if (isEnabled(CleanUpConstants.VARIABLE_DECLARATION_USE_TYPE_ARGUMENTS_FOR_RAW_TYPE_REFERENCES)) {
			Java50Fix fix= Java50Fix.createRawTypeReferenceFix(compilationUnit, problem);
			if (fix != null)
				return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public int maximalNumberOfFixes(CompilationUnit compilationUnit) {
		int result= 0;
		IProblem[] problems= compilationUnit.getProblems();
		if (isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS) && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_OVERRIDE)) {
			result+= getNumberOfProblems(problems, IProblem.MissingOverrideAnnotation);
		}
		if (isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS) && isEnabled(CleanUpConstants.ADD_MISSING_ANNOTATIONS_DEPRECATED)) {
			for (int i=0;i<problems.length;i++) {
				int id= problems[i].getID();
				if (id == IProblem.FieldMissingDeprecatedAnnotation || id == IProblem.MethodMissingDeprecatedAnnotation || id == IProblem.TypeMissingDeprecatedAnnotation)
					result++;
			}
		}
		if (isEnabled(CleanUpConstants.VARIABLE_DECLARATION_USE_TYPE_ARGUMENTS_FOR_RAW_TYPE_REFERENCES)) {
			for (int i=0;i<problems.length;i++) {
				int id= problems[i].getID();
				if (id == IProblem.UnsafeTypeConversion || id == IProblem.RawTypeReference || id == IProblem.UnsafeRawMethodInvocation)
					result++;
			}
		}
		return result;
	}
	
}
