package WebParser;

import KrawlerObjects.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class LolUserParser {
    public User retrieveData(String userName) throws IOException {
        Document doc = Jsoup.connect("https://na.op.gg/summoner/userName=" + userName).get();

        return new User(userName, getUserImageURL(doc), getMostPlayedChampion(doc), getWinRatio(doc), getKillRatio(doc), getDeathRatio(doc), getAssistRatio(doc));
    }

    private String getUserImageURL(Document doc) {
        return doc.select("img.ProfileImage").first().absUrl("src");
    }

    private String getMostPlayedChampion(Document doc) {
        return doc.select("tr.TopRanker:nth-child(1) > td:nth-child(3) > a:nth-child(1)").text();
    }

    private String getKillRatio(Document doc) {
        return doc.select("tr.TopRanker:nth-child(1) > td:nth-child(5) > div:nth-child(1) > span:nth-child(1)").text();
    }

    private String getDeathRatio(Document doc) {
        return doc.select("tr.TopRanker:nth-child(1) > td:nth-child(5) > div:nth-child(1) > span:nth-child(2)").text();
    }

    private String getAssistRatio(Document doc) {
        return doc.select("tr.TopRanker:nth-child(1) > td:nth-child(5) > div:nth-child(1) > span:nth-child(3)").text();
    }

    private String getWinRatio(Document doc) {
        return doc.select("tr.TopRanker:nth-child(1) > td:nth-child(4) > div:nth-child(1) > span:nth-child(2)").text();
    }


}
