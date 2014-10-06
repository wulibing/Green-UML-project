/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package edu.buffalo.cse.green.test.core;

import static edu.buffalo.cse.green.constants.DialogStrings.DIALOG_CHOOSE_TYPE_TITLE;
import static edu.buffalo.cse.green.constants.DialogStrings.DIALOG_EDIT_NOTE_TITLE;
import static edu.buffalo.cse.green.constants.DialogStrings.WIZARD_ADD_FIELD_TITLE;
import static edu.buffalo.cse.green.constants.DialogStrings.WIZARD_ADD_METHOD_TITLE;
import static edu.buffalo.cse.green.test.core.PaletteTool.Association;
import static edu.buffalo.cse.green.test.core.PaletteTool.Composition;
import static edu.buffalo.cse.green.test.core.PaletteTool.Dependency;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;

import abbot.tester.swt.WidgetTester;
import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.action.ContextAction;
import edu.buffalo.cse.green.editor.action.EditNoteAction;
import edu.buffalo.cse.green.editor.controller.AbstractPart;
import edu.buffalo.cse.green.editor.controller.RootPart;
import edu.buffalo.cse.green.editor.controller.TypePart;
import edu.buffalo.cse.green.editor.controller.policies.CreateRelationshipCommand;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.FieldModel;
import edu.buffalo.cse.green.editor.model.MethodModel;
import edu.buffalo.cse.green.editor.model.NoteModel;
import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.editor.model.commands.AddJavaElementCommand;

/** @author bcmartin */
public abstract class EnhancedTest extends TestCase {
	protected static final String DEFAULT_FIELD_NAME = "_a";
    protected static final String DEFAULT_METHOD_NAME = "someMethod";
    protected static final String DEFAULT_PACKAGE_NAME = "testPackage";
    protected static final String DEFAULT_TYPE = "int";
    protected static final String DEFAULT_TYPE_NAME = "SourceClass";
    protected static final String RENAMED_STRING = "Renamed";
    protected static final String TARGET_PACKAGE_NAME = "toPack";

    protected static final Robot TESTING_ROBOT = new Robot();
	protected static final String FAIL_EXCEPTION = "An exception occurred";
    protected static final String FAIL_IMPORT =
    	"The imports were not added correctly";
	protected static final String FAIL_UNIMPLEMENTED_ERROR =
		"You haven't implemented this test yet";
    protected static final String FAIL_INCORRECT_RELATIONSHIP_CARDINALITY =
    	"The relationship's cardinality is incorrect";
	protected static final String FAIL_INCORRECT_RENAMED_FIELD =
		"Renamed field name is incorrect";
    protected static final String FAIL_INCORRECT_RENAMED_METHOD =
    	"Refactored method name is incorrect";
    protected static final String FAIL_INCORRECT_RENAMED_PACKAGE =
    	"Renamed package name is incorrect";
	protected static final String FAIL_INCORRECT_RENAMED_TYPE =
		"Renamed type name is incorrect";
	protected static final String FAIL_TYPE_DOES_NOT_EXIST =
		"Type cannot be found";
	protected static final String FAIL_FIELD_COUNT = "Wrong number of fields";
	protected static final String FAIL_INCORRECT_CODE =
		"The correct code was not added";
	protected static final String FAIL_METHOD_COUNT = "Wrong number of methods";
	protected static final String FAIL_METHOD_ABSENT = "Method was not added";
	protected static final String FAIL_FIELD_ABSENT = "Field was not added";
	protected static final String FAIL_DELETE = "Deletion failed";
	protected static final String FAIL_DRAW_RELATIONSHIP =
		"The relationship wasn't drawn";
	protected static final String FAIL_EDITOR_REFACTOR =
		"The refactor wasn't handled by the editor";
	protected static final String FAIL_ICON = "Incorrect icon acquired";
	protected static final String FAIL_ICON_KEY =
		"The mapping keys are inconsistent";
	protected static final String FAIL_REDRAW =
		"The figure wasn't redrawn correctly";
	protected static final String FAIL_SETTEXT =
		"The text was not correctly set";
	protected static final String FAIL_VISIBILITY = "Visibility is incorrect";
	protected static final String FAIL_ILLEGAL_RELATIONSHIP =
		"An illegal relationship was drawn";
    
