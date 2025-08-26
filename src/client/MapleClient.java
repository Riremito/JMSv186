/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngine;
import database.ExtraDB;
import database.query.DQ_Accounts;
import database.query.DQ_Characters;
import server.network.MaplePacket;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.world.MapleMessengerCharacter;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.MapleGuildCharacter;
import java.util.ArrayList;
import server.maps.MapleMap;
import server.shops.IMaplePlayerShop;
import tools.FileoutputUtil;
import server.network.MapleAESOFB;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.mina.common.IoSession;
import server.quest.MapleQuest;

public class MapleClient {

    public static final int DEFAULT_CHARSLOT = 6;

    private final IoSession session;
    private boolean offline = false;
    private final MapleAESOFB aes_send;
    private final MapleAESOFB aes_recv;
    private String nexon_id = null;
    private String maple_id = null;
    private int id = 0;
    private int world;
    private int channel = 1;
    private boolean gameMaster;
    private byte gender = 0;
    private boolean loggedIn = false;
    private int loginAttempt = 0;
    private boolean serverTransition = false;
    private int charslots = DEFAULT_CHARSLOT;
    private List<Integer> character_ids = null;
    private List<MapleCharacter> characters = null;
    private MapleCharacter player = null;

    public MapleClient(MapleAESOFB aes_send, MapleAESOFB aes_recv, IoSession session) {
        this.aes_send = aes_send;
        this.aes_recv = aes_recv;
        this.session = session;
    }

    public boolean setAccountData(int id, boolean gameMaster, byte gender) {
        this.id = id;
        this.gameMaster = gameMaster;
        this.gender = gender;
        return true;
    }

    public IoSession getSession() {
        return this.session;
    }

    public boolean isOffline() {
        return this.offline;
    }

    public void setOffline() {
        this.offline = true;
    }

    public void SendPacket(MaplePacket packet) {
        this.session.write(packet);
    }

    public MapleAESOFB getReceiveCrypto() {
        return this.aes_recv;
    }

