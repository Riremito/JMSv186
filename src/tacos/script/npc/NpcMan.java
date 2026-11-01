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
package tacos.script.npc;

import tacos.client.TacosCharacter;
import tacos.debug.DebugMan;
import tacos.debug.IDebugMan;
import tacos.wz.NpcWz.NpcInfo;

/**
 *
 * @author Riremito
 */
public class NpcMan {

    public static boolean start(TacosCharacter chr, NpcInfo npc_info, int npc_script_id, int npc_id) {
        DebugMan script;
        switch (npc_script_id) {
            case 2110005 -> {
                // JMS147
                // ラクダ
                script = new NpcCamel();
                script.setNpcId(npc_script_id);
                script.start((IDebugMan) script, chr);
                return true;
            }
            case 9110000, 9110008 -> {
                // カニングシティ, ペリー
                // キノコ神社, ペリー
                script = new NpcPerry();
                script.setNpcId(npc_script_id);
                script.start((IDebugMan) script, chr);
                return true;
            }
            case 9110107 -> {
                // JMS131-151
                // キノコ神社 & 楓城, 駕籠
                script = new NpcPalakeen();
                script.setNpcId(npc_script_id);
                script.start((IDebugMan) script, chr);
                return true;
            }
            case 9120003 -> {
                // ショーワ町, 銭湯
                script = new NpcHikari();
                script.setNpcId(npc_script_id);
                script.start((IDebugMan) script, chr);
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
