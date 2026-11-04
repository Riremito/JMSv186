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
// Npc.wz script : Event06 (JMS131-302), 会話文は lua_script_jp (Event06) を利用
public class NpcPietra extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_MENU = 1;
    private static final int NODE_ASK = 2;
    private static final int NODE_RETURN = 3;
    private final int map_south_perry = 60000; // サウスペリ : サウスペリ
    private final int map_lith_harbor = 104000000; // リス : 港口
    private final int map_orbis = 200000000; // オルビス : オルビス
    private final int map_ludi = 220000000; // ルディブリアム : ルディブリアム
    private final int map_g_star = 109080003; // 隠れ道 : G★ココナッツシーズン
    private final int npc_baikun = 9000006; // バイクン
    private final int item_map = 4031018; // 宝の地図
    private final int quest_event_from = 9000; // イベントに参加した場所
    private final int quest_g_star = 9200; // G★
    private int node = NODE_START;
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        String from = getQuestData(this.quest_event_from);
        int map_return = from.equals("maple") ? this.map_south_perry : from.equals("victoria") ? this.map_lith_harbor : from.equals("ossyria") ? this.map_orbis : this.map_ludi;
        // 翻訳
        switch (this.node) {
            case NODE_START -> {
                if (getQuestData(this.quest_g_star).equals("1")) {
                    warp(this.map_g_star);
                    return false;
                }
                if (1 <= getItemCount(this.item_map)) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("#b#t" + this.item_map + "##kを持ってるんだね。私と話すより、#t" + this.item_map + "#を賞品と交換する件で#p" + this.npc_baikun + "#と話したいんじゃない？\r\n\r\n#b#L0# #p" + this.npc_baikun + "#って誰？#l\r\n#b#L1# 元いた場所に戻して。#l#k", false);
                    askMenu(nt);
                    this.node = NODE_MENU;
                    return true;
                }
                say("残念だけど、今回はイベントに勝てなかったね。また別の機会に挑戦してね。私のところから元いた場所に戻れるよ。", false, true);
                this.node = NODE_RETURN;
                return true;
            }
            case NODE_MENU -> {
                if (answer == 0) {
                    say("#b#p" + this.npc_baikun + "##kは、#t" + this.item_map + "#を賞品と交換できるマップへ送ってくれる人だよ。今私がいる場所の左側にいるから、すぐ見つかるはず。");
                    return false;
                }
                askYesNo("戻る前に#b#t" + this.item_map + "##kを賞品と交換しておくのがおすすめだよ。リスハーバーでも交換できるけど、忙しいなら今すぐ帰ってもいいよ。今すぐ帰る？");
                this.node = NODE_ASK;
                return true;
            }
            case NODE_ASK -> {
                if (!ok) {
                    return false;
                }
                removeQuestData(this.quest_event_from);
                warp(map_return);
                return false;
            }
            case NODE_RETURN -> {
                removeQuestData(this.quest_event_from);
                warp(map_return);
                return false;
            }
            default -> {
                return false;
            }
        }
    }
}