    public MapleAESOFB getSendCrypto() {
        return this.aes_send;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getMapleId() {
        return this.maple_id;
    }

    public void setMapleId(String maple_id) {
        this.maple_id = maple_id;
    }

    public int getChannel() {
        return this.channel;
    }

    public void setChannel(final int channel) {
        this.channel = channel;
    }

    public int getWorld() {
        return this.world;
    }

    public void setWorld(final int world) {
        this.world = world;
    }

    public MapleCharacter getPlayer() {
        return this.player;
    }

    public void setPlayer(MapleCharacter player) {
        this.player = player;
    }

    public boolean isGameMaster() {
        return this.gameMaster;
    }

    public void setGameMaster() {
        this.gameMaster = true;
    }

    public byte getGender() {
        return this.gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public String getSessionIPAddress() {
        return session.getRemoteAddress().toString().split(":")[0];
    }

    public boolean isLoggedIn() {
        return this.loggedIn;
    }

    public void setLoggedIn(boolean loggedin) {
        this.loggedIn = loggedin;
    }

    public int loginAttempt() {
        return this.loginAttempt++;
    }

    public void resetLoginAttempt() {
        this.loginAttempt = 0;
    }

    public boolean getServerTransition() {
        return this.serverTransition;
    }

    public void setServerTransition(boolean serverTransition) {
        this.serverTransition = serverTransition;
    }

    public int getCharSlots() {
        return this.charslots;
    }

    public boolean setCharSlots(int charslots) {
        this.charslots = charslots;
        return true;
    }

    public List<Integer> getCharacterIds() {
        return getCharacterIds(false);
    }

    public List<Integer> getCharacterIds(boolean reload) {
        if (reload || this.character_ids == null) {
            this.character_ids = DQ_Characters.getCharatcerIds(this);
        }
        return this.character_ids;
    }

    public final List<MapleCharacter> loadCharactersFromDB() {
        return loadCharactersFromDB(false);
    }

    public final List<MapleCharacter> loadCharactersFromDB(boolean reload) {
        if (!reload && characters != null) {
            return characters;
        }
        characters = new ArrayList<>();
        for (int character_id : getCharacterIds(true)) {
            MapleCharacter chr_mine = MapleCharacter.loadCharFromDB(character_id, this, false);
            characters.add(chr_mine);
        }
        return characters;
    }

    public void addCharacter(MapleCharacter chr_new) {
        getCharacterIds().add(chr_new.getId());
        characters.add(chr_new);
    }

    public final boolean checkCharacterId(int character_id) {
        return getCharacterIds().contains(character_id);
    }

    public int getCharaterCount() {
        return getCharacterIds().size();
    }

    private Map<String, ScriptEngine> engines = new HashMap<>();

    public final void setScriptEngine(final String name, final ScriptEngine e) {
        engines.put(name, e);
    }

    public final ScriptEngine getScriptEngine(final String name) {
        return engines.get(name);
    }

    public final void removeScriptEngine(final String name) {
        engines.remove(name);
    }

    // ping pong
    private int alive_req = 0;
    private int alive_res = 0;

    public void recvPong() {
        alive_res++;
    }

    public final void sendPing() {
        alive_req++;
    }

    public static final transient byte LOGIN_NOTLOGGEDIN = 0,
            LOGIN_SERVER_TRANSITION = 1,
            LOGIN_LOGGEDIN = 2,
            LOGIN_WAITING = 3,
            CASH_SHOP_TRANSITION = 4,
            LOGIN_CS_LOGGEDIN = 5,
            CHANGE_CHANNEL = 6;
    public static final String CLIENT_KEY = "CLIENT";

    public final void removalTask() {
        try {
            player.cancelAllBuffs_();
            player.cancelAllDebuffs();
            if (player.getMarriageId() > 0) {
                final MapleQuestStatus stat1 = player.getQuestNAdd(MapleQuest.getInstance(160001));
                final MapleQuestStatus stat2 = player.getQuestNAdd(MapleQuest.getInstance(160002));
                if (stat1.getCustomData() != null && (stat1.getCustomData().equals("2_") || stat1.getCustomData().equals("2"))) {
                    //dc in process of marriage
                    if (stat2.getCustomData() != null) {
                        stat2.setCustomData("0");
                    }
                    stat1.setCustomData("3");
                }
            }
            player.changeRemoval(true);
            if (player.getEventInstance() != null) {
                player.getEventInstance().playerDisconnected(player, player.getId());
            }
            if (player.getMap() != null) {
                switch (player.getMapId()) {
                    case 541010100: //latanica
                    case 541020800: //scar/targa
                    case 551030200: //krexel
                    case 220080001: //pap
                        player.getMap().addDisconnected(player.getId());
                        break;
                }
                player.getMap().removePlayer(player);
            }

            final IMaplePlayerShop shop = player.getPlayerShop();
            if (shop != null) {
                shop.removeVisitor(player);
                if (shop.isOwner(player)) {
                    if (shop.getShopType() == 1 && shop.isAvailable()) {
                        shop.setOpen(true);
                    } else {
                        shop.closeShop(true, true, 6);
                    }
                }
            }
            player.setMessenger(null);
        } catch (final Throwable e) {
            FileoutputUtil.outputFileError(FileoutputUtil.Acc_Stuck, e);
        }
    }

    public final void disconnect(final boolean RemoveInChannelServer, final boolean fromCS) {
        disconnect(RemoveInChannelServer, fromCS, false);
    }

    public final void disconnect(final boolean RemoveInChannelServer, final boolean fromCS, final boolean shutdown) {
        if (player != null && isLoggedIn()) {
            MapleMap map = player.getMap();
            final MapleParty party = player.getParty();
            final boolean clone = player.isClone();
            final String namez = player.getName();
            final boolean hidden = player.isHidden();
            final int gmLevel = player.getGMLevel();
            final int idz = player.getId(), messengerid = player.getMessenger() == null ? 0 : player.getMessenger().getId(), gid = player.getGuildId(), fid = player.getFamilyId();
            final BuddyList bl = player.getBuddylist();
            final MaplePartyCharacter chrp = new MaplePartyCharacter(player);
            final MapleMessengerCharacter chrm = new MapleMessengerCharacter(player);
            final MapleGuildCharacter chrg = player.getMGC();
            final MapleFamilyCharacter chrf = player.getMFC();

            removalTask();
            player.saveToDB(true, fromCS);
            // 追加データ
            if (!fromCS) {
                ExtraDB.saveData(player);
            }
            if (shutdown) {
                this.player = null;
                this.offline = true;
                return;
            }

            if (!fromCS) {
                final ChannelServer ch = ChannelServer.getInstance(map == null ? channel : map.getChannel());

                try {
                    if (ch == null || clone || ch.isShutdown()) {
                        player = null;
                        return;//no idea
                    }
                    if (messengerid > 0) {
                        World.Messenger.leaveMessenger(messengerid, chrm);
                    }
                    if (party != null) {
                        chrp.setOnline(false);
                        World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                        if (map != null && party.getLeader().getId() == idz) {
                            MaplePartyCharacter lchr = null;
                            for (MaplePartyCharacter pchr : party.getMembers()) {
                                if (pchr != null && map.getCharacterById(pchr.getId()) != null && (lchr == null || lchr.getLevel() < pchr.getLevel())) {
                                    lchr = pchr;
                                }
                            }
                            if (lchr != null) {
                                World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER_DC, lchr);
                            }
                        }
                    }
                    if (bl != null) {
                        if (!serverTransition && isLoggedIn()) {
                            World.Buddy.loggedOff(namez, idz, channel, bl.getBuddyIds(), gmLevel, hidden);
                        } else { // Change channel
                            World.Buddy.loggedOn(namez, idz, channel, bl.getBuddyIds(), gmLevel, hidden);
                        }
                    }
                    if (gid > 0) {
                        World.Guild.setGuildMemberOnline(chrg, false, -1);
                    }
                    if (fid > 0) {
                        World.Family.setFamilyMemberOnline(chrf, false, -1);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    FileoutputUtil.outputFileError(FileoutputUtil.Acc_Stuck, e);
                } finally {
                    if (RemoveInChannelServer && ch != null) {
                        ch.removePlayer(idz, namez);
                    }
                    player = null;
                }
            } else {
                final int ch = World.Find.findChannel(idz);
                if (ch > 0) {
                    disconnect(RemoveInChannelServer, false);//u lie
                    return;
                }
                try {
                    if (party != null) {
                        chrp.setOnline(false);
                        World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                    }
                    if (!serverTransition && isLoggedIn()) {
                        World.Buddy.loggedOff(namez, idz, channel, bl.getBuddyIds(), gmLevel, hidden);
                    } else { // Change channel
                        World.Buddy.loggedOn(namez, idz, channel, bl.getBuddyIds(), gmLevel, hidden);
                    }
                    if (gid > 0) {
                        World.Guild.setGuildMemberOnline(chrg, false, -1);
                    }
                    if (player != null) {
                        player.setMessenger(null);
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    FileoutputUtil.outputFileError(FileoutputUtil.Acc_Stuck, e);
                } finally {
                    if (RemoveInChannelServer && ch > 0) {
                        CashShopServer.getPlayerStorage().deregisterPlayer(idz, namez);
                    }
                    player = null;
                }
            }
        }
        if (!serverTransition && isLoggedIn()) {
            DQ_Accounts.updateLoginState(this, MapleClient.LOGIN_NOTLOGGEDIN);
        }
    }

    public final ChannelServer getChannelServer() {
        return ChannelServer.getInstance(channel);
    }

    // TODO : remove, probably not needed.
    private final Lock npc_mutex = new ReentrantLock();

    public final Lock getNPCLock() {
        return npc_mutex;
    }

}
