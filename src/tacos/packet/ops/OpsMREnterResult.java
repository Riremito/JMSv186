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
    // GMS v95
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

    OpsMREnterResult(int val) {
        this.value = val;
    }

    OpsMREnterResult() {
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

    public static OpsMREnterResult find(int val) {
        for (OpsMREnterResult ops : OpsMREnterResult.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (Config.LessOrEqual(Region.JMS, 147)) {
            return;
        }
    }

}
