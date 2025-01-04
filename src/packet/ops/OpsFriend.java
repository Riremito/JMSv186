/*
 * Copyright (C) 2024 Riremito
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
package packet.ops;

/**
 *
 * @author Riremito
 */
public enum OpsFriend {
    // GMS v95
    FriendReq_LoadFriend(0x0),
    FriendReq_SetFriend(0x1),
    FriendReq_AcceptFriend(0x2),
    FriendReq_DeleteFriend(0x3),
    FriendReq_NotifyLogin(0x4),
    FriendReq_NotifyLogout(0x5),
    FriendReq_IncMaxCount(0x6),
    FriendRes_LoadFriend_Done(0x7),
    FriendRes_NotifyChange_FriendInfo(0x8),
    FriendRes_Invite(0x9),
    FriendRes_SetFriend_Done(0xA),
    FriendRes_SetFriend_FullMe(0xB),
    FriendRes_SetFriend_FullOther(0xC),
    FriendRes_SetFriend_AlreadySet(0xD),
    FriendRes_SetFriend_Master(0xE),
    FriendRes_SetFriend_UnknownUser(0xF),
    FriendRes_SetFriend_Unknown(0x10),
    FriendRes_AcceptFriend_Unknown(0x11),
    FriendRes_DeleteFriend_Done(0x12),
    FriendRes_DeleteFriend_Unknown(0x13),
    FriendRes_Notify(0x14),
    FriendRes_IncMaxCount_Done(0x15),
    FriendRes_IncMaxCount_Unknown(0x16),
    FriendRes_PleaseWait(0x17),
    UNKNOWN(-1);

    private int value;

    OpsFriend(int flag) {
        value = flag;
    }

    OpsFriend() {
        value = -1;
    }

    public int get() {
        return value;
    }

    public static OpsFriend find(int val) {
        for (final OpsFriend o : OpsFriend.values()) {
            if (o.get() == val) {
                return o;
            }
        }
        return UNKNOWN;
    }
}
