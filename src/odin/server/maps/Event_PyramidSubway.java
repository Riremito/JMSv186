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
package odin.server.maps;

import odin.client.MapleQuestStatus;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import odin.handling.world.MapleParty.MaplePartyCharacter;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCField_Massacre;
import tacos.packet.response.ResCField_MassacreResult;
import tacos.packet.ops.OpsFieldEffect;
import tacos.packet.response.builder.PB_FieldEffect;
import tacos.packet.response.ResCWvsContext;
import odin.server.Randomizer;
import odin.server.Timer.MapTimer;
import odin.server.quest.MapleQuest;
import tacos.wz.WzXML;

// subway (type -1) and nett's pyramid (type 0-3 = difficulty)
public class Event_PyramidSubway {

    private static final int SUBWAY = -1;
    private static final int SUBWAY_FIRST_MAP_ID = 910320100;
    private static final int SUBWAY_LAST_MAP_ID = 910320304;
    private static final int SUBWAY_BONUS_MAP_ID = 910320010;
    private static final int SUBWAY_CLEAR_MAP_ID = 910330001;
    private static final int SUBWAY_FAIL_MAP_ID = 910320001;
    private static final int PYRAMID_FIRST_MAP_ID = 926010100;
    private static final int PYRAMID_LAST_MAP_ID = 926013504;
    private static final int PYRAMID_BONUS_MAP_ID = 926010010;
    private static final int PYRAMID_CLEAR_MAP_ID = 926020001;
    private static final int PYRAMID_FAIL_MAP_ID = 926010001;
    private static final int YETI_MOB_ID = 9300021;
    // [type + 1][rank], rank 0-3
    private static final int[][] RANK_POINTS = {
        {22000, 17000, 10750, 7000}, // subway
        {60500, 55000, 46750, 22000},
        {66000, 60000, 51750, 24000},
        {71500, 65000, 55250, 26000},
        {77000, 70000, 59500, 28000},};
    // the kills needed for rank 0-3
    private static final int[] SUBWAY_RANK_KILLS = {2000, 1500, 1000, 500};
    private static final int[] PYRAMID_RANK_KILLS = {3000, 2000, 1500, 500};

    private int kill = 0;
    private int cool = 0;
    private int miss = 0;
    private int skill = 0;
    private final int type;
    private int energybar = 100;
    private ScheduledFuture<?> energyBarDecrease;
    private ScheduledFuture<?> timerSchedule;
    private ScheduledFuture<?> yetiSchedule;

    public Event_PyramidSubway(TacosCharacter chr) {
        int mapid = chr.getMapId();
        type = (mapid / 10000 == 91032) ? SUBWAY : mapid % 10000 / 1000;
        if (isLeader(chr)) {
            commenceTimerNextMap(chr, 1);
            energyBarDecrease = MapTimer.getInstance().register(() -> {
                energybar -= isPartyPlay(chr) ? 10 : 5;
                if (energybar <= 0) {
                    fail(chr);
                }
            }, 1000);
        }
    }

    private static boolean isLeader(TacosCharacter chr) {
        return chr.getParty() == null || chr.getParty().getLeader().equals(new MaplePartyCharacter(chr));
    }

    private static boolean isPartyPlay(TacosCharacter chr) {
        return chr.getParty() != null && chr.getParty().getMembers().size() > 1;
    }

    // the party members on the same map, or the character alone
    private static List<TacosCharacter> getPlayers(TacosCharacter chr) {
        List<TacosCharacter> ret = new ArrayList<>();
        if (!isPartyPlay(chr)) {
            ret.add(chr);
            return ret;
        }
        for (MaplePartyCharacter mpc : chr.getParty().getMembers()) {
            TacosCharacter target = chr.getMap().getPlayerById(mpc.getId());
            if (target != null) {
                ret.add(target);
            }
        }
        return ret;
    }

