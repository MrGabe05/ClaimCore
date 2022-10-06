package com.gabrielhd.claimcore.player;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public enum PlayerRole {

    OWNER(4, RolePermissions.ALL),
    ADMIN(3, RolePermissions.INVITE_PLAYER, RolePermissions.KICK_PLAYER, RolePermissions.CLAIM_CHUNK, RolePermissions.CLOSE_PARTY, RolePermissions.OPEN_PARTY, RolePermissions.UPGRADES_PARTY, RolePermissions.CONTROLE_BALANCE),
    MOD(2, RolePermissions.INVITE_PLAYER, RolePermissions.KICK_PLAYER),
    MEMBER(1);

    private final int prority;
    private final Set<RolePermissions> permissions;

    PlayerRole(int priority, RolePermissions... permissions) {
        this.prority = priority;
        this.permissions = new HashSet<>();
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public boolean hasPermission(RolePermissions permissions) {
        return this.permissions.contains(permissions) || this.permissions.contains(RolePermissions.ALL);
    }

    public enum RolePermissions {
        ALL,
        INVITE_PLAYER,
        KICK_PLAYER,
        CONTROLE_BALANCE,
        UPGRADES_PARTY,
        CLAIM_CHUNK,
        CLOSE_PARTY,
        OPEN_PARTY
    }
}
