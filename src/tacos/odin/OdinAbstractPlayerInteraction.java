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
package tacos.odin;

import java.awt.Point;
import java.util.List;
import odin.client.inventory.Equip;
import odin.client.SkillFactory;
import odin.constants.GameConstants;
import odin.client.ISkill;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MaplePet;
import odin.client.MapleQuestStatus;
import odin.client.inventory.IItem;
import odin.handling.world.MapleParty;
import odin.handling.world.MaplePartyCharacter;
import odin.handling.world.guild.MapleGuild;
import odin.server.Randomizer;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleReactor;
import odin.server.maps.MapleMapObject;
import odin.server.maps.SavedLocationType;
import odin.server.maps.Event_DojoAgent;
import odin.server.life.MapleMonster;
import odin.server.life.MapleLifeFactory;
import odin.server.quest.MapleQuest;
import odin.client.inventory.MapleInventoryIdentifier;
import tacos.debug.DebugLogger;
import odin.handling.world.OdinWorld;
import tacos.packet.ops.OpsFieldEffect;
import tacos.packet.ops.arg.ArgFieldEffect;
import tacos.packet.ops.OpsScriptMan;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCScriptMan;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.packet.response.wrapper.WrapCUserLocal;
import tacos.script.TacosScriptEvent;
import tacos.script.TacosScriptNPC;
import tacos.server.TacosChannel;

public abstract class OdinAbstractPlayerInteraction {

    protected MapleClient client;

    public OdinAbstractPlayerInteraction(MapleClient client) {
        this.client = client;
    }

    public final MapleClient getClient() {
        return client;
    }

    public final MapleClient getC() {
        return client;
    }

    public MapleCharacter getChar() {
        return client.getPlayer();
    }

    public TacosChannel getChannelServer() {
        return client.getChannelServer();
    }

    public final MapleCharacter getPlayer() {
        return client.getPlayer();
    }

    public final OdinEventInstanceManager getEventInstance() {
        return null;
    }

