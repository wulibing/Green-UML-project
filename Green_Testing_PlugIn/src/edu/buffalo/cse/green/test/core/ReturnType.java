package edu.buffalo.cse.green.test.core;

public enum ReturnType {
	Byte(0), Short(1), Int(2), Long(3), Float(4), Double(5), Boolean(6),
	Char(7);
	
	private int _location;
	
	ReturnType(int location) {
		_location = location;
	}
	
	public int getLocation(boolean isField) {
		return isField ? _location : _location + 1;
	}
}
