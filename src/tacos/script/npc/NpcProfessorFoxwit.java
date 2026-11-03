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
public class NpcProfessorFoxwit extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        // iTCG
        switch (status) {
            case 0 -> {
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("どうしたんじゃ？");
                nt.addMenu(0, "こんなものを見つけたからちょっと見てほしくて、あなたが適任者だと思ったの。");
                nt.addMenu(1, "いいえ、なんでもないです。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : iTCG のアイテムの交換 (未実装)
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
