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
import tacos.server.map.object.TacosMonster;
import tacos.debug.DebugLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odin.server.life.MapleMonsterStats;
import odin.server.life.MapleMonsterStats.Element;
import odin.server.life.MapleMonsterStats.ElementalEffectiveness;
import java.util.LinkedList;
import java.util.AbstractMap.SimpleImmutableEntry;
import lombok.Getter;
import tacos.constants.TacosConstants;

/**
 *
 * @author Riremito
 */
public class MobWz extends WzXML {

    public MobWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Mob" : "Mob.wz");
    }

    private final Map<Integer, MapleMonsterStats> monsterStats = new HashMap<>();

    public TacosMonster createMob(int mob_id) {
        if (!WzDataStorage.MOB.check(mob_id)) {
            DebugLogger.ErrorLog("createMob : invalid id, " + mob_id);
            return null;
        }

        MapleMonsterStats stats = this.monsterStats.get(mob_id);
        if (stats == null) {
            stats = WzXML.MOB.loadMonsterStats(mob_id);
            if (stats == null) {
                return null;
            }
            this.monsterStats.put(mob_id, stats);
        }
        return new TacosMonster(mob_id, stats);
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

    private boolean isDmgSponge(int mid) {
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

    @Getter
    public class MobBanInfo {

        private String banMsg;
        private int field;
        private String portal;
    }

    public MapleMonsterStats loadMonsterStats(int mob_id) {
        MapleData monsterData = getImg(mob_id);
        if (monsterData == null) {
            return null;
        }
        MapleData monsterInfoData = monsterData.getChildByPath("info");
        MapleMonsterStats stats = new MapleMonsterStats();

        stats.setHp(monsterInfoData.getInt("maxHP"));
        int mp = monsterInfoData.getInt("maxMP");
        stats.setMp(isBrokenMPMob(mob_id) ? 30000 : mp);

        stats.setExp(monsterInfoData.getInt("exp"));
        stats.setLevel((short) monsterInfoData.getInt("level"));
        stats.setRemoveAfter(monsterInfoData.getInt("removeAfter"));
        stats.setrareItemDropLevel((byte) monsterInfoData.getInt("rareItemDropLevel"));
        stats.setFixedDamage(monsterInfoData.getInt("fixedDamage", -1));
        stats.setOnlyNormalAttack(monsterInfoData.getInt("onlyNormalAttack") > 0);
        stats.setBoss(monsterInfoData.getInt("boss") > 0 || mob_id == 8810018 || mob_id == 9410066 || (mob_id >= 8810118 && mob_id <= 8810122));
        stats.setExplosiveReward(monsterInfoData.getInt("explosiveReward") > 0);
        stats.setFfaLoot(monsterInfoData.getInt("publicReward") > 0);
        stats.setUndead(monsterInfoData.getInt("undead") > 0);
        stats.setName(WzXML.STRING.getMob().getString(mob_id + "/name", "MISSINGNO"));
        stats.setBuffToGive(monsterInfoData.getInt("buff", -1));
        stats.setFriendly(monsterInfoData.getInt("damagedByMob") > 0);
        stats.setExplosiveReward(monsterInfoData.getInt("explosiveReward") > 0);
        stats.setNoDoom(monsterInfoData.getInt("noDoom") > 0);
        stats.setFfaLoot(monsterInfoData.getInt("publicReward") > 0);
        stats.setCP((byte) monsterInfoData.getInt("getCP"));
        stats.setPoint(monsterInfoData.getInt("point"));
        stats.setDropItemPeriod(monsterInfoData.getInt("dropItemPeriod"));
        stats.setPhysicalDefense((short) monsterInfoData.getInt("PDDamage"));
        stats.setMagicDefense((short) monsterInfoData.getInt("MDDamage"));
        stats.setEva((short) monsterInfoData.getInt("eva"));
        boolean hideHP = monsterInfoData.getInt("HPgaugeHide") > 0 || monsterInfoData.getInt("hideHP") > 0;
        MapleData selfd = monsterInfoData.getChildByPath("selfDestruction");
        if (selfd != null) {
            stats.setSelfDHP(selfd.getInt("hp"));
            stats.setSelfD((byte) selfd.getInt("action", -1));
        } else {
            stats.setSelfD((byte) -1);
        }
        stats.setFirstAttack(monsterInfoData.getInt("firstAttack") > 0);
        if (stats.isBoss() || isDmgSponge(mob_id)) {
            if (hideHP || monsterInfoData.getChildByPath("hpTagColor") == null || monsterInfoData.getChildByPath("hpTagBgcolor") == null) {
                stats.setTagColor(0);
                stats.setTagBgColor(0);
            } else {
                stats.setTagColor(monsterInfoData.getInt("hpTagColor"));
                stats.setTagBgColor(monsterInfoData.getInt("hpTagBgcolor"));
            }
        }

        // info/ban
        MapleData md_info_ban = monsterInfoData.getChildByPath("ban");
        if (md_info_ban != null) {
            // example, 7090000.img
            MobBanInfo mbd = new MobBanInfo();
            mbd.banMsg = md_info_ban.getString("banMsg"); // info/ban/banMsg
            mbd.field = md_info_ban.getInt("banMap/0/field", TacosConstants.MAP_ID_PERION); // info/ban/banMap/0/field
            mbd.portal = md_info_ban.getString("banMap/0/portal", "sp"); // info/ban/banMap/0/field/portal
            stats.setBanishInfo(mbd);
        }

        MapleData reviveInfo = monsterInfoData.getChildByPath("revive");
        if (reviveInfo != null) {
            List<Integer> revives = new LinkedList<>();
            for (MapleData bdata : reviveInfo) {
                revives.add(reviveInfo.getInt(bdata.getName()));
            }
            stats.setRevives(revives);
        }

        MapleData monsterSkillData = monsterInfoData.getChildByPath("skill");
        if (monsterSkillData != null) {
            int i = 0;
            List<SimpleImmutableEntry<Integer, Integer>> skills = new ArrayList<>();
            while (monsterSkillData.getChildByPath(Integer.toString(i)) != null) {
                skills.add(new SimpleImmutableEntry<>(monsterSkillData.getInt(i + "/skill"), monsterSkillData.getInt(i + "/level")));
                i++;
            }
            stats.setSkills(skills);
        }

        decodeElementalString(stats, monsterInfoData.getString("elemAttr"));

        // Other data which isn;t in the mob, but might in the linked data
        int link_id = monsterInfoData.getInt("link");
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

    @Getter
    public class MobAttackInfo {

        private boolean deadlyAttack;
        private int mpBurn;
        private int conMP;
        private int disease;
        private int level;
    }

    private Map<SimpleImmutableEntry<Integer, Integer>, MobAttackInfo> map_mobAttacks = null;

    public MobAttackInfo getMobAttackInfo(TacosMonster mob, int attack) {
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
                int link_id = mobData.getInt("info/link");
                mobData = getImg(link_id);
            }
            MapleData attackData = mobData.getChildByPath("attack" + (attack + 1) + "/info");
            if (attackData != null) {
                ret.deadlyAttack = attackData.getChildByPath("deadlyAttack") != null;
                ret.mpBurn = attackData.getInt("mpBurn");
                ret.disease = attackData.getInt("disease");
                ret.level = attackData.getInt("level");
                ret.conMP = attackData.getInt("conMP");
            }
        }
        map_mobAttacks.put(new SimpleImmutableEntry<>(mob.getId(), attack), ret);
        return ret;
    }

    private Map<Integer, List<Integer>> map_QuestCountGroup = null;

    private Map<Integer, List<Integer>> getQuestCountGroup() {
        if (map_QuestCountGroup != null) {
            return map_QuestCountGroup;
        }
        map_QuestCountGroup = new HashMap<>();
        for (MapleDataDirectoryEntry mapz : getRootDirectory().getSubDirectories()) {
            if (mapz.getName().equals("QuestCountGroup")) {
                for (MapleDataEntity entry : mapz.getFiles()) {
                    int id = Integer.parseInt(entry.getName().substring(0, entry.getName().length() - 4));
                    MapleData dat = getData("QuestCountGroup/" + entry.getName());
                    MapleData md_info = (dat != null) ? dat.getChildByPath("info") : null;
                    if (md_info != null) {
                        List<Integer> z = new ArrayList<>();
                        for (MapleData da : md_info) {
                            z.add(md_info.getInt(da.getName()));
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

    public boolean checkQuestCountGroup(int group_id, int mob_id) {
        List<Integer> groups = getQuestCountGroup().get(group_id);
        if (groups == null) {
            return false;
        }
        return groups.contains(mob_id);
    }
}
