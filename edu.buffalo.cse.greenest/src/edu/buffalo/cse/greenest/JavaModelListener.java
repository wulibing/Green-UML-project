package edu.buffalo.cse.greenest;

import static org.eclipse.jdt.core.IJavaElementDelta.ADDED;
import static org.eclipse.jdt.core.IJavaElementDelta.REMOVED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.SourceField;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceType;

//import edu.buffalo.cse.green.CompilationUnitRefactorHandler;
//import edu.buffalo.cse.green.FieldRefactorHandler;
//import edu.buffalo.cse.green.JavaModelListener;
//import edu.buffalo.cse.green.MethodRefactorHandler;
//import edu.buffalo.cse.green.PackageRefactorHandler;
//import edu.buffalo.cse.green.ProjectRefactorHandler;
//import edu.buffalo.cse.green.RefactorHandler;
//import edu.buffalo.cse.green.TypeRefactorHandler;
//import edu.buffalo.cse.green.editor.model.RootModel;

//import edu.buffalo.cse.green.GreenException;
//import edu.buffalo.cse.green.TypeRefactorHandler;
//import edu.buffalo.cse.green.editor.DiagramEditor;
//import edu.buffalo.cse.green.editor.model.RootModel;

public class JavaModelListener implements IElementChangedListener {
	
	private static JavaModelListener _listener = new JavaModelListener();
	private static Map<Class, RefactorHandler> map;
	
	static {
//		// add the elements to consider changes for to a list
		map = new HashMap<Class, RefactorHandler>();
//		map.put(JavaProject.class, ProjectRefactorHandler.instance());
//		map.put(PackageFragment.class, PackageRefactorHandler.instance());
//		map.put(CompilationUnit.class,
//				CompilationUnitRefactorHandler.instance());
//		map.put(SourceType.class, TypeRefactorHandler.instance());
//		map.put(SourceField.class, FieldRefactorHandler.instance());
//		map.put(SourceMethod.class, MethodRefactorHandler.instance());
	}

	public void elementChanged(ElementChangedEvent event) {
		try {
			/* Goes through these classes looking for any that are added, moved
			 * or removed. Calls methods that updates the editor to reflect any
			 * changes found.
			 */
			for (Class type : map.keySet()) {
				List<IJavaElementDelta> added =
					findAddedElements(event.getDelta(), type);
				List<IJavaElementDelta> removed =
					findRemovedElements(event.getDelta(), type);
				List<IJavaElementDelta> changed =
					findChangedElements(event.getDelta(), type);
				HashMap<IJavaElement, IJavaElement> moved =
					extractMovedElements(added, removed);
				
				// ignore updating the editors if no changes occurred
				if (added.size() == 0 && removed.size() == 0
						&& moved.size() == 0 && changed.size() == 0) {
					continue;
				}
				//We need to put in the list of editors we have once thats implemented.
				List<DiagramEditor> editors =
					new ArrayList<DiagramEditor>(DiagramEditor.getEditors());
				
				// handle changes
				for (DiagramEditor editor : editors) {
					RootModel root = editor.getRootModel();
					
					// handle moves
					for (IJavaElement sourceElement : moved.keySet()) {
						IJavaElement targetElement = moved.get(sourceElement);
						map.get(sourceElement.getClass()).handleMove(
								root, sourceElement, targetElement);
					}

					// handle removes
					for (IJavaElementDelta removedElement : removed) {
						map.get(removedElement.getElement().getClass())
						.handleRemove(root, removedElement.getElement());
					}
					
					// handle adds
					for (IJavaElementDelta addedElement : added) {
						map.get(addedElement.getElement().getClass()).handleAdd(
								root, addedElement.getElement());
					}

					// handle changes (to modifiers, etc.)
					for (IJavaElementDelta changedElement : changed) {
						handleElementChange(changedElement);
					}
					
					editor.forceRefreshRelationships();
				}

			}
		}
		catch (Throwable t) {
			//TODO Incremental exploration throws Null Pointer
			GreenException.critical(t);
		} finally {
			//TypeRefactorHandler.REMOVED_TYPE = null;
		}
	}

	private HashMap<IJavaElement, IJavaElement> extractMovedElements(
			List<IJavaElementDelta> added, List<IJavaElementDelta> removed) {
		// TODO Auto-generated method stub
		return null;
	}

	private void handleElementChange(IJavaElementDelta changedElement) {
		// This requres so many other classes that haven't been implemented.
		
	}


	private List<IJavaElementDelta> findChangedElements(
			IJavaElementDelta parentDelta, Class type) {

		IJavaElementDelta delta;
		List<IJavaElementDelta> changes = new ArrayList<IJavaElementDelta>();

		// adds deltas representing the removed elements of
		// the specified type to the list of changes
		for (int i = 0; i < parentDelta.getChangedChildren().length; i++) {
			delta = parentDelta.getChangedChildren()[i];

			if (type.isInstance(delta.getElement())) {
				if (delta.getChangedChildren().length == 0) {
					changes.add(delta);
				}
			}
		}

		// traverse all changed branches
		// this code shouldn't need altering
		for (int i = 0; i < parentDelta.getChangedChildren().length; i++) {
			delta = parentDelta.getChangedChildren()[i];
			changes.addAll(findChangedElements(delta, type));
		}

		return changes;
	
		// TODO Auto-generated method stub
		return null;
	}

	private List<IJavaElementDelta> findRemovedElements(
			IJavaElementDelta parentDelta, Class type) {
		List<IJavaElementDelta> changes = new ArrayList<IJavaElementDelta>();

		// check for removed element
		if (parentDelta.getKind() == REMOVED) {
			if (type.isInstance(parentDelta.getElement())) {
				changes.add(parentDelta);
			}
		}

		// traverse all changed branches
		// this code shouldn't need altering
		for (IJavaElementDelta delta : parentDelta.getAffectedChildren()) {
			changes.addAll(findRemovedElements(delta, type));
		}

		return changes;
	
	}

	@SuppressWarnings("unused")
	private List<IJavaElementDelta> findAddedElements(
			IJavaElementDelta parentDelta,
			Class type) {
		List<IJavaElementDelta> changes = new ArrayList<IJavaElementDelta>();

		// check for added element
		if (parentDelta.getKind() == ADDED) {
			if (type.isInstance(parentDelta.getElement())) {
				changes.add(parentDelta);
			}
		}

		// traverse all changed branches
		// this code shouldn't need altering
		for (IJavaElementDelta delta : parentDelta.getAffectedChildren()) {
			changes.addAll(findAddedElements(delta, type));
		}

		return changes;
	}
	//This requires RootModel
	interface RefactorHandler<E extends IJavaElement> {
		/**
		 * Handles the addition of an <code>IJavaElement</code> to the workspace.
		 * 
		 * @param root
		 * @param element
		 */
		public void handleAdd(RootModel root, E element);
		
		/**
		 * Handles movement of an <code>IJavaElement</code> within the workspace.
		 * 
		 * @param root
		 * @param sourceElement
		 * @param targetElement
		 */
		public void handleMove(RootModel root, E sourceElement, E targetElement);
		
		/**
		 * Handles the removal of an <code>IJavaElement</code> from the workspace.
		 * 
		 * @param root
		 * @param element
		 */
		public void handleRemove(RootModel root, E element);
	}
}
