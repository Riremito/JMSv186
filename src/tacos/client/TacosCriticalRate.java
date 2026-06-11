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
package tacos.client;

import odin.client.ISkill;
import odin.client.SkillFactory;
import odin.server.MapleStatEffect;
import tacos.config.Version;
import tacos.packet.ops.OpsSkill;

/**
 *
 * @author Riremito
 */
public class TacosCriticalRate {

    private final TacosCharacter chr;
    private final int base;
    private int critical_rate = 0;

    public TacosCriticalRate(TacosCharacter chr) {
        this.chr = chr;
        this.base = Version.PostBB() ? 5 : 0;
    }

    public int get() {
        return this.critical_rate + this.base + getPassiveRate();
    }

    public int getPassiveRate() {
        int job = this.chr.getJob() / 100 * 100;

        OpsSkill ops_skill;
        switch (job) {
            case 300 -> {
                // 300.img/3000001/skill/level/1/prop
                ops_skill = OpsSkill.ARCHER_CRITICAL_SHOT;
            }
            case 400 -> {
                ops_skill = OpsSkill.ASSASSIN_CRITICAL_THROW;
            }
            case 500 -> {
                ops_skill = OpsSkill.GUNSLINGER_CRITICAL_SHOT;
            }
            case 1300 -> {
                ops_skill = OpsSkill.WINDBREAKER_CRITICAL_SHOT;
            }
            case 1400 -> {
                ops_skill = OpsSkill.NIGHTWALKER_CRITICAL_THROW;
            }
            case 2200 -> {
                ops_skill = OpsSkill.EVAN_MAGIC_CRITICAL;
            }
            default -> {
                return 0;
            }
        }

        ISkill skill = SkillFactory.getSkill(ops_skill.get());
        MapleStatEffect mse = skill.getEffect(this.chr.getSkillLevel(ops_skill));
        return mse.getProb();
    }
}
