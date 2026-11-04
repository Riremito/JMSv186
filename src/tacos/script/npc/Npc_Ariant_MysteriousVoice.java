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
public class Npc_Ariant_MysteriousVoice extends NpcScriptMan {

    private final int map_thieves_hideout = 260010402; // バーニングロード : 盗賊の巣窟
    private final String portal_name = "center00";
    // 翻訳
    private final String[] passwords = {
        "開けゴマ",
        "ひらけゴマ"
    };
    private final int password_length_max = 11;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 翻訳
                askText("洞窟の扉を開けたければ、合言葉を言え…", "", 0, this.password_length_max);
                return true;
            }
            case 1 -> {
                for (String password : this.passwords) {
                    if (password.equals(getAnswerText())) {
                        // TODO : メッセージの種類
                        // 翻訳
                        message("合言葉を言うと、神秘的な力が洞窟の中へと導いた。");
                        warp(this.map_thieves_hideout, this.portal_name);
                        return false;
                    }
                }
                // TODO : メッセージの種類
                // 翻訳
                message("洞窟の扉はびくともしない。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
