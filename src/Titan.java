import java.io.Serializable;

public class Titan implements Serializable{

    private static int id = 0;
    private String districtName;
    private String name;
    private String type;
    private String status;

    Titan(String district, String name, String type){
        this.id = id;
        this.name = name;
        this.districtName = district;
        this.type = type;
        this.status = "Alive";
        Titan.id++;
    }

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
