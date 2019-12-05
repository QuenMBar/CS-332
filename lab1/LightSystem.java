import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This class implements a LightSystem that acts as a hub/home base for the
 * communication to take place through (connects LightPanels to each other)
 *
 * @author: Professor Norman
 */
public class LightSystem extends Thread {
	public static final int DEFAULT_PORT = 9223;
	public static final String HIGH = "H";
	public static final String LOW = "L";

	private static Random random = new Random();

	/**
	 * This returns a reference to a Random object that is used by the
	 * LightPanel class to generate a random integer for its ID
	 */
	public static Random getRandom() {
		return random;
	}

	private Set clients = new HashSet();
	private boolean isHigh = false;
	private int port;

	/**
	 * Default constructor that calls the explicit constructor with
	 * the DEFAULT_PORT specified above
	 */
	public LightSystem() {
		this(DEFAULT_PORT);
	}

	/**
	 * Explicit constructor that stores the given port and
	 * starts the program by calling the run() method
	 *
	 * @param port the port the LightSystem will run on across all
	               machines involved in the system
	 */
	public LightSystem(int port) {
		this.port = port;
		start();
	}

	/**
	 * Starts a server for the LightPanel clients to connect to,
	 * creates new threads for each connection, notifies the user when new
	 * LightPanels connect, and reads incoming information from said clients
	 */
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket clientSocket = serverSocket.accept();

				System.out.println(clientSocket + " connected");

				PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
				clients.add(clientOut);
				BufferedReader clientSocketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				LightSystemThread thread = new LightSystemThread(this, clientSocketIn);
				thread.start();
				notifyClient(clientOut);
			}
		} catch (BindException e) {
			throw new RuntimeException("LightSystem/other already running on port");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * If the system, is off, turn it on and notify the other clients;
	 * otherwise do nothing
	 */
	public void switchOn() {
		if (!isHigh) {
			isHigh = true;
			notifyClients();
		}
	}

	/**
	 * If the system is on, turn it off and notify the other clients;
	 * otherwise do nothing
	 */
	public void switchOff() {
		if (isHigh) {
			isHigh = false;
			notifyClients();
		}
	}

	/**
	 * Loop through all clients in the system, and notify them
	 * to turn on or off
	 */
	private void notifyClients() {
		Iterator it = clients.iterator();
		while (it.hasNext()) {
			PrintWriter clientOut = (PrintWriter) it.next();
			notifyClient(clientOut);
		}
	}

	/**
	 * Notify an individual client to turn on or off based on
	 * the status of the entire system (on/off)
	 *
	 * @param clientOut the client the LightSystem wants to notify
	 */
	private void notifyClient(PrintWriter clientOut) {
		if (isHigh)
			clientOut.println(HIGH);
		else
			clientOut.println(LOW);
	}
}