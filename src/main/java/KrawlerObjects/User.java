package KrawlerObjects;

public class User {
    private String name;
    private String imageURL;
    private String mostPlayedChampion;
    private String winRate;
    private String averageKill;
    private String averageDeath;
    private String averageAssist;

    public User(String name, String imageURL, String mostPlayedChampion, String winRate, String averageKill, String averageDeath, String averageAssist) {
        this.name = name;
        this.imageURL = imageURL;
        this.mostPlayedChampion = mostPlayedChampion;
        this.winRate = winRate;
        this.averageKill = averageKill;
        this.averageDeath = averageDeath;
        this.averageAssist = averageAssist;
    }

    public String getName(){
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getMostPlayedChampion() {
        return mostPlayedChampion;
    }

    public String getWinRate() {
        return winRate;
    }

    public String getAverageKill() {
        return averageKill;
    }

    public String getAverageDeath() {
        return averageDeath;
    }

    public String getAverageAssist() {
        return averageAssist;
    }
}
