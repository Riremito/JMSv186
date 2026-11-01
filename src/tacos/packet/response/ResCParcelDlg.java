/*
 * Copyright (C) 2024 Riremito
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
package tacos.packet.response;

import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsParcel;

/**
 *
 * @author Riremito
 */
public class ResCParcelDlg {

    // CParcelDlg::OnPacket, 宅配
    public static ServerPacket Parcel_Open(boolean isQuick, boolean isNPC) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Parcel);
        // 0x3B or 0x40
        if (isQuick) {
            // 速達のUI
            sp.Encode1(OpsParcel.ParcelRes_OpenQuickSend.get());
            return sp;
        }
        // 通常のUI
        sp.Encode1(OpsParcel.ParcelRes_Loaded.get());
        // NPC会話 or 速達の通知から開いたかの判定
        sp.Encode1((byte) (isNPC ? 0 : 1));
        sp.Encode1((byte) 0);
        sp.Encode1((byte) 0);
        return sp;
    }

    // CParcelDlg::OnPacket
    public static ServerPacket Parcel_Send() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Parcel);
        sp.Encode1(OpsParcel.ParcelRes_ConfirmSend_Success.get());
        return sp;
    }

    // CParcelDlg::OnPacket, CParcelDlg::NoticeResult
    public static ServerPacket Parcel_Failed() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_Parcel);
        sp.Encode1(OpsParcel.ParcelRes_ConfirmSend_Failure.get());
        return sp;
    }
}
