package com.ghostchu.mods.epicdragonfight2.skill.enemy.impl;

import com.ghostchu.mods.epicdragonfight2.DragonFight;
import com.ghostchu.mods.epicdragonfight2.Stage;
import com.ghostchu.mods.epicdragonfight2.skill.enemy.AbstractEpicDragonSkill;
import com.ghostchu.mods.epicdragonfight2.skill.enemy.SkillEndReason;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WitherShield extends AbstractEpicDragonSkill {
    private final NamespacedKey BOSSBAR_KEY;
    private final KeyedBossBar bossBar;
    private int spawned = 0;

    public WitherShield(@NotNull DragonFight fight) {
        super(fight, "wither-shield");
        BOSSBAR_KEY = new NamespacedKey(fight.getPlugin(), "withersheild");
        bossBar = Bukkit.createBossBar(BOSSBAR_KEY, "???", BarColor.RED, BarStyle.SEGMENTED_6, BarFlag.CREATE_FOG, BarFlag.DARKEN_SKY);
    }

    @Override
    public int start() {
        spawned = 0;
        List<Player> playerList = getPlayerInWorld();
        for (int i = 0; i < Math.min(playerList.size(), 6); i++) {
            Player player = playerList.get(i);
            Wither wither = (Wither) getWorld().spawnEntity(player.getLocation().add(0, 20, 0), EntityType.WITHER);
            wither.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false, false));
            markEntitySummonedByPlugin(wither);
            spawned++;
        }
        return Integer.MAX_VALUE;
    }


    @Override
    public boolean tick() {
        int total = spawned;
        getPlayerInWorld().forEach(bossBar::addPlayer);
        List<Wither> withers = getAllWithers();
        if(withers.size() > total){
            total = withers.size();
        }
        bossBar.setTitle("凋零护盾 - " + withers.size() + " / " + total);
        bossBar.setProgress(Math.min((double) withers.size() / total,1.0f));
        if(getTick() % 200 == 0){
            getPlugin().getLogger().info("[WitherShield] 剩余 "+ withers.size()+" 只凋零");
            withers.forEach(w-> getPlugin().getLogger().info("[WitherShield] - "+ w.getLocation()));
        }
        return withers.isEmpty();
    }

    @EventHandler(ignoreCancelled = true)
    public void dragonAttacked(EntityDamageEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
            event.setDamage(0);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void dragonAttacked(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
            event.setDamage(0);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void dragonAttacked(EntityDamageByBlockEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
            event.setDamage(0);
        }
    }


    public List<Wither> getAllWithers() {
        return getWorld().getEntitiesByClass(Wither.class)
                .stream()
                .filter(this::isMarkedSummonedByPlugin)
                .toList();
    }
//
//    @EventHandler(ignoreCancelled = true)
//    public void witherTargeting(EntityTargetLivingEntityEvent event) {
//        if (!(event.getEntity() instanceof Wither)) {
//            return;
//        }
//        if (!(event.getTarget() instanceof Player)) {
//            // random a target
//            Player player = RandomUtil.randomPick(getPlayerInWorld());
//            if (player == null) {
//                event.setCancelled(true);
//                event.setTarget(null);
//            } else {
//                event.setTarget(player);
//            }
//        }
//    }

    @Override
    public int skillStartWaitingTicks() {
        return 1;
    }

    public static @NotNull Stage[] getAdaptStages() {
        return new Stage[]{Stage.STAGE_3,Stage.STAGE_4};
    }

    @Override
    public void end(@NotNull SkillEndReason var1) {
        getAllWithers().forEach(Entity::remove);
        bossBar.removeAll();
        Bukkit.removeBossBar(bossBar.getKey());
        getPlayerInWorld().forEach(p -> p.playSound(p, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0));
        broadcast("凋零护盾已被击破！");
    }
}