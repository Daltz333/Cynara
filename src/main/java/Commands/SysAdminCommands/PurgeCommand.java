package Commands.SysAdminCommands;

import Constants.Configuration;
import Handlers.RateLimitHandler;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PurgeCommand extends Command {
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public PurgeCommand() {
        this.category = new Category("Admin");
        this.name = "purge";
        this.help = "Purges the last X messages, 2 to 100.";
        this.arguments = "numOfMessages";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getAuthor().getId().equals(Configuration.kOwnerId)) {
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("You are not authorized for this command!");
                return;
            }
        }

        int numMessages;
        try {
            numMessages = Integer.parseInt(event.getArgs());
        } catch (NumberFormatException e) {
            event.reply("Invalid Argument!");
            return;
        }

        if (numMessages > 100) {
            event.reply("You can only purge 100 messages at a time!");
            return;
        }

        List<Message> result = event.getChannel().getIterableHistory().stream().limit(numMessages).collect(Collectors.toList());

        if (result.isEmpty()) {
            event.reply("No messages to delete!");
            return;
        }

        int numDeletedMessages = 0;
        try {
            event.getTextChannel().deleteMessages(result).queue();
            //this could fail because of discord api
            numDeletedMessages = result.size();
        } catch (IllegalArgumentException e) {
            //check if fail because message older than 2 weeks
            if (e.getMessage().contains("2 weeks")) {
                for (Message message : result) {
                    message.delete().queue();
                    numDeletedMessages++;
                }
            }
        }

        event.reply("Deleted " + numDeletedMessages + " from " + event.getChannel().getName() + " in " + event.getGuild().getName());
    }
}
