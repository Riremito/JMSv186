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
package tacos.packet.ops.arg;

import odin.client.MapleCharacter;
import tacos.packet.ops.OpsFriend;

/**
 *
 * @author Riremito
 */
public class ArgFriend {

    public OpsFriend flag;
    public int nFriendMax;
    public MapleCharacter chr;
    public int friend_id;
    public int friend_channel;
    public int friend_level;
    public int friend_job;
    public String friend_name;
    public String friend_tag;
}
