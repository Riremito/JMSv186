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

import tacos.wz.data.EtcWz;
import tacos.wz.data.MobWz;
import tacos.wz.data.StringWz;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import odin.provider.WzXML.MapleDataType;
import tacos.odin.OdinPair;
import odin.tools.StringUtil;
import odin.provider.IMapleData;
import tacos.wz.TacosWzDataTool;

public class MapleLifeFactory {

    private static Map<Integer, String> npcNames = new HashMap<Integer, String>();
    private static Map<Integer, MapleMonsterStats> monsterStats = new HashMap<Integer, MapleMonsterStats>();
    private static Map<Integer, Integer> NPCLoc = new HashMap<Integer, Integer>();

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

    public static int getNPCLocation(int npcid) {
        if (NPCLoc.containsKey(npcid)) {
            return NPCLoc.get(npcid);
        }
        final int map = TacosWzDataTool.getIntConvert(Integer.toString(npcid) + "/0", EtcWz.get().getNpcLocation(), -1);
        NPCLoc.put(npcid, map);
        return map;
    }

    public static final List<Integer> getQuestCount(final int id) {
        return MobWz.get().getQuestCountGroup().get(id);
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

    public static MapleMonster getMonster(int mid) {
        MapleMonsterStats stats = monsterStats.get(mid);

        if (stats == null) {
            IMapleData monsterData = MobWz.get().getData(StringUtil.getLeftPaddedStr(Integer.toString(mid) + ".img", '0', 11));
            if (monsterData == null) {
                return null;
            }
            IMapleData monsterInfoData = monsterData.getChildByPath("info");
            stats = new MapleMonsterStats();

            stats.setHp(TacosWzDataTool.getIntConvert("maxHP", monsterInfoData));
            int mp = TacosWzDataTool.getIntConvert("maxMP", monsterInfoData, 0);
            stats.setMp(IsBrokenMPMob(mid) ? 30000 : mp);

            stats.setExp(TacosWzDataTool.getIntConvert("exp", monsterInfoData, 0));
            stats.setLevel((short) TacosWzDataTool.getIntConvert("level", monsterInfoData));
            stats.setRemoveAfter(TacosWzDataTool.getIntConvert("removeAfter", monsterInfoData, 0));
            stats.setrareItemDropLevel((byte) TacosWzDataTool.getIntConvert("rareItemDropLevel", monsterInfoData, 0));
            stats.setFixedDamage(TacosWzDataTool.getIntConvert("fixedDamage", monsterInfoData, -1));
            stats.setOnlyNormalAttack(TacosWzDataTool.getIntConvert("onlyNormalAttack", monsterInfoData, 0) > 0);
            stats.setBoss(TacosWzDataTool.getIntConvert("boss", monsterInfoData, 0) > 0 || mid == 8810018 || mid == 9410066 || (mid >= 8810118 && mid <= 8810122));
            stats.setExplosiveReward(TacosWzDataTool.getIntConvert("explosiveReward", monsterInfoData, 0) > 0);
            stats.setFfaLoot(TacosWzDataTool.getIntConvert("publicReward", monsterInfoData, 0) > 0);
            stats.setUndead(TacosWzDataTool.getIntConvert("undead", monsterInfoData, 0) > 0);
            stats.setName(TacosWzDataTool.getStringPath(mid + "/name", StringWz.get().getMob(), "MISSINGNO"));
            stats.setBuffToGive(TacosWzDataTool.getIntConvert("buff", monsterInfoData, -1));
            stats.setFriendly(TacosWzDataTool.getIntConvert("damagedByMob", monsterInfoData, 0) > 0);
            stats.setExplosiveReward(TacosWzDataTool.getIntConvert("explosiveReward", monsterInfoData, 0) > 0);
            stats.setNoDoom(TacosWzDataTool.getIntConvert("noDoom", monsterInfoData, 0) > 0);
            stats.setFfaLoot(TacosWzDataTool.getIntConvert("publicReward", monsterInfoData, 0) > 0);
            stats.setCP((byte) TacosWzDataTool.getIntConvert("getCP", monsterInfoData, 0));
            stats.setPoint(TacosWzDataTool.getIntConvert("point", monsterInfoData, 0));
            stats.setDropItemPeriod(TacosWzDataTool.getIntConvert("dropItemPeriod", monsterInfoData, 0));
            stats.setPhysicalDefense((short) TacosWzDataTool.getIntConvert("PDDamage", monsterInfoData, 0));
            stats.setMagicDefense((short) TacosWzDataTool.getIntConvert("MDDamage", monsterInfoData, 0));
            stats.setEva((short) TacosWzDataTool.getIntConvert("eva", monsterInfoData, 0));
            final boolean hideHP = TacosWzDataTool.getIntConvert("HPgaugeHide", monsterInfoData, 0) > 0 || TacosWzDataTool.getIntConvert("hideHP", monsterInfoData, 0) > 0;
            final IMapleData selfd = monsterInfoData.getChildByPath("selfDestruction");
            if (selfd != null) {
                stats.setSelfDHP(TacosWzDataTool.getIntConvert("hp", selfd, 0));
                stats.setSelfD((byte) TacosWzDataTool.getIntConvert("action", selfd, -1));
            } else {
                stats.setSelfD((byte) -1);
            }
            final IMapleData firstAttackData = monsterInfoData.getChildByPath("firstAttack");
            if (firstAttackData != null) {
                if (firstAttackData.getType() == MapleDataType.FLOAT) {
                    stats.setFirstAttack(Math.round(TacosWzDataTool.getFloat(firstAttackData, 0.0f)) > 0);
                } else {
                    stats.setFirstAttack(TacosWzDataTool.getInt(firstAttackData) > 0);
                }
            }
            if (stats.isBoss() || isDmgSponge(mid)) {
                if (hideHP || monsterInfoData.getChildByPath("hpTagColor") == null || monsterInfoData.getChildByPath("hpTagBgcolor") == null) {
                    stats.setTagColor(0);
                    stats.setTagBgColor(0);
                } else {
                    stats.setTagColor(TacosWzDataTool.getIntConvert("hpTagColor", monsterInfoData));
                    stats.setTagBgColor(TacosWzDataTool.getIntConvert("hpTagBgcolor", monsterInfoData));
                }
            }

            final IMapleData banishData = monsterInfoData.getChildByPath("ban");
            if (banishData != null) {
                stats.setBanishInfo(new BanishInfo(
                        TacosWzDataTool.getStringPath("banMsg", banishData, ""),
                        TacosWzDataTool.getIntPath("banMap/0/field", banishData, -1),
                        TacosWzDataTool.getStringPath("banMap/0/portal", banishData, "sp")));
            }

            final IMapleData reviveInfo = monsterInfoData.getChildByPath("revive");
            if (reviveInfo != null) {
                List<Integer> revives = new LinkedList<>();
                for (IMapleData bdata : reviveInfo) {
                    revives.add(TacosWzDataTool.getInt(bdata));
                }
                stats.setRevives(revives);
            }

            final IMapleData monsterSkillData = monsterInfoData.getChildByPath("skill");
            if (monsterSkillData != null) {
                int i = 0;
                List<OdinPair<Integer, Integer>> skills = new ArrayList<>();
                while (monsterSkillData.getChildByPath(Integer.toString(i)) != null) {
                    skills.add(new OdinPair<>(TacosWzDataTool.getIntPath(i + "/skill", monsterSkillData, 0), TacosWzDataTool.getIntPath(i + "/level", monsterSkillData, 0)));
                    i++;
                }
                stats.setSkills(skills);
            }

            decodeElementalString(stats, TacosWzDataTool.getStringPath("elemAttr", monsterInfoData, ""));

            // Other data which isn;t in the mob, but might in the linked data
            final int link = TacosWzDataTool.getIntConvert("link", monsterInfoData, 0);
            if (link != 0) { // Store another copy, for faster processing.
                monsterData = MobWz.get().getData(StringUtil.getLeftPaddedStr(link + ".img", '0', 11));
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
            } else if (mid >= 9300184 && mid <= 9300215) { // Mulung TC mobs
                hpdisplaytype = 2;
            } else if (!stats.isBoss() || mid == 9410066) { // Not boss and dong dong chiang
                hpdisplaytype = 3;
            }
            stats.setHPDisplayType(hpdisplaytype);

            monsterStats.put(mid, stats);
        }
        return new MapleMonster(mid, stats);
    }

    public static final void decodeElementalString(MapleMonsterStats stats, String elemAttr) {
        for (int i = 0; i < elemAttr.length(); i += 2) {
            stats.setEffectiveness(
                    Element.getFromChar(elemAttr.charAt(i)),
                    ElementalEffectiveness.getByNumber(Integer.valueOf(String.valueOf(elemAttr.charAt(i + 1)))));
        }
    }

    private static final boolean isDmgSponge(final int mid) {
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

    public static MapleNPC getNPC(final int nid) {
        String name = npcNames.get(nid);
        if (name == null) {
            name = TacosWzDataTool.getStringPath(nid + "/name", StringWz.get().getNpc(), "MISSINGNO");
            npcNames.put(nid, name);
        }
        if (name.contains("Maple TV")) {
            return null;
        }
        return new MapleNPC(nid, name);
    }
}
