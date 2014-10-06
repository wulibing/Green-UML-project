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

import java.util.Iterator;
import java.util.List;

import abbot.tester.swt.WidgetTester;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.controller.AbstractPart;
import edu.buffalo.cse.green.editor.controller.RelationshipPart;
import edu.buffalo.cse.green.editor.controller.TypePart;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.TypeModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @author evertwoo
 */
public class Robot extends WidgetTester {
    public static final int PALETTE_VSPACING = 20;
	
	/**
     * Activates the shortcut menu using the keyboard shortcut.
     * 
     * @param display The display to activate the shortcut on.
     */
    public final void actionShortcutMenu(Display display) {
    	actionKey(SWT.F10 | SWT.SHIFT, display);
    }

    /**
     * Activates a menu item in a context menu. It activates the
     * <code>itemPosition</code> <sup>th</sup> item. <br>
     * Warning: This method only works on systems where the following is true:
     * <br>
     * <ul>
     * <li>Shift-F10 is the keyboard shortcut for context menu</li>
     * <li>The menu wraps over between the first and the last menu item</li>
     * <li>The item that will be selected when hitting up right after the
     * context menu appears is the last menu item</li>
     * </ul>
     * 
     * @param itemPosition
     *            The position of the menu item (as dictated by the number of
     *            arrow-down keys that needs to be pressed). The first menu item
     *            is item 1.
     * @param display
     */
    public Thread actionShortcutMenuActivate(final int itemPosition, final List<Integer> submenus) {
    	//Runs the keystrokes in a seperate thread. Replaces the OLD code below.
    	return new Thread(){
    		public void run() {
    		    actionKey(SWT.F10|SWT.SHIFT, getDisplay());
    		    
    		    // traverse submenus until you get to the menu containing the desired item 
    		    for (Iterator iter = submenus.iterator(); iter.hasNext();) {
    	            Integer subMenuPos = (Integer) iter.next();
        			actionKey(SWT.ARROW_UP, getDisplay());

    	            for (int i = 0; i < subMenuPos.intValue(); i++) {
    	                actionKey(SWT.ARROW_DOWN, getDisplay());
    	            }
    	            
        		    actionKey(SWT.CR, getDisplay());
        		    actionKey(SWT.ARROW_RIGHT, getDisplay());
    	        }

    		    // select the desired item
    			actionKey(SWT.ARROW_UP, getDisplay());

    			for (int i = 0; i < itemPosition; i++){
    				actionKey(SWT.ARROW_DOWN, getDisplay());
    			}
    			
    		    actionKey(SWT.CR, getDisplay());
    		}
    	};
    }

    public void actionJitterMouseAt(Widget widget, int x, int y){
    	mouseMove(widget, x, y);
    	mouseMove(widget, x-1, y);
    	mouseMove(widget, x+1, y);
    	mouseMove(widget, x, y);
    	mouseMove(widget, x, y-1);
    	mouseMove(widget, x, y+1);
    	mouseMove(widget, x, y);
    }

    /**
     * Clicks on the edit part.
     * Knows how to handle <code>TypeEditPart</code> specially.
     * 
     * @param editPart The edit part to click on.
     */
    public void actionClick(AbstractPart editPart) {
    	Point location = getLocation(editPart);
        Control control = null;
        
        while (control == null) {
            control = editPart.getViewer().getControl();
            delay(10);
        }
    
    	actionClick(control, location.x, location.y);
    }

    private Control getControl (AbstractPart part) {
    	Control control = null;
    	
    	while (control == null) {
    		control = part.getViewer().getControl();
    		delay(1);
    	}
    	
    	return control;
    }
    
    public void jitterMouse(AbstractPart part) {
    	Control control = getControl(part);
    	Point location = getLocation(part);
    	actionJitterMouseAt(control, location.x, location.y);
    }
    
    /**
     * Clicks on the specified subcomponent of the edit part. 
     * 
     * @param editPartToClickOn The edit part which contains the reference <code>Control</code> to click on.
     * @param figureSubcomponent The figure on the control to click.
     */
    public void actionClick(AbstractPart editPartToClickOn, IFigure figureSubcomponent) {
        actionClick(editPartToClickOn.getViewer().getControl(), figureSubcomponent);
    }

    /**
     * Clicks on a figure, relative to the specified reference control.
     * 
     * @param referenceControl The reference control to click on.
     * @param figureToClickOn The figure to click on, containing location relative to the specified reference control.
     */
    public void actionClick(Control referenceControl, IFigure figureToClickOn){
    	Rectangle bounds = figureToClickOn.getBounds();
        Point center = bounds.getCenter();
        actionClick(referenceControl, center.x, center.y);
    }

    /**
     * Convenience for calling actionClick(Widget, int, int) twice.
     * 
     * @see #actionClick(Widget, int, int)
     * 
     * @param widgetToClickOn The widget to click on.
     * @param x The x location to click on (relative to the widget).
     * @param y The y location to click on (relative to the widget).
     */
    public final void actionDoubleClick(Widget widgetToClickOn, int x, int y){
    	actionClick(widgetToClickOn, x, y);
    	actionClick(widgetToClickOn, x, y);
    }

    /**
     * Convenience for calling actionClick(Widget) twice.
     * 
     * @see #actionClick(Widget)
     * 
     * @param widgetToClickOn The widget to click on.
     */
    public final void actionDoubleClick(Widget widgetToClickOn){
    	actionClick(widgetToClickOn);
    	actionClick(widgetToClickOn);
    }

