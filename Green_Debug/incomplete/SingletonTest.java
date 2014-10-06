/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package edu.buffalo.cse.green.test.designpattern;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.buffalo.cse.green.designpattern.singleton.SingletonGenerator;
import edu.buffalo.cse.green.designpattern.singleton.SingletonRecognizer;
import edu.buffalo.cse.green.relationship.RelationshipVisitor;
import edu.buffalo.cse.green.testbase.Test;

/**
 * @author bcmartin 
 * @author rjtruban
 * @author tomhicks
 */
public class SingletonTest extends Test {
	protected boolean useEditor() {
	    return false;
	}
    
    public void testSingletonPattern() throws JavaModelException {
	    IType testSingleton = createClass("MyS", "" +
	            "public MyS() {\n" +
	            "    // default constructor\n" +
	            "}\n" +
	            "\n" +
	            "public MyS(Object o) {\n" +
	            "    // secondary constructor\n" +
	            "}\n" +
	            "\n" +
	            "public void someMethod(Object o) {\n" +
	            "    // non-constructor method\n" +
	            "}\n").getType();
	    
	    List constructors = new ArrayList();
	    List nonConstructors = new ArrayList();
	    
	    for (int x = 0; x < testSingleton.getMethods().length; x++) {
	        assertTrue("All methods should be public to begin with",
	                Flags.isPublic(testSingleton.getMethods()[x].getFlags()));
	        
	        // add the method to the appropriate list
	        if (testSingleton.getMethods()[x].isConstructor()) {
	            constructors.add(testSingleton.getMethods()[x]);
	        } else {
	            nonConstructors.add(testSingleton.getMethods()[x]);
	        }
	    }
	    
	    // create the compilation unit
	    CompilationUnit cu = RelationshipVisitor.getCompilationUnit(testSingleton);

	    // run the recognizer on the generator - make sure there's no singleton
	    SingletonRecognizer recognizer = new SingletonRecognizer();
	    cu.accept(recognizer);
	    assertEquals("The type shouldn't be a singleton yet", 0, recognizer.getSingletonCount());
	    
	    // run the generator
	    SingletonGenerator generator = new SingletonGenerator(testSingleton);
	    cu.accept(generator);
	    
	    // run the recognizer on the generator
	    cu.accept(recognizer);
	    assertEquals("The type should be a singleton, but it's not", 1, recognizer.getSingletonCount());

	    ASTVisitor typeAsserter = new ASTVisitor(false) {
	        public boolean visit(MethodDeclaration node) {
	            if (node.isConstructor()) {
	                assertTrue("A constructor (" +
	                        node.getName() + ") was not private",
	                        Flags.isPrivate(node.getModifiers()));
	            } else {
	                assertTrue("A method (" +
	                        node.getName() + ") was private",
	                        !Flags.isPrivate(node.getModifiers()));
	            }

	            
	            return true;
	        }
	    };
	    
	    // make sure all constructors are public and other methods are not
	    cu.accept(typeAsserter);
	}
	
	public void testSingletonPatternWithNoConstructor() throws JavaModelException {
	    IType testSingleton = createClass("MyS").getType();
	    
	    List constructors = new ArrayList();
	    List nonConstructors = new ArrayList();
	    
	    // create the compilation unit
	    CompilationUnit cu = RelationshipVisitor.getCompilationUnit(testSingleton);

	    // run the recognizer on the generator - make sure there's no singleton
	    SingletonRecognizer recognizer = new SingletonRecognizer();
	    cu.accept(recognizer);
	    assertEquals("The type shouldn't be a singleton yet", 0, recognizer.getSingletonCount());
	    
	    // run the generator
	    SingletonGenerator generator = new SingletonGenerator(testSingleton);
	    cu.accept(generator);
	    
	    // run the recognizer on the generator
	    cu.accept(recognizer);
	    assertEquals("The type should be a singleton, but it's not", 1, recognizer.getSingletonCount());

	    ASTVisitor typeAsserter = new ASTVisitor(false) {
	        public boolean visit(MethodDeclaration node) {
	            if (node.isConstructor()) {
	                assertTrue("A constructor (" +
	                        node.getName() + ") was not private",
	                        Flags.isPrivate(node.getModifiers()));
	            } else {
	                assertTrue("A method (" +
	                        node.getName() + ") was private",
	                        !Flags.isPrivate(node.getModifiers()));
	            }

	            
	            return true;
	        }
	    };
	    
	    // make sure all constructors are public and other methods are not
	    cu.accept(typeAsserter);
	}
	
