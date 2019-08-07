package Commands.SysAdminCommands;

import Constants.Configuration;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import oshi.SystemInfo;

public class SpecsCommand extends Command {
    private SystemInfo info = new SystemInfo();

    public SpecsCommand() {
        this.name = "specs";
        this.ownerCommand = true;
        this.help = "Retrieves the system specs of the host machine.";
        this.arguments = "none";
    }

    @Override
    protected void execute(CommandEvent event) {
        System.out.println("running");
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Configuration.kEmbedColor);
        eb.setFooter(Configuration.kEmbedFooterText, Configuration.kEmbedFooterUrl);
        StringBuilder currentCpuLoad = new StringBuilder();

        eb.addField("Operating System", info.getOperatingSystem().getFamily(), false);
        eb.addField("CPU", info.getHardware().getProcessor().getName() + " " + info.getHardware().getProcessor().getMaxFreq()/1000000000 + "GHz", false);

        eb.addField("CPU Load", Math.round(info.getHardware().getProcessor().getSystemCpuLoad()*100) + "%", false);
        eb.addField("Total Memory", info.getHardware().getMemory().getTotal()/1000000000 + "GB", false);
        eb.addField("Memory Available", info.getHardware().getMemory().getAvailable()/1000000000 + "GB", false);

        event.reply(eb.build());
    }
}
