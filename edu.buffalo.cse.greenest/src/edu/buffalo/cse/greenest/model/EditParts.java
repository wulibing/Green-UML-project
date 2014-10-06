package edu.buffalo.cse.greenest.model;

import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayeredPane;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
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
public class EditParts implements EditPart{
	
	private EditPart _part;
	


	@Override
	public Object getAdapter(Class adapter) {
		return _part.getAdapter(adapter);
	}

	@Override
	public void activate() {
		_part.activate();
	}

	@Override
	public void addEditPartListener(EditPartListener listener) {
		_part.addEditPartListener(listener);
	}

	@Override
	public void addNotify() {
		_part.addNotify();
	}

	@Override
	public void deactivate() {
		_part.deactivate();
	}

	@Override
	public void eraseSourceFeedback(Request request) {
		_part.eraseSourceFeedback(request);
	}

	@Override
	public void eraseTargetFeedback(Request request) {
		_part.eraseTargetFeedback(request);
	}

	@Override
	public List getChildren() {
		return _part.getChildren();
	}

	@Override
	public Command getCommand(Request request) {
		return _part.getCommand(request);
	}

	@Override
	public DragTracker getDragTracker(Request request) {
		return _part.getDragTracker(request);
	}

	@Override
	public EditPolicy getEditPolicy(Object key) {
		return _part.getEditPolicy(key);
	}

	@Override
	public Object getModel() {
		return _part.getModel();
	}

	@Override
	public EditPart getParent() {
		return _part.getParent();
	}

	@Override
	public RootEditPart getRoot() {
		return _part.getRoot();
	}

	@Override
	public int getSelected() {
		return _part.getSelected();
	}

	@Override
	public EditPart getTargetEditPart(Request request) {
		return _part.getTargetEditPart(request);
	}

	@Override
	public EditPartViewer getViewer() {
		return _part.getViewer();
	}

	@Override
	public boolean hasFocus() {
		return _part.hasFocus();
	}

	@Override
	public void installEditPolicy(Object role, EditPolicy editPolicy) {
		_part.installEditPolicy(role, editPolicy);
	}

	@Override
	public boolean isActive() {
		return _part.isActive();
	}

	@Override
	public boolean isSelectable() {
		return _part.isSelectable();
	}

	@Override
	public void performRequest(Request request) {
		_part.performRequest(request);
	}

	@Override
	public void refresh() {
		_part.refresh();
	}

	@Override
	public void removeEditPartListener(EditPartListener listener) {
		_part.removeEditPartListener(listener);
	}

	@Override
	public void removeEditPolicy(Object role) {
		_part.removeEditPolicy(role);
	}

	@Override
	public void removeNotify() {
		_part.removeNotify();
	}

	@Override
	public void setFocus(boolean hasFocus) {
		_part.setFocus(hasFocus);
	}

	@Override
	public void setModel(Object model) {
		_part.setModel(model);
	}

	@Override
	public void setParent(EditPart parent) {
		_part.setParent(parent);
	}

	@Override
	public void setSelected(int value) {
		_part.setSelected(value);
	}

	@Override
	public void showSourceFeedback(Request request) {
		_part.showSourceFeedback(request);
	}

	@Override
	public void showTargetFeedback(Request request) {
		_part.showTargetFeedback(request);
	}

	@Override
	public boolean understandsRequest(Request request) {
		return _part.understandsRequest(request);
	}
	
}