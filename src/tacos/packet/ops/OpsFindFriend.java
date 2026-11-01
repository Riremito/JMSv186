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
public enum OpsFindFriend implements IPacketOps {

    FindFriend_MyInfoRequest(5),
    FindFriend_MyInfoResult(6),
    FindFriend_SearchRequest(7),
    FindFriend_SearchResult(8),
    FindFriend_SearchResult_Error(9),
    FindFriend_DetailRequest(10),
    FindFriend_DetailResult(11),
    FindFriend_ErrorCode_OverflowQueue(12),
    UNKNOWN;

    private int value;

    OpsFindFriend(int val) {
        this.value = val;
    }

    OpsFindFriend() {
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
}
