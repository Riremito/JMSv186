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

import java.util.ArrayList;
import lombok.Builder;
import tacos.server.map.object.TacosPet;

/**
 *
 * @author Riremito
 */
@Builder
public class PB_ScriptMessage {

    // common
    public int npc_id; // nSpeakerTemplateID
    @Builder.Default
    public byte param = 0;
    @Builder.Default
    public String text = "";
    // SM_SAY
    @Builder.Default
    public boolean prev = false;
    @Builder.Default
    public boolean next = false;
    // SM_ASKTEXT
    @Builder.Default
    public String text_default = "";
    @Builder.Default
    public int length_min = 0;
    @Builder.Default
    public int length_max = 0;
    // SM_ASKNUMBER
    @Builder.Default
    public int number_default = 0;
    @Builder.Default
    public int number_min = 0;
    @Builder.Default
    public int number_max = 0;
    // SM_ASKAVATAR
    public ArrayList<Integer> ids;
    // SM_ASKPET
    public ArrayList<TacosPet> pets;
}
