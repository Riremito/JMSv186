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
package server.life;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;
import packet.response.wrapper.ResWrapper;

import server.MapleCarnivalFactory;
import server.MapleCarnivalFactory.MCSkill;
import server.maps.MapleMap;
import server.maps.MapleReactor;

public class SpawnPoint extends Spawns {

    private MapleMonster monster;
    private Point pos;
    private long nextPossibleSpawn;
    private int mobTime, carnival = -1;
    private AtomicInteger spawnedMonsters = new AtomicInteger(0);
    private boolean immobile;
    private String msg;
    private byte carnivalTeam;

    public SpawnPoint(final MapleMonster monster, final Point pos, final int mobTime, final byte carnivalTeam, final String msg) {
        this.monster = monster;
        this.pos = pos;
        // 10分間隔未満は通常処理
        if (mobTime < 600 || !monster.getStats().isBoss()) {
            this.mobTime = (mobTime < 0 ? -1 : (mobTime * 1000));
        } else {
            // 10分以上の間隔の場合は1秒に設定
            this.mobTime = 1000;
        }
        this.carnivalTeam = carnivalTeam;
        if (msg != null || !monster.getStats().isBoss()) {
            this.msg = msg;
        } else {
            // ボス出現メッセージがない場合のメッセージ
            this.msg = monster.getStats().getName() + "が現れました。";
        }
        this.immobile = !monster.getStats().getMobile();
        this.nextPossibleSpawn = System.currentTimeMillis();
    }

    public final void setCarnival(int c) {
        this.carnival = c;
    }

    @Override
    public final Point getPosition() {
        return pos;
    }

    @Override
    public final MapleMonster getMonster() {
        return monster;
    }

    @Override
    public final byte getCarnivalTeam() {
        return carnivalTeam;
    }

    @Override
    public final int getCarnivalId() {
        return carnival;
    }

    @Override
    public final boolean shouldSpawn() {
        if (mobTime < 0) {
            return false;
        }
        // regular spawnpoints should spawn a maximum of 3 monsters; immobile spawnpoints or spawnpoints with mobtime a
        // maximum of 1
        if (((mobTime != 0 || immobile) && spawnedMonsters.get() > 0) || spawnedMonsters.get() > 1) {
            return false;
        }
        return nextPossibleSpawn <= System.currentTimeMillis();
    }

    @Override
    public final MapleMonster spawnMonster(final MapleMap map) {
        final MapleMonster mob = new MapleMonster(monster);
        mob.setPosition(pos);
        mob.setCarnivalTeam(carnivalTeam);
        spawnedMonsters.incrementAndGet();
        mob.addListener(new MonsterListener() {

            @Override
            public void monsterKilled() {
                nextPossibleSpawn = System.currentTimeMillis();

                if (mobTime > 0) {
                    nextPossibleSpawn += mobTime;
                }
                spawnedMonsters.decrementAndGet();
            }
        });
        map.spawnMonster(mob, -2);
        if (carnivalTeam > -1) {
            for (MapleReactor r : map.getAllReactorsThreadsafe()) { //parsing through everytime a monster is spawned? not good idea
                if (r.getName().startsWith(String.valueOf(carnivalTeam)) && r.getReactorId() == (9980000 + carnivalTeam) && r.getState() < 5) {
                    final int num = Integer.parseInt(r.getName().substring(1, 2)); //00, 01, etc
                    final MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
                    if (skil != null) {
                        skil.getSkill().applyEffect(null, mob, false);
                    }
                }
            }
        }
        if (msg != null) {
            map.broadcastMessage(ResWrapper.BroadCastMsgNotice(msg));
            if (mob.getStats().isBoss()) {
                // TODO : replace to DebugMsg
                map.broadcastMessage(ResWrapper.BroadCastMsgNotice("Normal Boss Mob = " + monster.getId() + ", MapID = " + map.getId()));
            }
        }
        return mob;
    }

    @Override
    public final int getMobTime() {
        return mobTime;
    }
}
