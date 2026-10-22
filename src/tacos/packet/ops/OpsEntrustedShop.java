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
public enum OpsEntrustedShop implements IPacketOps {
    // GMS v95
    EntrustedShopReq_CheckOpenPossible(0),
    EntrustedShopReq_Create(1),
    EntrustedShopReq_Save(2),
    EntrustedShopReq_CloseProcess(3),
    EntrustedShopReq_FindShopByEmployerName(4),
    EntrustedShopReq_CheckIfClosed(5),
    EntrustedShopReq_GetPos(6),
    EntrustedShopRes_OpenPossible(7),
    EntrustedShopRes_OpenImpossible_Using(8),
    EntrustedShopRes_OpenImpossible_Stored(9),
    EntrustedShopRes_OpenImpossible_AnotherCharacter(10),
    EntrustedShopRes_OpenImpossible_Block(11),
    EntrustedShopRes_Create_Failed(12),
    EntrustedShopReq_SetMiniMapColor(13),
    EntrustedShopReq_RenameResult(14), // ?_? res?
    EntrustedShopRes_ItemExistInStoreBank(15),
    EntrustedShopRes_GetPosResult(16),
    EntrustedShopRes_Enter(17),
    EntrustedShopRes_ServerMsg(18),
    StoreBankReq_Load(19),
    StoreBankReq_Remove(20),
    StoreBankReq_CheckSSN2(21),
    StoreBankRes_Load_Done(22),
    StoreBankRes_Load_Failed(23),
    StoreBankRes_Block(24),
    StoreBankRes_Remove_Done(25),
    StoreBankReq_CalculateFee(26),
    StoreBankReq_GetAll(27),
    StoreBankReq_Exit(28),
    StoreBankRes_CalculateFee(29),
    StoreBankRes_GetAll_Done(30),
    StoreBankRes_GetAll_OverPrice(31),
    StoreBankRes_GetAll_OnlyItem(32),
    StoreBankRes_GetAll_NoFee(33),
    StoreBankRes_GetAll_NoSlot(34),
    StoreBankRes_OpenStoreBankDlg(35),
    StoreBankRes_StoreBankCalculateFee(36),
    StoreBankRes_StoreBankLoadFailed(37),
    StoreBankRes_StoreBankBlock(38),
    UNKNOWN;

    private int value;

    OpsEntrustedShop(int val) {
        this.value = val;
    }

    OpsEntrustedShop() {
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

    public static OpsEntrustedShop find(int val) {
        for (OpsEntrustedShop ops : OpsEntrustedShop.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsEntrustedShop ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        if (Config.LessOrEqual(Region.JMS, 147)) {
            clear();
            EntrustedShopReq_CheckOpenPossible.set(0);
            EntrustedShopRes_OpenPossible.set(7);
            EntrustedShopRes_OpenImpossible_Using.set(8);
            EntrustedShopRes_OpenImpossible_Stored.set(9);
            EntrustedShopRes_OpenImpossible_AnotherCharacter.set(10);
            EntrustedShopRes_OpenImpossible_Block.set(11);
            EntrustedShopRes_Create_Failed.set(12);
            EntrustedShopReq_SetMiniMapColor.set(13);
            EntrustedShopReq_RenameResult.set(14);
            EntrustedShopRes_ItemExistInStoreBank.set(15);
            EntrustedShopRes_GetPosResult.set(16);
            EntrustedShopRes_Enter.set(17);
            return;
        }
    }

}
