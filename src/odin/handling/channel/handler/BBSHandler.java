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
package odin.handling.channel.handler;

import tacos.client.TacosClient;
import odin.handling.world.OdinWorld;
import odin.handling.world.guild.MapleBBSThread;
import java.util.List;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ClientPacket;

public class BBSHandler {

    private static String correctLength(final String in, final int maxSize) {
        if (in.length() > maxSize) {
            return in.substring(0, maxSize);
        }
        return in;
    }

    public static final void BBSOperation(ClientPacket cp, final TacosClient client) {
        if (client.getPlayer().getGuildId() <= 0) {
            return; // expelled while viewing bbs or hax
        }
        int localthreadid = 0;
        final byte action = cp.Decode1();
        switch (action) {
            case 0: // start a new post
                final boolean bEdit = cp.Decode1() > 0;
                if (bEdit) {
                    localthreadid = cp.Decode4();
                }
                final boolean bNotice = cp.Decode1() > 0;
                final String title = correctLength(cp.DecodeStr(), 25);
                String text = correctLength(cp.DecodeStr(), 600);
                final int icon = cp.Decode4();
                if (icon >= 0x64 && icon <= 0x6a) {
                    if (!client.getPlayer().haveItem(5290000 + icon - 0x64, 1, false, true)) {
                        return; // hax, using an nx icon that s/he doesn't have
                    }
                } else if (icon < 0 || icon > 2) {
                    return; // hax, using an invalid icon
                }
                if (!bEdit) {
                    newBBSThread(client, title, text, icon, bNotice);
                } else {
                    editBBSThread(client, title, text, icon, localthreadid);
                }
                break;
            case 1: // delete a thread
                localthreadid = cp.Decode4();
                deleteBBSThread(client, localthreadid);
                break;
            case 2: // list threads
                int start = cp.Decode4();
                listBBSThreads(client, start * 10);
                break;
            case 3: // list thread + reply, followed by id (int)
                localthreadid = cp.Decode4();
                displayThread(client, localthreadid);
                break;
            case 4: // reply
                localthreadid = cp.Decode4();
                text = correctLength(cp.DecodeStr(), 25);
                newBBSReply(client, localthreadid, text);
                break;
            case 5: // delete reply
                localthreadid = cp.Decode4();
                int replyid = cp.Decode4();
                deleteBBSReply(client, localthreadid, replyid);
                break;
        }
    }

    private static void listBBSThreads(TacosClient client, int start) {
        if (client.getPlayer().getGuildId() <= 0) {
            return;
        }
        client.SendPacket(ResCWvsContext.BBSThreadList(OdinWorld.Guild.getBBS(client.getPlayer().getGuildId()), start));
    }

    private static void newBBSReply(final TacosClient client, final int localthreadid, final String text) {
        if (client.getPlayer().getGuildId() <= 0) {
            return;
        }
        OdinWorld.Guild.addBBSReply(client.getPlayer().getGuildId(), localthreadid, text, client.getPlayer().getId());
        displayThread(client, localthreadid);
    }

    private static void editBBSThread(final TacosClient client, final String title, final String text, final int icon, final int localthreadid) {
        if (client.getPlayer().getGuildId() <= 0) {
            return; // expelled while viewing?
        }
        OdinWorld.Guild.editBBSThread(client.getPlayer().getGuildId(), localthreadid, title, text, icon, client.getPlayer().getId(), client.getPlayer().getGuildRank());
        displayThread(client, localthreadid);
    }

    private static void newBBSThread(final TacosClient client, final String title, final String text, final int icon, final boolean bNotice) {
        if (client.getPlayer().getGuildId() <= 0) {
            return; // expelled while viewing?
        }
        displayThread(client, OdinWorld.Guild.addBBSThread(client.getPlayer().getGuildId(), title, text, icon, bNotice, client.getPlayer().getId()));
    }

    private static final void deleteBBSThread(final TacosClient client, final int localthreadid) {
        if (client.getPlayer().getGuildId() <= 0) {
            return;
        }
        OdinWorld.Guild.deleteBBSThread(client.getPlayer().getGuildId(), localthreadid, client.getPlayer().getId(), (int) client.getPlayer().getGuildRank());
    }

    private static void deleteBBSReply(final TacosClient client, final int localthreadid, final int replyid) {
        if (client.getPlayer().getGuildId() <= 0) {
            return;
        }

        OdinWorld.Guild.deleteBBSReply(client.getPlayer().getGuildId(), localthreadid, replyid, client.getPlayer().getId(), (int) client.getPlayer().getGuildRank());
        displayThread(client, localthreadid);
    }

    private static void displayThread(final TacosClient client, final int localthreadid) {
        if (client.getPlayer().getGuildId() <= 0) {
            return;
        }
        final List<MapleBBSThread> bbsList = OdinWorld.Guild.getBBS(client.getPlayer().getGuildId());
        if (bbsList != null) {
            for (MapleBBSThread t : bbsList) {
                if (t != null && t.localthreadID == localthreadid) {
                    client.SendPacket(ResCWvsContext.showThread(t));
                }
            }
        }
    }
}
