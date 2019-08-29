package Commands.CustomCommands.Subscribers;

import Constants.Configuration;
import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RssLeagueThread implements Runnable {
    private MessageReceivedEvent event;
    private RssReader reader = new RssReader();
    private boolean stop = false;
    private int initTime = (int) System.currentTimeMillis();
    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);
    private String lastKnownTitle = "";

    public RssLeagueThread(MessageReceivedEvent event) {
        this.event = event;
    }

    @Override
    public void run() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setFooter("Riot News");
        builder.setColor(Color.BLUE);

        while (!stop){
            int difference = (int) (System.currentTimeMillis() - initTime);
            if (difference >= 10000) {
                Stream<Item> stream = null;
                ArrayList<Item> items = null;
                initTime = (int) System.currentTimeMillis();
                try {
                    stream = reader.read("https://na.leagueoflegends.com/en/rss.xml");
                    items = (ArrayList<Item>) stream.collect(Collectors.toList());
                    int i = 0;

                    for (Item item : items) {
                        if (!item.getTitle().isPresent() || !item.getDescription().isPresent()) {
                            continue;
                        }

                        //only grab the first one
                        if (i != 0) {
                            break;
                        }

                        i++;

                        System.out.println("Comparing " + lastKnownTitle + " vs " + item.getTitle().get());

                        if (!lastKnownTitle.equalsIgnoreCase(item.getTitle().get())) {
                            lastKnownTitle = item.getTitle().get();

                            Document soup = Jsoup.parse(item.getDescription().get());

                            Element image = soup.select("img").first();

                            if (image == null || image.text() == null) {
                                logger.warn("No image in document?\n" + item.getDescription().get());
                                continue;
                            }

                            String imageUrl = "https://na.leagueoflegends.com/" + image.attr("src");

                            logger.info("Using Image URL: " + imageUrl);

                            builder.setTitle(lastKnownTitle);
                            builder.setImage(imageUrl);
                            event.getChannel().sendMessage(builder.build()).queue();
                            break;
                        } else {
                            System.out.println("RSS not in list");
                        }
                    }

                } catch (IOException e) {
                    logger.error("Unknown exception: ", e);
                }
            }

            //sleep to save cpu time
            try {
                Thread.sleep(2000);
                System.out.println(difference);
            } catch (InterruptedException e) {
                logger.error("Error: ", e);
            }
        }
    }

    public void requestStop() {
        stop = true;
    }
}
