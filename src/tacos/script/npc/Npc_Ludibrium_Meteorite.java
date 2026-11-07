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
public class Npc_Ludibrium_Meteorite extends NpcScriptMan {

    private final int npc_meteorite_1 = 2050014; // 隕石1
    private final int quest_meteorite_sample = 3421; // 隕石サンプル採集
    private final int item_meteorite_sample = 4031117; // 隕石サンプル
    private final String all_collected = "111111";

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 隕石1 ~ 隕石6
                int index = getNpcId() - this.npc_meteorite_1;
                int quest_status = getQuestStatus(this.quest_meteorite_sample);
                String info = getQuestData(this.quest_meteorite_sample);
                if (info.length() != this.all_collected.length()) {
                    info = "000000";
                }
                if (quest_status != 1 || info.equals(this.all_collected) || index < 0 || this.all_collected.length() <= index) {
                    // 翻訳
                    say("宇宙から落ちてきた隕石のようですが、ひどく壊れていて、役に立ちそうなものは何も見つかりません。");
                    return false;
                }
                if (info.charAt(index) == '1') {
                    // 翻訳
                    say("宇宙から落ちてきた隕石のようですが、すでにサンプルが採取されています。別の隕石のサンプルを探してください。");
                    return false;
                }
                if (!canHold(this.item_meteorite_sample, 1)) {
                    // 翻訳
                    say("宇宙から落ちてきた隕石のようです。さらに調べたところ、状態の良いサンプルが採取できましたが、ETCインベントリがいっぱいのためアイテムを入れられませんでした。");
                    return false;
                }
                gainItem(this.item_meteorite_sample, 1);
                setQuestData(this.quest_meteorite_sample, info.substring(0, index) + "1" + info.substring(index + 1));
                // 翻訳
                say("宇宙から落ちてきた隕石のようです。さらに調べたところ、状態の良いサンプルが採取できました..");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
