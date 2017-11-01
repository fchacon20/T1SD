import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server extends Thread{

    private static List<Client> clients = new ArrayList<>();

    //Mostrar clientes
    private static void showClients(){
        for (Client client: clients)
            System.out.println("> " + client.getMyIP() + " con puerto " + client.getPort() +
                    " en distrito " + client.getDistrict());
    }

    //Función que crea distritos
    private static District createDistrict(){
        District ret;
        Scanner reader = new Scanner(System.in);
        System.out.println("AGREGAR DISTRITO");
        System.out.println("[Servidor Central] Nombre Distrito");
        String name = reader.next();
        System.out.println("[Servidor Central] IP Multicast");
        String IPM = reader.next();
        System.out.println("[Servidor Central] Puerto Multicast (5000-5500)");
        int PM = reader.nextInt();
        System.out.println("[Servidor Central] IP Peticiones (10.10.2.130)");
        String IPP = reader.next();
        System.out.println("[Servidor Central] Puerto Peticiones (5000-5500)");
        int PP = reader.nextInt();
        ret = new District(name, IPM, PM, IPP, PP);
        return ret;
    }

    public static void main(String[] args) throws Exception{

        int portNumber = 4000;

        //Creación y sincronización de distritos con Servidor de Distritos
        ServerRequest serverRequest = new ServerRequest(portNumber, clients);
        int nDistricts = 0;
        System.out.println("[Servidor Central] Esperando a que se conecte el Servidor de Distrito");
        ServerSocket serverDistrictSocket = new ServerSocket(portNumber);
        Socket districtSocket = serverDistrictSocket.accept();
        PrintWriter districtOut = new PrintWriter(districtSocket.getOutputStream(), true);

        do {
            Scanner ans = new Scanner(System.in);
            System.out.println("[Servidor Central] Desea crear más distritos? (si o no)");
            if (ans.next().equals("si")) {
                serverRequest.addDistrict(createDistrict());
                StringBuilder districtData =  new StringBuilder();
                District district = serverRequest.getDistricts().get(nDistricts);
                districtData.append(district.getName());         districtData.append(",");
                districtData.append(district.getIPmulticast());  districtData.append(",");
                districtData.append(district.getPortM());        districtData.append(",");
                districtData.append(district.getIPPeticiones()); districtData.append(",");
                districtData.append(district.getPortP());
                districtOut.println(districtData);
                nDistricts++;
            } else {
                //Cierre de la conexión con el Servidor de Distrito
                districtOut.println("ready");
                serverDistrictSocket.close();
                districtSocket.close();
                break;
            }
        }while (true);

        Thread t = new Thread(serverRequest);
        t.start();

        ServerSocket serverSocket = new ServerSocket(portNumber);
        Socket clientSocket;
        PrintWriter out;
        BufferedReader in;
        int autorization;
        Scanner reader = new Scanner(System.in);

        while (true) {
            try {
                //Conexión con cliente entrante
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String ipSource = clientSocket.getInetAddress().toString();
                String districtName = in.readLine();

                if (districtName == null)
                    continue;

                System.out.println("[Servidor Central] Dar autorización a " + ipSource + " por Distrito " + districtName);
                System.out.println("1.- Si\n2.- No");

                autorization = reader.nextInt();
                System.out.println("[Servidor Central] Respuesta a " + ipSource +
                        " por " + districtName);

                if (autorization == 1) {
                    for (District district : serverRequest.getDistricts()) {
                        if (district.getName().equals(districtName)) {
                            String msg = "Nombre: " + district.getName() + ", IP Multicast: "
                                    + district.getIPmulticast() + ", Puerto Multicast: " + district.getPortM() + ", "
                                    + "IP Peticiones: " + district.getIPPeticiones() +
                                    ", Puerto Peticiones: " + district.getPortP();
                            System.out.print("[Servidor Central] ");
                            System.out.println(msg);
                            out.println(msg);
                        }
                    }

                    //Nuevo cliente o cambio de distrito
                    if (!serverRequest.clientExist(ipSource, clientSocket.getPort()))
                        clients.add(new Client(ipSource, districtName, clientSocket.getPort()));
                    else
                        serverRequest.setDistrictToClient(ipSource, districtName);

                } else {
                    out.println("no");
                }

                showClients();

            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port " + portNumber
                        + " or listening for a connection");
                System.out.println(e.getMessage());
            }
        }
    }
}
