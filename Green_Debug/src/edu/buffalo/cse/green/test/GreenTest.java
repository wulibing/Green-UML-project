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
import edu.buffalo.cse.green.test.core.EnhancedTest;
import edu.buffalo.cse.green.test.core.ReturnType;

public class GreenTest extends EnhancedTest {
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
	
	protected void setup() throws Exception {
		_targetClass = createClass("TargetClass");
		_interface = createInterface("AnInterface");
		_sourceClass = createSubClass(SOURCE_CLASS_NAME,
				"TargetClass implements AnInterface",
				"\tprivate int _a = 5;\n" +
				"\n" +
				"\tpublic SourceClass() {\n" +
				"\t\tString string = new String();\n" +
				"\t}\n" +
				"\n" +
				"\tpublic void someMethod() {\n" +
				"\t}\n");
        _field = (FieldModel) _sourceClass.getFieldCompartmentModel().getChildren().get(0);
        _method = (MethodModel) _sourceClass.getMethodCompartmentModel().getChildren().get(1);
		_sourceClass.setLocation(0, 0);
		_targetClass.setLocation(0, 200);
		_interface.setLocation(200, 0);
		
		_rGeneralization = getRelationship(_sourceClass, _targetClass,
				Generalization);
	}
	
	public void testACTION_AddField_Compartment() throws Exception {
		// create a new field in the class using the field compartment
		selectContextMenuItem(_field, new AddFieldAction(),
				fillInFieldWizard("testField", Flags.AccPrivate,
						ReturnType.Boolean, Flags.AccFinal, _sourceClass));
	    assertEquals(FAIL_FIELD_COUNT, 2, getFields(_sourceClass).size());
	
	    //Test that the correct code was added to the file
	    String source = _sourceClass.getType().getSource();
	    int index = source.indexOf("private final boolean testField;");
	    assertFalse(FAIL_INCORRECT_CODE , index ==  -1);
	}

	public void testACTION_AddField_Interface() throws Exception {
		// create a new field in the class using the field compartment
		int flags = Flags.AccFinal;
		selectContextMenuItem(_interface, new AddFieldAction(),
				fillInFieldWizard("testField", Flags.AccPublic,
						ReturnType.Byte, flags, _interface));
	    assertEquals(FAIL_FIELD_COUNT, 1,
	    		_sourceClass.getFieldCompartmentModel().getChildren().size());
	
	    //Test that the correct code was added to the file
	    String source = _interface.getType().getSource();
	    int index = source.indexOf("public final static byte testField;");
	    assertFalse(FAIL_INCORRECT_CODE, index ==  -1);
	}

	public void testACTION_AddField_Class() throws Exception {
		// create a new field in the class using the class box
		int flags = Flags.AccStatic;
		selectContextMenuItem(_sourceClass, new AddFieldAction(),
				fillInFieldWizard("testField", Flags.AccProtected,
						ReturnType.Int,	flags, _sourceClass));
	    assertEquals(FAIL_FIELD_COUNT, 2, getFields(_sourceClass).size());
	
	    //Test that the correct code was added to the file
	    String source = _sourceClass.getType().getSource();
	    int index = source.indexOf(
	    		"protected static int testField;");
	    assertFalse(FAIL_INCORRECT_CODE, index == -1);
	}

	public void testACTION_AddMethod_Interface() throws Exception {
		// create a new method in the interface
		selectContextMenuItem(_interface, new AddMethodAction(),
				fillInMethodWizard("testMethod0", Flags.AccPublic,
						ReturnType.Char, 0, _interface));
	
	    //Test that the correct code was added to the file
		String source = _interface.getType().getSource();
	    int index = source.indexOf("char testMethod0();");
	    assertFalse(FAIL_INCORRECT_CODE, index ==  -1);
	}

	public void testACTION_AddMethod_Compartment() throws Exception {
		// create a new method in the class using the method compartment
		int flags = Flags.AccAbstract;
		selectContextMenuItem(_method, new AddMethodAction(),
				fillInMethodWizard("testMethod0", Flags.AccPrivate,
						ReturnType.Short, flags, _sourceClass));
	
		// Test that the correct code was added to the file
	    String source = _sourceClass.getType().getSource();
	    System.err.println(source);
	    int index = source.indexOf(
	    		"protected abstract short testMethod0();");
	    assertFalse(FAIL_INCORRECT_CODE, index ==  -1);
	}

	public void testACTION_AddMethod_Type() throws Exception {
		// create a new method in the class using the class box
		
		selectContextMenuItem(_sourceClass, new AddMethodAction(),
				fillInMethodWizard("testMethod0", Flags.AccPublic,
						ReturnType.Int, 0, _sourceClass));
		
		//Test that the correct code was added to the file
	    String source = _sourceClass.getType().getSource();
	    int index = source.indexOf("public int testMethod0() {");    
	    assertFalse(FAIL_INCORRECT_CODE, index ==  -1);
	}

