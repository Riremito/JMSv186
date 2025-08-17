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
public enum OpsEntrustedShop implements IPacketOps {
    EntrustedShopReq_CheckOpenPossible,
    EntrustedShopReq_Create,
    EntrustedShopReq_Save,
    EntrustedShopReq_CloseProcess,
    EntrustedShopReq_FindShopByEmployerName,
    EntrustedShopReq_CheckIfClosed,
    EntrustedShopReq_GetPos,
    EntrustedShopRes_OpenPossible,
    EntrustedShopRes_OpenImpossible_Using,
    EntrustedShopRes_OpenImpossible_Stored,
    EntrustedShopRes_OpenImpossible_AnotherCharacter,
    EntrustedShopRes_OpenImpossible_Block,
    EntrustedShopRes_Create_Failed,
    EntrustedShopReq_SetMiniMapColor,
    EntrustedShopReq_RenameResult, // ?_? res?
    EntrustedShopRes_ItemExistInStoreBank,
    EntrustedShopRes_GetPosResult,
    EntrustedShopRes_Enter,
    EntrustedShopRes_ServerMsg,
    StoreBankReq_Load,
    StoreBankReq_Remove,
    StoreBankReq_CheckSSN2,
    StoreBankRes_Load_Done,
    StoreBankRes_Load_Failed,
    StoreBankRes_Block,
    StoreBankRes_Remove_Done,
    StoreBankReq_CalculateFee,
    StoreBankReq_GetAll,
    StoreBankReq_Exit,
    StoreBankRes_CalculateFee,
    StoreBankRes_GetAll_Done,
    StoreBankRes_GetAll_OverPrice,
    StoreBankRes_GetAll_OnlyItem,
    StoreBankRes_GetAll_NoFee,
    StoreBankRes_GetAll_NoSlot,
    StoreBankRes_OpenStoreBankDlg,
    StoreBankRes_StoreBankCalculateFee,
    StoreBankRes_StoreBankLoadFailed,
    StoreBankRes_StoreBankBlock,
    UNKNOWN(-1);

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
        for (final OpsEntrustedShop ops : OpsEntrustedShop.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (Version.LessOrEqual(Region.JMS, 147)) {
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
