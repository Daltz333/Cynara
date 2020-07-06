package Commands.CustomCommands;

import Commands.CustomCommands.Subscribers.RiotGuild;
import Commands.CustomCommands.Subscribers.RiotNewsScheduler;
import Constants.Configuration;
import Utils.Pair;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LeagueNewsCommand extends ListenerAdapter {
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);
    private static ArrayList<Pair> channels = new ArrayList<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) {
            return;
        }

        if (event.getMember() == null) {
            return;
        }

        if (event.getMessage().getContentDisplay().contains(Configuration.kBotPrefix + "newsenable")) {
            if(alreadyAdded(event)) {
                event.getChannel().sendMessage("News is already enabled!").queue();
            } else {
                boolean found = false;
                for (RiotGuild guild : RiotNewsScheduler.riotGuilds) {
                    if (guild.getGuildId().equalsIgnoreCase(event.getGuild().getId())) {
                        found = true;

                        guild.setNewsEnabled(true);
                        guild.setAnnouncementChannelId(event.getTextChannel().getId());

                        event.getChannel().sendMessage("Enabled news output for " + event.getGuild().getName()).queue();

                        try {
                            Connection connection = DriverManager.getConnection(Configuration.kDatabaseUrl);
                            connection.createStatement().execute("UPDATE MAIN_GUILD_DATA SET Riot_Rss_Enable=1, Riot_Rss_Channel=" + event.getTextChannel().getId() + ", Riot_Rss_Last_Message=\"" + guild.getLastShownTitle() + "\" WHERE Guild_ID=" + event.getGuild().getId());
                            connection.close();
                        } catch (SQLException e) {
                            logger.error("Error connecting to database", e);
                        }
                        break;
                    }
                }

                if (!found) {
                    RiotNewsScheduler.riotGuilds.add(new RiotGuild(event.getGuild().getId(), event.getChannel().getId(), true, ""));
                    try {
                        Connection connection = DriverManager.getConnection(Configuration.kDatabaseUrl);
                        ResultSet data = connection.createStatement().executeQuery("SELECT * FROM MAIN_GUILD_DATA WHERE Guild_ID=\""+event.getGuild().getId() +"\"");

                        if (!data.next()) {
                            connection.createStatement().execute("INSERT INTO MAIN_GUILD_DATA (Guild_ID, Member_Id, Member_Name, Member_Xp, Riot_Rss_Enable, Riot_Rss_Channel, Bot_Prefix) VALUES (" + event.getGuild().getId() + ", NULL, NULL, NULL, 1," + event.getTextChannel().getId() + ", NULL)");
                        } else {
                            //connection.createStatement().execute("UPDATE MAIN_GUILD_DATA SET Riot_Rss_Enable=1, Riot_Rss_Channel=" + event.getTextChannel().getId() + ", Riot_Rss_Last_Message=\"" +  + "\" WHERE Guild_ID=" + event.getGuild().getId());
                        }
                        connection.close();
                    } catch (SQLException e) {
                        logger.error("Error connecting to database", e);
                    }
                    event.getChannel().sendMessage("Enabled news output for " + event.getGuild().getName()).queue();
                }
                //TODO add to database!
            }
        } else if (event.getMessage().getContentDisplay().contains(Configuration.kBotPrefix + "newsdisable")) {
            boolean found = false;
            for (RiotGuild guild : RiotNewsScheduler.riotGuilds) {
                if (guild.getGuildId().equalsIgnoreCase(event.getGuild().getId())) {
                    found = true;

                    guild.setNewsEnabled(false);

                    event.getChannel().sendMessage("Disabled news output for " + event.getGuild().getName()).queue();
                    try {
                        Connection connection = DriverManager.getConnection(Configuration.kDatabaseUrl);
                        connection.createStatement().execute("UPDATE MAIN_GUILD_DATA SET Riot_Rss_Enable=0, Riot_Rss_Channel=" + event.getTextChannel().getId() + " WHERE Guild_ID=" + event.getGuild().getId());
                        connection.close();
                    } catch (SQLException e) {
                        logger.error("Error connecting to database", e);
                    }
                    break;
                }
            }

            if (!found) {
                event.getChannel().sendMessage("News is already disabled on this server!").queue();
            }
        } else {
            //unknown command ignore
        }

    }

    public boolean alreadyAdded(MessageReceivedEvent event) {
        for (RiotGuild guild : RiotNewsScheduler.riotGuilds) {
            if (guild.getGuildId().equalsIgnoreCase(event.getGuild().getId())) {
                if (guild.newsEnabled()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        return false;
    }
}
