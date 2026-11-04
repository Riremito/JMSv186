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

import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
// Npc.wz script : go_victoria (JMS131-302), 会話文は lua_script_jp (go_victoria) を利用
public class Npc_HappyVille_Rupi extends NpcScriptMan {

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
    private final int[] item_xmas = {
        1472063, // 魔法ミトン
        2060005, // 溶けない雪球
        2060006, // とても大きな雪球
    };
    private int last_status = 0;
    private int return_index = 6;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        // 翻訳
        if (status == 0) {
            String val = getQuestData(this.quest_return);
            for (int i = 0; i < this.maps_return.length; i++) {
                if (val.equals(String.valueOf(i))) {
                    this.return_index = i;
                }
            }
            // lua の文章の誤り (マップ ID や地域名) はそのまま
            String[] texts = {
                "ここではもうやることがないのね？#m250000000#に戻りたい？戻りたいなら、今すぐ#m250000000#へ送ってあげられるわ。どうする？戻る？",
                "ここではもうやることがないのね？ビクトリアアイランドの#m1010000に戻りたい？戻りたいなら、今すぐ#m101000000#へ送ってあげられるわ。どうする？戻る？",
                "ここではもうやることがないのね？オシリアの#m200000000#に戻りたい？戻りたいなら、今すぐ#m200000000#へ送ってあげられるわ。どうする？戻る？",
                "ここではもうやることがないのね？オシリアの#m200000000#に戻りたい？戻りたいなら、今すぐ#m211000000#へ送ってあげられるわ。どうする？戻る？",
                "ここではもうやることがないのね？オシリアの#m2000000300#に戻りたい？戻りたいなら、今すぐ#m220000300#へ送ってあげられるわ。どうする？戻る？",
                "ここではもうやることがないのね？マステリアの#m600000000#に戻りたい？戻りたいなら、今すぐ#m600000000#へ送ってあげられるわ。どうする？戻る？",
                "ここではもうやることがないのね？ビクトリアアイランドの#m101000000#に戻りたい？戻りたいなら、今すぐ#m100000000#へ送ってあげられるわ。どうする？戻る？"
            };
            askYesNo(texts[this.return_index]);
            return true;
        }
        if (!ok) {
            say("ここで何か用事があるのよね？戻る前に、このあたりで少し休んで力を回復しておくのも悪くないわよ。");
            return false;
        }
        for (int item_id : this.item_xmas) {
            removeItemAll(item_id);
            MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.EQUIPPED, item_id, 1, true, false);
        }
        warp(this.maps_return[this.return_index], "xmas00");
        return false;
    }
}
