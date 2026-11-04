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
// Npc.wz script : Event09 (JMS131-302), 会話文は lua_script_jp (Event09) を利用
public class NpcHarry extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_MENU = 1;
    private static final int NODE_EXIT = 2;
    private static final int NODE_BUY = 3;
    private final int map_event_exit = 109050001; // 隠れ道 : イベント出口
    private final int item_stick = 1322005; // 棒
    private final int price_stick = 1;
    private int node = NODE_START;
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        // 翻訳
        switch (this.node) {
            case NODE_START -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("いやあ…暑いね！！！何か手伝おうか？\r\n#b#L0# イベントのゲームから出る#l\r\n#b#L1# 武器を買う。(#t" + this.item_stick + "# 1メル)#l", false);
                askMenu(nt);
                this.node = NODE_MENU;
                return true;
            }
            case NODE_MENU -> {
                if (answer == 0) {
                    askYesNo("今出ると、今後24時間はこのイベントに参加できなくなるよ。本当に出る？");
                    this.node = NODE_EXIT;
                    return true;
                }
                if (answer == 1) {
                    askYesNo("初心者向けの#t" + this.item_stick + "#は1メルだよ。どう？買う？");
                    this.node = NODE_BUY;
                    return true;
                }
                return false;
            }
            case NODE_EXIT -> {
                if (!ok) {
                    say("よし。諦めずに全力で頑張って。全力で頑張れば、ご褒美がもらえるよ！");
                    return false;
                }
                warp(this.map_event_exit);
                return false;
            }
            case NODE_BUY -> {
                if (!ok) {
                    say("攻撃力の高い武器より、攻撃速度の速い武器の方が大事なんだ。必要になったら、また来てね。");
                    return false;
                }
                if (!exchange(-this.price_stick, this.item_stick, 1)) {
                    say("本当に空きスロットはある？それとも1メル持ってる？確認してみて。");
                    return false;
                }
                say("#t" + this.item_stick + "#は手に入れた？幸運を祈ってるよ！");
                return false;
            }
            default -> {
                return false;
            }
        }
    }
}
