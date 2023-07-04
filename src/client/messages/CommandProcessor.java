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
package client.messages;

import java.util.ArrayList;
import client.MapleCharacter;
import client.MapleClient;
import client.messages.commands.*;
import client.messages.commands.AdminCommand;
import client.messages.commands.GMCommand;
import client.messages.commands.InternCommand;
import client.messages.commands.PlayerCommand;
import constants.ServerConstants.CommandType;
import constants.ServerConstants.PlayerGMRank;
import database.DatabaseConnection;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import scripting.NPCScriptManager;
import server.life.MapleLifeFactory;
import server.life.MapleNPC;
import server.maps.MapleMap;
import server.maps.SavedLocationType;
import tools.FileoutputUtil;
import wz.LoadData;

public class CommandProcessor {

    private final static HashMap<String, CommandObject> commands = new HashMap<String, CommandObject>();
    private final static HashMap<Integer, ArrayList<String>> commandList = new HashMap<Integer, ArrayList<String>>();

    static {

        Class<?>[] CommandFiles = {
            PlayerCommand.class, InternCommand.class, GMCommand.class, AdminCommand.class
        };

        for (Class<?> clasz : CommandFiles) {
            try {
                PlayerGMRank rankNeeded = (PlayerGMRank) clasz.getMethod("getPlayerLevelRequired", new Class<?>[]{}).invoke(null, (Object[]) null);
                Class<?>[] a = clasz.getDeclaredClasses();
                ArrayList<String> cL = new ArrayList<String>();
                for (Class<?> c : a) {
                    try {
                        if (!Modifier.isAbstract(c.getModifiers()) && !c.isSynthetic()) {
                            Object o = c.newInstance();
                            boolean enabled;
                            try {
                                enabled = c.getDeclaredField("enabled").getBoolean(c.getDeclaredField("enabled"));
                            } catch (NoSuchFieldException ex) {
                                enabled = true; //Enable all coded commands by default.
                            }
                            if (o instanceof CommandExecute && enabled) {
                                cL.add(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase());
                                commands.put(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase(), new CommandObject(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase(), (CommandExecute) o, rankNeeded.getLevel()));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
                    }
                }
                Collections.sort(cL);
                commandList.put(rankNeeded.getLevel(), cL);
            } catch (Exception ex) {
                ex.printStackTrace();
                FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
            }
        }
    }

    private static void sendDisplayMessage(MapleClient c, String msg, CommandType type) {
        if (c.getPlayer() == null) {
            return;
        }
        switch (type) {
            case NORMAL:
                c.getPlayer().dropMessage(6, msg);
                break;
            case TRADE:
                c.getPlayer().dropMessage(-2, "Error : " + msg);
                break;
        }

    }

    private static boolean warp(MapleClient c, int mapid) {
        try {
            if (!(0 < mapid && mapid < 999999999)) {
                c.getPlayer().Notice("存在しないMapIDです");
                return false;
            }
            MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
            if (map == null) {
                c.getPlayer().Notice("存在しないMapIDです");
                return false;
            }

            c.getPlayer().changeMap(map, map.getPortal(0));
            return true;
        } catch (Exception e) {
        }
        c.getPlayer().Notice("例外発生");
        return false;
    }

    private static boolean NPCTalk(MapleClient c, int npcid) {
        MapleNPC npc = MapleLifeFactory.getNPC(npcid);

        if (npc == null || npc.getName().equals("MISSINGNO")) {
            return false;
        }

        NPCScriptManager.getInstance().start(c, npcid);
        return true;
    }

    // テスト用
    private static boolean CustomNPCTalk(MapleClient c, final int npcid, final int npc_script) {
        MapleNPC npc = MapleLifeFactory.getNPC(npcid);

        if (npc == null || npc.getName().equals("MISSINGNO")) {
            return false;
        }

        NPCScriptManager.getInstance().start(c, npcid, npc_script);
        return true;
    }

    public static boolean processCommand(MapleClient c, String line, CommandType type) {
        // 通常クライアントのコマンドの処理
        if (line.charAt(0) == '!' || line.charAt(0) == '@') {
            line = '/' + line.substring(1);
        }
        if (line.charAt(0) == PlayerGMRank.MAPLE.getCommandPrefix()) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase();

            if ("/ea".equals(splitted[0]) || "/stuck".equals(splitted[0]) || "/unlock".equals(splitted[0])) {
                c.getPlayer().UpdateStat(true);
                return true;
            }

            if ("/heal".equals(splitted[0])) {
                MapleCharacter chr = c.getPlayer();
                chr.getStat().setHp(chr.getStat().getMaxHp());
                chr.getStat().setMp(chr.getStat().getMaxMp());
                chr.UpdateStat(true);
                return true;
            }

            // デバッグモード
            if ("/debug".equals(splitted[0])) {
                c.getPlayer().SetDebugger();
                if (c.getPlayer().GetDebugger()) {
                    c.getPlayer().Notice("デバッグモード有効");
                } else {
                    c.getPlayer().Notice("デバッグモード無効");
                }
                return true;
            }

            // スクリプト情報
            if ("/info".equals(splitted[0])) {
                c.getPlayer().SetInformation();
                if (c.getPlayer().GetInformation()) {
                    c.getPlayer().Notice("スクリプト情報有効");
                } else {
                    c.getPlayer().Notice("スクリプト情報無効");
                }
                return true;
            }
            // ヘルプ
            if ("/help".equals(splitted[0])) {
                c.getPlayer().Notice("/save, /map2, /fm, /henesys, /leafre, /magatia");
                return true;
            }

            // セーブ
            if ("/save".equals(splitted[0]) || "/セーブ".equals(splitted[0])) {
                c.getPlayer().saveToDB(false, false);
                c.getPlayer().Notice("現在のキャラクターの状態がDBへ反映されました");
                return true;
            }
            // map2
            if ("/map2".equals(splitted[0])) {
                try {
                    int mapid = Integer.parseInt(splitted[1]);
                    if (0 < mapid && mapid < 999999999) {
                        warp(c, mapid);
                    }
                } catch (Exception e) {
                }
                return true;
            }

            // FM
            if ("/fm".equals(splitted[0]) || "/フリマ".equals(splitted[0])) {
                c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET, c.getPlayer().getMap().getReturnMap().getId());
                warp(c, 910000000);
                return true;
            }
            // ヘネシス
            if ("/henesys".equals(splitted[0]) || "/ヘネシス".equals(splitted[0])) {
                warp(c, 100000000);
                return true;
            }
            // リプレ
            if ("/leafre".equals(splitted[0]) || "/リプレ".equals(splitted[0])) {
                warp(c, 240000000);
                return true;
            }
            // マガティア
            if ("/magatia".equals(splitted[0]) || "/マガティア".equals(splitted[0])) {
                warp(c, 261000000);
                return true;
            }

            if ("/nextmap".equals(splitted[0])) {
                int index = LoadData.GetMapIDIndex(c.getPlayer().getMapId());
                int mapid = LoadData.GetMapIDByIndex(index + 1);

                if (0 <= mapid) {
                    warp(c, mapid);
                }
                return true;
            }

            if ("/prevmap".equals(splitted[0]) || "/previousmap".equals(splitted[0])) {
                int index = LoadData.GetMapIDIndex(c.getPlayer().getMapId());
                int mapid = LoadData.GetMapIDByIndex(index - 1);

                if (0 <= mapid) {
                    warp(c, mapid);
                }
                return true;
            }

            // warphere all
            if ("/wh".equals(splitted[0])) {
                MapleCharacter chr = c.getPlayer();
                for (MapleCharacter victim : c.getChannelServer().getPlayerStorage().getAllCharacters()) {
                    if (victim != chr) {
                        victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition()));
                    }
                }
                return true;
            }

