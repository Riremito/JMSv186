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

import odin.server.life.MobSkill;
import tacos.packet.ops.OpsMobSkill;
import tacos.server.map.object.TacosMonster;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class Npc_Magatia_IncompleteMagicSquare extends NpcScriptMan {

    private final int mob_dito_and_roi = 8090000; // ディトとロイ

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // マクロ対策モンスターの弱体化 (スキル封印)
                TacosMonster mob = getPlayer().getMap().getMonsterById(this.mob_dito_and_roi);
                MobSkill seal_skill = WzXML.SKILL.getMobSkillData(OpsMobSkill.MOBSKILL_SEALSKILL.get(), 1);
                if (mob != null && seal_skill != null) {
                    seal_skill.applyEffect(null, mob, false);
                }
                say("幾何学な模様が描かれている魔方陣だ。完成には至っていないようだが…。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
