package com.equilka.discordbot.data.config;

import com.equilka.discordbot.data.BotData;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.Collection;
import java.util.EnumSet;

public class BotConfigData {
    private OnlineStatus status;
    private Collection<GatewayIntent> intents;
    private MemberCachePolicy cachePolicy;
    private ChunkingFilter chunkingFilter;
    private Collection<CacheFlag> cacheFlag;

    public BotConfigData() {
        this.status = OnlineStatus.ONLINE;
        this.intents = EnumSet.allOf(GatewayIntent.class);

        this.chunkingFilter = ChunkingFilter.ALL;
        this.cachePolicy = MemberCachePolicy.ALL;
        this.cacheFlag = EnumSet.allOf(CacheFlag.class);
    }

    public OnlineStatus getStatus() {
        return status;
    }

    public void setStatus(OnlineStatus status) {
        this.status = status;
    }

    public Collection<GatewayIntent> getIntents() {
        return intents;
    }

    public void setIntents(Collection<GatewayIntent> intents) {
        this.intents = intents;
    }

    public ChunkingFilter getChunkingFilter() {
        return chunkingFilter;
    }

    public void setChunkingFilter(ChunkingFilter chunkingFilter) {
        this.chunkingFilter = chunkingFilter;
    }

    public MemberCachePolicy getCachePolicy() {
        return cachePolicy;
    }

    public void setCachePolicy(MemberCachePolicy cachePolicy) {
        this.cachePolicy = cachePolicy;
    }

    public Collection<CacheFlag> getCacheFlag() {
        return cacheFlag;
    }

    public void setCacheFlag(Collection<CacheFlag> cacheFlag) {
        this.cacheFlag = cacheFlag;
    }
}
