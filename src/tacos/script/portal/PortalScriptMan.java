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
import odin.client.inventory.Equip;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.handling.world.MapleParty;
import odin.handling.world.MapleParty.MaplePartyCharacter;
import odin.server.MapleCarnivalParty;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.Randomizer;
import odin.server.maps.Event_DojoAgent;
import odin.server.maps.SavedLocationType;
import odin.server.quest.MapleQuest;
import tacos.client.TacosCharacter;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.ops.OpsFieldEffect;
import tacos.packet.ops.OpsMessage;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.request.ReqCUser;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCScriptMan;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_BroadcastMsg;
import tacos.packet.response.builder.PB_FieldEffect;
import tacos.packet.response.builder.PB_Message;
import tacos.packet.response.builder.PB_UserEffect;
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
// Portal の処理の基底クラス, js の pi (OdinPortalPlayerInteraction) と同じ処理を Odin のクラスを利用せずに実装する
// js の pi.playPortalSE はポータルスクリプトの実行前にサーバー側で送信済みのため不要
// enter の戻り値 : true = 移動した (または処理済み), false = 移動しなかった (クライアントの操作制限を解除する)
public abstract class PortalScriptMan {

    // lua の currentTime の形式
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yy/MM/dd/HH/mm");
    private TacosCharacter chr;
    private TacosPortal portal;

    public abstract boolean enter();

    public boolean start(TacosCharacter chr, TacosPortal portal) {
        this.chr = chr;
        this.portal = portal;
        return enter();
    }

    public TacosCharacter getPlayer() {
        return this.chr;
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
        return getPlayer().getMap();
    }

    public int getMapId() {
        return getMap().getId();
    }

    // 同じチャンネルのマップ
    public TacosMap getMapById(int map_id) {
        return getPlayer().findMap(map_id);
    }

    public boolean warp(int map_id) {
        return getPlayer().changeMap(map_id);
    }

    public boolean warp(int map_id, int portal_id) {
        TacosCharacter chr = getPlayer();
        TacosMap map = getMapById(map_id);
        // 同じマップの近くのポータルの場合は瞬間移動
        if (portal_id != 0 && map_id == chr.getMapId() && new Point(chr.getMap().getPortalById(portal_id).getPosition()).distanceSq(chr.getPosition()) < 90000.0) {
            this.chr.SendPacket(ResCUserLocal.UserTeleport((byte) portal_id));
            return true;
        }
        chr.changeMap(map_id, portal_id);
        return true;
    }

    public boolean warp(int map_id, String portal_name) {
        if (map_id == 109060000 || map_id == 109060002 || map_id == 109060004) {
            return chr.changeMap(map_id, getMapById(map_id).getSnowballPortal());
        }
        return chr.changeMap(map_id, portal_name);
    }

    // 同じマップ内のポータルへ瞬間移動
    public boolean instantWarp(int portal) {
        this.chr.SendPacket(ResCUserLocal.UserTeleport((byte) portal));
        return true;
    }

    public boolean warpParty(int map_id) {
        MapleParty party = getPlayer().getParty();
        if (party == null || party.getMembers().size() == 1) {
            return warp(map_id, 0);
        }
        TacosMap map = getMapById(map_id);
        for (MaplePartyCharacter mpc : party.getMembers()) {
            TacosCharacter member = getMap().getPlayerById(mpc.getId());
            if (member != null) {
                member.changeMap(map_id, 0);
            }
        }
        return true;
    }

    public boolean warpParty(int map_id, int portal) {
        MapleParty party = getPlayer().getParty();
        if (party == null || party.getMembers().size() == 1) {
            if (portal < 0) {
                return warp(map_id);
            }
            return warp(map_id, portal);
        }
        TacosMap map = getMapById(map_id);
        for (MaplePartyCharacter mpc : party.getMembers()) {
            TacosCharacter member = getMap().getPlayerById(mpc.getId());
            if (member == null) {
                continue;
            }
            // portal が負の値の場合はランダムなポータル
            if (portal < 0) {
                member.changeMap(map_id);
            } else {
                member.changeMap(map_id, portal);
            }
        }
        return true;
    }

    public boolean warpParty(int map_id, String portal_name) {
        int portal_id = getMapById(map_id).getPortalByName(portal_name).getId();
        return warpParty(map_id, portal_id);
    }

