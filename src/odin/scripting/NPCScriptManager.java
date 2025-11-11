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
package odin.scripting;

import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;

import odin.client.MapleClient;
import tacos.config.property.Property_Java;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import odin.server.quest.MapleQuest;
import odin.tools.FileoutputUtil;

public class NPCScriptManager extends AbstractScriptManager {

    private final Map<MapleClient, NPCConversationManager> cms = new WeakHashMap<MapleClient, NPCConversationManager>();
    private static final NPCScriptManager instance = new NPCScriptManager();

    public static final NPCScriptManager getInstance() {
        return instance;
    }

    public final void start(final MapleClient c, final int npc) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c)) {
                Invocable iv = getInvocable("npc/" + npc + ".js", c, true);
                if (iv == null) {
                    c.getPlayer().DebugMsg("NPC Script = " + npc + ", MapID = " + c.getPlayer().getMapId());
                    iv = getInvocable("npc/notcoded.js", c, true); //safe disposal
                    if (iv == null) {
                        dispose(c);
                        return;
                    }
                } else {
                    c.getPlayer().DebugMsg("NPC Script = " + npc + ", MapID = " + c.getPlayer().getMapId());
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, -1, (byte) -1, iv);
                cms.put(c, cm);
                scriptengine.put("cm", cm);

                c.getPlayer().setConversation(1);
                //System.out.println("NPCID started: " + npc);
                try {
                    iv.invokeFunction("start"); // Temporary until I've removed all of start
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            } else {
                c.getPlayer().dropMessage(-1, "You already are talking to an NPC. Use @ea if this is not intended.");
            }

        } catch (final Exception e) {
            System.err.println("Error executing NPC script, NPC ID : " + npc + "." + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing NPC script, NPC ID : " + npc + "." + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    // 下位バージョンへの互換性を残すために、NPCのアイコンだけ別表示で存在しないNPCIDのスクリプトを呼び出せるようにしておく
    public final void start(final MapleClient c, final int npc_icon, final int script_name) {
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c)) {
                Invocable iv = getInvocable("npc/" + script_name + ".js", c, true);
                if (iv == null) {
                    c.getPlayer().DebugMsg("NPC Script = " + script_name + ", MapID = " + c.getPlayer().getMapId());
                    iv = getInvocable("npc/notcoded.js", c, true); //safe disposal
                    if (iv == null) {
                        dispose(c);
                        return;
                    }
                } else {
                    c.getPlayer().DebugMsg("NPC Script = " + script_name + ", MapID = " + c.getPlayer().getMapId());
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc_icon, -1, (byte) -1, iv, script_name);
                cms.put(c, cm);
                scriptengine.put("cm", cm);

                c.getPlayer().setConversation(1);
                //System.out.println("NPCID started: " + npc);
                try {
                    iv.invokeFunction("start"); // Temporary until I've removed all of start
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            } else {
                c.getPlayer().dropMessage(-1, "You already are talking to an NPC. Use @ea if this is not intended.");
            }

        } catch (final Exception e) {
            System.err.println("Error executing NPC script, NPC ID : " + script_name + "." + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing NPC script, NPC ID : " + script_name + "." + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void action(final MapleClient c, final byte mode, final byte type, final int selection) {
        if (mode != -1) {
            final NPCConversationManager cm = cms.get(c);
            if (cm == null || cm.getLastMsg() > -1) {
                return;
            }
            final Lock lock = c.getNPCLock();
            lock.lock();
            try {

                if (cm.pendingDisposal) {
                    dispose(c);
                } else {
                    cm.getIv().invokeFunction("action", mode, type, selection);
                }
            } catch (final Exception e) {
                System.err.println("Error executing NPC script. NPC ID : " + cm.getScript() + ":" + e);
                dispose(c);
                FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing NPC script, NPC ID : " + cm.getScript() + "." + e);
            } finally {
                lock.unlock();
            }
        }
    }

    public final void startQuest(final MapleClient c, final int npc, final int quest) {
        if (!MapleQuest.getInstance(quest).canStart(c.getPlayer(), null)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c)) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte) 0, iv);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                //System.out.println("NPCID started: " + npc + " startquest " + quest);
                iv.invokeFunction("start", (byte) 1, (byte) 0, 0); // start it off as something
            } else {
                c.getPlayer().dropMessage(-1, "You already are talking to an NPC. Use @ea if this is not intended.");
            }
        } catch (final Exception e) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void startQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                cm.getIv().invokeFunction("start", mode, type, selection);
            }
        } catch (Exception e) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getScript() + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getScript() + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final int npc, final int quest, final boolean customEnd) {
        if (!customEnd && !MapleQuest.getInstance(quest).canComplete(c.getPlayer(), null)) {
            return;
        }
        final Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if (!cms.containsKey(c)) {
                final Invocable iv = getInvocable("quest/" + quest + ".js", c, true);
                if (iv == null) {
                    dispose(c);
                    return;
                }
                final ScriptEngine scriptengine = (ScriptEngine) iv;
                final NPCConversationManager cm = new NPCConversationManager(c, npc, quest, (byte) 1, iv);
                cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                //System.out.println("NPCID started: " + npc + " endquest " + quest);
                iv.invokeFunction("end", (byte) 1, (byte) 0, 0); // start it off as something
            } else {
                c.getPlayer().dropMessage(-1, "You already are talking to an NPC. Use @ea if this is not intended.");
            }
        } catch (Exception e) {
            System.err.println("Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + quest + ")..NPCID: " + npc + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void endQuest(final MapleClient c, final byte mode, final byte type, final int selection) {
        final Lock lock = c.getNPCLock();
        final NPCConversationManager cm = cms.get(c);
        if (cm == null || cm.getLastMsg() > -1) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                cm.getIv().invokeFunction("end", mode, type, selection);
            }
        } catch (Exception e) {
            System.err.println("Error executing Quest script. (" + cm.getQuest() + ")...NPC: " + cm.getScript() + ":" + e);
            FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Error executing Quest script. (" + cm.getQuest() + ")..NPCID: " + cm.getScript() + ":" + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public final void dispose(final MapleClient c) {
        final NPCConversationManager npccm = cms.get(c);
        if (npccm != null) {
            cms.remove(c);
            if (npccm.getType() == -1) {
                c.removeScriptEngine(Property_Java.getDir_Scripts() + "npc/" + npccm.getScript() + ".js");
                c.removeScriptEngine(Property_Java.getDir_Scripts() + "npc/notcoded.js");
            } else {
                c.removeScriptEngine(Property_Java.getDir_Scripts() + "quest/" + npccm.getQuest() + ".js");
            }
        }
        if (c.getPlayer() != null && c.getPlayer().getConversation() == 1) {
            c.getPlayer().setConversation(0);
        }
    }

    public final NPCConversationManager getCM(final MapleClient c) {
        return cms.get(c);
    }
}
