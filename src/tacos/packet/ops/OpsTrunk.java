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
public enum OpsTrunk implements IPacketOps {
    // GMS v95
    TrunkReq_Load(0),
    TrunkReq_Save(1),
    TrunkReq_Close(2),
    TrunkReq_CheckSSN2(3),
    TrunkReq_GetItem(4),
    TrunkReq_PutItem(5),
    TrunkReq_SortItem(6),
    TrunkReq_Money(7),
    TrunkReq_CloseDialog(8),
    TrunkRes_GetSuccess(9),
    TrunkRes_GetUnknown(10),
    TrunkRes_GetNoMoney(11),
    TrunkRes_GetHavingOnlyItem(12), // TruncRes_GetHavingOnlyItem (typo in the client)
    TrunkRes_PutSuccess(13),
    TrunkRes_PutIncorrectRequest(14),
    TrunkRes_SortItem(15),
    TrunkRes_PutNoMoney(16),
    TrunkRes_PutNoSpace(17),
    TrunkRes_PutUnknown(18),
    TrunkRes_MoneySuccess(19),
    TrunkRes_MoneyUnknown(20),
    TrunkRes_TrunkCheckSSN2(21),
    TrunkRes_OpenTrunkDlg(22),
    TrunkRes_TradeBlocked(23),
    TrunkRes_ServerMsg(24),
    UNKNOWN;

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
        for (OpsTrunk ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    private static void reset() {
        for (OpsTrunk ops : OpsTrunk.values()) {
            ops.set(-1);
        }
    }

    public static void clear() {
        for (OpsTrunk ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        if (Config.LessOrEqual(Region.KMS, 43)) {
            clear();
            reset();
            TrunkRes_OpenTrunkDlg.set(19);
            return;
        }
        if (Config.GreaterOrEqual(Region.JMS, 302)) {
            clear();
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
        if (Config.LessOrEqual(Region.JMS, 131)) {
            clear();
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

        if (Config.LessOrEqual(Region.JMS, 147)) {
            clear();
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
            TrunkRes_PutSuccess.set(11);
            TrunkRes_PutIncorrectRequest.set(12);
            TrunkRes_SortItem.set(13);
            TrunkRes_PutNoMoney.set(14);
            TrunkRes_PutNoSpace.set(15);
            TrunkRes_PutUnknown.set(16);
            TrunkRes_MoneySuccess.set(17);
            TrunkRes_MoneyUnknown.set(18);
            // unk
            TrunkRes_OpenTrunkDlg.set(20);
            return;
        }

        if (Config.GreaterOrEqual(Region.CMS, 73)) {
            clear();
            TrunkReq_Close.set(3);
            TrunkReq_GetItem.set(4);
            TrunkReq_PutItem.set(5);
            TrunkReq_SortItem.set(6);
            TrunkReq_Money.set(7);
            TrunkReq_CloseDialog.set(8);
            TrunkRes_GetSuccess.set(9);
            TrunkRes_GetUnknown.set(10);
            TrunkRes_GetNoMoney.set(11);
            TrunkRes_GetHavingOnlyItem.set(12);
            TrunkRes_PutSuccess.set(13);
            TrunkRes_PutIncorrectRequest.set(14);
            TrunkRes_SortItem.set(15);
            TrunkRes_PutNoMoney.set(16);
            TrunkRes_PutNoSpace.set(17);
            TrunkRes_PutUnknown.set(18);
            TrunkRes_MoneySuccess.set(19);
            TrunkRes_MoneyUnknown.set(20);
            TrunkRes_OpenTrunkDlg.set(22);
            return;
        }        // JMS186 (Pre-BB)
        if (!Config.PostBB()) {
            clear();
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
            // unk
            TrunkRes_OpenTrunkDlg.set(21);
            return;
        }
        // GMS95
    }

}
