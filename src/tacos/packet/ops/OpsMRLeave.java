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

/**
 *
 * @author Riremito
 */
// MRP_Leave reason
public enum OpsMRLeave implements IPacketOps {
    // GMS v95 (JMS v186 has the same values)
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

    OpsMRLeave(int val) {
        this.value = val;
    }

    OpsMRLeave() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int val) {
        this.value = val;
    }

    public static OpsMRLeave find(int val) {
        for (OpsMRLeave ops : OpsMRLeave.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
    }
}
