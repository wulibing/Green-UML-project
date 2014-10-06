/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.dialogs;

import static edu.buffalo.cse.green.constants.DialogStrings.WIZARD_ADD_METHOD_MESSAGE;
import static edu.buffalo.cse.green.constants.DialogStrings.WIZARD_ADD_METHOD_TITLE;
import static edu.buffalo.cse.green.dialogs.NewElementWizardSettings.ClassMethodSettings;
import static edu.buffalo.cse.green.dialogs.NewElementWizardSettings.InterfaceMethodSettings;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Composite;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.model.TypeModel;

/**
 * @author kfjacobs
 * 
 * Wizard used for creating methods
 */
public class NewMethodWizard extends NewElementWizard {
	private NewMethodWizardPage _page;
	private String _methodName;
	private TypeModel _parent;
	private List<String> _modifiers;
	private String _returnTypeName;

	public NewMethodWizard(TypeModel parent) {
		super();
		_parent = parent;
		_methodName = "";
		_returnTypeName = "";
		setDialogSettings(PlugIn.getDefault().getDialogSettings());
		setWindowTitle(WIZARD_ADD_METHOD_TITLE);
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		_page = new NewMethodWizardPage(_parent.getType().getJavaProject());
		try {
			_page.setInterface(_parent.isInterface());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		addPage(_page);
	}

	/**
	 * @see edu.buffalo.cse.green.dialogs.GreenWizard#doFinish()
	 */
	public boolean doFinish() {
		boolean result = super.doFinish();
		if (_page.getErrorMessage() != null) {
			result = false;
		}
		
		if (result) {
			_methodName = _page.getName();
			_modifiers = _page.getModifiers();
			_returnTypeName = _page.getTypeName();
		}
		
		return result;
	}

	/**
	 * @return The name of the method.
	 */
	public String getMethodName() {
		return _methodName;
	}

	/**
	 * @return The modifiers.
	 */
	public String getModifiers() {
		String modifiers = _modifiers.toString();
		modifiers = modifiers.replaceAll(",", "");
		return modifiers.substring(1, modifiers.length() - 1); 
	}

	/**
	 * @return The name of the return type.
	 */
	public String getReturnTypeName() {
		return _returnTypeName;
	}

	/**
	 * The page for <code>NewMethodWizard</code>
	 * 
	 * @author bcmartin
	 */
	class NewMethodWizardPage extends NewMemberSignatureWizardPage {
		public NewMethodWizardPage(IJavaProject project) {
			super("NewMethodWizardPage");

			setTitle(WIZARD_ADD_METHOD_TITLE);
			setDescription(WIZARD_ADD_METHOD_MESSAGE);

		}

		/**
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			super.createControl(parent);

			if (isInterface()) {
				getVisibilityContainer().setDefaultSelected(true);
				getVisibilityContainer().setPrivateEnabled(false);
				getVisibilityContainer().setProtectedEnabled(false);
				getVisibilityContainer().setPublicEnabled(false);
			} else {
				getVisibilityContainer().setPublicSelected(true);
			}
		}

		/**
		 * @see edu.buffalo.cse.green.dialogs.NewMemberSignatureWizardPage#getSettings()
		 */
		protected NewElementWizardSettings getSettings() {
			return isInterface() ? InterfaceMethodSettings
					: ClassMethodSettings;
		}

		/**
		 * @see edu.buffalo.cse.green.dialogs.NewMemberSignatureWizardPage#allowVoidType()
		 */
		protected boolean allowVoidType() {
			return true;
		}
		
		protected boolean isField() {
			//Used in determining if default veriable name will use preferred
			//field prefix.
			return false;
		}
	}

	/**
	 * @see edu.buffalo.cse.green.dialogs.NewElementWizard#canRunForked()
	 */
	protected boolean canRunForked() {
		return true;
	}
}
