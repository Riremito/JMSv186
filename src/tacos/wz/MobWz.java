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

import tacos.config.Content;
import tacos.debug.DebugLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odin.server.life.MapleMonster;
import odin.server.life.MobAttackInfo;
import odin.server.life.MapleMonsterStats;
import odin.server.life.Element;
import odin.server.life.ElementalEffectiveness;
import odin.server.life.BanishInfo;
import java.util.LinkedList;
import java.util.AbstractMap.SimpleImmutableEntry;

/**
 *
 * @author Riremito
 */
public class MobWz extends WzXML {

    public MobWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Mob" : "Mob.wz");
    }

    public MapleData getImg(int mob_id) {
        String target_img_path = String.format("%07d.img", mob_id);
        return getData(target_img_path);
    }

    // fix broken MP mob
    private boolean isBrokenMPMob(int mob_id) {
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

    private boolean isDmgSponge(final int mid) {
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

    private void decodeElementalString(MapleMonsterStats stats, String elemAttr) {
        for (int i = 0; i < elemAttr.length(); i += 2) {
            stats.setEffectiveness(
                    Element.getFromChar(elemAttr.charAt(i)),
                    ElementalEffectiveness.getByNumber(Integer.parseInt(String.valueOf(elemAttr.charAt(i + 1)))));
        }
    }

    public MapleMonsterStats loadMonsterStats(int mob_id) {
        MapleData monsterData = getImg(mob_id);
        if (monsterData == null) {
            return null;
        }
        MapleData monsterInfoData = monsterData.getChildByPath("info");
        MapleMonsterStats stats = new MapleMonsterStats();

        stats.setHp(WzDataTool.getIntPath("maxHP", monsterInfoData, 0));
        int mp = WzDataTool.getIntPath("maxMP", monsterInfoData, 0);
        stats.setMp(isBrokenMPMob(mob_id) ? 30000 : mp);

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
        final MapleData selfd = monsterInfoData.getChildByPath("selfDestruction");
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

        final MapleData banishData = monsterInfoData.getChildByPath("ban");
        if (banishData != null) {
            stats.setBanishInfo(new BanishInfo(
                    WzDataTool.getStringPath("banMsg", banishData, ""),
                    WzDataTool.getIntPath("banMap/0/field", banishData, -1),
                    WzDataTool.getStringPath("banMap/0/portal", banishData, "sp")));
        }

        final MapleData reviveInfo = monsterInfoData.getChildByPath("revive");
        if (reviveInfo != null) {
            List<Integer> revives = new LinkedList<>();
            for (MapleData bdata : reviveInfo) {
                revives.add(WzDataTool.getInt(bdata));
            }
            stats.setRevives(revives);
        }

        final MapleData monsterSkillData = monsterInfoData.getChildByPath("skill");
        if (monsterSkillData != null) {
            int i = 0;
            List<SimpleImmutableEntry<Integer, Integer>> skills = new ArrayList<>();
            while (monsterSkillData.getChildByPath(Integer.toString(i)) != null) {
                skills.add(new SimpleImmutableEntry<>(WzDataTool.getIntPath(i + "/skill", monsterSkillData, 0), WzDataTool.getIntPath(i + "/level", monsterSkillData, 0)));
                i++;
            }
            stats.setSkills(skills);
        }

        decodeElementalString(stats, WzDataTool.getStringPath("elemAttr", monsterInfoData, ""));

        // Other data which isn;t in the mob, but might in the linked data
        int link_id = WzDataTool.getIntPath("link", monsterInfoData, 0);
        if (link_id != 0) { // Store another copy, for faster processing.
            monsterData = getImg(link_id);
        }

        for (MapleData idata : monsterData) {
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

        return stats;
    }

    private Map<SimpleImmutableEntry<Integer, Integer>, MobAttackInfo> map_mobAttacks = null;

    public MobAttackInfo getMobAttackInfo(MapleMonster mob, int attack) {
        if (map_mobAttacks == null) {
            map_mobAttacks = new HashMap<>();
        }
        MobAttackInfo mai_found = map_mobAttacks.get(new SimpleImmutableEntry<>(mob.getId(), attack));
        if (mai_found != null) {
            return mai_found;
        }

        MobAttackInfo ret = new MobAttackInfo();
        MapleData mobData = getImg(mob.getId());
        if (mobData != null) {
            MapleData infoData = mobData.getChildByPath("info/link");
            if (infoData != null) {
                int link_id = WzDataTool.getIntPath("info/link", mobData, 0);
                mobData = getImg(link_id);
            }
            MapleData attackData = mobData.getChildByPath("attack" + (attack + 1) + "/info");
            if (attackData != null) {
                ret.setDeadlyAttack(attackData.getChildByPath("deadlyAttack") != null);
                ret.setMpBurn(WzDataTool.getIntPath("mpBurn", attackData, 0));
                ret.setDiseaseSkill(WzDataTool.getIntPath("disease", attackData, 0));
                ret.setDiseaseLevel(WzDataTool.getIntPath("level", attackData, 0));
                ret.setMpCon(WzDataTool.getIntPath("conMP", attackData, 0));
            }
        }
        map_mobAttacks.put(new SimpleImmutableEntry<>(mob.getId(), attack), ret);
        return ret;
    }

    private Map<Integer, List<Integer>> map_QuestCountGroup = null;

    public Map<Integer, List<Integer>> getQuestCountGroup() {
        if (map_QuestCountGroup != null) {
            return map_QuestCountGroup;
        }
        map_QuestCountGroup = new HashMap<>();
        for (MapleDataDirectoryEntry mapz : getRootDirectory().getSubDirectories()) {
            if (mapz.getName().equals("QuestCountGroup")) {
                for (MapleDataEntity entry : mapz.getFiles()) {
                    final int id = Integer.parseInt(entry.getName().substring(0, entry.getName().length() - 4));
                    MapleData dat = getData("QuestCountGroup/" + entry.getName());
                    if (dat != null && dat.getChildByPath("info") != null) {
                        List<Integer> z = new ArrayList<>();
                        for (MapleData da : dat.getChildByPath("info")) {
                            z.add(WzDataTool.getInt(da, 0));
                        }
                        map_QuestCountGroup.put(id, z);
                    } else {
                        DebugLogger.ErrorLog("null questcountgroup");
                    }
                }
            }
        }

        return map_QuestCountGroup;
    }
}
