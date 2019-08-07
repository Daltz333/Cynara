package WebParser;

import KrawlerObjects.Champion;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class LoLCounterParser {

    public Champion retrieveData(String championName) throws IOException {
        Document doc = Jsoup.connect("https://lolcounter.com/champions/" + championName.toLowerCase()).get();

        Champion champion = new Champion(getName(doc), getImageURL(doc, championName), getDescription(doc), getRole(doc), getLane(doc), getWeakChamps(doc), getStrongChamps(doc));

        return champion;
    }

    private String getName(Document doc) {
        Element name = doc.select("div.champ-block").select("div.name").first();

        return name.text();
    }

    private String getDescription(Document doc) {
        Element name = doc.select("div.champ-block").select("div.title").first();

        return name.text();
    }

    private String getLane(Document doc) {
        Elements lanes = doc.select("div.tabs").select("div.tlanes");

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lanes.size(); i++) {
            Element role = lanes.get(i);

            if (role.text().equalsIgnoreCase("general") || role.text().equalsIgnoreCase("all")) {
                continue;
            }

            if (i == lanes.size() - 1) {
                result.append(role.text());
            } else {
                result.append(role.text()).append(", ");
            }

        }

        return result.toString();
    }

    private String getRole(Document doc) {
        Elements roles = doc.select("div.champ-block").select("div.role");

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < roles.size(); i++) {
            Element role = roles.get(i);

            if (i == roles.size() - 1) {
                result.append(role.text());
            } else {
                result.append(role.text()).append(", ");
            }

        }

        return result.toString();
    }

    private String getImageURL(Document doc, String championName) {
        return "http://ddragon.leagueoflegends.com/cdn/8.24.1/img/champion/" + getName(doc) + ".png";
    }

    private ArrayList<String> getWeakChamps(Document doc) {
        Elements champNames = doc.select("div._all").select("div.weak-block").select("div.name");

        ArrayList<String> weakChamps = new ArrayList<>();

        for (Element e : champNames) {
            weakChamps.add(e.text());
        }

        return weakChamps;
    }

    private ArrayList<String> getStrongChamps(Document doc) {
        Elements champNames = doc.select("div._all").select("div.strong-block").select("div.name");

        ArrayList<String> strongChamps = new ArrayList<>();

        for (Element e : champNames) {
            strongChamps.add(e.text());
        }

        return strongChamps;
    }

    public ArrayList<String> getAllChamps() throws IOException {
        Document doc = Jsoup.connect("https://lolcounter.com/").get();

        Elements elements = doc.select("div.champions").select("div.name");

        ArrayList<String> champions = new ArrayList<>();

        for (Element e : elements) {
            champions.add(e.text());
        }

        return champions;
    }
}