    public void testCODE_AddMethod() throws Exception {
        IMethod method = _targetClass.getType().createMethod(
        		"void " + DEFAULT_METHOD_NAME + "() {}", null, false, PM);
        assertTrue(FAIL_METHOD_ABSENT, containsMethod(_targetClass, method));
    }
    
    public void testCODE_AddField() throws Exception {
        IField field = _targetClass.getType().createField(
        		DEFAULT_TYPE + " " + DEFAULT_FIELD_NAME + ";", null, false, PM);
        assertTrue(FAIL_FIELD_ABSENT, containsField(_targetClass, field));
    }

	public void testREL_CnD_Composition() throws Exception {
		List<FieldModel> fields = getFields(_sourceClass);
		int oldFieldCount = fields.size();
		
		RelationshipModel rModel =
	    	drawRelationship(_sourceClass, _targetClass, Composition);
	    
		assertEquals(FAIL_FIELD_ABSENT,
				fields.size(), oldFieldCount + 1);

		// delete the relationship
		delete(rModel);
		
		// make sure the number of fields is updated
		assertEquals(FAIL_FIELD_COUNT,
				fields.size(), oldFieldCount);
	} 

    public void testHOTKEY_DEL_Field() throws Exception {
        delete(_field);
        assertTrue(FAIL_DELETE,
        		!containsField(_sourceClass, _field.getField()));
    }

    public void testHOTKEY_DEL_Method() throws Exception {
        delete(_method);
        assertTrue(FAIL_DELETE,
        		!containsMethod(_sourceClass, _method.getMethod()));
    }
    
    public void testHOTKEY_DEL_Type() throws Exception {
        delete(_sourceClass);
        assertTrue(FAIL_DELETE,
        		!ROOT.getChildren().contains(_sourceClass));
    }
    
    public void testFUNCT_Note() throws Exception {
    	String newText = "some other text";
    	NoteModel note = createNote("some text");
    	setNoteLabel(note, newText);
    	Label label = (Label) getFigure(note);
    	assertEquals(FAIL_SETTEXT, newText,
    			label.getText());
    	delete(note);
        assertTrue(FAIL_DELETE,
        		!ROOT.getChildren().contains(note));
    }

    public void testACTION_QuickFix() throws Exception {
	    selectContextMenuItem(_sourceClass, new QuickFixAction(null));
	    
	    String source = _sourceClass.getType().getSource();
	    int index = source.indexOf("@SuppressWarnings(\"unused\"");
	    assertFalse(FAIL_INCORRECT_CODE, index ==  -1);
    }
    
    public void testACTION_RemoveType() throws Exception {
		assertCount(RelationshipModel.class, 2);
		assertCount(TypeModel.class, 3);
		
		// remove the relationships' source type from the diagram
		_sourceClass.removeFromParent();
		
		// should have removed the type model and the relationships
		assertCount(RelationshipModel.class, 0);
		assertCount(TypeModel.class, 2);
	}

    public void testJML_CompilationUnitMove() throws Exception {
        // create the target package and move the CU there
        IPackageFragment toPack = createPackage(TARGET_PACKAGE_NAME);
        _sourceClass.getType().getCompilationUnit().move(toPack, null, null, false, PM);
        
        // find the type after it was moved
        List<IType> types = (ArrayList<IType>) (List) ROOT.getElementsOfKind(
        		IJavaElement.TYPE);
        
        for (IType type : types) {
        	String handle = type.getHandleIdentifier();
        	
        	if ((handle.indexOf("<" + TARGET_PACKAGE_NAME + "{") != -1) &&
        			(handle.endsWith("[" + SOURCE_CLASS_NAME))) {
        		return;
        	}
        }
        
        fail(FAIL_TYPE_DOES_NOT_EXIST);
    }

    public void testJML_CompilationUnitRename() throws Exception {
        // rename the CU and get the new CU model
        _sourceClass.getType().rename(DEFAULT_TYPE_NAME + RENAMED_STRING, false, PM);
        _sourceClass = findType(DEFAULT_TYPE_NAME + RENAMED_STRING);
        assertEquals(FAIL_INCORRECT_RENAMED_TYPE,
        		_package.getElementName() + "." +
        		DEFAULT_TYPE_NAME + RENAMED_STRING,
        		_sourceClass.getType().getFullyQualifiedName());
    }
    
    public void testJML_FieldMove() throws Exception {
        //Create a type to move the field to.
    	IField field = _field.getField();
    	field.move(_targetClass.getType(), null, null, false, PM);
    	_field = findField(_targetClass, DEFAULT_FIELD_NAME);
    	Label label = (Label) getFigure(_field);
    	assertEquals(FAIL_INCORRECT_RENAMED_FIELD, DEFAULT_TYPE + " " + DEFAULT_FIELD_NAME, label.getText());
    }

