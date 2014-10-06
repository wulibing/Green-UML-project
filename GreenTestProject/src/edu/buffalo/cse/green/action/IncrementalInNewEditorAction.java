/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.action;

import org.eclipse.jface.action.IAction;

/**
 * Opens a new editor, then incrementally explore the selected elements
 * 
 * @author <a href="mailto:zgwang@sourceforge.net">Gene Wang</a>
 * @author Brian McSkimming
 */
public class IncrementalInNewEditorAction extends NewEditorAction { //implements IActionDelegate {
//	ISelection _selection;

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		super.run(action,true);
	}
//		StructuredSelection ss = (StructuredSelection) _selection;
//		DiagramEditor editor = DiagramEditor.getActiveEditor();
//		
//		try {
//			editor = DiagramEditor.createEditor(ss);
//		} catch (JavaModelException e) {
//			e.printStackTrace();
//		}
//		
//		for (IJavaElement element1
//				: new IterableSelection<IJavaElement>(ss)) {
//			// If current editor's project is not set, add to it.
//			if (editor.getProject() != null) {
//				if (!editor.getProject().getHandleIdentifier().equals(
//						element1.getJavaProject().getHandleIdentifier())) {
//					// if the editor we found can't hold the current element,
//					// see if an editor exists that can hold the element
//					editor = DiagramEditor.findProjectEditor(element1
//							.getJavaProject());
//					// if no such editor exists...
//					if (editor == null) {
//						// create one
//						try {
//							editor =
//								DiagramEditor.createEditor(
//										new StructuredSelection(element1));
//						} catch (JavaModelException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//
//			try {
//				editor.execute(new AddJavaElementCommand(editor, element1));
//				
//
//			} catch (GreenException e) {
//				MessageDialog.openError(editor.getSite().getShell(),
//						"Error", e.getLocalizedMessage());
//			}
//			
//		}
//		RootModel root = editor.getRootModel();
//		
//		//modelList is a new list so that the iterator on root's children does not
//		//throw a ConcurrentModificationException
//		List<AbstractModel> modelList = new ArrayList<AbstractModel>(root.getChildren());
//		
//		for(AbstractModel model: modelList) {
//			if(model instanceof TypeModel) {
//				editor.execute(new IncrementalExploreCommand(editor, (TypeModel)model, true));
//			}
//		}
//
//		editor.refresh();
//	}
//
//	/**
//	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
//	 */
//	public void selectionChanged(IAction action, ISelection selection) {
//		if (selection instanceof StructuredSelection) {
//			_selection = selection;
//		}
//	}
//
//	public IncrementalInNewEditorAction() {
//	}
}
