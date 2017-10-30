import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MulticastServer {

    static String hostname = "192.168.122.1"; //CAMBIAR
    static int portNumber = 4000;          //CAMBIAR

    public static void main(String[] args) throws IOException {

        List<Thread> districts = new ArrayList<>();
        Socket serverSocket = new Socket(hostname, portNumber);
        BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        String res = in.readLine();
        int i = 0;
        String[] attr;

        try {
            //Mientras aún se estén agregando distritos en el servidor central
            while(!res.equals("ready")){
                attr = res.split(",");

                //Corre el thread multicast del distrito agregado
                //Hace falta agregar el de peticiones
                districts.add(new Thread(new DistrictThread(attr[0], attr[1],
                        Integer.valueOf(attr[2]), attr[3], Integer.valueOf(attr[4]))));
                districts.get(i).start();
                i++;
                in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                res = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        serverSocket.close();

        //loop para que no termine el programa (?), puede que con join sea más elegante
        while (true) {}
    }
}

