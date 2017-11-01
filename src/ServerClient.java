import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServerClient {

    static List<Titan> titans = new ArrayList<>();
    static List<Titan> capturedTitans = new ArrayList<>();
    static List<Titan> killedTitans = new ArrayList<>();
    static Thread t;

    //Menú con las opciones disponibles
    public static void help(String name) throws IOException {
        System.out.println("[Cliente] Estás en el Distrito " + name);
        System.out.println("[Cliente] Consola");
        System.out.println("[Cliente] (1) Listar Titanes");
        System.out.println("[Cliente] (2) Cambiar Distrito");
        System.out.println("[Cliente] (3) Capturar Titan");
        System.out.println("[Cliente] (4) Asesinar Titan");
        System.out.println("[Cliente] (5) Listar Titanes Capturados");
        System.out.println("[Cliente] (6) Listar Titanes Asesinados");
        System.out.println("[Cliente] (7) Salir del Distrito");
        System.out.println("[Cliente] (8) Ayuda");
    }

    //Función que le avisa al servidor central que el cliente se va del juego
    public static void exit(Socket exitSocket) throws IOException{
        PrintWriter exitOut = new PrintWriter(exitSocket.getOutputStream(),true);
        exitOut.println("ServerRequest");
        System.out.println("Se ha desconectado del juego.");
    }

    //Función que cambia de distrito a través del servidor central //NO FUNCIONA
    public static List<String> change(Socket exitSocket, String hostname, int portNumber) throws IOException, InterruptedException {
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
        List<String> ret = new ArrayList<>();
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

    public static String showTitans(List<Titan> titans, String name){
        StringBuilder ret = new StringBuilder();
        for (Titan titan: titans){
            if (titan.getDistrictName().equals(name)){
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
            ret.append("No hay titanes en este distrito");
        return ret.toString();
    }

    public static String showCapturedTitans(List<Titan> titans){
        StringBuilder ret = new StringBuilder();
        for (Titan titan: titans){
            ret.append("- ");
            ret.append(titan.getId());
            ret.append(", ");
            ret.append(titan.getName());
            ret.append(", ");
            ret.append(titan.getType());
            ret.append(", ");
            ret.append(titan.getDistrictName());
            ret.append(" ");
        }
        if (ret.length() == 0)
            ret.append("No hay titanes capturados");
        return ret.toString();
    }

    public static String showKilledTitans(List<Titan> titans){
        StringBuilder ret = new StringBuilder();
        for (Titan titan: titans){
            ret.append("- ");
            ret.append(titan.getId());
            ret.append(", ");
            ret.append(titan.getName());
            ret.append(", ");
            ret.append(titan.getType());
            ret.append(", ");
            ret.append(titan.getDistrictName());
            ret.append(" ");
        }
        if (ret.length() == 0)
            ret.append("No hay titanes asesinados");
        return ret.toString();
    }

    public static void captureTitan(int id){
        for (Titan titan: titans){
            if (titan.getId() == id){
                capturedTitans.add(titan);
            }
        }
    }

    public static void killedTitans(int id){
        for (Titan titan: titans){
            if (titan.getId() == id){
                killedTitans.add(titan);
            }
        }
    }

    public static Titan getTitan(int id){
        Titan auxTitan = null;
        for (Titan titan: titans){
            if (titan.getId() == id){
                auxTitan = titan;
            }
        }
        return auxTitan;
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
            int idd; //Capturar o asesinar
            Titan auxTitan;

            //attrL contiene los atributos del distrito en una lista
            System.out.println(attrL);

            //Se inicia un thread que escucha lo transmitido por el distrito
            t = new Thread(new Info(attrL, titans));
            t.start();

            //Mensaje con las opciones
            help(attrL.get(0));

            int option;
            while (true){
                option = reader.nextInt();
                switch (option){
                    case 1:
                        System.out.println(showTitans(titans, district));
                        break;
                    case 2:
                        attrL = change(exitSocket, hostname, portNumber);
                        t = new Thread(new Info(attrL, titans));
                        t.start();
                        break;
                    case 3:
                        System.out.println("[Cliente] ID del titán a capturar?");
                        idd = reader.nextInt();
                        auxTitan = getTitan(idd);
                        if (auxTitan.getType().equals("Excéntrico")) {
                            System.out.println("[Cliente] No puedes capturar un Titán tipo Excéntrico");
                        } else {
                            DatagramSocket clientSocket = new DatagramSocket();
                            InetAddress IPAddress = InetAddress.getByName(attrL.get(3));
                            byte[] sendData;
                            String sentence = "Capture " + idd;
                            sendData = sentence.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                                    IPAddress, Integer.valueOf(attrL.get(4)));
                            clientSocket.send(sendPacket);
                            captureTitan(idd);
                        }
                        break;
                    case 4:
                        System.out.println("[Cliente] ID del titán a asesinar?");
                        idd = reader.nextInt();
                        auxTitan = getTitan(idd);
                        if (auxTitan.getType().equals("Cambiante")) {
                            System.out.println("[Cliente] No puedes asesinar un Titán tipo Cambiante");
                        } else {
                            DatagramSocket clientSocket = new DatagramSocket();
                            InetAddress IPAddress = InetAddress.getByName(attrL.get(3));
                            byte[] sendData;
                            String sentence = "Asesinado " + idd;
                            sendData = sentence.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                                    IPAddress, Integer.valueOf(attrL.get(4)));
                            clientSocket.send(sendPacket);
                            killedTitans(idd);
                        }
                        break;
                    case 5:
                        System.out.println(showCapturedTitans(capturedTitans));
                        break;
                    case 6:
                        System.out.println(showKilledTitans(killedTitans));
                        break;
                    case 7:
                        exit(exitSocket);
                        t.join();
                        System.exit(1);
                        break;
                    case 8:
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
