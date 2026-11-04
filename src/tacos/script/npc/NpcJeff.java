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

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
public class NpcJeff extends NpcScriptMan {

    private final int required_level = 50;
    private final int map_dungeon = 211040300; // エルナス : 険しき絶壁1
    private final int map_freeze = 921100100; // 氷の谷
    private final int quest_job_4th = 6263; // 4次転職
    private final int item_hammer = 4031450; // オリハルコンハンマー
    private boolean asked = false;
    private boolean menu = false;

    @Override
    public boolean action(int status, int answer) {
        if (status == 0 && !this.menu && !this.asked && getPlayer().getQuestStatus(this.quest_job_4th) == 2 && 0 < getPlayer().getItemQuantity(this.item_hammer, false)) {
            this.menu = true;
            // 翻訳
            NpcScriptTag nt = new NpcScriptTag();
            nt.add("お前…本当に#b#t" + this.item_hammer + "##kを作ったのか。ボーゲンほど品質を保証できる者はいない。よし、#m" + this.map_freeze + "#に入れてやろう。\r\n#b#L0# はい。#m" + this.map_freeze + "#に連れて行ってください。#l\r\n#L1# エルナス山に入りたいです。#l", false);
            askMenu(nt);
            return true;
        }
        if (this.menu) {
            if (status == 1 && !this.asked) {
                if (answer == 0) {
                    if (enterFieldSet("S4freeze", 0) != TacosFieldSet.ENTER_SUCCESS) {
                        // 翻訳
                        say("ほかのキャラクターがクエスト中だ。後で話しかけてくれ。");
                    }
                    return false;
                }
                if (answer != 1) {
                    return false;
                }
            }
            // メニューの分だけずらす
            status--;
        }
        // いいえを選択した
        if (status == 0 && this.asked) {
            // 翻訳
            say("レベルが高くても、あそこに入るのは難しい。だが、気が変わったら話しかけてくれ。結局のところ、この場所を守るのが俺の務めだからな。");
            return false;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                say("おい、ここからもっと遠くへ行きたいようだな。だが、向こうには凶暴で危険なモンスターがそこら中にいる。準備ができていると思っていても、気をつけるんだ。昔、この町の勇敢な男たちが町を脅かす者を倒しに向かったが、二度と戻ってこなかった…", false, true);
                return true;
            }
            case 1 -> {
                if (getPlayer().getLevel() < this.required_level) {
                    // 翻訳
                    say("入ろうと考えているなら、考え直すことをすすめる。だが、本当に入りたいなら…中で生き延びられるほど強い者だけが許される。もう誰かが死ぬのは見たくないんだ。どれどれ…ふむ…お前はまだレベル" + this.required_level + "に達していない。入れるわけにはいかない、あきらめろ。");
                    return false;
                }
                this.asked = true;
                // 翻訳
                askYesNo("入ろうと考えているなら、考え直すことをすすめる。だが、本当に入りたいなら…中で生き延びられるほど強い者だけが許される。もう誰かが死ぬのは見たくないんだ。どれどれ…ふむ…！お前はなかなか強そうだな。よし、入るか？");
                return true;
            }
            case 2 -> {
                warp(this.map_dungeon, "under00");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
