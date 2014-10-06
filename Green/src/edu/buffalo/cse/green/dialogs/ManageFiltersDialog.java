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

import static edu.buffalo.cse.green.preferences.PreferenceInitializer.P_FILTERS_MEMBER;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import edu.buffalo.cse.green.PlugIn;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.filters.MemberFilter;
import edu.buffalo.cse.green.editor.model.filters.MemberVisibility;

/**
 * Provides the user with the ability to create and enable/disable filters used
 * in Green. Filters can be used to remove members with different visibilities
 * and support pattern matching in the members' names.
 * 
 * @author bcmartin
 */
public class ManageFiltersDialog extends Dialog implements OKCancelListener {
	private Combo _behaveCombo;
	private Text _patternText;
	private Combo _typeCombo;
	private VisibilityComposite _visHolder;
	private Table _filterTable;
	
	public ManageFiltersDialog(Shell shell) {
		super(shell);
		create();
		getShell().setText("Manage Filters");
	}
	
	/**
	 * Adds a filter to the list using the values in the dialog.
	 */
	private void addFilter() {
		int i = _typeCombo.getSelectionIndex();
		
		int enabled = 2 + _behaveCombo.getSelectionIndex();
		boolean types = (i == 1 || i == 3);
		boolean fields = (i == 1 || i == 0);
		boolean methods = (i == 1 || i == 2);
		int visibility = _visHolder.getValue().intValue();
		String name = _patternText.getText();
		
		if (!Pattern.matches("[a-zA-Z0-9?*_.]+", name)) {
			MessageDialog.openInformation(getShell(), "Invalid Pattern",
					"Invalid Pattern");
		}

		addFilter(new MemberFilter(enabled, types, fields, methods,
				MemberVisibility.makeVisibility(visibility), name));
	}

	/**
	 * Adds the given filter to the list of filters and resets the dialog's
	 * settings to the defaults.
	 * 
	 * @param filter - The given <code>MemberFilter</code>.
	 */
	private void addFilter(MemberFilter filter) {
		TableItem tableItem = new TableItem(_filterTable, 0);
		tableItem.setText(filter.getDescription());
		tableItem.setData(filter);
		tableItem.setChecked(filter.isEnabled());
		resetOptions();
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite typeComposite = new Composite(parent, 0);
		typeComposite.setLayout(new GridLayout(4, false));
		Composite visComposite = new Composite(parent, 0);
		visComposite.setLayout(new GridLayout(2, false));
		Composite nameComposite = new Composite(parent, 0);
		nameComposite.setLayout(new GridLayout(3, false));
		GridData gd2 = new GridData();
		gd2.horizontalAlignment = GridData.FILL;
		gd2.grabExcessHorizontalSpace = true;
		gd2.horizontalSpan = 1;
		nameComposite.setLayoutData(gd2);
		
		GridData gd3 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		_filterTable = new Table(parent, SWT.CHECK);
		_filterTable.setLayout(new GridLayout(1, false));
		gd3.horizontalSpan = 1;
		_filterTable.setLayoutData(gd3);
		_filterTable.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				_filterTable.remove(_filterTable.getSelectionIndex());
			}

			/**
			 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
			 */
			public void mouseDown(MouseEvent e) {}

