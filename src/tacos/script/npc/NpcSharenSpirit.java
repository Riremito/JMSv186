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

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : guildquest1_NPC1 (JMS131-302), 会話文は lua_script_jp (guildquest1_NPC1) を利用
public class NpcSharenSpirit extends NpcScriptMan {

    private final String field_set_name = "Guild1";
    private final int guild_rank_sub_master = 2;

    @Override
    public boolean action(int status, int answer) {
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        // 翻訳
        if (field_set.getVar("npc1_talk").equals("end")) {
            say("玉座まであと少しだが、玉座への入口はエルゴスの邪悪な力によって固く閉ざされている。\rこれを破るには、#r聖なる力を形作る#k必要がある…");
            return false;
        }
        // TODO : lua の isGuildQuestRegistered はギルドクエストの登録者の確認だが、ギルドマスターと副マスターの確認で代用
        if (getPlayer().getGuildId() <= 0 || this.guild_rank_sub_master < getPlayer().getGuildRank()) {
            String[] says = {
                "永遠に続くかと思われた眠りの果てに、ついにシャレニアンを救ってくれる者を見つけた。",
                "ギルドのリーダーが私に話しかけてくれ。"
            };
            if (status < 0 || says.length <= status) {
                return false;
            }
            say(says[status], 0 < status, status < says.length - 1);
            return status < says.length - 1;
        }
        say("永遠に続くかと思われた眠りの果てに、ついにシャレニアンを救ってくれる者を見つけた。この老いぼれが、クエストを続けられるよう道を開こう。");
        field_set.setReactorState(23, "ghostgate", 1);
        effectScreen("quest/party/clear");
        effectSound("Party1/Clear");
        field_set.setVar("ghostgateopen", "yes");
        field_set.setVar("npc1_talk", "end");
        return false;
    }
}
