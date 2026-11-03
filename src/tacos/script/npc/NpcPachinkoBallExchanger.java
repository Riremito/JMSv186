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
public class NpcPachinkoBallExchanger extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("パチンコ玉を購入するためにポイントショップへ行きますか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                getPlayer().EnterPointShop();
                return false;
            }
            case -1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("またのご利用をお待ちしております。");
                say(nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
