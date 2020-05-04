package Commands.MemeCommands;

import Constants.Configuration;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;

public class EggCommand extends Command {
    public EggCommand() {
        this.name = "eggthou";
        this.help = "EGG THOU WITH UNHOLY WATER";
        this.arguments = "none";
        this.category = new Category("Admin");
    }
    @Override
    protected void execute(CommandEvent event) {
        if (!event.getAuthor().getId().equals(Configuration.kOwnerId)) {
            if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("You are not authorized for this command!");
                return;
            }
        }

        Runnable thread = new Runnable() {
            @Override
            public void run() {
                List<Message> messages = event.getTextChannel().getHistory().retrievePast(50).complete();

                for (Message message : messages) {
                    message.addReaction("\uD83E\uDD5A").queue();
                }
            }
        };

        Thread asyncTask = new Thread(thread);
        asyncTask.setDaemon(true);
        asyncTask.start();
    }
}
