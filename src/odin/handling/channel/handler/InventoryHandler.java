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

import tacos.packet.ops.OpsMRLeave;
import tacos.wz.WzXML;
import tacos.server.map.object.TacosMerchant;
import java.util.Map;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MapleInventory;
import odin.constants.GameConstants;
import tacos.packet.ClientPacket;
import odin.server.RandomRewards;
import odin.server.MapleInventoryManipulator;
import odin.server.maps.SavedLocationType;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import tacos.script.TacosScriptNPC;

public class InventoryHandler {

    // mount skill items : item id -> {mount id, expiration days}
    private static final Map<Integer, int[]> MOUNT_ITEMS = Map.ofEntries(
            Map.entry(2430036, new int[]{1027, 1}), //croco 1 day
            Map.entry(2430037, new int[]{1028, 1}), //black scooter 1 day
            Map.entry(2430038, new int[]{1029, 1}), //pink scooter 1 day
            Map.entry(2430039, new int[]{1030, 1}), //clouds 1 day
            Map.entry(2430040, new int[]{1031, 1}), //balrog 1 day
            Map.entry(2430053, new int[]{1027, 1}), //croco 30 day
            Map.entry(2430054, new int[]{1028, 30}), //black scooter 30 day
            Map.entry(2430055, new int[]{1029, 30}), //pink scooter 30 day
            Map.entry(2430056, new int[]{1035, 30}), //mist rog 30 day
            //race kart 30 day? unknown 2430057
            Map.entry(2430072, new int[]{1034, 7}), //ZD tiger 7 day
            Map.entry(2430073, new int[]{1036, 15}), //lion 15 day
            Map.entry(2430074, new int[]{1037, 15}), //unicorn 15 day
            Map.entry(2430075, new int[]{1038, 15}), //low rider 15 day
            Map.entry(2430076, new int[]{1039, 15}), //red truck 15 day
            Map.entry(2430077, new int[]{1040, 15}), //gargoyle 15 day
            Map.entry(2430080, new int[]{1042, 20}), //shinjo 20 day
            Map.entry(2430082, new int[]{1044, 7}), //orange mush 7 day
            Map.entry(2430091, new int[]{1049, 10}), //nightmare 10 day
            Map.entry(2430092, new int[]{1050, 10}), //yeti 10 day
            Map.entry(2430093, new int[]{1051, 10}), //ostrich 10 day
            Map.entry(2430101, new int[]{1052, 10}), //pink bear 10 day
            Map.entry(2430102, new int[]{1053, 10}), //transformation robo 10 day
            Map.entry(2430103, new int[]{1054, 30}), //chicken 30 day
            Map.entry(2430117, new int[]{1036, 365}), //lion 1 year
            Map.entry(2430118, new int[]{1039, 365}), //red truck 1 year
            Map.entry(2430119, new int[]{1040, 365}), //gargoyle 1 year
            Map.entry(2430120, new int[]{1037, 365}), //unicorn 1 year
            Map.entry(2430136, new int[]{1069, 30}), //owl 30 day
            Map.entry(2430137, new int[]{1069, 365}), //owl 1 year
            Map.entry(2430201, new int[]{1096, 60}), //giant bunny 60 day
            Map.entry(2430228, new int[]{1101, 60}), //tiny bunny 60 day
            Map.entry(2430229, new int[]{1102, 60}) //bunny rickshaw 60 day
    );