    protected Project _project;
    protected IPackageFragment _package;
    protected DiagramEditor EDITOR;
    protected RootModel ROOT;
	protected RootPart ROOTPART;
    protected IProgressMonitor PM = PlugIn.getEmptyProgressMonitor();
	
	protected final void setUp() throws Exception {
		if (doSetup()) {
			if (_project != null) {
				GreenException.illegalOperation(
						"setUp() should not be called manually");
			}
			
			_project = new Project("TestProject", null, true);
			_package = _project.createPackage(DEFAULT_PACKAGE_NAME);
			
			createEditor();
		}
        
        setup();
    }

    protected void setup() throws Exception {
        // do nothing by default
    }
    
	protected void createEditor() throws Exception {
		EDITOR = openEditor(getName());
		ROOT = EDITOR.getRootModel();
		ROOTPART = EDITOR.getRootPart();
	}

	/**
     * Waits until the test completes before running the next.
     * 
     * @throws Exception
     */
    protected final void tearDown() throws Exception {
        closeEditors();
        _project.dispose();

        super.tearDown();
    }

//-Eclipse Instance based-------------------------------------------------------
    /**
     * Gets the default plugin's workbench. 
     * 
     * @return A workbench on which the plugin is installed.
     */
    private static IWorkbench getDefaultWorkbench() {
        return PlugIn.getDefault().getWorkbench();
    }

    /**
     * Gets the default display of the default plugin's workbench.
     * 
     * @return
     */
    protected static final Display getDisplay() {
        return getDefaultWorkbench().getDisplay();
    }

    /**
     * Gets the palette's control.
     * 
     * @param editor - The editor which contains the palette.
     * @return The palette's control.
     */
    protected Control getPaletteControl() {
        return EDITOR.getRootPart().getViewer().getEditDomain()
                .getPaletteViewer().getControl();
    }

    /**
     * Clicks on the figure representing a given model.
     * 
     * @param editor - the editor the model is in
     * @param model - the model whose figure will be clicked
     */
    protected void leftClick(AbstractModel model) {
        AbstractPart editPart = getPart(model);

        if (editPart instanceof TypePart) {
            TypeModel typeModel = (TypeModel) editPart.getModel();
            editPart = getPart(typeModel.getNameCompartmentModel());
        }
        
        if (editPart == null) {
            throw new IllegalArgumentException("null edit part");
        }

        if (model instanceof TypeModel) {
        	Point location = TESTING_ROBOT.getLocation(editPart);
        	Control control = null;
            
            while (control == null) {
                control = editPart.getViewer().getControl();
                delay(10);
            }
        
        	leftClick(control, location.x,
        			location.y - getFigure(model).getSize().height);
        } else {
        	TESTING_ROBOT.actionClick(editPart);
        }
        
        
        waitForIdle();
    }
    
    /**
     * See EnhancedTest#selectContextMenuItem(AbstractModel, ContextAction)
     * 
     * @param model - The model to right-click on
     * @param action - The type of action to run
     * @param thread - The thread to run before the context action is activated
     */
    protected void selectContextMenuItem(AbstractModel model, ContextAction action, Thread thread) {
    	thread.start();
    	selectContextMenuItem(model, action);
    	waitForThread(thread);
    }

