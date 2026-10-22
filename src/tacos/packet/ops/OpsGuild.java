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
import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public enum OpsGuild implements IPacketOps {
    // GMS v95
    GuildReq_LoadGuild(0),
    GuildReq_InputGuildName(1),
    GuildReq_CheckGuildName(2),
    GuildReq_CreateGuildAgree(3),
    GuildReq_CreateNewGuild(4),
    GuildReq_InviteGuild(5),
    GuildReq_JoinGuild(6),
    GuildReq_WithdrawGuild(7),
    GuildReq_KickGuild(8),
    GuildReq_RemoveGuild(9),
    GuildReq_IncMaxMemberNum(10),
    GuildReq_ChangeLevel(11),
    GuildReq_ChangeJob(12),
    GuildReq_SetGradeName(13),
    GuildReq_SetMemberGrade(14),
    GuildReq_SetMark(15),
    GuildReq_SetNotice(16),
    GuildReq_InputMark(17),
    GuildReq_CheckQuestWaiting(18),
    GuildReq_CheckQuestWaiting2(19),
    GuildReq_InsertQuestWaiting(20),
    GuildReq_CancelQuestWaiting(21),
    GuildReq_RemoveQuestCompleteGuild(22),
    GuildReq_IncPoint(23),
    GuildReq_IncCommitment(24),
    GuildReq_SetQuestTime(25),
    GuildReq_ShowGuildRanking(26),
    GuildReq_SetSkill(27),
    GuildRes_LoadGuild_Done(28),
    GuildRes_CheckGuildName_Available(29),
    GuildRes_CheckGuildName_AlreadyUsed(30),
    GuildRes_CheckGuildName_Unknown(31),
    GuildRes_CreateGuildAgree_Reply(32),
    GuildRes_CreateGuildAgree_Unknown(33),
    GuildRes_CreateNewGuild_Done(34),
    GuildRes_CreateNewGuild_AlreayJoined(35),
    GuildRes_CreateNewGuild_GuildNameAlreayExist(36),
    GuildRes_CreateNewGuild_Beginner(37),
    GuildRes_CreateNewGuild_Disagree(38),
    GuildRes_CreateNewGuild_NotFullParty(39),
    GuildRes_CreateNewGuild_Unknown(40),
    GuildRes_JoinGuild_Done(41),
    GuildRes_JoinGuild_AlreadyJoined(42),
    GuildRes_JoinGuild_AlreadyFull(43),
    GuildRes_JoinGuild_UnknownUser(44),
    GuildRes_JoinGuild_Unknown(45),
    GuildRes_WithdrawGuild_Done(46),
    GuildRes_WithdrawGuild_NotJoined(47),
    GuildRes_WithdrawGuild_Unknown(48),
    GuildRes_KickGuild_Done(49),
    GuildRes_KickGuild_NotJoined(50),
    GuildRes_KickGuild_Unknown(51),
    GuildRes_RemoveGuild_Done(52),
    GuildRes_RemoveGuild_NotExist(53),
    GuildRes_RemoveGuild_Unknown(54),
    GuildRes_InviteGuild_BlockedUser(55),
    GuildRes_InviteGuild_AlreadyInvited(56),
    GuildRes_InviteGuild_Rejected(57),
    GuildRes_AdminCannotCreate(58),
    GuildRes_AdminCannotInvite(59),
    GuildRes_IncMaxMemberNum_Done(60),
    GuildRes_IncMaxMemberNum_Unknown(61),
    GuildRes_ChangeLevelOrJob(62),
    GuildRes_NotifyLoginOrLogout(63),
    GuildRes_SetGradeName_Done(64),
    GuildRes_SetGradeName_Unknown(65),
    GuildRes_SetMemberGrade_Done(66),
    GuildRes_SetMemberGrade_Unknown(67),
    GuildRes_SetMemberCommitment_Done(68),
    GuildRes_SetMark_Done(69),
    GuildRes_SetMark_Unknown(70),
    GuildRes_SetNotice_Done(71),
    GuildRes_InsertQuest(72),
    GuildRes_NoticeQuestWaitingOrder(73),
    GuildRes_SetGuildCanEnterQuest(74),
    GuildRes_IncPoint_Done(75),
    GuildRes_ShowGuildRanking(76),
    GuildRes_GuildQuest_NotEnoughUser(77),
    GuildRes_GuildQuest_RegisterDisconnected(78),
    GuildRes_GuildQuest_NoticeOrder(79),
    GuildRes_Authkey_Update(80),
    GuildRes_SetSkill_Done(81),
    GuildRes_ServerMsg(82),
    UNKNOWN;

    private int value;

    OpsGuild(int val) {
        this.value = val;
    }

    OpsGuild() {
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

    public static void clear() {
        for (OpsGuild ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        if (Config.PostBB()) {
            return;
        }
        // JMS v186 : no ShowGuildRanking/SetSkill requests and no SetMemberCommitment_Done.
        if (Region.JMS.check()) {
            clear();
            GuildReq_LoadGuild.set(0);
            GuildReq_InputGuildName.set(1);
            GuildReq_CheckGuildName.set(2);
            GuildReq_CreateGuildAgree.set(3);
            GuildReq_CreateNewGuild.set(4);
            GuildReq_InviteGuild.set(5);
            GuildReq_JoinGuild.set(6);
            GuildReq_WithdrawGuild.set(7);
            GuildReq_KickGuild.set(8);
            GuildReq_RemoveGuild.set(9);
            GuildReq_IncMaxMemberNum.set(10);
            GuildReq_ChangeLevel.set(11);
            GuildReq_ChangeJob.set(12);
            GuildReq_SetGradeName.set(13);
            GuildReq_SetMemberGrade.set(14);
            GuildReq_SetMark.set(15);
            GuildReq_SetNotice.set(16);
            GuildReq_InputMark.set(17);
            GuildReq_CheckQuestWaiting.set(18);
            GuildReq_CheckQuestWaiting2.set(19);
            GuildReq_InsertQuestWaiting.set(20);
            GuildReq_CancelQuestWaiting.set(21);
            GuildReq_RemoveQuestCompleteGuild.set(22);
            GuildReq_IncPoint.set(23);
            GuildReq_IncCommitment.set(24);
            GuildReq_SetQuestTime.set(25);
            GuildRes_LoadGuild_Done.set(26);
            GuildRes_CheckGuildName_Available.set(27);
            GuildRes_CheckGuildName_AlreadyUsed.set(28);
            GuildRes_CheckGuildName_Unknown.set(29);
            GuildRes_CreateGuildAgree_Reply.set(30);
            GuildRes_CreateGuildAgree_Unknown.set(31);
            GuildRes_CreateNewGuild_Done.set(32);
            GuildRes_CreateNewGuild_AlreayJoined.set(33);
            GuildRes_CreateNewGuild_GuildNameAlreayExist.set(34);
            GuildRes_CreateNewGuild_Beginner.set(35);
            GuildRes_CreateNewGuild_Disagree.set(36);
            GuildRes_CreateNewGuild_NotFullParty.set(37);
            GuildRes_CreateNewGuild_Unknown.set(38);
            GuildRes_JoinGuild_Done.set(39);
            GuildRes_JoinGuild_AlreadyJoined.set(40);
            GuildRes_JoinGuild_AlreadyFull.set(41);
            GuildRes_JoinGuild_UnknownUser.set(42);
            GuildRes_JoinGuild_Unknown.set(43);
            GuildRes_WithdrawGuild_Done.set(44);
            GuildRes_WithdrawGuild_NotJoined.set(45);
            GuildRes_WithdrawGuild_Unknown.set(46);
            GuildRes_KickGuild_Done.set(47);
            GuildRes_KickGuild_NotJoined.set(48);
            GuildRes_KickGuild_Unknown.set(49);
            GuildRes_RemoveGuild_Done.set(50);
            GuildRes_RemoveGuild_NotExist.set(51);
            GuildRes_RemoveGuild_Unknown.set(52);
            GuildRes_InviteGuild_BlockedUser.set(53);
            GuildRes_InviteGuild_AlreadyInvited.set(54);
            GuildRes_InviteGuild_Rejected.set(55);
            GuildRes_AdminCannotCreate.set(56);
            GuildRes_AdminCannotInvite.set(57);
            GuildRes_IncMaxMemberNum_Done.set(58);
            GuildRes_IncMaxMemberNum_Unknown.set(59);
            GuildRes_ChangeLevelOrJob.set(60);
            GuildRes_NotifyLoginOrLogout.set(61);
            GuildRes_SetGradeName_Done.set(62);
            GuildRes_SetGradeName_Unknown.set(63);
            GuildRes_SetMemberGrade_Done.set(64);
            GuildRes_SetMemberGrade_Unknown.set(65);
            GuildRes_SetMark_Done.set(66);
            GuildRes_SetMark_Unknown.set(67);
            GuildRes_SetNotice_Done.set(68);
            GuildRes_InsertQuest.set(69);
            GuildRes_NoticeQuestWaitingOrder.set(70);
            GuildRes_SetGuildCanEnterQuest.set(71);
            GuildRes_IncPoint_Done.set(72);
            GuildRes_ShowGuildRanking.set(73);
            GuildRes_GuildQuest_NotEnoughUser.set(74);
            GuildRes_GuildQuest_RegisterDisconnected.set(75);
            GuildRes_GuildQuest_NoticeOrder.set(76);
            GuildRes_Authkey_Update.set(77);
            return;
        }
    }
}
