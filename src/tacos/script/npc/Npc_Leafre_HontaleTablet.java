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
// Npc.wz script : hontale_enterToE (JMS131-302), 会話文は lua_script_jp (hontale_enterToE) を利用
public class Npc_Leafre_HontaleTablet extends NpcScriptMan {

    private final int map_entrance = 240050400; // リプレ : ホーンテイルの洞窟入口
    private final int item_proof = 4001086; // 名誉遠征隊員の証票
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        if (status == 0) {
            // 翻訳
            if (!isPartyBoss()) {
                say("パーティーリーダーを通して進めてください。");
                return false;
            }
            if (getItemCount(this.item_proof) <= 0) {
                say("石板の文字が読めません。どこで使うのかまったくわかりません。");
                return false;
            }
            askYesNo("石板の文字がきらめき、裏口が開きました。秘密の通路へ行きますか？");
            return true;
        }
        if (!ok) {
            // 翻訳
            say("移動したい場合は、もう一度話しかけてください。");
            return false;
        }
        // TODO : lua の transferParty の第3引数 (2) の用途は不明
        transferParty(this.map_entrance, "");
        return false;
    }
}
