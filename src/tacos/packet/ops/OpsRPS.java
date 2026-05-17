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
public enum OpsRPS implements IPacketOps {
    RPSReq_StartGame(0),
    RPSReq_UserSelection(1),
    RPSReq_TimeOver(2),
    RPSReq_Continue(3),
    RPSReq_Quit(4),
    RPSReq_Retry(5),
    RPSRes_NotEnoughMoney(6),
    RPSRes_NoEmptySlotForReward(7),
    RPSRes_Open(8),
    RPSRes_StartGame(9),
    RPSRes_TimeOver(10),
    RPSRes_NpcSelection(11),
    RPSRes_Coninue(12),
    RPSRes_Quit(13),
    RPSRes_Retry(14),
    UNKNOWN;

    private int value;

    OpsRPS(int val) {
        this.value = val;
    }

    OpsRPS() {
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

    public static OpsRPS find(int val) {
        for (OpsRPS ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
