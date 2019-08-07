package Commands.CustomCommands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DadBotCustomCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay().toLowerCase();

        double randNumber = Math.random();

        if (randNumber < 0.75) {
            return;
        }

        if (event.getAuthor().isBot()) {
            return;
        }

        if (message.contains("i am ")) {
            int beginLocation = message.indexOf("i am");

            if (beginLocation == -1) {
                return;
            } else {
                beginLocation = beginLocation + 5;
            }

            String response = message.substring(beginLocation);

            event.getChannel().sendMessage("Hi " + response + " my name is Cynara!").queue();
        } else if (message.contains("i'm ")) {
            int beginLocation = message.indexOf("i'm");

            if (beginLocation == -1) {
                return;
            } else {
                beginLocation = beginLocation + 4;
            }

            String response = message.substring(beginLocation);

            event.getChannel().sendMessage("Hi " + response + " my name is Cynara!").queue();
        } else if (message.contains("im ")) {
            int beginLocation = message.indexOf("im");

            if (beginLocation == -1) {
                return;
            } else {
                beginLocation = beginLocation + 3;
            }

            String response = message.substring(beginLocation);

            event.getChannel().sendMessage("Hi " + response + " my name is Cynara!").queue();
        }
    }
}
