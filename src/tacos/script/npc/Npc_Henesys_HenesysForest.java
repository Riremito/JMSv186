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
public class Npc_Henesys_HenesysForest extends NpcScriptMan {

    private final int npc_black_shadow = 1012115; // ヘネシス草むら (blackShadowHene1)
    private final int quest_black_shadow = 20706; // 豚農場の黒い影
    private final int npc_report = 1103001; // ローカ

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getNpcId() != this.npc_black_shadow) {
                    // TODO : 文章 (適当)
                    say("このあたりに怪しいものはないようだ。");
                    return false;
                }
                switch (getQuestStatus(this.quest_black_shadow)) {
                    case 1 -> {
                        MapleQuest.getInstance(this.quest_black_shadow).forceComplete(getPlayer(), getNpcId());
                        // TODO : 文章 (適当)
                        say("影を見つけた！#p" + this.npc_report + "#に報告しよう。");
                        return false;
                    }
                    case 2 -> {
                        // TODO : 文章 (適当)
                        say("影はもう見つけた。#p" + this.npc_report + "#に報告しよう。");
                        return false;
                    }
                    default -> {
                        // TODO : 文章 (適当)
                        say("このあたりに怪しいものはないようだ。");
                        return false;
                    }
                }
            }
            default -> {
            }
        }
        return false;
    }
}
