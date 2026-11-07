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
// Npc.wz script : bari (JMS131), lua の処理を利用
public class Npc_MapleIsland_Bari extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        if (hasPages()) {
            return nextPage();
        }
        // 翻訳
        return sayPages("この町は#b#m60000##kといって、メイプルアイランドの東にあるんだ。転職して、もっと強いモンスターと戦いたくないかい？それなら、船に乗って#bビクトリアアイランド#kへ向かうといい。",
                // 翻訳
                "150メル必要だけど、そんなに大金じゃないよ…島のあちこちにいるモンスターを倒せば、それくらいは稼げるさ。町のあちこちにあるお店でアイテムを売ってもいいしね。");
    }
}
