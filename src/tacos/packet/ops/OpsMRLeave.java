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
// MRP_Leave reason
public enum OpsMRLeave implements IPacketOps {
    MRLeave_UserRequest(0),
    MRLeave_WrongPosition(1),
    MRLeave_Closed(2),
    MRLeave_HostOut(3),
    MRLeave_Booked(4),
    MRLeave_Kicked(5), // 強制退場されました。
    MRLeave_OpenTimeOver(6), // 制限時間が経過し、商店を開く事ができませんでした。
    TRLeave_TradeDone(7),
    TRLeave_TradeFail(8),
    TRLeave_TradeFail_OnlyItem(9),
    TRLeave_TradeFail_Expired(10),
    TRLeave_TradeFail_Denied(11),
    TRLeave_FieldError(12),
    TRLeave_ItemCRCFailed(13),
    PSLeave_NoMoreItem(14), // 品物は売れ切れです。
    PSLeave_KickedTimeOver(15),
    ESLeave_Open(16),
    ESLeave_StartManage(17), // 商店の主人が物品整理中でございます。
    ESLeave_ClosedTimeOver(18),
    ESLeave_EndManage(19),
    ESLeave_DestoryByAdmin(20), // closes the dialog without a notice
    MGLeave_UserRequest(21),
    UNKNOWN;

    private int value;

    private OpsMRLeave(int value) {
        this.value = value;
    }

    private OpsMRLeave() {
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
        for (OpsMRLeave ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        // JMS146, JMS147, JMS164 : CTradingRoomDlg::OnLeave, CPersonalShopDlg::OnLeave, CEntrustedShopDlg::OnLeave
        // no OpenTimeOver, TradeFail_Expired, TradeFail_Denied and ItemCRCFailed
        if (Config.LessOrEqual(Region.JMS, 164)) {
            clear();
            MRLeave_UserRequest.set(0);
            MRLeave_WrongPosition.set(1);
            MRLeave_Closed.set(2);
            MRLeave_HostOut.set(3);
            MRLeave_Booked.set(4);
            MRLeave_Kicked.set(5);
            TRLeave_TradeDone.set(6);
            TRLeave_TradeFail.set(7);
            TRLeave_TradeFail_OnlyItem.set(8);
            TRLeave_FieldError.set(9);
            PSLeave_NoMoreItem.set(10);
            PSLeave_KickedTimeOver.set(11);
            ESLeave_Open.set(12);
            ESLeave_StartManage.set(13);
            ESLeave_ClosedTimeOver.set(14);
            ESLeave_EndManage.set(15);
            ESLeave_DestoryByAdmin.set(16);
            MGLeave_UserRequest.set(17); // not checked
            return;
        }
        // JMS165 : no TradeFail_Expired, TradeFail_Denied and ItemCRCFailed
        if (Config.LessOrEqual(Region.JMS, 165)) {
            clear();
            MRLeave_UserRequest.set(0);
            MRLeave_WrongPosition.set(1);
            MRLeave_Closed.set(2);
            MRLeave_HostOut.set(3);
            MRLeave_Booked.set(4);
            MRLeave_Kicked.set(5);
            MRLeave_OpenTimeOver.set(6);
            TRLeave_TradeDone.set(7);
            TRLeave_TradeFail.set(8);
            TRLeave_TradeFail_OnlyItem.set(9);
            TRLeave_FieldError.set(10);
            PSLeave_NoMoreItem.set(11);
            PSLeave_KickedTimeOver.set(12);
            ESLeave_Open.set(13);
            ESLeave_StartManage.set(14);
            ESLeave_ClosedTimeOver.set(15);
            ESLeave_EndManage.set(16);
            ESLeave_DestoryByAdmin.set(17);
            MGLeave_UserRequest.set(18); // not checked
            return;
        }
    }
}
