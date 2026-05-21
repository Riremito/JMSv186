/*
 * Copyright (C) 2026 Riremito
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
package tacos.server;

import java.util.ArrayList;
import tacos.client.TacosCharacter;
import tacos.network.MaplePacket;
import tacos.packet.ops.OpsMessenger;
import tacos.packet.response.ResCUIMessenger;

/**
 *
 * @author Riremito
 */
public class TacosMessenger {

    private static final int MESSENGER_MAX_PLAYERS = 3;
    private static int MESSENGER_ID = 7777;
    private TacosCharacter[] players = new TacosCharacter[MESSENGER_MAX_PLAYERS];
    private int id;

    public TacosMessenger() {
        this.id = MESSENGER_ID++;
    }

    public int getId() {
        return this.id;
    }

    private boolean add(TacosCharacter player) {
        for (int index = 0; index < this.players.length; index++) {
            if (this.players[index] == null) {
                this.players[index] = player;
                return true;
            }
        }

        return false;
    }

    private boolean remove(TacosCharacter player) {
        for (int index = 0; index < this.players.length; index++) {
            if (this.players[index] == player) {
                this.players[index] = null;
                return true;
            }
        }

        return false;
    }

    public int getIndex(TacosCharacter player) {
        for (int index = 0; index < this.players.length; index++) {
            if (this.players[index] == player) {
                return index;
            }
        }
        return -1;
    }

    public ArrayList<TacosCharacter> getPlayers() {
        ArrayList<TacosCharacter> players_in = new ArrayList<>();

        for (TacosCharacter player : this.players) {
            if (player == null) {
                continue;
            }
            players_in.add(player);
        }

        return players_in;
    }

    public void SendPacket(MaplePacket packet, TacosCharacter chr) {
        for (TacosCharacter player : getPlayers()) {
            if (player == chr) {
                continue;
            }
            player.SendPacket(packet);
        }
    }

    boolean check(TacosCharacter player) {
        for (TacosCharacter player_in : getPlayers()) {
            if (player_in == player) {
                return true;
            }
        }
        return false;
    }

    public boolean enter(TacosCharacter player) {
        if (!add(player)) {
            return false;
        }

        player.SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_SelfEnterResult, this, player));
        for (TacosCharacter player_in : getPlayers()) {
            if (player_in == player) {
                continue;
            }
            player.SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Enter, this, player_in));
        }

        SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Enter, this, player), player);
        return true;
    }

    public boolean leave(TacosCharacter player) {
        SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Leave, this, player), null);

        if (!remove(player)) {
            return false;
        }

        return true;
    }

    public boolean invite(TacosCharacter player, String name) {
        TacosWorld world = player.getWorld();
        TacosCharacter invited = world.findOnlinePlayer(name, false);

        if (invited == null) {
            SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_InviteResult, this, player, invited, name), null);
            return true;
        }
        if (world.getMessenger(invited) != null) {
            // already in other messenger.
            SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_InviteResult, this, player, null, name), null);
            return true;
        }

        SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_InviteResult, this, player, invited, name), null);
        invited.SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Invite, this, player, invited, null));
        return true;
    }

    public boolean blocked(String name, boolean blocked) {
        SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Blocked, this, null, null, name, blocked), null);
        return true;
    }

    public boolean chat(TacosCharacter player, String msg) {
        SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Chat, this, player, null, msg), player);
        return true;
    }

    public boolean avatar(TacosCharacter player) {
        SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Avatar, this, player), player);
        return true;
    }
    // TODO : leave when player gets disconnected, channel info update.
}
