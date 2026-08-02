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
import tacos.config.Version;
import tacos.database.query.DQ_Accounts;
import tacos.database.query.DQ_Character_slots;
import tacos.database.query.DQ_Characters;
import tacos.debug.DebugLogger;
import tacos.debug.DebugUser;
import java.util.ArrayList;
import java.util.Random;
import odin.client.PlayerStats;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsBodyPart;
import tacos.packet.ops.OpsNewCharacter;
import tacos.packet.response.ResCClientSocket;
import tacos.packet.response.ResCLogin;
import odin.server.MapleItemInformationProvider;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ops.OpsLogin;
import tacos.packet.ops.OpsViewAllChar;
import tacos.server.TacosChannel;
import tacos.server.TacosWorld;
import tacos.wz.WzXML;
import tacos.wz.WzDataStorage;

/**
 *
 * @author Riremito
 */
public class ReqCLogin {

    // CLogin::OnPacket
    public static boolean OnPacket(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_CheckPassword: {
                // ログイン
                if (OnCheckPassword(client, cp)) {
                    client.getLoginServer().getClients().remove(client);
                    client.getLoginServer().getAuthorizedClients().add(client);
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
            case CP_LogoutWorld: {
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
            case CP_CreateNewCharacterInCS: {
                // キャラクターカード
                OnCreateNewCharacterInCS(client, cp);
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
                client.SendPacket(ResCLogin.ViewAllCharResult(client, OpsViewAllChar.VAC_ResCode_CountRelatedSvrs));
                client.SendPacket(ResCLogin.ViewAllCharResult(client, OpsViewAllChar.VAC_ResCode_Success));
                return true;
            }
            case CP_SelectCharacterByVAC: {
                OnSelectCharacterByVAC(client, cp);
                return true;
            }
            case CP_JMS_CheckGameGuardUpdated: {
                // JMS147 : @0010
                // ログインボタンの有効化 (GameGuard Update)
                client.SendPacket(ResCLogin.CheckGameGuardUpdated(true));
                return true;
            }
            case CP_EnableSPWRequest: {
                boolean isSPW = (cp.Decode1() != 0);
                int character_id = cp.Decode4();
                if (isSPW) {
                    String password2 = cp.DecodeStr();
                }
                OnSelectCharacter(client, character_id);
                return true;
            }
            case CP_CheckSPWRequest: {
                String password2 = cp.DecodeStr();
                int character_id = cp.Decode4();

                OnSelectCharacter(client, character_id);
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
                client.SendPacket(ResCLogin.SetMapLogin());
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // KMS beta to KMS149 and JMS302.
    public static boolean OnCheckPassword(MapleClient client, ClientPacket cp) {
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
    public static boolean OnCheckPassword_KMS160(MapleClient client, ClientPacket cp) {
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

    public static boolean OnCreateNewCharacter(MapleClient client, ClientPacket cp) {
        String character_name;
        byte character_gender = client.getGender();
        int job_type = 0;
        int job_id = 0;
        short job_dualblade = 0;
        int face_id = 0;
        int hair_id = 0;
        int hair_color = 0;
        int skin_color = 0;
        ArrayList<Integer> item_ids = new ArrayList<>();
        boolean is_dice = false;
        int dice_str = 0;
        int dice_dex = 0;
        int dice_int = 0;
        int dice_luk = 0;

        character_name = cp.DecodeStr();
        if (Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.KMS, 169) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 126)) {
            int unk = cp.Decode4();
        }
        if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 67) || Version.GreaterOrEqual(Region.JMS, 165) || Version.GreaterOrEqual(Region.CMS, 74) || Version.GreaterOrEqual(Region.TWMS, 96) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 73) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 55)) {
            if (Version.LessOrEqual(Region.GMS, 73)) {
                // none
            } else {
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
        }
        if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 92) || Version.GreaterOrEqual(Region.JMS, 180) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70)) {
            job_dualblade = cp.Decode2(); // 1 = DB, 2 = キャノンシューター, 10 = 蒼龍
        }

