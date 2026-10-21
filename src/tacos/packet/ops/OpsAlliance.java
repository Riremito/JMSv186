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

/**
 *
 * @author Riremito
 */
public enum OpsAlliance implements IPacketOps {
    // GMS v95, same values in JMS v186.
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
}
