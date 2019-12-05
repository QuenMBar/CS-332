import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This is a test class that creates a server, light display,
 * and a bit display.
 * 
 * @author: Quentin Barnes
 * @author: Ty Vredeveld
 */

public class TestServer {
    public static void main(String[] args) {
        LightSystem system = new LightSystem();
        LightDisplay d1 = new LightDisplay(new LightPanel());
        BitDisplay b1 = new BitDisplay(new BitHandler());
    }
}

