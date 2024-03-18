/*
 * Copyright (C) 2024 Riremito
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
package packet.client.request;

import client.MapleCharacter;
import client.MapleClient;
import handling.MaplePacket;
import packet.client.ClientPacket;
import packet.server.ServerPacket;
import server.maps.MapleDynamicPortal;

/**
 *
 * @author Riremito
 */
public class ItemRequest {
    
    public static boolean OnPacket(ClientPacket p, ClientPacket.Header header, MapleClient c) {
        switch (header) {
            case CP_UserConsumeCashItemUseRequest: {
                return true;
            }
            case CP_UserDestroyPetItemRequest: {
                // 期限切れデンデン
                c.getPlayer().UpdateStat(true);
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
                //MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(749050200);
                //c.getPlayer().changeMap(to, to.getPortal(0));
                MapleCharacter chr = c.getPlayer();
                MapleDynamicPortal dynamic_portal = chr.getMap().findDynamicPortal(portal_id);
                if (dynamic_portal == null) {
                    c.getPlayer().UpdateStat(true);
                    return true;
                }
                dynamic_portal.warp(chr);
                return true;
            }
            case CP_JMS_PINKBEAN_PORTAL_CREATE: {
                // v194
                p.Decode4(); // -2145728229, 00A6618A
                short item_slot = p.Decode2(); // 50, 00A66198
                int item_id = p.Decode4(); // 2420004, 00A661A6
                short x = p.Decode2(); // -1776, 00A661C1
                short y = p.Decode2(); // 213, 00A661DD
                MapleDynamicPortal dynamic_portal = new MapleDynamicPortal(item_id, 749050200, x, y);
                c.getPlayer().getMap().addMapObject(dynamic_portal);
                c.getPlayer().getMap().broadcastMessage(CreatePinkBeanEventPortal(dynamic_portal));
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
    
    public static MaplePacket CreatePinkBeanEventPortal(MapleDynamicPortal dynamic_portal) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_PINKBEAN_PORTAL_CREATE);
        sp.Encode1(1);
        sp.Encode4(dynamic_portal.getItemID()); // item id
        sp.Encode4(dynamic_portal.getObjectId()); // object id
        sp.Encode2(dynamic_portal.getPosition().x);
        sp.Encode2(dynamic_portal.getPosition().y);
        sp.Encode4(0);
        sp.Encode4(0);
        sp.Encode2(dynamic_portal.getPosition().x);
        sp.Encode2(dynamic_portal.getPosition().y);
        return sp.Get();
    }
    
}
