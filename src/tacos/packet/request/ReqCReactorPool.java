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

import odin.client.MapleCharacter;
import odin.server.maps.MapleMap;
import tacos.client.TacosClient;
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
        MapleCharacter chr = client.getPlayer();
        if (chr == null) {
            return false;
        }
        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        int object_id = cp.Decode4(); // dwID

        MapleReactor reactor = map.getReactorByOid(object_id);
        if (reactor == null) {
            return false;
        }

        switch (header) {
            case CP_ReactorHit: {
                OnReactorHit(client, reactor, cp);
                return true;
            }
            case CP_ReactorTouch: {
                OnReactorTouch(client, reactor, cp);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // CReactorPool::FindHitReactor, 0
    // CReactorPool::FindSkillReactor, 1
    public static boolean OnReactorHit(TacosClient client, MapleReactor reactor, ClientPacket cp) {
        int type = Config.PostBB() ? cp.Decode4() : 0;
        int dwHitOption = cp.Decode4(); // dwHitOption, dwOption
        short tDelay = cp.Decode2(); // tDelay, tActionDelay
        int nSkillID = Config.PostBB() ? cp.Decode4() : 0;

        reactor.hitReactor(dwHitOption, tDelay, client);
        return true;
    }

    // CReactorPool::FindTouchReactorAroundLocalUser
    public static boolean OnReactorTouch(TacosClient client, MapleReactor reactor, ClientPacket cp) {
        boolean is_in_rect = cp.Decode1() != 0;

        if (is_in_rect) {
            TacosScriptReactor.getInstance().act(client, reactor);
        }

        return true;
    }
}
