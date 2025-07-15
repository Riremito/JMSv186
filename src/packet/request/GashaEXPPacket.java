/*
 * Copyright (C) 2023 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package packet.request;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.Region;
import config.ServerConfig;
import config.Version;
import data.client.DC_Exp;
import packet.ClientPacket;
import packet.response.wrapper.ResWrapper;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
// 兵法書
public class GashaEXPPacket {

    public static boolean OnPacket(ClientPacket p, ClientPacket.Header header, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        switch (header) {
            case CP_UserExpUpItemUseRequest: {
                int time_stamp = p.Decode4();
                short nPOS = p.Decode2();
                int nItemID = p.Decode4();
                chr.updateTick(time_stamp);
                UseItem(chr, nPOS, nItemID);
                return true;
            }
            case CP_UserTempExpUseRequest: {
                int time_stamp = p.Decode4();
                chr.updateTick(time_stamp);
                UseTempExp(chr);
                return true;
            }
        }

        return false;
    }

    private static boolean UseItem(MapleCharacter chr, short nPOS, int nItemID) {
        IItem item = chr.getInventory(MapleInventoryType.USE).getItem(nPOS);
        if (item == null || chr.getGashaEXP() > 0 || item.getItemId() != nItemID || (nItemID / 10000) != 237) {
            // LoadData.IsValidItem
            chr.SendPacket(ResWrapper.StatChanged(chr));
            return false;
        }

        int exp_gasha = MapleItemInformationProvider.getInstance().getItemEffect(item.getItemId()).getExp();
        chr.setGashaEXP(exp_gasha);

        UseTempExp(chr);

        // 兵法書実装前
        if (Version.LessOrEqual(Region.JMS, 131)) {
            while (UseTempExp(chr)) {
                // loop
            }
        }

        MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, nItemID, 1, true, false);
        return true;
    }

    private static boolean UseTempExp(MapleCharacter chr) {
        int exp_table = DC_Exp.getExpNeededForLevel(chr.getLevel());
        int exp_current = chr.getExp();
        int exp_temp = chr.getGashaEXP();

        if (exp_temp <= 0) {
            chr.SendPacket(ResWrapper.StatChanged(chr));
            return false;
        }

        if (exp_table - exp_current - exp_temp > 0) {
            chr.setGashaEXP(0);
            chr.gainExp(exp_temp, true, true, false);
        } else {
            chr.setGashaEXP(exp_temp - (exp_table - exp_current));
            chr.gainExp(exp_table - exp_current, true, true, false);
        }

        chr.UpdateStat(true);
        return true;
    }
}
