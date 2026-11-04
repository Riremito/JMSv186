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
package tacos.script.portal;

import java.awt.Point;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import odin.constants.GameConstants;
import odin.handling.world.MapleParty;
import odin.server.MapleCarnivalParty;
import odin.server.MapleInventoryManipulator;
import odin.server.Randomizer;
import odin.server.quest.MapleQuest;
import tacos.client.TacosCharacter;
import tacos.client.TacosClient;
import tacos.debug.DebugLogger;
import tacos.odin.OdinPortalPlayerInteraction;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.ops.OpsFieldEffect;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_BroadcastMsg;
import tacos.packet.response.builder.PB_FieldEffect;
import tacos.script.js.TacosScriptNPC;
import tacos.script.npc.NpcMan;
import tacos.server.TacosFieldSet;
import tacos.server.map.TacosMap;
import tacos.server.map.TacosPortal;
import tacos.server.map.object.TacosMonster;
import tacos.server.map.object.TacosReactor;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
// Portal の処理の基底クラス, js の pi (OdinPortalPlayerInteraction) と同じ処理を利用する
// js の pi.playPortalSE はポータルスクリプトの実行前にサーバー側で送信済みのため不要
// enter の戻り値 : true = 移動した (または処理済み), false = 移動しなかった (クライアントの操作制限を解除する)
public abstract class PortalScriptMan {

    // lua の currentTime の形式
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yy/MM/dd/HH/mm");
    private TacosClient client;
    private TacosPortal portal;
    private OdinPortalPlayerInteraction pi;

    public abstract boolean enter();

    public boolean start(TacosClient client, TacosPortal portal) {
        this.client = client;
        this.portal = portal;
        this.pi = new OdinPortalPlayerInteraction(client, portal);
        return enter();
    }

    public TacosClient getClient() {
        return this.client;
    }

    public TacosCharacter getPlayer() {
        return this.client.getPlayer();
    }

    public TacosPortal getPortal() {
        return this.portal;
    }

    public String getPortalName() {
        return this.portal.getName();
    }

    // 複数のスクリプトを 1 つのクラスで処理する場合に利用
    public String getScriptName() {
        return this.portal.getScriptName();
    }

    public TacosMap getMap() {
        return this.pi.getMap();
    }

    public int getMapId() {
        return this.pi.getMapId();
    }

    // 同じチャンネルのマップ
    public TacosMap getMapById(int map_id) {
        return this.pi.getMap(map_id);
    }

    // ランダムなポータルへ移動
    public boolean warp(int map_id) {
        this.pi.warp(map_id);
        return true;
    }

    public boolean warp(int map_id, int portal) {
        this.pi.warp(map_id, portal);
        return true;
    }

    public boolean warp(int map_id, String portal) {
        this.pi.warp(map_id, portal);
        return true;
    }

    public boolean warpS(int map_id, int portal) {
        this.pi.warpS(map_id, portal);
        return true;
    }

    public boolean warpS(int map_id, String portal) {
        this.pi.warpS(map_id, portal);
        return true;
    }

    // 同じマップ内のポータルへ瞬間移動
    public boolean instantWarp(int portal) {
        this.pi.instantwarp(getMapId(), portal);
        return true;
    }

    // パーティーメンバーを移動 (パーティーがない場合は本人のみ)
    public boolean warpParty(int map_id) {
        this.pi.warpParty(map_id);
        return true;
    }

    public boolean warpParty(int map_id, int portal) {
        this.pi.warpParty(map_id, portal);
        return true;
    }

    // マップにいる全てのキャラクターを移動
    public boolean warpMapAll(int map_id, int portal) {
        this.pi.warpMap(map_id, portal);
        return true;
    }

    // ポータルに入ったキャラクターへのメッセージ
    public void playerMessage(String text) {
        this.pi.playerMessage(text);
    }

    public void playerMessage(int type, String text) {
        this.pi.playerMessage(type, text);
    }

