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
public class NpcAgatha extends NpcScriptMan {

    private static final int MENU_ELLINIA = 0;
    private static final int MENU_LUDIBRIUM = 1;
    private static final int MENU_LEAFRE = 2;
    private static final int MENU_ARIANT = 3;
    // レベル30以上
    private final int[] item_tickets = {
        4031047, // エリニア行きのチケット(大人用)
        4031074, // ルディブリアム行チケット(大人用)
        4031331, // リプレ行きのチケット(大人用)
        4031576 // アリアント行きチケット(一般)
    };
    private final int[] costs = {
        5000,
        6000,
        30000,
        0 // TODO : アリアント行きのチケットの値段が不明
    };
    // レベル30未満
    private final int[] item_tickets_child = {
        4031046, // エリニア行きのチケット(子供用)
        4031073, // ルディブリアム行チケット(子供用)
        4031330, // リプレ行きのチケット(子供用)
        4031575 // アリアント行きチケット(ビギナー)
    };
    private final int[] costs_child = {
        1000,
        2000,
        10000,
        0 // TODO : アリアント行きのチケットの値段が不明
    };
    // 翻訳
    private final String[] infos = {
        "ビクトリアアイランドのエリニア行きの便は、毎時0分から15分おきに出発し、",
        "ルディブリアム行きの便は、毎時0分から10分おきに出発し、",
        "リプレ近くのミナルの森行きの便は、毎時0分から10分おきに出発し、",
        "" // TODO : アリアント行きの便の説明が不明
    };
    private int ticket = 0;
    private int cost = 0;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 購入確認でいいえを選択した
                if (this.ticket != 0) {
                    // 翻訳
                    say("ここに何か用事があるんですよね？");
                    return false;
                }
                if (getPlayer().getLevel() < 10) {
                    // 翻訳
                    say("あなたのレベルはまだ低すぎるようです。安全上の理由から、レベル10未満の方はこの航路をご利用いただけません。");
                    return false;
                }
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("私は各地域に行く船のチケットを売っています。どのチケットを購入しますか？");
                nt.addMenu(MENU_ELLINIA, "ビクトリアアイランドのエリニア");
                nt.addMenu(MENU_LUDIBRIUM, "ルディブリアム城");
                nt.addMenu(MENU_LEAFRE, "リプレ村");
                nt.addMenu(MENU_ARIANT, "アリアント");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.item_tickets.length <= answer) {
                    return false;
                }
                if (answer == MENU_ARIANT) {
                    // TODO : アリアント行きのチケットの購入
                    return false;
                }
                boolean child = getPlayer().getLevel() < 30;
                this.ticket = child ? this.item_tickets_child[answer] : this.item_tickets[answer];
                this.cost = child ? this.costs_child[answer] : this.costs[answer];
                // 翻訳
                askYesNo(this.infos[answer] + "料金は#b" + this.cost + "メル#kです。#b#t" + this.ticket + "##kを購入しますか？");
                return true;
            }
            case 2 -> {
                if (getPlayer().getMeso() < this.cost || !MapleInventoryManipulator.checkSpace(getPlayer(), this.ticket, 1, "")) {
                    // 翻訳
                    say("本当に#b" + this.cost + "メル#kをお持ちですか？お持ちでしたら、ETCインベントリがいっぱいでないか確認してみてください。");
                    return false;
                }
                payMoney(this.cost);
                MapleInventoryManipulator.addById(getPlayer(), this.ticket, (short) 1);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
