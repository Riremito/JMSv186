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

import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class NpcWarriorJobInstructorTest extends NpcScriptMan {

    private final int item_dark_marble = 4031013; // 黒い玉
    private final int item_proof_of_hero = 4031012; // 英雄の証
    private final int map_exit = 102020300; // ぺリオン : 西の岩山4
    private final int required_quantity = 30;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (!getPlayer().haveItem(this.item_dark_marble, this.required_quantity)) {
                    // TODO : 文章 (コメントアウトされた旧スクリプトの文章)
                    NpcScriptTag nt = new NpcScriptTag();
                    nt.add("You will have to collect me #b" + this.required_quantity + " #t" + this.item_dark_marble + "##k. Good luck.");
                    say(nt);
                    return false;
                }
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("お！#b#z" + this.item_dark_marble + "##k" + this.required_quantity + "個を全部集めたのか！よくやった！よし、君は試験に合格だ。その証拠に#b#z" + this.item_proof_of_hero + "##kをあげるよ。このアイテムを持ってペリオンに帰ってみなさい。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                // TODO : クエスト (100004, 100005) の更新, アイテム欄の空き確認
                int quantity = getPlayer().getItemQuantity(this.item_dark_marble, false);
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_dark_marble, quantity, true, false);
                MapleInventoryManipulator.addById(getPlayer(), this.item_proof_of_hero, 1); // TODO : item effect.
                warp(this.map_exit); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
