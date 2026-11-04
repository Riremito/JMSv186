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

import odin.server.quest.MapleQuest;

/**
 *
 * @author Riremito
 */
public class Npc_Ludibrium_MarkTheToySoldier extends NpcScriptMan {

    private final int quest_doll_house = 3230; // 人形の家
    private final int item_pendulum = 4031145; // 時計ネジ
    private final int item_blue_pill = 2000010; // 青い丸薬
    private final int blue_pill_count = 100;
    private final int reward_exp = 2400;
    private final int map_eos_tower_100f = 221024400; // ルディブリアム : エオス塔100階
    private final int MENU_EXIT = 0;
    private boolean clear = false;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (this.asked) {
                    // 翻訳
                    say("残ってくれると思っていたよ！始めたことを最後までやり遂げることが大切なんだ！さあ、ほかと違うドールハウスを見つけて壊し、#b#t" + this.item_pendulum + "##kを持ってきてくれ！");
                    return false;
                }
                if (getQuestStatus(this.quest_doll_house) != 1) {
                    // 翻訳
                    say("何だこれは…別の次元のモンスターが隠れているから、この部屋への立ち入りは禁止しているんだ。どうやってここに来たのかわからないが、すぐに出て行ってもらわないと。この部屋の中はとても危険なんだ。");
                    warp(this.map_eos_tower_100f);
                    return false;
                }
                if (checkItem(this.item_pendulum)) {
                    this.clear = true;
                    // 翻訳
                    say("おお！すごい！違うドールハウスを見つけて、#b#t" + this.item_pendulum + "##kを手に入れたんだね！見事な仕事ぶりだ！！これでルディブリアムの時計塔がまた動くようになる！手伝ってくれてありがとう。ささやかだが、お礼を受け取ってくれ。でも、受け取る前に、消費インベントリがいっぱいでないか確認してくれ。", false, true);
                    return true;
                }
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("やあ！私は#b" + getNpcName() + "#k、この部屋を守る担当だ。中にはドールハウスがたくさんあって、その中にほかと少し違って見えるものがあるはずだ。君の仕事は、それを見つけて扉を壊し、ルディブリアムの時計塔に欠かせない部品である#b#t" + this.item_pendulum + "##kを見つけることだ。制限時間があるし、間違ったドールハウスを壊すとここに戻されてしまうから、気をつけてくれ。");
                nt.addMenu(MENU_EXIT, "ここから出たい。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (this.clear) {
                    if (!checkItem(this.item_pendulum) || !canHold(this.item_blue_pill, this.blue_pill_count)) {
                        // 翻訳
                        say("本当に#b#t" + this.item_pendulum + "##kを1個持っているかい？確かなら、消費インベントリを確認してくれ。いっぱいかもしれないぞ！！");
                        return false;
                    }
                    removeItem(this.item_pendulum, 1);
                    gainItem(this.item_blue_pill, this.blue_pill_count);
                    gainExp(this.reward_exp);
                    MapleQuest.getInstance(this.quest_doll_house).forceComplete(getPlayer(), getNpcId());
                    // 翻訳
                    say("どうだい？私があげた#b#t" + this.item_blue_pill + "#" + this.blue_pill_count + "個#kは気に入ったかい？手伝ってくれて本当にありがとう。君の英雄的な働きのおかげで時計塔はまた動くし、別の次元のモンスターもいなくなったようだ。では、外に出してあげよう。またどこかで会おう！", false, true);
                    return true;
                }
                if (answer != MENU_EXIT) {
                    return false;
                }
                this.asked = true;
                // 翻訳
                askYesNo("本当にもう諦めるのかい？わかった…でも、次にここに来たときは、ドールハウスの位置が入れ替わっていて、また一つひとつ注意深く調べなければならなくなるよ。どうする？それでもここから出るかい？");
                return true;
            }
            case 2 -> {
                warp(this.map_eos_tower_100f, "q000");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
