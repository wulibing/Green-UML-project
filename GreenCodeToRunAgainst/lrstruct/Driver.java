/**
 * 
 */
package lrstruct;

import lrstruct.visitors.LargestVisitor;
import lrstruct.visitors.LengthVisitor;

/**
 * @author alphonce
 *
 */
public class Driver {

	public static void main(String [] args) {
		LRStruct<String> stringList;
		stringList = new LRStruct<String>();
		System.out.println("The length of "+stringList+ " is "+ stringList.execute(new LengthVisitor<String>(), null));
		stringList.insertFront("George").insertFront("Judy").insertFront("Jane");
		stringList.insertFront("Elroy");
		System.out.println("The length of "+stringList+ " is "+ stringList.execute(new LengthVisitor<String>(), null));
		LRStruct<Integer> intList = new LRStruct<Integer>();
		intList.insertFront(1).insertFront(8903242).insertFront(3).insertFront(4);
		intList.insertFront(5).insertFront(6).insertFront(8);
		System.out.println("The largest of "+intList+ " is "+intList.execute(new LargestVisitor(), null));
	}
}
