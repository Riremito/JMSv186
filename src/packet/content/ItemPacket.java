/*
 * Copyright (C) 2023 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package packet.content;

import client.MapleClient;
import handling.channel.ChannelServer;
import packet.ClientPacket;
import packet.ServerPacket;
import server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class ItemPacket {

    public static boolean OnPacket(ClientPacket p, ClientPacket.Header header, MapleClient c) {

        switch (header) {
            case CP_UserConsumeCashItemUseRequest: {
                return true;
            }
            case CP_UserStatChangeItemUseRequest: {
                return true;
            }
            case CP_UserItemReleaseRequest: {
                return true;
            }
            case CP_UserScriptItemUseRequest: {
                return true;
            }
            case CP_UserPortalScrollUseRequest: {
                return true;
            }
            case CP_UserUpgradeItemUseRequest: {
                return true;
            }
            case CP_UserItemOptionUpgradeItemUseRequest: {
                return true;
            }
            case CP_UserHyperUpgradeItemUseRequest: {
                return true;
            }
            case CP_UserMobSummonItemUseRequest: {
                return true;
            }
            case USE_TREASUER_CHEST: {
                return true;
            }
            case CP_UserSkillLearnItemUseRequest: {
                return true;
            }
            case CP_UserBridleItemUseRequest: {
                return true;
            }
            case CP_UserTamingMobFoodItemUseRequest: {
                return true;
            }
            case CP_UserUseGachaponBoxRequest: {
                return true;
            }
            case CP_ShopLinkRequest: {
                return true;
            }
            case CP_UserShopScannerItemUseRequest: {
                return true;
            }
            case CP_UserSkillResetItemUseRequest: {
                // v194
                p.Decode4(); // 2114524514, 00A67BE0
                short item_slot = p.Decode2(); // 60, 00A67BEE
                int item_id = p.Decode4(); // 2500000, 00A67BFC
                return true;
            }
            case CP_JMS_MONSTERBOOK_SET: {
                // v194
                p.Decode4(); // 2114843894, 00A60ACB
                int item_slot = p.Decode4(); // 64, 00A60AD9 4 bytes
                int song_time = p.Decode4(); // 2560000, 00A60AE3
                return true;
            }
            case CP_JMS_JUKEBOX: {
                // v194
                p.Decode4(); // 2113673714, 00A70E25
                short item_slot = p.Decode2(); // 43, 00A70E36
                int item_id = p.Decode4(); // 2150001, 00A70E40
                int song_time = p.Decode4(); // 113788, 00A70E4A
                c.getPlayer().UpdateStat(true);
                return true;
            }

            case CP_JMS_PINKBEAN_PORTAL_ENTER: {
                int portal_id = p.Decode4();
                byte flag = p.Decode1();

                MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(749050200);
                c.getPlayer().changeMap(to, to.getPortal(0));
                return true;
            }
            case CP_JMS_PINKBEAN_PORTAL_CREATE: {
                // v194
                p.Decode4(); // -2145728229, 00A6618A
                short item_slot = p.Decode2(); // 50, 00A66198
                int item_id = p.Decode4(); // 2420004, 00A661A6
                short x = p.Decode2(); // -1776, 00A661C1
                short y = p.Decode2(); // 213, 00A661DD

                ServerPacket ip = new ServerPacket(ServerPacket.Header.LP_JMS_PINKBEAN_PORTAL_CREATE);
                ip.Encode1(1);
                ip.Encode4(item_id);
                ip.Encode4(1234); // portal id
                ip.Encode2(x);
                ip.Encode2(y);
                ip.Encode4(5555);
                ip.Encode4(5666);
                ip.Encode4(0);
                ip.Encode4(0);
                c.SendPacket(ip.Get());
                c.getPlayer().UpdateStat(true);
                return true;
            }
            default: {
                break;
            }
        }

        c.getPlayer().UpdateStat(true);
        return false;
    }
}
