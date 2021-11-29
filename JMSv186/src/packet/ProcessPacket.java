// クライアントへ送信するパケットの生成
package packet;

import handling.MaplePacket;

public class ProcessPacket {

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
            InPacket p = new InPacket(InPacket.Header.DUEY);
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
            InPacket p = new InPacket(InPacket.Header.DUEY);
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
            InPacket p = new InPacket(InPacket.Header.VICIOUS_HAMMER);
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
            InPacket p = new InPacket(InPacket.Header.VICIOUS_HAMMER);
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
            InPacket p = new InPacket(InPacket.Header.VICIOUS_HAMMER);
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

}
