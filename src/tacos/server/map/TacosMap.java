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
package tacos.server.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import odin.client.MapleCharacter;
import odin.server.MapleSquad;
import odin.server.life.Spawns;
import odin.server.maps.MapleMapEffect;
import odin.server.maps.MapleMapObject;
import odin.server.maps.MapleMapObjectType;
import odin.server.maps.MapleNodes;
import odin.tools.Pair;
import tacos.server.ServerOdinGame;

/**
 *
 * @author Riremito
 */
public class TacosMap extends TacosMapData {

    protected int channel;
    protected float monsterRate;
    protected Map<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> mapobjects;
    protected int runningOid = 100000;
    protected List<MapleCharacter> characters = new ArrayList<>();
    protected List<Spawns> monsterSpawn = new ArrayList<>();
    protected AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
    protected int createMobInterval = 9000;
    protected long lastSpawnTime = 0;
    protected boolean isSpawns = true;
    protected int maxRegularSpawn = 0;
    protected int fixedMob;
    protected String onUserEnter;
    protected String onFirstUserEnter;
    protected boolean everlast = false;
    protected MapleNodes nodes;
    protected Map<String, Integer> environment = new LinkedHashMap<>();
    protected long speedRunStart = 0;
    protected String speedRunLeader = "";
    protected boolean squadTimer = false;
    protected String squad = "";
    protected ScheduledFuture<?> squadSchedule;
    protected MapleMapEffect mapEffect;
    private long lastHurtTime = 0;
    private int consumeItemCoolTime = 0;
    private boolean personalShop;
    private boolean soaring = false;
    // no idea.
    protected Lock mutex = new ReentrantLock();
    protected ReentrantReadWriteLock charactersLock = new ReentrantReadWriteLock();
    protected Map<MapleMapObjectType, ReentrantReadWriteLock> mapobjectlocks;

    public TacosMap(int mapid, int channel, int returnMapId, float monsterRate) {
        super(mapid, returnMapId);
        this.channel = channel;
        this.monsterRate = monsterRate;

        EnumMap<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> objsMap = new EnumMap<>(MapleMapObjectType.class);
        EnumMap<MapleMapObjectType, ReentrantReadWriteLock> objlockmap = new EnumMap<>(MapleMapObjectType.class);
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            objsMap.put(type, new LinkedHashMap<>());
            objlockmap.put(type, new ReentrantReadWriteLock());
        }
        this.mapobjects = Collections.unmodifiableMap(objsMap);
        this.mapobjectlocks = Collections.unmodifiableMap(objlockmap);
    }

    public int getChannel() {
        return this.channel;
    }

    public void setCreateMobInterval(int createMobInterval) {
        this.createMobInterval = createMobInterval;
    }

    public void setSpawns(final boolean fm) {
        this.isSpawns = fm;
    }

    public boolean canSpawn() {
        // 即沸き
        return this.lastSpawnTime == 0 || (this.lastSpawnTime > 0 && this.isSpawns && this.lastSpawnTime + this.createMobInterval < System.currentTimeMillis());
    }

    public List<Spawns> getMonsterSpawn() {
        return this.monsterSpawn;
    }

    public void setFixedMob(int fm) {
        this.fixedMob = fm;
    }

    public void setUserEnter(String onUserEnter) {
        this.onUserEnter = onUserEnter;
    }

    public void setFirstUserEnter(String onFirstUserEnter) {
        this.onFirstUserEnter = onFirstUserEnter;
    }

    public void setEverlast(boolean everlast) {
        this.everlast = everlast;
    }

    public boolean getEverlast() {
        return this.everlast;
    }

    public void setNodes(MapleNodes mn) {
        this.nodes = mn;
    }

    public List<MapleNodes.MaplePlatform> getPlatforms() {
        return this.nodes.getPlatforms();
    }

    public Collection<MapleNodes.MapleNodeInfo> getNodes() {
        return this.nodes.getNodes();
    }

    public MapleNodes.MapleNodeInfo getNode(int index) {
        return this.nodes.getNode(index);
    }

    public List<Pair<Integer, Integer>> getMobsToSpawn() {
        return this.nodes.getMobsToSpawn();
    }

    public List<Integer> getSkillIds() {
        return this.nodes.getSkillIds();
    }

    public Map<String, Integer> getEnvironment() {
        return this.environment;
    }

    public void startSpeedRun(String leader) {
        this.speedRunStart = System.currentTimeMillis();
        this.speedRunLeader = leader;
    }

    public void endSpeedRun() {
        this.speedRunStart = 0;
        this.speedRunLeader = "";
    }

    public final MapleSquad getSquadBegin() {
        if (this.squad.length() > 0) {
            return ServerOdinGame.getInstance(this.channel).getMapleSquad(this.squad);
        }
        return null;
    }

    public void setSquad(String squad) {
        this.squad = squad;
    }

    public final void cancelSquadSchedule() {
        this.squadTimer = false;
        if (this.squadSchedule != null) {
            this.squadSchedule.cancel(false);
            this.squadSchedule = null;
        }
    }

    @Override
    public void setHPDec(int delta) {
        super.setHPDec(delta);
        if (0 < delta || mapid == 749040100) {
            this.lastHurtTime = System.currentTimeMillis();
        }
    }

    public boolean canHurt() {
        if (this.lastHurtTime == 0) {
            return false;
        }
        if (this.lastHurtTime + decHPInterval < System.currentTimeMillis()) {
            this.lastHurtTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public int getConsumeItemCoolTime() {
        return consumeItemCoolTime;
    }

    public void setConsumeItemCoolTime(int ciit) {
        this.consumeItemCoolTime = ciit;
    }

    public boolean allowPersonalShop() {
        return this.personalShop;
    }

    public void setPersonalShop(boolean personalShop) {
        this.personalShop = personalShop;
    }

    public void setSoaring(boolean soaring) {
        this.soaring = soaring;
    }

    // unused
    public boolean canSoar() {
        return this.soaring;
    }

}
