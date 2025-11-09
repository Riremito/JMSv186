/*
 * Copyright (C) 2024 Riremito
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
package test;

import client.MapleCharacter;
import server.server.ServerOdinGame;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class ToolMan {

    static JTextField tf_tama;
    static JTextField tf_mapid;
    static JTextArea textarea_info;
    static JTextField tf_mesos;
    static JTextField tf_jobid;
    static JComboBox cb_character;
    static JTextField tf_level;

    public static void Open() {
        JFrame f = new JFrame();
        f.setTitle("ToolMan");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel();
        p.setLayout(null);
        {
            JButton b = new JButton("\u66f4\u65b0");
            b.setBounds(10, 10, 80, 20);
            b.addActionListener((ActionEvent e) -> {
                UpdateCharacterListComboBox();
            });
            p.add(b);
        }
        {
            JComboBox c = new JComboBox();
            c.setBounds(100, 10, 200, 20);
            p.add(c);
            cb_character = c;
        }
        {
            JButton b = new JButton("\u60c5\u5831\u53d6\u5f97");
            b.setBounds(10, 40, 100, 20);
            b.addActionListener((ActionEvent e) -> {
                GetCharacterInfo();
            });
            p.add(b);
        }
        {
            JTextArea a = new JTextArea("\u60c5\u5831", 10, 50);
            a.setBounds(10, 70, 360, 300);
            p.add(a);
            textarea_info = a;
        }
        {
            JTextField t = new JTextField();
            JButton b = new JButton("\u30e1\u30eb\u8ffd\u52a0");
            t.setBounds(400, 70, 180, 20);
            b.setBounds(600, 70, 120, 20);
            b.addActionListener((ActionEvent e) -> {
                GainMesos();
            });
            p.add(t);
            p.add(b);
            tf_mesos = t;
        }
        {
            JTextField t = new JTextField();
            JButton b = new JButton("\u7389\u8ffd\u52a0");
            t.setBounds(400, 100, 180, 20);
            b.setBounds(600, 100, 120, 20);
            b.addActionListener((ActionEvent e) -> {
                // not  coeded
            });
            p.add(t);
            p.add(b);
            tf_tama = t;
        }
        {
            JTextField t = new JTextField("910000000");
            JButton b = new JButton("\u30de\u30c3\u30d7\u79fb\u52d5");
            t.setBounds(400, 130, 180, 20);
            b.setBounds(600, 130, 120, 20);
            b.addActionListener((ActionEvent e) -> {
                MoveMap();
            });
            p.add(t);
            p.add(b);
            tf_mapid = t;
        }
        {
            JTextField t = new JTextField();
            JButton b = new JButton("\u8ee2\u8077");
            t.setBounds(400, 160, 180, 20);
            b.setBounds(600, 160, 120, 20);
            b.addActionListener((ActionEvent e) -> {
                // not  coeded
            });
            p.add(t);
            p.add(b);
            tf_jobid = t;
        }
        {
            JTextField t = new JTextField();
            JButton b = new JButton("\u30ec\u30d9\u30eb");
            t.setBounds(400, 190, 180, 20);
            b.setBounds(600, 190, 120, 20);
            b.addActionListener((ActionEvent e) -> {
                LevelUp();
            });
            p.add(t);
            p.add(b);
            tf_level = t;
        }
        f.getContentPane().add(p);
        f.setVisible(true);
    }

    // 情報取得
    private static boolean GetCharacterInfo() {
        MapleCharacter c = GetLoggedInCharacter();
        if (c == null) {
            return false;
        }
        String text = "";
        text += "\u30ad\u30e3\u30e9\u30af\u30bf\u30fc\u540d: " + c.getName() + "\n";
        text += "\u30ec\u30d9\u30eb: " + c.getLevel() + "\n";
        text += "\u30e1\u30eb: " + c.getMeso() + "\n";
        text += "\u7389: " + c.getTama() + "\n";
        textarea_info.setText(text);
        return true;
    }

    // Comboboxで選択されたログイン中のキャラクター名からオブジェクトを取得
    private static MapleCharacter GetLoggedInCharacter() {
        if (cb_character.getItemCount() == 0) {
            return null;
        }
        String target_name = (String) cb_character.getSelectedItem();
        for (int i : ServerOdinGame.getAllInstance()) {
            ServerOdinGame channel = ServerOdinGame.getInstance(i);
            if (channel != null) {
                MapleCharacter c = channel.getPlayerStorage().getCharacterByName(target_name);
                if (c != null) {
                    return c;
                }
            }
        }
        return null;
    }

    private static boolean MoveMap() {
        MapleCharacter c = GetLoggedInCharacter();
        if (c == null) {
            return false;
        }
        int mapid = Integer.parseInt(tf_mapid.getText());
        for (int i : ServerOdinGame.getAllInstance()) {
            ServerOdinGame ch = ServerOdinGame.getInstance(i);
            MapleMap map = ch.getMapFactory().getMap(mapid);
            c.changeMap(map, map.getPortal(0));
            return true;
        }
        return false;
    }

    // test
    private static void PanelTest() {
        for (int i : ServerOdinGame.getAllInstance()) {
            ServerOdinGame channel = ServerOdinGame.getInstance(i);
            if (channel != null) {
                for (MapleCharacter chr : channel.getPlayerStorage().getAllCharacters()) {
                    if (chr != null) {
                        MapleMap map = channel.getMapFactory().getMap(910000000);
                        chr.changeMap(map, map.getPortal(0));
                    }
                }
            }
        }
    }

    private static boolean GainMesos() {
        MapleCharacter c = GetLoggedInCharacter();
        if (c == null) {
            return false;
        }
        int mesos = Integer.parseInt(tf_mesos.getText());
        c.gainMeso(mesos, true);
        return false;
    }

    private static boolean LevelUp() {
        MapleCharacter c = GetLoggedInCharacter();
        if (c == null) {
            return false;
        }
        //int level = Integer.parseInt(tf_level.getText());
        c.levelUp();
        if (c.getExp() < 0) {
            c.gainExp(-c.getExp(), false, false, true);
        }
        return false;
    }

    private static void UpdateCharacterListComboBox() {
        cb_character.removeAllItems();
        for (int i : ServerOdinGame.getAllInstance()) {
            ServerOdinGame channel = ServerOdinGame.getInstance(i);
            if (channel != null) {
                for (MapleCharacter chr : channel.getPlayerStorage().getAllCharacters()) {
                    if (chr != null) {
                        cb_character.addItem(chr.getName());
                    }
                }
            }
        }
    }

}
