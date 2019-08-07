package InternalParser;

public class JsonChampion {
    private String id;
    private String name;
    private int key;
    private String title;
    private String blurb;
    private String[] tags;

    public JsonChampion(String id, String name, int key, String title, String blurb, String[] tags) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.title = title;
        this.blurb = blurb;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getBlurb() {
        return blurb;
    }

    public String[] getTags() {
        return tags;
    }
}
