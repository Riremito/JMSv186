/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.login.handler;

import java.util.List;
import java.util.Calendar;
import client.inventory.IItem;
import client.inventory.Item;
import client.MapleClient;
import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.login.LoginWorker;
import packet.OutPacket;
import server.MapleItemInformationProvider;
import server.Start;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.packet.LoginPacket;
import tools.KoreanDateUtil;

public class CharLoginHandler {

    private static int SelectedWorld = 0;
    private static int SelectedChannel = 0;

    private static final boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        if (c.loginAttempt > 5) {
            return true;
        }
        return false;
    }

    public static final boolean login(OutPacket p, final MapleClient c) {
        String login = new String(p.DecodeBuffer());
        final String pwd = new String(p.DecodeBuffer());
        boolean endwith_ = false;

        // MapleIDは最低4文字なので、5文字以上の場合に性別変更の特殊判定を行う
        if (login.length() >= 5 && login.endsWith("_")) {
            login = login.substring(0, login.length() - 1);
            endwith_ = true;
        }

        c.setAccountName(login);
        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.hasBannedMac();

        int loginok = c.login(login, pwd, ipBan || macBan);

        if (loginok == 5) {
            if (c.auto_register(login, pwd) == 1) {
                loginok = c.login(login, pwd, ipBan || macBan);
            }
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
                c.getSession().write(LoginPacket.getLoginFailed(loginok));
            }
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            if (!loginFailCount(c)) {
                c.getSession().write(LoginPacket.getTempBan(KoreanDateUtil.getTempBanTimestamp(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            }
        } else {
            c.loginAttempt = 0;
            // アカウントの性別変更
            if (endwith_) {
                c.setGender((byte) 1);
            }
            LoginWorker.registerClient(c);
            return true;
        }
        return false;
    }

    public static final void ServerListRequest(final MapleClient c) {
        // かえで
        c.getSession().write(LoginPacket.getServerList(0));
        // もみじ (サーバーを分離すると接続人数を取得するのが難しくなる)
        c.getSession().write(LoginPacket.getServerList(1, false, 16));
        c.getSession().write(LoginPacket.getEndOfServerList());
    }

    // 必要なさそう
    public static final void ServerStatusRequest(final MapleClient c) {
        // 0 = Select world normally
        // 1 = "Since there are many users, you may encounter some..."
        // 2 = "The concurrent users in this world have reached the max"
        final int numPlayer = LoginServer.getUsersOn();
        final int userLimit = LoginServer.getUserLimit();
        if (numPlayer >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.getSession().write(LoginPacket.getServerStatus(1));
        } else {
            c.getSession().write(LoginPacket.getServerStatus(0));
        }
    }

    public static final void CharlistRequest(OutPacket p, final MapleClient c) {
        int server = p.Decode1();
        final int channel = p.Decode1() + 1;

        if (server == 12) {
            server = 0;
        }

        SelectedWorld = server;
        SelectedChannel = channel - 1;

        c.setWorld(server);
        //System.out.println("Client " + c.getSession().getRemoteAddress().toString().split(":")[0] + " is connecting to server " + server + " channel " + channel + "");
        c.setChannel(channel);

        final List<MapleCharacter> chars = c.loadCharacters(server);
        if (chars != null) {
            c.getSession().write(LoginPacket.getCharList(c.getSecondPassword() != null, chars, c.getCharacterSlots()));
        } else {
            c.getSession().close();
        }
    }

    public static final void CheckCharName(OutPacket p, final MapleClient c) {
        String name = new String(p.DecodeBuffer());
        c.getSession().write(LoginPacket.charNameResponse(name,
                !MapleCharacterUtil.canCreateChar(name) || LoginInformationProvider.getInstance().isForbiddenName(name)));
    }

    public static final void CreateChar(OutPacket p, final MapleClient c) {
        final String name = new String(p.DecodeBuffer());

        if (Start.getMainVersion() == 164) {
            final int face = p.Decode4();
            final int hair = p.Decode4();
            final int hairColor = 0;
            final byte skinColor = (byte) 0;
            final int top = p.Decode4();
            final int bottom = p.Decode4();
            final int shoes = p.Decode4();
            final int weapon = p.Decode4();
            final byte gender = c.getGender();

            MapleCharacter newchar = MapleCharacter.getDefault(c, 1);
            newchar.setWorld((byte) c.getWorld());
            newchar.setFace(face);
            newchar.setHair(hair + hairColor);
            newchar.setGender(gender);
            newchar.setName(name);
            newchar.setSkinColor(skinColor);

            MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
            final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

            IItem item = li.getEquipById(top);
            item.setPosition((byte) -5);
            equip.addFromDB(item);

            item = li.getEquipById(bottom);
            item.setPosition((byte) -6);
            equip.addFromDB(item);

            item = li.getEquipById(shoes);
            item.setPosition((byte) -7);
            equip.addFromDB(item);

            item = li.getEquipById(weapon);
            item.setPosition((byte) -11);
            equip.addFromDB(item);

            newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000013, (byte) 0, (short) 100, (byte) 0));
            newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000014, (byte) 0, (short) 100, (byte) 0));
            newchar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (byte) 0, (short) 1, (byte) 0));

            if (MapleCharacterUtil.canCreateChar(name) && !LoginInformationProvider.getInstance().isForbiddenName(name)) {
                addInitialItems(newchar);
                MapleCharacter.saveNewCharToDB(newchar, 1, false);
                c.getSession().write(LoginPacket.addNewCharEntry(newchar, true));
                c.createdChar(newchar.getId());
            } else {
                c.getSession().write(LoginPacket.addNewCharEntry(newchar, false));
            }
            return;
        }
        final int JobType = p.Decode4();

        short db = 0;

        if (Start.getMainVersion() > 176) {
            db = p.Decode2();
        }

        final int face = p.Decode4();
        final int hair = p.Decode4();
        final int hairColor = 0;
        final byte skinColor = (byte) 0;
        final int top = p.Decode4();
        final int bottom = p.Decode4();
        final int shoes = p.Decode4();
        final int weapon = p.Decode4();
        final byte gender = c.getGender();

        MapleCharacter newchar = MapleCharacter.getDefault(c, JobType);
        newchar.setWorld((byte) c.getWorld());
        newchar.setFace(face);
        newchar.setHair(hair + hairColor);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor(skinColor);

        MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        IItem item = li.getEquipById(top);
        item.setPosition((byte) -5);
        equip.addFromDB(item);

        if (db == 0) {
            item = li.getEquipById(bottom);
            item.setPosition((byte) -6);
            equip.addFromDB(item);
        }

        item = li.getEquipById(shoes);
        item.setPosition((byte) -7);
        equip.addFromDB(item);

        item = li.getEquipById(weapon);
        item.setPosition((byte) -11);
        equip.addFromDB(item);

        //blue/red pots
        if (JobType == 0) {
            newchar.setQuestAdd(MapleQuest.getInstance(20022), (byte) 1, "1");
            newchar.setQuestAdd(MapleQuest.getInstance(20010), (byte) 1, null); //>_>_>_> ugh
            newchar.setQuestAdd(MapleQuest.getInstance(20000), (byte) 1, null); //>_>_>_> ugh
            newchar.setQuestAdd(MapleQuest.getInstance(20015), (byte) 1, null); //>_>_>_> ugh
            newchar.setQuestAdd(MapleQuest.getInstance(20020), (byte) 1, null); //>_>_>_> ugh
        }

        if (MapleCharacterUtil.canCreateChar(name) && !LoginInformationProvider.getInstance().isForbiddenName(name)) {
            addInitialItems(newchar);
            MapleCharacter.saveNewCharToDB(newchar, JobType, JobType == 1 && db > 0);

            c.getSession().write(LoginPacket.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
        } else {
            c.getSession().write(LoginPacket.addNewCharEntry(newchar, false));
        }
    }

    public static void addInitialItems(final MapleCharacter chr) {
        MapleInventory use = chr.getInventory(MapleInventoryType.USE);
        MapleInventory etc = chr.getInventory(MapleInventoryType.ETC);
        MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIP);
        MapleInventory cash = chr.getInventory(MapleInventoryType.CASH);
        MapleInventory setup = chr.getInventory(MapleInventoryType.SETUP);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        // メル
        chr.setMeso(5000000);

        // パチンコ玉
        //chr.setTama(500000);
        // エリクサー
        use.addItem(new Item(2000004, (byte) 0, (short) 100, (byte) 0));
        use.addItem(new Item(2000004, (byte) 0, (short) 100, (byte) 0));
        use.addItem(new Item(2000004, (byte) 0, (short) 100, (byte) 0));
        use.addItem(new Item(2000004, (byte) 0, (short) 100, (byte) 0));
        // 万病治療薬
        use.addItem(new Item(2050004, (byte) 0, (short) 100, (byte) 0));
        // コーヒー牛乳
        use.addItem(new Item(2030008, (byte) 0, (short) 100, (byte) 0));
        // いちご牛乳
        use.addItem(new Item(2030009, (byte) 0, (short) 100, (byte) 0));
        // フルーツ牛乳
        use.addItem(new Item(2030010, (byte) 0, (short) 100, (byte) 0));
        // 帰還の書(ヘネシス)
        use.addItem(new Item(2030004, (byte) 0, (short) 100, (byte) 0));
        // 強化書
        use.addItem(new Item(2040303, (byte) 0, (short) 30, (byte) 0));
        use.addItem(new Item(2040506, (byte) 0, (short) 30, (byte) 0));
        use.addItem(new Item(2040710, (byte) 0, (short) 30, (byte) 0));
        use.addItem(new Item(2040807, (byte) 0, (short) 30, (byte) 0));
        use.addItem(new Item(2044703, (byte) 0, (short) 30, (byte) 0));
        use.addItem(new Item(2044503, (byte) 0, (short) 30, (byte) 0));
        use.addItem(new Item(2043803, (byte) 0, (short) 30, (byte) 0));
        use.addItem(new Item(2043003, (byte) 0, (short) 30, (byte) 0));
        use.addItem(new Item(2049100, (byte) 0, (short) 100, (byte) 0));
        use.addItem(new Item(2049003, (byte) 0, (short) 100, (byte) 0));

        if (Start.getMainVersion() >= 186) {
            use.addItem(new Item(2049300, (byte) 0, (short) 100, (byte) 0));
            use.addItem(new Item(2049400, (byte) 0, (short) 100, (byte) 0));
            use.addItem(new Item(2470000, (byte) 0, (short) 100, (byte) 0));
        }

        // 魔法の石
        etc.addItem(new Item(4006000, (byte) 0, (short) 100, (byte) 0));
        // 召喚の石
        etc.addItem(new Item(4006001, (byte) 0, (short) 100, (byte) 0));

        // 軍手(茶)
        equip.addItem(ii.getEquipById(1082149));
        // ドロシー(銀)
        equip.addItem(ii.getEquipById(1072264));
        // 冒険家のマント(黄)
        equip.addItem(ii.getEquipById(1102040));
        // 緑ずきん
        equip.addItem(ii.getEquipById(1002391));
        // オウルアイ
        equip.addItem(ii.getEquipById(1022047));
        // 犬鼻
        equip.addItem(ii.getEquipById(1012056));
        // メイプルシールド
        equip.addItem(ii.getEquipById(1092030));
        // タオル(黒)
        equip.addItem(ii.getEquipById(1050127));
        // バスタオル(黄)
        equip.addItem(ii.getEquipById(1051140));
        // エレメントピアス
        equip.addItem(ii.getEquipById(1032062));
        if (Start.getMainVersion() >= 186) {
            // 錬金術師の指輪
            equip.addItem(ii.getEquipById(1112400));
        }

        // ドラゴン(アビス)
        setup.addItem(new Item(3010047, (byte) 0, (short) 1, (byte) 0));

        // 拡声器
        cash.addItem(new Item(5071000, (byte) 0, (short) 100, (byte) 0));
        // アイテム拡声器
        cash.addItem(new Item(5076000, (byte) 0, (short) 100, (byte) 0));
        // 黒板
        cash.addItem(new Item(5370000, (byte) 0, (short) 1, (byte) 0));
        // 営業許可証
        cash.addItem(new Item(5140000, (byte) 0, (short) 1, (byte) 0));
        // 高性能テレポストーン
        cash.addItem(new Item(5041000, (byte) 0, (short) 100, (byte) 0));
        // ガシャポンチケット
        cash.addItem(new Item(5220000, (byte) 0, (short) 100, (byte) 0));
        if (Start.getMainVersion() >= 186) {
            // ビシャスのハンマー
            cash.addItem(new Item(5570000, (byte) 0, (short) 100, (byte) 0));
            // ミラクルキューブ
            cash.addItem(new Item(5062000, (byte) 0, (short) 100, (byte) 0));
            // ベガの呪文書(10%)
            cash.addItem(new Item(5610000, (byte) 0, (short) 100, (byte) 0));
            // ベガの呪文書(60%)
            cash.addItem(new Item(5610001, (byte) 0, (short) 100, (byte) 0));
        }
        // AP再分配の書
        cash.addItem(new Item(5050000, (byte) 0, (short) 100, (byte) 0));
        cash.addItem(new Item(5050000, (byte) 0, (short) 100, (byte) 0));

    }

    public static final void DeleteChar(OutPacket p, final MapleClient c) {
        final int Character_ID = p.Decode4();

        if (!c.login_Auth(Character_ID)) {
            c.getSession().close();
            return; // Attempting to delete other character
        }

        byte state = 0;

        if (state == 0) {
            state = (byte) c.deleteCharacter(Character_ID);
        }

        c.getSession().write(LoginPacket.deleteCharResponse(Character_ID, state));
    }

    public static final boolean Character_WithSecondPassword(OutPacket p, final MapleClient c) {
        final int charId = p.Decode4();

        if (loginFailCount(c) || !c.login_Auth(charId)) { // This should not happen unless player is hacking
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
}
