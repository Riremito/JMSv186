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
package tacos.server;

import java.util.ArrayList;
import odin.client.MapleCharacter;
import odin.client.MapleCoolDownValueHolder;
import odin.client.MapleDiseaseValueHolder;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MaplePet;
import odin.server.Timer;
import odin.server.maps.MapleMap;
import tacos.debug.DebugLogger;
import tacos.network.MaplePacket;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.wz.data.ItemWz;

/**
 *
 * @author Riremito
 */
public class TacosWorld {

    private static ArrayList<TacosWorld> worlds = new ArrayList<>();

    public static ArrayList<TacosWorld> getWorlds() {
        return worlds;
    }

    public static TacosWorld find(int id) {
        for (TacosWorld world : worlds) {
            if (world.getId() == id) {
                return world;
            }
        }
        return null;
    }

    public static void add(TacosWorld world) {
        worlds.add(world);
    }

    private int id;
    private String name;
    private int flag;
    private String event_desc;
    private TacosLogin login = null;
    private ArrayList<TacosChannel> channels = new ArrayList<>();
    private TacosCashShop cashshop = null;
    private TacosITC itc = null;
    private ArrayList<MapleCharacter> player_migrating = new ArrayList<>();

    public TacosWorld(int id, String name, int flag, String event_desc) {
        this.id = id;
        this.name = name;
        this.flag = flag; // world flag icon.
        this.event_desc = event_desc; // BMS24 does not support this.
    }

    public TacosWorld() {
        // to do remove.
    }

    public void broadcastPacket(MaplePacket packet) {
        for (TacosChannel ch_server : this.channels) {
            ch_server.broadcastPacket(packet);
        }
    }

    public void broadcastMegaphonePacket(MaplePacket packet) {
        for (TacosChannel ch_server : this.channels) {
            ch_server.broadcastMegaphonePacket(packet);
        }
    }

    public int getId() {
        return this.id;
    }

    public int getFlag() {
        return this.flag;
    }

    public String getName() {
        return this.name;
    }

    public String getEvent() {
        return this.event_desc;
    }

    public void addChannel(TacosChannel channel) {
        this.channels.add(channel);
    }

    public ArrayList<TacosChannel> getChannels() {
        return this.channels;
    }

    // from 1.
    public TacosChannel getChannelServer(int channel) {
        for (TacosChannel ch_server : this.channels) {
            if (ch_server.getChannel() == channel) {
                return ch_server;
            }
        }
        return null;
    }

    public void setCashShop(TacosCashShop cashshop) {
        this.cashshop = cashshop;
    }

    public TacosCashShop getCashShop() {
        return this.cashshop;
    }

    public void setITC(TacosITC itc) {
        this.itc = itc;
    }

    public TacosITC getITC() {
        return this.itc;
    }

    public void setLogin(TacosLogin login) {
        this.login = login;
    }

    public TacosLogin getLogin() {
        return this.login;
    }

    public boolean addMigratingPlayer(MapleCharacter player) {
        if (this.player_migrating.contains(player)) {
            DebugLogger.DebugLog("addMigratingPlayer : NG.");
            return false;
        }
        this.player_migrating.add(player);
        return true;
    }

    public MapleCharacter findMigratingPlayer(int character_id) {
        for (MapleCharacter chr_mig : this.player_migrating) {
            if (chr_mig.getId() == character_id) {
                return chr_mig;
            }
        }
        DebugLogger.DebugLog("findMigratingPlayer : NG.");
        return null;
    }

    public boolean removeMigratingPlayer(MapleCharacter player) {
        MapleCharacter chr_mig = findMigratingPlayer(player.getId());
        if (chr_mig == null) {
            DebugLogger.DebugLog("removeMigratingPlayer : NG.");
            return false;
        }
        this.player_migrating.remove(chr_mig);
        return true;
    }

    public MapleCharacter findOnlinePlayer(String player_name) {
        return findOnlinePlayer(player_name, true);
    }

