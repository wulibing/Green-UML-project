/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import org.eclipse.search.ui.text.Match;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;

import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jdt.internal.corext.dom.NodeFinder;

public class ExceptionOccurrencesFinder extends ASTVisitor implements IOccurrencesFinder {

	public static final String IS_EXCEPTION= "isException"; //$NON-NLS-1$
	
	private AST fAST;
	private Name fSelectedName;
	
	private ITypeBinding fException;
	private ASTNode fStart;
	private List fResult;
	
	public ExceptionOccurrencesFinder() {
		fResult= new ArrayList();
	}
	
	public String initialize(CompilationUnit root, int offset, int length) {
		return initialize(root, NodeFinder.perform(root, offset, length));
	}
	
	public String initialize(CompilationUnit root, ASTNode node) {
		fAST= root.getAST();
		if (!(node instanceof Name)) {
			return SearchMessages.ExceptionOccurrencesFinder_no_exception;  
		}
		fSelectedName= ASTNodes.getTopMostName((Name)node);
		ASTNode parent= fSelectedName.getParent();
		MethodDeclaration decl= resolveMethodDeclaration(parent);
		if (decl != null && methodThrowsException(decl, fSelectedName)) {
			fException= fSelectedName.resolveTypeBinding();
			fStart= decl.getBody();
		} else if (parent instanceof Type) {
			parent= parent.getParent();
			if (parent instanceof SingleVariableDeclaration && parent.getParent() instanceof CatchClause) {
				CatchClause catchClause= (CatchClause)parent.getParent();
				TryStatement tryStatement= (TryStatement)catchClause.getParent();
				if (tryStatement != null) {
					IVariableBinding var= catchClause.getException().resolveBinding();
					if (var != null && var.getType() != null) {
						fException= var.getType();
						fStart= tryStatement.getBody();
					}
				}
			}
		}
		if (fException == null || fStart == null)
			return SearchMessages.ExceptionOccurrencesFinder_no_exception;  
		return null;
	}
	
	private MethodDeclaration resolveMethodDeclaration(ASTNode node) {
		if (node instanceof MethodDeclaration)
			return (MethodDeclaration)node;
		Javadoc doc= (Javadoc) ASTNodes.getParent(node, ASTNode.JAVADOC);
		if (doc == null)
			return null;
		if (doc.getParent() instanceof MethodDeclaration)
			return (MethodDeclaration) doc.getParent();
		return null;
	}
	
	private boolean methodThrowsException(MethodDeclaration method, Name exception) {
		ASTMatcher matcher = new ASTMatcher();
		for (Iterator iter = method.thrownExceptions().iterator(); iter.hasNext();) {
			Name thrown = (Name)iter.next();
			if (exception.subtreeMatch(matcher, thrown))
				return true;
		}
		return false;
	}
	
	public List perform() {
		fStart.accept(this);
		if (fSelectedName != null) {
			fResult.add(fSelectedName);
		}
		return fResult;
	}
	
	public void collectOccurrenceMatches(IJavaElement element, IDocument document, Collection resultingMatches) {
		HashMap lineToLineElement= new HashMap();
		
		for (Iterator iter= fResult.iterator(); iter.hasNext();) {
			ASTNode node= (ASTNode) iter.next();
			int startPosition= node.getStartPosition();
			int length= node.getLength();
			try {
				boolean isException= node == fSelectedName;
				int line= document.getLineOfOffset(startPosition);
				Integer lineInteger= new Integer(line);
				ExceptionOccurrencesGroupKey groupKey= (ExceptionOccurrencesGroupKey) lineToLineElement.get(lineInteger);
				if (groupKey == null) {
					IRegion region= document.getLineInformation(line);
					String lineContents= document.get(region.getOffset(), region.getLength()).trim();
					groupKey= new ExceptionOccurrencesGroupKey(element, line, lineContents, isException);
					lineToLineElement.put(lineInteger, groupKey);
				} else if (isException) {
					// the line with the target exception always has the exception icon:
					groupKey.setException(true);
				}
				Match match= new Match(groupKey, startPosition, length);
				resultingMatches.add(match);
			} catch (BadLocationException e) {
				//nothing
			}
		}
	}
		
	public String getJobLabel() {
		return SearchMessages.ExceptionOccurrencesFinder_searchfor ; 
	}
	
	public String getElementName() {
		if (fSelectedName != null) {
			return ASTNodes.asString(fSelectedName);
		}
		return null;
	}
	
	public String getUnformattedPluralLabel() {
		return SearchMessages.ExceptionOccurrencesFinder_label_plural;
	}
	
	public String getUnformattedSingularLabel() {
		return SearchMessages.ExceptionOccurrencesFinder_label_singular;
	}
	
	public boolean visit(AnonymousClassDeclaration node) {
		return false;
	}

	public boolean visit(CastExpression node) {
		if ("java.lang.ClassCastException".equals(fException.getQualifiedName())) //$NON-NLS-1$
			fResult.add(node.getType());
		return super.visit(node);
	}
	
	public boolean visit(ClassInstanceCreation node) {
		if (matches(node.resolveConstructorBinding())) {
			fResult.add(node.getType());
		}
		return super.visit(node);
	}
	
	public boolean visit(ConstructorInvocation node) {
		if (matches(node.resolveConstructorBinding())) {
			// mark this
			SimpleName name= fAST.newSimpleName("xxxx"); //$NON-NLS-1$
			name.setSourceRange(node.getStartPosition(), 4);
			fResult.add(name);
		}
		return super.visit(node);
	}
	
	public boolean visit(MethodInvocation node) {
		if (matches(node.resolveMethodBinding()))
			fResult.add(node.getName());
		return super.visit(node);
	}
	
	public boolean visit(SuperConstructorInvocation node) {
		if (matches(node.resolveConstructorBinding())) {
			SimpleName name= fAST.newSimpleName("xxxxx"); //$NON-NLS-1$
			name.setSourceRange(node.getStartPosition(), 5);
			fResult.add(name);
		}
		return super.visit(node);
	}
	
	public boolean visit(SuperMethodInvocation node) {
		if (matches(node.resolveMethodBinding())) {
			fResult.add(node.getName());
		}
		return super.visit(node);
	}
	
	public boolean visit(ThrowStatement node) {
		if (matches(node.getExpression().resolveTypeBinding())) {
			SimpleName name= fAST.newSimpleName("xxxxx"); //$NON-NLS-1$
			name.setSourceRange(node.getStartPosition(), 5);
			fResult.add(name);
			
		}
		return super.visit(node);
	}
	
	public boolean visit(TypeDeclarationStatement node) {
		// don't dive into local type declarations.
		return false;
	}
	
	private boolean matches(IMethodBinding binding) {
		if (binding == null)
			return false;
		ITypeBinding[] exceptions= binding.getExceptionTypes();
		for (int i = 0; i < exceptions.length; i++) {
			ITypeBinding exception= exceptions[i];
			if(matches(exception))
				return true;
		}
		return false;
	}

	private boolean matches(ITypeBinding exception) {
		if (exception == null)
			return false;
		while (exception != null) {
			if (Bindings.equals(fException, exception))
				return true;
			exception= exception.getSuperclass();
		}
		return false;
	}


}
