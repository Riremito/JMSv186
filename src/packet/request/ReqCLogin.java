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
import client.MapleCharacterUtil;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import debug.Debug;
import debug.DebugUser;
import handling.channel.ChannelServer;
import handling.channel.handler.InterServerHandler;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import java.util.List;
import java.util.Map;
import packet.ClientPacket;
import packet.response.ResCLogin;
import packet.response.ResCLogin.LoginResult;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import wz.LoadData;

/**
 *
 * @author Riremito
 */
public class ReqCLogin {

    public static boolean OnPacket(ClientPacket.Header header, ClientPacket cp, MapleClient c) {
        switch (header) {
            // ログイン
            // CClientSocket::OnCheckPassword
            case CP_CheckPassword: {
                if (OnCheckPassword(cp, c)) {
                    InterServerHandler.SetLogin(false);
                    Debug.InfoLog("[LOGIN MAPLEID] \"" + c.getAccountName() + "\"");
                    c.SendPacket(ResCLogin.AuthenMessage());
                }
                return true;
            }
            case CP_AliveAck: {
                return true;
            }
            case CP_Check2ndPassword: {
                if (ServerConfig.IsJMS() && ServerConfig.IsPostBB()) {
                    ServerListRequest(c);
                }
                return true;
            }
            // サーバー一覧
            case CP_WorldInfoRequest: {
                ServerListRequest(c);
                return true;
            }
            // サーバーの状態
            case CP_CheckUserLimit: {
                ServerStatusRequest(c);
                return true;
            }
            // キャラクター一覧
            case CP_SelectWorld: {
                CharlistRequest(cp, c);
                return true;
            }
            // キャラクター作成時の名前重複確認
            case CP_CheckDuplicatedID: {
                // p
                CheckCharName(cp, c);
                return true;
            }
            // キャラクター作成
            case CP_CreateNewCharacter: {
                CreateChar(cp, c);
                return true;
            }
            // キャラクター削除
            case CP_DeleteCharacter: {
                DeleteChar(cp, c);
                return true;
            }
            // クラッシュデータ
            case CP_ExceptionLog: {
                // @000F EncodeBuffer(CrashDumpLog)
                // 起動時に何らかの条件で前回のクラッシュの詳細のテキストが送信される
                // 文字列で送信されているがnullで終わっていないので注意
                return true;
            }
            case CP_SecurityPacket: {
                return true;
            }
            // キャラクター選択
            case CP_SelectCharacter:
            case CP_CheckPinCode: {
                if (OnSelectCharacter(cp, c)) {
                    InterServerHandler.SetLogin(false);
                }
                return true;
            }
            case CP_ViewAllChar: {
                c.SendPacket(ResCLogin.ViewAllCharResult(c, true));
                c.SendPacket(ResCLogin.ViewAllCharResult(c, false));
                return true;
            }
            case CP_JMS_CheckGameGuardUpdated: {
                // ログインボタン有効化 (GG専用)
                c.SendPacket(ResCLogin.CheckGameGuardUpdated(true));
                return true;
            }
            case CP_JMS_MapLogin: {
                // クライアントがログイン画面に移行した場合に送信される
                return true;
            }
            case CP_JMS_GetMapLogin: {
                // @0018
                // クライアントがログイン画面に移行した場合に送信される (初回限定)
                // BIGBANG前後のJMSでMapLoginが複数存在するバージョンで必要となる
                c.SendPacket(ResCLogin.SetMapLogin("MapLogin"));
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // login
    public static final boolean OnCheckPassword(ClientPacket cp, MapleClient c) {
        String maple_id = new String(cp.DecodeBuffer());
        String password = new String(cp.DecodeBuffer());
        return OnCheckPassword(c, maple_id, password);
    }

    public static final boolean OnCheckPassword(MapleClient c, String maple_id, String password) {
        c.loginAttempt++;
        if (5 < c.loginAttempt) {
            c.SendPacket(ResCLogin.CheckPasswordResult(c, LoginResult.SYSTEM_ERROR));
            //c.getSession().close();
            return false;
        }
        boolean endwith_ = false;
        boolean startwith_GM = false;
        // MapleIDは最低4文字なので、5文字以上の場合に性別変更の特殊判定を行う
        if (maple_id.length() >= 5 && maple_id.endsWith("_")) {
            maple_id = maple_id.substring(0, maple_id.length() - 1);
            endwith_ = true;
            Debug.InfoLog("[FEMALE MODE] \"" + maple_id + "\"");
        }
        if (ServerConfig.IsGMTestMode()) {
            if (maple_id.startsWith("GM")) {
                startwith_GM = true;
                Debug.InfoLog("[GM MODE] \"" + maple_id + "\"");
            }
        }
        c.setAccountName(maple_id);
        int loginok = c.login(maple_id, password);
        if (loginok == 5) {
            if (c.auto_register(maple_id, password) == 1) {
                Debug.InfoLog("[NEW MAPLEID] \"" + maple_id + "\"");
                loginok = c.login(maple_id, password);
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
        if (loginok != 0) {
            c.SendPacket(ResCLogin.CheckPasswordResult(c, loginok));
        } else {
            c.loginAttempt = 0;
            registerClient(c);
            return true;
        }
        return false;
    }

    private static long lastUpdate = 0;

    public static void registerClient(final MapleClient c) {
        if (LoginServer.isAdminOnly() && !c.isGm()) {
            c.SendPacket(ResCLogin.CheckPasswordResult(c, ResCLogin.LoginResult.INVALID_ADMIN_IP));
            return;
        }
        if (System.currentTimeMillis() - lastUpdate > 600000) {
            // Update once every 10 minutes
            lastUpdate = System.currentTimeMillis();
            final Map<Integer, Integer> load = ChannelServer.getChannelLoad();
            int usersOn = 0;
            if (load == null || load.size() <= 0) {
                // In an unfortunate event that client logged in before load
                lastUpdate = 0;
                c.SendPacket(ResCLogin.CheckPasswordResult(c, ResCLogin.LoginResult.ALREADY_LOGGEDIN));
                return;
            }
            final double loadFactor = 1200 / ((double) LoginServer.getUserLimit() / load.size());
            for (Map.Entry<Integer, Integer> entry : load.entrySet()) {
                usersOn += entry.getValue();
                load.put(entry.getKey(), Math.min(1200, (int) (entry.getValue() * loadFactor)));
            }
            LoginServer.setLoad(load, usersOn);
            lastUpdate = System.currentTimeMillis();
        }
        if (c.finishLogin() == 0) {
            c.SendPacket(ResCLogin.CheckPasswordResult(c, ResCLogin.LoginResult.SUCCESS));
        } else {
            c.SendPacket(ResCLogin.CheckPasswordResult(c, ResCLogin.LoginResult.ALREADY_LOGGEDIN));
            return;
        }
        // 2次パスワード要求する場合は入力を待つ必要がある, -1で無視すれば不要
        ReqCLogin.ServerListRequest(c);
    }

    public static final void CreateChar(ClientPacket cp, final MapleClient c) {
        final String name = new String(cp.DecodeBuffer());
        // very old ver, please merge it
        if (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 164) {
            final int face = cp.Decode4();
            final int hair = cp.Decode4();
            final int hairColor = 0;
            final byte skinColor = (byte) 0;
            final int top = cp.Decode4();
            final int bottom = cp.Decode4();
            final int shoes = cp.Decode4();
            final int weapon = cp.Decode4();
            final byte gender = c.getGender();
            MapleCharacter newchar = MapleCharacter.getDefault(c, 1);
            // サイコロ
            if (ServerConfig.GetVersion() <= 131) {
                newchar.getStat().str = cp.Decode1();
                newchar.getStat().dex = cp.Decode1();
                newchar.getStat().int_ = cp.Decode1();
                newchar.getStat().luk = cp.Decode1();
            }
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
            if (MapleCharacterUtil.canCreateChar(name) && !LoginInformationProvider.getInstance().isForbiddenName(name)) {
                DebugUser.AddStarterSet(newchar);
                MapleCharacter.saveNewCharToDB(newchar, 1, false);
                c.getSession().write(ResCLogin.addNewCharEntry(newchar, true));
                c.createdChar(newchar.getId());
            } else {
                c.getSession().write(ResCLogin.addNewCharEntry(newchar, false));
            }
            return;
        }
        final int JobType = cp.Decode4();
        short db = 0;
        if ((ServerConfig.IsJMS() && 176 < ServerConfig.GetVersion()) || ServerConfig.IsTWMS() || ServerConfig.IsCMS() || ServerConfig.IsKMS()) {
            db = cp.Decode2();
        }
        if (ServerConfig.IsJMS() && 302 <= ServerConfig.GetVersion()) {
            cp.Decode1(); // 01
            cp.Decode1(); // 00
            cp.Decode1(); // 07
            // Kanna
            if (JobType == 9) {
                c.setGender((byte) 1);
            }
        }
        final int face = cp.Decode4();
        final int hair = cp.Decode4();
        final int hairColor = 0;
        final byte skinColor = (byte) 0;
        final int top = cp.Decode4();
        final int bottom = cp.Decode4();
        final int shoes = cp.Decode4();
        final int weapon = cp.Decode4();
        final byte gender = c.getGender();
        if (!LoadData.IsValidFaceID(face) || !LoadData.IsValidHairID(hair) || !LoadData.IsValidItemID(top) || !LoadData.IsValidItemID(bottom) || !LoadData.IsValidItemID(shoes) || !LoadData.IsValidItemID(weapon)) {
            Debug.DebugLog("Character creation error");
            c.getSession().write(ResCLogin.addNewCharEntry(null, false));
            return;
        }
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
            DebugUser.AddStarterSet(newchar);
            MapleCharacter.saveNewCharToDB(newchar, JobType, JobType == 1 && db > 0);
            c.getSession().write(ResCLogin.addNewCharEntry(newchar, true));
            c.createdChar(newchar.getId());
        } else {
            c.getSession().write(ResCLogin.addNewCharEntry(newchar, false));
        }
    }

    public static final void CheckCharName(ClientPacket cp, final MapleClient c) {
        String name = new String(cp.DecodeBuffer());
        c.getSession().write(ResCLogin.charNameResponse(name, !MapleCharacterUtil.canCreateChar(name) || LoginInformationProvider.getInstance().isForbiddenName(name)));
    }

    public static final void CharlistRequest(ClientPacket cp, final MapleClient c) {
        if (ServerConfig.IsKMS()) {
            byte unk = cp.Decode1();
        }
        int server = cp.Decode1();
        final int channel = cp.Decode1();
        // もみじ block test
        if (server == 1) {
            c.SendPacket(ResCLogin.getCharList(c, ResCLogin.LoginResult.TOO_MANY_USERS));
            return;
        }
        CharlistRequest(c, server, channel + 1);
    }

    public static final void DeleteChar(ClientPacket cp, final MapleClient c) {
        byte state = 0;
        // BB後
        if (ServerConfig.IsPostBB() && !ServerConfig.IsKMS()) {
            String MapleID = cp.DecodeStr();
            if (!MapleID.equals(c.getAccountName())) {
                // state = 0以外にすると切断されます
            }
        }
        if (ServerConfig.IsKMS()) {
            byte unk1 = cp.Decode1();
            int unk2 = cp.Decode4();
        }
        final int Character_ID = cp.Decode4();
        if (!c.login_Auth(Character_ID)) {
            c.getSession().close();
            return;
        }
        if (state == 0) {
            state = (byte) c.deleteCharacter(Character_ID);
        }
        c.getSession().write(ResCLogin.deleteCharResponse(Character_ID, state));
    }

    // 必要なさそう
    public static final void ServerStatusRequest(final MapleClient c) {
        // 0 = Select world normally
        // 1 = "Since there are many users, you may encounter some..."
        // 2 = "The concurrent users in this world have reached the max"
        final int numPlayer = LoginServer.getUsersOn();
        final int userLimit = LoginServer.getUserLimit();
        if (numPlayer >= userLimit) {
            c.getSession().write(ResCLogin.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.getSession().write(ResCLogin.getServerStatus(1));
        } else {
            c.getSession().write(ResCLogin.getServerStatus(0));
        }
    }

    public static final void ServerListRequest(final MapleClient c) {
        // かえで
        c.SendPacket(ResCLogin.getServerList(0));
        // もみじ (サーバーを分離すると接続人数を取得するのが難しくなる)
        c.SendPacket(ResCLogin.getServerList(1, false, 16));
        c.SendPacket(ResCLogin.getEndOfServerList());
        if (ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion()) {
            c.SendPacket(ResCLogin.RecommendWorldMessage());
            c.SendPacket(ResCLogin.LatestConnectedWorld());
        }
    }

    private static int SelectedChannel = 0;
    private static int SelectedWorld = 0;

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

    public static final boolean SelectCharacterTest(MapleClient c) {
        List<MapleCharacter> chars = c.loadCharacters(0);
        if (chars == null) {
            return false;
        }
        final int charId = chars.get(0).getId();
        return SelectCharacter(c, charId);
    }

    public static final boolean SelectCharacter(MapleClient c, int charId) {
        if (!c.login_Auth(charId)) {
            c.getSession().close();
            return false;
        }
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        c.SendPacket(ResCLogin.SelectCharacterResult(LoginServer.WorldPort[SelectedWorld] + SelectedChannel, charId));
        return true;
    }

    // CClientSocket::OnSelectCharacter
    public static final boolean OnSelectCharacter(ClientPacket cp, MapleClient c) {
        final int charId = cp.Decode4();
        return SelectCharacter(c, charId);
    }
}
