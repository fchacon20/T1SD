import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Exit implements Runnable {

    private int portNumber;
    private List<Client> clients;
    private List<District> districts;

    Exit(int portNumber){
        this.portNumber = portNumber + 1;
        this.clients = new ArrayList<Client>();
        this.districts = new ArrayList<District>();
    }

    public void addDistrict(District district) {
        this.districts.add(district);
    }

    public boolean clientExist(String ipSource){
        for(Client client: clients){
            if(client.getMyIP().equals(ipSource)){
                return true;
            }
        }
        return false;
    }

    public void setDistrictToClient(String ipSource, String district){
        for(Client client: clients){
            if(client.getMyIP().equals(ipSource)){
                client.setDistrict(district);
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
            //System.out.print("- ");
            //System.out.println(district.getName());
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
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String ipSource = clientSocket.getInetAddress().toString();
        String exit = null;
        try {
            exit = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exit.equals("Exit")){
            this.removeClient(ipSource);
        }else if (exit.equals("Change")){
            out.println(this.showDistricts());
        }
    }
}
