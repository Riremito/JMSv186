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
package tacos.script.portal;

import java.util.HashMap;
import java.util.regex.Pattern;
import tacos.client.TacosCharacter;
import tacos.server.map.TacosPortal;

/**
 *
 * @author Riremito
 */
public class TacosSharedPortal {

    public enum SharedPortalType {
        FREE_MARKET,
        PACHINKO_HALL,
        MEISTER_VILLE,
        UNKNOWN;
    }

    public static boolean use(TacosCharacter chr, TacosPortal portal) {
        for (TacosSharedPortal shared_portal : chr.getSharedPortals().values()) {
            // 正規表現でenter側にleaveの名前が含まれてしまうのでleaveを優先して処理する, enter側から除外すれば並び替えても問題ない
            if (shared_portal.leave(chr, portal)) {
                chr.DebugMsg("SharedPortal : " + shared_portal.getType().name() + ", leave (" + shared_portal.getReturnMapId() + ", " + shared_portal.getReturnPortalName() + ")");
                return true;
            }
            if (shared_portal.enter(chr, portal)) {
                chr.DebugMsg("SharedPortal : " + shared_portal.getType().name() + ", enter (" + shared_portal.getReturnMapId() + ", " + shared_portal.getReturnPortalName() + ")");
                return true;
            }
        }
        return false;
    }

    public static HashMap<SharedPortalType, TacosSharedPortal> load() {
        HashMap<SharedPortalType, TacosSharedPortal> ret = new HashMap<>();
        for (SharedPortalType type : SharedPortalType.values()) {
            TacosSharedPortal portal = new TacosSharedPortal();
            switch (type) {
                case FREE_MARKET -> {
                    portal.setEnter("market(J|)\\d+", 910000000, "out00");
                    portal.setLeave("market00", 102000000, "market00"); // Perion.
                }
                case PACHINKO_HALL -> {
                    portal.setEnter("pachinko(0*\\d+|Enter)", 809030000, "out00");
                    portal.setLeave("pachinko", 801000300, "in00"); // Showa Market.
                }
                case MEISTER_VILLE -> {
                    portal.setEnter("profession\\d+", 910001000, "st00");
                    portal.setLeave("out_profession", 100000000, "profession"); // Henesys.
                }
                default -> {
                    continue;
                }
            }
            portal.setType(type);
            ret.put(type, portal);
        }
        return ret;
    }

    private SharedPortalType type;
    // enter
    private Pattern enter_script_pattern;
    private int target_map_id;
    private String target_portal_name;
    // leave
    private String leave_script_name;
    private int return_map_id;
    private String return_portal_name;

    private SharedPortalType getType() {
        return this.type;
    }

    private void setType(SharedPortalType type) {
        this.type = type;
    }

    private void setEnter(String script_name, int map_id, String portal_name) {
        this.enter_script_pattern = Pattern.compile(script_name);
        this.target_map_id = map_id;
        this.target_portal_name = portal_name;
    }

    private void setLeave(String script_name) {
        this.leave_script_name = script_name;
        this.return_map_id = 100000000;
        this.return_portal_name = "";
    }

    private void setLeave(String script_name, int map_id, String portal_name) {
        this.leave_script_name = script_name;
        this.return_map_id = map_id;
        this.return_portal_name = portal_name;
    }

    private boolean enter(TacosCharacter chr, TacosPortal portal) {
        if (!this.enter_script_pattern.matcher(portal.getScriptName()).matches()) {
            return false;
        }
        this.return_map_id = chr.getMap().getId();
        this.return_portal_name = portal.getName();
        return chr.changeMap(this.target_map_id, this.target_portal_name);
    }

    private boolean leave(TacosCharacter chr, TacosPortal portal) {
        if (!this.leave_script_name.equals(portal.getScriptName())) {
            return false;
        }
        return chr.changeMap(this.return_map_id, this.return_portal_name);
    }

    public int getReturnMapId() {
        return this.return_map_id;
    }

    public void setReturnMapId(int return_map_id) {
        this.return_map_id = return_map_id;
    }

    public String getReturnPortalName() {
        return this.return_portal_name;
    }

    public void setReturnPortalName(String return_portal_name) {
        this.return_portal_name = return_portal_name;
    }
}
