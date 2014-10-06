package edu.buffalo.cse.greenest.model.commands;

import java.util.ArrayList;
import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import edu.buffalo.cse.greenest.model.TypeModel;
import edu.buffalo.cse.greenest.GreenException;
import edu.buffalo.cse.greenest.model.GreenestDiagramEditor;

public class AddJavaElementCommand extends Command{
	private IJavaElement _element;
	private GreenestDiagramEditor _editor;
	private ArrayList<Command> _commands;

	/**
	 * Constructor initializes a new ArrayList containing the commands that can be performed
	 */
	public AddJavaElementCommand(Command command, IJavaElement element, GreenestDiagramEditor editor){
		_commands = new ArrayList<Command>();
		_element = element;
		_editor = editor;
	}

	public void execute() {
		/** 
		 * RootModel root = _editor.getRootModel();
		 * There is currently no Root Model class, don't know if we'll be using one
		 * The following code will take the element that is passed in (from the context menu?) and create TypeModels for everything. What class is 
		 * going to handle creating the TypeModels has to be decided as well as what is going to handle placing the UMLBoxes.
		 */
		try {
			if (_element instanceof IJavaProject) {
				IJavaProject project = (IJavaProject) _element;

				for (IPackageFragment packFrag
						: project.getPackageFragments()) {
					if (!packFrag.isReadOnly()) {
						openPackage(packFrag);
					}
				}
			} else if (_element instanceof IPackageFragment) {
				IPackageFragment packFrag = (IPackageFragment) _element;
				openPackage(packFrag);
			} else if (_element instanceof ICompilationUnit) {
				openCU((ICompilationUnit) _element);
			} else if (_element instanceof IClassFile) {
				openClass((IClassFile) _element);
			} else if (_element instanceof IType) {
				createType((IType) _element);
			} else if (_element instanceof IMember) {
				_element = _element.getAncestor(IJavaElement.TYPE);
				createType((IType) _element);
			} else {
				GreenException.illegalOperation( "Cannot open this kind of Java Element: " + _element);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void openClass(IClassFile classFile) {
		IType type = classFile.getType();
		createType( type);
	}

	private void openCU(ICompilationUnit cu) throws JavaModelException {
		IType[] types = cu.getAllTypes();

		for (int i = 0; i < types.length; i++) {
			createType(types[i]);
		}
	}


	private void openPackage (IPackageFragment packFrag)
			throws JavaModelException {
		ICompilationUnit[] cus = packFrag.getCompilationUnits();
		IClassFile[] classFiles = packFrag.getClassFiles();

		for (ICompilationUnit cu : cus) {
			openCU(cu);
		}

		for (IClassFile classFile : classFiles) {
			openClass(classFile);
		}
	}
	/**
	 * Returns true if the command can be Executed
	 */
	public boolean canExecute(){
		return false;
	}

	/**
	 * Returns true if the command can be Undone
	 */
	public boolean canUndo(){
		return false;
	}

	/**
	 * Returns true if the command can be Redone
	 */
	public boolean canRedo(){
		return false;
	}

	/**
	 * Returns true if the stack is dirty
	 * The command stack is dirty when the last Executed or Redone command is different
	 * than the command at the top of the Undo stack when markSaveLocation() was called
	 */
	public boolean isDirty(){
		return false;
	}

	/**
	 * Marks the last executed or redone command, which is the point where last changes were saved
	 * isDirty() is based off of this 
	 */
	public void markSaveLocation(){

	}

	/**
	 * Flushes the entire stack back to an empty state
	 * Can be used to revert back to previously saved state
	 */
	public void flush(){

	}

	/**
	 * Returns a list of the commands in the order they were executed
	 */
	public java.lang.Object[] getCommands(){
		return null;

	}

	/**
	 * Peeks at the top of the Redo stack 
	 * Notifies the users what command can be redone
	 */
	public Command getRedoCommand(){
		return null;

	}

	/**
	 * Peeks at the top of the Undo stack 
	 * Notifies the users what command can be undone
	 */
	public Command getUndoCommand(){
		return null;

	}

	/**
	 * Adds the specified Command if it is not null
	 */
	public void add(Command command){
	}

	/**
	 * Executes the specified command
	 * Can only be called if canExecute() returns True
	 */
	//	public void execute(){
	//		
	//	}
	//	
	/**
	 * Executes the Redo command. 
	 * This command should only be called once an Undone command has been called
	 * and canRedo() returns True.
	 */
	public void redo(java.lang.String Redo, Command command){
		execute();
	}

	/**
	 * Executes the Undo command. 
	 * Undoes the changes performed by the Execute command
	 * Can only be called after execute() has been called
	 * Can only be performed if canUndo() returns True.
	 * */
	public void undo(java.lang.String Undo, Command command){
		execute();
	}

	private void createType(IType type) {
		Command command = new AddTypeCommand(type);
		_commands.add(command);
		command.execute();
	}
	class AddTypeCommand extends Command {
		/**
		 * The type being added.
		 */
		private IType _type;

		/**
		 * The <code>TypeModel</code> representing the added type.
		 */
		private TypeModel _model;

		public AddTypeCommand(IType type) {

			_type = type;
		}

		/**
		 * @see org.eclipse.gef.commands.Command#execute()
		 */
		public void execute() {
			_model = new TypeModel(_type);
			
			//This will call a method on something to create the TypeModel then it will place a UMLBox in the formerly RootModel.
			//Not sure what will handle what the ViewModel was.

			//			_model = _root.createTypeModel(_type);
			//
			//			if (_model != null) {
			//				_root.placeUMLBox(_model);
			//			}
		}

		/**
		 * @see org.eclipse.gef.commands.Command#undo()
		 */
		public void undo() {
			//	_model.removeFromParent();
		}

		/**
		 * @see org.eclipse.gef.commands.Command#redo()
		 */
		public void redo() {
			execute();
		}
	}
}
