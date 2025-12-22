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
import odin.provider.IMapleData;
import odin.provider.IMapleDataProvider;

/**
 *
 * @author Riremito
 */
public class QuestWz {

    private static TacosWz wz = null;

    private static TacosWz getWz() {
        if (wz == null) {
            wz = new TacosWz(Content.Wz_SingleFile.get() ? "Data.wz/Quest" : "Quest.wz");
        }
        return wz;
    }

    private static IMapleDataProvider getWzRoot() {
        return getWz().getWzRoot();
    }

    private static IMapleData img_Act = null;
    private static IMapleData img_Check = null;
    private static IMapleData img_QuestInfo = null;
    private static IMapleData img_PQuest = null;

    public static IMapleData getAct() {
        if (img_Act == null) {
            img_Act = getWz().getData("Act.img");
        }
        return img_Act;
    }

    public static IMapleData getCheck() {
        if (img_Check == null) {
            img_Check = getWz().getData("Check.img");
        }
        return img_Check;
    }

    public static IMapleData getQuestInfo() {
        if (img_QuestInfo == null) {
            img_QuestInfo = getWz().getData("QuestInfo.img");
        }
        return img_QuestInfo;
    }

    public static IMapleData getPQuest() {
        if (img_PQuest == null) {
            img_PQuest = getWz().getData("PQuest.img");
        }
        return img_PQuest;
    }

}