    /**
     * Selects the appropriate menu item.
     * 
     * This method uses getMenuItemPosition(...) to find the menu item with the given class in its
     * submenu. The list of submenus passed in to said method is the strings that represent the
     * labels of the submenus that must be traversed to find the menu item. The list of values
     * passed back (through the same list) is a collection of <code>Integer</code>s that can be
     * used to calculate the number of keystrokes actionShortcutMenuActivate(...) uses to reach
     * each submenu.
     * 
     * @param model - The model to right-click on 
     * @param action - The type of action to run
     */
    protected void selectContextMenuItem(AbstractModel model, ContextAction action) {
        // save (in case refactoring occurs)
    	save();
    	
    	waitForIdle();
        leftClick(model);

        List<String> submenus = action.getSubMenuLabels();
        List<Integer> submenuIndices = new ArrayList<Integer>();
        int itemPos = getMenuItemPosition(action.getClass(), submenus,
        		submenuIndices, model);
        leftClick(model);
        runThread(TESTING_ROBOT.actionShortcutMenuActivate(itemPos, submenuIndices));
        waitForIdle();
    }

    /**
     * Selects the appropriate menu item. See selectContextMenuItem(...) above.
     * 
     * @param editor
     * @param model
     * @param action - The type of action to run
     * @param subMenuPos - The index of the menu item (starts at 1)
     */
    protected void selectContextMenuItem(AbstractModel model, ContextAction action, int subMenuPos) {
        // save (in case refactoring occurs)
    	save();

    	waitForIdle();
        leftClick(model);

        // get a list of submenus
        // this must be assigned - it is destroyed in getMenuItemPosition()
        // sorry to be a hackneyed jerk
        List<String> submenus = action.getSubMenuLabels();
        List<Integer> submenuIndices = new ArrayList<Integer>();
        getMenuItemPosition(action.getClass(), submenus, submenuIndices, model);
        leftClick(model);
        runThread(TESTING_ROBOT.actionShortcutMenuActivate(subMenuPos, submenuIndices));
        waitForIdle();
    }
    
	/**
	 * @param editor - The editor to select the item in.
	 * @param klass - The class of the menu item to select.
	 * @param submenus - The strings of the submenus to traverse.
	 * @param model - The context for the menu.
	 * @return The position of the menu item (one-based).
	 */
	private int getMenuItemPosition(Class klass,
			List<String> submenus, List<Integer> submenuIndices,
			AbstractModel model) {
	    IMenuManager menuManager = getMenuManager(EDITOR);
	    
	    for (String menuLabel : submenus) {
	    	List<Integer> numInvisible = new ArrayList<Integer>();
	        int menuPos = findSubmenu(menuManager, menuLabel, numInvisible);
            menuManager = (IMenuManager) menuManager.getItems()[menuPos - 1];
            
            int numInvisibleItems = numInvisible.get(0);
            submenuIndices.add(menuPos - numInvisibleItems);
        }
	    
	    IContributionItem[] menuItems = menuManager.getItems();
	    int invisible = 0;
	    
	    for (int x = 0; x < menuItems.length; x++) {
	    	if (!menuItems[x].isVisible()) {
	    	    invisible++;
	    	}
	        
	        if (menuItems[x] instanceof ActionContributionItem) {
	    	    ActionContributionItem action = (ActionContributionItem) menuItems[x];
	    	    if (klass.equals(action.getAction().getClass())) {
	    	        return x + 1 - invisible;
	    	    }
	    	}
	    }
	    
	    fail("The menu item specified by " + klass + " was not found.");
		return -1;
	}
    
	/**
	 * @param label - The label of the menu.
	 * @param model - The context for the menu.
	 * @return the position of the menu item (one-based).
	 */
	private int findSubmenu(IMenuManager menuManager, String label,
			List<Integer> numInvisible) {
		int invisible = 0;
	    
	    IContributionItem[] items = menuManager.getItems();
        for (int i = 0; i < items.length; i++) {
        	if (!items[i].isVisible()) {
        		invisible++;
            }
            
            if (items[i] instanceof MenuManager) {
                MenuManager subMenu = (MenuManager) items[i];
                
                if (subMenu.getMenuText().equals(label)) {
                    if (!subMenu.isVisible()) {
                        throw new IllegalArgumentException("Submenu is invisible");
                    }
                    
                    numInvisible.add(invisible);
                    return i + 1;
                }
            }
        }
        
        fail("Cannot find string with label \"" + label + "\"");
        return -1;
	}
	
