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
package tacos.server;

import tacos.config.property.Property_Dummy_World;
import tacos.config.property.Property_World;
import java.util.HashMap;
import java.util.Map;

// TODO : remove
public class ServerOdinLogin {

    private static Server_Login server_login = null;

    public static void set(Server_Login server) {
        server_login = server;
    }

    public static final boolean isAdminOnly() {
        return server_login.isAdminOnly();
    }

    public static final boolean isShutdown() {
        return server_login.isShutdown();
    }

    private static Map<Integer, Integer> load = new HashMap<Integer, Integer>();
    private static int usersOn = 0;

    private static final int WolrdLimit = 20;
    public static int NumberOfWorld = 0;
    public static final int WorldPort[] = new int[WolrdLimit];
    public static final String WorldEvent[] = new String[WolrdLimit];
    public static final String WorldMessage[] = new String[WolrdLimit];
    public static final String WorldName[] = new String[WolrdLimit];
    public static final int WorldFlag[] = new int[WolrdLimit];
    public static final int WorldChannels[] = new int[WolrdLimit];
    public static final int WorldLanguages[] = new int[WolrdLimit];

    public static final void SetWorldConfig() {
        // Kaede
        WorldChannels[NumberOfWorld] = Property_World.getChannels();
        WorldLanguages[NumberOfWorld] = Property_World.getLanguages();
        WorldPort[NumberOfWorld] = Property_World.getPort();
        WorldName[NumberOfWorld] = Property_World.getName();
        WorldEvent[NumberOfWorld] = Property_World.getEvent();
        WorldMessage[NumberOfWorld] = Property_World.getMessage();
        WorldFlag[NumberOfWorld] = Property_World.getFlags();
        NumberOfWorld++;
        // Momiji, not used
        WorldChannels[NumberOfWorld] = Property_Dummy_World.getChannels();
        WorldLanguages[NumberOfWorld] = Property_Dummy_World.getLanguages();
        WorldPort[NumberOfWorld] = Property_Dummy_World.getPort();
        WorldName[NumberOfWorld] = Property_Dummy_World.getName();
        WorldEvent[NumberOfWorld] = Property_Dummy_World.getEvent();
        WorldMessage[NumberOfWorld] = Property_Dummy_World.getMessage();
        WorldFlag[NumberOfWorld] = Property_Dummy_World.getFlags();
        NumberOfWorld++;
    }

    public static final void addChannel(final int channel) {
        load.put(channel, 0);
    }

    public static final void removeChannel(final int channel) {
        load.remove(channel);
    }

    public static void setLoad(final Map<Integer, Integer> load_, final int usersOn_) {
        load = load_;
        usersOn = usersOn_;
    }

    public static final int getUsersOn() {
        return usersOn;
    }

}
