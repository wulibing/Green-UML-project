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
import edu.buffalo.cse.green.editor.model.MethodModel;
import edu.buffalo.cse.green.editor.model.TypeModel;

/**
 * Command for adding a method to an existing type
 * 
 * @author cgullans
 */
public class AddMethodCommand extends Command {
	/**
	 * The name of the method.
	 */
	private String _methodName;

	/**
	 * The parent of the model being created.
	 */
	private TypeModel _model;

	/**
	 * The representation of the method declaration.
	 */
	private String _methodString;

	public AddMethodCommand(
			String methodName,
			String methodString,
			TypeModel model) {
		_model = model;
		_methodName = methodName;
		_methodString = methodString;
	}

	/**
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		if (!_model.getType().getMethod(_methodName, new String[] {}).exists()) {
			try {
				_model.getType().createMethod(_methodString, null, false,
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
		List<AbstractModel> list = _model.getMethodCompartmentModel()
				.getChildren();
		boolean continue0 = true;
		int index = 0;
		MethodModel mModel = null;

		while (continue0 && index < list.size()) {
			MethodModel tempModel = (MethodModel) list.get(index);
			if (tempModel.getMember().getElementName().equals(_methodName)) {
				mModel = tempModel;
				continue0 = false;
			}
			index++;
		}
		
		try {
			mModel.getMember().delete(true, PlugIn.getEmptyProgressMonitor());
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
}
