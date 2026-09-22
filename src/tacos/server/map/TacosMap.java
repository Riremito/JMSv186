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

import tacos.server.map.object.TacosMapObject;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import odin.client.MapleCharacter;
import odin.client.inventory.Equip;
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
import odin.server.maps.MapScriptMethods;
import odin.server.maps.MapleDoor;
import odin.server.maps.MapleDynamicPortal;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapEffect;
import odin.server.maps.MapleMapItem;
import odin.server.maps.MapleMist;
import odin.server.maps.MapleReactor;
import odin.server.maps.MapleSummon;
import odin.server.shops.HiredMerchant;
import odin.server.shops.MapleMiniGame;
import odin.server.shops.MaplePlayerShop;
import tacos.client.TacosCharacter;
import tacos.client.TacosDragon;
import tacos.client.TacosSkillPet;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsMobAppear;
import tacos.packet.ops.OpsMobLeaveField;
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
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.response.builder.PB_BroadcastMsg;
import tacos.packet.ops.OpsFieldEffect;
import tacos.packet.response.builder.PB_FieldEffect;
import tacos.script.TacosScriptEvent;
import tacos.server.TacosWorld;

/**
 *
 * @author Riremito
 */
public class TacosMap extends TacosMapData {

    protected int channel;

    protected int runningOid = 100000;
    protected Map<String, Integer> environment = new LinkedHashMap<>();
    protected boolean squadTimer = false;
    protected String squad = "";
    protected ScheduledFuture<?> squadSchedule;
    protected MapleMapEffect mapEffect;

    public TacosMap(int mapid, int channel) {
        super(mapid);
        this.channel = channel;
    }

    public int getChannel() {
        return this.channel;
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

    // player.
    private LinkedHashMap<Integer, MapleCharacter> players = new LinkedHashMap<>();

    public void addPlayer(MapleCharacter chr) {
        this.players.put(chr.getObjectId(), chr);
    }

    public boolean removePlayer(int object_id) {
        return this.players.remove(object_id) != null;
    }

    public List<MapleCharacter> getAllPlayers() {
        ArrayList<MapleCharacter> ret = new ArrayList<>();
        for (MapleCharacter chr : this.players.values()) {
            ret.add(chr);
        }
        return ret;
    }

    public MapleCharacter getPlayerByOid(int object_id) {
        return this.players.get(object_id);
    }

    public MapleCharacter getPlayerById(int id) {
        for (MapleCharacter player : this.players.values()) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
    }

    public List<MapleCharacter> getPlayersInRect(Rectangle box) {
        ArrayList<MapleCharacter> ret = new ArrayList<>();
        for (MapleCharacter chr : this.players.values()) {
            if (box.contains(chr.getPosition())) {
                ret.add(chr);
            }
        }
        return ret;
    }

    public List<MapleCharacter> getPlayersInRectAndInList(Rectangle box, List<MapleCharacter> chrList) {
        List<MapleCharacter> character = new LinkedList<>();
        Iterator<MapleCharacter> ltr = this.players.values().iterator();
        MapleCharacter a;
        while (ltr.hasNext()) {
            a = ltr.next();
            if (chrList.contains(a) && box.contains(a.getPosition())) {
                character.add(a);
            }
        }
        return character;
    }

    public int getCharactersSize() {
        return this.players.size();
    }

    private String fe_change_bgm = "";

    public void setChangeBGM(String wz_path) {
        this.fe_change_bgm = wz_path;
        if (!getChangeBGM().equals("")) {
            broadcastMessage(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_ChangeBGM, PB_FieldEffect.builder().wz_path(getChangeBGM()).build()));
        }
    }

    public String getChangeBGM() {
        return this.fe_change_bgm;
    }

