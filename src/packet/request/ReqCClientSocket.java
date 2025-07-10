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
import config.ServerConfig;
import database.ExtraDB;
import handling.MaplePacket;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.world.CharacterIdChannelPair;
import handling.world.CharacterTransfer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.PlayerBuffStorage;
import handling.world.World;
import handling.world.guild.MapleGuild;
import java.util.List;
import packet.ClientPacket;
import packet.response.ResCClientSocket;
import packet.response.ResCField;
import packet.response.ResCFuncKeyMappedMan;
import packet.response.ResCStage;
import packet.response.ResCUser_Pet;
import packet.response.ResCWvsContext;
import packet.response.wrapper.ResWrapper;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import tools.FileoutputUtil;

/**
 *
 * @author Riremito
 */
public class ReqCClientSocket {

    public static boolean login_test = false;

    public static final void ChangeChannel(ClientPacket p, final MapleClient c, final MapleCharacter chr) {
        if (!chr.isAlive() || chr.getEventInstance() != null || chr.getMap() == null || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())) {
            c.getSession().write(ResWrapper.enableActions());
            return;
        }
        ExtraDB.saveData(chr);
        int channel = p.Decode1() + 1;
        chr.changeChannel(channel);
    }

    public static final void Loggedin(final int playerid, final MapleClient c) {
        final ChannelServer channelServer = c.getChannelServer();
        MapleCharacter player;
        final CharacterTransfer transfer = channelServer.getPlayerStorage().getPendingCharacter(playerid);
        if (transfer == null) {
            // Player isn't in storage, probably isn't CC
            player = MapleCharacter.loadCharFromDB(playerid, c, true);
        } else {
            player = MapleCharacter.ReconstructChr(transfer, c, true);
        }
        ExtraDB.loadData(player);
        player.UpdateStat(true);
        c.setPlayer(player);
        c.setAccID(player.getAccountID());
        if (!c.CheckIPAddress()) {
            // Remote hack
            c.getSession().close();
            return;
        }
        final int state = c.getLoginState();
        boolean allowLogin = false;
        if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL) {
            if (!World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
                allowLogin = true;
            }
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            return;
        }
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        channelServer.addPlayer(player);
        if (ServerConfig.JMS302orLater()) {
            // 分割 SetField
            c.SendPacket(ResCStage.SetField_JMS_302(player, 1, true, null, 0, 0));
            c.SendPacket(ResCStage.SetField_JMS_302(player, 2, true, null, 0, -1));
        } else {
            c.SendPacket(ResWrapper.getCharInfo(player));
        }
        c.SendPacket(ResWrapper.getInventoryFull()); // TWMS148 gets weird stat without sending this.
        c.SendPacket(ResCWvsContext.temporaryStats_Reset());
        player.getMap().addPlayer(player);
        player.spawnSavedPets();
        MapleMap player_map = player.getMap();
        if (player_map != null) {
            for (final MaplePet pet : player.getPets()) {
                if (pet.getSummoned()) {
                    //player.SendPacket(ResCUser_Pet.Activated(player, pet));
                    player_map.broadcastMessage(player, ResCUser_Pet.TransferField(player, pet), true);
                }
            }
        }
        player.UpdateStat(true); // this gives you crash, if you did not send pet spawn packet in JMS131.
        try {
            player.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(player.getId()));
            player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()));
            player.giveSilentDebuff(PlayerBuffStorage.getDiseaseFromStorage(player.getId()));
            // Start of buddylist
            final int[] buddyIds = player.getBuddylist().getBuddyIds();
            World.Buddy.loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds, player.getGMLevel(), player.isHidden());
            if (player.getParty() != null) {
                World.Party.updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
            }
            final CharacterIdChannelPair[] onlineBuddies = World.Find.multiBuddyFind(player.getId(), buddyIds);
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                final BuddylistEntry ble = player.getBuddylist().get(onlineBuddy.getCharacterId());
                ble.setChannel(onlineBuddy.getChannel());
                player.getBuddylist().put(ble);
            }
            c.getSession().write(ResWrapper.updateBuddylist(player));
            // Start of Messenger
            final MapleMessenger messenger = player.getMessenger();
            if (messenger != null) {
                World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()));
                World.Messenger.updateMessenger(messenger.getId(), c.getPlayer().getName(), c.getChannel());
            }
            // Start of Guild and alliance
            if (player.getGuildId() > 0) {
                World.Guild.setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.getSession().write(ResCWvsContext.showGuildInfo(player));
                final MapleGuild gs = World.Guild.getGuild(player.getGuildId());
                if (gs != null) {
                    final List<MaplePacket> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        for (MaplePacket pack : packetList) {
                            if (pack != null) {
                                c.getSession().write(pack);
                            }
                        }
                    }
                } else {
                    //guild not found, change guild id
                    player.setGuildId(0);
                    player.setGuildRank((byte) 5);
                    player.setAllianceRank((byte) 5);
                    player.saveGuildStatus();
                }
            }
            if (player.getFamilyId() > 0) {
                World.Family.setFamilyMemberOnline(player.getMFC(), true, c.getChannel());
            }
            c.getSession().write(ResCWvsContext.getFamilyInfo(player));
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.Login_Error, e);
        }
        c.getSession().write(ResCWvsContext.getFamilyData());
        player.showNote();
        player.updatePartyMemberHP();
        player.startFairySchedule(false);
        player.baseSkills(); //fix people who've lost skills.
        c.getSession().write(ResCFuncKeyMappedMan.getKeymap(player, false));
        c.getSession().write(ResCFuncKeyMappedMan.getMacros(player));
        if (ServerConfig.JMS164orLater()) {
            c.getSession().write(ResCFuncKeyMappedMan.getPetAutoHP(player));
            c.getSession().write(ResCFuncKeyMappedMan.getPetAutoMP(player));
            c.getSession().write(ResCFuncKeyMappedMan.getPetAutoCure(player));
        } else {
            c.getSession().write(ResCFuncKeyMappedMan.getPetAutoHPMP_JMS_v131(player));
        }
        for (MapleQuestStatus status : player.getStartedQuests()) {
            if (status.hasMobKills()) {
                c.SendPacket(ResWrapper.updateQuestMobKills(status));
            }
        }
        final CharacterNameAndId pendingBuddyRequest = player.getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            player.getBuddylist().put(new BuddylistEntry(pendingBuddyRequest.getName(), pendingBuddyRequest.getId(), "\u30de\u30a4\u53cb\u672a\u6307\u5b9a", -1, false, pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
            c.getSession().write(ResWrapper.requestBuddylistAdd(pendingBuddyRequest.getId(), pendingBuddyRequest.getName(), pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
        }
        player.expirationTask();
        if (player.getJob() == 132) {
            // DARKKNIGHT
            player.checkBerserk();
        }
    }

    public static final void EnterCS(final MapleClient c, final MapleCharacter chr, final boolean mts) {
        if (!chr.isAlive() || chr.getEventInstance() != null || c.getChannelServer() == null) {
            c.getSession().write(ResCField.serverBlocked(2));
            c.getSession().write(ResWrapper.enableActions());
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
    // CClientSocket::OnCenterMigrateInResult
    // CClientSocket::OnCenterMigrateOutResult

    public static boolean GetLogin() {
        return login_test;
    }

    public static void SetLogin(boolean login_state) {
        login_test = login_state;
    }
}
