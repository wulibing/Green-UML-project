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
public class EvenFilterVisitor implements IAlgo<LRStruct<Integer>,Integer,LRStruct<Integer>> {

	/* (non-Javadoc)
	 * @see lrstruct.LRStruct.IAlgo#emptyCase(lrstruct.LRStruct, I)
	 */
	public LRStruct<Integer> emptyCase(LRStruct<Integer> host, LRStruct<Integer> newList) {
		return newList;
	}

	/* (non-Javadoc)
	 * @see lrstruct.LRStruct.IAlgo#nonEmptyCase(lrstruct.LRStruct, I)
	 */
	public LRStruct<Integer> nonEmptyCase(LRStruct<Integer> host, LRStruct<Integer> newList) {
		if (host.getDatum() % 2 == 0) {
			newList.insertFront(host.getDatum());
		}
		return host.getRest().execute(this, newList);
	}

}
