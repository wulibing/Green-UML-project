package nonDefaultPackage;


public class Foo1 implements IFoo, IBar {

	private String _stringName;
	private ArrayIndexOutOfBoundsException _exp;
	private Foo2 _foo2;

	
	public Foo1() {
		_exp = new ArrayIndexOutOfBoundsException();
	}

}
