package InternalParser;

import Constants.Configuration;
import InternalParser.JsonLol.*;
import com.google.gson.JsonArray;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class JsonLoader {
    public static ArrayList<JsonChampion> champions = new ArrayList<>();
    public static ArrayList<JsonRunePrimary> runesPrimary = new ArrayList<>();
    public static ArrayList<JsonRune> runesSecondary = new ArrayList<>();
    public static ArrayList<JsonItem> items = new ArrayList<>();

    private static Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public void loadJson(InputStream url, DataType type) {
        logger.info("Parsing Champion Information");

        champions.clear();

        Scanner scanner = new Scanner(url);
        StringBuilder json = new StringBuilder();

        while (scanner.hasNext()) {
            json.append(scanner.next());
        }

        if (type == DataType.CHAMPIONS) {
            JSONObject jsonObject = new JSONObject(json.toString());

            //load champion json data
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

        } else if (type == DataType.ITEMS) {
            //TODO

        } else if (type == DataType.RUNES) {
            JSONArray jsonDataArray = new JSONArray(json.toString());

            for (int i = 0; i < jsonDataArray.length(); i++) {
                JSONObject primaryData = jsonDataArray.getJSONObject(i);

                int primaryId = primaryData.getInt("id");
                String primaryKey = primaryData.getString("key");
                String primaryName = primaryData.getString("name");

                JSONArray slots = primaryData.getJSONArray("slots");

                for (int j = 0; j < slots.length(); j++) {
                    JSONObject runes = slots.getJSONObject(j);

                    for (int k = 0; k < runes.length(); k++) {
                        JSONArray data = runes.getJSONArray("runes");

                        for (int l = 0; l < data.length(); l++) {
                            JSONObject runeFinalData = data.getJSONObject(l);

                            int id = runeFinalData.getInt("id");
                            String key = runeFinalData.getString("key");
                            String name = runeFinalData.getString("name");
                            String shortDesc = runeFinalData.getString("shortDesc");

                            runesSecondary.add(new JsonRune(name, key, id, shortDesc));
                        }
                    }
                }

                runesPrimary.add(new JsonRunePrimary(primaryName, primaryKey, primaryId));
            }
        }
    }
}
