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
public enum Ops_Whisper implements IPacketOps {
    WP_Location(1), // 0x1
    WP_Whisper(1 << 1), // 0x2
    WP_Request(1 << 2), // 0x4, req_res
    WP_Result(1 << 3), // 0x8, req_res
    WP_Receive(1 << 4), // 0x10, req_res
    WP_Blocked(1 << 5), // 0x20
    WP_Location_F(1 << 6), // 0x40
    WP_Manager(1 << 7), // 0x80
    UNKNOWN(0);

    private int value;

    Ops_Whisper(int val) {
        this.value = val;
    }

    Ops_Whisper() {
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

    public static Ops_Whisper find(int val) {
        for (final Ops_Whisper ops : Ops_Whisper.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public boolean check(int flag) {
        return (flag & get()) != 0;
    }

    public static void init() {

    }
}
