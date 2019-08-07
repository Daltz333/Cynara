package Commands.LeagueCommands;

import Constants.Configuration;
import Handlers.RateLimitHandler;
import InternalParser.JsonChampion;
import InternalParser.JsonLoader;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.champion.dto.ChampionInfo;
import net.rithms.riot.api.request.ratelimit.RateLimitException;
import net.rithms.riot.constant.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class CurrentRotationCommand extends Command {
    private RiotApi api;
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public CurrentRotationCommand(RiotApi api) {
        this.name = "lolrotation";
        this.help = "Returns the current champion rotation.";
        this.api = api;
        this.arguments = "region";
        this.category = new Category("League");
    }

    @Override
    protected void execute(CommandEvent event) {
        if (RateLimitHandler.isRateLimited()) {
            event.reply("Currently rate limited! Please try again later!");
            return;
        }

        Platform platform;

        switch (event.getArgs().toUpperCase()) {
            case "NA":
                platform = Platform.NA;
                break;
            case "JP":
                platform = Platform.JP;
                break;
            default:
                event.reply("Invalid region! Valid regions are ``NA``, ``JP``");
                return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("LoL Champion Free Rotation");
        eb.setColor(Configuration.kEmbedColor);
        eb.setFooter(Configuration.kEmbedFooterText, Configuration.kEmbedFooterUrl);

        try {
            ChampionInfo rotation = api.getChampionRotations(platform);

            ArrayList<Integer> champIds = new ArrayList<>(rotation.getFreeChampionIds());

            String champNames = "";

            for (Integer champ : champIds) {
                String champName = "";

                for (JsonChampion championData : JsonLoader.champions) {
                    if (champ == championData.getKey()) {
                        champName = championData.getName();
                        break;
                    }
                }

                champNames = champNames + champName + " ";
            }

            eb.addField("Champions", champNames, false);

        } catch (RateLimitException e1) {
            RateLimitHandler.setRateLimit(e1.getRetryAfter());
            event.reply("We are being rate limited! Try again in " + e1.getRetryAfter() + "s.");
            return;
        } catch (RiotApiException e2) {
            logger.error("Exception: ", e2);
            event.reply("An unknown exception has occurred! This has been logged!");
            return;
        }

        event.reply(eb.build());
    }
}
