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
package tacos.server;

import java.util.ArrayList;
import java.util.HashMap;
import odin.handling.world.MapleParty;
import odin.handling.world.MapleParty.MaplePartyCharacter;
import tacos.client.TacosCharacter;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_BroadcastMsg;
import tacos.server.map.TacosMap;
import tacos.server.map.object.TacosReactor;
import tacos.wz.MapleData;
import tacos.wz.ServerImg;

/**
 *
 * @author Riremito
 */
// FieldSet.img のフィールドセット (チャンネルごとに 1 つ)
// TODO : event, action, script, shuffle, unaffected, endfieldset は未実装
public class TacosFieldSet {

    public static final int ENTER_SUCCESS = 0;
    public static final int ENTER_ERROR = -1;
    public static final int ENTER_NO_PARTY = 1;
    public static final int ENTER_PARTY_MEMBER = 2;
    public static final int ENTER_PARTY_LEVEL = 3;
    public static final int ENTER_USING = 4;
    // チャンネル -> 名前 -> フィールドセット
    private static final HashMap<Integer, HashMap<String, TacosFieldSet>> field_sets = new HashMap<>();

    public static synchronized TacosFieldSet get(TacosCharacter chr, String name) {
        HashMap<String, TacosFieldSet> list = field_sets.computeIfAbsent(chr.getChannelId(), k -> new HashMap<>());
        TacosFieldSet field_set = list.get(name);
        if (field_set != null) {
            return field_set;
        }
        MapleData md_field_set = ServerImg.getAutoFieldSet(name);
        if (md_field_set == null) {
            return null;
        }
        field_set = new TacosFieldSet(chr.getChannelServer(), name, md_field_set);
        list.put(name, field_set);
        return field_set;
    }

    // マップ移動時の時計の表示
    public static void sendClock(TacosCharacter chr) {
        TacosFieldSet field_set = find(chr);
        if (field_set == null) {
            return;
        }
        int remain = field_set.getClockTime(System.currentTimeMillis());
        if (remain <= 0) {
            return;
        }
        chr.SendPacket(ResCField.Clock(remain));
    }

    // 制限時間の確認
    public static boolean update(TacosCharacter chr, long time_current) {
        TacosFieldSet field_set = find(chr);
        if (field_set == null) {
            return false;
        }
        if (field_set.getClockTime(time_current) != 0) {
            return false;
        }
        int map_id = field_set.getTargetFieldID();
        if (map_id == 0) {
            map_id = chr.getMap().getForcedReturnId();
        }
        if (!chr.changeMapById(map_id)) {
            chr.changeMapById(chr.getMap().getReturnMapId());
        }
        return true;
    }

    private static synchronized TacosFieldSet find(TacosCharacter chr) {
        HashMap<String, TacosFieldSet> list = field_sets.get(chr.getChannelId());
        if (list == null) {
            return null;
        }
        for (TacosFieldSet field_set : list.values()) {
            if (field_set.field_ids.contains(chr.getMapId())) {
                return field_set;
            }
        }
        return null;
    }

    private final TacosChannel channel;
    private final String name;
    private final ArrayList<Integer> field_ids = new ArrayList<>();
    private final int time_out_default;
    private final boolean manual_start;
    private final int member_min;
    private final int member_max;
    private final int level_min;
    private final int level_max;
    private final HashMap<String, String> vars = new HashMap<>();
    private int time_out;
    private long time_start = 0;
    private int target_field_id = 0;

    private TacosFieldSet(TacosChannel channel, String name, MapleData md_field_set) {
        this.channel = channel;
        this.name = name;
        for (int i = 0; md_field_set.getChildByPath(String.valueOf(i)) != null; i++) {
            this.field_ids.add(md_field_set.getInt(String.valueOf(i)));
        }
        this.time_out_default = md_field_set.getInt("timeOut");
        this.time_out = this.time_out_default;
        this.manual_start = md_field_set.getInt("manualstart") != 0;
        this.member_min = md_field_set.getInt("party/memberMin");
        this.member_max = md_field_set.getInt("party/memberMax");
        this.level_min = md_field_set.getInt("party/levelMin");
        this.level_max = md_field_set.getInt("party/levelMax");
    }

    public String getName() {
        return this.name;
    }

    public int getFieldId(int index) {
        if (index < 0 || this.field_ids.size() <= index) {
            return 0;
        }
        return this.field_ids.get(index);
    }

    // フィールドセット内のキャラクター
    public synchronized ArrayList<TacosCharacter> getUsers() {
        ArrayList<TacosCharacter> users = new ArrayList<>();
        for (int field_id : this.field_ids) {
            TacosMap map = this.channel.findMap(field_id);
            if (map != null) {
                users.addAll(map.getAllPlayers());
            }
        }
        return users;
    }

    public synchronized int getUserCount() {
        return getUsers().size();
    }

    // 1 人で入場する
    public synchronized int enter(TacosCharacter chr, int index) {
        ArrayList<TacosCharacter> members = new ArrayList<>();
        members.add(chr);
        return enter(members, index);
    }

