package InternalParser.JsonLol;

public class JsonRune {
    private String name;
    private String key;
    private int id;
    private String shortDesc;

    public JsonRune(String name, String key, int id, String shortDesc) {
        this.name = name;
        this.key = key;
        this.id = id;
        this.shortDesc = shortDesc;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public int getId() {
        return id;
    }

    public String getShortDesc() {
        return shortDesc;
    }
}
