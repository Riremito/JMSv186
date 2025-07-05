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
package packet.response.struct;

import packet.response.data.DataGW_ItemSlotBase;
import client.MapleCharacter;
import client.inventory.IEquip;
import client.inventory.IItem;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import constants.GameConstants;
import debug.Debug;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import server.movement.LifeMovementFragment;
import server.shops.AbstractPlayerStore;
import server.shops.IMaplePlayerShop;
import tools.KoreanDateUtil;
import tools.data.output.LittleEndianWriter;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class TestHelper {

    public static final byte[] unk1 = new byte[]{(byte) 0, (byte) 64, (byte) 224, (byte) 253};
    public static final long MAX_TIME = 150842304000000000L; //00 80 05 BB 46 E6 17 02
    private static final long FT_UT_OFFSET = 116444592000000000L; // EDT
    public static final byte[] unk2 = new byte[]{(byte) 59, (byte) 55, (byte) 79, (byte) 1};

    public static final long getTime(final long realTimestamp) {
        if (realTimestamp == -1) {
            return MAX_TIME;
        }
        long time = realTimestamp / 1000; // convert to seconds
        return (time * 10000000) + FT_UT_OFFSET;
    }

    public static final void addCharLook(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr, final boolean mega) {
        mplew.write(chr.getGender());
        mplew.write(chr.getSkinColor());
        mplew.writeInt(chr.getFace());
        mplew.write(mega ? 0 : 1);
        mplew.writeInt(chr.getHair());
        final Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        final Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);
        for (final IItem item : equip.list()) {
            if (item.getPosition() < -128) {
                //not visible
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
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(255); // end of visible itens
        // masked itens
        for (final Map.Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(255); // ending markers
        final IItem cWeapon = equip.getItem((byte) -111);
        mplew.writeInt(cWeapon != null ? cWeapon.getItemId() : 0);
        mplew.writeInt(0);
        if (ServerConfig.version >= 164) {
            mplew.writeLong(0);
        }
    }

    public static final void addExpirationTime(final MaplePacketLittleEndianWriter mplew, final long time) {
        mplew.write(0);
        mplew.writeShort(1408); // 80 05
        if (time != -1) {
            mplew.writeInt(KoreanDateUtil.getItemTimestamp(time));
            mplew.write(1);
        } else {
            mplew.writeInt(400967355);
            mplew.write(2);
        }
    }

    public static final void serializeMovementList(final LittleEndianWriter lew, final List<LifeMovementFragment> moves) {
        lew.write(moves.size());
        for (LifeMovementFragment move : moves) {
            move.serialize(lew);
        }
    }

    public static final void addItemInfo(final MaplePacketLittleEndianWriter mplew, final IItem item, final boolean zeroPosition, final boolean leaveOut) {
        if (zeroPosition && leaveOut) {
            mplew.write(DataGW_ItemSlotBase.Encode(item));
            return;
        }
        Debug.ErrorLog("!!! addItemInfo, old");
        addItemInfo(mplew, item, zeroPosition, leaveOut, false);
    }

    public static final void addItemInfo(final MaplePacketLittleEndianWriter mplew, final IItem item, final boolean zeroPosition, final boolean leaveOut, final boolean trade) {
        short pos = item.getPosition();
        if (zeroPosition) {
            if (!leaveOut) {
                mplew.write(0);
            }
        } else {
            if (pos <= -1) {
                pos *= -1;
                if (pos > 100 && pos < 1000) {
                    pos -= 100;
                }
            }
            if (!trade && item.getType() == 1) {
                mplew.writeShort(pos);
            } else {
                mplew.write(pos);
            }
        }
        mplew.write(item.getPet() != null ? 3 : item.getType());
        mplew.writeInt(item.getItemId());
        boolean hasUniqueId = item.getUniqueId() > 0;
        //marriage rings arent cash items so dont have uniqueids, but we assign them anyway for the sake of rings
        mplew.write(hasUniqueId ? 1 : 0);
        if (hasUniqueId) {
            mplew.writeLong(item.getUniqueId());
        }
        if (item.getPet() != null) {
            // Pet
            //addPetItemInfo(mplew, item, item.getPet());
        } else {
            addExpirationTime(mplew, item.getExpiration());
            if (item.getType() == 1) {
                final IEquip equip = (IEquip) item;
                mplew.write(equip.getUpgradeSlots());
                mplew.write(equip.getLevel());
                //                mplew.write(0);
                mplew.writeShort(equip.getStr());
                mplew.writeShort(equip.getDex());
                mplew.writeShort(equip.getInt());
                mplew.writeShort(equip.getLuk());
                mplew.writeShort(equip.getHp());
                mplew.writeShort(equip.getMp());
                mplew.writeShort(equip.getWatk());
                mplew.writeShort(equip.getMatk());
                mplew.writeShort(equip.getWdef());
                mplew.writeShort(equip.getMdef());
                mplew.writeShort(equip.getAcc());
                mplew.writeShort(equip.getAvoid());
                mplew.writeShort(equip.getHands());
                mplew.writeShort(equip.getSpeed());
                mplew.writeShort(equip.getJump());
                mplew.writeMapleAsciiString(equip.getOwner());
                // ポイントアイテムの一度も装備していないことを確認するためのフラグ
                if (hasUniqueId) {
                    // ポイントアイテム交換可能
                    mplew.writeShort(16);
                } else {
                    mplew.writeShort(equip.getFlag());
                }
                mplew.write(0);
                mplew.write(Math.max(equip.getBaseLevel(), equip.getEquipLevel())); // Item level
                if (hasUniqueId) {
                    mplew.write(unk1);
                } else {
                    mplew.writeShort(0);
                    mplew.writeShort(equip.getExpPercentage() * 4); // Item Exp... 98% = 25%
                }
                mplew.writeInt(equip.getDurability());
                if (ServerConfig.game_server_enable_hammer) {
                    mplew.writeInt(equip.getViciousHammer());
                } else {
                    mplew.writeInt(0);
                }
                if (!hasUniqueId) {
                    mplew.write(equip.getHidden()); //7 = unique for the lulz
                    mplew.write(equip.getEnhance());
                    if (ServerConfig.game_server_enable_potential) {
                        mplew.writeShort(equip.getPotential1()); //potential stuff 1. total damage
                        mplew.writeShort(equip.getPotential2()); //potential stuff 2. critical rate
                        mplew.writeShort(equip.getPotential3()); //potential stuff 3. all stats
                    } else {
                        mplew.writeShort(0);
                        mplew.writeShort(0);
                        mplew.writeShort(0);
                    }
                }
                mplew.writeShort(equip.getHpR());
                mplew.writeShort(equip.getMpR());
                mplew.writeLong(0); //some tracking ID
                mplew.write(unk1);
                mplew.write(unk2);
                mplew.writeInt(-1);
            } else {
                mplew.writeShort(item.getQuantity());
                mplew.writeMapleAsciiString(item.getOwner());
                mplew.writeShort(item.getFlag());
                if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                    mplew.writeInt(2);
                    mplew.writeShort(84);
                    mplew.write(0);
                    mplew.write(52);
                }
            }
        }
    }

    public static final void addInteraction(final MaplePacketLittleEndianWriter mplew, IMaplePlayerShop shop) {
        mplew.write(shop.getGameType());
        mplew.writeInt(((AbstractPlayerStore) shop).getObjectId());
        mplew.writeMapleAsciiString(shop.getDescription());
        if (shop.getShopType() != 1) {
            mplew.write(shop.getPassword().length() > 0 ? 1 : 0); //password = false
        }
        mplew.write(shop.getItemId() % 10);
        mplew.write(shop.getSize()); //current size
        mplew.write(shop.getMaxSize()); //full slots... 4 = 4-1=3 = has slots, 1-1=0 = no slots
        if (shop.getShopType() != 1) {
            mplew.write(shop.isOpen() ? 0 : 1);
        }
    }

    public static final void addAnnounceBox(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr) {
        if (chr.getPlayerShop() != null && chr.getPlayerShop().isOwner(chr) && chr.getPlayerShop().getShopType() != 1 && chr.getPlayerShop().isAvailable()) {
            addInteraction(mplew, chr.getPlayerShop());
        } else {
            mplew.write(0);
        }
    }

    public static final long getKoreanTimestamp(final long realTimestamp) {
        if (realTimestamp == -1) {
            return MAX_TIME;
        }
        long time = realTimestamp / 1000 / 60; // convert to minutes
        return (time * 600000000) + FT_UT_OFFSET;
    }

}