    /**
     * Clicks on a tool in the palette.
     * 
     * @see ConstantsUI
     * 
     * @param tool - the tool as specified in <code>ConstantsUI</code>.
     */
    public void actionSelectPaletteTool(Control paletteControl, int tool, Display display) {
        actionClick(paletteControl, 5, tool * PALETTE_VSPACING);
        waitForIdle(display);
    }

    /**
     * Gets a location on the specified <code>AbstractPart</code> that can be clicked on to select it.
     * 
     * @param editPart The <code>AbstractPart</code> to get the location on.
     * @return A location on the edit part to click on to select it.
     */
    public Point getLocation(AbstractPart editPart){
        // If relationship, just try to click (and hopefully miss any labels)
    	if (editPart instanceof RelationshipPart) {
    	    RelationshipPart rEditPart = (RelationshipPart) editPart;
    	    PolylineConnection connection = (PolylineConnection) rEditPart.getFigure();
    	    IFigure umlBoxSource = ((AbstractPart) rEditPart.getSource()).getFigure();
    	    IFigure umlBoxTarget = ((AbstractPart) rEditPart.getTarget()).getFigure();
    	    PointList ptList = connection.getPoints();
    	    Point pt1 = null, pt2 = null;				// optimal points (bendpoints not hidden by source or target)
    	    Point pt1Backup = null;	// transition points
    	    boolean optimal = false;
    	    for (int p = 0; p < ptList.size() - 1; p++) {
    	    	Point ptCurrent = ptList.getPoint(p);
    	    	Point ptNext = ptList.getPoint(p + 1);
    	    	boolean pt1InBoxSource = umlBoxSource.containsPoint(ptCurrent);
    	    	boolean pt1InBoxTarget = umlBoxTarget.containsPoint(ptCurrent);
    	    	boolean pt2InBoxSource = umlBoxSource.containsPoint(ptNext);
    	    	boolean pt2InBoxTarget = umlBoxTarget.containsPoint(ptNext);
				if (!(pt1InBoxSource || pt1InBoxTarget)){
					pt1 = ptCurrent;
					pt2 = ptNext;
					optimal = !(pt2InBoxSource || pt2InBoxTarget);
					if (optimal){ break; }
				}
				else if ((pt1InBoxSource && pt2InBoxTarget) || 
						(pt2InBoxSource && pt1InBoxTarget)) {	// transition points
					pt1Backup = ptCurrent;
				}
			}

    	    if (pt1 == null){ pt1 = pt1Backup; pt2 = pt1Backup; }
    	    return new Point((pt1.x + pt2.x) / 2, (pt1.y + pt2.y) / 2);
    	}
    	else if (editPart instanceof TypePart){
    		// Click on name compartment edit part instead if clicking on TypeEditPart
    		AbstractModel nameComponentModel = ((TypeModel) editPart.getModel()).getNameCompartmentModel();
    		AbstractPart nameCompartmentEditPart = editPart.getRootPart().getPartFromModel(nameComponentModel);
    		
    		// If the map is not initialized, do it the hard way
    		if (nameCompartmentEditPart == null){
    			nameCompartmentEditPart = editPart.getRootPart().getPartFromModel(nameComponentModel);
    		}
    		
    		// If we can find it
    		if (nameCompartmentEditPart != null){
    			editPart = nameCompartmentEditPart;
    		}
    	}
        return editPart.getFigure().getBounds().getCenter();
    }

    /**
     * Gets the palette control within the specified editor.
     * 
     * @param editor The editor to get the control from.
     * @return The palette's control.
     */
    public Control getPaletteControl(DiagramEditor editor){
    	return getPaletteViewer(editor).getControl();
    }

    /**
     * Gets the palette viewer within the specified editor.
     * 
     * @param editor The editor to get the control from.
     * @return The palette's viewer.
     */
    public PaletteViewer getPaletteViewer(DiagramEditor editor){
    	return editor.getRootPart().getViewer().getEditDomain().getPaletteViewer();
    }

    /**
     * Expands the tree item on the package explorer.
     * 
     * @param item The tree item to expand.
     */
    public void actionExpandTreeItem(TreeItem item) {
        WidgetTester.waitForIdle(item.getDisplay());
        if (!item.getExpanded()) {
        	// if this is a item is direct child of the root, do it manually
            if (item.getParentItem() == null) {
                Tree root = item.getParent();
                actionClick(root, 5, 5);
            }
            else {
                int parentX = item.getParentItem().getBounds().x;
                int childX = item.getBounds().x;
                //TreeItems that are descendent of the root have bounding boxes relative to the label
                //Get the left side of the parent's label
                //Then get the left side of the parent's icon (- 16 px)
                //Then go forward 5 px to get the arrow (+ 5)
                actionClick(item, parentX - childX - 11, 5);
            }
            WidgetTester.waitForIdle(item.getDisplay());
        }
    }
    
    public Display getDisplay() {
        Display display;
        
        display = PlugIn.getDefault().getWorkbench().getDisplay();
        
        if (display == null || display.isDisposed()) {
            display = Display.getCurrent();
            
            if (display == null || display.isDisposed()) {
                display = Display.getDefault();
                
                if (display == null || display.isDisposed()) {
                    throw new NullPointerException("Evert Woo exception");
                }
            }
        }
        
        return display;
    }
}