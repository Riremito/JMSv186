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
public class Npc_Kerning_SubwayTicketSeller extends NpcScriptMan {

    private final int npc_gate = 1052007; // 改札口
    private final int required_level = 20;
    private final String[] ticket_names = {
        "工事現場B1",
        "工事現場B2",
        "工事現場B3"
    };
    private final int[] ticket_ids = {
        4031036, // 工事場B1入場券
        4031037, // 工事場B2入場券
        4031038 // 工事場B3入場券
    };
    private final int[] ticket_levels = {
        20,
        30,
        40
    };
    private final int[] ticket_costs = {
        500,
        1200,
        2000
    };
    // 翻訳
    private final String[] bought_texts = {
        "切符は#p1052007#に入れてください。エリア1には貴重なアイテムがあるそうですが、あちこちに罠が多くて、ほとんどの人がすぐに戻ってきてしまいます。幸運を祈ります。",
        "切符は#p1052007#に入れてください。エリア2には貴重なアイテムがあるそうですが、あちこちに罠が多くて、ほとんどの人がすぐに戻ってきてしまいます。どうかお気をつけて。",
        "切符は#p1052007#に入れてください。エリア3には貴重なアイテムがあるそうですが、あちこちに罠が多くて、ほとんどの人がすぐに戻ってきてしまいます。幸運を祈ります。"
    };
    private int selected = -1;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        // いいえを選択した
        if (status == 0 && this.asked) {
            // 翻訳
            say("切符を買えば中に入れます。中のあちこちに奇妙な装置があるそうですが、最後には貴重で珍しいアイテムが待っているそうですよ。気が変わったら言ってください。");
            return false;
        }
        switch (status) {
            case 0 -> {
                if (getLevel() < this.required_level) {
                    // 翻訳
                    say("切符を買えば入れますが、あなたにはまだ無理そうですね。地下には奇妙な装置があって、あなたが相手にするのはとても大変でしょうから、修行して準備を整えてから来てくださいね。");
                    return false;
                }
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("入るには切符を買う必要があります。切符を買ったら、右側の" + getNpcName(this.npc_gate) + "から入れます。どれを買いますか？");
                for (int i = 0; i < this.ticket_ids.length; i++) {
                    if (this.ticket_levels[i] <= getLevel()) {
                        nt.addMenu(i, this.ticket_names[i]);
                    }
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.ticket_ids.length <= answer || getLevel() < this.ticket_levels[answer]) {
                    return false;
                }
                this.selected = answer;
                this.asked = true;
                // 翻訳
                askYesNo("#b" + this.ticket_names[this.selected] + "#kの切符を買いますか？" + this.ticket_costs[this.selected] + "メルかかります。購入する前に、ETCインベントリに空きスロットがあるか確認してください。");
                return true;
            }
            case 2 -> {
                int ticket_id = this.ticket_ids[this.selected];
                if (getMeso() < this.ticket_costs[this.selected] || !canHold(ticket_id, 1)) {
                    // 翻訳
                    say("お金が足りませんか？ETCインベントリに空きスロットがあるかどうかも確認してください。");
                    return false;
                }
                payMoney(this.ticket_costs[this.selected]);
                gainItem(ticket_id, 1);
                say(this.bought_texts[this.selected]);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
