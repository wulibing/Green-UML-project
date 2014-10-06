package edu.buffalo.cse.green.editor.action;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;

public class QuickFix {
	private IMarker _marker;
	private IMarkerResolution _resolution;

	public QuickFix(IMarker marker, IMarkerResolution resolution) {
		_marker = marker;
		_resolution = resolution;
	}
	
	public IMarker getMarker() {
		return _marker;
	}
	
	public IMarkerResolution getResolution() {
		return _resolution;
	}
}
