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
// Npc.wz script : Event02 (JMS131-302), 会話文は lua_script_jp (Event02) を利用
public class Npc_Event_Pietro extends NpcScriptMan {

    private final int map_south_perry = 60000; // サウスペリ : サウスペリ
    private final int map_lith_harbor = 104000000; // リス : 港口
    private final int map_orbis = 200000000; // オルビス : オルビス
    private final int map_ludi = 220000000; // ルディブリアム : ルディブリアム
    private final int map_g_star = 109080003; // 隠れ道 : G★ココナッツシーズン
    private final int npc_jean = 9000001; // ジャング
    private final int npc_paul = 9000000; // ポル
    private final int npc_tenchi = 9000007; // 天地
    private final int item_scroll = 4031019; // 悪魔の文書
    private final int quest_event_from = 9000; // イベントに参加した場所
    private final int quest_g_star = 9200; // G★
    private String[] says = null;
    private boolean has_scroll = false;
    private int map_return = 0;

    @Override
    public boolean action(int status, int answer) {
        // 翻訳
        if (status == 0 && this.says == null) {
            if (getQuestData(this.quest_g_star).equals("1")) {
                warp(this.map_g_star);
                return false;
            }
            String from = getQuestData(this.quest_event_from);
            if (!from.equals("maple") && !from.equals("victoria") && !from.equals("ossyria") && !from.equals("ludi")) {
                say("#p" + this.npc_jean + "#にも#p" + this.npc_paul + "#にも会っていないみたいだけど。そもそもどうやってここに来たの？？？君は…？！？！");
                return false;
            }
            this.map_return = from.equals("maple") ? this.map_south_perry : from.equals("victoria") ? this.map_lith_harbor : from.equals("ossyria") ? this.map_orbis : this.map_ludi;
            this.has_scroll = 1 <= getItemCount(this.item_scroll);
            if (this.has_scroll) {
                this.says = new String[]{
                    "君はもう#r#t" + this.item_scroll + "##kを持ってるよ。この書には驚くほど強力な魔力が込められていて、肌身離さず持っておくべきなんだ。すぐにこの書を#r#p" + this.npc_tenchi + "##kのところへ持って行ってね。"
                };
            } else {
                this.says = new String[]{
                    "パンパカパーン！！！#bイベント#kのゲームに勝利したよ。ここまでたどり着くなんておめでとう！",
                    "勝者へのご褒美として、#b#t" + this.item_scroll + "##kを贈るよ。この書には、古代文字で書かれた秘密の情報が記されているんだ。",
                    "秘密の書は、ルディブリアムにいる#r#p" + this.npc_tenchi + "##kか#rジーニー#kが解読してくれるよ。持って行けば、何かいいことがあるはずさ。"
                };
            }
        }
        if (status < 0) {
            return false;
        }
        if (status < this.says.length) {
            say(this.says[status], 0 < status, true);
            return true;
        }
        if (!this.has_scroll) {
            // TODO : lua の exchangeEx の Period:43200 (有効期限) は未実装
            if (!exchange(0, this.item_scroll, 1)) {
                say("インベントリがいっぱいみたいだね。空きを作ってから話しかけてね。");
                return false;
            }
            // TODO : lua の logEvent は未実装
        }
        removeQuestData(this.quest_event_from);
        warp(this.map_return);
        return false;
    }
}
