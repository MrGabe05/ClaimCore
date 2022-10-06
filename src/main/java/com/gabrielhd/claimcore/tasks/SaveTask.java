package com.gabrielhd.claimcore.tasks;

import com.gabrielhd.claimcore.claims.Claim;
import com.gabrielhd.claimcore.database.Database;
import com.gabrielhd.claimcore.manager.ClaimManager;

public class SaveTask implements Runnable {

    @Override
    public void run() {
        for(Claim claim : ClaimManager.getClaims().values()) {
            Database.getStorage().saveClaim(claim, false);
        }
    }
}
