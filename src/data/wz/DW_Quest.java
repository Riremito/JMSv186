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

import config.Content;
import odin.provider.MapleData;
import odin.provider.MapleDataProvider;

/**
 *
 * @author Riremito
 */
public class DW_Quest {

    private static DataWz wz = null;

    private static DataWz getWz() {
        if (wz == null) {
            wz = new DataWz(Content.Wz_SingleFile.get() ? "Data.wz/Quest" : "Quest.wz");
        }
        return wz;
    }

    private static MapleDataProvider getWzRoot() {
        return getWz().getWzRoot();
    }

    private static MapleData img_Act = null;
    private static MapleData img_Check = null;
    private static MapleData img_QuestInfo = null;
    private static MapleData img_PQuest = null;

    public static MapleData getAct() {
        if (img_Act == null) {
            img_Act = getWz().loadData("Act.img");
        }
        return img_Act;
    }

    public static MapleData getCheck() {
        if (img_Check == null) {
            img_Check = getWz().loadData("Check.img");
        }
        return img_Check;
    }

    public static MapleData getQuestInfo() {
        if (img_QuestInfo == null) {
            img_QuestInfo = getWz().loadData("QuestInfo.img");
        }
        return img_QuestInfo;
    }

    public static MapleData getPQuest() {
        if (img_PQuest == null) {
            img_PQuest = getWz().loadData("PQuest.img");
        }
        return img_PQuest;
    }

}