	protected void leftClick(PaletteTool tool) {
		TESTING_ROBOT.actionSelectPaletteTool(
				getPaletteControl(), tool.getLocation(), getDisplay());
    }

    /**
     * Waits for a specified period of time before continuing the test
     * 
     * @param duration - The time (in milliseconds) to wait
     * @deprecated This leads to slow tests and is unnecessary. Use it only for
     * viewing the behavior of a test as it executes.
     */
	protected void delay(int duration) {
        WidgetTester.delay(duration);
    }
    
    /**
     * Default waitForIdle() action which waits on the default display.
     *
     * @deprecated there must be a better way to accomplish this
     */
    protected void waitForIdle(){
    	WidgetTester.waitForIdle(getDisplay());
    }
    
	protected void hide(AbstractModel model){
		leftClick(model);
		key('h');
	}
	
	/**
	 * Draws a relationship between two types.
	 * @param sourceModel - The UML box at the start of the relationship.
	 * @param targetModel - The UML box at the end of the relationship.
	 * @param paletteTool - The tool representing the desired relationship.
	 * @param editor - The editor to draw the relationship in.
	 */
	protected RelationshipModel drawRelationship(TypeModel sourceModel,
			TypeModel targetModel, PaletteTool tool, int cardinality) {
	    leftClick(tool);
	    
	    if (tool == Association || 
	            tool == Composition ||
	            tool == Dependency) {
	    	CreateRelationshipCommand.forceCardinality(cardinality);
	    }
	    
   	    leftClick(sourceModel);
   	    leftClick(targetModel);
   	    
	    // get the created relationship
	    return ROOT.getRelationshipCache().getRelationshipModel(
	    		sourceModel.getType(), targetModel.getType(), tool.getName());
	}
	
	private void ok() {
	    key(SWT.CTRL | SWT.CR);
	}
	
	/**
	 * Selects an implementor of an interface or an abstract class.
	 * 
	 * index denotes the nth class down from the interface/abstract class in 
	 * the Chose Type dialog window
	 * 
	 * @deprecated a better implementation that uses an <code>IType</code> is
	 * pending
	 */
	protected Thread chooseType(final int index){
		if(index < 0){
			fail("Cannot have a negative index while chosing a type.");
		}
		
		return new Thread() {
            public void run() {
            	waitForDialog(DIALOG_CHOOSE_TYPE_TITLE);
            	//Select the first class shown under the abstract/interface
            	key(SWT.ARROW_DOWN, index);
            	key(SWT.CONTROL | SWT.CR);
            }
        };
	}
	
	/**
	 * Saves the current editor.
	 */
	private void save() {
	    EDITOR.doSave(null);
	}

	/**
	 * Runs a thread and waits twenty seconds
	 * for that thread to finish.
	 * 
	 * @param thread
	 */
	private void runThread(Thread thread) {
	    thread.start();
	    waitForThread(thread);
	}
	
	private void waitForThread(Thread thread) {
	    waitForThread(thread, 20);
	}
	
	protected void undo() {
		EDITOR.undo();
	}
	
	protected void redo() {
		EDITOR.redo();
	}
	
	private IMenuManager getMenuManager(DiagramEditor editor) {
	    // the menu must be displayed at least once before the menu manager can be found
	    runThread(new Thread() {
	        public void run() {
		        key(SWT.SHIFT | SWT.F10);
		        key(SWT.ESC);
	        }
	    });
	    
	    return editor.getMenuManager();
	}
	
