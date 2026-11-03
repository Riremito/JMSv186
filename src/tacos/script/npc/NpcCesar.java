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
public class NpcCesar extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("何のようだ？");
                nt.addMenu(0, "一番目闘技場(空室)");
                nt.addMenu(1, "二番目闘技場(空室)");
                nt.addMenu(2, "三番目闘技場(空室)");
                nt.addMenu(3, "闘技大会について知りたいです。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : 闘技場への入場, 闘技大会の説明
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
