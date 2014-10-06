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

package org.eclipse.jdt.internal.ui.preferences.cleanup;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatterExtension;
import org.eclipse.jface.text.formatter.IFormattingContext;

import org.eclipse.jdt.core.JavaCore;

import org.eclipse.jdt.internal.ui.IJavaStatusConstants;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.fix.ICleanUp;
import org.eclipse.jdt.internal.ui.fix.MultiFixMessages;
import org.eclipse.jdt.internal.ui.preferences.formatter.JavaPreview;
import org.eclipse.jdt.internal.ui.text.comment.CommentFormattingContext;


public class CleanUpPreview extends JavaPreview {

	private ICleanUp[] fPreviewCleanUps;
	private boolean fFormat;
	public CleanUpPreview(Composite parent, ICleanUp[] cleanUps) {
		super(JavaCore.getDefaultOptions(), parent);
		fPreviewCleanUps= cleanUps;
		fFormat= false;
	}
	
	public void setCleanUps(ICleanUp[] fCleanUps) {
		fPreviewCleanUps= fCleanUps;
	}
	
	public void setFormat(boolean enable) {
		fFormat= enable;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void doFormatPreview() {
	
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < fPreviewCleanUps.length; i++) {
			buf.append(fPreviewCleanUps[i].getPreview());
			buf.append("\n"); //$NON-NLS-1$
		}
		format(buf.toString());
	}
	
	private void format(String text) {		
        if (text == null) {
            fPreviewDocument.set(""); //$NON-NLS-1$
            return;
        }
        fPreviewDocument.set(text);
		
        if (!fFormat)
        	return;
        
		fSourceViewer.setRedraw(false);
		final IFormattingContext context = new CommentFormattingContext();
		try {
			final IContentFormatter formatter =	fViewerConfiguration.getContentFormatter(fSourceViewer);
			if (formatter instanceof IContentFormatterExtension) {
				final IContentFormatterExtension extension = (IContentFormatterExtension) formatter;
				context.setProperty(FormattingContextProperties.CONTEXT_PREFERENCES, JavaCore.getOptions());
				context.setProperty(FormattingContextProperties.CONTEXT_DOCUMENT, Boolean.valueOf(true));
				extension.format(fPreviewDocument, context);
			} else
				formatter.format(fPreviewDocument, new Region(0, fPreviewDocument.getLength()));
		} catch (Exception e) {
			final IStatus status= new Status(IStatus.ERROR, JavaPlugin.getPluginId(), IJavaStatusConstants.INTERNAL_ERROR, 
				MultiFixMessages.CleanUpRefactoringWizard_formatterException_errorMessage, e); 
			JavaPlugin.log(status);
		} finally {
		    context.dispose();
		    fSourceViewer.setRedraw(true);
		}
	}

    public void setWorkingValues(Map workingValues) {
    	//Don't change the formatter settings
    }
    
}
