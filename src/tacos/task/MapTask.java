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
package tacos.task;

import odin.client.MapleCharacter;
import odin.server.life.MapleMonster;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapItem;
import odin.server.maps.MapleMist;
import tacos.packet.ops.OpsMobAppear;
import tacos.packet.response.ResCAffectedAreaPool;
import tacos.packet.response.ResCDropPool;
import tacos.packet.response.ResCMobPool;
import tacos.server.map.TacosSpawnPoint;

/**
 *
 * @author Riremito
 */
public class MapTask {

    private static final int DROP_ITEM_EXPIRED = 120000;

    public static boolean update(MapleCharacter chr, MapleMap map, long time) {
        if (!map.updateTime(time, 5000)) {
            return false;
        }
        // drop removal.
        for (MapleMapItem mmi : map.getAllItems()) {
            if (mmi.getTime() + DROP_ITEM_EXPIRED < time) {
                map.removeDrop(mmi.getObjectId());
                map.broadcastMessage(ResCDropPool.DropLeaveField(mmi, ResCDropPool.DropLeaveType.EXPIRED));
            }
        }
        // mob respawn.
        for (TacosSpawnPoint sp : chr.getMap().getMonsterSpawnPoint()) {
            if (sp.getLastRegenTime() + map.getCreateMobInterval() <= time) {
                MapleMonster monster = sp.regen(map);
                if (monster != null) {
                    map.addMonster(monster);
                    map.broadcastMessage(ResCMobPool.MobEnterField(monster));
                    chr.SendPacket(ResCMobPool.MobChangeController(monster, false));
                    monster.setAT(OpsMobAppear.MOBAPPEAR_NORMAL);
                    monster.setATEx(OpsMobAppear.MOBAPPEAR_NORMAL.get());
                }
            }
        }
        // mist.
        for (MapleMist mist : map.getAllMists()) {
            // TODO : fix interval.
            switch (mist.isPoisonMist()) {
                case 1 -> {
                    for (MapleMonster monster : map.getMonstersInRect(mist.getBox())) {
                        if (mist.makeChanceResult()) {
                            int max_hp = (int) monster.getMobMaxHp();
                            int damage = max_hp / (70 - mist.getSkillLevel());
                            //monster.applyStatus(map.getCharacterById(mist.getOwnerId()), new MonsterStatusEffect(MonsterStatus.POISON, 1, mist.getSourceSkill().getId(), null, false), true, mist.getDuration(), false);
                            monster.setHp(Math.max(1, monster.getHp() - damage));
                            map.broadcastMessage(ResCMobPool.MobDamaged(monster, damage, 0));
                            /*
                            if (mist.getOwnerId() == chr.getId()) {
                                chr.SendPacket(ResCMobPool.MobHPIndicator(monster, (int) Math.ceil(monster.getHp() * 100.0 / max_hp)));
                            }
                             */
                        }
                    }
                }
                case 2 -> {
                    /*
                    for (Object player : map.getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER))) {
                        if (mist.makeChanceResult()) {
                            ((MapleCharacter) player).addMP((int) (mist.getSource().getX() * (((MapleCharacter) player).getStat().getMaxMp() / 100.0)));
                        }
                    }
                     */
                }
                case 3 -> {
                }
                default -> {
                }
            }
            // mist removal.
            if (mist.getTimeRemoval() < time) {
                map.removeMist(mist.getObjectId());
                map.broadcastMessage(ResCAffectedAreaPool.AffectedAreaRemoved(mist));
            }
        }
        return true;
    }
}
