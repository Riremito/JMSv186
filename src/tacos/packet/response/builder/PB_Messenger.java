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
package tacos.packet.response.builder;

import lombok.Builder;
import tacos.client.TacosCharacter;

/**
 *
 * @author Riremito
 */
@Builder
public class PB_Messenger {

    public int player_index;
    public TacosCharacter player;
    public boolean is_new;
    public String inviter_name;
    public int inviter_channel_id;
    public int messenger_id;
    public String invitee_name;
    public boolean is_found;
    public boolean is_auto_blocked;
    public String message;
}
