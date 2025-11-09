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
package packet.request.sub;

import client.MapleCharacter;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import config.Region;
import config.ServerConfig;
import config.Version;
import constants.GameConstants;
import debug.DebugLogger;
import debug.DebugShop;
import server.server.Server_Game;
import handling.channel.handler.PlayerHandler;
import handling.world.World;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import packet.ClientPacket;
import packet.ops.OpsBodyPart;
import packet.ops.OpsBroadcastMsg;
import packet.ops.OpsShopScanner;
import packet.ops.arg.ArgBroadcastMsg;
import packet.request.ReqCUser;
import packet.request.ReqCUser_Pet;
import packet.response.ResCMapleTVMan;
import packet.response.ResCParcelDlg;
import packet.response.ResCUIItemUpgrade;
import packet.response.ResCUser;
import packet.response.ResCUserLocal;
import packet.response.ResCUser_Pet;
import packet.response.ResCWvsContext;
import packet.response.wrapper.ResWrapper;
import packet.response.wrapper.WrapCWvsContext;
import server.MapleItemInformationProvider;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.shops.HiredMerchant;

/**
 *
 * @author Riremito
 */
public class ReqSub_UserConsumeCashItemUseRequest {

    public static boolean OnUserConsumeCashItemUseRequestInternal(MapleMap map, MapleCharacter chr, ClientPacket cp) {
        int timestamp = ServerConfig.JMS180orLater() ? cp.Decode4() : 0;
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
                MapleCharacter target_chr = null;
                if (action == 0) {
                    map_id = cp.Decode4();
                } else {
                    String target_name = cp.DecodeStr();
                    target_chr = chr.getClient().getChannelServer().getPlayerStorage().getCharacterByName(target_name);
                    if (target_chr == null) {
                        return false;
                    }
                    map_id = target_chr.getMap().getId();
                }
                if (FieldLimitType.VipRock.check(chr.getClient().getChannelServer().getMapFactory().getMap(map_id).getFieldLimit())) {
                    return false;
                }
                item_use.run();
                if (action == 0) {
                    PlayerHandler.ChangeMap(chr.getClient(), map_id);
                } else {
                    chr.changeMap(target_chr.getMap(), target_chr.getMap().findClosestSpawnpoint(target_chr.getPosition()));
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

                final int buff = miip.getStateChangeItem(cash_item_id);
                if (buff != 0) {
                    for (MapleCharacter mChar : map.getCharactersThreadsafe()) {
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
                final List<HiredMerchant> hms = chr.getClient().getChannelServer().searchMerchant(target_item_id);
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
                chr.SendPacket(ResCParcelDlg.Open(true, false));
                chr.UpdateStat(true);
                return true;
            }
            case 537: // 5370000
            {
                String message = cp.DecodeStr();
                chr.setADBoard(message);
                map.broadcastMessage(ResCUser.UserADBoard(chr));
                chr.SendPacket(WrapCWvsContext.updateInv());
                return true;
            }
            case 545: {
                //MapleShopFactory.getInstance().getShop(11100).sendShop(chr.getClient());
                // test
                DebugShop ds = new DebugShop();
                ds.setRechargeAll();
                ds.setRandomItems(3);
                ds.start(chr);
                chr.SendPacket(WrapCWvsContext.updateInv());
                return true;
            }
            case 557: // 5570000
            {
                int inv_type = cp.Decode4(); // unused
                int item_slot = cp.Decode4();
                IItem item = chr.getInventory(MapleInventoryType.EQUIP).getItem((short) item_slot);

                if (item == null) {
                    return false;
                }

                Equip equip = (Equip) item;
                if (!GameConstants.canHammer(equip.getItemId()) || MapleItemInformationProvider.getInstance().getSlots(equip.getItemId()) <= 0) {
                    return false;
                }
                // TODO : supports 3+
                if (2 <= equip.getViciousHammer()) {
                    chr.SendPacket(ResCUIItemUpgrade.Failure(1));
                    return false;
                }

                equip.setViciousHammer(equip.getViciousHammer() + 1);
                equip.setUpgradeSlots(equip.getUpgradeSlots() + 1);

                chr.SendPacket(ResWrapper.addInventorySlot(MapleInventoryType.EQUIP, equip));
                chr.SendPacket(ResCUIItemUpgrade.Update(equip.getViciousHammer()));
                item_use.run();
                return true;
            }
            case 561: {
                int inv_type_equip = cp.Decode4(); // unused
                int equip_slot = cp.Decode4();
                int inv_type_use = cp.Decode4(); // unused
                int scroll_slot = cp.Decode4();

                if (ReqCUser.OnUserUpgradeItemUseRequest(map, chr, (short) scroll_slot, (short) equip_slot, cash_item_id)) {
                    item_use.run();
                    return true;
                }
                return false;
            }
            case 562: {
                ReqCUser.OnUserSkillLearnItemUseRequest(map, chr, cash_item_slot, cash_item_id);
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

    public static boolean cashItem506(MapleCharacter chr, int cash_item_id, ClientPacket cp) {
        switch (cash_item_id) {
            case 5060000: // ネームメーカー
            {
                short equipped_slot = cp.Decode2();
                int timestamp = Version.LessOrEqual(Region.JMS, 147) ? cp.Decode4() : 0; // not in JMS302

                if (0 <= equipped_slot) {
                    return false;
                }

                IItem item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) equipped_slot);
                if (item == null) {
                    return false;
                }

                item.setOwner(chr.getName());
                chr.SendPacket(ResWrapper.addInventorySlot(MapleInventoryType.EQUIP, item));
                return true;
            }
            case 5060001: // 封印の錠
            {
                int inv_type = cp.Decode4(); // unused
                int item_slot = cp.Decode4();

                MapleInventoryType type = MapleInventoryType.getByType((byte) inv_type);

                IItem item = chr.getInventory(type).getItem((short) item_slot);
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
                chr.SendPacket(ResWrapper.addInventorySlot(type, item));
                return true;
            }
            case 5062000:
            case 5062001:
            case 5062002:
            case 5062003: // miracle cubes
            {
                int equip_slot = cp.Decode4();
                IItem item = chr.getInventory(MapleInventoryType.EQUIP).getItem((short) equip_slot);
                if (item == null) {
                    return false;
                }

                Equip equip = (Equip) item;
                equip.resetPotential(cash_item_id == 5062001 || cash_item_id == 5062003, cash_item_id == 5062002 || cash_item_id == 5062003);
                chr.SendPacket(ResCUser.UserItemUnreleaseEffect(chr));
                chr.getMap().broadcastMessage(chr, ResCUser.UserItemUnreleaseEffect(chr), false);
                chr.SendPacket(ResWrapper.addInventorySlot(MapleInventoryType.EQUIP, equip));
                //MapleInventoryManipulator.addById(chr.getClient(), 2430112, (short) 1);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static MapleCharacter findCharacterByName(String name_to) {
        int ch = World.Find.findChannel(name_to);
        if (ch != -1) {
            MapleCharacter chr_to = Server_Game.getInstance(ch).getPlayerStorage().getCharacterByName(name_to);
            return chr_to;
        }

        return null;
    }

    public static boolean cashItem507_Megaphone(MapleCharacter chr, int cash_item_id, ClientPacket cp) {
        byte channel = (byte) chr.getClient().getChannel();
        switch (cash_item_id) {
            // メガホン
            case 5070000: {
                String message = cp.DecodeStr();

                ArgBroadcastMsg bma = new ArgBroadcastMsg();
                bma.bm = OpsBroadcastMsg.BM_SPEAKERCHANNEL;
                bma.chr = chr;
                bma.message = message;
                chr.getClient().getChannelServer().broadcastSmega(ResCWvsContext.BroadcastMsg(bma).getBytes());
                return true;
            }
            // 拡声器
            case 5071000: {
                String message = cp.DecodeStr();
                byte ear = Version.LessOrEqual(Region.KMS, 31) ? 1 : cp.Decode1();

                ArgBroadcastMsg bma = new ArgBroadcastMsg();
                bma.bm = OpsBroadcastMsg.BM_SPEAKERWORLD;
                bma.chr = chr;
                bma.message = message;
                bma.ear = ear;
                World.Broadcast.broadcastSmega(ResCWvsContext.BroadcastMsg(bma).getBytes());
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

                ArgBroadcastMsg bma = new ArgBroadcastMsg();
                bma.bm = OpsBroadcastMsg.BM_HEARTSPEAKER;
                bma.chr = chr;
                bma.message = message;
                bma.ear = ear;
                World.Broadcast.broadcastSmega(ResCWvsContext.BroadcastMsg(bma).getBytes());
                return true;
            }
            // ドクロ拡声器
            case 5074000: {
                String message = cp.DecodeStr();
                byte ear = cp.Decode1();

                ArgBroadcastMsg bma = new ArgBroadcastMsg();
                bma.bm = OpsBroadcastMsg.BM_SKULLSPEAKER;
                bma.chr = chr;
                bma.message = message;
                bma.ear = ear;
                World.Broadcast.broadcastSmega(ResCWvsContext.BroadcastMsg(bma).getBytes());
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

                MapleCharacter chr_to = name_to.equals("") ? null : findCharacterByName(name_to);
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

                MapleCharacter chr_to = findCharacterByName(name_to);
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

                MapleCharacter chr_to = name_to.equals("") ? null : findCharacterByName(name_to);
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

                MapleCharacter chr_to = findCharacterByName(name_to);
                chr.SendPacket(ResCMapleTVMan.MapleTVUpdateMessage((byte) 3, 2, chr, messages, chr_to));
                return true;
            }
            // アイテム拡声器
            case 5076000: {
                String message = cp.DecodeStr();
                byte ear = cp.Decode1();
                byte showitem = cp.Decode1();
                IItem item = null;
                if (showitem == 1) {
                    // アイテム情報
                    int type = cp.Decode4();
                    int slot = cp.Decode4();
                    item = chr.getInventory(MapleInventoryType.getByType((byte) type)).getItem((short) slot);
                }

                ArgBroadcastMsg bma = new ArgBroadcastMsg();
                bma.bm = OpsBroadcastMsg.BM_ITEMSPEAKER;
                bma.chr = chr;
                bma.message = message;
                bma.ear = ear;
                bma.item = item;
                World.Broadcast.broadcastSmega(ResCWvsContext.BroadcastMsg(bma).getBytes());
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

                ArgBroadcastMsg bma = new ArgBroadcastMsg();
                bma.bm = OpsBroadcastMsg.MEGAPHONE_TRIPLE;
                bma.chr = chr;
                bma.ear = ear;
                bma.multi_line = true;
                bma.messages = messages;

                World.Broadcast.broadcastSmega(ResCWvsContext.BroadcastMsg(bma).getBytes());
                return true;
            }
            default: {
                break;
            }
        }

        chr.SendPacket(WrapCWvsContext.updateStat());
        return false;
    }

    public static boolean cashItem517_PetNameChange(MapleCharacter chr, int cash_item_id, ClientPacket cp) {
        switch (cash_item_id) {
            case 5170000: {
                MaplePet pet = null;

                if (Version.LessOrEqual(Region.JMS, 147)) {
                    pet = chr.getPet(0);
                } else {
                    long pet_uid = cp.Decode8();
                    pet = chr.getPetByUniqueId(pet_uid);
                }

                String pet_name = cp.DecodeStr();

                if (pet == null) {
                    chr.SendPacket(WrapCWvsContext.updateInv());
                    return false;
                }

                // new name
                pet.setName(pet_name);
                chr.SendPacket(ResWrapper.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition())));
                chr.getMap().broadcastMessage(chr, ResCUser_Pet.PetNameChanged(chr, pet, pet_name), true);
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean cashItem520_Money(MapleCharacter chr, int cash_item_id) {
        switch (cash_item_id) {
            case 5200000:
            case 5200001:
            case 5200002: // メル袋
            {
                final int meso = MapleItemInformationProvider.getInstance().getMeso(cash_item_id);

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
                    chr.SendPacket(ResCUserLocal.RandomMesoBagFailed());
                    return false;
                }

                chr.SendPacket(ResCUserLocal.RandomMesoBagSuccess((byte) (r + 1), randommeso));
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // 勲章の名前を付けたキャラクター名
    public static String MegaphoneGetSenderName(MapleCharacter chr) {
        IItem equipped_medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(OpsBodyPart.BP_MEDAL.getSlot());
        // "キャラクター名"
        if (equipped_medal == null) {
            return chr.getName();
        }
        String medal_name = MapleItemInformationProvider.getInstance().getName(equipped_medal.getItemId());
        //Debug.DebugLog("medal = " + equipped_medal.getItemId());
        if (medal_name == null) {
            return chr.getName();
        }
        int padding = medal_name.indexOf("の勲章");
        if (padding > 0) {
            medal_name = medal_name.substring(0, padding);
        }
        // "<勲章> キャラクター名"
        return "<" + medal_name + "> " + chr.getName();
    }
}
