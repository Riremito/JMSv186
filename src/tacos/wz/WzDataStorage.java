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
                Pattern pattern = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.CHARACTER.getRootDirectory().getFiles()) {
                    Matcher matcher = pattern.matcher(dir.getName());
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1)) % 100;
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case FACE: {
                Pattern pattern = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.CHARACTER.getSubDirectoryFiles("Face")) {
                    Matcher matcher = pattern.matcher(dir.getName());
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1));
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case HAIR: {
                Pattern pattern = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.CHARACTER.getSubDirectoryFiles("Hair")) {
                    Matcher matcher = pattern.matcher(dir.getName());
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1));
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case JOB: {
                Pattern pattern = Pattern.compile("(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.SKILL.getRootDirectory().getFiles()) {
                    Matcher matcher = pattern.matcher(dir.getName());
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1));
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case ITEM: {
                Pattern pattern_equip = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataDirectoryEntry equip_dir : WzXML.CHARACTER.getRootDirectory().getSubDirectories()) {
                    for (IMapleDataEntity dir : equip_dir.getFiles()) {
                        Matcher img_matcher = pattern_equip.matcher(dir.getName());
                        if (img_matcher.matches()) {
                            int id = Integer.parseInt(img_matcher.group(1));
                            // ignore hair
                            if (1000000 <= id) {
                                add(id);
                            }
                        }
                    }
                }
                // Item.wz/Cash/0501.img
                Pattern pattern_item = Pattern.compile("0*(\\d+)\\.img");
                Pattern pattern_id = Pattern.compile("0*(\\d+)");

                for (IMapleDataDirectoryEntry dir : WzXML.ITEM.getRootDirectory().getSubDirectories()) {
                    switch (dir.getName()) {
                        case "Cash", "Consume", "Etc", "Install" -> {
                            for (IMapleDataEntity mde : dir.getFiles()) {
                                if (pattern_item.matcher(mde.getName()).matches()) {
                                    for (IMapleData md : WzXML.ITEM.getData(dir.getName() + "/" + mde.getName()).getChildren()) {
                                        if (pattern_id.matcher(md.getName()).matches()) {
                                            int id = Integer.parseInt(md.getName());
                                            add(id);
                                        }
                                    }
                                }
                            }
                        }
                        default -> {
                        }
                    }
                }
                // Item.wz/Pet/5000000.img
                Pattern pattern_pet = Pattern.compile("(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.ITEM.getSubDirectoryFiles("Pet")) {
                    Matcher matcher = pattern_pet.matcher(dir.getName());
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1));
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case MAP: {
                Pattern dir_pattern = Pattern.compile("Map(\\d+)");
                Pattern img_pattern = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataDirectoryEntry dir : WzXML.MAP.getSubDirectory("Map").getSubDirectories()) {
                    Matcher dir_matcher = dir_pattern.matcher(dir.getName());
                    if (dir_matcher.matches()) {
                        // Map.wz/Map/Map[0-9]
                        for (IMapleDataEntity mde : dir.getFiles()) {
                            // Map.wz/Map/Map[0-9]/
                            Matcher img_matcher = img_pattern.matcher(mde.getName());
                            if (img_matcher.matches()) {
                                int id = Integer.parseInt(img_matcher.group(1));
                                add(id);
                            }
                        }
                    }
                }

                dlt.End();
                return true;
            }
            case MOB: {
                Pattern pattern = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.MOB.getRootDirectory().getFiles()) {
                    Matcher matcher = pattern.matcher(dir.getName());
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1));
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case NPC: {
                Pattern pattern = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.NPC.getRootDirectory().getFiles()) {
                    Matcher matcher = pattern.matcher(dir.getName());
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1));
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case REACTOR: {
                Pattern pattern = Pattern.compile("0*(\\d+)\\.img");
                for (IMapleDataEntity dir : WzXML.REACTOR.getWzRoot().getRootDirectory().getFiles()) {
                    Matcher matcher = pattern.matcher(dir.getName());
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1));
                        add(id);
                    }
                }

                dlt.End();
                return true;
            }
            case SKILL: {
                Pattern img_pattern = Pattern.compile("0*(\\d+)\\.img");
                Pattern id_pattern = Pattern.compile("0*(\\d+)");
                for (IMapleDataEntity dir : WzXML.SKILL.getRootDirectory().getFiles()) {
                    Matcher img_matcher = img_pattern.matcher(dir.getName());
                    if (img_matcher.matches()) {
                        IMapleData md_skill = WzXML.SKILL.getData(dir.getName()).getChildByPath("skill");
                        if (md_skill != null) {
                            for (IMapleData md : md_skill.getChildren()) {
                                Matcher id_matcher = id_pattern.matcher(md.getName());
                                if (id_matcher.matches()) {
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
