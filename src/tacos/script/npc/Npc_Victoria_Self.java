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

import odin.server.quest.MapleQuest;

/**
 *
 * @author Riremito
 */
public class Npc_Victoria_Self extends NpcScriptMan {

    private final int quest_mushroom_forest = 2314; // キノコの森探査1
    private final int quest_castle_wall = 2322; // 城壁を越えて2
    private final int npc_report = 1300003; // 内務大臣
    private final int item_spray = 2430014; // 殺キノコスプレー

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getQuestStatus(this.quest_mushroom_forest) == 1) {
                    // TODO : 文章 (適当)
                    sayUser("これは…#bキノコの胞子#kを強力な魔法に変えた、強力な魔法の結界だ。力ずくでは突破できない。#b内務大臣#kに報告した方がよさそうだ。", false, true);
                    return true;
                }
                if (getQuestStatus(this.quest_castle_wall) == 1) {
                    // TODO : 文章 (適当)
                    sayUser("巨大な城壁の表面に、とげのあるツルが絡みついている。いったいどうやって城に入ればいいのだろう？とりあえず#b#p" + this.npc_report + "##kに報告しよう。", false, false);
                    return false;
                }
                // TODO : 文章 (適当)
                sayUser("#t" + this.item_spray + "#を使えば結界を破れるかもしれない。", false, false);
                return false;
            }
            case 1 -> {
                if (getQuestStatus(this.quest_mushroom_forest) != 1) {
                    return false;
                }
                avatarOriented("Effect/OnUserEff.img/normalEffect/mushroomcastle/chatBalloon1");
                MapleQuest.getInstance(this.quest_mushroom_forest).forceComplete(getPlayer(), getNpcId());
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
