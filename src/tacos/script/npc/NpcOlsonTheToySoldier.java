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
public class NpcOlsonTheToySoldier extends NpcScriptMan {

    private final int quest_dollhouse = 3230; // 人形の家
    private final int npc_right = 2040001; // ライトくん
    private final int npc_mark = 2040028; // マークくん
    private final int item_walnut = 4031093; // おいしいクルミ
    private final int item_pendulum = 4031094; // 時計錘
    private final int map_ludibrium = 220000000; // ルディブリアム : ルディブリアム
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        int quest_status = getPlayer().getQuestStatus(this.quest_dollhouse);
        if (quest_status == 2) {
            NpcScriptTag nt = new NpcScriptTag();
            nt.add("Thanks to #h #, we got the #b#t" + this.item_pendulum + "##k back and destroyed the monster from the other dimension. Thankfully we haven't found one like that since. I can't thank you enough for helping us out. Hope you enjoy your stay here at " + getMapName(this.map_ludibrium) + "!");
            say(nt);
            return false;
        }
        if (quest_status != 1) {
            NpcScriptTag nt = new NpcScriptTag();
            nt.add("僕達はこの部屋に誰でも入らないように警備をしています。なぜ中にはいれないか、中に何があるかはまだ言えません。僕に用がないんでしたら、もう仕事に戻りますんで、では、さよなら。");
            say(nt);
            return false;
        }
        // TODO : 英語の文章
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("Hmmm...I've heard a lot about you through #b" + getNpcName(this.npc_right) + "#k. You got him a bunch of #b#t" + this.item_walnut + "##k so he can fight off boredom at work. Well ... alright, then. There's a dangerous, dangerous monster inside. I want to ask you for help in regards to locating it. Would you like to help me out?");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                if (this.asked) {
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("I see. Please talk to me when you're ready to take on this task. I advise you not to take too much time, through, for the monster may turn into something totally different. We have to act like we don't know anything.");
                    say(nt);
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("Thank you so much. Actually, #b#t" + this.item_walnut + "##k asked you to get #b" + getNpcName(this.npc_right) + "#k as a way of testing your abilities to see if you can handle this, so don't think of it as a random request. I think someone like you can handle adversity well.");
                say(nt, false, true);
                return true;
            }
            case 2 -> {
                this.asked = true;
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("A while ago, a monster came here from another dimension thanks to a crack in dimensions, and it stole the pendulum of the clock. It hid itself inside the room over there camouflaged as a dollhouse. It all looks the same to me, so there's no way to find it. Would you help us locate it?");
                askYesNo(nt);
                return true;
            }
            case 3 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("Alright! I'll take you to a room, where you'll find a number of dollhouses all over the place. One of them will look slightly different from the others. Your job is to locate it and break its door. If you break a wrong dollhouse, however, you'll be sent out here without warning, so please be careful on that.");
                say(nt, false, true);
                return true;
            }
            case 4 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("You'll also find monsters in there, and they have gotten so powerful thanks to the monster from the other dimension that you won't able to take them down. Please find #b#t" + this.item_pendulum + "##k within the time limit and then notify #b" + getNpcName(this.npc_mark) + "#k, who should be inside. Let's get this started!");
                say(nt, true, true);
                return true;
            }
            case 5 -> {
                // TODO : DollHouse イベントの開始 (1人ずつ入場), おいしいクルミ (item_walnut) の回収
                // Someone else must be inside looking for the dollhouse. Unfortunately I can only let in one person at a time, so please wait for your turn.
                return false;
            }
            case -1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("I see. It's very understandable, considering the fact that you'll be facing a very dangerous monster inside. If you ever feel a change of heart, then please come talk to me. I sure can use help from someone like you.");
                say(nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
