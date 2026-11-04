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
// Npc.wz script : go_xmas (JMS131-302), 会話文は lua_script_jp (go_xmas) を利用
// 期間の確認は削除
public class Npc_Perion_Runi extends NpcScriptMan {

    private final int map_happy_village = 209000000; // 隠れ道 : 幸福の村
    // 幸福の村からの帰還先 (lua の quest 9002 の値の順)
    private final int[] maps_return = {
        250000000, // 武陵 : 武陵
        101000000, // エリニア : エリニア
        200000000, // オルビス : オルビス
        211000000, // エルナス : エルナス
        220000300, // ルディブリアム : ルディブリアム住宅街
        600000000, // ニューリーフシティ
        100000000, // ヘネシス : ヘネシス
    };
    private final int quest_return = 9002; // 幸福の村からの帰還先
    private final int quest_newspaper = 4437; // 新聞配達の申込書
    private int last_status = 0;
    private int return_index = -1;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        // 翻訳
        if (status == 0) {
            int map_id = getMapId();
            for (int i = 0; i < this.maps_return.length; i++) {
                if (map_id == this.maps_return[i]) {
                    this.return_index = i;
                }
            }
            if (this.return_index < 0) {
                return false;
            }
            int quest_status = getPlayer().getQuestStatus(this.quest_newspaper);
            if (quest_status == 1 || quest_status == 2) {
                askYesNo("あら～新聞配達の申込書を手に入れたのね～ ハッピービレッジがあなたのためにどれだけの冒険を用意しているか、#G気になる:気になる#？行きましょう！");
            } else if (map_id == 101000000) {
                askYesNo("ハッピービレッジがあなたのために用意している冒険が#G気になる:気になる#？行きましょう～～");
            } else {
                askYesNo("ハッピービレッジがあなたのために用意している冒険が#G気になる:気になる#？行きましょう！");
            }
            return true;
        }
        if (!ok) {
            say("今はとても#G忙しい:忙しい#みたいね。旅の途中で時間ができたら、ぜひ会いに来て！ほかにはないクリスマスの町を見られるわよ～");
            return false;
        }
        setQuestData(this.quest_return, String.valueOf(this.return_index));
        warp(this.map_happy_village, "st00");
        return false;
    }
}
