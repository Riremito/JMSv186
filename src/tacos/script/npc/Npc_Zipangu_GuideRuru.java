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
// Npc.wz script : party_ludimaze_fail (JMS131-302), 会話文は lua_script_jp (party_ludimaze_fail) を利用
public class Npc_Zipangu_GuideRuru extends NpcScriptMan {

    private final int map_ludibrium = 220000000; // ルディブリアム : ルディブリアム
    private final int item_coupon = 4001106; // 迷路クーポン

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 翻訳
                say("残念だったね。また挑戦してね！");
                return true;
            }
            case 1 -> {
                int count = getPlayer().getItemQuantity(this.item_coupon, false);
                if (0 < count && !exchange(0, this.item_coupon, -count)) {
                    // 翻訳
                    say("何かがおかしいよ。もう一度試してね。");
                    return false;
                }
                warp(this.map_ludibrium);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
