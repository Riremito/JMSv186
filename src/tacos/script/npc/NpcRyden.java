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
public class NpcRyden extends NpcScriptMan {

    private final int required_level = 20;
    private final int subcategory_dual_blade = 1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getPlayer().getLevel() < this.required_level) {
                    if (getPlayer().getSubcategory() != this.subcategory_dual_blade) {
                        // TODO : 文章 (適当)
                        say("私と話すには、キャラクター作成時にデュアルブレイドを選択している必要がある。");
                        return false;
                    }
                    // TODO : 文章 (適当)
                    say("私と話すには、レベル2とレベル9のクエストを受けている必要がある。");
                    return false;
                }
                // TODO : 文章 (適当)
                say("私は秘密の花園の入口を守っている…おっと、もう秘密ではなくなってしまったかな？");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