        if (Version.GreaterOrEqual(Region.KMS, 138) || Version.GreaterOrEqual(Region.KMST, 391) || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            character_gender = cp.Decode1();
            if (!Version.Equal(Region.KMST, 391)) {
                skin_color = cp.Decode1();
            }
            int body_part_count = cp.Decode1();

            face_id = cp.Decode4();
            body_part_count--;
            hair_id = cp.Decode4();
            body_part_count--;
            if (Version.GreaterOrEqual(Region.GMS, 111)) { // ?_? mercdes OK
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
            if (Region.MSEA.check() || Region.THMS.check() || Region.GMS.check() || Region.GMST.check() || Region.EMS.check() || Region.BMS.check() || Region.VMS.check() || Region.BMS.check()) {
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

        if (Version.LessOrEqual(Region.JMS, 147)) {
            is_dice = true;
            dice_str = cp.Decode1();
            dice_dex = cp.Decode1();
            dice_int = cp.Decode1();
            dice_luk = cp.Decode1();
            // dice check, 12-5-4-4
            if ((dice_str + dice_dex + dice_int + dice_luk) != 25
                    || dice_str < 4 || dice_str < 4 || dice_dex < 4 || dice_int < 4 || dice_luk < 4
                    || 12 < dice_str || 12 < dice_dex || 12 < dice_int || 12 < dice_luk) {
                DebugLogger.DebugLog("dice error");
                client.SendPacket(ResCLogin.CreateNewCharacterResult(null, OpsLogin.LoginResCode_Unknown));
                return false;
            }
        }
        // data check
        if (!WzDataStorage.FACE.check(face_id) || !WzDataStorage.HAIR.check(hair_id)) {
            DebugLogger.DebugLog("Character creation error");
            client.SendPacket(ResCLogin.CreateNewCharacterResult(null, OpsLogin.LoginResCode_Unknown));
            return false;
        }
        // name check
        if (!checkCharacterName(character_name)) {
            client.SendPacket(ResCLogin.CreateNewCharacterResult(null, OpsLogin.LoginResCode_InvalidCharacterName));
            return false;
        }

        MapleCharacter chr = new MapleCharacter();
        chr.init_step1();
        chr.setClient(client);
        chr.setFace(face_id);
        chr.setHair(hair_id + hair_color);
        chr.setGender(character_gender);
        chr.setName(character_name);
        chr.setSkinColor(skin_color);
        chr.setJob(job_id);
        chr.setSubcategory(job_dualblade);

        PlayerStats stat = chr.getStat();
        stat.str = is_dice ? dice_str : 12;
        stat.dex = is_dice ? dice_dex : 5;
        stat.int_ = is_dice ? dice_int : 4;
        stat.luk = is_dice ? dice_luk : 4;
        stat.maxhp = 50;
        stat.hp = 50;
        stat.maxmp = 50;
        stat.mp = 50;

        chr.setAccountId(client.getId());
        chr.setLevel(1);
        chr.setRemainingAp(0);
        chr.setFame(0);
        chr.setExp(0);
        chr.setMeso(0);
        chr.setMap(null);
        chr.setGM(0);
        chr.setTama(0);
        chr.setBuddylist(20);

        for (int id : item_ids) {
            SetDefaultEquip(chr, id);
        }

        DebugUser.AddStarterSet(chr);
        chr.saveNewCharToDB();
        client.SendPacket(ResCLogin.CreateNewCharacterResult(chr, OpsLogin.LoginResCode_Success));
        client.addCharacter(chr);
        return true;
    }

    public static boolean SetDefaultEquip(MapleCharacter newchar, int item_id) {
        if (!WzDataStorage.ITEM.check(item_id)) {
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

    public static boolean OnCreateNewCharacterInCS(MapleClient client, ClientPacket cp) {
        String m_sCheckedName = cp.DecodeStr();
        int m_nCurSelectedRace = cp.Decode4();
        int m_nCurSelectedSubJob = cp.Decode4(); // JMS187
        int m_nCharSaleJob = cp.Decode4();// m_nCharSaleJob - 1
        // equip...
        return false;
    }

    public static void OnCheckDuplicatedID(MapleClient client, String character_name) {
        boolean isOK = checkCharacterName(character_name);

        client.SendPacket(ResCLogin.CheckDuplicatedIDResult(character_name, isOK));
    }

    public static boolean OnSelectWorld(MapleClient client, ClientPacket cp) {
        if (Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.EMS, 89) || Region.KMS.check() || Region.KMST.check() || Region.IMS.check() || Version.GreaterOrEqual(Region.TWMS, 148)) {
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

        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 83) || Region.IMS.check() || Version.GreaterOrEqual(Region.CMS, 104)) {
            int ip = cp.Decode4(); // S_addr
        }

        // もみじ(1)
        if (world == 1) {
            client.SendPacket(ResCLogin.SelectWorldResult(client, OpsLogin.LoginResCode_Timeout));
            return false;
        }
        // 強制的にかえで(0)に書き換える
        if (world == 12) {
            world = 0;
        }
        // 選択中のワールドを設定
        client.setSelectedWorld(world);
        client.setSelectedChannel(channel);
        DQ_Character_slots.load(client);
        client.SendPacket(ResCLogin.SelectWorldResult(client, OpsLogin.LoginResCode_Success));
        return true;
    }

    public static boolean OnDeleteCharacter(MapleClient client, ClientPacket cp) {
        // JMS188+
        if (Version.GreaterOrEqual(Region.JMS, 188)) {
            String MapleID = cp.DecodeStr();
            if (!MapleID.equals(client.getMapleId())) {
                // state = 0以外にすると切断されます
            }
        }
        if (Region.KMS.check() || Region.KMST.check()) {
            if (Version.GreaterOrEqual(Region.KMS, 160)) {
                String secondpw = cp.DecodeStr();
            } else {
                byte unk1 = cp.Decode1();
                int unk2 = cp.Decode4();
            }
        }

        if (Region.GMS.check() || Region.GMST.check() || Region.EMS.check()) {
            int unke = cp.Decode4();
        }
        if (Region.THMS.check() || Region.VMS.check() || Region.BMS.check()) {
            String key = cp.DecodeStr(); // 32 bytes hex or PIC
        }

        int character_id = cp.Decode4();
        if (!client.checkCharacterId(character_id)) {
            client.loginFailed("OnDeleteCharacter");
            return false;
        }

        boolean success = DQ_Characters.deleteCharacter(client, character_id);
        client.SendPacket(ResCLogin.DeleteCharacterResult(character_id, success ? OpsLogin.LoginResCode_Success : OpsLogin.LoginResCode_Unknown));
        return success;
    }

    // JMS以外必要
    public static void OnCheckUserLimit(MapleClient client, ClientPacket cp) {
        short world_id = cp.Decode2();
        client.SendPacket(ResCLogin.CheckUserLimitResult(client.getLoginServer().getWolrdStatus(world_id)));
    }

    public static void OnWorldInfoRequest(MapleClient c) {
        for (TacosWorld world : TacosWorld.getWorlds()) {
            c.SendPacket(ResCLogin.WorldInformation(world));
        }
        c.SendPacket(ResCLogin.WorldInformation(null));

        if (Version.PostBB() || Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70)) {
            c.SendPacket(ResCLogin.RecommendWorldMessage());
            c.SendPacket(ResCLogin.LatestConnectedWorld());
        }

    }

    public static boolean OnSelectCharacter(MapleClient client, int character_id) {
        if (!client.checkCharacterId(character_id)) {
            client.loginFailed("OnSelectCharacter");
            return false;
        }

        TacosChannel game_server = TacosWorld.find(client.getSelectedWorld()).getChannelServer(client.getSelectedChannel() + 1);
        client.sendSelectCharacterResult(game_server, character_id);
        client.getLoginServer().getAuthorizedClients().remove(client);
        return true;
    }

    public static boolean OnSelectCharacterByVAC(MapleClient client, ClientPacket cp) {
        int dwCharacterID = cp.Decode4();
        int wolrd_id = cp.Decode4();
        String mac_addresses = cp.DecodeStr(); // sMacAddress
        String hwid = cp.DecodeStr(); // sMacAddressWithHDDSerial

        if (!client.checkCharacterId(dwCharacterID)) {
            client.loginFailed("OnSelectCharacterByVAC");
            return false;
        }

        client.setSelectedWorld(wolrd_id);
        client.setSelectedChannel(0);

        TacosChannel game_server = TacosWorld.find(client.getSelectedWorld()).getChannelServer(client.getSelectedChannel() + 1);
        client.sendSelectCharacterByVACResult(game_server, dwCharacterID);
        client.getLoginServer().getAuthorizedClients().remove(client);
        return true;
    }

    // TODO : move to other class.
    public static boolean checkLogin(MapleClient client, String maple_id, String password) {
        if (5 <= client.loginAttempt()) {
            client.SendPacket(ResCLogin.CheckPasswordResult(client, OpsLogin.LoginResCode_DBFail));
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
        client.setMapleId(maple_id);
        int loginok = DQ_Accounts.login(client, maple_id, password);
        if (loginok == 5) {
            if (DQ_Accounts.autoRegister(maple_id, password)) {
                loginok = DQ_Accounts.login(client, maple_id, password);
            }
        }
        // アカウントの性別変更
        if (endwith_) {
            client.setGender((byte) 1);
        }
        // GM test
        if (startwith_GM) {
            client.setGameMaster(true);
        }
        if (loginok != 0) {
            client.SendPacket(ResCLogin.CheckPasswordResult(client, loginok));
        } else {
            client.resetLoginAttempt();
            registerClient(client);
            return true;
        }
        return false;
    }

    private static long lastUpdate = 0;

    public static void registerClient(MapleClient client) {
        if (client.getLoginServer().isAdminOnly() && !client.isGameMaster()) {
            client.SendPacket(ResCLogin.CheckPasswordResult(client, OpsLogin.LoginResCode_ImpossibleIP));
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
            client.SendPacket(ResCLogin.CheckPasswordResult(client, OpsLogin.LoginResCode_Success));
        } else {
            client.SendPacket(ResCLogin.CheckPasswordResult(client, OpsLogin.LoginResCode_AlreadyConnected));
            return;
        }
        // 2次パスワード要求する場合は入力を待つ必要がある, -1で無視すれば不要
        OnWorldInfoRequest(client);
    }

    public static boolean checkCharacterName(String character_name) {
        if (character_name.getBytes().length < 2) {
            return false;
        }
        if ((Content.CharacterNameLength.getInt() - 1) < character_name.getBytes().length) {
            return false;
        }
        if (WzXML.ETC.isForbiddenName(character_name)) {
            return false;
        }
        // already registered
        if (DQ_Characters.getIdByName(character_name) != -1) {
            return false;
        }
        return true;
    }
}
