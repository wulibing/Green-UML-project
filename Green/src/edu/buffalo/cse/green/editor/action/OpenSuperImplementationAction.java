/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.action;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.SuperTypeHierarchyCache;
import org.eclipse.jdt.internal.ui.callhierarchy.CallHierarchyUI;

/**
 * An adaptation of <code>OpenSuperImplementation</code>.
 * 
 * @author bcmartin
 */
public class OpenSuperImplementationAction extends ContextAction {
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	public void doRun() throws JavaModelException {
		IJavaElement elements[] = new IJavaElement[] {
			_element };

		CallHierarchyUI.open(elements,
				getEditor().getSite().getWorkbenchWindow());
	}

	/**
	 * Decides whether this action should be visible for the given method
	 * 
	 * @param element - The method to consider
	 * @return true if it should be visible, false otherwise
	 */
	public boolean decideVisibility(IJavaElement element) {
		try {
			if (!(element instanceof IMethod)) return false;
			
			IMethod impl;
			IMethod method = (IMethod) element;
			impl = findSuperImplementation((IType) method.getParent(),
					method.getElementName(), method.getParameterTypes(),
					method.isConstructor());
			if (impl == null) { return false; }
		} catch (JavaModelException e) {
			return false;
		}

		return true;
	}

	/**
	 * Finds the super implementation of a method
	 * 
	 * @param declaringType - The type the method is declared in
	 * @param name - The name of the method
	 * @param paramTypes - The types of the parameters
	 * @param isConstructor - Whether or not the method is a constructor
	 * @return the method representing the super implementation of the given
	 * method
	 * @throws JavaModelException
	 */
	private IMethod findSuperImplementation(
			IType declaringType,
			String name,
			String[] paramTypes,
			boolean isConstructor) throws JavaModelException {
		ITypeHierarchy hierarchy = SuperTypeHierarchyCache
				.getTypeHierarchy(declaringType);
		return JavaModelUtil.findMethodInHierarchy(hierarchy, declaringType,
				name, paramTypes, isConstructor);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return "Open Super Implementation";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_METHOD;
	}

	/**
	 * @see org.eclipse.jface.action.IAction#isEnabled()
	 */
	public boolean isEnabled() {
		return decideVisibility(_element);
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.None;
	}
}