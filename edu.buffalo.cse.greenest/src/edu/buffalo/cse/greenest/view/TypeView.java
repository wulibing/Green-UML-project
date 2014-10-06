package edu.buffalo.cse.greenest.view;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Color;

/*
 * This was largely taken from 
 * http://eclipse.org/articles/Article-GEF-Draw2d/GEF-Draw2d.html
 */
public class TypeView extends Figure implements Observer {
	private Color _classColor = new Color(null,255,255,206);
	private TypeCompartmentView _fieldCompartment;
	private TypeCompartmentView _methodCompartment;
	
	/**
	 * Constructor for a default new class. 
	 * i.e., if a new type is being created from the Diagram Editor,
	 * a default class box will appear instead of a wizard. 
	 * 
	 * Because wizards stink.
	 */
	public TypeView() {
		this(new Label("NewClass"));
	}
	
	/**
	 * Constructor for an existing class.
	 * This is for when the user is creating a diagram from existing code.
	 */
	public TypeView(Label name) {
		_fieldCompartment = new TypeCompartmentView();
		_methodCompartment = new TypeCompartmentView();
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);	
		setBorder(new LineBorder(ColorConstants.black,1));
		setBackgroundColor(_classColor);
		setOpaque(true);

		add(name);
		add(_fieldCompartment);
		add(_methodCompartment);
	}
	
	public TypeCompartmentView getFieldCompartment() {
		return _fieldCompartment;
	}
	public TypeCompartmentView getMethodCompartment() {
		return _methodCompartment;
	}
	
	/*
	 * Adding/removing fields will be delegated to the 
	 * proper compartments.
	 */
	public void addField(Label fieldName) {
		_fieldCompartment.addField(fieldName);
	}
	
	public boolean removeField(Label fieldName) {
		return _fieldCompartment.removeField(fieldName);
	}

	public void addMethod(Label methodName) {
		_fieldCompartment.addMethod(methodName);
	}
	
	public boolean removeMethod(Label methodName) {
		return _fieldCompartment.removeMethod(methodName);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}
