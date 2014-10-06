/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.relationships;

import static org.eclipse.jdt.core.dom.ASTNode.FIELD_DECLARATION;
import static org.eclipse.jdt.core.dom.ASTNode.METHOD_DECLARATION;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.buffalo.cse.green.GreenException;

/**
 * Provides a uniform way to access information contained within an
 * <code>AbstractTypeDeclaration</code> subclass. This is necessary because the
 * nodes that represent enum instances are different from the ones that
 * represent classes and interfaces.
 * 
 * @author bcmartin
 * 
 * Hypothesis: we need to augment this to also handle AnonymousClassDeclaration nodes
 */
public abstract class DeclarationInfoProvider {
//	private AbstractTypeDeclaration _node;
	
	
	/**
	 * @return A list of <code>FieldDeclaration</code> nodes belonging to the
	 * given <code>AbstractTypeDeclaration</code>.
	 */
	public abstract List<FieldDeclaration> getFields();
	
	/**
	 * @return A list of <code>MethodDeclaration</code> nodes belonging to the
	 * given <code>AbstractTypeDeclaration</code>.
	 */
	public abstract List<MethodDeclaration> getMethods();
	
	/**
	 * @return The superclass <code>Type</code> of the given
	 * <code>AbstractTypeDeclaration</code>. 
	 */
	public abstract Type getSuperclassType();
	
	/**
	 * @return true if the given <code>AbstractTypeDeclaration</code> represents
	 * an interface, false otherwise.
	 */
	public abstract boolean isInterface();
	
	/**
	 * Sets the given <code>AbstractTypeDeclaration</code>'s superclass to the
	 * given type.
	 * 
	 * @param type - The given <code>Type</code>
	 */
	public abstract void setSuperclassType(Type type);
	
	/**
	 * @return A list of <code>Type</code> nodes. For a class or enum, returns
	 * a list of implemented interfaces; for an interface, returns a list of
	 * extended interfaces. 
	 */
	public abstract List<Type> getSuperInterfaceTypes();
	
//	protected DeclarationInfoProvider(AbstractTypeDeclaration node) {
//	protected DeclarationInfoProvider(ASTNode node) {
//		if (node instanceof AbstractTypeDeclaration) {
//			_node = (AbstractTypeDeclaration) node;
//		}
//		else if (node instanceof AnonymousClassDeclaration) {
//			_someOtherVariable = (AnonymousClassDeclaration) node;
//		}
//	}

	public static DeclarationInfoProvider getInfoProvider(AnonymousClassDeclaration node) {
		return new AnonymousClassDeclarationInfoProvider(node);
	}
	
	/**
	 * @param node - The <code>EnumDeclaration</code> to represent
	 * @return An info provider for the given enum
	 */
	public static DeclarationInfoProvider getInfoProvider(EnumDeclaration node) {
		return new EnumDeclarationInfoProvider(node);
	}
	
	/**
	 * @param node - The <code>TypeDeclaration</code> to represent
	 * @return An info provider for the given type
	 */
	public static DeclarationInfoProvider getInfoProvider(TypeDeclaration node) {
		return new TypeDeclarationInfoProvider(node);
	}
	
	/**
	 * @param fields - A list of <code>FieldDeclaration</code> to get the names
	 * of
	 * @return The names of the given fields
	 */
	public static List<String> getFieldNames(List<FieldDeclaration> fields) {
		List<String> fieldNames = new ArrayList<String>();
		
		for (FieldDeclaration field : fields) {
			fieldNames.addAll(getFieldNamesFromFragments(field));
		}
		
		return fieldNames;
	}
	

	/**
	 * @param field - The given <code>FieldDeclaration</code>
	 * @return The list of names of fields in the given declaration
	 */
	@SuppressWarnings("unchecked")
	private static List<String> getFieldNamesFromFragments(
			FieldDeclaration field) {
		List<String> fields = new ArrayList<String>();
		List<VariableDeclarationFragment> fragments =
			(List<VariableDeclarationFragment>) field.fragments();

		for (VariableDeclarationFragment fragment : fragments) {
			String name = fragment.getName().getIdentifier();

			if (!name.equals("")) {
				fields.add(name);
				break;
			}
		}
		
		return fields;
	}

	/**
	 * @return The node represented by this instance
	 */
//	public abstract ASTNode getDeclaration();
//	{
//		return _node;
//	}

	/**
	 * @return All the body declarations of the represented node
	 */
	public abstract List<BodyDeclaration> bodyDeclarations();
//	{
//		return _node.bodyDeclarations();
//	}

	public abstract ITypeBinding resolveBinding();
//	{
//		return null;
//	}
}

/**
 * An implementation of a <code>DeclarationInfoProvider</code> for
 * <code>EnumDeclaration</code> nodes.
 * 
 * @author bcmartin
 */
class EnumDeclarationInfoProvider extends DeclarationInfoProvider {
	private EnumDeclaration _node;

