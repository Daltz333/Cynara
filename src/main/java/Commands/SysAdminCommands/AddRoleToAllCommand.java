package Commands.SysAdminCommands;

import Abstract.CommandExtension;
import Constants.Configuration;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddRoleToAllCommand extends CommandExtension {
    private static Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public AddRoleToAllCommand() {
        this.ownerCommand = true;
        this.name = "AddRoleAll";
        this.category = new Category("Owner");
        this.arguments = "role";
        this.help = "Gives role to all members";
    }

    @Override
    protected void executeCommand(CommandEvent event) {
        String roleArg = event.getArgs();

        Role mainRole = null;

        for (Role role : event.getGuild().getRoles()) {
            if (role.getName().equalsIgnoreCase(roleArg)) {
                mainRole = role;
            }
        }

        if (mainRole == null) {
            event.reply("The arg cannot be null!");
            return;
        }

        event.getGuild().retrieveMembers().complete(null);
        for (Member member: event.getGuild().getMembers()) {
            logger.info("Process member " + member.getNickname());
            event.getGuild().addRoleToMember(member, mainRole).queue();
        }

        event.reply("Finished adding roles to all members!");
    }
}
