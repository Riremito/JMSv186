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
import config.DebugConfig;
import config.ServerConfig;
import debug.Debug;
import debug.DebugAutoLogin;
import handling.channel.handler.InterServerHandler;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import packet.ClientPacket;
import packet.response.ResCLogin;
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
            // GameGuard
            case CP_T_UpdateGameGuard: {
                c.SendPacket(ResCLogin.CheckGameGuardUpdate());
                break;
            }
            // ログイン画面
            case CP_CreateSecurityHandle: {
                c.SendPacket(ResCLogin.LoginAUTH(cp, c));
                return true;
            }
            // ログイン
            case CP_CheckPassword: {
                if (login(cp, c)) {
                    InterServerHandler.SetLogin(false);
                    Debug.InfoLog("[LOGIN MAPLEID] \"" + c.getAccountName() + "\"");
                    c.SendPacket(SocketPacket.AuthenMessage());
                }
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
                // +p
                ServerListRequest(c);
                return true;
            }
            // サーバーの状態
            case CP_CheckUserLimit: {
                // +p
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
            // キャラクター選択
            case CP_SelectCharacter:
            case CP_CheckPinCode: {
                if (Character_WithSecondPassword(cp, c)) {
                    InterServerHandler.SetLogin(false);
                }
                return true;
            }
            // ログイン画面に到達
            case REACHED_LOGIN_SCREEN: {
                // @0018
                // ログイン画面に到達した場合に送信される

                if (DebugConfig.auto_login) {
                    DebugAutoLogin.AutoLogin(c);
                }
                return true;
            }
            case CP_ViewAllChar: {
                c.SendPacket(ResCLogin.ViewAllCharResult_Alloc(c));
                c.SendPacket(ResCLogin.ViewAllCharResult(c));
                return true;
            }
            default: {
                break;
            }
        }

        return false;
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
                LoginRequest.AddStarterSet(newchar);
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
            LoginRequest.AddStarterSet(newchar);
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

    public static final boolean login(ClientPacket cp, final MapleClient c) {
        String login = new String(cp.DecodeBuffer());
        final String pwd = new String(cp.DecodeBuffer());
        return LoginRequest.login(c, login, pwd);
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
        LoginRequest.CharlistRequest(c, server, channel + 1);
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

    public static final boolean Character_WithSecondPassword(ClientPacket cp, MapleClient c) {
        final int charId = cp.Decode4();
        return LoginRequest.Character_WithSecondPassword(c, charId);
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
}
