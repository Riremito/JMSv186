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

/**
 *
 * @author Riremito
 */
// Reactor.wz action : fgodNPC0, fgodNPC1 (JMS186), js の処理を利用
// TODO : js では OrbisPQ (EventManager) の経験値 (6000) と進行状況を設定しているが、グループクエストの Java の処理が未完成のため未移行
public class Reactor_Orbis_FgodNPC0 extends ReactorScriptMan {

    private final int reactor_fgod_npc0 = 2006000; // fgodNPC0
    private final int npc_eak = 2013001; // 侍従イク
    private final int npc_minerva = 2013002; // 女神ミネルバ

    @Override
    public void action() {
        if (getReactorId() != this.reactor_fgod_npc0) {
            summonNpc(this.npc_minerva);
            return;
        }
        // TODO : 文章 (js の英文を翻訳, 原文 : As the light flickers, someone appears out of the light.)
        mapMessage(5, "光が揺らめき、光の中から誰かが現れた。");
        summonNpc(this.npc_eak);
    }
}
