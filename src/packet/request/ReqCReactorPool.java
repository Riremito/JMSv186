// 設置物
package packet.request;

import client.MapleClient;
import config.Region;
import config.Version;
import packet.ClientPacket;
import scripting.ReactorScriptManager;
import server.maps.MapleReactor;

public class ReqCReactorPool {

    public static boolean OnPacket(ClientPacket cp, ClientPacket.Header header, MapleClient c) {
        int oid = cp.Decode4();
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
                if (Version.GreaterThanOrEqual(Region.JMS, 302)) {
                    int unk = cp.Decode4();
                }
                int charPos = cp.Decode4();
                short stance = cp.Decode2();

                reactor.hitReactor(charPos, stance, c);
                return true;
            }
            // 触れる
            case CP_ReactorTouch: {
                // TouchReactor
                byte touched = cp.Decode1();

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

}
