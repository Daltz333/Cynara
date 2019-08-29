package Commands.SysAdminCommands;

import Constants.Configuration;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class MoveMusicChannelCommand extends Command {
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public MoveMusicChannelCommand() {
        this.name = "moveuser";
        this.arguments = "member, channel";
        this.help = "Moves user to specified voice channel.";
        this.category = new Category("Admin");
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getAuthor().getId().equals(Configuration.kOwnerId)) {
            if (!event.getMember().hasPermission(Permission.VOICE_MOVE_OTHERS)) {
                event.reply("You are not authorized for this command!");
                return;
            }
        }

        if (!PermissionUtil.checkPermission(event.getSelfMember(), Permission.VOICE_MOVE_OTHERS)) {
            event.reply("The bot does not have the VOICE_MOVE_OTHERS permission!");
            return;
        }

        StringTokenizer tokens = new StringTokenizer(event.getArgs());

        String[] args = event.getArgs().split(" ");

        if(args.length > 2) {
            for (int i = 0; i < args.length; i++) {
                if (i >= 2) {
                    args[1] = args[1] + " " + args[i];
                }
            }
        }

        Member member = null;

        for (Member memb : event.getGuild().getMembers()) {
            if (memb.getUser().getName().equals(args[0]) || memb.getUser().getAsMention().equals(args[0]) || memb.getUser().getId().equals(args[0])) {
                member = memb;
                break;
            }
        }

        if (member == null) {
            event.reply("The specified member does not exist!");
            return;
        }

        //join voice channel, only if bot is not already connected
        if (member.getVoiceState() == null) {
            event.getChannel().sendMessage("There was an error grabbing voice channels!").queue();
            logger.warn(member.getVoiceState().toString());
            return;
        } else if (!member.getVoiceState().inVoiceChannel()) {
            event.getChannel().sendMessage("Specified member is not currently not in a voice channel!").queue();
            return;
        }

        VoiceChannel requestedChannel = null;

        for (VoiceChannel channel : event.getGuild().getVoiceChannels()) {
            if (channel.getName().equalsIgnoreCase(args[1])) {
                requestedChannel = channel;
                break;
            }
        }

        if (requestedChannel == null) {
            event.reply("The specified channel does not exist!");
            return;
        }

        event.getGuild().moveVoiceMember(member, requestedChannel).queue();
        event.reply("Successfully moved member!");
    }
}
