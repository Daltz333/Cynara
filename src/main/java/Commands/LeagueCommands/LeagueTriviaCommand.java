package Commands.LeagueCommands;

import Constants.Configuration;
import WebParser.LoLCounterParser;
import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class LeagueTriviaCommand extends Command {
    private static final String StoryBaseURL = "https://universe-meeps.leagueoflegends.com/v1/en_us/champions/";
    private LoLCounterParser loLCounterParser = new LoLCounterParser();
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public LeagueTriviaCommand() {
        this.name = "loltrivia";
        this.help = "Guess that champion!";
        this.arguments = "none";
        this.category = new Category("League");
    }

    @Override
    protected void execute(CommandEvent event) {
        ArrayList<String> championList = null;
        try {
            championList = loLCounterParser.getAllChamps();
        } catch (IOException e) {
            logger.error("Error!", e);
            event.reply("There was an error getting a random champion");
            return;
        }

        String champName = championList.get(1 + (int) (Math.random() * ((championList.size() - 1) + 1)));
        champName = champName.replaceAll("[^a-zA-Z0-9]", "");
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(IOUtils.toString(new URL(StoryBaseURL + champName.toLowerCase() + "/index.json"), StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("Unable to read from URL", e);
            event.reply("Error parsing for champion: " + champName.toLowerCase());
            return;
        }

        String champInfo = jsonObject.getJSONObject("champion").getJSONObject("biography").getString("full");

        String plainText;
        if (champInfo.length() < 2000) {
            plainText = champInfo;
        } else {
            plainText = Jsoup.parse(champInfo).text().substring(0, 2000) + "...";
        }

        plainText = plainText.replaceAll(champName, "KAREN");

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(event.getSelfMember().getNickname());
        builder.setTitle("Guess that Champion!");
        builder.setDescription(plainText);
        event.reply(builder.build());
    }
}
