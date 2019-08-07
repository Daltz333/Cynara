package InternalParser;

import Constants.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Scanner;

public class JsonLoader {
    public static ArrayList<JsonChampion> champions = new ArrayList<>();
    private static Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public void loadChampions() {
        logger.info("Parsing Champion Information");

        champions.clear();

        Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream("DDragon/champion.json"));
        StringBuilder json = new StringBuilder();

        while (scanner.hasNext()) {
            json.append(scanner.next());
        }

        JSONObject jsonObject = new JSONObject(json.toString());
        JSONObject champData = jsonObject.getJSONObject("data");
        JSONArray data = champData.names();

        for (int i = 0; i < data.length(); i++) {
            JSONObject object = champData.getJSONObject(data.getString(i));
            String id = object.getString("id");
            String name = object.getString("name");
            int key = object.getInt("key");
            String title = object.getString("title");
            String blurb = object.getString("blurb");

            JSONArray rolesArray = object.getJSONArray("tags");

            String[] roles = new String[rolesArray.length()];
            for (int j = 0; j < rolesArray.length(); j++) {
                roles[j] = rolesArray.get(j).toString();
            }

            champions.add(new JsonChampion(id, name, key, title, blurb, roles));
        }
    }
}
