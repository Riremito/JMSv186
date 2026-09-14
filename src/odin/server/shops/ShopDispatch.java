/*
 * Copyright (C) 2026 Riremito
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
package odin.server.shops;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import tacos.packet.ServerPacket;

/**
 * AbstractPlayerStoreを HiredMerchant / MaplePlayerShop / MapleMiniGame の3クラスへ
 * 統合(フラット化)したことに伴い、共通の店舗操作をObject型経由でinstanceof分岐して行うための
 * ディスパッチヘルパー。
 * 新しく店舗として扱う型を追加した場合は、このクラスの各メソッドにも分岐を追加すること。
 *
 * @author Riremito
 */
public final class ShopDispatch {

    private ShopDispatch() {
    }

    public static byte getShopType(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getShopType();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getShopType();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getShopType();
        }
        throw new IllegalArgumentException("getShopType: unknown shop type: " + o);
    }

    public static int getMaxSize(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getMaxSize();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getMaxSize();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getMaxSize();
        }
        throw new IllegalArgumentException("getMaxSize: unknown shop type: " + o);
    }

    public static int getSize(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getSize();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getSize();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getSize();
        }
        throw new IllegalArgumentException("getSize: unknown shop type: " + o);
    }

    public static void broadcastToVisitors(Object o, ServerPacket packet) {
        if (o instanceof HiredMerchant) {
            ((HiredMerchant) o).broadcastToVisitors(packet);
        } else if (o instanceof MaplePlayerShop) {
            ((MaplePlayerShop) o).broadcastToVisitors(packet);
        } else if (o instanceof MapleMiniGame) {
            ((MapleMiniGame) o).broadcastToVisitors(packet);
        } else {
            throw new IllegalArgumentException("broadcastToVisitors: unknown shop type: " + o);
        }
    }

    public static int getMeso(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getMeso();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getMeso();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getMeso();
        }
        throw new IllegalArgumentException("getMeso: unknown shop type: " + o);
    }

    public static void setMeso(Object o, int meso) {
        if (o instanceof HiredMerchant) {
            ((HiredMerchant) o).setMeso(meso);
        } else if (o instanceof MaplePlayerShop) {
            ((MaplePlayerShop) o).setMeso(meso);
        } else if (o instanceof MapleMiniGame) {
            ((MapleMiniGame) o).setMeso(meso);
        } else {
            throw new IllegalArgumentException("setMeso: unknown shop type: " + o);
        }
    }

    public static void setOpen(Object o, boolean open) {
        if (o instanceof HiredMerchant) {
            ((HiredMerchant) o).setOpen(open);
        } else if (o instanceof MaplePlayerShop) {
            ((MaplePlayerShop) o).setOpen(open);
        } else if (o instanceof MapleMiniGame) {
            ((MapleMiniGame) o).setOpen(open);
        } else {
            throw new IllegalArgumentException("setOpen: unknown shop type: " + o);
        }
    }

    public static boolean isOpen(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).isOpen();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).isOpen();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).isOpen();
        }
        throw new IllegalArgumentException("isOpen: unknown shop type: " + o);
    }

    public static void update(Object o) {
        if (o instanceof HiredMerchant) {
            ((HiredMerchant) o).update();
        } else if (o instanceof MaplePlayerShop) {
            ((MaplePlayerShop) o).update();
        } else if (o instanceof MapleMiniGame) {
            ((MapleMiniGame) o).update();
        } else {
            throw new IllegalArgumentException("update: unknown shop type: " + o);
        }
    }

    public static void addVisitor(Object o, MapleCharacter visitor) {
        if (o instanceof HiredMerchant) {
            ((HiredMerchant) o).addVisitor(visitor);
        } else if (o instanceof MaplePlayerShop) {
            ((MaplePlayerShop) o).addVisitor(visitor);
        } else if (o instanceof MapleMiniGame) {
            ((MapleMiniGame) o).addVisitor(visitor);
        } else {
            throw new IllegalArgumentException("addVisitor: unknown shop type: " + o);
        }
    }

    public static void removeVisitor(Object o, MapleCharacter visitor) {
        if (o instanceof HiredMerchant) {
            ((HiredMerchant) o).removeVisitor(visitor);
        } else if (o instanceof MaplePlayerShop) {
            ((MaplePlayerShop) o).removeVisitor(visitor);
        } else if (o instanceof MapleMiniGame) {
            ((MapleMiniGame) o).removeVisitor(visitor);
        } else {
            throw new IllegalArgumentException("removeVisitor: unknown shop type: " + o);
        }
    }

    public static byte getVisitorSlot(Object o, MapleCharacter visitor) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getVisitorSlot(visitor);
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getVisitorSlot(visitor);
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getVisitorSlot(visitor);
        }
        throw new IllegalArgumentException("getVisitorSlot: unknown shop type: " + o);
    }

    public static void removeAllVisitors(Object o, int error, int type) {
        if (o instanceof HiredMerchant) {
            ((HiredMerchant) o).removeAllVisitors(error, type);
        } else if (o instanceof MaplePlayerShop) {
            ((MaplePlayerShop) o).removeAllVisitors(error, type);
        } else if (o instanceof MapleMiniGame) {
            ((MapleMiniGame) o).removeAllVisitors(error, type);
        } else {
            throw new IllegalArgumentException("removeAllVisitors: unknown shop type: " + o);
        }
    }

    public static String getOwnerName(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getOwnerName();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getOwnerName();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getOwnerName();
        }
        throw new IllegalArgumentException("getOwnerName: unknown shop type: " + o);
    }

    public static String getDescription(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getDescription();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getDescription();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getDescription();
        }
        throw new IllegalArgumentException("getDescription: unknown shop type: " + o);
    }

    public static List<SimpleImmutableEntry<Byte, MapleCharacter>> getVisitors(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getVisitors();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getVisitors();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getVisitors();
        }
        throw new IllegalArgumentException("getVisitors: unknown shop type: " + o);
    }

    public static List<MaplePlayerShopItem> getItems(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getItems();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getItems();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getItems();
        }
        throw new IllegalArgumentException("getItems: unknown shop type: " + o);
    }

    public static void addItem(Object o, MaplePlayerShopItem item) {
        if (o instanceof HiredMerchant) {
            ((HiredMerchant) o).addItem(item);
        } else if (o instanceof MaplePlayerShop) {
            ((MaplePlayerShop) o).addItem(item);
        } else if (o instanceof MapleMiniGame) {
            ((MapleMiniGame) o).addItem(item);
        } else {
            throw new IllegalArgumentException("addItem: unknown shop type: " + o);
        }
    }

    public static void removeFromSlot(Object o, int slot) {
        if (o instanceof HiredMerchant) {
            ((HiredMerchant) o).removeFromSlot(slot);
        } else if (o instanceof MaplePlayerShop) {
            ((MaplePlayerShop) o).removeFromSlot(slot);
        } else if (o instanceof MapleMiniGame) {
            ((MapleMiniGame) o).removeFromSlot(slot);
        } else {
            throw new IllegalArgumentException("removeFromSlot: unknown shop type: " + o);
        }
    }

    public static byte getFreeSlot(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getFreeSlot();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getFreeSlot();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getFreeSlot();
        }
        throw new IllegalArgumentException("getFreeSlot: unknown shop type: " + o);
    }

    public static int getItemId(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getItemId();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getItemId();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getItemId();
        }
        throw new IllegalArgumentException("getItemId: unknown shop type: " + o);
    }

    public static boolean isOwner(Object o, MapleCharacter chr) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).isOwner(chr);
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).isOwner(chr);
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).isOwner(chr);
        }
        throw new IllegalArgumentException("isOwner: unknown shop type: " + o);
    }

    public static String getPassword(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getPassword();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getPassword();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getPassword();
        }
        throw new IllegalArgumentException("getPassword: unknown shop type: " + o);
    }

    public static MapleCharacter getMCOwner(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getMCOwner();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getMCOwner();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getMCOwner();
        }
        throw new IllegalArgumentException("getMCOwner: unknown shop type: " + o);
    }

    public static int getGameType(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).getGameType();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).getGameType();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).getGameType();
        }
        throw new IllegalArgumentException("getGameType: unknown shop type: " + o);
    }

    public static boolean isAvailable(Object o) {
        if (o instanceof HiredMerchant) {
            return ((HiredMerchant) o).isAvailable();
        } else if (o instanceof MaplePlayerShop) {
            return ((MaplePlayerShop) o).isAvailable();
        } else if (o instanceof MapleMiniGame) {
            return ((MapleMiniGame) o).isAvailable();
        }
        throw new IllegalArgumentException("isAvailable: unknown shop type: " + o);
    }

    public static void setAvailable(Object o, boolean b) {
        if (o instanceof HiredMerchant) {
            ((HiredMerchant) o).setAvailable(b);
        } else if (o instanceof MaplePlayerShop) {
            ((MaplePlayerShop) o).setAvailable(b);
        } else if (o instanceof MapleMiniGame) {
            ((MapleMiniGame) o).setAvailable(b);
        } else {
            throw new IllegalArgumentException("setAvailable: unknown shop type: " + o);
        }
    }

    public static void buy(Object o, TacosClient client, int item, short quantity) {
        if (o instanceof HiredMerchant) {
            ((HiredMerchant) o).buy(client, item, quantity);
        } else if (o instanceof MaplePlayerShop) {
            ((MaplePlayerShop) o).buy(client, item, quantity);
        } else if (o instanceof MapleMiniGame) {
            ((MapleMiniGame) o).buy(client, item, quantity);
        } else {
            throw new IllegalArgumentException("buy: unknown shop type: " + o);
        }
    }

    public static void closeShop(Object o, boolean saveItems, boolean remove, int reason) {
        if (o instanceof HiredMerchant) {
            ((HiredMerchant) o).closeShop(saveItems, remove, reason);
        } else if (o instanceof MaplePlayerShop) {
            ((MaplePlayerShop) o).closeShop(saveItems, remove, reason);
        } else if (o instanceof MapleMiniGame) {
            ((MapleMiniGame) o).closeShop(saveItems, remove, reason);
        } else {
            throw new IllegalArgumentException("closeShop: unknown shop type: " + o);
        }
    }
}