	/**
	 * Deletes a model from the editor and confirms the deletion dialog.
	 * 
	 * @param editor
	 * @param model
	 */
	protected void delete(final AbstractModel model) {
        waitForIdle();
		leftClick(model);
        
	    Thread thread = new Thread() {
            public void run() {
                waitForDialog("Confirm Delete");
                key(' ');
            }
        };
        
        thread.start();
        key(SWT.DEL);
        waitForThread(thread);
	    
	}
	
	protected void waitForDialog(String title) {
	    Robot.waitForShellShowing(title);
	}
	
	/**
	 * Assumes you are in a text box where "select all" is available
	 * and replaces the current text with the given text.
     *
 	 * @param text - The text to input
	 */
	private void setTextInBox(final String text) {
		runThread(new Thread() {
	        public void run() {
	            key(SWT.CTRL | 'a');
	            
	            if (text.equals("")) {
	                key(SWT.DEL);
	            } else {
	                TESTING_ROBOT.actionKeyString(text, getDisplay());
	            }
	        }
	    });
	}
	
	private void setTextInBox(final int key, final String text) {
	    Thread thread = new Thread() {
	        public void run() {
	            key(key);
	    	    setTextInBox(text);
	        }
	    };
	    
	    thread.start();
	    waitForThread(thread, 20);
	}
	
	/**
	 * Fills out the required information for a "new field" dialog. This method returns a thread which
	 * must be handled by the user.
	 * 
	 * @param name - The name of the field to create
	 * @param visibility - The desired visibility of the field (see Flags)
	 * @param typeName - The type of the field
	 */
	protected Thread fillInFieldWizard(final String name, final int visibility,
			final ReturnType type, final int modifierFlags,
			final TypeModel model) throws Exception {
		return new Thread() {
			public void run() {
				boolean isInterface = false;
				
				try {
					isInterface = isInterface(model);
				} catch (Exception e) {
					fail(FAIL_EXCEPTION);
				}
				
				// wait for the "new field" dialog
				waitForDialog(WIZARD_ADD_FIELD_TITLE);
				
	            // fill in the field's name
				setTextInBox(SWT.ALT | 'n', name);
				
				// set the desired visibility
				key(SWT.ALT | 'm');
	            
				if (!isInterface) {
					switch (visibility) {
					case Flags.AccPrivate:
						// do nothing - private is default
						break;
					case Flags.AccPublic:
						key(SWT.ARROW_LEFT, 2);
						break;
					case Flags.AccProtected:
						key(SWT.ARROW_RIGHT);
						break;
					default:
						key(SWT.ARROW_LEFT);
						break;
					}
				}
				
				// final
				if (!isInterface) {
					key(SWT.TAB);
					if(Flags.isFinal(modifierFlags)){
						key(' ');
					}
				}
				
				// static
				if (!isInterface) {
					key(SWT.TAB);
					if(Flags.isStatic(modifierFlags)){
						key(' ');
					}
				}
				
				// set return type
				key(SWT.ALT | 'r');
				for (int x = 0; x < type.getLocation(true); x++) {
					key(SWT.ARROW_RIGHT);
				}
				
				// hit "finish"
				key(SWT.ALT | 'f');
	        }
		};
	}
	
