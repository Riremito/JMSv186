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

import odin.server.Randomizer;

/**
 *
 * @author Riremito
 */
public class Npc_LithHarbor_ExpGachapon extends NpcScriptMan {

    private final int item_ticket = 5220000; // ガシャポンチケット
    private final int[] rewards = {
        2370000, // 兵法書（孫子）
        2370001, // 兵法書（呉子）
        2370002, // 兵法書（尉繚子）
        2370003, // 兵法書（六韜）
        2370004, // 兵法書（三略）
        2370005, // 兵法書（司馬法）
        2370006, // 兵法書（李衛公問対）
        2370007, // 兵法書（孫ピン兵法）
        2370008, // 兵法書（兵法三十六計）
        2370009, // 兵法書（兵法二十四編）
        2370010, // 兵法書（戦国策）
        2370011, // 兵法書（百戦奇略）
        2370012 // 兵法書（心書）
    };
    private static final int MENU_PROB = 0;
    private static final int MENU_USE = 1;
    private static final int MENU_ABOUT = 2;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("何の用ですか。");
                nt.addMenu(MENU_USE, "ガシャポン（兵法書）を利用したいです");
                nt.addMenu(MENU_ABOUT, "兵法書の使用方法について知りたいです");
                nt.addMenu(MENU_PROB, "排出率");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                switch (answer) {
                    case MENU_PROB -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("この「ガシャポン」を1回ご使用いただくと、次のレアアイテムを含むいずれか1つを獲得できます。");
                        nt.add("※購入回数に応じて抽選の確率が変動するものではございません。");
                        nt.add("※全てのアイテムは重複して入手可能です。");
                        for (int reward : this.rewards) {
                            nt.add("#v" + reward + "##t" + reward + "# #b(" + (100 / this.rewards.length) + "%)#k");
                        }
                        say(nt);
                    }
                    case MENU_USE -> {
                        // TODO : ガシャポンチケットを持っていない場合, 獲得時の文章
                        if (!checkItem(this.item_ticket)) {
                            return false;
                        }
                        int reward = this.rewards[Randomizer.nextInt(this.rewards.length)];
                        if (!canHold(reward, 1)) {
                            return false;
                        }
                        removeItem(this.item_ticket, 1);
                        gainItem(reward, 1); // TODO : item effect.
                    }
                    case MENU_ABOUT -> {
                        // TODO : 文章の続き (元のスクリプトの文章が途切れている)
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("#b■兵法書の使用方法について。");
                        nt.add("兵法書アイテムをダブルクリックすると経験値を得る事ができます。");
                        nt.add("");
                        nt.add("■兵法書に設定されている経験値が、兵法書使用時のキャラクターレベルの必要経験値（※１）より多かった場合の動作について。");
                        nt.add("上記の場合、レベルアップするまでは兵法書の経験値を使用しますが、兵法書の経験値が残った場合、画面下の経験値バーに兵法書の残経験値が記録されます。");
                        nt.add("");
                        nt.add("■兵法書の残経験値の確認方法について。");
                        nt.add("経験値バーの通常の色はグリーン色ですが、残経験値はオレンジ...");
                        say(nt);
                    }
                    default -> {
                    }
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
