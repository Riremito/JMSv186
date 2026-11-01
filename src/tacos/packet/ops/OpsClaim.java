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
public enum OpsClaim implements IPacketOps {

    ClaimRes_Success(1),
    ClaimRes_Success_Sender(2),
    ClaimRes_Success_Target(3),
    ClaimRes_Fail(64),
    ClaimRes_Fail_Unknown(65),
    ClaimRes_Fail_InvalidCharacter(66),
    ClaimRes_Fail_NotEnoughMoney(67),
    ClaimRes_Fail_NotConnected(68),
    ClaimRes_Fail_OverLimit(69),
    ClaimRes_Fail_OverWeeklyLimit(70),
    ClaimRes_Fail_UnAvailable(71),
    ClaimRes_Fail_Liar(72),
    UNKNOWN;

    private int value;

    private OpsClaim(int value) {
        this.value = value;
    }

    private OpsClaim() {
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
}
