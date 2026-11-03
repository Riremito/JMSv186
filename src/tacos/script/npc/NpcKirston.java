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
public class NpcKirston extends NpcScriptMan {

    private final int map_twilight = 270050100; // 神殿の奥 : 神々の黄昏
    private final int reactor_pink_bean = 2709000; // ピンクビーン召喚

    @Override
    public boolean action(int status, int answer) {
        // ピンクビーン召喚
        switch (status) {
            case 0 -> {
                // TODO : 本来は askAcceptDecline
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("女神の鏡さえあれば…もう一度暗黒の魔法使いを呼び出すことができる！　…");
                nt.add("お、おかしい…どうして暗黒の魔法使いを呼び出さないんだ？　この気はなんだ？　暗黒の魔法使いとは全く違う…ウワアアアッ！");
                nt.add("#b(キルストンの肩に手をかける)");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                // TODO : 神々の黄昏 (map_twilight) のキルストンの削除とリアクター (reactor_pink_bean) の起動
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
