package nonDefaultPackage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Glorp {
	
	private Foo2 foo2;
	public void methodA() {
	}
	public void methodB() {
		new javax.swing.JButton().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
			}
			
		});
	}
	public void methodC() {
		new Foo2();
		new Foo2();
	}
	public void methodD() {
		
	}
	public void methodE() {
		
	}
	public Glorp() {}
}
