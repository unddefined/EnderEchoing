package com.unddefined.enderechoing.server.team;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerTeam {
    private final UUID teamId;
    private final List<UUID> members = new ArrayList<>();
    private final Map<UUID, UUID> captainVotes = new LinkedHashMap<>();
    @Nullable
    private UUID captain;

    public PlayerTeam(UUID teamId, List<UUID> members, @Nullable UUID captain) {
        this.teamId = Objects.requireNonNull(teamId);
        this.members.addAll(members);
        this.captain = captain;
    }

    public UUID teamId() {
        return teamId;
    }

    public List<UUID> members() {
        return Collections.unmodifiableList(members);
    }

    @Nullable
    public UUID captain() {
        return captain;
    }

    public boolean isMember(UUID playerId) {
        return members.contains(playerId);
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public void addMember(UUID playerId) {
        if (!members.contains(playerId)) {
            members.add(playerId);
            captainVotes.clear();
        }
    }

    public boolean removeMember(UUID playerId) {
        boolean removed = members.remove(playerId);
        if (removed) {
            captainVotes.remove(playerId);
            captainVotes.clear();
            if (playerId.equals(captain)) captain = null;
        }
        return removed;
    }

    public void setCaptain(@Nullable UUID captain) {
        this.captain = captain;
        captainVotes.clear();
    }

    public Map<UUID, UUID> captainVotes() {
        return Collections.unmodifiableMap(captainVotes);
    }

    public void putCaptainVote(UUID memberId, UUID candidateId) {
        captainVotes.put(memberId, candidateId);
    }

    public void castCaptainVote(UUID memberId, UUID candidateId) {
        captainVotes.put(memberId, candidateId);
    }
}
