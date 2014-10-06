/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.relationship.alternateassociation;

import static org.eclipse.jdt.core.dom.ASTNode.CLASS_INSTANCE_CREATION;
import static org.eclipse.jdt.core.dom.ASTNode.STRING_LITERAL;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import edu.buffalo.cse.green.editor.model.RelationshipKind;
import edu.buffalo.cse.green.relationships.RelationshipRecognizer;
import edu.buffalo.cse.green.relationships.DeclarationInfoProvider;

/**
 * @see edu.buffalo.cse.green.relationship.RelationshipRecognizer
 * 
 * @author bcmartin
 */
public class AssociationRecognizer extends RelationshipRecognizer {
	
	private class Potential {
		public AbstractList<ASTNode> features;
		public IType type;
		public ITypeBinding typeBinding;
		public Name name;
		
		public Potential( AbstractList<ASTNode> l, IType t, ITypeBinding tb, Name n )
		{
			features = l;
			type = t;
			typeBinding = tb;
			name = n;
		}
	}
	
	private Collection<Potential> _potentials = new ArrayList<Potential>();
	private Collection<Name> _nots = new ArrayList<Name>();
	private boolean _lock = false;
	
	@Override
	public void endVisit(TypeDeclaration t) {
		super.endVisit(t);
		
		_lock = true;
		for(Potential p : _potentials)
		{
			if( ! _nots.contains( p.name ) )
			{
				fireFoundRelationship(p.type, p.typeBinding,
						AssociationPart.class, p.features);
			}
		}
		_lock = false;
	}
	
	/**
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Assignment)
	 */
	public boolean visit(Assignment node) {
		
		while(_lock)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		Expression eLHS = node.getLeftHandSide();
		Expression eRHS = node.getRightHandSide();
		
		if (inConstructor())
		{
			if (eLHS instanceof Name) {
				Name LHS = (Name) eLHS;

				if (isField(LHS))
				{
					if (eRHS.getNodeType() == CLASS_INSTANCE_CREATION) {
						_nots.add(LHS);
					} else if (eRHS.getNodeType() == STRING_LITERAL) {
						_nots.add(LHS);
					}
				}
			}
		}

		if (eLHS instanceof Name && eRHS instanceof Name) {
			Name LHS = (Name) eLHS;
			Name RHS = (Name) eRHS;

			if (!isField(LHS)) return true;
			if (!isParameter(RHS)) return true;
			
			System.out.println("Booyah: found this d00d: "+LHS.getFullyQualifiedName());

			AbstractList<ASTNode> features = new ArrayList<ASTNode>();
			features.add(node.getParent());
			features.add(getMethodDeclaration());
			
			processAddInvocations(features, LHS, node);
			
			//fireFoundRelationship(getCurrentType(), LHS.resolveTypeBinding(),
			//		AssociationPart.class, features);
			
			_potentials.add(
					new Potential(
							features,
							getCurrentType(),
							LHS.resolveTypeBinding(),
							LHS
							)
					);
		}

		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipVisitor#process(org.eclipse.jdt.core.dom.TypeDeclaration)
	 */
	protected boolean process(DeclarationInfoProvider node) {
		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.RelationshipRecognizer#getFlags()
	 */
	public RelationshipKind getFlags() {
		return RelationshipKind.Cardinal;
	}
}