    public void testJML_FieldRename() throws Exception {
        // rename the field and get the new field model
        IField field = _field.getField();
    	field.rename(DEFAULT_FIELD_NAME + RENAMED_STRING, false, PM);
    	_field = findField(_sourceClass, DEFAULT_FIELD_NAME + RENAMED_STRING);
    	FieldModel fieldModel = (FieldModel) _sourceClass.getFieldCompartmentModel().getChildren().get(0);
        
        assertEquals(FAIL_INCORRECT_RENAMED_FIELD, DEFAULT_TYPE + " " + DEFAULT_FIELD_NAME + RENAMED_STRING, fieldModel.getDisplayName());
    }
    
    public void testJML_MethodMove() throws Exception {
    	//Move existing method to DefaultType
    	IMethod method = _method.getMethod(); 
    	_method = findMethod(_sourceClass, DEFAULT_METHOD_NAME);
    	method.move(_targetClass.getType(), null, null, false, PM);
    	Label label = (Label) getFigure(_method);
    	assertEquals(FAIL_INCORRECT_RENAMED_METHOD, "void " + DEFAULT_METHOD_NAME + "()", label.getText());
    }

    public void testJML_MethodRename() throws Exception {
        // rename the method and get the new method model
    	IMethod method = _method.getMethod(); 
    	method.rename(DEFAULT_METHOD_NAME + RENAMED_STRING, false, PM);
    	_method = findMethod(_sourceClass, DEFAULT_METHOD_NAME + RENAMED_STRING);
    	Label label = (Label) getFigure(_method);
        assertEquals(FAIL_INCORRECT_RENAMED_METHOD, "void " + DEFAULT_METHOD_NAME + RENAMED_STRING + "()", label.getText());
    }

    public void testJML_PackageRename() throws Exception {
        IPackageFragment packFrag = (IPackageFragment) _sourceClass.getType().getAncestor(IJavaElement.PACKAGE_FRAGMENT);
        String renamed = "test";
        
        // rename the package and find the new type model
        packFrag.rename(renamed, false, PM);
        EDITOR.refresh();
        
        for (TypeModel model : getTypes()) {
        	if (model.getType().getElementName().equals(
        			DEFAULT_TYPE_NAME)) {
        		_sourceClass = model;
        		break;
        	}
        }
        
        assertEquals(FAIL_EDITOR_REFACTOR,
        		(renamed + "." + DEFAULT_TYPE_NAME),
        		_sourceClass.getType().getFullyQualifiedName());
    }

	public void testREL_CR_AssociationCreatesField() throws Exception {
	    drawRelationship(_sourceClass, _targetClass, Association);
	    assertNotNull("Field was not created for relationship", findField(
	    		_sourceClass, "targetClass"));
	}

	public void testREL_CR_CompositionCreatesField() throws Exception {
	    drawRelationship(_sourceClass, _targetClass, Composition);
	    assertNotNull(FAIL_FIELD_ABSENT, findField(
	    		_sourceClass, "targetClass"));
	}
	
	public void testREL_CR_NoConstructor() throws Exception {
		assertNotNull(FAIL_DRAW_RELATIONSHIP,
				drawRelationship(_targetClass, _sourceClass, Composition));
	}
	
	public void testACTION_IncrementallyExplore() throws Exception {
		// hide the two other types
		_targetClass.removeFromParent();
		_interface.removeFromParent();
		assertCount(TypeModel.class, 1);
		
		// find all types connected to the source
		// should display java.lang.String and the other two types
		selectContextMenuItem(_sourceClass, new IncrementalExploreAction());
		assertCount(TypeModel.class, 4);
	}
	
	public void testACTION_AlterRelationshipVisibility() throws Exception {
		// hide the generalization between source and target
		RelationshipModel rModel =
			getRelationship(_sourceClass, _targetClass, Generalization);
		EDITOR.execute(new AlterRelationshipVisibilityCommand(
				EDITOR, GeneralizationPart.class));
		
		// ensure the relationship isn't visible
		assertVisible(rModel, false);
		
		// show the relationship
		EDITOR.execute(new AlterRelationshipVisibilityCommand(
				EDITOR, GeneralizationPart.class));
		assertVisible(rModel, true);
	}
	
	public void testREL_Cardinal_Count() throws Exception {
		RelationshipModel rModel;
		
		// ensure the cardinality of the relationship is 3
		rModel = drawRelationship(
				_sourceClass, _targetClass, Association, 3);
		assertEquals(FAIL_INCORRECT_RELATIONSHIP_CARDINALITY, "3",
				rModel.getCardinality());
		
		// ensure the cardinality of the relationship is 5
		rModel = drawRelationship(
				_sourceClass, _targetClass, Association, 2);
		assertEquals(FAIL_INCORRECT_RELATIONSHIP_CARDINALITY, "5",
				rModel.getCardinality());
	}
	
