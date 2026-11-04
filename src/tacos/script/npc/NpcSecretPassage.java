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
import tacos.packet.ops.OpsBroadcastMsg;

/**
 *
 * @author Riremito
 */
// Npc.wz script : secretNPC (JMS146-302), lua_script_jp の secretNPC は中身がないため js の処理を利用
public class NpcSecretPassage extends NpcScriptMan {

    private final int quest_password = 3360; // 暗証番号の認証
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
                int quest_state = getPlayer().getQuestStatus(this.quest_password);
                if (quest_state == 2) {
                    // TODO : メッセージの種類
                    // TODO : 文章 (適当)
                    getPlayer().dropMessage(OpsBroadcastMsg.BM_EVENT.get(), "名簿に名前が載っている。秘密通路へ移動します。");
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
                // TODO : 暗証番号はクエスト開始時にクエストのデータとして設定される想定 (設定する処理がない)
                String password = getPlayer().getQuest(MapleQuest.getInstance(this.quest_password)).getCustomData();
                if (password.isEmpty() || !password.equals(getAnswerText())) {
                    // TODO : 文章 (適当)
                    say("暗証番号が違います。");
                    return false;
                }
                MapleQuest.getInstance(this.quest_password).forceComplete(getPlayer(), getNpcId());
                // TODO : メッセージの種類
                // TODO : 文章 (適当)
                getPlayer().dropMessage(OpsBroadcastMsg.BM_EVENT.get(), "セキュリティ装置が解除された。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
