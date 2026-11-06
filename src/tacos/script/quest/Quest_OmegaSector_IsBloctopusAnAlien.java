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
package tacos.script.quest;

/**
 *
 * @author Riremito
 */
// Quest.wz endscript : q3452e (JMS186), ブロックパスは外界生物？, lua の処理を利用
public class Quest_OmegaSector_IsBloctopusAnAlien extends QuestScriptMan {

    private final int item_strap = 4000099; // ブロックパスのストラップ
    private final int item_blueprint = 4001125; // ブロックパスの設計図
    private final int item_scroll = 2040701; // 素早さの書(靴)
    private final int item_mana_elixir_pill = 2000011; // マナエリクサー丸薬
    private boolean has_blueprint = false;

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getItemCount(this.item_strap) < 1) {
                    // 翻訳
                    say("まだタコのキーホルダーを手に入れていないようだな。それはブロックトパスたちが持っているよ。");
                    return false;
                }
                this.has_blueprint = getItemCount(this.item_blueprint) >= 1;
                if (this.has_blueprint) {
                    // 翻訳
                    say("タコのキーホルダーは手に入れてきたかね？ふむ…かわいらしい品だな。だが、これこそ地球を脅かす異星人の正体を明かす重要な品…待て！", false, true);
                    return true;
                }
                // 翻訳
                say("タコのキーホルダーは手に入れてきたかね？ふむ…かわいらしい品だな。だが、これこそ地球を脅かす異星人の正体を明かす重要な品なのだ。本当にありがとう。\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + this.item_mana_elixir_pill + "# #t" + this.item_mana_elixir_pill + "# 50個\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 8000 経験値", false, true);
                return true;
            }
            case 1 -> {
                if (this.has_blueprint) {
                    // 翻訳
                    say("君が持っているその品！ちょっと見せてくれたまえ。君が手に持っている、その設計図だよ。おお～こんなものをどこで手に入れたのかね？これさえあれば、ブロックトパスの研究をもっと早く進められそうだ。", true, true);
                    return true;
                }
                if (!exchange(0, this.item_strap, -1, this.item_mana_elixir_pill, 50)) {
                    // 翻訳
                    say("どうしてそんなにたくさん持ち歩いているのかね？インベントリに空きがあるか確認してくれたまえ。");
                    return false;
                }
                gainExp(8000);
                forceCompleteQuest();
                questEndEffect();
                // 翻訳
                say("ブロックトパスと異星人…どう考えても何か似ているんだよなあ。いや、タコと異星人が似ているのかもしれん…ふむ。これもまた新しい理論だな。");
                return false;
            }
            case 2 -> {
                // 翻訳
                say("思わぬ収穫だな。よし。君に特別な贈り物をしよう。きっと役に立つはずだ。はっはっは～\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + this.item_scroll + "# #t" + this.item_scroll + "# 1個\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 16000 経験値", true, true);
                return true;
            }
            case 3 -> {
                if (!exchange(0, this.item_strap, -1, this.item_blueprint, -1, this.item_scroll, 1)) {
                    // 翻訳
                    say("どうしてそんなにたくさん持ち歩いているのかね？インベントリに空きがあるか確認してくれたまえ。");
                    return false;
                }
                gainExp(16000);
                forceCompleteQuest();
                questEndEffect();
                // 翻訳
                say("ブロックトパスと異星人…どう考えても何か似ているんだよなあ。いや、タコと異星人が似ているのかもしれん…ふむ。これもまた新しい理論だな。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
