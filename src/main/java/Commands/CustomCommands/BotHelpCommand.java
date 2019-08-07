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
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Help Commands");
        eb.setColor(Configuration.kEmbedColor);
        eb.setFooter(Configuration.kEmbedFooterText, Configuration.kEmbedFooterUrl);

        //ignore cuz bot
        if (event.getAuthor().isBot()) {
            return;
        }

        //iterate over the command objects, and display their help and name values
        if (event.getMessage().getContentDisplay().startsWith(client.getPrefix() + "help")) {
            for (Command command : client.getCommands()) {
                eb.addField(command.getName(), command.getHelp() + " args: " + command.getArguments(), false);
            }

            event.getChannel().sendMessage(eb.build()).queue();
        }
    }
}
