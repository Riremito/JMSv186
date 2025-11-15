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
package odin.handling.channel.handler;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import odin.client.inventory.IItem;
import odin.client.ISkill;
import odin.client.inventory.ItemFlag;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MapleInventory;
import odin.client.MapleStat;
import odin.client.PlayerStats;
import odin.constants.GameConstants;
import odin.client.SkillFactory;
import tacos.config.ServerConfig;
import tacos.debug.DebugLogger;
import odin.handling.world.MaplePartyCharacter;
import odin.handling.world.World;
import java.awt.Rectangle;
import java.util.concurrent.locks.Lock;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsBodyPart;
import tacos.packet.response.ResCDropPool;
import tacos.packet.response.ResCDropPool.LeaveType;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.packet.response.wrapper.WrapCWvsContext;
import odin.server.Randomizer;
import odin.server.RandomRewards;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleInventoryManipulator;
import odin.server.StructRewardItem;
import odin.server.maps.SavedLocationType;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapItem;
import odin.server.maps.MapleMapObject;
import odin.server.maps.MapleMapObjectType;
import odin.server.life.MapleMonster;
import odin.scripting.NPCScriptManager;
import odin.server.maps.MapleMist;
import odin.server.shops.HiredMerchant;
import odin.server.shops.IMaplePlayerShop;
import odin.tools.Pair;
import odin.tools.data.input.SeekableLittleEndianAccessor;
import tacos.packet.response.ResCMiniRoomBaseDlg;

public class InventoryHandler {

