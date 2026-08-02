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
package tacos.packet.response;

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import tacos.config.Region;
import tacos.config.Version;
import odin.constants.GameConstants;
import tacos.packet.ServerPacket;
import tacos.packet.response.data.DataCClientOptMan;
import tacos.packet.response.data.DataCWvsContext;
import tacos.packet.response.data.DataCharacterData;
import tacos.packet.ServerPacketHeader;
import tacos.packet.response.data.DataCS_COMMODITY;
import tacos.server.TacosITC;
import tacos.shared.SharedDate;

/**
 *
 * @author Riremito
 */
// CStage::OnPacket
public class ResCStage {

    // CStage::OnSetField
    public static ServerPacket SetField(MapleCharacter chr, boolean bCharacterData) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetField);
        // JMS184orLater
        if (((Region.JMS.check() || Region.JMST.check() || Region.CMS.check() || Region.GMS.check() || Region.GMST.check()) && Version.PostBB() || Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70))
                || Version.GreaterOrEqual(Region.EMS, 89)) {
            sp.EncodeBuffer(DataCClientOptMan.EncodeOpt()); // 2 bytes
        }
        // チャンネル
        sp.Encode4(chr.getClient().getChannelId() - 1); // m_nChannelID
        if (Version.GreaterOrEqual(Region.KMS, 138) || Version.GreaterOrEqual(Region.KMST, 391)
                || ((Region.JMS.check() || Region.JMST.check()) && Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 47) || Version.GreaterOrEqual(Region.JMS, 146) || Version.GreaterOrEqual(Region.CMS, 62) || Version.GreaterOrEqual(Region.TWMS, 73) || Version.GreaterOrEqual(Region.THMS, 0) || Version.GreaterOrEqual(Region.GMS, 61) || Version.GreaterOrEqual(Region.MSEA, 0) || Version.GreaterOrEqual(Region.EMS, 0) || Version.GreaterOrEqual(Region.BMS, 24) || Version.GreaterOrEqual(Region.VMS, 35))
                || Version.GreaterOrEqual(Region.EMS, 89)
                || Version.GreaterOrEqual(Region.TWMS, 148)
                || Version.GreaterOrEqual(Region.CMS, 104)
                || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0);
        }

        if (Version.GreaterOrEqual(Region.EMS, 89)) {
            sp.Encode1(1); // Supreme/Ibara World
        }

        if (((Region.JMS.check() || Region.JMST.check() || Region.TWMS.check() || Region.THMS.check() || Region.CMS.check() || Region.MSEA.check() || Region.EMS.check() || Region.GMS.check() || Region.GMST.check() || Region.IMS.check()) && Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 92) || Version.GreaterOrEqual(Region.JMS, 180) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70))
                || (Version.GreaterOrEqual(Region.KMS, 101) || Version.GreaterOrEqual(Region.KMST, 330))) {
            sp.Encode4(0); // m_dwOldDriverID
        }

        sp.Encode1(chr.getPortalCount()); // sNotifierMessage?
        if (Region.CMS.check()) {
            sp.Encode1(0);
        }
        if (Version.GreaterOrEqual(Region.KMS, 114) || Version.GreaterOrEqual(Region.KMST, 391) || Version.GreaterOrEqual(Region.JMS, 194) || Version.GreaterOrEqual(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 76) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode4(0);
        }
        sp.Encode1(bCharacterData ? 1 : 0); // bCharacterData, 1 = all data, 0 = map change
        if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 47) || Version.GreaterOrEqual(Region.JMS, 146) || Version.GreaterOrEqual(Region.CMS, 62) || Version.GreaterOrEqual(Region.TWMS, 73) || Version.GreaterOrEqual(Region.THMS, 0) || Version.GreaterOrEqual(Region.GMS, 61) || Version.GreaterOrEqual(Region.MSEA, 0) || Version.GreaterOrEqual(Region.EMS, 0) || Version.GreaterOrEqual(Region.BMS, 24) || Version.GreaterOrEqual(Region.VMS, 35)) {
            sp.Encode2(0); // nNotifierCheck
        }
        if (bCharacterData) {
            sp.Encode4(chr.getCalcDamage().m_s1);
            sp.Encode4(chr.getCalcDamage().m_s2);
            sp.Encode4(chr.getCalcDamage().m_s3);
            // キャラクター情報
            if (Version.GreaterOrEqual(Region.GMS, 126)) {
                sp.EncodeBuffer(DataCharacterData.Encode(chr, -1L & ~0x400000000000L));
            } else if (Version.GreaterOrEqual(Region.GMS, 111)) {
                sp.EncodeBuffer(DataCharacterData.Encode(chr, -1L & ~0x200000000L));
            } else {
                sp.EncodeBuffer(DataCharacterData.Encode(chr));
            }
            // JMS184orLater
            if ((Region.JMS.check() || Region.JMST.check() || Region.CMS.check() || Region.TWMS.check() || Region.GMS.check() || Region.GMST.check())
                    && Version.PostBB() || Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70)) {
                // ログアウトギフト
                sp.EncodeBuffer(DataCWvsContext.LogoutGiftConfig());
            }
        } else {
            if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 92) || Version.GreaterOrEqual(Region.JMS, 180) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70) || Version.GreaterOrEqual(Region.KMS, 84) || Version.GreaterOrEqual(Region.GMS, 83)) {
                sp.Encode1(0); // clear stat, call CWvsContext::OnRevive
            }
            // KMS118 only
            if (Version.Equal(Region.KMS, 118)) {
                sp.Encode1(0);
            }
            sp.Encode4(chr.getPosMap()); // dwPosMap
            sp.Encode1(chr.getPortal()); // nPortal
            if (Version.PreBB()) {
                sp.Encode2(chr.getStat().getHp()); // nHP_CS
            } else {
                sp.Encode4(chr.getStat().getHp());
            }

            if (Region.EMS.check() || Region.TWMS.check() || Region.GMS.check() || Region.GMST.check() || Region.VMS.check() || Region.BMS.check() || Region.THMS.check() || Region.MSEA.check()) {
                boolean m_bChaseEnable = false;
                sp.Encode1(m_bChaseEnable ? 1 : 0); // m_bChaseEnable
                if (m_bChaseEnable) {
                    sp.Encode4(0); // m_nTargetPosition_X
                    sp.Encode4(0); // m_nTargetPosition_Y
                }
            }
        }

        if (Version.LessOrEqual(Region.KMS, 31)) {
            return sp;
        }

        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            sp.Encode1(0);
        }
        // サーバーの時間?
        sp.Encode8(SharedDate.getTimestamp()); // ftServer
        if (Version.GreaterOrEqual(Region.KMS, 114) || Version.GreaterOrEqual(Region.KMST, 391) || Version.GreaterOrEqual(Region.JMS, 194) || Version.GreaterOrEqual(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 76) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode4(100); // nMobStatAdjustRate
        }
        if (Version.GreaterOrEqual(Region.KMS, 119) || Version.GreaterOrEqual(Region.KMST, 391) || Version.Equal(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0);
        }
        // KMS169 OK
        if (Version.GreaterOrEqual(Region.KMS, 127) || Version.GreaterOrEqual(Region.KMST, 391) || Version.Equal(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0);
        }
        // not in KMS169
        if (Version.GreaterOrEqual(Region.KMS, 197) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 126)) {
            sp.Encode1(0);
        }
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            sp.Encode1(0);
        }
        return sp;
    }

    // 分割版
    public static ServerPacket SetField_JMS_302(MapleCharacter chr, int part, boolean bCharacterData, long datamask_2) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetField);
        // 分割, 1 -> 2の順で送信
        sp.Encode4(part);
        // main
        if (part == 1) {
            // 008ABA10
            sp.EncodeBuffer(DataCClientOptMan.EncodeOpt());
            sp.Encode4(chr.getClient().getChannelId() - 1);
            sp.Encode1(0);
            sp.Encode1(0);
            sp.Encode4(0);
            sp.Encode1(chr.getPortalCount());
            sp.Encode4(0);
            sp.Encode1(bCharacterData ? 1 : 0); // 1 = all data, 0 = map change
            sp.Encode2(0); // not 0, EncodeStr, EncodeStr x count
            // logged in
            if (bCharacterData) {
                sp.Encode4(chr.getCalcDamage().m_s1); // seed x3
                sp.Encode4(chr.getCalcDamage().m_s2);
                sp.Encode4(chr.getCalcDamage().m_s3);
                long datamask_1 = 0x00444200L | 0x20000000000L; // JMS302
                if (Version.GreaterOrEqual(Region.JMS, 308)) {
                    datamask_1 = 0x00444200L | 0x80000000000L; // JMS308
                }
                sp.EncodeBuffer(DataCharacterData.Encode_302_1(chr, -1 & ~(datamask_1))); // Quest除外
                sp.EncodeBuffer(DataCWvsContext.LogoutGiftConfig());
            } else {
                sp.Encode1(0);
                sp.Encode4(chr.getPosMap());
                sp.Encode1(chr.getPortal());
                sp.Encode4(chr.getStat().getHp());
                sp.Encode1(0); // not 0, 0059E9C0
            }
            sp.Encode1(0);
            return sp;
        }

        // sub
        if (part == 2) {
            // 008AAA80
            sp.EncodeBuffer(DataCharacterData.Encode_302_2(chr, datamask_2));
            sp.Encode8(SharedDate.getTimestamp());
            sp.Encode4(100); // nMobStatAdjustRate
            if (Version.GreaterOrEqual(Region.JMS, 308)) {
                sp.Encode1(0);
            }
            sp.Encode1(0);
            sp.Encode1(GameConstants.is_extendsp_job(chr.getJob()) ? 1 : 0);
            if (Version.GreaterOrEqual(Region.JMS, 308)) {
                sp.Encode1(0);
            }
            return sp;
        }

        // Err
        return sp;
    }

    // CStage::OnSetITC
    public static ServerPacket SetITC(final MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetITC);
        sp.EncodeBuffer(DataCharacterData.Encode(chr));
        // CITC::LoadData
        {
            sp.EncodeStr(chr.getClient().getMapleId());
            sp.Encode4(TacosITC.MTS_MESO); // m_nRegisterFeeMeso
            sp.Encode4(TacosITC.MTS_TAX); // m_nCommissionRate
            sp.Encode4(TacosITC.MTS_BASE); // m_nCommissionBase
            sp.Encode4(24); // m_nAuctionDurationMin
            sp.Encode4(168); // m_nAuctionDurationMax
            if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 47) || Version.GreaterOrEqual(Region.JMS, 146) || Version.GreaterOrEqual(Region.CMS, 62) || Version.GreaterOrEqual(Region.TWMS, 73) || Version.GreaterOrEqual(Region.THMS, 0) || Version.GreaterOrEqual(Region.GMS, 61) || Version.GreaterOrEqual(Region.MSEA, 0) || Version.GreaterOrEqual(Region.EMS, 0) || Version.GreaterOrEqual(Region.BMS, 24) || Version.GreaterOrEqual(Region.VMS, 35)) {
                sp.Encode8(SharedDate.getTimestamp());
            }
        }
        return sp;
    }

    // CStage::OnSetCashShop
    public static ServerPacket SetCashShop(MapleClient c) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetCashShop);
        sp.EncodeBuffer(DataCharacterData.Encode(c.getPlayer()));
        // CCashShop::LoadData
        {
            if (Region.GMS.check() || Region.EMS.check() || Region.BMS.check()) {
                sp.Encode1(1); // EMS55
            }
            // not asia soft.
            if (!(Region.MSEA.check() || Region.THMS.check() || Region.VMS.check())) {
                sp.EncodeStr(c.getMapleId());
            }
            if (Region.EMS.check()) {
                sp.Encode1(0); // EMS55
            }
            // CWvsContext::SetSaleInfo
            {
                if (Version.GreaterOrEqual(Region.JMS, 187) || Version.GreaterOrEqual(Region.CMS, 88) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.EMS, 73)
                        || Region.GMS.check() || Region.BMS.check()) {
                    sp.Encode4(0); // NotSaleCount
                }
                sp.EncodeBuffer(DataCS_COMMODITY.SetSaleInfo());
                if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 92) || Version.GreaterOrEqual(Region.JMS, 180) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70) && !Region.EMS.check() && !Region.GMS.check()) { // X EMS v55
                    sp.Encode2(0); // non 0, Decode4, DecodeStr
                }
                sp.EncodeBuffer(ResCCashShop.getDiscountRates());
            }
            sp.EncodeBuffer(ResCCashShop.getBestItems(), 1080);
            sp.Encode2(0); // CCashShop::DecodeStock
            sp.Encode2(0); // CCashShop::DecodeLimitGoods
            if (Version.GreaterOrEqual(Region.GMS, 83)) {
                sp.Encode2(0);
            }
        }
        sp.Encode1(0); // m_bEventOn

        if (Region.IMS.check()) {
            sp.Encode1(0);
        }
        // m_nHighestCharacterLevelInThisAccount
        if (Region.GMS.check()) {
            sp.Encode4(0);
        }

        return sp;
    }
}
