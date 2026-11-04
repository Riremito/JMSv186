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
package tacos.script.npc;

import java.awt.Point;
import java.awt.Rectangle;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.handling.world.MapleParty;
import odin.server.MapleInventoryManipulator;
import odin.server.Randomizer;
import odin.server.maps.SavedLocationType;
import odin.server.quest.MapleQuest;
import tacos.client.TacosCharacter;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.ops.OpsFieldEffect;
import tacos.packet.ops.OpsScriptMan;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCScriptMan;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_BroadcastMsg;
import tacos.packet.response.builder.PB_FieldEffect;
import tacos.packet.response.builder.PB_ScriptMessage;
import tacos.packet.response.builder.PB_UserEffect;
import tacos.server.TacosFieldSet;
import tacos.server.map.TacosMap;
import tacos.server.map.TacosPortal;
import tacos.server.map.object.TacosMonster;
import tacos.server.map.object.TacosPet;
import tacos.server.map.object.TacosReactor;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public abstract class NpcScriptMan {

    // lua の currentTime の形式
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yy/MM/dd/HH/mm");
    // NPC の変数 (lua の self.getStrReg, self.setStrReg), チャンネル -> NPC ID -> 名前 -> 値
    private static final HashMap<Integer, HashMap<Integer, HashMap<String, String>>> NPC_VARS = new HashMap<>();
    private int npc_script_id;

    public NpcScriptMan() {
        this(TacosConstants.DEFAULT_NPC_ID);
    }

    public NpcScriptMan(int npc_script_id) {
        this.npc_script_id = npc_script_id;
    }

    public abstract boolean action(int status, int answer);

    public int getNpcId() {
        return this.npc_script_id;
    }

    public void setNpcId(int npc_script_id) {
        this.npc_script_id = npc_script_id;
    }

    public static boolean OnScriptMessageAnswer(TacosCharacter chr, OpsScriptMan ops, int nMsgType, int action, int m_nSelect, String m_sInputStr_Result, long liCashItemSN) {
        NpcScriptMan npc_script = chr.getNpcScript();

        switch (ops) {
            case SM_ASKTEXT:
            case SM_ASKNUMBER:
            case SM_ASKPET: {
                // 1 = ok, 0 = cancel, 2 = no pet (askPet only)
                if (action == 1 || (ops == OpsScriptMan.SM_ASKPET && action == 2)) {
                    npc_script.status++;
                    npc_script.answer_text = (m_sInputStr_Result != null) ? m_sInputStr_Result : "";
                    npc_script.answer_pet_sn = liCashItemSN;
                    if (npc_script.action(npc_script.getStatus(), m_nSelect)) {
                        // continue
                        return true;
                    }
                }
                // end
                npc_script.end();
                return true;
            }
            default: {
                break;
            }
        }

        npc_script.updateStatus(action);

        switch (ops) {
            case SM_SAY:
            case SM_ASKYESNO:
            case SM_ASKMENU:
            case SM_ASKAVATAR:
            case SM_ASKACCEPT:
            case SM_ASKSLIDEMENU: {
                if (action != -1) {
                    if (npc_script.action(npc_script.getStatus(), m_nSelect)) {
                        // continue
                        return true;
                    }
                }
                // end
                npc_script.end();
                return true;
            }
            default: {
                break;
            }
        }

        npc_script.end();
        DebugLogger.ErrorLog("NpcScriptMan : not coded, nMsgType = " + nMsgType);
        return false;
    }

    private int status = 0;
    private String answer_text = "";
    private long answer_pet_sn = 0;
    private final ArrayList<String> pages = new ArrayList<>();

    private int getStatus() {
        return this.status;
    }

    private boolean updateStatus(int val) {
        switch (val) {
            case 1 -> // next
            {
                this.status++;
                return true;
            }
            case 0 -> // prev
            {
                this.status--;
                return true;
            }
            case -1 -> // cancel
            {
                this.status = -1;
                return true;
            }
            default -> {
            }
        }
        return false;
    }

    private TacosCharacter chr;

    protected TacosCharacter getPlayer() {
        return this.chr;
    }

    public boolean start(TacosCharacter chr) {
        this.status = 0;
        this.pages.clear();
        chr.DebugMsg("NpcScriptMan : started.");
        this.chr = chr;
        this.chr.setNpcScript(this);
        // 初回の会話で終了する場合 (return false) はここで終了する
        // 終了しないと会話を閉じた時にクライアントが送信する応答で action が再度呼び出され、同じメッセージが 2 度表示される
        if (!action(this.status, 0)) {
            end();
        }
        return true;
    }

    public boolean end() {
        this.status = -1;
        this.chr.DebugMsg("NpcScriptMan : finished.");
        this.chr.setNpcScript(null);
        return true;
    }

    protected void say(String text) {
        NpcScriptTag nt = new NpcScriptTag();
        nt.add(text);
        say(nt);
    }

    protected void say(String text, boolean prev, boolean next) {
        NpcScriptTag nt = new NpcScriptTag();
        nt.add(text);
        say(nt, prev, next);
    }

    protected void askYesNo(String text) {
        NpcScriptTag nt = new NpcScriptTag();
        nt.add(text);
        askYesNo(nt);
    }

    protected void say(NpcScriptTag nt) {
        say(nt, false, false);
    }

    protected void say(NpcScriptTag nt, boolean prev, boolean next) {
        if (nt.get().contains("#L")) {
            DebugLogger.ErrorLog("NpcScriptMan : say.");
            askMenu(nt, prev, next);
            return;
        }
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(OpsScriptMan.SM_SAY, PB_ScriptMessage.builder().npc_id(getNpcId()).text(nt.get()).prev(prev).next(next).build()));
    }

    // プレイヤーの台詞
    protected void sayUser(String text, boolean prev, boolean next) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(OpsScriptMan.SM_SAY, PB_ScriptMessage.builder().npc_id(getNpcId()).param((byte) 2).text(text).prev(prev).next(next).build()));
    }

    // チュートリアルのガイドの表示
    protected void hireTutor(boolean summon) {
        this.chr.setTutorialSummon(summon);
        this.chr.SendPacket(ResCUserLocal.UserHireTutor(summon));
    }

    // チュートリアルのガイドのメッセージ
    protected void tutorMsg(int index) {
        if (!this.chr.hasTutorialSummon()) {
            hireTutor(true);
        }
        this.chr.SendPacket(ResCUserLocal.UserTutorMsg(index));
    }

    protected void showReservedEffect(String path) {
        this.chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_ReservedEffect, PB_UserEffect.builder().path(path).build()));
    }

    protected void askYesNo(NpcScriptTag nt) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(OpsScriptMan.SM_ASKYESNO, PB_ScriptMessage.builder().npc_id(getNpcId()).text(nt.get()).build()));
    }

    protected void askMenu(NpcScriptTag nt) {
        askMenu(nt, false, false);
    }

    protected void askMenu(NpcScriptTag nt, boolean prev, boolean next) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(OpsScriptMan.SM_ASKMENU, PB_ScriptMessage.builder().npc_id(getNpcId()).text(nt.get()).prev(prev).next(next).build()));
    }

    protected void askAvatar(NpcScriptTag nt, ArrayList<Integer> ids) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(OpsScriptMan.SM_ASKAVATAR, PB_ScriptMessage.builder().npc_id(getNpcId()).text(nt.get()).ids(ids).build()));
    }

    protected void askAccept(NpcScriptTag nt) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(OpsScriptMan.SM_ASKACCEPT, PB_ScriptMessage.builder().npc_id(getNpcId()).text(nt.get()).build()));
    }

    protected void askSlideMenu(NpcScriptTag nt) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(OpsScriptMan.SM_ASKSLIDEMENU, PB_ScriptMessage.builder().npc_id(getNpcId()).text(nt.get()).build()));
    }

    // 入力された文字列は getAnswerText で取得する
    protected void askText(String text, String text_default, int length_min, int length_max) {
        NpcScriptTag nt = new NpcScriptTag();
        nt.add(text);
        askText(nt, text_default, length_min, length_max);
    }

    protected void askText(NpcScriptTag nt, String text_default, int length_min, int length_max) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(OpsScriptMan.SM_ASKTEXT, PB_ScriptMessage.builder().npc_id(getNpcId()).text(nt.get()).text_default(text_default).length_min(length_min).length_max(length_max).build()));
    }

    // 入力された数値は action の answer で取得する
    protected void askNumber(String text, int number_default, int number_min, int number_max) {
        NpcScriptTag nt = new NpcScriptTag();
        nt.add(text);
        askNumber(nt, number_default, number_min, number_max);
    }

    protected void askNumber(NpcScriptTag nt, int number_default, int number_min, int number_max) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(OpsScriptMan.SM_ASKNUMBER, PB_ScriptMessage.builder().npc_id(getNpcId()).text(nt.get()).number_default(number_default).number_min(number_min).number_max(number_max).build()));
    }

    // 選択されたペットは getAnswerPet で取得する (ペットがいない場合や未選択の場合は null)
    // text が空の場合はクライアントが先頭のペットを自動で選択する
    protected void askPet(String text, ArrayList<TacosPet> pets) {
        NpcScriptTag nt = new NpcScriptTag();
        nt.add(text);
        askPet(nt, pets);
    }

    protected void askPet(NpcScriptTag nt, ArrayList<TacosPet> pets) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(OpsScriptMan.SM_ASKPET, PB_ScriptMessage.builder().npc_id(getNpcId()).text(nt.get()).pets(pets).build()));
    }

    protected String getAnswerText() {
        return this.answer_text;
    }

    protected TacosPet getAnswerPet() {
        if (this.answer_pet_sn == 0) {
            return null;
        }
        for (Item item : this.chr.getInventory(MapleInventoryType.CASH).list()) {
            if (item.getPet() != null && item.getPet().getUniqueId() == this.answer_pet_sn) {
                return item.getPet();
            }
        }
        return null;
    }

    // 所持しているすべてのペット
    protected ArrayList<TacosPet> getPetsInInventory() {
        ArrayList<TacosPet> pets = new ArrayList<>();
        for (Item item : this.chr.getInventory(MapleInventoryType.CASH).list()) {
            if (item.getPet() != null) {
                pets.add(item.getPet());
            }
        }
        return pets;
    }

    protected String getMapName(int map_id) {
        return "#m" + map_id + "#";
    }

    protected String getNpcName() {
        return getNpcName(getNpcId());
    }

    protected String getNpcName(int npc_id) {
        return "#p" + npc_id + "#";
    }

    protected boolean payMoney(int cost) {
        return this.chr.gainMeso(-cost, true, false, true);
    }

    protected int getMapId() {
        return this.chr.getMap().getId();
    }

    protected boolean checkItem(int item_id) {
        return this.chr.haveItem(item_id);
    }

    protected boolean checkItem(int item_id, int count) {
        return this.chr.haveItem(item_id, count, false, true);
    }

    // lua の inventory.exchange 相当
    // meso : 所持金の増減, items : アイテムIDと増減数の組 (負の値は消費, 正の値は入手)
    // 所持金・アイテムの不足またはインベントリに空きがない場合は何もせずに false を返す
    protected boolean exchange(int meso, int... items) {
        if (items.length % 2 != 0) {
            DebugLogger.ErrorLog("NpcScriptMan : exchange invalid items.");
            return false;
        }
        if (meso < 0 && this.chr.getMeso() < -meso) {
            return false;
        }
        for (int i = 0; i < items.length; i += 2) {
            int item_id = items[i];
            int count = items[i + 1];
            if (count < 0 && !checkItem(item_id, -count)) {
                return false;
            }
            if (count > 0 && !MapleInventoryManipulator.checkSpace(this.chr, item_id, count, "")) {
                return false;
            }
        }
        if (meso != 0) {
            this.chr.gainMeso(meso, true, false, true);
        }
        for (int i = 0; i < items.length; i += 2) {
            int item_id = items[i];
            int count = items[i + 1];
            if (count < 0) {
                MapleInventoryManipulator.removeById(this.chr, GameConstants.getInventoryType(item_id), item_id, -count, true, false);
            }
        }
        for (int i = 0; i < items.length; i += 2) {
            int item_id = items[i];
            int count = items[i + 1];
            if (count > 0) {
                MapleInventoryManipulator.addById(this.chr, item_id, count);
            }
        }
        return true;
    }

    protected boolean removeNpc() {
        return this.chr.getMap().removeNpcById(getNpcId());
    }

    protected boolean forceStartReactor(int reactor_id) {
        TacosReactor reactor = this.chr.getMap().getReactorById(reactor_id);
        if (reactor == null) {
            return false;
        }
        reactor.forceStartReactor(this.chr.getClient());
        return true;
    }

    protected void spawnBossMonster(int id, int x, int y, int type) {
        this.chr.getMap().spawnMonsterOnGroundBelow(WzXML.MOB.createMob(id), new Point(x, y), type);
    }

    protected TacosFieldSet getFieldSet(String name) {
        return TacosFieldSet.get(this.chr, name);
    }

    // FieldSet に 1 人で入場する
    protected int enterFieldSet(String name, int index) {
        TacosFieldSet field_set = getFieldSet(name);
        if (field_set == null) {
            return TacosFieldSet.ENTER_ERROR;
        }
        return field_set.enter(this.chr, index);
    }

    // FieldSet にパーティーで入場する
    protected int enterFieldSetParty(String name, int index) {
        TacosFieldSet field_set = getFieldSet(name);
        if (field_set == null) {
            return TacosFieldSet.ENTER_ERROR;
        }
        return field_set.enterParty(this.chr, index);
    }

    protected boolean warp(int map_id) {
        return this.chr.changeMapById(map_id);
    }

    protected boolean warp(int map_id, String portal_name) {
        return this.chr.changeMapById(map_id, portal_name);
    }

    // lua の target.isPartyBoss
    protected boolean isPartyBoss() {
        MapleParty party = this.chr.getParty();
        return party != null && party.getLeader().getId() == this.chr.getId();
    }

    // lua の target.transferParty (同じマップにいるパーティーメンバーを移動させる)
    protected void transferParty(int map_id, String portal_name) {
        for (TacosCharacter member : TacosFieldSet.getPartyMembers(this.chr)) {
            member.changeMapById(map_id, portal_name);
        }
    }

    // lua の inventory.itemCount
    protected int getItemCount(int item_id) {
        return this.chr.getItemQuantity(item_id, false);
    }

    // 所持しているアイテムをすべて消費する
    protected boolean removeItemAll(int item_id) {
        int count = getItemCount(item_id);
        if (count <= 0) {
            return true;
        }
        return exchange(0, item_id, -count);
    }

    // lua の inventory.slotCount( type ) > inventory.holdCount( type )
    protected boolean hasEmptySlot(MapleInventoryType type) {
        return !this.chr.getInventory(type).isFull();
    }

    // lua の random( min, max ), min 以上 max 以下
    protected int random(int min, int max) {
        return Randomizer.rand(min, max);
    }

    // lua の shuffle( 1, text ), 文字の並びをランダムに入れ替える
    protected String shuffle(String text) {
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

    // lua の qr.get
    protected String getQuestData(int quest_id) {
        String data = this.chr.getQuest(MapleQuest.getInstance(quest_id)).getCustomData();
        return data == null ? "" : data;
    }

    // lua の qr.set
    protected void setQuestData(int quest_id, String data) {
        MapleQuest.getInstance(quest_id).forceStart(this.chr, getNpcId(), data);
    }

    // lua の qr.remove
    protected void removeQuestData(int quest_id) {
        MapleQuest.getInstance(quest_id).forfeit(this.chr);
    }

    // lua の currentTime
    protected String currentTime() {
        return LocalDateTime.now().format(TIME_FORMAT);
    }

    // lua の compareTime( time1, time2 ), time1 - time2 (分), 不正な値の場合は time2 を十分に古い時刻として扱う
    protected long compareTime(String time1, String time2) {
        try {
            return ChronoUnit.MINUTES.between(LocalDateTime.parse(time2, TIME_FORMAT), LocalDateTime.parse(time1, TIME_FORMAT));
        } catch (DateTimeParseException e) {
            return Long.MAX_VALUE;
        }
    }

    // lua の field.effectScreen
    protected void effectScreen(String path) {
        this.chr.getMap().broadcastPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Screen, PB_FieldEffect.builder().wz_path(path).build()));
    }

    // lua の field.effectSound
    protected void effectSound(String path) {
        this.chr.getMap().broadcastPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Sound, PB_FieldEffect.builder().wz_path(path).build()));
    }

    // lua の field.effectObject
    protected void effectObject(String path) {
        this.chr.getMap().broadcastPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Object, PB_FieldEffect.builder().wz_path(path).build()));
    }

    // lua の field.enablePortal
    protected boolean enablePortal(String portal_name, boolean enable) {
        TacosPortal portal = this.chr.getMap().getPortal(portal_name);
        if (portal == null) {
            return false;
        }
        portal.setPortalState(enable);
        return true;
    }

    // lua の field.notice
    protected void fieldNotice(OpsBroadcastMsg type, String text) {
        this.chr.getMap().broadcastPacket(ResCWvsContext.BroadcastMsg(type, PB_BroadcastMsg.builder().message(text).build()));
    }

    // lua の target.message
    protected void message(String text) {
        this.chr.dropMessage(OpsBroadcastMsg.BM_EVENT.get(), text);
    }

    // lua の self.sendMessage, 同じチャンネルにいるキャラクターにメッセージを送る
    protected boolean sendMessage(String character_name, String text) {
        TacosCharacter target = this.chr.getChannelServer().getOnlinePlayers().findByName(character_name);
        if (target == null) {
            return false;
        }
        // TODO : lua の sendMessage の表示方法は不明
        target.dropMessage(OpsBroadcastMsg.BM_EVENT.get(), text);
        return true;
    }

    // lua の field.getUserCount
    protected int getUserCount() {
        return this.chr.getMap().getCharactersSize();
    }

    // lua の field.transferFieldAll (同じマップにいるユーザーを全員移動させる)
    protected void transferFieldAll(int map_id, String portal_name) {
        for (TacosCharacter user : this.chr.getMap().getAllPlayers()) {
            user.changeMapById(map_id, portal_name);
        }
    }

    // lua の Field( map_id ).getUserCount
    protected int getUserCount(int map_id) {
        TacosMap map = this.chr.getChannelServer().findMap(map_id);
        if (map == null) {
            return 0;
        }
        return map.getCharactersSize();
    }

    // lua の field.countUserInArea
    protected int countUserInArea(int index) {
        List<Rectangle> areas = this.chr.getMap().getNodeInfo().getAreas();
        if (index < 0 || areas.size() <= index) {
            return 0;
        }
        int count = 0;
        for (TacosCharacter user : this.chr.getMap().getAllPlayers()) {
            if (areas.get(index).contains(user.getPosition())) {
                count++;
            }
        }
        return count;
    }

    // lua の Field( map_id ).getMobCount( mob_id )
    protected int getMobCount(int map_id, int mob_id) {
        TacosMap map = this.chr.getChannelServer().findMap(map_id);
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

    // lua の field.setMobGen
    // TODO : 特定のモンスターの出現の切り替えは未実装
    protected void setMobGen(int map_id, boolean enable, int mob_id) {
        DebugLogger.ErrorLog("NpcScriptMan : setMobGen is not implemented. map = " + map_id + ", enable = " + enable + ", mob = " + mob_id);
    }

    // lua の field.removeAllMob
    protected void removeAllMob(int map_id) {
        TacosMap map = this.chr.getChannelServer().findMap(map_id);
        if (map == null) {
            return;
        }
        map.killAllMonsters(false);
    }

    // lua の target.isWear
    protected boolean isWear(int item_id) {
        return this.chr.getInventory(MapleInventoryType.EQUIPPED).findById(item_id) != null;
    }

    // lua の field.summonMob
    protected void summonMob(int x, int y, int mob_id) {
        this.chr.getMap().spawnMonsterOnGroundBelow(WzXML.MOB.createMob(mob_id), new Point(x, y));
    }

    // lua の self.getStrReg
    protected String getNpcVar(String key) {
        return getNpcVar(getNpcId(), key);
    }

    // lua の self.setStrReg
    protected void setNpcVar(String key, String value) {
        setNpcVar(getNpcId(), key, value);
    }

    // lua の field.getNpcStrVar
    protected String getNpcVar(int npc_id, String key) {
        synchronized (NPC_VARS) {
            HashMap<Integer, HashMap<String, String>> channel_vars = NPC_VARS.get(this.chr.getChannelId());
            if (channel_vars == null || channel_vars.get(npc_id) == null) {
                return "";
            }
            return channel_vars.get(npc_id).getOrDefault(key, "");
        }
    }

    // lua の field.setNpcVar
    protected void setNpcVar(int npc_id, String key, String value) {
        synchronized (NPC_VARS) {
            NPC_VARS.computeIfAbsent(this.chr.getChannelId(), k -> new HashMap<>()).computeIfAbsent(npc_id, k -> new HashMap<>()).put(key, value);
        }
    }

    // キャラクターの名前
    protected String getCharacterName() {
        return this.chr.getName();
    }

    protected int getLevel() {
        return this.chr.getLevel();
    }

    protected int getJob() {
        return this.chr.getJob();
    }

    protected void changeJob(int job_id) {
        this.chr.changeJob(job_id);
    }

    protected int getMeso() {
        return this.chr.getMeso();
    }

    protected int getRemainingSp() {
        return this.chr.getRemainingSp();
    }

    protected void gainAp(int ap) {
        this.chr.gainAp((short) ap);
    }

    // lua の target.incEXP
    protected void gainExp(int exp) {
        this.chr.gainExp(exp, true, true, true);
    }

    protected int getGuildId() {
        return this.chr.getGuildId();
    }

    protected int getGuildRank() {
        return this.chr.getGuildRank();
    }

    // 0 = 未開始, 1 = 進行中, 2 = 完了
    protected int getQuestStatus(int quest_id) {
        return this.chr.getQuestStatus(quest_id);
    }

    // 装備中のアイテムも含めて確認する (checkItem は装備中のアイテムを含まない)
    protected boolean haveItem(int item_id, int count) {
        return this.chr.haveItem(item_id, count);
    }

    // インベントリに空きがあるか
    protected boolean canHold(int item_id, int count) {
        return MapleInventoryManipulator.checkSpace(this.chr, item_id, count, "");
    }

    // アイテムを入手 (インベントリの空きは確認しない)
    protected boolean gainItem(int item_id, int count) {
        return MapleInventoryManipulator.addById(this.chr, item_id, count);
    }

    // アイテムを消費 (所持数は確認しない)
    protected boolean removeItem(int item_id, int count) {
        return MapleInventoryManipulator.removeById(this.chr, GameConstants.getInventoryType(item_id), item_id, count, true, false);
    }

    // 装備中のアイテムを削除
    protected boolean removeEquippedItem(int item_id, int count) {
        return MapleInventoryManipulator.removeById(this.chr, MapleInventoryType.EQUIPPED, item_id, count, true, false);
    }

    // type : インベントリの種類 (1 = 装備, 2 = 消費, 3 = 設置, 4 = ETC, 5 = キャッシュ)
    protected void expandInventory(int type, int slots) {
        this.chr.expandInventory((byte) type, slots);
    }

    protected void saveLocation(SavedLocationType type) {
        this.chr.saveLocation(type);
    }

    protected TacosMap findMap(int map_id) {
        return this.chr.findMap(map_id);
    }

    protected void setStandAloneMode(boolean enable) {
        this.chr.SendPacket(ResCUserLocal.SetStandAloneMode(enable));
    }

    protected void setDirectionMode(boolean enable) {
        this.chr.SendPacket(ResCUserLocal.SetDirectionMode(enable));
    }

    // lua の self.say が連続する会話, 最後のページ以外は次へボタンを表示する
    // 続きがある場合は true を返すので action の戻り値として利用する, 次のページは action の先頭で nextPage を呼び出して表示する
    protected boolean sayPages(String... texts) {
        this.pages.clear();
        Collections.addAll(this.pages, texts);
        return nextPage();
    }

    // 表示していないページがあるか
    protected boolean hasPages() {
        return !this.pages.isEmpty();
    }

    // 次のページを表示する, 続きがある場合は true
    protected boolean nextPage() {
        if (this.pages.isEmpty()) {
            return false;
        }
        String text = this.pages.remove(0);
        boolean next = !this.pages.isEmpty();
        say(text, false, next);
        return next;
    }

    // lua の integer, 数値でない場合は 0
    protected int integer(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ユーザーエフェクト (AvatarOriented) を自分だけに表示
    protected void avatarOriented(String path) {
        this.chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_AvatarOriented, PB_UserEffect.builder().path(path).build()));
    }
}
