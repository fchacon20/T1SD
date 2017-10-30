import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//Clase que es utilizada para manejar la salida del cliente del juego
//Sirve para actualizar la lista de clientes
//También maneja el cambio de distrito
public class Exit implements Runnable {

    private int portNumber;
    private List<Client> clients;
    private List<District> districts;

    Exit(int portNumber){
        this.portNumber = portNumber + 1;
        this.clients = new ArrayList<>();
        this.districts = new ArrayList<>();
    }

    public void addDistrict(District district) {
        this.districts.add(district);
    }

    public boolean clientExist(String ipSource){
        for(Client client: clients) {
            if (client.getMyIP().equals(ipSource)) {
                return true;
            }
        }
        return false;
    }

    public void setDistrictToClient(String ipSource, String district){
        for(Client client: clients){
            if(client.getMyIP().equals(ipSource)){
                client.setDistrict(district);
                break;
            }
        }
    }

    public void addClient(Client client) {
        this.clients.add(client);
    }

    public void showClients(){
        for (Client client: clients)
            System.out.println(client.getMyIP());
    }

    public List<District> getDistricts() {
        return districts;
    }

    public String showDistricts(){
        StringBuilder ret = new StringBuilder();
        for (District district: districts) {
            ret.append("- ");
            ret.append(district.getName());
            ret.append(" ");
        }
        return ret.toString();
    }

    public void removeClient(String ipSource){
        Iterator<Client> iter = this.clients.iterator();

        while (iter.hasNext()) {
            Client client = iter.next();
            if (client.getMyIP().equals(ipSource))
                iter.remove();
        }
    }

    @Override
    public void run() {
        Socket clientSocket;
        BufferedReader in;
        ServerSocket serverSocket = null;
        PrintWriter out = null;
        String ipSource = null;
        String exit = null;

        //Escucha por puerto 4001
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                //Espera a recibir algún paquete de algún cliente
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ipSource = clientSocket.getInetAddress().toString();
                exit = in.readLine();

                //Acciones según el paquete recibido
                if (exit.equals("Exit")) {
                    this.removeClient(ipSource);
                } else if (exit.equals("Change")) {
                    out.println(this.showDistricts());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
