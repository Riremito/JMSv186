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
public class NpcAlcaster extends NpcScriptMan {

    private final int quest_old_book = 3035; // 古代の本の封印
    private final int[] item_ids = {
        2050003, // 聖水
        2050004, // 万病治療薬
        4006000, // 魔法の石
        4006001 // 召喚の石
    };
    private final int[] costs = {
        300,
        400,
        5000,
        5000
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getPlayer().getQuestStatus(this.quest_old_book) != 2) {
                    if (54 < getPlayer().getLevel()) {
                        // 翻訳
                        say("私を手伝ってくれると決めてくれたら、お礼にアイテムを売ってあげよう。");
                        return false;
                    }
                    // 翻訳
                    say("私は魔術師アルケスター。300年以上この町に住み、多くのまじないや呪文を研究してきた。");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("君のお陰で#b古代の本#kは無事に封印できた。だがそのせいで８００年間に修練してきたわしの力の半分を無くしたがね、もう死んでも惜しくない。そうだ、どころで君、珍しいアイテムは欲しくないか？今まで手伝ってくれたお礼だ。わしが持っているもので必要なものがあるなら安く売ってあげよう。");
                for (int i = 0; i < this.item_ids.length; i++) {
                    nt.addMenu(i, "#t" + this.item_ids[i] + "#(値段：" + this.costs[i] + " メル)");
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : アイテムの購入 (askNumber で購入数を入力, メルの支払い)
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
