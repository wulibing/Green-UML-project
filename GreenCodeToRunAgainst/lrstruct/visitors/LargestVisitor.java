/**
 * 
 */
package lrstruct.visitors;

import lrstruct.LRStruct;
import lrstruct.LRStruct.IAlgo;

/**
 * @author alphonce
 *
 */
public class LargestVisitor implements IAlgo<Object,Integer,Integer> {
	public Integer emptyCase(LRStruct<Integer> host, Object _) {
		// empty list does not have a largest element
		throw new IllegalStateException("Empty list does not have a largest element");
	}

	public Integer nonEmptyCase(LRStruct<Integer> host, Object _) {
		return host.getRest().execute(new IAlgo<Integer,Integer,Integer>() {

			public Integer emptyCase(LRStruct<Integer> host, Integer largest) {
				return largest;
			}

			public Integer nonEmptyCase(LRStruct<Integer> host, Integer largest) {
				int newLargest = (largest > host.getDatum() ? largest : host.getDatum());
				return host.getRest().execute(this, newLargest);
			}
			
		},host.getDatum());
	}
}
