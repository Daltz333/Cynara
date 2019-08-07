package Commands.MemeCommands;

import Constants.Configuration;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class InsultCommand extends Command {
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public InsultCommand() {
        this.name = "insult";
        this.arguments = "none";
        this.help = "The bot will insult you.";
        this.category = new Category("Fun");
    }

    @Override
    protected void execute(CommandEvent event) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("insults.txt");

        if (stream == null) {
            logger.error("Unable to locate insults.txt!");
            event.reply("An unknown error has occurred! This has been logged!");
            return;
        }

        Scanner scanner = new Scanner(stream);

        ArrayList<String> insults = new ArrayList<>();

        while (scanner.hasNext()) {
            insults.add(scanner.nextLine());
        }

        Random random = new Random();

        event.reply(insults.get(random.nextInt(insults.size())));
    }
}