    // マップ全体へのメッセージ
    public void mapMessage(String text) {
        this.pi.mapMessage(text);
    }

    public void mapMessage(int type, String text) {
        this.pi.mapMessage(type, text);
    }

    // lua の target.message
    public void message(String text) {
        getPlayer().dropMessage(OpsBroadcastMsg.BM_EVENT.get(), text);
    }

    // lua の field.notice
    public void fieldNotice(OpsBroadcastMsg type, String text) {
        getMap().broadcastPacket(ResCWvsContext.BroadcastMsg(type, PB_BroadcastMsg.builder().message(text).build()));
    }

    // 0 = 未開始, 1 = 進行中, 2 = 完了
    public int getQuestStatus(int quest_id) {
        return this.pi.getQuestStatus(quest_id);
    }

    public boolean isQuestActive(int quest_id) {
        return this.pi.isQuestActive(quest_id);
    }

    public boolean isQuestFinished(int quest_id) {
        return this.pi.isQuestFinished(quest_id);
    }

    public void forceStartQuest(int quest_id) {
        this.pi.forceStartQuest(quest_id);
    }

    public void forceStartQuest(int quest_id, String data) {
        this.pi.forceStartQuest(quest_id, data);
    }

    public void forceCompleteQuest(int quest_id) {
        this.pi.forceCompleteQuest(quest_id);
    }

    // lua の qr.get
    public String getQuestData(int quest_id) {
        String data = getPlayer().getQuest(MapleQuest.getInstance(quest_id)).getCustomData();
        return data == null ? "" : data;
    }

    // lua の qr.set
    public void setQuestData(int quest_id, String data) {
        this.pi.forceStartQuest(quest_id, data);
    }

    public String getInfoQuest(int quest_id) {
        return this.pi.getInfoQuest(quest_id);
    }

    public void updateInfoQuest(int quest_id, String data) {
        this.pi.updateInfoQuest(quest_id, data);
    }

    public boolean haveItem(int item_id) {
        return this.pi.haveItem(item_id);
    }

    public boolean haveItem(int item_id, int count) {
        return this.pi.haveItem(item_id, count);
    }

    // lua の inventory.itemCount
    public int getItemCount(int item_id) {
        return getPlayer().getItemQuantity(item_id, false);
    }

    public void gainItem(int item_id, int count) {
        this.pi.gainItem(item_id, (short) count);
    }

    public void removeAll(int item_id) {
        this.pi.removeAll(item_id);
    }

    public int getLevel() {
        return getPlayer().getLevel();
    }

    public int getJob() {
        return this.pi.getJob();
    }

    // 指定したマップにいるキャラクターの数
    public int getPlayerCount(int map_id) {
        return this.pi.getPlayerCount(map_id);
    }

    // 現在のマップにいるキャラクターの数, lua の field.getUserCount
    public int getUserCount() {
        return getMap().getCharactersSize();
    }

    public boolean haveMonster(int mob_id) {
        return this.pi.haveMonster(mob_id);
    }

    public int getMonsterCount(int map_id) {
        return this.pi.getMonsterCount(map_id);
    }

    public boolean isPartyMember() {
        return getPlayer().getParty() != null;
    }

    // パーティーリーダーか
    public boolean isLeader() {
        return this.pi.isLeader();
    }

    // lua の target.isPartyBoss
    public boolean isPartyBoss() {
        MapleParty party = getPlayer().getParty();
        return party != null && party.getLeader().getId() == getPlayer().getId();
    }

    // パーティーメンバーが全員同じマップにいるか
    public boolean allMembersHere() {
        return this.pi.allMembersHere();
    }

    // lua の target.transferParty (同じマップにいるパーティーメンバーを移動させる)
    public boolean transferParty(int map_id, String portal_name) {
        for (TacosCharacter member : TacosFieldSet.getPartyMembers(getPlayer())) {
            member.changeMapById(map_id, portal_name);
        }
        return true;
    }

