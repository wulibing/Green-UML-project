/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.model.commands;

import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.JavaModelException;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.FieldModel;
import edu.buffalo.cse.green.editor.model.TypeModel;

/**
 * Command used to add fields to existing <code>IType</code>s.
 * 
 * @author cgullans
 */
public class AddFieldCommand extends Command {
	/**
	 * The representation of the field declaration.
	 */
	private String _fieldString;

	/**
	 * The name of the field.
	 */
	private String _fieldName;

	/**
	 * The model representing the field.
	 */
	private FieldModel _fModel;

	/**
	 * The <code>TypeModel</code> containing the field.
	 */
	private TypeModel _model;

	public AddFieldCommand(String fieldString, String fieldName, TypeModel model) {
		_fieldString = fieldString;
		_fieldName = fieldName;
		_model = model;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		if (!_model.getType().getField(_fieldName).exists()) {
			try {
				_model.getType().createField(_fieldString, null, false,
						PlugIn.getEmptyProgressMonitor());

			} catch (JavaModelException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		List<AbstractModel> list = _model.getFieldCompartmentModel()
				.getChildren();
		boolean continue0 = true;
		int index = 0;
		
		while (continue0 && index < list.size()) {
			FieldModel fModel = (FieldModel) list.get(index);
			if (fModel.getMember().getElementName().equals(_fieldName)) {
				_fModel = fModel;
				continue0 = false;
			}
			index++;
		}

		try {
			_fModel.getMember().delete(true, PlugIn.getEmptyProgressMonitor());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
}