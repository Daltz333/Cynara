package Commands.CustomCommands.Subscribers;

import Constants.Configuration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TimerTask;

public class RiotNewsScheduler extends TimerTask {
    private JDA jda;
    private int i = 0;

    public static ArrayList<RiotGuild> riotGuilds = new ArrayList<>();
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public RiotNewsScheduler(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        logger.info("Running News Command!");
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(IOUtils.toString(new URL("https://lolstatic-a.akamaihd.net/frontpage/apps/prod/harbinger-l10-website/en-us/production/en-us/page-data/news/game-updates/page-data.json"), StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("Error retrieving JSON News!", e);
            return;
        }

        JSONObject array = jsonObject.getJSONObject("result").getJSONObject("pageContext").getJSONObject("data").getJSONArray("sections").getJSONObject(0).getJSONObject("props").getJSONArray("articles").getJSONObject(0);

        for (RiotGuild guild : riotGuilds) {
            if (!guild.newsEnabled()) {
                continue;
            }

            System.out.println(array.toString());
            String title = array.getString("title");
            boolean internal = array.getJSONObject("link").getBoolean("internal");
            String rawURL = array.getJSONObject("link").getString("url");
            String url = rawURL;
            // verify if the url is internal or not
            if (internal) {
                url = "https://na.leagueoflegends.com/en-us/" + rawURL;
            }
            String imageUrl = array.getString("imageUrl");

            if(guild.getLastShownTitle() == null) {
                guild.setLastShownTitle("");
            }

            if (!guild.getLastShownTitle().equalsIgnoreCase(title)){
                if (guild.getAnnouncementChannelId() != null) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle(title);
                    builder.setDescription("[Read More!](" + url + ")");
                    builder.setImage(imageUrl);
                    builder.setFooter("Riot Patch Notes");

                    guild.setLastShownTitle(title);

                    jda.getGuildById(guild.getGuildId()).getTextChannelById(guild.getAnnouncementChannelId()).sendMessage(builder.build()).queue();

                    try {
                        Connection connection = DriverManager.getConnection(Configuration.kDatabaseUrl);
                        connection.createStatement().execute("UPDATE MAIN_GUILD_DATA SET Riot_Rss_Last_Message=\"" + title + "\" WHERE Guild_ID=" + guild.getGuildId());
                        connection.close();
                    } catch (SQLException e) {
                        logger.error("Error connecting to database", e);
                    }

                } else {
                    logger.error("Announcements are enabled but channel is null!");
                }
            } else {
                logger.info(title + " has already been shown!");
            }
        }
    }
}
