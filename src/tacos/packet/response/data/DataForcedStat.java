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
package tacos.packet.response.data;

import tacos.client.TacosCharacter;
import tacos.client.TacosForcedStat;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsForcedStat;

/**
 *
 * @author Riremito
 */
public class DataForcedStat {

    // ForcedStat::Decode
    public static byte[] Encode(TacosCharacter chr) {
        ServerPacket data = new ServerPacket();

        TacosForcedStat fs = chr.getForcedStat();
        int mask = fs.getMask();

        data.Encode4(mask);

        if ((mask & OpsForcedStat.STR.get()) != 0) {
            data.Encode2(fs.getSTR());
        }
        if ((mask & OpsForcedStat.DEX.get()) != 0) {
            data.Encode2(fs.getDEX());
        }
        if ((mask & OpsForcedStat.INT.get()) != 0) {
            data.Encode2(fs.getINT());
        }
        if ((mask & OpsForcedStat.LUK.get()) != 0) {
            data.Encode2(fs.getLUK());
        }
        if ((mask & OpsForcedStat.PAD.get()) != 0) {
            data.Encode2(fs.getPAD());
        }
        if ((mask & OpsForcedStat.PDD.get()) != 0) {
            data.Encode2(fs.getPDD());
        }
        if ((mask & OpsForcedStat.MAD.get()) != 0) {
            data.Encode2(fs.getMAD());
        }
        if ((mask & OpsForcedStat.MDD.get()) != 0) {
            data.Encode2(fs.getMDD());
        }
        if ((mask & OpsForcedStat.ACC.get()) != 0) {
            data.Encode2(fs.getACC());
        }
        if ((mask & OpsForcedStat.EVA.get()) != 0) {
            data.Encode2(fs.getEVA());
        }
        if ((mask & OpsForcedStat.SPEED.get()) != 0) {
            data.Encode1(fs.getSpeed());
        }
        if ((mask & OpsForcedStat.JUMP.get()) != 0) {
            data.Encode1(fs.getJump());
        }
        if ((mask & OpsForcedStat.SPEEDMAX.get()) != 0) {
            data.Encode1(fs.getSpeedMax());
        }

        return data.get().getBytes();
    }

}
