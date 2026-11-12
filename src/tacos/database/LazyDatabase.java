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
package tacos.database;

import tacos.database.query.DQ_Root;
import tacos.client.TacosCharacter;
import tacos.debug.DebugLogger;
import java.util.ArrayList;
import tacos.script.portal.TacosSharedPortal.SharedPortalType;

/**
 *
 * @author Riremito
 */
public class LazyDatabase {

    public static boolean loadData(TacosCharacter chr) {
        ArrayList<LazyData> lazy_data_list = chr.getLazyDataList();
        lazy_data_list.clear();
        for (LazyDataNames ldn : LazyDataNames.values()) {
            if (ldn.getType() == LazyDataTypes.UNKNOWN) {
                continue;
            }
            LazyData ld = new LazyData(ldn);
            if (DQ_Root.get(chr, ld)) {
                ld.setOk(true);
            }
            lazy_data_list.add(ld);
        }

        for (LazyData ld : lazy_data_list) {
            if (!ld.getOk()) {
                continue;
            }
            DebugLogger.DebugLog("LazyDB load : " + ld.getDataName().name() + " = " + ld.getInt() + ", \"" + ld.getStr() + "\"");
            switch (ld.getDataName()) {
                case PET_ITEM_HP -> {
                    chr.setPetAutoHPItem(ld.getInt());
                }
                case PET_ITEM_MP -> {
                    chr.setPetAutoMPItem(ld.getInt());
                }
                case PET_ITEM_CURE -> {
                    chr.setPetAutoCureItem(ld.getInt());
                }
                case RETURN_MAP_FREEMARKET -> {
                    if (ld.getInt() != 0) {
                        chr.getSharedPortal(SharedPortalType.FREE_MARKET).setReturnMapId(ld.getInt());
                    }
                }
                case RETURN_PORTAL_FREEMARKET -> {
                    if (!ld.getStr().isEmpty()) {
                        chr.getSharedPortal(SharedPortalType.FREE_MARKET).setReturnPortalName(ld.getStr());
                    }
                }
                case RETURN_MAP_PACHINKO -> {
                    if (ld.getInt() != 0) {
                        chr.getSharedPortal(SharedPortalType.PACHINKO_HALL).setReturnMapId(ld.getInt());
                    }
                }
                case RETURN_PORTAL_PACHINKO -> {
                    if (!ld.getStr().isEmpty()) {
                        chr.getSharedPortal(SharedPortalType.PACHINKO_HALL).setReturnPortalName(ld.getStr());
                    }
                }
                case RETURN_MAP_ARDENTMILL -> {
                    if (ld.getInt() != 0) {
                        chr.getSharedPortal(SharedPortalType.MEISTER_VILLE).setReturnMapId(ld.getInt());
                    }
                }
                case RETURN_PORTAL_ARDENTMILL -> {
                    if (!ld.getStr().isEmpty()) {
                        chr.getSharedPortal(SharedPortalType.MEISTER_VILLE).setReturnPortalName(ld.getStr());
                    }
                }
                default -> {
                }
            }
        }

        return true;
    }

    public static boolean saveData(TacosCharacter chr) {
        ArrayList<LazyData> lazy_data_list = chr.getLazyDataList();

        for (LazyData ld : lazy_data_list) {
            int value_int = 0;
            String value_str = "";
            switch (ld.getDataName()) {
                case PET_ITEM_HP -> {
                    value_int = chr.getPetAutoHPItem();
                }
                case PET_ITEM_MP -> {
                    value_int = chr.getPetAutoMPItem();
                }
                case PET_ITEM_CURE -> {
                    value_int = chr.getPetAutoCureItem();
                }
                case RETURN_MAP_FREEMARKET -> {
                    value_int = chr.getSharedPortal(SharedPortalType.FREE_MARKET).getReturnMapId();
                }
                case RETURN_PORTAL_FREEMARKET -> {
                    value_str = chr.getSharedPortal(SharedPortalType.FREE_MARKET).getReturnPortalName();
                }
                case RETURN_MAP_PACHINKO -> {
                    value_int = chr.getSharedPortal(SharedPortalType.PACHINKO_HALL).getReturnMapId();
                }
                case RETURN_PORTAL_PACHINKO -> {
                    value_str = chr.getSharedPortal(SharedPortalType.PACHINKO_HALL).getReturnPortalName();
                }
                case RETURN_MAP_ARDENTMILL -> {
                    value_int = chr.getSharedPortal(SharedPortalType.MEISTER_VILLE).getReturnMapId();
                }
                case RETURN_PORTAL_ARDENTMILL -> {
                    value_str = chr.getSharedPortal(SharedPortalType.MEISTER_VILLE).getReturnPortalName();
                }
                default -> {
                }
            }
            switch (ld.getDataName().getType()) {
                case TYPE_INT -> {
                    if (ld.getInt() != value_int) {
                        ld.setInt(value_int);
                        DebugLogger.DebugLog("LazyDB save : " + ld.getDataName().name() + " = " + ld.getInt() + ", \"" + ld.getStr() + "\"");
                        if (ld.getOk()) {
                            DQ_Root.updateInt(chr, ld);
                        } else {
                            DQ_Root.setInt(chr, ld);
                            ld.setOk(true);
                        }
                    }
                }
                case TYPE_STR -> {
                    if (!ld.getStr().equals(value_str)) {
                        ld.setStr(value_str);
                        DebugLogger.DebugLog("LazyDB save : " + ld.getDataName().name() + " = " + ld.getInt() + ", \"" + ld.getStr() + "\"");
                        if (ld.getOk()) {
                            DQ_Root.updateStr(chr, ld);
                        } else {
                            DQ_Root.setStr(chr, ld);
                            ld.setOk(true);
                        }
                    }
                }
                default -> {
                }
            }
        }

        return true;
    }
}
