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
package packet.ops;

import config.Region;
import config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsTrunk implements IPacketOps {
    TrunkReq_Load,
    TrunkReq_Save,
    TrunkReq_Close,
    TrunkReq_CheckSSN2,
    TrunkReq_GetItem,
    TrunkReq_PutItem,
    TrunkReq_SortItem,
    TrunkReq_Money,
    TrunkReq_CloseDialog,
    TrunkRes_GetSuccess,
    TrunkRes_GetUnknown,
    TrunkRes_GetNoMoney,
    TrunkRes_GetHavingOnlyItem, // lol
    TrunkRes_PutSuccess,
    TrunkRes_PutIncorrectRequest,
    TrunkRes_SortItem,
    TrunkRes_PutNoMoney,
    TrunkRes_PutNoSpace,
    TrunkRes_PutUnknown,
    TrunkRes_MoneySuccess,
    TrunkRes_MoneyUnknown,
    TrunkRes_TrunkCheckSSN2,
    TrunkRes_OpenTrunkDlg,
    TrunkRes_TradeBlocked,
    TrunkRes_ServerMsg,
    UNKNOWN(-1);

    private int value;

    OpsTrunk(int val) {
        this.value = val;
    }

    OpsTrunk() {
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

    public static OpsTrunk find(int val) {
        for (final OpsTrunk ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            TrunkReq_Load.set(0);
            TrunkReq_Save.set(1);
            TrunkReq_Close.set(2);
            TrunkReq_GetItem.set(3);
            TrunkReq_PutItem.set(4);
            TrunkReq_SortItem.set(5);
            TrunkReq_Money.set(6);
            TrunkReq_CloseDialog.set(7);
            TrunkRes_GetSuccess.set(8);
            TrunkRes_GetUnknown.set(9);
            TrunkRes_GetNoMoney.set(10);
            TrunkRes_GetHavingOnlyItem.set(11);
            TrunkRes_PutSuccess.set(12);
            TrunkRes_PutIncorrectRequest.set(13);
            TrunkRes_SortItem.set(14);
            TrunkRes_PutNoMoney.set(15);
            TrunkRes_PutNoSpace.set(16);
            TrunkRes_PutUnknown.set(17);
            TrunkRes_MoneySuccess.set(18);
            TrunkRes_MoneyUnknown.set(19);
            TrunkRes_OpenTrunkDlg.set(21);
            return;
        }
        if (Version.LessOrEqual(Region.JMS, 131)) {
            TrunkReq_Load.set(0);
            TrunkReq_Save.set(1);
            TrunkReq_Close.set(2);
            TrunkReq_GetItem.set(3);
            TrunkReq_PutItem.set(4);
            TrunkReq_Money.set(5);
            TrunkReq_CloseDialog.set(6);
            TrunkRes_GetSuccess.set(7);
            TrunkRes_GetUnknown.set(8);
            TrunkRes_GetNoMoney.set(9);
            TrunkRes_PutSuccess.set(10);
            TrunkRes_PutIncorrectRequest.set(11);
            TrunkRes_PutNoMoney.set(12);
            TrunkRes_PutNoSpace.set(13);
            TrunkRes_PutUnknown.set(14);
            TrunkRes_MoneySuccess.set(15);
            TrunkRes_MoneyUnknown.set(16);
            TrunkRes_OpenTrunkDlg.set(18);
            return;
        }

        // JMS186
        TrunkReq_Load.set(0);
        TrunkReq_Save.set(1);
        TrunkReq_Close.set(2);
        TrunkReq_GetItem.set(3);
        TrunkReq_PutItem.set(4);
        TrunkReq_SortItem.set(5);
        TrunkReq_Money.set(6);
        TrunkReq_CloseDialog.set(7);
        TrunkRes_GetSuccess.set(8);
        TrunkRes_GetUnknown.set(9);
        TrunkRes_GetNoMoney.set(10);
        TrunkRes_GetHavingOnlyItem.set(11);
        TrunkRes_PutSuccess.set(12);
        TrunkRes_PutIncorrectRequest.set(13);
        TrunkRes_SortItem.set(14);
        TrunkRes_PutNoMoney.set(15);
        TrunkRes_PutNoSpace.set(16);
        TrunkRes_PutUnknown.set(17);
        TrunkRes_MoneySuccess.set(18);
        TrunkRes_MoneyUnknown.set(19);
        TrunkRes_OpenTrunkDlg.set(21);
        return;
    }

}
