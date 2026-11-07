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
package tacos.script.reactor;

import tacos.packet.ops.OpsMobSkill;

/**
 *
 * @author Riremito
 */
// Reactor.wz action : scaScript1, scaScript2, scaScript3, scaScript4 (JMS186), lua の scavenger0 (sca_lich0 - sca_lich7) を参考
// 状態 0 から 1 (アイテムで反応) で回避率の低下 (155 Lv.2), 攻撃で破壊されるとスキル封印 (157 Lv.1)
public class Reactor_ElNath_ScaScript extends ReactorScriptMan {

    private final int mob_lich = 6090000; // リーチ

    @Override
    public void changeState(int old_state) {
        if (old_state != 0 || getReactor().getState() != 1) {
            return;
        }
        if (checkMob(this.mob_lich)) {
            nuffMob(this.mob_lich, OpsMobSkill.MOBSKILL_EVA.get(), 2);
            // 原文ママ
            mapMessage("碑石が輝かしながら消えた途端、リッチの魔法能力が消えた。");
        }
    }

    @Override
    public void action() {
        if (checkMob(this.mob_lich)) {
            nuffMob(this.mob_lich, OpsMobSkill.MOBSKILL_SEALSKILL.get(), 1);
            // 原文ママ
            mapMessage("リッチの力の源である箱が破壊された途端、リッチを保護した目に見えない力が消えた。");
        }
    }
}
