/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.server.life;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import tacos.odin.OdinPair;
import odin.provider.IMapleData;
import tacos.wz.WzDataTool;
import tacos.wz.WzXML;

public class MapleLifeFactory {

    private static Map<Integer, MapleMonsterStats> monsterStats = new HashMap<>();
    private static Map<Integer, String> npcNames = new HashMap<>();

    public static AbstractLoadedMapleLife getLife(int id, String type) {
        if (type.equalsIgnoreCase("n")) {
            return getNPC(id);
        } else if (type.equalsIgnoreCase("m")) {
            // randomize mob
            //id = DWI_LoadXML.getMob().getRandom();
            return getMonster(id);
        } else {
            System.err.println("Unknown Life type: " + type + "");
            return null;
        }
    }

    public static List<Integer> getQuestCount(int id) {
        return WzXML.MOB.getQuestCountGroup().get(id);
    }

    // fix broken MP mob
    public static boolean IsBrokenMPMob(int mob_id) {
        switch (mob_id) {
            // レプラコーン, leprechaun
            case 9400583:
            case 9400584: {
                // enable 8000 damage candy attack skill
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static MapleMonster getMonster(int mob_id) {
        MapleMonsterStats stats = monsterStats.get(mob_id);

        if (stats == null) {
            IMapleData monsterData = WzXML.MOB.getImg(mob_id);
            if (monsterData == null) {
                return null;
            }
            IMapleData monsterInfoData = monsterData.getChildByPath("info");
            stats = new MapleMonsterStats();

            stats.setHp(WzDataTool.getIntPath("maxHP", monsterInfoData, 0));
            int mp = WzDataTool.getIntPath("maxMP", monsterInfoData, 0);
            stats.setMp(IsBrokenMPMob(mob_id) ? 30000 : mp);

            stats.setExp(WzDataTool.getIntPath("exp", monsterInfoData, 0));
            stats.setLevel((short) WzDataTool.getIntPath("level", monsterInfoData, 0));
            stats.setRemoveAfter(WzDataTool.getIntPath("removeAfter", monsterInfoData, 0));
            stats.setrareItemDropLevel((byte) WzDataTool.getIntPath("rareItemDropLevel", monsterInfoData, 0));
            stats.setFixedDamage(WzDataTool.getIntPath("fixedDamage", monsterInfoData, -1));
            stats.setOnlyNormalAttack(WzDataTool.getIntPath("onlyNormalAttack", monsterInfoData, 0) > 0);
            stats.setBoss(WzDataTool.getIntPath("boss", monsterInfoData, 0) > 0 || mob_id == 8810018 || mob_id == 9410066 || (mob_id >= 8810118 && mob_id <= 8810122));
            stats.setExplosiveReward(WzDataTool.getIntPath("explosiveReward", monsterInfoData, 0) > 0);
            stats.setFfaLoot(WzDataTool.getIntPath("publicReward", monsterInfoData, 0) > 0);
            stats.setUndead(WzDataTool.getIntPath("undead", monsterInfoData, 0) > 0);
            stats.setName(WzDataTool.getStringPath(mob_id + "/name", WzXML.STRING.getMob(), "MISSINGNO"));
            stats.setBuffToGive(WzDataTool.getIntPath("buff", monsterInfoData, -1));
            stats.setFriendly(WzDataTool.getIntPath("damagedByMob", monsterInfoData, 0) > 0);
            stats.setExplosiveReward(WzDataTool.getIntPath("explosiveReward", monsterInfoData, 0) > 0);
            stats.setNoDoom(WzDataTool.getIntPath("noDoom", monsterInfoData, 0) > 0);
            stats.setFfaLoot(WzDataTool.getIntPath("publicReward", monsterInfoData, 0) > 0);
            stats.setCP((byte) WzDataTool.getIntPath("getCP", monsterInfoData, 0));
            stats.setPoint(WzDataTool.getIntPath("point", monsterInfoData, 0));
            stats.setDropItemPeriod(WzDataTool.getIntPath("dropItemPeriod", monsterInfoData, 0));
            stats.setPhysicalDefense((short) WzDataTool.getIntPath("PDDamage", monsterInfoData, 0));
            stats.setMagicDefense((short) WzDataTool.getIntPath("MDDamage", monsterInfoData, 0));
            stats.setEva((short) WzDataTool.getIntPath("eva", monsterInfoData, 0));
            final boolean hideHP = WzDataTool.getIntPath("HPgaugeHide", monsterInfoData, 0) > 0 || WzDataTool.getIntPath("hideHP", monsterInfoData, 0) > 0;
            final IMapleData selfd = monsterInfoData.getChildByPath("selfDestruction");
            if (selfd != null) {
                stats.setSelfDHP(WzDataTool.getIntPath("hp", selfd, 0));
                stats.setSelfD((byte) WzDataTool.getIntPath("action", selfd, -1));
            } else {
                stats.setSelfD((byte) -1);
            }
            stats.setFirstAttack(WzDataTool.getIntPath("firstAttack", monsterInfoData, 0) > 0);
            if (stats.isBoss() || isDmgSponge(mob_id)) {
                if (hideHP || monsterInfoData.getChildByPath("hpTagColor") == null || monsterInfoData.getChildByPath("hpTagBgcolor") == null) {
                    stats.setTagColor(0);
                    stats.setTagBgColor(0);
                } else {
                    stats.setTagColor(WzDataTool.getIntPath("hpTagColor", monsterInfoData, 0));
                    stats.setTagBgColor(WzDataTool.getIntPath("hpTagBgcolor", monsterInfoData, 0));
                }
            }

            final IMapleData banishData = monsterInfoData.getChildByPath("ban");
            if (banishData != null) {
                stats.setBanishInfo(new BanishInfo(
                        WzDataTool.getStringPath("banMsg", banishData, ""),
                        WzDataTool.getIntPath("banMap/0/field", banishData, -1),
                        WzDataTool.getStringPath("banMap/0/portal", banishData, "sp")));
            }

            final IMapleData reviveInfo = monsterInfoData.getChildByPath("revive");
            if (reviveInfo != null) {
                List<Integer> revives = new LinkedList<>();
                for (IMapleData bdata : reviveInfo) {
                    revives.add(WzDataTool.getInt(bdata));
                }
                stats.setRevives(revives);
            }

            final IMapleData monsterSkillData = monsterInfoData.getChildByPath("skill");
            if (monsterSkillData != null) {
                int i = 0;
                List<OdinPair<Integer, Integer>> skills = new ArrayList<>();
                while (monsterSkillData.getChildByPath(Integer.toString(i)) != null) {
                    skills.add(new OdinPair<>(WzDataTool.getIntPath(i + "/skill", monsterSkillData, 0), WzDataTool.getIntPath(i + "/level", monsterSkillData, 0)));
                    i++;
                }
                stats.setSkills(skills);
            }

            decodeElementalString(stats, WzDataTool.getStringPath("elemAttr", monsterInfoData, ""));

            // Other data which isn;t in the mob, but might in the linked data
            int link_id = WzDataTool.getIntPath("link", monsterInfoData, 0);
            if (link_id != 0) { // Store another copy, for faster processing.
                monsterData = WzXML.MOB.getImg(link_id);
            }

            for (IMapleData idata : monsterData) {
                if (idata.getName().equals("fly")) {
                    stats.setFly(true);
                    stats.setMobile(true);
                    break;
                } else if (idata.getName().equals("move")) {
                    stats.setMobile(true);
                }
            }

            byte hpdisplaytype = -1;
            if (stats.getTagColor() > 0) {
                hpdisplaytype = 0;
            } else if (stats.isFriendly()) {
                hpdisplaytype = 1;
            } else if (mob_id >= 9300184 && mob_id <= 9300215) { // Mulung TC mobs
                hpdisplaytype = 2;
            } else if (!stats.isBoss() || mob_id == 9410066) { // Not boss and dong dong chiang
                hpdisplaytype = 3;
            }
            stats.setHPDisplayType(hpdisplaytype);

            monsterStats.put(mob_id, stats);
        }
        return new MapleMonster(mob_id, stats);
    }

    public static void decodeElementalString(MapleMonsterStats stats, String elemAttr) {
        for (int i = 0; i < elemAttr.length(); i += 2) {
            stats.setEffectiveness(
                    Element.getFromChar(elemAttr.charAt(i)),
                    ElementalEffectiveness.getByNumber(Integer.parseInt(String.valueOf(elemAttr.charAt(i + 1)))));
        }
    }

    private static boolean isDmgSponge(final int mid) {
        switch (mid) {
            case 8810018:
            case 8810118:
            case 8810119:
            case 8810120:
            case 8810121:
            case 8810122:
            case 8820009:
            case 8820010:
            case 8820011:
            case 8820012:
            case 8820013:
            case 8820014:
                return true;
        }
        return false;
    }


    public static MapleNPC getNPC(int npc_id) {
        String name = npcNames.get(npc_id);
        if (name == null) {
            name = WzDataTool.getStringPath(npc_id + "/name", WzXML.STRING.getNpc(), "MISSINGNO");
            npcNames.put(npc_id, name);
        }
        return new MapleNPC(npc_id, name);
    }
}
