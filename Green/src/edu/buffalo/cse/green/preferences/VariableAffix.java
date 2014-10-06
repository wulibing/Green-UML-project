package edu.buffalo.cse.green.preferences;

import org.eclipse.jdt.core.JavaCore;

public enum VariableAffix {
	FieldPrefix("org.eclipse.jdt.core.codeComplete.fieldPrefixes"),
	FieldSuffix("org.eclipse.jdt.core.codeComplete.fieldSuffixes"),
	LocalPrefix("org.eclipse.jdt.core.codeComplete.localPrefixes"),
	LocalSuffix("org.eclipse.jdt.core.codeComplete.localSuffixes"),
	ParameterPrefix("org.eclipse.jdt.core.codeComplete.argumentPrefixes"),
	ParameterSuffix("org.eclipse.jdt.core.codeComplete.argumentSuffixes");
	
	private String _id;

	VariableAffix(String id) {
		_id = id;
	}
	
	public String getPreferenceId() {
		return _id;
	}
	
	public static String getAffixString(VariableAffix affix)
	{
		return JavaCore.getPlugin().getPluginPreferences().getString(
				affix.getPreferenceId().split(",")[0]);
	}
}