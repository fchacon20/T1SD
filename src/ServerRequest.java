import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//Clase que es utilizada para manejar la salida del cliente del juego
//Sirve para actualizar la lista de clientes
//También maneja el cambio de distrito
public class ServerRequest implements Runnable {

    private int portNumber;
    private List<Client> clients;
    private List<District> districts;

    ServerRequest(int portNumber, List<Client> clients){
        this.clients = clients;
        this.portNumber = portNumber + 1;
        this.districts = new ArrayList<>();
    }

    public void addDistrict(District district) {
        this.districts.add(district);
    }

    public boolean clientExist(String ipSource, int port){
        for(Client client: clients) {
            if (client.getMyIP().equals(ipSource) && client.getPort() == port) {
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

    public void removeClient(String ipSource, int port){
        for (Client client: clients){
            if (client.getMyIP().equals(ipSource) && client.getPort()==port){
                clients.remove(client);
                break;
            }
        }
    }

    @Override
    public void run() {
        Socket clientSocket;
        BufferedReader in;
        ServerSocket serverSocket = null;
        PrintWriter out;
        String ipSource;
        String exit;

        //Escucha por puerto 4501
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                //Espera a recibir algún paquete de algún cliente
                assert serverSocket != null;
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ipSource = clientSocket.getInetAddress().toString();
                exit = in.readLine();

                //Acciones según el paquete recibido
                if (exit.equals("ServerRequest")) {
                    this.removeClient(ipSource, clientSocket.getPort());
                    break;
                } else if (exit.equals("Change")) {
                    out.println(showDistricts());
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
