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
package tacos.wz;

import lombok.Getter;
import lombok.Setter;
import odin.client.MapleCharacter;

/**
 *
 * @author Riremito
 */
@Getter
@Setter
public class WzName {

    private int id = 0;
    private String name = null;
    private String mapName = null;
    private String streetName = null;
    private boolean available = false;

    public void sendDebugMsg(MapleCharacter chr) {
        String text = this.id + " : \"" + this.name + "\"";
        if (this.available) {
            chr.DebugMsg(text);
        } else {
            chr.DebugMsg2(text);
        }
    }

    public void sendDebugMsgItem(MapleCharacter chr) {
        String text = this.id + " : \"" + this.name + "\"";
        if (this.available) {
            chr.DebugMsgItem(text, this.id);
        } else {
            chr.DebugMsg2(text);
        }
    }

    public void sendMapDebugMsg(MapleCharacter chr) {
        String text = this.id + " : \"" + this.mapName + "\" - \"" + this.streetName + "\"";
        if (this.available) {
            chr.DebugMsg(text);
        } else {
            chr.DebugMsg2(text);
        }
    }
}
