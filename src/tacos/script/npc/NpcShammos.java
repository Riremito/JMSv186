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

import tacos.client.TacosCharacter;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : shammos (JMS186-302), lua が存在しないため旧 js (scripts_JMS) と JMS194 の Map.wz を参考に実装
// パーティーの人数の確認は削除
// TODO : 旧 js の制限時間 (30分) は未実装
public class NpcShammos extends NpcScriptMan {

    private final int map_stage1 = 921120005; // 氷の峡谷 : 氷の峡谷1
    private final int map_stage2 = 921120100; // 氷の峡谷 : 氷の峡谷2
    private final int map_stage3 = 921120200; // 氷の峡谷 : 氷の峡谷3
    private final int map_stage4 = 921120300; // 氷の峡谷 : 氷の峡谷4
    private final int map_stage5 = 921120400; // 氷の峡谷 : 氷の峡谷5
    private final int map_boss = 921120500; // 氷の峡谷 : 万年氷河洞窟
    private final int map_solo = 921120705; // 氷の峡谷 : 氷の峡谷1
    private final int item_bottle = 4032649; // 万年氷下水用の空き瓶
    private final int item_water = 2022698; // 万年氷下水
    private final int level_min = 75;

    @Override
    public boolean action(int status, int answer) {
        if (status == 0) {
            NpcScriptTag nt = new NpcScriptTag();
            // TODO : 文章 (適当)
            nt.add("#b#L0# #t" + this.item_bottle + "#をもらう。#l\r\n#L1# ひとりで氷の峡谷へ行く。#l\r\n#L2# パーティーで氷の峡谷へ行く。#l#k", false);
            askMenu(nt);
            return true;
        }
        if (status != 1) {
            return false;
        }
        switch (answer) {
            case 0 -> {
                if (0 < getItemCount(this.item_bottle) || 0 < getItemCount(this.item_water)) {
                    // TODO : 文章 (適当)
                    say("もう持っているだろう。");
                    return false;
                }
                if (!exchange(0, this.item_bottle, 1)) {
                    // TODO : 文章 (適当)
                    say("その他タブに空きがないようだ。空きを作ってから話しかけてくれ。");
                    return false;
                }
                return false;
            }
            case 1 -> {
                warp(this.map_solo);
                return false;
            }
            case 2 -> {
                if (!isPartyBoss()) {
                    // TODO : 文章 (適当)
                    say("パーティーのリーダーが話しかけてくれ。");
                    return false;
                }
                for (TacosCharacter member : TacosFieldSet.getPartyMembers(getPlayer())) {
                    if (member.getLevel() < this.level_min) {
                        // TODO : 文章 (適当)
                        say("パーティーメンバー全員がレベル75以上でなければならない。");
                        return false;
                    }
                }
                int[] stages = {this.map_stage1, this.map_stage2, this.map_stage3, this.map_stage4, this.map_stage5, this.map_boss};
                for (int map_id : stages) {
                    if (0 < getUserCount(map_id)) {
                        // TODO : 文章 (適当)
                        say("他のパーティーがすでに氷の峡谷に入っている。少し後でもう一度話しかけてくれ。");
                        return false;
                    }
                }
                // シャモスはマップの onFirstUserEnter (shammos_Fenter) で召喚される
                transferParty(this.map_stage1, "");
                return false;
            }
            default -> {
                return false;
            }
        }
    }
}
