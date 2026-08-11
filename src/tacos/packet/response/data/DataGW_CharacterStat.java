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
 */
package tacos.packet.response.data;

import odin.client.MapleCharacter;
import odin.client.PlayerStats;
import odin.client.inventory.MaplePet;
import tacos.config.Content;
import tacos.config.Region;
import odin.constants.GameConstants;
import tacos.client.TacosCharacter;
import tacos.config.Config;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsChangeStat;

/**
 *
 * @author Riremito
 */
public class DataGW_CharacterStat {

    // GW_CharacterStat::Decode
    public static byte[] Encode(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        data.Encode4(chr.getId()); // dwCharacterID
        data.EncodeBuffer(chr.getName(), Content.CharacterNameLength.getInt());
        data.Encode1(chr.getGender());
        data.Encode1(chr.getSkinColor());
        data.Encode4(chr.getFace());
        data.Encode4(chr.getHair());

        if (Config.GreaterOrEqual(Region.KMS, 138) || Config.GreaterOrEqual(Region.KMST, 391) || (Region.THMS.check() && Config.PostBB()) || Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.CMS, 88)) {
            // none
        } else if (Region.KMSB.check() || Config.LessOrEqual(Region.KMS, 95) || Config.LessOrEqual(Region.JMS, 131) || Region.BMS.check() || Region.VMS.check()) {
            data.EncodeZeroBytes(8);
        } else if ((Region.JMS.check() || Region.JMST.check() || Region.THMS.check() || Region.GMS.check() || Region.GMST.check() || Region.CMS.check() || Region.MSEA.check() || ((Region.TWMS.check() || Region.EMS.check()) && Config.PreBB()))) {
            data.EncodeZeroBytes(24);
        }

        data.Encode1(chr.getLevel());
        data.Encode2(chr.getJob());

        PlayerStats stat = chr.getStat();

        data.Encode2(stat.str);
        data.Encode2(stat.dex);
        data.Encode2(stat.int_);
        data.Encode2(stat.luk);

        // BB前
        if (Config.PreBB()) {
            data.Encode2(stat.hp);
            data.Encode2(stat.maxhp);
            data.Encode2(stat.mp);
            data.Encode2(stat.maxmp);
        } else {
            // BB後 (v187+)
            data.Encode4(stat.hp);
            data.Encode4(stat.maxhp);
            data.Encode4(stat.mp);
            data.Encode4(stat.maxmp);
        }

        data.Encode2(chr.getRemainingAp());

