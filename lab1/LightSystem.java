import java.io.*;
import java.net.*;
import java.util.*;

public class LightSystem extends Thread {
	public static final int DEFAULT_PORT = 9223;
	public static final String HIGH = "H";
	public static final String LOW = "L";

	private static Random random = new Random();

	public static Random getRandom() {
		return random;
	}

	private Set clients = new HashSet();
	private boolean isHigh = false;
	private int port;

	public LightSystem() {
		this(DEFAULT_PORT);
	}

	public LightSystem(int port) {
		this.port = port;
		start();
	}

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

	public void switchOn() {
		if (!isHigh) {
			isHigh = true;
			notifyClients();
		}
	}

	public void switchOff() {
		if (isHigh) {
			isHigh = false;
			notifyClients();
		}
	}

	private void notifyClients() {
		Iterator it = clients.iterator();
		while (it.hasNext()) {
			PrintWriter clientOut = (PrintWriter) it.next();
			notifyClient(clientOut);
		}
	}

	private void notifyClient(PrintWriter clientOut) {
		if (isHigh)
			clientOut.println(HIGH);
		else
			clientOut.println(LOW);
	}
}