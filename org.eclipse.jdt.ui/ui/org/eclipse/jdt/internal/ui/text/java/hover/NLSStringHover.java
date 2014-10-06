/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.text.java.hover;

import java.util.Properties;

import org.eclipse.core.resources.IStorage;

import org.eclipse.jface.internal.text.html.HTMLPrinter;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

import org.eclipse.ui.IEditorInput;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;

import org.eclipse.jdt.internal.corext.dom.NodeFinder;
import org.eclipse.jdt.internal.corext.refactoring.nls.AccessorClassReference;
import org.eclipse.jdt.internal.corext.refactoring.nls.NLSHintHelper;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.javaeditor.ClassFileEditor;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;

/**
 * Provides externalized string as hover info for NLS key.
 *
 * @since 3.1
 */
public class NLSStringHover extends AbstractJavaEditorTextHover {


	/*
	 * @see org.eclipse.jdt.internal.ui.text.java.hover.AbstractJavaEditorTextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		if (!(getEditor() instanceof JavaEditor))
			return null;

		IJavaElement je= getEditorInputJavaElement();
		if (je == null)
			return null;

		// Never wait for an AST in UI thread.
		CompilationUnit ast= JavaPlugin.getDefault().getASTProvider().getAST(je, ASTProvider.WAIT_NO, null);
		if (ast == null)
			return null;

		ASTNode node= NodeFinder.perform(ast, offset, 1);
		if (node instanceof StringLiteral) {
			StringLiteral stringLiteral= (StringLiteral)node;
			return new Region(stringLiteral.getStartPosition(), stringLiteral.getLength());
		} else if (node instanceof SimpleName) {
			SimpleName simpleName= (SimpleName)node;
			return new Region(simpleName.getStartPosition(), simpleName.getLength());
		}

		return null;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.text.java.hover.AbstractJavaEditorTextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		if (!(getEditor() instanceof JavaEditor))
			return null;

		IJavaElement je= getEditorInputJavaElement();
		if (je == null)
			return null;

		CompilationUnit ast= JavaPlugin.getDefault().getASTProvider().getAST(je, ASTProvider.WAIT_ACTIVE_ONLY, null);
		if (ast == null)
			return null;

		ASTNode node= NodeFinder.perform(ast, hoverRegion.getOffset(), hoverRegion.getLength());
		if (!(node instanceof StringLiteral) && !(node instanceof SimpleName))
			return null;
		
		if (node.getLocationInParent() == QualifiedName.QUALIFIER_PROPERTY)
			return null;

		AccessorClassReference ref= NLSHintHelper.getAccessorClassReference(ast, hoverRegion);
		if (ref == null)
			return null;

		IStorage propertiesFile;
		try {
			propertiesFile= NLSHintHelper.getResourceBundle(je.getJavaProject(), ref);
			if (propertiesFile == null)
				return toHtml(JavaHoverMessages.NLSStringHover_NLSStringHover_PropertiesFileNotDetectedWarning, ""); //$NON-NLS-1$
		} catch (JavaModelException ex) {
			return null;
		}

		final String propertiesFileName= propertiesFile.getName();
		Properties properties= NLSHintHelper.getProperties(propertiesFile);
		if (properties == null)
			return null;
		if (properties.isEmpty())
			return toHtml(propertiesFileName, JavaHoverMessages.NLSStringHover_NLSStringHover_missingKeyWarning);

		String identifier= null;
		if (node instanceof StringLiteral) {
			identifier= ((StringLiteral)node).getLiteralValue();
		} else {
			identifier= ((SimpleName)node).getIdentifier();
		}
		if (identifier == null)
			return null;
		
		String value= properties.getProperty(identifier, null);
		if (value != null)
			value= HTMLPrinter.convertToHTMLContent(value);
		else
			value= JavaHoverMessages.NLSStringHover_NLSStringHover_missingKeyWarning;

		return toHtml(propertiesFileName, value);
	}

	private String toHtml(String header, String string) {

		StringBuffer buffer= new StringBuffer();

		HTMLPrinter.addSmallHeader(buffer, header);
		HTMLPrinter.addParagraph(buffer, string);
		HTMLPrinter.insertPageProlog(buffer, 0);
		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
	}

	private IJavaElement getEditorInputJavaElement() {
		if (getEditor() instanceof CompilationUnitEditor)
			return JavaPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(getEditor().getEditorInput());
		else if (getEditor() instanceof ClassFileEditor) {
			IEditorInput editorInput= getEditor().getEditorInput();
			if (editorInput instanceof IClassFileEditorInput)
				return ((IClassFileEditorInput)editorInput).getClassFile();

		}
		return null;
	}

}
