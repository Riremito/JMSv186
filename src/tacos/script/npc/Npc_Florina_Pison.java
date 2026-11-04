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

import odin.server.maps.SavedLocationType;

/**
 *
 * @author Riremito
 */
public class Npc_Florina_Pison extends NpcScriptMan {

    private final int map_florina_beach = 110000000; // フロリナ : ビーチ
    private final int map_lith_harbor = 104000000; // ビクトリアアイランド : 港口
    private final int MENU_RETURN = 0;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        // 記録したマップがない場合は港口へ戻す
        int map_id = getPlayer().getSavedLocation(SavedLocationType.FLORINA);
        if (map_id < 0) {
            map_id = this.map_lith_harbor;
        }
        switch (status) {
            case 0 -> {
                if (this.asked) {
                    // 翻訳
                    say("ここで何か用事があるんだろう。#m" + map_id + "#で少し休んでいくのも悪くないさ。俺なんか、ここが気に入りすぎて住み着いちまいそうだよ。ははは…まあとにかく、戻りたくなったら話しかけてくれ。");
                    return false;
                }
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#b#m" + this.map_florina_beach + "##kから出たいのかい？それなら、#b#m" + map_id + "##kまで送ってあげるよ。");
                nt.add("");
                nt.addMenu(MENU_RETURN, "今すぐ戻りたい。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer != MENU_RETURN) {
                    return false;
                }
                this.asked = true;
                // 原文ママ
                askYesNo("#b#m" + map_id + "##kに戻りますか。");
                return true;
            }
            case 2 -> {
                getPlayer().clearSavedLocation(SavedLocationType.FLORINA);
                if (map_id == this.map_lith_harbor) {
                    warp(map_id, "set00");
                    return false;
                }
                warp(map_id);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
