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
package tacos.packet.request;

import tacos.client.TacosCharacter;
import tacos.client.TacosStoreBank;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsEntrustedShop;
import tacos.packet.response.ResCStoreBankDlg;

/**
 *
 * @author Riremito
 */
public class ReqCStoreBankDlg {

    // CStoreBankDlg::SendCalculateFeeRequest, CStoreBankDlg::SendGetAllRequest, CStoreBankDlg::SetRet
    public static boolean OnPacket(TacosCharacter chr, ClientPacket cp) {
        byte req = cp.Decode1();

        switch (OpsEntrustedShop.find(req)) {
            case StoreBankReq_CalculateFee: {
                chr.SendPacket(ResCStoreBankDlg.StoreBankCalculateFee(TacosStoreBank.getPassingDay(chr), 0));
                return true;
            }
            case StoreBankReq_GetAll: {
                chr.SendPacket(ResCStoreBankDlg.StoreBankGetAllResult(TacosStoreBank.getAll(chr)));
                return true;
            }
            case StoreBankReq_Exit: {
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("ReqCStoreBankDlg : not coded " + req);
        return false;
    }
}
