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
package tacos.wz;

import java.util.HashMap;
import lombok.Getter;
import odin.server.life.MapleNPC;
import tacos.config.Content;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class NpcWz extends WzXML {

    public NpcWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Npc" : "Npc.wz");
    }

    public MapleNPC createNpc(int npc_id) {
        if (!WzDataStorage.NPC.check(npc_id)) {
            DebugLogger.ErrorLog("createNpc : invalid id, " + npc_id);
            return null;
        }
        return new MapleNPC(npc_id);
    }

    private MapleData getImg(int npc_id) {
        String target_img_path = String.format("%07d.img", npc_id);
        return getData(target_img_path);
    }

    @Getter
    public class NpcInfo {

        private int guildRank;
        private int parcel;
        private int rpsGame;
        private String script;
        private int storebank;
        private int trunkGet;
        private int trunkPut;
    }

    private final HashMap<Integer, NpcInfo> npc_info_list = new HashMap<>();

    public NpcInfo getNpcInfo(int npc_id) {
        NpcInfo ret = this.npc_info_list.get(npc_id);
        if (ret != null) {
            return ret;
        }
        ret = new NpcInfo();

        MapleData md_npc_img = getImg(npc_id);
        if (md_npc_img != null) {
            ret.guildRank = md_npc_img.getInt("info/guildRank");
            ret.parcel = md_npc_img.getInt("info/parcel");
            ret.rpsGame = md_npc_img.getInt("info/rpsGame");
            ret.script = md_npc_img.getString("info/script/0/script");
            ret.storebank = md_npc_img.getInt("info/storebank");
            ret.trunkGet = md_npc_img.getInt("info/trunkGet");
            ret.trunkPut = md_npc_img.getInt("info/trunkPut");
        }

        this.npc_info_list.put(npc_id, ret);
        return ret;
    }
}
