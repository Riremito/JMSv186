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
// Npc.wz script : secretNPC (JMS146-302), lua_script_jp の secretNPC は中身がないため js の処理を利用
public class Npc_Magatia_SecretPassage extends NpcScriptMan {

    private final int quest_password = 3360; // 暗証番号の認証
    private final int quest_password_key = 7061; // q3360s (暗証番号の認証) で設定される秘密の通路のキー
    private final int map_zenumist_hallway = 261010000; // ジェニミスト研究所 : 研究所1階廊下
    private final int map_secret_tunnel = 261030000; // アルカドノ研究所 : 暗黒の魔法使いの研究室へ続く道
    private final String portal_zenumist = "sp_jenu";
    private final String portal_alcadno = "sp_alca";
    // TODO : 暗証番号の長さ
    private final int password_length_min = 1;
    private final int password_length_max = 32;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                int quest_state = getQuestStatus(this.quest_password);
                if (quest_state == 2) {
                    // TODO : メッセージの種類
                    // TODO : 文章 (適当)
                    message("名簿に名前が載っている。秘密通路へ移動します。");
                    if (getMapId() == this.map_zenumist_hallway) {
                        warp(this.map_secret_tunnel, this.portal_zenumist);
                        return false;
                    }
                    warp(this.map_secret_tunnel, this.portal_alcadno);
                    return false;
                }
                if (quest_state != 1) {
                    return false;
                }
                // TODO : 文章 (適当)
                askText("暗証番号を入力してください。", "", this.password_length_min, this.password_length_max);
                return true;
            }
            case 1 -> {
                String password = getQuestData(this.quest_password_key);
                if (password.isEmpty() || !password.equals(getAnswerText())) {
                    // TODO : 文章 (適当)
                    say("暗証番号が違います。");
                    return false;
                }
                MapleQuest.getInstance(this.quest_password).forceComplete(getPlayer(), getNpcId());
                // TODO : メッセージの種類
                // TODO : 文章 (適当)
                message("セキュリティ装置が解除された。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
