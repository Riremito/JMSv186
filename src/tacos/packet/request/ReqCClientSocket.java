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

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import tacos.constants.MapleClientState;
import odin.client.MapleQuestStatus;
import odin.client.inventory.MaplePet;
import tacos.config.ContentState;
import tacos.config.Region;
import tacos.config.Version;
import tacos.database.LazyDatabase;
import tacos.database.query.DQ_Accounts;
import tacos.network.MaplePacket;
import odin.handling.world.MapleMessengerCharacter;
import odin.handling.world.MaplePartyCharacter;
import odin.handling.world.PartyOperation;
import odin.handling.world.OdinWorld;
import odin.handling.world.guild.MapleGuild;
import java.util.List;
import odin.server.MTSStorage;
import tacos.packet.ClientPacket;
import tacos.packet.response.ResCClientSocket;
import tacos.packet.response.ResCFuncKeyMappedMan;
import tacos.packet.response.ResCUser_Pet;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;
import odin.server.maps.MapleMap;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ops.OpsCashItem;
import tacos.packet.response.ResCCashShop;
import tacos.packet.response.ResCStage;
import tacos.server.TacosWorld;

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
                OnMigrateIn(cp, client);
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
                OnMigrateIn(cp, client);
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
                OnMigrateIn(cp, client);
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

    public static boolean OnMigrateIn(ClientPacket cp, MapleClient client) {
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            int unk1 = cp.Decode4();
        }
        int character_id = cp.Decode4(); // m_dwCharacterId
        /*
        cp.DecodeBuffer(16); // MachineId (HWID)
        cp.Decode1(); // GM (JMS186, 2 bytes)
        cp.Decode1(); // unk 0
        cp.DecodeBuffer(8); // m_aClientKey
         */
        if (client.getPlayer() != null) {
            client.loginFailed("OnMigrateIn : client already has character.");
            return false;
        }
        TacosWorld world = client.getWorld();
        MapleCharacter transfer = world.findMigratingPlayer(character_id);
        if (transfer != null) {
            MapleClient old_client = transfer.getClient();
            String maple_id = old_client.getMapleId();
            String nexon_id = old_client.getNexonId();
            client.setMapleId(maple_id);
            client.setNexonId(nexon_id);
            client.setPlayer(transfer);
            client.setId(transfer.getAccountID());
            transfer.setClient(client);
            world.removeMigratingPlayer(transfer);
        }

        switch (client.getServer().getType()) {
            case GAME_SERVER: {
                MapleCharacter chr = (transfer == null) ? MapleCharacter.loadCharFromDB(character_id, client, true) : transfer;
                if (chr == null) {
                    client.loginFailed("OnMigrateIn : GAME_SERVER.");
                    return false;
                }
                if (transfer == null) {
                    client.setPlayer(chr);
                    client.setId(chr.getAccountID());
                    chr.setChannelId(client.getChannelServer().getChannel());
                    chr.setClient(client);
                    LazyDatabase.loadData(chr);
                    DQ_Accounts.updateLoginState(client, MapleClientState.LOGIN_LOGGEDIN);
                }
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
                // idk - 1
                if (chr.getMessenger() != null) {
                    OdinWorld.Messenger.silentJoinMessenger(chr.getMessenger().getId(), new MapleMessengerCharacter(client.getPlayer()));
                    OdinWorld.Messenger.updateMessenger(chr.getMessenger().getId(), client.getPlayer().getName(), client.getChannelId());
                }

                chr.sendSetField(true);
                // initialize
                chr.updateStat(); // TWMS148 gets weird stat without sending this.
                chr.SendPacket(ResCWvsContext.ForcedStatReset());
                // pet
                for (final MaplePet pet : chr.getPets()) {
                    if (pet.getSummoned()) {
                        chr.SendPacket(ResCUser_Pet.Activated(chr, pet));
                    }
                }
                if (Version.LessOrEqual(Region.JMS, 131) || Region.check(Region.BMS)) {
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
                    List<MaplePacket> packetList = OdinWorld.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        for (MaplePacket pack : packetList) {
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
                //if (chr.getJob() == 132) {
                //chr.checkBerserk();
                //}
                // internet cafe
                if (ContentState.CS_NETCAFE.get()) {
                    chr.SendPacket(ResCClientSocket.AuthenCodeChanged());
                }
                // 上部スライドメッセージ
                chr.SendPacket(ResWrapper.BroadCastMsgSlide(chr.getChannelServer().getServerMessage()));
                // [other players]
                // your pet
                // [entering map]
                MapleMap map = chr.getMap();
                map.addPlayer(chr);
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
                world.getCashShop().getOnlinePlayers().add(transfer);
                transfer.notityOnlineToFriends(true);
                transfer.SendPacket(ResCStage.SetCashShop(client));
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

    // CClientSocket::OnMigrateOut
    // CClientSocket::OnCenterMigrateOutResult
}
