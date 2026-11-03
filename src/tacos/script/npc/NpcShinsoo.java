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
public class NpcShinsoo extends NpcScriptMan {

    private final int item_blessing_of_shinsoo = 2022458; // 神獣の祝福

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 神獣の祝福 (item_blessing_of_shinsoo) の効果を付与する
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("メイプルワールドの平和を守るため、より強くなるのだ…。");
                say(nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
