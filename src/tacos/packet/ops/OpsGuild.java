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
package tacos.packet.ops;

import tacos.config.Config;

/**
 *
 * @author Riremito
 */
public enum OpsGuild implements IPacketOps {
    // GMS v95
    GuildReq_LoadGuild(0x0),
    GuildReq_InputGuildName(0x1),
    GuildReq_CheckGuildName(0x2),
    GuildReq_CreateGuildAgree(0x3),
    GuildReq_CreateNewGuild(0x4),
    GuildReq_InviteGuild(0x5),
    GuildReq_JoinGuild(0x6),
    GuildReq_WithdrawGuild(0x7),
    GuildReq_KickGuild(0x8),
    GuildReq_RemoveGuild(0x9),
    GuildReq_IncMaxMemberNum(0xA),
    GuildReq_ChangeLevel(0xB),
    GuildReq_ChangeJob(0xC),
    GuildReq_SetGradeName(0xD),
    GuildReq_SetMemberGrade(0xE),
    GuildReq_SetMark(0xF),
    GuildReq_SetNotice(0x10),
    GuildReq_InputMark(0x11),
    GuildReq_CheckQuestWaiting(0x12),
    GuildReq_CheckQuestWaiting2(0x13),
    GuildReq_InsertQuestWaiting(0x14),
    GuildReq_CancelQuestWaiting(0x15),
    GuildReq_RemoveQuestCompleteGuild(0x16),
    GuildReq_IncPoint(0x17),
    GuildReq_IncCommitment(0x18),
    GuildReq_SetQuestTime(0x19),
    GuildReq_ShowGuildRanking(0x1A),
    GuildReq_SetSkill(0x1B),
    GuildRes_LoadGuild_Done(0x1C),
    GuildRes_CheckGuildName_Available(0x1D),
    GuildRes_CheckGuildName_AlreadyUsed(0x1E),
    GuildRes_CheckGuildName_Unknown(0x1F),
    GuildRes_CreateGuildAgree_Reply(0x20),
    GuildRes_CreateGuildAgree_Unknown(0x21),
    GuildRes_CreateNewGuild_Done(0x22),
    GuildRes_CreateNewGuild_AlreayJoined(0x23),
    GuildRes_CreateNewGuild_GuildNameAlreayExist(0x24),
    GuildRes_CreateNewGuild_Beginner(0x25),
    GuildRes_CreateNewGuild_Disagree(0x26),
    GuildRes_CreateNewGuild_NotFullParty(0x27),
    GuildRes_CreateNewGuild_Unknown(0x28),
    GuildRes_JoinGuild_Done(0x29),
    GuildRes_JoinGuild_AlreadyJoined(0x2A),
    GuildRes_JoinGuild_AlreadyFull(0x2B),
    GuildRes_JoinGuild_UnknownUser(0x2C),
    GuildRes_JoinGuild_Unknown(0x2D),
    GuildRes_WithdrawGuild_Done(0x2E),
    GuildRes_WithdrawGuild_NotJoined(0x2F),
    GuildRes_WithdrawGuild_Unknown(0x30),
    GuildRes_KickGuild_Done(0x31),
    GuildRes_KickGuild_NotJoined(0x32),
    GuildRes_KickGuild_Unknown(0x33),
    GuildRes_RemoveGuild_Done(0x34),
    GuildRes_RemoveGuild_NotExist(0x35),
    GuildRes_RemoveGuild_Unknown(0x36),
    GuildRes_InviteGuild_BlockedUser(0x37),
    GuildRes_InviteGuild_AlreadyInvited(0x38),
    GuildRes_InviteGuild_Rejected(0x39),
    GuildRes_AdminCannotCreate(0x3A),
    GuildRes_AdminCannotInvite(0x3B),
    GuildRes_IncMaxMemberNum_Done(0x3C),
    GuildRes_IncMaxMemberNum_Unknown(0x3D),
    GuildRes_ChangeLevelOrJob(0x3E),
    GuildRes_NotifyLoginOrLogout(0x3F),
    GuildRes_SetGradeName_Done(0x40),
    GuildRes_SetGradeName_Unknown(0x41),
    GuildRes_SetMemberGrade_Done(0x42),
    GuildRes_SetMemberGrade_Unknown(0x43),
    GuildRes_SetMemberCommitment_Done(0x44),
    GuildRes_SetMark_Done(0x45),
    GuildRes_SetMark_Unknown(0x46),
    GuildRes_SetNotice_Done(0x47),
    GuildRes_InsertQuest(0x48),
    GuildRes_NoticeQuestWaitingOrder(0x49),
    GuildRes_SetGuildCanEnterQuest(0x4A),
    GuildRes_IncPoint_Done(0x4B),
    GuildRes_ShowGuildRanking(0x4C),
    GuildRes_GuildQuest_NotEnoughUser(0x4D),
    GuildRes_GuildQuest_RegisterDisconnected(0x4E),
    GuildRes_GuildQuest_NoticeOrder(0x4F),
    GuildRes_Authkey_Update(0x50),
    GuildRes_SetSkill_Done(0x51),
    GuildRes_ServerMsg(0x52),
    UNKNOWN;

    private final int gms95;
    private int value;

    OpsGuild(int val) {
        this.gms95 = val;
        this.value = val;
    }

    OpsGuild() {
        this.gms95 = -1;
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int val) {
        this.value = val;
    }

    public static OpsGuild find(int val) {
        for (OpsGuild ops : values()) {
            if (ops.get() == val && val != UNKNOWN.get()) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (Config.PostBB()) {
            return;
        }
        // JMS v186 : no ShowGuildRanking/SetSkill requests and no SetMemberCommitment_Done.
        for (OpsGuild ops : values()) {
            if (ops == GuildReq_ShowGuildRanking || ops == GuildReq_SetSkill || ops == GuildRes_SetMemberCommitment_Done || ops == GuildRes_SetSkill_Done || ops == GuildRes_ServerMsg) {
                ops.set(UNKNOWN.get());
            } else if (GuildRes_LoadGuild_Done.ordinal() <= ops.ordinal() && ops.ordinal() < GuildRes_SetMemberCommitment_Done.ordinal()) {
                ops.set(ops.gms95 - 2);
            } else if (GuildRes_SetMemberCommitment_Done.ordinal() < ops.ordinal() && ops != UNKNOWN) {
                ops.set(ops.gms95 - 3);
            }
        }
    }
}
