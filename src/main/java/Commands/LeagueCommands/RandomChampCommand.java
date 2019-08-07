package Commands.LeagueCommands;

import WebParser.LoLCounterParser;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class RandomChampCommand extends Command {
    LoLCounterParser loLCounterParser = new LoLCounterParser();

    public RandomChampCommand() {
        this.name = "lolrandom";
        this.aliases = new String[]{"randomchampion", "randomchamp", "randchamp"};
        this.help = "Retrieves a random LoL champion.";
        this.arguments = "none";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendTyping().queue();

        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(Color.BLUE);
        eb.setFooter("Brought to you by Daltz333", "https://avatars2.githubusercontent.com/u/10674555?s=460&v=4.png");


        try {
            ArrayList<String> championList = loLCounterParser.getAllChamps();

            String champName = championList.get(1 + (int)(Math.random() * ((championList.size() - 1) + 1)));

            ChampionEmbedBuilder.build(champName, eb);

        } catch (IOException e) {
            eb.setTitle("ERROR!");
            eb.setDescription(e.getMessage());
        }

        event.reply(eb.build());

    }
}
