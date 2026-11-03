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
public class NpcWaitingRoomExit extends NpcScriptMan {

    // 控え室
    private final int[] map_waiting_rooms = {
        200000122, // オルビス : 控え室<ルディブリアム行き>
        220000111, // ルディブリアム : 控え室<オルビス行き>
        101000301, // ビクトリアロード : 控え室<オルビス行き>
        200000112, // オルビス : 控え室<エリニア行き>
        240000111, // リプレ : 控え室<オルビス行き>
        200000132, // オルビス : 控え室<リプレ行き>
        200000152, // オルビス : 控え室<アリアント行き>
        260000110 // アリアント : 控え室<オルビス行き>
    };
    // 移動先
    private final int[] map_returns = {
        200000100, // オルビス : オルビスチケット売場
        220000100, // ルディブリアム : チケット売場
        101000300, // ビクトリアロード : エリニアステーション
        200000100, // オルビス : オルビスチケット売場
        240000100, // リプレ : チケット売場
        200000100, // オルビス : オルビスチケット売場
        200000100, // オルビス : オルビスチケット売場 (TODO : luaに記載なし)
        260000100 // アリアント : アリアント乗降場 (TODO : luaに記載なし)
    };
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                if (this.asked) {
                    // 翻訳
                    say("もうすぐ目的地に到着します。ほかの人と話でもしていれば、あっという間に着きますよ。");
                }
                return false;
            }
            case 0 -> {
                this.asked = true;
                // 翻訳
                askYesNo("待合室から出ますか？出ることはできますが、チケットの払い戻しは「できません」。本当にこの部屋から出ますか？");
                return true;
            }
            case 1 -> {
                for (int i = 0; i < this.map_waiting_rooms.length; i++) {
                    if (getMapId() == this.map_waiting_rooms[i]) {
                        warp(this.map_returns[i]);
                        return false;
                    }
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
