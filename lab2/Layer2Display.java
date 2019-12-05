import java.awt.event.*;
import javax.swing.*;

/**
 * This class implements a layer 2 display that is responsible for
 * displaying received layer 2 frames and providing an interface for
 * users to create and send layer 2 frames
 *
 * @author: Quentin Barnes
 * @author: Ty Vredeveld
 */
public class Layer2Display implements ActionListener, Layer2Listener
{
    private L2Handler handler;
    private JTextField displayFieldFull;
    private JTextField displayFieldAddress;
    private JTextField displayFieldPayload;
    private JTextField addressField;
    private JTextField payloadField;

    public Layer2Display(L2Handler handler) {
		this.handler = handler;
		handler.setListener(this);

		JFrame frame = new JFrame(handler.toString());
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),
							       BoxLayout.PAGE_AXIS));

		frame.getContentPane().add(new JLabel("Full message:"));

		displayFieldFull = new JTextField(20);
		displayFieldFull.setEditable(false);
		frame.getContentPane().add(displayFieldFull);

		frame.getContentPane().add(new JLabel("Address Received From:"));

		displayFieldAddress = new JTextField(20);
		displayFieldAddress.setEditable(false);
		frame.getContentPane().add(displayFieldAddress);

		frame.getContentPane().add(new JLabel("Recived Payload:"));

		displayFieldPayload = new JTextField(20);
		displayFieldPayload.setEditable(false);
		frame.getContentPane().add(displayFieldPayload);

		frame.getContentPane().add(new JLabel("Destination Address:"));

		addressField = new JTextField(20);
		addressField.addActionListener(this);
		frame.getContentPane().add(addressField);

		frame.getContentPane().add(new JLabel("Payload:"));

		payloadField = new JTextField(20);
		payloadField.addActionListener(this);
		frame.getContentPane().add(payloadField);

		frame.pack();
		frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
		displayFieldFull.setText("Sending...");
		new Thread()
		{
		    public void run()
		    {
				handler.send(new L2Frame(Integer.parseInt(addressField.getText()), 
										handler.getMACAddress(),
										0,
										0,
										payloadField.getText()));
		    }
		}.start();
    }

	/**
	 * Handles a received frame and displays its contents
	 *
	 * @param h the L2Handler that passed the display the frame
	 * @param f the frame that is passed to the display
	 */
    public void frameReceived(L2Handler h, L2Frame f) {
    	displayFieldFull.setText(f.toString());
    	displayFieldAddress.setText(Integer.toString(f.getSrcAddr()));
    	displayFieldPayload.setText(f.getPayload());
    }

}