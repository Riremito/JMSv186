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
public enum OpsESWithdrawAll implements IPacketOps {
    // GMS v95 and JMS v186, CEntrustedShopDlg::OnWithdrawAllResult
    ESWithdrawAll_Done(0), // アイテムとメルを全部取り出しました。
    ESWithdrawAll_OverPrice(1), // 商店にあるお金が多すぎでメルとアイテムを取り戻せませんでした。
    ESWithdrawAll_OnlyItem(2), // メルを取り戻しました。1個のみ持てないアイテムがあってアイテムは取り戻せませんでした。
    ESWithdrawAll_NoSlot(3), // メルを取り戻しました。インベントリに空きがなくてアイテムは取り戻せませんでした。
    ESWithdrawAll_Unknown(4), // メルを取り戻しました。原因不明の理由でアイテムは取り戻せませんでした。
    ESWithdrawAll_NoMessage(5), // closes the dialog without a notice
    UNKNOWN;

    private int value;

    OpsESWithdrawAll(int val) {
        this.value = val;
    }

    OpsESWithdrawAll() {
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

    public static OpsESWithdrawAll find(int val) {
        for (OpsESWithdrawAll ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
