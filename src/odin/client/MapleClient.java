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
package odin.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.ScriptEngine;
import tacos.database.ExtraDB;
import tacos.database.query.DQ_Accounts;
import tacos.database.query.DQ_Characters;
import tacos.server.ServerOdinGame;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.mina.common.IoSession;
import tacos.client.TacosClient;

public class MapleClient extends TacosClient {

    public static final String CLIENT_KEY = "CLIENT";

    private boolean serverTransition = false;
    private List<Integer> character_ids = null;
    private List<MapleCharacter> characters = null;

    public MapleClient(IoSession session) {
        super(session);
    }

    public boolean getServerTransition() {
        return this.serverTransition;
    }

    public void setServerTransition(boolean serverTransition) {
        this.serverTransition = serverTransition;
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

    public final ServerOdinGame getChannelServer() {
        return ServerOdinGame.getInstance(getChannel());
    }

    public boolean disconnect(boolean RemoveInChannelServer, boolean fromCS) {
        return disconnect(RemoveInChannelServer, fromCS, false);
    }

    public boolean disconnect(boolean RemoveInChannelServer, boolean fromCS, boolean shutdown) {
        if (!isLoggedIn()) {
            return false;
        }
        // save to DB
        if (getPlayer() != null) {
            getPlayer().removalTask();
            getPlayer().saveToDB(true, fromCS);
            if (!fromCS) {
                ExtraDB.saveData(getPlayer());
            }
        }
        if (shutdown) {
            setMigrating();
            return true;
        }
        // dc
        if (getPlayer() != null) {
            getPlayer().disconnect(RemoveInChannelServer, fromCS);
            setPlayer(null);
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

}
