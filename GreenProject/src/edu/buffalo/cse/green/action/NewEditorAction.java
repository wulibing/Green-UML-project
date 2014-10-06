package edu.buffalo.cse.green.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.editor.model.TypeModel;
import edu.buffalo.cse.green.editor.model.commands.AddJavaElementCommand;
import edu.buffalo.cse.green.editor.model.commands.IncrementalExploreCommand;
import edu.buffalo.cse.green.util.IterableSelection;

public class NewEditorAction implements IActionDelegate {
	
	ISelection _selection;
	
	/** Note: if we take this approach, we have to decide whether or not a new editor should be 'incremental' by default.
	 * This seems to mean that it explores all children in the current model
	 * 		- Does this have an effect on the seemingly random class models we see upon refresh?
	 * 		- For 4/14: use this combined class with default set to both incremental and normal exploration, and verify that it changes results
	 * All places where 'run()' occurs would have to be replaced with the secondary method if we want to overwrite default behavior
	 * 		- This seems to be handled by eclipse's plugin feature, so is there a better way? Under what conditions do we explore incrementally?
	 * 		- Both methods (incremental and regular) seem to be called in the same places - is only one ever used?
	 */
	public void run(IAction action) {
		run(action, false);
	}

	public void run(IAction action, boolean incremental) {
		
		StructuredSelection ss = (StructuredSelection) _selection;
		DiagramEditor editor = DiagramEditor.getActiveEditor();
		
		try {
			editor = DiagramEditor.createEditor(ss);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		for (IJavaElement element1
				: new IterableSelection<IJavaElement>(ss)) {
			// If current editor's project is not set, add to it.
			if (editor.getProject() != null) {
				if (!editor.getProject().getHandleIdentifier().equals(
						element1.getJavaProject().getHandleIdentifier())) {
					// if the editor we found can't hold the current element,
					// see if an editor exists that can hold the element
					editor = DiagramEditor.findProjectEditor(element1
							.getJavaProject());
					// if no such editor exists...
					if (editor == null) {
						// create one
						try {
							editor =
								DiagramEditor.createEditor(
										new StructuredSelection(element1));
						} catch (JavaModelException e) {
							e.printStackTrace();
						}
					}
				}
			}

			try {
				editor.execute(new AddJavaElementCommand(editor, element1));
				

			} catch (GreenException e) {
				MessageDialog.openError(editor.getSite().getShell(),
						"Error", e.getLocalizedMessage());
			}
			
		}
		
		if (incremental) {
			RootModel root = editor.getRootModel();
			
			//modelList is a new list so that the iterator on root's children does not
			//throw a ConcurrentModificationException
			List<AbstractModel> modelList = new ArrayList<AbstractModel>(root.getChildren());
			
			for(AbstractModel model: modelList) {
				if(model instanceof TypeModel) {
					editor.execute(new IncrementalExploreCommand(editor, (TypeModel)model, true));
				}
			}
		}

		editor.refresh();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof StructuredSelection) {
			_selection = selection;
		}
	}

}
