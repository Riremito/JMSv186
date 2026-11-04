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
public class Npc_Ellinia_Shane extends NpcScriptMan {

    private final int quest_diet_medicine = 2050; // ダイエット薬
    private final int quest_rejuvenation_medicine = 2051; // 若返りの薬
    private final int map_forest_of_patience_1 = 101000100; // 隠れ道 : 忍耐の森-1段階
    private final int map_forest_of_patience_3 = 101000102; // 隠れ道 : 忍耐の森-3段階
    private final int cost = 6000;
    private int to_map_id = -1;
    private int to_cost = 0;

    @Override
    public boolean action(int status, int answer) {
        int state_diet = getPlayer().getQuestStatus(this.quest_diet_medicine);
        int state_rejuvenation = getPlayer().getQuestStatus(this.quest_rejuvenation_medicine);
        switch (status) {
            case -1 -> {
                // 翻訳
                say("そうか…だが、こっちの立場もわかってくれ。ここにただでいさせるわけにはいかないんだ。");
                return false;
            }
            case 0 -> {
                // 若返りの薬のクエスト進行中
                if (state_rejuvenation == 1) {
                    // TODO : JMSの料金が不明 (翻訳元はレベル x 200)
                    this.to_map_id = this.map_forest_of_patience_3;
                    this.to_cost = getPlayer().getLevel() * 200;
                        // 翻訳
                    askYesNo("この前の奴か…#b#p1061005##kにまた頼まれたのか？何？もっと長くいる必要があるって？うーん…あそこはとても危険だが…まあいい、#r" + this.to_cost + "メルで隅々まで探させてやろう。それで、入場料を払うか？");
                    return true;
                }
                // 若返りの薬のクエスト完了
                if (state_rejuvenation == 2) {
                    this.to_map_id = this.map_forest_of_patience_3;
                    // 翻訳
                    say("この前の奴か…#p1061005#は若返りの薬作りを頑張っているのか？とにかく、お前がここを難なく通り抜けたのには少し驚いたよ。ご褒美として、しばらくはただでいさせてやる。途中でいいアイテムが見つかるかもしれないぞ。", false, true);
                    return true;
                }
                // ダイエット薬のクエスト進行中
                if (state_diet == 1) {
                    this.to_map_id = this.map_forest_of_patience_1;
                    this.to_cost = this.cost;
                        NpcScriptTag nt = new NpcScriptTag();
                    nt.add("#bサビトラマ#kから頼まれて薬草を取りに来ただって？　だが、ここは父から譲り受けた大切な場所なんだよ…。本当なら誰も入れる訳にはいけないんだが、#r" + this.to_cost + "#k メルさえ払えば話は違うぞ。どうだ、メルを払うかい？");
                    askYesNo(nt);
                    return true;
                }
                // ダイエット薬のクエスト完了
                if (state_diet == 2) {
                    // 翻訳
                    say("この前の奴か…#p1061005#はダイエット薬作りを頑張っているのか？とにかく、お前がここを難なく通り抜けたのには少し驚いたよ。ご褒美として、しばらくはただでいさせてやる。途中でいいアイテムが見つかるかもしれないぞ。");
                    warp(this.map_forest_of_patience_1);
                    return false;
                }
                // 翻訳
                say("入りたいのか？ここに貴重な薬草があると聞いたんだろう？だが、俺がこの土地の持ち主だということも知らないようなよそ者を入れるわけにはいかない。悪いが、それだけだ。");
                return false;
            }
            case 1 -> {
                if (state_rejuvenation == 2) {
                    // 翻訳
                    say("ああ、そうだ…この町の#p1032100#がこっそり忍び込もうとしてな。俺が捕まえたんだが、そのときに#p1032100#をあそこに落としていったんだ。探してみたんだが、どこにあるのか見当もつかない。探してみたらどうだ？");
                    warp(this.to_map_id);
                    return false;
                }
                if (!payMoney(this.to_cost)) {
                    // 翻訳
                    say("もしかしてお金が足りないのか？手元に#r" + this.to_cost + "#kメル以上あるか確認しな。値引きなんて期待するなよ。");
                    return false;
                }
                warp(this.to_map_id);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