    public MapleCharacter findOnlinePlayer(String player_name, boolean cs_itc) {
        MapleCharacter player = null;
        // channel servers
        for (TacosChannel ch_server : this.channels) {
            player = ch_server.getOnlinePlayers().findByName(player_name);
            if (player != null) {
                return player;
            }
        }
        if (!cs_itc) {
            return player;
        }
        // itc
        player = this.itc.getOnlinePlayers().findByName(player_name);
        if (player != null) {
            return player;
        }
        // cs
        player = this.cashshop.getOnlinePlayers().findByName(player_name);
        if (player != null) {
            return player;
        }
        // not found.
        return player;
    }

    public MapleCharacter findOnlinePlayerById(int player_id) {
        return findOnlinePlayerById(player_id, true);
    }

    public MapleCharacter findOnlinePlayerById(int player_id, boolean cs_itc) {
        MapleCharacter player = null;
        // channel servers
        for (TacosChannel ch_server : this.channels) {
            player = ch_server.getOnlinePlayers().findById(player_id);
            if (player != null) {
                return player;
            }
        }
        if (!cs_itc) {
            return player;
        }
        // itc
        player = this.itc.getOnlinePlayers().findById(player_id);
        if (player != null) {
            return player;
        }
        // cs
        player = this.cashshop.getOnlinePlayers().findById(player_id);
        if (player != null) {
            return player;
        }
        // not found.
        return player;
    }

    public boolean hasMerchant(int character_id) {
        for (TacosChannel channel : this.channels) {
            if (channel.containsMerchant(character_id)) {
                return true;
            }
        }
        return false;
    }

    // map updates
    public void registerRespawn() {
        Runnable respawn = new Runnable() {
            private int numTimes = 0;

            @Override
            public void run() {
                numTimes++;
                for (TacosChannel channel : getChannels()) {
                    for (MapleMap map : channel.getMapFactory().getAllMaps()) {
                        map.updateMapItem();
                        map.updateSpawn();
                        for (MapleCharacter player : map.getCharacters()) {
                            handleCooldowns(player, numTimes);
                        }
                    }
                    for (MapleMap map : channel.getMapFactory().getAllInstanceMaps()) {
                        map.updateMapItem();
                        map.updateSpawn();
                        for (MapleCharacter player : map.getCharacters()) {
                            handleCooldowns(player, numTimes);
                        }
                    }
                }
            }
        };

        Timer.WorldTimer.getInstance().register(respawn, 3000);
    }

    public boolean handleCooldowns(MapleCharacter player, int numTimes) {
        long now = System.currentTimeMillis();
        for (MapleCoolDownValueHolder m : player.getCooldowns()) {
            if (m.startTime + m.length < now) {
                int skil = m.skillId;
                player.removeCooldown(skil);
                player.SendPacket(ResCUserLocal.SkillCooltimeSet(skil, 0));
            }
        }
        for (MapleDiseaseValueHolder m : player.getAllDiseases()) {
            if (m.startTime + m.length < now) {
                player.dispelDebuff(m.disease);
            }
        }

        // ?_?
        if (numTimes % 20 != 0) {
            return true;
        }

        // pet
        for (MaplePet pet : player.getPets()) {
            if (!pet.getSummoned()) {
                continue;
            }
            if (pet.getPetItemId() == 5000054 && 0 < pet.getSecondsLeft()) {
                pet.setSecondsLeft(pet.getSecondsLeft() - 1);
                if (pet.getSecondsLeft() <= 0) {
                    player.unequipPet(pet, true, true);
                    continue;
                }
            }
            int newFullness = pet.getFullness() - ItemWz.get().getHunger(pet.getPetItemId());
            if (newFullness <= 5) {
                pet.setFullness(15);
                player.unequipPet(pet, true, true);
                continue;
            }
            pet.setFullness(newFullness);
            player.SendPacket(ResWrapper.updatePet(pet, player.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition())));
        }

        return true;
    }

}
