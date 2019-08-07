package Commands.LeagueCommands;

import Constants.Configuration;
import KrawlerObjects.Champion;
import WebParser.LoLCounterParser;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.IOException;

public class ChampionEmbedBuilder {
    private static LoLCounterParser loLCounterParser = new LoLCounterParser();

    public static void build(String championName, EmbedBuilder eb) throws IOException {
        Champion champion = loLCounterParser.retrieveData(championName);

        eb.setTitle(champion.getName() + " Information");
        eb.setDescription(champion.getDescription());
        eb.setImage(champion.getImageURL());
        eb.setFooter(Configuration.kEmbedFooterText, Configuration.kEmbedFooterUrl);
        eb.setColor(Configuration.kEmbedColor);

        StringBuilder weakChamps = new StringBuilder();
        StringBuilder strongChamps = new StringBuilder();

        for (int i = 0; i < champion.getWeakChamps().size(); i++) {
            String champName = champion.getWeakChamps().get(i);

            if (i == champion.getWeakChamps().size() - 1) {
                weakChamps.append(champName);
            } else {
                weakChamps.append(champName).append(", ");
            }

        }

        for (int i = 0; i < champion.getStrongChamps().size(); i++) {
            String champName = champion.getStrongChamps().get(i);

            if (i == champion.getStrongChamps().size() - 1) {
                strongChamps.append(champName);
            } else {
                strongChamps.append(champName).append(", ");
            }

        }

        eb.addField("Lanes: " , champion.getLane(), false);
        eb.addField("Roles: ", champion.getRole(), false);
        eb.addField(champion.getName() + " Strength: ", weakChamps.toString(), false);
        eb.addField(champion.getName() + " Weakness: ", strongChamps.toString(), false);
    }
}
