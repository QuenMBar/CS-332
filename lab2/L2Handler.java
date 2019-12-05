/**
 * This class implements a layer 2 handler that is responsible for
 * passing along an L2Frame to be sent on layer 1 and receiving a
 * string of bits from layer 1 and creating an L2Frame from them
 *
 * @author: Quentin Barnes
 * @author: Ty Vredeveld
 */
public class L2Handler implements BitListener {
	private BitHandler handler;
	private Layer2Listener layer2listener;
	private int macAddr;

	public L2Handler(String host, int port, int addr) {
		handler = new BitHandler(host, port);
		handler.setListener(this);
		macAddr = addr;
	}

	public L2Handler(int addr) {
		this("localhost", LightSystem.DEFAULT_PORT, addr);
	}

	public int getMACAddress() {
		return macAddr;
	}

	public void setListener(Layer2Listener l) {
		layer2listener = l;
	}

	/**
	 * When an L2Handler receives a string of bits, create a new frame from
	 * it, check if the frame's destination matches the handler's MAC
	 * address, and if it does pass it up to the layer 2 listener
	 *
	 * @param handler the BitHandler the L2Handler is receiving bits from
	 * @param bits the string of bits the L2Handler is receiving
	 */
	public void bitsReceived(BitHandler handler, String bits) {
		L2Frame newFrame = new L2Frame(bits);
		if (newFrame.getDestAddr() == macAddr) {
			if (layer2listener != null) {
				layer2listener.frameReceived(this, newFrame);
			}
		}
	}

	/**
	 * Conversion method that takes the MAC address and the number
	 * of bits to be used for the conversion (i.e. the number of bits
	 * assigned to the field determined by our protocol) and returns
	 * a string that is a binary representation of the MAC address
	 *
	 * @param bitLength the number of bits to be used when converting
	 */
	public String toString() {
		return "#" + Integer.toString(macAddr);
	}

	/**
	 * Sends out an L2Frame when the handler is silent
	 *
	 * @param frame the L2Frame desired to be sent out
	 */
	public void send(L2Frame frame) {
		while (true) {
			handler.pause(BitHandler.HALFPERIOD);
			if (handler.isSilent()) {
				try {
					handler.broadcast(frame.toString());
				} catch (CollisionException ex) {
					System.out.println(ex);
				}
			}
		}
	}
}