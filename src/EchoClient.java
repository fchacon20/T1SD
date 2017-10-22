import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class EchoClient {
    public static void main(String[] args) throws IOException {

//        if (args.length != 2) {
//            System.err.println("Usage: java EchoCLient <host name> <port number>");
//            System.exit(1);
//        }

        //String hostname = args[0];
        //int portNumber = Integer.parseInt(args[1]);

        //String hostname = "fchacon-HP-240-G5-Notebook-PC";

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
        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        out.println(district);

        try (
                //Socket echoSocket = new Socket(hostname, portNumber);
                //PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println("echo: " + in.readLine());
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
