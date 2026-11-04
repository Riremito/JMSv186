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
import java.util.ArrayList;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleInventoryManipulator;
import tacos.client.TacosCharacter;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsScriptMan;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.ResCScriptMan;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.builder.PB_ScriptMessage;
import tacos.packet.response.builder.PB_UserEffect;
import tacos.server.TacosFieldSet;
import tacos.server.map.object.TacosPet;
import tacos.server.map.object.TacosReactor;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public abstract class NpcScriptMan {

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
        chr.DebugMsg("NpcScriptMan : started.");
        this.chr = chr;
        this.chr.setNpcScript(this);
        action(this.status, 0);
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
}
