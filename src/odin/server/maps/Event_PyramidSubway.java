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

import odin.client.MapleCharacter;
import odin.client.MapleQuestStatus;
import odin.handling.world.MaplePartyCharacter;
import java.awt.Point;
import java.util.concurrent.ScheduledFuture;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCField_Massacre;
import tacos.packet.response.ResCField_MassacreResult;
import tacos.packet.response.wrapper.ResWrapper;
import odin.server.Randomizer;
import odin.server.Timer.MapTimer;
import odin.server.quest.MapleQuest;
import odin.server.life.MapleLifeFactory;

public class Event_PyramidSubway {

    private int kill = 0;
    private int cool = 0;
    private int miss = 0;
    private int skill = 0;
    private int type;
    private int energybar = 100;
    private boolean broaded = false;
    private ScheduledFuture<?> energyBarDecrease;
    private ScheduledFuture<?> timerSchedule;
    private ScheduledFuture<?> yetiSchedule;
    //type: -1 = subway, 0-3 = difficulty of nett's pyramid.

    public Event_PyramidSubway(final MapleCharacter chr) {
        final int mapid = chr.getMapId();
        if (mapid / 10000 == 91032) {
            type = -1;
        } else {
            type = mapid % 10000 / 1000;
        }
        if (chr.getParty() == null || chr.getParty().getLeader().equals(new MaplePartyCharacter(chr))) {
            commenceTimerNextMap(chr, 1);
            energyBarDecrease = MapTimer.getInstance().register(new Runnable() {

                public void run() {
                    energybar -= (chr.getParty() != null && chr.getParty().getMembers().size() > 1 ? 10 : 5);
                    if (broaded) {
                        //broadcastUpdate(c);
                        chr.getMap().respawn(true);
                    } else {
                        broaded = true;
                    }
                    if (energybar <= 0) { //why
                        fail(chr);
                    }
                }
            }, 1000);
        }
    }

    public final void fullUpdate(final MapleCharacter chr, final int stage) {
        broadcastEnergy(chr, "massacre_party", chr.getParty() == null ? 0 : chr.getParty().getMembers().size()); //huh
        broadcastEnergy(chr, "massacre_miss", miss);
        broadcastEnergy(chr, "massacre_cool", cool);
        broadcastEnergy(chr, "massacre_skill", skill);
        broadcastEnergy(chr, "massacre_laststage", stage - 1);
        broadcastEnergy(chr, "massacre_hit", kill);
        broadcastUpdate(chr);
    }

