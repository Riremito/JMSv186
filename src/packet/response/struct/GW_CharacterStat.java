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
package packet.response.struct;

import client.MapleCharacter;
import client.PlayerStats;
import client.inventory.MaplePet;
import config.ServerConfig;
import constants.GameConstants;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class GW_CharacterStat {

    public enum Flag {
        // v186
        // 0x00000001
        SKIN(1),
        // 0x00000002
        FACE(1 << 1),
        // 0x00000004
        HAIR(1 << 2),
        // 0x00000008
        PET1(1 << 3),
        // 0x00000010
        LEVEL(1 << 4),
        // 0x00000020
        JOB(1 << 5),
        // 0x00000040
        STR(1 << 6),
        // 0x00000080
        DEX(1 << 7),
        // 0x00000100
        INT(1 << 8),
        // 0x00000200
        LUK(1 << 9),
        // 0x00000400
        HP(1 << 10),
        // 0x00000800
        MAXHP(1 << 11),
        // 0x00001000
        MP(1 << 12),
        // 0x00002000
        MAXMP(1 << 13),
        // 0x00004000
        AP(1 << 14),
        // 0x00008000
        SP(1 << 15),
        // 0x00010000
        EXP(1 << 16),
        // 0x00020000
        FAME(1 << 17),
        // 0x00040000
        MESO(1 << 18),
        // 0x00080000
        PET2(1 << 19),
        // 0x00100000
        PET3(1 << 20),
        // 0x00200000
        GASHAEXP(1 << 21),
        UNKNOWN;

        private int value;

        Flag(int flag) {
            value = flag;
        }

        Flag() {
            value = -1;
        }

        public boolean set(int flag) {
            value = flag;
            return true;
        }

        public int get() {
            return value;
        }
    }

    public static void Init() {
        if ((ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion())) {
            // something inserted
            Flag.PET2.set(0x00100000);
            Flag.PET3.set(0x00200000);
            Flag.GASHAEXP.set(0x00400000);
        }
    }

    // GW_CharacterStat::Decode
    // CharStats
    public static byte[] Encode(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        data.Encode4(chr.getId());
        data.EncodeBuffer(chr.getName(), ServerConfig.GetCharacterNameSize());
        data.Encode1(chr.getGender());
        data.Encode1(chr.getSkinColor());
        data.Encode4(chr.getFace());
        data.Encode4(chr.getHair());

        if (ServerConfig.JMS131orEarlier() || ServerConfig.KMS95orEarlier()) {
            data.EncodeZeroBytes(8);
        } else if ((ServerConfig.IsJMS() || ServerConfig.IsCMS() || (ServerConfig.IsTWMS() && ServerConfig.IsPreBB()))) {
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
        if (ServerConfig.IsPreBB()) {
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
        if (ServerConfig.JMS186orLater()) {
            // is_extendsp_job
            if (GameConstants.is_extendsp_job(chr.getJob())) {
                final int size = chr.getRemainingSpSize();
                // ExtendSP::Decode
                data.Encode1(size);
                for (int i = 0; i < chr.getRemainingSps().length; i++) {
                    if (chr.getRemainingSp(i) > 0) {
                        data.Encode1(i + 1);
                        data.Encode1(chr.getRemainingSp(i));
                    }
                }
            } else {
                data.Encode2(chr.getRemainingSp());
            }
        } else {
            data.Encode2(chr.getRemainingSp());
        }

        if (ServerConfig.JMS302orLater()) {
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(chr.getMapId());
            data.Encode1(chr.getInitialSpawnpoint());
            data.Encode2(chr.getSubcategory());
            // job 3100 -> Encode4, Demon Slayer
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
            data.EncodeZeroBytes(12);
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
            data.EncodeZeroBytes(8);
            data.Encode4(0);
            data.Encode4(0);
            return data.Get().getBytes();
        }

        data.Encode4(chr.getExp());
        data.Encode2(chr.getFame());

        if ((ServerConfig.IsJMS() || ServerConfig.IsCMS() || ServerConfig.IsTWMS() || ServerConfig.IsEMS())
                && ServerConfig.JMS164orLater()) {
            data.Encode4(chr.getGashaEXP()); // Gachapon exp
        }

        if (ServerConfig.IsTWMS() || ServerConfig.IsCMS() || ServerConfig.IsEMS()) {
            data.Encode8(0);
        }

        data.Encode4(chr.getMapId()); // current map id
        data.Encode1(chr.getInitialSpawnpoint()); // spawnpoint

        if (ServerConfig.IsCMS() || ServerConfig.IsKMS() || ServerConfig.IsEMS()) {
            data.Encode2(chr.getSubcategory());
        } else if (ServerConfig.IsTWMS()) {
            data.Encode2(chr.getSubcategory());
            data.EncodeZeroBytes(25);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
            data.Encode1(0);
        } else if ((ServerConfig.JMS180orLater())) {
            // デュアルブレイドフラグ
            data.Encode2(chr.getSubcategory());
            if (ServerConfig.IsPostBB()) {
                if (ServerConfig.IsJMS() && ServerConfig.GetVersion() == 187) {
                    data.Encode4(0);
                    data.Encode8(0);
                    data.Encode4(0);
                    data.Encode4(0);
                } else {
                    // v194 OK
                    data.Encode8(0);
                    data.Encode4(0);
                    data.Encode4(0);
                }
            } else {
                data.Encode8(0);
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
            }
        } else {
            // v164, v165
            data.Encode8(0);
            data.Encode4(0);
            data.Encode4(0);
        }

        return data.Get().getBytes();
    }

    // GW_CharacterStat::DecodeMoney
    public static byte[] EncodeMoney(MapleCharacter chr) {
        ServerPacket p = new ServerPacket();

        p.Encode4(chr.getMeso());
        return p.Get().getBytes();
    }

    // DecodeBuffer size 0x0C
    public static byte[] EncodePachinko(MapleCharacter chr) {
        ServerPacket p = new ServerPacket();

        p.Encode4(chr.getId());
        p.Encode4(chr.getTama());
        p.Encode4(0);
        return p.Get().getBytes();
    }

    // GW_CharacterStat::DecodeChangeStat
    // GW_CharacterStat::EncodeChangeStat
    public static byte[] EncodeChangeStat(MapleCharacter chr, int statmask) {
        ServerPacket data = new ServerPacket();

        if (ServerConfig.JMS302orLater()) {
            data.Encode8(statmask);
        } else {
            data.Encode4(statmask);
        }

        // Skin
        if ((statmask & Flag.SKIN.get()) > 0) {
            data.Encode1(chr.getSkinColor());
        }
        // Face
        if ((statmask & Flag.FACE.get()) > 0) {
            data.Encode4(chr.getFace());
        }
        // Hair
        if ((statmask & Flag.HAIR.get()) > 0) {
            data.Encode4(chr.getHair());
        }
        // Pet 1
        if ((statmask & Flag.PET1.get()) > 0) {
            MaplePet pet = chr.getPet(0);
            data.Encode8((pet != null && pet.getSummoned()) ? pet.getUniqueId() : 0);
        }
        // Level
        if ((statmask & Flag.LEVEL.get()) > 0) {
            data.Encode1(chr.getLevel());
        }
        // Job
        if ((statmask & Flag.JOB.get()) > 0) {
            data.Encode2(chr.getJob());
        }
        // STR
        if ((statmask & Flag.STR.get()) > 0) {
            data.Encode2(chr.getStat().getStr());
        }
        // DEX
        if ((statmask & Flag.DEX.get()) > 0) {
            data.Encode2(chr.getStat().getDex());
        }
        // INT
        if ((statmask & Flag.INT.get()) > 0) {
            data.Encode2(chr.getStat().getInt());
        }
        // LUK
        if ((statmask & Flag.LUK.get()) > 0) {
            data.Encode2(chr.getStat().getLuk());
        }
        // HP
        if ((statmask & Flag.HP.get()) > 0) {
            if (ServerConfig.IsPreBB()) {
                data.Encode2(chr.getStat().getHp());
            } else {
                data.Encode4(chr.getStat().getHp());
            }
        }
        // MAXHP
        if ((statmask & Flag.MAXHP.get()) > 0) {
            if (ServerConfig.IsPreBB()) {
                data.Encode2(chr.getStat().getMaxHp());
            } else {
                data.Encode4(chr.getStat().getMaxHp());
            }
        }
        // MP
        if ((statmask & Flag.MP.get()) > 0) {
            if (ServerConfig.IsPreBB()) {
                data.Encode2(chr.getStat().getMp());
            } else {
                data.Encode4(chr.getStat().getMp());
            }
        }
        // MAXMP
        if ((statmask & Flag.MAXMP.get()) > 0) {
            if (ServerConfig.IsPreBB()) {
                data.Encode2(chr.getStat().getMaxMp());
            } else {
                data.Encode4(chr.getStat().getMaxMp());
            }
        }
        // AP
        if ((statmask & Flag.AP.get()) > 0) {
            data.Encode2(chr.getRemainingAp());
        }
        // SP
        if ((statmask & Flag.SP.get()) > 0) {
            if (GameConstants.is_extendsp_job(chr.getJob())) {
                data.Encode1(chr.getRemainingSpSize());
                for (int i = 0; i < chr.getRemainingSps().length; i++) {
                    if (chr.getRemainingSp(i) > 0) {
                        data.Encode1(i + 1);
                        data.Encode1(chr.getRemainingSp(i));
                    }
                }
            } else {
                data.Encode2(chr.getRemainingSp());
            }
        }
        // EXP
        if ((statmask & Flag.EXP.get()) > 0) {
            data.Encode4(chr.getExp());
        }
        // 人気度
        if ((statmask & Flag.FAME.get()) > 0) {
            data.Encode2(chr.getFame());
        }
        // Meso
        if ((statmask & Flag.MESO.get()) > 0) {
            data.Encode4(chr.getMeso());
        }
        // v188 ここから+1
        // Pet 2
        if ((statmask & Flag.PET2.get()) > 0) {
            MaplePet pet = chr.getPet(1);
            data.Encode8((pet != null && pet.getSummoned()) ? pet.getUniqueId() : 0);
        }
        // Pet 3
        if ((statmask & Flag.PET3.get()) > 0) {
            MaplePet pet = chr.getPet(2);
            data.Encode8((pet != null && pet.getSummoned()) ? pet.getUniqueId() : 0);
        }
        // 兵法書, GashaExp
        if ((statmask & Flag.GASHAEXP.get()) > 0) {
            data.Encode4(chr.getGashaEXP());
        }

        return data.Get().getBytes();
    }
}
