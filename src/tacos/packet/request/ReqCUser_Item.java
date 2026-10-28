/*
 * Copyright (C) 2025 Riremito
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */
package tacos.packet.request;

import tacos.server.map.object.TacosPet;
import tacos.packet.ops.OpsMREnterResult;
import tacos.server.map.object.TacosMerchant;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import odin.client.inventory.Equip;
import odin.client.inventory.Item;
import odin.client.inventory.Item.ItemFlag;
import odin.client.inventory.MapleInventoryType;
import tacos.config.Region;
import odin.constants.GameConstants;
import tacos.debug.DebugLogger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import odin.client.inventory.MapleInventory;
import odin.server.MapleInventoryManipulator;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.ops.OpsShopScanner;
import tacos.packet.response.builder.PB_BroadcastMsg;
import tacos.packet.response.ResCMapleTVMan;
import tacos.packet.response.ResCParcelDlg;
import tacos.packet.response.ResCUIItemUpgrade;
import tacos.packet.response.ResCUser;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUser_Pet;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_InvOp;
import odin.server.MapleItemInformationProvider;
import odin.server.RandomRewards;
import odin.server.maps.SavedLocationType;
import tacos.config.Config;
import tacos.packet.ops.OpsCashItem;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import tacos.script.TacosScriptNPC;
import tacos.wz.WzXML;
import tacos.wz.opt.FieldOpt;

/**
 *
 * @author Riremito
 */
public class ReqCUser_Item {

    // owl.
    public static final int OWL_ID = 2; //don't change. 0 = owner ID, 1 = store ID, 2 = object ID

