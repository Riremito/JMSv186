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
package tacos.packet.request;

import odin.handling.world.guild.MapleBBSThread;
import odin.handling.world.guild.MapleGuild;
import tacos.client.TacosCharacter;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsGuildBBS;
import tacos.packet.response.ResCWvsContext;

/**
 *
 * @author Riremito
 */
public class ReqCUser_GuildBBS {

    private static final int TITLE_MAX_LENGTH = 25;
    private static final int TEXT_MAX_LENGTH = 600;
    private static final int COMMENT_MAX_LENGTH = 25;
    private static final int CASH_EMOTICON_MIN = 100;
    private static final int CASH_EMOTICON_MAX = 106;
    private static final int CASH_EMOTICON_ITEM_ID = 5290000;

    public static void OnGuildBBS(ClientPacket cp, TacosCharacter chr) {
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

    private static String correctLength(String in, int maxSize) {
        return (in.length() > maxSize) ? in.substring(0, maxSize) : in;
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
