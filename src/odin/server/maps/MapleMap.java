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
import java.util.Collection;
import java.util.List;
import odin.client.inventory.Item;
import odin.constants.GameConstants;
import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCDropPool;
import tacos.packet.response.ResCDropPool.DropEnterType;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCMobPool;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleStatEffect;
import odin.server.life.MapleMonster;
import odin.server.life.MapleLifeFactory;
import odin.server.MapleCarnivalFactory;
import odin.server.MapleCarnivalFactory.MCSkill;
import odin.server.Timer.MapTimer;
import odin.server.maps.MapleNodes.MonsterPoint;
import tacos.debug.DebugLogger;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.packet.ops.OpsMobLeaveField;
import tacos.packet.response.ResCDropPool.DropLeaveType;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.builder.PB_UserEffect;
import tacos.server.map.TacosMap;
import tacos.server.map.TacosReward;
import tacos.wz.WzXML;

public final class MapleMap extends TacosMap {

    public MapleMap(int mapid, int channel) {
        super(mapid, channel);
    }

    @Override
    public void spawnRevives(MapleMonster monster, int oid) {
        monster.setMap(this);
        super.spawnRevives(monster, oid);
    }

    @Override
    public int spawnMonsterWithEffect(MapleMonster monster, int effect, Point pos) {
        monster.setMap(this);
        return super.spawnMonsterWithEffect(monster, effect, pos);
    }

    @Override
    public void spawnFakeMonster(MapleMonster monster) {
        monster.setMap(this);
        super.spawnFakeMonster(monster);
    }

    @Override
    public void spawnReactor(MapleReactor reactor) {
        reactor.setMap(this);
        super.spawnReactor(reactor);
    }

    public void killMonster(MapleMonster monster, MapleCharacter chr, boolean withDrops, boolean second, OpsMobLeaveField animation) {
        killMonster(monster, chr, withDrops, second, animation, 0);
    }