	/**
	 * Fills out the required information for a "new field" dialog. This method returns a thread which
	 * must be handled by the user.
	 * 
	 * @param name - The name of the field to create
	 * @param visibility - The desired visibility of the field (see Flags)
	 * @param returnType - The type of the field
	 * @param model - The type we are adding the method to
	 */
	protected Thread fillInMethodWizard(final String name, final int visibility,
			final ReturnType type, final int modifierFlags,
			final TypeModel model) throws Exception {
		return new Thread() {
			public void run() {
				boolean isInterface = false;
				
				try {
					isInterface = isInterface(model);
				} catch (Exception e) {
					fail(FAIL_EXCEPTION);
				}
				
				// wait for the "new method" dialog
				waitForDialog(WIZARD_ADD_METHOD_TITLE);
				
	            // fill in the method's name
				setTextInBox(SWT.ALT | 'n', name);
	            
				key(SWT.ALT | 'm');

				// set the desired visibility
				if (!isInterface) {
					switch (visibility) {
					case Flags.AccPublic:
						// do nothing - public is the default
						break;

					case Flags.AccPrivate:
						key(SWT.ARROW_RIGHT, 2);
						break;

					case Flags.AccProtected:
						key(SWT.ARROW_RIGHT, 3);
						break;

					default:
						key(SWT.ARROW_RIGHT);
						break;
					}
				}
					
				if (!isInterface) {
					// set modifiers
					key(SWT.ALT | 'm');

					key(SWT.TAB);
					if (Flags.isAbstract(modifierFlags)) {
						key(' ');
					}

					key(SWT.TAB);
					if(Flags.isFinal(modifierFlags)){
						key(' ');
					}

					key(SWT.TAB);
					if(Flags.isStatic(modifierFlags)){
						key(' ');
					}
				}
				
				// set return type
				key(SWT.ALT | 'r');
				key(SWT.ARROW_RIGHT, type.getLocation(false));
				
				// hit "finish"
				key(SWT.ALT | 'f');
	        }
		};
	}
	
	private void key(int key) {
		TESTING_ROBOT.actionKey(key, getDisplay());
		waitForIdle();
	}

	private void key(int key, int times) {
		for (int x = 0; x < times; x++) {
			key(key);
		}
	}
	
	private boolean isInterface(TypeModel model) throws Exception {
		return model.isInterface();
	}
	
	protected AbstractModel getModel(IJavaElement element) {
	    return ROOT.getModelFromElement(element); 
	}

    protected TypeModel createClass(String typeName) throws Exception {
        return createClass(_project, _package, typeName, "");
    }

    protected TypeModel createClass(String typeName, String body)
    throws Exception {
        return createClass(_project, _package, typeName, body);
    }
    
    protected TypeModel createClass(Project project, IPackageFragment packFrag,
    		String typeName, String body) throws Exception {
    	return addType(EDITOR, project.createType(packFrag, typeName + ".java",
    			"public class " + typeName + " {\n" + body + "\n}"));
    }

    protected TypeModel createSubClass(String typeName, String superclassName)
    throws Exception {
        return createSubClass(typeName, superclassName, "");
    }

    protected TypeModel createSubClass(String typeName, String superclassName,
    		String body) throws Exception {
    	return addType(EDITOR, _project.createType(_package, typeName + ".java",
    			"public class " + typeName + " extends " + superclassName
    			+ " {\n" + body + "\n}"));
    }

    protected TypeModel createInterface(String typeName) throws Exception {
        return createInterface(typeName, "");
    }

    protected TypeModel createInterface(String typeName, String body)
    throws Exception {
    	return addType(EDITOR, _project.createType(_package, typeName + ".java",
    			"public interface " + typeName + " {\n" + body + "\n}"));
    }

    protected TypeModel createSubInterface(String typeName,
            String superInterfaceName) throws Exception {
        return createSubInterface(typeName, superInterfaceName, "");
    }

    protected TypeModel createSubInterface(String typeName,
    		String superInterfaceName, String body) throws Exception {
    	return addType(EDITOR, _project.createType(_package, typeName + ".java",
    			"public interface " + typeName + " extends "
    			+ superInterfaceName + " {\n" + body + "\n}"));
    }

    protected TypeModel createImplementingClass(IPackageFragment packageFrag,
    		String typeName, String superTypeName, String[] superInterfaceNames,
    		String body) throws Exception {
    	StringBuffer typeSignature = new StringBuffer("public class ").append(typeName);
    	if (superTypeName != null && !("".equals(superTypeName))){
    		typeSignature.append(" extends ").append(superTypeName);
    	}
    	if (superInterfaceNames != null && superInterfaceNames.length > 0){
    		typeSignature.append(" implements ").append(superInterfaceNames[0]);
    		for (int i = 1; i < superInterfaceNames.length; i++) {
    			typeSignature.append(", ").append(superInterfaceNames[i]);
    		}
    	}
    	typeSignature.append(" {\n");
    	if (body != null){ typeSignature.append(body); }
    	typeSignature.append("\n}");
    	
    	return addType(EDITOR,
    			_project.createType(packageFrag, typeName + ".java",
    			typeSignature.toString()));
    }
	