    public static final int UseRewardItem(short slot, final int itemId, final MapleClient c, final MapleCharacter chr) {
        final IItem toUse = c.getPlayer().getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
        c.getSession().write(WrapCWvsContext.updateStat());
        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {
            if (chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.USE).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.ETC).getNextFreeSlot() > -1) {
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final Pair<Integer, List<StructRewardItem>> rewards = ii.getRewardItem(itemId);

                if (rewards != null && rewards.getLeft() > 0) {
                    boolean rewarded = false;
                    while (!rewarded) {
                        for (StructRewardItem reward : rewards.getRight()) {
                            if (reward.prob > 0 && Randomizer.nextInt(rewards.getLeft()) < reward.prob) { // Total prob
                                if (GameConstants.getInventoryType(reward.itemid) == MapleInventoryType.EQUIP) {
                                    final IItem item = ii.getEquipById(reward.itemid);
                                    if (reward.period > 0) {
                                        item.setExpiration(System.currentTimeMillis() + (reward.period * 60 * 60 * 10));
                                    }
                                    MapleInventoryManipulator.addbyItem(c, item);
                                } else {
                                    MapleInventoryManipulator.addById(c, reward.itemid, reward.quantity);
                                }
                                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);

                                c.getSession().write(ResCUserLocal.showRewardItemAnimation(reward.itemid, reward.effect));
                                chr.getMap().broadcastMessage(chr, ResCUserRemote.showRewardItemAnimation(reward.itemid, reward.effect, chr.getId()), false);
                                rewarded = true;
                                return reward.itemid;
                            }
                        }
                    }
                } else {
                    chr.dropMessage(6, "Unknown error.");
                }
            } else {
                chr.dropMessage(6, "Insufficient inventory slot.");
            }
        }
        return 0;
    }

    public static final void UseScriptedNPCItem(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemId = slea.readInt();
        final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        long expiration_days = 0;
        int mountid = 0;

        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId) {
            switch (toUse.getItemId()) {
                case 2430007: // Blank Compass
                {
                    final MapleInventory inventory = chr.getInventory(MapleInventoryType.SETUP);
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);

                    if (inventory.countById(3994102) >= 20 // Compass Letter "North"
                            && inventory.countById(3994103) >= 20 // Compass Letter "South"
                            && inventory.countById(3994104) >= 20 // Compass Letter "East"
                            && inventory.countById(3994105) >= 20) { // Compass Letter "West"
                        MapleInventoryManipulator.addById(c, 2430008, (short) 1); // Gold Compass
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994102, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994103, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994104, 20, false, false);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.SETUP, 3994105, 20, false, false);
                    } else {
                        MapleInventoryManipulator.addById(c, 2430007, (short) 1); // Blank Compass
                    }
                    NPCScriptManager.getInstance().start(c, 2084001);
                    break;
                }
                case 2430008: // Gold Compass
                {
                    chr.saveLocation(SavedLocationType.RICHIE);
                    MapleMap map;
                    boolean warped = false;

                    for (int i = 390001000; i <= 390001004; i++) {
                        map = c.getChannelServer().getMapFactory().getMap(i);

                        if (map.getCharactersSize() == 0) {
                            chr.changeMap(map, map.getPortal(0));
                            warped = true;
                            break;
                        }
                    }
                    if (warped) { // Removal of gold compass
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, 2430008, 1, false, false);
                    } else { // Or mabe some other message.
                        c.getPlayer().dropMessage(5, "All maps are currently in use, please try again later.");
                    }
                    break;
                }
                case 2430112: //miracle cube
                    if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                        if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430112) >= 25) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049400, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, 2430112, 25, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049400, (short) 1);
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else if (c.getPlayer().getInventory(MapleInventoryType.USE).countById(2430112) >= 10) {
                            if (MapleInventoryManipulator.checkSpace(c, 2049400, 1, "") && MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, 2430112, 10, true, false)) {
                                MapleInventoryManipulator.addById(c, 2049401, (short) 1);
                            } else {
                                c.getPlayer().dropMessage(5, "Please make some space.");
                            }
                        } else {
                            c.getPlayer().dropMessage(5, "There needs to be 10 Fragments for a Potential Scroll, 25 for Advanced Potential Scroll.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "Please make some space.");
                    }
                    break;
                case 2430036: //croco 1 day
                    mountid = 1027;
                    expiration_days = 1;
                    break;
                case 2430037: //black scooter 1 day
                    mountid = 1028;
                    expiration_days = 1;
                    break;
                case 2430038: //pink scooter 1 day
                    mountid = 1029;
                    expiration_days = 1;
                    break;
                case 2430039: //clouds 1 day
                    mountid = 1030;
                    expiration_days = 1;
                    break;
                case 2430040: //balrog 1 day
                    mountid = 1031;
                    expiration_days = 1;
                    break;
                case 2430053: //croco 30 day
                    mountid = 1027;
                    expiration_days = 1;
                    break;
                case 2430054: //black scooter 30 day
                    mountid = 1028;
                    expiration_days = 30;
                    break;
                case 2430055: //pink scooter 30 day
                    mountid = 1029;
                    expiration_days = 30;
                    break;
                case 2430056: //mist rog 30 day
                    mountid = 1035;
                    expiration_days = 30;
                    break;
                //race kart 30 day? unknown 2430057
                case 2430072: //ZD tiger 7 day
                    mountid = 1034;
                    expiration_days = 7;
                    break;
                case 2430073: //lion 15 day
                    mountid = 1036;
                    expiration_days = 15;
                    break;
                case 2430074: //unicorn 15 day
                    mountid = 1037;
                    expiration_days = 15;
                    break;
                case 2430075: //low rider 15 day
                    mountid = 1038;
                    expiration_days = 15;
                    break;
                case 2430076: //red truck 15 day
                    mountid = 1039;
                    expiration_days = 15;
                    break;
                case 2430077: //gargoyle 15 day
                    mountid = 1040;
                    expiration_days = 15;
                    break;
                case 2430080: //shinjo 20 day
                    mountid = 1042;
                    expiration_days = 20;
                    break;
                case 2430082: //orange mush 7 day
                    mountid = 1044;
                    expiration_days = 7;
                    break;
                case 2430091: //nightmare 10 day
                    mountid = 1049;
                    expiration_days = 10;
                    break;
                case 2430092: //yeti 10 day
                    mountid = 1050;
                    expiration_days = 10;
                    break;
                case 2430093: //ostrich 10 day
                    mountid = 1051;
                    expiration_days = 10;
                    break;
                case 2430101: //pink bear 10 day
                    mountid = 1052;
                    expiration_days = 10;
                    break;
                case 2430102: //transformation robo 10 day
                    mountid = 1053;
                    expiration_days = 10;
                    break;
                case 2430103: //chicken 30 day
                    mountid = 1054;
                    expiration_days = 30;
                    break;
                case 2430117: //lion 1 year
                    mountid = 1036;
                    expiration_days = 365;
                    break;
                case 2430118: //red truck 1 year
                    mountid = 1039;
                    expiration_days = 365;
                    break;
                case 2430119: //gargoyle 1 year
                    mountid = 1040;
                    expiration_days = 365;
                    break;
                case 2430120: //unicorn 1 year
                    mountid = 1037;
                    expiration_days = 365;
                    break;
                case 2430136: //owl 30 day
                    mountid = 1069;
                    expiration_days = 30;
                    break;
                case 2430137: //owl 1 year
                    mountid = 1069;
                    expiration_days = 365;
                    break;
                case 2430201: //giant bunny 60 day
                    mountid = 1096;
                    expiration_days = 60;
                    break;
                case 2430228: //tiny bunny 60 day
                    mountid = 1101;
                    expiration_days = 60;
                    break;
                case 2430229: //bunny rickshaw 60 day
                    mountid = 1102;
                    expiration_days = 60;
                    break;
            }
        }
        if (mountid > 0) {
            mountid += (GameConstants.isAran(c.getPlayer().getJob()) ? 20000000 : (GameConstants.isEvan(c.getPlayer().getJob()) ? 20010000 : (GameConstants.isKOC(c.getPlayer().getJob()) ? 10000000 : (GameConstants.isResist(c.getPlayer().getJob()) ? 30000000 : 0))));
            if (c.getPlayer().getSkillLevel(mountid) > 0) {
                c.getPlayer().dropMessage(5, "You already have this skill.");
            } else if (expiration_days > 0) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(mountid), (byte) 1, (byte) 1, System.currentTimeMillis() + (long) (expiration_days * 24 * 60 * 60 * 1000));
                c.getPlayer().dropMessage(5, "The skill has been attained.");
            }
        }
        c.getSession().write(WrapCWvsContext.updateStat());
    }

    public static final int UseTreasureChest(MapleCharacter chr, short slot, int item_id) {
        final IItem toUse = chr.getInventory(MapleInventoryType.ETC).getItem((byte) slot);
        if (toUse == null || toUse.getQuantity() <= 0 || toUse.getItemId() != item_id) {
            return 0;
        }

        int reward;
        int keyIDforRemoval = 0;

        switch (toUse.getItemId()) {
            case 4280000: // Gold box
                reward = RandomRewards.getInstance().getGoldBoxReward();
                keyIDforRemoval = 5490000;
                break;
            case 4280001: // Silver box
                reward = RandomRewards.getInstance().getSilverBoxReward();
                keyIDforRemoval = 5490001;
                break;
            default: {
                return 0;
            }
        }

        // Get the quantity
        int amount = 1;
        switch (reward) {
            case 2000004:
                amount = 200; // Elixir
                break;
            case 2000005:
                amount = 100; // Power Elixir
                break;
        }

        if (chr.getInventory(MapleInventoryType.CASH).countById(keyIDforRemoval) <= 0) {
            return 0;
        }

        final IItem item = MapleInventoryManipulator.addbyId_Gachapon(chr.getClient(), reward, (short) amount);

        if (item == null) {
            return 0;
        }

        MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.ETC, (byte) slot, (short) 1, true);
        MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.CASH, keyIDforRemoval, 1, true, false);

        return reward;
    }

    public static final void UseCashItem(final SeekableLittleEndianAccessor slea, final MapleClient c, ClientPacket op) {
        if (ServerConfig.JMS164orLater()) {
            c.getPlayer().updateTick(slea.readInt());
        }
        final short slot = slea.readShort();
        final int itemId = slea.readInt();

        final IItem toUse = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slot);
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
            c.getSession().write(WrapCWvsContext.updateStat());
            return;
        }

        boolean used = false, cc = false;

        switch (itemId) {
            case 5050000: { // AP Reset
                List<Pair<MapleStat, Integer>> statupdate = new ArrayList<Pair<MapleStat, Integer>>(2);
                final int apto = slea.readInt();
                final int apfrom = slea.readInt();

                if (apto == apfrom) {
                    break; // Hack
                }
                final int job = c.getPlayer().getJob();
                final PlayerStats playerst = c.getPlayer().getStat();
                used = true;

                switch (apto) { // AP to
                    case 64: // str
                        if (playerst.getStr() >= 999) {
                            used = false;
                        }
                        break;
                    case 128: // dex
                        if (playerst.getDex() >= 999) {
                            used = false;
                        }
                        break;
                    case 256: // int
                        if (playerst.getInt() >= 999) {
                            used = false;
                        }
                        break;
                    case 512: // luk
                        if (playerst.getLuk() >= 999) {
                            used = false;
                        }
                        break;
                    case 2048: // hp
                        if (playerst.getMaxHp() >= 30000) {
                            used = false;
                        }
                        break;
                    case 8192: // mp
                        if (playerst.getMaxMp() >= 30000) {
                            used = false;
                        }
                        break;
                }
                switch (apfrom) { // AP to
                    case 64: // str
                        if (playerst.getStr() <= 4) {
                            used = false;
                        }
                        break;
                    case 128: // dex
                        if (playerst.getDex() <= 4) {
                            used = false;
                        }
                        break;
                    case 256: // int
                        if (playerst.getInt() <= 4) {
                            used = false;
                        }
                        break;
                    case 512: // luk
                        if (playerst.getLuk() <= 4) {
                            used = false;
                        }
                        break;
                    case 2048: // hp
                        if (/*playerst.getMaxMp() < ((c.getPlayer().getLevel() * 14) + 134) || */c.getPlayer().getHpApUsed() <= 0 || c.getPlayer().getHpApUsed() >= 10000) {
                            used = false;
                        }
                        break;
                    case 8192: // mp
                        if (/*playerst.getMaxMp() < ((c.getPlayer().getLevel() * 14) + 134) || */c.getPlayer().getHpApUsed() <= 0 || c.getPlayer().getHpApUsed() >= 10000) {
                            used = false;
                        }
                        break;
                }
                if (used) {
                    switch (apto) { // AP to
                        case 64: { // str
                            final int toSet = playerst.getStr() + 1;
                            playerst.setStr((short) toSet);
                            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.STR, toSet));
                            break;
                        }
                        case 128: { // dex
                            final int toSet = playerst.getDex() + 1;
                            playerst.setDex((short) toSet);
                            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.DEX, toSet));
                            break;
                        }
                        case 256: { // int
                            final int toSet = playerst.getInt() + 1;
                            playerst.setInt((short) toSet);
                            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.INT, toSet));
                            break;
                        }
                        case 512: { // luk
                            final int toSet = playerst.getLuk() + 1;
                            playerst.setLuk((short) toSet);
                            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.LUK, toSet));
                            break;
                        }
                        case 2048: // hp
                            int maxhp = playerst.getMaxHp();

                            if (job == 0) { // Beginner
                                maxhp += Randomizer.rand(8, 12);
                            } else if ((job >= 100 && job <= 132) || (job >= 3200 && job <= 3212)) { // Warrior
                                ISkill improvingMaxHP = SkillFactory.getSkill(1000001);
                                int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                maxhp += Randomizer.rand(20, 25);
                                if (improvingMaxHPLevel >= 1) {
                                    maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                }
                            } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job))) { // Magician
                                maxhp += Randomizer.rand(10, 20);
                            } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 3300 && job <= 3312)) { // Bowman
                                maxhp += Randomizer.rand(16, 20);
                            } else if ((job >= 500 && job <= 522) || (job >= 3500 && job <= 3512)) { // Pirate
                                ISkill improvingMaxHP = SkillFactory.getSkill(5100000);
                                int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                maxhp += Randomizer.rand(18, 22);
                                if (improvingMaxHPLevel >= 1) {
                                    maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                }
                            } else if (job >= 1500 && job <= 1512) { // Pirate
                                ISkill improvingMaxHP = SkillFactory.getSkill(15100000);
                                int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                maxhp += Randomizer.rand(18, 22);
                                if (improvingMaxHPLevel >= 1) {
                                    maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                }
                            } else if (job >= 1100 && job <= 1112) { // Soul Master
                                ISkill improvingMaxHP = SkillFactory.getSkill(11000000);
                                int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                maxhp += Randomizer.rand(36, 42);
                                if (improvingMaxHPLevel >= 1) {
                                    maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                }
                            } else if (job >= 1200 && job <= 1212) { // Flame Wizard
                                maxhp += Randomizer.rand(15, 21);
                            } else if (job >= 2000 && job <= 2112) { // Aran
                                maxhp += Randomizer.rand(40, 50);
                            } else { // GameMaster
                                maxhp += Randomizer.rand(50, 100);
                            }
                            maxhp = (short) Math.min(30000, Math.abs(maxhp));
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + 1));
                            playerst.setMaxHp(maxhp);
                            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.MAXHP, (int) maxhp));
                            break;

                        case 8192: // mp
                            int maxmp = playerst.getMaxMp();

                            if (job == 0) { // Beginner
                                maxmp += Randomizer.rand(6, 8);
                            } else if (job >= 100 && job <= 132) { // Warrior
                                maxmp += Randomizer.rand(5, 7);
                            } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job)) || (job >= 3200 && job <= 3212)) { // Magician
                                ISkill improvingMaxMP = SkillFactory.getSkill(2000001);
                                int improvingMaxMPLevel = c.getPlayer().getSkillLevel(improvingMaxMP);
                                maxmp += Randomizer.rand(18, 20);
                                if (improvingMaxMPLevel >= 1) {
                                    maxmp += improvingMaxMP.getEffect(improvingMaxMPLevel).getY() * 2;
                                }
                            } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 500 && job <= 522) || (job >= 3200 && job <= 3212) || (job >= 3500 && job <= 3512) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 1500 && job <= 1512)) { // Bowman
                                maxmp += Randomizer.rand(10, 12);
                            } else if (job >= 1100 && job <= 1112) { // Soul Master
                                maxmp += Randomizer.rand(6, 9);
                            } else if (job >= 1200 && job <= 1212) { // Flame Wizard
                                ISkill improvingMaxMP = SkillFactory.getSkill(12000000);
                                int improvingMaxMPLevel = c.getPlayer().getSkillLevel(improvingMaxMP);
                                maxmp += Randomizer.rand(18, 20);
                                if (improvingMaxMPLevel >= 1) {
                                    maxmp += improvingMaxMP.getEffect(improvingMaxMPLevel).getY() * 2;
                                }
                            } else if (job >= 2000 && job <= 2112) { // Aran
                                maxmp += Randomizer.rand(6, 9);
                            } else { // GameMaster
                                maxmp += Randomizer.rand(50, 100);
                            }
                            maxmp = (short) Math.min(30000, Math.abs(maxmp));
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + 1));
                            playerst.setMaxMp(maxmp);
                            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.MAXMP, (int) maxmp));
                            break;
                    }
                    switch (apfrom) { // AP from
                        case 64: { // str
                            final int toSet = playerst.getStr() - 1;
                            playerst.setStr((short) toSet);
                            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.STR, toSet));
                            break;
                        }
                        case 128: { // dex
                            final int toSet = playerst.getDex() - 1;
                            playerst.setDex((short) toSet);
                            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.DEX, toSet));
                            break;
                        }
                        case 256: { // int
                            final int toSet = playerst.getInt() - 1;
                            playerst.setInt((short) toSet);
                            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.INT, toSet));
                            break;
                        }
                        case 512: { // luk
                            final int toSet = playerst.getLuk() - 1;
                            playerst.setLuk((short) toSet);
                            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.LUK, toSet));
                            break;
                        }
                        case 2048: // HP
                            int maxhp = playerst.getMaxHp();
                            if (job == 0) { // Beginner
                                maxhp -= 12;
                            } else if (job >= 100 && job <= 132) { // Warrior
                                ISkill improvingMaxHP = SkillFactory.getSkill(1000001);
                                int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                maxhp -= 24;
                                if (improvingMaxHPLevel >= 1) {
                                    maxhp -= improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                }
                            } else if (job >= 200 && job <= 232) { // Magician
                                maxhp -= 10;
                            } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 3300 && job <= 3312) || (job >= 3500 && job <= 3512)) { // Bowman, Thief
                                maxhp -= 15;
                            } else if (job >= 500 && job <= 522) { // Pirate
                                ISkill improvingMaxHP = SkillFactory.getSkill(5100000);
                                int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                maxhp -= 15;
                                if (improvingMaxHPLevel > 0) {
                                    maxhp -= improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                }
                            } else if (job >= 1500 && job <= 1512) { // Pirate
                                ISkill improvingMaxHP = SkillFactory.getSkill(15100000);
                                int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                maxhp -= 15;
                                if (improvingMaxHPLevel > 0) {
                                    maxhp -= improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                }
                            } else if (job >= 1100 && job <= 1112) { // Soul Master
                                ISkill improvingMaxHP = SkillFactory.getSkill(11000000);
                                int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                                maxhp -= 27;
                                if (improvingMaxHPLevel >= 1) {
                                    maxhp -= improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                                }
                            } else if (job >= 1200 && job <= 1212) { // Flame Wizard
                                maxhp -= 12;
                            } else if ((job >= 2000 && job <= 2112) || (job >= 3200 && job <= 3212)) { // Aran
                                maxhp -= 40;
                            } else { // GameMaster
                                maxhp -= 20;
                            }
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() - 1));
                            playerst.setHp(maxhp);
                            playerst.setMaxHp(maxhp);
                            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.MAXHP, (int) maxhp));
                            break;
                        case 8192: // MP
                            int maxmp = playerst.getMaxMp();
                            if (job == 0) { // Beginner
                                maxmp -= 8;
                            } else if (job >= 100 && job <= 132) { // Warrior
                                maxmp -= 4;
                            } else if (job >= 200 && job <= 232) { // Magician
                                ISkill improvingMaxMP = SkillFactory.getSkill(2000001);
                                int improvingMaxMPLevel = c.getPlayer().getSkillLevel(improvingMaxMP);
                                maxmp -= 20;
                                if (improvingMaxMPLevel >= 1) {
                                    maxmp -= improvingMaxMP.getEffect(improvingMaxMPLevel).getY();
                                }
                            } else if ((job >= 500 && job <= 522) || (job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 1500 && job <= 1512) || (job >= 3300 && job <= 3312) || (job >= 3500 && job <= 3512)) { // Pirate, Bowman. Thief
                                maxmp -= 10;
                            } else if (job >= 1100 && job <= 1112) { // Soul Master
                                maxmp -= 6;
                            } else if (job >= 1200 && job <= 1212) { // Flame Wizard
                                ISkill improvingMaxMP = SkillFactory.getSkill(12000000);
                                int improvingMaxMPLevel = c.getPlayer().getSkillLevel(improvingMaxMP);
                                maxmp -= 25;
                                if (improvingMaxMPLevel >= 1) {
                                    maxmp -= improvingMaxMP.getEffect(improvingMaxMPLevel).getY();
                                }
                            } else if (job >= 2000 && job <= 2112) { // Aran
                                maxmp -= 5;
                            } else { // GameMaster
                                maxmp -= 20;
                            }
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() - 1));
                            playerst.setMp(maxmp);
                            playerst.setMaxMp(maxmp);
                            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.MAXMP, (int) maxmp));
                            break;
                    }
                    c.getPlayer().UpdateStat(true);
                }
                break;
            }
            case 5050001: // SP Reset (1st job)
            case 5050002: // SP Reset (2nd job)
            case 5050003: // SP Reset (3rd job)
            case 5050004:  // SP Reset (4th job)
            case 5050005: //evan sp resets
            case 5050006:
            case 5050007:
            case 5050008:
            case 5050009: {
                if (itemId >= 5050005 && !GameConstants.isEvan(c.getPlayer().getJob())) {
                    break;
                } //well i dont really care other than this o.o
                if (itemId < 5050005 && GameConstants.isEvan(c.getPlayer().getJob())) {
                    break;
                } //well i dont really care other than this o.o
                int skill1 = slea.readInt();
                int skill2 = slea.readInt();
                for (int i : GameConstants.blockedSkills) {
                    if (skill1 == i) {
                        c.getPlayer().dropMessage(1, "You may not add this skill.");
                        return;
                    }
                }

                ISkill skillSPTo = SkillFactory.getSkill(skill1);
                ISkill skillSPFrom = SkillFactory.getSkill(skill2);

                if (skillSPTo.isBeginnerSkill() || skillSPFrom.isBeginnerSkill()) {
                    break;
                }
                if (GameConstants.getSkillBookForSkill(skill1) != GameConstants.getSkillBookForSkill(skill2)) { //resistance evan
                    break;
                }
                if (GameConstants.getJobNumber(skill1 / 10000) > GameConstants.getJobNumber(skill2 / 10000)) { //putting 3rd job skillpoints into 4th job for example
                    break;
                }
                if ((c.getPlayer().getSkillLevel(skillSPTo) + 1 <= skillSPTo.getMaxLevel()) && c.getPlayer().getSkillLevel(skillSPFrom) > 0 && skillSPTo.canBeLearnedBy(c.getPlayer().getJob())) {
                    if (skillSPTo.isFourthJob() && (c.getPlayer().getSkillLevel(skillSPTo) + 1 > c.getPlayer().getMasterLevel(skillSPTo))) {
                        break;
                    }
                    if (itemId >= 5050005) {
                        if (GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2 && GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2 + 1) {
                            break;
                        }
                    } else {
                        if (GameConstants.getJobNumber(skill2 / 10000) != itemId - 5050000) { //you may only subtract from the skill if the ID matches Sp reset
                            break;
                        }
                    }
                    c.getPlayer().changeSkillLevel(skillSPFrom, (byte) (c.getPlayer().getSkillLevel(skillSPFrom) - 1), c.getPlayer().getMasterLevel(skillSPFrom));
                    c.getPlayer().changeSkillLevel(skillSPTo, (byte) (c.getPlayer().getSkillLevel(skillSPTo) + 1), c.getPlayer().getMasterLevel(skillSPTo));
                    used = true;
                }
                break;
            }
            case 5520001: //p.karma
            case 5520000: { // Karma
                final MapleInventoryType type = MapleInventoryType.getByType((byte) slea.readInt());
                final IItem item = c.getPlayer().getInventory(type).getItem((byte) slea.readInt());

                if (item != null && !ItemFlag.KARMA_EQ.check(item.getFlag()) && !ItemFlag.KARMA_USE.check(item.getFlag())) {
                    if ((itemId == 5520000 && MapleItemInformationProvider.getInstance().isKarmaEnabled(item.getItemId())) || (itemId == 5520001 && MapleItemInformationProvider.getInstance().isPKarmaEnabled(item.getItemId()))) {
                        byte flag = item.getFlag();
                        if (type == MapleInventoryType.EQUIP) {
                            flag |= ItemFlag.KARMA_EQ.getValue();
                        } else {
                            flag |= ItemFlag.KARMA_USE.getValue();
                        }
                        item.setFlag(flag);

                        c.getPlayer().forceReAddItem_Flag(item, type);
                        used = true;
                    }
                }
                break;
            }
            case 5060003: {//peanut
                IItem item = c.getPlayer().getInventory(MapleInventoryType.ETC).findById(4170023);
                if (item == null || item.getQuantity() <= 0) { // hacking{
                    return;
                }
                if (getIncubatedItems(c)) {
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, item.getPosition(), (short) 1, false);
                    used = true;
                }
                break;
            }
            case 5090100: // Wedding Invitation Card
            case 5090000: { // Note
                final String sendTo = slea.readMapleAsciiString();
                final String msg = slea.readMapleAsciiString();
                c.getPlayer().sendNote(sendTo, msg);
                used = true;
                break;
            }
            case 5281001: //idk, but probably
            case 5280001: // Gas Skill
            case 5281000: { // Passed gas
                Rectangle bounds = new Rectangle((int) c.getPlayer().getPosition().getX(), (int) c.getPlayer().getPosition().getY(), 1, 1);
                MapleMist mist = new MapleMist(bounds, c.getPlayer());
                c.getPlayer().getMap().spawnMist(mist, 10000, true);
                used = true;
                break;
            }
            case 5390000: // Diablo Messenger
            case 5390001: // Cloud 9 Messenger
            case 5390002: // Loveholic Messenger
            case 5390003: // New Year Megassenger 1
            case 5390004: // New Year Megassenger 2
            case 5390005: // Cute Tiger Messenger
            case 5390006: { // Tiger Roar's Messenger
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String text = slea.readMapleAsciiString();
                    if (text.length() > 55) {
                        break;
                    }
                    final boolean ear = slea.readByte() != 0;
                    World.Broadcast.broadcastSmega(ResCWvsContext.getAvatarMega(c.getPlayer(), c.getChannel(), itemId, text, ear).getBytes());
                    used = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            default: {
                if (itemId / 10000 == 553) {
                    UseRewardItem(slot, itemId, c, c.getPlayer());// this too
                } else {
                    System.out.println("Unhandled CS item : " + itemId);
                    System.out.println(slea.toString(true));
                }
                break;
            }
        }

        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (short) 1, false, true);
        }
        c.getSession().write(WrapCWvsContext.updateStat());
    }

    public static final boolean PickUp(MapleCharacter chr, int object_id) {
        final MapleMapObject object = chr.getMap().getMapObject(object_id, MapleMapObjectType.ITEM);

        if (object == null) {
            DebugLogger.ErrorLog("PickUp : item null");
            return false;
        }

        final MapleMapItem mapitem = (MapleMapItem) object;
        final Lock lock = mapitem.getLock();
        lock.lock();
        try {
            if (mapitem.isPickedUp()) {
                DebugLogger.ErrorLog("PickUp : isPickedUp");
                return false;
            }
            if (mapitem.getOwner() != chr.getId() && ((!mapitem.isPlayerDrop() && mapitem.getDropType() == 0) || (mapitem.isPlayerDrop() && chr.getMap().getEverlast()))) {
                DebugLogger.ErrorLog("PickUp : getOwner");
                return false;
            }
            if (!mapitem.isPlayerDrop() && mapitem.getDropType() == 1 && mapitem.getOwner() != chr.getId() && (chr.getParty() == null || chr.getParty().getMemberById(mapitem.getOwner()) == null)) {
                DebugLogger.ErrorLog("PickUp : isPlayerDrop");
                return false;
            }

            // Meso
            if (mapitem.getMeso() > 0) {
                // ?_?
                if (chr.getParty() != null && mapitem.getOwner() != chr.getId()) {
                    final List<MapleCharacter> toGive = new LinkedList<MapleCharacter>();

                    for (MaplePartyCharacter z : chr.getParty().getMembers()) {
                        MapleCharacter m = chr.getMap().getCharacterById(z.getId());
                        if (m != null) {
                            toGive.add(m);
                        }
                    }
                    for (final MapleCharacter m : toGive) {
                        m.gainMeso(mapitem.getMeso() / toGive.size() + (m.getStat().hasPartyBonus ? (int) (mapitem.getMeso() / 20.0) : 0), true, true);
                    }
                } else {
                    chr.gainMeso(mapitem.getMeso(), true, true);
                }

                removeItem(chr, mapitem, object);
                return true;
            }

            // item
            if (MapleItemInformationProvider.getInstance().isPickupBlocked(mapitem.getItem().getItemId())) {
                DebugLogger.ErrorLog("PickUp : isPickupBlocked");
                return false;
            }
            if (useItem(chr.getClient(), mapitem.getItemId())) {
                removeItem(chr, mapitem, object);
                DebugLogger.InfoLog("PickUp : useItem");
                return true;
            }
            if (!MapleInventoryManipulator.checkSpace(chr.getClient(), mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                chr.SendPacket(ResWrapper.getShowInventoryFull());
                DebugLogger.ErrorLog("PickUp : checkSpace");
                return false;
            }
            if (!MapleInventoryManipulator.addFromDrop(chr.getClient(), mapitem.getItem(), true, mapitem.getDropper() instanceof MapleMonster)) {
                DebugLogger.ErrorLog("PickUp : addFromDrop");
                return false;
            }
            removeItem(chr, mapitem, object);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public static final boolean useItem(final MapleClient c, final int id) {
        if (GameConstants.isUse(id)) { // TO prevent caching of everything, waste of mem
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final byte consumeval = ii.isConsumeOnPickup(id);

            if (consumeval > 0) {
                if (consumeval == 2) {
                    if (c.getPlayer().getParty() != null) {
                        for (final MaplePartyCharacter pc : c.getPlayer().getParty().getMembers()) {
                            final MapleCharacter chr = c.getPlayer().getMap().getCharacterById(pc.getId());
                            if (chr != null) {
                                ii.getItemEffect(id).applyTo(chr);
                            }
                        }
                    } else {
                        ii.getItemEffect(id).applyTo(c.getPlayer());
                    }
                } else {
                    ii.getItemEffect(id).applyTo(c.getPlayer());
                }
                c.SendPacket(ResWrapper.DropPickUpMessage(id, (byte) 1));
                return true;
            }
        }
        return false;
    }

    public static final void removeItem_Pet(final MapleCharacter chr, final MapleMapItem mapitem, int pet) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(ResCDropPool.DropLeaveField(mapitem, LeaveType.PICK_UP_PET, chr, pet), mapitem.getPosition());
        chr.getMap().removeMapObject(mapitem);
        if (mapitem.isRandDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }

    private static final void removeItem(final MapleCharacter chr, final MapleMapItem mapitem, final MapleMapObject ob) {
        mapitem.setPickedUp(true);
        chr.getMap().broadcastMessage(ResCDropPool.DropLeaveField(mapitem, LeaveType.PICK_UP, chr, 0), mapitem.getPosition());
        chr.getMap().removeMapObject(ob);
        if (mapitem.isRandDrop()) {
            chr.getMap().spawnRandDrop();
        }
    }

    private static final void addMedalString(final MapleCharacter c, final StringBuilder sb) {
        final IItem medal = c.getInventory(MapleInventoryType.EQUIPPED).getItem(OpsBodyPart.BP_MEDAL.getSlot());
        if (medal != null) { // Medal
            sb.append("<");
            sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
            sb.append("> ");
        }
    }

    private static final boolean getIncubatedItems(MapleClient c) {
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 2 || c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 2 || c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 2) {
            c.getPlayer().dropMessage(5, "Please make room in your inventory.");
            return false;
        }
        final int[] ids = {2430091, 2430092, 2430093, 2430101, 2430102, //mounts 
            2340000, //rares
            1152000, 1152001, 1152004, 1152005, 1152006, 1152007, 1152008, //toenail only comes when db is out.
            1000040, 1102246, 1082276, 1050169, 1051210, 1072447, 1442106, //blizzard
            3010019, //chairs
            1001060, 1002391, 1102004, 1050039, 1102040, 1102041, 1102042, 1102043, //equips
            1082145, 1082146, 1082147, 1082148, 1082149, 1082150, //wg
            2043704, 2040904, 2040409, 2040307, 2041030, 2040015, 2040109, 2041035, 2041036, 2040009, 2040511, 2040408, 2043804, 2044105, 2044903, 2044804, 2043009, 2043305, 2040610, 2040716, 2041037, 2043005, 2041032, 2040305, //scrolls
            2040211, 2040212, 1022097, //dragon glasses
            2049000, 2049001, 2049002, 2049003, //clean slate
            1012058, 1012059, 1012060, 1012061, //pinocchio nose msea only.
            1332100, 1382058, 1402073, 1432066, 1442090, 1452058, 1462076, 1472069, 1482051, 1492024, 1342009,//durability weapons level 105
            2049400, 2049401, 2049301};
        //out of 1000
        final int[] chances = {100, 100, 100, 100, 100,
            1,
            10, 10, 10, 10, 10, 10, 10,
            5, 5, 5, 5, 5, 5, 5,
            2,
            10, 10, 10, 10, 10, 10, 10, 10,
            5, 5, 5, 5, 5, 5,
            10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
            5, 5, 10,
            10, 10, 10, 10,
            5, 5, 5, 5,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            1, 2, 1, 2};
        int z = Randomizer.nextInt(ids.length);
        while (chances[z] < Randomizer.nextInt(1000)) {
            z = Randomizer.nextInt(ids.length);
        }
        int z_2 = Randomizer.nextInt(ids.length);
        while (z_2 == z || chances[z_2] < Randomizer.nextInt(1000)) {
            z_2 = Randomizer.nextInt(ids.length);
        }
        c.getSession().write(ResCWvsContext.getPeanutResult(ids[z], (short) 1, ids[z_2], (short) 1));
        return MapleInventoryManipulator.addById(c, ids[z], (short) 1) && MapleInventoryManipulator.addById(c, ids[z_2], (short) 1);

    }

    public static final int OWL_ID = 2; //don't change. 0 = owner ID, 1 = store ID, 2 = object ID

    public static final void OwlWarp(MapleClient c, int id, int map) {
        c.getSession().write(WrapCWvsContext.updateStat());
        if (c.getPlayer().getMapId() >= 910000000 && c.getPlayer().getMapId() <= 910000022 && c.getPlayer().getPlayerShop() == null) {
            if (map >= 910000001 && map <= 910000022) {
                final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(map);
                c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                HiredMerchant merchant = null;
                List<MapleMapObject> objects;
                switch (OWL_ID) {
                    case 0:
                        objects = mapp.getAllHiredMerchantsThreadsafe();
                        for (MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop) {
                                final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                                if (ips instanceof HiredMerchant) {
                                    final HiredMerchant merch = (HiredMerchant) ips;
                                    if (merch.getOwnerId() == id) {
                                        merchant = merch;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case 1:
                        objects = mapp.getAllHiredMerchantsThreadsafe();
                        for (MapleMapObject ob : objects) {
                            if (ob instanceof IMaplePlayerShop) {
                                final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                                if (ips instanceof HiredMerchant) {
                                    final HiredMerchant merch = (HiredMerchant) ips;
                                    if (merch.getStoreId() == id) {
                                        merchant = merch;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    default:
                        final MapleMapObject ob = mapp.getMapObject(id, MapleMapObjectType.HIRED_MERCHANT);
                        if (ob instanceof IMaplePlayerShop) {
                            final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                            if (ips instanceof HiredMerchant) {
                                merchant = (HiredMerchant) ips;
                            }
                        }
                        break;
                }
                if (merchant != null) {
                    if (merchant.isOwner(c.getPlayer())) {
                        merchant.setOpen(false);
                        merchant.removeAllVisitors((byte) 16, (byte) 0);
                        c.getPlayer().setPlayerShop(merchant);
                        c.getSession().write(ResCMiniRoomBaseDlg.getHiredMerch(c.getPlayer(), merchant, false));
                    } else {
                        if (!merchant.isOpen() || !merchant.isAvailable()) {
                            c.getPlayer().dropMessage(1, "This shop is in maintenance, please come by later.");
                        } else {
                            if (merchant.getFreeSlot() == -1) {
                                c.getPlayer().dropMessage(1, "This shop has reached it's maximum capacity, please come by later.");
                            } else if (merchant.isInBlackList(c.getPlayer().getName())) {
                                c.getPlayer().dropMessage(1, "You have been banned from this store.");
                            } else {
                                c.getPlayer().setPlayerShop(merchant);
                                merchant.addVisitor(c.getPlayer());
                                c.getSession().write(ResCMiniRoomBaseDlg.getHiredMerch(c.getPlayer(), merchant, false));
                            }
                        }
                    }
                } else {
                    c.getPlayer().dropMessage(1, "This shop is in maintenance, please come by later.");
                }
            }
        }
    }
}
