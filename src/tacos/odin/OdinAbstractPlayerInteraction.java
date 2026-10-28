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
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import tacos.server.map.object.TacosPet;
import tacos.server.map.object.TacosMonster;
import tacos.server.map.object.TacosReactor;
import java.util.List;
import odin.client.inventory.Equip;
import odin.constants.GameConstants;
import odin.client.Skill;
import tacos.client.TacosClient;
import odin.client.inventory.MapleInventoryType;
import odin.client.MapleQuestStatus;
import odin.client.inventory.Item;
import odin.handling.world.MapleParty;
import odin.handling.world.MapleParty.MaplePartyCharacter;
import odin.handling.world.guild.MapleGuild;
import odin.server.Randomizer;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.maps.SavedLocationType;
import odin.server.maps.Event_DojoAgent;
import odin.server.quest.MapleQuest;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsFieldEffect;
import tacos.packet.response.builder.PB_FieldEffect;
import tacos.packet.ops.OpsScriptMan;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCScriptMan;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_UserEffect;
import tacos.packet.ops.OpsMessage;
import tacos.packet.response.builder.PB_Message;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.response.builder.PB_BroadcastMsg;
import tacos.packet.response.builder.PB_InvOp;
import tacos.script.TacosScriptEvent;
import tacos.script.TacosScriptNPC;
import tacos.server.TacosChannel;
import tacos.server.TacosWorld;
import tacos.wz.WzXML;

public abstract class OdinAbstractPlayerInteraction {

    protected TacosClient client;

    public OdinAbstractPlayerInteraction(TacosClient client) {
        this.client = client;
    }

    public TacosClient getClient() {
        return client;
    }

    public TacosClient getC() {
        return client;
    }

    public TacosCharacter getChar() {
        return client.getPlayer();
    }

    public TacosChannel getChannelServer() {
        return client.getChannelServer();
    }

    public TacosCharacter getPlayer() {
        return client.getPlayer();
    }

    public OdinEventInstanceManager getEventInstance() {
        return null;
    }

