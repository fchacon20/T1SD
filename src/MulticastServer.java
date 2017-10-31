import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MulticastServer {

    static List<Titan> titans = new ArrayList<>();
    static String hostname = "192.168.122.1"; //CAMBIAR
    static int portNumber = 4000;          //CAMBIAR

    public static String showDistricts(List<District> districts){
        StringBuilder ret = new StringBuilder();
        for (District district: districts) {
            ret.append("- ");
            ret.append(district.getName());
            ret.append(" ");
        }
        return ret.toString();
    }

    public static void main(String[] args) throws IOException {

        List<Thread> districts = new ArrayList<>();
        List<Thread> requests = new ArrayList<>();
        List<District> districts1 = new ArrayList<>();
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
                requests.add(new Thread(new DistrictRequest(attr[0], Integer.valueOf(attr[4]) , titans)));
                districts.add(new Thread(new DistrictThread(attr[0], titans, attr[1],
                        Integer.valueOf(attr[2]), attr[3], Integer.valueOf(attr[4]))));
                requests.get(i).start();
                districts.get(i).start();
                districts1.add(new District(attr[0], attr[1],
                                Integer.valueOf(attr[2]), attr[3], Integer.valueOf(attr[4])));
                i++;
                in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
                res = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        serverSocket.close();

        //Publicación de titanes
        Scanner reader = new Scanner(System.in);
        String districtName;
        String titanName;
        String titanType;

        while (true) {
            System.out.println("[Distrito] Publicar Titán?");
            System.out.println("1.- Si");
            reader.next();

            System.out.println("[Distrito] Lista de Distritos:");
            System.out.println(showDistricts(districts1));

            System.out.println("[Distrito] Nombre del Distrito donde aparecerá el Titán");
            districtName = reader.next();

            System.out.println("[Distrito " + districtName + "] Introducir nombre del Titán" );
            titanName = reader.next();

            System.out.println("[Distrito " + districtName + "] Introducir tipo");
            System.out.println("1.- Normal\n2.- Excéntrico\n3.- Cambiante");
            titanType = reader.next();
            switch (titanType){
                case("1"):
                    titanType = "Normal";
                    break;
                case("2"):
                    titanType = "Excéntrico";
                    break;
                case("3"):
                    titanType = "Cambiante";
                    break;
            }

            titans.add(new Titan(districtName, titanName, titanType));
            System.out.println("[Distrito " + districtName + "] Se ha publicado" +
                    "el titán: " + titanName);
            System.out.println("**********");
            System.out.println("ID: " + titans.get(titans.size() - 1).getId());
            System.out.println("Nombre: " + titans.get(titans.size() - 1).getName());
            System.out.println("Tipo: " + titans.get(titans.size() - 1).getType());
            System.out.println("**********");

        }
    }
}

