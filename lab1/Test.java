import java.io.*;
import java.net.*;
import java.util.*;

public class Test {
    public static void main(String[] args) {
        LightSystem system = new LightSystem();
        LightDisplay d1 = new LightDisplay(new LightPanel());
        LightDisplay d2 = new LightDisplay(new LightPanel());
        BitDisplay b1 = new BitDisplay(new BitHandler());
        BitDisplay b2 = new BitDisplay(new BitHandler());

    }
}

