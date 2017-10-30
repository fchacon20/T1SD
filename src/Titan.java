import java.io.Serializable;

public class Titan implements Serializable{

    private static int id;
    private String name;
    private String type;
    private String status;

    Titan(int id, String name, String type){
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = "Alive";
        Titan.id++;
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

    public void setStatus(String status) {
        this.status = status;
    }
}
