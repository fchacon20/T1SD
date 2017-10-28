import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EchoClient {
    public static void help() throws IOException {
        System.out.println("[Cliente] Consola");
        System.out.println("[Cliente] (1) Listar Titanes");
        System.out.println("[Cliente] (2) Cambiar Distrito");
        System.out.println("[Cliente] (3) Capturar Titan");
        System.out.println("[Cliente] (4) Asesinar Titan");
        System.out.println("[Cliente] (5) Listar Titantes Capturados");
        System.out.println("[Cliente] (6) Listar Titanes Asesinados");
        System.out.println("[Cliente] (7) Salir del Distrito");
        System.out.println("[Cliente] (8) Ayuda");
    }

    public static List<String> atributos(String[] attr){
        List<String> ret = new ArrayList<String>();
        String name = attr[0].substring(8);
        String IPM = attr[1].substring(15);
        String PM = attr[2].substring(19);
        String IPP = attr[3].substring(16);
        String PP = attr[4].substring(20);
        ret.add(name);
        ret.add(IPM);
        ret.add(PM);
        ret.add(IPP);
        ret.add(PP);
        return ret;
    }

    public static void main(String[] args) throws IOException {

        System.out.println("Bienvenido Attack on Distribuidos!");
        System.out.println("[Cliente] Ingresar IP Servidor Central");

        Scanner reader = new Scanner(System.in);
        String hostname = reader.next();

        System.out.println("[Cliente] Ingresar Puerto Servidor Central");
        int portNumber = reader.nextInt();
        System.out.println("\n[Cliente] Introducir Nombre de Distrito a Investigar, Ej: Trost, Shiganshina");
        String district = reader.next();

        //String hostname = "192.168.122.1";
        //int portNumber = 4000;

        Socket echoSocket = new Socket(hostname, portNumber);
        Socket exitSocket = new Socket(hostname, portNumber+1);
        PrintWriter exitOut = new PrintWriter(exitSocket.getOutputStream(),true);
        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        out.println(district);

        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String response = in.readLine();

            if (response.equals("no")){
                System.out.println("Ha sido denegado el acceso.");
                System.exit(1);
            }

            String[] attr = response.split(",");
            List<String> attrL = new ArrayList<>(atributos(attr));

            //attrL contiene los atributos importantes del distrito en una lista
            System.out.println(attrL);

            Thread t = new Thread(new Info(attrL));
            t.start();

            help();

            int option;
            while (true){
                option = reader.nextInt();
                switch (option){
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        exitOut.println("Exit");
                        System.out.println("Se ha desconectado del juego.");
                        System.exit(1);
                        break;
                    case 8:
                        help();
                        break;
                    default:
                        break;
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostname);
            System.exit(1);
        }
    }
}
