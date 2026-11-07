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
public class Npc_Magatia_Carson extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 続きの会話 (アルカドノの説明など)
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("錬金術、そして錬金術師…どちらも重要だが、最も重要なのは錬金術の歴史を担ってきたこのマガティアという偉大な村だ。マガティアの歴史はこれからも守っていかなければならない。君にその力があるか？");
                say(nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
