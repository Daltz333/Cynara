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
import net.rithms.riot.api.endpoints.spectator.dto.*;
import net.rithms.riot.api.endpoints.static_data.dto.Champion;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.api.request.ratelimit.RateLimitException;
import net.rithms.riot.constant.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class LeagueSpectatorCommand extends Command {
    private RiotApi api;
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public LeagueSpectatorCommand(RiotApi api) {
        this.name = "lolmatch";
        this.aliases = new String[]{"lolspectator", "lolspectate"};
        this.category = new Category("League");
        this.help = "Fetches in-match data of a summoner.";
        this.arguments = "region, summonerName";

        this.api = api;
    }

    @Override
    protected void execute(CommandEvent event) {
        StringTokenizer tokens = new StringTokenizer(event.getArgs());

        String[] args = event.getArgs().split(" ");

        Platform region;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Match Information");
        eb.setColor(Configuration.kEmbedColor);
        eb.setFooter(Configuration.kEmbedFooterText, Configuration.kEmbedFooterUrl);

        switch (args[0]) {
            case "NA":
                region = Platform.NA;
                break;
            case "JP":
                region = Platform.JP;
                break;
            default:
                event.reply("Unsupported region! Regions are ``NA, JP``!");
                return;
        }

        if (RateLimitHandler.isRateLimited()) {
            event.reply("Currently being rate limited! Please try again later!");
            return;
        }

        try {
            Summoner summoner = api.getSummonerByName(region, args[1]);
            CurrentGameInfo summonerGame = api.getActiveGameBySummoner(region, summoner.getId());

            if (summonerGame == null) {
                event.reply("Specified user is currently not in a game!");
                return;
            }

            eb.addField("Gamemode", summonerGame.getGameMode() + " " + summonerGame.getGameType(), false);

            String bannedChampions = "";

            for (BannedChampion champ : summonerGame.getBannedChampions()) {
                for (JsonChampion jsonChampion : JsonLoader.champions) {
                    if (champ.getChampionId() == jsonChampion.getKey()) {
                        bannedChampions = bannedChampions + jsonChampion.getName() + " ";
                        break;
                    }
                }
            }

            eb.addField("Banned Champions", bannedChampions, false);
            eb.addField("Game Length", String.valueOf(summonerGame.getGameLength()/60) + " minutes", false);

            CurrentGameParticipant participant = summonerGame.getParticipantByParticipantId(summoner.getId());

            String currentChampion = "";

            for (JsonChampion jsonChampion : JsonLoader.champions) {
                if (participant.getChampionId() == jsonChampion.getKey()) {
                    currentChampion = jsonChampion.getName();
                    break;
                }
            }

            eb.addField("Playing As", currentChampion, false);
            eb.addField("Runes", "TODO", false);

            String items = "";

            for (GameCustomizationObject gameItems : participant.getGameCustomizationObjects()) {
                items = items + gameItems.toString() + " ";
            }

            eb.addField("Items", "TODO", false);

        } catch (RateLimitException e1) {
            event.reply("Currently being rate limited! Please try again later!");
            return;

        } catch (RiotApiException e2) {
            logger.error("Exception: ", e2);
        }

        event.reply(eb.build());
    }
}
