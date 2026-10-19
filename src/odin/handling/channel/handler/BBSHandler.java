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

import tacos.client.TacosCharacter;
import odin.handling.world.guild.MapleBBSThread;
import java.util.List;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ClientPacket;

public class BBSHandler {

    private static String correctLength(String in, int maxSize) {
        if (in.length() > maxSize) {
            return in.substring(0, maxSize);
        }
        return in;
    }

    public static void BBSOperation(ClientPacket cp, TacosCharacter chr) {
        if (chr.getGuildId() <= 0) {
            return; // expelled while viewing bbs or hax
        }
        int localthreadid = 0;
        byte action = cp.Decode1();
        switch (action) {
            case 0: // start a new post
                boolean bEdit = cp.Decode1() > 0;
                if (bEdit) {
                    localthreadid = cp.Decode4();
                }
                boolean bNotice = cp.Decode1() > 0;
                String title = correctLength(cp.DecodeStr(), 25);
                String text = correctLength(cp.DecodeStr(), 600);
                int icon = cp.Decode4();
                if (icon >= 0x64 && icon <= 0x6a) {
                    if (!chr.haveItem(5290000 + icon - 0x64, 1, false, true)) {
                        return; // hax, using an nx icon that s/he doesn't have
                    }
                } else if (icon < 0 || icon > 2) {
                    return; // hax, using an invalid icon
                }
                if (!bEdit) {
                    newBBSThread(chr, title, text, icon, bNotice);
                } else {
                    editBBSThread(chr, title, text, icon, localthreadid);
                }
                break;
            case 1: // delete a thread
                localthreadid = cp.Decode4();
                deleteBBSThread(chr, localthreadid);
                break;
            case 2: // list threads
                int start = cp.Decode4();
                listBBSThreads(chr, start * 10);
                break;
            case 3: // list thread + reply, followed by id (int)
                localthreadid = cp.Decode4();
                displayThread(chr, localthreadid);
                break;
            case 4: // reply
                localthreadid = cp.Decode4();
                text = correctLength(cp.DecodeStr(), 25);
                newBBSReply(chr, localthreadid, text);
                break;
            case 5: // delete reply
                localthreadid = cp.Decode4();
                int replyid = cp.Decode4();
                deleteBBSReply(chr, localthreadid, replyid);
                break;
        }
    }

    private static void listBBSThreads(TacosCharacter chr, int start) {
        if (chr.getGuildId() <= 0) {
            return;
        }
        chr.SendPacket(ResCWvsContext.BBSThreadList(chr.getWorld().getGuild().getBBS(chr.getGuildId()), start));
    }

    private static void newBBSReply(TacosCharacter chr, int localthreadid, String text) {
        if (chr.getGuildId() <= 0) {
            return;
        }
        chr.getWorld().getGuild().addBBSReply(chr.getGuildId(), localthreadid, text, chr.getId());
        displayThread(chr, localthreadid);
    }

    private static void editBBSThread(TacosCharacter chr, String title, String text, int icon, int localthreadid) {
        if (chr.getGuildId() <= 0) {
            return; // expelled while viewing?
        }
        chr.getWorld().getGuild().editBBSThread(chr.getGuildId(), localthreadid, title, text, icon, chr.getId(), chr.getGuildRank());
        displayThread(chr, localthreadid);
    }

    private static void newBBSThread(TacosCharacter chr, String title, String text, int icon, boolean bNotice) {
        if (chr.getGuildId() <= 0) {
            return; // expelled while viewing?
        }
        displayThread(chr, chr.getWorld().getGuild().addBBSThread(chr.getGuildId(), title, text, icon, bNotice, chr.getId()));
    }

    private static void deleteBBSThread(TacosCharacter chr, int localthreadid) {
        if (chr.getGuildId() <= 0) {
            return;
        }
        chr.getWorld().getGuild().deleteBBSThread(chr.getGuildId(), localthreadid, chr.getId(), (int) chr.getGuildRank());
    }

    private static void deleteBBSReply(TacosCharacter chr, int localthreadid, int replyid) {
        if (chr.getGuildId() <= 0) {
            return;
        }

        chr.getWorld().getGuild().deleteBBSReply(chr.getGuildId(), localthreadid, replyid, chr.getId(), (int) chr.getGuildRank());
        displayThread(chr, localthreadid);
    }

    private static void displayThread(TacosCharacter chr, int localthreadid) {
        if (chr.getGuildId() <= 0) {
            return;
        }
        List<MapleBBSThread> bbsList = chr.getWorld().getGuild().getBBS(chr.getGuildId());
        if (bbsList != null) {
            for (MapleBBSThread t : bbsList) {
                if (t != null && t.localthreadID == localthreadid) {
                    chr.SendPacket(ResCWvsContext.showThread(t));
                }
            }
        }
    }
}
