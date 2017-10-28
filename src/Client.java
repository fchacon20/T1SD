public class Client {
    private String myIP;
    private String district;

    Client(String myIP, String district){
        this.myIP = myIP;
        this.district = district;
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
}
