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
// Npc.wz script : goshanghai2 (JMS146-302), lua_script_jp の goshanghai2 は中身がないため js の処理を利用
public class NpcYangThePilot extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        // TODO : 飛行機でペリオンへ移動する処理 (String.wz の func : 飛行機操縦士(上海→ペリオン)), js は会話のみ
        // 原文ママ (JMS186 String.wz の n0)
        say("空を飛ぶことは誰にでもできることではないさ。");
        return false;
    }
}
