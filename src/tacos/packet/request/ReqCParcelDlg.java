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
package tacos.packet.request;

import tacos.client.TacosCharacter;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsParcel;
import tacos.packet.response.ResCParcelDlg;

/**
 *
 * @author Riremito
 */
public class ReqCParcelDlg {

    // 宅配
    public static boolean Accept(TacosCharacter chr, ClientPacket cp) {
        // 処理内容
        byte action = cp.Decode1();
        switch (OpsParcel.find(action)) {
            // 配送
            case ParcelReq_Send: {
                // parcels are not stored yet, the client waits for a result with the send button disabled.
                chr.SendPacket(ResCParcelDlg.Parcel_Failed());
                return true;
            }
            // 閉じる
            case ParcelReq_Close: {
                // 厳密に宅配可能状態か判断する場合は宅配UIを開いた時に何らかのフラグをサーバー側で保持して閉じるパケットが送信されたフラグをオフにする必要がある
                return true;
            }
            default:
                break;
        }
        return false;
    }
}
