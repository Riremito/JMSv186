/*
 * Copyright (C) 2023 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package debug;

import client.MapleClient;
import packet.request.LoginRequest;

/**
 *
 * @author Riremito
 */
public class DebugAutoLogin {

    // need to send packet when client reached to login screen, if client did not reach to login screen, client does not go to in game
    public static boolean AutoLogin(MapleClient c) {
        Debug.InfoLog("Auto Login Test");
        if (!LoginRequest.login(c, "test1", "testtest")) {
            if (!LoginRequest.login(c, "test2", "testtest")) {
                return true;
            }
        }

        LoginRequest.ServerListRequest(c); // world list
        LoginRequest.ServerStatusRequest(c); // channel status
        LoginRequest.CharlistRequest(c, 0, 1); // select 1st character
        LoginRequest.Character_WithSecondPassword(c); // no characters = stop
        return true;
    }
}
