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
package packet.request;

import client.BuddylistEntry;
import client.CharacterNameAndId;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.inventory.MaplePet;
import config.Region;
import config.Version;
import database.ExtraDB;
import debug.Debug;
import server.network.MaplePacket;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.world.CharacterIdChannelPair;
import handling.world.CharacterTransfer;
import handling.world.MapleMessengerCharacter;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.PlayerBuffStorage;
import handling.world.World;
import handling.world.guild.MapleGuild;
import java.util.List;
import packet.ClientPacket;
import packet.ops.OpsTransferChannel;
import packet.response.ResCClientSocket;
import packet.response.ResCField;
import packet.response.ResCFuncKeyMappedMan;
import packet.response.ResCStage;
import packet.response.ResCUser_Pet;
import packet.response.ResCWvsContext;
import packet.response.wrapper.ResWrapper;
import server.maps.FieldLimitType;
import server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class ReqCClientSocket {

    public static boolean login_test = false;

    public static boolean OnPacket(ClientPacket.Header header, ClientPacket cp, MapleClient c) {
        switch (header) {
            case CP_MigrateIn: // Enter Game Server (Login)
            {
                OnMigrateIn(cp, c);
                return true;
            }
            case CP_AliveAck: {
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

    public static boolean OnMigrateIn(ClientPacket cp, MapleClient c) {
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            int unk1 = cp.Decode4();
        }
        int character_id = cp.Decode4();
        EnterGameServer(c, character_id);
        return true;
    }

    public static final boolean ChangeChannel(MapleCharacter chr, int channel) {
        if (!chr.isAlive() || chr.getEventInstance() != null || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())) {
            return false;
        }
        ExtraDB.saveData(chr);
        chr.changeChannel(channel + 1);
        return true;
    }

    // CClientSocket::OnCenterMigrateInResult
    public static boolean EnterGameServer(MapleClient c, int character_id) {
        // ログイン or CH変更
        ChannelServer channel = c.getChannelServer();
        CharacterTransfer transfer = channel.getPlayerStorage().getPendingCharacter(character_id);
        MapleCharacter chr = (transfer == null) ? MapleCharacter.loadCharFromDB(character_id, c, true) : MapleCharacter.ReconstructChr(transfer, c, true);
        ExtraDB.loadData(chr);
        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());

        if (!c.CheckIPAddress()) {
            c.setPlayer(null);
            c.getSession().close();
            Debug.ErrorLog("remoted hack detected.");
            return false;
        }

        switch (c.getLoginState()) {
            case MapleClient.LOGIN_SERVER_TRANSITION:
            case MapleClient.CHANGE_CHANNEL: {
                if (World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
                    c.setPlayer(null);
                    c.getSession().close();
                    Debug.ErrorLog("already loggedin.");
                    return false;
                }
                // OK
                if (transfer != null) {
                    Debug.DebugLog(chr, "CC");
                } else {
                    Debug.DebugLog(chr, "Login");
                }
                break;
            }
            default: {
                Debug.ErrorLog("invalid client state.");
                return false;
            }
        }

        // entering game server
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        channel.addPlayer(chr);
        // [update character data in server]
        // character
        chr.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(chr.getId()));
        chr.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(chr.getId()));
        chr.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(chr.getId()));
        // pet
        chr.spawnSavedPets();
        // group            
        if (chr.getParty() != null) {
            World.Party.updateParty(chr.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(chr));
        }
        // friend
        World.Buddy.loggedOn(chr.getName(), chr.getId(), c.getChannel(), chr.getBuddylist().getBuddyIds(), chr.getGMLevel(), chr.isHidden());
        for (CharacterIdChannelPair onlineBuddy : World.Find.multiBuddyFind(chr.getId(), chr.getBuddylist().getBuddyIds())) {
            final BuddylistEntry ble = chr.getBuddylist().get(onlineBuddy.getCharacterId());
            ble.setChannel(onlineBuddy.getChannel());
            chr.getBuddylist().put(ble);
        }
        // guild
        MapleGuild gs = null;
        if (0 < chr.getGuildId()) {
            World.Guild.setGuildMemberOnline(chr.getMGC(), true, c.getChannel());
            gs = World.Guild.getGuild(chr.getGuildId());
            if (gs == null) {
                chr.setGuildId(0);
                chr.setGuildRank((byte) 5);
                chr.setAllianceRank((byte) 5);
                chr.saveGuildStatus();
            }
        }
        // family
        if (0 < chr.getFamilyId()) {
            World.Family.setFamilyMemberOnline(chr.getMFC(), true, c.getChannel());
        }
        // idk - 1
        if (chr.getMessenger() != null) {
            World.Messenger.silentJoinMessenger(chr.getMessenger().getId(), new MapleMessengerCharacter(c.getPlayer()));
            World.Messenger.updateMessenger(chr.getMessenger().getId(), c.getPlayer().getName(), c.getChannel());
        }
        // idk - 2
        CharacterNameAndId pendingBuddyRequest = chr.getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            chr.getBuddylist().put(new BuddylistEntry(pendingBuddyRequest.getName(), pendingBuddyRequest.getId(), "マイ友未指定", -1, false, pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
        }
        // [update character data in client by packet]
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            // 分割 SetField
            chr.SendPacket(ResCStage.SetField_JMS_302(chr, 1, true, null, 0, 0));
            chr.SendPacket(ResCStage.SetField_JMS_302(chr, 2, true, null, 0, -1));
        } else {
            chr.SendPacket(ResWrapper.getCharInfo(chr));
        }
        // initialize
        chr.SendPacket(ResWrapper.getInventoryFull()); // TWMS148 gets weird stat without sending this.
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
        chr.SendPacket(ResCFuncKeyMappedMan.getKeymap(chr, false));
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
            List<MaplePacket> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
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
        // idk - 2
        if (pendingBuddyRequest != null) {
            chr.SendPacket(ResWrapper.requestBuddylistAdd(pendingBuddyRequest.getId(), pendingBuddyRequest.getName(), pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
        }
        chr.UpdateStat(true); // this gives you crash, if you did not send pet spawn packet in JMS131.
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
        chr.SendPacket(ResCClientSocket.AuthenCodeChanged());
        // 上部スライドメッセージ
        chr.SendPacket(ResWrapper.BroadCastMsgSlide(channel.getServerMessage()));
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

        Debug.DebugLog("users = " + World.getConnected().get(0));
        return true;
    }

    public static final void EnterCS(final MapleClient c, final MapleCharacter chr, final boolean mts) {
        // temporary off
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            //chr.SendPacket(ResCField.TransferChannelReqIgnored(mts ? OpsTransferChannel.TC_ITCSVR_DISCONNECTED : OpsTransferChannel.TC_SHOPSVR_DISCONNECTED));
            chr.SendPacket(ResCField.TransferChannelReqIgnored(mts ? OpsTransferChannel.TC_ITCSVR_DISCONNECTED : OpsTransferChannel.TC_SHOPSVR_DISCONNECTED));
            return;
        }

        if (!chr.isAlive() || chr.getEventInstance() != null || c.getChannelServer() == null) {
            chr.SendPacket(ResCField.TransferChannelReqIgnored(mts ? OpsTransferChannel.TC_ITCSVR_DISCONNECTED : OpsTransferChannel.TC_SHOPSVR_DISCONNECTED));
            return;
        }
        final ChannelServer ch = ChannelServer.getInstance(c.getChannel());
        chr.changeRemoval();
        if (chr.getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
            World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());
        World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), mts ? -20 : -10);
        ch.removePlayer(chr);
        c.updateLoginState(MapleClient.CHANGE_CHANNEL, c.getSessionIPAddress());
        c.SendPacket(ResCClientSocket.MigrateCommand(CashShopServer.getPort()));
        chr.saveToDB(false, false);
        ExtraDB.saveData(chr);
        chr.getMap().removePlayer(chr);
        c.setPlayer(null);
        c.setReceiving(false);
    }

    // CClientSocket::OnMigrateOut
    // CClientSocket::OnCenterMigrateOutResult
}
