package edu.buffalo.cse.green.editor.action;

import org.eclipse.jdt.core.JavaModelException;

public class QuickFixAction extends ContextAction {
	private QuickFix _fix;

	public QuickFixAction(QuickFix fix) {
		super(null);
		_fix = fix;
		setText(getLabel());
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getLabel()
	 */
	public String getLabel() {
		if (_fix == null) return "";
		
		return trimNonAlphaNumeric(_fix.getResolution().getLabel());
	}

	private String trimNonAlphaNumeric(String input) {
		char[] chars = input.toCharArray();
		StringBuffer buf = new StringBuffer();
		
		for (Character ch : chars) {
			if (Character.isLetterOrDigit(ch) || ch == ' ') {
				buf.append(ch);
			}
		}
		
		return new String(buf.toString());
	}
	
	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#doRun()
	 */
	protected void doRun() throws JavaModelException {
		_fix.getResolution().run(_fix.getMarker());
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getSupportedModels()
	 */
	protected int getSupportedModels() {
		return CM_MEMBER;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @see edu.buffalo.cse.green.editor.action.ContextAction#getPath()
	 */
	public Submenu getPath() {
		return Submenu.None;
	}
}
