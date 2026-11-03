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
public class NpcMarTheFairy extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : ペットの復活 (生命の水と生命の呪文書の確認と消費, 人形になったペットの復活)
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#b生命の水#kと#b生命の呪文書#kさえあればその人形を何とか以前のように動く動物にすることができるかもしれません、どうでしょう…アイテムを集めてきますか？アイテムさえ持って来てくださったら喜んでその人形を元どおりにしてあげますよ…");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
