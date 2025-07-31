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
package data.wz.ids;

/**
 *
 * @author Riremito
 */
public class DWI_Validation {

    // Character.wz
    public static boolean isValidFaceID(int id) {
        return DWI_LoadXML.getFace().isValidID(id);
    }

    // Character.wz
    public static boolean isValidSkinID(int id) {
        return DWI_LoadXML.getSkin().isValidID(id);
    }

    // Character.wz
    public static boolean isValidHairID(int id) {
        return DWI_LoadXML.getHair().isValidID(id);
    }

    // Skill.wz
    public static boolean isValidJobID(int id) {
        // KMS001
        if (id == 0) {
            return true;
        }
        return DWI_LoadXML.getJob().isValidID(id);
    }

    // Item.wz, Character.wz
    public static boolean isValidItemID(int id) {
        return DWI_LoadXML.getItem().isValidID(id);
    }

    // Map.wz
    public static boolean isValidMapID(int id) {
        return DWI_LoadXML.getMap().isValidID(id);
    }

    // NPC.wz
    public static boolean isValidNPCID(int id) {
        return DWI_LoadXML.getNpc().isValidID(id);
    }

    // Mob.wz
    public static boolean isValidMobID(int id) {
        return DWI_LoadXML.getMob().isValidID(id);
    }

    // Reactor.wz
    public static boolean isValidReactorID(int id) {
        return DWI_LoadXML.reactorids.contains(id);
    }

}
