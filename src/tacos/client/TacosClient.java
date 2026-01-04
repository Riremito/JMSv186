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

import odin.client.MapleCharacter;
import org.apache.mina.common.IoSession;
import tacos.config.DeveloperMode;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.server.Server_CashShop;
import tacos.server.Server_Game;
import tacos.server.Server_Login;
import tacos.server.TacosServer;
import tacos.server.TacosWorld;

/**
 *
 * @author Riremito
 */
public class TacosClient extends BaseClient {

    // account info
    private TacosServer server;
    private int id = 0;
    private String nexon_id = null;
    private String maple_id = null;
    private String password2_hash = null;
    private String password2_salt = null;
    private boolean gameMaster;
    private byte gender = 0;
    private int charslots = TacosConstants.DEFAULT_CHARSLOT;
    // server info
    private boolean logged_in = false;
    private int loginAttempt = 0;
    private boolean migrating = false;
    private int world = 0;
    private int selected_world = 0;
    private int selected_channel = 1;
    // in game info
    private MapleCharacter player = null;

    public TacosClient(IoSession session) {
        super(session);
        this.logged_in = false;
    }

    public TacosServer getServer() {
        return this.server;
    }

    public Server_Login getLoginServer() {
        return (Server_Login) this.server;
    }

    public Server_Game getChannelServer() {
        return (Server_Game) this.server;
    }

    public Server_CashShop getCashShopServer() {
        return (Server_CashShop) this.server;
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
        return ((Server_Game) this.server).getWorld();
    }

    public Server_Game getChannel() {
        return (Server_Game) this.server;
    }

    public int getChannelId() {
        return ((Server_Game) this.server).getChannel(); // from 1.
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
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

    public boolean isLoggedIn() {
        return this.logged_in;
    }

    public void setLoggedIn(boolean logged_in) {
        this.logged_in = logged_in;
    }

    public int loginAttempt() {
        return this.loginAttempt++;
    }

    public void resetLoginAttempt() {
        this.loginAttempt = 0;
    }

    public boolean isMigrating() {
        return this.migrating;
    }

    public void setMigrating() {
        this.migrating = true;
        setPlayer(null);
    }

    public MapleCharacter getPlayer() {
        return this.player;
    }

    public void setPlayer(MapleCharacter player) {
        this.player = player;
    }

    public void loginFailed(String text) {
        DebugLogger.ErrorLog("loginFailed : " + text);
        setPlayer(null);
        getSession().close();
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
