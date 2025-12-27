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
package tacos.script;

/**
 *
 * @author Riremito
 */
public enum TacosScriptType {
    PORTAL("portal/"),
    TELEPORT,
    NPC("npc/"),
    QUEST("quest/"),
    REACOTR("reactor/"),
    EVENT("event/"),
    UNKNOWN;
    private String folder_name;

    TacosScriptType() {
        this.folder_name = "test/";
    }

    TacosScriptType(String folder_name) {
        this.folder_name = folder_name;
    }

    public String get() {
        return this.folder_name;
    }

}
