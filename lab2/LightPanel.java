import java.io.*;
import java.net.*;
import java.util.*;

public class LightPanel extends Thread {
	private static Set idsUsed = new HashSet();

	private int id;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private boolean isHigh = false;

	public LightPanel() {
		this("localhost", LightSystem.DEFAULT_PORT);
	}

	public LightPanel(String host, int port) {
		do {
			id = LightSystem.getRandom().nextInt(15) + 1;
		} while (!idsUsed.add(new Integer(id)));

		try {
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			start();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Invalid host:  " + host);
		} catch (IOException e) {
			throw new RuntimeException("Unable to connect to LightSystem");
		}
	}

	public void switchOn() {
		out.println(LightSystem.HIGH);
	}

	public void switchOff() {
		out.println(LightSystem.LOW);
	}

	public void close() {
		try {
			out.close();
			in.close();
			socket.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void run() {
		try {
			String line = in.readLine();
			while (line != null) {
				if (line.equals(LightSystem.HIGH))
					isHigh = true;
				else if (line.equals(LightSystem.LOW))
					isHigh = false;
				line = in.readLine();
			}
		} catch (Exception e) {
			System.out.println("LightPanel disconnected");
			throw new RuntimeException(e);
		}
	}

	public boolean isOn() {
		return isHigh;
	}

	public String toString() {
		return "#" + id;
	}

	public int getID() {
		return id;
	}
}