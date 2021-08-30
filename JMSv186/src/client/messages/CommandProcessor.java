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
import server.maps.MapleMap;
import server.maps.SavedLocationType;
import tools.FileoutputUtil;

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

    public static boolean processCommand(MapleClient c, String line, CommandType type) {
        // 通常クライアントのコマンドの処理
        if (line.charAt(0) == PlayerGMRank.MAPLE.getCommandPrefix()) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase();

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
