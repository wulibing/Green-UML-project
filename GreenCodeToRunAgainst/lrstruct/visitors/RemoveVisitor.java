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
public class RemoveVisitor<E extends Comparable> implements IAlgo<Object,E,Object> {
	public Object emptyCase(LRStruct<E> host, Object _) {
		return null;
	}
	public Object nonEmptyCase(LRStruct<E> host, Object _) {
		return host.removeFront();
	}
}