    public void killMonster(MapleMonster monster, MapleCharacter chr, boolean withDrops, boolean second, OpsMobLeaveField animation, int lastSkill) {
        if ((monster.getId() == 8810122 || monster.getId() == 8810018) && !second) {
            MapTimer.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    killMonster(monster, chr, true, true, OpsMobLeaveField.MOBLEAVEFIELD_ETC);
                    killAllMonsters(true);
                }
            }, 3000);
            return;
        }
        if (monster.getId() == 8820014) { //pb sponge, kills pb(w) first before dying
            killMonster(8820000);
        } else if (monster.getId() == 9300166) { //ariant pq bomb
            animation = OpsMobLeaveField.MOBLEAVEFIELD_SWALLOW; //or is it 3?
        }
        removeMonster(monster.getObjectId());
        int dropOwner = monster.killBy(chr, lastSkill);
        broadcastMessage(ResCMobPool.MobLeaveField(monster, animation));

        if (monster.getBuffToGive() > -1) {
            int buffid = monster.getBuffToGive();
            MapleStatEffect buff = MapleItemInformationProvider.getInstance().getItemEffect(buffid);

            for (MapleCharacter mc : getAllPlayers()) {
                if (mc.isAlive()) {
                    buff.applyTo(mc);

                    switch (monster.getId()) {
                        case 8810018:
                        case 8810122:
                        case 8820001: {
                            PB_UserEffect pb = PB_UserEffect.builder()
                                    .player(mc)
                                    .skill_id(buffid)
                                    .build();
                            mc.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_BuffItemEffect, pb));
                            broadcastMessage(mc, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_BuffItemEffect, pb), false);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            }
        }

        sendExpedition(chr, monster);

        int mobid = monster.getId();
        if (mobid >= 8800003 && mobid <= 8800010) {
            boolean makeZakReal = true;
            final Collection<MapleMonster> monsters = getAllMonsters();

            for (final MapleMonster mons : monsters) {
                if (mons.getId() >= 8800003 && mons.getId() <= 8800010) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (final Object object : monsters) {
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
        if (mobid == 8820008) { //wipe out statues and respawn
            for (final Object mmo : getAllMonsters()) {
                MapleMonster mons = (MapleMonster) mmo;
                if (mons.getSummonOption() != monster.getObjectId()) {
                    killMonster(mons, chr, false, false, animation);
                }
            }
        } else if (mobid >= 8820010 && mobid <= 8820014) {
            for (final Object mmo : getAllMonsters()) {
                MapleMonster mons = (MapleMonster) mmo;
                if (mons.getId() != 8820000 && mons.getObjectId() != monster.getObjectId() && mons.getSummonOption() != monster.getObjectId()) {
                    killMonster(mons, chr, false, false, animation);
                }
            }
        }
        if (!withDrops) {
            return;
        }
        MapleCharacter killer = getPlayerById(dropOwner); // highest damage player
        if (killer == null) {
            killer = chr;
        }
        TacosReward.getReward(killer, monster);
    }

    public final void spawnMonster_sSack(final MapleMonster mob, final Point pos, final int spawnType) {
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mob.setPosition(spos);
        spawnMonster(mob, spawnType);
    }

    public void spawnMonsterOnGroundBelow(MapleMonster mob, Point pos, int type) {
        spawnMonster_sSack(mob, pos, type);
    }

    // 多分Reactorの中心座標とサイズが必要, Map上の座標を利用すると足場より下に設置されているように見えるので落下する
    public void spawnZakum(MapleReactor zakum_reactor) {
        MapleMonster mainb = MapleLifeFactory.getMonster(8800000);
        int reactor_fh_id = getFootholds().findReactorFootId(zakum_reactor.getMobSpawnPoint());
        Point zakum_pos = new Point(zakum_reactor.getMobSpawnPoint());
        zakum_pos.y = getFootholds().findFootHold(reactor_fh_id).getY1() - 1;

        DebugLogger.DebugLog("spawnZakum : fh = " + reactor_fh_id + ", " + zakum_pos);

        mainb.setPosition(zakum_pos);
        mainb.setFootholdId(reactor_fh_id);
        mainb.setHomeFoothold(reactor_fh_id);
        mainb.setFake(true);

        spawnFakeMonster(mainb);
        int[] zakpart = {8800003, 8800004, 8800005, 8800006, 8800007, 8800008, 8800009, 8800010};
        for (int i : zakpart) {
            MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(zakum_pos);
            part.setFootholdId(reactor_fh_id);
            mainb.setHomeFoothold(reactor_fh_id);
            spawnMonster(part, -2);
        }
        if (squadSchedule != null) {
            cancelSquadSchedule();
            broadcastMessage(ResCField.DestroyClock());
        }
    }

    public void spawnMobDrop(Item idrop, Point dropPos, MapleMonster mob, MapleCharacter chr, byte droptype, short quest_id) {
        MapleMapItem mdrop = new MapleMapItem(idrop, dropPos, mob, chr, droptype, false, quest_id);
        addDrop(mdrop);
        broadcastMessage(ResCDropPool.DropEnterField(mdrop, ResCDropPool.DropEnterType.NORMAL, dropPos, mob.getPosition(), mob.getObjectId()));
        activateItemReactors(mdrop, chr.getClient());
    }

    public void talkMonster(String msg, int itemId, MapleMonster monster) {
        if (itemId > 0) {
            startMapEffect(msg, itemId, false);
        }
        broadcastMessage(ResCMobPool.MobEscortStopSay(monster, itemId, msg)); // 5120035
        broadcastMessage(ResCMobPool.MobEscortReturnBefore(monster));
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
                broadcastMessage(ResCField.BlowWeather(null, 0, false));
                broadcastMessage(ResCField.BlowWeather(msg, itemId, false));
                //dont remove mapeffect.
            }
        }, 60000);
    }

    public final void startJukebox(final String msg, final int itemId) {
        startMapEffect(msg, itemId, true);
    }

    private class ActivateItemReactor implements Runnable {

        private MapleMapItem mapitem;
        private MapleReactor reactor;
        private TacosClient client;

        public ActivateItemReactor(MapleMapItem mapitem, MapleReactor reactor, TacosClient client) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.client = client;
        }

        @Override
        public void run() {
            if (mapitem != null && mapitem == findDrop(mapitem.getObjectId())) {
                removeDrop(mapitem.getObjectId());
                broadcastMessage(ResCDropPool.DropLeaveField(mapitem, DropLeaveType.EXPIRED));
                reactor.hitReactor(client);
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

    private void activateItemReactors(MapleMapItem drop, TacosClient client) {
        Item item = drop.getItem();
        for (MapleReactor react : getAllReactors()) {

            if (react.getReactorType() == 100) {
                if (GameConstants.isCustomReactItem(react.getReactorId(), item.getItemId(), react.getReactItem().getKey()) && react.getReactItem().getValue() == item.getQuantity()) {
                    if (react.getArea().contains(drop.getPosition())) {
                        if (!react.isTimerActive()) {
                            MapTimer.getInstance().schedule(new ActivateItemReactor(drop, react, client), 5000);
                            react.setTimerActive(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    public String getSnowballPortal() {
        int[] teamss = new int[2];
        for (MapleCharacter chr : getAllPlayers()) {
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

    public boolean makeCarnivalSpawn(int team, MapleMonster newMons, int num) {
        MonsterPoint ret = null;
        for (MonsterPoint mp : getNodeInfo().getMonsterPoints()) {
            if (mp.team == team || mp.team == -1) {
                Point newpos = calcPointBelow(new Point(mp.x, mp.y));
                newpos.y -= 1;
                boolean found = false;
                /*
                for (Object s : monsterSpawn) {
                    if (SpawnDispatch.getCarnivalId(s) > -1 && (mp.team == -1 || SpawnDispatch.getCarnivalTeam(s) == mp.team) && SpawnDispatch.getPosition(s).x == newpos.x && SpawnDispatch.getPosition(s).y == newpos.y) {
                        found = true;
                        break; //this point has already been used.
                    }
                }
                 */
                if (!found) {
                    ret = mp; //this point is safe for use.
                    break;
                }
            }
        }
        if (ret != null) {
            newMons.setCy(ret.cy);
            newMons.setF(0); //always.
            newMons.setFootholdId(ret.fh);
            newMons.setRx0(ret.x + 50);
            newMons.setRx1(ret.x - 50); //does this matter
            newMons.setPosition(new Point(ret.x, ret.y));
            //SpawnPoint sp = addMonsterSpawn(newMons, 1, (byte) team, null);
            //sp.setCarnival(num);
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
        for (SimpleImmutableEntry<Point, Integer> guard : getNodeInfo().getGuardians()) {
            if (guard.getValue() == team || guard.getValue() == -1) {
                boolean found = false;
                for (MapleReactor r : react) {
                    if (r.getPosition().x == guard.getKey().x && r.getPosition().y == guard.getKey().y && r.getState() < 5) {
                        found = true;
                        break; //already used
                    }
                }
                if (!found) {
                    guardz = guard.getKey(); //this point is safe for use.
                    break;
                }
            }
        }
        if (guardz != null) {
            final MapleReactorStats stats = WzXML.REACTOR.getReactor(9980000 + team);
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

    // used by script
    @Override
    public void spawnMonster(MapleMonster monster, int spawnType) {
        monster.setMap(this);
        super.spawnMonster(monster, spawnType);
    }

    // used by script
    public final void spawnMonsterOnGroundBelow(final MapleMonster mob, final Point pos) {
        spawnMonster_sSack(mob, pos, -2);
    }

    // used by script
    public final int spawnMonsterWithEffectBelow(final MapleMonster mob, final Point pos, final int effect) {
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        return spawnMonsterWithEffect(mob, effect, spos);
    }

    // used by script
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
            broadcastMessage(ResCField.DestroyClock());
        }
    }

    // used by script
    public void spawnItemDrop(Object dropper, MapleCharacter owner, Item item, Point pos, boolean ffaDrop, boolean playerDrop) {
        Point droppos = calcDropPos(pos, pos);
        MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte) 2, playerDrop);
        addDrop(drop);
        Point dropperPosition;
        if (dropper instanceof MapleCharacter) {
            dropperPosition = ((MapleCharacter) dropper).getPosition();
        } else if (dropper instanceof MapleReactor) {
            dropperPosition = ((MapleReactor) dropper).getPosition();
        } else {
            throw new IllegalArgumentException("spawnItemDrop: unknown dropper type: " + dropper);
        }
        broadcastMessage(ResCDropPool.DropEnterField(drop, DropEnterType.NORMAL, droppos, dropperPosition));
        broadcastMessage(ResCDropPool.DropEnterField(drop, DropEnterType.UPDATE, droppos, dropperPosition)); // enable pick up for new players
        if (!getEverlast()) {
            activateItemReactors(drop, owner.getClient());
        }
    }
}
