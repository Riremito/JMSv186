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
import tacos.config.Region;
import odin.constants.GameConstants;
import tacos.config.Config;
import tacos.packet.ServerPacket;
import tacos.packet.response.data.RD_CharacterData;
import tacos.packet.ServerPacketHeader;
import tacos.packet.response.data.RD_CStage;
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

        sp.EncodeBuffer(RD_CStage.ClientOptMan_EncodeOpt(), Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.EMS, 89)); // 2 bytes
        sp.Encode4(chr.getClient().getChannelId() - 1); // m_nChannelID
        sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 138) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 146) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 89));
        sp.Encode1(1, Config.GreaterOrEqual(Region.EMS, 89)); // Supreme/Ibara World
        sp.Encode4(0, Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)); // m_dwOldDriverID
        sp.Encode1(chr.getPortalCount()); // sNotifierMessage?
        sp.Encode1(0, Region.CMS.check());
        sp.Encode4(0, Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 76));
        sp.Encode1(bCharacterData ? 1 : 0); // bCharacterData, 1 = all data, 0 = map change
        sp.Encode2(0, Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 47) || Config.GreaterOrEqual(Region.JMS, 146) || Config.GreaterOrEqual(Region.CMS, 62) || Config.GreaterOrEqual(Region.TWMS, 74) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 61) || Config.GreaterOrEqual(Region.MSEA, 0) || Config.GreaterOrEqual(Region.EMS, 55) || Config.GreaterOrEqual(Region.BMS, 24) || Config.GreaterOrEqual(Region.VMS, 35)); // nNotifierCheck
        if (bCharacterData) {
            sp.Encode4(chr.getCalcDamage().m_s1);
            sp.Encode4(chr.getCalcDamage().m_s2);
            sp.Encode4(chr.getCalcDamage().m_s3);

            if (Config.GreaterOrEqual(Region.GMS, 126)) {
                sp.EncodeBuffer(RD_CharacterData.Encode(chr, -1L & ~0x400000000000L));
            } else if (Config.GreaterOrEqual(Region.GMS, 111)) {
                sp.EncodeBuffer(RD_CharacterData.Encode(chr, -1L & ~0x200000000L));
            } else {
                sp.EncodeBuffer(RD_CharacterData.Encode(chr));
            }

            sp.EncodeBuffer(RD_CStage.CWvsContext_OnSetLogoutGiftConfig(), Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.GMS, 91));
        } else {
            sp.Encode1(0, Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 84) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 83) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)); // clear stat, call CWvsContext::OnRevive
            sp.Encode1(0, Config.Equal(Region.KMS, 118));
            sp.Encode4(chr.getPosMap()); // dwPosMap
            sp.Encode1(chr.getPortal()); // nPortal

            if (Config.PreBB()) {
                sp.Encode2(chr.getStat().getHp()); // nHP_CS
            } else {
                sp.Encode4(chr.getStat().getHp());
            }

            if (Region.TWMS.check() || Region.HKMS.check() || Region.THMS.check() || Region.GMS.check() || Region.GMST.check() || Region.MSEA.check() || Region.EMS.check() || Region.BMS.check() || Region.VMS.check()) {
                boolean m_bChaseEnable = false;
                sp.Encode1(m_bChaseEnable ? 1 : 0); // m_bChaseEnable
                if (m_bChaseEnable) {
                    sp.Encode4(0); // m_nTargetPosition_X
                    sp.Encode4(0); // m_nTargetPosition_Y
                }
            }
        }

        if (Config.LessOrEqual(Region.KMS, 31)) {
            return sp;
        }

        sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 197));
        sp.Encode8(SharedDate.getTimestamp()); // ftServer
        sp.Encode4(100, Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 76)); // nMobStatAdjustRate
        sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 119) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 89));
        sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 127) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 89));
        sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 197) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.GMS, 126) || Config.GreaterOrEqual(Region.EMS, 89));
        sp.Encode1(0, Config.GreaterOrEqual(Region.KMS, 197));
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
            sp.EncodeBuffer(RD_CStage.ClientOptMan_EncodeOpt());
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
                if (Config.GreaterOrEqual(Region.JMS, 308)) {
                    datamask_1 = 0x00444200L | 0x80000000000L; // JMS308
                }
                sp.EncodeBuffer(RD_CharacterData.Encode_302_1(chr, -1 & ~(datamask_1))); // Quest除外
                sp.EncodeBuffer(RD_CStage.CWvsContext_OnSetLogoutGiftConfig());
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
            sp.EncodeBuffer(RD_CharacterData.Encode_302_2(chr, datamask_2));
            sp.Encode8(SharedDate.getTimestamp());
            sp.Encode4(100); // nMobStatAdjustRate
            sp.Encode1(0, Config.GreaterOrEqual(Region.JMS, 308));
            sp.Encode1(0);
            sp.Encode1(GameConstants.is_extendsp_job(chr.getJob()) ? 1 : 0);
            sp.Encode1(0, Config.GreaterOrEqual(Region.JMS, 308));

            return sp;
        }

        // Err
        return sp;
    }

    // CStage::OnSetITC
    public static ServerPacket SetITC(final MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetITC);

        sp.EncodeBuffer(RD_CharacterData.Encode(chr));
        sp.EncodeBuffer(RD_CStage.CITC_CITC(chr));
        return sp;
    }

    // CStage::OnSetCashShop
    public static ServerPacket SetCashShop(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SetCashShop);

        sp.EncodeBuffer(RD_CharacterData.Encode(chr));
        sp.EncodeBuffer(RD_CStage.CCashShop_CCashShop(chr));
        return sp;
    }
}
