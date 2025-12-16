package com.equilka.discordbot.model.voice;

import com.equilka.discordbot.data.LanguageRepository;
import com.equilka.discordbot.model.downloadable.DownloadFile;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VoiceManager {
    private final Map<Long, GuildMusicManager> guildManagers = new HashMap<>();
    protected final LanguageRepository lr;
    protected final Locale lang;

    public VoiceManager(LanguageRepository lr, Locale lang) {
        this.lr = lr;
        this.lang = lang;
    }

    public void JoinChannel(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild())
            return;
        if (event.getMember().getUser().isBot())
            return;

        AudioChannelUnion channel = event.getMember().getVoiceState().getChannel();
        if (channel == null) {
            event.reply(lr.getTranslatable(lang, "voice.message.not_in_channel")).setEphemeral(true).queue();
            return;
        }

        Long id = event.getGuild().getIdLong();

        GuildMusicManager guildManager = guildManagers.get(id);
        if (guildManager == null) {
            guildManager = new GuildMusicManager(new DefaultAudioPlayerManager(), channel.asVoiceChannel());
            guildManager.getManager().openAudioConnection(channel);

            guildManagers.put(id, guildManager);
        }
    }

    public void LeaveChannel(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild())
            return;

        AudioChannelUnion channel = event.getMember().getVoiceState().getChannel();
        if (channel == null) {
            event.reply(lr.getTranslatable(lang, "voice.message.not_in_channel")).setEphemeral(true).queue();
            return;
        }

        GuildMusicManager guildManager = guildManagers.get(event.getGuild().getIdLong());
        if (!guildManager.getManager().getConnectedChannel().asVoiceChannel().equals(event.getMember().getVoiceState().getChannel()))
            return;

        guildManager.getManager().closeAudioConnection();

        Long id = event.getGuild().getIdLong();
        guildManagers.remove(id, guildManager);
    }

    public void PlayMusic(SlashCommandInteractionEvent event, String path, InteractionHook interactionHook) {
        if (!event.isFromGuild())
            return;

        AudioChannelUnion channel = event.getMember().getVoiceState().getChannel();
        if (channel == null) {
            interactionHook.deleteOriginal().queue();
            event.reply(lr.getTranslatable(lang, "voice.message.not_in_channel")).setEphemeral(true).queue();
            return;
        }

        GuildMusicManager guildManager = guildManagers.get(event.getGuild().getIdLong());
        if (guildManager == null) {
            JoinChannel(event);
            guildManager = guildManagers.get(event.getGuild().getIdLong());
        }

        if (path == null || path.isEmpty()) {
            interactionHook.editOriginal(lr.getTranslatable(lang, "voice.message.cant_download")).queue();
            return;
        }

        AudioSourceManagers.registerLocalSource(guildManager.getPlayerManager());
        AudioSourceManagers.registerRemoteSources(guildManager.getPlayerManager());

        AudioPlayer player = guildManager.getPlayer();
        event.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));

        TrackScheduler trackScheduler = guildManager.getSchedule();
        guildManager.getPlayerManager().loadItem(path, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                trackScheduler.queue(track);
                interactionHook.editOriginal(lr.getTranslatable(lang, "voice.message.now_playing") + " " + track.getInfo().title).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    trackScheduler.queue(track);
                }
                interactionHook.editOriginal(lr.getTranslatable(lang, "voice.message.tracks_loaded") + " " + playlist.getTracks().size()).queue();
            }

            @Override
            public void noMatches() {
                System.out.println(path + " " + new File(path).exists());
                interactionHook.editOriginal(lr.getTranslatable(lang, "voice.message.not_found")).queue();
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                interactionHook.editOriginal(lr.getTranslatable(lang, "voice.message.error") + " " + throwable.getMessage()).queue();
            }
        });
    }
}
