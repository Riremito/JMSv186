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

/**
 *
 * @author Riremito
 */
public enum OpsMiniRoomType implements IPacketOps {
    MR_OmokRoom(1),
    MR_MemoryGameRoom(2),
    MR_TradingRoom(3),
    MR_PersonalShop(4),
    MR_EntrustedShop(5),
    MR_CashTradingRoom(6),
    MR_TypeNo(7),
    UNKNOWN(-1);

    private int value;

    OpsMiniRoomType(int val) {
        this.value = val;
    }

    OpsMiniRoomType() {
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

    public static OpsMiniRoomType find(int val) {
        for (final OpsMiniRoomType ops : OpsMiniRoomType.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
    }
}
