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
package tacos.command;

import odin.client.MapleCharacter;
import odin.server.maps.MapleMap;
import odin.server.maps.SavedLocationType;
import tacos.packet.request.ReqCUser;

/**
 *
 * @author Riremito
 */
public class TacosCommandPlayer {

    public static boolean executeCommand(TacosCommander dcmd, MapleCharacter chr) {
        MapleMap map = chr.getMap();

        switch (dcmd.get(0)) {
            // enable actions.
            case "/ea" -> {
                chr.sendStatChanged(true);
                return true;
            }
            case "/save" -> {
                chr.saveToDB(false);
                chr.DebugMsg("save : done.");
                return true;
            }
            // player command.
            case "/fm" -> {
                chr.saveLocation(SavedLocationType.FREE_MARKET, map.getReturnMap().getId());
                chr.changeMapById(910000000);
                return true;
            }
            case "/henesys" -> {
                chr.changeMapById(100000000);
                return true;
            }
            case "/leafre" -> {
                chr.changeMapById(240000000);
                return true;
            }
            case "/magatia" -> {
                chr.changeMapById(261000000);
                return true;
            }
            case "/autosp" -> {
                int skill_id = chr.getSpUsed().get();
                if (skill_id != 0) {
                    while (ReqCUser.OnSkillUpRequestInternal(chr, skill_id));
                }

                chr.DebugMsg("autosp : " + skill_id);
                return true;
            }
            case "/wh" -> {
                for (MapleCharacter player : chr.getChannelServer().getOnlinePlayers().get()) {
                    if (player.getId() != chr.getId()) {
                        player.changeMapWithCoordinate(map.getId(), chr.getX(), chr.getY());
                    }
                }
                return true;
            }
            default -> {
            }
        }

        return false;
    }
}
