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

import client.MapleCharacter;
import client.inventory.IItem;
import java.util.List;

/**
 *
 * @author Riremito
 */
public class OpsBroadcastMsgArg {

    public OpsBroadcastMsg bm = OpsBroadcastMsg.UNKNOWN;
    public MapleCharacter chr = null;
    public String message = "";
    public byte ear = 0; // 拡声器系統専用
    public IItem item = null; // アイテム拡声器専用
    public boolean multi_line = false; // 3連拡声器専用
    public List<String> messages = null; // 3連拡声器専用
}
