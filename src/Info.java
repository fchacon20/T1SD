import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.List;

public class Info implements Runnable {

    private List<String> attrL;
    private boolean running = true;

    Info(List<String> attrL){
        this.attrL = attrL;
    }

    //Finaliza el thread
    public void terminate(){
        this.running = false;
    }

    @Override
    public void run(){
        MulticastSocket socket = null;
        InetAddress address = null;

        //Crea un socket Multicast
        try {
            socket = new MulticastSocket(Integer.valueOf(attrL.get(2)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            address = InetAddress.getByName(attrL.get(1));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //Se une al grupo Multicast
        try {
            socket.joinGroup(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DatagramPacket packet;
        while (running) {

            if(Thread.currentThread().isInterrupted()) {
                try {
                    socket.leaveGroup(address);
                    terminate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            byte[] buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Imprime lo recibido por el grupo Multicast
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Quote of the Moment: " + received);
        }
    }
}