	public void testREL_Recursive_Bendpoints() throws Exception {
		int ds = 20;
		
		RelationshipModel rModel = drawRelationship(
				_targetClass, _targetClass, Composition);
		assertNotNull(FAIL_DRAW_RELATIONSHIP, rModel);
		RelationshipFigure rFigure = (RelationshipFigure) getFigure(rModel);
		IFigure anchor = getFigure(rModel.getSourceModel()); 
		PointList bendpoints = rFigure.getPoints();
		Dimension difference[] = new Dimension[bendpoints.size()];
		
		for (int x = 0; x < bendpoints.size(); x++) {
			difference[x] = anchor.getBounds().getTopRight().getDifference(
					bendpoints.getPoint(x)); 
		}
		
		// move the recursive relationship's anchor
		_targetClass.setLocation(new Point(
				_targetClass.getLocation().x,
				_targetClass.getLocation().y - ds));
		_targetClass.setSize(new Dimension(
				_targetClass.getSize().width + ds,
				_targetClass.getSize().height + ds));

		for (int x = 0; x < bendpoints.size(); x++) {
			assertEquals(FAIL_REDRAW, difference[x],
					anchor.getBounds().getTopRight().getDifference(
							bendpoints.getPoint(x)));
		}
	}
	
	public void testREL_TwoTargetsSameSimpleNames() throws Exception {
		IPackageFragment frag1 = createPackage("pack1");
		IPackageFragment frag2 = createPackage("pack2");
		
		// create target classes with the same simple names
		TypeModel target1 = createClass(_project, frag1, "Ditto", "");
		TypeModel target2 = createClass(_project, frag2, "Ditto", "");
		target1.setLocation(400, 0);
		target2.setLocation(400, 200);
		
		// draw relationships from the source to both target classes
		RelationshipModel rModel1 =
			drawRelationship(_sourceClass, target1, Association);
		RelationshipModel rModel2 =
			drawRelationship(_sourceClass, target2, Association);
		
		assertNotNull(FAIL_DRAW_RELATIONSHIP, rModel1);
		assertNotNull(FAIL_DRAW_RELATIONSHIP, rModel2);
		assertNotSame(FAIL_IMPORT, rModel1, rModel2);
	}
	
	public void testREL_InnerClass() throws Exception {
		TypeModel outer = createClass("OuterSource", ""
				+ "\tclass InnerSource {}\n"
				+ "\tclass InnerTarget {}");
		
		TypeModel innerSource = addType(EDITOR, outer.getType().getTypes()[0]);
		TypeModel innerTarget = addType(EDITOR, outer.getType().getTypes()[1]);
		innerSource.setLocation(400, 0);
		innerTarget.setLocation(400, 200);
		
		assertNotNull(FAIL_DRAW_RELATIONSHIP,
				drawRelationship(
						innerSource, innerTarget, Generalization));
	}
	
	public void testREL_Generics() throws Exception {
		// create a generic relationship
		RelationshipModel rCModel =
			drawRelationship(_sourceClass, _targetClass, Association, 0);
		assertEquals(FAIL_DRAW_RELATIONSHIP, "0..*",
				rCModel.getCardinality());
		
		// create a normal relationship
		drawRelationship(_sourceClass, _targetClass, Association, 2);
		assertEquals(FAIL_DRAW_RELATIONSHIP, "2..*",
				rCModel.getCardinality());
	}
	
