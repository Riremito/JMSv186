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
package tacos.script.reactor;

import odin.server.Randomizer;
import tacos.client.TacosCharacter;
import tacos.client.TacosClient;
import tacos.odin.OdinReactorActionManager;
import tacos.server.TacosFieldSet;
import tacos.server.map.TacosMap;
import tacos.server.map.object.TacosReactor;

/**
 *
 * @author Riremito
 */
// Reactor の処理の基底クラス, js の rm (OdinReactorActionManager) と同じ処理を利用する
public abstract class ReactorScriptMan {

    private TacosClient client;
    private TacosReactor reactor;
    private OdinReactorActionManager rm;

    public abstract void action();

    public void start(TacosClient client, TacosReactor reactor) {
        this.client = client;
        this.reactor = reactor;
        this.rm = new OdinReactorActionManager(client, reactor);
        action();
    }

    public TacosClient getClient() {
        return this.client;
    }

    public TacosCharacter getPlayer() {
        return this.client.getPlayer();
    }

    public TacosReactor getReactor() {
        return this.reactor;
    }

    public TacosFieldSet getFieldSet(String name) {
        return TacosFieldSet.get(getPlayer(), name);
    }

    public TacosMap getMap() {
        return this.reactor.getMap();
    }

    public int getReactorId() {
        return this.reactor.getId();
    }

    // Reward.img のアイテムを落とす (Reward.img にデータがない場合はメルを落とす)
    public void dropItems() {
        this.rm.dropItems();
    }

    public void dropItems(boolean meso, int meso_chance, int meso_min, int meso_max) {
        this.rm.dropItems(meso, meso_chance, meso_min, meso_max);
    }

    public void dropItems(boolean meso, int meso_chance, int meso_min, int meso_max, int items_min) {
        this.rm.dropItems(meso, meso_chance, meso_min, meso_max, items_min);
    }

    // Reactor の位置に召喚
    public void summonMob(int mob_id) {
        this.rm.spawnMonster(mob_id);
    }

    // Reactor の位置に召喚
    public void summonMob(int mob_id, int count) {
        this.rm.spawnMonster(mob_id, count);
    }

    public void summonMob(int mob_id, int x, int y) {
        this.rm.spawnMonster(mob_id, x, y);
    }

    public void summonMob(int mob_id, int count, int x, int y) {
        this.rm.spawnMonster(mob_id, count, x, y);
    }

    public void killMob(int mob_id) {
        this.rm.killMonster(mob_id);
    }

    // Reactor の位置に召喚
    public void summonNpc(int npc_id) {
        this.rm.spawnNpc(npc_id);
    }

    public void summonNpc(int npc_id, int x, int y) {
        this.rm.spawnNpc(npc_id, x, y);
    }

    public void changeMusic(String music) {
        this.rm.changeMusic(music);
    }

    // マップ全体へのメッセージ
    public void mapMessage(String text) {
        this.rm.mapMessage(text);
    }

    public void mapMessage(int type, String text) {
        this.rm.mapMessage(type, text);
    }

    // Reactor を動かしたキャラクターへのメッセージ
    public void playerMessage(String text) {
        this.rm.playerMessage(text);
    }

    public void playerMessage(int type, String text) {
        this.rm.playerMessage(type, text);
    }

    public void warp(int map_id) {
        this.rm.warp(map_id);
    }

    public void warp(int map_id, int portal) {
        this.rm.warp(map_id, portal);
    }

    public void warp(int map_id, String portal) {
        this.rm.warp(map_id, portal);
    }

    public void warpS(int map_id, int portal) {
        this.rm.warpS(map_id, portal);
    }

    // マップにいる全てのキャラクターを移動
    public void warpMapAll(int map_id, int portal) {
        this.rm.warpMap(map_id, portal);
    }

    // 同じチャンネルのマップ
    public TacosMap getMapById(int map_id) {
        return this.rm.getMap(map_id);
    }

    public void killAllMob() {
        this.rm.killAllMob();
    }

    public void spawnZakum() {
        this.rm.spawnZakum();
    }

    public void gainGP(int gp) {
        this.rm.gainGP(gp);
    }

    public void givePartyNX(int amount) {
        this.rm.givePartyNX(amount);
    }

    // 移動先の候補からランダムに選んで移動 (ReactorAction.img の type 0)
    public void warpRandom(int[] map_ids, String[] portals) {
        int index = Randomizer.nextInt(map_ids.length);
        this.rm.warp(map_ids[index], portals[index]);
    }
}
