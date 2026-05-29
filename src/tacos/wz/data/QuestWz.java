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

import tacos.wz.WzXML;
import tacos.config.Content;
import odin.provider.IMapleData;

/**
 *
 * @author Riremito
 */
public class QuestWz extends WzXML {

    public QuestWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Quest" : "Quest.wz");
    }

    public IMapleData getAct() {
        return getData("Act.img");
    }

    public IMapleData getCheck() {
        return getData("Check.img");
    }

    public IMapleData getQuestInfo() {
        return getData("QuestInfo.img");
    }

    public IMapleData getPQuest() {
        return getData("PQuest.img");
    }
}
