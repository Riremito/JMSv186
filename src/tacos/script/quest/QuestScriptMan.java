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
package tacos.script.quest;

import odin.server.quest.MapleQuest;
import tacos.client.TacosCharacter;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.builder.PB_UserEffect;
import tacos.script.npc.NpcScriptMan;

/**
 *
 * @author Riremito
 */
// quest の startscript (開始時) と endscript (完了時) の処理, 会話は NPC スクリプトと同じ形式で動作する
public abstract class QuestScriptMan extends NpcScriptMan {

    private int quest_id;
    private boolean quest_start;

    public void setQuest(int quest_id, boolean quest_start) {
        this.quest_id = quest_id;
        this.quest_start = quest_start;
    }

    @Override
    public boolean action(int status, int answer) {
        if (this.quest_start) {
            return questStart(status, answer);
        }
        return questEnd(status, answer);
    }

    // startscript
    public boolean questStart(int status, int answer) {
        return false;
    }

    // endscript
    public boolean questEnd(int status, int answer) {
        return false;
    }

    protected int getQuestId() {
        return this.quest_id;
    }

    protected boolean isQuestStart() {
        return this.quest_start;
    }

    // クエストを開始 (進行中) にする
    protected void forceStartQuest() {
        forceStartQuest(this.quest_id);
    }

    protected void forceStartQuest(int quest_id) {
        MapleQuest.getInstance(quest_id).forceStart(getPlayer(), getNpcId(), null);
    }

    protected void forceStartQuest(int quest_id, String data) {
        MapleQuest.getInstance(quest_id).forceStart(getPlayer(), getNpcId(), data);
    }

    // クエストを完了にする (lua の qr.setComplete, qr.setState(quest_id, 2))
    protected void forceCompleteQuest() {
        forceCompleteQuest(this.quest_id);
    }

    protected void forceCompleteQuest(int quest_id) {
        MapleQuest.getInstance(quest_id).forceComplete(getPlayer(), getNpcId());
    }

    // クエストを放棄する
    protected void forfeitQuest(int quest_id) {
        MapleQuest.getInstance(quest_id).forfeit(getPlayer());
    }

    // lua の target.questEndEffect (クエスト完了のエフェクト)
    protected void questEndEffect() {
        TacosCharacter chr = getPlayer();
        PB_UserEffect pb = PB_UserEffect.builder()
                .player(chr)
                .build();
        chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_QuestComplete));
        chr.getMap().splitSendPacket(chr, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_QuestComplete, pb), chr.getId());
    }
}
