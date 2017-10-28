import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.List;

public class Info implements Runnable {

    private List<String> attrL;

    Info(List<String> attrL){
        this.attrL = attrL;
    }

    @Override
    public void run(){
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(Integer.valueOf(attrL.get(2)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        InetAddress address = null;
        try {
            address = InetAddress.getByName(attrL.get(1));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket.joinGroup(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DatagramPacket packet;
        while (true) {
            byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Quote of the Moment: " + received);
        }

    }
}
