package Commands.SysAdminCommands;

import Abstract.CommandExtension;
import Constants.Configuration;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.Channel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AntiRaidCommand extends CommandExtension {
    private boolean isActive = false;
    private boolean create = false;
    private static ArrayList raidChannels = new ArrayList<>();

    private static Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public AntiRaidCommand() {
        this.category = new Category("Admin");
        this.name = "activateAntiRaid";
        this.arguments = "Default Role";
        this.help = "Activates server anti cheat!";
    }

    @Override
    protected void executeCommand(CommandEvent event) {
        if (!isActive) {
            // verify isActive status
            checkActiveStatus(event);
        }

        String roleArg = event.getArgs();
        long roleId = -1;

        // verify that the given roleArg and channel are valid
        boolean validRole = false;
        for (Role roleQueue : event.getGuild().getRoles()) {
            if (roleQueue.getName().equalsIgnoreCase(roleArg))  {
                validRole = true;
                roleId = roleQueue.getIdLong();
                break;
            }
        }

        if (!validRole) {
            event.reply("The roleArg you have given is invalid!");
            return;
        }
        // we are not currently enabled!
        if (!this.isActive) {
            Message message = event.getTextChannel().sendMessage("Please react with the \u2705 if you agree!").complete();
            message.addReaction("\u2705").queue();
            updateServer(roleId, message.getIdLong(), event.getGuild().getIdLong());
        }
    }

    private void updateServer(long roleId, long messageId, long guildId) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(Configuration.kDatabaseUrl);
            connection.createStatement().execute("UPDATE MAIN_GUILD_DATA SET Riot_Anti_Raid_Active = 1, " +
                    "Riot_Anti_Raid_Role = " + roleId +", " +
                    "Riot_Anti_Raid_Message = " + messageId + " "
                    + "WHERE Guild_ID = "+ guildId);
            logger.info("Successfully updated server!");
            connection.close();
        } catch (SQLException e) {
            logger.error("Error updating SQL for anti-raid", e);
        }

    }
    private void checkActiveStatus(CommandEvent event) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(Configuration.kDatabaseUrl);
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM MAIN_GUILD_DATA WHERE Guild_ID=\""+event.getGuild().getId() +"\"");

            if (!data.next()) {
                connection.createStatement().execute("INSERT INTO MAIN_GUILD_DATA (Guild_ID, Member_Id, Member_Name, Member_Xp, Riot_Rss_Enable, Riot_Rss_Channel, Bot_Prefix) VALUES (" + event.getGuild().getId() + ", NULL, NULL, NULL, 0, NULL, NULL)");
                logger.info("Created guild into database from anti-raid");
                this.create = true;
                return;
            }

            // do some initialization
            while (data.next()) {
                int active = data.getInt("Riot_Anti_Raid_Active");
                int guild = data.getInt("Guild_ID");


                if (guild == Integer.parseInt(event.getGuild().getId())) {
                    if (active == 1) {
                        this.isActive = true;
                    } else {
                        this.isActive = false;
                    }

                    break;
                }
            }

            connection.close();
        } catch (SQLException throwables) {
            logger.error("SQL error during DB check of anti-raid", throwables);
        }
    }

    public static boolean isRaidMessage(long message) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(Configuration.kDatabaseUrl);
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM MAIN_GUILD_DATA");

            while (data.next()) {
                if (data.getLong("Riot_Anti_Raid_Message") == message)  {
                    connection.close();
                    return true;
                }
            }

            connection.close();
            return false;
        } catch (SQLException e) {
            logger.info("Exception", e);
            return false;
        }
    }

    public static long getRoleId(long guildId) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(Configuration.kDatabaseUrl);
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM MAIN_GUILD_DATA");

            while (data.next()) {
                if (data.getLong("Guild_ID") == guildId) {
                    long role = data.getLong("Riot_Anti_Raid_Role");
                    connection.close();
                    return role;
                }
            }

            connection.close();
        } catch (SQLException e) {
            logger.info("Exception", e);
        }

        return -1;
    }
}
