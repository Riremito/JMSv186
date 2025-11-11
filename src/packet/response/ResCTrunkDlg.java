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
package packet.response;

import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import config.Region;
import config.ServerConfig;
import config.Version;
import server.network.MaplePacket;
import java.util.Collection;
import packet.ServerPacket;
import packet.ops.OpsTrunk;
import packet.response.data.DataGW_ItemSlotBase;

/**
 *
 * @author Riremito
 */
public class ResCTrunkDlg {

    // アイテムを入れる
    // storeStorage
    public static MaplePacket ItemIn(byte slots, MapleInventoryType type, Collection<IItem> items) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TrunkResult);
        sp.Encode1(OpsTrunk.TrunkRes_PutSuccess.get());
        sp.Encode1(slots);
        sp.Encode2(type.getBitfieldEncoding());
        if (ServerConfig.JMS164orLater()) {
            sp.Encode2(0);
            sp.Encode4(0);
        }
        sp.Encode1((byte) items.size());
        for (IItem item : items) {
            sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(item));
        }
        return sp.get();
    }

    public static MaplePacket Error(OpsTrunk ops) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TrunkResult);
        sp.Encode1(ops.get());
        return sp.get();
    }

    // アイテムを取り出す
    // takeOutStorage
    public static MaplePacket ItemOut(byte slots, MapleInventoryType type, Collection<IItem> items) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TrunkResult);
        sp.Encode1(OpsTrunk.TrunkRes_GetSuccess.get());
        sp.Encode1(slots);
        sp.Encode2(type.getBitfieldEncoding());
        if (ServerConfig.JMS164orLater()) {
            sp.Encode2(0);
            sp.Encode4(0);
        }
        sp.Encode1((byte) items.size());
        for (IItem item : items) {
            sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(item));
        }
        return sp.get();
    }

    // メルの出し入れ
    public static MaplePacket MesoInOut(byte slots, int meso) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TrunkResult);
        sp.Encode1(OpsTrunk.TrunkRes_PutSuccess.get());
        sp.Encode1(slots);
        sp.Encode2(2);
        if (ServerConfig.JMS164orLater()) {
            sp.Encode2(0);
            sp.Encode4(0);
        }
        sp.Encode4(meso);
        return sp.get();
    }

    // 倉庫を開く
    // getStorage
    public static MaplePacket Open(int npcId, byte slots, Collection<IItem> items, int meso) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TrunkResult);
        if (Version.LessOrEqual(Region.KMS, 31)) {
            // may be other operation uses different header value
        } else {
            sp.Encode1(OpsTrunk.TrunkRes_OpenTrunkDlg.get());
        }
        sp.Encode4(npcId);
        sp.Encode1(slots);

        if (Version.LessOrEqual(Region.KMS, 31) || Version.LessOrEqual(Region.JMS, 131)) {
            sp.Encode2(126);
        } else {
            sp.Encode8(126);
        }

        sp.Encode4(meso);
        sp.Encode1(0);
        sp.Encode1(0);
        sp.Encode1((byte) items.size());
        for (IItem item : items) {
            sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(item));
        }
        sp.Encode1(0);
        sp.Encode1(0);
        return sp.get();
    }

}
