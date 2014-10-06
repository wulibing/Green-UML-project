/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.test;

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_FQN_TYPE_NAMES;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_METHOD_PARAMETERS;
import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_DISPLAY_NONPUBLIC_METHODS;
import static edu.buffalo.cse.green.test.PaletteTool.Association;
import static edu.buffalo.cse.green.test.PaletteTool.Class;
import static edu.buffalo.cse.green.test.PaletteTool.Composition;

import edu.buffalo.cse.green.editor.model.TypeModel;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;

/**
 * The demo for OOPSLA '05
 * 
 * @author Blake
 */
public class GreenDemo extends Demo {
	protected TypeModel _applet;
	private TypeModel _behaviorSwap;
	private TypeModel _changeableShape;
	private TypeModel _colorSwap;
	private TypeModel _graphicSwap;
	private TypeModel _propSwap;

	private IType _ps;
	private Point _editorLocation;
	
	/**
	 * @see edu.buffalo.cse.green.test.EnhancedTest#setup()
	 */
	protected void setup() throws Exception {
		_project = loadZIP("OOPSLA05DemoProject", "c:\\lab3.zip");
		IJavaProject project = _project.getJavaProject();  
		createEditor();
		IType applet = project.findType("Lab3.Applet");
		IType bsb = project.findType("Lab3.MyBehaviorSwapButton");
		IType csb = project.findType("Lab3.MyColorSwapButton");
		IType gsb = project.findType("Lab3.MyGraphicSwapButton");
		IType cs = project.findType("CSE115.ShapeWorld.ChangeableShape");
		
		_behaviorSwap = addType(EDITOR, bsb);
		_colorSwap = addType(EDITOR, csb);
		_graphicSwap = addType(EDITOR, gsb);
		_applet = addType(EDITOR, applet);
		_changeableShape = addType(EDITOR, cs);
		
		_behaviorSwap.setLocation(0, 0);
		_colorSwap.setLocation(300, 0);
		_graphicSwap.setLocation(600, 0);
		_applet.setLocation(0, 200);
		_changeableShape.setLocation(600, 200);
		
		IPreferenceStore store = edu.buffalo.cse.green.PlugIn.getDefault().getPreferenceStore();
		store.setValue(P_DISPLAY_FQN_TYPE_NAMES, false);
		store.setValue(P_DISPLAY_METHOD_PARAMETERS, false);
		store.setValue(P_DISPLAY_NONPUBLIC_METHODS, false);
	}
	
	@SuppressWarnings("deprecation")
	/**
	 * The demo.
	 */
	public void testDemo() throws Exception {
		PlugIn.addDemoThread(new Thread() {
			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				try {
					_editorLocation =
						getLocation(EDITOR.getRootPart().getViewer().getControl());
					
					// first, create the PropertySwapper class (200, 200)
					clickLocation(EDITOR.getRootPart().getViewer().getControl(),
							Class, 150 + _editorLocation.x, 200 + _editorLocation.y,
							300, 200);
					
					_ps = _project.getJavaProject().findType("Lab3.PropertySwapper");
					_propSwap = (TypeModel) getModel(_ps);
				} catch (Exception e) {
					// do nothing
				}
			}
		});
		
		PlugIn.addDemoThread(new Thread() {
			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				drawRelationship(_applet, _propSwap, Composition);
				drawRelationship(_propSwap, _changeableShape, Composition, 2);
			}
		});

		PlugIn.addDemoThread(new Thread() {
			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				drawRelationship(_behaviorSwap, _changeableShape, Association, 2);
				drawRelationship(_colorSwap, _changeableShape, Association, 2);
				drawRelationship(_graphicSwap, _changeableShape, Association, 2);
			}
		});

		PlugIn.addDemoThread(new Thread() {
			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				drawRelationship(_propSwap, _behaviorSwap, Composition, 1);
				drawRelationship(_propSwap, _colorSwap, Composition, 1);
				drawRelationship(_propSwap, _graphicSwap, Composition, 1);
			}
		});
		
		// set up demo
		EDITOR.addKeyAction('q', AdvanceDemoAction.INSTANCE);
		EDITOR.refresh();
		
		while (!PlugIn.isDead()) {
			delay(100000);
		}
	}
}