	EnumDeclarationInfoProvider(EnumDeclaration node) {
//		super(node);
		_node = node;
	}
	
	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getFields()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FieldDeclaration> getFields() {
		List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
		
		for (BodyDeclaration dec : (List<BodyDeclaration>)
				_node.bodyDeclarations()) {
			if (dec.getNodeType() == FIELD_DECLARATION) {
				fields.add((FieldDeclaration) dec);
			}
		}
		
		return fields;
	}
	
	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getMethods()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MethodDeclaration> getMethods() {
		List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
		
		for (BodyDeclaration dec : (List<BodyDeclaration>)
				_node.bodyDeclarations()) {
			if (dec.getNodeType() == METHOD_DECLARATION) {
				methods.add((MethodDeclaration) dec);
			}
		}
		
		return methods;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getSuperclassType()
	 */
	@Override
	public Type getSuperclassType() {
		return null;  // Should this, for pedagogical reasons, perhaps return Object?
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getSuperInterfaceTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Type> getSuperInterfaceTypes() {
		return (List<Type>) _node.superInterfaceTypes();
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#isInterface()
	 */
	@Override
	public boolean isInterface() {
		return false;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#setSuperclassType(org.eclipse.jdt.core.dom.Type)
	 */
	@Override
	public void setSuperclassType(Type type) {
		GreenException.illegalOperation("Enums do not have a superclass");
	}

//	@Override
//	// may need to return ASTNode to handle
//	// TypeDeclaration (for interfaces and classes)
//	// EnumDeclaration
//	// AnonymousClassDeclaration
//	public ASTNode getDeclaration() {
//		return _node;
//	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BodyDeclaration> bodyDeclarations() {
		return (List<BodyDeclaration>) _node.bodyDeclarations();
	}

	@Override
	public ITypeBinding resolveBinding() {
		return _node.resolveBinding();
	}
}

/**
 * An implementation of a <code>DeclarationInfoProvider</code> for
 * <code>TypeDeclaration</code> nodes.
 * 
 * @author bcmartin
 */
class TypeDeclarationInfoProvider extends DeclarationInfoProvider {
	private TypeDeclaration _node;

	TypeDeclarationInfoProvider(TypeDeclaration node) {
//		super(node);
		_node = node;
	}
	
	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getFields()
	 */
	@Override
	public List<FieldDeclaration> getFields() {
		List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
		
		for (FieldDeclaration field : _node.getFields()) {
			fields.add(field);
		}
		
		return fields;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getMethods()
	 */
	@Override
	public List<MethodDeclaration> getMethods() {
		List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
		
		for (MethodDeclaration method : _node.getMethods()) {
			methods.add(method);
		}

		return methods;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getSuperclassType()
	 */
	@Override
	public Type getSuperclassType() {
		return _node.getSuperclassType();
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getSuperInterfaceTypes()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Type> getSuperInterfaceTypes() {
		return (AbstractList<Type>) _node.superInterfaceTypes();
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#isInterface()
	 */
	@Override
	public boolean isInterface() {
		return _node.isInterface();
	}
	
	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#setSuperclassType(org.eclipse.jdt.core.dom.Type)
	 */
	@Override
	public void setSuperclassType(Type type) {
		_node.setSuperclassType(type);
	}

//	@Override
//	public ASTNode getDeclaration() {
//		return _node;
//	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BodyDeclaration> bodyDeclarations() {
		return (List<BodyDeclaration>) _node.bodyDeclarations();
	}

	@Override
	public ITypeBinding resolveBinding() {
		return _node.resolveBinding();
	}
}

/**
 * An implementation of a <code>DeclarationInfoProvider</code> for
 * <code>AnonymousClassDeclaration</code> nodes.
 * 
 * @author alphonce
 */
class AnonymousClassDeclarationInfoProvider extends DeclarationInfoProvider {
	private AnonymousClassDeclaration _node;

	AnonymousClassDeclarationInfoProvider(AnonymousClassDeclaration node) {
		_node = node;
	}
	
	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getFields()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<FieldDeclaration> getFields() {
		List<BodyDeclaration> bodyDecls = _node.bodyDeclarations();
		final List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
	
		for(BodyDeclaration bd : bodyDecls) {
			bd.accept(new ASTVisitor() {
				@Override
				public boolean visit(FieldDeclaration fd){
					fields.add(fd);
					return true;
				}
			});
		}
		return fields;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getMethods()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MethodDeclaration> getMethods() {
		List<BodyDeclaration> bodyDecls = _node.bodyDeclarations();
		final List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	
		for(BodyDeclaration bd : bodyDecls) {
			bd.accept(new ASTVisitor() {
				@Override
				public boolean visit(MethodDeclaration md){
					methods.add(md);
					return true;
				}
			});
		}
		return methods;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getSuperclassType()
	 */
	@Override
	public Type getSuperclassType() {
		return null; // Maybe Object, for pedagogical reasons?
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#getSuperInterfaceTypes()
	 */
	@Override
	public List<Type> getSuperInterfaceTypes() {
		List<Type> superInterfaces = new ArrayList<Type>();
		superInterfaces.add(((ClassInstanceCreation)_node.getParent()).getType());
		return superInterfaces;
	}

	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#isInterface()
	 */
	@Override
	public boolean isInterface() {
		return false;
	}
	
	/**
	 * @see edu.buffalo.cse.green.relationships.DeclarationInfoProvider#setSuperclassType(org.eclipse.jdt.core.dom.Type)
	 */
	@Override
	public void setSuperclassType(Type type) {
		// CANNOT DO THIS - SHOULD WE THROW AN EXCEPTION??
	}

//	@Override
//	public ASTNode getDeclaration() {
//		return _node;
//	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BodyDeclaration> bodyDeclarations() {
		return (List<BodyDeclaration>) _node.bodyDeclarations();
	}

	@Override
	public ITypeBinding resolveBinding() {
		return _node.resolveBinding();
	}
}
