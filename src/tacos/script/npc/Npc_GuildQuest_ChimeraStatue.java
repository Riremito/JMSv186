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

import odin.handling.world.Guild;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : guildquest1_bonus (JMS131-302), 会話文は lua_script_jp (guildquest1_bonus) を利用
public class Npc_GuildQuest_ChimeraStatue extends NpcScriptMan {

    private final String field_set_name = "Guild1";
    private final String field_set_bonus = "Guild1Bonus";
    private final int map_bonus = 990001000; // シャレン3世の部屋
    private final int item_ruby = 4001024; // 魔性のルビー
    private final int guild_rank_sub_master = 2;

    @Override
    public boolean action(int status, int answer) {
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        // 翻訳
        if (status == 0) {
            // TODO : lua の isGuildQuestRegistered はギルドクエストの登録者の確認だが、ギルドマスターと副マスターの確認で代用
            if (getPlayer().getGuildId() <= 0 || this.guild_rank_sub_master < getPlayer().getGuildRank()) {
                say("#t" + this.item_ruby + "#を持っているリーダーが私に話しかけてください。");
                return false;
            }
            if (getItemCount(this.item_ruby) < 1) {
                say("#t" + this.item_ruby + "#の力を持っていますか？");
                return false;
            }
            say("強大なエルゴスを倒し、シャレニアンに栄光を取り戻しました！", false, true);
            effectScreen("quest/party/clear");
            effectSound("Party1/Clear");
            return true;
        }
        if (status == 1) {
            say("#t" + this.item_ruby + "#の力を持つ者よ、シャレニアンの宝物庫へ案内しましょう。", true, true);
            return true;
        }
        if (status != 2) {
            return false;
        }
        if (!removeItemAll(this.item_ruby)) {
            return false;
        }
        // GP 報酬
        int min = field_set.getQuestTime() / 60;
        int point;
        if (min < 25) {
            point = 850;
        } else if (min < 30) {
            point = 800 - ((min - 25) * 20);
        } else if (min < 40) {
            point = 580 - ((min - 30) * 10);
        } else if (min < 60) {
            point = 400 - ((min - 40) * 5);
        } else if (min < 90) {
            point = 260 - ((min - 60) * 2);
        } else {
            point = 200;
        }
        if (!field_set.getVar("isIncGuildPointState08").equals("1")) {
            Guild world_guild = getPlayer().getWorld().getGuild();
            world_guild.gainGP(getPlayer().getGuildId(), point);
            field_set.setVar("isIncGuildPointState08", "1");
        }
        // 翻訳
        field_set.broadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "ギルドクエストをクリアしました。このマップから出るにはヌリスに話しかけてください。");
        // ギルドメンバー全員をボーナスマップへ送る
        if (enterFieldSet(this.field_set_bonus, 0) == TacosFieldSet.ENTER_SUCCESS) {
            field_set.transferFieldAll(this.map_bonus, "st00");
        }
        return false;
    }
}