    protected IPackageFragment createPackage(String pack) throws Exception {
    	return _project.createPackage(pack);
    }
    
    /**
     * Opens a <code>DiagramEditor</code> with the filename "&lt;
     * <code>diagramName</code> &gt;.dia". If the file does not exist, this
     * method will create a new one.
     * 
     * @param diagramName The filename of the diagram editor.
     * @return
     */
    private DiagramEditor openEditor(String diagramName) throws Exception {
        IFile file = _project.getJavaProject().getProject().getFile(
        		diagramName + ".dia");
        if (!file.exists()) {
        	file.create(new ByteArrayInputStream(new byte[0]), true,
        			PlugIn.getEmptyProgressMonitor());
        }
        return (DiagramEditor)
        IDE.openEditor(PlugIn.getDefault().getWorkbench()
        		.getActiveWorkbenchWindow().getActivePage(), file, true);
    }

    private void closeEditors() {
    	// save all editors
    	IWorkspaceRoot root =
    		_project.getJavaProject().getJavaModel().getWorkspace().getRoot();
    	IDE.saveAllEditors(new IResource[] {root}, false);
    	
    	while (DiagramEditor.getEditors().size() != 0) {
            DiagramEditor editor =
            	(DiagramEditor) DiagramEditor.getEditors().get(0);
            editor.getEditorSite().getPage().closeEditor(editor, false);
        }
    }

    protected List<RelationshipModel> getRelationships(DiagramEditor editor) {
    	RootModel rootModel = editor.getRootModel();
        List<RelationshipModel> relationships =
        	new ArrayList<RelationshipModel>();

        for (Iterator iter = rootModel.getChildren().iterator(); iter.hasNext();) {
            AbstractModel model = (AbstractModel) iter.next();

            if (model instanceof RelationshipModel) {
                RelationshipModel rModel = (RelationshipModel) model;
                relationships.add(rModel);
            }
        }

        return relationships;
    }
    
    protected RelationshipModel getRelationship(TypeModel sourceModel,
    		TypeModel targetModel, PaletteTool tool) {
    	return ROOT.getRelationshipCache().getRelationshipModel(
    			sourceModel.getType(), targetModel.getType(), tool.getName());
    }

    /**
     * Searches the editor for the figure part that corresponds to the given model.
     * 
     * @param rootEditPart - the topmost level edit part being searched
     * @param model - the model being searched for
     * @return the figure corresponding to the given model
     */
    protected IFigure getFigure(AbstractModel model){
    	if (model instanceof TypeModel) {
    		TypePart typeEditPart = (TypePart) getPart(model);
    		return typeEditPart.getNameLabel();
    	} else {
        	return getPart(model).getFigure();
    	}
    }

    /**
     * @param model - The model being searched for
     * @return the edit part corresponding to the given model
     */
    protected AbstractPart getPart(AbstractModel model) {
        return EDITOR.getRootPart().getPartFromModel(model);
    }
 
	protected void assertVisible(AbstractModel model, boolean value) {
	    assertEquals(FAIL_VISIBILITY, value, getFigure(model).isVisible());
	}

    protected void execute(Command command) {
        EDITOR.execute(command);
    }
    
    protected void assertCount(Class<?> klass, int count) {
    	if (!AbstractModel.class.isAssignableFrom(klass)) {
			GreenException.illegalOperation(
					"Can only pass an AbstractModel class as a parameter");
    	}
    	
    	int children = ROOT.getChildren(klass).size();
    	
    	if (children != count) {
    		fail("Wrong number of children: expected " + count + ", but was " +
    				children);
    	}
    }
    
