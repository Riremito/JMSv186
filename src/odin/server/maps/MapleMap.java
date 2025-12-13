/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.server.maps;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.ScheduledFuture;
import java.util.Calendar;
import odin.client.inventory.Equip;
import odin.client.inventory.IItem;
import odin.client.inventory.Item;
import odin.constants.GameConstants;
import odin.client.MapleBuffStat;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MaplePet;
import odin.client.status.MonsterStatus;
import odin.client.status.MonsterStatusEffect;
import tacos.wz.data.ReactorWz;
import tacos.wz.data.StringWz;
import tacos.wz.data.StringWz.DropMonsterBook;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;
import tacos.network.MaplePacket;
import tacos.server.ServerOdinGame;
import odin.handling.world.PartyOperation;
import odin.handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCDropPool;
import tacos.packet.response.ResCDropPool.EnterType;
import tacos.packet.response.ResCDropPool.LeaveType;
import tacos.packet.response.ResCAffectedAreaPool;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCUser_Dragon;
import tacos.packet.response.ResCMobPool;
import tacos.packet.response.ResCNpcPool;
import tacos.packet.response.ResCReactorPool;
import tacos.packet.response.ResCUser_Pet;
import tacos.packet.response.ResCSummonedPool;
import tacos.packet.response.ResCUserPool;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.Res_JMS_CInstancePortalPool;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.packet.response.wrapper.WrapCUserLocal;
import tacos.packet.response.wrapper.WrapCUserRemote;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleStatEffect;
import odin.server.Randomizer;
import odin.server.MapleInventoryManipulator;
import odin.server.life.MapleMonster;
import odin.server.life.MapleNPC;
import odin.server.life.MapleLifeFactory;
import odin.server.life.Spawns;
import odin.server.life.SpawnPoint;
import odin.server.life.SpawnPointAreaBoss;
import odin.server.life.MonsterDropEntry;
import odin.server.life.MapleMonsterInformationProvider;
import odin.tools.StringUtil;
import odin.scripting.EventManager;
import odin.server.MapleCarnivalFactory;
import odin.server.MapleCarnivalFactory.MCSkill;
import odin.server.MapleSquad;
import odin.server.SpeedRunner;
import odin.server.Timer.MapTimer;
import odin.server.events.MapleEvent;
import odin.server.maps.MapleNodes.MonsterPoint;
import odin.tools.Pair;
import tacos.server.map.TacosMap;

public final class MapleMap extends TacosMap {

    public MapleMap(int mapid, int channel, int returnMapId, float monsterRate) {
        super(mapid, channel, returnMapId, monsterRate);
    }

    public MapleMap getReturnMap() {
        return ServerOdinGame.getInstance(channel).getMapFactory().getMap(returnMapId);
    }

    public MapleMap getForcedReturnMap() {
        return ServerOdinGame.getInstance(channel).getMapFactory().getMap(forcedReturnMap);
    }

    private int dropFromMonster(final MapleCharacter chr, final MapleMonster mob) {
        if (mob == null || chr == null || ServerOdinGame.getInstance(channel) == null || mob.dropsDisabled() || chr.getPyramidSubway() != null) { //no drops in pyramid ok? no cash either
            return -1;
        }

        //We choose not to readLock for this.
        //This will not affect the internal state, and we don't want to
        //introduce unneccessary locking, especially since this function
        //is probably used quite often.
        if (mapobjects.get(MapleMapObjectType.ITEM).size() >= 225) {
            removeDrops();
        }

        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final byte droptype = (byte) (mob.getStats().isExplosiveReward() ? 3 : mob.getStats().isFfaLoot() ? 2 : chr.getParty() != null ? 1 : 0);
        final int mobpos = mob.getPosition().x, cmServerrate = ServerOdinGame.getInstance(channel).getMesoRate(), chServerrate = ServerOdinGame.getInstance(channel).getDropRate();
        IItem idrop;
        byte d = 1;
        Point pos = new Point(0, mob.getPosition().y);
        double showdown = 100.0;
        final MonsterStatusEffect mse = mob.getBuff(MonsterStatus.SHOWDOWN);
        if (mse != null) {
            showdown += mse.getX();
        }

        final MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
        final List<MonsterDropEntry> dropEntry = mi.retrieveDrop(mob.getId());
        Collections.shuffle(dropEntry);

        final boolean forced_drop = mob.getStats().isBoss();

        int dropped_count = 0;

        // この辺でドロップ確定させるMobのIDのチェック処理を入れる
        for (final MonsterDropEntry de : dropEntry) {
            if (de.itemId == mob.getStolen()) {
                continue;
            }

            // モンスターカード
            if (GameConstants.isMonsterCard(de.itemId)) {
                if (chr.getMonsterBook().getLevel(de.itemId) >= 5) {
                    continue;
                }
            }

            // ボスは無条件でドロップ確定, 通常Mobはx/1000の確率でDBの値を参照してドロップする
            if (forced_drop || (Math.floor(Math.random() * 1000) < (int) (de.chance * chServerrate * chr.getDropMod() * (chr.getStat().dropBuff / 100.0) * (showdown / 100.0)))) {
                if (droptype == 3) {
                    pos.x = (mobpos + (d % 2 == 0 ? (40 * (d + 1) / 2) : -(40 * (d / 2))));
                } else {
                    pos.x = (mobpos + ((d % 2 == 0) ? (25 * (d + 1) / 2) : -(25 * (d / 2))));
                }
                // メル
                if (de.itemId == 0) {
                    int mesos = de.Minimum;
                    if (de.Maximum > de.Minimum) {
                        mesos = Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum;
                    }

                    if (mesos > 0) {
                        spawnMobMesoDrop((int) (mesos * (chr.getStat().mesoBuff / 100.0) * chr.getDropMod() * cmServerrate), calcDropPos(pos, mob.getPosition()), mob, chr, false, droptype);
                        dropped_count++;
                    }
                } else {
                    // 装備
                    if (GameConstants.getInventoryType(de.itemId) == MapleInventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        // 通常アイテム
                        final int range = Math.abs(de.Maximum - de.Minimum);
                        idrop = new Item(de.itemId, (byte) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(range <= 0 ? 1 : range) + de.Minimum : 1), (byte) 0);
                    }
                    spawnMobDrop(idrop, calcDropPos(pos, mob.getPosition()), mob, chr, droptype, de.questid);
                    dropped_count++;
                }
                d++;
            }
        }

        // drop data in DB
        if (dropEntry.size() != 0) {
            return dropped_count;
        }
        int meso_value = (Randomizer.nextInt(100) < 50) ? 0 : 10 + Randomizer.nextInt(150);

        if (droptype == 3) {
            pos.x = (mobpos + (dropped_count % 2 == 0 ? (40 * (dropped_count + 1) / 2) : -(40 * (dropped_count / 2))));
        } else {
            pos.x = (mobpos + ((dropped_count % 2 == 0) ? (25 * (dropped_count + 1) / 2) : -(25 * (dropped_count / 2))));
        }

        if (0 < meso_value) {
            spawnMobMesoDrop(meso_value, calcDropPos(pos, mob.getPosition()), mob, chr, false, droptype);
            dropped_count++;
        }

        if (!StringWz.checkBookAvailable()) {
            return dropped_count;
        }

        // load from monster book
        DropMonsterBook book_info = StringWz.getMonseterBookDrop(mob.getId());
        for (int item_id : book_info.drop_ids) {
            int type = item_id / 1000000;
            // 装備 3%
            if (type == 1) {
                if (!(Randomizer.nextInt(100) < 3)) {
                    continue;
                }
            }
            // 消費 5%
            if (type == 2) {
                if (!(Randomizer.nextInt(100) < 5)) {
                    continue;
                }
            }
            // Setup 5%
            if (type == 3) {
                if (!(Randomizer.nextInt(100) < 5)) {
                    continue;
                }
            }
            // ETC 50%
            if (type == 4) {
                if (book_info.drop_ids.get(0) == item_id) {
                    if (!(Randomizer.nextInt(100) < 50)) {
                        continue;
                    }
                } else {
                    // ETC 5%
                    if (!(Randomizer.nextInt(100) < 5)) {
                        continue;
                    }
                }
            }
            if (GameConstants.getInventoryType(item_id) == MapleInventoryType.EQUIP) {
                idrop = ii.randomizeStats((Equip) ii.getEquipById(item_id));
            } else {
                idrop = new Item(item_id, (byte) 0, (short) 1, (byte) 0);
            }
            if (droptype == 3) {
                pos.x = (mobpos + (dropped_count % 2 == 0 ? (40 * (dropped_count + 1) / 2) : -(40 * (dropped_count / 2))));
            } else {
                pos.x = (mobpos + ((dropped_count % 2 == 0) ? (25 * (dropped_count + 1) / 2) : -(25 * (dropped_count / 2))));
            }
            spawnMobDrop(idrop, calcDropPos(pos, mob.getPosition()), mob, chr, droptype, (short) 0);
            dropped_count++;
        }