	public void testCOM_UndoRedo() throws Exception {
		abstract class CommandTest {
			protected Command _command;
			
			public CommandTest(Command command) {
				if (!command.canUndo()) {
					GreenException.illegalOperation(
							"Cannot run test on a non-undoable command");
				}
				
				_command = command;
			}
			
			public Exception run(DiagramEditor editor) {
				try {
					// perform execution, undo, and redo, checking to ensure the
					// appropriate behavior is observed
					this.setUp();
					editor.execute(_command);
					if (!postExecute()) throw new Exception(
							"postExecute() failed after executing the command");
					editor.undo();
					if (!postUndo()) throw new Exception(
							"postUndo() failed");
					editor.redo();
					if (!postExecute()) throw new Exception(
							"postExecute() failed after redoing the command");
					editor.undo();
					this.tearDown();
					
					return null;
				} catch (Exception e) {
					return new Exception(_command.getClass() + ": " +
							e.getLocalizedMessage());
				}
			}
			
			protected abstract boolean postExecute();

			protected abstract boolean postUndo();
			
			protected void setUp() {}
			
			protected void tearDown() {}
		}
		
		List<CommandTest> tests = new ArrayList<CommandTest>();
		List<Exception> exceptions = new ArrayList<Exception>();
		
		// things to be used in various tests
		final String newNoteText = "jabber";
		final RelationshipFigure rGenFigure =
			(RelationshipFigure) getFigure(_rGeneralization); 
		final NoteModel noteModel = new NoteModel("Undo/Redo");
		CreateCommand createCommand = new CreateCommand(ROOT);
		createCommand.setChild(noteModel);
		createCommand.setLocation(new Point(1, 1));
		createCommand.setSize(new Dimension(100, 100));

		BendpointRequest createRequest = new BendpointRequest();
		createRequest.setIndex(0);
		createRequest.setLocation(new Point(1, 1));
		createRequest.setSource((RelationshipPart) getPart(_rGeneralization));
		
		BendpointRequest deleteRequest = new BendpointRequest();
		createRequest.setIndex(0);
		createRequest.setSource((RelationshipPart) getPart(_rGeneralization));

		BendpointRequest moveRequest = new BendpointRequest();
		createRequest.setIndex(0);
		createRequest.setLocation(new Point(1, 2));
		createRequest.setSource((RelationshipPart) getPart(_rGeneralization));
		
		final CreateBendpointCommand createBendpointCommand =
			new CreateBendpointCommand(rGenFigure, createRequest);

		tests.add(new CommandTest(new AddFieldCommand("int a", "a", _targetClass)) {
			protected boolean postExecute() {
				return getFields(_targetClass).size() == 1;
			}

			protected boolean postUndo() {
				return getFields(_targetClass).size() == 0;
			}
		});
		
		tests.add(new CommandTest(new AddMethodCommand("a", "void a() {}", _targetClass)) {
			protected boolean postExecute() {
				return getMethods(_targetClass).size() == 1;
			}

			protected boolean postUndo() {
				return getMethods(_targetClass).size() == 0;
			}
		});

		tests.add(new CommandTest(new AlterRelationshipVisibilityCommand(EDITOR, getPart(_rGeneralization).getClass())) {
			protected boolean postExecute() {
				return !rGenFigure.isVisible();
			}

			protected boolean postUndo() {
				return !postExecute();
			}
		});

		tests.add(new CommandTest(createCommand) {
			protected boolean postExecute() {
				return ROOT.getChildren().contains(noteModel);
			}

			protected boolean postUndo() {
				return !postExecute();
			}
		});

		tests.add(new CommandTest(new DeleteNoteCommand(noteModel)) {
			protected void setUp() {
				ROOT.addChild(noteModel);
				((DeleteCommand) _command).suppressMessage(true);
			}
			
			protected boolean postExecute() {
				return !ROOT.getChildren().contains(noteModel);
			}

			protected boolean postUndo() {
				return !postExecute();
			}
			
			protected void tearDown() {
				noteModel.removeFromParent();
			}
		});


		tests.add(new CommandTest(new EditNoteCommand(noteModel, newNoteText)) {
			protected void setUp() {
				ROOT.addChild(noteModel);
			}
			
			protected boolean postExecute() {
				Label label = (Label) getFigure(noteModel);
				return label.getText().equals(newNoteText);
			}

			protected boolean postUndo() {
				return !postExecute();
			}
			
			protected void tearDown() {
				noteModel.removeFromParent();
			}
		});

		tests.add(new CommandTest(new HideRelationshipCommand(_rGeneralization)) {
			protected boolean postExecute() {
				return !rGenFigure.isVisible();
			}

			protected boolean postUndo() {
				return !postExecute();
			}
		});
		
		tests.add(new CommandTest(new HideTypeCommand(_sourceClass)) {
			protected boolean postExecute() {
				EDITOR.refresh();
				return !_sourceClass.isVisible();
			}

			protected boolean postUndo() {
				return !postExecute();
			}
		});

		tests.add(new CommandTest(new ShowRelationshipCommand(_rGeneralization)) {
			protected void setUp() {
				_rGeneralization.setVisible(false);
			}
			
			protected boolean postExecute() {
				return rGenFigure.isVisible();
			}

			protected boolean postUndo() {
				return !postExecute();
			}
			
			protected void tearDown() {
				_rGeneralization.setVisible(true);
			}
		});

		for (CommandTest test : tests) {
			Exception e = test.run(EDITOR);
			
			if (e != null) {
				exceptions.add(e);
			}
		}
		
		tests.add(new CommandTest(createBendpointCommand) {
			protected boolean postExecute() {
				return (rGenFigure.getPoints().size() == 1);
			}

			protected boolean postUndo() {
				return (rGenFigure.getPoints().size() == 0);
			}
		});
		
		tests.add(new CommandTest(new DeleteBendpointCommand(deleteRequest)) {
			protected void setUp() {
				EDITOR.execute(createBendpointCommand);
			}
			
			protected boolean postExecute() {
				return (rGenFigure.getPoints().size() == 0);
			}

			protected boolean postUndo() {
				return (rGenFigure.getPoints().size() == 1);
			}
			
			protected void tearDown() {
				EDITOR.undo();
			}
		});

		tests.add(new CommandTest(new MoveBendpointCommand(moveRequest)) {
			GreenBendpoint bendpoint;
			Point oldBendpointLocation;
			
			protected void setUp() {
				EDITOR.execute(createBendpointCommand);
		    	List constraint =
		    		(List) rGenFigure.getConnectionRouter().getConstraint(
		    				rGenFigure);
		    	List<Bendpoint> list =
		    		(ArrayList<Bendpoint>) constraint;
				bendpoint = (GreenBendpoint) list.get(0);

				oldBendpointLocation = bendpoint.getLocation();
			}
			
			protected boolean postExecute() {
				return bendpoint.getLocation() != oldBendpointLocation;
			}

			protected boolean postUndo() {
				return !postExecute();
			}
			
			protected void tearDown() {
				EDITOR.undo();
			}
		});

		if (exceptions.size() != 0) {
			fail("The following commands were unsuccessful: " + exceptions);
		}
	}
	
