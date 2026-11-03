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
public class NpcGoldCompass extends NpcScriptMan {

    private final int map_free_market = 910000000; // フリーマーケット入口
    private final int item_compass = 2430007; // 文字盤のないコンパス
    private final int item_gold_compass = 2430008; // ゴールドコンパス
    private final int[] item_ids = {
        2430007, // 文字盤のないコンパス
        3994102, // コンパス用N
        3994103, // コンパス用E
        3994104, // コンパス用W
        3994105 // コンパス用S
    };
    private final int[] item_counts = {
        1,
        20,
        20,
        20,
        20
    };
    private final int[] item_prices = {
        3000000,
        100000,
        100000,
        100000,
        100000
    };

    @Override
    public boolean action(int status, int answer) {
        // フリーマーケットのイベント
        if (getMapId() != this.map_free_market) {
            return false;
        }
        // TODO : 英語の文章
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("Seriously, how is this world going to function without moi, the great Cassandra? I have yet to spend a day free of worries about this great world. What am I talking about, you ask?");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("Apparently, the treasure storage of the richest man in the world of Maple, Gold Richie, has been destroyed by this herd of pigs eating up all his treasures, and he wants this taken care of right now. #b#h0#!#k Well, I don't find you too trustworthy, but I can't find anyone else willing to help, so... will you do it? Who knows, you might end up taking one of his treasures home... but make sure your 'USE' inventory has at least one slot available!");
                say(nt, true, true);
                return true;
            }
            case 2 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("The only thing I have right now is #t" + this.item_compass + "#, but you can find the following items through a small fee: #b#t" + this.item_ids[1] + "#, #t" + this.item_ids[2] + "#, #t" + this.item_ids[3] + "#, and #t" + this.item_ids[4] + "##k. Once you gather them all up, and double-click the #b#t" + this.item_compass + "##k that I gave you, you'll be able to build the #b#t" + this.item_gold_compass + "##k that'll lead you to Gold Richie's Treasure Storage.");
                say(nt, true, true);
                return true;
            }
            case 3 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("What do you want now? ");
                for (int i = 0; i < this.item_ids.length; i++) {
                    nt.add("#b#L" + i + "##t" + this.item_ids[i] + "#" + (this.item_counts[i] > 1 ? " x" + this.item_counts[i] : "") + " for " + this.item_prices[i] + " mesos#l");
                }
                askMenu(nt);
                return true;
            }
            case 4 -> {
                if (answer < 0 || this.item_ids.length <= answer) {
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_ids[answer], this.item_counts[answer], "") || !payMoney(this.item_prices[answer])) {
                    say("You arn't gonna fool me are you?");
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), this.item_ids[answer], (short) this.item_counts[answer]); // TODO : item effect.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
