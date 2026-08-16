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
package tacos.packet.request;

import java.util.ArrayList;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import tacos.constants.MapleClientState;
import odin.client.MapleQuestStatus;
import odin.client.inventory.MaplePet;
import tacos.config.ContentState;
import tacos.config.Region;
import tacos.database.LazyDatabase;
import tacos.database.query.DQ_Accounts;
import odin.handling.world.MaplePartyCharacter;
import odin.handling.world.PartyOperation;
import odin.handling.world.OdinWorld;
import odin.handling.world.guild.MapleGuild;
import java.util.List;
import odin.client.PlayerStats;
import odin.client.inventory.MapleInventoryType;
import odin.server.MTSStorage;
import tacos.packet.ClientPacket;
import tacos.packet.response.ResCClientSocket;
import tacos.packet.response.ResCFuncKeyMappedMan;
import tacos.packet.response.ResCUser_Pet;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;
import odin.server.maps.MapleMap;
import tacos.config.Config;
import tacos.config.Content;
import tacos.database.query.DQ_Characters;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsCashItem;
import static tacos.packet.request.ReqCLogin.SetDefaultEquip;
import tacos.packet.response.ResCCashShop;
import tacos.packet.response.ResCStage;
import tacos.packet.response.ResCUserLocal;
import tacos.server.TacosWorld;
import tacos.wz.WzDataStorage;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class ReqCClientSocket {

    // CClientSocket::ProcessPacket
    public static boolean OnPacket_Login(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_AliveAck: {
                client.recvPong();
                return true;
            }
            case CP_ExceptionLog: {
                return true;
            }
            case CP_SecurityPacket: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // CClientSocket::ProcessPacket
    public static boolean OnPacket(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_MigrateIn: {
                // enter game server, change channel, leave cs/mts.
                OnMigrateIn(client, cp);
                return true;
            }
            case CP_JMS_164_KOC_UI_Request: {
                OnKOCCreation(client, cp);
                return true;
            }
            case CP_AliveAck: {
                client.recvPong();
                return true;
            }
            case CP_SecurityPacket: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // CClientSocket::ProcessPacket
    public static boolean OnPacket_ITC(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_MigrateIn: {
                // enter mts.
                OnMigrateIn(client, cp);
                return true;
            }
            case CP_AliveAck: {
                client.recvPong();
                return true;
            }
            case CP_SecurityPacket: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // CClientSocket::ProcessPacket
    public static boolean OnPacket_CS(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_MigrateIn: {
                // enter cashshop.
                OnMigrateIn(client, cp);
                return true;
            }
            case CP_AliveAck: {
                client.recvPong();
                return true;
            }
            case CP_SecurityPacket: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean OnMigrateIn(MapleClient client, ClientPacket cp) {
        int unk1 = cp.Decode4(Config.GreaterOrEqual(Region.KMS, 197));
        int character_id = cp.Decode4(); // m_dwCharacterId
        if (Config.GreaterOrEqual(Region.KMS, 92) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.GMS, 91)) { // 180+
            byte[] machine_id = cp.DecodeBuffer(16); // MachineId (HWID)
            client.setMachineId(machine_id);
        }
        if (Config.GreaterOrEqual(Region.KMS, 95) || Config.GreaterOrEqual(Region.JMS, 131)) {
            short unk2 = cp.Decode2(); // 0, GM?
        } else if (Config.GreaterOrEqual(Region.GMS, 61)) {
            byte unk2 = cp.Decode1(); // 1 byte
        }
        byte unk3 = cp.Decode1(Config.GreaterOrEqual(Region.JMS, 146) || Config.GreaterOrEqual(Region.GMS, 61));
        if (Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.GMS, 84)) { // 180+
            long client_key = cp.Decode8(); // m_aClientKey, jms always sends 0. but GMS supports this.
            client.setClientKey(client_key);
        }
        int unk4 = cp.Decode4(Config.GreaterOrEqual(Region.KMS, 95));

        if (client.getPlayer() != null) {
            client.loginFailed("OnMigrateIn : client already has character.");
            return false;
        }
        TacosWorld world = client.getWorld();
        MapleCharacter transfer = world.findMigratingPlayer(character_id);
        // channge channel, enter & leave itc/cs.
        if (transfer != null) {
            MapleClient old_client = transfer.getClient();
            // check machine id.
            if (client.getMachineId() != null) {
                if (!old_client.getMachineId().equals(client.getMachineId())) {
                    // TODO : detect hwid randomizer.
                    DebugLogger.ErrorLog("MachineId : new_client = " + client.getMachineId());
                    DebugLogger.ErrorLog("MachineId : old_client = " + old_client.getMachineId());
                    client.loginFailed("OnMigrateIn : invalid machine id.");
                    return false;
                }
            } else {
                // ip check for old versions because there is no machine id in migrate packet.
                if (!old_client.getIPAddress().equals(client.getIPAddress())) {
                    DebugLogger.ErrorLog("IP : new_client = " + client.getIPAddress());
                    DebugLogger.ErrorLog("IP : old_client = " + old_client.getIPAddress());
                    client.loginFailed("OnMigrateIn : invalid ip address.");
                    return false;
                }
            }
            // check client key. TODO : null check.
            if (client.getClientKey() != 0) {
                if (old_client.getClientKey() != client.getClientKey()) {
                    DebugLogger.ErrorLog("client key : new_client = " + client.getClientKey());
                    DebugLogger.ErrorLog("client key : old_client = " + old_client.getClientKey());
                    client.loginFailed("OnMigrateIn : invalid client key.");
                    return false;
                }
            }

            String maple_id = old_client.getMapleId();
            String nexon_id = old_client.getNexonId();
            client.setMapleId(maple_id);
            client.setNexonId(nexon_id);
            client.setPlayer(transfer);
            client.setId(transfer.getAccountId());
            transfer.setClient(client);
            world.removeMigratingPlayer(transfer);
        }

        switch (client.getServer().getType()) {
            case GAME_SERVER: {
                // login or changechannel & leave itc/cs.
                MapleCharacter chr = (transfer == null) ? MapleCharacter.loadCharFromDB(character_id, client, true) : transfer;
                if (chr == null) {
                    client.loginFailed("OnMigrateIn : GAME_SERVER.");
                    return false;
                }
                // login.
                if (transfer == null) {
                    client.setPlayer(chr);
                    client.setId(chr.getAccountId());
                    chr.setChannelId(client.getChannelServer().getChannel());
                    chr.setClient(client);
                    LazyDatabase.loadData(chr);
                    DQ_Accounts.updateLoginState(client, MapleClientState.LOGIN_LOGGEDIN);
                }
                // change channel.
                if (transfer != null) {
                    chr.setChannelId(client.getChannelServer().getChannel());
                    chr.updateMapById(chr.getPosMap(), chr.getPortal());
                }
                client.getChannelServer().getOnlinePlayers().add(chr);
                // pet
                chr.spawnSavedPets();
                // group            
                if (chr.getParty() != null) {
                    OdinWorld.Party.updateParty(chr.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(chr));
                }
                // friend
                chr.setOnlineFriends();
                chr.notityOnlineToFriends(true);
                // guild
                MapleGuild gs = null;
                if (0 < chr.getGuildId()) {
                    OdinWorld.Guild.setGuildMemberOnline(chr.getMGC(), true, client.getChannelId());
                    gs = OdinWorld.Guild.getGuild(chr.getGuildId());
                    if (gs == null) {
                        chr.setGuildId(0);
                        chr.setGuildRank((byte) 5);
                        chr.setAllianceRank((byte) 5);
                        chr.saveGuildStatus();
                    }
                }
                // family
                if (0 < chr.getFamilyId()) {
                    OdinWorld.Family.setFamilyMemberOnline(chr.getMFC(), true, client.getChannelId());
                }

                chr.sendSetField(true);
                if (Region.CMS.check()) {
                    // CMS causes crash without sending this packet when you use npc talk's avatar change.
                    chr.SendPacket(ResCWvsContext.CharacterCash(chr));
                }
                // initialize
                chr.updateStat(); // TWMS148 gets weird stat without sending this.
                chr.SendPacket(ResCWvsContext.ForcedStatReset());
                // pet
                for (final MaplePet pet : chr.getPets()) {
                    if (pet.getSummoned()) {
                        chr.SendPacket(ResCUser_Pet.Activated(chr, pet));
                    }
                }
                if (Config.LessOrEqual(Region.JMS, 131) || Region.BMS.check() || Region.VMS.check()) {
                    chr.SendPacket(ResCFuncKeyMappedMan.getPetAutoHPMP_JMS_v131(chr));
                } else {
                    chr.SendPacket(ResCFuncKeyMappedMan.getPetAutoHP(chr));
                    chr.SendPacket(ResCFuncKeyMappedMan.getPetAutoMP(chr));
                    chr.SendPacket(ResCFuncKeyMappedMan.getPetAutoCure(chr));
                }
                // keyboard
                chr.SendPacket(ResCFuncKeyMappedMan.FuncKeyMappedInit(chr, false));
                chr.SendPacket(ResCFuncKeyMappedMan.getMacros(chr));
                // quest
                for (MapleQuestStatus status : chr.getStartedQuests()) {
                    if (status.hasMobKills()) {
                        chr.SendPacket(ResWrapper.updateQuestMobKills(status));
                    }
                }
                // group
                chr.updatePartyMemberHP();
                // friend
                chr.SendPacket(ResWrapper.updateBuddylist(chr));
                // guild
                if (0 < chr.getGuildId()) {
                    chr.SendPacket(ResCWvsContext.showGuildInfo(chr));
                    List<ServerPacket> packetList = OdinWorld.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        for (ServerPacket pack : packetList) {
                            if (pack != null) {
                                chr.SendPacket(pack);
                            }
                        }
                    }
                }
                // family
                chr.SendPacket(ResCWvsContext.getFamilyData());
                chr.SendPacket(ResCWvsContext.getFamilyInfo(chr));
                chr.sendStatChanged(true); // this gives you crash, if you did not send pet spawn packet in JMS131.
                //chr.showNote();
                chr.baseSkills(); // ?_?
                // 精霊のペンダント
                //chr.startFairySchedule(false);
                // 期限切れ
                //chr.expirationTask();
                //if (chr_koc.getJob() == 132) {
                //chr.checkBerserk();
                //}
                // internet cafe
                if (ContentState.CS_NETCAFE.get() || chr.getInventory(MapleInventoryType.CASH).findById(5420007) != null) {
                    chr.SendPacket(ResCClientSocket.AuthenCodeChanged());
                }
                // 上部スライドメッセージ
                chr.SendPacket(ResWrapper.BroadCastMsgSlide(chr.getChannelServer().getServerMessage()));
                // [other players]
                // your pet
                // [entering map]
                MapleMap map = chr.getMap();
                map.userEnterField(chr);
                map.linkedObjectEnterField(chr);

                for (final MaplePet pet : chr.getPets()) {
                    if (pet.getSummoned()) {
                        map.broadcastMessage(chr, ResCUser_Pet.TransferField(chr, pet), true);
                    }
                }
                break;
            }
            case ITC_SERVER: {
                if (transfer == null) {
                    client.loginFailed("OnMigrateIn : ITC_SERVER.");
                    return false;
                }
                // enter itc.
                world.getITC().getOnlinePlayers().add(transfer);
                transfer.notityOnlineToFriends(true);
                transfer.SendPacket(ResCStage.SetITC(transfer));
                ReqCITC.MTSUpdate(MTSStorage.getInstance().getCart(transfer.getId()), client);
                break;
            }
            case CASHSHOP_SERVER: {
                if (transfer == null) {
                    client.loginFailed("OnMigrateIn : CASHSHOP_SERVER.");
                    return false;
                }
                // enter cs.
                world.getCashShop().getOnlinePlayers().add(transfer);
                transfer.notityOnlineToFriends(true);
                transfer.SendPacket(ResCStage.SetCashShop(transfer));
                transfer.SendPacket(ResCCashShop.CashShopQueryCashResult(transfer));
                transfer.SendPacket(ResCCashShop.CashItemResult(OpsCashItem.CashItemRes_LoadLocker_Done, client));
                ReqCCashShop.updateFreeCouponDate(transfer);
                break;
            }
            default: {
                client.loginFailed("OnMigrateIn : unk.");
                return false;
            }
        }

        return true;
    }

    public static boolean OnKOCCreation(MapleClient client, ClientPacket cp) {
        ArrayList<Integer> item_ids = new ArrayList<>();
        String name = cp.DecodeStr();
        int face_id = cp.Decode4();
        int hair_id = cp.Decode4();
        int skin_color = 10;
        if (Region.GMS.check()) {
            int hair_color = cp.Decode4();
            hair_id += hair_color;
            skin_color = cp.Decode4();
        }
        int top_id = cp.Decode4();
        item_ids.add(top_id);
        int bottom_id = cp.Decode4();
        item_ids.add(bottom_id);
        int shoes_id = cp.Decode4();
        item_ids.add(shoes_id);
        int weapon_id = cp.Decode4();
        item_ids.add(weapon_id);
        int gender_id = Region.GMS.check() ? cp.Decode4() : client.getPlayer().getGender();

        // item id checks. TODO : more validation.
        if (!WzDataStorage.FACE.check(face_id) || !WzDataStorage.HAIR.check(hair_id)) {
            client.SendPacket(ResCUserLocal.KOC_UI_Response(-1));
            return false;
        }

        for (int item_id : item_ids) {
            if (!WzDataStorage.ITEM.check(item_id)) {
                client.SendPacket(ResCUserLocal.KOC_UI_Response(-1));
                return false;
            }
        }

        // no character slot.
        client.loadCharactersFromDB();
        if (client.getCharSlots() <= client.getCharaterCount()) {
            client.SendPacket(ResCUserLocal.KOC_UI_Response(2));
            return false;
        }

        // name is duplicated.
        if (DQ_Characters.getIdByName(name) != -1) {
            client.SendPacket(ResCUserLocal.KOC_UI_Response(1));
            return false;
        }

        // invalid name.
        if (name.getBytes().length < 2 || (Content.CharacterNameLength.getInt() - 1) < name.getBytes().length | WzXML.ETC.isForbiddenName(name)) {
            client.SendPacket(ResCUserLocal.KOC_UI_Response(3));
            return true;
        }

        // create new character.
        MapleCharacter chr_koc = new MapleCharacter();
        chr_koc.init_step1();
        chr_koc.setClient(client);
        chr_koc.setFace(face_id);
        chr_koc.setHair(hair_id);
        chr_koc.setGender(gender_id);
        chr_koc.setName(name);
        chr_koc.setSkinColor(skin_color);
        chr_koc.setJob(1000);

        PlayerStats stat = chr_koc.getStat();
        stat.str = 12;
        stat.dex = 5;
        stat.int_ = 4;
        stat.luk = 4;
        stat.maxhp = 50;
        stat.hp = 50;
        stat.maxmp = 50;
        stat.mp = 50;

        chr_koc.setAccountId(client.getId());
        chr_koc.setLevel(1);
        chr_koc.setRemainingAp(0);
        chr_koc.setFame(0);
        chr_koc.setExp(0);
        chr_koc.setMeso(0);
        chr_koc.setMap(null);
        chr_koc.setBuddylist(20);

        for (int item_id : item_ids) {
            SetDefaultEquip(chr_koc, item_id);
        }

        chr_koc.saveNewCharToDB();
        client.addCharacter(chr_koc);

        client.SendPacket(ResCUserLocal.KOC_UI_Response(0));
        return true;
    }
}