        return dropped_count;
    }

    public void removeMonster(final MapleMonster monster) {
        spawnedMonstersOnMap.decrementAndGet();
        broadcastMessage(ResCMobPool.Kill(monster, 0));
        removeMapObject(monster);
    }

    private void killMonster(final MapleMonster monster) { // For mobs with removeAfter
        spawnedMonstersOnMap.decrementAndGet();
        monster.setHp(0);
        monster.spawnRevives(this);
        broadcastMessage(ResCMobPool.Kill(monster, 1));
        removeMapObject(monster);
    }

    public final void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops, final boolean second, byte animation) {
        killMonster(monster, chr, withDrops, second, animation, 0);
    }

    public final void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops, final boolean second, byte animation, final int lastSkill) {
        if ((monster.getId() == 8810122 || monster.getId() == 8810018) && !second) {
            MapTimer.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    killMonster(monster, chr, true, true, (byte) 1);
                    killAllMonsters(true);
                }
            }, 3000);
            return;
        }
        if (monster.getId() == 8820014) { //pb sponge, kills pb(w) first before dying
            killMonster(8820000);
        } else if (monster.getId() == 9300166) { //ariant pq bomb
            animation = 4; //or is it 3?
        }
        spawnedMonstersOnMap.decrementAndGet();
        removeMapObject(monster);
        int dropOwner = monster.killBy(chr, lastSkill);
        broadcastMessage(ResCMobPool.Kill(monster, animation));

        if (monster.getBuffToGive() > -1) {
            final int buffid = monster.getBuffToGive();
            final MapleStatEffect buff = MapleItemInformationProvider.getInstance().getItemEffect(buffid);

            charactersLock.readLock().lock();
            try {
                for (final MapleCharacter mc : characters) {
                    if (mc.isAlive()) {
                        buff.applyTo(mc);

                        switch (monster.getId()) {
                            case 8810018:
                            case 8810122:
                            case 8820001:
                                mc.SendPacket(WrapCUserLocal.EffectLocal(OpsUserEffect.UserEffect_BuffItemEffect, buffid)); // HT nine spirit
                                broadcastMessage(mc, WrapCUserRemote.EffectRemote(OpsUserEffect.UserEffect_BuffItemEffect, mc, buffid), false); // HT nine spirit
                                break;
                        }
                    }
                }
            } finally {
                charactersLock.readLock().unlock();
            }
        }
        final int mobid = monster.getId();
        SpeedRunType type = SpeedRunType.NULL;
        final MapleSquad sqd = getSquadByMap();
        if (mobid == 8810018) { // Horntail
            World.Broadcast.broadcastMessage(ResWrapper.BroadCastMsgNotice("大変な挑戦の終わりにホーンテイルを撃破した遠征隊よ！貴方達が本当のリプレの英雄だ！").getBytes());
            if (mapid == 240060200) {
                for (MapleCharacter c : getCharacters()) {
                    c.finishAchievement(16);
                }
                if (speedRunStart > 0) {
                    type = SpeedRunType.Horntail;
                }
                if (sqd != null) {
                    doShrine(true);
                }
            }
        } else if (mobid == 8810122 && mapid == 240060201) { // Horntail
            World.Broadcast.broadcastMessage(ResWrapper.BroadCastMsgNotice("To the crew that have finally conquered Chaos Horned Tail after numerous attempts, I salute thee! You are the true heroes of Leafre!!").getBytes());
            for (MapleCharacter c : getCharacters()) {
                c.finishAchievement(24);
            }
            if (speedRunStart > 0) {
                type = SpeedRunType.ChaosHT;
            }
            if (sqd != null) {
                doShrine(true);
            }
        } else if (mobid == 8500002) {
            if (mapid == 220080001) {
                if (speedRunStart > 0) {
                    type = SpeedRunType.Papulatus;
                }
            }
        } else if (mobid == 9400266 && mapid == 802000111) {
            if (speedRunStart > 0) {
                type = SpeedRunType.Nameless_Magic_Monster;
            }
            if (sqd != null) {
                doShrine(true);
            }
        } else if (mobid == 9400265 && mapid == 802000211) {
            if (speedRunStart > 0) {
                type = SpeedRunType.Vergamot;
            }
            if (sqd != null) {
                doShrine(true);
            }
        } else if (mobid == 9400270 && mapid == 802000411) {
            if (speedRunStart > 0) {
                type = SpeedRunType.Dunas;
            }
            if (sqd != null) {
                doShrine(true);
            }
        } else if (mobid == 9400273 && mapid == 802000611) {
            if (speedRunStart > 0) {
                type = SpeedRunType.Nibergen;
            }
            if (sqd != null) {
                doShrine(true);
            }
        } else if (mobid == 9400294 && mapid == 802000711) {
            if (speedRunStart > 0) {
                type = SpeedRunType.Dunas_2;
            }
            if (sqd != null) {
                doShrine(true);
            }
        } else if (mobid == 9400296 && mapid == 802000803) {
            if (speedRunStart > 0) {
                type = SpeedRunType.Core_Blaze;
            }
            if (sqd != null) {
                doShrine(true);
            }
        } else if (mobid == 9400289 && mapid == 802000821) {
            if (speedRunStart > 0) {
                type = SpeedRunType.Aufhaven;
            }
            if (sqd != null) {
                doShrine(true);
            }
        } else if ((mobid == 9420549 || mobid == 9420544) && mapid == 551030200) {
            if (speedRunStart > 0) {
                if (mobid == 9420549) {
                    type = SpeedRunType.Scarlion;
                } else {
                    type = SpeedRunType.Targa;
                }
            }
            //INSERT HERE: 2095_tokyo
        } else if (mobid == 8820001) {
            World.Broadcast.broadcastMessage(ResWrapper.BroadCastMsgNotice("不屈の闘志でピンクビーンを退けた遠征隊の諸君！　君たちが真の時間の覇者だ！").getBytes());
            if (mapid == 270050100) {
                for (MapleCharacter c : getCharacters()) {
                    c.finishAchievement(17);
                }
                if (speedRunStart > 0) {
                    type = SpeedRunType.Pink_Bean;
                }
                if (sqd != null) {
                    doShrine(true);
                }
            }
        } else if (mobid == 8800002) {
            if (mapid == 280030000) {
                for (MapleCharacter c : getCharacters()) {
                    c.finishAchievement(15);
                }
                if (speedRunStart > 0) {
                    type = SpeedRunType.Zakum;
                }
                if (sqd != null) {
                    doShrine(true);
                }
            }
        } else if (mobid == 8800102 && mapid == 280030001) {
            for (MapleCharacter c : getCharacters()) {
                c.finishAchievement(23);
            }
            if (speedRunStart > 0) {
                type = SpeedRunType.Chaos_Zakum;
            }

            if (sqd != null) {
                doShrine(true);
            }
        } else if (mobid >= 8800003 && mobid <= 8800010) {
            boolean makeZakReal = true;
            final Collection<MapleMonster> monsters = getAllMonsters();

            for (final MapleMonster mons : monsters) {
                if (mons.getId() >= 8800003 && mons.getId() <= 8800010) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (final MapleMapObject object : monsters) {
                    final MapleMonster mons = ((MapleMonster) object);
                    if (mons.getId() == 8800000) {
                        final Point pos = mons.getPosition();
                        this.killAllMonsters(true);
                        spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800000), pos);
                        break;
                    }
                }
            }
        } else if (mobid >= 8800103 && mobid <= 8800110) {
            boolean makeZakReal = true;
            final Collection<MapleMonster> monsters = getAllMonsters();

            for (final MapleMonster mons : monsters) {
                if (mons.getId() >= 8800103 && mons.getId() <= 8800110) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (final MapleMonster mons : monsters) {
                    if (mons.getId() == 8800100) {
                        final Point pos = mons.getPosition();
                        this.killAllMonsters(true);
                        spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800100), pos);
                        break;
                    }
                }
            }
        }
        if (type != SpeedRunType.NULL) {
            if (speedRunStart > 0 && speedRunLeader.length() > 0) {
                long endTime = System.currentTimeMillis();
                String time = StringUtil.getReadableMillis(speedRunStart, endTime);
                broadcastMessage(ResWrapper.BroadCastMsgEvent(speedRunLeader + "'s squad has taken " + time + " to defeat " + type + "!"));
                getRankAndAdd(speedRunLeader, time, type, (endTime - speedRunStart), (sqd == null ? null : sqd.getMembers()));
                endSpeedRun();
            }

        }
        if (mobid == 8820008) { //wipe out statues and respawn
            for (final MapleMapObject mmo : getAllMonsters()) {
                MapleMonster mons = (MapleMonster) mmo;
                if (mons.getLinkOid() != monster.getObjectId()) {
                    killMonster(mons, chr, false, false, animation);
                }
            }
        } else if (mobid >= 8820010 && mobid <= 8820014) {
            for (final MapleMapObject mmo : getAllMonsters()) {
                MapleMonster mons = (MapleMonster) mmo;
                if (mons.getId() != 8820000 && mons.getObjectId() != monster.getObjectId() && mons.getLinkOid() != monster.getObjectId()) {
                    killMonster(mons, chr, false, false, animation);
                }
            }
        }
        if (withDrops) {
            MapleCharacter drop = null;
            if (dropOwner <= 0) {
                drop = chr;
            } else {
                drop = getCharacterById(dropOwner);
                if (drop == null) {
                    drop = chr;
                }
            }
            dropFromMonster(drop, monster);
        }
    }

    public final void killAllMonsters(final boolean animate) {
        for (final MapleMapObject monstermo : getAllMonsters()) {
            final MapleMonster monster = (MapleMonster) monstermo;
            spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0);
            broadcastMessage(ResCMobPool.Kill(monster, animate ? 1 : 0));
            removeMapObject(monster);
        }
    }

    public final void killMonster(final int monsId) {
        for (final MapleMapObject mmo : getAllMonsters()) {
            if (((MapleMonster) mmo).getId() == monsId) {
                spawnedMonstersOnMap.decrementAndGet();
                removeMapObject(mmo);
                broadcastMessage(ResCMobPool.Kill((MapleMonster) mmo, 1));
                break;
            }
        }
    }

    public final void destroyReactor(final int oid) {
        final MapleReactor reactor = getReactorByOid(oid);
        broadcastMessage(ResCReactorPool.Destroy(reactor));
        reactor.setAlive(false);
        removeMapObject(reactor);
        reactor.setTimerActive(false);

        if (reactor.getDelay() > 0) {
            MapTimer.getInstance().schedule(new Runnable() {

                @Override
                public final void run() {
                    respawnReactor(reactor);
                }
            }, reactor.getDelay());
        }
    }

    public final void reloadReactors() {
        List<MapleReactor> toSpawn = new ArrayList<MapleReactor>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final MapleReactor reactor = (MapleReactor) obj;
                broadcastMessage(ResCReactorPool.Destroy(reactor));
                reactor.setAlive(false);
                reactor.setTimerActive(false);
                toSpawn.add(reactor);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor r : toSpawn) {
            removeMapObject(r);
            if (r.getReactorId() != 9980000 && r.getReactorId() != 9980001) { //guardians cpq
                respawnReactor(r);
            }
        }
    }

    /**
     * Automagically finds a new controller for the given monster from the chars
     * on the map...
     *
     * @param monster
     */
    public final void updateMonsterController(final MapleMonster monster) {
        if (!monster.isAlive()) {
            return;
        }
        if (monster.getController() != null) {
            if (monster.getController().getMap() != this) {
                monster.getController().stopControllingMonster(monster);
            } else { // Everything is fine :)
                return;
            }
        }
        int mincontrolled = -1;
        MapleCharacter newController = null;

        charactersLock.readLock().lock();
        try {
            final Iterator<MapleCharacter> ltr = characters.iterator();
            MapleCharacter chr;
            while (ltr.hasNext()) {
                chr = ltr.next();
                if (!chr.isHidden() && !chr.isClone() && (chr.getControlledSize() < mincontrolled || mincontrolled == -1)) {
                    mincontrolled = chr.getControlledSize();
                    newController = chr;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        if (newController != null) {
            if (monster.isFirstAttack()) {
                newController.controlMonster(monster, true);
                monster.setControllerHasAggro(true);
                monster.setControllerKnowsAboutAggro(true);
            } else {
                newController.controlMonster(monster, false);
            }
        }
    }

    public final void spawnNpc(final int id, final Point pos) {
        final MapleNPC npc = MapleLifeFactory.getNPC(id);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setFh(getFootholds().findBelow(pos).getId());
        npc.setCustom(true);
        addMapObject(npc);
        broadcastMessage(ResCNpcPool.NpcEnterField(npc, true));
    }

    public final void removeNpc(final int npcid) {
        mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC npc = (MapleNPC) itr.next();
                if (npc.isCustom() && npc.getId() == npcid) {
                    broadcastMessage(ResCNpcPool.NpcLeaveField(npc));
                    itr.remove();
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().unlock();
        }
    }

    public final void spawnMonster_sSack(final MapleMonster mob, final Point pos, final int spawnType) {
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mob.setPosition(spos);
        spawnMonster(mob, spawnType);
    }

    public final void spawnMonsterOnGroundBelow(final MapleMonster mob, final Point pos) {
        spawnMonster_sSack(mob, pos, -2);
    }

    public final int spawnMonsterWithEffectBelow(final MapleMonster mob, final Point pos, final int effect) {
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        return spawnMonsterWithEffect(mob, effect, spos);
    }

    public final void spawnZakum(final int x, final int y) {
        final Point pos = new Point(x, y);
        final MapleMonster mainb = MapleLifeFactory.getMonster(8800000);
        final Point spos = calcPointBelow(new Point(pos.x, pos.y));
        mainb.setPosition(spos);
        mainb.setFake(true);
        // Might be possible to use the map object for reference in future.
        spawnFakeMonster(mainb);
        final int[] zakpart = {8800003, 8800004, 8800005, 8800006, 8800007,
            8800008, 8800009, 8800010};
        for (final int i : zakpart) {
            final MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);
            spawnMonster(part, -2);
        }
        if (squadSchedule != null) {
            cancelSquadSchedule();
            broadcastMessage(ResCField.stopClock());
        }
    }

    public final void spawnChaosZakum(final int x, final int y) {
        final Point pos = new Point(x, y);
        final MapleMonster mainb = MapleLifeFactory.getMonster(8800100);
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);

        // Might be possible to use the map object for reference in future.
        spawnFakeMonster(mainb);

        final int[] zakpart = {8800103, 8800104, 8800105, 8800106, 8800107,
            8800108, 8800109, 8800110};

        for (final int i : zakpart) {
            final MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);

            spawnMonster(part, -2);
        }
        if (squadSchedule != null) {
            cancelSquadSchedule();
            broadcastMessage(ResCField.stopClock());
        }
    }

    public final void spawnFakeMonsterOnGroundBelow(final MapleMonster mob, final Point pos) {
        Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        spos.y -= 1;
        mob.setPosition(spos);
        spawnFakeMonster(mob);
    }

    private void checkRemoveAfter(final MapleMonster monster) {
        final int ra = monster.getStats().getRemoveAfter();

        if (ra > 0) {
            MapTimer.getInstance().schedule(new Runnable() {

                @Override
                public final void run() {
                    if (monster != null && monster == getMapObject(monster.getObjectId(), monster.getType())) {
                        killMonster(monster);
                    }
                }
            }, ra * 1000);
        }
    }

    public void spawnRevives(MapleMonster monster, int oid) {
        monster.setMap(this);
        checkRemoveAfter(monster);
        monster.setLinkOid(oid);
        addMapObject(monster);
        spawnRangedMapObject(monster, ResCMobPool.Spawn(monster, -3, 0, oid));
        updateMonsterController(monster);
        spawnedMonstersOnMap.incrementAndGet();
    }

    public void spawnMonster(MapleMonster monster, int spawnType) {
        monster.setMap(this);
        checkRemoveAfter(monster);
        addMapObject(monster);
        spawnRangedMapObject(monster, ResCMobPool.Spawn(monster, spawnType, 0, 0));
        updateMonsterController(monster);
        spawnedMonstersOnMap.incrementAndGet();
    }

    public int spawnMonsterWithEffect(MapleMonster monster, int effect, Point pos) {
        monster.setMap(this);
        monster.setPosition(pos);
        addMapObject(monster);
        spawnRangedMapObject(monster, ResCMobPool.Spawn(monster, -2, effect, 0));
        updateMonsterController(monster);
        spawnedMonstersOnMap.incrementAndGet();
        return monster.getObjectId();
    }

    public void spawnFakeMonster(MapleMonster monster) {
        monster.setMap(this);
        monster.setFake(true);
        addMapObject(monster);
        spawnRangedMapObject(monster, ResCMobPool.Spawn(monster, -4, 0, 0));
        updateMonsterController(monster);
        spawnedMonstersOnMap.incrementAndGet();
    }

    public void spawnReactor(MapleReactor reactor) {
        reactor.setMap(this);
        addMapObject(reactor);
        spawnRangedMapObject(reactor, ResCReactorPool.Spawn(reactor));
    }

    private void respawnReactor(final MapleReactor reactor) {
        reactor.setState((byte) 0);
        reactor.setAlive(true);
        spawnReactor(reactor);
    }

    public void spawnDoor(final MapleDoor door) {
        DebugLogger.DebugLog("Spawn Door : " + door.getMapId());
        addMapObject(door);
        spawnRangedMapObject(door, null);
    }

    public void spawnDynamicPortal(MapleDynamicPortal dynamic_portal) {
        addMapObject(dynamic_portal);
        spawnRangedMapObject(dynamic_portal, Res_JMS_CInstancePortalPool.CreatePinkBeanEventPortal(dynamic_portal));
    }

    public void spawnSummon(MapleSummon summon) {
        summon.updateMap(this);
        addMapObject(summon);
        spawnRangedMapObject(summon, ResCSummonedPool.spawnSummon(summon, true));
    }

    public void spawnDragon(MapleDragon dragon) {
        addMapObject(dragon);
        spawnRangedMapObject(dragon, ResCUser_Dragon.spawnDragon(dragon));
    }

    public void spawnMist(MapleMist mist, int duration, boolean fake) {
        addMapObject(mist);
        spawnRangedMapObject(mist, ResCAffectedAreaPool.spawnMist(mist));

        final MapTimer tMan = MapTimer.getInstance();
        final ScheduledFuture<?> poisonSchedule;
        switch (mist.isPoisonMist()) {
            case 1:
                //poison: 0 = none, 1 = poisonous, 2 = recovery aura
                final MapleCharacter owner = getCharacterById(mist.getOwnerId());
                poisonSchedule = tMan.register(new Runnable() {

                    @Override
                    public void run() {
                        for (final MapleMapObject mo : getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.MONSTER))) {
                            if (mist.makeChanceResult()) {
                                ((MapleMonster) mo).applyStatus(owner, new MonsterStatusEffect(MonsterStatus.POISON, 1, mist.getSourceSkill().getId(), null, false), true, duration, false);
                            }
                        }
                    }
                }, 2000, 2500);
                break;
            case 2:
                poisonSchedule = tMan.register(new Runnable() {

                    @Override
                    public void run() {
                        for (final MapleMapObject mo : getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER))) {
                            if (mist.makeChanceResult()) {
                                final MapleCharacter chr = ((MapleCharacter) mo);
                                chr.addMP((int) (mist.getSource().getX() * (chr.getStat().getMaxMp() / 100.0)));
                            }
                        }
                    }
                }, 2000, 2500);
                break;
            default:
                poisonSchedule = null;
                break;
        }
        tMan.schedule(new Runnable() {

            @Override
            public void run() {
                broadcastMessage(ResCAffectedAreaPool.removeMist(mist));
                removeMapObject(mist);
                if (poisonSchedule != null) {
                    poisonSchedule.cancel(false);
                }
            }
        }, duration);
    }

    public final void disappearingItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final IItem item, final Point pos) {
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte) 1, false);
        broadcastMessage(ResCDropPool.DropEnterField(drop, EnterType.SPAWN, droppos, dropper.getPosition()), drop.getPosition());
    }

    public void spawnMesoDrop(int meso, Point position, MapleMapObject dropper, MapleCharacter owner, boolean playerDrop, byte droptype) {
        Point droppos = calcDropPos(position, position);
        MapleMapItem mdrop = new MapleMapItem(meso, droppos, dropper, owner, droptype, playerDrop);
        addMapObject(mdrop);
        spawnRangedMapObject(mdrop, ResCDropPool.DropEnterField(mdrop, EnterType.ANIMATION, droppos, dropper.getPosition()));

        if (!everlast) {
            mdrop.registerExpire(120000);
            if (droptype == 0 || droptype == 1) {
                mdrop.registerFFA(30000);
            }
        }
    }

    public void spawnMobMesoDrop(int meso, Point position, MapleMapObject dropper, MapleCharacter owner, boolean playerDrop, byte droptype) {
        MapleMapItem mdrop = new MapleMapItem(meso, position, dropper, owner, droptype, playerDrop);
        addMapObject(mdrop);
        spawnRangedMapObject(mdrop, ResCDropPool.DropEnterField(mdrop, EnterType.ANIMATION, position, dropper.getPosition()));
        mdrop.registerExpire(120000);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000);
        }
    }

    public void spawnMobDrop(IItem idrop, Point dropPos, MapleMonster mob, MapleCharacter chr, byte droptype, short questid) {
        MapleMapItem mdrop = new MapleMapItem(idrop, dropPos, mob, chr, droptype, false, questid);
        addMapObject(mdrop);
        spawnRangedMapObject(mdrop, ResCDropPool.DropEnterField(mdrop, EnterType.ANIMATION, dropPos, mob.getPosition(), mob.getObjectId()));
        mdrop.registerExpire(120000);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000);
        }
        activateItemReactors(mdrop, chr.getClient());
    }

    public void spawnRandDrop() {
        // removed random code.
        return;
    }

    public void spawnAutoDrop(int itemid, Point pos) {
        IItem idrop = null;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) {
            idrop = ii.randomizeStats((Equip) ii.getEquipById(itemid));
        } else {
            idrop = new Item(itemid, (byte) 0, (short) 1, (byte) 0);
        }
        MapleMapItem mdrop = new MapleMapItem(pos, idrop);
        addMapObject(mdrop);
        spawnRangedMapObject(mdrop, ResCDropPool.DropEnterField(mdrop, EnterType.ANIMATION, pos, pos));
        broadcastMessage(ResCDropPool.DropEnterField(mdrop, EnterType.PICK_UP_ENABLED, pos, pos));
        mdrop.registerExpire(120000);
    }

    public void spawnItemDrop(MapleMapObject dropper, MapleCharacter owner, IItem item, Point pos, boolean ffaDrop, boolean playerDrop) {
        Point droppos = calcDropPos(pos, pos);
        MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte) 2, playerDrop);
        addMapObject(drop);
        spawnRangedMapObject(drop, ResCDropPool.DropEnterField(drop, EnterType.ANIMATION, droppos, dropper.getPosition()));
        broadcastMessage(ResCDropPool.DropEnterField(drop, EnterType.PICK_UP_ENABLED, droppos, dropper.getPosition())); // enable pick up for new players
        if (!everlast) {
            drop.registerExpire(120000);
            activateItemReactors(drop, owner.getClient());
        }
    }

    public final void returnEverLastItem(final MapleCharacter chr) {
        for (final MapleMapObject o : getAllItems()) {
            final MapleMapItem item = ((MapleMapItem) o);
            if (item.getOwner() == chr.getId()) {
                item.setPickedUp(true);

                DebugLogger.DebugLog("PICKUP REVER");
                broadcastMessage(ResCDropPool.DropLeaveField(item, LeaveType.PICK_UP, chr, 0), item.getPosition());
                if (item.getMeso() > 0) {
                    chr.gainMeso(item.getMeso(), false);
                } else {
                    MapleInventoryManipulator.addFromDrop(chr.getClient(), item.getItem(), false);
                }
                removeMapObject(item);
            }
        }
        spawnRandDrop();
    }

    public final void talkMonster(final String msg, final int itemId, final int objectid) {
        if (itemId > 0) {
            startMapEffect(msg, itemId, false);
        }
        broadcastMessage(ResCMobPool.talkMonster(objectid, itemId, msg)); //5120035
        broadcastMessage(ResCMobPool.removeTalkMonster(objectid));
    }

    public final void startMapEffect(final String msg, final int itemId) {
        startMapEffect(msg, itemId, false);
    }

    public final void startMapEffect(final String msg, final int itemId, final boolean jukebox) {
        if (mapEffect != null) {
            return;
        }
        mapEffect = new MapleMapEffect(msg, itemId);
        mapEffect.setJukebox(jukebox);
        broadcastMessage(mapEffect.makeStartData());
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                broadcastMessage(mapEffect.makeDestroyData());
                mapEffect = null;
            }
        }, jukebox ? 300000 : 30000);
    }

    public final void startExtendedMapEffect(final String msg, final int itemId) {
        broadcastMessage(ResCField.BlowWeather(msg, itemId, true));
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                broadcastMessage(ResWrapper.removeMapEffect());
                broadcastMessage(ResCField.BlowWeather(msg, itemId, false));
                //dont remove mapeffect.
            }
        }, 60000);
    }

    public final void startJukebox(final String msg, final int itemId) {
        startMapEffect(msg, itemId, true);
    }

    public final void addPlayer(final MapleCharacter chr) {
        mutex.lock();
        try {
            characters.add(chr);
            mapobjects.get(MapleMapObjectType.PLAYER).put(chr.getObjectId(), chr);
        } finally {
            mutex.unlock();
        }
        if (mapid == 109080000 || mapid == 109080001 || mapid == 109080002 || mapid == 109080003) {
            chr.setCoconutTeam(getAndSwitchTeam() ? 0 : 1);
        }
        if (!chr.isHidden()) {
            broadcastMessage(chr, ResCUserPool.UserEnterField(chr), false);
            if (chr.isGM() && speedRunStart > 0) {
                endSpeedRun();
                broadcastMessage(ResWrapper.BroadCastMsgEvent("The speed run has ended."));
            }
        }
        if (!chr.isClone()) {
            if (!onFirstUserEnter.equals("")) {
                if (getCharactersSize() == 1) {
                    MapScriptMethods.startScript_FirstUser(chr.getClient(), onFirstUserEnter);
                }
            }
            sendObjectPlacement(chr);

            // 多分不要
            // chr.getClient().getSession().write(UserPacket.spawnPlayerMapobject(chr));
            if (!onUserEnter.equals("")) {
                MapScriptMethods.startScript_User(chr.getClient(), onUserEnter);
            }
            switch (mapid) {
                case 109080000: // coconut shit
                case 109080001:
                case 109080002:
                case 109080003:
                    chr.getClient().getSession().write(ResCField.showEquipEffect(chr.getCoconutTeam()));
                    break;
                case 809000101:
                case 809000201:
                    chr.getClient().getSession().write(ResCField.showEquipEffect());
                    break;
            }
        }
        for (final MaplePet pet : chr.getPets()) {
            if (pet.getSummoned()) {
                broadcastMessage(chr, ResCUser_Pet.TransferField(chr, pet), true);
            }
        }
        if (chr.getParty() != null && !chr.isClone()) {
            chr.silentPartyUpdate();
            chr.getClient().getSession().write(ResCWvsContext.updateParty(chr.getClient().getChannel(), chr.getParty(), PartyOperation.SILENT_UPDATE, null));
            chr.updatePartyMemberHP();
            chr.receivePartyMemberHP();
        }
        final MapleStatEffect stat = chr.getStatForBuff(MapleBuffStat.SUMMON);
        if (stat != null && !chr.isClone()) {
            final MapleSummon summon = chr.getSummons().get(stat.getSourceId());
            summon.setPosition(chr.getPosition());
            try {
                summon.setFh(getFootholds().findBelow(chr.getPosition()).getId());
            } catch (NullPointerException e) {
                summon.setFh(0); //lol, it can be fixed by movement
            }
            chr.addVisibleMapObject(summon);
            this.spawnSummon(summon);
        }
        if (mapEffect != null) {
            mapEffect.sendStartData(chr.getClient());
        }
        if (timeLimit > 0 && getForcedReturnMap() != null && !chr.isClone()) {
            chr.startMapTimeLimitTask(timeLimit, getForcedReturnMap());
        }
        if (chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
            if (FieldLimitType.Mount.check(fieldLimit)) {
                chr.cancelBuffStats(MapleBuffStat.MONSTER_RIDING);
            }
        }
        if (!chr.isClone()) {
            if (chr.getEventInstance() != null && chr.getEventInstance().isTimerStarted() && !chr.isClone()) {
                chr.getClient().getSession().write(ResCField.getClock((int) (chr.getEventInstance().getTimeLeft() / 1000)));
            }
            if (hasClock()) {
                final Calendar cal = Calendar.getInstance();
                chr.getClient().getSession().write((ResCField.getClockTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND))));
            }
            if (chr.getCarnivalParty() != null && chr.getEventInstance() != null) {
                chr.getEventInstance().onMapLoad(chr);
            }
            MapleEvent.mapLoad(chr, channel);
            if (getSquadBegin() != null && getSquadBegin().getTimeLeft() > 0 && getSquadBegin().getStatus() == 1) {
                chr.getClient().getSession().write(ResCField.getClock((int) (getSquadBegin().getTimeLeft() / 1000)));
            }
            if (mapid / 1000 != 105100 && mapid / 100 != 8020003 && mapid / 100 != 8020008) { //no boss_balrog/2095/coreblaze/auf. but coreblaze/auf does AFTER
                final MapleSquad sqd = getSquadByMap(); //for all squads
                if (!squadTimer && sqd != null && chr.getName().equals(sqd.getLeaderName()) && !chr.isClone()) {
                    //leader? display
                    doShrine(false);
                    squadTimer = true;
                }
            }
            if (getNumMonsters() > 0 && (mapid == 280030001 || mapid == 240060201 || mapid == 280030000 || mapid == 240060200 || mapid == 220080001 || mapid == 541020800 || mapid == 541010100)) {
                String music = "Bgm09/TimeAttack";
                switch (mapid) {
                    case 240060200:
                    case 240060201:
                        music = "Bgm14/HonTale";
                        break;
                    case 280030000:
                    case 280030001:
                        music = "Bgm06/FinalFight";
                        break;
                }
                chr.getClient().getSession().write(ResWrapper.musicChange(music));
                //maybe timer too for zak/ht
            }
            if (mapid == 914000000) {
                chr.getClient().getSession().write(ResCWvsContext.temporaryStats_Aran());
            } else if (mapid == 105100300 && chr.getLevel() >= 91) {
                chr.getClient().getSession().write(ResCWvsContext.temporaryStats_Balrog(chr));
            } else if (mapid == 140090000 || mapid == 105100301 || mapid == 105100401 || mapid == 105100100) {
                chr.getClient().getSession().write(ResCWvsContext.ForcedStatReset());
            }
        }
        if (GameConstants.isEvan(chr.getJob()) && chr.getJob() >= 2200 && chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING) == null) {
            if (chr.getDragon() == null) {
                chr.makeDragon();
            }
            spawnDragon(chr.getDragon());
            if (!chr.isClone()) {
                updateMapObjectVisibility(chr, chr.getDragon());
            }
        }
        if ((mapid == 10000 && chr.getJob() == 0) || (mapid == 130030000 && chr.getJob() == 1000) || (mapid == 914000000 && chr.getJob() == 2000) || (mapid == 900010000 && chr.getJob() == 2001)) {
            chr.getClient().getSession().write(ResCField.BlowWeather("Welcome to " + chr.getClient().getChannelServer().getServerName() + "!", 5122000, true));
            chr.dropMessage(1, "Welcome to " + chr.getClient().getChannelServer().getServerName() + ", " + chr.getName() + " ! \r\nUse @joyce to collect your Item Of Appreciation once you're level 10! \r\nUse @help for commands. \r\nGood luck and have fun!");
            chr.dropMessage(5, "Your EXP Rate will be set to " + GameConstants.getExpRate_Below10(chr.getJob()) + "x until you reach level 10.");
            chr.dropMessage(5, "Use @joyce to collect your Item Of Appreciation once you're level 10! Use @help for commands. Good luck and have fun!");

        }
        if (getPlatforms().size() > 0) {
            chr.getClient().getSession().write(ResCField.getMovingPlatforms(this));
        }
        if (environment.size() > 0) {
            chr.getClient().getSession().write(ResCField.getUpdateEnvironment(this));
        }
        if (isTown()) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.RAINING_MINES);
        }
    }

    public void doShrine(final boolean spawned) { //false = entering map, true = defeated
        if (squadSchedule != null) {
            cancelSquadSchedule();
        }
        final int mode = (mapid == 280030000 ? 1 : (mapid == 280030001 ? 2 : (mapid == 240060200 || mapid == 240060201 ? 3 : 0)));
        //chaos_horntail message for horntail too because it looks nicer
        final MapleSquad sqd = getSquadByMap();
        final EventManager em = getEMByMap();
        if (sqd != null && em != null && getCharactersSize() > 0) {
            final String leaderName = sqd.getLeaderName();
            final String state = em.getProperty("state");
            final Runnable run;
            MapleMap returnMapa = getForcedReturnMap();
            if (returnMapa == null || returnMapa.getId() == mapid) {
                returnMapa = getReturnMap();
            }
            if (mode == 1) { //zakum
                broadcastMessage(ResCField.showZakumShrine(spawned, 5));
            } else if (mode == 2) { //chaoszakum
                broadcastMessage(ResCField.showChaosZakumShrine(spawned, 5));
            } else if (mode == 3) { //ht/chaosht
                broadcastMessage(ResCField.showChaosHorntailShrine(spawned, 5));
            } else {
                broadcastMessage(ResCField.showHorntailShrine(spawned, 5));
            }
            if (mode == 1 || spawned) { //both of these together dont go well
                broadcastMessage(ResCField.getClock(300)); //5 min
            }
            final MapleMap returnMapz = returnMapa;
            if (!spawned) { //no monsters yet; inforce timer to spawn it quickly
                final List<MapleMonster> monsterz = getAllMonsters();
                final List<Integer> monsteridz = new ArrayList<Integer>();
                for (MapleMapObject m : monsterz) {
                    monsteridz.add(m.getObjectId());
                }
                run = new Runnable() {

                    public void run() {
                        final MapleSquad sqnow = MapleMap.this.getSquadByMap();
                        if (MapleMap.this.getCharactersSize() > 0 && MapleMap.this.getNumMonsters() == monsterz.size() && sqnow != null && sqnow.getStatus() == 2 && sqnow.getLeaderName().equals(leaderName) && MapleMap.this.getEMByMap().getProperty("state").equals(state)) {
                            boolean passed = monsterz.isEmpty();
                            for (MapleMapObject m : MapleMap.this.getAllMonsters()) {
                                for (int i : monsteridz) {
                                    if (m.getObjectId() == i) {
                                        passed = true;
                                        break;
                                    }
                                }
                                if (passed) {
                                    break;
                                } //even one of the monsters is the same
                            }
                            if (passed) {
                                //are we still the same squad? are monsters still == 0?
                                MaplePacket packet;
                                if (mode == 1) { //zakum
                                    packet = ResCField.showZakumShrine(spawned, 0);
                                } else if (mode == 2) { //chaoszakum
                                    packet = ResCField.showChaosZakumShrine(spawned, 0);
                                } else {
                                    packet = ResCField.showHorntailShrine(spawned, 0); //chaoshorntail message is weird
                                }
                                for (MapleCharacter chr : MapleMap.this.getCharacters()) { //warp all in map
                                    chr.getClient().getSession().write(packet);
                                    chr.changeMap(returnMapz, returnMapz.getPortal(0)); //hopefully event will still take care of everything once warp out
                                }
                                checkStates("");
                                resetFully();
                            }
                        }

                    }
                };
            } else { //inforce timer to gtfo
                run = new Runnable() {

                    public void run() {
                        MapleSquad sqnow = MapleMap.this.getSquadByMap();
                        //we dont need to stop clock here because they're getting warped out anyway
                        if (MapleMap.this.getCharactersSize() > 0 && sqnow != null && sqnow.getStatus() == 2 && sqnow.getLeaderName().equals(leaderName) && MapleMap.this.getEMByMap().getProperty("state").equals(state)) {
                            //are we still the same squad? monsters however don't count
                            MaplePacket packet;
                            if (mode == 1) { //zakum
                                packet = ResCField.showZakumShrine(spawned, 0);
                            } else if (mode == 2) { //chaoszakum
                                packet = ResCField.showChaosZakumShrine(spawned, 0);
                            } else {
                                packet = ResCField.showHorntailShrine(spawned, 0); //chaoshorntail message is weird
                            }
                            for (MapleCharacter chr : MapleMap.this.getCharacters()) { //warp all in map
                                chr.getClient().getSession().write(packet);
                                chr.changeMap(returnMapz, returnMapz.getPortal(0)); //hopefully event will still take care of everything once warp out
                            }
                            checkStates("");
                            resetFully();
                        }
                    }
                };
            }
            squadSchedule = MapTimer.getInstance().schedule(run, 300000); //5 mins
        }
    }

    public final MapleSquad getSquadByMap() {
        String zz = null;
        switch (mapid) {
            case 105100400:
            case 105100300:
                zz = "BossBalrog";
                break;
            case 280030000:
                zz = "ZAK";
                break;
            case 280030001:
                zz = "ChaosZak";
                break;
            case 240060200:
                zz = "Horntail";
                break;
            case 240060201:
                zz = "ChaosHT";
                break;
            case 270050100:
                zz = "PinkBean";
                break;
            case 802000111:
                zz = "nmm_squad";
                break;
            case 802000211:
                zz = "VERGAMOT";
                break;
            case 802000311:
                zz = "2095_tokyo";
                break;
            case 802000411:
                zz = "Dunas";
                break;
            case 802000611:
                zz = "Nibergen_squad";
                break;
            case 802000711:
                zz = "dunas2";
                break;
            case 802000801:
            case 802000802:
            case 802000803:
                zz = "Core_Blaze";
                break;
            case 802000821:
                zz = "Aufheben";
                break;
            default:
                return null;
        }
        return ServerOdinGame.getInstance(channel).getMapleSquad(zz);
    }

    public final EventManager getEMByMap() {
        String em = null;
        switch (mapid) {
            case 105100400:
                em = "BossBalrog_EASY";
                break;
            case 105100300:
                em = "BossBalrog_NORMAL";
                break;
            case 280030000:
                em = "ZakumBattle";
                break;
            case 240060200:
                em = "HorntailBattle";
                break;
            case 280030001:
                em = "ChaosZakum";
                break;
            case 240060201:
                em = "ChaosHorntail";
                break;
            case 270050100:
                em = "PinkBeanBattle";
                break;
            case 802000111:
                em = "NamelessMagicMonster";
                break;
            case 802000211:
                em = "Vergamot";
                break;
            case 802000311:
                em = "2095_tokyo";
                break;
            case 802000411:
                em = "Dunas";
                break;
            case 802000611:
                em = "Nibergen";
                break;
            case 802000711:
                em = "Dunas2";
                break;
            case 802000801:
            case 802000802:
            case 802000803:
                em = "CoreBlaze";
                break;
            case 802000821:
                em = "Aufhaven";
                break;
            default:
                return null;
        }
        return ServerOdinGame.getInstance(channel).getEventSM().getEventManager(em);
    }

    public final void removePlayer(final MapleCharacter chr) {
        //log.warn("[dc] [level2] Player {} leaves map {}", new Object[] { chr.getName(), mapid });
        if (everlast) {
            returnEverLastItem(chr);
        }
        mutex.lock();
        try {
            characters.remove(chr);
        } finally {
            mutex.unlock();
        }
        removeMapObject(chr);
        chr.checkFollow();
        broadcastMessage(ResCUserPool.UserLeaveField(chr.getId()));
        if (!chr.isClone()) {
            for (final MapleMonster monster : chr.getControlledMonsters()) {
                monster.setController(null);
                monster.setControllerHasAggro(false);
                monster.setControllerKnowsAboutAggro(false);
                updateMonsterController(monster);
            }
            chr.leaveMap();
            checkStates(chr.getName());
            if (mapid == 109020001) {
                chr.canTalk(true);
            }
        }
        chr.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
        chr.cancelEffectFromBuffStat(MapleBuffStat.REAPER);
        boolean cancelSummons = false;
        for (final MapleSummon summon : chr.getSummons().values()) {
            if (summon.getMovementType() == SummonMovementType.STATIONARY || summon.getMovementType() == SummonMovementType.CIRCLE_STATIONARY || summon.getMovementType() == SummonMovementType.WALK_STATIONARY) {
                cancelSummons = true;
            } else {
                summon.setChangedMap(true);
                removeMapObject(summon);
            }
        }
        if (cancelSummons) {
            chr.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);

        }
        if (chr.getDragon() != null) {
            removeMapObject(chr.getDragon());
        }
    }

    public final void broadcastMessageClone(final MapleCharacter source, final MaplePacket packet) {
        int clone_delay = 1000;
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                broadcastMessage(source, packet, false);
            }
        }, clone_delay);
    }

    public final void broadcastMessageDelayed(MapleCharacter source, MaplePacket packet) {
        int delay = 1000;
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                broadcastMessage(source, packet, true);
            }
        }, delay);
    }

    private void sendObjectPlacement(MapleCharacter chr) {
        if (chr == null || chr.isClone()) {
            return;
        }
        for (final MapleMapObject o : this.getAllMonsters()) {
            updateMonsterController((MapleMonster) o);
        }
        for (final MapleMapObject o : getMapObjectsInRange(chr.getPosition(), chr.getViewRangeSq(), GameConstants.rangedMapobjectTypes)) {
            if (o.getType() == MapleMapObjectType.REACTOR) {
                if (!((MapleReactor) o).isAlive()) {
                    continue;
                }
            }
            o.sendSpawnData(chr.getClient());
            chr.addVisibleMapObject(o);
        }
    }

    public final void loadMonsterRate(final boolean first) {
        final int spawnSize = monsterSpawn.size();
        maxRegularSpawn = Math.round(spawnSize * monsterRate);
        if (maxRegularSpawn < 2) {
            maxRegularSpawn = 2;
        } else if (maxRegularSpawn > spawnSize) {
            maxRegularSpawn = spawnSize - (spawnSize / 15);
        }
        if (fixedMob > 0) {
            maxRegularSpawn = fixedMob;
        }
        Collection<Spawns> newSpawn = new LinkedList<Spawns>();
        Collection<Spawns> newBossSpawn = new LinkedList<Spawns>();
        for (final Spawns s : monsterSpawn) {
            if (s.getCarnivalTeam() >= 2) {
                continue; // Remove carnival spawned mobs
            }
            if (s.getMonster().getStats().isBoss()) {
                newBossSpawn.add(s);
            } else {
                newSpawn.add(s);
            }
        }
        monsterSpawn.clear();
        monsterSpawn.addAll(newBossSpawn);
        monsterSpawn.addAll(newSpawn);

        if (first && spawnSize > 0) {
            lastSpawnTime = 0; // 即沸き
            if (GameConstants.isForceRespawn(mapid)) {
                createMobInterval = 15000;
            }
        }
    }

    public final SpawnPoint addMonsterSpawn(final MapleMonster monster, final int mobTime, final byte carnivalTeam, final String msg) {
        final Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        final SpawnPoint sp = new SpawnPoint(monster, newpos, mobTime, carnivalTeam, msg);
        if (carnivalTeam > -1) {
            monsterSpawn.add(0, sp); //at the beginning
        } else {
            monsterSpawn.add(sp);
        }
        return sp;
    }

    public final void addAreaMonsterSpawn(final MapleMonster monster, Point pos1, Point pos2, Point pos3, final int mobTime, final String msg) {
        pos1 = calcPointBelow(pos1);
        pos2 = calcPointBelow(pos2);
        pos3 = calcPointBelow(pos3);
        if (pos1 != null) {
            pos1.y -= 1;
        }
        if (pos2 != null) {
            pos2.y -= 1;
        }
        if (pos3 != null) {
            pos3.y -= 1;
        }
        if (pos1 == null && pos2 == null && pos3 == null) {
            System.out.println("WARNING: mapid " + mapid + ", monster " + monster.getId() + " could not be spawned.");

            return;
        } else if (pos1 != null) {
            if (pos2 == null) {
                pos2 = new Point(pos1);
            }
            if (pos3 == null) {
                pos3 = new Point(pos1);
            }
        } else if (pos2 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos2);
            }
            if (pos3 == null) {
                pos3 = new Point(pos2);
            }
        } else if (pos3 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos3);
            }
            if (pos2 == null) {
                pos2 = new Point(pos3);
            }
        }
        monsterSpawn.add(new SpawnPointAreaBoss(monster, pos1, pos2, pos3, mobTime, msg));
    }

    public void movePlayer(final MapleCharacter player, final Point newPosition) {
        player.setPosition(newPosition);
        final Collection<MapleMapObject> visibleObjects = player.getVisibleMapObjects();
        final MapleMapObject[] visibleObjectsNow = visibleObjects.toArray(new MapleMapObject[visibleObjects.size()]);
        for (MapleMapObject mo : visibleObjectsNow) {
            if (getMapObject(mo.getObjectId(), mo.getType()) == mo) {
                updateMapObjectVisibility(player, mo);
            } else {
                player.removeVisibleMapObject(mo);
            }
        }
        // 表示可能範囲のNPC等を表示
        for (MapleMapObject mo : getMapObjectsInRange(player.getPosition(), player.getViewRangeSq())) {
            if (!player.isMapObjectVisible(mo) && mo.getObjectId() != player.getObjectId()) {
                mo.sendSpawnData(player.getClient());
                player.addVisibleMapObject(mo);
            }
        }
    }

    public int getSpawnedMonstersOnMap() {
        return spawnedMonstersOnMap.get();
    }

    private class ActivateItemReactor implements Runnable {

        private MapleMapItem mapitem;
        private MapleReactor reactor;
        private MapleClient c;

        public ActivateItemReactor(MapleMapItem mapitem, MapleReactor reactor, MapleClient c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }

        @Override
        public void run() {
            if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId(), mapitem.getType())) {
                if (mapitem.isPickedUp()) {
                    reactor.setTimerActive(false);
                    return;
                }
                mapitem.expire(MapleMap.this);
                reactor.hitReactor(c);
                reactor.setTimerActive(false);

                if (reactor.getDelay() > 0) {
                    MapTimer.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            reactor.forceHitReactor((byte) 0);
                        }
                    }, reactor.getDelay());
                }
            } else {
                reactor.setTimerActive(false);
            }
        }
    }

    private void activateItemReactors(final MapleMapItem drop, final MapleClient c) {
        final IItem item = drop.getItem();

        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (final MapleMapObject o : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final MapleReactor react = (MapleReactor) o;

                if (react.getReactorType() == 100) {
                    if (GameConstants.isCustomReactItem(react.getReactorId(), item.getItemId(), react.getReactItem().getLeft()) && react.getReactItem().getRight() == item.getQuantity()) {
                        if (react.getArea().contains(drop.getPosition())) {
                            if (!react.isTimerActive()) {
                                MapTimer.getInstance().schedule(new ActivateItemReactor(drop, react, c), 5000);
                                react.setTimerActive(true);
                                break;
                            }
                        }
                    }
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public void respawn(final boolean force) {
        lastSpawnTime = System.currentTimeMillis();
        if (force) { //cpq quick hack
            final int numShouldSpawn = monsterSpawn.size() - spawnedMonstersOnMap.get();

            if (numShouldSpawn > 0) {
                int spawned = 0;

                for (Spawns spawnPoint : monsterSpawn) {
                    spawnPoint.spawnMonster(this);
                    spawned++;
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        } else {
            final int numShouldSpawn = maxRegularSpawn - spawnedMonstersOnMap.get();
            if (numShouldSpawn > 0) {
                int spawned = 0;

                final List<Spawns> randomSpawn = new ArrayList<Spawns>(monsterSpawn);
                Collections.shuffle(randomSpawn);

                for (Spawns spawnPoint : randomSpawn) {
                    if (spawnPoint.shouldSpawn() || GameConstants.isForceRespawn(mapid)) {
                        spawnPoint.spawnMonster(this);
                        spawned++;
                    }
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    public String getSnowballPortal() {
        int[] teamss = new int[2];
        for (MapleCharacter chr : getCharacters()) {
            if (chr.getPosition().y > -80) {
                teamss[0]++;
            } else {
                teamss[1]++;
            }
        }
        if (teamss[0] > teamss[1]) {
            return "st01";
        } else {
            return "st00";
        }
    }

    public void getRankAndAdd(String leader, String time, SpeedRunType type, long timz, Collection<String> squad) {
        try {
            //Pair<String, Map<Integer, String>>
            StringBuilder rett = new StringBuilder();
            if (squad != null) {
                for (String chr : squad) {
                    rett.append(chr);
                    rett.append(",");
                }
            }
            String z = rett.toString();
            if (squad != null) {
                z = z.substring(0, z.length() - 1);
            }
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO speedruns(`type`, `leader`, `timestring`, `time`, `members`) VALUES (?,?,?,?,?)");
            ps.setString(1, type.name());
            ps.setString(2, leader);
            ps.setString(3, time);
            ps.setLong(4, timz);
            ps.setString(5, z);
            ps.executeUpdate();
            ps.close();

            if (SpeedRunner.getInstance().getSpeedRunData(type) == null) { //great, we just add it
                SpeedRunner.getInstance().addSpeedRunData(type, SpeedRunner.getInstance().addSpeedRunData(new StringBuilder("#rThese are the speedrun times for " + type + ".#k\r\n\r\n"), new HashMap<Integer, String>(), z, leader, 1, time));
            } else {
                //i wish we had a way to get the rank
                //TODO revamp
                SpeedRunner.getInstance().removeSpeedRunData(type);
                SpeedRunner.getInstance().loadSpeedRunData(type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void resetNPCs() {
        List<MapleNPC> npcs = getAllNPCs();
        for (MapleNPC npc : npcs) {
            if (npc.isCustom()) {
                broadcastMessage(ResCNpcPool.NpcEnterField(npc, false));
                removeMapObject(npc);
            }
        }
    }

    public final void resetFully() {
        resetFully(true);
    }

    public final void resetFully(final boolean respawn) {
        killAllMonsters(false);
        reloadReactors();
        removeDrops();
        resetNPCs();
        resetSpawns();
        endSpeedRun();
        cancelSquadSchedule();
        resetPortals();
        environment.clear();
        if (respawn) {
            respawn(true);
        }
    }

    public final void removeDrops() {
        List<MapleMapItem> items = this.getAllItems();
        for (MapleMapItem i : items) {
            i.expire(this);
        }
    }

    public final void resetSpawns() {
        boolean changed = false;
        Iterator<Spawns> sss = monsterSpawn.iterator();
        while (sss.hasNext()) {
            if (sss.next().getCarnivalId() > -1) {
                sss.remove();
                changed = true;
            }
        }
        setSpawns(true);
        if (changed) {
            loadMonsterRate(true);
        }
    }

    public final boolean makeCarnivalSpawn(final int team, final MapleMonster newMons, final int num) {
        MonsterPoint ret = null;
        for (MonsterPoint mp : nodes.getMonsterPoints()) {
            if (mp.team == team || mp.team == -1) {
                final Point newpos = calcPointBelow(new Point(mp.x, mp.y));
                newpos.y -= 1;
                boolean found = false;
                for (Spawns s : monsterSpawn) {
                    if (s.getCarnivalId() > -1 && (mp.team == -1 || s.getCarnivalTeam() == mp.team) && s.getPosition().x == newpos.x && s.getPosition().y == newpos.y) {
                        found = true;
                        break; //this point has already been used.
                    }
                }
                if (!found) {
                    ret = mp; //this point is safe for use.
                    break;
                }
            }
        }
        if (ret != null) {
            newMons.setCy(ret.cy);
            newMons.setF(0); //always.
            newMons.setFh(ret.fh);
            newMons.setRx0(ret.x + 50);
            newMons.setRx1(ret.x - 50); //does this matter
            newMons.setPosition(new Point(ret.x, ret.y));
            newMons.setHide(false);
            final SpawnPoint sp = addMonsterSpawn(newMons, 1, (byte) team, null);
            sp.setCarnival(num);
        }
        return ret != null;
    }

    public final boolean makeCarnivalReactor(final int team, final int num) {
        final MapleReactor old = getReactorByName(team + "" + num);
        if (old != null && old.getState() < 5) { //already exists
            return false;
        }
        Point guardz = null;
        final List<MapleReactor> react = getAllReactors();
        for (Pair<Point, Integer> guard : nodes.getGuardians()) {
            if (guard.right == team || guard.right == -1) {
                boolean found = false;
                for (MapleReactor r : react) {
                    if (r.getPosition().x == guard.left.x && r.getPosition().y == guard.left.y && r.getState() < 5) {
                        found = true;
                        break; //already used
                    }
                }
                if (!found) {
                    guardz = guard.left; //this point is safe for use.
                    break;
                }
            }
        }
        if (guardz != null) {
            final MapleReactorStats stats = ReactorWz.getReactor(9980000 + team);
            final MapleReactor my = new MapleReactor(stats, 9980000 + team);
            stats.setFacingDirection((byte) 0); //always
            my.setPosition(guardz);
            my.setState((byte) 1);
            my.setDelay(0);
            my.setName(team + "" + num); //lol
            //with num. -> guardians in factory
            spawnReactor(my);
            final MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
            for (MapleMonster mons : getAllMonsters()) {
                if (mons.getCarnivalTeam() == team) {
                    skil.getSkill().applyEffect(null, mons, false);
                }
            }
        }
        return guardz != null;
    }

    public boolean getAndSwitchTeam() {
        return getCharactersSize() % 2 != 0;
    }

    public void checkStates(final String chr) {
        final MapleSquad sqd = getSquadByMap();
        final EventManager em = getEMByMap();
        final int size = getCharactersSize();
        if (sqd != null) {
            sqd.removeMember(chr);
            if (em != null) {
                if (sqd.getLeaderName().equals(chr)) {
                    em.setProperty("leader", "false");
                }
                if (chr.equals("") || size == 0) {
                    sqd.clear();
                    em.setProperty("state", "0");
                    em.setProperty("leader", "true");
                    cancelSquadSchedule();
                }
            }
        }
        if (em != null && em.getProperty("state") != null) {
            if (size == 0) {
                em.setProperty("state", "0");
                if (em.getProperty("leader") != null) {
                    em.setProperty("leader", "true");
                }
            }
        }
        if (speedRunStart > 0 && speedRunLeader.equalsIgnoreCase(chr)) {
            if (size > 0) {
                broadcastMessage(ResWrapper.BroadCastMsgEvent("The leader is not in the map! Your speedrun has failed"));
            }
            endSpeedRun();
        }
    }

}