    public static void UseScriptedNPCItem(ClientPacket cp, TacosCharacter chr) {
        int unk1 = cp.Decode4();
        byte slot = (byte) cp.Decode2();
        int itemId = cp.Decode4();
        Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            chr.updateInv();
            return;
        }
        switch (itemId) {
            case 2430007: { // Blank Compass
                useBlankCompass(  chr, slot);
                break;
            }
            case 2430008: { // Gold Compass
                useGoldCompass(  chr);
                break;
            }
            case 2430112: { //miracle cube
                useMiracleCube(  chr);
                break;
            }
            default: {
                int[] mount = MOUNT_ITEMS.get(itemId);
                if (mount != null) {
                    useMountItem(  chr, slot, mount[0], mount[1]);
                }
                break;
            }
        }
        chr.updateInv();
    }

    private static void useBlankCompass(TacosCharacter chr, byte slot) {
        MapleInventory inventory = chr.getInventory(MapleInventoryType.SETUP);
        MapleInventoryManipulator.removeFromSlot(chr, MapleInventoryType.USE, slot, (byte) 1, false);

        if (inventory.countById(3994102) >= 20 // Compass Letter "North"
                && inventory.countById(3994103) >= 20 // Compass Letter "South"
                && inventory.countById(3994104) >= 20 // Compass Letter "East"
                && inventory.countById(3994105) >= 20) { // Compass Letter "West"
            MapleInventoryManipulator.addById(chr, 2430008, (short) 1); // Gold Compass
            MapleInventoryManipulator.removeById(chr, MapleInventoryType.SETUP, 3994102, 20, false, false);
            MapleInventoryManipulator.removeById(chr, MapleInventoryType.SETUP, 3994103, 20, false, false);
            MapleInventoryManipulator.removeById(chr, MapleInventoryType.SETUP, 3994104, 20, false, false);
            MapleInventoryManipulator.removeById(chr, MapleInventoryType.SETUP, 3994105, 20, false, false);
        } else {
            MapleInventoryManipulator.addById(chr, 2430007, (short) 1); // Blank Compass
        }
        TacosScriptNPC.getInstance().start(chr.getClient(), 2084001);
    }

    private static void useGoldCompass(TacosCharacter chr) {
        chr.saveLocation(SavedLocationType.RICHIE);
        for (int i = 390001000; i <= 390001004; i++) {
            TacosMap map = chr.findMap(i);
            if (map.getCharactersSize() == 0) {
                chr.changeMapPortal(map, map.getPortal(0));
                MapleInventoryManipulator.removeById(chr, MapleInventoryType.USE, 2430008, 1, false, false); // Removal of gold compass
                return;
            }
        }
        chr.Notice("All maps are currently in use, please try again later."); // Or mabe some other message.
    }

    private static void useMiracleCube(TacosCharacter chr) {
        if (chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1) {
            chr.Notice("Please make some space.");
            return;
        }
        int fragments = chr.getInventory(MapleInventoryType.USE).countById(2430112);
        int cost;
        int reward;
        if (fragments >= 25) {
            cost = 25;
            reward = 2049400;
        } else if (fragments >= 10) {
            cost = 10;
            reward = 2049401;
        } else {
            chr.Notice("There needs to be 10 Fragments for a Potential Scroll, 25 for Advanced Potential Scroll.");
            return;
        }
        if (MapleInventoryManipulator.checkSpace(chr, 2049400, 1, "") && MapleInventoryManipulator.removeById(chr, MapleInventoryType.USE, 2430112, cost, true, false)) {
            MapleInventoryManipulator.addById(chr, reward, (short) 1);
        } else {
            chr.Notice("Please make some space.");
        }
    }

    private static void useMountItem(TacosCharacter chr, byte slot, int mountid, long expiration_days) {
        mountid += (GameConstants.isAran(chr.getJob()) ? 20000000 : (GameConstants.isEvan(chr.getJob()) ? 20010000 : (GameConstants.isKOC(chr.getJob()) ? 10000000 : (GameConstants.isResist(chr.getJob()) ? 30000000 : 0))));
        if (chr.getSkillLevel(mountid) > 0) {
            chr.Notice("You already have this skill.");
            return;
        }
        if (expiration_days > 0) {
            MapleInventoryManipulator.removeFromSlot(chr, MapleInventoryType.USE, slot, (byte) 1, false);
            chr.changeSkillLevel(WzXML.SKILL.getSkill().get(mountid), (byte) 1, (byte) 1, System.currentTimeMillis() + expiration_days * 24 * 60 * 60 * 1000);
            chr.Notice("The skill has been attained.");
        }
    }

    public static int UseTreasureChest(TacosCharacter chr, short slot, int item_id) {
        Item toUse = chr.getInventory(MapleInventoryType.ETC).getItem((byte) slot);
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

        Item item = MapleInventoryManipulator.addbyId_Gachapon(chr, reward, (short) amount);

        if (item == null) {
            return 0;
        }

        MapleInventoryManipulator.removeFromSlot(chr, MapleInventoryType.ETC, (byte) slot, (short) 1, true);
        MapleInventoryManipulator.removeById(chr, MapleInventoryType.CASH, keyIDforRemoval, 1, true, false);

        return reward;
    }

    public static final int OWL_ID = 2; //don't change. 0 = owner ID, 1 = store ID, 2 = object ID

    public static void OwlWarp(TacosCharacter chr, int id, int map) {
        chr.updateInv();
        if (chr.getMapId() < 910000000 || chr.getMapId() > 910000022 || chr.getPlayerShop() != null || map < 910000001 || map > 910000022) {
            return;
        }
        TacosMap mapp = chr.findMap(map);
        chr.changeMapPortal(mapp, mapp.getPortal(0));
        visitOwlMerchant(chr, findOwlMerchant(mapp, id));
    }

    private static TacosMerchant findOwlMerchant(TacosMap mapp, int id) {
        switch (OWL_ID) {
            case 0: {
                for (TacosMerchant merch : mapp.getAllMerchants()) {
                    if (merch.getOwnerId() == id) {
                        return merch;
                    }
                }
                return null;
            }
            case 1: {
                for (TacosMerchant merch : mapp.getAllMerchants()) {
                    if (merch.getStoreId() == id) {
                        return merch;
                    }
                }
                return null;
            }
            default: {
                return mapp.getMerchantByOid(id);
            }
        }
    }

    private static void visitOwlMerchant(TacosCharacter chr, TacosMerchant merchant) {
        if (merchant == null) {
            chr.Alert("This shop is in maintenance, please come by later.");
            return;
        }
        if (merchant.isOwner(chr)) {
            merchant.setOpen(false);
            merchant.removeAllVisitors(OpsMRLeave.ESLeave_StartManage.get(), 0);
            chr.setPlayerShop(merchant);
            chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_EntrustedShop(chr, merchant, false));
            return;
        }
        if (!merchant.isOpen() || !merchant.isAvailable()) {
            chr.Alert("This shop is in maintenance, please come by later.");
            return;
        }
        if (merchant.getFreeSlot() == -1) {
            chr.Alert("This shop has reached it's maximum capacity, please come by later.");
            return;
        }
        if (merchant.isInBlackList(chr.getName())) {
            chr.Alert("You have been banned from this store.");
            return;
        }
        chr.setPlayerShop(merchant);
        merchant.addVisitor(chr);
        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_EntrustedShop(chr, merchant, false));
    }
}
