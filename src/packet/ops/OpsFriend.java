/*
 * Copyright (C) 2025 Riremito
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
public enum OpsFriend implements IPacketOps {
    FriendReq_LoadFriend(0),
    FriendReq_SetFriend(1),
    FriendReq_AcceptFriend(2),
    FriendReq_DeleteFriend(3),
    FriendReq_NotifyLogin(4),
    FriendReq_NotifyLogout(5),
    FriendReq_IncMaxCount(6),
    FriendRes_LoadFriend_Done(7),
    FriendRes_NotifyChange_FriendInfo(8),
    FriendRes_Invite(9),
    FriendRes_SetFriend_Done(10),
    FriendRes_SetFriend_FullMe(11),
    FriendRes_SetFriend_FullOther(12),
    FriendRes_SetFriend_AlreadySet(13),
    FriendRes_SetFriend_Master(14),
    FriendRes_SetFriend_UnknownUser(15),
    FriendRes_SetFriend_Unknown(16),
    FriendRes_AcceptFriend_Unknown(17),
    FriendRes_DeleteFriend_Done(18),
    FriendRes_DeleteFriend_Unknown(19),
    FriendRes_Notify(20),
    FriendRes_IncMaxCount_Done(21),
    FriendRes_IncMaxCount_Unknown(22),
    FriendRes_PleaseWait(23),
    UNKNOWN(-1);

    private int value;

    OpsFriend(int val) {
        this.value = val;
    }

    OpsFriend() {
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

    public static OpsFriend find(int val) {
        for (final OpsFriend o : OpsFriend.values()) {
            if (o.get() == val) {
                return o;
            }
        }
        return UNKNOWN;
    }

    public static void init() {

    }

}
