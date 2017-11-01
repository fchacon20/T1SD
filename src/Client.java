public class Client {
    private String myIP;
    private String district;
    private int port;

    Client(String myIP, String district, int port){
        this.myIP = myIP;
        this.district = district;
        this.port = port;
    }

    public String getDistrict() {
        return district;
    }

    public String getMyIP() {
        return myIP;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getPort() {
        return port;
    }
}
