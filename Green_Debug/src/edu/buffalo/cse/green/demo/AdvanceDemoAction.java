/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.demo;

import edu.buffalo.cse.green.editor.action.ContextAction;
import edu.buffalo.cse.green.editor.action.Submenu;
import edu.buffalo.cse.green.test.PlugIn;

import org.eclipse.jdt.core.JavaModelException;

/**
 * Advances the current demo by one step.
 * 
 * @author Blake
 */
public class AdvanceDemoAction extends ContextAction {
	public static AdvanceDemoAction INSTANCE;

	public AdvanceDemoAction() {
		INSTANCE = this;
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		return "";
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	protected void doRun() throws JavaModelException {
		PlugIn.runDemoThread();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_ALL;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.Invisible;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}
}
