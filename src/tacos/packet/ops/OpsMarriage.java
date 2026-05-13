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
public enum OpsMarriage implements IPacketOps {
    MarriageReq_Propose(0),
    MarriageReq_CancelPropose(1),
    MarriageReq_Accept(2),
    MarriageReq_BreakUp(3),
    MarriageReq_Reserve(4),
    MarriageReq_Invite(5),
    MarriageReq_Invitation(6),
    MarriageReq_Marry(7),
    MarriageReq_Divorce(8),
    MarriageReq_WishList(9),
    MarriageReq_LoadReservation(10),
    MarriageRes_Engaged(11),
    MarriageRes_Married(12),
    MarriageRes_BrokeUp(13),
    MarriageRes_Divorced(14),
    MarriageRes_ShowInvitation(15),
    MarriageRes_ReservationDone(16),
    MarriageRes_LoadReservationDone(17),
    MarriageRes_WrongName(18),
    MarriageRes_NotSameMap(19),
    MarriageRes_RequesterNoEmptySlot(20),
    MarriageRes_TargetNoEmptySlot(21),
    MarriageRes_WrongGender(22),
    MarriageRes_RequesterAlreadyEngaged(23),
    MarriageRes_TargetAlreadyEngaged(24),
    MarriageRes_RequesterAlreadyMarried(25),
    MarriageRes_TargetAlreadyMarried(26),
    MarriageRes_RequesterAlreadyInProcess(27),
    MarriageRes_TargetAlreadyInProcess(28),
    MarriageRes_RequesterCanceled(29),
    MarriageRes_TargetRefused(30),
    MarriageRes_ReservationCanceled(31),
    MarriageRes_CantBreakupAfterReserve(32),
    MarriageRes_NotEnoughMoney(33),
    MarriageRes_WrongMarriageNo(34),
    MarriageRes_ReservationFail(35),
    MarriageRes_Unknown(36),
    UNKNOWN;

    private int value;

    OpsMarriage(int val) {
        this.value = val;
    }

    OpsMarriage() {
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

    public static OpsMarriage find(int val) {
        for (OpsMarriage ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
