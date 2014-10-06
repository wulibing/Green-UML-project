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

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.editor.model.TypeModel;

/**
 * Delete the selected <code>IType</code>.
 * 
 * @author bcmartin
 */
public class DeleteTypeCommand extends DeleteCommand {
	private TypeModel _typeModel;

	public DeleteTypeCommand(TypeModel umlTypeModel) {
		super();
		_typeModel = umlTypeModel;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.commands.DeleteCommand#doDelete()
	 */
	public void doDelete() {
		// TODO On 01/30/2007, on Gene's advice, we interchanged the following two lines,
		// originally
		//		_typeModel.removeFromParent();
		//		_typeModel.removeChildren(); // remove fields/methods
		//		RootModel root = _typeModel.getRootModel();
		RootModel root = _typeModel.getRootModel();
		_typeModel.removeChildren(); // remove fields/methods
		_typeModel.removeFromParent();
		try {
			IType type = _typeModel.getType();
			ICompilationUnit cu = (ICompilationUnit) type
					.getAncestor(IJavaElement.COMPILATION_UNIT);
			
			if (type.equals(cu.findPrimaryType())) {
				cu.delete(true, PlugIn.getEmptyProgressMonitor());
			} else {
				type.delete(true, PlugIn.getEmptyProgressMonitor());
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		root.updateRelationships();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.commands.DeleteCommand#getDeleteMessage()
	 */
	public String getDeleteMessage() {
		if (_typeModel.getType().isBinary()) { return null; }

		return "Are you sure you want to delete "
				+ _typeModel.getMember().getElementName() + "?";
	}
}