            // 転職
            if ("/jc".equals(splitted[0]) || "/jobchange".equals(splitted[0]) || "/転職".equals(splitted[0])) {
                return CustomNPCTalk(c, 1012003, 9330104);
            }

            // NPC
            if ("/npctalk".equals(splitted[0]) || "/NPC会話".equals(splitted[0])) {
                if (splitted.length < 2) {
                    return true;
                }
                try {
                    int npcid = Integer.parseInt(splitted[1]);
                    if (NPCTalk(c, npcid)) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    ;
                }
                c.getPlayer().Notice("無効なNPCIDです");
                return true;
            }
            if ("/npctalk2".equals(splitted[0])) {
                if (splitted.length < 2) {
                    return true;
                }
                try {
                    int npcid = Integer.parseInt(splitted[1]);
                    if (CustomNPCTalk(c, 1012003, npcid)) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    ;
                }
                c.getPlayer().Notice("無効なNPCIDです");
                return true;
            }

            // テスト
            if ("/test".equals(splitted[0]) || "/テスト".equals(splitted[0])) {
                return CustomNPCTalk(c, 1012003, 9010021);
            }

            splitted[0] = splitted[0].replace("/", "!");
            CommandObject co = commands.get(splitted[0]);
            if (co == null) {
                c.getPlayer().dropMessage(6, "error");
                return true;
            }

            co.execute(c, splitted);
            c.getPlayer().dropMessage(6, splitted[0]);
            return true;
        }

        if (line.charAt(0) == PlayerGMRank.NORMAL.getCommandPrefix()) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase();

            CommandObject co = commands.get(splitted[0]);
            if (co == null || co.getType() != type) {
                sendDisplayMessage(c, "That player command does not exist.", type);
                return true;
            }
            try {
                int ret = co.execute(c, splitted); //Don't really care about the return value. ;D
            } catch (Exception e) {
                sendDisplayMessage(c, "There was an error.", type);
                if (c.getPlayer().isGM()) {
                    sendDisplayMessage(c, "Error: " + e, type);
                }
            }
            return true;
        }

        if (c.getPlayer().getGMLevel() > PlayerGMRank.NORMAL.getLevel()) {
            if (line.charAt(0) == PlayerGMRank.INTERN.getCommandPrefix() || line.charAt(0) == PlayerGMRank.GM.getCommandPrefix() || line.charAt(0) == PlayerGMRank.ADMIN.getCommandPrefix()) { //Redundant for now, but in case we change symbols later. This will become extensible.
                String[] splitted = line.split(" ");
                splitted[0] = splitted[0].toLowerCase();

                if (line.charAt(0) == '!') { //GM Commands
                    CommandObject co = commands.get(splitted[0]);
                    if (co == null || co.getType() != type) {
                        sendDisplayMessage(c, "That command does not exist.", type);
                        return true;
                    }
                    if (c.getPlayer().getGMLevel() >= co.getReqGMLevel()) {
                        int ret = co.execute(c, splitted);
                        if (ret > 0 && c.getPlayer() != null) { //incase d/c after command or something
                            logGMCommandToDB(c.getPlayer(), line);
                        }
                    } else {
                        sendDisplayMessage(c, "You do not have the privileges to use that command.", type);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private static void logGMCommandToDB(MapleCharacter player, String command) {
        PreparedStatement ps = null;
        try {
            ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO gmlog (cid, command, mapid) VALUES (?, ?, ?)");
            ps.setInt(1, player.getId());
            ps.setString(2, command);
            ps.setInt(3, player.getMap().getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, ex);
            ex.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {/*Err.. Fuck?*/

            }
        }
    }
}