    public static void OnShopLinkRequest(TacosCharacter chr, int id, int map) {
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
            chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_Failed(OpsMREnterResult.MREnterResult_NoRoom));
            return;
        }
        if (chr.getPlayerShop() != null || chr.getTrade() != null) {
            chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_Failed(OpsMREnterResult.MREnterResult_Busy));
            return;
        }
        OpsMREnterResult result = merchant.enter(chr);
        if (result != OpsMREnterResult.MREnterResult_Success) {
            chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_Failed(result));
        }
    }

    // script item.
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

    public static void OnUserScriptItemUseRequest(ClientPacket cp, TacosCharacter chr) {
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
                useBlankCompass(chr, slot);
                break;
            }
            case 2430008: { // Gold Compass
                useGoldCompass(chr);
                break;
            }
            case 2430112: { //miracle cube
                useMiracleCube(chr);
                break;
            }
            default: {
                int[] mount = MOUNT_ITEMS.get(itemId);
                if (mount != null) {
                    useMountItem(chr, slot, mount[0], mount[1]);
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

    public static boolean OnUserConsumeCashItemUseRequestInternal(TacosCharacter chr, TacosMap map, ClientPacket cp) {
        int timestamp = (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 92) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70) || Region.BMS.check()) ? cp.Decode4() : 0;
        short cash_item_slot = cp.Decode2();
        int cash_item_id = cp.Decode4();

        Runnable item_use = chr.checkItemSlot(cash_item_slot, cash_item_id);
        if (item_use == null) {
            DebugLogger.ErrorLog("OnUserConsumeCashItemUseRequest : invalid item.");
            return true;
        }

        int cash_item_type = cash_item_id / 10000;

        switch (cash_item_type) {
            case 504: // 5040000
            {
                byte action = cp.Decode1();
                int map_id = 999999999;
                TacosCharacter target_chr = null;
                if (action == 0) {
                    map_id = cp.Decode4();
                } else {
                    String target_name = cp.DecodeStr();
                    target_chr = chr.getClient().getChannelServer().getOnlinePlayers().findByName(target_name);
                    if (target_chr == null) {
                        return false;
                    }
                    map_id = target_chr.getMap().getId();
                }
                if (FieldOpt.FIELDOPT_TELEPORTITEMLIMIT.check(chr.findMap(map_id).getFieldLimit())) {
                    return false;
                }
                item_use.run();
                if (action == 0) {
                    chr.changeMapById(map_id);
                } else {
                    chr.changeMapWithCoordinate(map_id, chr.getPosition().x, chr.getPosition().y);
                }
                return true;
            }
            case 506: {
                if (cashItem506(chr, cash_item_id, cp)) {
                    item_use.run();
                    return true;
                }
                return false;
            }
            case 507: {
                if (cashItem507_Megaphone(chr, cash_item_id, cp)) {
                    item_use.run();
                    return true;
                }
                return false;
            }
            case 510: {
                int song_time = cp.Decode4(); // unused

                map.startJukebox(chr.getName(), cash_item_id);
                item_use.run();
                return true;
            }
            case 512: {
                String message = cp.DecodeStr();
                MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
                String msg = miip.getMsg(cash_item_id).replaceFirst("%s", chr.getName()).replaceFirst("%s", message);
                map.startMapEffect(msg, cash_item_id);

                int buff = miip.getStateChangeItem(cash_item_id);
                if (buff != 0) {
                    for (TacosCharacter mChar : map.getAllPlayers()) {
                        miip.getItemEffect(buff).applyTo(mChar);
                    }
                }

                item_use.run();
                return true;
            }
            case 517: {
                if (cashItem517_PetNameChange(chr, cash_item_id, cp)) {
                    item_use.run();
                    return true;
                }
                return false;
            }
            case 520: // 5201000
            {
                if (cashItem520_Money(chr, cash_item_id)) {
                    item_use.run();
                    return true;
                }
                return false;
            }
            case 523: {
                // OnUserShopScannerItemUseRequest
                int target_item_id = cp.Decode4();
                List<TacosMerchant> hms = chr.getChannelServer().searchMerchant(target_item_id);
                chr.SendPacket(ResCWvsContext.ShopScannerResult(OpsShopScanner.ShopScannerRes_SearchResult));
                item_use.run();
                return true;
            }
            case 524: {
                // TODO : fix
                ReqCUser_Pet.OnPetFood(chr, MapleInventoryType.CASH, cash_item_slot, cash_item_id);
                return true;
            }
            case 533: // 速達
            {
                chr.SendPacket(ResCParcelDlg.Parcel_Open(true, false));
                chr.sendStatChanged(true);
                return true;
            }
            case 537: // 5370000
            {
                String message = cp.DecodeStr();
                chr.setADBoard(message);
                map.broadcastPacket(ResCUser.UserADBoard(chr));
                chr.updateInv();
                return true;
            }
            case 545: {
                int npc_script_id = 9090000; // ミョミョ
                int npc_id = npc_script_id;
                ReqCUser.OnUserSelectNpcInternal(chr, npc_script_id, npc_id);
                chr.updateInv();
                return true;
            }
            case 557: // 5570000
            {
                int inv_type = cp.Decode4(); // unused
                int item_slot = cp.Decode4();
                Equip equip = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem((short) item_slot);
                if (equip == null) {
                    return false;
                }
                boolean success = equip.getEnhancement().useViciousHammer();

                chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.EQUIP, equip).build()));
                chr.SendPacket(ResCUIItemUpgrade.ItemUpgradeResult(success ? OpsCashItem.CashItemRes_ItemUpgradeSuccess : OpsCashItem.CashItemReq_ItemUpgradeFail, equip));
                item_use.run();
                return true;
            }
            case 561: {
                int inv_type_equip = cp.Decode4(); // unused
                int equip_slot = cp.Decode4();
                int inv_type_use = cp.Decode4(); // unused
                int scroll_slot = cp.Decode4();

                if (ReqCUser.useScroll(chr, map, (short) scroll_slot, (short) equip_slot, cash_item_id)) {
                    item_use.run();
                    return true;
                }
                return false;
            }
            case 562: {
                ReqCUser.OnUserSkillLearnItemUseRequest(chr, map, cash_item_slot, cash_item_id);
                return true;
            }
            default: {
                break;
            }
        }

        // not coded.
        DebugLogger.ErrorLog("OnUserConsumeCashItemUseRequest : not coded yet. type = " + cash_item_type);
        return false;
    }

    public static boolean cashItem506(TacosCharacter chr, int cash_item_id, ClientPacket cp) {
        switch (cash_item_id) {
            case 5060000: // ネームメーカー
            {
                short equipped_slot = cp.Decode2();
                int timestamp = Config.LessOrEqual(Region.JMS, 147) ? cp.Decode4() : 0; // not in JMS302

                if (0 <= equipped_slot) {
                    return false;
                }

                Item item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) equipped_slot);
                if (item == null) {
                    return false;
                }

                item.setOwner(chr.getName());
                chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.EQUIP, item).build()));
                return true;
            }
            case 5060001: // 封印の錠
            {
                int inv_type = cp.Decode4(); // unused
                int item_slot = cp.Decode4();

                MapleInventoryType type = MapleInventoryType.getByType((byte) inv_type);

                Item item = chr.getInventory(type).getItem((short) item_slot);
                if (item == null) {
                    return false;
                }

                boolean isTarget = false;

                if (type == MapleInventoryType.EQUIP) {
                    isTarget = true;
                }

                if (type == MapleInventoryType.USE) {
                    if (GameConstants.isRechargable(item.getItemId())) {
                        isTarget = true;
                    }
                }

                if (!isTarget) {
                    return false;
                }

                item.setFlag((byte) (item.getFlag() | ItemFlag.LOCK.getValue()));
                chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(type, item).build()));
                return true;
            }
            case 5062000: {
                int equip_slot = cp.Decode4();
                Equip equip = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem((short) equip_slot);
                if (equip == null) {
                    return false;
                }
                equip.getEnhancement().useMiracleCube();
                chr.SendPacket(ResCUser.UserItemUnreleaseEffect(chr));
                chr.getMap().splitSendPacket(chr, ResCUser.UserItemUnreleaseEffect(chr), chr.getId());
                chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.EQUIP, equip).build()));
                MapleInventoryManipulator.addById(chr, 2430112, (short) 1);
                return true;
            }
            case 5062001: {
                int equip_slot = cp.Decode4();
                Equip equip = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem((short) equip_slot);
                if (equip == null) {
                    return false;
                }
                equip.getEnhancement().useHyperMiracleCube();
                chr.SendPacket(ResCUser.UserItemUnreleaseEffect(chr));
                chr.getMap().splitSendPacket(chr, ResCUser.UserItemUnreleaseEffect(chr), chr.getId());
                chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.EQUIP, equip).build()));
                MapleInventoryManipulator.addById(chr, 2430112, (short) 1);
                return true;
            }
            case 5062002: {
                int equip_slot = cp.Decode4();
                Equip equip = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem((short) equip_slot);
                if (equip == null) {
                    return false;
                }
                equip.getEnhancement().useMasterMiracleCube();
                chr.SendPacket(ResCUser.UserItemUnreleaseEffect(chr));
                chr.getMap().splitSendPacket(chr, ResCUser.UserItemUnreleaseEffect(chr), chr.getId());
                chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.EQUIP, equip).build()));
                MapleInventoryManipulator.addById(chr, 2430112, (short) 1);
                return true;
            }
            case 5062003: {
                int equip_slot = cp.Decode4();
                Equip equip = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem((short) equip_slot);
                if (equip == null) {
                    return false;
                }
                equip.getEnhancement().useHyperMasterMiracleCube();
                chr.SendPacket(ResCUser.UserItemUnreleaseEffect(chr));
                chr.getMap().splitSendPacket(chr, ResCUser.UserItemUnreleaseEffect(chr), chr.getId());
                chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.EQUIP, equip).build()));
                MapleInventoryManipulator.addById(chr, 2430112, (short) 1);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean cashItem507_Megaphone(TacosCharacter chr, int cash_item_id, ClientPacket cp) {
        byte channel = (byte) chr.getClient().getChannelId();
        switch (cash_item_id) {
            // メガホン
            case 5070000: {
                String message = cp.DecodeStr();

                chr.getChannelServer().broadcastMegaphonePacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_SPEAKERCHANNEL, PB_BroadcastMsg.builder().chr(chr).message(message).build()));
                return true;
            }
            // 拡声器
            case 5071000: {
                String message = cp.DecodeStr();
                byte ear = Config.LessOrEqual(Region.KMS, 31) ? 1 : cp.Decode1();

                chr.getWorld().broadcastMegaphonePacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_SPEAKERWORLD, PB_BroadcastMsg.builder().chr(chr).message(message).ear(ear).build()));
                return true;
            }
            // 高機能拡声器 (使えない)
            case 5072000: {
                break;
            }
            // ハート拡声器
            case 5073000: {
                String message = cp.DecodeStr();
                byte ear = cp.Decode1();

                chr.getWorld().broadcastMegaphonePacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_HEARTSPEAKER, PB_BroadcastMsg.builder().chr(chr).message(message).ear(ear).build()));
                return true;
            }
            // ドクロ拡声器
            case 5074000: {
                String message = cp.DecodeStr();
                byte ear = cp.Decode1();

                chr.getWorld().broadcastMegaphonePacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_SKULLSPEAKER, PB_BroadcastMsg.builder().chr(chr).message(message).ear(ear).build()));
                return true;
            }
            case 5075000: // メッセージ送信機 (MapleTV)
            {
                // 15 sec
                byte nFlag = cp.Decode1();
                String name_to = cp.DecodeStr();
                List<String> messages = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    messages.add(cp.DecodeStr());
                }

                TacosCharacter chr_to = name_to.equals("") ? null : chr.getWorld().findOnlinePlayer(name_to, false);
                chr.SendPacket(ResCMapleTVMan.MapleTVUpdateMessage(nFlag, 0, chr, messages, chr_to));
                return true;
            }
            case 5075001: // スターメッセージ送信機
            {
                // 30 sec
                List<String> messages = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    messages.add(cp.DecodeStr());
                }

                chr.SendPacket(ResCMapleTVMan.MapleTVUpdateMessage((byte) 1, 1, chr, messages, null));
                return true;
            }
            case 5075002: // ハートメッセージ送信機
            {
                // 60 sec
                String name_to = cp.DecodeStr();
                List<String> messages = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    messages.add(cp.DecodeStr());
                }

                TacosCharacter chr_to = chr.getWorld().findOnlinePlayer(name_to, false);
                chr.SendPacket(ResCMapleTVMan.MapleTVUpdateMessage((byte) 3, 2, chr, messages, chr_to));
                return true;
            }
            case 5075003: // メッセージ拡声器
            {
                byte nFlag = cp.Decode1();
                byte ear = cp.Decode1();
                String name_to = cp.DecodeStr();
                List<String> messages = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    messages.add(cp.DecodeStr());
                }

                TacosCharacter chr_to = name_to.equals("") ? null : chr.getWorld().findOnlinePlayer(name_to, false);
                chr.SendPacket(ResCMapleTVMan.MapleTVUpdateMessage(nFlag, 0, chr, messages, chr_to));
                return true;
            }
            case 5075004: // スターメッセージ拡声器
            {
                byte ear = cp.Decode1();
                List<String> messages = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    messages.add(cp.DecodeStr());
                }

                chr.SendPacket(ResCMapleTVMan.MapleTVUpdateMessage((byte) 1, 1, chr, messages, null));
                // TODO : smega things
                return true;
            }
            case 5075005: // ハートメッセージ拡声器
            {
                byte ear = cp.Decode1();
                String name_to = cp.DecodeStr();
                List<String> messages = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    messages.add(cp.DecodeStr());
                }

                TacosCharacter chr_to = chr.getWorld().findOnlinePlayer(name_to, false);
                chr.SendPacket(ResCMapleTVMan.MapleTVUpdateMessage((byte) 3, 2, chr, messages, chr_to));
                return true;
            }
            // アイテム拡声器
            case 5076000: {
                String message = cp.DecodeStr();
                byte ear = cp.Decode1();
                byte showitem = cp.Decode1();
                Item item = null;
                if (showitem == 1) {
                    // アイテム情報
                    int type = cp.Decode4();
                    int slot = cp.Decode4();
                    item = chr.getInventory(MapleInventoryType.getByType((byte) type)).getItem((short) slot);
                }

                chr.getWorld().broadcastMegaphonePacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_ITEMSPEAKER, PB_BroadcastMsg.builder().chr(chr).message(message).ear(ear).item(item).build()));
                return true;
            }
            // 三連拡声器
            case 5077000: {
                List<String> messages = new LinkedList<>();
                // メッセージの行数
                byte line = cp.Decode1();
                if (3 < line) {
                    DebugLogger.ErrorLog("三連拡声器 - lines");
                    return false;
                }
                for (int i = 0; i < line; i++) {
                    String message = cp.DecodeStr();
                    if (message.length() > 65) {
                        DebugLogger.ErrorLog("三連拡声器 - letters");
                        return false;
                    }
                    messages.add(message);
                }
                byte ear = cp.Decode1();

                chr.getWorld().broadcastMegaphonePacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_ARTSPEAKERWORLD, PB_BroadcastMsg.builder().chr(chr).ear(ear).messages(messages).build()));
                return true;
            }
            default: {
                break;
            }
        }

        chr.updateStat();
        return false;
    }

    public static boolean cashItem517_PetNameChange(TacosCharacter chr, int cash_item_id, ClientPacket cp) {
        switch (cash_item_id) {
            case 5170000: {
                TacosPet pet = null;

                if (Config.LessOrEqual(Region.JMS, 147)) {
                    pet = chr.getPetByIndex(0);
                } else {
                    long pet_uid = cp.Decode8();
                    pet = chr.getPetByUniqueId(pet_uid);
                }

                String pet_name = cp.DecodeStr();

                if (pet == null) {
                    chr.updateInv();
                    return false;
                }

                // new name
                pet.setName(pet_name);
                chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.CASH, chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition())).build()));
                chr.getMap().splitSendPacket(chr, ResCUser_Pet.PetNameChanged(chr, pet, pet_name), chr.getId());
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean cashItem520_Money(TacosCharacter chr, int cash_item_id) {
        switch (cash_item_id) {
            case 5200000:
            case 5200001:
            case 5200002: // メル袋
            {
                int meso = MapleItemInformationProvider.getInstance().getMeso(cash_item_id);

                if (!chr.gainMeso(meso, false)) {
                    chr.SendPacket(ResCUserLocal.MesoGive_Failed());
                    return false;
                }

                chr.SendPacket(ResCUserLocal.MesoGive_Succeeded(meso));
                return true;
            }
            case 5201000:
            case 5201001:
            case 5201002: // パチンコ玉
            {
                int tama = MapleItemInformationProvider.getInstance().getInt(cash_item_id, "info/dama");
                if (!chr.gainTama(tama, true)) {
                    chr.SendPacket(ResCUserLocal.PachinkoBoxFailure());
                    return false;
                }

                chr.SendPacket(ResCUserLocal.PachinkoBoxSuccess(tama));
                return true;
            }
            case 5202000: // ランダムメル袋 (未実装アイテム)
            {
                int randommeso = 0;
                int meso = MapleItemInformationProvider.getInstance().getInt(cash_item_id, "info/meso");
                int mesomax = MapleItemInformationProvider.getInstance().getInt(cash_item_id, "info/mesomax");
                int mesomin = MapleItemInformationProvider.getInstance().getInt(cash_item_id, "info/mesomin");
                int mesostdev = MapleItemInformationProvider.getInstance().getInt(cash_item_id, "info/mesostdev");

                Random random = new Random();
                int r = random.nextInt(4);

                switch (r) {
                    case 0:
                        randommeso = mesomin;
                        break;
                    case 1:
                        randommeso = mesostdev;
                        break;
                    case 2:
                        randommeso = meso;
                        break;
                    case 3:
                        randommeso = mesomax;
                        break;
                    default:
                        randommeso = mesomin;
                        break;
                }

                if (!chr.gainMeso(randommeso, false)) {
                    chr.SendPacket(ResCUserLocal.Random_Mesobag_Failed());
                    return false;
                }

                chr.SendPacket(ResCUserLocal.Random_Mesobag_Succeed((byte) (r + 1), randommeso));
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // gachapon item.
    public static int OnUserUseGachaponBoxRequest(TacosCharacter chr, short slot, int item_id) {
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
}
