import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.List;

public class Info implements Runnable {

    private List<String> attrL;
    private boolean running = true;
    private List<Titan> titans;

    Info(List<String> attrL, List<Titan> titans){
        this.titans = titans;
        this.attrL = attrL;
    }

    public void removeTitan(int id){
        for (Titan titan: titans){
            if (titan.getId() == id){
                titans.remove(titan);
                break;
            }
        }
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
            assert socket != null;
            assert address != null;
            socket.joinGroup(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DatagramPacket packet;
        String[] update;
        String[] auxUpdate;
        int idd;
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
            System.out.println("[Cliente] " + received);

            if (received.substring(0,4).equals("List")){
                titans.clear();
                update = received.split("-");
                for (String up: update){
                    if (up.equals(update[0]))
                        continue;
                    auxUpdate = up.split(",");
                    auxUpdate[0] = auxUpdate[0].substring(1);
                    auxUpdate[1] = auxUpdate[1].substring(1);
                    auxUpdate[2] = auxUpdate[2].substring(1, auxUpdate[2].length()-1);
                    titans.add(new Titan(Integer.valueOf(auxUpdate[0]), attrL.get(0),
                            auxUpdate[1], auxUpdate[2]));
                }
            }else if(received.substring(0,5).equals("Alert")){
                update = received.split(",");
                titans.add(new Titan(Integer.valueOf(update[3].substring(4)), attrL.get(0),
                        update[1].substring(1), update[2].substring(6)));
            }else if(received.substring(0,7).equals("Captura")){
                idd = Integer.valueOf(received.split(": ")[1]);
                removeTitan(idd);
            }else if(received.substring(0,9).equals("Asesinato")){
                idd = Integer.valueOf(received.split(": ")[1]);
                removeTitan(idd);
            }
        }
    }
}
