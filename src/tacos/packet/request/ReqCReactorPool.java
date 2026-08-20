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
package tacos.packet.request;

import tacos.client.TacosClient;
import tacos.config.Region;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import odin.server.maps.MapleReactor;
import tacos.config.Config;
import tacos.packet.ClientPacketHeader;
import tacos.script.TacosScriptReactor;

/**
 *
 * @author Riremito
 */
public class ReqCReactorPool {

    public static boolean OnPacket(TacosClient client, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_ReactorHit: {
                int oid = cp.Decode4();
                MapleReactor reactor = client.getPlayer().getMap().getReactorByOid(oid);
                if (reactor == null || !reactor.isAlive()) {
                    DebugLogger.ErrorLog("ReactorHit");
                    return true;
                }

                if (Config.GreaterOrEqual(Region.JMS, 302)) {
                    int unk = cp.Decode4();
                }
                int charPos = cp.Decode4();
                short stance = cp.Decode2();

                reactor.hitReactor(charPos, stance, client);
                return true;
            }
            case CP_ReactorTouch: {
                int oid = cp.Decode4();

                MapleReactor reactor = client.getPlayer().getMap().getReactorByOid(oid);
                if (reactor == null || !reactor.isAlive()) {
                    DebugLogger.ErrorLog("ReactorTouch");
                    return true;
                }

                byte touched = cp.Decode1();

                if (touched == 0) {
                    return false;
                }

                // 不明
                if (reactor.getReactorId() < 6109013 || reactor.getReactorId() > 6109027) {

                    return false;
                }

                TacosScriptReactor.getInstance().act(client, reactor);
                return true;
            }
            case CP_RequireFieldObstacleStatus: {
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }
}
