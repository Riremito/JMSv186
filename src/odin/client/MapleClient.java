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

import java.util.List;
import tacos.database.LazyDatabase;
import tacos.database.query.DQ_Characters;
import java.util.ArrayList;
import org.apache.mina.common.IoSession;
import tacos.client.TacosClient;
import tacos.constants.MapleClientState;
import tacos.database.query.DQ_Accounts;
import tacos.server.TacosServerType;

public class MapleClient extends TacosClient {

    public static final String CLIENT_KEY = "CLIENT";

    private List<Integer> character_ids = null;
    private List<MapleCharacter> characters = null;

    public MapleClient(IoSession session) {
        super(session);
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

    public boolean disconnect(boolean RemoveInChannelServer, boolean fromCS) {
        return disconnect(RemoveInChannelServer, fromCS, false);
    }

    public boolean disconnect(boolean RemoveInChannelServer, boolean fromCS, boolean shutdown) {
        MapleCharacter chr = getPlayer();
        // save to DB
        if (chr != null) {
            chr.removalTask();
            chr.saveToDB(true, fromCS);
            if (!fromCS) {
                LazyDatabase.saveData(getPlayer());
            }
        }
        if (shutdown) {
            closeSession();
            return true;
        }
        if (getServer().getType() == TacosServerType.LOGIN_SERVER) {
            DQ_Accounts.updateLoginState(this, MapleClientState.LOGIN_NOTLOGGEDIN);
            return true;
        }
        // dc
        if (chr != null) {
            chr.disconnect(RemoveInChannelServer, fromCS);
            if (getWorld().findMigratingPlayer(chr.getId()) == null) {
                DQ_Accounts.updateLoginState(this, MapleClientState.LOGIN_NOTLOGGEDIN);
            }
        }
        return true;
    }

}
