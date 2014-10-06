package edu.buffalo.cse.greenest.model;

import java.awt.Point;

public class ViewModel {
	/*
	 * Placement and Size.
	 */
	private Point _topLeft;
	private int _length;	// in pixels
	private int _width;
	
	/*
	 * This will be true if we want the order to be "<type> <name>" for fields
	 * and methods, or false if it will be "<name> : <type>" as in the UML spec.
	 */
	private boolean _greenStyle = true;
	private boolean _showAccessMods = false;
	
	/*
	 * Visibility options.   
	 */
	private boolean _showFields = true;
	private boolean _showMethods = true;
	
	/**
	 * Default constructor for creating a type.
	 * This will be instantiated when the user creates a type
	 * in the Diagram Editor.
	 */
	public ViewModel(Point clickedPixel) {
		_topLeft = clickedPixel;
		_length = 100;		// TODO specify this and width in a constants file?
		_width = 100;
	}
	
	/**
	 * Default constructor for creating a type,
	 * when we already have code for the type.
	 */
	public ViewModel() {
		_topLeft = null;	// TODO can we do this better?
		_length = 100;
		_width = 100;
	}
	
	/*
	 * Setters and Getters.
	 */
	public boolean areFieldsVisible() { return _showFields; }
	public void setFieldVisibility(boolean b) { _showFields = b; }
	
	public boolean areMethodsVisible() { return _showMethods; }
	public void setMethodVisibility(boolean b) { _showMethods = b; }
	
	public boolean isGreenStyle() { return _greenStyle; }
	public void setGreenStyle(boolean b) { _greenStyle = b; }
	
	public Point getLocation() { return _topLeft; }
	public void setLocation(Point p) { _topLeft = p; }
	
	public int getLength() { return _length; }
	public void setLength(int l) { _length = l; }
}
