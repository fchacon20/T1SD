import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class DistrictThread implements Runnable {

    private String name;
    private List<Titan> titans;
    private String IPM;
    private int PM;
    private String IPP;
    private int PP;
    private long TWENTY_SECONDS = 20000;

    DistrictThread(String name, List<Titan> titans, String IPM, int PM, String IPP, int PP) {
        this.name = name;
        this.titans = titans;
        this.IPM = IPM;
        this.PM = PM;
        this.IPP = IPP;
        this.PP = PP;
    }

    public String showTitans() {
        StringBuilder ret = new StringBuilder();
        for (Titan titan : titans) {
            if (titan.getDistrictName().equals(name) && titan.getStatus().equals("Alive")) {
                ret.append("- ");
                ret.append(titan.getId());
                ret.append(", ");
                ret.append(titan.getName());
                ret.append(", ");
                ret.append(titan.getType());
                ret.append(" ");
            }
        }
        if (ret.length() == 0)
            ret.append("No hay titanes");
        return ret.toString();
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        InetAddress group = null;
        DatagramPacket packet;
        byte[] buf = new byte[256];
        int lastID = 0;
        boolean alert = false;

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //Mensaje a enviar
        String dString;

        try {
            //Se obtiene el grupo multicast
            group = InetAddress.getByName(IPM);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        while (true) {

            //Si aparece un nuevo titán, alerta al grupo multicast
            dString = "Lista: " + showTitans();
            alert = false;

            if (titans.size() != 0) {
                if (titans.get(titans.size() - 1).getDistrictName().equals(name)) {
                    if (titans.get(titans.size() - 1).getId() != lastID) {
                        lastID = titans.get(titans.size() - 1).getId();
                        dString = "Alerta: Aparece un nuevo Titán!, " + titans.get(lastID - 1).getName()
                                + ", tipo " + titans.get(lastID - 1).getType() + ", ID " +
                                titans.get(lastID - 1).getId();
                        alert = true;
                    }
                }
            }

            if (!alert) {
                for (Titan titan: titans) {
                    if (titan.getStatus().equals("Captured")) {
                        dString = "Captura de Titán " + titan.getName() + " con id: " + titan.getId();
                        titan.setStatus("Revised");
                        break;
                    } else if (titan.getStatus().equals("Killed")) {
                        dString = "Asesinato de Titán " + titan.getName() + " con id: " + titan.getId();
                        titan.setStatus("Revised");
                        break;
                    }
                }
            }

            buf = dString.getBytes();
            //Crea el paquete dirigido al grupo multicast
            packet = new DatagramPacket(buf, buf.length, group, PM);

            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Espera un tiempo para volver a enviar el paquete
            try {
                sleep((long) (Math.random() * TWENTY_SECONDS));
            } catch (InterruptedException e) {
            }
        }
    }

}
