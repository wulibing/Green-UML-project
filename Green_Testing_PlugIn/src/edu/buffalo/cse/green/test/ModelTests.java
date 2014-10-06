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

import static edu.buffalo.cse.green.test.core.PaletteTool.Association;
import static edu.buffalo.cse.green.test.core.PaletteTool.Composition;
import static edu.buffalo.cse.green.test.core.PaletteTool.Dependency;
import static edu.buffalo.cse.green.test.core.PaletteTool.Generalization;
import static edu.buffalo.cse.green.test.core.PaletteTool.Realization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.action.AddFieldAction;
import edu.buffalo.cse.green.editor.action.AddMethodAction;
import edu.buffalo.cse.green.editor.action.IncrementalExploreAction;
import edu.buffalo.cse.green.editor.action.QuickFixAction;
import edu.buffalo.cse.green.editor.controller.RelationshipPart;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.FieldModel;
import edu.buffalo.cse.green.editor.model.MethodModel;
import edu.buffalo.cse.green.editor.model.NoteModel;
import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.editor.model.commands.AddFieldCommand;
import edu.buffalo.cse.green.editor.model.commands.AddMethodCommand;
import edu.buffalo.cse.green.editor.model.commands.AlterRelationshipVisibilityCommand;
import edu.buffalo.cse.green.editor.model.commands.CreateBendpointCommand;
import edu.buffalo.cse.green.editor.model.commands.CreateCommand;
import edu.buffalo.cse.green.editor.model.commands.DeleteBendpointCommand;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.editor.model.commands.DeleteNoteCommand;
import edu.buffalo.cse.green.editor.model.commands.EditNoteCommand;
import edu.buffalo.cse.green.editor.model.commands.HideRelationshipCommand;
import edu.buffalo.cse.green.editor.model.commands.HideTypeCommand;
import edu.buffalo.cse.green.editor.model.commands.MoveBendpointCommand;
import edu.buffalo.cse.green.editor.model.commands.ShowRelationshipCommand;
import edu.buffalo.cse.green.editor.view.GreenBendpoint;
import edu.buffalo.cse.green.editor.view.RelationshipFigure;
import edu.buffalo.cse.green.relationship.generalization.GeneralizationPart;
import edu.buffalo.cse.green.relationships.RelationshipGroup;
import edu.buffalo.cse.green.test.core.EnhancedTest;
import edu.buffalo.cse.green.test.core.ReturnType;

public class ModelTests extends EnhancedTest {
	private TypeModel _interface;
	private TypeModel _sourceClass;
	private TypeModel _targetClass;
	private FieldModel _field;
	private MethodModel _method;
	private RelationshipModel _rGeneralization;
	private final String SOURCE_CLASS_NAME = "SourceClass";
	
	static {
		PlugIn.setTestMode();
	}
	
	@Override
	protected void setup() throws Exception {
		super.setup();
//		_targetClass = createClass("TargetClass");
//		_interface = createInterface("AnInterface");
//		_sourceClass = createSubClass(SOURCE_CLASS_NAME,
//				"TargetClass implements AnInterface",
//				"\tprivate int _a = 5;\n" +
//				"\n" +
//				"\tpublic SourceClass() {\n" +
//				"\t\tString string = new String();\n" +
//				"\t}\n" +
//				"\n" +
//				"\tpublic void someMethod() {\n" +
//				"\t}\n");
//        _field = (FieldModel) _sourceClass.getFieldCompartmentModel().getChildren().get(0);
//        _method = (MethodModel) _sourceClass.getMethodCompartmentModel().getChildren().get(1);
//		_targetClass = createClass("TargetClass");
//		_sourceClass.setLocation(0, 0);
//		_targetClass.setLocation(0, 200);
//		_interface.setLocation(200, 0);
//		
//		_rGeneralization = getRelationship(_sourceClass, _targetClass,
//				Generalization);
	}
	
//	public void test_pluginCreation() {
////		System.out.println(PlugIn.getDefault().getWorkbench().getEditorRegistry().getEditors("foo.grn"));
//		System.out.print("Entering test code...A");
//		List<RelationshipGroup> list = PlugIn.getRelationshipList();
//		System.out.print("...B");
//		int expected = 7;
//		System.out.print("...C");
//		int actual = list.size();
//		System.out.println("...D"+ROOT.getElementsOfKind(IJavaElement.TYPE).toString());
//		assertTrue("I expected "+expected+" relationship groups, but found "+actual, expected==actual);
//	}

	public void test_ClassCreation() {
		boolean expected = true;
		boolean actual = false;
		try {
			TypeModel aClass = createClass("ATestClass");
			List<AbstractModel> models = ROOT.getRootModel().getChildren();
			for (AbstractModel model : models) {
				System.out.println("Checking "+model.toString());
				if (aClass.equals(model)) {
					actual = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(expected=actual);
	}
	
}