    // パーティーで入場する (同じマップにいるパーティーメンバーが入場する)
    public synchronized int enterParty(TacosCharacter chr, int index) {
        MapleParty party = chr.getParty();
        if (party == null || party.getLeader().getId() != chr.getId()) {
            return ENTER_NO_PARTY;
        }
        ArrayList<TacosCharacter> members = getPartyMembers(chr);
        if (members.size() < this.member_min || (0 < this.member_max && this.member_max < members.size())) {
            return ENTER_PARTY_MEMBER;
        }
        for (TacosCharacter member : members) {
            if (member.getLevel() < this.level_min || (0 < this.level_max && this.level_max < member.getLevel())) {
                return ENTER_PARTY_LEVEL;
            }
        }
        return enter(members, index);
    }

    // 同じマップにいるパーティーメンバー
    public static ArrayList<TacosCharacter> getPartyMembers(TacosCharacter chr) {
        ArrayList<TacosCharacter> members = new ArrayList<>();
        MapleParty party = chr.getParty();
        if (party == null) {
            members.add(chr);
            return members;
        }
        for (MaplePartyCharacter mpc : party.getMembers()) {
            TacosCharacter member = chr.getMap().getPlayerById(mpc.getId());
            if (member != null) {
                members.add(member);
            }
        }
        return members;
    }

    private int enter(ArrayList<TacosCharacter> members, int index) {
        int field_id = getFieldId(index);
        if (field_id == 0) {
            return ENTER_ERROR;
        }
        if (0 < getUserCount()) {
            return ENTER_USING;
        }
        reset();
        if (!this.manual_start) {
            this.time_start = System.currentTimeMillis();
        }
        for (TacosCharacter member : members) {
            member.changeMapById(field_id);
        }
        return ENTER_SUCCESS;
    }

    private void reset() {
        this.vars.clear();
        this.time_out = this.time_out_default;
        this.time_start = 0;
        this.target_field_id = 0;
    }

    public synchronized boolean isStarted() {
        return this.time_start != 0;
    }

    // manualstart のフィールドセットの開始
    public synchronized boolean startManually() {
        if (isStarted()) {
            return false;
        }
        resetQuestTime();
        return true;
    }

    // 経過時間 (秒)
    public synchronized int getQuestTime() {
        if (!isStarted()) {
            return 0;
        }
        return (int) ((System.currentTimeMillis() - this.time_start) / 1000);
    }

    // 経過時間のリセット
    public synchronized void resetQuestTime() {
        this.time_start = System.currentTimeMillis();
        sendClockAll();
    }

    // 残り時間の変更 (秒)
    public synchronized void resetTimeOut(int time_out) {
        this.time_out = time_out;
        resetQuestTime();
    }

    public synchronized int getRemainTime() {
        return getRemainTime(System.currentTimeMillis());
    }

    // 時計の残り時間 (制限時間がない場合は -1)
    private synchronized int getClockTime(long time_current) {
        if (!isStarted() || this.time_out <= 0) {
            return -1;
        }
        return getRemainTime(time_current);
    }

    private int getRemainTime(long time_current) {
        long remain = this.time_start + this.time_out * 1000L - time_current;
        if (remain <= 0) {
            return 0;
        }
        return (int) ((remain + 999) / 1000);
    }

    private void sendClockAll() {
        if (this.time_out <= 0) {
            return;
        }
        for (TacosCharacter user : getUsers()) {
            user.SendPacket(ResCField.Clock(getRemainTime()));
        }
    }

    // 制限時間切れの場合の移動先
    public synchronized void setTargetFieldID(int field_id) {
        this.target_field_id = field_id;
    }

    public synchronized int getTargetFieldID() {
        return this.target_field_id;
    }

    public synchronized String getVar(String key) {
        return this.vars.getOrDefault(key, "");
    }

    public synchronized void setVar(String key, String value) {
        this.vars.put(key, value);
    }

    public synchronized void broadcastMsg(OpsBroadcastMsg type, String message) {
        for (TacosCharacter user : getUsers()) {
            user.SendPacket(ResCWvsContext.BroadcastMsg(type, PB_BroadcastMsg.builder().message(message).build()));
        }
    }

    public synchronized void incExpAll(int exp) {
        for (TacosCharacter user : getUsers()) {
            user.gainExp(exp, true, true, true);
        }
    }

    public synchronized void transferFieldAll(int field_id, String portal_name) {
        for (TacosCharacter user : getUsers()) {
            user.changeMapById(field_id, portal_name);
        }
    }

    private TacosReactor getReactor(int index, String reactor_name) {
        int field_id = getFieldId(index);
        if (field_id == 0) {
            return null;
        }
        TacosMap map = this.channel.findMap(field_id);
        if (map == null) {
            return null;
        }
        return map.getReactorByName(reactor_name);
    }

    public synchronized int getReactorState(int index, String reactor_name) {
        TacosReactor reactor = getReactor(index, reactor_name);
        if (reactor == null) {
            return -1;
        }
        return reactor.getState();
    }

    public synchronized boolean setReactorState(int index, String reactor_name, int state) {
        TacosReactor reactor = getReactor(index, reactor_name);
        if (reactor == null) {
            return false;
        }
        reactor.forceHitReactor((byte) state);
        return true;
    }
}
