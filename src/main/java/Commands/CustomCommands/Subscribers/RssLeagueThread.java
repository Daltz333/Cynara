package Commands.CustomCommands.Subscribers;

import Constants.Configuration;
import Utils.Pair;
import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RssLeagueThread extends TimerTask {
    private JDA jda;
    private RssReader reader = new RssReader();
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);
    private String lastKnownTitle = "";

    public static ArrayList<Pair> subscriberGuilds = new ArrayList<>();

    public RssLeagueThread(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setFooter("Riot News");
        builder.setColor(Color.BLUE);

        try {
            String query = ("SELECT * FROM MAIN_GUILD_DATA WHERE Riot_Rss_Enable = 1");
            Connection connection = DriverManager.getConnection(Configuration.kDatabaseUrl);
            ResultSet data = connection.createStatement().executeQuery(query);

            while (data.next()) {
                String guildId = String.valueOf(data.getLong("Guild_ID"));
                String rssChannel = String.valueOf(data.getLong("Riot_Rss_Channel"));
                String lastMessage = data.getString("Riot_Rss_Last_Message");
                if (lastMessage == null) {
                    lastKnownTitle = "";
                } else {
                    lastKnownTitle = lastMessage;
                }

                logger.info("Added guild information of " + guildId + " " + rssChannel);
                logger.info("Added guild " + guildId + " as a subscriber");
                subscriberGuilds.add(new Pair(guildId, rssChannel));
            }

            connection.close();

        } catch (SQLException e) {
            logger.error("Unable to establish SQL connection for RssThread!", e);
        }

        Stream<Item> stream;
        ArrayList<Item> items;

        try {
            stream = reader.read("https://na.leagueoflegends.com/en/rss.xml");
            items = (ArrayList<Item>) stream.collect(Collectors.toList());

            Item item = items.get(0);

            if (!item.getTitle().isPresent() || !item.getDescription().isPresent() || !item.getLink().isPresent()) {
                return;
            }


            if (!lastKnownTitle.equalsIgnoreCase(item.getTitle().get())) {
                lastKnownTitle = item.getTitle().get();
                Document soup = Jsoup.parse(item.getDescription().get());

                Element image = soup.select("img").first();
                String description = soup.selectFirst("div").text() + " [Read More!](" + item.getLink().get() + ")";

                if (image == null || image.text() == null) {
                    logger.warn("No image in document?\n" + item.getDescription().get());
                    return;
                }

                String imageUrl = "https://na.leagueoflegends.com/" + image.attr("src");

                logger.info("Using Image URL: " + imageUrl);

                builder.setTitle(lastKnownTitle);
                builder.setDescription(description);
                builder.setImage(imageUrl);

                for (Guild guild : jda.getGuilds()) {
                    for (Pair guilds : subscriberGuilds) {
                        String guildData = (String)guilds.getKey();
                        logger.info("Comparing " + guild.getId() + "vs" + guildData);
                        if (guildData.equalsIgnoreCase(guild.getId())) {
                            logger.info("Publishing feed for guild " + guild.getId());
                            Objects.requireNonNull(guild.getTextChannelById((String) guilds.getValue())).sendMessage(builder.build()).queue();

                            String query = "UPDATE MAIN_GUILD_DATA SET Riot_Rss_Last_Message='" + lastKnownTitle +"' WHERE Guild_ID=" + guild.getId();
                            try {
                                Connection connection = DriverManager.getConnection(Configuration.kDatabaseUrl);
                                connection.createStatement().execute(query);
                                connection.close();
                            } catch (SQLException e) {
                                logger.error("SQL Exception: ", e);
                            }
                        }
                    }
                }

            } //do nothing, rss is not in list

        } catch (IOException e) {
            logger.error("Unknown exception: ", e);
        }
    }
}
