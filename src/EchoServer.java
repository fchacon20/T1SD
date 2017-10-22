import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EchoServer extends Thread{

    public static void createDistrict(List<District> districts){
        Scanner reader = new Scanner(System.in);
        System.out.println("AGREGAR DISTRITO");
        System.out.println("[Servidor Central] Nombre Distrito");
        String name = reader.next();
        System.out.println("[Servidor Central] IP Multicast");
        String IPM = reader.next();
        System.out.println("[Servidor Central] Puerto Multicast");
        int PM = reader.nextInt();
        System.out.println("[Servidor Central] IP Peticiones");
        String IPP = reader.next();
        System.out.println("[Servidor Central] Puerto Peticiones");
        int PP = reader.nextInt();

        districts.add(new District(name, IPM, PM, IPP, PP));

    }

    public static void main(String[] args) throws Exception{

        List<District> districts = new ArrayList<District>();
        int portNumber = 4000;

        createDistrict(districts);

        try (
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ){
            String ipSource = clientSocket.getInetAddress().toString();
            String districtName = in.readLine();
            System.out.println("[Servidor Central] Dar autorización a " + ipSource + " por Distrito " + districtName);
            System.out.println("1.- Si");
            System.out.println("2.- No");
            Scanner reader = new Scanner(System.in);
            int answer = reader.nextInt();
            System.out.println("[Servidor Central] Respuesta a " + ipSource + " por " + districtName);

            if (answer == 1) {
                System.out.println("si");
                for (District district: districts) {
                    if (district.getName().equals(districtName)){
                        String msg = "Nombre: " + district.getName() + ", IP Multicast: "
                                + district.getIPmulticast() + ", Puerto Multicast: " + district.getPortM() + ", "
                                + "IP Peticiones: " + district.getIPPeticiones() +
                                ", Puerto Peticiones: " + district.getPortP();
                        System.out.println(msg);
                        out.println(msg);
                    }
                }
            }
            else
                System.out.println("no");

        } catch (IOException e){
            System.out.println("Exception caught when trying to listen on port " + portNumber
                + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
