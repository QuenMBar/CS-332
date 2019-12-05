/**
 * This interface defines what is necessary for a class to be
 * considered a BitListener - it must have a method to handle
 * received bits
 *
 * @author: Professor Norman
 */
public interface BitListener {
	/**
	 * Perform some action when the BitListener (or anything that
	 * implements this interface) receives bits
	 */
    void bitsReceived(BitHandler handler, String bits);
}