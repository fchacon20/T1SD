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

    Exit(int portNumber){
        this.portNumber = portNumber + 1;
        this.clients = new ArrayList<Client>();
    }

    public void addClient(Client client) {
        this.clients.add(client);
    }

    public void showClients(){
        for (Client client: clients)
            System.out.println(client.getMyIP());
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
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
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
        }
    }
}
