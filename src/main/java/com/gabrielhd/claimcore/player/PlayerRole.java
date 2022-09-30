package com.gabrielhd.claimcore.player;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public class PlayerRole {

    public static PlayerRole OWNER = new PlayerRole(RolePermissions.ALL);
    public static PlayerRole ADMIN = new PlayerRole(RolePermissions.INVITE_PLAYER, RolePermissions.KICK_PLAYER, RolePermissions.CLAIM_CHUNK, RolePermissions.CLOSE_PARTY, RolePermissions.OPEN_PARTY, RolePermissions.UPGRADES_PARTY, RolePermissions.MODIFY_FLAGS);
    public static PlayerRole MOD = new PlayerRole(RolePermissions.INVITE_PLAYER, RolePermissions.KICK_PLAYER);
    public static PlayerRole MEMBER = new PlayerRole();

    private final Set<RolePermissions> permissions;

    public PlayerRole(RolePermissions... permissions) {
        this.permissions = new HashSet<>();
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public boolean hasPermission(RolePermissions permissions) {
        return this.permissions.contains(permissions);
    }

    public enum RolePermissions {
        ALL,
        INVITE_PLAYER,
        KICK_PLAYER,
        DISBAND_PARTY,
        UPGRADES_PARTY,
        CLAIM_CHUNK,
        CLOSE_PARTY,
        OPEN_PARTY,
        MODIFY_FLAGS
    }
}
