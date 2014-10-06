/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.text.correction;

import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.text.IDocument;

import org.eclipse.jdt.core.ICompilationUnit;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;

import org.eclipse.jdt.internal.corext.dom.LinkedNodeFinder;
import org.eclipse.jdt.internal.corext.dom.NodeFinder;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
/**
 *
 */
public class RenameNodeCompletionProposal extends CUCorrectionProposal {

	private String fNewName;
	private int fOffset;
	private int fLength;

	public RenameNodeCompletionProposal(String name, ICompilationUnit cu, int offset, int length, String newName, int relevance) {
		super(name, cu, relevance, JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE));
		fOffset= offset;
		fLength= length;
		fNewName= newName;
	}

	/*(non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.CUCorrectionProposal#addEdits(org.eclipse.jface.text.IDocument)
	 */
	protected void addEdits(IDocument doc, TextEdit root) throws CoreException {
		super.addEdits(doc, root);

		// build a full AST
		CompilationUnit unit= JavaPlugin.getDefault().getASTProvider().getAST(getCompilationUnit(), ASTProvider.WAIT_YES, null);

		ASTNode name= NodeFinder.perform(unit, fOffset, fLength);
		if (name instanceof SimpleName) {

			SimpleName[] names= LinkedNodeFinder.findByProblems(unit, (SimpleName) name);
			if (names != null) {
				for (int i= 0; i < names.length; i++) {
					SimpleName curr= names[i];
					root.addChild(new ReplaceEdit(curr.getStartPosition(), curr.getLength(), fNewName));
				}
				return;
			}
		}
		root.addChild(new ReplaceEdit(fOffset, fLength, fNewName));
	}
}
