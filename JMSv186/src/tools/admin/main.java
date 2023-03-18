package tools.admin;

import client.MapleCharacter;
import client.messages.CommandProcessor;
import handling.channel.ChannelServer;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.Iterator;
import server.maps.MapleMap;

public class main {

    static JComboBox combobox_CH;

    public static void main() {
        JFrame frame = new JFrame();
        frame.setTitle("かえでサーバー v186.1 管理画面");
        //frame.setSize(800, 600);
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel();
        JButton button_FM = new JButton("FreeMarket");
        button_FM.addActionListener((ActionEvent event) -> {
            PanelTest();
        });
        JButton button_LV = new JButton("Level UP");
        button_LV.addActionListener((ActionEvent event) -> {
            PanelTest2();
        });

        p.add(button_FM);
        p.add(button_LV);

        JButton button_RE = new JButton("Reload");
        button_RE.addActionListener((ActionEvent event) -> {
            UpdateCharacterList();
        });
        p.add(button_RE);
        combobox_CH = new JComboBox();
        combobox_CH.addItem("更新してください");
        p.add(combobox_CH);

        JButton button_ST = new JButton("MesoPlz");
        button_ST.addActionListener((ActionEvent event) -> {
            SelectTest();
        });
        p.add(button_ST);

        frame.getContentPane().add(p, BorderLayout.WEST);
        frame.setVisible(true);
    }

    public static void UpdateCharacterList() {
        combobox_CH.removeAllItems();
        for (int i : ChannelServer.getAllInstance()) {
            ChannelServer channel = ChannelServer.getInstance(i);
            if (channel != null) {
                for (MapleCharacter chr : channel.getPlayerStorage().getAllCharacters()) {
                    if (chr != null) {
                        combobox_CH.addItem(chr.getName());
                    }
                }
            }
        }
    }

    public static void SelectTest() {
        String target_name = (String) combobox_CH.getSelectedItem();

        for (int i : ChannelServer.getAllInstance()) {
            ChannelServer channel = ChannelServer.getInstance(i);
            if (channel != null) {
                MapleCharacter target_character = channel.getPlayerStorage().getCharacterByName(target_name);
                if (target_character != null) {
                    target_character.gainMeso(777777, true);
                    return;
                }
            }
        }
    }

    public static void PanelTest() {
        for (int i : ChannelServer.getAllInstance()) {
            ChannelServer channel = ChannelServer.getInstance(i);
            if (channel != null) {
                //MapleCharacter character = channel.getPlayerStorage().getCharacterByName("リレミト");
                for (MapleCharacter chr : channel.getPlayerStorage().getAllCharacters()) {
                    if (chr != null) {
                        MapleMap map = channel.getMapFactory().getMap(910000000);
                        chr.changeMap(map, map.getPortal(0));
                    }
                }
            }
        }
    }

    public static void PanelTest2() {
        for (int i : ChannelServer.getAllInstance()) {
            ChannelServer channel = ChannelServer.getInstance(i);
            if (channel != null) {
                for (MapleCharacter chr : channel.getPlayerStorage().getAllCharacters()) {
                    if (chr != null) {
                        chr.gainExp(500000000, true, false, true);
                    }
                }
            }
        }
    }
}
