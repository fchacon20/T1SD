import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EchoClient {

    //Menú con las opciones disponibles
    public static void help(String name) throws IOException {
        System.out.println("[Cliente] Estás en el Distrito " + name);
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

    //Función que le avisa al servidor central que el cliente se va del juego
    public static void exit(Socket exitSocket) throws IOException{
        PrintWriter exitOut = new PrintWriter(exitSocket.getOutputStream(),true);
        exitOut.println("Exit");
        System.out.println("Se ha desconectado del juego.");
    }

    //Función que cambia de distrito a través del servidor central //NO FUNCIONA
    public static List<String> change(Socket exitSocket, String hostname, int portNumber) throws IOException {
        Scanner reader = new Scanner(System.in);
        Socket echoSocket = new Socket(hostname, portNumber);
        PrintWriter exitOut = new PrintWriter(exitSocket.getOutputStream(),true);
        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);

        //Avisa que habrá un cambio de distrito
        exitOut.println("Change");
        BufferedReader exitIn = new BufferedReader(new InputStreamReader(exitSocket.getInputStream()));

        //Imprime los nombres de los distritos
        System.out.println(exitIn.readLine());

        System.out.println("[Cliente] Nombre del Distrito");
        String name = reader.next();

        //Solicita conexión al nuevo distrito
        out.println(name);

        BufferedReader serverIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

        //Recibe y maneja los parámetros del nuevo distrito
        String[] attr = serverIn.readLine().split(",");
        System.out.println("[Cliente] Ha entrado al Distrito " + name);
        return atributos(attr);
    }

    //Filtra la información importante del mensaje que entrega el servidor central
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

    public static void main(String[] args) throws IOException, InterruptedException {

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
        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        BufferedReader in;

        //Entrega al servidor central el distrito al que quiere conectarse
        out.println(district);

        try {
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

            //Contiene la respuesta del servidor central
            String response = in.readLine();

            if (response.equals("no")){
                System.out.println("Ha sido denegado el acceso.");
                System.exit(1);
            }

            String[] attr = response.split(",");
            List<String> attrL = new ArrayList<>(atributos(attr));

            //attrL contiene los atributos del distrito en una lista
            System.out.println(attrL);

            //Se inicia un thread que escucha lo transmitido por el distrito
            Thread t = new Thread(new Info(attrL));
            t.start();

            //Mensaje con las opciones
            help(attrL.get(0));

            int option;
            while (true){
                option = reader.nextInt();
                switch (option){
                    case 1:
                        break;
                    case 2: //No funciona
                        t.interrupt();
                        t.join();
                        attrL = change(exitSocket, hostname, portNumber);
                        t = new Thread(new Info(attrL));
                        t.start();
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7: //Funciona
                        exit(exitSocket);
                        System.exit(1);
                        break;
                    case 8: //Funciona
                        help(attrL.get(0));
                        break;
                    default:
                        System.out.println("[Cliente] Opción errónea");
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
