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
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.Region;
import config.ServerConfig;
import config.Version;
import debug.Debug;
import handling.channel.handler.PlayerHandler;
import handling.world.World;
import java.util.LinkedList;
import java.util.List;
import packet.ClientPacket;
import packet.ops.OpsBodyPart;
import packet.ops.OpsBroadcastMsg;
import packet.ops.arg.ArgBroadcastMsg;
import packet.request.ReqCUser_Pet;
import packet.response.ResCUser;
import packet.response.ResCWvsContext;
import packet.response.wrapper.ResWrapper;
import server.MapleItemInformationProvider;
import server.maps.FieldLimitType;
import server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class ReqSub_UserConsumeCashItemUseRequest {

    public static boolean OnUserConsumeCashItemUseRequestInternal(MapleCharacter chr, MapleMap map, ClientPacket cp) {
        int timestamp = ServerConfig.JMS180orLater() ? cp.Decode4() : 0;
        short cash_item_slot = cp.Decode2();
        int cash_item_id = cp.Decode4();

        Runnable item_use = chr.checkItemSlot(MapleInventoryType.CASH, cash_item_slot, cash_item_id);
        if (item_use == null) {
            Debug.ErrorLog("OnUserConsumeCashItemUseRequest : invalid item.");
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
            case 507: {
                if (cashItem507_Megaphone(chr, cash_item_id, cp)) {
                    item_use.run();
                }
                return true;
            }
            case 524: {
                // TODO : fix
                ReqCUser_Pet.OnPetFood(chr, MapleInventoryType.CASH, cash_item_slot, cash_item_id);
                return true;
            }
            case 537: // 5370000
            {
                String message = cp.DecodeStr();
                chr.setADBoard(message);
                map.broadcastMessage(ResCUser.UserADBoard(chr));
                //item_use.run();
                return true;
            }
            default: {
                break;
            }
        }

        // not coded.
        Debug.ErrorLog("OnUserConsumeCashItemUseRequest : not coded yet. type = " + cash_item_type);
        return false;
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
            // MapleTV
            case 5075000:
            case 5075001:
            case 5075002:
            case 5075003:
            case 5075004:
            case 5075005: {
                break;
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
                    Debug.ErrorLog("三連拡声器 - lines");
                    return false;
                }
                for (int i = 0; i < line; i++) {
                    String message = cp.DecodeStr();
                    if (message.length() > 65) {
                        Debug.ErrorLog("三連拡声器 - letters");
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

        chr.SendPacket(ResWrapper.enableActions());
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
