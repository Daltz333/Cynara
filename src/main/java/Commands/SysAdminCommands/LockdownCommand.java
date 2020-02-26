package Commands.SysAdminCommands;

import Constants.Configuration;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.util.ArrayList;

public class LockdownCommand extends Command{
    public LockdownCommand() {
        this.name = "lockdown";
        this.arguments = "none";
        this.category = new Command.Category("Admin");
        this.help = "Locks down the channel to prevent sending message!";
    }

    private ArrayList<String> lockedChannels = new ArrayList<>();

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getAuthor().getId().equals(Configuration.kOwnerId)) {
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("You are not authorized for this command!");
                return;
            }
        }

        if (!PermissionUtil.checkPermission(event.getSelfMember(), Permission.MANAGE_CHANNEL)) {
            event.reply("The bot does not have the MANAGE_CHANNEL permission!");
            return;
        }

        boolean found = false;

        for (String channel : lockedChannels) {
            if (event.getChannel().getId().equalsIgnoreCase(channel)) {
                event.getTextChannel().putPermissionOverride(
                        event.getGuild().getPublicRole()
                ).setAllow(Permission.MESSAGE_WRITE).queue();

                lockedChannels.remove(channel);
                found = true;
                break;
            }
        }

        if (!found) {
            event.getTextChannel().putPermissionOverride(
                    event.getGuild().getPublicRole()
            ).deny(Permission.MESSAGE_WRITE).queue();

            lockedChannels.add(event.getChannel().getId());

            event.reply("This channel has been locked down! An administrator may type " + Configuration.kBotPrefix + this.name + " to undo this lockdown!");

        } else {
            event.reply("Lockdown removed!");
        }
    }
}
