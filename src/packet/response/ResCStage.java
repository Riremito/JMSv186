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
package packet.response;

import client.MapleCharacter;
import client.MapleClient;
import config.ServerConfig;
import constants.GameConstants;
import constants.ServerConstants;
import handling.MaplePacket;
import packet.ServerPacket;
import packet.response.data.DataCClientOptMan;
import packet.response.data.DataCWvsContext;
import packet.response.data.DataCharacterData;
import packet.response.struct.TestHelper;
import server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
// CStage::OnPacket
public class ResCStage {

    // CStage::OnSetField
    public static final MaplePacket SetField(MapleCharacter chr, boolean loggedin, MapleMap to, int spawnPoint) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SetField);
        // JMS184orLater
        if (((ServerConfig.IsJMS() || ServerConfig.IsCMS() || ServerConfig.IsGMS()) && ServerConfig.JMS186orLater())
                || ServerConfig.EMS89orLater()) {
            sp.EncodeBuffer(DataCClientOptMan.EncodeOpt()); // 2 bytes
        }
        // チャンネル
        sp.Encode4(chr.getClient().getChannel() - 1); // m_nChannelID
        if (ServerConfig.KMS138orLater()
                || (ServerConfig.IsJMS() && ServerConfig.JMS146orLater())
                || ServerConfig.EMS89orLater()
                || ServerConfig.TWMS148orLater()
                || ServerConfig.CMS104orLater()) {
            sp.Encode1(0);
        }

        if (ServerConfig.EMS89orLater()) {
            sp.Encode1(1); // Supreme/Ibara World
        }

        if (((ServerConfig.IsJMS() || ServerConfig.IsTWMS() || ServerConfig.IsTHMS() || ServerConfig.IsCMS() || ServerConfig.IsMSEA() || ServerConfig.IsEMS() || ServerConfig.IsGMS() || ServerConfig.IsIMS()) && ServerConfig.JMS180orLater())
                || (ServerConfig.IsKMS() && ServerConfig.IsPostBB())) {
            sp.Encode4(0); // m_dwOldDriverID
        }

        sp.Encode1(chr.getPortalCount()); // sNotifierMessage?
        if (ServerConfig.IsCMS()) {
            sp.Encode1(0);
        }
        if (ServerConfig.JMS194orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
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
            sp.EncodeBuffer(DataCharacterData.Encode(chr));
            // JMS184orLater
            if ((ServerConfig.IsJMS() || ServerConfig.IsCMS() || ServerConfig.IsTWMS() || ServerConfig.IsGMS())
                    && ServerConfig.JMS186orLater()) {
                // ログアウトギフト
                sp.EncodeBuffer(DataCWvsContext.LogoutGiftConfig());
            }
        } else {
            if (ServerConfig.JMS180orLater() || ServerConfig.KMS84orLater() || ServerConfig.GMS83orLater()) {
                sp.Encode1(0);
            }
            // KMS118 only
            if (ServerConfig.KMS118()) {
                sp.Encode1(0);
            }
            sp.Encode4(to.getId()); // characterStat._ZtlSecureTear_dwPosMap_CS
            sp.Encode1(spawnPoint); // characterStat.nPortal
            if (ServerConfig.IsPreBB()) {
                sp.Encode2(chr.getStat().getHp());
            } else {
                sp.Encode4(chr.getStat().getHp());
            }

            if (ServerConfig.IsEMS() || ServerConfig.IsTWMS() || ServerConfig.IsGMS() || ServerConfig.IsVMS() || ServerConfig.IsBMS() || ServerConfig.IsTHMS() || ServerConfig.IsMSEA()) {
                boolean m_bChaseEnable = false;
                sp.Encode1(m_bChaseEnable ? 1 : 0); // m_bChaseEnable
                if (m_bChaseEnable) {
                    sp.Encode4(0); // m_nTargetPosition_X
                    sp.Encode4(0); // m_nTargetPosition_Y
                }
            }
        }
        if (ServerConfig.KMS197orLater()) {
            sp.Encode1(0);
        }
        // サーバーの時間?
        sp.Encode8(TestHelper.getTime(System.currentTimeMillis()));
        if (ServerConfig.JMS194orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
            sp.Encode4(100); // nMobStatAdjustRate
        }
        if (ServerConfig.KMS119orLater() || ServerConfig.JMST110() || ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
            sp.Encode1(0);
        }
        if (ServerConfig.KMS127orLater() || ServerConfig.JMST110() || ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
            sp.Encode1(0);
        }
        if (ServerConfig.KMS197orLater() || ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
            sp.Encode1(0);
        }
        if (ServerConfig.KMS197orLater()) {
            sp.Encode1(0);
        }
        return sp.get();
    }

    // 分割版
    public static final MaplePacket SetField_JMS_302(MapleCharacter chr, int part, boolean loggedin, MapleMap to, int spawnPoint, long datamask_2) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SetField);
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
                if (ServerConfig.JMS308orLater()) {
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
            if (ServerConfig.JMS308orLater()) {
                sp.Encode1(0);
            }
            sp.Encode1(0);
            sp.Encode1(GameConstants.is_extendsp_job(chr.getJob()) ? 1 : 0);
            if (ServerConfig.JMS308orLater()) {
                sp.Encode1(0);
            }
            return sp.get();
        }

        // Err
        return sp.get();
    }

    // CStage::OnSetITC
    public static final MaplePacket SetITC(final MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SetITC);
        sp.EncodeBuffer(DataCharacterData.Encode(chr));
        // CITC::LoadData
        {
            sp.EncodeStr(chr.getClient().getAccountName());
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
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SetCashShop);
        sp.EncodeBuffer(DataCharacterData.Encode(c.getPlayer()));
        // CCashShop::LoadData
        {
            if (ServerConfig.IsEMS() || ServerConfig.IsGMS() || ServerConfig.IsBMS()) {
                sp.Encode1(1); // EMS v55
            }
            if (!(ServerConfig.IsVMS() || ServerConfig.IsTHMS() || ServerConfig.IsMSEA())) {
                sp.EncodeStr(c.getAccountName());
            }
            if (ServerConfig.IsEMS()) {
                sp.Encode1(0); // EMS v55
            }
            // CWvsContext::SetSaleInfo
            {
                if (ServerConfig.IsGMS() || ServerConfig.IsBMS()) {
                    sp.Encode4(0);
                }

                if (ServerConfig.IsPostBB() || ServerConfig.TWMS121orLater()) {
                    if (ServerConfig.IsJMS() || ServerConfig.IsTWMS() || ServerConfig.IsEMS()) {
                        sp.Encode4(0); // NotSaleCount
                    }
                }
                sp.EncodeBuffer(ResCCashShop.getModifiedData());
                if (ServerConfig.JMS180orLater() && !ServerConfig.IsEMS()) { // X EMS v55
                    sp.Encode2(0); // non 0, Decode4, DecodeStr
                }
                sp.EncodeBuffer(ResCCashShop.getDiscountRates());
            }
            sp.EncodeBuffer(ResCCashShop.getBestItems(), 1080);
            sp.Encode2(0); // CCashShop::DecodeStock
            sp.Encode2(0); // CCashShop::DecodeLimitGoods
            if (ServerConfig.GMS84orLater()) {
                sp.Encode2(0);
            }
        }
        sp.Encode1(0); // m_bEventOn

        if (ServerConfig.IsIMS()) {
            sp.Encode1(0);
        }

        // m_nHighestCharacterLevelInThisAccount
        if (ServerConfig.IsGMS()) {
            sp.Encode4(0);
        }
        return sp.get();
    }

}
