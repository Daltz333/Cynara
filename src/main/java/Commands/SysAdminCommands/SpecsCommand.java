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
        this.category = new Category("Owner");
    }

    @Override
    protected void execute(CommandEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Configuration.kEmbedColor);
        eb.setFooter(Configuration.kEmbedFooterText, Configuration.kEmbedFooterUrl);
        StringBuilder currentCpuLoad = new StringBuilder();

        eb.addField("Operating System", info.getOperatingSystem().getFamily(), false);
        eb.addField("CPU", info.getHardware().getProcessor().getName() + " " + info.getHardware().getProcessor().getMaxFreq()/1000000000 + "GHz", false);

        double speed = 0;
        for (long cpuLoad : info.getHardware().getProcessor().getCurrentFreq()) {
            speed = speed + cpuLoad;
        }

        speed = (speed / info.getHardware().getProcessor().getCurrentFreq().length) / 1000000000;
        double maxSpeed = info.getHardware().getProcessor().getMaxFreq() / 1000000000;

        eb.addField("CPU Speed", speed + "GHz/" + maxSpeed + "GHz", false);
        eb.addField("Total Memory", info.getHardware().getMemory().getAvailable()/1000000000 + "GB/" + info.getHardware().getMemory().getTotal()/1000000000 + "GB", false);

        event.reply(eb.build());
    }
}
