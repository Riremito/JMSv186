/*
This file is part of the ZeroFusion MapleStory Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>
ZeroFusion organized by "RMZero213" <RMZero213@hotmail.com>

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
package server.events;

import client.MapleCharacter;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.world.World;
import packet.response.ResCField;
import packet.response.wrapper.ResWrapper;
import server.MapleInventoryManipulator;
import server.RandomRewards;
import server.Randomizer;
import server.Timer.EventTimer;
import server.maps.MapleMap;
import server.maps.SavedLocationType;

public abstract class MapleEvent {

    protected int[] mapid;
    protected int channel;
    protected boolean isRunning = false;

    public MapleEvent(final int channel, final int[] mapid) {
        this.channel = channel;
        this.mapid = mapid;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public MapleMap getMap(final int i) {
        return getChannelServer().getMapFactory().getMap(mapid[i]);
    }

    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(channel);
    }

    public void broadcast(final MaplePacket packet) {
        for (int i = 0; i < mapid.length; i++) {
            getMap(i).broadcastMessage(packet);
        }
    }

    public void givePrize(final MapleCharacter chr) {
        final int reward = RandomRewards.getInstance().getEventReward();
        if (reward == 0) {
            final int mes = Randomizer.nextInt(9000000) + 1000000;
            chr.gainMeso(mes, true, false, false);
            chr.dropMessage(5, "You gained " + mes + " Mesos.");
        } else if (reward == 1) {
            final int cs = Randomizer.nextInt(4000) + 1000;
            chr.modifyCSPoints(1, cs, true);
            chr.dropMessage(5, "You gained " + cs + " cash.");
        } else if (reward == 2) {
            chr.setVPoints(chr.getVPoints() + 1);
            chr.dropMessage(5, "You gained 1 Vote Point.");
        } else if (reward == 3) {
            chr.addFame(10);
            chr.dropMessage(5, "You gained 10 Fame.");
        } else {
            int max_quantity = 1;
            switch (reward) {
                case 5062000:
                    max_quantity = 3;
                    break;
                case 5220000:
                    max_quantity = 25;
                    break;
                case 4031307:
                case 5050000:
                    max_quantity = 5;
                    break;
                case 2022121:
                    max_quantity = 10;
                    break;
            }
            final int quantity = (max_quantity > 1 ? Randomizer.nextInt(max_quantity) : 0) + 1;
            if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, quantity, "")) {
                MapleInventoryManipulator.addById(chr.getClient(), reward, (short) quantity);
            } else {
                givePrize(chr); //do again until they get
            }
            //5062000 = 1-3
            //5220000 = 1-25
            //5050000 = 1-5
            //2022121 = 1-10
            //4031307 = 1-5
        }
    }

    public void finished(MapleCharacter chr) { //most dont do shit here
    }

    public void onMapLoad(MapleCharacter chr) { //most dont do shit here
    }

    public void startEvent() {
    }

    public void warpBack(MapleCharacter chr) {
        int map = chr.getSavedLocation(SavedLocationType.EVENT);
        if (map <= -1) {
            map = 104000000;
        }
        final MapleMap mapp = chr.getClient().getChannelServer().getMapFactory().getMap(map);
        chr.changeMap(mapp, mapp.getPortal(0));
    }

    public void reset() {
        isRunning = true;
    }

    public void unreset() {
        isRunning = false;
    }

    public static final void setEvent(final ChannelServer cserv, final boolean auto) {
        if (auto) {
            for (MapleEventType t : MapleEventType.values()) {
                final MapleEvent e = cserv.getEvent(t);
                if (e.isRunning) {
                    for (int i : e.mapid) {
                        if (cserv.getEvent() == i) {
                            e.broadcast(ResWrapper.serverNotice(0, "The event will start in 30 seconds!"));
                            e.broadcast(ResCField.getClock(30));
                            EventTimer.getInstance().schedule(new Runnable() {

                                public void run() {
                                    e.startEvent();
                                }
                            }, 30000);
                            break;
                        }
                    }
                }
            }
        }
        cserv.setEvent(-1);
    }

    public static final void mapLoad(final MapleCharacter chr, final int channel) {
        if (chr == null) {
            return;
        } //o_o
        for (MapleEventType t : MapleEventType.values()) {
            final MapleEvent e = ChannelServer.getInstance(channel).getEvent(t);
            if (e.isRunning) {
                if (chr.getMapId() == 109050000) { //finished map
                    e.finished(chr);
                }
                for (int i : e.mapid) {
                    if (chr.getMapId() == i) {
                        e.onMapLoad(chr);
                    }
                }
            }
        }
    }

    public static final void onStartEvent(final MapleCharacter chr) {
        for (MapleEventType t : MapleEventType.values()) {
            final MapleEvent e = chr.getClient().getChannelServer().getEvent(t);
            if (e.isRunning) {
                for (int i : e.mapid) {
                    if (chr.getMapId() == i) {
                        e.startEvent();
                        chr.dropMessage(5, String.valueOf(t) + " has been started.");
                    }
                }
            }
        }
    }

    public static final String scheduleEvent(final MapleEventType event, final ChannelServer cserv) {
        if (cserv.getEvent() != -1 || cserv.getEvent(event) == null) {
            return "The event must not have been already scheduled.";
        }
        for (int i : cserv.getEvent(event).mapid) {
            if (cserv.getMapFactory().getMap(i).getCharactersSize() > 0) {
                return "The event is already running.";
            }
        }
        cserv.setEvent(cserv.getEvent(event).mapid[0]);
        cserv.getEvent(event).reset();
        World.Broadcast.broadcastMessage(ResWrapper.serverNotice(0, "Hello AsteriaSEA! Let's play a " + String.valueOf(event) + " event in channel " + cserv.getChannel() + "! Change to channel " + cserv.getChannel() + " and use @event command!").getBytes());
        return "";
    }
}
