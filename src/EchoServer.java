import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class EchoServer extends Thread{
    public static void main(String[] args) throws Exception{

//        if (args.length != 1){
//            System.err.println("Usage: java EchoServer <port number>");
//            System.exit(1);
//        }
//
//        int portNumber = Integer.parseInt(args[0]);

        int portNumber = 4000;

        try (
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ){
            String ipSource = clientSocket.getInetAddress().toString();
            String district = in.readLine();
            System.out.println("[Servidor Central] Dar autorización a " + ipSource + " por Distrito " + district);
            System.out.println("1.- Si");
            System.out.println("2.- No");
            Scanner reader = new Scanner(System.in);
            int answer = reader.nextInt();
            System.out.println("[Servidor Central] Respuesta a " + ipSource + " por " + district);

            if (answer == 1)
                System.out.println("si");
            else
                System.out.println("no");

        } catch (IOException e){
            System.out.println("Exception caught when trying to listen on port " + portNumber
                + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
