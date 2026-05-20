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

import tacos.config.Region;
import tacos.config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsParty implements IPacketOps {
    PartyReq_LoadParty(0),
    PartyReq_CreateNewParty(1),
    PartyReq_WithdrawParty(2),
    PartyReq_JoinParty(3),
    PartyReq_InviteParty(4),
    PartyReq_KickParty(5),
    PartyReq_ChangePartyBoss(6),
    PartyRes_LoadParty_Done(7),
    PartyRes_CreateNewParty_Done(8),
    PartyRes_CreateNewParty_AlreayJoined(9),
    PartyRes_CreateNewParty_Beginner(10),
    PartyRes_CreateNewParty_Unknown(11),
    PartyRes_WithdrawParty_Done(12),
    PartyRes_WithdrawParty_NotJoined(13),
    PartyRes_WithdrawParty_Unknown(14),
    PartyRes_JoinParty_Done(15),
    PartyRes_JoinParty_Done2(16),
    PartyRes_JoinParty_AlreadyJoined(17),
    PartyRes_JoinParty_AlreadyFull(18),
    PartyRes_JoinParty_OverDesiredSize(19),
    PartyRes_JoinParty_UnknownUser(20),
    PartyRes_JoinParty_Unknown(21),
    PartyRes_InviteParty_Sent(22),
    PartyRes_InviteParty_BlockedUser(23),
    PartyRes_InviteParty_AlreadyInvited(24),
    PartyRes_InviteParty_AlreadyInvitedByInviter(25),
    PartyRes_InviteParty_Rejected(26),
    PartyRes_InviteParty_Accepted(27),
    PartyRes_KickParty_Done(28),
    PartyRes_KickParty_FieldLimit(29),
    PartyRes_KickParty_Unknown(30),
    PartyRes_ChangePartyBoss_Done(31),
    PartyRes_ChangePartyBoss_NotSameField(32),
    PartyRes_ChangePartyBoss_NoMemberInSameField(33),
    PartyRes_ChangePartyBoss_NotSameChannel(34),
    PartyRes_ChangePartyBoss_Unknown(35),
    PartyRes_AdminCannotCreate(36),
    PartyRes_AdminCannotInvite(37),
    PartyRes_UserMigration(38),
    PartyRes_ChangeLevelOrJob(39),
    PartyRes_SuccessToSelectPQReward(40),
    PartyRes_FailToSelectPQReward(41),
    PartyRes_ReceivePQReward(42),
    PartyRes_FailToRequestPQReward(43),
    PartyRes_CanNotInThisField(44),
    PartyRes_ServerMsg(45),
    PartyInfo_TownPortalChanged(46),
    PartyInfo_OpenGate(47),
    ExpeditionReq_Load(48),
    ExpeditionReq_CreateNew(49),
    ExpeditionReq_Invite(50),
    ExpeditionReq_ResponseInvite(51),
    ExpeditionReq_Withdraw(52),
    ExpeditionReq_Kick(53),
    ExpeditionReq_ChangeMaster(54),
    ExpeditionReq_ChangePartyBoss(55),
    ExpeditionReq_RelocateMember(56),
    ExpeditionNoti_Load_Done(57),
    ExpeditionNoti_Load_Fail(58),
    ExpeditionNoti_CreateNew_Done(59),
    ExpeditionNoti_Join_Done(60),
    ExpeditionNoti_You_Joined(61),
    ExpeditionNoti_You_Joined2(62),
    ExpeditionNoti_Join_Fail(63),
    ExpeditionNoti_Withdraw_Done(64),
    ExpeditionNoti_You_Withdrew(65),
    ExpeditionNoti_Kick_Done(66),
    ExpeditionNoti_You_Kicked(67),
    ExpeditionNoti_Removed(68),
    ExpeditionNoti_MasterChanged(69),
    ExpeditionNoti_Modified(70),
    ExpeditionNoti_Modified2(71),
    ExpeditionNoti_Invite(72),
    ExpeditionNoti_ResponseInvite(73),
    AdverNoti_LoadDone(74),
    AdverNoti_Change(75),
    AdverNoti_Remove(76),
    AdverNoti_GetAll(77),
    AdverNoti_Apply(78),
    AdverNoti_ResultApply(79),
    AdverNoti_AddFail(80),
    AdverReq_Add(81),
    AdverReq_Remove(82),
    AdverReq_GetAll(83),
    AdverReq_RemoveUserFromNotiList(84),
    AdverReq_Apply(85),
    AdverReq_ResultApply(86),
    UNKNOWN;

    private int value;

    OpsParty(int val) {
        this.value = val;
    }

    OpsParty() {
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

    public static OpsParty find(int val) {
        for (OpsParty ops : values()) {
            if (ops.get() == val) {
                if (val != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsParty ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        if (Version.PostBB()) {
            return;
        }
        clear();
        if (Version.GreaterOrEqual(Region.JMS, 147)) {
            PartyReq_LoadParty.set(0);
            PartyReq_CreateNewParty.set(1);
            PartyReq_WithdrawParty.set(2);
            PartyReq_JoinParty.set(3);
            PartyReq_InviteParty.set(4);
            PartyReq_KickParty.set(5);
            PartyReq_ChangePartyBoss.set(6);
            PartyRes_LoadParty_Done.set(7);
            PartyRes_CreateNewParty_Done.set(8);
            PartyRes_CreateNewParty_AlreayJoined.set(9);
            PartyRes_CreateNewParty_Beginner.set(10);
            PartyRes_CreateNewParty_Unknown.set(11);
            PartyRes_WithdrawParty_Done.set(12);
            PartyRes_WithdrawParty_NotJoined.set(13);
            PartyRes_WithdrawParty_Unknown.set(14);
            PartyRes_JoinParty_Done.set(15);
            PartyRes_JoinParty_AlreadyJoined.set(16);
            PartyRes_JoinParty_AlreadyFull.set(17);
            PartyRes_JoinParty_OverDesiredSize.set(18);
            PartyRes_JoinParty_UnknownUser.set(19);
            PartyRes_InviteParty_Sent.set(20);
            PartyRes_InviteParty_BlockedUser.set(21);
            PartyRes_InviteParty_AlreadyInvited.set(22);
            PartyRes_InviteParty_Rejected.set(23);
            PartyRes_InviteParty_Accepted.set(24);
            PartyRes_KickParty_Done.set(25);
            PartyRes_ChangePartyBoss_Done.set(26);
            PartyRes_ChangePartyBoss_NotSameField.set(27);
            PartyRes_ChangePartyBoss_NoMemberInSameField.set(28);
            PartyRes_ChangePartyBoss_NotSameChannel.set(29);
            PartyRes_ChangePartyBoss_Unknown.set(30);
            PartyRes_AdminCannotCreate.set(31);
            PartyRes_AdminCannotInvite.set(32);
            PartyRes_UserMigration.set(33);
            PartyRes_ChangeLevelOrJob.set(34);
            PartyInfo_TownPortalChanged.set(35);
            return;
        }
    }
}
