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
// Npc.wz script : guildquest1_board (JMS131-302), 会話文は lua_script_jp (guildquest1_board) を利用
public class NpcGuildBoard extends NpcScriptMan {

    private final int map_base_camp = 101030104; // ぺリオン : 遺跡発掘ベースキャンプ
    private final int map_excavation_site = 990000000; // 遺跡発掘現場

    @Override
    public boolean action(int status, int answer) {
        int map_id = getMapId();
        if (map_id == this.map_base_camp) {
            // 翻訳
            say("<お知らせ> \r 勇気と信頼にあふれたギルドですか？それならギルドクエストに挑戦してみましょう！\r \r #b参加条件 :#k\r 1. ギルドは6人以上で構成されていること！\r 2. ギルドのリーダーは、ギルドマスターと副マスターであること！\r 3. 参加しているギルドメンバーが6人未満になった場合、またはリーダーが早期終了を決めた場合、ギルドクエストは途中で終了することがあります！");
            return false;
        }
        if (map_id == this.map_excavation_site) {
            // 翻訳
            say("#r<警告>#k 危険！シャレニアン城には邪悪な力が渦巻いています！\r 1. 現在シャレニアン城には邪悪な力が渦巻いているので、近づきすぎると命の危険があります。\r 2. 旅を早めに終えたい場合は、帰還の石を使ってください！");
            return false;
        }
        return false;
    }
}
