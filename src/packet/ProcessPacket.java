// クライアントへ送信するパケットの生成
package packet;

import packet.server.ServerPacket;
import client.inventory.IItem;
import handling.MaplePacket;
import java.util.List;
import packet.server.response.struct.GW_ItemSlotBase;

public class ProcessPacket {

    // 独自実装
    public static class Custom {

        public static MaplePacket Hash() {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_CUSTOM_WZ_HASH);
            p.EncodeStr("Skill.wz");
            return p.Get();
        }

        public static MaplePacket Patch(int address, byte memory[]) {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_CUSTOM_CLIENT_PATCH);
            p.Encode4(address);
            p.Encode2((short) memory.length);
            p.EncodeBuffer(memory);
            return p.Get();
        }

        public static MaplePacket Scan(int address, short size) {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_CUSTOM_MEMORY_SCAN);
            p.Encode4(address);
            p.Encode2(size);
            return p.Get();
        }
    }

    // 宅配
    public static class HomeDelivery {

        private enum Action {
            // 不明
            ERROR_09((byte) 0x09),
            // NPCドイから宅配UIを開く
            OPEN((byte) 0x0A),
            // メルが足りません。
            ERROR_0C((byte) 0x0C),
            // 間違った要請です。
            ERROR_0D((byte) 0x0D),
            // 宛先の名前を再確認してください。
            ERROR_0E((byte) 0x0E),
            // 同じID内のキャラクターには送れません。
            ERROR_0F((byte) 0x0F),
            // 宛先の宅配保管箱に空きがありません。
            ERROR_10((byte) 0x10),
            // 宅配を受け取ることができないキャラクターです。
            ERROR_11((byte) 0x11),
            // 1個しか持てないアイテムが宛先の宅配保管箱にあります。
            ERROR_12((byte) 0x12),
            // 宅配物を発送しました。
            SEND((byte) 0x13),
            // 原因不明のエラーが発生しました。
            ERROR_14((byte) 0x14),
            // 空きがあるか確認してください。
            ERROR_16((byte) 0x16),
            // 1個しか持てないアイテムがありメルとアイテムを取り出すことができませんでした。
            ERROR_17((byte) 0x17),
            // 宅配物を受け取りました。
            RECV((byte) 0x18),
            // クラッシュ
            ERROR_19((byte) 0x19),
            // 宅配物到着! 通知
            BALLOON_1A((byte) 0x1A),
            // 速達UI
            OPEN_EXPRESS((byte) 0x1B),
            // 宅配物到着! 通知
            BALLOON_1C((byte) 0x1C),
            // 原因不明のエラーが発生しました。
            ERROR_1D((byte) 0x1D),
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
        public static MaplePacket Open(boolean isQuick, boolean isNPC) {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_Parcel);
            // 0x3B or 0x40
            if (isQuick) {
                // 速達のUI
                p.Encode1(Action.OPEN_EXPRESS.Get());
                return p.Get();
            }

            // 通常のUI
            p.Encode1((byte) Action.OPEN.Get());
            // NPC会話 or 速達の通知から開いたかの判定
            p.Encode1((byte) (isNPC ? 0x00 : 0x01));
            p.Encode1((byte) 0x00);
            p.Encode1((byte) 0x00);
            return p.Get();
        }

        public static MaplePacket Send() {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_Parcel);
            p.Encode1((byte) Action.SEND.Get());
            return p.Get();
        }
    }

    // ビシャスのハンマー
    public static class ViciousHammer {

        private enum Action {
            // 成功
            SUCCESS((byte) 0x38),
            // 失敗
            FAILURE((byte) 0x39),
            // 強化成功通知 (上記の値以外なら何でも更新通知扱いとなる)
            UPDATE((byte) 0x3A),
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

        // ビシャスのハンマーの成功ダイアログで表示される残りアップグレード数を通知する
        public static MaplePacket Update(int hammered) {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_GoldHammerResult);
            // ビシャスのハンマーの使用回数を通知するフラグ, 0x38,0x39以外なら何でもOK
            p.Encode1(Action.UPDATE.Get());
            // 未使用
            p.Encode4(0);
            // 2 - 使用回数 = 残り回数
            p.Encode4(hammered);
            return p.Get();
        }

        // ビシャスのハンマーの成功ダイアログを表示
        public static MaplePacket Success() {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_GoldHammerResult);
            // 成功フラグ
            p.Encode1(Action.SUCCESS.Get());
            /*
                0x00        アップグレード可能回数が1回増えました。あと(2 - hammered)回増やすことが出来ます。
                0x00以外    原因不明の不具合
             */
            p.Encode4(0);
            return p.Get();
        }

        // ビシャスのハンマーの失敗ダイアログを表示, クライアント側で弾かれるのでチート以外では表示されることがないメッセージ
        public static MaplePacket Failure(int error) {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_GoldHammerResult);
            // 失敗フラグ
            p.Encode1(Action.FAILURE.Get());
            /*
                0x01            このアイテムには使用できません。
                0x02            すでにアップグレード可能回数を超えました。これ以上使用することができません。
                0x03            ホーンテイルのネックレスには使用できません。
                上記以外        原因不明の不具合
             */
            p.Encode4(error);
            return p.Get();
        }
    }

    // ベガの呪文書
    public static class VegaScroll {

        private enum Action {
            // 成功
            SUCCESS((byte) 0x3B),
            // 不明
            START_NOT_USED((byte) 0x3C),
            // 開始
            START((byte) 0x3E),
            // 失敗
            FAILURE((byte) 0x40),
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

        // ベガの呪文書開始
        public static MaplePacket Start() {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_VegaResult);
            // 0x3E or 0x40
            p.Encode1(Action.START.Get());
            return p.Get();
        }

        // ベガの呪文書の結果
        public static MaplePacket Result(boolean isSuccess) {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_VegaResult);
            // 成功可否
            p.Encode1((byte) (isSuccess ? Action.SUCCESS.Get() : Action.FAILURE.Get()));
            return p.Get();
        }
    }

    // 拡声器
    public static class Megaphone {

        private enum Action {
            // 青文字, [告知事項]
            UNKNOWN_00((byte) 0x00),
            // ダイアログ
            UNKNOWN_01((byte) 0x01),
            // メガホン
            MEGAPHONE_BLUE((byte) 0x02),
            // 拡声器
            MEGAPHONE((byte) 0x03),
            // 画面上部
            UNKNOWN_04((byte) 0x04),
            // ピンク文字
            UNKNOWN_05((byte) 0x05),
            // 青文字
            UNKNOWN_06((byte) 0x06),
            // 用途不明, 0x00B93F3F[0x07] = 00B93E27
            UNKNOWN_07((byte) 0x07),
            // アイテム拡声器
            MEGAPHONE_ITEM((byte) 0x08),
            // ワールド拡声器, 未実装
            MEGAPHONE_GREEN((byte) 0x09),
            // 三連拡声器
            MEGAPHONE_TRIPLE((byte) 0x0A),
            // 用途不明, 0x00B93F3F[0x0B] = 00B93ECA
            UNKNOWN_0B((byte) 0x0B),
            // ハート拡声器
            MEGAPHONE_HEART((byte) 0x0C),// v131 -> 0x08
            // ドクロ拡声器
            MEGAPHONE_SKULL((byte) 0x0D), // v131 -> 0x09
            // ガシャポン
            MEGAPHONE_GASHAPON((byte) 0x0E),
            // 青文字, 名前:アイテム名(xxxx個))
            UNKNOWN_0F((byte) 0x0F),
            // 体験用アバター獲得
            MEGAPHONE_AVATAR((byte) 0x10),
            // 青文字, アイテム表示
            UNKNOWN_11((byte) 0x11),
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

        // メガホン
        public static MaplePacket MegaphoneBlue(String text) {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
            p.Encode1(Action.MEGAPHONE_BLUE.Get());
            p.EncodeStr(text);
            return p.Get();
        }

        // 拡声器
        public static MaplePacket Megaphone(String text, byte channel, byte ear) {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
            p.Encode1(Action.MEGAPHONE.Get());
            p.EncodeStr(text);
            p.Encode1((byte) (channel - 1));
            p.Encode1(ear);
            return p.Get();
        }

        // ハート拡声器
        public static MaplePacket MegaphoneHeart(String text, byte channel, byte ear) {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
            p.Encode1(Action.MEGAPHONE_HEART.Get());
            p.EncodeStr(text);
            p.Encode1((byte) (channel - 1));
            p.Encode1(ear);
            return p.Get();
        }

        // ドクロ拡声器
        public static MaplePacket MegaphoneSkull(String text, byte channel, byte ear) {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
            p.Encode1(Action.MEGAPHONE_SKULL.Get());
            p.EncodeStr(text);
            p.Encode1((byte) (channel - 1));
            p.Encode1(ear);
            return p.Get();
        }

        // アイテム拡声器
        public static MaplePacket MegaphoneItem(String text, byte channel, byte ear, byte showitem, IItem item) {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
            p.Encode1(Action.MEGAPHONE_ITEM.Get());
            p.EncodeStr(text);
            p.Encode1((byte) (channel - 1));
            p.Encode1(ear);
            p.Encode1(showitem);
            if (showitem != 0) {
                p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
            }
            return p.Get();
        }

        // 三連拡声器
        public static MaplePacket MegaphoneTriple(List<String> text, byte channel, byte ear) {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
            p.Encode1(Action.MEGAPHONE_TRIPLE.Get());
            // 1行目
            p.EncodeStr(text.get(0));
            p.Encode1((byte) text.size());
            for (int i = 1; i < text.size(); i++) {
                p.EncodeStr(text.get(i));
            }
            p.Encode1((byte) (channel - 1));
            p.Encode1(ear);
            return p.Get();
        }
    }

    // 謎
    public static class Test {

        // 0x005E @005E 00, ミニマップ点滅, 再読み込みかも?
        public static MaplePacket ReloadMiniMap() {
            ServerPacket p = new ServerPacket(ServerPacket.Header.UNKNOWN_RELOAD_MINIMAP);
            p.Encode1((byte) 0x00);
            return p.Get();
        }

        // 0x0083 @0083, 画面の位置をキャラクターを中心とした場所に変更, 背景リロードしてるかも?
        public static MaplePacket ReloadMap() {
            ServerPacket p = new ServerPacket(ServerPacket.Header.LP_ClearBackgroundEffect);
            return p.Get();
        }
    }
}
