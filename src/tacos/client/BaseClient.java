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
package tacos.client;

import org.apache.mina.common.IoSession;
import tacos.network.MaplePacket;

/**
 *
 * @author Riremito
 */
public class BaseClient {

    private final IoSession session;

    public BaseClient(IoSession session) {
        this.session = session;
    }

    public IoSession getSession() {
        return this.session;
    }

    // TODO : fix class to byte[]
    public void SendPacket(MaplePacket packet) {
        this.session.write(packet);
    }

    public String getSessionIPAddress() {
        return this.session.getRemoteAddress().toString().split(":")[0];
    }

}