    public void sendChangeBGM(TacosCharacter chr) {
        if (!getChangeBGM().equals("")) {
            chr.SendPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_ChangeBGM, PB_FieldEffect.builder().wz_path(getChangeBGM()).build()));
        }
    }

    public boolean sendInitialization(MapleCharacter chr) {
        // user enter script.
        if (!getFirstUserEnter().equals("")) {
            if (getCharactersSize() == 1) {
                MapScriptMethods.startScript_FirstUser(chr.getClient(), getFirstUserEnter());
            }
        }
        if (!getUserEnter().equals("")) {
            MapScriptMethods.startScript_User(chr.getClient(), getUserEnter());
        }
        // station clock.
        if (hasClock()) {
            // 101000300
            Calendar cal = Calendar.getInstance();
            chr.SendPacket((ResCField.Clock(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND))));
        }
        if (TacosConstants.is_coconut(map_id)) {
            chr.setCoconutTeam(getCharactersSize() % 2);
        }
        if (TacosConstants.is_coconut(map_id) || TacosConstants.is_bath(map_id)) {
            chr.SendPacket(ResCField.FieldSpecificData(chr));
            return true;
        }
        if (TacosConstants.is_aran_tutorial(map_id)) {
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

        switch (map_id) {
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
                    world.broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, PB_BroadcastMsg.builder().message("大変な挑戦の終わりにホーンテイルを撃破した遠征隊よ！貴方達が本当のリプレの英雄だ！").build()));
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
                    world.broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, PB_BroadcastMsg.builder().message("不屈の闘志でピンクビーンを退けた遠征隊の諸君！　君たちが真の時間の覇者だ！").build()));
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
                    world.broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, PB_BroadcastMsg.builder().message("大変な挑戦の終わりにホーンテイルを撃破した遠征隊よ！貴方達が本当のリプレの英雄だ！").build()));
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
        ArrayList<MapSplitState> area_states = split.getArea(chr.getPosition().x, chr.getPosition().y, MapSplitState.ACTIVE);

        addPlayer(chr); // object id.

        // no split.
        sendChangeBGM(chr);
        if (!getNodeInfo().getPlatforms().isEmpty()) {
            chr.SendPacket(ResCField.FootHoldInfo(this));
        }
        if (!environment.isEmpty()) {
            chr.SendPacket(ResCField.FieldObstacleOnOffStatus(this));
        }
        sendInitialization(chr);
        updateParty(chr);
        sendMapEffect(chr);
        if (0 < getTimeLimit()) {
            chr.DebugMsg("timeLimit = " + getTimeLimit());
            if (getForcedReturnMap() != null) {
                chr.startMapTimeLimitTask(getTimeLimit(), getForcedReturnMap());
            }
        }
        sendExpedition(chr, null);

        for (MapleCharacter player : this.players.values()) {
            // self
            if (player.getId() == chr.getId()) {
                continue;
            }
            int player_number = split.find(player.getPosition().x, player.getPosition().y);
            if (split.getTotal() < player_number) {
                continue;
            }
            if (area_states.get(player_number) == MapSplitState.ACTIVE) {
                player.SendPacket(ResCUserPool.UserEnterField(chr));
                chr.SendPacket(ResCUserPool.UserEnterField(player));
            }
        }
        // mob
        for (MapleMonster mob : this.monsters.values()) {
            chr.SendPacket(ResCMobPool.MobEnterField(mob));

            int number = split.find(mob.getPosition().x, mob.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ACTIVE) {
                if (mob.getController() == null || getPlayerByOid(mob.getController().getId()) == null) {
                    mob.setController(chr);
                    chr.SendPacket(ResCMobPool.MobChangeController(mob, (mob.isFirstAttack() ? 1 : 0) + 1));
                }
            }
        }
        // npc
        for (MapleNPC npc : this.npcs.values()) {
            int number = split.find(npc.getPosition().x, npc.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ACTIVE) {
                chr.SendPacket(ResCNpcPool.NpcEnterField(npc, true));
                chr.SendPacket(ResCNpcPool.NpcChangeController(npc, true, true));
            }
        }
        // hired merchant
        for (HiredMerchant employee : this.hiredMerchants.values()) {
            int number = split.find(employee.getPosition().x, employee.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ACTIVE) {
                chr.SendPacket(ResCEmployeePool.EmployeeEnterField(employee));
            }
        }
        // drop
        for (MapleMapItem drop : this.drops.values()) {
            // quest item.
            int quest_id = drop.getQuest();
            if (0 < quest_id) {
                if (chr.getQuestStatus(quest_id) != 1) {
                    continue;
                }
            }
            int number = split.find(drop.getPosition().x, drop.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ACTIVE) {
                chr.SendPacket(ResCDropPool.DropEnterField(drop, ResCDropPool.DropEnterType.SILENT, drop.getPosition()));
            }
        }
        // mist
        for (MapleMist mist : this.mists.values()) {
            int number = split.find(mist.getPosition().x, mist.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ACTIVE) {
                chr.SendPacket(ResCAffectedAreaPool.AffectedAreaCreated(mist));
            }
        }
        // mystic door
        for (MapleDoor door : this.doors.values()) {
            int number = split.find(door.getPosition().x, door.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ACTIVE) {
                chr.SendPacket(ResCTownPortalPool.TownPortalCreated(door.getLink(), false));
            }
        }
        // mechanic gate
        // pinkbean cake event portal
        for (MapleDynamicPortal instance_portal : this.dynamicPortals.values()) {
            int number = split.find(instance_portal.getPosition().x, instance_portal.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ACTIVE) {
                chr.SendPacket(Res_JMS_CInstancePortalPool.InstancePortalCreated(instance_portal));
            }
        }
        // reactor
        for (MapleReactor reactor : this.reactors.values()) {
            int number = split.find(reactor.getPosition().x, reactor.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ACTIVE) {
                chr.SendPacket(ResCReactorPool.ReactorEnterField(reactor));
            }
        }
    }

    public void userLeaveField(MapleCharacter chr) {
        ArrayList<MapSplitState> area_states = split.getArea(chr.getPosition().x, chr.getPosition().y, MapSplitState.ACTIVE);

        removePlayer(chr.getObjectId());

        for (MapleCharacter player : this.players.values()) {
            // self
            if (player.getId() == chr.getId()) {
                continue;
            }
            int player_number = split.find(player.getPosition().x, player.getPosition().y);
            if (split.getTotal() < player_number) {
                continue;
            }
            if (area_states.get(player_number) == MapSplitState.ACTIVE) {
                player.SendPacket(ResCUserPool.UserLeaveField(chr));
            }
        }
        // mob
        for (MapleMonster mob : this.monsters.values()) {
            int number = split.find(mob.getPosition().x, mob.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ACTIVE) {
                boolean controlled = mob.getController() != null && mob.getController().getId() == chr.getId();
                if (controlled) {
                    mob.setController(null);
                }
            }
        }
    }

    public void userMove(MapleCharacter chr, ParseCMovePath move_path) {
        ArrayList<MapSplitState> area_states = split.getMoveArea(chr.getPosition().x, chr.getPosition().y, move_path.getX(), move_path.getY());

        for (MapleCharacter player : this.players.values()) {
            // self
            if (player.getId() == chr.getId()) {
                continue;
            }
            int player_number = split.find(player.getPosition().x, player.getPosition().y);
            if (split.getTotal() < player_number) {
                continue;
            }
            if (area_states.get(player_number) == MapSplitState.ENTER_MOVE) {
                player.SendPacket(ResCUserPool.UserEnterField(chr));
                chr.SendPacket(ResCUserPool.UserEnterField(player));
            }
            if (area_states.get(player_number) == MapSplitState.MOVE) {
                player.SendPacket(ResCUserRemote.UserMove(chr, move_path));
            }
            if (area_states.get(player_number) == MapSplitState.MOVE_LEAVE) {
                player.SendPacket(ResCUserPool.UserLeaveField(chr));
                chr.SendPacket(ResCUserPool.UserLeaveField(player));
            }
        }
        // mob
        for (MapleMonster mob : this.monsters.values()) {
            int number = split.find(mob.getPosition().x, mob.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            boolean not_controlled = mob.getController() == null || getPlayerByOid(mob.getController().getId()) == null;
            boolean controlled = mob.getController() != null && mob.getController().getId() == chr.getId();
            if (area_states.get(number) == MapSplitState.ENTER_MOVE) {
                if (not_controlled) {
                    mob.setController(chr);
                    chr.SendPacket(ResCMobPool.MobChangeController(mob, (mob.isFirstAttack() ? 1 : 0) + 1));
                }
            }
            if (area_states.get(number) == MapSplitState.MOVE_LEAVE) {
                if (controlled) {
                    mob.setController(null);
                    chr.SendPacket(ResCMobPool.MobChangeController(mob, 0));
                }
            }
        }
        // npc
        for (MapleNPC npc : this.npcs.values()) {
            int number = split.find(npc.getPosition().x, npc.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ENTER_MOVE) {
                chr.SendPacket(ResCNpcPool.NpcEnterField(npc, true));
                chr.SendPacket(ResCNpcPool.NpcChangeController(npc, true, true));
            }
            if (area_states.get(number) == MapSplitState.MOVE_LEAVE) {
                chr.SendPacket(ResCNpcPool.NpcLeaveField(npc));
            }
        }
        // hired merchant
        for (HiredMerchant employee : this.hiredMerchants.values()) {
            int number = split.find(employee.getPosition().x, employee.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ENTER_MOVE) {
                chr.SendPacket(ResCEmployeePool.EmployeeEnterField(employee));
            }
            if (area_states.get(number) == MapSplitState.MOVE_LEAVE) {
                chr.SendPacket(ResCEmployeePool.EmployeeLeaveField(employee));
            }
        }
        // drop
        for (MapleMapItem drop : this.drops.values()) {
            // quest item.
            int quest_id = drop.getQuest();
            if (0 < quest_id) {
                if (chr.getQuestStatus(quest_id) != 1) {
                    continue;
                }
            }
            int number = split.find(drop.getPosition().x, drop.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ENTER_MOVE) {
                chr.SendPacket(ResCDropPool.DropEnterField(drop, ResCDropPool.DropEnterType.SILENT, drop.getPosition()));
            }
            if (area_states.get(number) == MapSplitState.MOVE_LEAVE) {
                chr.SendPacket(ResCDropPool.DropLeaveField(drop, ResCDropPool.DropLeaveType.REMOVE));
            }
        }
        // mist
        for (MapleMist mist : this.mists.values()) {
            int number = split.find(mist.getPosition().x, mist.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ENTER_MOVE) {
                chr.SendPacket(ResCAffectedAreaPool.AffectedAreaCreated(mist));
            }
            if (area_states.get(number) == MapSplitState.MOVE_LEAVE) {
                chr.SendPacket(ResCAffectedAreaPool.AffectedAreaRemoved(mist));
            }
        }
        // mystic door
        for (MapleDoor door : this.doors.values()) {
            int number = split.find(door.getPosition().x, door.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ENTER_MOVE) {
                chr.SendPacket(ResCTownPortalPool.TownPortalCreated(door, false));
            }
            if (area_states.get(number) == MapSplitState.MOVE_LEAVE) {
                chr.SendPacket(ResCTownPortalPool.TownPortalRemoved(door));
            }
        }
        // mechanic gate
        // pinkbean cake event portal
        for (MapleDynamicPortal instance_portal : this.dynamicPortals.values()) {
            int number = split.find(instance_portal.getPosition().x, instance_portal.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ENTER_MOVE) {
                chr.SendPacket(Res_JMS_CInstancePortalPool.InstancePortalCreated(instance_portal));
            }
            if (area_states.get(number) == MapSplitState.MOVE_LEAVE) {
            }
        }
        // reactor
        for (MapleReactor reactor : this.reactors.values()) {
            int number = split.find(reactor.getPosition().x, reactor.getPosition().y);
            if (split.getTotal() < number) {
                continue;
            }
            if (area_states.get(number) == MapSplitState.ENTER_MOVE) {
                chr.SendPacket(ResCReactorPool.ReactorEnterField(reactor));
            }
            if (area_states.get(number) == MapSplitState.MOVE_LEAVE) {
                chr.SendPacket(ResCReactorPool.ReactorLeaveField(reactor));
            }
        }
    }

    public void splitSendPacket(TacosMapObject object, ServerPacket packet) {
        splitSendPacket(object, packet, 0);
    }

    public void splitSendPacket(TacosMapObject object, ServerPacket packet, int sender_id) {
        ArrayList<MapSplitState> area_states = split.getArea(object.getPosition().x, object.getPosition().y, MapSplitState.ACTIVE);

        for (MapleCharacter player : this.players.values()) {
            int player_number = split.find(player.getPosition().x, player.getPosition().y);
            if (split.getTotal() < player_number) {
                continue;
            }
            // ignore self
            if (player.getId() == sender_id) {
                continue;
            }
            if (area_states.get(player_number) == MapSplitState.ACTIVE) {
                player.SendPacket(packet);
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

    // summon.
    private LinkedHashMap<Integer, MapleSummon> summons = new LinkedHashMap<>();

    public void addSummon(MapleSummon summon) {
        this.runningOid++;
        summon.setObjectId(this.runningOid);
        this.summons.put(summon.getObjectId(), summon);
    }

    public boolean removeSummon(int object_id) {
        return this.summons.remove(object_id) != null;
    }

    public List<MapleSummon> getAllSummons() {
        ArrayList<MapleSummon> ret = new ArrayList<>();
        for (MapleSummon summon : this.summons.values()) {
            ret.add(summon);
        }
        return ret;
    }

    public MapleSummon getSummonByOid(int oid) {
        return this.summons.get(oid);
    }

    public void spawnSummon(MapleSummon summon) {
        addSummon(summon);
        broadcastMessage(ResCSummonedPool.SummonedEnterField(summon, true));
    }

    // mob
    private LinkedHashMap<Integer, MapleMonster> monsters = new LinkedHashMap<>();

    public MapleMonster getMonsterByOid(int object_id) {
        return this.monsters.get(object_id);
    }

    public List<MapleMonster> getAllMonsters() {
        ArrayList<MapleMonster> ret = new ArrayList<>();
        for (MapleMonster monster : this.monsters.values()) {
            ret.add(monster);
        }
        return ret;
    }

    public List<MapleMonster> getMonstersInRect(Rectangle box) {
        ArrayList<MapleMonster> ret = new ArrayList<>();
        for (MapleMonster monster : this.monsters.values()) {
            if (box.contains(monster.getPosition())) {
                ret.add(monster);
            }
        }
        return ret;
    }

    public void addMonster(MapleMonster monster) {
        if (monster.getObjectId() == 0) {
            monster.setObjectId();
        }
        this.monsters.put(monster.getObjectId(), monster);
    }

    public boolean removeMonster(int object_id) {
        this.monsters.remove(object_id);
        // remove from spawn point.
        for (TacosSpawnPoint sp : getMonsterSpawnPoint()) {
            MapleMonster monster = sp.getMonster();
            if (monster != null) {
                if (monster.getObjectId() == object_id) {
                    sp.removeMonster();
                    return true;
                }
            }
        }
        // no spwan point.
        return true;
    }

    // script.
    public void setSpawns(boolean spawns) {
    }

    public MapleMonster getMonsterById(int id) {
        for (MapleMonster monster : this.monsters.values()) {
            if (monster.getId() == id) {
                return monster;
            }
        }
        return null;
    }

    public int countMonsterById(int id) {
        int ret = 0;
        for (MapleMonster monster : this.monsters.values()) {
            if (monster.getId() == id) {
                ret++;
            }
        }
        return ret;
    }

    public int getNumMonsters() {
        return this.monsters.size();
    }

    public int getSpawnedMonstersOnMap() {
        return this.monsters.size();
    }

    public void removeMonster(MapleMonster monster) {
        removeMonster(monster.getObjectId());
        broadcastMessage(ResCMobPool.MobLeaveField(monster, OpsMobLeaveField.MOBLEAVEFIELD_REMAINHP));
    }

    public void killMonster(MapleMonster monster) {
        monster.setHp(0);
        monster.spawnRevives();
        removeMonster(monster.getObjectId());
        broadcastMessage(ResCMobPool.MobLeaveField(monster, OpsMobLeaveField.MOBLEAVEFIELD_ETC));
    }

    public void killAllMonsters(boolean animate) {
        for (MapleMonster monster : getAllMonsters()) {
            monster.setHp(0);
            removeMonster(monster.getObjectId());
            broadcastMessage(ResCMobPool.MobLeaveField(monster, animate ? OpsMobLeaveField.MOBLEAVEFIELD_ETC : OpsMobLeaveField.MOBLEAVEFIELD_REMAINHP));
        }
    }

    public boolean killMonster(int monsId) {
        for (MapleMonster monster : getAllMonsters()) {
            if (monster.getId() == monsId) {
                removeMonster(monster.getObjectId());
                broadcastMessage(ResCMobPool.MobLeaveField(monster, OpsMobLeaveField.MOBLEAVEFIELD_ETC));
                return true;
            }
        }
        return false;
    }

    public void checkRemoveAfter(MapleMonster monster) {
        int ra = monster.getStats().getRemoveAfter();

        if (ra > 0) {
            MapTimer.getInstance().schedule(() -> {
                if (monster == getMonsterByOid(monster.getObjectId())) {
                    killMonster(monster);
                }
            }, ra * 1000);
        }
    }

    public void spawnRevives(MapleMonster monster, int oid) {
        checkRemoveAfter(monster);
        monster.setLinkOid(oid);
        monster.setAT(OpsMobAppear.MOBAPPEAR_REVIVED);
        addMonster(monster);
        broadcastMessage(ResCMobPool.MobEnterField(monster));
        monster.setAT(OpsMobAppear.MOBAPPEAR_NORMAL);
    }

    public void spawnMonster(MapleMonster monster, int spawnType) {
        OpsMobAppear ops_at = OpsMobAppear.find(spawnType);

        checkRemoveAfter(monster);
        addMonster(monster);
        monster.setAT(ops_at != OpsMobAppear.UNKNOWN ? ops_at : OpsMobAppear.MOBAPPEAR_EFFECT);
        monster.setATEx(spawnType);
        broadcastMessage(ResCMobPool.MobEnterField(monster));
        monster.setAT(OpsMobAppear.MOBAPPEAR_NORMAL);
    }

    public int spawnMonsterWithEffect(MapleMonster monster, int effect, Point pos) {
        monster.setPosition(pos);
        monster.setAT(OpsMobAppear.MOBAPPEAR_REGEN);
        addMonster(monster);
        broadcastMessage(ResCMobPool.MobEnterField(monster));
        monster.setAT(OpsMobAppear.MOBAPPEAR_NORMAL);
        return monster.getObjectId();
    }

    public void spawnFakeMonster(MapleMonster monster) {
        monster.setFake(true);
        monster.setAT(OpsMobAppear.MOBAPPEAR_SUSPENDED);
        addMonster(monster);
        broadcastMessage(ResCMobPool.MobEnterField(monster));
    }

    // npc.
    private LinkedHashMap<Integer, MapleNPC> npcs = new LinkedHashMap<>();

    public void addNPC(MapleNPC npc) {
        if (npc.getObjectId() == 0) {
            TacosNPCSpawnPoint.setOBJECT_ID(npc);
        }
        this.npcs.put(npc.getObjectId(), npc);
    }

    public boolean removeNPC(int object_id) {
        this.npcs.remove(object_id);
        // remove from spawn point.
        for (TacosNPCSpawnPoint sp : getNPCSpawnPoint()) {
            MapleNPC npc = sp.getNPC();
            if (npc != null) {
                if (npc.getObjectId() == object_id) {
                    sp.removeNPC();
                    return true;
                }
            }
        }
        // no spwan point.
        return true;
    }

    public List<MapleNPC> getAllNPCs() {
        ArrayList<MapleNPC> ret = new ArrayList<>();
        for (MapleNPC npc : this.npcs.values()) {
            ret.add(npc);
        }
        return ret;
    }

    public boolean containsNPC(int npc_id) {
        for (MapleNPC npc : this.npcs.values()) {
            if (npc.getId() == npc_id) {
                return true;
            }
        }
        return false;
    }

    public MapleNPC getNPCById(int npc_id) {
        for (MapleNPC npc : this.npcs.values()) {
            if (npc.getId() == npc_id) {
                return npc;
            }
        }
        return null;
    }

    public MapleNPC getNPCByOid(int object_id) {
        return this.npcs.get(object_id);
    }

    public void spawnNpc(int npc_id, Point pos) {
        MapleNPC npc = MapleLifeFactory.getNPC(npc_id);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setFh(getFootholds().findBelow(pos).getId());
        npc.setCustom(true);
        TacosNPCSpawnPoint.setOBJECT_ID(npc);
        addNPC(npc);
        broadcastMessage(ResCNpcPool.NpcEnterField(npc, true));
    }

    public boolean removeNpc(int npc_id) {
        MapleNPC npc = getNPCById(npc_id);
        if (npc == null) {
            return false;
        }

        removeNPC(npc.getObjectId());
        broadcastMessage(ResCNpcPool.NpcLeaveField(npc));
        return true;
    }

    // mist.
    private LinkedHashMap<Integer, MapleMist> mists = new LinkedHashMap<>();

    public void addMist(MapleMist mist) {
        if (mist.getObjectId() == 0) {
            mist.setObjectId();
        }
        this.mists.put(mist.getObjectId(), mist);
        broadcastMessage(ResCAffectedAreaPool.AffectedAreaCreated(mist));
    }

    public boolean removeMist(int object_id) {
        this.mists.remove(object_id);
        return true;
    }

    public List<MapleMist> getAllMists() {
        ArrayList<MapleMist> ret = new ArrayList<>();
        for (MapleMist mist : this.mists.values()) {
            ret.add(mist);
        }
        return ret;
    }

    // merchant.
    private LinkedHashMap<Integer, HiredMerchant> hiredMerchants = new LinkedHashMap<>();

    public void addHiredMerchant(HiredMerchant merchant) {
        this.runningOid++;
        merchant.setObjectId(this.runningOid);
        this.hiredMerchants.put(merchant.getObjectId(), merchant);
    }

    public boolean removeHiredMerchant(int object_id) {
        return this.hiredMerchants.remove(object_id) != null;
    }

    public List<HiredMerchant> getAllHiredMerchants() {
        ArrayList<HiredMerchant> ret = new ArrayList<>();
        for (HiredMerchant merchant : this.hiredMerchants.values()) {
            ret.add(merchant);
        }
        return ret;
    }

    public HiredMerchant getHiredMerchantByOid(int object_id) {
        return this.hiredMerchants.get(object_id);
    }

    // mini game.
    private LinkedHashMap<Integer, MapleMiniGame> miniGames = new LinkedHashMap<>();

    public void addMiniGame(MapleMiniGame game) {
        this.runningOid++;
        game.setObjectId(this.runningOid);
        this.miniGames.put(game.getObjectId(), game);
    }

    public boolean removeMiniGame(int object_id) {
        return this.miniGames.remove(object_id) != null;
    }

    public List<MapleMiniGame> getAllMiniGames() {
        ArrayList<MapleMiniGame> ret = new ArrayList<>();
        for (MapleMiniGame game : this.miniGames.values()) {
            ret.add(game);
        }
        return ret;
    }

    public MapleMiniGame getMiniGameByOid(int object_id) {
        return this.miniGames.get(object_id);
    }

    // player shop.
    private LinkedHashMap<Integer, MaplePlayerShop> playerShops = new LinkedHashMap<>();

    public void addPlayerShop(MaplePlayerShop shop) {
        this.runningOid++;
        shop.setObjectId(this.runningOid);
        this.playerShops.put(shop.getObjectId(), shop);
    }

    public boolean removePlayerShop(int object_id) {
        return this.playerShops.remove(object_id) != null;
    }

    public List<MaplePlayerShop> getAllPlayerShops() {
        ArrayList<MaplePlayerShop> ret = new ArrayList<>();
        for (MaplePlayerShop shop : this.playerShops.values()) {
            ret.add(shop);
        }
        return ret;
    }

    public MaplePlayerShop getPlayerShopByOid(int object_id) {
        return this.playerShops.get(object_id);
    }

    // drop item.
    private LinkedHashMap<Integer, MapleMapItem> drops = new LinkedHashMap<>();
    private int DROP_OBJECT_ID = 300000;

    public void addDrop(MapleMapItem drop) {
        if (drop.getObjectId() == 0) {
            drop.setObjectId(DROP_OBJECT_ID++);
            drop.setTime();
        }
        this.drops.put(drop.getObjectId(), drop);
    }

    public boolean removeDrop(int object_id) {
        this.drops.remove(object_id);
        return true;
    }

    public MapleMapItem findDrop(int object_id) {
        return this.drops.get(object_id);
    }

    public List<MapleMapItem> getAllItems() {
        ArrayList<MapleMapItem> ret = new ArrayList<>();
        for (MapleMapItem drop : this.drops.values()) {
            ret.add(drop);
        }
        return ret;
    }

    public int getItemsSize() {
        return this.drops.size();
    }

    public void spawnMesoDrop(int meso, Point position, Object dropper, MapleCharacter owner, boolean playerDrop, byte droptype) {
        Point droppos = calcDropPos(position, position);
        MapleMapItem mdrop = new MapleMapItem(meso, droppos, dropper, owner, droptype, playerDrop);
        addDrop(mdrop);
        Point dropperPosition;
        if (dropper instanceof MapleCharacter) {
            dropperPosition = ((MapleCharacter) dropper).getPosition();
        } else if (dropper instanceof MapleMonster) {
            dropperPosition = ((MapleMonster) dropper).getPosition();
        } else {
            throw new IllegalArgumentException("spawnMesoDrop: unknown dropper type: " + dropper);
        }
        broadcastMessage(ResCDropPool.DropEnterField(mdrop, ResCDropPool.DropEnterType.NORMAL, droppos, dropperPosition));
    }

    public void spawnMobMesoDrop(int meso, Point position, MapleMonster dropper, MapleCharacter owner, boolean playerDrop, byte droptype) {
        MapleMapItem mdrop = new MapleMapItem(meso, position, dropper, owner, droptype, playerDrop);
        addDrop(mdrop);
        broadcastMessage(ResCDropPool.DropEnterField(mdrop, ResCDropPool.DropEnterType.NORMAL, position, dropper.getPosition()));
    }

    public void spawnAutoDrop(int itemid, Point pos) {
        Item idrop = null;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) {
            idrop = ii.randomizeStats((Equip) ii.getEquipById(itemid));
        } else {
            idrop = new Item(itemid, (byte) 0, (short) 1, (byte) 0);
        }
        MapleMapItem mdrop = new MapleMapItem(pos, idrop);
        addDrop(mdrop);
        broadcastMessage(ResCDropPool.DropEnterField(mdrop, ResCDropPool.DropEnterType.NORMAL, pos, pos));
        broadcastMessage(ResCDropPool.DropEnterField(mdrop, ResCDropPool.DropEnterType.UPDATE, pos, pos));
    }

    // mystic door.
    private LinkedHashMap<Integer, MapleDoor> doors = new LinkedHashMap<>();

    public void addDoor(MapleDoor door) {
        this.runningOid++;
        door.setObjectId(this.runningOid);
        this.doors.put(door.getObjectId(), door);
    }

    public boolean removeDoor(int object_id) {
        return this.doors.remove(object_id) != null;
    }

    public List<MapleDoor> getAllDoors() {
        ArrayList<MapleDoor> ret = new ArrayList<>();
        for (MapleDoor door : this.doors.values()) {
            ret.add(door);
        }
        return ret;
    }

    public MapleDoor getDoorByOid(int object_id) {
        return this.doors.get(object_id);
    }

    public void spawnDoor(MapleDoor door) {
        DebugLogger.DebugLog("Spawn Door : " + door.getMapId());
        addDoor(door);
        broadcastMessage(null);
    }

    // dynamic portal.
    private LinkedHashMap<Integer, MapleDynamicPortal> dynamicPortals = new LinkedHashMap<>();

    public void addDynamicPortal(MapleDynamicPortal dynamic_portal) {
        this.runningOid++;
        dynamic_portal.setObjectId(this.runningOid);
        this.dynamicPortals.put(dynamic_portal.getObjectId(), dynamic_portal);
    }

    public boolean removeDynamicPortal(int object_id) {
        return this.dynamicPortals.remove(object_id) != null;
    }

    public List<MapleDynamicPortal> getAllDynamicPortals() {
        ArrayList<MapleDynamicPortal> ret = new ArrayList<>();
        for (MapleDynamicPortal dynamic_portal : this.dynamicPortals.values()) {
            ret.add(dynamic_portal);
        }
        return ret;
    }

    public MapleDynamicPortal getDynamicPortalByOid(int object_id) {
        return this.dynamicPortals.get(object_id);
    }

    public MapleDynamicPortal findDynamicPortal(int portal_id) {
        for (MapleDynamicPortal dynamic_portal : this.dynamicPortals.values()) {
            if (dynamic_portal.getObjectId() == portal_id) {
                return dynamic_portal;
            }
        }
        return null;
    }

    public MapleDynamicPortal findDynamicPortalLink(int map_id_to) {
        DebugLogger.InfoLog("findDynamicPortalLink map_id_to" + map_id_to);
        for (MapleDynamicPortal dynamic_portal : this.dynamicPortals.values()) {
            DebugLogger.InfoLog("findDynamicPortalLink obj_to" + dynamic_portal.getMapID());
            if (dynamic_portal.getMapID() == map_id_to) {
                return dynamic_portal;
            }
        }
        return null;
    }

    public void spawnDynamicPortal(MapleDynamicPortal dynamic_portal) {
        addDynamicPortal(dynamic_portal);
        broadcastMessage(Res_JMS_CInstancePortalPool.InstancePortalCreated(dynamic_portal));
    }

    // reactor.
    private LinkedHashMap<Integer, MapleReactor> reactors = new LinkedHashMap<>();

    public void addReactor(MapleReactor reactor) {
        this.runningOid++;
        reactor.setObjectId(this.runningOid);
        this.reactors.put(reactor.getObjectId(), reactor);
    }

    public boolean removeReactor(int object_id) {
        return this.reactors.remove(object_id) != null;
    }

    public List<MapleReactor> getAllReactors() {
        ArrayList<MapleReactor> ret = new ArrayList<>();
        for (MapleReactor reactor : this.reactors.values()) {
            ret.add(reactor);
        }
        return ret;
    }

    public MapleReactor getReactorByOid(int oid) {
        return this.reactors.get(oid);
    }

    public MapleReactor getReactorById(int id) {
        MapleReactor ret = null;
        Iterator<MapleReactor> itr = this.reactors.values().iterator();
        while (itr.hasNext()) {
            MapleReactor n = itr.next();
            if (n.getReactorId() == id) {
                ret = n;
                break;
            }
        }
        return ret;
    }

    public MapleReactor getReactorByName(final String name) {
        for (MapleReactor mr : this.reactors.values()) {
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
        for (MapleReactor mr : this.reactors.values()) {
            mr.forceHitReactor((byte) state);
        }
    }

    public void shuffleReactors() {
        shuffleReactors(0, 9999999); //all
    }

    public void shuffleReactors(int first, int last) {
        List<Point> points = new ArrayList<>();
        for (MapleReactor mr : this.reactors.values()) {
            if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                points.add(mr.getPosition());
            }
        }
        Collections.shuffle(points);
        for (MapleReactor mr : this.reactors.values()) {
            if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                mr.setPosition(points.remove(points.size() - 1));
            }
        }
    }

    public void spawnReactor(MapleReactor reactor) {
        addReactor(reactor);
        broadcastMessage(ResCReactorPool.ReactorEnterField(reactor));
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
        removeReactor(reactor.getObjectId());
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
        for (MapleReactor reactor : this.reactors.values()) {
            broadcastMessage(ResCReactorPool.ReactorLeaveField(reactor));
            reactor.setAlive(false);
            reactor.setTimerActive(false);
            toSpawn.add(reactor);
        }
        for (MapleReactor r : toSpawn) {
            removeReactor(r.getObjectId());
            if (r.getReactorId() != 9980000 && r.getReactorId() != 9980001) { //guardians cpq
                respawnReactor(r);
            }
        }
    }

    // self and other players in range.
    public void broadcastMessage(ServerPacket packet, Point rangedFrom) {
        broadcastMessageInternal(null, packet, rangedFrom, false);
    }

    // other players in range.
    public void broadcastMessageTo(TacosCharacter source, ServerPacket packet, Point rangedFrom) {
        broadcastMessageInternal(source, packet, rangedFrom, false);
    }

    // self and other players.
    public void broadcastMessage(ServerPacket packet) {
        broadcastMessageInternal(null, packet, null, true);
    }

    // self and other players, or other players.
    public void broadcastMessage(TacosCharacter source, ServerPacket packet, boolean repeatToSource) {
        broadcastMessageInternal(repeatToSource ? null : source, packet, source.getPosition(), true);
    }

    private void broadcastMessageInternal(TacosCharacter source, ServerPacket packet, Point rangedFrom, boolean ignoreRange) {
        Iterator<MapleCharacter> ltr = this.players.values().iterator();
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
        for (final Object o : getAllItems()) {
            final MapleMapItem item = ((MapleMapItem) o);
            if (item.getOwner() == chr.getId()) {
                broadcastMessage(ResCDropPool.DropLeaveField(item, ResCDropPool.DropLeaveType.NORMAL, chr, 0), item.getPosition());
                if (item.getMeso() > 0) {
                    chr.gainMeso(item.getMeso(), false);
                } else {
                    MapleInventoryManipulator.addFromDrop(chr.getClient(), item.getItem(), false);
                }
                removeDrop(item.getObjectId());
            }
        }
    }

    public MapleSquad getSquadByMap() {
        String zz = null;
        switch (map_id) {
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
        switch (map_id) {
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
        cancelSquadSchedule();
        resetPortals();
        environment.clear();
    }

    public void removeDrops() {
        List<MapleMapItem> items = getAllItems();
        for (MapleMapItem i : items) {
            i.expire(this);
        }
    }

    // compatbility
    public MapleMap getReturnMap() {
        return TacosWorld.find(0).getChannelServer(channel).findMap(getReturnMapId());
    }

    public MapleMap getForcedReturnMap() {
        return TacosWorld.find(0).getChannelServer(channel).findMap(getForcedReturnId());
    }

    // update.
    private long time = 0;

    public boolean updateTime(long time, long interval) {
        if (this.time == 0) {
            this.time = time;
            return false;
        }

        long delta = time - this.time;
        if (interval <= delta) {
            this.time = time;
            return true;
        }

        return false;
    }
}
