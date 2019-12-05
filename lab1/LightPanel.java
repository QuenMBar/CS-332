import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class implements a LightPanel that represents a light
 * with a switch that the user can flip on and off to turn on 
 * and off the light - also stores a unique ID for the specific
 * light it represents (note: switch flipping happens in LightDisplay,
 * not LightPanel)
 *
 * @author: Professor Norman
 */
public class LightPanel extends Thread {
	private static Set idsUsed = new HashSet();

	private int id;
	private Socket socket;
	private PrintWriter socketOut;
	private BufferedReader socketIn;
	private boolean isHigh = false;

	/**
	 * Default constructor that calls the explicit constructor with
	 * a default host and the default port of the LightSystem it is a part of
	 */
	public LightPanel() {
		this("localhost", LightSystem.DEFAULT_PORT);
	}

	/**
	 * Explicit constructor that stores the given host and port information,
	 * creates a connection to the server with that host/port combination,
	 * and starts the client
	 *
	 * @param host the host the LightPanel will connect to
	 * @param port the port the LightPanel will connect to
	 */
	public LightPanel(String host, int port) {
		do {
			id = LightSystem.getRandom().nextInt(15) + 1;
		} while (!idsUsed.add(new Integer(id)));

		try {
			socket = new Socket(host, port);
			socketOut = new PrintWriter(socket.getOutputStream(), true);
			socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			start();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Invalid host:  " + host);
		} catch (IOException e) {
			throw new RuntimeException("Unable to connect to LightSystem");
		}
	}

	/**
	 * Tell the LightSystem (which tells the other LightPanels)
	 * that it has turned on
	 */
	public void switchOn() {
		socketOut.println(LightSystem.HIGH);
	}

	/**
	 * Tell the LightSystem (which tells the other LightPanels)
	 * that it has turned off
	 */
	public void switchOff() {
		socketOut.println(LightSystem.LOW);
	}

	/**
	 * Close the socket connection to the LightSystem
	 */
	public void close() {
		try {
			socketOut.close();
			socketIn.close();
			socket.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Forever loop that listens to the LightSystem and
	 * updates the LightPanel state whenever it hears a message
	 * from the LightSystem that says to turn on or off; also throws
	 * an exception when it disconnects unexpectedly
	 */
	public void run() {
		try {
			String line = socketIn.readLine();
			while (line != null) {
				if (line.equals(LightSystem.HIGH))
					isHigh = true;
				else if (line.equals(LightSystem.LOW))
					isHigh = false;
				line = socketIn.readLine();
			}
		} catch (Exception e) {
			System.out.println("LightPanel disconnected");
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns whether or not the LightPanel is on
	 */
	public boolean isOn() {
		return isHigh;
	}

	/**
	 * Returns a string representation of the LightPanel
	 */
	public String toString() {
		return "#" + id;
	}

	/**
	 * Returns the ID of the LightPanel
	 */
	public int getID() {
		return id;
	}
}