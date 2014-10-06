package edu.buffalo.cse.green.editor;

import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;

import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.model.commands.AddJavaElementCommand;
import edu.buffalo.cse.green.editor.DiagramEditor;

import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;

public class GreenScrollingViewer extends ScrollingGraphicalViewer {

	
	ISelection _selected;
	ISelection[] _invalidSelection;
	Transfer[] transfers;
	int ops;
	DiagramEditor currentEditor; 
	
	public GreenScrollingViewer()
	{
		super();
		   currentEditor = DiagramEditor.getActiveEditor();
		   int ops = DND.DROP_COPY | DND.DROP_MOVE;
		   
		   IWorkbenchWindow[] windows = PlugIn.getDefault().getWorkbench()
			.getWorkbenchWindows();

		   for (int i = 0; i < windows.length; i++) 
		   {
			   IViewPart packExplorer = windows[i].getPages()[0]
				.findView(JavaUI.ID_PACKAGES);

			   	if (packExplorer != null) {
			   			ISelection _selected = (ISelection) packExplorer.getViewSite()
			   			.getSelectionProvider().getSelection();
				}
		   }
		   transfers = new Transfer[1];
		   transfers[0] = FileTransfer.getInstance();
	}
	
	void makeDropSupporting()
	{
		  Control control = getControl();
		  DropTarget dropTarget = new DropTarget(control, ops);
		  dropTarget.setTransfer(transfers);
		  dropTarget.addDropListener(currentEditor.getTransferAdapter());
	}
}

/**
 * Create GreenScrollingViewer adapter which listens for drop events
 */
class GreenScrollingViewerAdapter extends AbstractTransferDropTargetListener
{
	DiagramEditor _currentEditor;
	IJavaElement _element;

	public GreenScrollingViewerAdapter(EditPartViewer viewer, Transfer transfer)
	{
		super(viewer, transfer);
		_currentEditor = DiagramEditor.getActiveEditor();
		_element = null;
	}
	
	public GreenScrollingViewerAdapter(EditPartViewer viewer)
	{
		super(viewer);
		_currentEditor = DiagramEditor.getActiveEditor();
		_element = null;
	}
	@Override
	protected void updateTargetRequest()
	{
		AddJavaElementCommand command = new AddJavaElementCommand(_currentEditor, _element);
	}
	
	/**
	 * Don't delete the things that we are dragging from the Package Explorer
	 */
	protected void handleDragOver() {
		   getCurrentEvent().detail = DND.DROP_COPY;
		   super.handleDragOver();
		}
	
	   public void handleDrop() {
		    
		   String[] dropData = ((String[])getCurrentEvent().data);
		   
		   for (int i = 0; i < dropData.length; i++)
		   {
			String temp = dropData[i];
			System.out.println("File name: " + dropData);
			
			if (!temp.contains(".java") && !temp.contains(".class"))
			{
				// set flag to handle non-dropable
				System.out.println("CANT DROP: " + temp);
				
			}
				//handle drop
				System.out.println("Dropping: " + temp);
	     
		   }

	   }
	   
	   //we need drop 
	   public void drop(DropTargetEvent event) {
			setCurrentEvent(event);
			eraseTargetFeedback();
			handleDrop();
			unload();
	   	}
}
