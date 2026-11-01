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
public enum OpsAntiMacro implements IPacketOps {

    AntiMacroReq_Fail_InvalidCharacterName(0),
    AntiMacroReq_Fail_NotAttack(1),
    AntiMacroReq_Fail_NotAvailableTime(2),
    AntiMacroReq_Fail_SolvingQuestion(3),
    AntiMacroReq_Pended(4),
    AntiMacroReq_Success(5),
    AntiMacroRes(6),
    AntiMacroRes_Fail(7),
    AntiMacroRes_TargetFail(8),
    AntiMacroRes_Success(9),
    AntiMacroRes_TargetSuccess(10),
    AntiMacroRes_Reward(11),
    UNKNOWN;

    private int value;

    OpsAntiMacro(int val) {
        this.value = val;
    }

    OpsAntiMacro() {
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
