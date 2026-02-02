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
package tacos.packet.request;

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventory;
import odin.client.inventory.MapleInventoryType;
import tacos.config.Content;
import tacos.config.ContentState;
import tacos.config.DeveloperMode;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import tacos.wz.data.EtcWz;
import tacos.wz.ids.DWI_Validation;
import tacos.database.query.DQ_Accounts;
import tacos.database.query.DQ_Character_slots;
import tacos.database.query.DQ_Characters;
import tacos.debug.DebugLogger;
import tacos.debug.DebugUser;
import java.util.ArrayList;
import java.util.Random;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsBodyPart;
import tacos.packet.ops.OpsNewCharacter;
import tacos.packet.response.ResCClientSocket;
import tacos.packet.response.ResCLogin;
import tacos.packet.response.ResCLogin.LoginResult;
import odin.server.MapleItemInformationProvider;
import tacos.packet.ClientPacketHeader;
import tacos.server.TacosChannel;
import tacos.server.TacosLogin;
import tacos.server.TacosWorld;

/**
 *
 * @author Riremito
 */
public class ReqCLogin {

    private TacosLogin login_server;

    public ReqCLogin(TacosLogin login_server) {
        this.login_server = login_server;
    }

    // CLogin::OnPacket
    public boolean OnPacket(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_CheckPassword: {
                // ログイン
                if (OnCheckPassword(client, cp)) {
                    client.getLoginServer().removeClient(client);
                    client.getLoginServer().addAuthorizedClient(client);
                    DebugLogger.InfoLog("[LOGIN MAPLEID] \"" + client.getMapleId() + "\"");
                    if (ContentState.CS_NETCAFE.get()) {
                        client.SendPacket(ResCClientSocket.AuthenMessage());
                    }
                }
                return true;
            }
            case CP_Check2ndPassword: {
                // 2次パスワード入力
                DebugLogger.TestLog("Check2ndPassword");
                OnWorldInfoRequest(client);
                return true;
            }
            case CP_WorldInfoRequest: {
                // ワールド情報の取得
                OnWorldInfoRequest(client);
                return true;
            }
            case CP_SelectWorld: {
                // チャンネル選択
                OnSelectWorld(client, cp);
                return true;
            }
            case CP_CheckUserLimit: {
                // JMSは不要
                OnCheckUserLimit(client, cp);
                return true;
            }
            case CP_CheckDuplicatedID: {
                // キャラクター名の確認
                String character_name = cp.DecodeStr();
                OnCheckDuplicatedID(client, character_name);
                return true;
            }
            case CP_CreateNewCharacter: {
                // キャラクター作成
                OnCreateNewCharacter(client, cp);
                return true;
            }
            case CP_DeleteCharacter: {
                // キャラクター削除
                OnDeleteCharacter(client, cp);
                return true;
            }
            case CP_CheckPinCode: {
                DebugLogger.TestLog("CheckPinCode");
                String password_2 = cp.DecodeStr(); // 2次パスワード (KMS160)
                int character_id = cp.Decode4();

                OnSelectCharacter(client, character_id);
                return true;
            }
            case CP_SelectCharacter: {
                // キャラクター選択
                int character_id = cp.Decode4();
                OnSelectCharacter(client, character_id);
                return true;
            }
            case CP_ViewAllChar: {
                // JMS186 : @000A
                client.SendPacket(ResCLogin.ViewAllCharResult(client, true));
                client.SendPacket(ResCLogin.ViewAllCharResult(client, false));
                return true;
            }
            case CP_JMS_CheckGameGuardUpdated: {
                // JMS147 : @0010
                // ログインボタンの有効化 (GameGuard Update)
                client.SendPacket(ResCLogin.CheckGameGuardUpdated(true));
                return true;
            }
            case CP_JMS_MapLogin: {
                // JMS186 : @0018
                // MapLoginの表示が完了
                return true;
            }
            case CP_JMS_SafetyPassword: {
                // JMS186 : @0019
                // 安心パスワード
                return true;
            }
            case CP_JMS_GetMapLogin: {
                // JMS186 : @001A
                // MapLoginの設定を取得 (UI.wz/MapLogin.img)
                client.SendPacket(ResCLogin.SetMapLogin("MapLogin"));
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // KMS beta to KMS149 and JMS302.
    public boolean OnCheckPassword(MapleClient client, ClientPacket cp) {
        // KMS160 or later, JMS308 or later.
        if (Version.GreaterOrEqual(Region.KMS, 160) || Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.EMS, 89)) {
            return OnCheckPassword_KMS160(client, cp);
        }
        // KMS149 or before, JMS302 or before.
        String maple_id = cp.DecodeStr(); // clean GMS83+ clients set password here by NMCO.
        String password = cp.DecodeStr(); // clean GMS83+ clients set passport here by NMCO.
        byte machine_id[] = cp.DecodeBuffer(16);

        // you can ignore all data after hwid.
        int unk1 = cp.Decode4(); // 0
        byte unk2 = (Version.GreaterOrEqual(Region.KMS, 31) || Version.GreaterOrEqual(Region.JMS, 131)) ? cp.Decode1() : 2; // old KMS uses 0?
        byte unk3 = (Version.GreaterOrEqual(Region.JMS, 147)) ? cp.Decode1() : 0; // JMS147
        // GMS83, BYTE
        // GMS83, DWORD

        client.setMachineId(machine_id);
        return checkLogin(client, maple_id, password);
    }

    // after KMS160 and JMS308.
    // around phantom or tempest update.
    public boolean OnCheckPassword_KMS160(MapleClient client, ClientPacket cp) {
        byte machine_id[] = cp.DecodeBuffer(16);
        int unk1 = cp.Decode4(); // 0
        byte unk2 = cp.Decode1(); // 2
        byte unk3 = (Version.GreaterOrEqual(Region.KMS, 160) || Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.EMS, 89)) ? cp.Decode1() : 0;
        byte unk4 = (Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.EMS, 89)) ? cp.Decode1() : 0;
        String maple_id = cp.DecodeStr();
        String password = cp.DecodeStr();

