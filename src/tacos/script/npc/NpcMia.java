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
// Npc.wz script : ludiEvent (JMS131-147), 会話文は lua_script_jp (ludiEvent) を利用
// 期間の確認は削除
public class NpcMia extends NpcScriptMan {

    private static final int MODE_FIRST = 0;
    private static final int MODE_EXCHANGE = 1;
    private final int item_material1 = 4000106; // チャックマの綿塊
    private final int item_material2 = 4000095; // ホワイトチューの罠
    private final int item_reward1 = 2000010; // 青い丸薬
    private final int item_reward2 = 2000009; // 白い丸薬
    private final int quest_event = 9000300; // 感謝祭イベント
    private final int material_count = 20;
    private final int reward_count = 150;
    private final int reward_exp = 2000;
    private int mode = MODE_FIRST;

    @Override
    public boolean action(int status, int answer) {
        // 翻訳 (lua の会話文は仮の文章のまま)
        if (status == 0) {
            String val = getQuestData(this.quest_event);
            if (val.isEmpty()) {
                setQuestData(this.quest_event, "s");
                this.mode = MODE_FIRST;
                say("感謝祭", false, true);
                return true;
            }
            if (val.equals("s")) {
                if (getItemCount(this.item_material1) < this.material_count || getItemCount(this.item_material2) < this.material_count) {
                    say("感謝祭。");
                    return false;
                }
                this.mode = MODE_EXCHANGE;
                say("感謝祭～", false, true);
                return true;
            }
            say("感謝祭～");
            return false;
        }
        if (status != 1) {
            return false;
        }
        if (this.mode == MODE_FIRST) {
            say("感謝祭。", true, false);
            return false;
        }
        if (!exchange(0, this.item_material1, -this.material_count, this.item_material2, -this.material_count, this.item_reward1, this.reward_count, this.item_reward2, this.reward_count)) {
            say("感謝祭。");
            return false;
        }
        setQuestData(this.quest_event, "end");
        getPlayer().gainExp(this.reward_exp, true, true, true);
        // TODO : lua の logEvent は未実装
        say("感謝祭～");
        return false;
    }
}
