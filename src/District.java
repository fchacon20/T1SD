public class District {

    private String name;
    private String IPmulticast;
    private int portM;
    private String IPPeticiones;
    private int portP;

    District(){}

    District(String name, String IPmulticast, int portM, String IPPeticiones, int portP){
        this.name = name;
        this.IPmulticast = IPmulticast;
        this.portM = portM;
        this.IPPeticiones = IPPeticiones;
        this.portP = portP;
    }

    public int getPortM() {
        return portM;
    }

    public int getPortP() {
        return portP;
    }

    public String getIPmulticast() {
        return IPmulticast;
    }

    public String getIPPeticiones() {
        return IPPeticiones;
    }

    public String getName() {
        return name;
    }
}
