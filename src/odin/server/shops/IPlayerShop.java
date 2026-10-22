/*
 * Copyright (C) 2025 Riremito
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
import odin.server.shops.MaplePlayerShop.MaplePlayerShopItem;
import tacos.client.TacosCharacter;
import tacos.packet.ServerPacket;

/**
 * The mini rooms a character can own or visit: TacosMerchant (entrusted shop), MaplePlayerShop (personal shop) and MapleMiniGame (omok, memory game).
 *
 * @author Riremito
 */
public interface IPlayerShop {

    byte getShopType();

    int getMaxSize();

    int getSize();

    void broadcastToVisitors(ServerPacket packet);

    int getMeso();

    void setMeso(int meso);

    void setOpen(boolean open);

    boolean isOpen();

    void addVisitor(TacosCharacter visitor);

    void removeVisitor(TacosCharacter visitor);

    byte getVisitorSlot(TacosCharacter visitor);

    void removeAllVisitors(int error, int type);

    String getOwnerName();

    String getDescription();

    List<SimpleImmutableEntry<Byte, TacosCharacter>> getVisitors();

    List<MaplePlayerShopItem> getItems();

    void addItem(MaplePlayerShopItem item);

    void removeFromSlot(int slot);

    byte getFreeSlot();

    int getItemId();

    boolean isOwner(TacosCharacter chr);

    String getPassword();

    TacosCharacter getMCOwner();

    int getGameType();

    boolean isAvailable();

    void setAvailable(boolean b);

    void buy(TacosCharacter chr, int item, short quantity);

    void closeShop(boolean saveItems, boolean remove, int reason);

    int getObjectId();
}
