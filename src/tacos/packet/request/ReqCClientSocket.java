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

import odin.client.BuddylistEntry;
import odin.client.CharacterNameAndId;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import tacos.constants.MapleClientState;
import odin.client.MapleQuestStatus;
import odin.client.inventory.MaplePet;
import tacos.config.ContentState;
import tacos.config.Region;
import tacos.config.Version;
import tacos.database.ExtraDB;
import tacos.database.query.DQ_Accounts;
import tacos.debug.DebugLogger;
import tacos.network.MaplePacket;
import tacos.server.ServerOdinGame;
import odin.handling.world.CharacterIdChannelPair;
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

/**
 *
 * @author Riremito
 */
public class ReqCClientSocket {

    // CClientSocket::ProcessPacket
    public static boolean OnPacket_Login(MapleClient c, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_AliveAck: {
                c.recvPong();
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
    public static boolean OnPacket(MapleClient c, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_MigrateIn: {
                // enter game server, change channel, leave cs/mts.
                OnMigrateIn(cp, c);
                return true;
            }
            case CP_AliveAck: {
                c.recvPong();
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
    public static boolean OnPacket_ITC(MapleClient c, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_MigrateIn: {
                // enter mts.
                OnMigrateIn_ITC(cp, c);
                return true;
            }
            case CP_AliveAck: {
                c.recvPong();
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
    public static boolean OnPacket_CS(MapleClient c, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_MigrateIn: {
                // enter cashshop.
                OnMigrateIn_CS(cp, c);
                return true;
            }
            case CP_AliveAck: {
                c.recvPong();
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

    public static boolean OnMigrateIn(ClientPacket cp, MapleClient c) {
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            int unk1 = cp.Decode4();
        }
        int character_id = cp.Decode4();

        ServerOdinGame channel = c.getOdinChannelServer();
        MapleCharacter transfer = c.getWorld().findMigratingPlayer(character_id);
        MapleCharacter chr = (transfer == null) ? MapleCharacter.loadCharFromDB(character_id, c, true) : transfer;
        ExtraDB.loadData(chr);
        c.setPlayer(chr);
        c.setId(chr.getAccountID());
        chr.setChannelId(c.getChannelServer().getChannel());

        if (transfer != null) {
            c.setMapleId(transfer.getClient().getMapleId());
            c.setNexonId(transfer.getClient().getNexonId());
            c.getWorld().removeMigratingPlayer(transfer);
        }
        chr.setClient(c);

        if (!DQ_Accounts.checkLoginIP(c)) {
            c.loginFailed("OnMigrateIn 1."); // Remote hack
            return false;
        }

        switch (DQ_Accounts.getLoginState(c)) {
            case LOGIN_SERVER_TRANSITION:
            case CHANGE_CHANNEL: {
                // OK
                if (transfer != null) {
                    DebugLogger.DebugLog(chr, "CC");
                } else {
                    DebugLogger.DebugLog(chr, "Login");
                }
                break;
            }
            default: {
                DebugLogger.ErrorLog("invalid client state.");
                return false;
            }
        }

        // entering game server
        DQ_Accounts.updateLoginState(c, MapleClientState.LOGIN_LOGGEDIN);
        channel.addPlayer(chr);
        // pet
        chr.spawnSavedPets();
        // group            
        if (chr.getParty() != null) {
            OdinWorld.Party.updateParty(chr.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(chr));
        }
        // friend
        OdinWorld.Buddy.loggedOn(chr.getName(), chr.getId(), c.getChannelId(), chr.getBuddylist().getBuddyIds(), chr.getGMLevel(), chr.isHidden());
        for (CharacterIdChannelPair onlineBuddy : OdinWorld.Find.multiBuddyFind(chr.getId(), chr.getBuddylist().getBuddyIds())) {
            final BuddylistEntry ble = chr.getBuddylist().get(onlineBuddy.getCharacterId());
            ble.setChannel(onlineBuddy.getChannel());
            chr.getBuddylist().put(ble);
        }
        // guild
        MapleGuild gs = null;
        if (0 < chr.getGuildId()) {
            OdinWorld.Guild.setGuildMemberOnline(chr.getMGC(), true, c.getChannelId());
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
            OdinWorld.Family.setFamilyMemberOnline(chr.getMFC(), true, c.getChannelId());
        }
        // idk - 1
        if (chr.getMessenger() != null) {
            OdinWorld.Messenger.silentJoinMessenger(chr.getMessenger().getId(), new MapleMessengerCharacter(c.getPlayer()));
            OdinWorld.Messenger.updateMessenger(chr.getMessenger().getId(), c.getPlayer().getName(), c.getChannelId());
        }
        // idk - 2
        CharacterNameAndId pendingBuddyRequest = chr.getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            chr.getBuddylist().put(new BuddylistEntry(pendingBuddyRequest.getName(), pendingBuddyRequest.getId(), "マイ友未指定", -1, false, pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
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
        if (Version.LessOrEqual(Region.JMS, 131)) {
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
        // notice
        if (transfer != null) {
            // Change Channel OK
            return true;
        }

        return true;
    }

    public static boolean OnMigrateIn_ITC(ClientPacket cp, MapleClient c) {
        int character_id = cp.Decode4();
        MapleCharacter chr = c.getWorld().findMigratingPlayer(character_id);
        if (chr == null) {
            c.loginFailed("OnMigrateIn_ITC 1.");
            return false;
        }
        c.setMapleId(chr.getClient().getMapleId());
        c.setNexonId(chr.getClient().getNexonId());
        chr.setClient(c);
        c.setPlayer(chr);
        c.setId(chr.getAccountID());

        if (!DQ_Accounts.checkLoginIP(c)) {
            c.loginFailed("OnMigrateIn_ITC 2."); // Remote hack
            return false;
        }
        final MapleClientState state = DQ_Accounts.getLoginState(c);
        boolean allowLogin = false;
        if (state == MapleClientState.LOGIN_SERVER_TRANSITION || state == MapleClientState.CHANGE_CHANNEL) {
            allowLogin = true;
        }
        if (!allowLogin) {
            c.loginFailed("OnMigrateIn_ITC 3.");
            return false;
        }

        c.getWorld().removeMigratingPlayer(chr);
        c.getWorld().getITC().getPlayerStorageMTS().registerPlayer(chr);

        DQ_Accounts.updateLoginState(c, MapleClientState.LOGIN_LOGGEDIN);
        chr.SendPacket(ResCStage.SetITC(chr));
        ReqCITC.MTSUpdate(MTSStorage.getInstance().getCart(chr.getId()), c);
        return true;
    }

    public static boolean OnMigrateIn_CS(ClientPacket cp, MapleClient c) {
        int character_id = cp.Decode4();
        MapleCharacter chr = c.getWorld().findMigratingPlayer(character_id);
        if (chr == null) {
            c.loginFailed("OnMigrateIn_CS 1.");
            return false;
        }
        c.setMapleId(chr.getClient().getMapleId());
        c.setNexonId(chr.getClient().getNexonId());
        chr.setClient(c);
        c.setPlayer(chr);
        c.setId(chr.getAccountID());

        if (!DQ_Accounts.checkLoginIP(c)) {
            c.loginFailed("OnMigrateIn_CS 2."); // Remote hack
            return false;
        }
        final MapleClientState state = DQ_Accounts.getLoginState(c);
        boolean allowLogin = false;
        if (state == MapleClientState.LOGIN_SERVER_TRANSITION || state == MapleClientState.CHANGE_CHANNEL) {
            allowLogin = true;
        }
        if (!allowLogin) {
            c.loginFailed("OnMigrateIn_CS 3.");
            return false;
        }

        chr.getWorld().removeMigratingPlayer(chr);
        c.getWorld().getCashShop().getPlayerStorage().registerPlayer(chr);

        DQ_Accounts.updateLoginState(c, MapleClientState.LOGIN_LOGGEDIN);
        chr.SendPacket(ResCStage.SetCashShop(c));
        chr.SendPacket(ResCCashShop.CashShopQueryCashResult(c.getPlayer()));
        chr.SendPacket(ResCCashShop.CashItemResult(OpsCashItem.CashItemRes_LoadLocker_Done, c));
        ReqCCashShop.updateFreeCouponDate(c.getPlayer());
        return true;
    }

    // CClientSocket::OnMigrateOut
    // CClientSocket::OnCenterMigrateOutResult
}
