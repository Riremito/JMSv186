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
// Npc.wz script : Event07 (JMS131-302), 会話文は lua_script_jp (Event07) を利用
public class Npc_Event_MobileMaple extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        // 翻訳
        if (status == 0) {
            NpcScriptTag nt = new NpcScriptTag();
            nt.add("携帯電話でメイプルストーリー（- ウィザード）が遊べるよ！しかもメイプルストーリー（- ウィザード）をダウンロードすると、キャッシュアイテムが6個無料でもらえるんだ！ダウンロードできる場所は（-Wizet）。へへ\r\n#b#L0# SKT(011, 017, 010) ユーザー#l\r\n#b#L1# KTF(016, 018, 010) ユーザー#l", false);
            askMenu(nt);
            return true;
        }
        if (answer == 0 || answer == 1) {
            say("購入済みアイテム");
        }
        return false;
    }
}
