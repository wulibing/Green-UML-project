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
public class LengthVisitor<E> implements IAlgo<Object,E,Integer> {

	/* (non-Javadoc)
	 * @see lrstruct.LRStruct.IAlgo#emptyCase(lrstruct.LRStruct, I)
	 */
	public Integer emptyCase(LRStruct<E> host, Object _) {
		return 0;  // isn't autoboxing cool :-)
	}

	/* (non-Javadoc)
	 * @see lrstruct.LRStruct.IAlgo#nonEmptyCase(lrstruct.LRStruct, I)
	 */
	public Integer nonEmptyCase(LRStruct<E> host, Object _) {
		return 1 + host.getRest().execute(this, _);
	}

}
