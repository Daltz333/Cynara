package Abstract;

import Constants.Configuration;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;

public abstract class CommandExtension extends Command {
    @Override
    protected final void execute(CommandEvent event) {
        if (this.category.getName().equalsIgnoreCase("Admin")) {
            if (!event.getAuthor().getId().equals(Configuration.kOwnerId)) {
                if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    event.reply("You are not authorized for this command!");
                    return;
                }
            }
        }
        executeCommand(event);
    }

    protected abstract void executeCommand(CommandEvent event);
}
