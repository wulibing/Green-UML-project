/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * A reusable composite for selecting either an OK or Cancel button.  
 * An Apply button can be enabled by passing in <code>true</code>
 * for the third parameter in the constructor.
 * 
 * @author bcmartin
 * @author zgwang
 */
public class OKCancelComposite extends Composite {
	
	/**
	 * Constructor
	 * @param listener - Listener for the OK, Cancel, [Apply] buttons.
	 * @param parent - The parent container of this container.
	 * @param showApply - Whether or not the Apply button should be shown.
	 */
	public OKCancelComposite(final OKCancelListener listener, Composite parent,
			boolean showApply) {
		super(parent, SWT.FILL);
		
		setLayoutData(new GridData(GridData.END
				| GridData.HORIZONTAL_ALIGN_END));
		
		Button okButton = new Button(this, 0);
		//TODO Find a better way to make buttons wide?
		okButton.setText("        OK        ");
		okButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		okButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				listener.okPressed();
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		 //Apply button is only generated if it is enabled by showApply in constructor
		if(showApply) {
			Button applyButton = new Button(this, 0);
			applyButton.setText("Apply");
			applyButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			applyButton.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					listener.applyPressed();
				}
	
				public void widgetDefaultSelected(SelectionEvent e) {}
			});
		}

		Button cancelButton = new Button(this, 0);
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancelButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				listener.cancelPressed();
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}
}

/**
 * Provides a way to hook into the button press events of an
 * <code>OKCancelComposite</code>.
 * 
 * @author bcmartin
 */
interface OKCancelListener {
	/**
	 * Called when the Apply button is pressed.
	 */
	void applyPressed();
	
	/**
	 * Called when the Cancel button is pressed.
	 */
	void cancelPressed();

	/**
	 * Called when the OK button is pressed.
	 */
	void okPressed();
}
