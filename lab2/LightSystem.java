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

	private Set clientOutputStreams = new HashSet();
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

				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				clientOutputStreams.add(out);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				LightSystemThread thread = new LightSystemThread(this, in);
				thread.start();
				notifyClient(out);
				// serverSocket.close();
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
		Iterator it = clientOutputStreams.iterator();
		while (it.hasNext()) {
			PrintWriter clientOutputStream = (PrintWriter) it.next();
			notifyClient(clientOutputStream);
		}
	}

	private void notifyClient(PrintWriter clientOutputStream) {
		if (isHigh) {
			clientOutputStream.println(HIGH);
		} else {
			clientOutputStream.println(LOW);
		}
	}
}
