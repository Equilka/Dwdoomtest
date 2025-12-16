package com.equilka.discordbot.commands;

import com.equilka.discordbot.Bot;
import com.equilka.discordbot.commands.general.downloadable.DownloadCommand;
import com.equilka.discordbot.commands.general.notify.NotifyCommand;
import com.equilka.discordbot.commands.general.quotes.QuoteCommand;
import com.equilka.discordbot.commands.general.random.RandomCommand;
import com.equilka.discordbot.commands.general.random.RollCommand;
import com.equilka.discordbot.data.commands.GuildStats;
import com.equilka.discordbot.commands.general.stats.StatsCommand;
import com.equilka.discordbot.commands.mod.guildconfig.GuildConfigCommand;
import com.equilka.discordbot.commands.mod.guildconfig.ReactionsConfigCommand;
import com.equilka.discordbot.commands.voice.JoinCommand;
import com.equilka.discordbot.commands.voice.LeaveCommand;
import com.equilka.discordbot.commands.voice.PlayCommand;
import com.equilka.discordbot.data.DataRepository;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;
import java.util.stream.Collectors;


public class CommandsManager extends ListenerAdapter {
    private final Map<String, SmartSlashCommand> commandsMap = new HashMap<>();
    private final DataRepository dataRepository;
    private final Bot bot;

    public CommandsManager(Bot bot) {
        this.dataRepository = new DataRepository("guildsData.json");
        this.bot = bot;
    }

    private void putCommand(SmartSlashCommand command, Locale lang) {
        commandsMap.put(command.getName() + lang, command);
    }

    @Override
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();
        updateAllCommands(jda);
    }

    public void updateAllCommands(JDA jda) {
        dataRepository.load();
        List<GuildStats> guilds = dataRepository.getAll(GuildStats.class);
        List<SmartSlashCommand> commands;
        for (Guild guild : jda.getGuilds()) {
            Locale lang;
            Optional<Locale> langOptional = guilds.stream().filter(g -> g.getId().equals(guild.getId())).map(GuildStats::getGuildLanguage).findFirst();
            lang = langOptional.orElse(Locale.US);

            commands = List.of(
                    new NotifyCommand(bot, lang),
                    new StatsCommand(bot, lang),
                    new QuoteCommand(bot, lang),
                    new ReactionsConfigCommand(bot, lang),
                    new GuildConfigCommand(bot, lang),
                    new JoinCommand(bot, lang),
                    new LeaveCommand(bot, lang),
                    new PlayCommand(bot, lang)
                    ,new DownloadCommand(bot, lang)
            );
            guild.updateCommands().addCommands(commands.stream()
                    //.filter(SmartSlashCommand::isGuildCommand)
                    .map(SlashCommand::buildCommandData)
                    .collect(Collectors.toList())).queue();

            commands.forEach(c -> putCommand(c, lang));
        }
        Locale lang = Locale.US;
        commands = List.of(
                new RollCommand(bot, lang),
                new RandomCommand(bot, lang)
                //,new DownloadCommand(bot, lang)
        );
        jda.updateCommands().addCommands(commands.stream()
                //.filter(c -> !c.isGuildCommand())
                .map(SlashCommand::buildCommandData)
                .collect(Collectors.toList())).queue();

        commands.forEach(c -> putCommand(c, lang));
    }

    public void updateGuildsCommands(JDA jda, String id) {
        dataRepository.load();
        List<GuildStats> guilds = dataRepository.getAll(GuildStats.class);
        GuildStats guild = guilds.stream().filter(g -> g.getId().equals(id)).findFirst().get();
        Locale lang = guild.getGuildLanguage();
        List<SmartSlashCommand> commands = List.of(
                new NotifyCommand(bot, lang),
                new StatsCommand(bot, lang),
                new QuoteCommand(bot, lang),
                new ReactionsConfigCommand(bot, lang),
                new GuildConfigCommand(bot, lang),
                new JoinCommand(bot, lang),
                new LeaveCommand(bot, lang),
                new PlayCommand(bot, lang));
        jda.getGuildById(guild.getId()).updateCommands().addCommands(commands.stream()
                .map(SlashCommand::buildCommandData)
                .collect(Collectors.toList())).queue();

        commands.forEach(c -> putCommand(c, lang));
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Locale lang = Locale.US;
        if(event.isGuildCommand()) {
            dataRepository.load();
            List<GuildStats> guilds = dataRepository.getAll(GuildStats.class);
            GuildStats guild = guilds.stream().filter(g -> g.getId().equals(event.getGuild().getId())).findFirst().get();
            lang = guild.getGuildLanguage();
        }
        SmartSlashCommand cmd = commandsMap.get(event.getName() + lang);
        cmd.execute(event);
    }
}
