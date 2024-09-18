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
package packet.response;

import handling.MaplePacket;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class ViciousHammerResponse {

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
