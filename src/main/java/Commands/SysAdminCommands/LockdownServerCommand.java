package Commands.SysAdminCommands;

import Constants.Configuration;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.util.ArrayList;

public class LockdownServerCommand extends Command{
    public LockdownServerCommand() {
        this.name = "lockdownserver";
        this.arguments = "none";
        this.category = new Command.Category("Admin");
        this.help = "Locks down the entire server to prevent sending messages!";
    }

    private ArrayList<String> lockedServers = new ArrayList<>();

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

        for (String channel : lockedServers) {
            if (event.getGuild().getId().equalsIgnoreCase(channel)) {
                event.getGuild().getPublicRole().getManager().givePermissions(Permission.MESSAGE_WRITE).queue();

                lockedServers.remove(channel);
                found = true;
                break;
            }
        }

        if (!found) {
            event.getGuild().getPublicRole().getManager().revokePermissions(Permission.MESSAGE_WRITE).queue();

            lockedServers.add(event.getGuild().getId());

            event.reply("This server has been locked down! An administrator may type " + Configuration.kBotPrefix + this.name + " to undo this lockdown!");

        } else {
            event.reply("Lockdown removed!");
        }
    }
}
