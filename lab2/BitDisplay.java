import java.awt.event.*;
import javax.swing.*;

public class BitDisplay implements ActionListener, BitListener {
	private BitHandler handler;
	private JTextField receiveField;
	private JTextField sendField;

	public BitDisplay(BitHandler handler) {
		this.handler = handler;

		JFrame frame = new JFrame(handler.toString());
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));

		receiveField = new JTextField(20);
		receiveField.setEditable(false);
		frame.getContentPane().add(receiveField);

		frame.getContentPane().add(new JLabel("Type in 0s and 1s below: hit enter to send"));
		sendField = new JTextField(20);
		sendField.addActionListener(this);
		frame.getContentPane().add(sendField);

		frame.pack();
		frame.setVisible(true);
		handler.setListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		new Thread() {
			public void run() {
				try {
					handler.broadcast(sendField.getText());
				} catch (CollisionException e) {
					receiveField.setText("Collision!");
				}

				// System.out.println("actionPerformed: done sending " + sendField.getText());
			}
		}.start();
		sendField.selectAll();
	}

	public void bitsReceived(BitHandler h, String bits) {
		receiveField.setText(bits);
	}
}