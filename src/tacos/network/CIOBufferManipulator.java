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
    public static void _En(byte[] Buffer) {
        int Ln = Buffer.length;

        for (int i = 0; i < 3; i++) {
            byte key = 0;
            for (int index = 0; index < Ln; index++) {
                int key_2 = Ln - index;
                key ^= (byte) (key_2 + __ROL1__(Buffer[index], 3));
                Buffer[index] = (byte) (0x47 - __ROR1__(key, key_2 % 8));
            }
            key = 0;
            for (int index = 0; index < Ln; index++) {
                int key_2 = Ln - index;
                key ^= (byte) key_2 + __ROL1__(Buffer[key_2 - 1], 4);
                Buffer[key_2 - 1] = __ROR1__((byte) (key ^ 0x13), 3);
            }
        }
    }

    // CIOBufferManipulator::_De
    public static byte[] _De(byte[] Buffer) {
        int Ln = Buffer.length;

        for (int i = 0; i < 3; i++) {
            byte data = 0;
            byte key = 0;
            byte key_next = 0;
            byte key_index = (byte) (Ln & 0xFF);
            for (int index = 0; index < Ln; index++) {
                data = Buffer[Ln - index - 1];
                data = __ROL1__(data, 3);
                data ^= 0x13;
                key_next = data;
                data ^= key;
                key = key_next;
                data -= key_index;
                data = __ROR1__(data, 4);
                Buffer[Ln - index - 1] = data;
                key_index--;
            }
            key = 0;
            key_next = 0;
            key_index = (byte) (Ln & 0xFF);
            for (int index = 0; index < Ln; index++) {
                data = Buffer[index];
                data -= 0x48;
                data = (byte) (~data & 0xFF);
                data = __ROL1__(data, key_index % 8);
                key_next = data;
                data ^= key;
                key = key_next;
                data -= key_index;
                data = __ROR1__(data, 3);
                Buffer[index] = data;
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
