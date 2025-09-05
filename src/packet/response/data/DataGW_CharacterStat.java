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
package packet.response.data;

import client.MapleCharacter;
import client.PlayerStats;
import client.inventory.MaplePet;
import config.Content;
import config.Region;
import config.ServerConfig;
import config.Version;
import constants.GameConstants;
import packet.ServerPacket;
import packet.ops.OpsChangeStat;

/**
 *
 * @author Riremito
 */
public class DataGW_CharacterStat {

    // GW_CharacterStat::Decode
    // CharStats
    public static byte[] Encode(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        data.Encode4(chr.getId());
        data.EncodeBuffer(chr.getName(), Content.CharacterNameLength.getInt());
        data.Encode1(chr.getGender());
        data.Encode1(chr.getSkinColor());
        data.Encode4(chr.getFace());
        data.Encode4(chr.getHair());

        if (ServerConfig.KMS138orLater() || (Region.IsTHMS() && Version.PostBB()) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.CMS, 104)) {
            // none
        } else if (Region.check(Region.KMSB) || Version.LessOrEqual(Region.JMS, 131) || Version.LessOrEqual(Region.KMS, 95) || Region.IsBMS() || Region.IsVMS()) {
            data.EncodeZeroBytes(8);
        } else if ((Region.IsJMS() || Region.IsTHMS() || Region.IsGMS() || Region.IsCMS() || Region.IsMSEA() || ((Region.IsTWMS() || Region.IsEMS()) && Version.PreBB()))) {
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
        if (Version.PreBB()) {
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
        if (ServerConfig.JMS186orLater() || Version.GreaterOrEqual(Region.GMS, 83)) {
            // is_extendsp_job
            if (GameConstants.is_extendsp_job(chr.getJob())) {
                final int size = chr.getRemainingSpSize();
                // ExtendSP::Decode
                data.Encode1(size);
                for (int i = 0; i < chr.getRemainingSps().length; i++) {
                    if (chr.getRemainingSp(i) > 0) {
                        data.Encode1(i + 1);
                        if (Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.KMS, 197) || Version.GreaterOrEqual(Region.EMS, 89)) {
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

        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            data.Encode8(0);
        }

        if (Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
            data.Encode4(chr.getExp());
            data.Encode4(chr.getFame());
            data.Encode4(chr.getGashaEXP());
            data.Encode8(0);
            data.Encode4(chr.getMapId());
            data.Encode1(chr.getInitialSpawnpoint());
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
            if (Version.GreaterOrEqual(Region.CMS, 104)) {
                return data.get().getBytes();
            }

            data.EncodeZeroBytes(25);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            return data.get().getBytes();
        }

        if (Version.GreaterOrEqual(Region.EMS, 89)) {
            data.Encode4(chr.getExp());
            data.Encode4(chr.getFame());
            data.Encode4(chr.getGashaEXP());
            data.Encode8(0);
            data.Encode4(chr.getMapId());
            data.Encode1(chr.getInitialSpawnpoint());
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
            return data.get().getBytes();
        }

        if (ServerConfig.KMS118orLater() || Version.GreaterOrEqual(Region.JMS, 302) || Version.Equal(Region.JMST, 110) || Version.GreaterOrEqual(Region.GMS, 111)) {
            data.Encode4(0);
            data.Encode4(0);
            if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.GMS, 111) || Version.Equal(Region.JMST, 110)) {
                data.Encode4(0);
            }
            data.Encode4(chr.getMapId());
            data.Encode1(chr.getInitialSpawnpoint());
            if (Version.GreaterOrEqual(Region.GMS, 111)) {
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
            if (Region.IsKMS()) {
                if (ServerConfig.KMS119orLater()) {
                    if (Version.GreaterOrEqual(Region.KMS, 160)) {
                        data.EncodeZeroBytes(21);
                    } else {
                        data.EncodeZeroBytes(12);
                    }
                    data.Encode4(0);
                    data.Encode1(0);
                    data.Encode4(0);
                    data.Encode1(0);
                    if (Version.GreaterOrEqual(Region.KMS, 160)) {
                        data.Encode1(0);
                    }
                    if (ServerConfig.KMS138orLater()) {
                        data.Encode4(0);
                    }
                    if (Version.GreaterOrEqual(Region.KMS, 160)) {
                        data.Encode1(0);
                    }
                    if (Version.GreaterOrEqual(Region.KMS, 148)) {
                        data.Encode4(0);
                        data.Encode4(0);
                    }
                    if (Version.GreaterOrEqual(Region.KMS, 160)) {
                        data.Encode4(0);
                        data.Encode1(0);
                        {
                            int unkloop_count = 6;
                            if (Version.GreaterOrEqual(Region.KMS, 197)) {
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
                    return data.get().getBytes();
                }
                if (ServerConfig.KMS118orLater()) {
                    data.EncodeZeroBytes(10);
                    data.Encode4(0);
                    data.Encode4(0);
                    data.Encode4(0);
                    data.Encode4(0);
                    return data.get().getBytes();
                }
                return data.get().getBytes();
            } else {
                if (Version.GreaterOrEqual(Region.JMS, 308) || Version.GreaterOrEqual(Region.GMS, 116)) {
                    data.EncodeZeroBytes(21);
                } else if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.GMS, 111) || Version.Equal(Region.JMST, 110)) {
                    data.EncodeZeroBytes(12);
                }
            }
            data.Encode4(0);
            data.Encode1(0);
            data.Encode4(0);
            data.Encode1(0);
            if (Version.GreaterOrEqual(Region.JMS, 308)) {
                data.Encode1(0);
            }
            if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.GMS, 116)) {
                data.Encode4(0);
                data.Encode1(0);
            }
            if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.GMS, 111)) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
            }
            if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.GMS, 116)) {
                data.Encode1(0);
                for (int i = 0; i < 6; i++) {
                    data.Encode4(0);
                    data.Encode1(0);
                    data.Encode4(0);
                }
            }
            if (Version.GreaterOrEqual(Region.JMS, 302)) {
                data.Encode4(0);
                data.Encode4(0);
                data.EncodeZeroBytes(8);
            }
            if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.GMS, 116)) {
                data.Encode4(0);
                data.Encode4(0);
            }
            return data.get().getBytes();
        }

        data.Encode4(chr.getExp());
        data.Encode2(chr.getFame());

        if ((Region.IsJMS() || Region.IsCMS() || Region.IsTHMS() || Region.IsTWMS() || Region.IsGMS() || Region.IsMSEA() || (Region.IsEMS() && Version.PostBB()))
                && ServerConfig.JMS146orLater()) {
            data.Encode4(chr.getGashaEXP()); // Gachapon exp
        }

        if (Version.GreaterOrEqual(Region.TWMS, 121) || Region.IsCMS() || Region.IsMSEA() || (Region.IsEMS() && Version.PostBB())) {
            data.Encode8(0);
        }

        data.Encode4(chr.getMapId()); // current map id
        data.Encode1(chr.getInitialSpawnpoint()); // spawnpoint

        if (Region.IsVMS()) {
            return data.get().getBytes();
        }

        if (Region.check(Region.KMSB)) {
            data.Encode8(0);
            data.Encode4(0);
            data.Encode4(0);
            return data.get().getBytes();
        }

        if (Region.IsGMS() || (Region.IsEMS() && Version.PreBB()) || Region.IsBMS()) {
            data.Encode4(0);
        }

        // KMS 84
        if (Version.LessOrEqual(Region.KMS, 84) || Region.IsBMS()) {
            return data.get().getBytes();
        }
        // JMS 180, KMS 95
        if (ServerConfig.JMS180orLater() || Version.GreaterOrEqual(Region.KMS, 92)) {
            data.Encode2(chr.getSubcategory());
        }
        // KMS, CMS, EMS
        if (Region.IsKMS() || Region.IsCMS() || Region.IsGMS() || Region.IsEMS() || Region.IsIMS() || Region.IsMSEA()) {
            return data.get().getBytes();
        }

        if (Region.IsTHMS()) {
            data.Encode4(0);
            return data.get().getBytes();
        }

        // TWMS
        if (Region.IsTWMS()) {
            data.EncodeZeroBytes(25);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            return data.get().getBytes();
        }
        // JMS
        if (Version.PreBB()) {
            data.Encode8(0);
            data.Encode4(0);
            data.Encode4(0);
            // JMS v180-186
            if (ServerConfig.JMS180orLater()) {
                data.Encode4(0);
            }
            return data.get().getBytes();
        }
        // Post BB
        if (Region.IsJMS() && Version.getVersion() == 187) {
            data.Encode4(0);
        }
        // JMS v188+
        data.Encode8(0);
        data.Encode4(0);
        data.Encode4(0);
        return data.get().getBytes();
    }

    // GW_CharacterStat::DecodeMoney
    public static byte[] EncodeMoney(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            data.Encode8(chr.getMeso());
        } else {
            data.Encode4(chr.getMeso());
        }
        return data.get().getBytes();
    }

    // DecodeBuffer size 0x0C
    public static byte[] EncodePachinko(MapleCharacter chr) {
        ServerPacket p = new ServerPacket();

        p.Encode4(chr.getId());
        p.Encode4(chr.getTama());
        p.Encode4(0);
        return p.get().getBytes();
    }

    // GW_CharacterStat::DecodeChangeStat
    // GW_CharacterStat::EncodeChangeStat
    public static byte[] EncodeChangeStat(MapleCharacter chr, int statmask) {
        ServerPacket data = new ServerPacket();

        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.Equal(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) | Version.GreaterOrEqual(Region.GMS, 111)) {
            data.Encode8(statmask);
        } else {
            data.Encode4(statmask);
        }

        // Skin
        if ((statmask & OpsChangeStat.CS_SKIN.get()) > 0) {
            data.Encode1(chr.getSkinColor());
        }
        // Face
        if ((statmask & OpsChangeStat.CS_FACE.get()) > 0) {
            data.Encode4(chr.getFace());
        }
        // Hair
        if ((statmask & OpsChangeStat.CS_HAIR.get()) > 0) {
            data.Encode4(chr.getHair());
        }
        // Pet 1
        if ((statmask & OpsChangeStat.CS_PETSN.get()) > 0) {
            MaplePet pet = chr.getPet(0);
            data.Encode8((pet != null && pet.getSummoned()) ? pet.getUniqueId() : 0);
        }
        // Level
        if ((statmask & OpsChangeStat.CS_LEV.get()) > 0) {
            data.Encode1(chr.getLevel());
        }
        // Job
        if ((statmask & OpsChangeStat.CS_JOB.get()) > 0) {
            data.Encode2(chr.getJob());
        }
        // STR
        if ((statmask & OpsChangeStat.CS_STR.get()) > 0) {
            data.Encode2(chr.getStat().getStr());
        }
        // DEX
        if ((statmask & OpsChangeStat.CS_DEX.get()) > 0) {
            data.Encode2(chr.getStat().getDex());
        }
        // INT
        if ((statmask & OpsChangeStat.CS_INT.get()) > 0) {
            data.Encode2(chr.getStat().getInt());
        }
        // LUK
        if ((statmask & OpsChangeStat.CS_LUK.get()) > 0) {
            data.Encode2(chr.getStat().getLuk());
        }
        // HP
        if ((statmask & OpsChangeStat.CS_HP.get()) > 0) {
            if (Version.PreBB()) {
                data.Encode2(chr.getStat().getHp());
            } else {
                data.Encode4(chr.getStat().getHp());
            }
        }
        // MAXHP
        if ((statmask & OpsChangeStat.CS_MHP.get()) > 0) {
            if (Version.PreBB()) {
                data.Encode2(chr.getStat().getMaxHp());
            } else {
                data.Encode4(chr.getStat().getMaxHp());
            }
        }
        // MP
        if ((statmask & OpsChangeStat.CS_MP.get()) > 0) {
            if (Version.PreBB()) {
                data.Encode2(chr.getStat().getMp());
            } else {
                data.Encode4(chr.getStat().getMp());
            }
        }
        // MAXMP
        if ((statmask & OpsChangeStat.CS_MMP.get()) > 0) {
            if (Version.PreBB()) {
                data.Encode2(chr.getStat().getMaxMp());
            } else {
                data.Encode4(chr.getStat().getMaxMp());
            }
        }
        // AP
        if ((statmask & OpsChangeStat.CS_AP.get()) > 0) {
            data.Encode2(chr.getRemainingAp());
        }
        // SP
        if ((statmask & OpsChangeStat.CS_SP.get()) > 0) {
            if (GameConstants.is_extendsp_job(chr.getJob())) {
                data.Encode1(chr.getRemainingSpSize());
                for (int i = 0; i < chr.getRemainingSps().length; i++) {
                    if (chr.getRemainingSp(i) > 0) {
                        data.Encode1(i + 1);
                        if (Version.GreaterOrEqual(Region.KMS, 197) || Version.GreaterOrEqual(Region.EMS, 89)) {
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
        if ((statmask & OpsChangeStat.CS_EXP.get()) > 0) {
            if (Version.GreaterOrEqual(Region.KMS, 197)) {
                data.Encode8(chr.getExp());
            } else {
                data.Encode4(chr.getExp());
            }
        }
        // 人気度
        if ((statmask & OpsChangeStat.CS_POP.get()) > 0) {
            if (Version.GreaterOrEqual(Region.KMS, 197) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.GMS, 111)) {
                data.Encode4(chr.getFame());
            } else {
                data.Encode2(chr.getFame());
            }
        }
        // Meso
        if ((statmask & OpsChangeStat.CS_MONEY.get()) > 0) {
            if (Version.GreaterOrEqual(Region.KMS, 197)) {
                data.Encode8(chr.getMeso());
            } else {
                data.Encode4(chr.getMeso());
            }
        }
        // v188 ここから+1
        // Pet 2
        if ((statmask & OpsChangeStat.CS_PETSN2.get()) > 0) {
            MaplePet pet = chr.getPet(1);
            data.Encode8((pet != null && pet.getSummoned()) ? pet.getUniqueId() : 0);
        }
        // Pet 3
        if ((statmask & OpsChangeStat.CS_PETSN3.get()) > 0) {
            MaplePet pet = chr.getPet(2);
            data.Encode8((pet != null && pet.getSummoned()) ? pet.getUniqueId() : 0);
        }
        // 兵法書, GashaExp
        if ((statmask & OpsChangeStat.CS_TEMPEXP.get()) > 0) {
            data.Encode4(chr.getGashaEXP());
        }

        return data.get().getBytes();
    }
}
