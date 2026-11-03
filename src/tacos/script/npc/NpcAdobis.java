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
public class NpcAdobis extends NpcScriptMan {

    private final int item_eye_of_fire = 4001017; // 火の目
    private final int map_zakum_altar = 280030000; // ジャクムダンジョン : ジャクムの祭壇

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("よし、君たちは十分にできそうだな。どの段階に挑戦してみるんだ？");
                nt.addMenu(0, "閉鉱洞窟の調査に行く。(1段階)");
                nt.addMenu(1, "ジャクムダンジョンを探査する。(2段階)");
                nt.addMenu(2, "製錬を要請する。(3段階)");
                nt.addMenu(3, "クエストに対する説明を聞く。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : 1段階, 2段階, 3段階, クエストの説明
                // TODO : 火の目 (item_eye_of_fire) を渡して map_zakum_altar に移動するデバッグモード
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
