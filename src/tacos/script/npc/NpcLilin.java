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
public class NpcLilin extends NpcScriptMan {

    private static final int MENU_TALK = 0;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("どうしましたか？");
                nt.addMenu(MENU_TALK, "あなたと話したいです。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : アランのクエスト, スキルの習得
                say("私があなたと話す理由がありますか？そんなにやすやす話はしません。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
