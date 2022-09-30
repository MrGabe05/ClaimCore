package com.gabrielhd.claimcore.utils.placeholders;

import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.config.Config;
import com.gabrielhd.claimcore.lang.Lang;
import com.gabrielhd.claimcore.upgrades.Upgrades;
import com.gabrielhd.claimcore.utils.TextPlaceholders;

public class ClaimPlaceholders extends TextPlaceholders {

    public ClaimPlaceholders(Claim claim) {
        this.set("%claim_uuid%", claim.getClaim());
        this.set("%claim_owner%", claim.getOwnerName());
        this.set("%claim_owner_uuid%", claim.getOwner());
        this.set("%claim_amount%", claim.getChunks().size());
        this.set("%claim_members%", claim.getMembers().size());
        this.set("%claim_money%", claim.getMoney());
        this.set("%claim_exp%", claim.getExp());
        this.set("%claim_level%", claim.getLevel());

        for(Upgrades upgrades : Upgrades.values()) {
            String upgradeName = (upgrades.name().replace("_", "").toLowerCase());

            this.set("%upgrade_" + upgradeName + "_level%", claim.getUpgradeLevel(upgrades));
            this.set("%upgrade_" + upgradeName + "_nextlevel%", (claim.getUpgradeLevel(upgrades) + 1));

            this.set("%" + upgradeName + "_limits%", Config.UPGRADES_LIMITS.get(claim.getUpgradeLevel(upgrades)));

            this.set("%next_" + upgradeName + "_price%", (Config.UPGRADES_COSTS.containsKey(claim.getUpgradeLevel(upgrades) + 1) ? Config.UPGRADES_COSTS.get(claim.getUpgradeLevel(upgrades) + 1) : Lang.UPGRADE_MAX_LEVEL_FORMAT.get()));
            this.set("%next_" + upgradeName + "_limits%", Config.UPGRADES_LIMITS.get(claim.getUpgradeLevel(upgrades) + 1));
        }
    }
}