	public void testSingletonPatternWithOneConstructor() throws JavaModelException {
	    IType testSingleton = createClass("MyS", "" +
	            "public MyS() {\n" +
	            "    // default constructor\n" +
	            "}\n").getType();
	    
	    List constructors = new ArrayList();
	    List nonConstructors = new ArrayList();
	    
	    for (int x = 0; x < testSingleton.getMethods().length; x++) {
	        assertTrue("All methods should be public to begin with",
	                Flags.isPublic(testSingleton.getMethods()[x].getFlags()));
	        
	        // add the method to the appropriate list
	        if (testSingleton.getMethods()[x].isConstructor()) {
	            constructors.add(testSingleton.getMethods()[x]);
	        } else {
	            nonConstructors.add(testSingleton.getMethods()[x]);
	        }
	    }
	    
	    // create the compilation unit
	    CompilationUnit cu = RelationshipVisitor.getCompilationUnit(testSingleton);

	    // run the recognizer on the generator - make sure there's no singleton
	    SingletonRecognizer recognizer = new SingletonRecognizer();
	    cu.accept(recognizer);
	    assertEquals("The type shouldn't be a singleton yet", 0, recognizer.getSingletonCount());
	    
	    // run the generator
	    SingletonGenerator generator = new SingletonGenerator(testSingleton);
	    cu.accept(generator);
	    
	    // run the recognizer on the generator
	    cu.accept(recognizer);
	    assertEquals("The type should be a singleton, but it's not", 1, recognizer.getSingletonCount());

	    ASTVisitor typeAsserter = new ASTVisitor(false) {
	        public boolean visit(MethodDeclaration node) {
	            if (node.isConstructor()) {
	                assertTrue("A constructor (" +
	                        node.getName() + ") was not private",
	                        Flags.isPrivate(node.getModifiers()));
	            } else {
	                assertTrue("A method (" +
	                        node.getName() + ") was private",
	                        !Flags.isPrivate(node.getModifiers()));
	            }

	            
	            return true;
	        }
	    };
	    
	    // make sure all constructors are public and other methods are not
	    cu.accept(typeAsserter);
	}
	
	public void testSingletonPatterMultipleGeneration() throws JavaModelException {
	    IType testSingleton = createClass("MyS", "" +
	            "public MyS() {\n" +
	            "    // default constructor\n" +
	            "}\n").getType();
	    
	    List constructors = new ArrayList();
	    List nonConstructors = new ArrayList();
	    
	    for (int x = 0; x < testSingleton.getMethods().length; x++) {
	        assertTrue("All methods should be public to begin with",
	                Flags.isPublic(testSingleton.getMethods()[x].getFlags()));
	        
	        // add the method to the appropriate list
	        if (testSingleton.getMethods()[x].isConstructor()) {
	            constructors.add(testSingleton.getMethods()[x]);
	        } else {
	            nonConstructors.add(testSingleton.getMethods()[x]);
	        }
	    }
	    
	    // create the compilation unit
	    CompilationUnit cu = RelationshipVisitor.getCompilationUnit(testSingleton);

	    // run the recognizer on the generator - make sure there's no singleton
	    SingletonRecognizer recognizer = new SingletonRecognizer();
	    cu.accept(recognizer);
	    assertEquals("The type shouldn't be a singleton yet", 0, recognizer.getSingletonCount());
	    
	    // run the generator
	    SingletonGenerator generator = new SingletonGenerator(testSingleton);
	    cu.accept(generator);
	    
	    // run the recognizer on the generator
	    cu.accept(recognizer);
	    assertEquals("The type should be a singleton, but it's not", 1, recognizer.getSingletonCount());

	    
	    int oldCUValue = cu.hashCode();
	    // Run the generator again and make sure nothing has changed
	    cu.accept(generator);
	    cu.accept(recognizer);
	    assertEquals("The type should be a singleton, but it's not", 1, recognizer.getSingletonCount());
	    assertEquals("The previous compilation unit does not match the current", oldCUValue, cu.hashCode());
	    
	    ASTVisitor typeAsserter = new ASTVisitor(false) {
	        public boolean visit(MethodDeclaration node) {
	            if (node.isConstructor()) {
	                assertTrue("A constructor (" +
	                        node.getName() + ") was not private",
	                        Flags.isPrivate(node.getModifiers()));
	            } else {
	                assertTrue("A method (" +
	                        node.getName() + ") was private",
	                        !Flags.isPrivate(node.getModifiers()));
	            }

	            
	            return true;
	        }
	    };
	    
	    // make sure all constructors are public and other methods are not
	    cu.accept(typeAsserter);
	}
}
