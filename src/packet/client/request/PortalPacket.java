// ポータル
package packet.client.request;

import client.MapleCharacter;
import client.MapleClient;
import packet.client.ClientPacket;
import server.maps.MapleMap;

public class PortalPacket {

    // CP_UserTransferFieldRequest, CP_UserPortalScriptRequest, CP_UserPortalTeleportRequest
    public static boolean OnPacket(ClientPacket p, ClientPacket.Header header, MapleClient c) {

        switch (header) {
            // ポータル or /map
            case CP_UserTransferFieldRequest: {
                byte portal_count = p.Decode1();
                int map_id = p.Decode4();

                MapleCharacter chr = c.getPlayer();
                // 復活
                if (!chr.isAlive() && map_id == 0) {
                    String portal_name = p.DecodeStr();
                    if (portal_name.equals("")) {
                        byte unk = p.Decode1();
                        byte revive_type = p.Decode1();

                        final MapleMap to = (revive_type > 0) ? chr.getMap() : chr.getMap().getReturnMap();
                        chr.changeMap(to, to.getPortal(0));
                        chr.getStat().setHp(chr.getStat().getMaxHp());
                        chr.UpdateStat(true);
                        return true;
                    }
                }

                return false;
            }
            // スクリプトポータル
            case CP_UserPortalScriptRequest: {
                return true;
            }
            // スクリプト
            case CP_UserPortalTeleportRequest: {
                return true;
            }
            default: {
                break;
            }

        }

        return false;
    }
}
