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

import tacos.config.Config;
import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public enum OpsParcel implements IPacketOps {
    ParcelReq_OpenQuickReceive(0),
    ParcelReq_Load(1),
    ParcelReq_Send(2),
    ParcelReq_ConfirmSend(3),
    ParcelReq_Receive(4),
    ParcelReq_Discard(5),
    ParcelReq_Remove(6),
    ParcelReq_Close(7),
    ParcelRes_Loaded(8),
    ParcelRes_SendSuccess(9),
    ParcelRes_SendErr_NotEnoughMoney(10),
    ParcelRes_SendErr_IncorrectRequest(11),
    ParcelRes_SendErr_NoCharacter(12),
    ParcelRes_SendErr_SameAccount(13),
    ParcelRes_SendErr_NoSpaceToPeer(14),
    ParcelRes_SendErr_TargetUserCannotReceive(15),
    ParcelRes_SendErr_TargetHavingOnlyItem(16),
    ParcelRes_SendErr_LimitMoney(17),
    ParcelRes_ConfirmSend_Success(18),
    ParcelRes_ConfirmSend_Failure(19),
    ParcelRes_ReceiveSuccess(20),
    ParcelRes_ReceiveErr_NoEmptySlot(21),
    ParcelRes_ReceiveErr_HavingOnlyItem(22),
    ParcelRes_RemoveSuccess(23),
    ParcelRes_NewParcelSent(24),
    ParcelRes_ReceivedNewParcel(25),
    ParcelRes_OpenQuickSend(26),
    ParcelRes_DelayedNotify(27),
    ParcelRes_UnknownError(28),
    ParcelRes_Expired(29),
    ParcelRes_ServerMsg(30),
    // added, not in GMS95
    ParcelRes_CheckSSN2(-1),
    UNKNOWN;

    private int value;

    OpsParcel(int val) {
        this.value = val;
    }

    OpsParcel() {
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

    public static OpsParcel find(int val) {
        for (OpsParcel ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsParcel ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        // GMS95
        if (Region.GMS.check() || Region.BMS.check() || Config.LessOrEqual(Region.EMS, 70)) {
            return;
        }
        // JMS186, KMST330
        clear();
        ParcelReq_OpenQuickReceive.set(0);
        ParcelReq_Load.set(1);
        ParcelReq_Send.set(3);
        ParcelReq_ConfirmSend.set(4);
        ParcelReq_Receive.set(5);
        ParcelReq_Discard.set(6);
        ParcelReq_Remove.set(7);
        ParcelReq_Close.set(8);
        ParcelRes_CheckSSN2.set(9);
        ParcelRes_Loaded.set(10);
        ParcelRes_SendSuccess.set(11);
        ParcelRes_SendErr_NotEnoughMoney.set(12);
        ParcelRes_SendErr_IncorrectRequest.set(13);
        ParcelRes_SendErr_NoCharacter.set(14);
        ParcelRes_SendErr_SameAccount.set(15);
        ParcelRes_SendErr_NoSpaceToPeer.set(16);
        ParcelRes_SendErr_TargetUserCannotReceive.set(17);
        ParcelRes_SendErr_TargetHavingOnlyItem.set(18);
        ParcelRes_ConfirmSend_Success.set(19);
        ParcelRes_ConfirmSend_Failure.set(20);
        ParcelRes_ReceiveSuccess.set(21);
        ParcelRes_ReceiveErr_NoEmptySlot.set(22);
        ParcelRes_ReceiveErr_HavingOnlyItem.set(23);
        ParcelRes_RemoveSuccess.set(24);
        ParcelRes_NewParcelSent.set(25);
        ParcelRes_ReceivedNewParcel.set(26);
        ParcelRes_OpenQuickSend.set(27);
        ParcelRes_DelayedNotify.set(28);
        ParcelRes_UnknownError.set(29);
    }

}
