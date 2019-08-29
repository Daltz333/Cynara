package Commands.CustomCommands;

import Constants.Configuration;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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

        EmbedBuilder ebFun = new EmbedBuilder();

        ebFun.setTitle("Fun Commands");
        ebFun.setColor(Configuration.kEmbedColor);
        ebFun.setFooter(Configuration.kEmbedFooterText, Configuration.kEmbedFooterUrl);

        EmbedBuilder ebAdmin = new EmbedBuilder();

        ebAdmin.setTitle("Administrator Commands");
        ebAdmin.setColor(Configuration.kEmbedColor);
        ebAdmin.setFooter(Configuration.kEmbedFooterText, Configuration.kEmbedFooterUrl);

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
                } else if (command.getCategory().getName().equalsIgnoreCase("Fun")) {
                    ebFun.addField(command.getName(), command.getHelp() + " - args: " + command.getArguments(), false);
                } else if (command.getCategory().getName().equalsIgnoreCase("Admin")) {
                    ebAdmin.addField(command.getName(), command.getHelp() + " - args: " + command.getArguments(), false);
                }
            }

            ebAdmin.addField("rssenable", "Enables the league feed to the channel - args: none", false);
            ebAdmin.addField("rssdisable", "Disables the league feed to the channel - args: none", false);

            event.getChannel().sendMessage("Check your DMs " + event.getAuthor().getAsMention()).queue();

            //send to dm
            event.getAuthor().openPrivateChannel().queue(channel -> {
                channel.sendMessage(ebMusic.build()).queue();
                channel.sendMessage(ebLeague.build()).queue();
                channel.sendMessage(ebFun.build()).queue();

                if (event.getMember() == null) {
                    logger.warn("Author is not a member!");
                    return;
                }

                if(event.getAuthor().getId().equalsIgnoreCase(Configuration.kOwnerId) || event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    channel.sendMessage(ebAdmin.build()).queue();
                }

                if (event.getAuthor().getId().equals(Configuration.kOwnerId)) {
                    channel.sendMessage(ebSys.build()).queue();
                }
            });

        }
    }
}
