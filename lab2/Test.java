public class Test {
	public static void main(String[] args) {
		LightSystem system = new LightSystem();
		// LightDisplay d1 = new LightDisplay(new LightPanel());
		// LightDisplay d2 = new LightDisplay(new LightPanel());

		// BitDisplay b = new BitDisplay(new BitHandler());
		// BitDisplay b2 = new BitDisplay(new BitHandler());

		// Step1Tests();
		// Step2Tests();
		Step3and4and5Tests();
	}

	// perform tests to guarantee the correctness of
	// step 1 of the assignment
	public static void Step1Tests() {
		try {
			L2Frame f1 = new L2Frame(12, 9, 3, 2, "100110");
			assert (f1.getDestAddr() == 12);
			assert (f1.getSrcAddr() == 9);
			assert (f1.getType() == 3);
			assert (f1.getVLANID() == 2);
			assert (f1.getPayload().equals("test"));
			assert (f1.getPayloadSize() == "test".length());
			assert (f1.toString().equals("0110010011110000001001100110"));
		} catch (Exception ex) {
			System.out.println(ex);
		}

		try {
			L2Frame f2 = new L2Frame(16, 9, 3, 2, "test");
			System.out.println("Cannot have a destination address larger than 15");
		} catch (Exception ex) {

		}

		try {
			L2Frame f3 = new L2Frame(-1, 9, 3, 2, "test");
			System.out.println("Cannot have a destination address smaller than 0");
		} catch (Exception ex) {

		}

		try {
			L2Frame f4 = new L2Frame(12, 15, 3, 2, "test");
			System.out.println("Cannot have a source address larger than 14");
		} catch (Exception ex) {

		}

		try {
			L2Frame f5 = new L2Frame(12, -1, 3, 2, "test");
			System.out.println("Cannot have a source address smaller than 0");
		} catch (Exception ex) {

		}

		try {
			L2Frame f6 = new L2Frame(12, 9, 4, 2, "test");
			System.out.println("Cannot have a type larger than 3");
		} catch (Exception ex) {

		}

		try {
			L2Frame f7 = new L2Frame(12, 9, -1, 2, "test");
			System.out.println("Cannot have a type smaller than 0");
		} catch (Exception ex) {

		}

		try {
			L2Frame f8 = new L2Frame(12, 9, 3, 4, "test");
			System.out.println("Cannot have a VLAN ID larger than 3");
		} catch (Exception ex) {

		}

		try {
			L2Frame f9 = new L2Frame(12, 9, 3, -1, "test");
			System.out.println("Cannot have a VLAN ID smaller than 0");
		} catch (Exception ex) {

		}

		System.out.println("All Step 1 Tests Passed!");
	}

	// perform tests to guarantee the correctness of
	// step 2 of the assignment
	public static void Step2Tests() {
		try {
			L2Handler h1 = new L2Handler("localhost", 9223, 1);
			assert (h1.getMACAddress() == 1);
			assert (h1.toString().equals("#1"));
		} catch (Exception ex) {
			System.out.println(ex);
		}

		try {
			L2Handler h1 = new L2Handler(14);
			assert (h1.getMACAddress() == 14);
			assert (h1.toString().equals("#14"));
		} catch (Exception ex) {
			System.out.println(ex);
		}

		System.out.println("All Step 2 Tests Passed!");
	}

	// perform tests to guarantee the correctness of
	// steps 3, 4, and 5 of the assignment
	public static void Step3and4and5Tests() {
		Layer2Display d1 = new Layer2Display(new L2Handler(1));
		Layer2Display d2 = new Layer2Display(new L2Handler(6));
		Layer2Display d3 = new Layer2Display(new L2Handler(4));
	}
}
