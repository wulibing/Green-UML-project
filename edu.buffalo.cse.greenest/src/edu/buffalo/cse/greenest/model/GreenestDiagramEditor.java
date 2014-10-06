package edu.buffalo.cse.greenest.model;


import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;

import org.eclipse.swt.graphics.Color;

import edu.buffalo.cse.greenest.view.TypeCompartmentView;

public class GreenestDiagramEditor extends Figure {
	public static Color classColor = new Color(null,255,255,206);
	private TypeCompartmentView attributeFigure= new TypeCompartmentView();
	private TypeCompartmentView methodFigure= new TypeCompartmentView();
	private TypeModel _typeModel;
	private ViewModel _viewModel;
	public GreenestDiagramEditor(TypeModel t, ViewModel v, Label name){
		_typeModel = t;
		_viewModel=v;
		ToolbarLayout layout = new ToolbarLayout();
	    setLayoutManager(layout);	
	    setBorder((Border) new LineBorder(ColorConstants.black,1));
	    setBackgroundColor(classColor);
	    setOpaque(true);
		
	    add(name);	
	    add(attributeFigure);
	    add(methodFigure);
	}
	
//	TODO create a new UML box
	
		
	
}
