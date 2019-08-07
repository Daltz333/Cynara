package Commands.LeagueCommands;

import Constants.Configuration;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;

public class ChampInfoCommand extends Command {
    Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public ChampInfoCommand() {
        this.name = "lolchamp";
        this.aliases = new String[]{"ci"};
        this.help = "Details basic information on the champion.";
        this.arguments = "championName";
        this.category = new Category("League");
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendTyping().queue();

        String championName = event.getArgs();
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(Color.BLUE);

        try {
            ChampionEmbedBuilder.build(championName, eb);

        } catch (IOException e) {
            eb.setTitle(e.getMessage());
            eb.setDescription("There was an error processing your request! Please try a different champion!");
        }

        event.reply(eb.build());
    }
}
