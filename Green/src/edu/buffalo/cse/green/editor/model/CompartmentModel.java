/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.editor.model;

import static org.eclipse.draw2d.ToolbarLayout.ALIGN_CENTER;
import static org.eclipse.draw2d.ToolbarLayout.ALIGN_TOPLEFT;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.IJavaElement;

import edu.buffalo.cse.green.GreenException;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.controller.CompartmentPart;
import edu.buffalo.cse.green.editor.controller.TypePart;
import edu.buffalo.cse.green.editor.model.commands.DeleteCommand;
import edu.buffalo.cse.green.editor.view.CompartmentFigure;

// Incremental exploration icons related imports
//import java.util.ArrayList;
//import java.util.List;
//import org.eclipse.draw2d.Label;
//import org.eclipse.draw2d.MouseEvent;
//import org.eclipse.draw2d.MouseListener;
//import edu.buffalo.cse.green.PlugIn;
//import edu.buffalo.cse.green.editor.model.commands.IncrementalExploreCommand;
//import edu.buffalo.cse.green.relationships.RelationshipGroup;

/**
 * Holds a list of IMembers. Provides a list-like interface.
 * 
 * @author hk47
 * @author bcmartin
 */
public abstract class CompartmentModel
extends AbstractModel<AbstractModel, TypeModel, IJavaElement> {
	private CompartmentModel() {}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getPartClass()
	 */
	public Class getPartClass() {
		return CompartmentPart.class;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getDeleteCommand(edu.buffalo.cse.green.editor.DiagramEditor)
	 */
	public DeleteCommand getDeleteCommand(DiagramEditor editor) {
		return null;
	}

	/**
	 * @param editor - The <code>DiagramEditor</code> containing this model.
	 * 
	 * @return A command to hide this model.
	 */
	public Command getHideCommand(DiagramEditor editor) {
		return null;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getJavaElement()
	 */
	public IJavaElement getJavaElement() {
		return null;
	}

	/**
	 * Adds a <code>MethodModel</code> child to this model.
	 * 
	 * @param model - The child.
	 */
	abstract void addChild(MethodModel model);

	/**
	 * Adds a <code>FieldModel</code> child to this model.
	 * 
	 * @param model - The child.
	 */
	abstract void addChild(FieldModel model);
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#removeFromParent()
	 */
	public void removeFromParent() {
		removeChildren();
		getParent().removeChild(this);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getChildren().toString();
	}

	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#handleDispose()
	 */
	public void handleDispose() {
		// do nothing
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#getTypeModel()
	 */
	public TypeModel getTypeModel() {
		return getParent().getTypeModel();
	}
	
	/**
	 * @return A value indicating how the compartment should lay out its
	 * contents (e.g. left- or center-aligned).
	 */
	public abstract int getLayout();

	/**
	 * @return A new <code>CompartmentModel</code> that holds
	 * <code>FieldModel</code> instances.
	 */
	public static CompartmentModel newFieldCompartment() {
		return new CompartmentModel() {
			/**
			 * @see edu.buffalo.cse.green.editor.model.CompartmentModel#addChild(edu.buffalo.cse.green.editor.model.FieldModel)
			 */
			void addChild(FieldModel model) {
				addChild(model, model.getJavaElement());
			}

			/**
			 * @see edu.buffalo.cse.green.editor.model.CompartmentModel#addChild(edu.buffalo.cse.green.editor.model.MethodModel)
			 */
			void addChild(MethodModel model) {
				GreenException.illegalOperation("Invalid parent.");
			}

			/**
			 * @see edu.buffalo.cse.green.editor.model.CompartmentModel#getLayout()
			 */
			public int getLayout() {
				return ALIGN_TOPLEFT;
			}

			/**
			 * @see edu.buffalo.cse.green.editor.model.CompartmentModel#dispatchLabels(edu.buffalo.cse.green.editor.controller.CompartmentPart, edu.buffalo.cse.green.editor.view.CompartmentFigure)
			 */
			public void dispatchLabels(CompartmentPart part, CompartmentFigure figure) {}

			public boolean isTypeLabelCompartment() {
				return false;
			}
		};
	}

	/**
	 * @return A new <code>CompartmentModel</code> that holds
	 * <code>MethodModel</code> instances.
	 */
	public static CompartmentModel newMethodCompartment() {
		return new CompartmentModel() {
			/**
			 * @see edu.buffalo.cse.green.editor.model.CompartmentModel#addChild(edu.buffalo.cse.green.editor.model.FieldModel)
			 */
			void addChild(FieldModel model) {
				GreenException.illegalOperation("Invalid parent.");
			}

			/**
			 * @see edu.buffalo.cse.green.editor.model.CompartmentModel#addChild(edu.buffalo.cse.green.editor.model.MethodModel)
			 */
			void addChild(MethodModel model) {
				addChild(model, model.getJavaElement());
			}

			/**
			 * @see edu.buffalo.cse.green.editor.model.CompartmentModel#getLayout()
			 */
			public int getLayout() {
				return ALIGN_TOPLEFT;
			}

			/**
			 * @see edu.buffalo.cse.green.editor.model.CompartmentModel#dispatchLabels(edu.buffalo.cse.green.editor.controller.CompartmentPart, edu.buffalo.cse.green.editor.view.CompartmentFigure)
			 */
			public void dispatchLabels(CompartmentPart part, CompartmentFigure figure) {}

			public boolean isTypeLabelCompartment() {
				return false;
			}
		};
	}

	/**
	 * @return A new <code>CompartmentModel</code> that holds
	 * <code>TypeModel</code> instances.
	 */
	public static CompartmentModel newTypeCompartment() {
		return new CompartmentModel() {
			/**
			 * @see edu.buffalo.cse.green.editor.model.CompartmentModel#addChild(edu.buffalo.cse.green.editor.model.FieldModel)
			 */
			void addChild(FieldModel model) {
				GreenException.illegalOperation("Invalid parent.");
			}

			/**
			 * @see edu.buffalo.cse.green.editor.model.CompartmentModel#addChild(edu.buffalo.cse.green.editor.model.MethodModel)
			 */
			void addChild(MethodModel model) {
				GreenException.illegalOperation("Invalid parent.");
			}

			/**
			 * @see edu.buffalo.cse.green.editor.model.CompartmentModel#getLayout()
			 */
			public int getLayout() {
				return ALIGN_CENTER;
			}

			/**
			 * @see edu.buffalo.cse.green.editor.model.CompartmentModel#dispatchLabels(edu.buffalo.cse.green.editor.controller.CompartmentPart)
			 */
			public void dispatchLabels(CompartmentPart part, CompartmentFigure figure) {
				final TypePart parent = (TypePart) part.getParent();
				IFigure relLabel = parent.getRelLabel();
				figure.add(parent.getNameLabel());
				figure.add(relLabel);
				relLabel.setLayoutManager(new ToolbarLayout(true));
				
				
//   Below is the code which inputs the incremental explorer icons within the display model itself.
//   It has been commented out awaiting decisions which will be made after the release of RC3 as to
//   how it should be displayed within the editor window, as well as possible options for toggling
//   it on and off.
//				
//				for (final RelationshipGroup group
//						: PlugIn.getRelationshipList()) {
//					Label rel =
//						new Label(" ", group.getImageDescriptor().createImage());
//					relLabel.add(rel);
//
//					rel.addMouseListener(new MouseListener() {
//						public void mousePressed(MouseEvent me) {
//							List<RelationshipGroup> relationships =
//								new ArrayList<RelationshipGroup>();
//							relationships.add(group);
//							TypeModel typeModel = (TypeModel) parent.getModel();
//							DiagramEditor editor = parent.getEditor();
//							editor.execute(new IncrementalExploreCommand(editor,
//									typeModel, relationships));
//						}
//
//						public void mouseReleased(MouseEvent me) {}
//						public void mouseDoubleClicked(MouseEvent me) {}
//					});
//				}
			}

			public boolean isTypeLabelCompartment() {
				return true;
			}
		};
	}

	/**
	 * Adds in extraneous labels as necessary.
	 * 
	 * @param part - The part corresponding to this model.
	 */
	public abstract void dispatchLabels(CompartmentPart part,
			CompartmentFigure figure);
	
	/**
	 * @return true if this <code>CompartmentModel</code> holds the type label,
	 * false otherwise.
	 */
	public abstract boolean isTypeLabelCompartment();
	
	/**
	 * @see edu.buffalo.cse.green.editor.model.AbstractModel#setSize(org.eclipse.draw2d.geometry.Dimension)
	 */
	public void setSize(Dimension size) {
		GreenException.illegalOperation("Cannot set a compartment's size");
	}
}