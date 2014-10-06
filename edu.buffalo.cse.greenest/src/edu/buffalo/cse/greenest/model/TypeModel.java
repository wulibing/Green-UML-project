package edu.buffalo.cse.greenest.model;

/**
 * author Green Development Team
 */

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import edu.buffalo.cse.greenest.GreenException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


/*
 * Private Fields for IType, IMethod, and IField
 */
public class TypeModel extends Observable implements Observer{
	private IType _type;
	
	/*
	 * Constructor initializes IType, IMethod, and IField
	 */
	public TypeModel(IType t) {
		_type = t;
	}
	
	/*
	 * Get IType information (Class, Abstract, Anonymous, Interface) 
	 * and Method and Field information 
	 */
	public String getName() {
		return _type.getElementName();
	}
	public void update (Observable o, Object arg) {
		
	}
	public boolean isAnonymous() {
		try {
			return _type.isAnonymous();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isClass() {
		try {
			return _type.isClass();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isInterface() {
		try{
			return _type.isInterface();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		}
	}	
	
	public IMethod[] getMethods() {
		try {
			return _type.getMethods();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void addMethod(IMethod m) {	// TODO
		/* 
		 * This will have to pass in a String consisting of a method body (contents), 
		 * the IJavaElement it will appear in code before (sibling), a boolean for overwriting existing methods (force)
		 * and a progress monitor (monitor), which we'll have to implement ourselves.
		 */
		// _type.createMethod(contents, sibling, force, monitor);
	}
	
	public IField[] getFields() {
		try {
			return _type.getFields();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void addField(IField f) {	// TODO
		// This will follow the same general concept as in addMethod.
		//_type.createField(contents, sibling, force, monitor);
	}	
	
	//Using to get Access Control Modifiers. Decode here? TODO
	public String getKey(){
		return _type.getKey();
	}
	
	// inner class creation
	public TypeModel createType(String contents, IJavaElement sibling, boolean forceOverwrite, IProgressMonitor monitor) {
		try {
			IType newType = _type.createType(contents, sibling, forceOverwrite, monitor);
			return new TypeModel(newType);
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}
}

