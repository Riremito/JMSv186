// クライアントから送信されたパケットに対応する処理
package packet;

import client.MapleClient;

public class SendPacket {

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
        public static boolean Accept(MapleClient c, OutPacket p) {
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

        public static boolean Accept(MapleClient c, OutPacket p) {
            // 成功可否
            int action = p.Decode4();
            // 用途不明
            int hammered = p.Decode4();
            // 関数に成功可否を渡しても良いと思われるが、成功確率が100%なので意味がない
            c.ProcessPacket(ProcessPacket.ViciousHammer.Success());
            return true;
        }
    }

}
