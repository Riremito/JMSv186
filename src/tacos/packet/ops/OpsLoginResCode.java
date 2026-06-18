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

import tacos.config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsLoginResCode implements IPacketOps {
    LoginResCode_ProcFail(-1),
    LoginResCode_Success(0),
    LoginResCode_TempBlocked(1),
    LoginResCode_Blocked(2),
    LoginResCode_Abandoned(3),
    LoginResCode_IncorrectPassword(4),
    LoginResCode_NotRegistered(5),
    LoginResCode_DBFail(6),
    LoginResCode_AlreadyConnected(7),
    LoginResCode_NotConnectableWorld(8),
    LoginResCode_Unknown(9),
    LoginResCode_Timeout(10),
    LoginResCode_NotAdult(11),
    LoginResCode_AuthFail(12),
    LoginResCode_ImpossibleIP(13),
    LoginResCode_NotAuthorizedNexonID(14),
    LoginResCode_NoNexonID(15),
    LoginResCode_NotAuthorized(16),
    LoginResCode_InvalidRegionInfo(17),
    LoginResCode_InvalidBirthDate(18),
    LoginResCode_PassportSuspended(19),
    LoginResCode_IncorrectSSN2(20),
    LoginResCode_WebAuthNeeded(21),
    LoginResCode_DeleteCharacterFailedOnGuildMaster(22),
    LoginResCode_NotagreedEULA(23),
    LoginResCode_DeleteCharacterFailedEngaged(24),
    LoginResCode_IncorrectSPW(20),
    LoginResCode_SamePasswordAndSPW(22),
    LoginResCode_SamePincodeAndSPW(23),
    LoginResCode_RegisterLimitedIP(25),
    LoginResCode_RequestedCharacterTransfer(26),
    LoginResCode_CashUserCannotUseSimpleClient(27),
    LoginResCode_DeleteCharacterFailedOnFamily(29),
    LoginResCode_InvalidCharacterName(30),
    LoginResCode_IncorrectSSN(31),
    LoginResCode_SSNConfirmFailed(32),
    LoginResCode_SSNNotConfirmed(33),
    LoginResCode_WorldTooBusy(34),
    LoginResCode_OTPReissuing(35),
    LoginResCode_OTPInfoNotExist(36),
    UNKNOWN;

    private int value;

    OpsLoginResCode(int val) {
        this.value = val;
    }

    OpsLoginResCode() {
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

    public static OpsLoginResCode find(int val) {
        for (OpsLoginResCode ops : values()) {
            if (ops.get() == val) {
                if (val != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsLoginResCode ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        if (Version.PostBB()) {
            return;
        }
    }
}
