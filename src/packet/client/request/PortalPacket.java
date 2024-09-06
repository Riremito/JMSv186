// ポータル
package packet.client.request;

import client.MapleCharacter;
import client.MapleClient;
import config.ServerConfig;
import handling.channel.handler.PlayerHandler;
import packet.client.ClientPacket;
import server.maps.MapleMap;

public class PortalPacket {

    // CP_UserTransferFieldRequest, CP_UserPortalScriptRequest, CP_UserPortalTeleportRequest
    public static boolean OnPacket(ClientPacket cp, ClientPacket.Header header, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        switch (header) {
            // ポータル or /map
            case CP_UserTransferFieldRequest: {
                if (ServerConfig.IsKMS()) {
                    short unk1 = cp.Decode2();
                    int unk2 = cp.Decode4();
                    byte portal_count = cp.Decode1();
                    int map_id = cp.Decode4();
                    String portal_name = cp.DecodeStr();
                    if (!portal_name.equals("")) {
                        short x = cp.Decode2();
                        short y = cp.Decode2();
                    }
                    byte unk4 = cp.Decode1();
                    byte unk5 = cp.Decode1();

                    if (!chr.isAlive() && map_id == 0) {
                        chr.getStat().setHp((short) 50);
                        final MapleMap to = chr.getMap().getReturnMap();
                        chr.changeMap(to, to.getPortal(0));
                    } else if (map_id != -1) {
                        PlayerHandler.ChangeMap(c, map_id);
                    } else {
                        PlayerHandler.ChangeMap(c, portal_name);
                    }
                    return true;
                } else {
                    byte portal_count = cp.Decode1();
                    int map_id = cp.Decode4();
                    // 復活
                    if (!chr.isAlive() && map_id == 0) {
                        String portal_name = cp.DecodeStr();
                        if (portal_name.equals("")) {
                            byte unk = cp.Decode1();
                            byte revive_type = cp.Decode1();

                            final MapleMap to = (revive_type > 0) ? chr.getMap() : chr.getMap().getReturnMap();
                            chr.changeMap(to, to.getPortal(0));
                            chr.getStat().setHp(chr.getStat().getMaxHp());
                            chr.UpdateStat(true);
                            return true;
                        }
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
