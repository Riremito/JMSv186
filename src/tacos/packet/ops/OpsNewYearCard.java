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
public enum OpsNewYearCard implements IPacketOps {

    NewYearCardReq_Send(0),
    NewYearCardReq_Receive(1),
    NewYearCardReq_Delete(2),
    NewYearCardReq_GetUnreceivedList(3),
    NewYearCardRes_Send_Done(4),
    NewYearCardRes_Send_Failed(5),
    NewYearCardRes_Receive_Done(6),
    NewYearCardRes_Receive_Failed(7),
    NewYearCardRes_Delete_Done(8),
    NewYearCardRes_Delete_Failed(9),
    NewYearCardRes_GetUnreceivedList_Done(10),
    NewYearCardRes_GetUnreceivedList_Failed(11),
    NewYearCardRes_NotiArrived(12),
    NewYearCardRes_BroadCast_AddCardInfo(13),
    NewYearCardRes_BroadCast_RemoveCardInfo(14),
    NewYearCardFailReason_CannotSendToSelf(15),
    NewYearCardFailReason_NoFreeSlot(16),
    NewYearCardFailReason_NotANewYearCard(17),
    NewYearCardFailReason_IncoherentItem(18),
    NewYearCardFailReason_TargetNotExist(19),
    NewYearCardFailReason_IncoherentData(20),
    NewYearCardFailReason_DBError(21),
    NewYearCardFailReason_UnknownError(22),
    UNKNOWN;

    private int value;

    OpsNewYearCard(int val) {
        this.value = val;
    }

    OpsNewYearCard() {
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