    private static void screenEffect(TacosCharacter chr, String path) {
        chr.SendPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Screen, PB_FieldEffect.builder().wz_path(path).build()));
    }

    // the first map of the 'count' maps from 'map_id' with no one in it
    private static TacosMap findEmptyMap(TacosCharacter player, int map_id, int count) {
        for (int i = 0; i < count; i++) {
            TacosMap map = player.findMap(map_id + i);
            if (map.getCharactersSize() == 0) {
                clearMap(map, false);
                return map;
            }
        }
        return null;
    }

    public void fullUpdate(TacosCharacter chr, int stage) {
        broadcastEnergy(chr, "massacre_party", chr.getParty() == null ? 0 : chr.getParty().getMembers().size());
        broadcastEnergy(chr, "massacre_miss", miss);
        broadcastEnergy(chr, "massacre_cool", cool);
        broadcastEnergy(chr, "massacre_skill", skill);
        broadcastEnergy(chr, "massacre_laststage", stage - 1);
        broadcastEnergy(chr, "massacre_hit", kill);
        broadcastUpdate(chr);
    }

    private static void cancel(ScheduledFuture<?> schedule) {
        if (schedule != null) {
            schedule.cancel(false);
        }
    }

    public void commenceTimerNextMap(TacosCharacter chr, int stage) {
        cancel(timerSchedule);
        timerSchedule = null;
        cancel(yetiSchedule);
        yetiSchedule = null;
        int time = (type == SUBWAY ? 180 : (stage == 1 ? 240 : 300)) - 1;
        for (TacosCharacter target : getPlayers(chr)) {
            target.SendPacket(ResCField.Clock(time));
            screenEffect(target, "killing/first/number/" + stage);
            screenEffect(target, "killing/first/stage");
            screenEffect(target, "killing/first/start");
            fullUpdate(target, stage);
        }
        if (type != SUBWAY && (stage == 4 || stage == 5)) { // yetis. temporary
            Point pos = chr.getPosition();
            TacosMap map = chr.getMap();
            yetiSchedule = MapTimer.getInstance().register(() -> {
                if (map.countMonsterById(YETI_MOB_ID) <= (stage == 4 ? 1 : 2)) {
                    map.spawnMonsterOnGroundBelow(WzXML.MOB.createMob(YETI_MOB_ID), new Point(pos));
                }
            }, 10000L);
        }
        timerSchedule = MapTimer.getInstance().schedule(() -> {
            boolean ret = (type == SUBWAY) ? warpNextMap_Subway(chr) : warpNextMap_Pyramid(chr, type);
            if (!ret) {
                fail(chr);
            }
        }, time * 1000L);
    }

    public void onKill(TacosCharacter player) {
        kill++;
        if (Randomizer.nextInt(100) < 5) { // monster properties coolDamage and coolDamageProb determine this, will code later
            cool++;
            broadcastEnergy(player, "massacre_cool", cool);
        }
        energybar = Math.min(energybar + 5, 100);
        if (type != SUBWAY) {
            for (int i = 5; i >= 1; i--) {
                if ((kill + cool) % (i * 100) == 0 && Randomizer.nextInt(100) < 50) {
                    broadcastEffect(player, "killing/yeti" + (i - 1));
                    break;
                }
            }
            // i dont want to give buffs as they could smuggle it
            if ((kill + cool) % 500 == 0) {
                skill++;
                broadcastEnergy(player, "massacre_skill", skill);
            }
        }
        broadcastUpdate(player);
        broadcastEnergy(player, "massacre_hit", kill);
    }

    public void onChangeMap(TacosCharacter player, int newmapid) {
        if ((type == SUBWAY && newmapid == SUBWAY_CLEAR_MAP_ID) || (type != SUBWAY && newmapid == PYRAMID_CLEAR_MAP_ID + type)) {
            succeed(player);
            return;
        }
        boolean in_event = (type == SUBWAY) ? (SUBWAY_FIRST_MAP_ID <= newmapid && newmapid <= SUBWAY_LAST_MAP_ID) : (PYRAMID_FIRST_MAP_ID <= newmapid && newmapid <= PYRAMID_LAST_MAP_ID);
        if (!in_event) {
            dispose(player);
            return;
        }
        if (isLeader(player)) {
            energybar = 100;
            commenceTimerNextMap(player, newmapid % 1000 / 100);
        }
    }

    private int getRank(int total_kill) {
        int[] needed = (type == SUBWAY) ? SUBWAY_RANK_KILLS : PYRAMID_RANK_KILLS;
        for (int rank = 0; rank < needed.length; rank++) {
            if (total_kill >= needed[rank]) {
                return rank;
            }
        }
        return 4;
    }

    public void succeed(TacosCharacter player) {
        MapleQuestStatus record = player.getQuestNAdd(MapleQuest.getInstance(type == SUBWAY ? 7662 : 7760));
        if (record.getCustomData() == null) {
            record.setCustomData("0");
        }
        int tk = kill + cool;
        record.setCustomData(String.valueOf(Integer.parseInt(record.getCustomData()) + tk));
        int rank = getRank(tk);
        int exp = 0;
        if (rank < 4) {
            int pt = (type + 1 < RANK_POINTS.length) ? RANK_POINTS[type + 1][rank] : RANK_POINTS[0][rank];
            exp = ((kill * 2) + (cool * 10) + pt) * player.getChannelServer().getExpRate();
            player.gainExp(exp, true, false, false);
        }
        screenEffect(player, "killing/clear");
        player.SendPacket(ResCField_MassacreResult.MassacreResult((byte) rank, exp));
        dispose(player);
    }

    public void fail(TacosCharacter player) {
        TacosMap map = player.findMap(type == SUBWAY ? SUBWAY_FAIL_MAP_ID : PYRAMID_FAIL_MAP_ID + type);
        changeMap(player, map, 1, 200, 2);
        dispose(player);
    }

    public void dispose(TacosCharacter player) {
        boolean lead = energyBarDecrease != null && timerSchedule != null;
        cancel(energyBarDecrease);
        energyBarDecrease = null;
        cancel(timerSchedule);
        timerSchedule = null;
        cancel(yetiSchedule);
        yetiSchedule = null;
        if (player.getParty() != null && lead && player.getParty().getMembers().size() > 1) {
            fail(player);
            return;
        }
        player.setPyramidSubway(null);
    }

    public void broadcastUpdate(TacosCharacter player) {
        for (TacosCharacter chr : getPlayers(player)) {
            chr.SendPacket(ResCField_Massacre.MassacreIncGauge(energybar));
        }
    }

    public void broadcastEffect(TacosCharacter player, String effect) {
        screenEffect(player, effect);
    }

    public void broadcastEnergy(TacosCharacter player, String type, int amount) {
        player.SendPacket(ResCWvsContext.sendString(1, type, String.valueOf(amount)));
    }

    public static boolean warpStartSubway(TacosCharacter player) {
        TacosMap map = findEmptyMap(player, SUBWAY_FIRST_MAP_ID, 5);
        if (map == null) {
            return false;
        }
        changeMap(player, map, 25, 30, 0);
        return true;
    }

    public static boolean warpBonusSubway(TacosCharacter player) {
        TacosMap map = findEmptyMap(player, SUBWAY_BONUS_MAP_ID, 20);
        if (map == null) {
            return false;
        }
        player.changeMapPortal(map, map.getPortal(0)); // solo
        return true;
    }

    public static boolean warpNextMap_Subway(TacosCharacter player) {
        int thisStage = (player.getMapId() - SUBWAY_FIRST_MAP_ID) / 100;
        clearMap(player.getMap(), true);
        if (thisStage >= 2) {
            changeMap(player, player.findMap(SUBWAY_CLEAR_MAP_ID), 1, 200, 1);
            return true;
        }
        TacosMap map = findEmptyMap(player, SUBWAY_FIRST_MAP_ID + ((thisStage + 1) * 100), 5);
        if (map == null) {
            return false;
        }
        changeMap(player, map, 1, 200, 1); // any level because they could level
        return true;
    }

    public static boolean warpStartPyramid(TacosCharacter player, int difficulty) {
        int minLevel = 40;
        int maxLevel = 60;
        switch (difficulty) {
            case 1:
                minLevel = 45;
                break;
            case 2:
                minLevel = 50;
                break;
            case 3:
                minLevel = 61;
                maxLevel = 200;
                break;
            default:
                break;
        }
        TacosMap map = findEmptyMap(player, PYRAMID_FIRST_MAP_ID + (difficulty * 1000), 5);
        if (map == null) {
            return false;
        }
        changeMap(player, map, minLevel, maxLevel, 0);
        return true;
    }

    public static boolean warpBonusPyramid(TacosCharacter player, int difficulty) {
        TacosMap map = findEmptyMap(player, PYRAMID_BONUS_MAP_ID + (difficulty * 20), 20);
        if (map == null) {
            return false;
        }
        player.changeMapPortal(map, map.getPortal(0)); // solo
        return true;
    }

    public static boolean warpNextMap_Pyramid(TacosCharacter player, int difficulty) {
        int first_map_id = PYRAMID_FIRST_MAP_ID + (difficulty * 1000);
        int thisStage = (player.getMapId() - first_map_id) / 100;
        clearMap(player.getMap(), true);
        if (thisStage >= 4) {
            changeMap(player, player.findMap(PYRAMID_CLEAR_MAP_ID + difficulty), 1, 200, 1);
            return true;
        }
        TacosMap map = findEmptyMap(player, first_map_id + ((thisStage + 1) * 100), 5);
        if (map == null) {
            return false;
        }
        changeMap(player, map, 1, 200, 1); // any level because they could level
        return true;
    }

    // clear : 0 = none, 1 = clear effect, 2 = fail effect. the player moves last.
    private static void changeMap(TacosCharacter player, TacosMap map, int minLevel, int maxLevel, int clear) {
        for (TacosCharacter chr : getPlayers(player)) {
            if (chr.getId() != player.getId() && minLevel <= chr.getLevel() && chr.getLevel() <= maxLevel) {
                warp(chr, map, clear);
            }
        }
        warp(player, map, clear);
    }

    private static void warp(TacosCharacter chr, TacosMap map, int clear) {
        if (clear == 1) {
            screenEffect(chr, "killing/clear");
        } else if (clear == 2) {
            screenEffect(chr, "killing/fail");
        }
        chr.changeMapPortal(map, map.getPortal(0));
    }

    private static void clearMap(TacosMap map, boolean check) {
        if (check && map.getCharactersSize() > 0) {
            return;
        }
        map.resetFully(false);
    }
}