	public void testREL_CR_RemoveMulti() {
		assertEquals("Wrong number of relationships to begin with", 2,
				ROOT.getRelationships().size());
		
		RelationshipModel r1 =
			drawRelationship(_sourceClass, _targetClass, Association, 1);
		RelationshipModel r2 =
			drawRelationship(_sourceClass, _targetClass, Composition, 1);
		
		assertEquals("Not all relationships were added", 4,
				ROOT.getRelationships().size());

		delete(r2);
		delete(r1);

		assertEquals("Not all relationships were removed", 2,
				ROOT.getRelationships().size());
	}

	public void testREL_CR_Invalid1() throws Exception {
		TypeModel interfac = createInterface("AnotherInterface");
		interfac.setLocation(200, 200);
		TypeModel targetClass = createClass("AnotherTargetClass");
		targetClass.setLocation(400, 0);
		
		/* interfaces may only be the sources of realizations with other
		 * interfaces; all other relationships should throw an exception
		 */
		assertIllegalRelationship(_interface, _sourceClass, Association);
//		assertIllegalRelationship(_interface, _sourceClass, Composition);
//		assertIllegalRelationship(_interface, _sourceClass, Dependency);
//		assertIllegalRelationship(_interface, _sourceClass, Generalization);
//		assertIllegalRelationship(_interface, _sourceClass, Realization);
//
//		assertIllegalRelationship(_interface, interfac, Association);
//		assertIllegalRelationship(_interface, interfac, Composition);
//		assertIllegalRelationship(_interface, interfac, Dependency);
//		assertIllegalRelationship(_interface, interfac, Realization);
//		
//		// a class cannot generalize an interface
//		assertIllegalRelationship(_sourceClass, _interface, Generalization);
//
//		// multiple generalizations by one class are illegal
//		assertIllegalRelationship(_sourceClass, targetClass, Generalization);
	}
	
	public void testREL_CR_Invalid2() throws Exception {
		TypeModel interfac = createInterface("AnotherInterface");
		interfac.setLocation(200, 200);
		TypeModel targetClass = createClass("AnotherTargetClass");
		targetClass.setLocation(400, 0);
		
		/* interfaces may only be the sources of realizations with other
		 * interfaces; all other relationships should throw an exception
		 */
//		assertIllegalRelationship(_interface, _sourceClass, Association);
		assertIllegalRelationship(_interface, _sourceClass, Composition);
//		assertIllegalRelationship(_interface, _sourceClass, Dependency);
//		assertIllegalRelationship(_interface, _sourceClass, Generalization);
//		assertIllegalRelationship(_interface, _sourceClass, Realization);
//
//		assertIllegalRelationship(_interface, interfac, Association);
//		assertIllegalRelationship(_interface, interfac, Composition);
//		assertIllegalRelationship(_interface, interfac, Dependency);
//		assertIllegalRelationship(_interface, interfac, Realization);
//		
//		// a class cannot generalize an interface
//		assertIllegalRelationship(_sourceClass, _interface, Generalization);
//
//		// multiple generalizations by one class are illegal
//		assertIllegalRelationship(_sourceClass, targetClass, Generalization);
	}
	
	public void testREL_CR_Invalid3() throws Exception {
		TypeModel interfac = createInterface("AnotherInterface");
		interfac.setLocation(200, 200);
		TypeModel targetClass = createClass("AnotherTargetClass");
		targetClass.setLocation(400, 0);
		
		/* interfaces may only be the sources of realizations with other
		 * interfaces; all other relationships should throw an exception
		 */
//		assertIllegalRelationship(_interface, _sourceClass, Association);
//		assertIllegalRelationship(_interface, _sourceClass, Composition);
		assertIllegalRelationship(_interface, _sourceClass, Dependency);
//		assertIllegalRelationship(_interface, _sourceClass, Generalization);
//		assertIllegalRelationship(_interface, _sourceClass, Realization);
//
//		assertIllegalRelationship(_interface, interfac, Association);
//		assertIllegalRelationship(_interface, interfac, Composition);
//		assertIllegalRelationship(_interface, interfac, Dependency);
//		assertIllegalRelationship(_interface, interfac, Realization);
//		
//		// a class cannot generalize an interface
//		assertIllegalRelationship(_sourceClass, _interface, Generalization);
//
//		// multiple generalizations by one class are illegal
//		assertIllegalRelationship(_sourceClass, targetClass, Generalization);
	}
	