    public final void warp(final int map) {
        final MapleMap mapz = getWarpMap(map);
        try {
            client.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        } catch (Exception e) {
            client.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    }

    public final void warp_Instanced(final int map) {
        final MapleMap mapz = getMap_Instanced(map);
        try {
            client.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        } catch (Exception e) {
            client.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    }

    public final void warp(final int map, final int portal) {
        final MapleMap mapz = getWarpMap(map);
        if (portal != 0 && map == client.getPlayer().getMapId()) { //test
            final Point portalPos = new Point(client.getPlayer().getMap().getPortal(portal).getPosition());
            if (portalPos.distanceSq(getPlayer().getPosition()) < 90000.0) { //estimation
                client.getSession().write(ResCUserLocal.Teleport((byte) portal)); //until we get packet for far movement, this will do
                client.getPlayer().checkFollow();
                client.getPlayer().getMap().movePlayer(client.getPlayer(), portalPos);
            } else {
                client.getPlayer().changeMap(mapz, mapz.getPortal(portal));
            }
        } else {
            client.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
    }

    public final void warpS(final int map, final int portal) {
        final MapleMap mapz = getWarpMap(map);
        client.getPlayer().changeMap(mapz, mapz.getPortal(portal));
    }

    public final void warp(final int map, String portal) {
        final MapleMap mapz = getWarpMap(map);
        if (map == 109060000 || map == 109060002 || map == 109060004) {
            portal = mapz.getSnowballPortal();
        }
        if (map == client.getPlayer().getMapId()) { //test
            final Point portalPos = new Point(client.getPlayer().getMap().getPortal(portal).getPosition());
            if (portalPos.distanceSq(getPlayer().getPosition()) < 90000.0) { //estimation
                client.getPlayer().checkFollow();
                client.getSession().write(ResCUserLocal.Teleport((byte) client.getPlayer().getMap().getPortal(portal).getId()));
                client.getPlayer().getMap().movePlayer(client.getPlayer(), new Point(client.getPlayer().getMap().getPortal(portal).getPosition()));
            } else {
                client.getPlayer().changeMap(mapz, mapz.getPortal(portal));
            }
        } else {
            client.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
    }

    public final void warpS(final int map, String portal) {
        final MapleMap mapz = getWarpMap(map);
        if (map == 109060000 || map == 109060002 || map == 109060004) {
            portal = mapz.getSnowballPortal();
        }
        client.getPlayer().changeMap(mapz, mapz.getPortal(portal));
    }

    public final void warpMap(final int mapid, final int portal) {
        final MapleMap map = getMap(mapid);
        for (MapleCharacter chr : client.getPlayer().getMap().getCharacters()) {
            chr.changeMap(map, map.getPortal(portal));
        }
    }

    private MapleMap getWarpMap(final int map) {
        return this.client.getChannelServer().getMapFactory().getMap(map);
    }

    public final MapleMap getMap() {
        return client.getPlayer().getMap();
    }

    public final MapleMap getMap(final int map) {
        return getWarpMap(map);
    }

    public final MapleMap getMap_Instanced(final int map) {
        return getMap(map);
    }

    public void spawnMonster(final int id, final int qty) {
        spawnMob(id, qty, new Point(client.getPlayer().getPosition()));
    }

    public final void spawnMobOnMap(final int id, final int qty, final int x, final int y, final int map) {
        for (int i = 0; i < qty; i++) {
            getMap(map).spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), new Point(x, y));
        }
    }

    public final void spawnMob(final int id, final int qty, final int x, final int y) {
        spawnMob(id, qty, new Point(x, y));
    }

    public final void spawnMob(final int id, final int x, final int y) {
        spawnMob(id, 1, new Point(x, y));
    }

    private final void spawnMob(final int id, final int qty, final Point pos) {
        for (int i = 0; i < qty; i++) {
            client.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
        }
    }

    public final void killMob(int ids) {
        client.getPlayer().getMap().killMonster(ids);
    }

    public final void killAllMob() {
        client.getPlayer().getMap().killAllMonsters(true);
    }

    public final void addHP(final int delta) {
        client.getPlayer().addHP(delta);
    }

    public final int getPlayerStat(final String type) {
        if (type.equals("LVL")) {
            return client.getPlayer().getLevel();
        } else if (type.equals("STR")) {
            return client.getPlayer().getStat().getStr();
        } else if (type.equals("DEX")) {
            return client.getPlayer().getStat().getDex();
        } else if (type.equals("INT")) {
            return client.getPlayer().getStat().getInt();
        } else if (type.equals("LUK")) {
            return client.getPlayer().getStat().getLuk();
        } else if (type.equals("HP")) {
            return client.getPlayer().getStat().getHp();
        } else if (type.equals("MP")) {
            return client.getPlayer().getStat().getMp();
        } else if (type.equals("MAXHP")) {
            return client.getPlayer().getStat().getMaxHp();
        } else if (type.equals("MAXMP")) {
            return client.getPlayer().getStat().getMaxMp();
        } else if (type.equals("RAP")) {
            return client.getPlayer().getRemainingAp();
        } else if (type.equals("RSP")) {
            return client.getPlayer().getRemainingSp();
        } else if (type.equals("GID")) {
            return client.getPlayer().getGuildId();
        } else if (type.equals("GRANK")) {
            return client.getPlayer().getGuildRank();
        } else if (type.equals("ARANK")) {
            return client.getPlayer().getAllianceRank();
        } else if (type.equals("GM")) {
            return client.getPlayer().isGM() ? 1 : 0;
        } else if (type.equals("ADMIN")) {
            return client.getPlayer().isAdmin() ? 1 : 0;
        } else if (type.equals("GENDER")) {
            return client.getPlayer().getGender();
        } else if (type.equals("FACE")) {
            return client.getPlayer().getFace();
        } else if (type.equals("HAIR")) {
            return client.getPlayer().getHair();
        }
        return -1;
    }

    public final String getName() {
        return client.getPlayer().getName();
    }

    public final boolean haveItem(final int itemid, final int quantity) {
        return haveItem(itemid, quantity, false, true);
    }

    public final boolean haveItem(final int itemid, final int quantity, final boolean checkEquipped, final boolean greaterOrEquals) {
        return client.getPlayer().haveItem(itemid, quantity, checkEquipped, greaterOrEquals);
    }

    public final boolean canHold() {
        for (int i = 1; i <= 5; i++) {
            if (client.getPlayer().getInventory(MapleInventoryType.getByType((byte) i)).getNextFreeSlot() <= -1) {
                return false;
            }
        }
        return true;
    }

    public final boolean canHold(final int itemid) {
        return client.getPlayer().getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }

    public final boolean canHold(final int itemid, final int quantity) {
        return MapleInventoryManipulator.checkSpace(client, itemid, quantity, "");
    }

    public final MapleQuestStatus getQuestRecord(final int id) {
        return client.getPlayer().getQuestNAdd(MapleQuest.getInstance(id));
    }

    public final boolean isQuestActive(final int id) {
        return getQuestStatus(id) == 1;
    }

    public final boolean isQuestFinished(final int id) {
        return getQuestStatus(id) == 2;
    }

    public final void showQuestMsg(final String msg) {
        client.SendPacket(ResWrapper.showQuestMsg(msg));
    }

    public final void forceStartQuest(final int id, final String data) {
        MapleQuest.getInstance(id).forceStart(client.getPlayer(), 0, data);
    }

    public final void forceStartQuest(final int id, final int data, final boolean filler) {
        MapleQuest.getInstance(id).forceStart(client.getPlayer(), 0, filler ? String.valueOf(data) : null);
    }

    public void forceStartQuest(final int id) {
        MapleQuest.getInstance(id).forceStart(client.getPlayer(), 0, null);
    }

    public void forceCompleteQuest(final int id) {
        MapleQuest.getInstance(id).forceComplete(getPlayer(), 0);
    }

    public void spawnNpc(final int npcId) {
        client.getPlayer().getMap().spawnNpc(npcId, client.getPlayer().getPosition());
    }

    public final void spawnNpc(final int npcId, final int x, final int y) {
        client.getPlayer().getMap().spawnNpc(npcId, new Point(x, y));
    }

    public final void spawnNpc(final int npcId, final Point pos) {
        client.getPlayer().getMap().spawnNpc(npcId, pos);
    }

    public final void removeNpc(final int mapid, final int npcId) {
        client.getChannelServer().getMapFactory().getMap(mapid).removeNpc(npcId);
    }

    public final void forceStartReactor(final int mapid, final int id) {
        MapleMap map = client.getChannelServer().getMapFactory().getMap(mapid);
        MapleReactor react;

        for (final MapleMapObject remo : map.getAllReactors()) {
            react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.forceStartReactor(client);
                break;
            }
        }
    }

    public final void destroyReactor(final int mapid, final int id) {
        MapleMap map = client.getChannelServer().getMapFactory().getMap(mapid);
        MapleReactor react;

        for (final MapleMapObject remo : map.getAllReactors()) {
            react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.hitReactor(client);
                break;
            }
        }
    }

