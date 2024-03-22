// 設置物
package packet.client.request;

import client.MapleClient;
import handling.MaplePacket;
import packet.client.ClientPacket;
import packet.server.ServerPacket;
import scripting.ReactorScriptManager;
import server.maps.MapleReactor;

public class ReactorPacket {

    public static boolean OnPacket(ClientPacket p, ClientPacket.Header header, MapleClient c) {
        int oid = p.Decode4();
        MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(oid);

        // 存在しない設置物
        if (reactor == null) {
            return false;
        }

        // 既に起動済み
        if (!reactor.isAlive()) {
            return false;
        }

        switch (header) {
            // 攻撃
            case CP_ReactorHit: {
                // HitReactor
                int charPos = p.Decode4();
                short stance = p.Decode2();

                reactor.hitReactor(charPos, stance, c);
                return true;
            }
            // 触れる
            case CP_ReactorTouch: {
                // TouchReactor
                byte touched = p.Decode1();

                if (touched == 0) {
                    return false;
                }

                // 不明
                if (reactor.getReactorId() < 6109013 || reactor.getReactorId() > 6109027) {

                    return false;
                }

                ReactorScriptManager.getInstance().act(c, reactor);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // spawnReactor
    public static MaplePacket Spawn(MapleReactor reactor) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_ReactorEnterField);

        p.Encode4(reactor.getObjectId());
        p.Encode4(reactor.getReactorId());
        p.Encode1(reactor.getState());
        p.Encode2(reactor.getPosition().x);
        p.Encode2(reactor.getPosition().y);
        p.Encode1(reactor.getFacingDirection()); // stance
        p.EncodeStr(reactor.getName());

        return p.Get();
    }

    // triggerReactor
    public static MaplePacket Hit(MapleReactor reactor, int stance) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_ReactorChangeState);

        p.Encode4(reactor.getObjectId());
        p.Encode1(reactor.getState());
        p.Encode2(reactor.getPosition().x);
        p.Encode2(reactor.getPosition().y);
        p.Encode2(stance);
        p.Encode1(0);
        p.Encode1(4);

        return p.Get();
    }

    // destroyReactor
    public static MaplePacket Destroy(MapleReactor reactor) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_ReactorLeaveField);

        p.Encode4(reactor.getObjectId());
        p.Encode1(reactor.getState());
        p.Encode2(reactor.getPosition().x);
        p.Encode2(reactor.getPosition().y);

        return p.Get();
    }
}
