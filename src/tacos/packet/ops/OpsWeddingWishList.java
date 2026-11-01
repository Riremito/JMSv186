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
public enum OpsWeddingWishList implements IPacketOps {

    WeddingWLReq_GiveLoad(0),
    WeddingWLReq_GiveSave(1),
    WeddingWLReq_GiveClose(2),
    WeddingWLReq_RecvLoad(3),
    WeddingWLReq_RecvSave(4),
    WeddingWLReq_RecvClose(5),
    WeddingWLReq_PutItem(6),
    WeddingWLReq_GetItem(7),
    WeddingWLReq_CloseDialog(8),
    WeddingWLRes_OpenGiveWishListDlg(9),
    WeddingWLRes_OpenRecvWishListDlg(10),
    WeddingWLRes_PutSuccess(11),
    WeddingWLRes_PutNoSpace(12),
    WeddingWLRes_PutIncorrectRequest(13),
    WeddingWLRes_PutUnknown(14),
    WeddingWLRes_GetSuccess(15),
    WeddingWLRes_GetUnknown(16),
    WeddingWLRes_GetHavingOnlyItem(17),
    UNKNOWN;

    private int value;

    OpsWeddingWishList(int val) {
        this.value = val;
    }

    OpsWeddingWishList() {
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
