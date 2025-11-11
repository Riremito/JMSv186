/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.server;

import odin.handling.channel.PlayerStorage;

// TODO : remove
public class ServerOdinCashShop {

    private static Server_CashShop server_cs = null;

    public static void set(Server_CashShop server) {
        server_cs = server;
    }

    public static final PlayerStorage getPlayerStorage() {
        return server_cs.getPlayerStorage();
    }

    public static final PlayerStorage getPlayerStorageMTS() {
        return server_cs.getPlayerStorageMTS();
    }

    public static boolean isShutdown() {
        return server_cs.isShutdown();
    }
}
