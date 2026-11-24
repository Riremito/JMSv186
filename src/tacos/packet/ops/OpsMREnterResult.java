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
package tacos.packet.ops;

import tacos.config.Config;
import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public enum OpsMREnterResult implements IPacketOps {
    MREnterResult_Success(0),
    MREnterResult_NoRoom(1),
    MREnterResult_Full(2),
    MREnterResult_Busy(3),
    MREnterResult_Dead(4),
    MREnterResult_Event(5),
    MREnterResult_PermissionDenied(6),
    MREnterResult_NoTrading(7),
    MREnterResult_Etc(8),
    MREnterResult_OnlyInSameField(9),
    MREnterResult_NearPortal(10),
    MREnterResult_CreateCountOver(11),
    MREnterResult_CreateIPCountOver(12),
    MREnterResult_ExistMiniRoom(13),
    MREnterResult_NotAvailableField_Game(14),
    MREnterResult_NotAvailableField_PersonalShop(15),
    MREnterResult_NotAvailableField_EntrustedShop(16),
    MREnterResult_OnBlockedList(17),
    MREnterResult_IsManaging(18),
    MGEnterResult_Tournament(19),
    MGEnterResult_AlreadyPlaying(20),
    MGEnterResult_NotEnoughMoney(21),
    MGEnterResult_InvalidPassword(22),
    MREnterResult_NotAvailableField_ShopScanner(23),
    MREnterResult_Expired(24),
    MREnterResult_TooShortTimeInterval(25),
    UNKNOWN;

    private int value;

    private OpsMREnterResult(int value) {
        this.value = value;
    }

    private OpsMREnterResult() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int value) {
        this.value = value;
    }

    public static void clear() {
        for (OpsMREnterResult ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        // JMS146, JMS147 : CMiniRoomBaseDlg::OnEnterResultStatic
        // no CreateCountOver, CreateIPCountOver, NotAvailableField_ShopScanner, Expired and TooShortTimeInterval
        if (Config.LessOrEqual(Region.JMS, 147)) {
            clear();
            MREnterResult_Success.set(0);
            MREnterResult_NoRoom.set(1); // 既に閉じている部屋です。
            MREnterResult_Full.set(2); // 定員超過で入場出来ません。
            MREnterResult_Busy.set(3); // 別のことを処理中です。
            MREnterResult_Dead.set(4); // 死んだ状態には出来ないことです。
            MREnterResult_Event.set(5); // イベント中には出来ないことです。
            MREnterResult_PermissionDenied.set(6); // このキャラクターには出来ないことです。
            MREnterResult_NoTrading.set(7); // 他の交換は不可能な状態です。
            MREnterResult_Etc.set(8); // no message
            MREnterResult_OnlyInSameField.set(9); // 同じマップにいる時のみ交換ができます。
            MREnterResult_NearPortal.set(10); // ポータル近くでは開店できません。
            MREnterResult_ExistMiniRoom.set(11); // ここではオープン出来ません。 (same message as ExistMiniRoom in KMST330)
            MREnterResult_NotAvailableField_Game.set(12); // ここではゲームのオープンが出来ません。
            MREnterResult_NotAvailableField_PersonalShop.set(13); // 個人商店は重要村にある自由市場のみでオープン出来ます。
            MREnterResult_NotAvailableField_EntrustedShop.set(14); // フリーマーケットの2階以上に位置したルーム(7番~22番)でのみ使用可能です。
            MREnterResult_OnBlockedList.set(15); // 当該商店に入場できません。
            MREnterResult_IsManaging.set(16); // 商店の主人が物品整理中でございます。
            MGEnterResult_Tournament.set(17); // トーナメントのルームには入場できません。
            MGEnterResult_AlreadyPlaying.set(18); // 他の交換は不可能な状態です。
            MGEnterResult_NotEnoughMoney.set(19); // メルの不足でエントリーできません。
            MGEnterResult_InvalidPassword.set(20); // パスワードが間違いました。
            return;
        }
    }
}
