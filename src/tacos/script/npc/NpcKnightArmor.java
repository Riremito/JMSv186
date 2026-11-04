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
// Npc.wz script : guildquest1_knight (JMS131-302), 会話文は lua_script_jp (guildquest1_knight) を利用
public class NpcKnightArmor extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        // 翻訳
        say("シャレニアンへの侵攻を成功させるため、悪魔エルゴスはシャレニアンを守っていた槍、#rロンギヌスの槍#kを封印しました。#rロンギヌスの槍#kの力を取り戻せば、行く手を阻む邪悪な力を打ち破れるかもしれません。");
        return false;
    }
}
