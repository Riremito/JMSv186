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
import config.DeveloperMode;
import config.Region;
import config.ServerConfig;
import config.Version;
import config.property.Property_Login;
import data.wz.DW_Etc;
import debug.Debug;
import debug.DebugUser;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import packet.ClientPacket;
import packet.ops.OpsBodyPart;
import packet.ops.OpsNewCharacter;
import packet.response.ResCClientSocket;
import packet.response.ResCLogin;
import packet.response.ResCLogin.LoginResult;
import server.MapleItemInformationProvider;
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
                    Debug.InfoLog("[LOGIN MAPLEID] \"" + c.getAccountName() + "\"");
                    c.SendPacket(ResCClientSocket.AuthenMessage());
                }
                return true;
            }
            case CP_AliveAck: {
                return true;
            }
            case CP_Check2ndPassword: {
                if (Region.IsJMS() && Version.PostBB()) {
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
                OnSelectCharacter(cp, c);
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
            case CP_UpdateScreenSetting: {
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
        if (Version.GreaterOrEqual(Region.KMS, 160) || Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148)) {
            byte hwid[] = cp.DecodeBuffer(16);
            int unk1 = cp.Decode4();
            byte unk2 = cp.Decode1();
            byte unk3 = cp.Decode1();
        }
        if (Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148)) {
            byte unk4 = cp.Decode1();
        }
        if (Version.GreaterOrEqual(Region.CMS, 104)) {
            byte hwid[] = cp.DecodeBuffer(16);
            int unk1 = cp.Decode4();
            byte unk2 = cp.Decode1();
        }
        String maple_id = cp.DecodeStr();
        String password = cp.DecodeStr();
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
        if (DeveloperMode.DM_GM_ACCOUNT.get()) {
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
            final double loadFactor = 1200 / ((double) Property_Login.getUserLimit() / load.size());
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

    public static boolean CreateChar(ClientPacket cp, final MapleClient c) {
        String character_name;
        byte character_gender = c.getGender();
        int job_type = 0;
        int job_id = 0;
        short job_dualblade = 0;
        int face_id = 0;
        int hair_id = 0;
        int hair_color = 0;
        int skin_color = 0;
        ArrayList<Integer> item_ids = new ArrayList<Integer>();
        int dice_str = 0;
        int dice_dex = 0;
        int dice_int = 0;
        int dice_luk = 0;

        character_name = cp.DecodeStr();
        if (Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.KMS, 197) || Version.GreaterOrEqual(Region.EMS, 89)) {
            int unk = cp.Decode4();
        }
        if (ServerConfig.JMS165orLater() && !(Region.IsGMS() && Version.getVersion() == 73)) {
            job_type = cp.Decode4();

            // バージョンによって異なる (左から順番)
            switch (OpsNewCharacter.find(job_type)) {
                case KnightsOfCygnus:
                    skin_color = 10;
                    job_id = 1000;
                    break;
                case Adventurers:
                case DualBlade:
                case CannonShooter:
                    job_id = 0;
                    break;
                case Aran:
                    skin_color = 11;
                    job_id = 2000;
                    break;
                case Evan:
                    job_id = 2001;
                    break;
                case Resistance:
                    job_id = 3000;
                    break;
                case Mercedes:
                    skin_color = 12;
                    job_id = 2002;
                    break;
                case Phantom:
                    job_id = 2003;
                    break;
                case DemonSlayer:
                    skin_color = 13;
                    job_id = 3001;
                    break;
                case Hayato:
                    job_id = 4001;
                    break;
                case Kanna:
                    job_id = 4002;
                    break;
                case Chivalrous:
                    job_id = 0;
                    break;
                case Luminous:
                    job_id = 2004;
                    break;
                case Kaizer:
                    job_id = 6000;
                    break;
                case AngelicBuster:
                    job_id = 6001;
                    break;
                default:
                    job_id = 0;
                    break;
            }
        }
        if (ServerConfig.JMS180orLater()) {
            job_dualblade = cp.Decode2(); // 1 = DB, 2 = キャノンシューター, 10 = 蒼龍
        }

        if (ServerConfig.KMS138orLater() || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
            character_gender = cp.Decode1();
            if (!Version.Equal(Region.KMST, 391)) {
                skin_color = cp.Decode1();
            }
            int body_part_count = cp.Decode1();

            face_id = cp.Decode4();
            body_part_count--;
            hair_id = cp.Decode4();
            body_part_count--;
            if (Region.IsEMS() && !Version.GreaterOrEqual(Region.EMS, 89)) { // ?_? mercdes OK
                hair_color = cp.Decode4();
                body_part_count--;
                skin_color = cp.Decode4();
                body_part_count--;
            }

            for (int i = 0; i < body_part_count; i++) {
                item_ids.add(cp.Decode4());
            }

        } else {
            int equip_top = 0;
            int equip_bottom = 0;
            int equip_shoes = 0;
            int equip_weapon = 0;

            face_id = cp.Decode4();
            hair_id = cp.Decode4();
            if (Region.IsMSEA() || Region.IsTHMS() || Region.IsGMS() || Region.IsEMS() || Region.IsBMS() || Region.IsVMS() || Region.IsBMS()) {
                hair_color = cp.Decode4();
                skin_color = cp.Decode4();
            }
            equip_top = cp.Decode4();
            equip_bottom = cp.Decode4();
            equip_shoes = cp.Decode4();
            equip_weapon = cp.Decode4();

            item_ids.add(equip_top);
            if (equip_bottom != 0) {
                item_ids.add(equip_bottom);
            }
            item_ids.add(equip_shoes);
            item_ids.add(equip_weapon);
        }

        if (Version.LessOrEqual(Region.JMS, 131)) {
            dice_str = cp.Decode1();
            dice_dex = cp.Decode1();
            dice_int = cp.Decode1();
            dice_luk = cp.Decode1();
            // dice check, 12-5-4-4
            if ((dice_str + dice_dex + dice_int + dice_luk) != 25
                    || dice_str < 4 || dice_str < 4 || dice_dex < 4 || dice_int < 4 || dice_luk < 4
                    || 12 < dice_str || 12 < dice_dex || 12 < dice_int || 12 < dice_luk) {
                Debug.DebugLog("dice error");
                c.SendPacket(ResCLogin.addNewCharEntry(null, false));
                return false;
            }
        }
        // data check
        if (!LoadData.IsValidFaceID(face_id) || !LoadData.IsValidHairID(hair_id)) {
            Debug.DebugLog("Character creation error");
            c.SendPacket(ResCLogin.addNewCharEntry(null, false));
            return false;
        }
        // name check
        if (!MapleCharacterUtil.canCreateChar(character_name) || DW_Etc.getInstance().isForbiddenName(character_name)) {
            c.SendPacket(ResCLogin.addNewCharEntry(null, false));
            return false;
        }

        MapleCharacter newchar = MapleCharacter.getDefault(c);
        newchar.setWorld((byte) c.getWorld());
        newchar.setFace(face_id);
        newchar.setHair(hair_id + hair_color);
        newchar.setGender(character_gender);
        newchar.setName(character_name);
        newchar.setSkinColor((byte) skin_color);
        newchar.setJob(job_id);
        newchar.setSubcategory(job_dualblade);

        // dice
        if (Version.LessOrEqual(Region.JMS, 131)) {
            newchar.getStat().str = dice_str;
            newchar.getStat().dex = dice_dex;
            newchar.getStat().int_ = dice_int;
            newchar.getStat().luk = dice_luk;
        }

        MapleInventory equip = newchar.getInventory(MapleInventoryType.EQUIPPED);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        for (int id : item_ids) {
            SetDefaultEquip(newchar, id);
        }

        DebugUser.AddStarterSet(newchar);
        MapleCharacter.saveNewCharToDB(newchar);
        c.SendPacket(ResCLogin.addNewCharEntry(newchar, true));
        c.createdChar(newchar.getId());
        return true;
    }

    public static boolean SetDefaultEquip(MapleCharacter newchar, int item_id) {
        if (!LoadData.IsValidItemID(item_id)) {
            Debug.ErrorLog("SetDefaultEquip, item_id = " + item_id);
            return false;
        }

        MapleInventory mv_equipped = newchar.getInventory(MapleInventoryType.EQUIPPED);
        MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
        IItem item = miip.getEquipById(item_id);
        OpsBodyPart bodypart = OpsBodyPart.get_bodypart_from_item(item_id);
        Debug.DebugLog("SetDefaultEquip, item_id = " + item_id + ", slot = " + -bodypart.get());
        item.setPosition((short) -bodypart.get());
        mv_equipped.addFromDB(item);
        return true;
    }

    public static final void CheckCharName(ClientPacket cp, final MapleClient c) {
        String name = cp.DecodeStr();
        c.getSession().write(ResCLogin.charNameResponse(name, !MapleCharacterUtil.canCreateChar(name) || DW_Etc.getInstance().isForbiddenName(name)));
    }

    public static final void CharlistRequest(ClientPacket cp, final MapleClient c) {
        if (Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.EMS, 89) || Region.IsKMS() || Region.IsIMS() || Version.GreaterOrEqual(Region.TWMS, 148)) {
            byte unk = cp.Decode1();
        }

        if (Version.GreaterOrEqual(Region.GMS, 83)) {
            byte m_nGameStartMode = cp.Decode1(); // m_nGameStartMode, always 2?
            if (m_nGameStartMode == 1) {
                String str = cp.DecodeStr();
                byte hwid[] = cp.DecodeBuffer(16);
                int GameRoomClient = cp.Decode4();
                int m_nGameStartMode_2 = cp.Decode1();
            }
        }

        int server = cp.Decode1(); // nWorldID
        final int channel = cp.Decode1(); // nChannelID)

        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 83) || Region.IsIMS() || Version.GreaterOrEqual(Region.CMS, 104)) {
            int ip = cp.Decode4(); // S_addr
        }

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
        if (Version.PostBB() && !Region.IsKMS()) {
            String MapleID = cp.DecodeStr();
            if (!MapleID.equals(c.getAccountName())) {
                // state = 0以外にすると切断されます
            }
        }
        if (Region.IsKMS()) {
            if (Version.GreaterOrEqual(Region.KMS, 160)) {
                String secondpw = cp.DecodeStr();
            } else {
                byte unk1 = cp.Decode1();
                int unk2 = cp.Decode4();
            }
        }

        if (Region.IsEMS() || Region.IsGMS()) {
            int unke = cp.Decode4();
        }
        if (Region.IsTHMS() || Region.IsVMS() || Region.IsBMS()) {
            String key = cp.DecodeStr(); // 32 bytes hex or PIC
        }

        final int Character_ID = cp.Decode4();
        if (!c.login_Auth(Character_ID)) {
            Debug.ErrorLog("Delete Character Error");
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
        final int userLimit = Property_Login.getUserLimit();
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
        if (ServerConfig.JMS186orLater()) {
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
        if (cp.GetOpcode() == ClientPacket.Header.CP_CheckPinCode) {
            String secondpw = cp.DecodeStr(); // KMS160
        }
        final int charId = cp.Decode4();
        return SelectCharacter(c, charId);
    }
}
