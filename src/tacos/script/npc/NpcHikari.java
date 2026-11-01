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

/**
 *
 * @author Riremito
 */
public class NpcHikari extends DebugMan implements IDebugMan {

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

    private final int cost = 300;

    private boolean payMoney(TacosCharacter chr, int cost) {
        return chr.gainMeso(-cost, true, false, true);
    }

    @Override
    public boolean action(TacosCharacter chr, int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcTag nt = new NpcTag();
                nt.add("銭湯に入りたいですか？では、" + this.cost + "メルを払いなさい。");
                super.askYesNo(chr, nt);
                return true;
            }
            case 1 -> {
                NpcTag nt = new NpcTag();
                if (!payMoney(chr, this.cost)) {
                    nt.add("入場料" + this.cost + "メルを持っているか確認してください。");
                    super.say(chr, nt);
                    return false;
                }
                int target_map_id = chr.getGender() == 0 ? 801000100 : 801000200;
                chr.changeMapById(target_map_id);
                return false;
            }
            case -1 -> {
                NpcTag nt = new NpcTag();
                nt.add("今度利用してください。");
                super.say(chr, nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
