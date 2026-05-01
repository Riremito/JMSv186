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

/**
 *
 * @author Riremito
 */
public class TacosSkillPet extends TacosObject {

    private int skill_id;

    public TacosSkillPet(TacosCharacter chr, int skill_id) {
        this.skill_id = skill_id;
        super(chr);
    }

    public int getSkillId() {
        return this.skill_id;
    }
}
