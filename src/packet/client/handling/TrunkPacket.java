// 倉庫
package packet.client.handling;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import constants.GameConstants;
import debug.Debug;
import handling.MaplePacket;
import java.util.Collection;
import packet.client.ClientPacket;
import packet.server.ServerPacket;
import packet.server.response.struct.GW_ItemSlotBase;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStorage;
import tools.MaplePacketCreator;

public class TrunkPacket {

    // server side
    public enum CP_Flag {
        BEGIN,
        UNKNOWN(-1),
        ITEM_OUT(0x03),
        ITEM_IN(0x04),
        TRUNK_SORT(0x05), // アイテム整列
        MESO_INOUT(0x06),
        CLOSE(0x07),
        END;

        private int value;

        CP_Flag(int flag) {
            value = flag;
        }

        CP_Flag() {
            value = -1;
        }

        public boolean set(int flag) {
            value = flag;
            return true;
        }

        public int get() {
            return value;
        }

        public static CP_Flag get(int v) {
            for (final CP_Flag f : CP_Flag.values()) {
                if (f.get() == v) {
                    return f;
                }
            }
            return CP_Flag.UNKNOWN;
        }
    }

    // client side
    public enum SP_Flag {
        BEGIN,
        ITEM_OUT(0x08),
        ITEM_INVENTORY_FULL(0x09),
        NOT_ENOUGH_MESOS_OUT(0x0A),
        // 0x0B 固有アイテム, 1つしか所持できないアイテムのため探せませんでした。
        ITEM_IN(0x0C),
        // 0x0E v186では何かが存在
        NOT_ENOUGH_MESOS_IN(0x0F),
        TRUNK_INVENTORY_FULL(0x10),
        MESO_INOUT(0x12),
        OPEN(0x15),
        ERROR(-1), // 存在しないフラグは全て"エラーが発生して取引出来ませんでした。"と表示される仕様
        END;

        private int value;

        SP_Flag(int flag) {
            value = flag;
        }

        SP_Flag() {
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
        if (ServerConfig.version <= 131) {
            // SS
            CP_Flag.ITEM_OUT.set(0x03);
            CP_Flag.ITEM_IN.set(0x04);
            CP_Flag.MESO_INOUT.set(0x05);
            CP_Flag.CLOSE.set(0x06);
            // CS
            SP_Flag.ITEM_OUT.set(0x07);
            SP_Flag.ITEM_INVENTORY_FULL.set(0x08);
            SP_Flag.NOT_ENOUGH_MESOS_OUT.set(0x09);
            SP_Flag.ITEM_IN.set(0x0A);
            SP_Flag.NOT_ENOUGH_MESOS_IN.set(0x0C);
            SP_Flag.TRUNK_INVENTORY_FULL.set(0x0D);
            SP_Flag.MESO_INOUT.set(0x0F);
            SP_Flag.OPEN.set(0x12);
        }
    }

