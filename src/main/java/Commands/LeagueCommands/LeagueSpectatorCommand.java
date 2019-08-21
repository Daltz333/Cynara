package Commands.LeagueCommands;

import Constants.Configuration;
import Handlers.RateLimitHandler;
import InternalParser.JsonLol.JsonChampion;
import InternalParser.JsonLoader;
import InternalParser.JsonLol.JsonRune;
import InternalParser.JsonLol.JsonRunePrimary;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.spectator.dto.*;
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
            //iterate over our args, append every arg after region, replace with html space code
            if(args.length > 2) {
                for (int i = 0; i < args.length; i++) {
                    if (i >= 2) {
                        args[1] = args[1] + "%20" + args[i];
                    }
                }
            }

            Summoner summoner = api.getSummonerByName(region, args[1]);

            logger.info("Using summonerID of " + summoner.getAccountId());

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
            eb.addField("Game Length", summonerGame.getGameLength() / 60 + " minutes", false);

            CurrentGameParticipant participant = summonerGame.getParticipantByParticipantId(summoner.getId());

            String currentChampion = "";

            for (JsonChampion jsonChampion : JsonLoader.champions) {
                if (participant.getChampionId() == jsonChampion.getKey()) {
                    currentChampion = jsonChampion.getName();
                    break;
                }
            }

            eb.addField("Playing As", currentChampion, false);

            String perkNames = "";
            for (long perkId : participant.getPerks().getPerkIds()) {
                boolean found = false;

                for (JsonRunePrimary primary : JsonLoader.runesPrimary) {
                    if (primary.getId() == perkId) {
                        found = true;
                        perkNames = perkNames + primary.getName() + " ";
                        break;
                    }
                }

                if (!found) {
                    for (JsonRune secondary : JsonLoader.runesSecondary) {
                        if (secondary.getId() == perkId) {
                            found = true;
                            perkNames = perkNames + secondary.getName() + " ";
                            break;
                        }
                    }
                }

                if (!found) {
                    logger.warn("Unknown Perk ID! " + perkId);
                }
            }

            eb.addField("Runes", perkNames, false);

            String items = "";

            for (GameCustomizationObject gameItems : participant.getGameCustomizationObjects()) {
                items = items + gameItems.toString() + " ";
            }

            eb.addField("Items", "TODO", false);

        } catch (RateLimitException e1) {
            event.reply("Currently being rate limited! Please try again later!");
            RateLimitHandler.setRateLimit(e1.getRetryAfter());
            return;

        } catch (RiotApiException e2) {
            if (e2.getErrorCode() == 404) {
                event.reply("Requested summoner was not found, or is not currently in a game!");
                return;
            }

            logger.error("Exception: ", e2);
        }

        event.reply(eb.build());
    }
}
