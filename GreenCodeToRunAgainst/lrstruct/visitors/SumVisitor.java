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
public class SumVisitor implements IAlgo<Object,Integer,Integer> {
	/* (non-Javadoc)
	 * @see lrstruct.LRStruct.IAlgo#emptyCase(lrstruct.LRStruct, I)
	 */
	public Integer emptyCase(LRStruct<Integer> host, Object _) {
		return 0;  // isn't autoboxing cool :-)
	}

	/* (non-Javadoc)
	 * @see lrstruct.LRStruct.IAlgo#nonEmptyCase(lrstruct.LRStruct, I)
	 */
	public Integer nonEmptyCase(LRStruct<Integer> host, Object _) {
		return host.getDatum() + host.getRest().execute(this, _);
	}
}
