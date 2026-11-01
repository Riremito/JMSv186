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
import tacos.debug.DebugMan.NpcTag;
import tacos.debug.IDebugMan;

/**
 *
 * @author Riremito
 */
public class NpcPalakeen extends DebugMan implements IDebugMan {

    @Override
    public boolean start(TacosCharacter chr) {
        super.start(this, chr);
        return true;
    }

    @Override
    public boolean end(TacosCharacter chr) {
        super.end(chr);
        return true;
    }

    @Override
    public boolean action(TacosCharacter chr, int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcTag nt = new NpcTag();
                // TODO : 文章
                nt.add("");
                super.askYesNo(chr, nt);
                return true;
            }
            case 1 -> {
                NpcTag nt = new NpcTag();
                if (chr.getMapId() == 800000000) {
                    // キノコ神社 -> 楓城 天下泰平
                    chr.changeMapById(800040000); // TODO : portal name.
                } else {
                    // 楓城 天下泰平 -> キノコ神社
                    chr.changeMapById(800000000); // TODO : portal name.
                }
                return false;
            }
            case -1 -> {
                // not coded yet.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
