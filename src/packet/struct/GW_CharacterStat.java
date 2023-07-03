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
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class GW_CharacterStat {

    // GW_CharacterStat::Decode
    // CharStats
    public static byte[] Encode(MapleCharacter chr) {
        ServerPacket p = new ServerPacket();

        p.Encode4(chr.getId());
        p.EncodeBuffer(chr.getName(), 13);
        p.Encode1(chr.getGender());
        p.Encode1(chr.getSkinColor());
        p.Encode4(chr.getFace());
        p.Encode4(chr.getHair());

        if (ServerConfig.version <= 131) {
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
        if (ServerConfig.version <= 186) {
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
        if (GameConstants.isEvan(chr.getJob()) || GameConstants.isResist(chr.getJob())) {
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

        if (164 <= ServerConfig.version) {
            p.Encode4(chr.getGashaEXP()); // Gachapon exp
        }

        p.Encode4(chr.getMapId()); // current map id
        p.Encode1(chr.getInitialSpawnpoint()); // spawnpoint
        if (ServerConfig.version > 176) {
            // デュアルブレイドフラグ
            p.Encode2(chr.getSubcategory());
            if (188 <= ServerConfig.version) {
                // v194 OK
                p.Encode8(0);
                p.Encode4(0);
                p.Encode4(0);
            } else {
                p.EncodeZeroBytes(20);
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
        if ((statmask & 1) > 0) {
            p.Encode1(chr.getSkinColor());
        }
        // Face
        if ((statmask & (1 << 1)) > 0) {
            p.Encode4(chr.getFace());
        }
        // Hair
        if ((statmask & (1 << 2)) > 0) {
            p.Encode4(chr.getHair());
        }
        // Pet 1
        if ((statmask & (1 << 3)) > 0) {
            MaplePet pet = chr.getPet(0);
            p.Encode8((pet != null) ? pet.getUniqueId() : 0);
        }
        // Level
        if ((statmask & (1 << 4)) > 0) {
            p.Encode1(chr.getLevel());
        }
        // Job
        if ((statmask & (1 << 5)) > 0) {
            p.Encode2(chr.getJob());
        }
        // STR
        if ((statmask & (1 << 6)) > 0) {
            p.Encode2(chr.getStat().getStr());
        }
        // DEX
        if ((statmask & (1 << 7)) > 0) {
            p.Encode2(chr.getStat().getDex());
        }
        // INT
        if ((statmask & (1 << 8)) > 0) {
            p.Encode2(chr.getStat().getInt());
        }
        // LUK
        if ((statmask & (1 << 9)) > 0) {
            p.Encode2(chr.getStat().getLuk());
        }
        // HP
        if ((statmask & (1 << 10)) > 0) {
            if (ServerConfig.version <= 186) {
                p.Encode2(chr.getStat().getHp());
            } else {
                p.Encode4(chr.getStat().getHp());
            }
        }
        // MAXHP
        if ((statmask & (1 << 11)) > 0) {
            if (ServerConfig.version <= 186) {
                p.Encode2(chr.getStat().getMaxHp());
            } else {
                p.Encode4(chr.getStat().getMaxHp());
            }
        }
        // MP
        if ((statmask & (1 << 12)) > 0) {
            if (ServerConfig.version <= 186) {
                p.Encode2(chr.getStat().getMp());
            } else {
                p.Encode4(chr.getStat().getMp());
            }
        }
        // MAXMP
        if ((statmask & (1 << 13)) > 0) {
            if (ServerConfig.version <= 186) {
                p.Encode2(chr.getStat().getMaxMp());
            } else {
                p.Encode4(chr.getStat().getMaxMp());
            }
        }
        // AP
        if ((statmask & (1 << 14)) > 0) {
            p.Encode2(chr.getRemainingAp());
        }
        // SP
        if ((statmask & (1 << 15)) > 0) {
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
        if ((statmask & (1 << 16)) > 0) {
            p.Encode4(chr.getExp());
        }
        // 人気度
        if ((statmask & (1 << 17)) > 0) {
            p.Encode2(chr.getFame());
        }
        // Meso
        if ((statmask & (1 << 18)) > 0) {
            p.Encode4(chr.getMeso());
        }
        // v188 ここから+1
        // Pet 2
        if ((statmask & (1 << 19)) > 0) {
            MaplePet pet = chr.getPet(0);
            p.Encode8((pet != null) ? pet.getUniqueId() : 0);
        }
        // Pet 3
        if ((statmask & (1 << 20)) > 0) {
            MaplePet pet = chr.getPet(0);
            p.Encode8((pet != null) ? pet.getUniqueId() : 0);
        }
        // 兵法書, GashaExp
        if ((statmask & (1 << 21)) > 0) {
            p.Encode4(chr.getGashaEXP());
        }

        return p.Get().getBytes();
    }
}
