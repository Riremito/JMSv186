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
public enum OpsShopScanner implements IPacketOps {
    ShopScannerReq_AddList(0),
    ShopScannerReq_RemoveList(1),
    ShopScannerReq_RemoveAll(2),
    ShopScannerReq_Rename(3),
    ShopScannerReq_Search(4),
    ShopScannerReq_LoadHotList(5),
    ShopScannerRes_SearchResult(6),
    ShopScannerRes_LoadHotListResult(7),
    UNKNOWN(-1);

    private int value;

    OpsShopScanner(int val) {
        this.value = val;
    }

    OpsShopScanner() {
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

    public static OpsShopScanner find(int val) {
        for (final OpsShopScanner ops : OpsShopScanner.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
    }

}
