package InternalParser.JsonLol;

public class JsonRunePrimary {
    private String name;
    private String key;
    private int id;

    public JsonRunePrimary(String name, String key, int id) {
        this.name = name;
        this.key = key;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String geyKey() {
        return key;
    }

    public int getId() {
        return id;
    }
}
