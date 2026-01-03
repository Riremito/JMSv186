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
import odin.client.MapleClient;
import odin.handling.world.MapleMessenger;
import odin.handling.world.MapleMessengerCharacter;
import odin.handling.world.OdinWorld;
import tacos.packet.ClientPacket;
import odin.server.maps.MapleMap;
import tacos.packet.ClientPacketHeader;
import tacos.packet.response.ResCUIMessenger;

/**
 *
 * @author Riremito
 */
public class ReqCUIMessenger {

    public static boolean OnPacket(MapleClient c, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return true;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return true;
        }

        switch (header) {
            case CP_Messenger: {
                OnMessenger(cp, c);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static void OnMessenger(ClientPacket cp, MapleClient c) {
        String input;
        MapleMessenger messenger = c.getPlayer().getMessenger();

        switch (cp.Decode1()) {
            case 0x00: // open
                if (messenger == null) {
                    int messengerid = cp.Decode4();
                    if (messengerid == 0) { // create
                        c.getPlayer().setMessenger(OdinWorld.Messenger.createMessenger(new MapleMessengerCharacter(c.getPlayer())));
                    } else { // join
                        messenger = OdinWorld.Messenger.getMessenger(messengerid);
                        if (messenger != null) {
                            final int position = messenger.getLowestPosition();
                            if (position > -1 && position < 4) {
                                c.getPlayer().setMessenger(messenger);
                                OdinWorld.Messenger.joinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()), c.getPlayer().getName(), c.getChannelId());
                            }
                        }
                    }
                }
                break;
            case 0x02: // exit
                if (messenger != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                    OdinWorld.Messenger.leaveMessenger(messenger.getId(), messengerplayer);
                    c.getPlayer().setMessenger(null);
                }
                break;
            case 0x03: // invite

                if (messenger != null) {
                    final int position = messenger.getLowestPosition();
                    if (position <= -1 || position >= 4) {
                        return;
                    }
                    input = cp.DecodeStr();
                    final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);

                    if (target != null) {
                        if (target.getMessenger() == null) {
                            if (!target.isGM() || c.getPlayer().isGM()) {
                                c.getSession().write(ResCUIMessenger.messengerNote(input, 4, 1));
                                target.getClient().getSession().write(ResCUIMessenger.messengerInvite(c.getPlayer().getName(), messenger.getId()));
                            } else {
                                c.getSession().write(ResCUIMessenger.messengerNote(input, 4, 0));
                            }
                        } else {
                            c.getSession().write(ResCUIMessenger.messengerChat(c.getPlayer().getName() + " : " + target.getName() + " is already using Maple Messenger."));
                        }
                    } else {
                        if (OdinWorld.isConnected(input)) {
                            OdinWorld.Messenger.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannelId(), c.getPlayer().isGM());
                        } else {
                            c.getSession().write(ResCUIMessenger.messengerNote(input, 4, 0));
                        }
                    }
                }
                break;
            case 0x05: // decline
                final String targeted = cp.DecodeStr();
                final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
                if (target != null) { // This channel
                    if (target.getMessenger() != null) {
                        target.getClient().getSession().write(ResCUIMessenger.messengerNote(c.getPlayer().getName(), 5, 0));
                    }
                } else { // Other channel
                    if (!c.getPlayer().isGM()) {
                        OdinWorld.Messenger.declineChat(targeted, c.getPlayer().getName());
                    }
                }
                break;
            case 0x06: // message
                if (messenger != null) {
                    OdinWorld.Messenger.messengerChat(messenger.getId(), cp.DecodeStr(), c.getPlayer().getName());

                }
                break;
        }
    }

}
