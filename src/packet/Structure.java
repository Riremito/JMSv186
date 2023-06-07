package packet;

import client.MapleCharacter;
import client.PlayerStats;
import client.inventory.IItem;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import constants.GameConstants;
import java.util.LinkedHashMap;
import java.util.Map;

public class Structure {

    public static void CharEntry(ServerPacket p, final MapleCharacter chr, boolean ranking) {
        CharStats(p, chr);
        CharLook(p, chr, true);

        if (ServerConfig.version > 164) {
            p.Encode1(0);
        }

        p.Encode1(ranking ? 1 : 0);

        if (ranking) {
            p.Encode4(chr.getRank());
            p.Encode4(chr.getRankMove());
            p.Encode4(chr.getJobRank());
            p.Encode4(chr.getJobRankMove());
        }
    }

    public static void CharStats(ServerPacket p, final MapleCharacter chr) {
        p.Encode4(chr.getId());
        p.EncodeBuffer(chr.getName(), 13);
        p.Encode1(chr.getGender());
        p.Encode1(chr.getSkinColor());
        p.Encode4(chr.getFace());
        p.Encode4(chr.getHair());

        if (ServerConfig.version < 164) {
            p.EncodeZeroBytes(8);
        } else {
            p.EncodeZeroBytes(24);
        }

        p.Encode1(chr.getLevel());
        p.Encode2(chr.getJob());

        PlayerStats(p, chr); // connectData

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

        if (ServerConfig.version >= 164) {
            p.Encode4(0); // Gachapon exp
        }

        p.Encode4(chr.getMapId()); // current map id
        p.Encode1(chr.getInitialSpawnpoint()); // spawnpoint
        if (ServerConfig.version > 176) {
            // デュアルブレイドフラグ
            p.Encode2(chr.getSubcategory());
            if (ServerConfig.version >= 188) {
                p.Encode8(0);
                p.Encode4(0);
                p.Encode4(0);
            } else {
                p.EncodeZeroBytes(20);
            }
        } else {
            p.EncodeZeroBytes(16);
        }
    }

    public static void CharLook(ServerPacket p, final MapleCharacter chr, final boolean mega) {
        p.Encode1(chr.getGender());
        p.Encode1(chr.getSkinColor());
        p.Encode4(chr.getFace());
        p.Encode1(mega ? 0 : 1);
        p.Encode4(chr.getHair());

        final Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        final Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);

        for (final IItem item : equip.list()) {
            if (item.getPosition() < -128) { //not visible
                continue;
            }
            byte pos = (byte) (item.getPosition() * -1);

            if (pos < 100 && myEquip.get(pos) == null) {
                myEquip.put(pos, item.getItemId());
            } else if ((pos > 100 || pos == -128) && pos != 111) {
                pos = (byte) (pos == -128 ? 28 : pos - 100);
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, item.getItemId());
            } else if (myEquip.get(pos) != null) {
                maskedEquip.put(pos, item.getItemId());
            }
        }
        for (final Map.Entry<Byte, Integer> entry : myEquip.entrySet()) {
            p.Encode1(entry.getKey());
            p.Encode4(entry.getValue());
        }
        p.Encode1(0xFF); // end of visible itens
        // masked itens
        for (final Map.Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            p.Encode1(entry.getKey());
            p.Encode4(entry.getValue());
        }
        p.Encode1(0xFF); // ending markers

        final IItem cWeapon = equip.getItem((byte) -111);
        p.Encode4(cWeapon != null ? cWeapon.getItemId() : 0);
        p.Encode4(0);

        if (ServerConfig.version >= 164) {
            p.Encode8(0);
        }
    }

    public static void PlayerStats(ServerPacket p, MapleCharacter chr) {
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
            return;
        }

        // BB後
        p.Encode4(stat.hp);
        p.Encode4(stat.maxhp);
        p.Encode4(stat.mp);
        p.Encode4(stat.maxmp);
    }
}
