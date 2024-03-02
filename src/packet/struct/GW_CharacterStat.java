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
package packet.struct;

import client.MapleCharacter;
import client.PlayerStats;
import client.inventory.MaplePet;
import config.ServerConfig;
import constants.GameConstants;
import packet.server.ServerPacket;

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
        if (194 <= ServerConfig.version) {
            // something inserted
            Flag.PET2.set(0x00100000);
            Flag.PET3.set(0x00200000);
            Flag.GASHAEXP.set(0x00400000);
        }
    }

    // GW_CharacterStat::Decode
    // CharStats
    public static byte[] Encode(MapleCharacter chr) {
        ServerPacket p = new ServerPacket();

        p.Encode4(chr.getId());
        p.EncodeBuffer(chr.getName(), (ServerConfig.IsJMS() || ServerConfig.IsCMS()) ? 13 : 15);
        p.Encode1(chr.getGender());
        p.Encode1(chr.getSkinColor());
        p.Encode4(chr.getFace());
        p.Encode4(chr.getHair());

        if (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 131) {
            p.EncodeZeroBytes(8);
        } else {
            p.EncodeZeroBytes(24);
        }

        p.Encode1(chr.getLevel());
        p.Encode2(chr.getJob());

        PlayerStats stat = chr.getStat();

        p.Encode2(stat.str);
        p.Encode2(stat.dex);
        p.Encode2(stat.int_);
        p.Encode2(stat.luk);

        // BB前
        if (ServerConfig.IsPreBB()) {
            p.Encode2(stat.hp);
            p.Encode2(stat.maxhp);
            p.Encode2(stat.mp);
            p.Encode2(stat.maxmp);
        } else {
            // BB後 (v187+)
            p.Encode4(stat.hp);
            p.Encode4(stat.maxhp);
            p.Encode4(stat.mp);
            p.Encode4(stat.maxmp);
        }

        p.Encode2(chr.getRemainingAp());

        // SP
        if (((ServerConfig.IsJMS() && 180 < ServerConfig.GetVersion()) || ServerConfig.IsTWMS() || ServerConfig.IsCMS()) && (GameConstants.isEvan(chr.getJob()) || GameConstants.isResist(chr.getJob()))) {
            final int size = chr.getRemainingSpSize();
            p.Encode1(size);
            for (int i = 0; i < chr.getRemainingSps().length; i++) {
                if (chr.getRemainingSp(i) > 0) {
                    p.Encode1(i + 1);
                    p.Encode1(chr.getRemainingSp(i));
                }
            }
        } else {
            p.Encode2(chr.getRemainingSp());
        }

        p.Encode4(chr.getExp());
        p.Encode2(chr.getFame());

        if (ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion()
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            p.Encode4(chr.getGashaEXP()); // Gachapon exp
        }

        if (ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            p.Encode8(0);
        }

        p.Encode4(chr.getMapId()); // current map id
        p.Encode1(chr.getInitialSpawnpoint()); // spawnpoint

        if (ServerConfig.IsCMS()) {
            p.Encode2(chr.getSubcategory());
        } else if (ServerConfig.IsTWMS()) {
            p.Encode2(chr.getSubcategory());
            p.EncodeZeroBytes(25);
            p.Encode1(0);
            p.Encode1(0);
            p.Encode1(0);
            p.Encode1(0);
            p.Encode1(0);
        } else if ((ServerConfig.IsJMS() && 180 <= ServerConfig.GetVersion())) {
            // デュアルブレイドフラグ
            p.Encode2(chr.getSubcategory());
            if (ServerConfig.IsJMS() && 188 <= ServerConfig.GetVersion()) {
                // v194 OK
                p.Encode8(0);
                p.Encode4(0);
                p.Encode4(0);
            } else {
                p.Encode8(0);
                p.Encode4(0);
                p.Encode4(0);
                p.Encode4(0);
            }
        } else {
            // v164, v165
            p.Encode8(0);
            p.Encode4(0);
            p.Encode4(0);
        }

        return p.Get().getBytes();
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
        ServerPacket p = new ServerPacket();

        p.Encode4(statmask);

        // Skin
        if ((statmask & Flag.SKIN.get()) > 0) {
            p.Encode1(chr.getSkinColor());
        }
        // Face
        if ((statmask & Flag.FACE.get()) > 0) {
            p.Encode4(chr.getFace());
        }
        // Hair
        if ((statmask & Flag.HAIR.get()) > 0) {
            p.Encode4(chr.getHair());
        }
        // Pet 1
        if ((statmask & Flag.PET1.get()) > 0) {
            MaplePet pet = chr.getPet(0);
            p.Encode8((pet != null) ? pet.getUniqueId() : 0);
        }
        // Level
        if ((statmask & Flag.LEVEL.get()) > 0) {
            p.Encode1(chr.getLevel());
        }
        // Job
        if ((statmask & Flag.JOB.get()) > 0) {
            p.Encode2(chr.getJob());
        }
        // STR
        if ((statmask & Flag.STR.get()) > 0) {
            p.Encode2(chr.getStat().getStr());
        }
        // DEX
        if ((statmask & Flag.DEX.get()) > 0) {
            p.Encode2(chr.getStat().getDex());
        }
        // INT
        if ((statmask & Flag.INT.get()) > 0) {
            p.Encode2(chr.getStat().getInt());
        }
        // LUK
        if ((statmask & Flag.LUK.get()) > 0) {
            p.Encode2(chr.getStat().getLuk());
        }
        // HP
        if ((statmask & Flag.HP.get()) > 0) {
            if (ServerConfig.version <= 186) {
                p.Encode2(chr.getStat().getHp());
            } else {
                p.Encode4(chr.getStat().getHp());
            }
        }
        // MAXHP
        if ((statmask & Flag.MAXHP.get()) > 0) {
            if (ServerConfig.version <= 186) {
                p.Encode2(chr.getStat().getMaxHp());
            } else {
                p.Encode4(chr.getStat().getMaxHp());
            }
        }
        // MP
        if ((statmask & Flag.MP.get()) > 0) {
            if (ServerConfig.version <= 186) {
                p.Encode2(chr.getStat().getMp());
            } else {
                p.Encode4(chr.getStat().getMp());
            }
        }
        // MAXMP
        if ((statmask & Flag.MAXMP.get()) > 0) {
            if (ServerConfig.version <= 186) {
                p.Encode2(chr.getStat().getMaxMp());
            } else {
                p.Encode4(chr.getStat().getMaxMp());
            }
        }
        // AP
        if ((statmask & Flag.AP.get()) > 0) {
            p.Encode2(chr.getRemainingAp());
        }
        // SP
        if ((statmask & Flag.SP.get()) > 0) {
            if (GameConstants.isEvan(chr.getJob()) || GameConstants.isResist(chr.getJob())) {
                p.Encode1(chr.getRemainingSpSize());
                for (int i = 0; i < chr.getRemainingSps().length; i++) {
                    if (chr.getRemainingSp(i) > 0) {
                        p.Encode1(i + 1);
                        p.Encode1(chr.getRemainingSp(i));
                    }
                }
            } else {
                p.Encode2(chr.getRemainingSp());
            }
        }
        // EXP
        if ((statmask & Flag.EXP.get()) > 0) {
            p.Encode4(chr.getExp());
        }
        // 人気度
        if ((statmask & Flag.FAME.get()) > 0) {
            p.Encode2(chr.getFame());
        }
        // Meso
        if ((statmask & Flag.MESO.get()) > 0) {
            p.Encode4(chr.getMeso());
        }
        // v188 ここから+1
        // Pet 2
        if ((statmask & Flag.PET2.get()) > 0) {
            MaplePet pet = chr.getPet(0);
            p.Encode8((pet != null) ? pet.getUniqueId() : 0);
        }
        // Pet 3
        if ((statmask & Flag.PET3.get()) > 0) {
            MaplePet pet = chr.getPet(0);
            p.Encode8((pet != null) ? pet.getUniqueId() : 0);
        }
        // 兵法書, GashaExp
        if ((statmask & Flag.GASHAEXP.get()) > 0) {
            p.Encode4(chr.getGashaEXP());
        }

        return p.Get().getBytes();
    }
}
