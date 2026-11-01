/*
 * Copyright (C) 2026 Riremito
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
public enum OpsPSBuy implements IPacketOps {
    // GMS v95, CPersonalShopDlg::OnBuyResult
    PSBuy_Success(0),
    PSBuy_NoStock(1),
    PSBuy_NoMoney(2),
    PSBuy_OverPrice(3),
    PSBuy_HostTooMuchMoney(4),
    PSBuy_NoSlot(5),
    PSBuy_OnlyItem(6),
    PSBuy_GenderMismatch(7),
    PSBuy_Under7Age(8),
    PSBuy_ItemExpired(9),
    PSBuy_Denied(10),
    PSBuy_DeniedUser(11),
    PSBuy_ItemCRCFailed(12),
    PSBuy_Unknown(13),
    PSBuy_MoneyLimit(14),
    UNKNOWN;

    private int value;

    OpsPSBuy(int val) {
        this.value = val;
    }

    OpsPSBuy() {
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

    public static OpsPSBuy find(int val) {
        for (OpsPSBuy ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
