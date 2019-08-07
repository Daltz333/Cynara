package Commands.MemeCommands;

import Constants.Configuration;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubredditReference;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimemeCommand extends Command {
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);
    private RedditClient client;

    public AnimemeCommand(RedditClient redditClient) {
        this.name = "animeme";
        this.aliases = new String[]{"animememe"};
        this.arguments = "none";
        this.help = "Retrieves a random meme from /r/Animemes/";
        this.category = new Category("Fun");

        this.client = redditClient;
    }

    @Override
    protected void execute(CommandEvent event) {
        SubredditReference reference = client.subreddit("Animemes");
        DefaultPaginator<Submission> posts = reference.posts().limit(100).build();

        event.getChannel().sendTyping().queue();

        List<String> images = new ArrayList<>();
        for (Submission post : posts.next()) {
            if (!post.isSelfPost() && (post.getUrl().contains("i.redd.it") || post.getUrl().contains("imgur.com"))) {
                images.add(post.getUrl());
            }
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Configuration.kEmbedColor);
        eb.setTitle("Animemes");
        eb.setFooter(Configuration.kEmbedFooterText, Configuration.kEmbedFooterUrl);

        Random random = new Random();

        if (images.isEmpty()) {
            event.reply("There was an error retrieving images from the reddit!");
            return;
        }

        int index = random.nextInt(images.size());

        eb.setImage(images.get(index));

        event.reply(eb.build());
    }
}
