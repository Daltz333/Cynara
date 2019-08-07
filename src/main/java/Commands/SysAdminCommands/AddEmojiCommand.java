package Commands.SysAdminCommands;

import Constants.Configuration;
import InternalParser.ConfigurationLoader;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddEmojiCommand extends Command {
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public AddEmojiCommand() {
        this.name = "addemoji";
        this.arguments = "emojiName, imageFile";
        this.category = new Category("Admin");
        this.help = "Adds the specified emoji to the server!";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getAuthor().getId().equals(Configuration.kOwnerId)) {
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("You are not authorized for this command!");
                return;
            }
        }

        if (!PermissionUtil.checkPermission(event.getSelfMember(), Permission.MANAGE_EMOTES)) {
            event.reply("The bot does not have the MANAGE_EMOTES permission!");
            return;
        }

        List<Message.Attachment> attachments = event.getMessage().getAttachments();

        Icon tempIcon = null;

        boolean imageFound = false;
        for (Message.Attachment attachment: attachments) {
            if (attachment.isImage()) {
                imageFound = true;
                try {
                    tempIcon = attachment.retrieveAsIcon().get();
                    break;
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Exception: ", e);
                    event.reply("An unknown error occurred! It has been logged!");
                    return;
                }
            }
        }

        if (!imageFound) {
            event.reply("You must submit a valid image file!");
            return;
        } else if (tempIcon == null) {
            event.reply("An unknown error occurred! It has been logged!");
            logger.error("File is still null!");
            return;
        }

        event.getGuild().createEmote(event.getArgs(), tempIcon).queue();
        event.reply("Successfully added emoji!");
    }
}