			/**
			 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
			 */
			public void mouseUp(MouseEvent e) {}
		});
		
		Composite buttonComposite = new OKCancelComposite(this, parent, true);
		buttonComposite.setLayout(new GridLayout(3, true));
		
		Label filterText = new Label(typeComposite, 0);
		filterText.setText("Filter all");
		_typeCombo = new Combo(typeComposite, SWT.READ_ONLY);
		_behaveCombo = new Combo(typeComposite, SWT.READ_ONLY);

		Label visText = new Label(visComposite, 0);
		visText.setText("visibility:");
		_visHolder = new VisibilityComposite(visComposite, 0, true); 
		
		Label nameText = new Label(nameComposite, 0);
		nameText.setText("name pattern:");
		_patternText = new Text(nameComposite, SWT.FILL);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 1;
		_patternText.setLayoutData(gd);
		
		Button addFilterBut = new Button(nameComposite, 0);
		addFilterBut.setText("Add Filter");
		addFilterBut.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				addFilter();
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		// set up the dialog box options
		resetOptions();
		
		for (MemberFilter filter : PlugIn.getMemberFilters()) {
			addFilter(filter);
		}
		
		// prepare the view
		parent.pack();
		return parent;
	}
	
	/**
	 * Resets the controls in this dialog to their defaults.
	 */
	private void resetOptions() {
		_behaveCombo.add("that meet the following conditions");
		_behaveCombo.add("that do not meet the following conditions");
		_behaveCombo.select(0);
		
		_typeCombo.add("fields");
		_typeCombo.add("members");
		_typeCombo.add("methods");
		_typeCombo.add("types");
		_typeCombo.select(1);
		
		_patternText.setText("*");
		
		_visHolder.reset();
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	public void okPressed() {
		applyPressed();
		super.okPressed();
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	public void cancelPressed() {
		super.cancelPressed();
	}
	
	/**
	 * @see edu.buffalo.cse.green.dialogs.OKCancelListener#applyPressed()
	 */
	public void applyPressed() {
		StringBuffer buffer = new StringBuffer();
		
		if (_filterTable.getItems().length > 0) {
			for (TableItem item : _filterTable.getItems()) {
				MemberFilter filter = (MemberFilter) item.getData();
				
				if (item.getChecked() != filter.isEnabled()) {
					int val = filter.getEnabledValue();
					
					if (item.getChecked()) {
						filter.setEnabled(val + 2);
					} else {
						filter.setEnabled(val - 2);
					}
				}
				
				buffer.append("|");
				buffer.append(filter);
			}
		} else {
			buffer.append("|");
		}
		
		PlugIn.getDefault().getPreferenceStore().putValue(P_FILTERS_MEMBER,
				buffer.toString().substring(1));
		
		// refresh the editor
		for (DiagramEditor editor : DiagramEditor.getEditors()) {
			editor.refresh();
		}
	}
}

/**
 * Holds common controls for selecting from the different visibilities.
 *
 * @author bcmartin
 */
class VisibilityComposite extends Composite {
	private MemberVisibility _value = MemberVisibility.PUBLIC;
	private Button _publicBut;
	private Button _defaultBut;
	private Button _privateBut;
	private Button _protectedBut;
	private List<IVisibilityChangedListener> _listeners;
	
	public VisibilityComposite(Composite parent, int style, boolean showAny) {
		super(parent, SWT.FILL);
		_listeners = new ArrayList<IVisibilityChangedListener>();
		
		setLayout(new GridLayout(showAny ? 8 : 7, false));
		
		if (showAny) {
			Button anyBut = new Button(this, SWT.RADIO);
			anyBut.setText("any");
			anyBut.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					_value = MemberVisibility.ANY;
					notifyListeners();
				}

				public void widgetDefaultSelected(SelectionEvent e) {}
			});
		}

		_publicBut = new Button(this, SWT.RADIO);
		_publicBut.setText("public");
		_publicBut.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				_value = MemberVisibility.PUBLIC;
				notifyListeners();
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		_defaultBut = new Button(this, SWT.RADIO);
		_defaultBut.setText("default");
		_defaultBut.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				_value = MemberVisibility.DEFAULT;
				notifyListeners();
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		_privateBut = new Button(this, SWT.RADIO);
		_privateBut.setText("private");
		_privateBut.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				_value = MemberVisibility.PRIVATE;
				notifyListeners();
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		_protectedBut = new Button(this, SWT.RADIO);
		_protectedBut.setText("protected");
		_protectedBut.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				_value = MemberVisibility.PROTECTED;
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}
	
	/**
	 * @return The selected <code>MemberVisibility</code>.
	 */
	public MemberVisibility getValue() {
		return _value;
	}
	
	/**
	 * Adds the given listener.
	 *
	 * @param listener - The given <code>IVisibilityChangedListener</code>.
	 */
	public void addListener(IVisibilityChangedListener listener) {
		_listeners.add(listener);
	}
	
	/**
	 * Removes the given listener.
	 *
	 * @param listener - The given <code>IVisibilityChangedListener</code>.
	 */
	public void removeListener(IVisibilityChangedListener listener) {
		_listeners.remove(listener);
	}
	
	/**
	 * Notifies all listeners that the selected visibility has changed.
	 */
	public void notifyListeners() {
		for (IVisibilityChangedListener listener : _listeners) {
			listener.visibilityChanged(_value);
		}
	}
	
	/**
	 * Sets the values of the controls to their defaults.
	 */
	public void reset() {
		_defaultBut.setSelection(false);
		_privateBut.setSelection(false);
		_protectedBut.setSelection(false);
		_publicBut.setSelection(true);
	}

	/**
	 * Sets whether the default button is enabled or not.
	 * 
	 * @param enable - The button is enabled if and only if this is true.
	 */
	public void setDefaultEnabled(boolean enable) {
		_defaultBut.setEnabled(enable);
	}

	/**
	 * Sets whether the private button is enabled or not.
	 * 
	 * @param enable - The button is enabled if and only if this is true.
	 */
	public void setPrivateEnabled(boolean enable) {
		_privateBut.setEnabled(enable);
	}

	/**
	 * Sets whether the protected button is enabled or not.
	 * 
	 * @param enable - The button is enabled if and only if this is true.
	 */
	public void setProtectedEnabled(boolean enable) {
		_protectedBut.setEnabled(enable);
	}

	/**
	 * Sets whether the public button is enabled or not.
	 * 
	 * @param enable - The button is enabled if and only if this is true.
	 */
	public void setPublicEnabled(boolean enable) {
		_publicBut.setEnabled(enable);
	}

	/**
	 * Sets whether the default button is selected or not.
	 * 
	 * @param select - The button is selected if and only if this is true.
	 */
	public void setDefaultSelected(boolean select) {
		_defaultBut.setSelection(select);
		_value = MemberVisibility.DEFAULT;
	}

	/**
	 * Sets whether the private button is selected or not.
	 * 
	 * @param select - The button is selected if and only if this is true.
	 */
	public void setPrivateSelected(boolean select) {
		_privateBut.setSelection(select);
		_value = MemberVisibility.PRIVATE;
	}

	/**
	 * Sets whether the protected button is selected or not.
	 * 
	 * @param select - The button is selected if and only if this is true.
	 */
	public void setProtectedSelected(boolean select) {
		_protectedBut.setSelection(select);
		_value = MemberVisibility.PROTECTED;
	}

	/**
	 * Sets whether the public button is selected or not.
	 * 
	 * @param select - The button is selected if and only if this is true.
	 */
	public void setPublicSelected(boolean select) {
		_publicBut.setSelection(select);
		_value = MemberVisibility.PUBLIC;
	}
}


/**
 * Provides a way to hook into visibility selections.
 * 
 * @author Blake
 */
interface IVisibilityChangedListener {
	void visibilityChanged(MemberVisibility value);
}