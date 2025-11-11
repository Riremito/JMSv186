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

import tacos.config.Region;
import tacos.config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsMREnterResult implements IPacketOps {
    MREnterResult_Success,
    MREnterResult_NoRoom,
    MREnterResult_Full,
    MREnterResult_Busy,
    MREnterResult_Dead,
    MREnterResult_Event,
    MREnterResult_PermissionDenied,
    MREnterResult_NoTrading,
    MREnterResult_Etc,
    MREnterResult_OnlyInSameField,
    MREnterResult_NearPortal,
    MREnterResult_CreateCountOver,
    MREnterResult_CreateIPCountOver,
    MREnterResult_ExistMiniRoom,
    MREnterResult_NotAvailableField_Game,
    MREnterResult_NotAvailableField_PersonalShop,
    MREnterResult_NotAvailableField_EntrustedShop,
    MREnterResult_OnBlockedList,
    MREnterResult_IsManaging,
    MGEnterResult_Tournament,
    MGEnterResult_AlreadyPlaying,
    MGEnterResult_NotEnoughMoney,
    MGEnterResult_InvalidPassword,
    MREnterResult_NotAvailableField_ShopScanner,
    MREnterResult_Expired,
    MREnterResult_TooShortTimeInterval,
    UNKNOWN(-1);

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
        for (final OpsMREnterResult ops : OpsMREnterResult.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (Version.LessOrEqual(Region.JMS, 147)) {
            return;
        }
    }

}
