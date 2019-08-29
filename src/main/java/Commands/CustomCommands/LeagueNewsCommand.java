package Commands.CustomCommands;

import Commands.CustomCommands.Subscribers.RssLeagueThread;
import Constants.Configuration;
import Utils.Pair;
import com.apptastic.rssreader.RssReader;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        if (event.getMember().hasPermission(Permission.MANAGE_CHANNEL) || event.getMember().getId().equals(Configuration.kOwnerId)) {
            //enable rss-feed for this channels
            if (event.getMessage().getContentDisplay().contains(Configuration.kBotPrefix + "rssenable")){
                if (containsId(event.getChannel().getId())) {
                    event.getChannel().sendMessage("This bot is already configured to this channel!").queue();
                } else {
                    RssLeagueThread runnable = new RssLeagueThread(event);
                    Thread t = new Thread(runnable);
                    channels.add(new Pair(event.getChannel().getId(), runnable));
                    t.setDaemon(true);
                    t.start();

                    event.getChannel().sendMessage("League Feed has been enabled for " + event.getChannel().getName()).queue();
                }
            } else if (event.getMessage().getContentDisplay().contains(Configuration.kBotPrefix + "rssdisable")) {
                if (containsId(event.getChannel().getId())) {
                    boolean found = false;
                    for (Pair pair : channels) {
                        if (pair.getKey().toString().equalsIgnoreCase(event.getChannel().getId())) {
                            found = true;
                            RssLeagueThread t = (RssLeagueThread) pair.getValue();
                            t.requestStop();
                            channels.remove(pair);
                            event.getChannel().sendMessage("Disabled for channel " + event.getChannel().getName()).queue();
                            break;
                        }
                    }

                    if (!found) {
                        event.getChannel().sendMessage("Unable to disable feed? Perhaps it was never enabled?").queue();
                        logger.warn("WARNING! ATTEMPTING TO DISABLE COMMAND NOT IN QUEUE! CHECK THREADS!");
                    }

                } else {
                    logger.warn("WARNING! ATTEMPTING TO DISABLE COMMAND NOT IN QUEUE! CHECK THREADS!");
                }
            }
        }
    }

    private boolean containsId(String id) {
        for (Pair pair : channels) {
            if (pair.getKey().toString().equalsIgnoreCase(id)) {
                return true;
            }
        }

        return false;
    }
}
