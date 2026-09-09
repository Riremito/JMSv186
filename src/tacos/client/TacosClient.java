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
package tacos.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import odin.client.MapleCharacter;
import org.apache.mina.common.IoSession;
import tacos.config.DeveloperMode;
import tacos.constants.MapleClientState;
import tacos.constants.TacosConstants;
import tacos.database.LazyDatabase;
import tacos.database.query.DQ_Accounts;
import tacos.debug.DebugLogger;
import tacos.packet.response.ResCLogin;
import tacos.server.TacosCashShop;
import tacos.server.TacosChannel;
import tacos.server.TacosITC;
import tacos.server.TacosLogin;
import tacos.server.TacosServer;
import tacos.server.TacosServerType;
import tacos.server.TacosWorld;
import tacos.tools.TacosTools;

/**
 *
 * @author Riremito
 */
public class TacosClient extends BaseClient {

    public static final String CLIENT_KEY = "CLIENT";

    private byte[] m_uSeqSnd = new byte[4];
    private byte[] m_uSeqRcv = new byte[4];

    // account info
    private TacosServer server;
    private int id = 0;
    private String machine_id = null;
    private long client_key = 0;
    private String nexon_id = null;
    private String maple_id = null;
    private String password2_hash = null;
    private String password2_salt = null;
    private boolean gameMaster;
    private byte gender = 0;
    private int charslots = TacosConstants.DEFAULT_CHARSLOT;
    private List<MapleCharacter> characters = null;
    // server info
    private int loginAttempt = 0;
    private int world = 0;
    private int selected_world = 0;
    private int selected_channel = 1;
    // in game info
    private MapleCharacter character = null;

    public TacosClient(IoSession session) {
        super(session);
        this.characters = new ArrayList<>();
        Random rand = new Random();
        rand.nextBytes(this.m_uSeqSnd);
        rand.nextBytes(this.m_uSeqRcv);
    }

    public byte[] getSeqSnd() {
        return this.m_uSeqSnd;
    }

    public void setSeqSnd(byte[] iv) {
        System.arraycopy(iv, 0, this.m_uSeqSnd, 0, this.m_uSeqSnd.length);
    }

    public byte[] getSeqRcv() {
        return this.m_uSeqRcv;
    }

    public void setSeqRcv(byte[] iv) {
        System.arraycopy(iv, 0, this.m_uSeqRcv, 0, this.m_uSeqRcv.length);
    }

    public TacosServer getServer() {
        return this.server;
    }

    public TacosLogin getLoginServer() {
        return (TacosLogin) this.server;
    }

    public TacosChannel getChannelServer() {
        return (TacosChannel) this.server;
    }

    public TacosCashShop getCashShopServer() {
        return (TacosCashShop) this.server;
    }

    public void setServer(TacosServer server) {
        this.server = server;
    }

    public int getSelectedWorld() {
        return this.selected_world;
    }

    public void setSelectedWorld(int selected_world) {
        DebugLogger.DebugLog("setSelectedWorld : " + selected_world);
        this.selected_world = selected_world;
    }

    public int getSelectedChannel() {
        return this.selected_channel;
    }

    public void setSelectedChannel(int selected_channel) {
        DebugLogger.DebugLog("setSelectedChannel : " + selected_channel);
        this.selected_channel = selected_channel;
    }

    public TacosWorld getWorld() {
        switch (this.server.getType()) {
            case LOGIN_SERVER: {
                return null;
            }
            case GAME_SERVER: {
                return ((TacosChannel) this.server).getWorld();
            }
            case ITC_SERVER: {
                return ((TacosITC) this.server).getWorld();
            }
            case CASHSHOP_SERVER: {
                return ((TacosCashShop) this.server).getWorld();
            }
            default: {
                break;
            }
        }
        return null;
    }

    public int getChannelId() {
        return ((TacosChannel) this.server).getChannel(); // from 1.
    }

    public void sendSelectCharacterResult(TacosServer game_server, int character_id) {
        // send next server ip and port.
        SendPacket(ResCLogin.SelectCharacterResult(game_server, character_id));
        // stop sending/receiving packets.
        closeSession();
    }

    public void sendSelectCharacterByVACResult(TacosServer game_server, int character_id) {
        SendPacket(ResCLogin.SelectCharacterByVACResult(game_server, character_id));
        closeSession();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMachineId() {
        return this.machine_id;
    }

    public void setMachineId(byte machine_id[]) {
        this.machine_id = TacosTools.DatatoString(machine_id);
    }

    public long getClientKey() {
        return this.client_key;
    }

    public void setClientKey(long client_key) {
        this.client_key = client_key;
    }

    public String getNexonId() {
        return this.nexon_id;
    }

    public void setNexonId(String nexon_id) {
        this.nexon_id = nexon_id;
    }

    public String getMapleId() {
        return this.maple_id;
    }

    public void setMapleId(String maple_id) {
        this.maple_id = maple_id;
        setNexonId(maple_id); // test
    }

    public void setPassword2Hash(String password2_hash) {
        this.password2_hash = password2_hash;
    }

    public void setPassword2Salt(String password2_salt) {
        this.password2_salt = password2_salt;
    }

    public boolean isGameMaster() {
        return this.gameMaster;
    }

    public void setGameMaster(boolean gm) {
        this.gameMaster = gm;
    }

    public byte getGender() {
        return this.gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public int getCharSlots() {
        return this.charslots;
    }

    public boolean setCharSlots(int charslots) {
        this.charslots = charslots;
        return true;
    }

    public int loginAttempt() {
        return this.loginAttempt++;
    }

    public void resetLoginAttempt() {
        this.loginAttempt = 0;
    }

    public MapleCharacter getPlayer() {
        return this.character;
    }

    public void setPlayer(MapleCharacter character) {
        this.character = character;
    }

    public void loginFailed(String text) {
        DebugLogger.ErrorLog("loginFailed : " + text);
        setPlayer(null);
        getSession().close();
    }

    public List<MapleCharacter> loadCharactersFromDB(List<Integer> character_ids) {
        this.characters = new ArrayList<>();
        for (int character_id : character_ids) {
            MapleCharacter chr_mine = MapleCharacter.loadCharFromDB(character_id, this, false);
            this.characters.add(chr_mine);
        }
        return this.characters;
    }

    public List<MapleCharacter> getCharacters() {
        return this.characters;
    }

    public void addCharacter(MapleCharacter new_character) {
        this.characters.add(new_character);
    }

    public boolean checkCharacterId(int character_id) {
        for (MapleCharacter chr : this.characters) {
            if (chr.getId() == character_id) {
                return true;
            }
        }
        return false;
    }

    public int getCharaterCount() {
        return this.characters.size();
    }

    public boolean disconnect(boolean RemoveInChannelServer, boolean shutdown) {
        MapleCharacter chr = getPlayer();
        // save to DB
        if (chr != null) {
            chr.removalTask();
            chr.saveToDB(false);
            LazyDatabase.saveData(getPlayer());
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
            chr.disconnect(RemoveInChannelServer, false);
            if (getWorld().findMigratingPlayer(chr.getId()) == null) {
                DQ_Accounts.updateLoginState(this, MapleClientState.LOGIN_NOTLOGGEDIN);
            }
        }
        return true;
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
                getSession().close();
                return false;
            }
        }

        this.alive_req++;
        return true;
    }
}
