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

import odin.handling.world.guild.MapleBBSThread;
import odin.handling.world.guild.MapleGuild;
import tacos.client.TacosCharacter;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsGuildBBS;
import tacos.packet.response.ResCWvsContext;

public class BBSHandler {

    private static final int TITLE_MAX_LENGTH = 25;
    private static final int TEXT_MAX_LENGTH = 600;
    private static final int COMMENT_MAX_LENGTH = 25;
    // basic emoticons are 0 to 2, cash emoticons are 100 to 106 (item 5290000 to 5290006).
    private static final int CASH_EMOTICON_MIN = 100;
    private static final int CASH_EMOTICON_MAX = 106;
    private static final int CASH_EMOTICON_ITEM_ID = 5290000;

    private static String correctLength(String in, int maxSize) {
        return (in.length() > maxSize) ? in.substring(0, maxSize) : in;
    }

    // CP_GuildBBS
    public static void BBSOperation(ClientPacket cp, TacosCharacter chr) {
        MapleGuild guild = (chr.getGuildId() > 0) ? chr.getWorld().getGuild().getGuild(chr.getGuildId()) : null;
        if (guild == null) {
            return; // expelled while viewing bbs
        }
        int op = cp.Decode1();
        switch (OpsGuildBBS.find(op)) {
            case GuildBBS_Register: {
                boolean bModify = cp.Decode1() != 0;
                int localthreadid = bModify ? cp.Decode4() : 0;
                boolean bNotice = cp.Decode1() != 0;
                String title = correctLength(cp.DecodeStr(), TITLE_MAX_LENGTH);
                String text = correctLength(cp.DecodeStr(), TEXT_MAX_LENGTH);
                int icon = cp.Decode4();
                if (!isValidEmoticon(chr, icon)) {
                    return;
                }
                if (bModify) {
                    guild.editBBSThread(localthreadid, title, text, icon, chr.getId(), chr.getGuildRank());
                } else {
                    localthreadid = guild.addBBSThread(title, text, icon, bNotice, chr.getId());
                }
                displayThread(chr, guild, localthreadid);
                return;
            }
            case GuildBBS_Delete: {
                int localthreadid = cp.Decode4();
                guild.deleteBBSThread(localthreadid, chr.getId(), chr.getGuildRank());
                return;
            }
            case GuildBBS_LoadListRequest: {
                int page = cp.Decode4(); // m_nEntryListStart
                chr.SendPacket(ResCWvsContext.GuildBBS_LoadListResult(guild, page * 10));
                return;
            }
            case GuildBBS_ViewEntryRequest: {
                int localthreadid = cp.Decode4();
                displayThread(chr, guild, localthreadid);
                return;
            }
            case GuildBBS_RegisterComment: {
                int localthreadid = cp.Decode4();
                String text = correctLength(cp.DecodeStr(), COMMENT_MAX_LENGTH);
                guild.addBBSReply(localthreadid, text, chr.getId());
                displayThread(chr, guild, localthreadid);
                return;
            }
            case GuildBBS_DeleteComment: {
                int localthreadid = cp.Decode4();
                int replyid = cp.Decode4(); // m_nSN
                guild.deleteBBSReply(localthreadid, replyid, chr.getId(), chr.getGuildRank());
                displayThread(chr, guild, localthreadid);
                return;
            }
            default: {
                DebugLogger.ErrorLog("GuildBBS : not coded " + op);
                return;
            }
        }
    }

    private static boolean isValidEmoticon(TacosCharacter chr, int icon) {
        if (CASH_EMOTICON_MIN <= icon && icon <= CASH_EMOTICON_MAX) {
            return chr.haveItem(CASH_EMOTICON_ITEM_ID + icon - CASH_EMOTICON_MIN, 1, false, true);
        }
        return 0 <= icon && icon <= 2;
    }

    private static void displayThread(TacosCharacter chr, MapleGuild guild, int localthreadid) {
        MapleBBSThread thread = guild.getBBSThread(localthreadid);
        if (thread == null) {
            chr.SendPacket(ResCWvsContext.GuildBBS_EntryNotFound());
            return;
        }
        chr.SendPacket(ResCWvsContext.GuildBBS_ViewEntryResult(thread));
    }
}
