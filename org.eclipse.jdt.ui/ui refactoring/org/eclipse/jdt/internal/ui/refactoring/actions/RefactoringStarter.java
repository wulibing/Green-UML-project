/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.refactoring.actions;

import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.dialogs.IDialogConstants;

import org.eclipse.jdt.internal.ui.refactoring.RefactoringSaveHelper;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;

/**
 * A helper class to activate the UI of a refactoring
 */
public class RefactoringStarter {
	
	private RefactoringStatus fStatus;

	/**
	 * @param refactoring
	 * @param wizard
	 * @param parent
	 * @param dialogTitle
	 * @param saveMode a save mode from {@link RefactoringSaveHelper}
	 * @return <code>true</code> if the refactoring was executed, <code>false</code> otherwise
	 * @throws JavaModelException
	 */
	public boolean activate(Refactoring refactoring, RefactoringWizard wizard, Shell parent, String dialogTitle, int saveMode) throws JavaModelException {
		RefactoringSaveHelper saveHelper= new RefactoringSaveHelper(saveMode);
		if (! canActivate(saveHelper, parent))
			return false;

		try {
			RefactoringWizardOpenOperation op= new RefactoringWizardOpenOperation(wizard);
			int result= op.run(parent, dialogTitle);
			fStatus= op.getInitialConditionCheckingStatus();
			if (result == IDialogConstants.CANCEL_ID || result == RefactoringWizardOpenOperation.INITIAL_CONDITION_CHECKING_FAILED) {
				saveHelper.triggerBuild();
				return false;
			} else {
				return true;
			}
		} catch (InterruptedException e) {
			return false; // User action got cancelled
		}
	}
	
	public RefactoringStatus getInitialConditionCheckingStatus() {
		return fStatus;
	}
		
	private boolean canActivate(RefactoringSaveHelper saveHelper, Shell shell) {
		return saveHelper.saveEditors(shell);
	}
}
