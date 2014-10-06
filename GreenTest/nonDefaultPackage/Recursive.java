package nonDefaultPackage;

public class Recursive {
	private Recursive _r;
	
	public Recursive() {
		_r = new Recursive();
	}

}
