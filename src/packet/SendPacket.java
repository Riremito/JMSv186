// クライアントから送信されたパケットに対応する処理
package packet;

import packet.client.ClientPacket;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import debug.Debug;
import handling.world.World;
import java.util.LinkedList;
import java.util.List;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;

public class SendPacket {

    // 独自実装
    public static class Custom {

        public static boolean Hash(ClientPacket p) {
            // v186以外は無視
            if (ServerConfig.version != 186) {
                return true;
            }
            final String wz_hash = p.DecodeStr();
            Debug.DebugLog(wz_hash);
            // v186 Skill.wz
            return wz_hash.startsWith("2e6008284345bbf5552b45ba206464404e474cbe8d8ba31bd61d0b4733422948");
        }

        public static boolean Scan(ClientPacket p) {
            // v186以外は無視
            if (ServerConfig.version != 186) {
                return true;
            }
            int scan_address = p.Decode4();
            byte scan_result[] = p.DecodeBuffer();
            // v186 damage hack
            if (scan_address == (int) 0x008625B5 && scan_result[0] == (byte) 0x8B && scan_result[1] == (byte) 0x45 && scan_result[2] == (byte) 0x18) {
                return true;
            }

            return false;
        }
    }

    public static class HomeDelivery {

        private enum Action {
            SEND((byte) 0x03),
            CLOSE((byte) 0x08),
            UNKNOWN((byte) -1);

            public static Action Find(byte b) {
                for (final Action o : Action.values()) {
                    if (o.Get() == b) {
                        return o;
                    }
                }

                return UNKNOWN;
            }

            private byte value;

            Action(byte b) {
                value = b;
            }

            Action() {
                value = -1;
            }

            public byte Get() {
                return value;
            }

        };

        // 宅配
        public static boolean Accept(MapleClient c, ClientPacket p) {
            // 処理内容
            byte action = p.Decode1();

            switch (Action.Find(action)) {
                // 配送
                case SEND: {
                    c.ProcessPacket(ProcessPacket.HomeDelivery.Send());
                    return true;
                }
                // 閉じる
                case CLOSE: {
                    // 厳密に宅配可能状態か判断する場合は宅配UIを開いた時に何らかのフラグをサーバー側で保持して閉じるパケットが送信されたフラグをオフにする必要がある
                    return true;
                }
                default:
                    break;
            }

            return false;
        }
    }

    // ビシャスのハンマー
    public static class ViciousHammer {
        // @0119 [38 00 00 00] [00 00 00 00]
        // 0x38が成功フラグなのでクライアント側から成功可否を通知している可能性がある

        public static boolean Accept(MapleClient c, ClientPacket p) {
            // 成功可否
            int action = p.Decode4();
            // 用途不明
            int hammered = p.Decode4();
            // 関数に成功可否を渡しても良いと思われるが、成功確率が100%なので意味がない
            c.ProcessPacket(ProcessPacket.ViciousHammer.Success());
            return true;
        }
    }

    // 拡声器
    public static class CashItem {

        public static boolean Use(MapleClient c, ClientPacket p) {
            if (ServerConfig.version > 131) {
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
                return Megaphone.Use(c, p, itemid);
            }

            return false;
        }

        public static class Megaphone {

            // 勲章が装備されるスロットのID
            private static final short SLOT_EQUIPPED_MEDAL = (short) -21;

            // 勲章の名前を付けたキャラクター名
            private static String GetSenderName(MapleClient c) {
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

            public static boolean Use(MapleClient c, ClientPacket p, int itemid) {
                switch (itemid) {
                    // メガホン
                    case 5070000: {
                        String message = new String(p.DecodeBuffer());
                        c.getPlayer().getMap().broadcastMessage(ProcessPacket.Megaphone.MegaphoneBlue(GetSenderName(c) + " : " + message));
                        return true;
                    }
                    // 拡声器
                    case 5071000: {
                        String message = new String(p.DecodeBuffer());
                        byte ear = p.Decode1();
                        World.Broadcast.broadcastSmega(ProcessPacket.Megaphone.Megaphone(GetSenderName(c) + " : " + message, (byte) c.getChannel(), ear).getBytes());
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
                        World.Broadcast.broadcastSmega(ProcessPacket.Megaphone.MegaphoneHeart(GetSenderName(c) + " : " + message, (byte) c.getChannel(), ear).getBytes());
                        return true;
                    }
                    // ドクロ拡声器
                    case 5074000: {
                        String message = new String(p.DecodeBuffer());
                        byte ear = p.Decode1();
                        World.Broadcast.broadcastSmega(ProcessPacket.Megaphone.MegaphoneSkull(GetSenderName(c) + " : " + message, (byte) c.getChannel(), ear).getBytes());
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
                        World.Broadcast.broadcastSmega(ProcessPacket.Megaphone.MegaphoneItem(GetSenderName(c) + " : " + message, (byte) c.getChannel(), ear, showitem, item).getBytes());
                        return true;
                    }
                    // 三連拡声器
                    case 5077000: {
                        List<String> messages = new LinkedList<>();
                        String sender = GetSenderName(c);
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
                        World.Broadcast.broadcastSmega(ProcessPacket.Megaphone.MegaphoneTriple(messages, (byte) c.getChannel(), ear).getBytes());
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

    }

}