    // Storage
    // CP_UserTrunkRequest
    public static boolean OnPacket(ClientPacket p, MapleClient c) {
        MapleCharacter chr = c.getPlayer();

        if (chr == null) {
            return false;
        }

        CP_Flag mode = CP_Flag.get(p.Decode1());

        final MapleStorage storage = chr.getStorage();

        switch (mode) {
            case ITEM_OUT: {
                // 手数料不足
                /*
                if (chr.getMeso() < 100) {
                    c.SendPacket(TrunkPacket.Error(SP_Flag.NOT_ENOUGH_MESOS_OUT));
                    return false;
                }
                 */

                final byte type = p.Decode1();
                final byte slot = storage.getSlot(MapleInventoryType.getByType(type), p.Decode1());
                final IItem item = storage.takeOut(slot); // 取り出す?

                if (item == null) {
                    return false;
                }

                if (!MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                    storage.store(item); // 戻す?
                    c.SendPacket(TrunkPacket.Error(SP_Flag.ITEM_INVENTORY_FULL));
                    return false;
                }

                //chr.gainMeso(-100, false, true, false);
                MapleInventoryManipulator.addFromDrop(c, item, false);
                storage.sendTakenOut(c, GameConstants.getInventoryType(item.getItemId()));
                return true;
            }
            case ITEM_IN: {
                // 手数料不足
                if (chr.getMeso() < 100) {
                    c.SendPacket(TrunkPacket.Error(SP_Flag.NOT_ENOUGH_MESOS_IN));
                    return false;
                }

                final byte slot = (byte) p.Decode2();
                final int itemId = p.Decode4();
                short quantity = p.Decode2();
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

                // packet hack
                if (quantity < 1) {
                    c.SendPacket(TrunkPacket.Error(SP_Flag.ERROR));
                    return false;
                }

                // 空きスロットがない
                if (storage.isFull()) {
                    c.SendPacket(TrunkPacket.Error(SP_Flag.TRUNK_INVENTORY_FULL));
                    return false;
                }

                MapleInventoryType type = GameConstants.getInventoryType(itemId);
                IItem item = chr.getInventory(type).getItem(slot).copy();

                if (GameConstants.isPet(item.getItemId())) {
                    c.SendPacket(TrunkPacket.Error(SP_Flag.ERROR));
                    return false;
                }

                final byte flag = item.getFlag();
                if (ii.isPickupRestricted(item.getItemId()) && storage.findById(item.getItemId()) != null) {
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return false;
                }

                if (item.getItemId() == itemId && (item.getQuantity() >= quantity || GameConstants.isThrowingStar(itemId) || GameConstants.isBullet(itemId))) {
                    if (ii.isDropRestricted(item.getItemId())) {
                        if (ItemFlag.KARMA_EQ.check(flag)) {
                            item.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                        } else if (ItemFlag.KARMA_USE.check(flag)) {
                            item.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
                        } else {
                            c.getSession().write(MaplePacketCreator.enableActions());
                            return false;
                        }
                    }
                    if (GameConstants.isThrowingStar(itemId) || GameConstants.isBullet(itemId)) {
                        quantity = item.getQuantity();
                    }
                    chr.gainMeso(-100, false, true, false);
                    MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
                    item.setQuantity(quantity);
                    storage.store(item);
                } else {
                    // ?
                    return false;
                }

                storage.sendStored(c, GameConstants.getInventoryType(itemId));
                return true;
            }
            case MESO_INOUT: {
                int meso = p.Decode4();
                final int storageMesos = storage.getMeso();
                final int playerMesos = chr.getMeso();

                // packet hack
                if (meso == 0) {
                    c.SendPacket(TrunkPacket.Error(SP_Flag.ERROR));
                    return false;
                }

                // packet hack
                if (meso <= 0 && (storageMesos - meso < 0)) {
                    c.SendPacket(TrunkPacket.Error(SP_Flag.ERROR));
                    return false;
                }

                // packet hack
                if (meso > 0 && (playerMesos + meso < 0)) {
                    c.SendPacket(TrunkPacket.Error(SP_Flag.ERROR));
                    return false;
                }

                storage.setMeso(storageMesos - meso);
                chr.gainMeso(meso, false, true, false);
                storage.sendMeso(c);
                return true;
            }
            case CLOSE: {
                storage.close();
                chr.setConversation(0);
                return true;
            }
            default: {
                Debug.ErrorLog("TrunkPacket");
                Debug.PacketLog(p);
                break;
            }
        }

        return false;
    }

    // 倉庫を開く
    // getStorage
    public static MaplePacket Open(int npcId, byte slots, Collection<IItem> items, int meso) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_TrunkResult);

        p.Encode1(SP_Flag.OPEN.get());
        p.Encode4(npcId);
        p.Encode1(slots);
        p.Encode2(0x7E);

        if (ServerConfig.version > 131) {
            p.Encode2(0);
            p.Encode4(0);
        }

        p.Encode4(meso);
        p.Encode2(0);

        p.Encode1((byte) items.size());
        for (IItem item : items) {
            p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
        }

        p.Encode2(0);
        p.Encode1(0); // v131 no
        p.Encode1(0); // v131 no

        return p.Get();
    }

    public static MaplePacket Error(SP_Flag error) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_TrunkResult);

        p.Encode1(error.get());
        return p.Get();
    }

    // メルの出し入れ
    public static MaplePacket MesoInOut(byte slots, int meso) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_TrunkResult);

        p.Encode1(SP_Flag.MESO_INOUT.get());
        p.Encode1(slots);
        p.Encode2(2);

        if (ServerConfig.version > 131) {
            p.Encode2(0);
            p.Encode4(0);
        }

        p.Encode4(meso);

        return p.Get();
    }

    // アイテムを入れる
    // storeStorage
    public static MaplePacket ItemIn(byte slots, MapleInventoryType type, Collection<IItem> items) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_TrunkResult);

        p.Encode1(SP_Flag.ITEM_IN.get());
        p.Encode1(slots);
        p.Encode2(type.getBitfieldEncoding());

        if (ServerConfig.version > 131) {
            p.Encode2(0);
            p.Encode4(0);
        }

        p.Encode1((byte) items.size());
        for (IItem item : items) {
            p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
        }

        return p.Get();
    }

    // アイテムを取り出す
    // takeOutStorage
    public static MaplePacket ItemOut(byte slots, MapleInventoryType type, Collection<IItem> items) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_TrunkResult);

        p.Encode1(SP_Flag.ITEM_OUT.get());
        p.Encode1(slots);
        p.Encode2(type.getBitfieldEncoding());

        if (ServerConfig.version > 131) {
            p.Encode2(0);
            p.Encode4(0);
        }

        p.Encode1((byte) items.size());
        for (IItem item : items) {
            p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
        }

        return p.Get();
    }

}
