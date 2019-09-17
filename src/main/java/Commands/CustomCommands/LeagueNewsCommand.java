package Commands.CustomCommands;

import Commands.CustomCommands.Subscribers.RssLeagueThread;
import Constants.Configuration;
import Utils.Pair;
import com.apptastic.rssreader.RssReader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

public class LeagueNewsCommand extends ListenerAdapter {
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);
    private static ArrayList<Pair> channels = new ArrayList<>();
    private RssReader reader = new RssReader();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) {
            return;
        }

        if (event.getMember() == null) {
            return;
        }

        Pair guild = new Pair(event.getGuild().getId(), event.getChannel().getId());
        if (event.getMessage().getContentDisplay().startsWith(Configuration.kBotPrefix + "rssenable")) {
            boolean found = false;
            for (Pair guilds : RssLeagueThread.subscriberGuilds) {
                String guildData = (String)guilds.getKey();
                if (guildData.equalsIgnoreCase(event.getGuild().getId())) {
                    logger.info("Guild is already subscribed!");
                    found = true;
                }
            }

            if (!found) {
                RssLeagueThread.subscriberGuilds.add(guild);
                try {
                    String doesExist = "SELECT * FROM MAIN_GUILD_DATA WHERE Guild_ID=" + event.getGuild().getId();
                    String query = "INSERT INTO MAIN_GUILD_DATA (Guild_ID, Member_Id, Member_Name, Member_Xp, Riot_Rss_Enable, Riot_Rss_Channel, Bot_Prefix) VALUES (" + event.getGuild().getId() + ", NULL, NULL, NULL, 1," + event.getTextChannel().getId() + ", NULL) WHERE NOT EXISTS(SELECT * FROM MAIN_GUILD_DATA WHERE Guild_ID= " + event.getGuild().getId() + ")";
                    String updateDb = "UPDATE MAIN_GUILD_DATA SET Riot_Rss_Enable=1, Riot_Rss_Channel=" + event.getTextChannel().getId() + " WHERE Guild_ID=" + event.getGuild().getId();
                    Connection connection = DriverManager.getConnection(Configuration.kDatabaseUrl);
                    logger.info("Using query \n" + query);
                    ResultSet data = connection.createStatement().executeQuery(doesExist);

                    boolean update = false;
                    if (data.next()) {
                        update = true;
                    }

                    if (update) {
                        connection.createStatement().execute(updateDb);
                    } else {
                        connection.createStatement().execute(query);
                    }
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Unable to add RSS subscriber to database", e);
                }

                event.getChannel().sendMessage("Successfully subscribed channel to feed!").queue();
            } else {
                event.getChannel().sendMessage("Channel is already configured for this feed!").queue();
            }

        } else if (event.getMessage().getContentDisplay().startsWith(Configuration.kBotPrefix + "rssdisable")) {
            int i = 0;
            for (Pair guilds : RssLeagueThread.subscriberGuilds) {
                String guildData = (String)guilds.getKey();
                if (guildData.equals(event.getGuild().getId())) {
                    try {
                        String query = "UPDATE MAIN_GUILD_DATA SET Riot_Rss_Enable=0 WHERE Guild_ID=" + event.getGuild().getId();
                        Connection connection = DriverManager.getConnection(Configuration.kDatabaseUrl);
                        connection.createStatement().execute(query);
                        connection.close();
                    } catch (SQLException e) {
                        logger.error("Unable to disconnect database!", e);
                        event.getTextChannel().sendMessage("Unable to disconnect database!").queue();
                        return;
                    }
                    event.getChannel().sendMessage("Guild found, successfully removed!").queue();
                    RssLeagueThread.subscriberGuilds.remove(i);
                    return;
                }
                i++;
            }

            event.getChannel().sendMessage("Feed is currently not enabled for this channel").queue();

        } else {
            //ignore, not our command
        }

    }
}
