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
package packet.ops;

/**
 *
 * @author Riremito
 */
public class PacketOps {

    public static void initAll() {
        OpsNewCharacter.init();
        OpsDBCHAR.init();
        OpsChangeStat.init();
        OpsSecondaryStat.init();
        OpsTransferField.init();
        OpsTransferChannel.init();
        OpsBroadcastMsg.init();
        OpsMessage.init();
        OpsScriptMan.init();
        OpsShop.init();
        OpsTrunk.init();
        OpsUserEffect.init();
        OpsQuest.init();
        OpsMovePathAttr.init();
        OpsFuncKeyMapped.init();
        OpsContiMove.init();
        OpsMapTransfer.init();
    }

}
