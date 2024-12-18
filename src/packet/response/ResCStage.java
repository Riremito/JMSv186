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
import constants.ServerConstants;
import handling.MaplePacket;
import packet.ServerPacket;
import packet.response.struct.CClientOptMan;
import packet.response.struct.CWvsContext;
import packet.response.struct.CharacterData;
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
        if ((ServerConfig.IsJMS() || ServerConfig.IsCMS())
                && ServerConfig.JMS186orLater()) {
            sp.EncodeBuffer(CClientOptMan.EncodeOpt()); // 2 bytes
        }
        // チャンネル
        sp.Encode4(chr.getClient().getChannel() - 1);
        if (ServerConfig.IsJMS()
                && ServerConfig.JMS164orLater()) {
            sp.Encode1(0);
        }

        if (((ServerConfig.IsJMS() || ServerConfig.IsTWMS() || ServerConfig.IsCMS() || ServerConfig.IsEMS()) && ServerConfig.JMS180orLater())
                || (ServerConfig.IsKMS() && ServerConfig.IsPostBB())) {
            sp.Encode4(0);
        }

        sp.Encode1(chr.getPortalCount());
        if (ServerConfig.JMS194orLater()
                || ServerConfig.IsEMS()) {
            sp.Encode4(0);
        }
        if (ServerConfig.IsCMS()) {
            sp.Encode1(0);
        }
        sp.Encode1(loggedin ? 1 : 0); // 1 = all data, 0 = map change
        if (ServerConfig.JMS164orLater()) {
            sp.Encode2(0);
        }
        if (loggedin) {
            // [chr.CRand().connectData(mplew);]
            {
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
            }
            // キャラクター情報
            sp.EncodeBuffer(CharacterData.Encode(chr));
            // JMS184orLater
            if ((ServerConfig.IsJMS() || ServerConfig.IsCMS() || ServerConfig.IsTWMS())
                    && ServerConfig.JMS186orLater()) {
                // ログアウトギフト
                sp.EncodeBuffer(CWvsContext.LogoutGiftConfig());
            }
        } else {
            if (ServerConfig.JMS180orLater()) {
                sp.Encode1(0);
            }
            sp.Encode4(to.getId()); // characterStat._ZtlSecureTear_dwPosMap_CS
            sp.Encode1(spawnPoint); // characterStat.nPortal
            if (ServerConfig.IsPreBB()) {
                sp.Encode2(chr.getStat().getHp());
            } else {
                sp.Encode4(chr.getStat().getHp());
            }

            if (ServerConfig.IsEMS() || ServerConfig.IsTWMS()) {
                boolean m_bChaseEnable = false;
                sp.Encode1(m_bChaseEnable ? 1 : 0); // m_bChaseEnable
                if (m_bChaseEnable) {
                    sp.Encode4(0); // m_nTargetPosition_X
                    sp.Encode4(0); // m_nTargetPosition_Y
                }
            }
        }
        // サーバーの時間?
        sp.Encode8(TestHelper.getTime(System.currentTimeMillis()));
        if (ServerConfig.JMS194orLater()
                || ServerConfig.IsEMS()) {
            sp.Encode4(0);
            if (ServerConfig.IsJMS()) {
                sp.Encode4(100); // nMobStatAdjustRate
            }
        }
        return sp.Get();
    }

    // CStage::OnSetITC
    public static final MaplePacket SetITC(final MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SetITC);
        sp.EncodeBuffer(CharacterData.Encode(chr));
        // CITC::LoadData
        {
            sp.EncodeStr(chr.getClient().getAccountName());
            sp.Encode4(ServerConstants.MTS_MESO); // m_nRegisterFeeMeso
            sp.Encode4(ServerConstants.MTS_TAX); // m_nCommissionRate
            sp.Encode4(ServerConstants.MTS_BASE); // m_nCommissionBase
            sp.Encode4(24); // m_nAuctionDurationMin
            sp.Encode4(168); // m_nAuctionDurationMax
            if (ServerConfig.JMS164orLater()) {
                sp.Encode8(TestHelper.getTime(System.currentTimeMillis()));
            }
        }
        return sp.Get();
    }

    // CStage::OnSetCashShop
    public static MaplePacket SetCashShop(MapleClient c) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SetCashShop);
        sp.EncodeBuffer(CharacterData.Encode(c.getPlayer()));
        // CCashShop::LoadData
        {
            sp.EncodeStr(c.getAccountName());
            // CWvsContext::SetSaleInfo
            {
                if ((ServerConfig.IsJMS() || ServerConfig.IsTWMS() || ServerConfig.IsEMS())
                        && ServerConfig.IsPostBB()) {
                    sp.Encode4(0); // NotSaleCount
                }
                sp.EncodeBuffer(ResCCashShop.getModifiedData());
                if (ServerConfig.JMS165orLater()) {
                    sp.Encode2(0); // non 0, Decode4, DecodeStr
                }
                sp.EncodeBuffer(ResCCashShop.getDiscountRates());
            }
            sp.EncodeBuffer(ResCCashShop.getBestItems(), 1080);
            sp.Encode2(0); // CCashShop::DecodeStock
            sp.Encode2(0); // CCashShop::DecodeLimitGoods
        }
        sp.Encode1(0); // m_bEventOn
        // m_nHighestCharacterLevelInThisAccount
        return sp.Get();
    }

}