    public void warp(int map) {
        TacosCharacter chr = client.getPlayer();
        TacosMap mapz = getWarpMap(map);
        try {
            chr.changeMapPortal(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        } catch (Exception e) {
            chr.changeMapPortal(mapz, mapz.getPortal(0));
        }
    }

    public void warp_Instanced(int map) {
        TacosCharacter chr = client.getPlayer();
        TacosMap mapz = getMap_Instanced(map);
        try {
            chr.changeMapPortal(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        } catch (Exception e) {
            chr.changeMapPortal(mapz, mapz.getPortal(0));
        }
    }

    public void warp(int map, int portal) {
        TacosCharacter chr = client.getPlayer();
        TacosMap mapz = getWarpMap(map);
        if (portal != 0 && map == chr.getMapId()) { //test
            Point portalPos = new Point(chr.getMap().getPortal(portal).getPosition());
            if (portalPos.distanceSq(getPlayer().getPosition()) < 90000.0) { //estimation
                client.SendPacket(ResCUserLocal.UserTeleport((byte) portal)); //until we get packet for far movement, this will do
            } else {
                chr.changeMapPortal(mapz, mapz.getPortal(portal));
            }
        } else {
            chr.changeMapPortal(mapz, mapz.getPortal(portal));
        }
    }

    public void warpS(int map, int portal) {
        TacosMap mapz = getWarpMap(map);
        client.getPlayer().changeMapPortal(mapz, mapz.getPortal(portal));
    }

    public void warp(int map, String portal) {
        TacosCharacter chr = client.getPlayer();
        TacosMap mapz = getWarpMap(map);
        if (map == 109060000 || map == 109060002 || map == 109060004) {
            portal = mapz.getSnowballPortal();
        }
        if (map == chr.getMapId()) { //test
            Point portalPos = new Point(chr.getMap().getPortal(portal).getPosition());
            if (portalPos.distanceSq(getPlayer().getPosition()) < 90000.0) { //estimation
                client.SendPacket(ResCUserLocal.UserTeleport((byte) chr.getMap().getPortal(portal).getId()));
            } else {
                chr.changeMapPortal(mapz, mapz.getPortal(portal));
            }
        } else {
            chr.changeMapPortal(mapz, mapz.getPortal(portal));
        }
    }

    public void warpS(int map, String portal) {
        TacosMap mapz = getWarpMap(map);
        if (map == 109060000 || map == 109060002 || map == 109060004) {
            portal = mapz.getSnowballPortal();
        }
        client.getPlayer().changeMapPortal(mapz, mapz.getPortal(portal));
    }

    public void warpMap(int mapid, int portal) {
        TacosMap map = getMap(mapid);
        for (TacosCharacter chr : client.getPlayer().getMap().getAllPlayers()) {
            chr.changeMapPortal(map, map.getPortal(portal));
        }
    }

    private TacosMap getWarpMap(int map) {
        return getPlayer().findMap(map);
    }

    public TacosMap getMap() {
        return client.getPlayer().getMap();
    }

    public TacosMap getMap(int map) {
        return getWarpMap(map);
    }

    public TacosMap getMap_Instanced(int map) {
        return getMap(map);
    }

    public void spawnMonster(int id, int qty) {
        spawnMob(id, qty, new Point(client.getPlayer().getPosition()));
    }

    public void spawnMobOnMap(int id, int qty, int x, int y, int map) {
        for (int i = 0; i < qty; i++) {
            getMap(map).spawnMonsterOnGroundBelow(WzXML.MOB.createMob(id), new Point(x, y));
        }
    }

    public void spawnMob(int id, int qty, int x, int y) {
        spawnMob(id, qty, new Point(x, y));
    }

    public void spawnMob(int id, int x, int y) {
        spawnMob(id, 1, new Point(x, y));
    }

    private void spawnMob(int id, int qty, Point pos) {
        for (int i = 0; i < qty; i++) {
            client.getPlayer().getMap().spawnMonsterOnGroundBelow(WzXML.MOB.createMob(id), pos);
        }
    }

    public void killMob(int ids) {
        client.getPlayer().getMap().killMonster(ids);
    }

    public void killAllMob() {
        client.getPlayer().getMap().killAllMonsters(true);
    }

    public void addHP(int delta) {
        client.getPlayer().addHP(delta);
    }

    public int getPlayerStat(String type) {
        TacosCharacter chr = client.getPlayer();
        if (type.equals("LVL")) {
            return chr.getLevel();
        } else if (type.equals("STR")) {
            return chr.getStat().getStr();
        } else if (type.equals("DEX")) {
            return chr.getStat().getDex();
        } else if (type.equals("INT")) {
            return chr.getStat().getInt();
        } else if (type.equals("LUK")) {
            return chr.getStat().getLuk();
        } else if (type.equals("HP")) {
            return chr.getStat().getHp();
        } else if (type.equals("MP")) {
            return chr.getStat().getMp();
        } else if (type.equals("MAXHP")) {
            return chr.getStat().getMaxHp();
        } else if (type.equals("MAXMP")) {
            return chr.getStat().getMaxMp();
        } else if (type.equals("RAP")) {
            return chr.getRemainingAp();
        } else if (type.equals("RSP")) {
            return chr.getRemainingSp();
        } else if (type.equals("GID")) {
            return chr.getGuildId();
        } else if (type.equals("GRANK")) {
            return chr.getGuildRank();
        } else if (type.equals("ARANK")) {
            return chr.getAllianceRank();
        } else if (type.equals("GM")) {
            return chr.isGM() ? 1 : 0;
        } else if (type.equals("ADMIN")) {
            return chr.isAdmin() ? 1 : 0;
        } else if (type.equals("GENDER")) {
            return chr.getGender();
        } else if (type.equals("FACE")) {
            return chr.getFace();
        } else if (type.equals("HAIR")) {
            return chr.getHair();
        }
        return -1;
    }

    public String getName() {
        return client.getPlayer().getName();
    }

    public boolean haveItem(int itemid, int quantity) {
        return haveItem(itemid, quantity, false, true);
    }

    public boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        return client.getPlayer().haveItem(itemid, quantity, checkEquipped, greaterOrEquals);
    }

    public boolean canHold() {
        for (int i = 1; i <= 5; i++) {
            if (client.getPlayer().getInventory(MapleInventoryType.getByType((byte) i)).getNextFreeSlot() <= -1) {
                return false;
            }
        }
        return true;
    }

    public boolean canHold(int itemid) {
        return client.getPlayer().getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }

    public boolean canHold(int itemid, int quantity) {
        return MapleInventoryManipulator.checkSpace(client.getPlayer(), itemid, quantity, "");
    }

    public MapleQuestStatus getQuestRecord(int id) {
        return client.getPlayer().getQuestNAdd(MapleQuest.getInstance(id));
    }

    public boolean isQuestActive(int id) {
        return getQuestStatus(id) == 1;
    }

    public boolean isQuestFinished(int id) {
        return getQuestStatus(id) == 2;
    }

    public void showQuestMsg(String msg) {
        client.SendPacket(ResCWvsContext.Message(OpsMessage.MS_SystemMessage, PB_Message.builder().str(msg).build()));
    }

    public void forceStartQuest(int id, String data) {
        MapleQuest.getInstance(id).forceStart(client.getPlayer(), 0, data);
    }

    public void forceStartQuest(int id, int data, boolean filler) {
        MapleQuest.getInstance(id).forceStart(client.getPlayer(), 0, filler ? String.valueOf(data) : null);
    }

    public void forceStartQuest(int id) {
        MapleQuest.getInstance(id).forceStart(client.getPlayer(), 0, null);
    }

    public void forceCompleteQuest(int id) {
        MapleQuest.getInstance(id).forceComplete(getPlayer(), 0);
    }

    public void spawnNpc(int npcId) {
        TacosCharacter chr = client.getPlayer();
        chr.getMap().spawnNpc(npcId, chr.getPosition());
    }

    public void spawnNpc(int npcId, int x, int y) {
        client.getPlayer().getMap().spawnNpc(npcId, new Point(x, y));
    }

    public void spawnNpc(int npcId, Point pos) {
        client.getPlayer().getMap().spawnNpc(npcId, pos);
    }

    public void removeNpc(int mapid, int npcId) {
        getPlayer().findMap(mapid).removeNPCById(npcId);
    }

    public void forceStartReactor(int mapid, int id) {
        TacosMap map = getPlayer().findMap(mapid);
        TacosReactor react;

        for (Object remo : map.getAllReactors()) {
            react = (TacosReactor) remo;
            if (react.getId() == id) {
                react.forceStartReactor(client);
                break;
            }
        }
    }

    public void destroyReactor(int mapid, int id) {
        TacosMap map = getPlayer().findMap(mapid);
        TacosReactor react;

        for (Object remo : map.getAllReactors()) {
            react = (TacosReactor) remo;
            if (react.getId() == id) {
                react.hitReactor(client);
                break;
            }
        }
    }

    public void hitReactor(int mapid, int id) {
        TacosMap map = getPlayer().findMap(mapid);
        TacosReactor react;

        for (Object remo : map.getAllReactors()) {
            react = (TacosReactor) remo;
            if (react.getId() == id) {
                react.hitReactor(client);
                break;
            }
        }
    }

    public void gainNX(int amount) {
        client.getPlayer().modifyCSPoints(1, amount, true);
    }

    public void gainItemPeriod(int id, short quantity, int period) { //period is in days
        gainItem(id, quantity, false, period, -1, "");
    }

    public void gainItemPeriod(int id, short quantity, long period, String owner) { //period is in days
        gainItem(id, quantity, false, period, -1, owner);
    }

    public void gainItem(int id, short quantity) {
        gainItem(id, quantity, false, 0, -1, "");
    }

    public void gainItem(int id, short quantity, boolean randomStats) {
        gainItem(id, quantity, randomStats, 0, -1, "");
    }

    public void Gashapon(int id, short quantity) {
        Item item_info = gainItem(id, quantity, true, 0, -1, "");
        if (item_info != null) {
            this.client.getWorld().broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_GACHAPONANNOUNCE, PB_BroadcastMsg.builder().chr(client.getPlayer()).message("をガシャポンで手に入れました。おめでとうございます！").item(item_info).build()));
        }
    }

    public Item gainItem(int id, short quantity, boolean randomStats, int slots) {
        return gainItem(id, quantity, randomStats, 0, slots, "");
    }

    public Item gainItem(int id, short quantity, long period) {
        return gainItem(id, quantity, false, period, -1, "");
    }

    public Item gainItem(int id, short quantity, boolean randomStats, long period, int slots) {
        return gainItem(id, quantity, randomStats, period, slots, "");
    }

    public Item gainItem(int id, short quantity, boolean randomStats, long period, int slots, String owner) {
        return gainItem(id, quantity, randomStats, period, slots, owner, getPlayer());
    }

    public Item gainItem(int id, short quantity, boolean randomStats, long period, int slots, String owner, TacosCharacter chr) {
        Item item_info = null;
        if (quantity >= 0) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            MapleInventoryType type = GameConstants.getInventoryType(id);

            if (!MapleInventoryManipulator.checkSpace(chr, id, quantity, "")) {
                return null;
            }
            if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(id) && !GameConstants.isBullet(id)) {
                Equip item = randomStats ? ii.getEquipById(id) : ii.getDefaultEquipById(id);
                if (period > 0) {
                    item.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                }
                if (slots > 0) {
                    item.setRUC((byte) (item.getRUC() + slots));
                }
                if (owner != null) {
                    item.setOwner(owner);
                }
                String name = ii.getName(id);
                if (id / 10000 == 114 && name != null && name.length() > 0) { //medal
                    String msg = "You have attained title <" + name + ">";
                    chr.dropMessage(-1, msg);
                    chr.Notice(msg);
                }
                MapleInventoryManipulator.addbyItem(chr, item.copy());
                item_info = item;
            } else {
                MapleInventoryManipulator.addById(chr, id, quantity, owner == null ? "" : owner, null, period);
            }
        } else {
            MapleInventoryManipulator.removeById(chr, GameConstants.getInventoryType(id), id, -quantity, true, false);
        }

        PB_UserEffect pb = PB_UserEffect.builder()
                .item_id(id)
                .item_quantity(quantity)
                .build();
        chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_Quest, pb));
        return item_info;
    }

    public void changeMusic(String songName) {
        getPlayer().getMap().setChangeBGM(songName);
    }

    // npc/9201006.js
    public void worldMessage(int type, String message) {
        this.client.getWorld().broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message(message).build()));
    }

    public void mapMessage(String message) {
        client.getPlayer().getMap().broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message(message).build()));
    }

    public void guildMessage(String message) {
        client.getWorld().getGuild().guildPacket(getPlayer().getGuildId(), ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message(message).build()));
    }

    public void playerMessage(int type, String message) {
        DebugLogger.DebugLog("playerMessage is called.");
        client.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.find((byte) type), PB_BroadcastMsg.builder().message(message).build()));
    }

    public void mapMessage(int type, String message) {
        DebugLogger.DebugLog("mapMessage is called.");
        client.getPlayer().getMap().broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.find((byte) type), PB_BroadcastMsg.builder().message(message).build()));
    }

    public void guildMessage(int type, String message) {
        DebugLogger.DebugLog("guildMessage is called.");
        if (getPlayer().getGuildId() > 0) {
            client.getWorld().getGuild().guildPacket(getPlayer().getGuildId(), ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.find((byte) type), PB_BroadcastMsg.builder().message(message).build()));
        }
    }

    public MapleGuild getGuild() {
        return getGuild(getPlayer().getGuildId());
    }

    public MapleGuild getGuild(int guildid) {
        return client.getWorld().getGuild().getGuild(guildid);
    }

    public MapleParty getParty() {
        return client.getPlayer().getParty();
    }

    public boolean isLeader() {
        if (getParty() == null) {
            return false;
        }
        return getParty().getLeader().getId() == client.getPlayer().getId();
    }

    public boolean isAllPartyMembersAllowedJob(int job) {
        TacosCharacter chr = client.getPlayer();
        if (chr.getParty() == null) {
            return false;
        }
        for (MaplePartyCharacter mem : chr.getParty().getMembers()) {
            if (mem.getJobId() / 100 != job) {
                return false;
            }
        }
        return true;
    }

    public boolean allMembersHere() {
        TacosCharacter player = client.getPlayer();
        if (player.getParty() == null) {
            return false;
        }
        for (MaplePartyCharacter mem : player.getParty().getMembers()) {
            TacosCharacter chr = player.getMap().getPlayerById(mem.getId());
            if (chr == null) {
                return false;
            }
        }
        return true;
    }

    public void warpParty(int mapId) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            warp(mapId, 0);
            return;
        }
        TacosMap target = getMap(mapId);

        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            TacosCharacter player = getMap().getPlayerById(chr.getId());
            if (player != null) {
                player.changeMapPortal(target, target.getPortal(0));
            }
        }
    }

    public void warpParty(int mapId, int portal) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            if (portal < 0) {
                warp(mapId);
            } else {
                warp(mapId, portal);
            }
            return;
        }
        boolean rand = portal < 0;
        TacosMap target = getMap(mapId);

        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            TacosCharacter player = getMap().getPlayerById(chr.getId());
            if (player != null) {
                if (rand) {
                    try {
                        player.changeMapPortal(target, target.getPortal(Randomizer.nextInt(target.getPortals().size())));
                    } catch (Exception e) {
                        player.changeMapPortal(target, target.getPortal(0));
                    }
                } else {
                    player.changeMapPortal(target, target.getPortal(portal));
                }
            }
        }
    }

    public void warpParty_Instanced(int mapId) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            warp_Instanced(mapId);
            return;
        }
        TacosMap target = getMap_Instanced(mapId);

        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            TacosCharacter player = getMap().getPlayerById(chr.getId());
            if (player != null) {
                player.changeMapPortal(target, target.getPortal(0));
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
        TacosCharacter chr = client.getPlayer();
        chr.gainExp(gain * chr.getChannelServer().getExpRate(), true, true, true);
    }

    public void givePartyItems(int id, short quantity, List<TacosCharacter> party) {
        for (TacosCharacter chr : party) {
            if (quantity >= 0) {
                MapleInventoryManipulator.addById(chr, id, quantity);
            } else {
                MapleInventoryManipulator.removeById(chr, GameConstants.getInventoryType(id), id, -quantity, true, false);
            }

            PB_UserEffect pb = PB_UserEffect.builder()
                    .item_id(id)
                    .item_quantity(quantity)
                    .build();
            chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_Quest, pb));
        }
    }

    public void givePartyItems(int id, short quantity) {
        givePartyItems(id, quantity, false);
    }

    public void givePartyItems(int id, short quantity, boolean removeAll) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainItem(id, (short) (removeAll ? -getPlayer().itemQuantity(id) : quantity));
            return;
        }

        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            TacosCharacter player = getMap().getPlayerById(chr.getId());
            if (player != null) {
                gainItem(id, (short) (removeAll ? -player.itemQuantity(id) : quantity), false, 0, 0, "", player);
            }
        }
    }

    public void givePartyExp(int amount, List<TacosCharacter> party) {
        for (TacosCharacter chr : party) {
            chr.gainExp(amount * chr.getChannelServer().getExpRate(), true, true, true);
        }
    }

    public void givePartyExp(int amount) {
        TacosCharacter character = client.getPlayer();
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainExp(amount * character.getChannelServer().getExpRate());
            return;
        }
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            TacosCharacter player = getMap().getPlayerById(chr.getId());
            if (player != null) {
                player.gainExp(amount * character.getChannelServer().getExpRate(), true, true, true);
            }
        }
    }

    public void givePartyNX(int amount, List<TacosCharacter> party) {
        for (TacosCharacter chr : party) {
            chr.modifyCSPoints(1, amount, true);
        }
    }

    public void givePartyNX(int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainNX(amount);
            return;
        }
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            TacosCharacter player = getMap().getPlayerById(chr.getId());
            if (player != null) {
                player.modifyCSPoints(1, amount, true);
            }
        }
    }

    public void endPartyQuest(int amount, List<TacosCharacter> party) {
        for (TacosCharacter chr : party) {
            chr.endPartyQuest(amount);
        }
    }

    public void endPartyQuest(int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            getPlayer().endPartyQuest(amount);
            return;
        }
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            TacosCharacter player = getMap().getPlayerById(chr.getId());
            if (player != null) {
                player.endPartyQuest(amount);
            }
        }
    }

    public void removeFromParty(int id, List<TacosCharacter> party) {
        for (TacosCharacter chr : party) {
            int possesed = chr.getInventory(GameConstants.getInventoryType(id)).countById(id);
            if (possesed > 0) {
                MapleInventoryManipulator.removeById(getPlayer(), GameConstants.getInventoryType(id), id, possesed, true, false);

                PB_UserEffect pb = PB_UserEffect.builder()
                        .item_id(id)
                        .item_quantity(-possesed)
                        .build();
                chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_Quest, pb));
            }
        }
    }

    public void removeFromParty(int id) {
        givePartyItems(id, (short) 0, true);
    }

    public void useSkill(int skill, int level) {
        if (level <= 0) {
            return;
        }
        WzXML.SKILL.getSkill().get(skill).getEffect(level).applyTo(client.getPlayer());
    }

    public void useItem(int id) {
        MapleItemInformationProvider.getInstance().getItemEffect(id).applyTo(client.getPlayer());
        client.SendPacket(ResCWvsContext.Message(OpsMessage.MS_GiveBuffMessage, PB_Message.builder().ItemID(id).build()));
    }

    public void cancelItem(int id) {
    }

    public int getMorphState() {
        return 0;
    }

    public void removeAll(int id) {
        client.getPlayer().removeAll(id);
    }

    public void gainCloseness(int closeness, int index) {
        TacosPet pet = getPlayer().getPetByIndex(index);
        if (pet != null) {
            pet.setCloseness(pet.getCloseness() + closeness);
            getClient().SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.CASH, getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition())).build()));
        }
    }

    public void gainClosenessAll(int closeness) {
        for (TacosPet pet : getPlayer().getPets()) {
            if (pet != null) {
                pet.setCloseness(pet.getCloseness() + closeness);
                getClient().SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.CASH, getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition())).build()));
            }
        }
    }

    public void resetMap(int mapid) {
        getMap(mapid).resetFully();
    }

    public void openNpc(int id) {
        TacosScriptNPC.getInstance().start(getClient(), id);
    }

    public void openNpc(TacosClient target, int id) {
        TacosScriptNPC.getInstance().start(target, id);
    }

    public int getMapId() {
        return client.getPlayer().getMap().getId();
    }

    public boolean haveMonster(int mobid) {
        for (Object obj : client.getPlayer().getMap().getAllMonsters()) {
            TacosMonster mob = (TacosMonster) obj;
            if (mob.getId() == mobid) {
                return true;
            }
        }
        return false;
    }

    public int getChannelNumber() {
        return client.getChannelId();
    }

    public int getMonsterCount(int mapid) {
        return getPlayer().findMap(mapid).getNumMonsters();
    }

    public void teachSkill(int id, byte level, byte masterlevel) {
        getPlayer().changeSkillLevel(WzXML.SKILL.getSkill().get(id), level, masterlevel);
    }

    public void teachSkill(int id, byte level) {
        Skill skil = WzXML.SKILL.getSkill().get(id);
        if (getPlayer().getSkillLevel(skil) > level) {
            level = getPlayer().getSkillLevel(skil);
        }
        getPlayer().changeSkillLevel(skil, level, skil.getMaxLevel());
    }

    public void dojo_getUp() {
        client.SendPacket(ResCWvsContext.Message(OpsMessage.MS_QuestRecordExMessage, PB_Message.builder().QuestID((short) 1207).str("pt=1;min=4;belt=1;tuto=1").build())); //todo
        client.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_PlayPortalSE));
        client.SendPacket(ResCUserLocal.UserTeleport((byte) 6));
    }

    // Oribs PQ, 920010700
    public void instantwarp(int map_id, int porta_id) {
        client.SendPacket(ResCUserLocal.UserTeleport((byte) porta_id));
    }

    public boolean dojoAgent_NextMap(boolean dojo, boolean fromresting) {
        TacosCharacter chr = client.getPlayer();
        if (dojo) {
            return Event_DojoAgent.warpNextMap(chr, fromresting);
        }
        return Event_DojoAgent.warpNextMap_Agent(chr, fromresting);
    }

    public int dojo_getPts() {
        return client.getPlayer().getDojo();
    }

    public OdinMapleEvent getEvent(String loc) {
        return null;
    }

    public int getSavedLocation(String loc) {
        Integer ret = client.getPlayer().getSavedLocation(SavedLocationType.fromString(loc));
        if (ret == null || ret == -1) {
            return 100000000;
        }
        return ret;
    }

    public void saveLocation(String loc) {
        client.getPlayer().saveLocation(SavedLocationType.fromString(loc));
    }

    public void saveReturnLocation(String loc) {
        TacosCharacter chr = client.getPlayer();
        chr.saveLocation(SavedLocationType.fromString(loc), chr.getMap().getReturnMap().getId());
    }

    public void clearSavedLocation(String loc) {
        client.getPlayer().clearSavedLocation(SavedLocationType.fromString(loc));
    }

    public void summonMsg(String msg) {
        if (!client.getPlayer().hasTutorialSummon()) {
            playerSummonHint(true);
        }
        client.SendPacket(ResCUserLocal.UserTutorMsg(msg));
    }

    public void summonMsg(int type) {
        if (!client.getPlayer().hasTutorialSummon()) {
            playerSummonHint(true);
        }
        client.SendPacket(ResCUserLocal.UserTutorMsg(type));
    }

    public void showInstruction(String msg, int width, int height) {
        client.SendPacket(ResCUserLocal.UserBalloonMsg(msg, width, height));
    }

    public void playerSummonHint(boolean summon) {
        client.getPlayer().setTutorialSummon(summon);
        client.SendPacket(ResCUserLocal.UserHireTutor(summon));
    }

    public String getInfoQuest(int id) {
        return client.getPlayer().getInfoQuest(id);
    }

    public void updateInfoQuest(int id, String data) {
        client.getPlayer().updateInfoQuest(id, data);
    }

    public boolean getEvanIntroState(String data) {
        return getInfoQuest(22013).equals(data);
    }

    public void updateEvanIntroState(String data) {
        updateInfoQuest(22013, data);
    }

    public void Aran_Start() {
        client.SendPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Sound, PB_FieldEffect.builder().wz_path("Aran/balloon").build()));
    }

    public void evanTutorial(String data, int v1) {
        client.SendPacket(ResCScriptMan.getEvanTutorial(data));
    }

    public void AranTutInstructionalBubble(String data) {
        client.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_AvatarOriented, PB_UserEffect.builder().path(data).build()));
    }

    public void ShowWZEffect(String data) {
        client.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_AvatarOriented, PB_UserEffect.builder().path(data).build()));
    }

    public void showWZEffect(String data) {
        client.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_ReservedEffect, PB_UserEffect.builder().path(data).build()));
    }

    public void EarnTitleMsg(String data) {
        client.SendPacket(ResCWvsContext.ScriptProgressMessage(data));
    }

    public void MovieClipIntroUI(boolean enabled) {
        client.SendPacket(ResCUserLocal.SetStandAloneMode(enabled));
        client.SendPacket(ResCUserLocal.SetDirectionMode(enabled));
    }

    public MapleInventoryType getInvType(int i) {
        return MapleInventoryType.getByType((byte) i);
    }

    public String getItemName(int id) {
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
            MapleInventoryManipulator.addById(getPlayer(), id, (short) 1, "", TacosPet.createPet(id, name, level, closeness, fullness, TacosWorld.getNextItemUniqueId(), id == 5000054 ? (int) period : 0), 45);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public void removeSlot(int invType, byte slot, short quantity) {
        MapleInventoryManipulator.removeFromSlot(getPlayer(), getInvType(invType), slot, quantity, true);
    }

    public void gainGP(int gp) {
        if (getPlayer().getGuildId() <= 0) {
            return;
        }
        client.getWorld().getGuild().gainGP(getPlayer().getGuildId(), gp); //1 for
    }

    public int getGP() {
        if (getPlayer().getGuildId() <= 0) {
            return 0;
        }
        return client.getWorld().getGuild().getGP(getPlayer().getGuildId()); //1 for
    }

    public void showMapEffect(String path) {
        getClient().SendPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Screen, PB_FieldEffect.builder().wz_path(path).build()));
    }

    public int itemQuantity(int itemid) {
        return getPlayer().itemQuantity(itemid);
    }

    public boolean isAllReactorState(int reactorId, int state) {
        boolean ret = false;
        for (TacosReactor r : getMap().getAllReactors()) {
            if (r.getId() == reactorId) {
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

    public void spawnBossAtNPC(int id, int x, int y, int type) {
        getMap().spawnMonsterOnGroundBelow(WzXML.MOB.createMob(id), new Point(x, y), type);
    }

    // multiple monsters, remote location
    public void spawnMonster(int id, int qty, int x, int y) {
        spawnMonster(id, qty, new Point(x, y));
    }

    // handler for all spawnMonster
    public void spawnMonster(int id, int qty, Point pos) {
        for (int i = 0; i < qty; i++) {
            getMap().spawnMonsterOnGroundBelow(WzXML.MOB.createMob(id), pos);
        }
    }

    public void sendNPCText(String text, int npc) {
        getMap().broadcastPacket(ResCScriptMan.ScriptMessage(npc, OpsScriptMan.SM_SAY, (byte) 0, text, false, false));
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
        return getPlayer().findMap(mapid).getCharactersSize();
    }

    public void playerMessage(String message) {
        client.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message(message).build()));
    }

    public OdinEventManager getEventManager(String event) {
        return TacosScriptEvent.getInstance().getEventManager(event);
    }

    public OdinEventInstanceManager getDisconnected(String event) {
        return null;
    }
}
