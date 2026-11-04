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
// Npc.wz script : aqua_taxi2 (JMS131-302), 会話文は lua_script_jp (aqua_taxi2) を利用
public class Npc_Aquarium_DolphinTaxi extends NpcScriptMan {

    private final int map_aquarium = 230000000; // アクアリウム : アクアリウム
    private final int cost_beginner = 1000;
    private final int cost = 10000;

    @Override
    public boolean action(int status, int answer) {
        boolean beginner = getJob() == 0;
        int cost_now = beginner ? this.cost_beginner : this.cost;
        switch (status) {
            case 0 -> {
                // 翻訳
                if (beginner) {
                    askYesNo("今から#b#m" + this.map_aquarium + "##kへ行く？初心者には90%割引の特別チケットがあるよ！料金は#b" + cost_now + " mesos#k.");
                } else {
                    askYesNo("今から#b#m" + this.map_aquarium + "##kへ行く？料金は#b" + cost_now + " mesos#k.");
                }
                return true;
            }
            case 1 -> {
                if (!payMoney(cost_now)) {
                    // 翻訳
                    say("お金が足りないみたいだね…");
                    return false;
                }
                warp(this.map_aquarium);
                return false;
            }
            default -> {
                // 翻訳
                say("うーん…今は忙しくてそれどころじゃない？とにかく、行きたくなったらまた声をかけてね。");
                return false;
            }
        }
    }
}
