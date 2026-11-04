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
public class Npc_Victoria_Mike extends NpcScriptMan {

    private final int quest_id = 2048; // グラディウスの覚醒方法
    private final int required_level = 45;
    // 翻訳
    private final String[] says_quest = {
        "ここを通ると、ビクトリアアイランドの中央ダンジョンに出る。気をつけるんだぞ…",
        "うーん…それで、#b#t4021009##k、#b#t4003002##k、#b#t4001005##k、#b#t4001006##kの手に入れ方を知りたいのか？そんな貴重な材料で何をするつもりだ？話には聞いたことがある…警備員として働く前に、島について少し勉強したからな…",
        "#b#t4021009##kと#b#t4003002##kか…#m101000000#の妖精たちが何か知っている気がするな。本当に決して溶けない#t4003002#の話をしているのなら、それは妖精の仕業だろう…おそらく#t4003002#も作っているはずだ。",
        "問題は#b#t4001005##kと#b#t4001006##kだ。モンスターたちが持っているかもしれない。長い間このあたりをうろついているからな…#t4001005#については…ああ、そうだ！ゴーレムが持っているはずだ。大昔に魔法使いたちによって作られたものだからな…",
        "#b#t4001006##k…聞いたことがある、羽根のような形の炎だとか…火を吹くドラゴンか何かと関係があるらしい…いずれにしても、とても凶暴だから、そいつから#t4001006#を奪うのは難しいだろう。幸運を祈る！"
    };
    private String[] says = null;

    @Override
    public boolean action(int status, int answer) {
        if (this.says == null) {
            if (getPlayer().getJob() != 0 && this.required_level <= getPlayer().getLevel() && getPlayer().getQuestStatus(this.quest_id) == 1) {
                this.says = this.says_quest;
            } else {
                this.says = new String[]{this.says_quest[0]};
            }
        }
        // 連続した会話
        if (status < 0 || this.says.length <= status) {
            return false;
        }
        boolean next = status + 1 < this.says.length;
        say(this.says[status], false, next);
        return next;
    }
}
