package CustomListeners;

import Commands.SysAdminCommands.AntiRaidCommand;
import Constants.Configuration;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmoteListener extends ListenerAdapter {
    private static Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (AntiRaidCommand.isRaidMessage(event.getMessageIdLong())) {
            event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(AntiRaidCommand.getRoleId(event.getGuild().getIdLong()))).queue();
        }
    }
}
