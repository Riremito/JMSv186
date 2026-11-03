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
public class NpcRain extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ここはメイプルアイランドの玄関口#bアムホスト#kっていう小さな町よ。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("キミ、いつまでここにいるの？　あたしも行けるものならここから船で#bビクトリアアイランド#kに行きたいんだけどさ……色々とあるのよ。");
                say(nt, true, true);
                return true;
            }
            case 2 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ビクトリアアイランドでは職業に就けるそうよ。戦士になるには#bペリオン#kだったかな？　その村は遺跡とかあって、原住民が歩きまわってるんだって。ま、あたしもサウスペリ原住民みたいなもんだけどさ。あーあ、なんか、たるぅ。");
                say(nt, true, false);
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
