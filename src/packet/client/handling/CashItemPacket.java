/*
 * Copyright (C) 2024 Riremito
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
package packet.client.handling;

import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import handling.world.World;
import java.util.LinkedList;
import java.util.List;
import packet.client.ClientPacket;
import packet.server.response.MegaphoneResponse;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;

/**
 *
 * @author Riremito
 */
public class CashItemPacket {

    public static boolean Use(MapleClient c, ClientPacket p) {
        if ((ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion())) {
            p.Decode4();
        }
        short slotid = p.Decode2();
        int itemid = p.Decode4();
        IItem iteminfo = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem(slotid);
        if (iteminfo == null) {
            return false;
        }
        if (iteminfo.getItemId() != itemid) {
            return false;
        }
        if (iteminfo.getQuantity() < 1) {
            return false;
        }
        // 拡声器
        if (itemid / 10000 == 507) {
            return UseMegaphone(c, p, itemid);
        }
        return false;
    }

    // 勲章が装備されるスロットのID
    private static final short SLOT_EQUIPPED_MEDAL = (short) -21;

    // 勲章の名前を付けたキャラクター名
    private static String MegaphoneGetSenderName(MapleClient c) {
        IItem equipped_medal = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(SLOT_EQUIPPED_MEDAL);
        // "キャラクター名"
        if (equipped_medal == null) {
            return c.getPlayer().getName();
        }
        String medal_name = MapleItemInformationProvider.getInstance().getName(equipped_medal.getItemId());
        int padding = medal_name.indexOf("の勲章");

        if (padding > 0) {
            medal_name = medal_name.substring(0, padding);
        }

        // "<勲章> キャラクター名"
        return "<" + medal_name + "> " + c.getPlayer().getName();
    }

    public static boolean UseMegaphone(MapleClient c, ClientPacket p, int itemid) {
        switch (itemid) {
            // メガホン
            case 5070000: {
                String message = new String(p.DecodeBuffer());
                c.getPlayer().getMap().broadcastMessage(MegaphoneResponse.MegaphoneBlue(MegaphoneGetSenderName(c) + " : " + message));
                return true;
            }
            // 拡声器
            case 5071000: {
                String message = new String(p.DecodeBuffer());
                byte ear = p.Decode1();
                World.Broadcast.broadcastSmega(MegaphoneResponse.Megaphone(MegaphoneGetSenderName(c) + " : " + message, (byte) c.getChannel(), ear).getBytes());
                return true;
            }
            // 高機能拡声器 (使えない)
            case 5072000: {
                break;
            }
            // ハート拡声器
            case 5073000: {
                String message = new String(p.DecodeBuffer());
                byte ear = p.Decode1();
                byte channel = (byte) c.getChannel();
                World.Broadcast.broadcastSmega(MegaphoneResponse.MegaphoneHeart(MegaphoneGetSenderName(c) + " : " + message, (byte) c.getChannel(), ear).getBytes());
                return true;
            }
            // ドクロ拡声器
            case 5074000: {
                String message = new String(p.DecodeBuffer());
                byte ear = p.Decode1();
                World.Broadcast.broadcastSmega(MegaphoneResponse.MegaphoneSkull(MegaphoneGetSenderName(c) + " : " + message, (byte) c.getChannel(), ear).getBytes());
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
                String message = new String(p.DecodeBuffer());
                byte ear = p.Decode1();
                byte showitem = p.Decode1();
                IItem item = null;
                if (showitem == 0x01) {
                    // アイテム情報
                    int type = p.Decode4();
                    int slot = p.Decode4();
                    item = c.getPlayer().getInventory(MapleInventoryType.getByType((byte) type)).getItem((short) slot);
                }
                World.Broadcast.broadcastSmega(MegaphoneResponse.MegaphoneItem(MegaphoneGetSenderName(c) + " : " + message, (byte) c.getChannel(), ear, showitem, item).getBytes());
                return true;
            }
            // 三連拡声器
            case 5077000: {
                List<String> messages = new LinkedList<>();
                String sender = MegaphoneGetSenderName(c);
                // メッセージの行数
                byte line = p.Decode1();
                for (int i = 0; i < line; i++) {
                    String message = new String(p.DecodeBuffer());
                    if (message.length() > 65) {
                        break;
                    }
                    messages.add(sender + " : " + message);
                }
                byte ear = p.Decode1();
                World.Broadcast.broadcastSmega(MegaphoneResponse.MegaphoneTriple(messages, (byte) c.getChannel(), ear).getBytes());
                return true;
            }
            default: {
                break;
            }
        }

        c.ProcessPacket(MaplePacketCreator.enableActions());
        return false;
    }
}