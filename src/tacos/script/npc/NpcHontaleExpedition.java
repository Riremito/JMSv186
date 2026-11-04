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
import tacos.server.TacosFieldSet;
import tacos.server.TacosNpcExpedition;

/**
 *
 * @author Riremito
 */
// Npc.wz script : hontale_accept (JMS131-302), 会話文は lua_script_jp (hontale_accept) を利用
// 遠征隊の最少人数とチャンネルの確認は削除
public class NpcHontaleExpedition extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_LEADER_ASK = 1;
    private static final int NODE_MASTER_MENU = 2;
    private static final int NODE_BAN_SELECT = 3;
    private static final int NODE_BAN_ASK = 4;
    private static final int NODE_NOBAN_SELECT = 5;
    private static final int NODE_MEMBER_MENU = 6;
    private final String field_set_name = "Hontale2";
    private final String field_set_boss = "Hontale3";
    private final String expedition_name = "Hontale";
    private final int map_boss = 240060000; // リプレ : 試験の洞窟1
    private final int quest_enter_time = 7310; // ホーンテイルの洞窟に入場した時刻
    private final int quest_enter_count = 7311; // ホーンテイルの洞窟に入場した回数
    private final int level_min = 80;
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
        TacosFieldSet field_set_boss = getFieldSet(this.field_set_boss);
        if (field_set == null || field_set_boss == null) {
            return false;
        }
        TacosNpcExpedition expedition = TacosNpcExpedition.get(getPlayer(), this.expedition_name);
        String name = getPlayer().getName();
        // lua の hontale_reset
        expedition.reset(field_set);
        // lua の hontale_master (遠征隊長の権限の確認)
        if (this.node == NODE_MASTER_MENU || this.node == NODE_BAN_SELECT || this.node == NODE_BAN_ASK || this.node == NODE_NOBAN_SELECT) {
            if (!expedition.getMaster().equals(name)) {
                // 翻訳
                say("あなたはすでに遠征隊長の権限を失っています。");
                return false;
            }
        }
        switch (this.node) {
            case NODE_START -> {
                // lua の hontale_timecheck (1日の入場回数の確認)
                if (compareTime(currentTime(), getQuestData(this.quest_enter_time)) < 1440 && getQuestData(this.quest_enter_count).equals("2")) {
                    // 翻訳
                    say("ホーンテイルの洞窟に入場できるのは1日2回までです。");
                    return false;
                }
                if (getPlayer().getLevel() < this.level_min) {
                    // 翻訳
                    say("参加できるのはレベル80以上のユーザーのみです。");
                    return false;
                }
                String master = expedition.getMaster();
                // 遠征隊長の申請
                if (master.isEmpty()) {
                    if (0 < field_set_boss.getUserCount()) {
                        // 翻訳
                        say("クエストはすでに進行中です。今は登録できません。");
                        return false;
                    }
                    if (!isPartyBoss()) {
                        // 翻訳
                        say("遠征隊長に申請できるのはパーティーリーダーです。");
                        return false;
                    }
                    if (name.equals(expedition.getLastMaster())) {
                        // 翻訳
                        say("2回続けて遠征隊長になることはできません。");
                        return false;
                    }
                    // 翻訳
                    askYesNo("ホーンテイル遠征隊の隊長になりますか？");
                    this.node = NODE_LEADER_ASK;
                    return true;
                }
                // 遠征隊長
                if (name.equals(master)) {
                    if (field_set.getVar("enter").equals("1")) {
                        // lua の hontale_entercheck (入場回数の記録)
                        String time = currentTime();
                        if (1440 <= compareTime(time, getQuestData(this.quest_enter_time)) || getQuestData(this.quest_enter_count).isEmpty()) {
                            setQuestData(this.quest_enter_time, time);
                            setQuestData(this.quest_enter_count, "1");
                        } else if (getQuestData(this.quest_enter_count).equals("1")) {
                            setQuestData(this.quest_enter_count, "2");
                        }
                        warp(this.map_boss);
                        return false;
                    }
                    if (!field_set.getVar("dName").isEmpty()) {
                        field_set.setVar("dName", "");
                    }
                    NpcScriptTag nt = new NpcScriptTag();
                    // 翻訳
                    nt.add("ホーンテイル遠征隊長、何をしますか？ \r\n#b#L0# 遠征隊員リストを見る#l\r\n#L1# 遠征隊から追放する#l\r\n#L2# ブロックしたユーザーを許可する#l\r\n#r#L3# 遠征隊を編成して入場する#l#k", false);
                    askMenu(nt);
                    this.node = NODE_MASTER_MENU;
                    return true;
                }
                // 遠征隊員
                if (expedition.isBanned(name)) {
                    // 翻訳
                    say(name + " 遠征隊長があなたをブロックしています。参加できません。");
                    return false;
                }
                if (field_set.getVar("enter").equals("1")) {
                    if (!expedition.isMember(name)) {
                        // 翻訳
                        say("入場できるのはホーンテイル遠征隊のメンバーのみです。");
                        return false;
                    }
                    // lua の hontale_entercheck (入場回数の記録)
                    String time = currentTime();
                    if (1440 <= compareTime(time, getQuestData(this.quest_enter_time)) || getQuestData(this.quest_enter_count).isEmpty()) {
                        setQuestData(this.quest_enter_time, time);
                        setQuestData(this.quest_enter_count, "1");
                    } else if (getQuestData(this.quest_enter_count).equals("1")) {
                        setQuestData(this.quest_enter_count, "2");
                    }
                    warp(this.map_boss);
                    return false;
                }
                this.master_before = master;
                NpcScriptTag nt = new NpcScriptTag();
                // 翻訳
                nt.add("何をしますか？\r\n#b#L0# ホーンテイル遠征隊に参加する#l\r\n#L1# ホーンテイル遠征隊から脱退する#l\r\n#L2# 遠征隊員リストを見る。#k", false);
                askMenu(nt);
                this.node = NODE_MEMBER_MENU;
                return true;
            }
            case NODE_LEADER_ASK -> {
                if (!ok) {
                    // 翻訳
                    say("遠征隊長になりたい場合は、もう一度話しかけてください。");
                    return false;
                }
                // lua の hontale_partycheck (パーティーの人数の確認は削除)
                for (TacosCharacter member : TacosFieldSet.getPartyMembers(getPlayer())) {
                    if (member.getLevel() < this.level_min) {
                        // 翻訳
                        say("パーティーにレベル80未満のキャラクターがいます。レベルを調整してください。");
                        return false;
                    }
                }
                String master = expedition.getMaster();
                if (!master.isEmpty()) {
                    // 翻訳
                    say("遠征隊長には、" + master + "がすでに任命されています。");
                    return false;
                }
                if (!field_set.startManually()) {
                    // 翻訳
                    say("遠征隊を編成する準備ができていません。しばらくしてからもう一度お試しください。");
                    return false;
                }
                field_set.resetQuestTime();
                field_set.setVar("reset", "");
                expedition.setMaster(name);
                // 翻訳
                fieldNotice(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, name + "がホーンテイル遠征隊の隊長です。今すぐ遠征隊への参加を申請してください。");
                say(" あなたがホーンテイル遠征隊の隊長ですか？5分以内に遠征隊の編成を完了してください。メンバー全員が入場する必要があります。");
                return false;
            }
            case NODE_MASTER_MENU -> {
                switch (answer) {
                    case 0 -> {
                        if (expedition.getMemberCount() == 0) {
                            // 翻訳
                            say("遠征隊に申請した人はいません。");
                            return false;
                        }
                        // lua の hontale_getname
                        StringBuilder sb = new StringBuilder();
                        sb.append("No.1 : #b").append(expedition.getMaster()).append("#k (遠征隊長)");
                        for (int i = 0; i < TacosNpcExpedition.MEMBER_MAX; i++) {
                            sb.append("\r\nNo.").append(i + 2).append(" : ").append(expedition.getMember(i));
                        }
                        // 翻訳
                        say("申請者の総数は#b" + (expedition.getMemberCount() + 1) + "#k人で、遠征隊員のリストは以下のとおりです。\r\n" + sb.toString());
                        return false;
                    }
                    case 1 -> {
                        if (expedition.getMemberCount() == 0) {
                            // 翻訳
                            say("遠征隊に申請した人はいません。");
                            return false;
                        }
                        // lua の hontale_ban
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < TacosNpcExpedition.MEMBER_MAX; i++) {
                            this.ban_list[i] = expedition.getMember(i);
                            if (!expedition.getMember(i).isEmpty()) {
                                sb.append("\r\n#L").append(i).append("#No.").append(i + 2).append(" : ").append(expedition.getMember(i)).append("#l");
                            }
                        }
                        NpcScriptTag nt = new NpcScriptTag();
                        // 翻訳
                        nt.add("誰を追放しますか？\r\n" + sb.toString(), false);
                        askMenu(nt);
                        this.node = NODE_BAN_SELECT;
                        return true;
                    }
                    case 2 -> {
                        if (expedition.getBannedCount() == 0) {
                            // 翻訳
                            say("遠征隊に申請した人はいません。");
                            return false;
                        }
                        // lua の hontale_noban
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < TacosNpcExpedition.BANNED_MAX; i++) {
                            if (!expedition.getBanned(i).isEmpty()) {
                                sb.append("\r\n#b#L").append(i).append("#").append(expedition.getBanned(i)).append("#l#k");
                            }
                        }
                        NpcScriptTag nt = new NpcScriptTag();
                        // 翻訳
                        nt.add("ホーンテイル遠征隊への申請を許可する人を選んでください。" + sb.toString(), false);
                        askMenu(nt);
                        this.node = NODE_NOBAN_SELECT;
                        return true;
                    }
                    case 3 -> {
                        // 遠征隊の人数の確認は削除
                        int res = enterFieldSetParty(this.field_set_boss, 0);
                        // 翻訳
                        switch (res) {
                            case TacosFieldSet.ENTER_SUCCESS -> {
                            }
                            case TacosFieldSet.ENTER_NO_PARTY -> {
                                say("挑戦するにはパーティーを組む必要があります。");
                                return false;
                            }
                            case TacosFieldSet.ENTER_PARTY_MEMBER -> {
                                say("クエストを開始するには、パーティーメンバーが3人以上必要です。");
                                return false;
                            }
                            case TacosFieldSet.ENTER_PARTY_LEVEL -> {
                                say("パーティーメンバーのレベルは80以上である必要があります。");
                                return false;
                            }
                            case TacosFieldSet.ENTER_USING -> {
                                say("現在、ほかのパーティーがクエストに挑戦中です。");
                                return false;
                            }
                            default -> {
                                say("不明な理由により入場できません。もう一度お試しください。");
                                return false;
                            }
                        }
                        // lua の hontale_enterMsg
                        for (int i = 0; i < TacosNpcExpedition.MEMBER_MAX; i++) {
                            if (!expedition.getMember(i).isEmpty()) {
                                // 翻訳
                                sendMessage(expedition.getMember(i), "遠征隊長がマップに入場しました。遠征隊の編成時間が終わる前にマップに入場してください。");
                            }
                        }
                        // lua の hontale_entercheck (入場回数の記録)
                        String time = currentTime();
                        if (1440 <= compareTime(time, getQuestData(this.quest_enter_time)) || getQuestData(this.quest_enter_count).isEmpty()) {
                            setQuestData(this.quest_enter_time, time);
                            setQuestData(this.quest_enter_count, "1");
                        } else if (getQuestData(this.quest_enter_count).equals("1")) {
                            setQuestData(this.quest_enter_count, "2");
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
                    say(member + "はすでに遠征隊から脱退しています。");
                    return false;
                }
                expedition.removeMember(member);
                // 翻訳
                sendMessage(member, "遠征隊長により遠征隊から追放されました。");
                // lua の hontale_ban2
                if (TacosNpcExpedition.BANNED_MAX <= expedition.getBannedCount()) {
                    // 翻訳
                    say("ブロックできるのは最大10人までです。");
                    return false;
                }
                field_set.setVar("dName", member);
                this.ban_name = member;
                // 翻訳
                askYesNo("#b" + member + "#kを制限リストに登録します。  よろしいですか？  制限されたキャラクターは、遠征隊長が許可するまでこの遠征隊に再申請できなくなります。");
                this.node = NODE_BAN_ASK;
                return true;
            }
            case NODE_BAN_ASK -> {
                if (!ok) {
                    return false;
                }
                expedition.addBanned(this.ban_name);
                // 翻訳
                sendMessage(this.ban_name, "遠征隊長により、遠征隊の制限リストに登録されました。");
                field_set.setVar("dName", "");
                return false;
            }
            case NODE_NOBAN_SELECT -> {
                String member = expedition.getBanned(answer);
                if (expedition.removeBanned(answer)) {
                    // 翻訳
                    sendMessage(member, "遠征隊長がブロックリストからあなたを解除しました。再び登録できます。");
                }
                return false;
            }
            case NODE_MEMBER_MENU -> {
                // lua の hontale_entercheck2
                if (field_set.getVar("enter").equals("1")) {
                    // 翻訳
                    say("あなたはすでに入場を許可されています。");
                    return false;
                }
                String master = expedition.getMaster();
                if (!this.master_before.isEmpty() && !this.master_before.equals(master)) {
                    // 翻訳
                    say("前回の遠征隊の申請はすでに終了しています。");
                    return false;
                }
                switch (answer) {
                    case 0 -> {
                        if (field_set.getVar("dName").equals(name)) {
                            // 翻訳
                            say("遠征隊長があなたをブロックするか検討中です。しばらくしてからもう一度お試しください。");
                            return false;
                        }
                        if (expedition.isBanned(name)) {
                            // 翻訳
                            say("#b" + name + "#k様、遠征隊への参加申請は隊長により拒否されました。");
                            return false;
                        }
                        if (expedition.isMember(name)) {
                            // 翻訳
                            say("あなたはすでに遠征隊に参加しています。");
                            return false;
                        }
                        // lua の hontale_in
                        if (!expedition.addMember(name)) {
                            // 翻訳
                            say("申請者数が上限に達しています。申請できません。");
                            return false;
                        }
                        // 翻訳
                        sendMessage(master, name + "が遠征隊に参加しました。");
                        say("ホーンテイル遠征隊に登録されました。");
                        return false;
                    }
                    case 1 -> {
                        if (expedition.isBanned(name)) {
                            // 翻訳
                            say("遠征隊長に拒否されました。参加するには許可が必要です。");
                            return false;
                        }
                        if (!expedition.isMember(name)) {
                            // 翻訳
                            say("遠征隊に参加していないので、脱退できません。");
                            return false;
                        }
                        // lua の hontale_out
                        expedition.removeMember(name);
                        // 翻訳
                        sendMessage(master, name + "がホーンテイル遠征隊から脱退しました。");
                        say("ホーンテイル遠征隊から脱退しました。");
                        return false;
                    }
                    case 2 -> {
                        // lua の hontale_getname
                        StringBuilder sb = new StringBuilder();
                        sb.append("No.1 : #b").append(master).append("#k (遠征隊長)");
                        for (int i = 0; i < TacosNpcExpedition.MEMBER_MAX; i++) {
                            sb.append("\r\nNo.").append(i + 2).append(" : ").append(expedition.getMember(i));
                        }
                        // 翻訳
                        say("申請者の総数は#b" + (expedition.getMemberCount() + 1) + "#k人で、遠征隊員のリストは以下のとおりです。\r\n" + sb.toString());
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
