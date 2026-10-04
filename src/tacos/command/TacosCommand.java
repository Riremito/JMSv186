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
package tacos.command;

import odin.client.MapleCharacter;

/**
 *
 * @author Riremito
 */
public class TacosCommand {

    public static boolean executeCommand(MapleCharacter chr, String message) {
        TacosCommander dcmd = new TacosCommander(message);

        if (!dcmd.checkPrefix()) {
            return false;
        }
        if (TacosCommandAdmin.executeCommand(dcmd, chr)) {
            return true;
        }
        if (TacosCommandDebug.executeCommand(dcmd, chr)) {
            return true;
        }
        if (TacosCommandTest.executeCommand(dcmd, chr)) {
            return true;
        }
        if (TacosCommandInfo.executeCommand(dcmd, chr)) {
            return true;
        }
        if (TacosCommandPlayer.executeCommand(dcmd, chr)) {
            return true;
        }
        if (TacosCommandCustom.executeCommand(dcmd, chr)) {
            return true;
        }

        return true;
    }
}