    public TacosFieldSet getFieldSet(String name) {
        return TacosFieldSet.get(getPlayer(), name);
    }

    public void saveLocation(String type) {
        this.pi.saveLocation(type);
    }

    // 保存されていない場合は 100000000
    public int getSavedLocation(String type) {
        return this.pi.getSavedLocation(type);
    }

    public void clearSavedLocation(String type) {
        this.pi.clearSavedLocation(type);
    }

    // NPC の会話を開始 (Java を優先し、存在しない場合は js)
    public void openNpc(int npc_id) {
        if (NpcMan.start(getPlayer(), WzXML.NPC.getNpcInfo(npc_id), npc_id, npc_id)) {
            return;
        }
        TacosScriptNPC.getInstance().start(this.client, npc_id);
    }

    public void removeNpc(int map_id, int npc_id) {
        this.pi.removeNpc(map_id, npc_id);
    }

    // キャラクターの位置に召喚
    public void summonMob(int mob_id) {
        this.pi.spawnMonster(mob_id);
    }

    // ポータルの位置に召喚
    public void summonMobAtPortal(int mob_id, int count) {
        this.pi.spawnMonster(mob_id, count);
    }

    public void summonMob(int mob_id, int x, int y) {
        this.pi.spawnMonster(mob_id, x, y);
    }

    public void summonMob(int mob_id, int count, int x, int y) {
        this.pi.spawnMonster(mob_id, count, x, y);
    }

    public void resetMap(int map_id) {
        this.pi.resetMap(map_id);
    }

    public void changeMusic(String music) {
        this.pi.changeMusic(music);
    }

    public void teachSkill(int skill_id, int level, int master_level) {
        this.pi.teachSkill(skill_id, (byte) level, (byte) master_level);
    }

    // チュートリアルの吹き出しなど (UserEffect_AvatarOriented)
    public void avatarOriented(String path) {
        this.pi.ShowWZEffect(path);
    }

    // UserEffect_ReservedEffect
    public void reservedEffect(String path) {
        this.pi.showWZEffect(path);
    }

