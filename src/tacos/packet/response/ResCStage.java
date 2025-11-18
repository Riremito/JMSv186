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
import tacos.config.ServerConfig;
import tacos.config.Version;
import odin.constants.GameConstants;
import odin.constants.ServerConstants;
import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import tacos.packet.response.data.DataCClientOptMan;
import tacos.packet.response.data.DataCWvsContext;
import tacos.packet.response.data.DataCharacterData;
import tacos.packet.response.struct.TestHelper;
import odin.server.maps.MapleMap;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
// CStage::OnPacket
public class ResCStage {

    // CStage::OnSetField
    public static final MaplePacket SetField(MapleCharacter chr, boolean loggedin, MapleMap to, int spawnPoint) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetField);
        // JMS184orLater
        if (((Region.IsJMS() || Region.IsCMS() || Region.IsGMS()) && ServerConfig.JMS186orLater())
                || Version.GreaterOrEqual(Region.EMS, 89)) {
            sp.EncodeBuffer(DataCClientOptMan.EncodeOpt()); // 2 bytes
        }
        // チャンネル
        sp.Encode4(chr.getClient().getChannel() - 1); // m_nChannelID
        if (ServerConfig.KMS138orLater()
                || (Region.IsJMS() && ServerConfig.JMS146orLater())
                || Version.GreaterOrEqual(Region.EMS, 89)
                || Version.GreaterOrEqual(Region.TWMS, 148)
                || Version.GreaterOrEqual(Region.CMS, 104)
                || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0);
        }

        if (Version.GreaterOrEqual(Region.EMS, 89)) {
            sp.Encode1(1); // Supreme/Ibara World
        }

        if (((Region.IsJMS() || Region.IsTWMS() || Region.IsTHMS() || Region.IsCMS() || Region.IsMSEA() || Region.IsEMS() || Region.IsGMS() || Region.IsIMS()) && ServerConfig.JMS180orLater())
                || (Region.IsKMS() && Version.PostBB())) {
            sp.Encode4(0); // m_dwOldDriverID
        }

        sp.Encode1(chr.getPortalCount()); // sNotifierMessage?
        if (Region.IsCMS()) {
            sp.Encode1(0);
        }
        if (ServerConfig.JMS194orLater() || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode4(0);
        }
        sp.Encode1(loggedin ? 1 : 0); // bCharacterData, 1 = all data, 0 = map change
        if (ServerConfig.JMS146orLater()) {
            sp.Encode2(0); // nNotifierCheck
        }
        if (loggedin) {
            // [chr.CRand().connectData(mplew);]
            {
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
            }
            // キャラクター情報
            if (Version.GreaterOrEqual(Region.GMS, 126)) {
                sp.EncodeBuffer(DataCharacterData.Encode(chr, -1L & ~0x400000000000L));
            } else if (Version.GreaterOrEqual(Region.GMS, 111)) {
                sp.EncodeBuffer(DataCharacterData.Encode(chr, -1L & ~0x200000000L));
            } else {
                sp.EncodeBuffer(DataCharacterData.Encode(chr));
            }
            // JMS184orLater
            if ((Region.IsJMS() || Region.IsCMS() || Region.IsTWMS() || Region.IsGMS())
                    && ServerConfig.JMS186orLater()) {
                // ログアウトギフト
                sp.EncodeBuffer(DataCWvsContext.LogoutGiftConfig());
            }
        } else {
            if (ServerConfig.JMS180orLater() || Version.GreaterOrEqual(Region.KMS, 84) || Version.GreaterOrEqual(Region.GMS, 83)) {
                sp.Encode1(0);
            }
            // KMS118 only
            if (Version.Equal(Region.KMS, 118)) {
                sp.Encode1(0);
            }
            sp.Encode4(to.getId()); // characterStat._ZtlSecureTear_dwPosMap_CS
            sp.Encode1(spawnPoint); // characterStat.nPortal
            if (Version.PreBB()) {
                sp.Encode2(chr.getStat().getHp());
            } else {
                sp.Encode4(chr.getStat().getHp());
            }

            if (Region.IsEMS() || Region.IsTWMS() || Region.IsGMS() || Region.IsVMS() || Region.IsBMS() || Region.IsTHMS() || Region.IsMSEA()) {
                boolean m_bChaseEnable = false;
                sp.Encode1(m_bChaseEnable ? 1 : 0); // m_bChaseEnable
                if (m_bChaseEnable) {
                    sp.Encode4(0); // m_nTargetPosition_X
                    sp.Encode4(0); // m_nTargetPosition_Y
                }
            }
        }

        if (Version.LessOrEqual(Region.KMS, 31)) {
            return sp.get();
        }

        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            sp.Encode1(0);
        }
        // サーバーの時間?
        sp.Encode8(TestHelper.getTime(System.currentTimeMillis()));
        if (ServerConfig.JMS194orLater() || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode4(100); // nMobStatAdjustRate
        }
        if (ServerConfig.KMS119orLater() || Version.Equal(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0);
        }
        // KMS169 OK
        if (ServerConfig.KMS127orLater() || Version.Equal(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0);
        }
        // not in KMS169
        if (Version.GreaterOrEqual(Region.KMS, 197) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 126)) {
            sp.Encode1(0);
        }
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            sp.Encode1(0);
        }
        return sp.get();
    }

    // 分割版
    public static final MaplePacket SetField_JMS_302(MapleCharacter chr, int part, boolean loggedin, MapleMap to, int spawnPoint, long datamask_2) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetField);
        // 分割, 1 -> 2の順で送信
        sp.Encode4(part);
        // main
        if (part == 1) {
            // 008ABA10
            sp.EncodeBuffer(DataCClientOptMan.EncodeOpt());
            sp.Encode4(chr.getClient().getChannel() - 1);
            sp.Encode1(0);
            sp.Encode1(0);
            sp.Encode4(0);
            sp.Encode1(chr.getPortalCount());
            sp.Encode4(0);
            sp.Encode1(loggedin ? 1 : 0); // 1 = all data, 0 = map change
            sp.Encode2(0); // not 0, EncodeStr, EncodeStr x count
            // logged in
            if (loggedin) {
                sp.Encode4(0); // seed x3
                sp.Encode4(0);
                sp.Encode4(0);
                long datamask_1 = 0x00444200L | 0x20000000000L; // JMS302
                if (Version.GreaterOrEqual(Region.JMS, 308)) {
                    datamask_1 = 0x00444200L | 0x80000000000L; // JMS308
                }
                sp.EncodeBuffer(DataCharacterData.Encode_302_1(chr, -1 & ~(datamask_1))); // Quest除外
                sp.EncodeBuffer(DataCWvsContext.LogoutGiftConfig());
            } else {
                sp.Encode1(0);
                sp.Encode4(to.getId());
                sp.Encode1(spawnPoint);
                sp.Encode4(chr.getStat().getHp());
                sp.Encode1(0); // not 0, 0059E9C0
            }
            sp.Encode1(0);
            return sp.get();
        }

        // sub
        if (part == 2) {
            // 008AAA80
            sp.EncodeBuffer(DataCharacterData.Encode_302_2(chr, datamask_2));
            sp.Encode8(TestHelper.getTime(System.currentTimeMillis()));
            sp.Encode4(100); // nMobStatAdjustRate
            if (Version.GreaterOrEqual(Region.JMS, 308)) {
                sp.Encode1(0);
            }
            sp.Encode1(0);
            sp.Encode1(GameConstants.is_extendsp_job(chr.getJob()) ? 1 : 0);
            if (Version.GreaterOrEqual(Region.JMS, 308)) {
                sp.Encode1(0);
            }
            return sp.get();
        }

        // Err
        return sp.get();
    }

    // CStage::OnSetITC
    public static final MaplePacket SetITC(final MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetITC);
        sp.EncodeBuffer(DataCharacterData.Encode(chr));
        // CITC::LoadData
        {
            sp.EncodeStr(chr.getClient().getMapleId());
            sp.Encode4(ServerConstants.MTS_MESO); // m_nRegisterFeeMeso
            sp.Encode4(ServerConstants.MTS_TAX); // m_nCommissionRate
            sp.Encode4(ServerConstants.MTS_BASE); // m_nCommissionBase
            sp.Encode4(24); // m_nAuctionDurationMin
            sp.Encode4(168); // m_nAuctionDurationMax
            if (ServerConfig.JMS146orLater()) {
                sp.Encode8(TestHelper.getTime(System.currentTimeMillis()));
            }
        }
        return sp.get();
    }

    // CStage::OnSetCashShop
    public static MaplePacket SetCashShop(MapleClient c) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetCashShop);
        sp.EncodeBuffer(DataCharacterData.Encode(c.getPlayer()));
        // CCashShop::LoadData
        {
            if (Region.IsEMS() || Region.IsGMS() || Region.IsBMS()) {
                sp.Encode1(1); // EMS v55
            }
            if (!(Region.IsVMS() || Region.IsTHMS() || Region.IsMSEA())) {
                sp.EncodeStr(c.getMapleId());
            }
            if (Region.IsEMS()) {
                sp.Encode1(0); // EMS v55
            }
            // CWvsContext::SetSaleInfo
            {
                if (Region.IsGMS() || Region.IsBMS()) {
                    sp.Encode4(0);
                }

                if (Version.PostBB() || Version.GreaterOrEqual(Region.TWMS, 121)) {
                    if (Region.IsJMS() || Region.IsTWMS() || Region.IsEMS()) {
                        sp.Encode4(0); // NotSaleCount
                    }
                }
                sp.EncodeBuffer(ResCCashShop.getModifiedData());
                if (ServerConfig.JMS180orLater() && !Region.IsEMS()) { // X EMS v55
                    sp.Encode2(0); // non 0, Decode4, DecodeStr
                }
                sp.EncodeBuffer(ResCCashShop.getDiscountRates());
            }
            sp.EncodeBuffer(ResCCashShop.getBestItems(), 1080);
            sp.Encode2(0); // CCashShop::DecodeStock
            sp.Encode2(0); // CCashShop::DecodeLimitGoods
            if (Version.GreaterOrEqual(Region.GMS, 84)) {
                sp.Encode2(0);
            }
        }
        sp.Encode1(0); // m_bEventOn

        if (Region.IsIMS()) {
            sp.Encode1(0);
        }

        // m_nHighestCharacterLevelInThisAccount
        if (Region.IsGMS()) {
            sp.Encode4(0);
        }
        return sp.get();
    }

}
