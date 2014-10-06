/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.surround;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;

import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.Selection;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;

public class SurroundWithTryCatchAnalyzer extends SurroundWithAnalyzer {


	private ISurroundWithTryCatchQuery fQuery;
	private ITypeBinding[] fExceptions;

	public SurroundWithTryCatchAnalyzer(ICompilationUnit unit, Selection selection, ISurroundWithTryCatchQuery query) throws JavaModelException {
		super(unit, selection);
		fQuery= query;
	}
	
	public ITypeBinding[] getExceptions() {
		return fExceptions;
	}

	public void endVisit(CompilationUnit node) {
		BodyDeclaration enclosingNode= null;
		if (!getStatus().hasFatalError() && hasSelectedNodes())
			enclosingNode= (BodyDeclaration)ASTNodes.getParent(getFirstSelectedNode(), BodyDeclaration.class);

		super.endVisit(node);
		if (enclosingNode != null && !getStatus().hasFatalError()) {
			fExceptions= ExceptionAnalyzer.perform(enclosingNode, getSelection());
			if (fExceptions == null || fExceptions.length == 0) {
				if (fQuery == null) {
					invalidSelection(RefactoringCoreMessages.SurroundWithTryCatchAnalyzer_noUncaughtExceptions); 
				} else if (fQuery.catchRuntimeException()) {
					fExceptions= new ITypeBinding[] {node.getAST().resolveWellKnownType("java.lang.RuntimeException")}; //$NON-NLS-1$
				}
			}
		}
	}
}