	public void testREL_CR_Invalid4() throws Exception {
		TypeModel interfac = createInterface("AnotherInterface");
		interfac.setLocation(200, 200);
		TypeModel targetClass = createClass("AnotherTargetClass");
		targetClass.setLocation(400, 0);
		
		/* interfaces may only be the sources of realizations with other
		 * interfaces; all other relationships should throw an exception
		 */
//		assertIllegalRelationship(_interface, _sourceClass, Association);
//		assertIllegalRelationship(_interface, _sourceClass, Composition);
//		assertIllegalRelationship(_interface, _sourceClass, Dependency);
		assertIllegalRelationship(_interface, _sourceClass, Generalization);
//		assertIllegalRelationship(_interface, _sourceClass, Realization);
//
//		assertIllegalRelationship(_interface, interfac, Association);
//		assertIllegalRelationship(_interface, interfac, Composition);
//		assertIllegalRelationship(_interface, interfac, Dependency);
//		assertIllegalRelationship(_interface, interfac, Realization);
//		
//		// a class cannot generalize an interface
//		assertIllegalRelationship(_sourceClass, _interface, Generalization);
//
//		// multiple generalizations by one class are illegal
//		assertIllegalRelationship(_sourceClass, targetClass, Generalization);
	}
	
	public void testREL_CR_Invalid5() throws Exception {
		TypeModel interfac = createInterface("AnotherInterface");
		interfac.setLocation(200, 200);
		TypeModel targetClass = createClass("AnotherTargetClass");
		targetClass.setLocation(400, 0);
		
		/* interfaces may only be the sources of realizations with other
		 * interfaces; all other relationships should throw an exception
		 */
//		assertIllegalRelationship(_interface, _sourceClass, Association);
//		assertIllegalRelationship(_interface, _sourceClass, Composition);
//		assertIllegalRelationship(_interface, _sourceClass, Dependency);
//		assertIllegalRelationship(_interface, _sourceClass, Generalization);
		assertIllegalRelationship(_interface, _sourceClass, Realization);

//		assertIllegalRelationship(_interface, interfac, Association);
//		assertIllegalRelationship(_interface, interfac, Composition);
//		assertIllegalRelationship(_interface, interfac, Dependency);
//		assertIllegalRelationship(_interface, interfac, Realization);
//		
//		// a class cannot generalize an interface
//		assertIllegalRelationship(_sourceClass, _interface, Generalization);
//
//		// multiple generalizations by one class are illegal
//		assertIllegalRelationship(_sourceClass, targetClass, Generalization);
	}
	
	public void testREL_CR_Invalid6() throws Exception {
		TypeModel interfac = createInterface("AnotherInterface");
		interfac.setLocation(200, 200);
		TypeModel targetClass = createClass("AnotherTargetClass");
		targetClass.setLocation(400, 0);
		
		/* interfaces may only be the sources of realizations with other
		 * interfaces; all other relationships should throw an exception
		 */
//		assertIllegalRelationship(_interface, _sourceClass, Association);
//		assertIllegalRelationship(_interface, _sourceClass, Composition);
//		assertIllegalRelationship(_interface, _sourceClass, Dependency);
//		assertIllegalRelationship(_interface, _sourceClass, Generalization);
//		assertIllegalRelationship(_interface, _sourceClass, Realization);

		assertIllegalRelationship(_interface, interfac, Association);
//		assertIllegalRelationship(_interface, interfac, Composition);
//		assertIllegalRelationship(_interface, interfac, Dependency);
//		assertIllegalRelationship(_interface, interfac, Realization);
//		
//		// a class cannot generalize an interface
//		assertIllegalRelationship(_sourceClass, _interface, Generalization);
//
//		// multiple generalizations by one class are illegal
//		assertIllegalRelationship(_sourceClass, targetClass, Generalization);
	}
	
	public void testREL_CR_Invalid7() throws Exception {
		TypeModel interfac = createInterface("AnotherInterface");
		interfac.setLocation(200, 200);
		TypeModel targetClass = createClass("AnotherTargetClass");
		targetClass.setLocation(400, 0);
		
		/* interfaces may only be the sources of realizations with other
		 * interfaces; all other relationships should throw an exception
		 */
//		assertIllegalRelationship(_interface, _sourceClass, Association);
//		assertIllegalRelationship(_interface, _sourceClass, Composition);
//		assertIllegalRelationship(_interface, _sourceClass, Dependency);
//		assertIllegalRelationship(_interface, _sourceClass, Generalization);
//		assertIllegalRelationship(_interface, _sourceClass, Realization);
//
//		assertIllegalRelationship(_interface, interfac, Association);
		assertIllegalRelationship(_interface, interfac, Composition);
//		assertIllegalRelationship(_interface, interfac, Dependency);
//		assertIllegalRelationship(_interface, interfac, Realization);
//		
//		// a class cannot generalize an interface
//		assertIllegalRelationship(_sourceClass, _interface, Generalization);
//
//		// multiple generalizations by one class are illegal
//		assertIllegalRelationship(_sourceClass, targetClass, Generalization);
	}
	
