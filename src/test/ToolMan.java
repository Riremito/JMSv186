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

import odin.client.MapleCharacter;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import odin.server.maps.MapleMap;
import tacos.server.TacosChannel;
import tacos.server.TacosWorld;

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
            JButton b = new JButton("更新");
            b.setBounds(10, 10, 80, 20);
            b.addActionListener((ActionEvent e) -> {
                updateCharacterListComboBox();
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
            JButton b = new JButton("情報取得");
            b.setBounds(10, 40, 100, 20);
            b.addActionListener((ActionEvent e) -> {
                getCharacterInfo();
            });
            p.add(b);
        }
        {
            JTextArea a = new JTextArea("", 10, 50);
            a.setBounds(10, 70, 360, 300);
            p.add(a);
            textarea_info = a;
        }
        {
            JTextField t = new JTextField();
            JButton b = new JButton("メル追加");
            t.setBounds(400, 70, 180, 20);
            b.setBounds(600, 70, 120, 20);
            b.addActionListener((ActionEvent e) -> {
                addMesos();
            });
            p.add(t);
            p.add(b);
            tf_mesos = t;
        }
        {
            JTextField t = new JTextField();
            JButton b = new JButton("玉追加");
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
            JButton b = new JButton("マップ移動");
            t.setBounds(400, 130, 180, 20);
            b.setBounds(600, 130, 120, 20);
            b.addActionListener((ActionEvent e) -> {
                moveMap();
            });
            p.add(t);
            p.add(b);
            tf_mapid = t;
        }
        {
            JTextField t = new JTextField();
            JButton b = new JButton("転職");
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
            JButton b = new JButton("レベル");
            t.setBounds(400, 190, 180, 20);
            b.setBounds(600, 190, 120, 20);
            b.addActionListener((ActionEvent e) -> {
                levelUp();
            });
            p.add(t);
            p.add(b);
            tf_level = t;
        }
        f.getContentPane().add(p);
        f.setVisible(true);
    }

    // 情報取得
    private static boolean getCharacterInfo() {
        MapleCharacter player = getLoggedInCharacter();
        if (player == null) {
            return false;
        }
        String text = "";
        text += "キャラクター名 : " + player.getName() + "\n";
        text += "レベル : " + player.getLevel() + "\n";
        text += "メル : " + player.getMeso() + "\n";
        text += "玉 : " + player.getTama() + "\n";
        textarea_info.setText(text);
        return true;
    }

    // Comboboxで選択されたログイン中のキャラクター名からオブジェクトを取得
    private static MapleCharacter getLoggedInCharacter() {
        if (cb_character.getItemCount() == 0) {
            return null;
        }
        String target_name = (String) cb_character.getSelectedItem();
        MapleCharacter player = TacosWorld.find(0).findOnlinePlayer(target_name);
        if (player != null) {
            return player;
        }

        return null;
    }

    private static boolean moveMap() {
        MapleCharacter player = getLoggedInCharacter();
        if (player == null) {
            return false;
        }
        int map_id = Integer.parseInt(tf_mapid.getText());
        MapleMap map_to = player.getChannelServer().getMapFactory().getMap(map_id);
        if (map_to == null) {
            return false;
        }
        player.changeMap(map_to, map_to.getPortal(0));
        return true;
    }

    private static boolean addMesos() {
        MapleCharacter player = getLoggedInCharacter();
        if (player == null) {
            return false;
        }
        int mesos = Integer.parseInt(tf_mesos.getText());
        player.gainMeso(mesos, true);
        return false;
    }

    private static boolean levelUp() {
        MapleCharacter player = getLoggedInCharacter();
        if (player == null) {
            return false;
        }
        //int level = Integer.parseInt(tf_level.getText());
        player.levelUp();
        if (player.getExp() < 0) {
            player.gainExp(-player.getExp(), false, false, true);
        }
        return false;
    }

    private static void updateCharacterListComboBox() {
        cb_character.removeAllItems();
        for (TacosChannel channel : TacosWorld.find(0).getChannels()) {
            for (MapleCharacter player : channel.getOnlinePlayers().get()) {
                if (player != null) {
                    cb_character.addItem(player.getName());
                }
            }
        }
    }

}
