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
public class NpcSpiruna extends NpcScriptMan {

    private static final int MENU_MAKE = 0;
    private static final int MENU_NOTHING = 1;
    private final int quest_id = 3014; // 入手した妖精の粉
    private final int npc_alcaster = 2020005; // アルケスタ
    private final int item_dark_crystal = 4005004; // 闇のクリスタル
    private final int item_dark_ore = 4004004; // クリスタルの原石(闇)
    private final int required_ore = 10;
    private final int cost = 50000;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        // いいえを選択した
        if (status == 0 && this.asked) {
            // 翻訳
            say("#b#t" + this.item_dark_crystal + "##k。ずいぶん長いこと見ていないわ…最後に精錬したのは何百年も前だから、どうやって作ったかほとんど思い出せないの…もちろん、今は持っていないでしょうけど…");
            return false;
        }
        switch (status) {
            case 0 -> {
                if (getPlayer().getQuestStatus(this.quest_id) != 2) {
                    // 翻訳
                    say("大事な呪文を研究しているところだから、邪魔しないですぐに出て行って。よそ者が家の周りをうろうろしていると集中できないの。お願いだから帰って…");
                    return false;
                }
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ヘラはいい子よ。難しいことでもそうでなくても、頼んだことは文句ひとつ言わずにやってくれるの。いつか私よりずっと優れた魔女になるわ。それで、私に何の用なの？？");
                nt.addMenu(MENU_MAKE, "#t" + this.item_dark_crystal + "#を作りたいです");
                nt.addMenu(MENU_NOTHING, "いいえ、何も");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer != MENU_MAKE) {
                    // 翻訳
                    say("大事な呪文を研究しているところだから、邪魔しないですぐに出て行って。よそ者が家の周りをうろうろしていると集中できないの。お願いだから帰って…");
                    return false;
                }
                this.asked = true;
                // 翻訳
                askYesNo("#b#t" + this.item_dark_crystal + "##k？？どうしてそれを…#b" + getNpcName(this.npc_alcaster) + "#kから聞いたの？ええ、精錬のしかたは知っているけど…この原石は手に入れるのがとても難しいのよ。#b#t" + this.item_dark_crystal + "#1個#kを作るには、#b#t" + this.item_dark_ore + "#" + this.required_ore + "個#kと" + this.cost + "メルが必要よ。作ってほしい？");
                return true;
            }
            case 2 -> {
                if (getPlayer().getMeso() < this.cost || !getPlayer().haveItem(this.item_dark_ore, this.required_ore) || !MapleInventoryManipulator.checkSpace(getPlayer(), this.item_dark_crystal, 1, "")) {
                    // 翻訳
                    say("メルが足りないの？#b#t" + this.item_dark_ore + "#" + this.required_ore + "個#kと" + this.cost + "メルを持っているか、ETCインベントリに空きがあるか確認してね。");
                    return false;
                }
                payMoney(this.cost);
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_dark_ore, this.required_ore, true, false);
                MapleInventoryManipulator.addById(getPlayer(), this.item_dark_crystal, (short) 1);
                // 翻訳
                say("はい、#b#t" + this.item_dark_crystal + "#1個#kよ。最後に作ったのはずいぶん前だから、うまくできているといいんだけど…それにしても、どうやってクリスタルの原石を手に入れたの？あなた、本当に特別な人なのね。とにかく、すごいアイテムよ。大切に使ってね。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