    public boolean warpMapAll(int map_id, int portal) {
        for (TacosCharacter user : getMap().getAllPlayers()) {
            user.changeMap(map_id, portal);
        }
        return true;
    }

    // ポータルに入ったキャラクターへのメッセージ
    public void playerMessage(String text) {
        this.chr.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message(text).build()));
    }

    public void playerMessage(int type, String text) {
        this.chr.SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.find((byte) type), PB_BroadcastMsg.builder().message(text).build()));
    }

    // マップ全体へのメッセージ
    public void mapMessage(String text) {
        getMap().broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message(text).build()));
    }

    public void mapMessage(int type, String text) {
        getMap().broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.find((byte) type), PB_BroadcastMsg.builder().message(text).build()));
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
        return getPlayer().getQuestStatus(quest_id);
    }

    public boolean isQuestActive(int quest_id) {
        return getQuestStatus(quest_id) == 1;
    }

    public boolean isQuestFinished(int quest_id) {
        return getQuestStatus(quest_id) == 2;
    }

    public void forceStartQuest(int quest_id) {
        MapleQuest.getInstance(quest_id).forceStart(getPlayer(), 0, null);
    }

    public void forceStartQuest(int quest_id, String data) {
        MapleQuest.getInstance(quest_id).forceStart(getPlayer(), 0, data);
    }

    public void forceCompleteQuest(int quest_id) {
        MapleQuest.getInstance(quest_id).forceComplete(getPlayer(), 0);
    }

    // lua の qr.get
    public String getQuestData(int quest_id) {
        String data = getPlayer().getQuest(MapleQuest.getInstance(quest_id)).getCustomData();
        return data == null ? "" : data;
    }

    // lua の qr.set
    public void setQuestData(int quest_id, String data) {
        forceStartQuest(quest_id, data);
    }

    public String getInfoQuest(int quest_id) {
        return getPlayer().getInfoQuest(quest_id);
    }

    public void updateInfoQuest(int quest_id, String data) {
        getPlayer().updateInfoQuest(quest_id, data);
    }

    public boolean haveItem(int item_id) {
        return haveItem(item_id, 1);
    }

    public boolean haveItem(int item_id, int count) {
        return getPlayer().haveItem(item_id, count, false, true);
    }

    // lua の inventory.itemCount
    public int getItemCount(int item_id) {
        return getPlayer().getItemQuantity(item_id, false);
    }

    public void gainItem(int item_id, int count) {
        gainItem(getPlayer(), item_id, count);
    }

    public void removeAll(int item_id) {
        getPlayer().removeAll(item_id);
    }

    public int getLevel() {
        return getPlayer().getLevel();
    }

    public int getJob() {
        return getPlayer().getJob();
    }

    // 指定したマップにいるキャラクターの数
    public int getPlayerCount(int map_id) {
        return getMapById(map_id).getCharactersSize();
    }

    // 現在のマップにいるキャラクターの数, lua の field.getUserCount
    public int getUserCount() {
        return getMap().getCharactersSize();
    }

    public boolean haveMonster(int mob_id) {
        for (TacosMonster monster : getMap().getAllMonsters()) {
            if (monster.getId() == mob_id) {
                return true;
            }
        }
        return false;
    }

    public int getMonsterCount(int map_id) {
        return getMapById(map_id).getNumMonsters();
    }

    public boolean isPartyMember() {
        return getPlayer().getParty() != null;
    }

    // パーティーリーダーか
    public boolean isLeader() {
        return isPartyBoss();
    }

    // lua の target.isPartyBoss
    public boolean isPartyBoss() {
        MapleParty party = getPlayer().getParty();
        return party != null && party.getLeader().getId() == getPlayer().getId();
    }

    // パーティーメンバーが全員同じマップにいるか
    public boolean allMembersHere() {
        MapleParty party = getPlayer().getParty();
        if (party == null) {
            return false;
        }
        for (MaplePartyCharacter mpc : party.getMembers()) {
            if (getMap().getPlayerById(mpc.getId()) == null) {
                return false;
            }
        }
        return true;
    }

    // lua の target.transferParty (同じマップにいるパーティーメンバーを移動させる)
    public boolean transferParty(int map_id, String portal_name) {
        for (TacosCharacter member : TacosFieldSet.getPartyMembers(getPlayer())) {
            member.changeMap(map_id, portal_name);
        }
        return true;
    }

    public TacosFieldSet getFieldSet(String name) {
        return TacosFieldSet.get(getPlayer(), name);
    }

    public void saveLocation(String type) {
        getPlayer().saveLocation(SavedLocationType.fromString(type));
    }

    // 保存されていない場合は 100000000
    public int getSavedLocation(String type) {
        Integer map_id = getPlayer().getSavedLocation(SavedLocationType.fromString(type));
        if (map_id == null || map_id == -1) {
            return 100000000;
        }
        return map_id;
    }

    public void clearSavedLocation(String type) {
        getPlayer().clearSavedLocation(SavedLocationType.fromString(type));
    }

    // NPC の会話を開始
    public void openNpc(int npc_id) {
        ReqCUser.OnUserSelectNpcInternal(getPlayer(), npc_id, npc_id);
    }

    public void removeNpc(int map_id, int npc_id) {
        getMapById(map_id).removeNpcById(npc_id);
    }

    // キャラクターの位置に召喚
    public void summonMob(int mob_id) {
        summonMob(mob_id, 1, getPlayer().getPosition());
    }

    // ポータルの位置に召喚
    public void summonMobAtPortal(int mob_id, int count) {
        summonMob(mob_id, count, new Point(this.portal.getPosition()));
    }

    public void summonMob(int mob_id, int x, int y) {
        summonMob(mob_id, 1, new Point(x, y));
    }

    public void summonMob(int mob_id, int count, int x, int y) {
        summonMob(mob_id, count, new Point(x, y));
    }

    private void summonMob(int mob_id, int count, Point position) {
        for (int i = 0; i < count; i++) {
            getMap().spawnMonsterOnGroundBelow(WzXML.MOB.createMob(mob_id), position);
        }
    }

    public void resetMap(int map_id) {
        getMapById(map_id).resetFully();
    }

    public void changeMusic(String music) {
        getMap().setChangeBGM(music);
    }

    public void teachSkill(int skill_id, int level, int master_level) {
        getPlayer().changeSkillLevel(WzXML.SKILL.getSkill().get(skill_id), (byte) level, (byte) master_level);
    }

    // チュートリアルの吹き出しなど (UserEffect_AvatarOriented)
    public void avatarOriented(String path) {
        this.chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_AvatarOriented, PB_UserEffect.builder().path(path).build()));
    }

    // UserEffect_ReservedEffect
    public void reservedEffect(String path) {
        this.chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_ReservedEffect, PB_UserEffect.builder().path(path).build()));
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
        TacosPortal target = getMap().getPortalByName(portal_name);
        if (target == null) {
            return false;
        }
        target.setDisabled(!enable);
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
        if (portal_name.isEmpty()) {
            return getPlayer().changeMap(map_id);
        }
        return getPlayer().changeMap(map_id, portal_name);
    }

    // lua の field.transferFieldAll (同じマップにいるキャラクターを全員移動させる)
    public boolean transferFieldAll(int map_id, String portal_name) {
        for (TacosCharacter user : getMap().getAllPlayers()) {
            user.changeMap(map_id, portal_name);
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
        int guild_id = getPlayer().getGuildId();
        if (guild_id <= 0) {
            return;
        }
        this.chr.getWorld().getGuild().gainGP(guild_id, gp);
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
        return getInfoQuest(22013).equals(data);
    }

    public void updateEvanIntroState(String data) {
        updateInfoQuest(22013, data);
    }

    public void evanTutorial(String path, int value) {
        this.chr.SendPacket(ResCScriptMan.getEvanTutorial(path));
    }

    public void movieClipIntroUI(boolean enabled) {
        this.chr.SendPacket(ResCUserLocal.SetStandAloneMode(enabled));
        this.chr.SendPacket(ResCUserLocal.SetDirectionMode(enabled));
    }

    // チュートリアルのガイドの表示
    public void hireTutor(boolean summon) {
        getPlayer().setTutorialSummon(summon);
        this.chr.SendPacket(ResCUserLocal.UserHireTutor(summon));
    }

    public void useItem(int item_id) {
        MapleItemInformationProvider.getInstance().getItemEffect(item_id).applyTo(getPlayer());
        this.chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_GiveBuffMessage, PB_Message.builder().ItemID(item_id).build()));
    }

    // アイテムの効果 (変身など) の解除
    public void cancelItem(int item_id) {
        // TODO : 未実装 (Odin の cancelItem も処理がない)
    }

    public boolean canHold(int item_id) {
        return -1 < getPlayer().getInventory(GameConstants.getInventoryType(item_id)).getNextFreeSlot();
    }

    // チュートリアルのガイド
    public void summonMsg(int type) {
        if (!getPlayer().hasTutorialSummon()) {
            hireTutor(true);
        }
        this.chr.SendPacket(ResCUserLocal.UserTutorMsg(type));
    }

    public void summonMsg(String text) {
        if (!getPlayer().hasTutorialSummon()) {
            hireTutor(true);
        }
        this.chr.SendPacket(ResCUserLocal.UserTutorMsg(text));
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
        reactor.hitReactor(chr);
    }

    // 自分だけにマップエフェクトを表示
    public void showMapEffect(String path) {
        this.chr.SendPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Screen, PB_FieldEffect.builder().wz_path(path).build()));
    }

    // 上部に表示されるクエストメッセージ
    public void showQuestMsg(String text) {
        this.chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_SystemMessage, PB_Message.builder().str(text).build()));
    }

    // アランのチュートリアルの吹き出し音
    public void aranStart() {
        this.chr.SendPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Sound, PB_FieldEffect.builder().wz_path("Aran/balloon").build()));
    }

    // 経験値倍率を適用した経験値
    public void gainExpR(int exp) {
        getPlayer().gainExp(exp * getPlayer().getChannelServer().getExpRate(), true, true, true);
    }

    public void gainAp(int ap) {
        getPlayer().gainAp((short) ap);
    }

    public void gainSp(int sp) {
        getPlayer().gainSP(sp);
    }

    // 指定したキャラクターにアイテムを与える
    public void gainItem(TacosCharacter chr, int item_id, int count) {
        if (count < 0) {
            MapleInventoryManipulator.removeById(chr, GameConstants.getInventoryType(item_id), item_id, -count, true, false);
        } else {
            if (!MapleInventoryManipulator.checkSpace(chr, item_id, count, "")) {
                return;
            }
            if (GameConstants.getInventoryType(item_id) == MapleInventoryType.EQUIP && !GameConstants.isThrowingStar(item_id) && !GameConstants.isBullet(item_id)) {
                Equip equip = MapleItemInformationProvider.getInstance().getDefaultEquipById(item_id);
                equip.setOwner("");
                // 勲章
                String name = equip.getName();
                if (item_id / 10000 == 114 && name != null && !name.isEmpty()) {
                    String text = "You have attained title <" + name + ">";
                    chr.dropMessage(-1, text);
                    chr.Notice(text);
                }
                MapleInventoryManipulator.addbyItem(chr, equip);
            } else {
                MapleInventoryManipulator.addById(chr, item_id, count, "", null, 0);
            }
        }
        chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_Quest, PB_UserEffect.builder().item_id(item_id).item_quantity((short) count).build()));
    }

    // 現在のマップにいるキャラクター
    public List<TacosCharacter> getMapPlayers() {
        return getMap().getAllPlayers();
    }

    public void endPartyQuest(int amount) {
        MapleParty party = getPlayer().getParty();
        if (party == null || party.getMembers().size() == 1) {
            getPlayer().endPartyQuest(amount);
            return;
        }
        for (MaplePartyCharacter mpc : party.getMembers()) {
            TacosCharacter member = getMap().getPlayerById(mpc.getId());
            if (member != null) {
                member.endPartyQuest(amount);
            }
        }
    }

    // 武陵道場の次のマップへ移動
    public boolean dojoNextMap() {
        return Event_DojoAgent.warpNextMap(getPlayer(), false);
    }

    // 武陵道場の上の階へ移動
    public void dojoGetUp() {
        // TODO : 武陵道場の記録 (Odin の処理の値をそのまま利用)
        this.chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_QuestRecordExMessage, PB_Message.builder().QuestID((short) 1207).str("pt=1;min=4;belt=1;tuto=1").build()));
        this.chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_PlayPortalSE));
        this.chr.SendPacket(ResCUserLocal.UserTeleport((byte) 6));
    }
}
