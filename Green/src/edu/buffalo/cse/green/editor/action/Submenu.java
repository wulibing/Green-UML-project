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

/**
 * An enumeration over all of the different submenus used in Green's context
 * menu. The <code>String</code> passed to the constructor determines what the
 * label of the menu will be.
 * 
 * This enum is used by <code>ContextAction</code> subclasses to specify what
 * menu they belong to. Putting a context menu item in the "Invisible" class
 * will prevent it from being shown. This is sometimes necessary so that an
 * instance is created to allow shortcut keys to function. 
 * 
 * @author bcmartin
 */
public enum Submenu {
	Add("Add"), Invisible("!inv"), None("/"), Refactor("Refactor"),
	Remove("Remove"), Visibility("Visibility");
	
	private String _path;
	
	Submenu(String path) {
		_path = path;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return _path;
	}
}