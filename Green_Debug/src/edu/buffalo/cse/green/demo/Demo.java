/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.demo;

import static edu.buffalo.cse.green.test.core.Robot.PALETTE_VSPACING;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import edu.buffalo.cse.green.editor.controller.TypePart;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.test.core.EnhancedTest;
import edu.buffalo.cse.green.test.core.PaletteTool;
import edu.buffalo.cse.green.test.core.Project;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.eclipse.ui.wizards.datatransfer.ZipFileStructureProvider;

public class Demo extends EnhancedTest {
	private static final int MOUSE_MOVE_TIME = 100;
	private static final int MOUSE_STEPS = 20;
	private static final int MOUSE_DELAY = MOUSE_MOVE_TIME / MOUSE_STEPS;

	/**
	 * Animatedly moves to and clicks on the given model.
	 * 
	 * @param model - The given model.
	 */
	protected void leftClick(AbstractModel model) {
		moveMouse(model);
		super.leftClick(model);
	}
	
	/**
	 * Slowly moves the mouse from its current location to the specified
	 * location.
	 * 
	 * @param model - The model to move to.
	 */
	private void moveMouse(AbstractModel model) {
		moveMouse(getModelLocation(model));
	}
	
	/**
	 * Slowly moves the mouse from its current location to the specified
	 * location.
	 *  
	 * @param tool - The <code>PaletteTool</code> to move to.
	 */
	private void moveMouse(PaletteTool tool) {
		Point point = getPaletteLocation();
		moveMouse(new Point(point.x, point.y +
				tool.getLocation() * PALETTE_VSPACING));
	}
	
	protected void leftClick(PaletteTool tool) {
		moveMouse(tool);
		super.leftClick(tool);
	}
	
	@SuppressWarnings("deprecation")
	/**
	 * Slowly moves the mouse from its current location to the specified
	 * location.
	 */
	private void moveMouse(Point end) {
		Point start = getMouseLocation();
		
		int dx = (end.x - start.x) / MOUSE_STEPS;
		int dy = (end.y - start.y) / MOUSE_STEPS;
		
		// move incrementally with delays
		for (int x = 0; x < MOUSE_STEPS; x++) {
			setMouseLocation(start.x + dx * x, start.y + dy * x);
			getDisplay().update();
			delay(MOUSE_DELAY);
		}
		
		// ensure the mouse is exactly on its target
		setMouseLocation(end);
	}
	
	/**
	 * @param point - The given Draw2D point.
	 * @return An SWT point representing the given Draw2D point.
	 */
	private Point getPoint(org.eclipse.draw2d.geometry.Point point) {
		return new Point(point.x, point.y);
	}
	
	private Point getMouseLocation() {
		return getDisplay().getCursorLocation();
	}
	
	/**
	 * Puts the mouse at the given location.
	 * 
	 * @param x - The x value of the location.
	 * @param y - The y value of the location.
	 */
	private void setMouseLocation(int x, int y) {
		getDisplay().setCursorLocation(x, y);
	}
	
	/**
	 * Puts the mouse at the given location.
	 * 
	 * @param point - The given location.
	 */
	private void setMouseLocation(Point point) {
		getDisplay().setCursorLocation(point);
	}
	
	/**
	 * @return The middle-top location of the palette. 
	 */
	private Point getPaletteLocation() {
		Point point = getLocation(getPaletteControl());
		
		return point;
	}
	
	protected Point getLocation(Control control) {
		return control.toDisplay(control.getLocation());
	}

	private Point getModelLocation(AbstractModel model) {
		IFigure figure;
		
		if (model.getPartClass().equals(TypePart.class)) {
			TypePart typePart = (TypePart) getPart(model);
			figure = (IFigure) typePart.getNameLabel();
		} else {
			figure = getPart(model).getFigure();
		}
		
		Point editorLocation = getEditorLocation();
		Point figureLocation = getPoint(figure.getBounds().getCenter());
		
		return new Point(editorLocation.x + figureLocation.x,
				editorLocation.y + figureLocation.y);
	}
	
	private Point getEditorLocation() {
		Point editorLocation = getLocation(
				EDITOR.getRootPart().getViewer().getControl());
		Point paletteSize = getPaletteControl().getSize(); 

		return new Point(editorLocation.x - paletteSize.x, editorLocation.y);
	}
	
	protected Project loadZIP(String name, String location) throws Exception {
		try {
			ZipFile file = new ZipFile(new File(location));
			List<ZipEntry> files = new ArrayList<ZipEntry>();
			
			for (Enumeration<? extends ZipEntry> e =
				file.entries(); e.hasMoreElements();) {
				files.add(e.nextElement());
			}
			
			Project project = new Project(name, null, false);			
			new ImportOperation(project.getJavaProject().getPath(),
					new ZipFileStructureProvider(file),
					new IOverwriteQuery() {
						public String queryOverwrite(String pathString) {
							return IOverwriteQuery.ALL;
						}
			}, files).run(null);

			return project;
		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected boolean doSetup() {
		return false;
	}
	
	protected void clickLocation(Control control, PaletteTool tool,
			int x, int y, int ex, int ey) {
		leftClick(tool);
		
		moveMouse(new Point(x, y));
		leftClick(control, ex, ey);
	}
}