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
package tacos.network;

/**
 *
 * @author Riremito
 */
public class CIOBufferManipulator {

    // CIOBufferManipulator::_En
    public static byte[] _En(byte[] Buffer) {
        int Ln = Buffer.length;

        for (int i = 0; i < 3; i++) {
            byte key_next = 0;
            int key_index = Ln;
            for (int offset = 0; offset < Ln; offset++) {
                // S1-1
                key_next ^= (byte) (key_index + __ROL1__(Buffer[offset], 3));
                // S1-2
                Buffer[offset] = (byte) (0x47 - __ROR1__(key_next, key_index % 8));
                key_index--;
            }
            key_next = 0;
            key_index = Ln;
            for (int offset = Ln - 1; 0 <= offset; offset--) {
                // S2-1
                key_next ^= (byte) (key_index + __ROL1__(Buffer[offset], 4));
                // S2-2
                Buffer[offset] = __ROR1__((byte) (key_next ^ 0x13), 3);
                key_index--;
            }
        }

        return Buffer;
    }

    // CIOBufferManipulator::_De
    public static byte[] _De(byte[] Buffer) {
        int Ln = Buffer.length;

        for (int i = 0; i < 3; i++) {
            byte data = 0;
            byte key_next = 0;
            int key_index = Ln;
            for (int offset = Ln - 1; 0 <= offset; offset--) {
                // S2-2
                data = (byte) (__ROL1__(Buffer[offset], 3) ^ 0x13);
                // S2-1
                Buffer[offset] = __ROR1__((byte) ((byte) (data ^ key_next) - key_index), 4);
                key_next = data;
                key_index--;
            }
            key_next = 0;
            key_index = Ln;
            for (int offset = 0; offset < Ln; offset++) {
                // S1-2
                data = __ROL1__((byte) ((0x47 - Buffer[offset]) & 0xFF), key_index % 8);
                // S1-1
                Buffer[offset] = __ROR1__((byte) ((byte) (data ^ key_next) - key_index), 3);
                key_next = data;
                key_index--;
            }
        }

        return Buffer;
    }

    private static byte __ROL1__(byte val, int shift) {
        return (byte) ((val & 0xFF) << shift | (val & 0xFF) >>> (8 - shift));
    }

    private static byte __ROR1__(byte val, int shift) {
        return (byte) ((val & 0xFF) >>> shift | (val & 0xFF) << (8 - shift));
    }
}
