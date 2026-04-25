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
package tacos.wz.data;

import tacos.wz.TacosWz;
import tacos.config.Content;
import java.util.HashMap;
import java.util.Map;
import odin.server.maps.MapleReactorStats;
import tacos.odin.OdinPair;
import odin.tools.StringUtil;
import odin.provider.IMapleData;
import tacos.wz.TacosWzDataTool;

/**
 *
 * @author Riremito
 */
public class ReactorWz extends TacosWz {

    private static ReactorWz wz = null;

    public static ReactorWz get() {
        if (wz == null) {
            wz = new ReactorWz(Content.Wz_SingleFile.get() ? "Data.wz/Reactor" : "Reactor.wz");
        }

        return wz;
    }

    public ReactorWz(String path) {
        super(path);
    }

    private Map<Integer, MapleReactorStats> map_reactorStats = null;

    public MapleReactorStats getReactor(int rid) {
        if (map_reactorStats == null) {
            map_reactorStats = new HashMap<>();
        }
        MapleReactorStats mrs_found = map_reactorStats.get(rid);
        if (mrs_found != null) {
            return mrs_found;
        }

        int infoId = rid;
        IMapleData reactorData = getData(StringUtil.getLeftPaddedStr(Integer.toString(infoId) + ".img", '0', 11));
        IMapleData link = reactorData.getChildByPath("info/link");
        if (link != null) {
            infoId = TacosWzDataTool.getIntPath("info/link", reactorData, 0);
            MapleReactorStats mrs_link = map_reactorStats.get(infoId);
            if (mrs_link != null) {
                map_reactorStats.put(rid, mrs_link);
                return mrs_link;
            }
        }

        MapleReactorStats stats = new MapleReactorStats();
        reactorData = getData(StringUtil.getLeftPaddedStr(Integer.toString(infoId) + ".img", '0', 11));
        if (reactorData == null) {
            return stats;
        }
        boolean areaSet = false;
        boolean foundState = false;
        for (byte i = 0; true; i++) {
            IMapleData reactorD = reactorData.getChildByPath(String.valueOf(i));
            if (reactorD == null) {
                break;
            }
            IMapleData reactorInfoData_ = reactorD.getChildByPath("event");
            if (reactorInfoData_ != null && reactorInfoData_.getChildByPath("0") != null) {
                IMapleData reactorInfoData = reactorInfoData_.getChildByPath("0");
                OdinPair<Integer, Integer> reactItem = null;
                int type = TacosWzDataTool.getIntPath("type", reactorInfoData, 0);
                if (type == 100) { //reactor waits for item
                    reactItem = new OdinPair<>(TacosWzDataTool.getIntPath("0", reactorInfoData, 0), TacosWzDataTool.getIntPath("1", reactorInfoData, 1));
                    if (!areaSet) { //only set area of effect for item-triggered reactors once
                        stats.setTL(TacosWzDataTool.getPoint(reactorInfoData.getChildByPath("lt")));
                        stats.setBR(TacosWzDataTool.getPoint(reactorInfoData.getChildByPath("rb")));
                        areaSet = true;
                    }
                }
                foundState = true;
                stats.addState(i, type, reactItem, (byte) TacosWzDataTool.getIntPath("state", reactorInfoData, 0), TacosWzDataTool.getIntPath("timeOut", reactorInfoData_, -1));
            } else {
                stats.addState(i, 999, null, (byte) (foundState ? -1 : (i + 1)), 0);
            }
        }
        map_reactorStats.put(infoId, stats);

        if (rid != infoId) {
            map_reactorStats.put(rid, stats);
        }

        return stats;
    }

}
