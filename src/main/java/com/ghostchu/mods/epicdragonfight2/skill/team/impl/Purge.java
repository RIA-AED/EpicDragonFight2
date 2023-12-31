package com.ghostchu.mods.epicdragonfight2.skill.team.impl;

import com.ghostchu.mods.epicdragonfight2.DragonFight;
import com.ghostchu.mods.epicdragonfight2.skill.EpicSkill;
import com.ghostchu.mods.epicdragonfight2.skill.team.AbstractEpicTeamSkill;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Enderman;
import org.jetbrains.annotations.NotNull;

@EpicSkill
public class Purge extends AbstractEpicTeamSkill {
    public Purge(@NotNull DragonFight fight) {
        super(fight, "purge");
    }

    @Override
    public boolean execute(CommandSender executor) {
        if (!isReady()) {
            return false;
        }
        getWorld().getEntitiesByClass(Enderman.class).forEach(e -> e.setHealth(0));
        broadcastUseMessage(executor);
        return true;
    }


}
