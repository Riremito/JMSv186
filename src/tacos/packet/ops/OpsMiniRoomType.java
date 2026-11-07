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
public enum OpsMiniRoomType implements IPacketOps {
    MR_OmokRoom(1),
    MR_MemoryGameRoom(2),
    MR_TradingRoom(3),
    MR_PersonalShop(4),
    MR_EntrustedShop(5),
    MR_CashTradingRoom(6),
    MR_TypeNo(7),
    UNKNOWN;

    private int value;

    private OpsMiniRoomType(int value) {
        this.value = value;
    }

    private OpsMiniRoomType() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int value) {
        this.value = value;
    }

    public static OpsMiniRoomType find(int value) {
        for (OpsMiniRoomType ops : values()) {
            if (ops.get() == value) {
                if (value != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }
}
