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
import tacos.client.TacosCharacter;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.packet.ops.OpsScriptMan;
import tacos.packet.response.ResCScriptMan;
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

    public static boolean OnScriptMessageAnswer(TacosCharacter chr, OpsScriptMan ops, int nMsgType, int action, int m_nSelect) {
        NpcScriptMan npc_script = chr.getNpcScript();
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
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(getNpcId(), OpsScriptMan.SM_SAY, (byte) 0, nt.get(), prev, next));
    }

    protected void askYesNo(NpcScriptTag nt) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(getNpcId(), OpsScriptMan.SM_ASKYESNO, (byte) 0, nt.get(), false, false));
    }

    protected void askMenu(NpcScriptTag nt) {
        askMenu(nt, false, false);
    }

    protected void askMenu(NpcScriptTag nt, boolean prev, boolean next) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(getNpcId(), OpsScriptMan.SM_ASKMENU, (byte) 0, nt.get(), prev, next));
    }

    protected void askAvatar(NpcScriptTag nt, ArrayList<Integer> ids) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(getNpcId(), OpsScriptMan.SM_ASKAVATAR, (byte) 0, nt.get(), false, false, ids));
    }

    protected void askAccept(NpcScriptTag nt) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(getNpcId(), OpsScriptMan.SM_ASKACCEPT, (byte) 0, nt.get(), false, false));
    }

    protected void askSlideMenu(NpcScriptTag nt) {
        this.chr.SendPacket(ResCScriptMan.ScriptMessage(getNpcId(), OpsScriptMan.SM_ASKSLIDEMENU, (byte) 0, nt.get(), false, false));
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

    protected boolean warp(int map_id) {
        return this.chr.changeMapById(map_id);
    }

    protected boolean warp(int map_id, String portal_name) {
        return this.chr.changeMapById(map_id, portal_name);
    }
}