	public void testREL_CR_Invalid8() throws Exception {
		TypeModel interfac = createInterface("AnotherInterface");
		interfac.setLocation(200, 200);
		TypeModel targetClass = createClass("AnotherTargetClass");
		targetClass.setLocation(400, 0);
		
		/* interfaces may only be the sources of realizations with other
		 * interfaces; all other relationships should throw an exception
		 */
//		assertIllegalRelationship(_interface, _sourceClass, Association);
//		assertIllegalRelationship(_interface, _sourceClass, Composition);
//		assertIllegalRelationship(_interface, _sourceClass, Dependency);
//		assertIllegalRelationship(_interface, _sourceClass, Generalization);
//		assertIllegalRelationship(_interface, _sourceClass, Realization);
//
//		assertIllegalRelationship(_interface, interfac, Association);
//		assertIllegalRelationship(_interface, interfac, Composition);
		assertIllegalRelationship(_interface, interfac, Dependency);
//		assertIllegalRelationship(_interface, interfac, Realization);
//		
//		// a class cannot generalize an interface
//		assertIllegalRelationship(_sourceClass, _interface, Generalization);
//
//		// multiple generalizations by one class are illegal
//		assertIllegalRelationship(_sourceClass, targetClass, Generalization);
	}
	
	public void testREL_CR_Invalid9() throws Exception {
		TypeModel interfac = createInterface("AnotherInterface");
		interfac.setLocation(200, 200);
		TypeModel targetClass = createClass("AnotherTargetClass");
		targetClass.setLocation(400, 0);
		
		/* interfaces may only be the sources of realizations with other
		 * interfaces; all other relationships should throw an exception
		 */
//		assertIllegalRelationship(_interface, _sourceClass, Association);
//		assertIllegalRelationship(_interface, _sourceClass, Composition);
//		assertIllegalRelationship(_interface, _sourceClass, Dependency);
//		assertIllegalRelationship(_interface, _sourceClass, Generalization);
//		assertIllegalRelationship(_interface, _sourceClass, Realization);
//
//		assertIllegalRelationship(_interface, interfac, Association);
//		assertIllegalRelationship(_interface, interfac, Composition);
//		assertIllegalRelationship(_interface, interfac, Dependency);
		assertIllegalRelationship(_interface, interfac, Realization);
		
//		// a class cannot generalize an interface
//		assertIllegalRelationship(_sourceClass, _interface, Generalization);
//
//		// multiple generalizations by one class are illegal
//		assertIllegalRelationship(_sourceClass, targetClass, Generalization);
	}
	
	public void testREL_CR_Invalid10() throws Exception {
		TypeModel interfac = createInterface("AnotherInterface");
		interfac.setLocation(200, 200);
		TypeModel targetClass = createClass("AnotherTargetClass");
		targetClass.setLocation(400, 0);
		
		/* interfaces may only be the sources of realizations with other
		 * interfaces; all other relationships should throw an exception
		 */
//		assertIllegalRelationship(_interface, _sourceClass, Association);
//		assertIllegalRelationship(_interface, _sourceClass, Composition);
//		assertIllegalRelationship(_interface, _sourceClass, Dependency);
//		assertIllegalRelationship(_interface, _sourceClass, Generalization);
//		assertIllegalRelationship(_interface, _sourceClass, Realization);
//
//		assertIllegalRelationship(_interface, interfac, Association);
//		assertIllegalRelationship(_interface, interfac, Composition);
//		assertIllegalRelationship(_interface, interfac, Dependency);
//		assertIllegalRelationship(_interface, interfac, Realization);
		
		// a class cannot generalize an interface
		assertIllegalRelationship(_sourceClass, _interface, Generalization);

//		// multiple generalizations by one class are illegal
//		assertIllegalRelationship(_sourceClass, targetClass, Generalization);
	}
	
public void testREL_CR_Invalid11() throws Exception {
		TypeModel interfac = createInterface("AnotherInterface");
		interfac.setLocation(200, 200);
		TypeModel targetClass = createClass("AnotherTargetClass");
		targetClass.setLocation(400, 0);
		
		/* interfaces may only be the sources of realizations with other
		 * interfaces; all other relationships should throw an exception
		 */
//		assertIllegalRelationship(_interface, _sourceClass, Association);
//		assertIllegalRelationship(_interface, _sourceClass, Composition);
//		assertIllegalRelationship(_interface, _sourceClass, Dependency);
//		assertIllegalRelationship(_interface, _sourceClass, Generalization);
//		assertIllegalRelationship(_interface, _sourceClass, Realization);
//
//		assertIllegalRelationship(_interface, interfac, Association);
//		assertIllegalRelationship(_interface, interfac, Composition);
//		assertIllegalRelationship(_interface, interfac, Dependency);
//		assertIllegalRelationship(_interface, interfac, Realization);
//		
//		// a class cannot generalize an interface
//		assertIllegalRelationship(_sourceClass, _interface, Generalization);

		// multiple generalizations by one class are illegal
		assertIllegalRelationship(_sourceClass, targetClass, Generalization);
	}
	
}