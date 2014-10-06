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

import edu.buffalo.cse.green.relationship.association.AssociationPart;
import edu.buffalo.cse.green.relationship.composition.CompositionPart;
import edu.buffalo.cse.green.relationship.dependency.DependencyLPart;
import edu.buffalo.cse.green.relationship.generalization.GeneralizationPart;
import edu.buffalo.cse.green.relationship.realization.RealizationPart;

/**
 * Contains palette tool constants. 
 * 
 * @author Blake
 */
public enum PaletteTool {
	Selection(1),
	Class(2),
	Enum(3),
	Interface(4),
	Note(5),
	Association(AssociationPart.class.getName(), 6),
	Composition(CompositionPart.class.getName(), 7),
	Dependency(DependencyLPart.class.getName(), 8),
	Generalization(GeneralizationPart.class.getName(), 9),
	Realization(RealizationPart.class.getName(), 10);
	
	private int _location;
	private String _name;
	
	PaletteTool(int location) {
		_location = location;
	}
	
	PaletteTool(String name, int location) {
		this(location);
		_name = name;
	}
	
	/**
	 * @return The location of the palette tool.
	 */
	public int getLocation() {
		return _location;
	}

	/**
	 * @return The palette tool's name.
	 */
	public String getName() {
		return _name;
	}
}
