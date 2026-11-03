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

import odin.client.Skill;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class NpcLordJonathan extends NpcScriptMan {

    private final int job_captain = 522; // キャプテン
    private final int skill_air_strike = 5221003; // エアストライク

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 本来はクエストで習得する
                if (getPlayer().getJob() == this.job_captain && 120 <= getPlayer().getLevel()) {
                    Skill skill = WzXML.SKILL.getSkill().get(this.skill_air_strike);
                    if (skill != null && getPlayer().getMasterLevel(skill) == 0) {
                        getPlayer().changeSkillLevel(skill, (byte) 0, (byte) 10);
                    }
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("エヘン！どうかしたのか？");
                say(nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
