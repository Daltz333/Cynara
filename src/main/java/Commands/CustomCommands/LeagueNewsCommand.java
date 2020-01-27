package Commands.CustomCommands;

import Constants.Configuration;
import Utils.Pair;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        if (event.getMessage().getContentDisplay().contains(Configuration.kBotPrefix + "rss")) {
            event.getChannel().sendMessage("Currently the RSS Feed is unavailable!").queue();
        }

    }
}
