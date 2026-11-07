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

/**
 *
 * @author Riremito
 */
// Npc.wz script : shammos2 (JMS186-302), lua が存在しないため旧 js (scripts_JMS) を参考に実装
// マップの onUserEnter (shammos_Enter) で開始される
public class Npc_ElNath_ShammosLetter extends NpcScriptMan {

    private final int reactor_rex = 2118003; // bossLex
    private final int mob_rex = 9300281; // レックス

    @Override
    public boolean action(int status, int answer) {
        if (status != 0) {
            return false;
        }
        // パーティーのリーダーがリアクターを起動してレックスを召喚する
        if (isPartyBoss() && getMobCount(getMapId(), this.mob_rex) == 0) {
            forceStartReactor(this.reactor_rex);
        }
        // TODO : 文章 (適当)
        say("ハハハ！愚か者どもめ！お前たちを裏切って、ホブ帝王レックスの封印を解いてやったぞ！");
        return false;
    }
}
