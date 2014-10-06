package edu.buffalo.cse.greenest.model;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayeredPane;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;

/* 
 * 
 * This code is instructing by The Help Menu
 * 
 */

//notification between view and typeViewModel

/**
 * 
 * @author Eve,Libing
 *
 */
public class EditParts implements EditPartFactory{
	
	private EditPart _part;
	
	public EditParts(TypeModel type){
		EditDomain editDomain = new DefaultEditDomain(null);		
		ScalableFreeformRootEditPart root = new ScalableFreeformRootEditPart(); 
		/*
		 * could also use ScalableRootEditPart.
		 */
		
		GraphicalViewer viewer = new ScrollingGraphicalViewer(); 
		viewer.createControl(parent);
		editDomain.addViewer(viewer);
		viewer.setRootEditPart(root); 
		viewer.setEditPartFactory(new EditPartFactory());
		/*
		 * Basically, the constructor configures a RootEditPart
		 * 
		 * GraphicalRootEditPart class has been deprecated and will eventually
		 * be removed from GEF.The equivalent functionality of this EditPart can
		 * be achieved by using a ScrollingGraphicalViewer with a ScalableRootEditPart.
		 */
		
	}
	
	/**
	 * Factory maps model elements to EditParts from org.eclipse.gef.examples
	 * @param context
	 * @param model
	 * @return
	 */
	public EditPart createEditPart(EditPart context, Object model){
		//Get EditPart for model element
		_part=getPartForElement(modelElement);
		//Store model element in EditPart
		_part.setModel(modelElement);
		return _part;
	}

	public EditPart contentsEditPart(){
		_part.createFigure();
		return _part;
	}// implementing the Contents EditParts
	
	public EditPart childrenEditPart(){
		_part.refreshVisuals();	
		/**
		 * This method is responsible for reflecting the model's properties in the view.
		 * Editparts must override this method based on the model and figure they work with
		 */
		_part.getModelChildren();
		/**
		 * a child of the diagram is also a parent with its own children
		 */
		return _part;
	}
	
	public EditPart connectionEditPart(){
		/**
		*Connections are special editparts that connect any two editparts in a diagram
		*An editpart is called a node if it can be the source or target of a connection
		*/
		_part.getModelSourceConnections();
		_part.getModelTargetConnections();
		return _part;
	}
	
}

