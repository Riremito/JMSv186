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
public enum OpsSueCharacter implements IPacketOps {

    SueCharacterRes_Success(0),
    SueCharacterRes_UnknownError(-1),
    SueCharacterRes_InvalidCharacterID(1),
    SueCharacterRes_AlreadyDoneToday(2),
    SueCharacterRes_Notify(3),
    UNKNOWN;

    private int value;

    OpsSueCharacter(int val) {
        this.value = val;
    }

    OpsSueCharacter() {
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
}
