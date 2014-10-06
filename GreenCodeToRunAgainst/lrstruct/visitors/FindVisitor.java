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
public class FindVisitor<E extends Comparable> implements IAlgo<E,E,LRStruct<E>> {
	public LRStruct<E> emptyCase(LRStruct<E> host, E arg) {
		return host;
	}
	public LRStruct<E> nonEmptyCase(LRStruct<E> host, E arg) {
		if (arg.compareTo(host.getDatum()) == 0) {
			return host;
		}
		else {
			return host.getRest().execute(this, arg);
		}
	}
}
