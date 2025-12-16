package com.equilka.discordbot.commands.mod.guildconfig;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.commands.SmartSlashCommand;
import com.equilka.discordbot.data.commands.GuildStats;
import com.equilka.discordbot.data.DataRepository;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GuildConfigCommand extends SmartSlashCommand {
    public GuildConfigCommand(Bot bot, Locale lang) {
        super(bot, lang);
        this.name = "gconfig";
        this.help = lr.getTranslatable(lang, "command.gconfig.help");
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "type", lr.getTranslatable(lang, "command.gconfig.option.type"), true)
                .addChoice("setQuotesChannel", "quote")
                .addChoice("setGuildLanguage", "language"));
        options.add(new OptionData(OptionType.STRING, "value", lr.getTranslatable(lang, "command.gconfig.option.value"), true));

        this.options = options;
        this.dataRepository = new DataRepository("guildsData.json");
    }

    @Override
    protected void execute(SlashCommandInteractionEvent event) {
        dataRepository.load();

        String type = event.getOption("type").getAsString();
        String value = event.getOption("value").getAsString();

        String guildId = event.getGuild().getId();
        GuildStats stats = dataRepository.getAll(GuildStats.class).stream().filter(g -> g.getId().equals(guildId)).findFirst().get();

        stats = changeConfig(stats, type, value);

        dataRepository.removeById(event.getGuild().getId(), GuildStats.class);
        dataRepository.add(stats);

        dataRepository.save();
        bot.getCommandsManager().updateGuildsCommands(bot.getJda(), guildId);
        event.reply(lr.getTranslatable(lang, "command.gconfig.response.done")).setEphemeral(true).queue();
    }

    private GuildStats changeConfig(GuildStats stats, String type, String value) {
        if (type.equals("quote")) {
            stats.setQuotesChannelId(value);
        } else if (type.equals("language")) {
            String[] values = value.split("_");
            stats.setGuildLanguage(new Locale(values[0], values[1]));
        }
        return stats;
    }
}
