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
package tacos.wz;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import odin.provider.IMapleData;
import odin.provider.IMapleDataDirectoryEntry;
import odin.provider.IMapleDataEntity;
import tacos.debug.DebugLoadTime;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class WzDataStorage {

    public static final WzDataStorage SKIN = new WzDataStorage(WzType.SKIN);
    public static final WzDataStorage FACE = new WzDataStorage(WzType.FACE);
    public static final WzDataStorage HAIR = new WzDataStorage(WzType.HAIR);
    public static final WzDataStorage JOB = new WzDataStorage(WzType.JOB);
    public static final WzDataStorage ITEM = new WzDataStorage(WzType.ITEM);
    public static final WzDataStorage MAP = new WzDataStorage(WzType.MAP);
    public static final WzDataStorage MOB = new WzDataStorage(WzType.MOB);
    public static final WzDataStorage NPC = new WzDataStorage(WzType.NPC);
    public static final WzDataStorage REACTOR = new WzDataStorage(WzType.REACTOR);
    public static final WzDataStorage SKILL = new WzDataStorage(WzType.SKILL);
    private static final Random RAND = new Random();

    private WzType type;
    private ArrayList<Integer> data = null;

    public WzDataStorage(WzType type) {
        this.type = type;
    }

    private boolean load() {
        if (this.data != null) {
            DebugLogger.ErrorLog("WzDataStorage load : already loaded, " + this.type);
            return false;
        }

        this.data = new ArrayList<>();

        DebugLoadTime dlt = new DebugLoadTime("WzDataStorage (" + this.type + ")");
        switch (this.type) {
            case SKIN: {
                // Character.wz/00002000.img
                Pattern pattern_skin_img = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.CHARACTER.getRootDirectory().getFiles()) {
                    Matcher matcher_skin_img = pattern_skin_img.matcher(dir.getName());
                    if (matcher_skin_img.matches()) {
                        int id = Integer.parseInt(matcher_skin_img.group(1)) % 100;
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case FACE: {
                // Character.wz/Face/00020000.img
                Pattern pattern_face_img = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.CHARACTER.getSubDirectoryFiles("Face")) {
                    Matcher matcher_face_img = pattern_face_img.matcher(dir.getName());
                    if (matcher_face_img.matches()) {
                        int id = Integer.parseInt(matcher_face_img.group(1));
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case HAIR: {
                // Character.wz/Hair/00030000.img
                Pattern pattern_hair_img = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.CHARACTER.getSubDirectoryFiles("Hair")) {
                    Matcher matcher_hair_img = pattern_hair_img.matcher(dir.getName());
                    if (matcher_hair_img.matches()) {
                        int id = Integer.parseInt(matcher_hair_img.group(1));
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case JOB: {
                // Skill.wz/000.img
                Pattern pattern_job_img = Pattern.compile("(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.SKILL.getRootDirectory().getFiles()) {
                    Matcher matcher_job_img = pattern_job_img.matcher(dir.getName());
                    if (matcher_job_img.matches()) {
                        int id = Integer.parseInt(matcher_job_img.group(1));
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case ITEM: {
                // Character.wz/Accessory/01010000.img
                Pattern pattern_equip_img = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataDirectoryEntry dir : WzXML.CHARACTER.getRootDirectory().getSubDirectories()) {
                    switch (dir.getName()) {
                        case "Afterimage", "Face", "Hair" -> {
                            // ignore.
                        }
                        default -> {
                            for (IMapleDataEntity file : dir.getFiles()) {
                                Matcher matcher_equip_img = pattern_equip_img.matcher(file.getName());
                                if (matcher_equip_img.matches()) {
                                    int id = Integer.parseInt(matcher_equip_img.group(1));
                                    add(id);
                                }
                            }
                        }

                    }
                }
                // Item.wz/Cash/0501.img/05010000
                // Item.wz/Pet/5000000.img
                Pattern pattern_item_category_img = Pattern.compile("0*\\d+\\.img");
                Pattern pattern_item_id = Pattern.compile("0*(\\d+)");
                Pattern pattern_pet_img = Pattern.compile("(\\d+)\\.img");
                for (IMapleDataDirectoryEntry dir : WzXML.ITEM.getRootDirectory().getSubDirectories()) {
                    switch (dir.getName()) {
                        case "Cash", "Consume", "Etc", "Install" -> {
                            for (IMapleDataEntity mde : dir.getFiles()) {
                                Matcher matcher_item_category_img = pattern_item_category_img.matcher(mde.getName());
                                if (matcher_item_category_img.matches()) {
                                    for (IMapleData md : WzXML.ITEM.getData(dir.getName() + "/" + mde.getName()).getChildren()) {
                                        Matcher matcher_item_id = pattern_item_id.matcher(md.getName());
                                        if (matcher_item_id.matches()) {
                                            int id = Integer.parseInt(matcher_item_id.group(1));
                                            add(id);
                                        }
                                    }
                                }
                            }
                        }
                        case "Pet" -> {
                            for (IMapleDataEntity mde : dir.getFiles()) {
                                Matcher matcher_pet_img = pattern_pet_img.matcher(mde.getName());
                                if (matcher_pet_img.matches()) {
                                    int id = Integer.parseInt(matcher_pet_img.group(1));
                                    add(id);
                                }
                            }
                        }
                        default -> {
                        }
                    }
                }

                dlt.End();
                return true;
            }
            case MAP: {
                // Map.wz/Map/Map0/000050001.img
                Pattern pattern_map_dir = Pattern.compile("Map(\\d+)");
                Pattern pattern_map_img = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataDirectoryEntry dir : WzXML.MAP.getSubDirectory("Map").getSubDirectories()) {
                    Matcher dir_matcher = pattern_map_dir.matcher(dir.getName());
                    if (dir_matcher.matches()) {
                        for (IMapleDataEntity mde : dir.getFiles()) {
                            Matcher matcher_map_img = pattern_map_img.matcher(mde.getName());
                            if (matcher_map_img.matches()) {
                                int id = Integer.parseInt(matcher_map_img.group(1));
                                add(id);
                            }
                        }
                    }
                }

                dlt.End();
                return true;
            }
            case MOB: {
                // Mob.wz/0100100.img
                Pattern pattern_mob_img = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.MOB.getRootDirectory().getFiles()) {
                    Matcher matcher_mob_img = pattern_mob_img.matcher(dir.getName());
                    if (matcher_mob_img.matches()) {
                        int id = Integer.parseInt(matcher_mob_img.group(1));
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case NPC: {
                // Npc.wz/0002000.img
                Pattern pattern_npc_img = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.NPC.getRootDirectory().getFiles()) {
                    Matcher matcher_npc_img = pattern_npc_img.matcher(dir.getName());
                    if (matcher_npc_img.matches()) {
                        int id = Integer.parseInt(matcher_npc_img.group(1));
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case REACTOR: {
                // Reactor.wz/0002000.img
                Pattern pattern_reactor_img = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.REACTOR.getWzRoot().getRootDirectory().getFiles()) {
                    Matcher matcher_reactor_img = pattern_reactor_img.matcher(dir.getName());
                    if (matcher_reactor_img.matches()) {
                        int id = Integer.parseInt(matcher_reactor_img.group(1));
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case SKILL: {
                // Skill.wz/100.img/skill/1000000
                Pattern pattern_job_img = Pattern.compile("0*(\\d+)\\.img");
                Pattern patern_skill_id = Pattern.compile("0*(\\d+)");
                for (IMapleDataEntity dir : WzXML.SKILL.getRootDirectory().getFiles()) {
                    Matcher matcher_job_img = pattern_job_img.matcher(dir.getName());
                    if (matcher_job_img.matches()) {
                        IMapleData md_skill = WzXML.SKILL.getData(dir.getName()).getChildByPath("skill");
                        if (md_skill != null) {
                            for (IMapleData md : md_skill.getChildren()) {
                                Matcher matcher_skill_id = patern_skill_id.matcher(md.getName());
                                if (matcher_skill_id.matches()) {
                                    int id = Integer.parseInt(md.getName());
                                    add(id);
                                }
                            }
                        }
                    }
                }

                dlt.End();
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    private boolean add(int id) {
        if (this.data.contains(id)) {
            return false;
        }

        this.data.add(id);
        return true;
    }

    public ArrayList<Integer> getIds() {
        if (this.data == null) {
            load();
        }
        return this.data;
    }

    public boolean check(int id) {
        if (this.data == null) {
            load();
        }

        // KMS001, beginner job does not have any skill.
        if (this.type == WzType.JOB) {
            if (id == 0) {
                return true;
            }
        }

        return this.data.contains(id);
    }

    public int getRandom() {
        if (this.data == null) {
            load();
        }

        return this.data.get(RAND.nextInt(this.data.size()));
    }
}
