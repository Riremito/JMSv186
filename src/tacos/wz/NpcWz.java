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

    public MapleData getImg(int npc_id) {
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
            MapleData md_info = md_npc_img.getChildByPath("info");
            if (md_info != null) {
                ret.guildRank = WzDataTool.getIntPath("guildRank", md_info, 0);
                ret.parcel = WzDataTool.getIntPath("parcel", md_info, 0);
                ret.rpsGame = WzDataTool.getIntPath("rpsGame", md_info, 0);
                ret.storebank = WzDataTool.getIntPath("storebank", md_info, 0);
                ret.trunkGet = WzDataTool.getIntPath("trunkGet", md_info, 0);
                ret.trunkPut = WzDataTool.getIntPath("trunkPut", md_info, 0);
                MapleData md_script = md_info.getChildByPath("script");
                if (md_script != null) {
                    ret.script = WzDataTool.getStringPath("0/script", md_script, "");
                }
            }
        }
        this.npc_info_list.put(npc_id, ret);
        return ret;
    }
}