    // lua の field.effectScreen
    public void effectScreen(String path) {
        getMap().broadcastPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Screen, PB_FieldEffect.builder().wz_path(path).build()));
    }

    // lua の field.effectSound
    public void effectSound(String path) {
        getMap().broadcastPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Sound, PB_FieldEffect.builder().wz_path(path).build()));
    }

    // lua の field.effectObject
    public void effectObject(String path) {
        getMap().broadcastPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Object, PB_FieldEffect.builder().wz_path(path).build()));
    }

    // lua の field.enablePortal
    public boolean enablePortal(String portal_name, boolean enable) {
        TacosPortal target = getMap().getPortal(portal_name);
        if (target == null) {
            return false;
        }
        target.setPortalState(enable);
        return true;
    }

    // lua の portal.getPortalID
    public int getPortalId() {
        return this.portal.getId();
    }

    // lua の registerTransferField, map_id が -1 の場合は現在のマップ, portal_name が "" の場合は 0 番目のポータル
    public boolean transferField(int map_id, String portal_name) {
        if (map_id == -1) {
            map_id = getMapId();
        }
        return getPlayer().changeMapById(map_id, portal_name.isEmpty() ? null : portal_name);
    }

    // lua の field.transferFieldAll (同じマップにいるキャラクターを全員移動させる)
    public boolean transferFieldAll(int map_id, String portal_name) {
        for (TacosCharacter user : getMap().getAllPlayers()) {
            user.changeMapById(map_id, portal_name.isEmpty() ? null : portal_name);
        }
        return true;
    }

    // lua の inventory.exchange 相当
    // meso : 所持金の増減, items : アイテムIDと増減数の組 (負の値は消費, 正の値は入手)
    // 所持金・アイテムの不足またはインベントリに空きがない場合は何もせずに false を返す
    public boolean exchange(int meso, int... items) {
        TacosCharacter chr = getPlayer();
        if (items.length % 2 != 0) {
            DebugLogger.ErrorLog("PortalScriptMan : exchange invalid items.");
            return false;
        }
        if (meso < 0 && chr.getMeso() < -meso) {
            return false;
        }
        for (int i = 0; i < items.length; i += 2) {
            int item_id = items[i];
            int count = items[i + 1];
            if (count < 0 && !chr.haveItem(item_id, -count, false, true)) {
                return false;
            }
            if (count > 0 && !MapleInventoryManipulator.checkSpace(chr, item_id, count, "")) {
                return false;
            }
        }
        if (meso != 0) {
            chr.gainMeso(meso, true, false, true);
        }
        for (int i = 0; i < items.length; i += 2) {
            int item_id = items[i];
            int count = items[i + 1];
            if (count < 0) {
                MapleInventoryManipulator.removeById(chr, GameConstants.getInventoryType(item_id), item_id, -count, true, false);
            }
        }
        for (int i = 0; i < items.length; i += 2) {
            int item_id = items[i];
            int count = items[i + 1];
            if (count > 0) {
                MapleInventoryManipulator.addById(chr, item_id, count);
            }
        }
        return true;
    }

    // 所持しているアイテムを全て削除
    public void removeItemAll(int item_id) {
        int count = getItemCount(item_id);
        if (count > 0) {
            exchange(0, item_id, -count);
        }
    }

    // lua の currentTime
    public String currentTime() {
        return LocalDateTime.now().format(TIME_FORMAT);
    }

    // lua の compareTime( time1, time2 ), time1 - time2 (分), 不正な値の場合は time2 を十分に古い時刻として扱う
    public long compareTime(String time1, String time2) {
        try {
            return ChronoUnit.MINUTES.between(LocalDateTime.parse(time2, TIME_FORMAT), LocalDateTime.parse(time1, TIME_FORMAT));
        } catch (DateTimeParseException e) {
            return Long.MAX_VALUE;
        }
    }

    // lua の substring( text, start, length )
    public String substring(String text, int start, int length) {
        if (start < 0 || text.length() <= start) {
            return "";
        }
        return text.substring(start, Math.min(text.length(), start + length));
    }

    // lua の integer( text ), 数値ではない場合は 0
    public int integer(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // lua の random( min, max ), min 以上 max 以下
    public int random(int min, int max) {
        return Randomizer.rand(min, max);
    }

    // lua の shuffle( 1, text ), 文字の並びをランダムに入れ替える
    public String shuffle(String text) {
        ArrayList<Character> chars = new ArrayList<>();
        for (char c : text.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars);
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            sb.append(c);
        }
        return sb.toString();
    }

    // lua の Field( map_id ).getUserCount
    public int getUserCount(int map_id) {
        TacosMap map = getPlayer().getChannelServer().findMap(map_id);
        if (map == null) {
            return 0;
        }
        return map.getCharactersSize();
    }

    // lua の Field( map_id ).getMobCount( mob_id )
    public int getMobCount(int map_id, int mob_id) {
        TacosMap map = getPlayer().getChannelServer().findMap(map_id);
        if (map == null) {
            return 0;
        }
        int count = 0;
        for (TacosMonster monster : map.getAllMonsters()) {
            if (monster.getId() == mob_id) {
                count++;
            }
        }
        return count;
    }

    // lua の Field( map_id ).removeAllMob
    public void removeAllMob(int map_id) {
        TacosMap map = getPlayer().getChannelServer().findMap(map_id);
        if (map == null) {
            return;
        }
        map.killAllMonsters(false);
    }

    // lua の field.summonMob( x, y, mob_id )
    public void summonMobAt(int x, int y, int mob_id) {
        getMap().spawnMonsterOnGroundBelow(WzXML.MOB.createMob(mob_id), new Point(x, y));
    }

    // lua の target.incEXP
    public void incExp(int exp) {
        getPlayer().gainExp(exp, true, true, true);
    }

    // lua の target.incGuildPoint
    public void incGuildPoint(int gp) {
        this.pi.gainGP(gp);
    }

    // lua の target.getTeamForMCarnival, 0 = 赤チーム, 1 = 青チーム, チームに所属していない場合は -1
    public int getTeamForMCarnival() {
        MapleCarnivalParty carnival_party = getPlayer().getCarnivalParty();
        if (carnival_party == null) {
            return -1;
        }
        return carnival_party.getTeam();
    }

    // エヴァンのチュートリアルの進行状況 (InfoQuest 22013)
    public boolean getEvanIntroState(String data) {
        return this.pi.getEvanIntroState(data);
    }

    public void updateEvanIntroState(String data) {
        this.pi.updateEvanIntroState(data);
    }

    public void evanTutorial(String path, int value) {
        this.pi.evanTutorial(path, value);
    }

    public void movieClipIntroUI(boolean enabled) {
        this.pi.MovieClipIntroUI(enabled);
    }

    // チュートリアルのガイドの表示
    public void hireTutor(boolean summon) {
        this.pi.playerSummonHint(summon);
    }

    public void useItem(int item_id) {
        this.pi.useItem(item_id);
    }

    // アイテムの効果 (変身など) の解除
    public void cancelItem(int item_id) {
        this.pi.cancelItem(item_id);
    }

    public boolean canHold(int item_id) {
        return this.pi.canHold(item_id);
    }

    // チュートリアルのガイド
    public void summonMsg(int type) {
        this.pi.summonMsg(type);
    }

    public void summonMsg(String text) {
        this.pi.summonMsg(text);
    }

    // 現在のマップのモンスターの数
    public int getMonsterCount() {
        return getMap().getNumMonsters();
    }

    // 現在のマップのリアクターの状態, 存在しない場合は -1
    public int getReactorState(String name) {
        TacosReactor reactor = getMap().getReactorByName(name);
        if (reactor == null) {
            return -1;
        }
        return reactor.getState();
    }

    // 現在のマップのリアクターを叩く
    public void hitReactor(String name) {
        TacosReactor reactor = getMap().getReactorByName(name);
        if (reactor == null) {
            return;
        }
        reactor.hitReactor(this.client);
    }

    // 自分だけにマップエフェクトを表示
    public void showMapEffect(String path) {
        this.pi.showMapEffect(path);
    }

    // 上部に表示されるクエストメッセージ
    public void showQuestMsg(String text) {
        this.pi.showQuestMsg(text);
    }

    // アランのチュートリアルの吹き出し音
    public void aranStart() {
        this.pi.Aran_Start();
    }

    // 経験値倍率を適用した経験値
    public void gainExpR(int exp) {
        this.pi.gainExpR(exp);
    }

    public void gainAp(int ap) {
        getPlayer().gainAp((short) ap);
    }

    public void gainSp(int sp) {
        getPlayer().gainSP(sp);
    }

    // 指定したキャラクターにアイテムを与える
    public void gainItem(TacosCharacter chr, int item_id, int count) {
        this.pi.gainItem(item_id, (short) count, false, 0, 0, "", chr);
    }

    // 現在のマップにいるキャラクター
    public List<TacosCharacter> getMapPlayers() {
        return getMap().getAllPlayers();
    }

    public void endPartyQuest(int amount) {
        this.pi.endPartyQuest(amount);
    }

    // 武陵道場の次のマップへ移動
    public boolean dojoNextMap() {
        return this.pi.dojoAgent_NextMap(true, false);
    }

    // 武陵道場の上の階へ移動
    public void dojoGetUp() {
        this.pi.dojo_getUp();
    }
}
