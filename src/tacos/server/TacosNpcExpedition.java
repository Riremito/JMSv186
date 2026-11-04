/*
 * Copyright (C) 2025 Riremito
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

import java.util.HashMap;
import tacos.client.TacosCharacter;

/**
 *
 * @author Riremito
 */
// 遠征隊 (lua の zakum_accept, hontale_accept で NPC の変数に保存されている遠征隊の情報)
public class TacosNpcExpedition {

    // 遠征隊長以外の遠征隊員の最大数
    public static final int MEMBER_MAX = 29;
    // 停止リストの最大数
    public static final int BANNED_MAX = 10;
    // 遠征隊の申請の受付時間 (秒), lua の getQuestTime > 302
    private static final int RESET_TIME = 302;
    // チャンネル -> 名前 -> 遠征隊
    private static final HashMap<Integer, HashMap<String, TacosNpcExpedition>> expeditions = new HashMap<>();

    public static synchronized TacosNpcExpedition get(TacosCharacter chr, String name) {
        return expeditions.computeIfAbsent(chr.getChannelId(), k -> new HashMap<>()).computeIfAbsent(name, k -> new TacosNpcExpedition());
    }

    private String master = "";
    private String last_master = "";
    private final String[] members = new String[MEMBER_MAX];
    private final String[] banned = new String[BANNED_MAX];
    // フィールドセットが存在しない遠征隊用 (東京系ボス)
    private long start_time = 0;
    private boolean entered = false;
    private String ban_pending = "";

    private TacosNpcExpedition() {
        clear();
    }

    private void clear() {
        this.master = "";
        for (int i = 0; i < MEMBER_MAX; i++) {
            this.members[i] = "";
        }
        for (int i = 0; i < BANNED_MAX; i++) {
            this.banned[i] = "";
        }
    }

    // lua の zakum_reset, hontale_reset (受付時間を過ぎた場合に遠征隊の情報を初期化する)
    public synchronized void reset(TacosFieldSet field_set) {
        if (field_set == null || field_set.getQuestTime() <= RESET_TIME || field_set.getVar("reset").equals("1")) {
            return;
        }
        field_set.setVar("enter", "");
        field_set.setVar("dName", "");
        this.last_master = this.master;
        clear();
        field_set.setVar("reset", "1");
    }

    // フィールドセットが存在しない遠征隊の結成 (すでに遠征隊長がいる場合は false)
    public synchronized boolean start(String name) {
        if (!this.master.isEmpty()) {
            return false;
        }
        clear();
        this.master = name;
        this.start_time = System.currentTimeMillis();
        this.entered = false;
        this.ban_pending = "";
        return true;
    }

    // フィールドセットが存在しない遠征隊の初期化 (受付時間を過ぎた場合)
    public synchronized void reset() {
        if (this.start_time == 0 || System.currentTimeMillis() - this.start_time <= RESET_TIME * 1000L) {
            return;
        }
        this.entered = false;
        this.ban_pending = "";
        this.last_master = this.master;
        this.start_time = 0;
        clear();
    }

    // 遠征隊長が入場済み (lua の FieldSet の変数 enter)
    public synchronized boolean isEntered() {
        return this.entered;
    }

    public synchronized void setEntered(boolean entered) {
        this.entered = entered;
    }

    // 停止リストへの登録を検討中の遠征隊員 (lua の FieldSet の変数 dName)
    public synchronized String getBanPending() {
        return this.ban_pending;
    }

    public synchronized void setBanPending(String name) {
        this.ban_pending = name;
    }

    public synchronized String getMaster() {
        return this.master;
    }

    public synchronized void setMaster(String name) {
        this.master = name;
    }

    public synchronized String getLastMaster() {
        return this.last_master;
    }

    public synchronized boolean isMember(String name) {
        for (String member : this.members) {
            if (member.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isBanned(String name) {
        for (String member : this.banned) {
            if (member.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public synchronized int getMemberCount() {
        int count = 0;
        for (String member : this.members) {
            if (!member.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public synchronized int getBannedCount() {
        int count = 0;
        for (String member : this.banned) {
            if (!member.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    // 遠征隊員の名前 (空き枠は "")
    public synchronized String getMember(int index) {
        if (index < 0 || MEMBER_MAX <= index) {
            return "";
        }
        return this.members[index];
    }

    // 停止リストの名前 (空き枠は "")
    public synchronized String getBanned(int index) {
        if (index < 0 || BANNED_MAX <= index) {
            return "";
        }
        return this.banned[index];
    }

    // 空き枠に遠征隊員を追加する (空き枠がない場合は false)
    public synchronized boolean addMember(String name) {
        for (int i = 0; i < MEMBER_MAX; i++) {
            if (this.members[i].isEmpty()) {
                this.members[i] = name;
                return true;
            }
        }
        return false;
    }

    public synchronized boolean removeMember(String name) {
        for (int i = 0; i < MEMBER_MAX; i++) {
            if (this.members[i].equals(name)) {
                this.members[i] = "";
                return true;
            }
        }
        return false;
    }

    // 空き枠に停止リストを追加する (空き枠がない場合は false)
    public synchronized boolean addBanned(String name) {
        for (int i = 0; i < BANNED_MAX; i++) {
            if (this.banned[i].isEmpty()) {
                this.banned[i] = name;
                return true;
            }
        }
        return false;
    }

    public synchronized boolean removeBanned(int index) {
        if (index < 0 || BANNED_MAX <= index || this.banned[index].isEmpty()) {
            return false;
        }
        this.banned[index] = "";
        return true;
    }
}
