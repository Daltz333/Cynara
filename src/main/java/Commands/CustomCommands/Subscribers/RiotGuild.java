package Commands.CustomCommands.Subscribers;

public class RiotGuild {
    private String guildId;
    private String announcementChannelId;
    private boolean newsEnabled;
    private String lastShownTitle;

    public RiotGuild(String guildId, String announcementChannelId, boolean newsEnabled, String lastShownTitle) {
        this.guildId = guildId;
        this.announcementChannelId = announcementChannelId;
        this.newsEnabled = newsEnabled;
        this.lastShownTitle = lastShownTitle;
    }

    public String getGuildId() {
        return this.guildId;
    }

    public String getAnnouncementChannelId() {
        return this.announcementChannelId;
    }

    public boolean newsEnabled() {
        return this.newsEnabled;
    }

    public String getLastShownTitle() {
        return this.lastShownTitle;
    }

    public void setNewsEnabled(boolean enabled) {
        this.newsEnabled = enabled;
    }

    public void setLastShownTitle(String title) {
        this.lastShownTitle = title;
    }

    public void setAnnouncementChannelId(String announcementChannelId) {
        this.announcementChannelId = announcementChannelId;
    }
}
