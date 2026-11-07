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
package tacos.script.portal;

import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : hontale_boss1, hontale_boss2 (JMS186), lua の処理を利用
// 試験の洞窟に入るとホーンテイルの頭を召喚する
// lua では召喚袋のアイテム ID (2100031, 2100032) を指定しているため、召喚されるモンスターの ID に置き換えた
public class Portal_Leafre_HontaleBoss extends PortalScriptMan {

    private final int mob_horntail_left = 8810024; // ホーンテイルの左頭召喚 (2100031, ホーンテイルA の包み)
    private final int mob_horntail_right = 8810025; // ホーンテイルの右頭召喚 (2100032, ホーンテイルC の包み)

    @Override
    public boolean enter() {
        boolean boss1 = getScriptName().equals("hontale_boss1");
        TacosFieldSet field_set = getFieldSet("Hontale3");
        if (field_set == null) {
            return false;
        }
        String key = boss1 ? "boss1" : "boss2";
        if (field_set.getVar(key).equals("1")) {
            return false;
        }
        // 翻訳
        fieldNotice(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "洞窟の奥深くから巨大な生き物が近づいてきます。");
        if (boss1) {
            summonMobAt(880, 220, this.mob_horntail_left);
        } else {
            summonMobAt(-350, 220, this.mob_horntail_right);
        }
        field_set.setVar(key, "1");
        return false;
    }
}
