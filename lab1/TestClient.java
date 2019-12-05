import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This is a test class that connects to a server and creates a
 * light display, and a bit display.
 * 
 * @author: Quentin Barnes
 * @author: Ty Vredeveld
 */
public class TestClient {
    public static void main(String[] args) {
        LightDisplay d1 = new LightDisplay(new LightPanel("153.106.116.87", 9223));
        BitDisplay b1 = new BitDisplay(new BitHandler("153.106.116.87", 9223));
    }
}