        client.setMachineId(machine_id);
        return checkLogin(client, maple_id, password);
    }

    public boolean OnCreateNewCharacter(MapleClient c, ClientPacket cp) {
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
        if (Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.KMS, 169) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 126)) {
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

        if (ServerConfig.KMS138orLater() || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            character_gender = cp.Decode1();
            if (!Version.Equal(Region.KMST, 391)) {
                skin_color = cp.Decode1();
            }
            int body_part_count = cp.Decode1();

            face_id = cp.Decode4();
            body_part_count--;
            hair_id = cp.Decode4();
            body_part_count--;
            if ((Region.IsEMS() && !Version.GreaterOrEqual(Region.EMS, 89)) || Version.GreaterOrEqual(Region.GMS, 111)) { // ?_? mercdes OK
                OpsNewCharacter onc = OpsNewCharacter.find(job_type);
                if (!OpsNewCharacter.isBadCodedJobs_GMS(onc)) {
                    hair_color = cp.Decode4();
                    body_part_count--;
                    skin_color = cp.Decode4();
                    body_part_count--;
                }
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
                DebugLogger.DebugLog("dice error");
                c.SendPacket(ResCLogin.CreateNewCharacterResult(null, false));
                return false;
            }
        }
        // data check
        if (!DWI_Validation.isValidFaceID(face_id) || !DWI_Validation.isValidHairID(hair_id)) {
            DebugLogger.DebugLog("Character creation error");
            c.SendPacket(ResCLogin.CreateNewCharacterResult(null, false));
            return false;
        }
        // name check
        if (!checkCharacterName(character_name)) {
            c.SendPacket(ResCLogin.CreateNewCharacterResult(null, false));
            return false;
        }

        MapleCharacter newchar = MapleCharacter.getDefault(c);
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
        c.SendPacket(ResCLogin.CreateNewCharacterResult(newchar, true));
        c.addCharacter(newchar);
        return true;
    }

    public boolean SetDefaultEquip(MapleCharacter newchar, int item_id) {
        if (!DWI_Validation.isValidItemID(item_id)) {
            DebugLogger.ErrorLog("SetDefaultEquip, item_id = " + item_id);
            return false;
        }

        MapleInventory mv_equipped = newchar.getInventory(MapleInventoryType.EQUIPPED);
        MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
        IItem item = miip.getEquipById(item_id);
        OpsBodyPart bodypart = OpsBodyPart.get_bodypart_from_item(item_id);
        DebugLogger.DebugLog("SetDefaultEquip, item_id = " + item_id + ", slot = " + -bodypart.get());
        item.setPosition((short) -bodypart.get());
        mv_equipped.addFromDB(item);
        return true;
    }

    public void OnCheckDuplicatedID(MapleClient c, String character_name) {
        boolean isOK = checkCharacterName(character_name);

        c.SendPacket(ResCLogin.CheckDuplicatedIDResult(character_name, isOK));
    }

    public boolean OnSelectWorld(MapleClient client, ClientPacket cp) {
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

        int world = cp.Decode1(); // nWorldID
        int channel = cp.Decode1(); // nChannelID

        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 83) || Region.IsIMS() || Version.GreaterOrEqual(Region.CMS, 104)) {
            int ip = cp.Decode4(); // S_addr
        }

        // もみじ(1)
        if (world == 1) {
            client.SendPacket(ResCLogin.SelectWorldResult(client, ResCLogin.LoginResult.TOO_MANY_USERS));
            return false;
        }
        // 強制的にかえで(0)に書き換える
        if (world == 12) {
            world = 0;
        }
        // 選択中のワールドを設定
        client.setSelectedWorld(world);
        client.setSelectedChannel(channel);
        DQ_Character_slots.setCharacterSlots(client);
        client.SendPacket(ResCLogin.SelectWorldResult(client, ResCLogin.LoginResult.SUCCESS));
        return true;
    }

    public boolean OnDeleteCharacter(MapleClient c, ClientPacket cp) {
        // BB後
        if (Version.PostBB() && !Region.IsKMS()) {
            String MapleID = cp.DecodeStr();
            if (!MapleID.equals(c.getMapleId())) {
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

        final int character_id = cp.Decode4();
        if (!c.checkCharacterId(character_id)) {
            c.loginFailed("OnDeleteCharacter");
            return false;
        }

        boolean success = DQ_Characters.deleteCharacter(c, character_id);
        c.SendPacket(ResCLogin.DeleteCharacterResult(character_id, success));
        return success;
    }

    // JMS以外必要
    public void OnCheckUserLimit(MapleClient c, ClientPacket cp) {
        short world_id = cp.Decode2();
        c.SendPacket(ResCLogin.CheckUserLimitResult(this.login_server.getWolrdStatus(world_id)));
    }

    public void OnWorldInfoRequest(MapleClient c) {
        for (TacosWorld world : TacosWorld.getWorlds()) {
            c.SendPacket(ResCLogin.WorldInformation(world));
        }
        c.SendPacket(ResCLogin.WorldInformation(null));

        if (ServerConfig.JMS186orLater()) {
            c.SendPacket(ResCLogin.RecommendWorldMessage());
            c.SendPacket(ResCLogin.LatestConnectedWorld());
        }

    }

    public boolean OnSelectCharacter(MapleClient client, int character_id) {
        if (!client.checkCharacterId(character_id)) {
            client.loginFailed("OnSelectCharacter");
            return false;
        }
        TacosChannel game_server = TacosWorld.find(client.getSelectedWorld()).getChannelServer(client.getSelectedChannel() + 1);
        client.sendSelectCharacterResult(game_server, character_id);
        client.getLoginServer().removeAuthorizedClient(client);
        return true;
    }

    // TODO : move to other class.
    public final boolean checkLogin(MapleClient c, String maple_id, String password) {
        if (5 <= c.loginAttempt()) {
            c.SendPacket(ResCLogin.CheckPasswordResult(c, LoginResult.SYSTEM_ERROR));
            return false;
        }
        boolean endwith_ = false;
        boolean startwith_GM = false;
        // MapleIDは最低4文字なので、5文字以上の場合に性別変更の特殊判定を行う
        if (maple_id.length() >= 5 && maple_id.endsWith("_")) {
            maple_id = maple_id.substring(0, maple_id.length() - 1);
            endwith_ = true;
            DebugLogger.InfoLog("[FEMALE MODE] \"" + maple_id + "\"");
        }
        if (DeveloperMode.DM_GM_ACCOUNT.get()) {
            if (maple_id.startsWith("GM")) {
                startwith_GM = true;
                DebugLogger.InfoLog("[GM MODE] \"" + maple_id + "\"");
            }
        }
        c.setMapleId(maple_id);
        int loginok = DQ_Accounts.login(c, maple_id, password);
        if (loginok == 5) {
            if (DQ_Accounts.autoRegister(maple_id, password)) {
                loginok = DQ_Accounts.login(c, maple_id, password);
            }
        }
        // アカウントの性別変更
        if (endwith_) {
            c.setGender((byte) 1);
        }
        // GM test
        if (startwith_GM) {
            c.setGameMaster(true);
        }
        if (loginok != 0) {
            c.SendPacket(ResCLogin.CheckPasswordResult(c, loginok));
        } else {
            c.resetLoginAttempt();
            registerClient(c);
            return true;
        }
        return false;
    }

    private static long lastUpdate = 0;

    public void registerClient(MapleClient client) {
        if (this.login_server.isAdminOnly() && !client.isGameMaster()) {
            client.SendPacket(ResCLogin.CheckPasswordResult(client, ResCLogin.LoginResult.INVALID_ADMIN_IP));
            return;
        }
        if (System.currentTimeMillis() - lastUpdate > 600000) {
            // Update once every 10 minutes
            lastUpdate = System.currentTimeMillis();
        }
        if (DQ_Accounts.finishLogin(client)) {
            Random rand = new Random();
            long client_key = rand.nextLong();
            client.setClientKey(client_key);
            client.SendPacket(ResCLogin.CheckPasswordResult(client, ResCLogin.LoginResult.SUCCESS));
        } else {
            client.SendPacket(ResCLogin.CheckPasswordResult(client, ResCLogin.LoginResult.ALREADY_LOGGEDIN));
            return;
        }
        // 2次パスワード要求する場合は入力を待つ必要がある, -1で無視すれば不要
        OnWorldInfoRequest(client);
    }

    public boolean checkCharacterName(final String character_name) {
        if (character_name.getBytes().length < 2) {
            return false;
        }
        if ((Content.CharacterNameLength.getInt() - 1) < character_name.getBytes().length) {
            return false;
        }
        if (EtcWz.get().isForbiddenName(character_name)) {
            return false;
        }
        // already registered
        if (DQ_Characters.getIdByName(character_name) != -1) {
            return false;
        }
        return true;
    }

}
