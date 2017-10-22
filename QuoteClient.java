import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class QuoteClient {
    public static void main(String[] args) throws IOException, InterruptedException {

        /*if (args.length != 1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            return;
        }*/

        String hostname = "fchacon-HP-240-G5-Notebook-PC";

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        // send request
        byte[] buf = new byte[256];
        InetAddress address = InetAddress.getByName(hostname);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4000);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Quote of the Moment: " + received);
        Thread.sleep(4000);

        socket.close();
    }
}
