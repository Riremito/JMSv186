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
package tacos.debug;

import java.util.HashMap;
import tacos.constants.TacosConstants;
import tacos.script.npc.NpcScriptTag;
import tacos.server.map.TacosMap;
import tacos.server.map.TacosPortal;

/**
 *
 * @author Riremito
 */
public class DebugMan_MapPortal extends DebugNpcScriptMan {

    private HashMap<Integer, TacosPortal> portals = new HashMap<>();

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                TacosMap map = getPlayer().getMap();
                NpcScriptTag nt = new NpcScriptTag();
                int index = 0;
                for (TacosPortal portal : map.getPortals()) {
                    if (portal.getScriptName().isEmpty()) {
                        if (portal.getTargetMapId() != map.getId() && portal.getTargetMapId() != TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID) {
                            nt.addMenu(index, portal.getName() + " : " + portal.getTargetMapId() + ", " + portal.getTarget() + " (#m" + portal.getTargetMapId() + "#)");
                            this.portals.put(index++, portal);
                        }
                    } else {
                        nt.addMenu(index, portal.getName() + " : " + portal.getScriptName());
                        this.portals.put(index++, portal);
                    }
                }
                if (index == 0) {
                    say("no portal.");
                    return false;
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                TacosPortal portal = this.portals.get(answer);
                if (portal == null) {
                    return false;
                }
                if (portal.getScriptName().isEmpty()) {
                    portal.enterPortal(getPlayer());
                } else {
                    portal.enterScriptPortal(getPlayer());
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
