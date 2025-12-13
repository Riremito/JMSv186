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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import odin.client.MapleCharacter;
import odin.server.MapleSquad;
import odin.server.life.MapleMonster;
import odin.server.life.MapleNPC;
import odin.server.life.Spawns;
import odin.server.maps.MapleDynamicPortal;
import odin.server.maps.MapleMapEffect;
import odin.server.maps.MapleMapItem;
import odin.server.maps.MapleMapObject;
import odin.server.maps.MapleMapObjectType;
import odin.server.maps.MapleNodes;
import odin.server.maps.MapleReactor;
import odin.server.maps.MapleSummon;
import odin.tools.Pair;
import tacos.debug.DebugLogger;
import tacos.network.MaplePacket;
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

    // object
    public List<MapleMapObject> getMapObjects(MapleMapObjectType type) {
        List<MapleMapObject> mmos = new ArrayList<>();
        for (MapleMapObject mmo : this.mapobjects.get(type).values()) {
            mmos.add(mmo);
        }
        return mmos;
    }

    public MapleMapObject getMapObject(int oid, MapleMapObjectType type) {
        return this.mapobjects.get(type).get(oid);
    }

    public void addMapObject(MapleMapObject mapobject) {
        this.runningOid++;
        mapobject.setObjectId(this.runningOid);
        this.mapobjects.get(mapobject.getType()).put(this.runningOid, mapobject);
    }

    public void spawnRangedMapObject(MapleMapObject mapobject, MaplePacket packet) {
        for (MapleCharacter player : this.characters) {
            if (player.isClone()) {
                continue;
            }
            if (player.getViewRangeSq() < player.getPosition().distanceSq(mapobject.getPosition())) {
                continue;
            }
            // visible object
            player.addVisibleMapObject(mapobject);
            // send spawn packet
            if (packet == null) {
                continue;
            }
            if (mapobject.getType() == MapleMapObjectType.SUMMON) {
                MapleSummon summon = (MapleSummon) mapobject;
                if (summon.isChangedMap() && summon.getOwnerId() != player.getId()) {
                    continue;
                }
            }
            if (mapobject.getType() == MapleMapObjectType.ITEM) {
                MapleMapItem mitem = (MapleMapItem) mapobject;
                if (0 < mitem.getQuest() && player.getQuestStatus(mitem.getQuest()) != 1) {
                    continue;
                }
            }
            player.SendPacket(packet);
        }
    }

    public void updateMapObject(MapleMapObject mapobject) {
        for (MapleCharacter player : this.characters) {
            if (player.isClone()) {
                continue;
            }
            updateMapObjectVisibility(player, mapobject);
        }
    }

    public boolean updateMapObjectVisibility(MapleCharacter player, MapleMapObject mapobject) {
        if (player == null || player.isClone()) {
            return false;
        }

        boolean already_visible = player.isMapObjectVisible(mapobject);
        // hide
        if (mapobject.getType() != MapleMapObjectType.SUMMON && player.getViewRangeSq() < player.getPosition().distanceSq(mapobject.getPosition())) {
            if (already_visible) {
                player.removeVisibleMapObject(mapobject);
                mapobject.sendDestroyData(player.getClient()); // packet
            }
            return true;
        }
        // show
        if (!already_visible) {
            player.addVisibleMapObject(mapobject);
            mapobject.sendSpawnData(player.getClient()); // packet
        }

        return true;
    }

    public void removeMapObject(MapleMapObject obj) {
        this.mapobjects.get(obj.getType()).remove(obj.getObjectId());
    }

    public List<MapleMapObject> getMapObjectsInRange(Point from, double rangeSq) {
        List<MapleMapObject> ret = new ArrayList<>();
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            Iterator<MapleMapObject> itr = this.mapobjects.get(type).values().iterator();
            while (itr.hasNext()) {
                MapleMapObject mmo = itr.next();
                if (from.distanceSq(mmo.getPosition()) <= rangeSq) {
                    ret.add(mmo);
                }
            }
        }
        return ret;
    }

    public List<MapleMapObject> getMapObjectsInRange(Point from, double rangeSq, List<MapleMapObjectType> MapObject_types) {
        List<MapleMapObject> ret = new ArrayList<>();
        for (MapleMapObjectType type : MapObject_types) {
            Iterator<MapleMapObject> ltr = this.mapobjects.get(type).values().iterator();
            MapleMapObject obj;
            while (ltr.hasNext()) {
                obj = ltr.next();
                if (from.distanceSq(obj.getPosition()) <= rangeSq) {
                    ret.add(obj);
                }
            }
        }
        return ret;
    }

    public List<MapleMapObject> getMapObjectsInRect(Rectangle box, List<MapleMapObjectType> MapObject_types) {
        List<MapleMapObject> ret = new ArrayList<>();
        for (MapleMapObjectType type : MapObject_types) {
            Iterator<MapleMapObject> ltr = this.mapobjects.get(type).values().iterator();
            MapleMapObject obj;
            while (ltr.hasNext()) {
                obj = ltr.next();
                if (box.contains(obj.getPosition())) {
                    ret.add(obj);
                }
            }
        }
        return ret;
    }

    public List<MapleCharacter> getPlayersInRectAndInList(Rectangle box, List<MapleCharacter> chrList) {
        List<MapleCharacter> character = new LinkedList<>();
        Iterator<MapleCharacter> ltr = this.characters.iterator();
        MapleCharacter a;
        while (ltr.hasNext()) {
            a = ltr.next();
            if (chrList.contains(a) && box.contains(a.getPosition())) {
                character.add(a);
            }
        }
        return character;
    }

    public List<MapleCharacter> getCharacters() {
        List<MapleCharacter> chars = new ArrayList<>();
        for (MapleCharacter mc : this.characters) {
            chars.add(mc);
        }
        return chars;
    }

    public MapleCharacter getCharacterById(int id) {
        for (MapleCharacter mc : this.characters) {
            if (mc.getId() == id) {
                return mc;
            }
        }
        return null;
    }

    public int characterSize() {
        return this.characters.size();
    }

    public int getCharactersSize() {
        int ret = 0;
        final Iterator<MapleCharacter> ltr = characters.iterator();
        MapleCharacter chr;
        while (ltr.hasNext()) {
            chr = ltr.next();
            if (!chr.isClone()) {
                ret++;
            }
        }
        return ret;
    }

    public void spawnPlayers(MapleCharacter chr) {
        for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.PLAYER).values()) {
            ((MapleCharacter) obj).sendSpawnData(chr.getClient());
        }
    }

    public MapleSummon getSummonByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.SUMMON);
        if (mmo == null) {
            return null;
        }
        return (MapleSummon) mmo;
    }

    public List<MapleMonster> getAllMonsters() {
        ArrayList<MapleMonster> ret = new ArrayList<>();
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.MONSTER).values()) {
            ret.add((MapleMonster) mmo);
        }
        return ret;
    }

    public MapleMonster getMonsterById(int id) {
        MapleMonster ret = null;
        Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.MONSTER).values().iterator();
        while (itr.hasNext()) {
            MapleMonster n = (MapleMonster) itr.next();
            if (n.getId() == id) {
                ret = n;
                break;
            }
        }
        return ret;
    }

    public int countMonsterById(int id) {
        int ret = 0;
        Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.MONSTER).values().iterator();
        while (itr.hasNext()) {
            MapleMonster n = (MapleMonster) itr.next();
            if (n.getId() == id) {
                ret++;
            }
        }
        return ret;
    }

    public MapleMonster getMonsterByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.MONSTER);
        if (mmo == null) {
            return null;
        }
        return (MapleMonster) mmo;
    }

    public int getNumMonsters() {
        return mapobjects.get(MapleMapObjectType.MONSTER).size();
    }

    public List<MapleNPC> getAllNPCs() {
        ArrayList<MapleNPC> ret = new ArrayList<>();
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.NPC).values()) {
            ret.add((MapleNPC) mmo);
        }
        return ret;
    }

    public boolean containsNPC(int npcid) {
        Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.NPC).values().iterator();
        while (itr.hasNext()) {
            MapleNPC n = (MapleNPC) itr.next();
            if (n.getId() == npcid) {
                return true;
            }
        }
        return false;
    }

    public MapleNPC getNPCById(int id) {
        Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.NPC).values().iterator();
        while (itr.hasNext()) {
            MapleNPC n = (MapleNPC) itr.next();
            if (n.getId() == id) {
                return n;
            }
        }
        return null;
    }

    public MapleNPC getNPCByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.NPC);
        if (mmo == null) {
            return null;
        }
        return (MapleNPC) mmo;
    }

    public List<MapleMapObject> getAllHiredMerchants() {
        ArrayList<MapleMapObject> ret = new ArrayList<>();
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.HIRED_MERCHANT).values()) {
            ret.add(mmo);
        }
        return ret;
    }

    public void spawnMerchant(MapleCharacter chr) {
        for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.HIRED_MERCHANT).values()) {
            obj.sendSpawnData(chr.getClient());
        }
    }

    public List<MapleMapItem> getAllItems() {
        ArrayList<MapleMapItem> ret = new ArrayList<>();
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.ITEM).values()) {
            ret.add((MapleMapItem) mmo);
        }
        return ret;
    }

    public int getItemsSize() {
        return this.mapobjects.get(MapleMapObjectType.ITEM).size();
    }

    public List<MapleMapObject> getAllDoors() {
        ArrayList<MapleMapObject> ret = new ArrayList<>();
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.DOOR).values()) {
            ret.add(mmo);
        }
        return ret;
    }

    public List<MapleReactor> getAllReactors() {
        ArrayList<MapleReactor> ret = new ArrayList<>();
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            ret.add((MapleReactor) mmo);
        }
        return ret;
    }

    public MapleReactor getReactorById(int id) {
        MapleReactor ret = null;
        Iterator<MapleMapObject> itr = this.mapobjects.get(MapleMapObjectType.REACTOR).values().iterator();
        while (itr.hasNext()) {
            MapleReactor n = (MapleReactor) itr.next();
            if (n.getReactorId() == id) {
                ret = n;
                break;
            }
        }
        return ret;
    }

    public MapleReactor getReactorByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.REACTOR);
        if (mmo == null) {
            return null;
        }
        return (MapleReactor) mmo;
    }

    public MapleReactor getReactorByName(final String name) {
        for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            MapleReactor mr = ((MapleReactor) obj);
            if (mr.getName().equalsIgnoreCase(name)) {
                return mr;
            }
        }
        return null;
    }

    public void resetReactors() {
        setReactorState((byte) 0);
    }

    // unused
    public void setReactorState() {
        setReactorState((byte) 1);
    }

    public void setReactorState(byte state) {
        for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            ((MapleReactor) obj).forceHitReactor((byte) state);
        }
    }

    public void shuffleReactors() {
        shuffleReactors(0, 9999999); //all
    }

    public void shuffleReactors(int first, int last) {
        List<Point> points = new ArrayList<>();
        for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            MapleReactor mr = (MapleReactor) obj;
            if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                points.add(mr.getPosition());
            }
        }
        Collections.shuffle(points);
        for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            MapleReactor mr = (MapleReactor) obj;
            if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                mr.setPosition(points.remove(points.size() - 1));
            }
        }
    }

    public void spawnDynamicPortal(MapleCharacter chr) {
        for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.DYNAMIC_PORTAL).values()) {
            ((MapleDynamicPortal) obj).sendSpawnPacket(chr.getClient());
        }
    }

    public MapleDynamicPortal findDynamicPortal(int portal_id) {
        for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.DYNAMIC_PORTAL).values()) {
            MapleDynamicPortal dynamic_portal = (MapleDynamicPortal) obj;
            if (dynamic_portal.getObjectId() == portal_id) {
                return dynamic_portal;
            }
        }
        return null;
    }

    public MapleDynamicPortal findDynamicPortalLink(int map_id_to) {
        DebugLogger.InfoLog("findDynamicPortalLink map_id_to" + map_id_to);
        for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.DYNAMIC_PORTAL).values()) {
            MapleDynamicPortal dynamic_portal = (MapleDynamicPortal) obj;

            DebugLogger.InfoLog("findDynamicPortalLink obj_to" + dynamic_portal.getMapID());
            if (dynamic_portal.getMapID() == map_id_to) {
                return dynamic_portal;
            }
        }
        return null;
    }

    // self and other players in range.
    public void broadcastMessage(MaplePacket packet, Point rangedFrom) {
        broadcastMessageInternal(null, packet, rangedFrom, false);
    }

    // other players in range.
    public void broadcastMessageTo(MapleCharacter source, MaplePacket packet, Point rangedFrom) {
        broadcastMessageInternal(source, packet, rangedFrom, false);
    }

    // self and other players.
    public void broadcastMessage(MaplePacket packet) {
        broadcastMessageInternal(null, packet, null, true);
    }

    // self and other players, or other players.
    public void broadcastMessage(MapleCharacter source, MaplePacket packet, boolean repeatToSource) {
        broadcastMessageInternal(repeatToSource ? null : source, packet, source.getPosition(), true);
    }

    private void broadcastMessageInternal(MapleCharacter source, MaplePacket packet, Point rangedFrom, boolean ignoreRange) {
        Iterator<MapleCharacter> ltr = characters.iterator();
        MapleCharacter chr;
        while (ltr.hasNext()) {
            chr = ltr.next();
            if (chr != source) {
                if (ignoreRange || rangedFrom.distanceSq(chr.getPosition()) <= chr.getViewRangeSq()) {
                    chr.SendPacket(packet);
                }
            }
        }
    }

    // unused
    public boolean canSoar() {
        return this.soaring;
    }

}
