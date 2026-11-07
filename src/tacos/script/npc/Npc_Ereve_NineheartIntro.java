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
public class Npc_Ereve_NineheartIntro extends NpcScriptMan {

    private final int map_ereve = 130000000; // クィーンズロード : エレヴ

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章 (適当)
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("シグナス騎士団の一員になるには、才能、信念、勇気、そして意志の力が必要です…あなたには十分その資格があるようですね。今すぐ騎士団に入りたいのなら、エレヴまでお連れしましょう。今すぐエレヴへ向かいますか？");
                askAccept(nt);
                return true;
            }
            default -> {
                // TODO : 元のマップへ戻す (CYGNUSINTRO)
                warp(this.map_ereve);
                return false;
            }
        }
    }
}
