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
package tacos.packet.response;

import tacos.client.TacosClient;
import tacos.config.Region;
import odin.constants.GameConstants;
import tacos.shared.SharedDate;
import tacos.debug.DebugShop;
import java.util.List;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsShop;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleShopItem;
import tacos.config.Config;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCShopDlg {

    // CShopDlg::OnPacket
    public static ServerPacket ShopResult(OpsShop ops, int level) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ShopResult);

        sp.Encode1(ops.get());

        switch (ops) {
            case ShopRes_SellSuccess: {
                /*
                if (Version.GreaterOrEqual(Region.JMS, 302)) {
                    sp.Encode1(0);
                    // CShopDlg::SetShopDlg
                    sp.Encode4(0);
                    sp.Encode4(9030000); // m_dwNpcTemplateID
                    sp.Encode1(0); // 1 = 十字旅団
                    //sp.Encode1(0);
                    sp.Encode2(0);
                }
                 */
                break;
            }
            case ShopRes_LimitLevel_Less:
            case ShopRes_LimitLevel_More: {
                sp.Encode4(level);
                break;
            }
            default: {
                break;
            }
        }

        if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.GMS, 131)) {
            sp.Encode1(0);
        }
        if (Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.GMS, 131)) {
            sp.Encode1(0);
        }

        return sp;
    }

    public static ServerPacket ShopResult(OpsShop ops) {
        return ResCShopDlg.ShopResult(ops, 0);
    }

    // CShopDlg::OnPacket
    public static ServerPacket OpenShopDlg_DS(DebugShop ds) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_OpenShopDlg);

        if (Config.GreaterOrEqual(Region.EMS, 89)) {
            // none
        } else if (Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 76)) {
            sp.Encode1(0);
        }

        if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.GMS, 131)) {
            sp.Encode4(0);
        }

        sp.Encode4(ds.getNpcId()); // m_dwNpcTemplateID

        if (Config.GreaterOrEqual(Region.EMS, 89)) {
            // none
        } else if (Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 76) || Config.GreaterOrEqual(Region.GMS, 131)) {
            sp.Encode1(0);
        }

        sp.Encode2(ds.getShopStocks().size()); // nCount

        for (DebugShop.ShopStock ss : ds.getShopStocks()) {
            sp.Encode4(ss.item_id); // nItemID
            sp.Encode4(ss.item_price); // nPrice

            if (Config.GreaterOrEqual(Region.GMS, 91)) {
                sp.Encode1(0); // nDiscountRate
            }

            if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 92) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 83) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
                sp.Encode4(0); // nTokenItemID
                sp.Encode4(0); // nTokenPrice
            }

            if (Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
                sp.Encode4(0); // nItemPeriod
            }

            if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 84) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 83) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
                sp.Encode4(0); // nLevelLimited
            }

            if (Config.GreaterOrEqual(Region.JMS, 302)) {
                sp.Encode4(0);
                sp.Encode1(0);
                sp.Encode4(0);
            }
            if (Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode4(0);
                sp.Encode8(0);
                sp.Encode8(SharedDate.getMagicalExpirationDate());
                sp.Encode4(0);
                sp.Encode1(0);
                sp.Encode4(0);
            }
            // 207 || 233
            if (GameConstants.isRechargable(ss.item_id)) {
                // dUnitPrice (8)
                sp.EncodeDouble((ss.item_price != 0) ? 0.0 : (double) ss.item_recharge_price);
                // nQuantity is unitialized if you put recharge item in first shop slot,
                // you need to put other item in first slot to initialize quantity value.
            } else {
                sp.Encode2(ss.item_quantity); // nQuantity
            }
            if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 47) || Config.GreaterOrEqual(Region.JMS, 146) || Config.GreaterOrEqual(Region.CMS, 62) || Config.GreaterOrEqual(Region.TWMS, 73) || Config.GreaterOrEqual(Region.THMS, 0) || Config.GreaterOrEqual(Region.GMS, 61) || Config.GreaterOrEqual(Region.MSEA, 0) || Config.GreaterOrEqual(Region.EMS, 0) || Config.GreaterOrEqual(Region.BMS, 24) || Config.GreaterOrEqual(Region.VMS, 35)) {
                sp.Encode2(ss.item_slot_max); // nMaxPerSlot
            }

            if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode1(0);
            }
            if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode4(0);
            }
            if (Config.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
                sp.EncodeZeroBytes(32);
            }
        }
        return sp;
    }

    public static ServerPacket OpenShopDlg(TacosClient client, int sid, List<MapleShopItem> items) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_OpenShopDlg);

        if (!Config.GreaterOrEqual(Region.EMS, 89)) {
            // none
        } else if (Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 76)) {
            sp.Encode1(0);
        }

        if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.GMS, 131)) {
            sp.Encode4(0);
        }

        sp.Encode4(sid); // m_dwNpcTemplateID

        if (!Config.GreaterOrEqual(Region.EMS, 89)) {
            // none
        } else if (Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 76) || Config.GreaterOrEqual(Region.GMS, 131)) {
            sp.Encode1(0);
        }

        sp.Encode2(items.size()); // item count
        for (MapleShopItem item : items) {
            sp.Encode4(item.getItemId());
            sp.Encode4(item.getPrice());

            if (Config.GreaterOrEqual(Region.GMS, 91)) {
                sp.Encode1(0); // nDiscountRate
            }

            if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 92) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 83) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
                sp.Encode4(item.getReqItem()); // nTokenItemID
                sp.Encode4(item.getReqItemQ()); // nTokenPrice
            }

            if (Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
                sp.Encode4(0); // nItemPeriod
            }

            if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 84) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 83) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
                sp.Encode4(0); // nLevelLimited
            }

            if (Config.GreaterOrEqual(Region.JMS, 302)) {
                sp.Encode4(0);
                sp.Encode1(0);
                sp.Encode4(0);
            }

            if (Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode4(0);
                sp.Encode8(0);
                sp.Encode8(SharedDate.getMagicalExpirationDate());
                sp.Encode4(0);
                sp.Encode1(0);
                sp.Encode4(0);
            }

            // 207 || 233
            if (GameConstants.isRechargable(item.getItemId())) {
                sp.EncodeDouble(ii.getPrice(item.getItemId()));
            } else {
                sp.Encode2(1); // nQuantity
            }
            if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 47) || Config.GreaterOrEqual(Region.JMS, 146) || Config.GreaterOrEqual(Region.CMS, 62) || Config.GreaterOrEqual(Region.TWMS, 73) || Config.GreaterOrEqual(Region.THMS, 0) || Config.GreaterOrEqual(Region.GMS, 61) || Config.GreaterOrEqual(Region.MSEA, 0) || Config.GreaterOrEqual(Region.EMS, 0) || Config.GreaterOrEqual(Region.BMS, 24) || Config.GreaterOrEqual(Region.VMS, 35)) {
                sp.Encode2(ii.getSlotMax(client, item.getItemId())); // nMaxPerSlot
            }
            if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode1(0);
            }
            if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode4(0);
            }
            if (Config.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
                sp.EncodeZeroBytes(32);
            }
        }
        return sp;
    }
}
