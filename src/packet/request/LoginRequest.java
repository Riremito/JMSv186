/*
 * Copyright (C) 2024 Riremito
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
package packet.request;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import config.DebugConfig;
import config.ServerConfig;
import debug.Debug;
import handling.login.LoginServer;
import handling.login.LoginWorker;
import java.util.Calendar;
import java.util.List;
import packet.response.ResCLogin;
import server.MapleItemInformationProvider;
import tools.MaplePacketCreator;
import wz.LoadData;

/**
 *
 * @author Riremito
 */
public class LoginRequest {

    private static int SelectedChannel = 0;
    private static int SelectedWorld = 0;

    private static final boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        if (c.loginAttempt > 5) {
            return true;
        }
        return false;
    }

    public static final void CharlistRequest(MapleClient c, int server, int channel) {
        if (server == 12) {
            server = 0;
        }
        SelectedWorld = server;
        SelectedChannel = channel - 1;
        c.setWorld(server);
        c.setChannel(channel);
        c.SendPacket(ResCLogin.getCharList(c, ResCLogin.LoginResult.SUCCESS));
    }

    public static boolean AddStarterSet(MapleCharacter chr) {
        if (!DebugConfig.starter_set) {
            return false;
        }
        // メル
        chr.setMeso(777000000);
        // パチンコ玉
        chr.setTama(500000);
        // エリクサー
        AddItem(chr, 2000004, 100);
        // 万病治療薬
        AddItem(chr, 2050004, 100);
        // コーヒー牛乳
        AddItem(chr, 2030008, 100);
        // いちご牛乳
        AddItem(chr, 2030009, 100);
        // フルーツ牛乳
        AddItem(chr, 2030010, 100);
        // 帰還の書(ヘネシス)
        AddItem(chr, 2030004, 100);
        // 強化書
        AddItem(chr, 2040303, 100);
        AddItem(chr, 2040506, 100);
        AddItem(chr, 2040710, 100);
        AddItem(chr, 2040807, 100);
        AddItem(chr, 2044703, 100);
        AddItem(chr, 2044503, 100);
        AddItem(chr, 2043803, 100);
        AddItem(chr, 2043003, 100);
        AddItem(chr, 2049100, 100);
        AddItem(chr, 2049003, 100);
        AddItem(chr, 2049300, 100);
        AddItem(chr, 2049400, 100);
        AddItem(chr, 2470000, 100);
        // 魔法の石
        AddItem(chr, 4006000, 100);
        // 召喚の石
        AddItem(chr, 4006001, 100);
        // 軍手(茶)
        AddItem(chr, 1082149);
        // ドロシー(銀)
        AddItem(chr, 1072264);
        // 冒険家のマント(黄)
        AddItem(chr, 1102040);
        // 緑ずきん
        AddItem(chr, 1002391);
        // オウルアイ
        AddItem(chr, 1022047);
        // 犬鼻
        AddItem(chr, 1012056);
        // メイプルシールド
        AddItem(chr, 1092030);
        // タオル(黒)
        AddItem(chr, 1050127);
        // バスタオル(黄)
        AddItem(chr, 1051140);
        // エレメントピアス
        AddItem(chr, 1032062);
        // 錬金術師の指輪
        AddItem(chr, 1112400);
        // ドラゴン(アビス)
        AddItem(chr, 3010047);
        // ポイントアイテム
        AddItem(chr, 5071000, 100); // 拡声器
        AddItem(chr, 5076000, 100); // アイテム拡声器
        AddItem(chr, 5370000, 100); // 黒板
        AddItem(chr, 5140000); // 営業許可証
        AddItem(chr, 5041000, 100); // 高性能テレポストーン
        AddItem(chr, 5220000, 100); // ガシャポンチケット
        AddItem(chr, 5570000, 100); // ビシャスのハンマー
        AddItem(chr, 5062000, 100); // ミラクルキューブ
        AddItem(chr, 5610000, 100); // ベガの呪文書(10%)
        AddItem(chr, 5610001, 100); // ベガの呪文書(60%)
        AddItem(chr, 5050000, 100); // AP再分配の書
        // プレミアムさすらいの商人ミョミョ
        return true;
    }

    // 初期アイテムをキャラクターデータに追加
    public static boolean AddItem(MapleCharacter chr, int itemid) {
        return AddItem(chr, itemid, 1);
    }

    public static boolean AddItem(MapleCharacter chr, int itemid, int count) {
        if (!LoadData.IsValidItemID(itemid)) {
            return false;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        // 存在しないitemid
        if (!ii.itemExists(itemid)) {
            Debug.ErrorLog("AddItem: " + itemid);
            return false;
        }
        switch (itemid / 1000000) {
            case 1: {
                MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIP);
                equip.addItem(ii.getEquipById(itemid));
                break;
            }
            case 2: {
                MapleInventory use = chr.getInventory(MapleInventoryType.USE);
                use.addItem(new Item(itemid, (byte) 0, (short) count, (byte) 0));
                break;
            }
            case 3: {
                MapleInventory setup = chr.getInventory(MapleInventoryType.SETUP);
                setup.addItem(new Item(itemid, (byte) 0, (short) 1, (byte) 0));
                break;
            }
            case 4: {
                MapleInventory etc = chr.getInventory(MapleInventoryType.ETC);
                etc.addItem(new Item(itemid, (byte) 0, (short) count, (byte) 0));
                break;
            }
            case 5: {
                MapleInventory cash = chr.getInventory(MapleInventoryType.CASH);
                cash.addItem(new Item(itemid, (byte) 0, (short) count, (byte) 0));
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }

    public static final boolean Character_WithSecondPassword(MapleClient c) {
        List<MapleCharacter> chars = c.loadCharacters(0);
        if (chars == null) {
            return false;
        }
        final int charId = chars.get(0).getId();
        return Character_WithSecondPassword(c, charId);
    }

    public static final boolean Character_WithSecondPassword(MapleClient c, int charId) {
        if (loginFailCount(c) || !c.login_Auth(charId)) {
            // This should not happen unless player is hacking
            c.getSession().close();
            return false;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        //c.getSession().write(MaplePacketCreator.getServerIP(Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1]), charId));
        c.getSession().write(MaplePacketCreator.getServerIP(LoginServer.WorldPort[SelectedWorld] + SelectedChannel, charId));
        return true;
    }

    public static final boolean login(MapleClient c, String login, String pwd) {
        boolean endwith_ = false;
        boolean startwith_GM = false;
        // MapleIDは最低4文字なので、5文字以上の場合に性別変更の特殊判定を行う
        if (login.length() >= 5 && login.endsWith("_")) {
            login = login.substring(0, login.length() - 1);
            endwith_ = true;
            Debug.InfoLog("[FEMALE MODE] \"" + login + "\"");
        }
        if (ServerConfig.IsGMTestMode()) {
            if (login.startsWith("GM")) {
                startwith_GM = true;
                Debug.InfoLog("[GM MODE] \"" + login + "\"");
            }
        }
        c.setAccountName(login);
        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.hasBannedMac();
        int loginok = c.login(login, pwd, ipBan || macBan);
        if (loginok == 5) {
            if (c.auto_register(login, pwd) == 1) {
                Debug.InfoLog("[NEW MAPLEID] \"" + login + "\"");
                loginok = c.login(login, pwd, ipBan || macBan);
            }
        }
        // アカウントの性別変更
        if (endwith_) {
            c.setGender((byte) 1);
        }
        // GM test
        if (startwith_GM) {
            c.setGM();
        }
        final Calendar tempbannedTill = c.getTempBanCalendar();
        if (loginok == 0 && (ipBan || macBan) && !c.isGm()) {
            loginok = 3;
            if (macBan) {
                // this is only an ipban o.O" - maybe we should refactor this a bit so it's more readable
                MapleCharacter.ban(c.getSession().getRemoteAddress().toString().split(":")[0], "Enforcing account ban, account " + login, false, 4, false);
            }
        }
        if (loginok != 0) {
            if (!loginFailCount(c)) {
                c.SendPacket(ResCLogin.CheckPasswordResult(c, loginok));
            }
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            if (!loginFailCount(c)) {
                c.SendPacket(ResCLogin.CheckPasswordResult(c, 2)); // ?
            }
        } else {
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
            return true;
        }
        return false;
    }

}
