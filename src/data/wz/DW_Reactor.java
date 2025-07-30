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
package data.wz;

import java.util.HashMap;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import server.maps.MapleReactorStats;
import tools.Pair;
import tools.StringUtil;

/**
 *
 * @author Riremito
 */
public class DW_Reactor {

    private static DataWz wz = null;

    private static DataWz getWz() {
        if (wz == null) {
            wz = new DataWz("Reactor.wz");
        }
        return wz;
    }

    private static MapleDataProvider getWzRoot() {
        return getWz().getWzRoot();
    }
    private static Map<Integer, MapleReactorStats> map_reactorStats = null;

    public static MapleReactorStats getReactor(int rid) {
        if (map_reactorStats == null) {
            map_reactorStats = new HashMap<>();
        }
        MapleReactorStats mrs_found = map_reactorStats.get(rid);
        if (mrs_found != null) {
            return mrs_found;
        }

        int infoId = rid;
        MapleData reactorData = getWzRoot().getData(StringUtil.getLeftPaddedStr(Integer.toString(infoId) + ".img", '0', 11));
        MapleData link = reactorData.getChildByPath("info/link");
        if (link != null) {
            infoId = MapleDataTool.getIntConvert("info/link", reactorData);
            MapleReactorStats mrs_link = map_reactorStats.get(infoId);
            if (mrs_link != null) {
                map_reactorStats.put(rid, mrs_link);
                return mrs_link;
            }
        }

        MapleReactorStats stats = new MapleReactorStats();
        reactorData = getWzRoot().getData(StringUtil.getLeftPaddedStr(Integer.toString(infoId) + ".img", '0', 11));
        if (reactorData == null) {
            return stats;
        }
        boolean areaSet = false;
        boolean foundState = false;
        for (byte i = 0; true; i++) {
            MapleData reactorD = reactorData.getChildByPath(String.valueOf(i));
            if (reactorD == null) {
                break;
            }
            MapleData reactorInfoData_ = reactorD.getChildByPath("event");
            if (reactorInfoData_ != null && reactorInfoData_.getChildByPath("0") != null) {
                MapleData reactorInfoData = reactorInfoData_.getChildByPath("0");
                Pair<Integer, Integer> reactItem = null;
                int type = MapleDataTool.getIntConvert("type", reactorInfoData);
                if (type == 100) { //reactor waits for item
                    reactItem = new Pair<>(MapleDataTool.getIntConvert("0", reactorInfoData), MapleDataTool.getIntConvert("1", reactorInfoData, 1));
                    if (!areaSet) { //only set area of effect for item-triggered reactors once
                        stats.setTL(MapleDataTool.getPoint("lt", reactorInfoData));
                        stats.setBR(MapleDataTool.getPoint("rb", reactorInfoData));
                        areaSet = true;
                    }
                }
                foundState = true;
                stats.addState(i, type, reactItem, (byte) MapleDataTool.getIntConvert("state", reactorInfoData), MapleDataTool.getIntConvert("timeOut", reactorInfoData_, -1));
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
