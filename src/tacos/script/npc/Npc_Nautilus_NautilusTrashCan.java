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
public class Npc_Nautilus_NautilusTrashCan extends NpcScriptMan {

    private final int item_crumpled_letter = 4031839; // クシャクシャの便箋

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章 (適当)
                say("書きかけの手紙だ…大事なものかもしれない！見てみようか？", false, true);
                return true;
            }
            case 1 -> {
                if (checkItem(this.item_crumpled_letter)) {
                    // TODO : 文章 (適当)
                    say("もう拾ってある。もう1枚拾う必要はなさそうだ。");
                    return false;
                }
                if (!canHold(this.item_crumpled_letter, 1)) {
                    return false;
                }
                gainItem(this.item_crumpled_letter, 1);
                // TODO : 文章 (適当)
                say("かろうじて読める…「キリン」と書いてあるようだ。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
