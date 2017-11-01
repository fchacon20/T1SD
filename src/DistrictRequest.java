import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

public class DistrictRequest implements Runnable{

    private int port;
    private byte[] receiveData = new byte[256];
    private DatagramPacket packet;
    private String name;
    private List<Titan> titans;

    DistrictRequest(String name, int port, List<Titan> titans){
        this.name = name;
        this.port = port;
        this.titans = titans;
    }

    public void captureTitan(int idd){
        for (Titan titan: titans){
            if (titan.getId() == idd) {
                titan.setStatus("Captured");
                break;
            }
        }
    }

    public void killTitan(int idd){
        for (Titan titan: titans){
            if (titan.getId() == idd) {
                titan.setStatus("Killed");
                break;
            }
        }
    }

    @Override
    public void run() {
        DatagramSocket serverSocket = null;
        packet = new DatagramPacket(receiveData,receiveData.length);

        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                serverSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String sentence = new String(packet.getData(), packet.getOffset(), packet.getLength());
            String[] params = sentence.split(" ");
            if (params[0].equals("Capture")) {
                captureTitan(Integer.valueOf(params[1]));
            }else if(params[0].equals("Asesinado")){
                killTitan(Integer.valueOf(params[1]));
            }

        }
    }
}
