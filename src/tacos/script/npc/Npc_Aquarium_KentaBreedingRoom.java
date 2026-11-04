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
public class Npc_Aquarium_KentaBreedingRoom extends NpcScriptMan {

    private final int item_research_report = 4031508; // 研究報告書
    private final int item_pheromone = 4031507; // フェロモン
    private final int required_count = 5;
    private final int map_zoo = 230000003; // アクアリウム : 動物園
    private final int map_breeding_room_passage = 923010100; // 飼育室通路
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                if (this.asked) {
                    // 翻訳
                    say("よく考えてから話しかけてね。");
                }
                return false;
            }
            case 0 -> {
                if (getItemCount(this.item_research_report) >= this.required_count && getItemCount(this.item_pheromone) >= this.required_count) {
                    // 翻訳
                    say("うわあ～#b#t" + this.item_research_report + "##kと#b#t" + this.item_pheromone + "##kを5個ずつ集めたんだね！  じゃあ、#m" + this.map_zoo + "#に送ってあげるよ。着いたらもう一度話しかけてね。");
                    warp(this.map_zoo);
                    return false;
                }
                this.asked = true;
                // 翻訳
                askYesNo("残念だけど、#b#t" + this.item_research_report + "##kか#b#t" + this.item_pheromone + "##kが5個ずつ集まっていないよ。クエストをやめて帰る？");
                return true;
            }
            case 1 -> {
                // 翻訳
                say("#m" + this.map_breeding_room_passage + "#へ移動します。");
                warp(this.map_breeding_room_passage);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
