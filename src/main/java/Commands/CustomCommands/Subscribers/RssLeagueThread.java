package Commands.CustomCommands.Subscribers;

import Constants.Configuration;
import Utils.Pair;
import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import com.sun.tools.javac.jvm.Items;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RssLeagueThread implements Runnable {
    private JDA jda;
    private RssReader reader = new RssReader();
    private boolean stop = false;
    private int initTime = (int) System.currentTimeMillis();
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

                logger.info("Added guild information of " + guildId + " " + rssChannel);
                logger.info("Added guild " + guildId + " as a subscriber");
                subscriberGuilds.add(new Pair(guildId, rssChannel));
            }

            connection.close();

        } catch (SQLException e) {
            logger.error("Unable to establish SQL connection for RssThread!", e);
        }

        while (!stop){
            int difference = (int) (System.currentTimeMillis() - initTime);
            if (difference >= 10000) {
                Stream<Item> stream = null;
                ArrayList<Item> items = null;
                initTime = (int) System.currentTimeMillis();
                try {
                    stream = reader.read("https://na.leagueoflegends.com/en/rss.xml");
                    items = (ArrayList<Item>) stream.collect(Collectors.toList());
                    int i = 0;

                    Item item = items.get(0);

                    if (!item.getTitle().isPresent() || !item.getDescription().isPresent()) {
                        continue;
                    }

                    i++;

                    if (!lastKnownTitle.equalsIgnoreCase(item.getTitle().get())) {
                        lastKnownTitle = item.getTitle().get();

                        Document soup = Jsoup.parse(item.getDescription().get());

                        Element image = soup.select("img").first();

                        if (image == null || image.text() == null) {
                            logger.warn("No image in document?\n" + item.getDescription().get());
                            continue;
                        }

                        String imageUrl = "https://na.leagueoflegends.com/" + image.attr("src");

                        logger.info("Using Image URL: " + imageUrl);

                        builder.setTitle(lastKnownTitle);
                        builder.setImage(imageUrl);

                        for (Guild guild : jda.getGuilds()) {
                            for (Pair guilds : subscriberGuilds) {
                                String guildData = (String)guilds.getKey();
                                logger.info("Comparing " + guild.getId() + "vs" + guildData);
                                if (guildData.equalsIgnoreCase(guild.getId())) {
                                    logger.info("Publishing feed for guild " + guild.getId());
                                    Objects.requireNonNull(guild.getTextChannelById((String) guilds.getValue())).sendMessage(builder.build()).queue();
                                }
                            }
                        }

                        break;
                    } else {
                        //do nothing, rss is not in list
                    }

                } catch (IOException e) {
                    logger.error("Unknown exception: ", e);
                }
            }

            //sleep to save cpu time
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                logger.error("Error: ", e);
            }
        }
    }

    public void requestStop() {
        stop = true;
    }
}
