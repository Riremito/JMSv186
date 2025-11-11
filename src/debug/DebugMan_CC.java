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
package debug;

import odin.client.MapleCharacter;
import data.wz.ids.DWI_LoadXML;
import java.util.ArrayList;

/**
 *
 * @author Riremito
 */
public class DebugMan_CC extends DebugMan implements IDebugMan {

    @Override
    public boolean start(MapleCharacter chr) {
        super.start(this, chr);
        return true;
    }

    @Override
    public boolean end(MapleCharacter chr) {
        super.end(chr);
        return true;
    }

    private enum AvatarType {
        SKIN,
        FACE,
        FACE_COLOR,
        HAIR,
        HAIR_COLOR,
        UNKNWON;
    }

    public ArrayList<Integer> getIdsTest(MapleCharacter chr, int answer) {
        ArrayList<Integer> ids = new ArrayList<>();
        if (answer == AvatarType.SKIN.ordinal()) {
            for (int skin_id : DWI_LoadXML.getSkin().getIds()) {
                ids.add(skin_id); // already filtered by list.
            }
            return ids;
        }
        if (answer == AvatarType.FACE.ordinal()) {
            int chr_face_color = chr.getFace() / 100 % 10;
            int chr_face_gender = chr.getFace() / 1000 % 10;
            for (int id : DWI_LoadXML.getFace().getIds()) {
                int id_color = id / 100 % 10;
                int id_gender = id / 1000 % 10;
                if (id_color == chr_face_color && id_gender == chr_face_gender) {
                    ids.add(id);
                }
            }
            return ids;
        }
        if (answer == AvatarType.FACE_COLOR.ordinal()) {
            int chr_face_base = chr.getFace() % 100;
            int chr_face_gender = chr.getFace() / 1000 % 10;
            for (int id : DWI_LoadXML.getFace().getIds()) {
                int id_base = id % 100;
                int id_gender = id / 1000 % 10;
                if (id_base == chr_face_base && id_gender == chr_face_gender) {
                    ids.add(id);
                }
            }
            return ids;
        }
        if (answer == AvatarType.HAIR.ordinal()) {
            int chr_hair_color = chr.getHair() % 10;
            int chr_hair_gender = (chr.getHair() % 30000) / 1000;
            for (int id : DWI_LoadXML.getHair().getIds()) {
                int id_color = id % 10;
                int id_gender = id % 30000 / 1000;
                if (id_color == chr_hair_color && id_gender == chr_hair_gender) {
                    ids.add(id);
                }
            }
            return ids;
        }
        if (answer == AvatarType.HAIR_COLOR.ordinal()) {
            int chr_hair_base = chr.getHair() - (chr.getHair() % 10);
            int chr_hair_gender = (chr.getHair() % 30000) / 1000;
            for (int id : DWI_LoadXML.getHair().getIds()) {
                int id_base = id - (id % 10);
                int id_gender = id % 30000 / 1000;
                if (id_base == chr_hair_base && id_gender == chr_hair_gender) {
                    ids.add(id);
                }
            }
            return ids;
        }
        return null;
    }

    private ArrayList<Integer> ids_selection = null;
    private int avatar_type = -1;

    @Override
    public boolean action(MapleCharacter chr, int status, int answer) {
        switch (status) {
            case 0: {
                NpcTag nt = new NpcTag();
                nt.addMenu(AvatarType.SKIN.ordinal(), "Skin");
                nt.addMenu(AvatarType.FACE.ordinal(), "Face");
                nt.addMenu(AvatarType.FACE_COLOR.ordinal(), "Face Color");
                nt.addMenu(AvatarType.HAIR.ordinal(), "Hair");
                nt.addMenu(AvatarType.HAIR_COLOR.ordinal(), "Hair Color");
                super.askMenu(chr, nt);
                return true;
            }
            case 1: {
                avatar_type = answer;
                NpcTag nt = new NpcTag();
                if (answer == AvatarType.SKIN.ordinal()) {
                    nt.add("Skin test.");
                    ids_selection = getIdsTest(chr, answer);
                    super.askAvatar(chr, nt, ids_selection);
                    return true;
                }
                if (answer == AvatarType.FACE.ordinal()) {
                    nt.add("Face test.");
                    ids_selection = getIdsTest(chr, answer);
                    super.askAvatar(chr, nt, ids_selection);
                    return true;
                }
                if (answer == AvatarType.FACE_COLOR.ordinal()) {
                    nt.add("Face Color test.");
                    ids_selection = getIdsTest(chr, answer);
                    super.askAvatar(chr, nt, ids_selection);
                    return true;
                }
                if (answer == AvatarType.HAIR.ordinal()) {
                    nt.add("Hair test.");
                    ids_selection = getIdsTest(chr, answer);
                    super.askAvatar(chr, nt, ids_selection);
                    return true;
                }
                if (answer == AvatarType.HAIR_COLOR.ordinal()) {
                    nt.add("Hair Color test.");
                    ids_selection = getIdsTest(chr, answer);
                    super.askAvatar(chr, nt, ids_selection);
                    return true;
                }
                return false;
            }
            case 2: {
                if (ids_selection == null) {
                    return false;
                }
                if (answer < 0 || ids_selection.size() < answer) {
                    return false;
                }
                int id = ids_selection.get(answer);
                if (avatar_type == AvatarType.SKIN.ordinal()) {
                    chr.DebugMsg("Skin : " + id);
                    chr.setSkinColor((byte) id);
                    chr.UpdateStat(false);
                    return false;
                }
                if (avatar_type == AvatarType.FACE.ordinal() || avatar_type == AvatarType.FACE_COLOR.ordinal()) {
                    chr.DebugMsg("Face : " + id);
                    chr.setFace(id);
                    chr.UpdateStat(false);
                    return false;
                }
                if (avatar_type == AvatarType.HAIR.ordinal() || avatar_type == AvatarType.HAIR_COLOR.ordinal()) {
                    chr.DebugMsg("Hair : " + id);
                    chr.setHair(id);
                    chr.UpdateStat(false);
                    return false;
                }
                return false;
            }
            default: {
                break;
            }
        }

        return false;
    }

}
