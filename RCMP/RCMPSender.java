import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.PortUnreachableException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;
import java.nio.ByteBuffer;

/**
 * This class implements a sender that sends a file over UDP using the RCMP
 * protocol as defined by Professor Norman
 * 
 * Written for CS-332, Calvin University, Dec 05 2019
 *
 * @author: Quentin Barnes
 * @author: Ty Vredeveld
 */
public class RCMPSender {

    // constants to use for sizes
    public static final int PACKETSIZE = 1450;
    public static final int HEADERSIZE = 13;
    public static final int ACKSIZE = 8;

    public static void main(String[] args) {

        // make sure the user specifies the correct
        // number of command line arguments
        if (args.length < 3) {
            System.err.println("Usage: java RCMPSender <hostName> <portNum> <fileName>");
            System.exit(0);
        }

        String hostName = args[0];

        int portNum = 22222;

        // make sure the port number specified is an integer
        try {
            portNum = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Cannot convert " + args[1] + " to int to use for port number");
            System.exit(0);
        }

        // create a File object, determine its total size (in bytes), and open a stream
        // for reading
        String fileName = args[2];
        File openFile = new File(fileName);
        int fileSize = (int) openFile.length();
        RandomAccessFile fin = null; // use a RandomAccessFile to be able to seek back in the file
        DatagramSocket socket = null;

        // try to open the specified file for reading
        // and create a UDP socket for sending packets
        try {
            fin = new RandomAccessFile(openFile, "rw");
            socket = new DatagramSocket();
            // set the socket to throw an exception when it doesn't
            // receive a packet when it expects to within 150ms
            socket.setSoTimeout(200);
        } catch (SocketException e) {
            System.err.println("Error creating socket with port number " + portNum + ": " + e);
            System.exit(0);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e);
            System.exit(0);
        }

        // set up variables used for packet creation and sending
        byte[] buffer = null, ackBuffer = null;
        DatagramPacket packetToSend = null, ackToReceive = null;
        ByteBuffer byteBuffer = null, ackByteBuffer = null;
        Random random = new Random();
        int eof = 1, connectionID = random.nextInt(), packetNum = 0, receivedID = -1, receivedPacketNum = -1,
                gapCounter = 0, nonAckedPackets = 0, lastAckedPacket = -1, filePosition = -1, timeoutCount = 0;
        byte packetShouldBeAcked = (byte) 1;
        boolean looping = true;

        // loop until we send a packet smaller than the defined PACKETSIZE
        while (looping) {

            try {

                // set up the buffer used for datagram sending and fill its header
                buffer = new byte[PACKETSIZE + HEADERSIZE];
                byteBuffer = ByteBuffer.wrap(buffer);
                byteBuffer.putInt(connectionID);
                byteBuffer.putInt(fileSize);
                byteBuffer.putInt(packetNum);

                // set up buffer and bytebuffer used for receiving acks
                ackBuffer = new byte[ACKSIZE];
                ackByteBuffer = ByteBuffer.wrap(ackBuffer);

                // figure out how much data we read in
                eof = fin.read(buffer, HEADERSIZE, PACKETSIZE);

                // make sure the packet is acked when it's the last one
                if (eof < PACKETSIZE)
                    packetShouldBeAcked = (byte) 1;

                byteBuffer.put(packetShouldBeAcked);

            } catch (IOException e) {
                System.err.println("Error receiving data from file: " + e);
                System.exit(0);
            }

            try {
                // create the packet with the data and send it
                packetToSend = new DatagramPacket(buffer, eof + HEADERSIZE, InetAddress.getByName(hostName), portNum);
                socket.send(packetToSend);

                // try to receive an ACK packet from the receiver if we marked the packet to be
                // acked
                if (packetShouldBeAcked == (byte) 1) {
                    ackToReceive = new DatagramPacket(ackBuffer, ACKSIZE);
                    socket.receive(ackToReceive);
                    receivedID = ackByteBuffer.getInt();
                    receivedPacketNum = ackByteBuffer.getInt();

                    // when we ack a packet, increase the gap counter, reset the number of
                    // packets that have not been acked, and mark the next packets to not be acked
                    timeoutCount = 0;
                    gapCounter++;
                    nonAckedPackets = 0;
                    packetShouldBeAcked = (byte) 0;
                    lastAckedPacket = packetNum;

                    // make sure the ACK packet has the correct connection id
                    if (receivedID != connectionID) {
                        System.err.println("ACK not received");
                        System.exit(0);
                    } else {
                        System.out.println("Received ID: " + receivedID + ", received packetNum: " + receivedPacketNum);

                        // if we've sent a packet that isn't 'full', break out of loop
                        if (eof < PACKETSIZE) {
                            looping = false;
                        }
                    }

                    // if we don't receive an ACK packet, increment the counter of non-acked packet
                } else {
                    nonAckedPackets++;
                }

                // mark the next one to be acked only if we have reached the gap counter - 1
                if (nonAckedPackets == (gapCounter - 1))
                    packetShouldBeAcked = (byte) 1;

            } catch (PortUnreachableException e) {
                System.err.println("Error reaching port: " + e);
                System.exit(0);

                // if the socket has timed out, reset our gap counter info
                // and where in the file we are reading data from
            } catch (SocketTimeoutException e) {

                // count how many timeouts we have received for the same packet
                timeoutCount++;

                // if we've gotten 10 of them and we've sent the last packet
                // break and consider the success of the transfer to be unknown
                if (eof < PACKETSIZE && timeoutCount == 10) {
                    looping = false;
                    System.out.println("Successful transfer unknown");
                }
                gapCounter = 0;
                nonAckedPackets = 0;
                packetShouldBeAcked = (byte) 1;
                packetNum = lastAckedPacket;
                // determine the position in the file to go back to
                filePosition = (packetNum + 1) * PACKETSIZE;
                try {
                    // move the file position back in the file to the desired location
                    fin.seek(filePosition);
                } catch (IOException e2) {
                    System.err.println("Error seeking to position " + filePosition + ": " + e2);
                    System.exit(0);
                }
            } catch (IOException e) {
                System.err.println("Error receiving data from socket: " + e);
                System.exit(0);
            }

            // increment the packet number counter
            packetNum++;

        }
        // close the socket when we're done receiving the file
        socket.close();
    }
}