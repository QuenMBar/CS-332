import java.io.*;
import java.net.*;
import java.util.*;

public class Test2 {
    public static void main(String[] args) {
        LightPanel lp1 = new LightPanel("153.106.116.87", 9223);
        LightDisplay d1 = new LightDisplay(lp1);
    }
}