        // SP
        if (Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70) || Config.GreaterOrEqual(Region.GMS, 83)) {
            // is_extendsp_job
            if (GameConstants.is_extendsp_job(chr.getJob())) {
                final int size = chr.getRemainingSpSize();
                // ExtendSP::Decode
                data.Encode1(size);
                for (int i = 0; i < chr.getRemainingSps().length; i++) {
                    if (chr.getRemainingSp(i) > 0) {
                        data.Encode1(i + 1);
                        if (Config.GreaterOrEqual(Region.KMS, 169) || Config.GreaterOrEqual(Region.JMS, 308) || Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.GMS, 126)) {
                            data.Encode4(chr.getRemainingSp(i));
                        } else {
                            data.Encode1(chr.getRemainingSp(i));
                        }
                    }
                }
            } else {
                data.Encode2(chr.getRemainingSp());
            }
        } else {
            data.Encode2(chr.getRemainingSp());
        }

        if (Config.GreaterOrEqual(Region.KMS, 197)) {
            data.Encode8(0);
        }

        if (Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.CMS, 104)) {
            data.Encode4(chr.getExp());
            data.Encode4(chr.getFame());
            data.Encode4(chr.getGashaEXP());
            data.Encode8(0);
            data.Encode4(chr.getPosMap());
            data.Encode1(chr.getPortal());
            data.Encode2(chr.getSubcategory());
            if (GameConstants.is_demonslayer(chr.getJob())) {
                data.Encode4(0);
            }
            data.Encode1(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.EncodeZeroBytes(21);
            data.Encode4(0);
            data.Encode1(0);
            data.Encode4(0);
            data.Encode1(0);
            data.Encode4(0);
            data.Encode1(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode1(0);
            for (int i = 0; i < 6; i++) {
                data.Encode4(0);
                data.Encode1(0);
                data.Encode4(0);
            }
            data.Encode4(0);
            data.Encode4(0);

            // CMS
            if (Config.GreaterOrEqual(Region.CMS, 104)) {
                return data.getBytes();
            }

            data.EncodeZeroBytes(25);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            return data.getBytes();
        }

        if (Config.GreaterOrEqual(Region.EMS, 89)) {
            data.Encode4(chr.getExp());
            data.Encode4(chr.getFame());
            data.Encode4(chr.getGashaEXP());
            data.Encode8(0);
            data.Encode4(chr.getPosMap());
            data.Encode1(chr.getPortal());
            data.Encode2(chr.getSubcategory());
            if (GameConstants.is_demonslayer(chr.getJob())) {
                data.Encode4(0);
            }
            data.Encode1(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.EncodeZeroBytes(21);
            data.Encode4(0);
            data.Encode1(0);
            data.Encode4(0);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode4(0);
            data.Encode1(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode1(0);
            for (int i = 0; i < 6; i++) {
                data.Encode4(0);
                data.Encode1(0);
                data.Encode4(0);
            }
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            return data.getBytes();
        }

        if (Config.GreaterOrEqual(Region.KMS, 118) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.GMS, 111)) {
            data.Encode4(0);
            data.Encode4(0);
            if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.JMST, 110)) {
                data.Encode4(0);
            }
            data.Encode4(chr.getPosMap());
            data.Encode1(chr.getPortal());
            if (Config.GreaterOrEqual(Region.GMS, 111)) {
                data.Encode4(0);
            }
            data.Encode2(chr.getSubcategory());
            if (GameConstants.is_demonslayer(chr.getJob())) {
                data.Encode4(0);
            }
            data.Encode1(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            if (Region.KMS.check() || Region.KMST.check()) {
                if (Config.GreaterOrEqual(Region.KMS, 119) || Config.GreaterOrEqual(Region.KMST, 391)) {
                    if (Config.GreaterOrEqual(Region.KMS, 160)) {
                        data.EncodeZeroBytes(21);
                    } else {
                        data.EncodeZeroBytes(12);
                    }
                    data.Encode4(0);
                    data.Encode1(0);
                    data.Encode4(0);
                    data.Encode1(0);
                    if (Config.GreaterOrEqual(Region.KMS, 160)) {
                        data.Encode1(0);
                    }
                    if (Config.GreaterOrEqual(Region.KMS, 138) || Config.GreaterOrEqual(Region.KMST, 391)) {
                        data.Encode4(0);
                    }
                    if (Config.GreaterOrEqual(Region.KMS, 160)) {
                        data.Encode1(0);
                    }
                    if (Config.GreaterOrEqual(Region.KMS, 148)) {
                        data.Encode4(0);
                        data.Encode4(0);
                    }
                    if (Config.GreaterOrEqual(Region.KMS, 160)) {
                        data.Encode4(0);
                        data.Encode1(0);
                        {
                            int unkloop_count = 6;
                            if (Config.GreaterOrEqual(Region.KMS, 197)) {
                                unkloop_count = 9;
                            }
                            for (int i = 0; i < unkloop_count; i++) {
                                data.Encode4(0);
                                data.Encode1(0);
                                data.Encode4(0);
                            }
                        }
                        data.Encode4(0);
                        data.Encode4(0);
                    }
                    return data.getBytes();
                }
                if (Config.GreaterOrEqual(Region.KMS, 118) || Config.GreaterOrEqual(Region.KMST, 391)) {
                    data.EncodeZeroBytes(10);
                    data.Encode4(0);
                    data.Encode4(0);
                    data.Encode4(0);
                    data.Encode4(0);
                    return data.getBytes();
                }
                return data.getBytes();
            } else {
                if (Config.GreaterOrEqual(Region.JMS, 308) || Config.GreaterOrEqual(Region.GMS, 116)) {
                    data.EncodeZeroBytes(21);
                } else if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.JMST, 110)) {
                    data.EncodeZeroBytes(12);
                }
            }
            data.Encode4(0);
            data.Encode1(0);
            data.Encode4(0);
            data.Encode1(0);

            if (Config.GreaterOrEqual(Region.JMST, 110)) {
                data.Encode8(0);
                data.Encode4(0);
                data.Encode4(0);
                return data.getBytes();
            }

            if (Config.GreaterOrEqual(Region.JMS, 308) || Config.GreaterOrEqual(Region.GMS, 126)) {
                data.Encode1(0);
            }
            if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.GMS, 116)) {
                data.Encode4(0);
                data.Encode1(0);
            }
            if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.GMS, 111)) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
            }
            if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.GMS, 116)) {
                data.Encode1(0);
                for (int i = 0; i < 6; i++) {
                    data.Encode4(0);
                    data.Encode1(0);
                    data.Encode4(0);
                }
            }
            if (Config.GreaterOrEqual(Region.JMS, 302)) {
                data.Encode4(0);
                data.Encode4(0);
                data.EncodeZeroBytes(8);
            }
            if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.GMS, 116)) {
                data.Encode4(0);
                data.Encode4(0);
            }
            return data.getBytes();
        }

        data.Encode4(chr.getExp()); // nEXP
        data.Encode2(chr.getFame()); // nPOP

        if (Config.GreaterOrEqual(Region.JMS, 146) || Config.GreaterOrEqual(Region.CMS, 62) || Config.GreaterOrEqual(Region.TWMS, 74) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 61) || Config.GreaterOrEqual(Region.MSEA, 0) || Config.GreaterOrEqual(Region.EMS, 76)) {
            data.Encode4(chr.getGashaEXP()); // nTempEXP
        }

        if (Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 76)) {
            data.Encode8(0);
        }

        data.Encode4(chr.getPosMap()); // dwPosMap
        data.Encode1(chr.getPortal()); // nPortal

        if (Region.VMS.check()) {
            return data.getBytes();
        }

        if (Region.KMSB.check()) {
            data.Encode8(0);
            data.Encode4(0);
            data.Encode4(0);
            return data.getBytes();
        }

        if (Config.GreaterOrEqual(Region.GMS, 62) || (Region.EMS.check() && Config.PreBB()) || Config.GreaterOrEqual(Region.BMS, 24)) {
            data.Encode4(0);
        }

        // KMS 84
        if (Config.LessOrEqual(Region.KMS, 84) || Region.BMS.check()) {
            return data.getBytes();
        }
        // JMS 180, KMS 95
        if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 92) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
            data.Encode2(chr.getSubcategory());
        }

        if (Config.LessOrEqual(Region.KMST, 330)) {
            data.Encode4(0); // same as JMS187?
            return data.getBytes();
        }

        // KMS, CMS, EMS
        if (Region.KMS.check() || Region.KMST.check() || Region.CMS.check() || Region.GMS.check() || Region.GMST.check() || Region.EMS.check() || Region.IMS.check() || Region.MSEA.check()) {
            return data.getBytes();
        }

        if (Region.THMS.check()) {
            data.Encode4(0);
            return data.getBytes();
        }

        // TWMS
        if (Region.TWMS.check()) {
            data.EncodeZeroBytes(25);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            return data.getBytes();
        }
        // JMS
        if (Config.PreBB()) {
            data.Encode8(0);
            data.Encode4(0);
            data.Encode4(0);
            // JMS v180-186
            if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 92) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)) {
                data.Encode4(0);
            }
            return data.getBytes();
        }
        // Post BB
        if (Config.Equal(Region.JMS, 187)) {
            data.Encode4(0);
        }
        // JMS v188+
        data.Encode8(0);
        data.Encode4(0);
        data.Encode4(0);
        return data.getBytes();
    }

    // GW_CharacterStat::DecodeMoney
    public static byte[] EncodeMoney(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        if (Config.GreaterOrEqual(Region.KMS, 197)) {
            data.Encode8(chr.getMeso());
        } else {
            data.Encode4(chr.getMeso());
        }
        return data.getBytes();
    }

    // DecodeBuffer size 0x0C
    public static byte[] EncodePachinko(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        data.Encode4(chr.getId());
        data.Encode4(chr.getTama());
        data.Encode4(0);
        return data.getBytes();
    }

    // GW_CharacterStat::DecodeChangeStat
    // GW_CharacterStat::EncodeChangeStat
    public static byte[] EncodeChangeStat(TacosCharacter chr, int statmask) {
        ServerPacket data = new ServerPacket();

        if (Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.CMS, 104) | Config.GreaterOrEqual(Region.GMS, 111)) {
            data.Encode8(statmask);
        } else {
            data.Encode4(statmask);
        }

        // Skin
        if ((statmask & OpsChangeStat.CS_SKIN.get()) != 0) {
            data.Encode1(chr.getSkinColor());
        }
        // Face
        if ((statmask & OpsChangeStat.CS_FACE.get()) != 0) {
            data.Encode4(chr.getFace());
        }
        // Hair
        if ((statmask & OpsChangeStat.CS_HAIR.get()) != 0) {
            data.Encode4(chr.getHair());
        }
        // Pet 1
        if ((statmask & OpsChangeStat.CS_PETSN.get()) != 0) {
            MaplePet pet = chr.getPet(0);
            data.Encode8((pet != null && pet.getSummoned()) ? pet.getUniqueId() : 0);
        }
        // Level
        if ((statmask & OpsChangeStat.CS_LEV.get()) != 0) {
            data.Encode1(chr.getLevel());
        }
        // Job
        if ((statmask & OpsChangeStat.CS_JOB.get()) != 0) {
            data.Encode2(chr.getJob());
            if (Config.GreaterOrEqual(Region.KMS, 197)) {
                data.Encode2(0);
            }
        }
        // STR
        if ((statmask & OpsChangeStat.CS_STR.get()) != 0) {
            data.Encode2(chr.getStat().getStr());
        }
        // DEX
        if ((statmask & OpsChangeStat.CS_DEX.get()) != 0) {
            data.Encode2(chr.getStat().getDex());
        }
        // INT
        if ((statmask & OpsChangeStat.CS_INT.get()) != 0) {
            data.Encode2(chr.getStat().getInt());
        }
        // LUK
        if ((statmask & OpsChangeStat.CS_LUK.get()) != 0) {
            data.Encode2(chr.getStat().getLuk());
        }
        // HP
        if ((statmask & OpsChangeStat.CS_HP.get()) != 0) {
            if (Config.PreBB()) {
                data.Encode2(chr.getStat().getHp());
            } else {
                data.Encode4(chr.getStat().getHp());
            }
        }
        // MAXHP
        if ((statmask & OpsChangeStat.CS_MHP.get()) != 0) {
            if (Config.PreBB()) {
                data.Encode2(chr.getStat().getMaxHp());
            } else {
                data.Encode4(chr.getStat().getMaxHp());
            }
        }
        // MP
        if ((statmask & OpsChangeStat.CS_MP.get()) != 0) {
            if (Config.PreBB()) {
                data.Encode2(chr.getStat().getMp());
            } else {
                data.Encode4(chr.getStat().getMp());
            }
        }
        // MAXMP
        if ((statmask & OpsChangeStat.CS_MMP.get()) != 0) {
            if (Config.PreBB()) {
                data.Encode2(chr.getStat().getMaxMp());
            } else {
                data.Encode4(chr.getStat().getMaxMp());
            }
        }
        // AP
        if ((statmask & OpsChangeStat.CS_AP.get()) != 0) {
            data.Encode2(chr.getRemainingAp());
        }
        // SP
        if ((statmask & OpsChangeStat.CS_SP.get()) != 0) {
            if (GameConstants.is_extendsp_job(chr.getJob())) {
                data.Encode1(chr.getRemainingSpSize());
                for (int i = 0; i < chr.getRemainingSps().length; i++) {
                    if (chr.getRemainingSp(i) > 0) {
                        data.Encode1(i + 1);
                        if (Config.GreaterOrEqual(Region.KMS, 197) || Config.GreaterOrEqual(Region.EMS, 89) || Config.GreaterOrEqual(Region.GMS, 131)) {
                            data.Encode4(chr.getRemainingSp(i));
                        } else {
                            data.Encode1(chr.getRemainingSp(i));
                        }
                    }
                }
            } else {
                data.Encode2(chr.getRemainingSp());
            }
        }
        // EXP
        if ((statmask & OpsChangeStat.CS_EXP.get()) != 0) {
            if (Config.GreaterOrEqual(Region.KMS, 197)) {
                data.Encode8(chr.getExp());
            } else {
                data.Encode4(chr.getExp());
            }
        }
        // 人気度
        if ((statmask & OpsChangeStat.CS_POP.get()) != 0) {
            if (Config.GreaterOrEqual(Region.KMS, 197) || Config.GreaterOrEqual(Region.JMS, 302) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.CMS, 104) || Config.GreaterOrEqual(Region.TWMS, 148) || Config.GreaterOrEqual(Region.GMS, 111) || Config.GreaterOrEqual(Region.EMS, 89)) {
                data.Encode4(chr.getFame());
            } else {
                data.Encode2(chr.getFame());
            }
        }
        // Meso
        if ((statmask & OpsChangeStat.CS_MONEY.get()) != 0) {
            if (Config.GreaterOrEqual(Region.KMS, 197)) {
                data.Encode8(chr.getMeso());
            } else {
                data.Encode4(chr.getMeso());
            }
        }
        // v188 ここから+1
        // Pet 2
        if ((statmask & OpsChangeStat.CS_PETSN2.get()) != 0) {
            MaplePet pet = chr.getPet(1);
            data.Encode8((pet != null && pet.getSummoned()) ? pet.getUniqueId() : 0);
        }
        // Pet 3
        if ((statmask & OpsChangeStat.CS_PETSN3.get()) != 0) {
            MaplePet pet = chr.getPet(2);
            data.Encode8((pet != null && pet.getSummoned()) ? pet.getUniqueId() : 0);
        }
        // 兵法書, GashaExp
        if ((statmask & OpsChangeStat.CS_TEMPEXP.get()) != 0) {
            data.Encode4(chr.getGashaEXP());
        }

        return data.getBytes();
    }
}
