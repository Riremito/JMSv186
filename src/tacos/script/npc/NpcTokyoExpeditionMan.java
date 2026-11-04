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

import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosNpcExpedition;

/**
 *
 * @author Riremito
 */
// 東京系ボスの遠征隊 (Go_boss1-7, TokyoPQ), lua が存在しないため旧 js (scripts_msea102) を参考に Zakum (zakum_accept) と同じ方式で実装
// チャンネルと人数の制限は削除
public abstract class NpcTokyoExpeditionMan extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_LEADER_ASK = 1;
    private static final int NODE_MASTER_MENU = 2;
    private static final int NODE_BAN_SELECT = 3;
    private static final int NODE_BAN_ASK = 4;
    private static final int NODE_NOBAN_SELECT = 5;
    private static final int NODE_MEMBER_MENU = 6;
    private static final int NODE_EXIT = 7;
    private final String expedition_name;
    private final String boss_name;
    private final int map_wait;
    private final int map_boss;
    private final int mob_boss;
    private final int mob_x;
    private final int mob_y;
    private int node = NODE_START;
    private int last_status = 0;
    private String master_before = "";
    private String ban_name = "";
    private final String[] ban_list = new String[TacosNpcExpedition.MEMBER_MAX];

    // mob_boss が 0 の場合はボスを召喚しない (マップのモンスターを利用する)
    public NpcTokyoExpeditionMan(String expedition_name, String boss_name, int map_wait, int map_boss, int mob_boss, int mob_x, int mob_y) {
        this.expedition_name = expedition_name;
        this.boss_name = boss_name;
        this.map_wait = map_wait;
        this.map_boss = map_boss;
        this.mob_boss = mob_boss;
        this.mob_x = mob_x;
        this.mob_y = mob_y;
    }

    // ボスマップへの入場 (最初に入場した場合はボスを召喚する)
    private void enterBoss() {
        boolean first_user = getUserCount(this.map_boss) == 0;
        if (first_user && this.mob_boss != 0) {
            removeAllMob(this.map_boss);
        }
        warp(this.map_boss);
        if (first_user && this.mob_boss != 0) {
            summonMob(this.mob_x, this.mob_y, this.mob_boss);
        }
    }

    private String getMemberList(TacosNpcExpedition expedition) {
        StringBuilder sb = new StringBuilder();
        sb.append("1 : #b").append(expedition.getMaster()).append("#k（遠征隊長）");
        for (int i = 0; i < TacosNpcExpedition.MEMBER_MAX; i++) {
            if (!expedition.getMember(i).isEmpty()) {
                sb.append("\r\n").append(i + 2).append(" : ").append(expedition.getMember(i));
            }
        }
        // TODO : 文章 (適当)
        return "申請者は合計#b" + (expedition.getMemberCount() + 1) + "#k人で、申請者のリストは次のとおりです。\r\n" + sb.toString();
    }

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        TacosNpcExpedition expedition = TacosNpcExpedition.get(getPlayer(), this.expedition_name);
        String name = getPlayer().getName();
        // 受付時間を過ぎた場合は遠征隊の情報を初期化する
        expedition.reset();
        // 遠征隊長の権限の確認
        if (this.node == NODE_MASTER_MENU || this.node == NODE_BAN_SELECT || this.node == NODE_BAN_ASK || this.node == NODE_NOBAN_SELECT) {
            if (!expedition.getMaster().equals(name)) {
                // TODO : 文章 (適当)
                say("あなたはすでに遠征隊長の権限を失っています。");
                return false;
            }
        }
        switch (this.node) {
            case NODE_START -> {
                // ボスマップ内では退場する
                if (getMapId() != this.map_wait) {
                    // TODO : 文章 (適当)
                    say("今すぐ外に出ますか？", false, true);
                    this.node = NODE_EXIT;
                    return true;
                }
                String master = expedition.getMaster();
                // 遠征隊長の申請
                if (master.isEmpty()) {
                    if (0 < getUserCount(this.map_boss)) {
                        // TODO : 文章 (適当)
                        say("すでに" + this.boss_name + "との戦闘が始まっています。");
                        return false;
                    }
                    // TODO : 文章 (適当)
                    askYesNo(this.boss_name + "遠征隊の隊長になりますか？");
                    this.node = NODE_LEADER_ASK;
                    return true;
                }
                // 遠征隊長
                if (name.equals(master)) {
                    if (expedition.isEntered()) {
                        enterBoss();
                        return false;
                    }
                    if (!expedition.getBanPending().isEmpty()) {
                        expedition.setBanPending("");
                    }
                    NpcScriptTag nt = new NpcScriptTag();
                    // TODO : 文章 (適当)
                    nt.add("ごきげんよう、" + this.boss_name + "遠征隊長。何をしますか？ \r\n#b#L0# 遠征隊のリストを確認する#l\r\n#L1# 遠征隊からメンバーを追放する#l\r\n#L2# 停止リストからメンバーを再び受け入れる#l\r\n#r#L3# 遠征隊を結成して入場する#l#k", false);
                    askMenu(nt);
                    this.node = NODE_MASTER_MENU;
                    return true;
                }
                // 遠征隊員
                if (expedition.isBanned(name)) {
                    // TODO : 文章 (適当)
                    say(name + " は停止リストに入っているため参加できません。");
                    return false;
                }
                if (expedition.isEntered()) {
                    if (!expedition.isMember(name)) {
                        // TODO : 文章 (適当)
                        say(this.boss_name + "遠征隊のメンバーでなければ、この場所には入れません。");
                        return false;
                    }
                    enterBoss();
                    return false;
                }
                this.master_before = master;
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("何をしますか？\r\n#b#L0# " + this.boss_name + "遠征隊に参加する#l\r\n#L1# " + this.boss_name + "遠征隊から脱退する#l\r\n#L2# 遠征隊のリストを確認する。#k", false);
                askMenu(nt);
                this.node = NODE_MEMBER_MENU;
                return true;
            }
            case NODE_LEADER_ASK -> {
                if (!ok) {
                    // TODO : 文章 (適当)
                    say("遠征隊長になりたいなら、私に話しかけてください。");
                    return false;
                }
                if (0 < getUserCount(this.map_boss)) {
                    // TODO : 文章 (適当)
                    say("すでに" + this.boss_name + "との戦闘が始まっています。");
                    return false;
                }
                if (!expedition.start(name)) {
                    // TODO : 文章 (適当)
                    say("" + expedition.getMaster() + " はすでに遠征隊長に任命されています。");
                    return false;
                }
                // TODO : 文章 (適当)
                fieldNotice(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, name + "が" + this.boss_name + "遠征隊の隊長に任命されました。遠征隊に参加したい方は、今すぐ申請してください！");
                say("あなたは" + this.boss_name + "遠征隊の隊長に任命されました。これから5分以内に遠征隊を結成し、メンバー全員をミッションに入場させてください。");
                return false;
            }
            case NODE_MASTER_MENU -> {
                switch (answer) {
                    case 0 -> {
                        if (expedition.getMemberCount() == 0) {
                            // TODO : 文章 (適当)
                            say("遠征隊に申請したユーザーはいません。");
                            return false;
                        }
                        say(getMemberList(expedition));
                        return false;
                    }
                    case 1 -> {
                        if (expedition.getMemberCount() == 0) {
                            // TODO : 文章 (適当)
                            say("遠征隊に申請したユーザーはいません。");
                            return false;
                        }
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < TacosNpcExpedition.MEMBER_MAX; i++) {
                            this.ban_list[i] = expedition.getMember(i);
                            if (!expedition.getMember(i).isEmpty()) {
                                sb.append("\r\n#L").append(i).append("#").append(i + 2).append(" : ").append(expedition.getMember(i)).append("#l");
                            }
                        }
                        NpcScriptTag nt = new NpcScriptTag();
                        // TODO : 文章 (適当)
                        nt.add("どのメンバーを追放しますか？\r\n" + sb.toString(), false);
                        askMenu(nt);
                        this.node = NODE_BAN_SELECT;
                        return true;
                    }
                    case 2 -> {
                        if (expedition.getBannedCount() == 0) {
                            // TODO : 文章 (適当)
                            say("停止リストに登録されたユーザーはいません。");
                            return false;
                        }
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < TacosNpcExpedition.BANNED_MAX; i++) {
                            if (!expedition.getBanned(i).isEmpty()) {
                                sb.append("\r\n#b#L").append(i).append("#").append(expedition.getBanned(i)).append("#l#k");
                            }
                        }
                        NpcScriptTag nt = new NpcScriptTag();
                        // TODO : 文章 (適当)
                        nt.add("誰の申請を受け入れますか？" + sb.toString(), false);
                        askMenu(nt);
                        this.node = NODE_NOBAN_SELECT;
                        return true;
                    }
                    case 3 -> {
                        // 遠征隊の人数の確認は削除
                        enterBoss();
                        for (int i = 0; i < TacosNpcExpedition.MEMBER_MAX; i++) {
                            if (!expedition.getMember(i).isEmpty()) {
                                // TODO : 文章 (適当)
                                sendMessage(expedition.getMember(i), "遠征隊長がマップに入りました。遠征隊の時間がなくなる前にマップに入ってください。");
                            }
                        }
                        expedition.setEntered(true);
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
                    // TODO : 文章 (適当)
                    say(member + " はすでに遠征隊から脱退したメンバーです。");
                    return false;
                }
                expedition.removeMember(member);
                // TODO : 文章 (適当)
                sendMessage(member, "遠征隊長によって、あなたは遠征隊から外されました。");
                if (TacosNpcExpedition.BANNED_MAX <= expedition.getBannedCount()) {
                    // TODO : 文章 (適当)
                    say("停止リストには最大10人まで登録できます。");
                    return false;
                }
                expedition.setBanPending(member);
                this.ban_name = member;
                // TODO : 文章 (適当)
                askYesNo("本当に#b" + member + "#kを停止リストに登録しますか？停止されると、遠征隊長が停止を解除するまで、そのユーザーは再申請できません。");
                this.node = NODE_BAN_ASK;
                return true;
            }
            case NODE_BAN_ASK -> {
                if (!ok) {
                    expedition.setBanPending("");
                    return false;
                }
                expedition.addBanned(this.ban_name);
                // TODO : 文章 (適当)
                sendMessage(this.ban_name, "遠征隊長によって、あなたは遠征隊の停止リストに登録されました。");
                expedition.setBanPending("");
                return false;
            }
            case NODE_NOBAN_SELECT -> {
                String member = expedition.getBanned(answer);
                if (expedition.removeBanned(answer)) {
                    // TODO : 文章 (適当)
                    sendMessage(member, "遠征隊長が停止を解除したので、遠征隊に再申請できるようになりました。");
                }
                return false;
            }
            case NODE_MEMBER_MENU -> {
                if (expedition.isEntered()) {
                    // TODO : 文章 (適当)
                    say("クエストへの入場はすでに決定されています。");
                    return false;
                }
                String master = expedition.getMaster();
                if (!this.master_before.isEmpty() && !this.master_before.equals(master)) {
                    // TODO : 文章 (適当)
                    say(this.boss_name + "遠征隊の申請受付はすでに終了しています。");
                    return false;
                }
                switch (answer) {
                    case 0 -> {
                        if (expedition.getBanPending().equals(name)) {
                            // TODO : 文章 (適当)
                            say("遠征隊長があなたを停止リストに登録するかどうか検討中です。少し後でもう一度確認してください。");
                            return false;
                        }
                        if (expedition.isBanned(name)) {
                            // TODO : 文章 (適当)
                            say("#b" + name + "#kは遠征隊長によって、遠征隊への参加を正式に拒否されました。");
                            return false;
                        }
                        if (expedition.isMember(name)) {
                            // TODO : 文章 (適当)
                            say("あなたはすでに遠征隊の一員です。");
                            return false;
                        }
                        if (!expedition.addMember(name)) {
                            // TODO : 文章 (適当)
                            say("申請者がすでに最大人数に達しているため、申請できません。");
                            return false;
                        }
                        // TODO : 文章 (適当)
                        sendMessage(master, name + "が遠征隊に参加しました。");
                        say(this.boss_name + "遠征隊に登録されました。");
                        return false;
                    }
                    case 1 -> {
                        if (!expedition.isMember(name)) {
                            // TODO : 文章 (適当)
                            say(this.boss_name + "遠征隊に参加していないため、遠征隊から脱退できません。");
                            return false;
                        }
                        expedition.removeMember(name);
                        // TODO : 文章 (適当)
                        sendMessage(master, name + " が遠征隊から脱退しました。");
                        say("遠征隊から正式に脱退しました。");
                        return false;
                    }
                    case 2 -> {
                        say(getMemberList(expedition));
                        return false;
                    }
                    default -> {
                        return false;
                    }
                }
            }
            case NODE_EXIT -> {
                if (ok) {
                    warp(this.map_wait);
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
