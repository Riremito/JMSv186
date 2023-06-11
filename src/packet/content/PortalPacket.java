// ポータル
package packet.content;

import client.MapleClient;
import packet.ClientPacket;

public class PortalPacket {

    // CP_UserTransferFieldRequest, CP_UserPortalScriptRequest, CP_UserPortalTeleportRequest
    public static boolean OnPacket(ClientPacket p, ClientPacket.Header header, MapleClient c) {

        switch (header) {
            // ポータル or /map
            case CP_UserTransferFieldRequest: {
                return true;
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
