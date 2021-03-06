import Commands.CustomCommands.BotHelpCommand;
import Commands.CustomCommands.DadBotCustomCommand;
import Commands.CustomCommands.LeagueNewsCommand;
import Commands.CustomCommands.Subscribers.RiotGuild;
import Commands.CustomCommands.Subscribers.RiotNewsScheduler;
import Commands.LeagueCommands.*;
import Commands.MemeCommands.AnimemeCommand;
import Commands.MemeCommands.EggCommand;
import Commands.MemeCommands.InsultCommand;
import Commands.MusicCommands.*;
import Commands.SysAdminCommands.*;
import Constants.Configuration;
import CustomListeners.EmoteListener;
import Handlers.StatusUpdater;
import InternalParser.ConfigurationLoader;
import InternalParser.JsonLoader;
import InternalParser.JsonLol.DataType;
import Music.MusicManager;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    private static JDA jda;

    private static Timer timer = new Timer();

    public static void main(String[] args) {
        try {
            JsonLoader jsonLoader = new JsonLoader();

            //load champion static information
            jsonLoader.loadJson(DataType.CHAMPIONS);
            jsonLoader.loadJson(DataType.RUNES);

            try {
                ConfigurationLoader.copyTemplateJSON();
            } catch (IOException e) {
                logger.error("Failed to copy template json!", e);
                return;
            }

            Scanner scanner = null;
            try {
                scanner = new Scanner(new File(ConfigurationLoader.getCurrentDir() + File.separator + "config.json"));
            } catch (FileNotFoundException e) {
                logger.error("Configuration file not found!");
                return;
            }

            StringBuilder json = new StringBuilder();

            while (scanner.hasNext()) {
                json.append(scanner.next());
            }

            System.out.println(json.toString());

            JSONObject data;

            try {
                data = new JSONObject(json.toString());
            } catch (JSONException e) {
                logger.error("There was an error loading the configuration valid, check it's validity!");
                return;
            }

            String botToken = data.getString("botToken");
            String riotToken = data.getString("riotToken");
            String redditUsername = data.getString("redditUsername");
            String redditPassword = data.getString("redditPassword");
            String redditId = data.getString("redditId");
            String redditSecret = data.getString("redditSecret");
            String redirectUrl = data.getString("redirectUrl");

            if (botToken == null || riotToken == null || redditId == null || redditSecret == null || redirectUrl == null) {
                logger.error("Failed to load tokens! Check Configuration!");
                return;
            }

            Credentials credentials = Credentials.script(redditUsername, redditPassword, redditId, redditSecret);
            UserAgent userAgent = new UserAgent("bot", "daltz.bot.cynara", "1.0.0", "Cynara");

            RedditClient reddit = null;
            //regen

            try {
                reddit = OAuthHelper.automatic(new OkHttpNetworkAdapter(userAgent), credentials);
            } catch (NetworkException e) {
                logger.error("Failed to authenticate REDDIT API! Associated commands will not work!", e);
            }

            CommandClientBuilder builder = new CommandClientBuilder();
            ApiConfig config = new ApiConfig().setKey(riotToken);
            RiotApi api = new RiotApi(config);

            MusicManager manager = new MusicManager();

            //set bot configurations
            builder.setOwnerId(Configuration.kOwnerId);
            builder.setPrefix(Configuration.kBotPrefix);
            builder.setActivity(Activity.playing(Configuration.kActivityText));
            builder.useHelpBuilder(false);

            builder.addCommands(new ChampInfoCommand(), new RandomChampCommand(),
                    new CurrentRotationCommand(api), new SpecsCommand(), new PlayCommand(manager),
                    new StopCommand(manager), new SkipCommand(manager), new PlaylistCommand(manager),
                    new SkipToTrackCommand(manager), new LeagueSpectatorCommand(api), new AnimemeCommand(reddit),
                    new PurgeCommand(), new InsultCommand(), new AddEmojiCommand(), new MoveMusicChannelCommand(),
                    new LockdownCommand(), new LockdownServerCommand(), new EggCommand(), new AntiRaidCommand(),
                    new AddRoleToAllCommand(), new LeagueTriviaCommand());

            CommandClient client = builder.build();

            boolean dbLess = false;

            Connection connection = null;
            try {
                connection = DriverManager.getConnection(Configuration.kDatabaseUrl);

            } catch (SQLException e) {
                logger.error("Error connecting to database, continuing in dbless mode", e);
                dbLess = true;
            }

            try {
                jda = JDABuilder.createDefault(botToken).build();

                if (!dbLess) {
                    try {
                        String query = "CREATE TABLE IF NOT EXISTS MAIN_GUILD_DATA (Guild_ID int, Member_Id int, " +
                                "Member_Name char(255), Member_Xp int, Riot_Rss_Enable int, Riot_Rss_Channel int, " +
                                "Bot_Prefix char(255), Riot_Rss_Last_Message char(255), Riot_Anti_Raid_Message int, " +
                                "Riot_Anti_Raid_Role int, Riot_Anti_Raid_Active int, PRIMARY KEY(Guild_ID))";

                        String queryGuilds = "SELECT * FROM MAIN_GUILD_DATA WHERE Riot_Rss_Enable = 1";
                        connection.createStatement().execute(query);
                        ResultSet sqlData = connection.createStatement().executeQuery(queryGuilds);

                        while (sqlData.next()) {
                            String guildId = String.valueOf(sqlData.getLong("Guild_ID"));
                            String rssChannel = String.valueOf(sqlData.getLong("Riot_Rss_Channel"));
                            String lastMessage = sqlData.getString("Riot_Rss_Last_Message");
                            boolean enabled = sqlData.getInt("Riot_Rss_Enable") == 1;

                            RiotNewsScheduler.riotGuilds.add(new RiotGuild(guildId, rssChannel, enabled, lastMessage));
                        }

                        connection.close();
                    } catch (SQLException ex) {
                        logger.error("Error creating statement for guild ", ex);
                    }
                }

                try {
                    jda.awaitReady();
                } catch (InterruptedException e) {
                    logger.error("How the fuck did the JDA await get interrupted???");
                }

                //loader our client and custom commands
                jda.addEventListener(client, new DadBotCustomCommand(), new BotHelpCommand(client), new LeagueNewsCommand(), new EmoteListener());
                TimerTask updaterTask = new StatusUpdater(jda);
                TimerTask newsTask = new RiotNewsScheduler(jda);

                timer.schedule(updaterTask, 0, 3600000);
                timer.schedule(newsTask, 0, 600000);

            } catch (LoginException e) {
                logger.error("Exception: ", e);
            }
        } catch (RuntimeException e) {
            logger.error("UNCAUGHTEXCEPTION: ", e);
        }
    }
}