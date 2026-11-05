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

import java.awt.Point;
import odin.handling.world.MapleParty;
import odin.handling.world.MapleParty.MaplePartyCharacter;
import odin.server.Randomizer;
import odin.server.life.MobSkill;
import tacos.client.TacosCharacter;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.ops.OpsMobSkill;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_BroadcastMsg;
import tacos.server.TacosFieldSet;
import tacos.server.map.TacosMap;
import tacos.server.map.TacosReward;
import tacos.server.map.object.TacosMonster;
import tacos.server.map.object.TacosReactor;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
// Reactor の処理の基底クラス, js の rm (OdinReactorActionManager) と同じ処理を Odin のクラスを利用せずに実装する
public abstract class ReactorScriptMan {

    private TacosCharacter chr;
    private TacosReactor reactor;

    public abstract void action();

    public void start(TacosCharacter chr, TacosReactor reactor) {
        this.chr = chr;
        this.reactor = reactor;
        action();
    }

    // Reactor の状態が変化した (破壊されていない) 場合の処理, 必要な場合のみ override する
    public void changeState(int old_state) {
    }

    public void startChangeState(TacosCharacter chr, TacosReactor reactor, int old_state) {
        this.chr = chr;
        this.reactor = reactor;
        changeState(old_state);
    }

    public TacosCharacter getPlayer() {
        return this.chr;
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
        dropItems(false, 0, 0, 0, 0);
    }

    public void dropItems(boolean meso, int meso_chance, int meso_min, int meso_max) {
        dropItems(meso, meso_chance, meso_min, meso_max, 0);
    }

    public void dropItems(boolean meso, int meso_chance, int meso_min, int meso_max, int items_min) {
        // Reward.img にデータがある場合は Reward.img のアイテムとメルを落とす, 引数は Reward.img にデータがない場合のみ利用する
        if (0 <= TacosReward.getReactorReward(getPlayer(), this.reactor)) {
            return;
        }
        int meso_count = 0;
        if (meso && Math.random() < (1 / (double) meso_chance)) {
            meso_count = 1;
        }
        // 最低限のドロップ数をメルで補う
        meso_count = Math.max(meso_count, items_min);
        Point position = this.reactor.getPosition();
        position.x -= (12 * meso_count);
        for (int i = 0; i < meso_count; i++) {
            int range = meso_max - meso_min;
            int meso_drop = ((0 < range) ? Randomizer.nextInt(range) : 0) + meso_min * getPlayer().getChannelServer().getMesoRate();
            getMap().spawnMesoDrop(meso_drop, position, this.reactor.getPosition(), getPlayer(), false, (byte) 0);
            position.x += 25;
        }
    }

    // Reactor の位置に召喚
    public void summonMob(int mob_id) {
        summonMob(mob_id, 1, getSummonPosition());
    }

    // Reactor の位置に召喚
    public void summonMob(int mob_id, int count) {
        summonMob(mob_id, count, getSummonPosition());
    }

    public void summonMob(int mob_id, int x, int y) {
        summonMob(mob_id, 1, new Point(x, y));
    }

    public void summonMob(int mob_id, int count, int x, int y) {
        summonMob(mob_id, count, new Point(x, y));
    }

    private void summonMob(int mob_id, int count, Point position) {
        for (int i = 0; i < count; i++) {
            getPlayer().getMap().spawnMonsterOnGroundBelow(WzXML.MOB.createMob(mob_id), position);
        }
    }

    // Reactor の少し上の位置 (モンスターと NPC の召喚位置)
    private Point getSummonPosition() {
        Point position = this.reactor.getPosition();
        position.y -= 10;
        return position;
    }

    public void killMob(int mob_id) {
        getMap().killMonster(mob_id);
    }

    // マップにいるモンスターの数
    public int getMobCount(int mob_id) {
        int count = 0;
        for (TacosMonster monster : getMap().getAllMonsters()) {
            if (monster.getId() == mob_id) {
                count++;
            }
        }
        return count;
    }

    // lua の field.nuffMob, マップにいるモンスターに mob skill の効果を与える
    public void nuffMob(int mob_id, int skill_id, int skill_level) {
        MobSkill mob_skill = WzXML.SKILL.getMobSkillData(skill_id, skill_level);
        if (mob_skill == null) {
            return;
        }
        for (TacosMonster monster : getMap().getAllMonsters()) {
            if (monster.getId() == mob_id) {
                mob_skill.applyEffect(null, monster, false);
            }
        }
    }