    public final void commenceTimerNextMap(final MapleCharacter chr, final int stage) {
        if (timerSchedule != null) {
            timerSchedule.cancel(false);
            timerSchedule = null;
        }
        if (yetiSchedule != null) {
            yetiSchedule.cancel(false);
            yetiSchedule = null;
        }
        final MapleMap ourMap = chr.getMap();
        final int time = (type == -1 ? 180 : (stage == 1 ? 240 : 300)) - 1;
        if (chr.getParty() != null && chr.getParty().getMembers().size() > 1) {
            for (MaplePartyCharacter mpc : chr.getParty().getMembers()) {
                final MapleCharacter target = ourMap.getCharacterById(mpc.getId());
                if (target != null) {
                    target.SendPacket(ResCField.Clock(time));
                    target.SendPacket(ResWrapper.showEffect("killing/first/number/" + stage));
                    target.SendPacket(ResWrapper.showEffect("killing/first/stage"));
                    target.SendPacket(ResWrapper.showEffect("killing/first/start"));
                    fullUpdate(target, stage);
                }
            }
        } else {
            chr.SendPacket(ResCField.Clock(time));
            chr.SendPacket(ResWrapper.showEffect("killing/first/number/" + stage));
            chr.SendPacket(ResWrapper.showEffect("killing/first/stage"));
            chr.SendPacket(ResWrapper.showEffect("killing/first/start"));
            fullUpdate(chr, stage);
        }
        if (type != -1 && (stage == 4 || stage == 5)) { //yetis. temporary
            final Point pos = chr.getPosition();
            final MapleMap map = chr.getMap();
            yetiSchedule = MapTimer.getInstance().register(new Runnable() {

                public void run() {
                    if (map.countMonsterById(9300021) <= (stage == 4 ? 1 : 2)) {
                        map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9300021), new Point(pos));
                    }
                }
            }, 10000L);
        }
        timerSchedule = MapTimer.getInstance().schedule(new Runnable() {

            public void run() {
                boolean ret = false;
                if (type == -1) {
                    ret = warpNextMap_Subway(chr);
                } else {
                    ret = warpNextMap_Pyramid(chr, type);
                }
                if (!ret) {
                    fail(chr);
                }
            }
        }, time * 1000L);
    }

    public final void onKill(final MapleCharacter player) {
        kill++;
        if (Randomizer.nextInt(100) < 5) { //monster properties coolDamage and coolDamageProb determine this, will code later
            cool++;
            broadcastEnergy(player, "massacre_cool", cool);
        }
        energybar += 5;
        if (energybar > 100) {
            energybar = 100; //rofl
        }
        if (type != -1) {
            for (int i = 5; i >= 1; i--) {
                if ((kill + cool) % (i * 100) == 0 && Randomizer.nextInt(100) < 50) {
                    broadcastEffect(player, "killing/yeti" + (i - 1));
                    break;
                }
            }
            //i dont want to give buffs as they could smuggle it
            if ((kill + cool) % 500 == 0) {
                skill++;
                broadcastEnergy(player, "massacre_skill", skill);
            }
        }

        broadcastUpdate(player);
        broadcastEnergy(player, "massacre_hit", kill);
    }

    public final void onChangeMap(final MapleCharacter player, final int newmapid) {
        if ((newmapid == 910330001 && type == -1) || (newmapid == 926020001 + type && type != -1)) {
            succeed(player);
        } else {
            if (type == -1 && (newmapid < 910320100 || newmapid > 910320304)) {
                dispose(player);
                return;
            } else if (type != -1 && (newmapid < 926010100 || newmapid > 926013504)) {
                dispose(player);
                return;
            } else if (player.getParty() == null || player.getParty().getLeader().equals(new MaplePartyCharacter(player))) {
                energybar = 100;
                commenceTimerNextMap(player, newmapid % 1000 / 100);
            }
        }
    }

    public final void succeed(final MapleCharacter player) {
        final MapleQuestStatus record = player.getQuestNAdd(MapleQuest.getInstance(type == -1 ? 7662 : 7760));
        String data = record.getCustomData();
        if (data == null) {
            record.setCustomData("0");
            data = record.getCustomData();
        }
        final int mons = Integer.parseInt(data);
        final int tk = kill + cool;
        record.setCustomData(String.valueOf(mons + tk));
        byte rank = 4;
        if (type == -1) {
            if (tk >= 2000) {
                rank = 0;
            } else if (tk >= 1500 && tk <= 1999) {
                rank = 1;
            } else if (tk >= 1000 && tk <= 1499) {
                rank = 2;
            } else if (tk >= 500 && tk <= 999) {
                rank = 3;
            }
        } else {
            if (tk >= 3000) {
                rank = 0;
            } else if (tk >= 2000 && tk <= 2999) {
                rank = 1;
            } else if (tk >= 1500 && tk <= 1999) {
                rank = 2;
            } else if (tk >= 500 && tk <= 1499) {
                rank = 3;
            }
        }

        int pt = 0;
        switch (type) {
            case 0:
                switch (rank) {
                    case 0:
                        pt = 60500;
                        break;
                    case 1:
                        pt = 55000;
                        break;
                    case 2:
                        pt = 46750;
                        break;
                    case 3:
                        pt = 22000;
                        break;
                }
                break;
            case 1:
                switch (rank) {
                    case 0:
                        pt = 66000;
                        break;
                    case 1:
                        pt = 60000;
                        break;
                    case 2:
                        pt = 51750;
                        break;
                    case 3:
                        pt = 24000;
                        break;
                }
                break;
            case 2:
                switch (rank) {
                    case 0:
                        pt = 71500;
                        break;
                    case 1:
                        pt = 65000;
                        break;
                    case 2:
                        pt = 55250;
                        break;
                    case 3:
                        pt = 26000;
                        break;
                }
                break;
            case 3:
                switch (rank) {
                    case 0:
                        pt = 77000;
                        break;
                    case 1:
                        pt = 70000;
                        break;
                    case 2:
                        pt = 59500;
                        break;
                    case 3:
                        pt = 28000;
                        break;
                }
                break;
            default:
                switch (rank) {
                    case 0:
                        pt = 22000;
                        break;
                    case 1:
                        pt = 17000;
                        break;
                    case 2:
                        pt = 10750;
                        break;
                    case 3:
                        pt = 7000;
                        break;
                }
                break;
        }
        int exp = 0;
        if (rank < 4) {
            exp = (((kill * 2) + (cool * 10)) + pt) * player.getChannelServer().getExpRate();
            player.gainExp(exp, true, false, false);
        }
        player.SendPacket(ResWrapper.showEffect("killing/clear"));
        player.SendPacket(ResCField_MassacreResult.MassacreResult(rank, exp));
        dispose(player);
    }

    public final void fail(final MapleCharacter player) {
        final MapleMap map;
        if (type == -1) {
            map = player.findMap(910320001);
        } else {
            map = player.findMap(926010001 + type);
        }
        changeMap(player, map, 1, 200, 2);
        dispose(player);
    }

    public final void dispose(final MapleCharacter player) {
        final boolean lead = energyBarDecrease != null && timerSchedule != null;
        if (energyBarDecrease != null) {
            energyBarDecrease.cancel(false);
            energyBarDecrease = null;
        }
        if (timerSchedule != null) {
            timerSchedule.cancel(false);
            timerSchedule = null;
        }
        if (yetiSchedule != null) {
            yetiSchedule.cancel(false);
            yetiSchedule = null;
        }
        if (player.getParty() != null && lead && player.getParty().getMembers().size() > 1) {
            fail(player);
            return;
        }
        player.setPyramidSubway(null);
    }

    public final void broadcastUpdate(final MapleCharacter player) {
        final MapleMap map = player.getMap();
        if (player.getParty() != null && player.getParty().getMembers().size() > 1) {
            for (MaplePartyCharacter mpc : player.getParty().getMembers()) {
                final MapleCharacter chr = map.getCharacterById(mpc.getId());
                if (chr != null) {
                    chr.SendPacket(ResCField_Massacre.MassacreIncGauge(energybar));
                }
            }
        } else {
            player.SendPacket(ResCField_Massacre.MassacreIncGauge(energybar));
        }
    }

    public final void broadcastEffect(final MapleCharacter player, final String effect) {
        player.SendPacket(ResWrapper.showEffect(effect));
    }

    public final void broadcastEnergy(final MapleCharacter player, final String type, final int amount) {
        player.SendPacket(ResWrapper.sendPyramidEnergy(type, String.valueOf(amount)));
    }

    public static boolean warpStartSubway(MapleCharacter player) {
        final int mapid = 910320100;
        for (int i = 0; i < 5; i++) {
            final MapleMap map = player.findMap(mapid + i);
            if (map.getCharactersSize() == 0) {
                clearMap(map, false);
                changeMap(player, map, 25, 30);
                return true;
            }
        }
        return false;
    }

    public static boolean warpBonusSubway(MapleCharacter player) {
        final int mapid = 910320010;

        for (int i = 0; i < 20; i++) {
            final MapleMap map = player.findMap(mapid + i);
            if (map.getCharactersSize() == 0) {
                clearMap(map, false);
                player.changeMap(map, map.getPortal(0));//solo
                return true;
            }
        }
        return false;
    }

    public static boolean warpNextMap_Subway(MapleCharacter player) {
        final int currentmap = player.getMapId();
        final int thisStage = (currentmap - 910320100) / 100;

        MapleMap map = player.getMap();
        clearMap(map, true);
        if (thisStage >= 2) {
            map = player.findMap(910330001);
            changeMap(player, map, 1, 200, 1);
            return true;
        }
        final int nextmapid = 910320100 + ((thisStage + 1) * 100);
        for (int i = 0; i < 5; i++) {
            map = player.findMap(nextmapid + i);
            if (map.getCharactersSize() == 0) {
                clearMap(map, false);
                changeMap(player, map, 1, 200, 1); //any level because they could level
                return true;
            }
        }
        return false;
    }

    public static boolean warpStartPyramid(MapleCharacter player, final int difficulty) {
        final int mapid = 926010100 + (difficulty * 1000);
        int minLevel = 40, maxLevel = 60;
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
        }
        for (int i = 0; i < 5; i++) {
            final MapleMap map = player.findMap(mapid + i);
            if (map.getCharactersSize() == 0) {
                clearMap(map, false);
                changeMap(player, map, minLevel, maxLevel);
                return true;
            }
        }
        return false;
    }

    public static boolean warpBonusPyramid(MapleCharacter player, int difficulty) {
        final int mapid = 926010010 + (difficulty * 20);
        for (int i = 0; i < 20; i++) {
            final MapleMap map = player.findMap(mapid + i);
            if (map.getCharactersSize() == 0) {
                clearMap(map, false);
                player.changeMap(map, map.getPortal(0));//solo
                return true;
            }
        }
        return false;
    }

    public static boolean warpNextMap_Pyramid(MapleCharacter player, int difficulty) {
        final int currentmap = player.getMapId();
        final int thisStage = (currentmap - (926010100 + (difficulty * 1000))) / 100;

        MapleMap map = player.getMap();
        clearMap(map, true);
        if (thisStage >= 4) {
            map = player.findMap(926020001 + difficulty);
            changeMap(player, map, 1, 200, 1);
            return true;
        }
        final int nextmapid = 926010100 + ((thisStage + 1) * 100) + (difficulty * 1000);
        for (int i = 0; i < 5; i++) {
            map = player.findMap(nextmapid + i);
            if (map.getCharactersSize() == 0) {
                clearMap(map, false);
                changeMap(player, map, 1, 200, 1); //any level because they could level
                return true;
            }
        }
        return false;
    }

    private static final void changeMap(final MapleCharacter player, final MapleMap map, final int minLevel, final int maxLevel) {
        changeMap(player, map, minLevel, maxLevel, 0);
    }

    private static final void changeMap(final MapleCharacter player, final MapleMap map, final int minLevel, final int maxLevel, final int clear) {
        final MapleMap oldMap = player.getMap();
        if (player.getParty() != null && player.getParty().getMembers().size() > 1) {
            for (MaplePartyCharacter mpc : player.getParty().getMembers()) {
                final MapleCharacter chr = oldMap.getCharacterById(mpc.getId());
                if (chr != null && chr.getId() != player.getId() && chr.getLevel() >= minLevel && chr.getLevel() <= maxLevel) {
                    if (clear == 1) {
                        chr.SendPacket(ResWrapper.showEffect("killing/clear"));
                    } else if (clear == 2) {
                        chr.SendPacket(ResWrapper.showEffect("killing/fail"));
                    }
                    chr.changeMap(map, map.getPortal(0));
                }
            }
        }
        if (clear == 1) {
            player.SendPacket(ResWrapper.showEffect("killing/clear"));
        } else if (clear == 2) {
            player.SendPacket(ResWrapper.showEffect("killing/fail"));
        }
        player.changeMap(map, map.getPortal(0));
    }

    private static final void clearMap(final MapleMap map, final boolean check) {
        if (check && map.getCharactersSize() > 0) {
            return;
        }
        map.resetFully(false);
    }
}
