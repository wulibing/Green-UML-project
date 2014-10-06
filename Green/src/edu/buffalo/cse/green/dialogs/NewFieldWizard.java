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

import static edu.buffalo.cse.green.constants.DialogStrings.WIZARD_ADD_FIELD_MESSAGE;
import static edu.buffalo.cse.green.constants.DialogStrings.WIZARD_ADD_FIELD_TITLE;
import static edu.buffalo.cse.green.dialogs.NewElementWizardSettings.ClassFieldSettings;
import static edu.buffalo.cse.green.dialogs.NewElementWizardSettings.InterfaceFieldSettings;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Composite;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.model.TypeModel;

import edu.buffalo.cse.green.preferences.VariableAffix;

/**
 * @author tomhicks
 */
public class NewFieldWizard extends NewElementWizard {
	private NewFieldWizardPage fPage;
	private String _fieldName;
	private TypeModel _parent;
	private List<String> _modifiers;
	private String _typeName;

	public NewFieldWizard(TypeModel parent) {
		super();
		_parent = parent;
		_fieldName = "";
		_typeName = "";
		setDialogSettings(PlugIn.getDefault().getDialogSettings());
		setWindowTitle(WIZARD_ADD_FIELD_TITLE);
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		fPage = new NewFieldWizardPage(_parent.getType().getJavaProject());
		addPage(fPage);
		try {
			fPage.setInterface(_parent.isInterface());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see edu.buffalo.cse.green.dialogs.GreenWizard#doFinish()
	 */
	public boolean doFinish() {
		boolean result = super.doFinish();
		if (fPage.getErrorMessage() != null) {
			result = false;
		}

		if (result) {
			_fieldName = VariableAffix.getAffixString(VariableAffix.FieldPrefix) + fPage.getName();
			_modifiers = fPage.getModifiers();
			_typeName = fPage.getTypeName();
		}
		
		return result;
	}

	/**
	 * @return The name of the field.
	 */
	public String getFieldName() {
		return _fieldName;
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
	 * @return The name of the type.
	 */
	public String getTypeName() {
		return _typeName;
	}

	/**
	 * The page for <code>NewFieldWizard</code>. 
	 * 
	 * @author bcmartin
	 */
	class NewFieldWizardPage extends NewMemberSignatureWizardPage {
		public NewFieldWizardPage(IJavaProject project) {
			super("AddFieldWizardPage");

			setTitle(WIZARD_ADD_FIELD_TITLE);
			setDescription(WIZARD_ADD_FIELD_MESSAGE);
		}

		/**
		 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			super.createControl(parent);

			if (isInterface()) {
				getVisibilityContainer().setDefaultEnabled(false);
				getVisibilityContainer().setPrivateEnabled(false);
				getVisibilityContainer().setProtectedEnabled(false);
				getVisibilityContainer().setPublicSelected(true);
			} else {
				getVisibilityContainer().setPrivateSelected(true);
			}
		}

		/**
		 * @see edu.buffalo.cse.green.dialogs.NewMemberSignatureWizardPage#getSettings()
		 */
		protected NewElementWizardSettings getSettings() {
			return isInterface() ? InterfaceFieldSettings : ClassFieldSettings;
		}

		/**
		 * @see edu.buffalo.cse.green.dialogs.NewMemberSignatureWizardPage#allowVoidType()
		 */
		protected boolean allowVoidType() {
			return false;
		}
		
		protected boolean isField() {
			//Used in determining if default veriable name will use preferred
			//field prefix.
			return true;
		}
	}

	/**
	 * @see edu.buffalo.cse.green.dialogs.NewElementWizard#canRunForked()
	 */
	protected boolean canRunForked() {
		return true;
	}
}
