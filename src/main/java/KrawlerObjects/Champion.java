package KrawlerObjects;

import java.util.ArrayList;

public class Champion {
    private String name;
    private String imageURL;
    private String description;
    private String role;
    private String lane;
    private ArrayList<String> weakChamps;
    private ArrayList<String> strongChamps;

    public Champion(String name, String imageURL, String description, String role, String lane, ArrayList<String> weakChamps, ArrayList<String> strongChamps) {
        this.name = name;
        this.imageURL = imageURL;
        this.description = description;
        this.role = role;
        this.lane = lane;
        this.weakChamps = weakChamps;
        this.strongChamps = strongChamps;
    }

    public String getName() {
        return this.name;
    }

    public String getRole() {
        return this.role;
    }

    public String getLane() {
        return this.lane;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public String getDescription() {
        return this.description;
    }

    public ArrayList<String> getWeakChamps() {
        return this.weakChamps;
    }

    public ArrayList<String> getStrongChamps() {
        return this.strongChamps;
    }
}
