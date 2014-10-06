package edu.buffalo.cse.greenest.view;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;

/*
 * This was largely taken from 
 * http://eclipse.org/articles/Article-GEF-Draw2d/GEF-Draw2d.html
 */
public class TypeCompartmentView extends Figure {
	private ToolbarLayout _layout;
	
	public TypeCompartmentView() {
		_layout = new ToolbarLayout();
		_layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		_layout.setStretchMinorAxis(false);
		_layout.setSpacing(2);
		setLayoutManager(_layout);
		setBorder(new CompartmentFigureBorder());
	}
	
	public void addField(Label fieldName) {
		this.add(fieldName);
	}
	
	public boolean removeField(Label fieldName) {
		try {
			this.remove(fieldName);
			return true;
		} catch (Exception e) {
			System.out.println("Failed to remove field %s".format(fieldName.toString()));
			//e.printStackTrace();	TODO do we need this?
			return false;
		}
	}
	
	public void addMethod(Label methodName) {
		this.add(methodName);
	}
	
	public boolean removeMethod(Label methodName) {
		try {
			this.remove(methodName);
			return true;
		} catch (Exception e) {
			System.out.println("Failed to remove method %s".format(methodName.toString()));
			//e.printStackTrace();
			return false;
		}
	}
	
	// -----------------------------------------------------------------
	public class CompartmentFigureBorder extends AbstractBorder {
		public Insets getInsets(IFigure figure) {
			return new Insets(1,0,0,0);
		}
		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			graphics.drawLine(getPaintRectangle(figure, insets).getTopLeft(),
					tempRect.getTopRight());
		}
	}
}
