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
public class BooleanVisitor<E> implements IAlgo<Object,E,Boolean> {
	public Boolean emptyCase(LRStruct<E> host, Object _) {
		return false;
	}
	public Boolean nonEmptyCase(LRStruct<E> host, Object _) {
		return true;
	}
}
