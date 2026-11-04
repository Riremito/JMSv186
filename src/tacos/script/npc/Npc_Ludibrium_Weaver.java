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

import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class Npc_Ludibrium_Weaver extends NpcScriptMan {

    private final int item_weaver_letter = 4031128; // ウィーバーの手紙
    private final int npc_neru = 2040033; // ネル
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                if (this.asked) {
                    // 翻訳
                    say("うーん…今は#G忙しい:忙しい#のかい？後でやりたくなったら、また来て話しかけてくれ。");
                }
                return false;
            }
            case 0 -> {
                if (checkItem(this.item_weaver_letter)) {
                    // 翻訳
                    say("この手紙を持って、ペットと一緒に障害物を飛び越え、弟の" + getNpcName(this.npc_neru) + "に手紙を届けてくれ。手紙を届けたら、君のペットにいいことがあるよ。");
                    return false;
                }
                this.asked = true;
                // 翻訳
                askYesNo("ここはペットを散歩させられる道だよ。普通に歩いてもいいし、ペットに障害物を飛び越える訓練をさせてもいい。まだペットとあまり親しくないなら、ちょっと大変かもしれないね。言うことをあまり聞いてくれないかもしれないから…それで…どうだい？ペットを訓練してみるかい？");
                return true;
            }
            case 1 -> {
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_weaver_letter, 1, "")) {
                    // 翻訳
                    say("ETCインベントリがいっぱいだよ！インベントリに空きがないと手紙を渡せないんだ。インベントリに空きを作ってから、もう一度話しかけてくれ。");
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), this.item_weaver_letter, (short) 1);
                // 翻訳
                say("よし、これが手紙だ。手紙なしで行かせたら、弟は君が誰だかわからないからね。ペットと一緒に障害物を越えて頂上まで行ったら、" + getNpcName(this.npc_neru) + "に話しかけて手紙を渡してくれ。障害物を越えるときにペットに気を配っていれば、難しくはないよ。頑張って！");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
