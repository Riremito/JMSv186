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

import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : PartyAmoria_play2 (BMS8-24, EMS70-76, GMS92-95), 会話文は lua_script_jp (PartyAmoria_play2) を利用
// lua のモンスター (2101057) は存在しないため GMS のモンスターに置き換え
public class Npc_Amoria_GlimmerMan extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_STAGE1_GUIDE = 1;
    private static final int NODE_EXIT = 2;
    private final String field_set_name = "PartyAmoria";
    private final int map_stage1 = 670010200; // Hidden Street : Stage 1 - Magik Mirror
    private final int map_exit = 670011000; // Hidden Street : Exit
    private final int map_training = 670010000; // Hidden Street : Amos' Training Ground
    private final int mob_indigo_eye = 9400533; // Indigo Eye PQ
    private final int mob_crystal_boar = 9400534; // Crystal Boar PQ
    private final int mob_fairy = 9400535; // Magik Fiarry A PQ
    private final int mob_fairy_boss = 9400518; // Magik Fierry B (TODO : lua の 2101057)
    private final int item_shard = 4031595; // Magik Mirror Shard
    private final int item_hammer = 4031596; // Wing Hammer
    // lua の takeawayitem (クエストアイテム)
    private final int[] items_quest = {
        4031592, // Entrance Ticket
        4031593, // Lip Lock Key
        4031594, // Geist Fang
        4031595, // Magik Mirror Shard
        4031596, // Wing Hammer
        4031597 // Cupid Code Piece
    };
    private int node = NODE_START;

    @Override
    public boolean action(int status, int answer) {
        switch (this.node) {
            case NODE_STAGE1_GUIDE -> {
                if (status == 1) {
                    // 翻訳
                    say("さて、その鏡はウィングハンマーでしか壊せない。モンスターを全部倒すと、マジックフェアリーが現れる。そいつがウィングハンマーを落とすんだ。それを鏡の上に落として、かけらを俺のところに持ってきてくれ。ポータルは性別によって違うから、女性キャラクターが通れても男性キャラクターは通れないかもしれないことに注意してくれ。さあ、次の場所へ行けるポータルを探してくれ。行け！", true, false);
                    TacosFieldSet field_set = getFieldSet(this.field_set_name);
                    if (field_set != null) {
                        field_set.setVar("stage1up", "ing");
                    }
                }
                return false;
            }
            case NODE_EXIT -> {
                if (status == 1) {
                    warp(this.map_training, "st00");
                    for (int item : this.items_quest) {
                        removeItemAll(item);
                    }
                }
                return false;
            }
            default -> {
            }
        }
        if (status != 0) {
            return false;
        }
        // アモリアの挑戦 出口
        if (getMapId() == this.map_exit) {
            // 翻訳
            say("今すぐこのエリアから出してあげよう。", false, true);
            this.node = NODE_EXIT;
            return true;
        }
        if (getMapId() != this.map_stage1) {
            return false;
        }
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        // ステージ1 - マジックミラー
        String val = field_set.getVar("stage1up");
        // lua では end の場合も最初の説明に戻るが、else if として扱う
        if (val.equals("end")) {
            // 翻訳
            say("よし、それじゃあアモスに会いに上へ送り返してやろう！");
            transferParty(this.map_stage1, "st01");
            enablePortal("go00", false);
            enablePortal("go01", false);
            enablePortal("go02", false);
            return false;
        }
        if (!val.equals("ing")) {
            // 翻訳
            say("やあ。旅の途中で手に入れた特別な鏡が部屋の真ん中にある。モンスターミラーと呼ばれていて、倒すのと同じくらいの速さで敵を生み出すんだ。次の部屋へ進むには、鏡を壊す方法を見つけてくれ。幸運を祈る！", false, true);
            this.node = NODE_STAGE1_GUIDE;
            return true;
        }
        if (!isPartyBoss()) {
            // 翻訳
            say("おい、パーティーリーダーに話しかけてほしいんだ。ほかの者じゃだめだ。");
            return false;
        }
        if (field_set.getVar("stage1Boss").equals("1")) {
            if (getItemCount(this.item_shard) < 1) {
                // 翻訳
                say("へへ、俺の鏡はちゃんと動いているぞ。まだ終わってないな…");
                return false;
            }
            if (!exchange(0, this.item_shard, -1)) {
                // 翻訳
                say("すまないが、インベントリにアイテムを入れるのに問題が起きているようだ。後でまた来てくれ。");
                return false;
            }
            // 翻訳
            say("おお…マジックミラーを砕いたんだな。ありがとう。さあ、アモスに会いに行け。そうすれば出してくれるだろう。");
            effectScreen("quest/party/clear");
            effectSound("Party1/Clear");
            field_set.setVar("stage1_clear", "1");
            field_set.setVar("stage1up", "end");
            field_set.incExpAll(2000);
            return false;
        }
        int mob_count = getMobCount(getMapId(), this.mob_indigo_eye) + getMobCount(getMapId(), this.mob_crystal_boar) + getMobCount(getMapId(), this.mob_fairy);
        if (mob_count != 0 || 0 < getItemCount(this.item_hammer) || 0 < getItemCount(this.item_shard)) {
            // 翻訳
            say("おい、まだモンスターが残っているぞ。");
            return false;
        }
        // 翻訳
        say("よくやったな、友よ。鏡が召喚したモンスターをマップから一掃してくれた。だが、まだ最後のモンスターが1体残っていて、そいつは特別だ。最後の瞬間まで全力を尽くせ！");
        summonMob(1000, 1340, this.mob_fairy_boss);
        field_set.setVar("stage1Boss", "1");
        // 翻訳
        field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "特別なマジックフェアリーがマップのどこかに召喚された。");
        return false;
    }
}
