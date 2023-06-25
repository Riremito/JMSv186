package tools.admin;

import client.MapleCharacter;
import config.DebugConfig;
import handling.channel.ChannelServer;
import javax.swing.*;
import java.awt.event.*;
import server.maps.MapleMap;

public class main {

    static JComboBox cb_character;
    static JTextArea textarea_info;
    static JTextField tf_mesos, tf_tama, tf_mapid, tf_jobid, tf_level;

    public static void main() {
        if (!DebugConfig.open_debug_ui) {
            return;
        }

        JFrame f = new JFrame();
        f.setTitle("管理画面");
        f.setSize(800, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel();
        p.setLayout(null);

        {
            JButton b = new JButton("更新");
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
            JButton b = new JButton("情報取得");
            b.setBounds(10, 40, 100, 20);
            b.addActionListener((ActionEvent e) -> {
                GetCharacterInfo();
            });
            p.add(b);
        }

        {
            JTextArea a = new JTextArea("情報", 10, 50);
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
                GainMesos();
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
                MoveMap();
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
                LevelUp();
            });
            p.add(t);
            p.add(b);
            tf_level = t;
        }

        f.getContentPane().add(p);
        f.setVisible(true);
    }

    private static void UpdateCharacterListComboBox() {
        cb_character.removeAllItems();

        for (int i : ChannelServer.getAllInstance()) {
            ChannelServer channel = ChannelServer.getInstance(i);
            if (channel != null) {
                for (MapleCharacter chr : channel.getPlayerStorage().getAllCharacters()) {
                    if (chr != null) {
                        cb_character.addItem(chr.getName());
                    }
                }
            }
        }
    }

    // Comboboxで選択されたログイン中のキャラクター名からオブジェクトを取得
    private static MapleCharacter GetLoggedInCharacter() {
        if (cb_character.getItemCount() == 0) {
            return null;
        }

        String target_name = (String) cb_character.getSelectedItem();

        for (int i : ChannelServer.getAllInstance()) {
            ChannelServer channel = ChannelServer.getInstance(i);
            if (channel != null) {
                MapleCharacter c = channel.getPlayerStorage().getCharacterByName(target_name);
                if (c != null) {
                    return c;
                }

            }
        }

        return null;
    }

    // 情報取得
    private static boolean GetCharacterInfo() {
        MapleCharacter c = GetLoggedInCharacter();
        if (c == null) {
            return false;
        }

        String text = "";
        text += "キャラクター名: " + c.getName() + "\n";
        text += "レベル: " + c.getLevel() + "\n";
        text += "メル: " + c.getMeso() + "\n";
        text += "玉: " + c.getTama() + "\n";
        textarea_info.setText(text);
        return true;
    }

    private static boolean MoveMap() {
        MapleCharacter c = GetLoggedInCharacter();
        if (c == null) {
            return false;
        }

        int mapid = Integer.parseInt(tf_mapid.getText());
        for (int i : ChannelServer.getAllInstance()) {
            ChannelServer ch = ChannelServer.getInstance(i);
            MapleMap map = ch.getMapFactory().getMap(mapid);
            c.changeMap(map, map.getPortal(0));
            return true;
        }
        return false;
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

    // test
    private static void PanelTest() {
        for (int i : ChannelServer.getAllInstance()) {
            ChannelServer channel = ChannelServer.getInstance(i);
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
}
