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
package tacos.packet.request;

import odin.client.MapleCharacter;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ops.OpsMessenger;
import tacos.server.TacosMessenger;
import tacos.server.TacosWorld;

/**
 *
 * @author Riremito
 */
public class ReqCUIMessenger {

    public static boolean OnPacket(MapleCharacter chr, ClientPacketHeader header, ClientPacket cp) {

        switch (header) {
            case CP_Messenger: {
                OnMessenger(chr, cp);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnMessenger(MapleCharacter chr, ClientPacket cp) {
        TacosWorld world = chr.getWorld();
        TacosMessenger messenger = world.getMessenger(chr);

        byte type = cp.Decode1();

        switch (OpsMessenger.find(type)) {
            case MSMP_Enter: {
                if (messenger != null) {
                    return false;
                }

                int messenger_id = cp.Decode4();
                if (messenger_id == 0) {
                    messenger = world.createMessenger(chr);
                    return messenger.enter(chr);
                }

                messenger = world.findMessenger(messenger_id);
                if (messenger == null) {
                    return false;
                }

                return messenger.enter(chr);
            }
            case MSMP_Leave: {
                if (messenger == null) {
                    return false;
                }

                return world.leaveMessenger(chr);
            }
            case MSMP_Invite: {
                if (messenger == null) {
                    return false;
                }

                String player_name = cp.DecodeStr();
                return messenger.invite(chr, player_name);
            }
            case MSMP_Blocked: {
                if (messenger != null) {
                    return false;
                }
                String player_name = cp.DecodeStr();
                String own_name = cp.DecodeStr(); // may be not required.
                boolean blocked = (cp.Decode1() != 0); // auto block or not.

                MapleCharacter player = world.findOnlinePlayer(player_name, false);
                if (player == null) {
                    return false;
                }

                messenger = world.getMessenger(player);
                if (messenger == null) {
                    return false;
                }

                return messenger.blocked(own_name, blocked);
            }
            case MSMP_Chat: {
                if (messenger == null) {
                    return false;
                }

                String msg = cp.DecodeStr(); // message includes name lol.
                return messenger.chat(chr, msg);
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnMessenger : not coded, type = " + type);
        return false;
    }
}
