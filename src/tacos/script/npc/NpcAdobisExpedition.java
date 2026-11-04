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
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosNpcExpedition;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : zakum_accept (JMS131-302), 会話文は lua_script_jp (zakum_accept) を利用
// 遠征隊の最少人数とチャンネルの確認は削除
public class NpcAdobisExpedition extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_LEADER_ASK = 1;
    private static final int NODE_MASTER_MENU = 2;
    private static final int NODE_BAN_SELECT = 3;
    private static final int NODE_BAN_ASK = 4;
    private static final int NODE_NOBAN_SELECT = 5;
    private static final int NODE_MEMBER_MENU = 6;
    private final String field_set_name = "ZakumEnter";
    private final String expedition_name = "Zakum";
    private final int map_altar = 280030000; // ジャクムダンジョン : ジャクムの祭壇
    private final int quest_enter_time = 7003; // ジャクムの祭壇に入場した時刻
    private final int quest_enter_count = 7004; // ジャクムの祭壇に入場した回数
    private final int level_min = 50;
    private int node = NODE_START;
    private int last_status = 0;
    private String master_before = "";
    private String ban_name = "";
    private final String[] ban_list = new String[TacosNpcExpedition.MEMBER_MAX];

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        TacosNpcExpedition expedition = TacosNpcExpedition.get(getPlayer(), this.expedition_name);
        String name = getPlayer().getName();
        // lua の zakum_reset
        expedition.reset(field_set);
        // lua の zakum_master (遠征隊長の権限の確認)
        if (this.node == NODE_MASTER_MENU || this.node == NODE_BAN_SELECT || this.node == NODE_BAN_ASK || this.node == NODE_NOBAN_SELECT) {
            if (!expedition.getMaster().equals(name)) {
                // 翻訳
                say("あなたはすでに遠征隊長の権限を失っています。");
                return false;
            }
        }
        switch (this.node) {
            case NODE_START -> {
                String master = expedition.getMaster();
                if (getPlayer().getLevel() < this.level_min) {
                    // 翻訳
                    say("ジャクム遠征隊に参加できるのは、レベル50以上のプレイヤーだけです。");
                    return false;
                }
                // 遠征隊長の申請
                if (master.isEmpty()) {
                    if (0 < getUserCount(this.map_altar)) {
                        // 翻訳
                        say("クエストが現在進行中のため、クエストに申請できません。");
                        return false;
                    }
                    if (!isPartyBoss()) {
                        // 翻訳
                        say("遠征隊長に申請できるのはパーティーのリーダーです。");
                        return false;
                    }
                    if (name.equals(expedition.getLastMaster())) {
                        // 翻訳
                        say("続けてミッションの遠征隊長になることはできません。");
                        return false;
                    }
                    // 翻訳
                    askYesNo("ジャクム遠征隊の隊長になりますか？");
                    this.node = NODE_LEADER_ASK;
                    return true;
                }
                // 遠征隊長
                if (name.equals(master)) {
                    if (field_set.getVar("enter").equals("1")) {
                        warp(this.map_altar);
                        // lua の zakum_timecheck1 (入場回数の記録)
                        String time = currentTime();
                        if (1440 <= compareTime(time, getQuestData(this.quest_enter_time)) || getQuestData(this.quest_enter_count).isEmpty()) {
                            setQuestData(this.quest_enter_time, time);
                            setQuestData(this.quest_enter_count, "1");
                        } else if (getQuestData(this.quest_enter_count).equals("1")) {
                            setQuestData(this.quest_enter_count, "2");
                        }
                        return false;
                    }
                    if (!field_set.getVar("dName").isEmpty()) {
                        field_set.setVar("dName", "");
                    }
                    NpcScriptTag nt = new NpcScriptTag();
                    // 翻訳
                    nt.add("ごきげんよう、ジャクム遠征隊長。何をしますか？ \r\n#b#L0# 遠征隊のリストを確認する#l\r\n#L1# 遠征隊からメンバーを追放する#l\r\n#L2# 停止リストからメンバーを再び受け入れる#l\r\n#r#L3# 遠征隊を結成して入場する#l#k", false);
                    askMenu(nt);
                    this.node = NODE_MASTER_MENU;
                    return true;
                }
                // 遠征隊員
                if (expedition.isBanned(name)) {
                    // 翻訳
                    say(name + " は停止リストに入っているため参加できません。");
                    return false;
                }
                if (field_set.getVar("enter").equals("1")) {
                    if (!expedition.isMember(name)) {
                        // 翻訳
                        say("ジャクム遠征隊のメンバーでなければ、この場所には入れません。");
                        return false;
                    }
                    warp(this.map_altar);
                    // lua の zakum_timecheck1 (入場回数の記録)
                    String time = currentTime();
                    if (1440 <= compareTime(time, getQuestData(this.quest_enter_time)) || getQuestData(this.quest_enter_count).isEmpty()) {
                        setQuestData(this.quest_enter_time, time);
                        setQuestData(this.quest_enter_count, "1");
                    } else if (getQuestData(this.quest_enter_count).equals("1")) {
                        setQuestData(this.quest_enter_count, "2");
                    }
                    return false;
                }
                this.master_before = master;
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                nt.add("何をしますか？\r\n#b#L0# ジャクム遠征隊に参加する#l\r\n#L1# ジャクム遠征隊から脱退する#l\r\n#L2# 遠征隊のリストを確認する。#k", false);
                askMenu(nt);
                this.node = NODE_MEMBER_MENU;
                return true;
            }
            case NODE_LEADER_ASK -> {
                if (!ok) {
                    // 翻訳
                    say("遠征隊長になりたいなら、私に話しかけてください。");
                    return false;
                }
                // lua の zakum_partycheck (パーティーの人数の確認は削除)
                for (TacosCharacter member : TacosFieldSet.getPartyMembers(getPlayer())) {
                    if (member.getLevel() < this.level_min) {
                        // 翻訳
                        say("パーティーにレベル50未満のユーザーがいます。メンバーのレベルを合わせてください。");
                        return false;
                    }
                }
                String master = expedition.getMaster();
                if (!master.isEmpty()) {
                    // 翻訳
                    say("" + master + " はすでに遠征隊長に任命されています。");
                    return false;
                }
                if (!field_set.startManually()) {
                    // 翻訳
                    say("遠征隊はまだ結成できる状態ではありません。少し後でもう一度確認してください。");
                    return false;
                }
                field_set.resetQuestTime();
                field_set.setVar("reset", "");
                expedition.setMaster(name);
                // 翻訳
                fieldNotice(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, name + "がジャクム遠征隊の隊長に任命されました。遠征隊に参加したい方は、今すぐ申請してください！");
                say("あなたはジャクム遠征隊の隊長に任命されました。これから5分以内に遠征隊を結成し、メンバー全員をミッションに入場させてください。");
                return false;
            }
            case NODE_MASTER_MENU -> {
                switch (answer) {
                    case 0 -> {
                        if (expedition.getMemberCount() == 0) {
                            // 翻訳
                            say("遠征隊に申請したユーザーはいません。");
                            return false;
                        }
                        // lua の zakum_getname
                        StringBuilder sb = new StringBuilder();
                        sb.append("1 : #b").append(expedition.getMaster()).append("#k（遠征隊長）");
                        for (int i = 0; i < TacosNpcExpedition.MEMBER_MAX; i++) {
                            sb.append("\r\n").append(i + 2).append(" : ").append(expedition.getMember(i));
                        }
                        // 翻訳
                        say("申請者は合計#b" + (expedition.getMemberCount() + 1) + "#k人で、申請者のリストは次のとおりです。\r\n" + sb.toString());
                        return false;
                    }
                    case 1 -> {
                        if (expedition.getMemberCount() == 0) {
                            // 翻訳
                            say("遠征隊に申請したユーザーはいません。");
                            return false;
                        }
                        // lua の zakum_ban
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < TacosNpcExpedition.MEMBER_MAX; i++) {
                            this.ban_list[i] = expedition.getMember(i);
                            if (!expedition.getMember(i).isEmpty()) {
                                sb.append("\r\n#L").append(i).append("#").append(i + 2).append(" : ").append(expedition.getMember(i)).append("#l");
                            }
                        }
                        NpcScriptTag nt = new NpcScriptTag();
                        // 翻訳
                        nt.add("どのメンバーを追放しますか？\r\n" + sb.toString(), false);
                        askMenu(nt);
                        this.node = NODE_BAN_SELECT;
                        return true;
                    }
                    case 2 -> {
                        if (expedition.getBannedCount() == 0) {
                            // 翻訳
                            say("遠征隊に申請したユーザーはいません。");
                            return false;
                        }
                        // lua の zakum_noban
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < TacosNpcExpedition.BANNED_MAX; i++) {
                            if (!expedition.getBanned(i).isEmpty()) {
                                sb.append("\r\n#b#L").append(i).append("#").append(expedition.getBanned(i)).append("#l#k");
                            }
                        }
                        NpcScriptTag nt = new NpcScriptTag();
                        // 翻訳
                        nt.add("誰の申請を受け入れますか？" + sb.toString(), false);
                        askMenu(nt);
                        this.node = NODE_NOBAN_SELECT;
                        return true;
                    }
                    case 3 -> {
                        // 遠征隊の人数の確認は削除
                        warp(this.map_altar);
                        // lua の zakum_timecheck1 (入場回数の記録)
                        String time = currentTime();
                        if (1440 <= compareTime(time, getQuestData(this.quest_enter_time)) || getQuestData(this.quest_enter_count).isEmpty()) {
                            setQuestData(this.quest_enter_time, time);
                            setQuestData(this.quest_enter_count, "1");
                        } else if (getQuestData(this.quest_enter_count).equals("1")) {
                            setQuestData(this.quest_enter_count, "2");
                        }
                        // lua の zakum_enterMsg
                        for (int i = 0; i < TacosNpcExpedition.MEMBER_MAX; i++) {
                            if (!expedition.getMember(i).isEmpty()) {
                                // 翻訳
                                sendMessage(expedition.getMember(i), "遠征隊長がマップに入りました。遠征隊の時間がなくなる前にマップに入ってください。");
                            }
                        }
                        field_set.setVar("enter", "1");
                        return false;
                    }
                    default -> {
                        return false;
                    }
                }
            }
            case NODE_BAN_SELECT -> {
                if (answer < 0 || TacosNpcExpedition.MEMBER_MAX <= answer || this.ban_list[answer] == null || this.ban_list[answer].isEmpty()) {
                    return false;
                }
                String member = this.ban_list[answer];
                // 選択肢の表示後に遠征隊員が脱退した場合
                if (!expedition.getMember(answer).equals(member)) {
                    // 翻訳
                    say(member + " はすでに遠征隊から脱退したメンバーです。");
                    return false;
                }
                expedition.removeMember(member);
                // 翻訳
                sendMessage(member, "遠征隊長によって、あなたは遠征隊から外されました。");
                // lua の zakum_ban2
                if (TacosNpcExpedition.BANNED_MAX <= expedition.getBannedCount()) {
                    // 翻訳
                    say("停止リストには最大10人まで登録できます。");
                    return false;
                }
                field_set.setVar("dName", member);
                this.ban_name = member;
                // 翻訳
                askYesNo("本当に#b" + member + "#kを停止リストに登録しますか？停止されると、遠征隊長が停止を解除するまで、そのユーザーは再申請できません。");
                this.node = NODE_BAN_ASK;
                return true;
            }
            case NODE_BAN_ASK -> {
                if (!ok) {
                    return false;
                }
                expedition.addBanned(this.ban_name);
                // 翻訳
                sendMessage(this.ban_name, "遠征隊長によって、あなたは遠征隊の停止リストに登録されました。");
                field_set.setVar("dName", "");
                return false;
            }
            case NODE_NOBAN_SELECT -> {
                String member = expedition.getBanned(answer);
                if (expedition.removeBanned(answer)) {
                    // 翻訳
                    sendMessage(member, "遠征隊長が停止を解除したので、遠征隊に再申請できるようになりました。");
                }
                return false;
            }
            case NODE_MEMBER_MENU -> {
                // lua の zakum_entercheck2
                if (field_set.getVar("enter").equals("1")) {
                    // 翻訳
                    say("クエストへの入場はすでに決定されています。");
                    return false;
                }
                String master = expedition.getMaster();
                if (!this.master_before.isEmpty() && !this.master_before.equals(master)) {
                    // 翻訳
                    say("ジャクム遠征隊の申請受付はすでに終了しています。");
                    return false;
                }
                switch (answer) {
                    case 0 -> {
                        if (field_set.getVar("dName").equals(name)) {
                            // 翻訳
                            say("遠征隊長があなたを停止リストに登録するかどうか検討中です。少し後でもう一度確認してください。");
                            return false;
                        }
                        if (expedition.isBanned(name)) {
                            // 翻訳
                            say("#b" + name + "#kは遠征隊長によって、遠征隊への参加を正式に拒否されました。");
                            return false;
                        }
                        if (expedition.isMember(name)) {
                            // 翻訳
                            say("あなたはすでに遠征隊の一員です。");
                            return false;
                        }
                        // lua の zakum_in
                        if (!expedition.addMember(name)) {
                            // 翻訳
                            say("申請者がすでに最大人数に達しているため、申請できません。");
                            return false;
                        }
                        // 翻訳
                        sendMessage(master, name + "が遠征隊に参加しました。");
                        say("ジャクム遠征隊に登録されました。");
                        return false;
                    }
                    case 1 -> {
                        if (expedition.isBanned(name)) {
                            // 翻訳
                            say("あなたの遠征隊への参加は正式に拒否されています。遠征隊長の承認がある場合にのみ参加できます。");
                            return false;
                        }
                        if (!expedition.isMember(name)) {
                            // 翻訳
                            say("ジャクム遠征隊に参加していないため、遠征隊から脱退できません。");
                            return false;
                        }
                        // lua の zakum_out
                        expedition.removeMember(name);
                        // 翻訳
                        sendMessage(master, name + " が遠征隊から脱退しました。");
                        say("遠征隊から正式に脱退しました。");
                        return false;
                    }
                    case 2 -> {
                        // lua の zakum_getname
                        StringBuilder sb = new StringBuilder();
                        sb.append("1 : #b").append(master).append("#k（遠征隊長）");
                        for (int i = 0; i < TacosNpcExpedition.MEMBER_MAX; i++) {
                            sb.append("\r\n").append(i + 2).append(" : ").append(expedition.getMember(i));
                        }
                        // 翻訳
                        say("申請者は合計#b" + (expedition.getMemberCount() + 1) + "#k人で、申請者のリストは次のとおりです。\r\n" + sb.toString());
                        return false;
                    }
                    default -> {
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
