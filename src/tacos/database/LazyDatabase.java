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

import odin.client.MapleCharacter;
import tacos.database.query.DQ_Root;
import tacos.debug.DebugLogger;
import java.util.ArrayList;

/**
 *
 * @author Riremito
 */
public class LazyDatabase {

    public static boolean loadData(MapleCharacter chr) {
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
                case PET_ITEM_HP: {
                    chr.setPetAutoHPItem(ld.getInt());
                    break;
                }
                case PET_ITEM_MP: {
                    chr.setPetAutoMPItem(ld.getInt());
                    break;
                }
                case PET_ITEM_CURE: {
                    chr.setPetAutoCureItem(ld.getInt());
                    break;
                }
                case RETURN_MAP_FREEMARKET: {
                    chr.getFreeMarketPortal().setReturnMapId(ld.getInt());
                    break;
                }
                case RETURN_PORTAL_FREEMARKET: {
                    chr.getFreeMarketPortal().setReturnPortalName(ld.getStr());
                    break;
                }
                case RETURN_MAP_ARDENTMILL: {
                    chr.getArdentmillPortal().setReturnMapId(ld.getInt());
                    break;
                }
                case RETURN_PORTAL_ARDENTMILL: {
                    chr.getArdentmillPortal().setReturnPortalName(ld.getStr());
                    break;
                }
                default: {
                    break;
                }
            }
        }

        return true;
    }

    public static boolean saveData(MapleCharacter chr) {
        ArrayList<LazyData> lazy_data_list = chr.getLazyDataList();

        for (LazyData ld : lazy_data_list) {
            int value_int = 0;
            String value_str = "";
            switch (ld.getDataName()) {
                case PET_ITEM_HP: {
                    value_int = chr.getPetAutoHPItem();
                    break;
                }
                case PET_ITEM_MP: {
                    value_int = chr.getPetAutoMPItem();
                    break;
                }
                case PET_ITEM_CURE: {
                    value_int = chr.getPetAutoCureItem();
                    break;
                }
                case RETURN_MAP_FREEMARKET: {
                    value_int = chr.getFreeMarketPortal().getReturnMapId();
                    break;
                }
                case RETURN_PORTAL_FREEMARKET: {
                    value_str = chr.getFreeMarketPortal().getReturnPortalName();
                    break;
                }
                case RETURN_MAP_ARDENTMILL: {
                    value_int = chr.getArdentmillPortal().getReturnMapId();
                    break;
                }
                case RETURN_PORTAL_ARDENTMILL: {
                    value_str = chr.getArdentmillPortal().getReturnPortalName();
                    break;
                }
                default: {
                    break;
                }
            }
            switch (ld.getDataName().getType()) {
                case TYPE_INT: {
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
                    break;
                }
                case TYPE_STR: {
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
                    break;
                }
                default: {
                    break;
                }
            }
        }

        return true;
    }

}
