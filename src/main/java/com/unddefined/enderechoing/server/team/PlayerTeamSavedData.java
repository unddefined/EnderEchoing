package com.unddefined.enderechoing.server.team;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class PlayerTeamSavedData extends SavedData {
    public static final String ID = "enderechoing_player_teams";

    private final Map<UUID, PlayerTeam> teams = new LinkedHashMap<>();

    public PlayerTeamSavedData() {
    }

    public static PlayerTeamSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new Factory<>(PlayerTeamSavedData::new, PlayerTeamSavedData::load), ID);
    }

    public Collection<PlayerTeam> allTeams() {
        return Collections.unmodifiableCollection(teams.values());
    }

    public PlayerTeam teamOf(UUID playerId) {
        for (PlayerTeam team : teams.values()) if (team.isMember(playerId)) return team;
        return null;
    }

    public void addTeam(PlayerTeam team) {
        teams.put(team.teamId(), team);
        setDirty();
    }

    public void removeTeam(UUID teamId) {
        if (teams.remove(teamId) != null) setDirty();
    }

    public static PlayerTeamSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        PlayerTeamSavedData data = new PlayerTeamSavedData();
        ListTag teamsTag = tag.getList("teams", Tag.TAG_COMPOUND);
        for (Tag value : teamsTag) {
            if (!(value instanceof CompoundTag teamTag)) continue;
            UUID teamId = teamTag.getUUID("team_id");
            ListTag membersTag = teamTag.getList("members", Tag.TAG_COMPOUND);
            List<UUID> members = new java.util.ArrayList<>();
            for (Tag memberValue : membersTag) {
                if (memberValue instanceof CompoundTag memberTag)
                    members.add(memberTag.getUUID("uuid"));
            }
            UUID captain = teamTag.contains("captain") ? teamTag.getUUID("captain") : null;
            PlayerTeam team = new PlayerTeam(teamId, members, captain);
            if (teamTag.contains("captain_votes")) {
                ListTag votesTag = teamTag.getList("captain_votes", Tag.TAG_COMPOUND);
                for (Tag voteValue : votesTag) {
                    if (voteValue instanceof CompoundTag voteTag)
                        team.putCaptainVote(voteTag.getUUID("member"), voteTag.getUUID("candidate"));
                }
            }
            data.teams.put(teamId, team);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag teamsTag = new ListTag();
        for (PlayerTeam team : teams.values()) {
            CompoundTag teamTag = new CompoundTag();
            teamTag.putUUID("team_id", team.teamId());
            ListTag membersTag = new ListTag();
            for (UUID member : team.members()) {
                CompoundTag memberTag = new CompoundTag();
                memberTag.putUUID("uuid", member);
                membersTag.add(memberTag);
            }
            teamTag.put("members", membersTag);
            if (team.captain() != null) teamTag.putUUID("captain", team.captain());
            ListTag votesTag = new ListTag();
            for (Map.Entry<UUID, UUID> vote : team.captainVotes().entrySet()) {
                CompoundTag voteTag = new CompoundTag();
                voteTag.putUUID("member", vote.getKey());
                voteTag.putUUID("candidate", vote.getValue());
                votesTag.add(voteTag);
            }
            teamTag.put("captain_votes", votesTag);
            teamsTag.add(teamTag);
        }
        tag.put("teams", teamsTag);
        return tag;
    }

}