	/**
	 * Try to wait for the specified thread for the specified number of seconds.
	 * 
	 * @param threadToWait The thread to wait for.
	 * @param seconds The number of seconds to wait.
	 */
	private void waitForThread(Thread threadToWait, int seconds){
        do {
            WidgetTester.delay(100);
        } while (threadToWait.isAlive() && --seconds >= 0);		
	}
	
	protected boolean containsField(TypeModel typeModel, IField field) {
		for (FieldModel fModel : getFields(typeModel)) {
			if (fModel.getMember().equals(field)) return true;
		}
			
		return false;
	}
	
	protected boolean containsMethod(TypeModel typeModel, IMethod method) {
		for (MethodModel mModel : getMethods(typeModel)) {
			if (mModel.getMember().equals(method)) return true;
		}
		
		return false;
	}
	
	protected List<FieldModel> getFields(TypeModel typeModel) {
		return (ArrayList<FieldModel>) (List)
		typeModel.getFieldCompartmentModel().getChildren();
	}
	
	protected List<MethodModel> getMethods(TypeModel typeModel) {
		List list = typeModel.getMethodCompartmentModel().getChildren();
		return (ArrayList<MethodModel>) list;
	}
	
	protected NoteModel createNote(String label) {
		NoteModel note = new NoteModel(label);
		note.setSize(200, 200);
		note.setLocation(0, 0);
		ROOT.addChild(note);
		
		return note;
	}
	
	protected void setNoteLabel(final NoteModel model, final String label) {
		final Thread thread = new Thread() {
			public void run() {
				waitForDialog(DIALOG_EDIT_NOTE_TITLE);
				setTextInBox(label);
				ok();
			}
		};
		
		thread.start();
		selectContextMenuItem(model, new EditNoteAction());
		waitForThread(thread);
	}
	
	protected List<TypeModel> getTypes() {
		return (ArrayList<TypeModel>) (List) ROOT.getChildren(TypeModel.class);
	}
	
	protected FieldModel findField(TypeModel model, String name) {
		for (FieldModel fieldModel : getFields(model)) {
			if (fieldModel.getField().getElementName().equals(name)) {
				return fieldModel;
			}
		}
		
		fail("Field not found");
		return null;
	}
	
	protected MethodModel findMethod(TypeModel model, String name) {
		for (MethodModel methodModel : getMethods(model)) {
			if (methodModel.getMethod().getElementName().equals(name)) {
				return methodModel;
			}
		}
		
		fail("Method not found");
		return null;
	}
	
	protected TypeModel findType(String name) {
		for (TypeModel typeModel : getTypes()) {
			if (typeModel.getType().getElementName().equals(name)) {
				return typeModel;
			}
		}
		
		fail("Type not found");
		return null;
	}

	protected void assertIllegalRelationship(TypeModel sourceModel,
			TypeModel targetModel, PaletteTool tool) {
		boolean illegal = false;
		
		try {
			assertNull(FAIL_ILLEGAL_RELATIONSHIP,
					drawRelationship(sourceModel, targetModel, tool));
		} catch (GreenException e) {
			illegal = true;
		} finally {
			assertTrue("The relationship generator was run", illegal);
		}
	}
	
	protected void show(AbstractModel model) {
		model.setVisible(true);
	}
	
	protected RelationshipModel drawRelationship(TypeModel sourceModel,
			TypeModel targetModel, PaletteTool tool) {
		return drawRelationship(sourceModel, targetModel, tool, 1);
	}
	
	protected TypeModel addType(DiagramEditor editor, IType type) {
		editor.execute(new AddJavaElementCommand(editor, type));
		return editor.getRootModel().getModelFromType(type);
	}
	
	protected boolean doSetup() {
		return true;
	}
	
	protected void leftClick(Control control, int x, int y) {
		TESTING_ROBOT.actionClick(control, x, y);
	}
}
