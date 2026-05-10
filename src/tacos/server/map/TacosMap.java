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
import java.util.Calendar;
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
import odin.client.inventory.Equip;
import odin.client.inventory.IItem;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.handling.world.PartyOperation;
import tacos.odin.OdinEventManager;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleSquad;
import odin.server.Timer.MapTimer;
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleMonster;
import odin.server.life.MapleNPC;
import odin.server.life.Spawns;
import odin.server.maps.MapScriptMethods;
import odin.server.maps.MapleDoor;
import odin.server.maps.MapleDynamicPortal;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapEffect;
import odin.server.maps.MapleMapItem;
import odin.server.maps.MapleMapObject;
import odin.server.maps.MapleMapObjectType;
import odin.server.maps.MapleMist;
import odin.server.maps.MapleNodes;
import odin.server.maps.MapleReactor;
import odin.server.maps.MapleSummon;
import odin.server.shops.HiredMerchant;
import tacos.client.TacosCharacter;
import tacos.client.TacosDragon;
import tacos.client.TacosSkillPet;
import tacos.constants.TacosConstants;
import tacos.odin.OdinPair;
import tacos.debug.DebugLogger;
import tacos.network.MaplePacket;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.ResCAffectedAreaPool;
import tacos.packet.response.ResCDropPool;
import tacos.packet.response.ResCEmployeePool;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCMobPool;
import tacos.packet.response.ResCNpcPool;
import tacos.packet.response.ResCReactorPool;
import tacos.packet.response.ResCSummonedPool;
import tacos.packet.response.ResCTownPortalPool;
import tacos.packet.response.ResCUserPool;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.ResCUser_Dragon;
import tacos.packet.response.ResCUser_SkillPet;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.Res_JMS_CInstancePortalPool;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.script.TacosScriptEvent;
import tacos.server.TacosWorld;
import tacos.unofficial.CustomMonsterBookDrop;

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

    public List<OdinPair<Integer, Integer>> getMobsToSpawn() {
        return this.nodes.getMobsToSpawn();
    }

    public List<Integer> getSkillIds() {
        return this.nodes.getSkillIds();
    }

    public Map<String, Integer> getEnvironment() {
        return this.environment;
    }

    public final MapleSquad getSquadBegin() {
        if (this.squad.length() > 0) {
            return TacosWorld.find(0).getChannelServer(this.channel).getMapleSquad(this.squad); // TODO : fix
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

    public void removeMapObject(MapleMapObject obj) {
        this.mapobjects.get(obj.getType()).remove(obj.getObjectId());
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

    private String fe_change_bgm = "";

    public void setChangeBGM(String wz_path) {
        this.fe_change_bgm = wz_path;
        if (!getChangeBGM().equals("")) {
            broadcastMessage(ResWrapper.musicChange(getChangeBGM()));
        }
    }

    public String getChangeBGM() {
        return this.fe_change_bgm;
    }

    public void sendChangeBGM(TacosCharacter chr) {
        if (!getChangeBGM().equals("")) {
            chr.SendPacket(ResWrapper.musicChange(getChangeBGM()));
        }
    }

    public void SplitSendPacket(int x, int y) {
        int number = this.map_split.getSplitMap(x, y);
        int row = number / this.map_split.getCol();
        int col = number % this.map_split.getCol();

        for (int i = 0; i < this.map_split.getRow(); i++) {
            if (i < (row - 1) || (row + 1) < i) {
                continue;
            }
            for (int j = 0; j < this.map_split.getCol(); j++) {
                if (j < (col - 1) || (col + 1) < j) {
                    continue;
                }
            }
        }
    }

    public List<Integer> getStateList() {
        List<Integer> state = new ArrayList<>();
        for (int i = 0; i < this.map_split.getSplit(); i++) {
            state.add(0);
        }
        return state;
    }

    public boolean sendInitialization(MapleCharacter chr) {
        // user enter script.
        if (!onFirstUserEnter.equals("")) {
            if (getCharactersSize() == 1) {
                MapScriptMethods.startScript_FirstUser(chr.getClient(), onFirstUserEnter);
            }
        }
        if (!onUserEnter.equals("")) {
            MapScriptMethods.startScript_User(chr.getClient(), onUserEnter);
        }
        // station clock.
        if (hasClock()) {
            // 101000300
            Calendar cal = Calendar.getInstance();
            chr.SendPacket((ResCField.Clock(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND))));
        }
        if (TacosConstants.is_coconut(mapid)) {
            chr.setCoconutTeam(getCharactersSize() % 2);
        }
        if (TacosConstants.is_coconut(mapid) || TacosConstants.is_bath(mapid)) {
            chr.SendPacket(ResCField.FieldSpecificData(chr));
            return true;
        }
        if (TacosConstants.is_aran_tutorial(mapid)) {
            chr.setForcedStatAran();
            chr.SendPacket(ResCWvsContext.ForcedStatSet(chr));
            return true;
        }
        return false;
    }

    public boolean updateParty(MapleCharacter chr) {
        if (chr.getParty() == null) {
            return false;
        }
        chr.silentPartyUpdate();
        chr.SendPacket(ResCWvsContext.PartyResult(chr.getClient().getChannelId(), chr.getParty(), PartyOperation.SILENT_UPDATE, null));
        chr.updatePartyMemberHP();
        chr.receivePartyMemberHP();
        return true;
    }

    public boolean sendExpedition(MapleCharacter chr, MapleMonster monster) {
        int boss_id = 0;
        if (monster != null) {
            boss_id = monster.getId();
        }
        TacosWorld world = chr.getWorld();
        int exit_timer = 5 * 60;

        switch (mapid) {
            case TacosConstants.MAP_ID_ZAKUM: {
                if (boss_id == TacosConstants.MOB_ID_ZAKUM) {
                    broadcastMessage(ResCField.ZakumTimer(true, 5));
                    broadcastMessage(ResCField.Clock(exit_timer));
                    return true;
                }
                broadcastMessage(ResCField.ZakumTimer(false, 5));
                return true;
            }
            case TacosConstants.MAP_ID_HORNTAIL: {
                if (boss_id == TacosConstants.MOB_ID_HORNTAIL) {
                    // 大変な挑戦の終わりにホンテールを撃破した遠征隊よ！貴方達が本当のリプレの英雄だ！ (JMS164)
                    // 大変な挑戦の終わりにホーンテイルを撃破した遠征隊よ！貴方達が本当のリプレの英雄だ！ (JMS302)
                    world.broadcastPacket(ResWrapper.BroadCastMsgNotice("大変な挑戦の終わりにホーンテイルを撃破した遠征隊よ！貴方達が本当のリプレの英雄だ！"));
                    broadcastMessage(ResCField.Clock(exit_timer));
                    broadcastMessage(ResCField.HontaleTimer(true, 5));
                    return true;
                }
                broadcastMessage(ResCField.HontaleTimer(false, 5));
                return true;
            }
            case TacosConstants.MAP_ID_PINKBEAN: {
                if (boss_id == TacosConstants.MOB_ID_PINKBEAN) {
                    // 不屈の闘志でピンクビーンを退けた遠征隊の諸君！　君たちが真の時間の覇者だ！ (JMS164-302)
                    world.broadcastPacket(ResWrapper.BroadCastMsgNotice("不屈の闘志でピンクビーンを退けた遠征隊の諸君！　君たちが真の時間の覇者だ！"));
                    broadcastMessage(ResCField.Clock(exit_timer));
                    return true;
                }
                return true;
            }
            case TacosConstants.MAP_ID_CHAOS_ZAKUM: {
                if (boss_id == TacosConstants.MOB_ID_CHAOS_ZAKUM) {
                    broadcastMessage(ResCField.ChaosZakumTimer(true, 5));
                    broadcastMessage(ResCField.Clock(exit_timer));
                    return true;
                }
                broadcastMessage(ResCField.ChaosZakumTimer(false, 5));
                return true;
            }
            case TacosConstants.MAP_ID_CHAOS_HORNTAIL: {
                if (boss_id == TacosConstants.MOB_ID_CHAOS_HORNTAIL) {
                    world.broadcastPacket(ResWrapper.BroadCastMsgNotice("大変な挑戦の終わりにホーンテイルを撃破した遠征隊よ！貴方達が本当のリプレの英雄だ！"));
                    broadcastMessage(ResCField.Clock(exit_timer));
                    broadcastMessage(ResCField.HontaleTimer(true, 5));
                    return true;
                }
                broadcastMessage(ResCField.HontaleTimer(false, 5));
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public boolean sendMapEffect(MapleCharacter chr) {
        if (mapEffect == null) {
            return false;
        }
        mapEffect.sendStartData(chr);
        return true;
    }

    public void userEnterField(MapleCharacter chr) {
        this.characters.add(chr);
        this.mapobjects.get(MapleMapObjectType.PLAYER).put(chr.getObjectId(), chr); // object id.

        // no split.
        sendChangeBGM(chr);
        if (!getPlatforms().isEmpty()) {
            chr.SendPacket(ResCField.FootHoldInfo(this));
        }
        if (!environment.isEmpty()) {
            chr.SendPacket(ResCField.FieldObstacleOnOffStatus(this));
        }
        sendInitialization(chr);
        updateParty(chr);
        sendMapEffect(chr);
        if (0 < timeLimit) {
            chr.DebugMsg("timeLimit = " + timeLimit);
            if (getForcedReturnMap() != null) {
                chr.startMapTimeLimitTask(timeLimit, getForcedReturnMap());
            }
        }
        sendExpedition(chr, null);

        // split.
        List<Integer> enter_state = getStateList();
        int enter_x = chr.getPosition().x;
        int enter_y = chr.getPosition().y;
        int enter_number = this.map_split.getSplitMap(enter_x, enter_y);
        int enter_row = enter_number / this.map_split.getCol();
        int enter_col = enter_number % this.map_split.getCol();
        for (int row = 0; row < this.map_split.getRow(); row++) {
            if (row < (enter_row - 1) || (enter_row + 1) < row) {
                continue;
            }
            for (int col = 0; col < this.map_split.getCol(); col++) {
                if (col < (enter_col - 1) || (enter_col + 1) < col) {
                    continue;
                }
                enter_state.set((row * this.map_split.getCol()) + col, 1);
            }
        }

        for (MapleCharacter player : this.characters) {
            // self
            if (player.getId() == chr.getId()) {
                continue;
            }
            int player_number = this.map_split.getSplitMap(player.getPosition().x, player.getPosition().y);
            if (this.map_split.getSplit() < player_number) {
                continue;
            }
            int player_state = enter_state.get(player_number);
            if ((player_state & 1) != 0) {
                player.SendPacket(ResCUserPool.UserEnterField(chr));
                chr.SendPacket(ResCUserPool.UserEnterField(player));
            }
        }
        // mob
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.MONSTER).values()) {
            MapleMonster mob = (MapleMonster) mmo;
            int number = this.map_split.getSplitMap(mob.getPosition().x, mob.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = enter_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(ResCMobPool.MobEnterField(mob, -1, 0, 0));
                if (mob.getController() == null || mob.getController() == chr) {
                    mob.setController(chr);
                    chr.SendPacket(ResCMobPool.MobChangeController(mob, false, mob.isFirstAttack()));
                    chr.controlMonster(mob, mob.isFirstAttack());
                    mob.setControllerHasAggro(mob.isFirstAttack());
                    mob.setControllerKnowsAboutAggro(mob.isFirstAttack());
                }
            }
        }
        // npc
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.NPC).values()) {
            MapleNPC npc = (MapleNPC) mmo;
            int number = this.map_split.getSplitMap(npc.getPosition().x, npc.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = enter_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(ResCNpcPool.NpcEnterField(npc, true));
                //chr.SendPacket(ResCNpcPool.NpcChangeController(npc, true, true));
            }
        }
        // hired merchant
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.HIRED_MERCHANT).values()) {
            HiredMerchant employee = (HiredMerchant) mmo;
            int number = this.map_split.getSplitMap(employee.getPosition().x, employee.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = enter_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(ResCEmployeePool.EmployeeEnterField(employee));
            }
        }
        // drop
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.ITEM).values()) {
            MapleMapItem drop = (MapleMapItem) mmo;
            // quest item.
            int quest_id = drop.getQuest();
            if (0 < quest_id) {
                if (chr.getQuestStatus(quest_id) != 1) {
                    continue;
                }
            }
            int number = this.map_split.getSplitMap(drop.getPosition().x, drop.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = enter_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(ResCDropPool.DropEnterField(drop, ResCDropPool.EnterType.NO_ANIMATION, drop.getPosition()));
            }
        }
        // mist
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.MIST).values()) {
            MapleMist mist = (MapleMist) mmo;
            int number = this.map_split.getSplitMap(mist.getPosition().x, mist.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = enter_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(ResCAffectedAreaPool.AffectedAreaCreated(mist));
            }
        }
        // mystic door
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.DOOR).values()) {
            MapleDoor door = (MapleDoor) mmo;
            int number = this.map_split.getSplitMap(door.getPosition().x, door.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = enter_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(ResCTownPortalPool.TownPortalCreated(door.getLink(), false));
            }
        }
        // mechanic gate
        // pinkbean cake event portal
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.DYNAMIC_PORTAL).values()) {
            MapleDynamicPortal instance_portal = (MapleDynamicPortal) mmo;
            int number = this.map_split.getSplitMap(instance_portal.getPosition().x, instance_portal.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = enter_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(Res_JMS_CInstancePortalPool.InstancePortalCreated(instance_portal));
            }
        }
        // reactor
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            MapleReactor reactor = (MapleReactor) mmo;
            int number = this.map_split.getSplitMap(reactor.getPosition().x, reactor.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = enter_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(ResCReactorPool.ReactorEnterField(reactor));
            }
        }
    }

    public void userLeaveField(MapleCharacter chr) {
        this.characters.remove(chr);
        removeMapObject(chr);

        List<Integer> leave_state = getStateList();
        int leave_x = chr.getPosition().x;
        int leave_y = chr.getPosition().y;
        int leave_number = this.map_split.getSplitMap(leave_x, leave_y);
        int leave_row = leave_number / this.map_split.getCol();
        int leave_col = leave_number % this.map_split.getCol();
        for (int row = 0; row < this.map_split.getRow(); row++) {
            if (row < (leave_row - 1) || (leave_row + 1) < row) {
                continue;
            }
            for (int col = 0; col < this.map_split.getCol(); col++) {
                if (col < (leave_col - 1) || (leave_col + 1) < col) {
                    continue;
                }
                leave_state.set((row * this.map_split.getCol()) + col, 4);
            }
        }

        for (MapleCharacter player : this.characters) {
            // self
            if (player.getId() == chr.getId()) {
                continue;
            }
            int player_number = this.map_split.getSplitMap(player.getPosition().x, player.getPosition().y);
            if (this.map_split.getSplit() < player_number) {
                continue;
            }
            int player_state = leave_state.get(player_number);
            if ((player_state & 4) != 0) {
                player.SendPacket(ResCUserPool.UserLeaveField(chr.getId()));
            }
        }
        // mob
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.MONSTER).values()) {
            MapleMonster mob = (MapleMonster) mmo;
            int number = this.map_split.getSplitMap(mob.getPosition().x, mob.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = leave_state.get(number);
            if ((state & 4) != 0) {
                if (mob.getController() == chr) {
                    mob.setController(null);
                    mob.setControllerHasAggro(false);
                    mob.setControllerKnowsAboutAggro(false);
                    updateMonsterController(mob);
                }
            }
        }
    }

    public void userMove(MapleCharacter chr, ParseCMovePath move_path) {
        List<Integer> move_state = getStateList();
        int prev_x = chr.getPosition().x;
        int prev_y = chr.getPosition().y;
        int next_x = move_path.getX();
        int next_y = move_path.getY();

        int prev_number = this.map_split.getSplitMap(prev_x, prev_y);
        int prev_row = prev_number / this.map_split.getCol();
        int prev_col = prev_number % this.map_split.getCol();
        // move & leave
        for (int row = 0; row < this.map_split.getRow(); row++) {
            if (row < (prev_row - 1) || (prev_row + 1) < row) {
                continue;
            }
            for (int col = 0; col < this.map_split.getCol(); col++) {
                if (col < (prev_col - 1) || (prev_col + 1) < col) {
                    continue;
                }
                move_state.set((row * this.map_split.getCol()) + col, 2 | 4); // 2 = move, 4 = leave
            }
        }
        // enter & move
        int next_number = this.map_split.getSplitMap(next_x, next_y);
        int next_row = next_number / this.map_split.getCol();
        int next_col = next_number % this.map_split.getCol();
        for (int row = 0; row < this.map_split.getRow(); row++) {
            if (row < (next_row - 1) || (next_row + 1) < row) {
                continue;
            }
            for (int col = 0; col < this.map_split.getCol(); col++) {
                if (col < (next_col - 1) || (next_col + 1) < col) {
                    continue;
                }
                if (move_state.get((row * this.map_split.getCol()) + col) != 0) {
                    move_state.set((row * this.map_split.getCol()) + col, 2); // 2 = move
                } else {
                    move_state.set((row * this.map_split.getCol()) + col, 1 | 2); // 1 = enter, 2 = move
                }
            }
        }
        for (MapleCharacter player : this.characters) {
            // self
            if (player.getId() == chr.getId()) {
                continue;
            }
            int player_number = this.map_split.getSplitMap(player.getPosition().x, player.getPosition().y);
            if (this.map_split.getSplit() < player_number) {
                continue;
            }
            int player_state = move_state.get(player_number);
            if ((player_state & 1) != 0) {
                player.SendPacket(ResCUserPool.UserEnterField(chr));
                chr.SendPacket(ResCUserPool.UserEnterField(player));
            }
            if ((player_state & 2) != 0) {
                player.SendPacket(ResCUserRemote.Move(chr, move_path));
            }
            if ((player_state & 4) != 0) {
                player.SendPacket(ResCUserPool.UserLeaveField(chr.getId()));
                chr.SendPacket(ResCUserPool.UserLeaveField(player.getId()));
            }
        }
        // mob
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.MONSTER).values()) {
            MapleMonster mob = (MapleMonster) mmo;
            int number = this.map_split.getSplitMap(mob.getPosition().x, mob.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = move_state.get(number);

            if ((state & 1) != 0) {
                chr.SendPacket(ResCMobPool.MobEnterField(mob, -1, 0, 0));
                if (mob.getController() == null || mob.getController() == chr) {
                    mob.setController(chr);
                    chr.SendPacket(ResCMobPool.MobChangeController(mob, false, mob.isFirstAttack()));
                    chr.controlMonster(mob, mob.isFirstAttack());
                    mob.setControllerHasAggro(mob.isFirstAttack());
                    mob.setControllerKnowsAboutAggro(mob.isFirstAttack());
                }
            }
            if ((state & 4) != 0) {
                chr.SendPacket(ResCMobPool.MobLeaveField(mob, 0));
            }
        }
        // npc
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.NPC).values()) {
            MapleNPC npc = (MapleNPC) mmo;
            int number = this.map_split.getSplitMap(npc.getPosition().x, npc.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = move_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(ResCNpcPool.NpcEnterField(npc, true));
                //chr.SendPacket(ResCNpcPool.NpcChangeController(npc, true, true));
            }
            if ((state & 4) != 0) {
                chr.SendPacket(ResCNpcPool.NpcLeaveField(npc));
            }
        }
        // hired merchant
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.HIRED_MERCHANT).values()) {
            HiredMerchant employee = (HiredMerchant) mmo;
            int number = this.map_split.getSplitMap(employee.getPosition().x, employee.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = move_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(ResCEmployeePool.EmployeeEnterField(employee));
            }
            if ((state & 4) != 0) {
                chr.SendPacket(ResCEmployeePool.EmployeeLeaveField(employee));
            }
        }
        // drop
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.ITEM).values()) {
            MapleMapItem drop = (MapleMapItem) mmo;
            // quest item.
            int quest_id = drop.getQuest();
            if (0 < quest_id) {
                if (chr.getQuestStatus(quest_id) != 1) {
                    continue;
                }
            }
            int number = this.map_split.getSplitMap(drop.getPosition().x, drop.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = move_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(ResCDropPool.DropEnterField(drop, ResCDropPool.EnterType.NO_ANIMATION, drop.getPosition()));
            }
            if ((state & 4) != 0) {
                chr.SendPacket(ResCDropPool.DropLeaveField(drop, ResCDropPool.LeaveType.NO_ANIMATION));
            }
        }
        // mist
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.MIST).values()) {
            MapleMist mist = (MapleMist) mmo;
            int number = this.map_split.getSplitMap(mist.getPosition().x, mist.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = move_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(ResCAffectedAreaPool.AffectedAreaCreated(mist));
            }
            if ((state & 4) != 0) {
                chr.SendPacket(ResCAffectedAreaPool.AffectedAreaRemoved(mist));
            }
        }
        // mystic door
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.DOOR).values()) {
            MapleDoor door = (MapleDoor) mmo;
            int number = this.map_split.getSplitMap(door.getPosition().x, door.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = move_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(ResCTownPortalPool.TownPortalCreated(door, false));
            }
            if ((state & 4) != 0) {
                chr.SendPacket(ResCTownPortalPool.TownPortalRemoved(door));
            }
        }
        // mechanic gate
        // pinkbean cake event portal
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.DYNAMIC_PORTAL).values()) {
            MapleDynamicPortal instance_portal = (MapleDynamicPortal) mmo;
            int number = this.map_split.getSplitMap(instance_portal.getPosition().x, instance_portal.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = move_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(Res_JMS_CInstancePortalPool.InstancePortalCreated(instance_portal));
            }
            if ((state & 4) != 0) {
            }
        }
        // reactor
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            MapleReactor reactor = (MapleReactor) mmo;
            int number = this.map_split.getSplitMap(reactor.getPosition().x, reactor.getPosition().y);
            if (this.map_split.getSplit() < number) {
                continue;
            }
            int state = move_state.get(number);
            if ((state & 1) != 0) {
                chr.SendPacket(ResCReactorPool.ReactorEnterField(reactor));
            }
            if ((state & 4) != 0) {
                chr.SendPacket(ResCReactorPool.ReactorLeaveField(reactor));
            }
        }
    }

    public void linkedObjectEnterField(TacosCharacter chr) {
        // evan dragon
        TacosDragon dragon = chr.getDragon();
        if (dragon != null) {
            dragon.reset(chr);
            broadcastMessage(ResCUser_Dragon.DragonEnterField(dragon));
        }
        // kanna fox
        TacosSkillPet skill_pet = chr.getSkillPet();
        if (skill_pet != null) {
            skill_pet.reset(chr);
            broadcastMessage(ResCUser_SkillPet.SkillPetTransferField(skill_pet));
        }
    }

    public void linkedObjectLeaveField(TacosCharacter chr) {
        // evan dragon
        TacosDragon dragon = chr.getDragon();
        if (dragon != null) {
            // TODO : leave field.
        }
        // kanna fox
        TacosSkillPet skill_pet = chr.getSkillPet();
        if (skill_pet != null) {
            // TODO : leave field.
        }
    }

    public MapleSummon getSummonByOid(int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.SUMMON);
        if (mmo == null) {
            return null;
        }
        return (MapleSummon) mmo;
    }

    public void spawnSummon(MapleSummon summon) {
        addMapObject(summon);
        spawnRangedMapObject(summon, ResCSummonedPool.SummonedEnterField(summon, true));
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

    public int getSpawnedMonstersOnMap() {
        return this.spawnedMonstersOnMap.get();
    }

    public boolean updateMonsterController(MapleMonster monster) {
        if (!monster.isAlive()) {
            return false;
        }

        if (monster.getController() != null) {
            if (monster.getController().getMap() != this) {
                monster.getController().stopControllingMonster(monster);
            } else { // Everything is fine :)
                return false;
            }
        }

        int mincontrolled = -1;
        MapleCharacter newController = null;

        Iterator<MapleCharacter> ltr = this.characters.iterator();
        MapleCharacter chr;
        while (ltr.hasNext()) {
            chr = ltr.next();
            if (!chr.isHidden() && !chr.isClone() && (chr.getControlledSize() < mincontrolled || mincontrolled == -1)) {
                mincontrolled = chr.getControlledSize();
                newController = chr;
            }
        }
        if (newController != null) {
            if (monster.isFirstAttack()) {
                newController.controlMonster(monster, true);
                monster.setControllerHasAggro(true);
                monster.setControllerKnowsAboutAggro(true);
            } else {
                newController.controlMonster(monster, false);
            }
        }

        return true;
    }

    public void removeMonster(MapleMonster monster) {
        this.spawnedMonstersOnMap.decrementAndGet();
        broadcastMessage(ResCMobPool.MobLeaveField(monster, 0));
        removeMapObject(monster);
    }

    public void killMonster(MapleMonster monster) {
        this.spawnedMonstersOnMap.decrementAndGet();
        monster.setHp(0);
        monster.spawnRevives();
        broadcastMessage(ResCMobPool.MobLeaveField(monster, 1));
        removeMapObject(monster);
    }

    public void killAllMonsters(boolean animate) {
        for (MapleMapObject monstermo : getAllMonsters()) {
            MapleMonster monster = (MapleMonster) monstermo;
            this.spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0);
            broadcastMessage(ResCMobPool.MobLeaveField(monster, animate ? 1 : 0));
            removeMapObject(monster);
        }
    }

    public boolean killMonster(int monsId) {
        for (MapleMapObject mmo : getAllMonsters()) {
            if (((MapleMonster) mmo).getId() == monsId) {
                this.spawnedMonstersOnMap.decrementAndGet();
                removeMapObject(mmo);
                broadcastMessage(ResCMobPool.MobLeaveField((MapleMonster) mmo, 1));
                return true;
            }
        }
        return false;
    }

    public void checkRemoveAfter(MapleMonster monster) {
        int ra = monster.getStats().getRemoveAfter();

        if (ra > 0) {
            MapTimer.getInstance().schedule(() -> {
                if (monster == getMapObject(monster.getObjectId(), monster.getType())) {
                    killMonster(monster);
                }
            }, ra * 1000);
        }
    }

    public void spawnRevives(MapleMonster monster, int oid) {
        checkRemoveAfter(monster);
        monster.setLinkOid(oid);
        addMapObject(monster);
        spawnRangedMapObject(monster, ResCMobPool.MobEnterField(monster, -3, 0, oid));
        updateMonsterController(monster);
        this.spawnedMonstersOnMap.incrementAndGet();
    }

    public void spawnMonster(MapleMonster monster, int spawnType) {
        checkRemoveAfter(monster);
        addMapObject(monster);
        spawnRangedMapObject(monster, ResCMobPool.MobEnterField(monster, spawnType, 0, 0));
        updateMonsterController(monster);
        this.spawnedMonstersOnMap.incrementAndGet();
    }

    public int spawnMonsterWithEffect(MapleMonster monster, int effect, Point pos) {
        monster.setPosition(pos);
        addMapObject(monster);
        spawnRangedMapObject(monster, ResCMobPool.MobEnterField(monster, -2, effect, 0));
        updateMonsterController(monster);
        this.spawnedMonstersOnMap.incrementAndGet();
        return monster.getObjectId();
    }

    public void spawnFakeMonster(MapleMonster monster) {
        monster.setFake(true);
        addMapObject(monster);
        spawnRangedMapObject(monster, ResCMobPool.MobEnterField(monster, -4, 0, 0));
        updateMonsterController(monster);
        this.spawnedMonstersOnMap.incrementAndGet();
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

    public void spawnNpc(int id, Point pos) {
        MapleNPC npc = MapleLifeFactory.getNPC(id);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setFh(getFootholds().findBelow(pos).getId());
        npc.setCustom(true);
        addMapObject(npc);
        broadcastMessage(ResCNpcPool.NpcEnterField(npc, true));
    }

    public void removeNpc(int npcid) {
        Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
        while (itr.hasNext()) {
            MapleNPC npc = (MapleNPC) itr.next();
            if (npc.isCustom() && npc.getId() == npcid) {
                broadcastMessage(ResCNpcPool.NpcLeaveField(npc));
                itr.remove();
            }
        }
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

    public void spawnMesoDrop(int meso, Point position, MapleMapObject dropper, MapleCharacter owner, boolean playerDrop, byte droptype) {
        Point droppos = calcDropPos(position, position);
        MapleMapItem mdrop = new MapleMapItem(meso, droppos, dropper, owner, droptype, playerDrop);
        addMapObject(mdrop);
        spawnRangedMapObject(mdrop, ResCDropPool.DropEnterField(mdrop, ResCDropPool.EnterType.ANIMATION, droppos, dropper.getPosition()));

        if (!this.everlast) {
            mdrop.registerExpire(120000);
            if (droptype == 0 || droptype == 1) {
                mdrop.registerFFA(30000);
            }
        }
    }

    public void spawnMobMesoDrop(int meso, Point position, MapleMapObject dropper, MapleCharacter owner, boolean playerDrop, byte droptype) {
        MapleMapItem mdrop = new MapleMapItem(meso, position, dropper, owner, droptype, playerDrop);
        addMapObject(mdrop);
        spawnRangedMapObject(mdrop, ResCDropPool.DropEnterField(mdrop, ResCDropPool.EnterType.ANIMATION, position, dropper.getPosition()));
        mdrop.registerExpire(120000);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000);
        }
    }

    public void spawnAutoDrop(int itemid, Point pos) {
        IItem idrop = null;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) {
            idrop = ii.randomizeStats((Equip) ii.getEquipById(itemid));
        } else {
            idrop = new Item(itemid, (byte) 0, (short) 1, (byte) 0);
        }
        MapleMapItem mdrop = new MapleMapItem(pos, idrop);
        addMapObject(mdrop);
        spawnRangedMapObject(mdrop, ResCDropPool.DropEnterField(mdrop, ResCDropPool.EnterType.ANIMATION, pos, pos));
        broadcastMessage(ResCDropPool.DropEnterField(mdrop, ResCDropPool.EnterType.PICK_UP_ENABLED, pos, pos));
        mdrop.registerExpire(120000);
    }

    public int dropFromMonster(MapleCharacter player, MapleMonster monster) {
        // drop database
        int dropped_count = MonsterDrop.dropFromDatabase(this.channel, player, monster);
        if (0 < dropped_count) {
            return dropped_count;
        }
        // drop monster book
        dropped_count += CustomMonsterBookDrop.dropFromMonsterBook(player, monster);
        return dropped_count;
    }

    public List<MapleMapObject> getAllDoors() {
        ArrayList<MapleMapObject> ret = new ArrayList<>();
        for (MapleMapObject mmo : this.mapobjects.get(MapleMapObjectType.DOOR).values()) {
            ret.add(mmo);
        }
        return ret;
    }

    public void spawnDoor(MapleDoor door) {
        DebugLogger.DebugLog("Spawn Door : " + door.getMapId());
        addMapObject(door);
        spawnRangedMapObject(door, null);
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

    public void spawnDynamicPortal(MapleDynamicPortal dynamic_portal) {
        addMapObject(dynamic_portal);
        spawnRangedMapObject(dynamic_portal, Res_JMS_CInstancePortalPool.InstancePortalCreated(dynamic_portal));
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

    public void spawnReactor(MapleReactor reactor) {
        addMapObject(reactor);
        spawnRangedMapObject(reactor, ResCReactorPool.ReactorEnterField(reactor));
    }

    public void respawnReactor(MapleReactor reactor) {
        reactor.setState((byte) 0);
        reactor.setAlive(true);
        spawnReactor(reactor);
    }

    public void destroyReactor(int oid) {
        MapleReactor reactor = getReactorByOid(oid);
        broadcastMessage(ResCReactorPool.ReactorLeaveField(reactor));
        reactor.setAlive(false);
        removeMapObject(reactor);
        reactor.setTimerActive(false);

        if (reactor.getDelay() > 0) {
            MapTimer.getInstance().schedule(new Runnable() {

                @Override
                public final void run() {
                    respawnReactor(reactor);
                }
            }, reactor.getDelay());
        }
    }

    public void reloadReactors() {
        List<MapleReactor> toSpawn = new ArrayList<>();
        for (MapleMapObject obj : this.mapobjects.get(MapleMapObjectType.REACTOR).values()) {
            final MapleReactor reactor = (MapleReactor) obj;
            broadcastMessage(ResCReactorPool.ReactorLeaveField(reactor));
            reactor.setAlive(false);
            reactor.setTimerActive(false);
            toSpawn.add(reactor);
        }
        for (MapleReactor r : toSpawn) {
            removeMapObject(r);
            if (r.getReactorId() != 9980000 && r.getReactorId() != 9980001) { //guardians cpq
                respawnReactor(r);
            }
        }
    }

    // self and other players in range.
    public void broadcastMessage(MaplePacket packet, Point rangedFrom) {
        broadcastMessageInternal(null, packet, rangedFrom, false);
    }

    // other players in range.
    public void broadcastMessageTo(TacosCharacter source, MaplePacket packet, Point rangedFrom) {
        broadcastMessageInternal(source, packet, rangedFrom, false);
    }

    // self and other players.
    public void broadcastMessage(MaplePacket packet) {
        broadcastMessageInternal(null, packet, null, true);
    }

    // self and other players, or other players.
    public void broadcastMessage(TacosCharacter source, MaplePacket packet, boolean repeatToSource) {
        broadcastMessageInternal(repeatToSource ? null : source, packet, source.getPosition(), true);
    }

    private void broadcastMessageInternal(TacosCharacter source, MaplePacket packet, Point rangedFrom, boolean ignoreRange) {
        Iterator<MapleCharacter> ltr = characters.iterator();
        TacosCharacter chr;
        while (ltr.hasNext()) {
            chr = ltr.next();
            if (source == null || chr.getId() != source.getId()) {
                if (ignoreRange || rangedFrom.distanceSq(chr.getPosition()) <= chr.getViewRangeSq()) {
                    chr.SendPacket(packet);
                }
            }
        }
    }

    public void returnEverLastItem(final MapleCharacter chr) {
        for (final MapleMapObject o : getAllItems()) {
            final MapleMapItem item = ((MapleMapItem) o);
            if (item.getOwner() == chr.getId()) {
                item.setPickedUp(true);

                DebugLogger.DebugLog("PICKUP REVER");
                broadcastMessage(ResCDropPool.DropLeaveField(item, ResCDropPool.LeaveType.PICK_UP, chr, 0), item.getPosition());
                if (item.getMeso() > 0) {
                    chr.gainMeso(item.getMeso(), false);
                } else {
                    MapleInventoryManipulator.addFromDrop(chr.getClient(), item.getItem(), false);
                }
                removeMapObject(item);
            }
        }
    }

    public MapleSquad getSquadByMap() {
        String zz = null;
        switch (mapid) {
            case 105100400:
            case 105100300:
                zz = "BossBalrog";
                break;
            case 280030000:
                zz = "ZAK";
                break;
            case 280030001:
                zz = "ChaosZak";
                break;
            case 240060200:
                zz = "Horntail";
                break;
            case 240060201:
                zz = "ChaosHT";
                break;
            case 270050100:
                zz = "PinkBean";
                break;
            case 802000111:
                zz = "nmm_squad";
                break;
            case 802000211:
                zz = "VERGAMOT";
                break;
            case 802000311:
                zz = "2095_tokyo";
                break;
            case 802000411:
                zz = "Dunas";
                break;
            case 802000611:
                zz = "Nibergen_squad";
                break;
            case 802000711:
                zz = "dunas2";
                break;
            case 802000801:
            case 802000802:
            case 802000803:
                zz = "Core_Blaze";
                break;
            case 802000821:
                zz = "Aufheben";
                break;
            default:
                return null;
        }
        return TacosWorld.find(0).getChannelServer(channel).getMapleSquad(zz);
    }

    public OdinEventManager getEMByMap() {
        String em = null;
        switch (mapid) {
            case 105100400:
                em = "BossBalrog_EASY";
                break;
            case 105100300:
                em = "BossBalrog_NORMAL";
                break;
            case 280030000:
                em = "ZakumBattle";
                break;
            case 240060200:
                em = "HorntailBattle";
                break;
            case 280030001:
                em = "ChaosZakum";
                break;
            case 240060201:
                em = "ChaosHorntail";
                break;
            case 270050100:
                em = "PinkBeanBattle";
                break;
            case 802000111:
                em = "NamelessMagicMonster";
                break;
            case 802000211:
                em = "Vergamot";
                break;
            case 802000311:
                em = "2095_tokyo";
                break;
            case 802000411:
                em = "Dunas";
                break;
            case 802000611:
                em = "Nibergen";
                break;
            case 802000711:
                em = "Dunas2";
                break;
            case 802000801:
            case 802000802:
            case 802000803:
                em = "CoreBlaze";
                break;
            case 802000821:
                em = "Aufhaven";
                break;
            default:
                return null;
        }
        return TacosScriptEvent.getInstance().getEventManager(em);
    }

    public void resetFully() {
        resetFully(true);
    }

    public void resetFully(boolean respawn) {
        setChangeBGM("");
        killAllMonsters(false);
        reloadReactors();
        removeDrops();
        resetSpawns();
        cancelSquadSchedule();
        resetPortals();
        environment.clear();
        if (respawn) {
            respawn(true);
        }
    }

    public void removeDrops() {
        List<MapleMapItem> items = getAllItems();
        for (MapleMapItem i : items) {
            i.expire(this);
        }
    }

    public void resetSpawns() {
        boolean changed = false;
        Iterator<Spawns> sss = monsterSpawn.iterator();
        while (sss.hasNext()) {
            if (sss.next().getCarnivalId() > -1) {
                sss.remove();
                changed = true;
            }
        }
        setSpawns(true);
        if (changed) {
            loadMonsterRate(true);
        }
    }

    public void loadMonsterRate(boolean first) {
        final int spawnSize = monsterSpawn.size();
        maxRegularSpawn = Math.round(spawnSize * monsterRate);
        if (maxRegularSpawn < 2) {
            maxRegularSpawn = 2;
        } else if (maxRegularSpawn > spawnSize) {
            maxRegularSpawn = spawnSize - (spawnSize / 15);
        }
        if (fixedMob > 0) {
            maxRegularSpawn = fixedMob;
        }
        Collection<Spawns> newSpawn = new LinkedList<Spawns>();
        Collection<Spawns> newBossSpawn = new LinkedList<Spawns>();
        for (final Spawns s : monsterSpawn) {
            if (s.getCarnivalTeam() >= 2) {
                continue; // Remove carnival spawned mobs
            }
            if (s.getMonster().getStats().isBoss()) {
                newBossSpawn.add(s);
            } else {
                newSpawn.add(s);
            }
        }
        monsterSpawn.clear();
        monsterSpawn.addAll(newBossSpawn);
        monsterSpawn.addAll(newSpawn);

        if (first && spawnSize > 0) {
            lastSpawnTime = 0; // 即沸き
            if (GameConstants.isForceRespawn(mapid)) {
                createMobInterval = 15000;
            }
        }
    }

    public void respawn(boolean force) {
        lastSpawnTime = System.currentTimeMillis();
        if (force) { //cpq quick hack
            final int numShouldSpawn = monsterSpawn.size() - spawnedMonstersOnMap.get();

            if (numShouldSpawn > 0) {
                int spawned = 0;

                for (Spawns spawnPoint : monsterSpawn) {
                    spawnPoint.spawnMonster(this);
                    spawned++;
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        } else {
            final int numShouldSpawn = maxRegularSpawn - spawnedMonstersOnMap.get();
            if (numShouldSpawn > 0) {
                int spawned = 0;

                final List<Spawns> randomSpawn = new ArrayList<Spawns>(monsterSpawn);
                Collections.shuffle(randomSpawn);

                for (Spawns spawnPoint : randomSpawn) {
                    if (spawnPoint.shouldSpawn() || GameConstants.isForceRespawn(mapid)) {
                        spawnPoint.spawnMonster(this);
                        spawned++;
                    }
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    // unused
    public boolean canSoar() {
        return this.soaring;
    }

    // compatbility
    public MapleMap getReturnMap() {
        return TacosWorld.find(0).getChannelServer(channel).getMapFactory().getMap(returnMapId);
    }

    public MapleMap getForcedReturnMap() {
        return TacosWorld.find(0).getChannelServer(channel).getMapFactory().getMap(forcedReturnMap);
    }

    public boolean updateMapItem() {
        for (MapleMapItem item : getAllItems()) {
            if (item.shouldExpire()) {
                item.expire(this);
                continue;
            }
            if (item.shouldFFA()) {
                item.setDropType((byte) 2);
            }
        }
        return true;
    }

    public boolean updateSpawn() {
        if (!canSpawn()) {
            return false;
        }
        if (this.characters.isEmpty()) {
            return false;
        }
        respawn(false);
        return true;
    }

}