    public final void hitReactor(final int mapid, final int id) {
        MapleMap map = client.getChannelServer().getMapFactory().getMap(mapid);
        MapleReactor react;

        for (final MapleMapObject remo : map.getAllReactors()) {
            react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.hitReactor(client);
                break;
            }
        }
    }

    public final void gainNX(final int amount) {
        client.getPlayer().modifyCSPoints(1, amount, true);
    }

    public final void gainItemPeriod(final int id, final short quantity, final int period) { //period is in days
        gainItem(id, quantity, false, period, -1, "");
    }

    public final void gainItemPeriod(final int id, final short quantity, final long period, final String owner) { //period is in days
        gainItem(id, quantity, false, period, -1, owner);
    }

    public final void gainItem(final int id, final short quantity) {
        gainItem(id, quantity, false, 0, -1, "");
    }

    public final void gainItem(final int id, final short quantity, final boolean randomStats) {
        gainItem(id, quantity, randomStats, 0, -1, "");
    }

    public final void Gashapon(final int id, final short quantity) {
        IItem item_info = gainItem(id, quantity, true, 0, -1, "");
        if (item_info != null) {
            this.client.getWorld().broadcastPacket(ResWrapper.BroadCastMsgGachaponAnnounce(client.getPlayer(), item_info));
        }
    }

    public final IItem gainItem(final int id, final short quantity, final boolean randomStats, final int slots) {
        return gainItem(id, quantity, randomStats, 0, slots, "");
    }

    public final IItem gainItem(final int id, final short quantity, final long period) {
        return gainItem(id, quantity, false, period, -1, "");
    }

    public final IItem gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots) {
        return gainItem(id, quantity, randomStats, period, slots, "");
    }

    public final IItem gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots, final String owner) {
        return gainItem(id, quantity, randomStats, period, slots, owner, client);
    }

    public final IItem gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots, final String owner, final MapleClient cg) {
        IItem item_info = null;
        if (quantity >= 0) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final MapleInventoryType type = GameConstants.getInventoryType(id);

            if (!MapleInventoryManipulator.checkSpace(cg, id, quantity, "")) {
                return null;
            }
            if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(id) && !GameConstants.isBullet(id)) {
                final Equip item = (Equip) (randomStats ? ii.randomizeStats((Equip) ii.getEquipById(id)) : ii.getEquipById(id));
                if (period > 0) {
                    item.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                }
                if (slots > 0) {
                    item.setUpgradeSlots((byte) (item.getUpgradeSlots() + slots));
                }
                if (owner != null) {
                    item.setOwner(owner);
                }
                final String name = ii.getName(id);
                if (id / 10000 == 114 && name != null && name.length() > 0) { //medal
                    final String msg = "You have attained title <" + name + ">";
                    cg.getPlayer().dropMessage(-1, msg);
                    cg.getPlayer().dropMessage(5, msg);
                }
                MapleInventoryManipulator.addbyItem(cg, item.copy());
                item_info = item;
            } else {
                MapleInventoryManipulator.addById(cg, id, quantity, owner == null ? "" : owner, null, period);
            }
        } else {
            MapleInventoryManipulator.removeById(cg, GameConstants.getInventoryType(id), id, -quantity, true, false);
        }
        cg.getSession().write(WrapCUserLocal.getShowItemGain(id, quantity, true));
        return item_info;
    }

    public final void changeMusic(final String songName) {
        getPlayer().getMap().broadcastMessage(ResWrapper.musicChange(songName));
    }

    // npc/9201006.js
    public final void worldMessage(final int type, final String message) {
        this.client.getWorld().broadcastPacket(ResWrapper.BroadCastMsgEvent(message));
    }

    public final void mapMessage(final String message) {
        client.getPlayer().getMap().broadcastMessage(ResWrapper.BroadCastMsgEvent(message));
    }

    public final void guildMessage(final String message) {
        OdinWorld.Guild.guildPacket(getPlayer().getGuildId(), ResWrapper.BroadCastMsgEvent(message));
    }

    public final void playerMessage(final int type, final String message) {
        DebugLogger.DebugLog("playerMessage is called.");
        client.SendPacket(ResWrapper.BroadCastMsg_SN(type, message));
    }

    public final void mapMessage(final int type, final String message) {
        DebugLogger.DebugLog("mapMessage is called.");
        client.getPlayer().getMap().broadcastMessage(ResWrapper.BroadCastMsg_SN(type, message));
    }

    public final void guildMessage(final int type, final String message) {
        DebugLogger.DebugLog("guildMessage is called.");
        if (getPlayer().getGuildId() > 0) {
            OdinWorld.Guild.guildPacket(getPlayer().getGuildId(), ResWrapper.BroadCastMsg_SN(type, message));
        }
    }

    public final MapleGuild getGuild() {
        return getGuild(getPlayer().getGuildId());
    }

    public final MapleGuild getGuild(int guildid) {
        return OdinWorld.Guild.getGuild(guildid);
    }

    public final MapleParty getParty() {
        return client.getPlayer().getParty();
    }

    public final boolean isLeader() {
        if (getParty() == null) {
            return false;
        }
        return getParty().getLeader().getId() == client.getPlayer().getId();
    }

    public final boolean isAllPartyMembersAllowedJob(final int job) {
        if (client.getPlayer().getParty() == null) {
            return false;
        }
        for (final MaplePartyCharacter mem : client.getPlayer().getParty().getMembers()) {
            if (mem.getJobId() / 100 != job) {
                return false;
            }
        }
        return true;
    }

    public final boolean allMembersHere() {
        if (client.getPlayer().getParty() == null) {
            return false;
        }
        for (final MaplePartyCharacter mem : client.getPlayer().getParty().getMembers()) {
            final MapleCharacter chr = client.getPlayer().getMap().getCharacterById(mem.getId());
            if (chr == null) {
                return false;
            }
        }
        return true;
    }

    public final void warpParty(final int mapId) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            warp(mapId, 0);
            return;
        }
        final MapleMap target = getMap(mapId);

        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
    }

    public final void warpParty(final int mapId, final int portal) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            if (portal < 0) {
                warp(mapId);
            } else {
                warp(mapId, portal);
            }
            return;
        }
        final boolean rand = portal < 0;
        final MapleMap target = getMap(mapId);

        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                if (rand) {
                    try {
                        curChar.changeMap(target, target.getPortal(Randomizer.nextInt(target.getPortals().size())));
                    } catch (Exception e) {
                        curChar.changeMap(target, target.getPortal(0));
                    }
                } else {
                    curChar.changeMap(target, target.getPortal(portal));
                }
            }
        }
    }

    public final void warpParty_Instanced(final int mapId) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            warp_Instanced(mapId);
            return;
        }
        final MapleMap target = getMap_Instanced(mapId);

        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
    }

    public void gainMeso(int gain) {
        client.getPlayer().gainMeso(gain, true, false, true);
    }

    public void gainExp(int gain) {
        client.getPlayer().gainExp(gain, true, true, true);
    }

    public void gainExpR(int gain) {
        client.getPlayer().gainExp(gain * client.getPlayer().getChannelServer().getExpRate(), true, true, true);
    }

    public final void givePartyItems(final int id, final short quantity, final List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            if (quantity >= 0) {
                MapleInventoryManipulator.addById(chr.getClient(), id, quantity);
            } else {
                MapleInventoryManipulator.removeById(chr.getClient(), GameConstants.getInventoryType(id), id, -quantity, true, false);
            }
            chr.getClient().getSession().write(WrapCUserLocal.getShowItemGain(id, quantity, true));
        }
    }

    public final void givePartyItems(final int id, final short quantity) {
        givePartyItems(id, quantity, false);
    }

    public final void givePartyItems(final int id, final short quantity, final boolean removeAll) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainItem(id, (short) (removeAll ? -getPlayer().itemQuantity(id) : quantity));
            return;
        }

        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                gainItem(id, (short) (removeAll ? -curChar.itemQuantity(id) : quantity), false, 0, 0, "", curChar.getClient());
            }
        }
    }

    public final void givePartyExp(final int amount, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            chr.gainExp(amount * chr.getChannelServer().getExpRate(), true, true, true);
        }
    }

    public final void givePartyExp(final int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainExp(amount * client.getPlayer().getChannelServer().getExpRate());
            return;
        }
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.gainExp(amount * client.getPlayer().getChannelServer().getExpRate(), true, true, true);
            }
        }
    }

    public final void givePartyNX(final int amount, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            chr.modifyCSPoints(1, amount, true);
        }
    }

    public final void givePartyNX(final int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainNX(amount);
            return;
        }
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.modifyCSPoints(1, amount, true);
            }
        }
    }

    public final void endPartyQuest(final int amount, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            chr.endPartyQuest(amount);
        }
    }

    public final void endPartyQuest(final int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            getPlayer().endPartyQuest(amount);
            return;
        }
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.endPartyQuest(amount);
            }
        }
    }

    public final void removeFromParty(final int id, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            final int possesed = chr.getInventory(GameConstants.getInventoryType(id)).countById(id);
            if (possesed > 0) {
                MapleInventoryManipulator.removeById(client, GameConstants.getInventoryType(id), id, possesed, true, false);
                chr.getClient().getSession().write(WrapCUserLocal.getShowItemGain(id, (short) -possesed, true));
            }
        }
    }

    public final void removeFromParty(final int id) {
        givePartyItems(id, (short) 0, true);
    }

    public final void useSkill(final int skill, final int level) {
        if (level <= 0) {
            return;
        }
        SkillFactory.getSkill(skill).getEffect(level).applyTo(client.getPlayer());
    }

    public final void useItem(final int id) {
        MapleItemInformationProvider.getInstance().getItemEffect(id).applyTo(client.getPlayer());
        client.getSession().write(ResWrapper.getStatusMsg(id));
    }

    public final void cancelItem(final int id) {
        client.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(id), false, -1);
    }

    public final int getMorphState() {
        return client.getPlayer().getMorphState();
    }

    public final void removeAll(final int id) {
        client.getPlayer().removeAll(id);
    }

    public final void gainCloseness(final int closeness, final int index) {
        final MaplePet pet = getPlayer().getPet(index);
        if (pet != null) {
            pet.setCloseness(pet.getCloseness() + closeness);
            getClient().getSession().write(ResWrapper.updatePet(pet, getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition())));
        }
    }

    public final void gainClosenessAll(final int closeness) {
        for (final MaplePet pet : getPlayer().getPets()) {
            if (pet != null) {
                pet.setCloseness(pet.getCloseness() + closeness);
                getClient().getSession().write(ResWrapper.updatePet(pet, getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition())));
            }
        }
    }

    public final void resetMap(final int mapid) {
        getMap(mapid).resetFully();
    }

    public final void openNpc(final int id) {
        TacosScriptNPC.getInstance().start(getClient(), id);
    }

    public final void openNpc(final MapleClient cg, final int id) {
        TacosScriptNPC.getInstance().start(cg, id);
    }

    public final int getMapId() {
        return client.getPlayer().getMap().getId();
    }

    public final boolean haveMonster(final int mobid) {
        for (MapleMapObject obj : client.getPlayer().getMap().getAllMonsters()) {
            final MapleMonster mob = (MapleMonster) obj;
            if (mob.getId() == mobid) {
                return true;
            }
        }
        return false;
    }

    public final int getChannelNumber() {
        return client.getChannelId();
    }

    public final int getMonsterCount(final int mapid) {
        return client.getChannelServer().getMapFactory().getMap(mapid).getNumMonsters();
    }

    public final void teachSkill(final int id, final byte level, final byte masterlevel) {
        getPlayer().changeSkillLevel(SkillFactory.getSkill(id), level, masterlevel);
    }

    public final void teachSkill(final int id, byte level) {
        final ISkill skil = SkillFactory.getSkill(id);
        if (getPlayer().getSkillLevel(skil) > level) {
            level = getPlayer().getSkillLevel(skil);
        }
        getPlayer().changeSkillLevel(skil, level, skil.getMaxLevel());
    }

    public final void dojo_getUp() {
        client.SendPacket(ResWrapper.updateInfoQuest(1207, "pt=1;min=4;belt=1;tuto=1")); //todo
        client.SendPacket(WrapCUserLocal.EffectLocal(OpsUserEffect.UserEffect_PlayPortalSE));
        client.SendPacket(ResCUserLocal.Teleport((byte) 6));
    }

    // Oribs PQ, 920010700
    public void instantwarp(int map_id, int porta_id) {
        client.SendPacket(ResCUserLocal.Teleport((byte) porta_id));
    }

    public final boolean dojoAgent_NextMap(final boolean dojo, final boolean fromresting) {
        if (dojo) {
            return Event_DojoAgent.warpNextMap(client.getPlayer(), fromresting);
        }
        return Event_DojoAgent.warpNextMap_Agent(client.getPlayer(), fromresting);
    }

    public final int dojo_getPts() {
        return client.getPlayer().getDojo();
    }

    public OdinMapleEvent getEvent(String loc) {
        return null;
    }

    public final int getSavedLocation(final String loc) {
        final Integer ret = client.getPlayer().getSavedLocation(SavedLocationType.fromString(loc));
        if (ret == null || ret == -1) {
            return 100000000;
        }
        return ret;
    }

    public final void saveLocation(final String loc) {
        client.getPlayer().saveLocation(SavedLocationType.fromString(loc));
    }

    public final void saveReturnLocation(final String loc) {
        client.getPlayer().saveLocation(SavedLocationType.fromString(loc), client.getPlayer().getMap().getReturnMap().getId());
    }

    public final void clearSavedLocation(final String loc) {
        client.getPlayer().clearSavedLocation(SavedLocationType.fromString(loc));
    }

    public final void summonMsg(final String msg) {
        if (!client.getPlayer().hasSummon()) {
            playerSummonHint(true);
        }
        client.getSession().write(ResCUserLocal.summonMessage(msg));
    }

    public final void summonMsg(final int type) {
        if (!client.getPlayer().hasSummon()) {
            playerSummonHint(true);
        }
        client.getSession().write(ResCUserLocal.summonMessage(type));
    }

    public final void showInstruction(final String msg, final int width, final int height) {
        client.SendPacket(ResCUserLocal.BalloonMsg(msg, width, height));
    }

    public final void playerSummonHint(final boolean summon) {
        client.getPlayer().setHasSummon(summon);
        client.getSession().write(ResCUserLocal.summonHelper(summon));
    }

    public final String getInfoQuest(final int id) {
        return client.getPlayer().getInfoQuest(id);
    }

    public final void updateInfoQuest(final int id, final String data) {
        client.getPlayer().updateInfoQuest(id, data);
    }

    public final boolean getEvanIntroState(final String data) {
        return getInfoQuest(22013).equals(data);
    }

    public final void updateEvanIntroState(final String data) {
        updateInfoQuest(22013, data);
    }

    public final void Aran_Start() {
        client.getSession().write(ResCField.FieldEffect(new ArgFieldEffect(OpsFieldEffect.FieldEffect_Sound, "Aran/balloon")));
    }

    public final void evanTutorial(final String data, final int v1) {
        client.getSession().write(ResCScriptMan.getEvanTutorial(data));
    }

    public final void AranTutInstructionalBubble(final String data) {
        client.getSession().write(ResCUserLocal.AranTutInstructionalBalloon(data));
    }

    public final void ShowWZEffect(final String data) {
        client.getSession().write(ResCUserLocal.AranTutInstructionalBalloon(data));
    }

    public final void showWZEffect(final String data) {
        client.getSession().write(ResCUserLocal.ShowWZEffect(data));
    }

    public final void EarnTitleMsg(final String data) {
        client.getSession().write(ResCWvsContext.getTopMsg(data));
    }

    public final void MovieClipIntroUI(final boolean enabled) {
        client.getSession().write(ResCUserLocal.IntroDisableUI(enabled));
        client.getSession().write(ResCUserLocal.IntroLock(enabled));
    }

    public MapleInventoryType getInvType(int i) {
        return MapleInventoryType.getByType((byte) i);
    }

    public String getItemName(final int id) {
        return MapleItemInformationProvider.getInstance().getName(id);
    }

    public void gainPet(int id, String name, int level, int closeness, int fullness, long period) {
        if (id > 5000200 || id < 5000000) {
            id = 5000000;
        }
        if (level > 30) {
            level = 30;
        }
        if (closeness > 30000) {
            closeness = 30000;
        }
        if (fullness > 100) {
            fullness = 100;
        }
        try {
            MapleInventoryManipulator.addById(client, id, (short) 1, "", MaplePet.createPet(id, name, level, closeness, fullness, MapleInventoryIdentifier.getInstance(), id == 5000054 ? (int) period : 0), 45);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public void removeSlot(int invType, byte slot, short quantity) {
        MapleInventoryManipulator.removeFromSlot(client, getInvType(invType), slot, quantity, true);
    }

    public void gainGP(final int gp) {
        if (getPlayer().getGuildId() <= 0) {
            return;
        }
        OdinWorld.Guild.gainGP(getPlayer().getGuildId(), gp); //1 for
    }

    public int getGP() {
        if (getPlayer().getGuildId() <= 0) {
            return 0;
        }
        return OdinWorld.Guild.getGP(getPlayer().getGuildId()); //1 for
    }

    public void showMapEffect(String path) {
        getClient().getSession().write(ResCField.FieldEffect(new ArgFieldEffect(OpsFieldEffect.FieldEffect_Screen, path)));
    }

    public int itemQuantity(int itemid) {
        return getPlayer().itemQuantity(itemid);
    }

    public boolean isAllReactorState(final int reactorId, final int state) {
        boolean ret = false;
        for (MapleReactor r : getMap().getAllReactors()) {
            if (r.getReactorId() == reactorId) {
                ret = r.getState() == state;
            }
        }
        return ret;
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public void spawnMonster(int id) {
        spawnMonster(id, 1, new Point(getPlayer().getPosition()));
    }

    // summon one monster, remote location
    public void spawnMonster(int id, int x, int y) {
        spawnMonster(id, 1, new Point(x, y));
    }

    // multiple monsters, remote location
    public void spawnMonster(int id, int qty, int x, int y) {
        spawnMonster(id, qty, new Point(x, y));
    }

    // handler for all spawnMonster
    public void spawnMonster(int id, int qty, Point pos) {
        for (int i = 0; i < qty; i++) {
            getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
        }
    }

    public void sendNPCText(final String text, final int npc) {
        getMap().broadcastMessage(ResCScriptMan.ScriptMessage(npc, OpsScriptMan.SM_SAY, (byte) 0, text, false, false));
    }

    // event script compatibility
    public byte getQuestStatus(int id) {
        return client.getPlayer().getQuestStatus(id);
    }

    public int getJob() {
        return client.getPlayer().getJob();
    }

    public boolean haveItem(int itemid) {
        return haveItem(itemid, 1);
    }

    public int getPlayerCount(int mapid) {
        return client.getChannelServer().getMapFactory().getMap(mapid).getCharactersSize();
    }

    public void playerMessage(String message) {
        client.SendPacket(ResWrapper.BroadCastMsgEvent(message));
    }

    public OdinEventManager getEventManager(String event) {
        return TacosScriptEvent.getInstance().getEventManager(event);
    }

    public OdinEventInstanceManager getDisconnected(String event) {
        return null;
    }

}
