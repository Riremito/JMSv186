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
package tacos.client;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 *
 * @author Riremito
 */
@Data
public class TacosSummonSkill {

    @Setter(AccessLevel.NONE)
    private final int id;
    @Setter(AccessLevel.NONE)
    private final int level;
    // info
    private String hs = "";
    private int itemCon = 0;
    private int itemConNo = 0;
    private int mad = 0;
    private int mastery = 0;
    private int mobCount = 0;
    private int mpCon = 0;
    private int pad = 0;
    private int prop = 0;
    private int time = 0;
    private int x = 0;

    public TacosSummonSkill(int id, int level) {
        this.id = id;
        this.level = level;
    }
}
