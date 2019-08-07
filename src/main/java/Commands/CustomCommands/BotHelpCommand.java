package Commands.CustomCommands;

import Constants.Configuration;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotHelpCommand extends ListenerAdapter {
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    private CommandClient client;

    public BotHelpCommand(CommandClient client) {
        this.client = client;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        EmbedBuilder ebMusic = new EmbedBuilder();

        ebMusic.setTitle("Music Commands");
        ebMusic.setColor(Configuration.kEmbedColor);
        ebMusic.setFooter(Configuration.kEmbedFooterText, Configuration.kEmbedFooterUrl);

        EmbedBuilder ebLeague = new EmbedBuilder();

        ebLeague.setTitle("League Commands");
        ebLeague.setColor(Configuration.kEmbedColor);
        ebLeague.setFooter(Configuration.kEmbedFooterText, Configuration.kEmbedFooterUrl);

        EmbedBuilder ebSys = new EmbedBuilder();

        ebSys.setTitle("Owner Commands");
        ebSys.setColor(Configuration.kEmbedColor);
        ebSys.setFooter(Configuration.kEmbedFooterText, Configuration.kEmbedFooterUrl);

        //ignore cuz bot
        if (event.getAuthor().isBot()) {
            return;
        }

        //iterate over the command objects, and display their help and name values
        if (event.getMessage().getContentDisplay().startsWith(client.getPrefix() + "help")) {
            for (Command command : client.getCommands()) {
                if (command.getCategory().getName().equalsIgnoreCase("Music")) {
                    ebMusic.addField(command.getName(), command.getHelp() + " - args: " + command.getArguments(), false);
                } else if (command.getCategory().getName().equalsIgnoreCase("League")) {
                    ebLeague.addField(command.getName(), command.getHelp() + " - args: " + command.getArguments(), false);
                } else if (command.getCategory().getName().equalsIgnoreCase("Owner")) {
                    ebSys.addField(command.getName(), command.getHelp() + " - args: " + command.getArguments(), false);
                }
            }

            event.getChannel().sendMessage(ebMusic.build()).queue();
            event.getChannel().sendMessage(ebLeague.build()).queue();

            if (event.getAuthor().getId().equals(Configuration.kOwnerId)) {
                event.getChannel().sendMessage(ebSys.build()).queue();
            }
        }
    }
}
