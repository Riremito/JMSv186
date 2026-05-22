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
import tacos.packet.builder.MessengerData;
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

        MessengerData pd_self = MessengerData.builder()
                .player_index(this.getIndex(player))
                .build();

        player.SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_SelfEnterResult, pd_self));
        for (TacosCharacter player_in : getPlayers()) {
            if (player_in == player) {
                continue;
            }

            MessengerData pd_enter = MessengerData.builder()
                    .player_index(this.getIndex(player_in))
                    .player(player_in)
                    .is_new(false)
                    .build();

            player.SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Enter, pd_enter));
        }

        MessengerData pd_enter = MessengerData.builder()
                .player_index(this.getIndex(player))
                .player(player)
                .is_new(true)
                .build();
        SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Enter, pd_enter), player);
        return true;
    }

    public boolean leave(TacosCharacter player) {
        MessengerData pd = MessengerData.builder()
                .player_index(this.getIndex(player))
                .build();
        SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Leave, pd), null);

        if (!remove(player)) {
            return false;
        }

        return true;
    }

    public boolean invite(TacosCharacter player, String invitee_name) {
        TacosWorld world = player.getWorld();
        TacosCharacter invitee = world.findOnlinePlayer(invitee_name, false);

        if (invitee != null) {
            if (world.getMessenger(invitee) != null) {
                invitee = null;
            }
        }

        MessengerData pd_result = MessengerData.builder()
                .invitee_name(invitee_name)
                .is_found((invitee != null))
                .build();
        SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_InviteResult, pd_result), null);

        if (invitee == null) {
            return false;
        }

        MessengerData pd_invite = MessengerData.builder()
                .inviter_name(player.getName())
                .inviter_channel_id(player.getChannelId() - 1)
                .messenger_id(this.id)
                .build();

        invitee.SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Invite, pd_invite));
        return true;
    }

    public boolean blocked(String name, boolean blocked) {
        MessengerData pd = MessengerData.builder()
                .invitee_name(name)
                .is_auto_blocked(blocked)
                .build();

        SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Blocked, pd), null);
        return true;
    }

    public boolean chat(TacosCharacter player, String message) {
        MessengerData pd = MessengerData.builder()
                .message(message)
                .build();

        SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Chat, pd), player);
        return true;
    }

    public boolean avatar(TacosCharacter player) {
        MessengerData pd = MessengerData.builder()
                .player_index(this.getIndex(player))
                .player(player)
                .build();

        SendPacket(ResCUIMessenger.Messenger(OpsMessenger.MSMP_Avatar, pd), player);
        return true;
    }
    // TODO : leave when player gets disconnected, channel info update.
}