    // マクロ対策モンスターの弱体化 (回避率の低下 155 Lv.2, スキル封印 157 Lv.1)
    public void weakenMacroMob(int mob_id) {
        nuffMob(mob_id, OpsMobSkill.MOBSKILL_SEALSKILL.get(), 1);
        nuffMob(mob_id, OpsMobSkill.MOBSKILL_EVA.get(), 2);
    }

    // Reactor の位置に召喚
    public void summonNpc(int npc_id) {
        getPlayer().getMap().spawnNpc(npc_id, getSummonPosition());
    }

    public void summonNpc(int npc_id, int x, int y) {
        getPlayer().getMap().spawnNpc(npc_id, new Point(x, y));
    }

    public void changeMusic(String music) {
        getPlayer().getMap().setChangeBGM(music);
    }

    // マップ全体へのメッセージ
    public void mapMessage(String text) {
        this.chr.getMap().broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message(text).build()));
    }

    public void mapMessage(int type, String text) {
        this.chr.getMap().broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.find((byte) type), PB_BroadcastMsg.builder().message(text).build()));
    }

    // Reactor を動かしたキャラクターへのメッセージ
    public void playerMessage(String text) {
        this.chr.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message(text).build()));
    }

    public void playerMessage(int type, String text) {
        this.chr.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.find((byte) type), PB_BroadcastMsg.builder().message(text).build()));
    }

    public void warp(int map_id) {
        TacosMap map = getMapById(map_id);
        try {
            getPlayer().changeMapPortal(map, map.getPortal(Randomizer.nextInt(map.getPortals().size())));
        } catch (Exception e) {
            getPlayer().changeMapPortal(map, map.getPortal(0));
        }
    }

    public void warp(int map_id, int portal) {
        TacosCharacter chr = getPlayer();
        TacosMap map = getMapById(map_id);
        // 同じマップの近くのポータルの場合は瞬間移動
        if (portal != 0 && map_id == chr.getMapId() && new Point(chr.getMap().getPortal(portal).getPosition()).distanceSq(chr.getPosition()) < 90000.0) {
            this.chr.SendPacket(ResCUserLocal.UserTeleport((byte) portal));
            return;
        }
        chr.changeMapPortal(map, map.getPortal(portal));
    }

    public void warp(int map_id, String portal) {
        TacosCharacter chr = getPlayer();
        TacosMap map = getMapById(map_id);
        if (map_id == 109060000 || map_id == 109060002 || map_id == 109060004) {
            portal = map.getSnowballPortal();
        }
        // 同じマップの近くのポータルの場合は瞬間移動
        if (map_id == chr.getMapId() && new Point(chr.getMap().getPortal(portal).getPosition()).distanceSq(chr.getPosition()) < 90000.0) {
            this.chr.SendPacket(ResCUserLocal.UserTeleport((byte) chr.getMap().getPortal(portal).getId()));
            return;
        }
        chr.changeMapPortal(map, map.getPortal(portal));
    }

    public void warpS(int map_id, int portal) {
        TacosMap map = getMapById(map_id);
        getPlayer().changeMapPortal(map, map.getPortal(portal));
    }

    // マップにいる全てのキャラクターを移動
    public void warpMapAll(int map_id, int portal) {
        TacosMap map = getMapById(map_id);
        for (TacosCharacter user : getPlayer().getMap().getAllPlayers()) {
            user.changeMapPortal(map, map.getPortal(portal));
        }
    }

    // 同じチャンネルのマップ
    public TacosMap getMapById(int map_id) {
        return getPlayer().findMap(map_id);
    }

    public void killAllMob() {
        getPlayer().getMap().killAllMonsters(true);
    }

    public void spawnZakum() {
        getMap().spawnZakum(this.reactor);
    }

    public void gainGP(int gp) {
        int guild_id = getPlayer().getGuildId();
        if (guild_id <= 0) {
            return;
        }
        this.chr.getWorld().getGuild().gainGP(guild_id, gp);
    }

    public void givePartyNX(int amount) {
        MapleParty party = getPlayer().getParty();
        if (party == null || party.getMembers().size() == 1) {
            getPlayer().modifyCSPoints(1, amount, true);
            return;
        }
        for (MaplePartyCharacter mpc : party.getMembers()) {
            TacosCharacter member = getPlayer().getMap().getPlayerById(mpc.getId());
            if (member != null) {
                member.modifyCSPoints(1, amount, true);
            }
        }
    }

    // 移動先の候補からランダムに選んで移動 (ReactorAction.img の type 0)
    public void warpRandom(int[] map_ids, String[] portals) {
        int index = Randomizer.nextInt(map_ids.length);
        warp(map_ids[index], portals[index]);
    }
}
