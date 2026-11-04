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
package tacos.script.portal;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : dojang_next, dojang_up, dojang_tuto (JMS186), js の処理を利用
public class Portal_MuLung_Dojang extends PortalScriptMan {

    private final int mob_boss_check = 9300216; // ボスモンスター退治チェック用透明モンスター
    private final int map_dojo_entrance = 925020001; // 武陵道場 : 武陵道場入口

    @Override
    public boolean enter() {
        switch (getScriptName()) {
            case "dojang_next" -> {
                if (!haveMonster(this.mob_boss_check)) {
                    // TODO : 文章 (js の英文を翻訳, 原文 : There are still some monsters remaining.)
                    playerMessage("まだモンスターが残っています。");
                    return false;
                }
                return dojoNextMap();
            }
            case "dojang_up" -> {
                if (!haveMonster(this.mob_boss_check)) {
                    // TODO : 文章 (js の英文を翻訳, 原文 : There are still some monsters remaining.)
                    playerMessage("まだモンスターが残っています。");
                    return false;
                }
                dojoGetUp();
                getMap().setReactorState();
                return true;
            }
            case "dojang_tuto" -> {
                if (getMap().getMonsterById(this.mob_boss_check) != null) {
                    return warp(this.map_dojo_entrance, 0);
                }
                // TODO : 文章 (js の英文を翻訳, 原文 : So Gong: Haha! You're going to run away like a coward? I won't let you get away that easily!)
                playerMessage(6, "ソゴン : ハハ！臆病者のように逃げ出すつもりか？そう簡単には逃がさんぞ！");
                return false;
            }
            default -> {
                return false;
            }
        }
    }
}
