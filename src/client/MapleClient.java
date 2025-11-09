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

import config.DeveloperMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngine;
import database.ExtraDB;
import database.query.DQ_Accounts;
import database.query.DQ_Characters;
import debug.DebugLogger;
import server.network.MaplePacket;
import server.server.Server_Game;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.mina.common.IoSession;

public class MapleClient {

    public static final String CLIENT_KEY = "CLIENT";
    public static final int DEFAULT_CHARSLOT = 6;

    private final IoSession session;
    private boolean migrating = false;
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

    public MapleClient(IoSession session) {
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

    public boolean isMigrating() {
        return this.migrating;
    }

    public void setMigrating() {
        this.migrating = true;
        this.player = null;
    }

    public void loginFailed(String text) {
        DebugLogger.ErrorLog("loginFailed : " + text);
        this.player = null;
        this.session.close();
    }

    public void SendPacket(MaplePacket packet) {
        this.session.write(packet);
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

    public final Server_Game getChannelServer() {
        return Server_Game.getInstance(channel);
    }

    public boolean disconnect(boolean RemoveInChannelServer, boolean fromCS) {
        return disconnect(RemoveInChannelServer, fromCS, false);
    }

    public boolean disconnect(boolean RemoveInChannelServer, boolean fromCS, boolean shutdown) {
        if (!this.loggedIn) {
            return false;
        }
        // save to DB
        if (player != null) {
            player.removalTask();
            player.saveToDB(true, fromCS);
            if (!fromCS) {
                ExtraDB.saveData(player);
            }
        }
        if (shutdown) {
            this.player = null;
            this.migrating = true;
            return true;
        }
        // dc
        if (player != null) {
            player.disconnect(RemoveInChannelServer, fromCS);
            this.player = null;
        }
        if (!serverTransition) {
            DQ_Accounts.updateLoginState(this, MapleClientState.LOGIN_NOTLOGGEDIN);
        }
        return true;
    }

    // TODO : remove, probably not needed.
    private final Lock npc_mutex = new ReentrantLock();

    public final Lock getNPCLock() {
        return npc_mutex;
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
        this.alive_res++;
    }

    public final boolean sendPing() {
        int alive_diff = alive_req - alive_res;

        // or use  PingTimer.
        if (alive_diff <= -1 || 3 <= alive_diff) {
            if (!DeveloperMode.DM_NO_ALIVE_CHECK.get()) {
                DebugLogger.DebugLog("Ping DC : " + alive_req + ", " + alive_res);
                session.close();
                return false;
            }
        }

        this.alive_req++;
        return true;
    }

}
