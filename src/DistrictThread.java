import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class DistrictThread implements Runnable{

    private String name;
    private List<Titan> titans;
    private String IPM;
    private int PM;
    private String IPP;
    private int PP;
    private long FIVE_SECONDS = 5000;

    DistrictThread(String name, String IPM, int PM, String IPP, int PP){
        this.name = name;
        this.titans = new ArrayList<>();
        this.IPM = IPM;
        this.PM = PM;
        this.IPP = IPP;
        this.PP = PP;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        InetAddress group = null;
        DatagramPacket packet;
        byte[] buf = new byte[256];

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //Mensaje a enviar
        String dString = name;
        buf = dString.getBytes();

        try {
            //Se obtiene el grupo multicast
            group = InetAddress.getByName(IPM);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        while(true) {

            //Crea el paquete dirigido al grupo multicast
            packet = new DatagramPacket(buf, buf.length, group, PM);

            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Espera un tiempo para volver a enviar el paquete
            try {
                sleep((long) (Math.random() * FIVE_SECONDS));
            } catch (InterruptedException e) {
            }
        }
    }
}
