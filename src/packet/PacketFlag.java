/*
 * Copyright (C) 2025 Riremito
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
package packet;

import packet.ops.OpsBroadcastMsg;
import packet.ops.OpsChangeStat;
import packet.ops.OpsDBCHAR;
import packet.ops.OpsMessage;
import packet.ops.OpsMovePathAttr;
import packet.ops.OpsNewCharacter;
import packet.ops.OpsQuest;
import packet.ops.OpsScriptMan;
import packet.ops.OpsSecondaryStat;
import packet.ops.OpsTransferChannel;
import packet.ops.OpsTransferField;
import packet.ops.OpsUserEffect;
import packet.request.ReqCNpcPool;
import packet.request.ReqCTrunkDlg;

/**
 *
 * @author Riremito
 */
public class PacketFlag {

    public static void Update() {
        OpsDBCHAR.init();
        ReqCTrunkDlg.init();
        ReqCNpcPool.init();
        OpsChangeStat.init();
        OpsSecondaryStat.init();
        OpsMessage.init();
        OpsNewCharacter.init();
        OpsScriptMan.init();
        OpsUserEffect.init();
        OpsQuest.init();
        OpsBroadcastMsg.init();
        OpsMovePathAttr.init();
        OpsTransferField.init();
        OpsTransferChannel.init();
    }

}
