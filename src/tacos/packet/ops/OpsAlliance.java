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
public enum OpsAlliance implements IPacketOps {
    // GMS v95
    AllianceReq_Create(0),
    AllianceReq_Load(1),
    AllianceReq_Withdraw(2),
    AllianceReq_Invite(3),
    AllianceReq_Join(4),
    AllianceReq_UpdateMemberCountMax(5),
    AllianceReq_Kick(6),
    AllianceReq_ChangeMaster(7),
    AllianceReq_SetGradeName(8),
    AllianceReq_ChangeGrade(9),
    AllianceReq_SetNotice(10),
    AllianceReq_Destroy(11),
    AllianceRes_LoadDone(12),
    AllianceRes_LoadGuildDone(13),
    AllianceRes_NotifyLoginOrLogout(14),
    AllianceRes_CreateDone(15),
    AllianceRes_Withdraw_Done(16),
    AllianceRes_Withdraw_Failed(17),
    AllianceRes_Invite_Done(18),
    AllianceRes_Invite_Failed(19),
    AllianceRes_InviteGuild_BlockedByOpt(20),
    AllianceRes_InviteGuild_AlreadyInvited(21),
    AllianceRes_InviteGuild_Rejected(22),
    AllianceRes_UpdateAllianceInfo(23),
    AllianceRes_ChangeLevelOrJob(24),
    AllianceRes_ChangeMaster_Done(25),
    AllianceRes_SetGradeName_Done(26),
    AllianceRes_ChangeGrade_Done(27),
    AllianceRes_SetNotice_Done(28),
    AllianceRes_Destroy_Done(29),
    AllianceRes_UpdateGuildInfo(30),
    UNKNOWN;

    private int value;

    OpsAlliance(int val) {
        this.value = val;
    }

    OpsAlliance() {
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

    public static OpsAlliance find(int val) {
        for (OpsAlliance ops : values()) {
            if (ops.get() == val && val != UNKNOWN.get()) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsAlliance ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        // JMS v164 - v187 : one more request after Destroy, every result is one higher than GMS v95 (CWvsContext::OnAllianceResult).
        if (Config.Between(Region.JMS, 164, 187)) {
            clear();
            AllianceReq_Create.set(0);
            AllianceReq_Load.set(1);
            AllianceReq_Withdraw.set(2);
            AllianceReq_Invite.set(3);
            AllianceReq_Join.set(4);
            AllianceReq_UpdateMemberCountMax.set(5);
            AllianceReq_Kick.set(6);
            AllianceReq_ChangeMaster.set(7);
            AllianceReq_SetGradeName.set(8);
            AllianceReq_ChangeGrade.set(9);
            AllianceReq_SetNotice.set(10);
            AllianceReq_Destroy.set(11);
            AllianceRes_LoadDone.set(13);
            AllianceRes_LoadGuildDone.set(14);
            AllianceRes_NotifyLoginOrLogout.set(15);
            AllianceRes_CreateDone.set(16);
            AllianceRes_Withdraw_Done.set(17);
            AllianceRes_Withdraw_Failed.set(18);
            AllianceRes_Invite_Done.set(19);
            AllianceRes_Invite_Failed.set(20);
            AllianceRes_InviteGuild_BlockedByOpt.set(21);
            AllianceRes_InviteGuild_AlreadyInvited.set(22);
            AllianceRes_InviteGuild_Rejected.set(23);
            AllianceRes_UpdateAllianceInfo.set(24);
            AllianceRes_ChangeLevelOrJob.set(25);
            AllianceRes_ChangeMaster_Done.set(26);
            AllianceRes_SetGradeName_Done.set(27);
            AllianceRes_ChangeGrade_Done.set(28);
            AllianceRes_SetNotice_Done.set(29);
            AllianceRes_Destroy_Done.set(30);
            AllianceRes_UpdateGuildInfo.set(31);
            return;
        }
    }
}
