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
// Npc.wz script : LuckyBag (JMS146-302), lua と js が存在しないため mock として実装
// TODO : LuckyBag の処理
public class Npc_Zipangu_BuunBuun extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        if (status == 0) {
            // 原文ママ (JMS186 String.wz の d0)
            say("おいらのコマさばきを見ておくれよ！");
        }
        return false;
    }
}
