/*
 * Copyright (C) 2026 Riremito
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

import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class NpcMrDo extends NpcScriptMan {

    private final int[] scroll_ids = {
        2043000, // 攻撃の書(片手剣)
        2043100, // 攻撃の書(片手斧)
        2043200, // 攻撃の書(片手鈍器)
        2043300, // 攻撃の書(短剣)
        2043700, // 魔力の書(杖)
        2043800, // 魔力の書(棒)
        2044000, // 攻撃の書(両手剣)
        2044100, // 攻撃の書(両手斧)
        2044200, // 攻撃の書(両手鈍器)
        2044300, // 攻撃の書(槍)
        2044400, // 攻撃の書(鉾)
        2044500, // 攻撃の書(弓)
        2044600, // 攻撃の書(弩)
        2044700, // 攻撃の書(投擲)
        2044800, // 攻撃の書（ナックル）
        2044900 // 攻撃の書（銃）
    };
    private final int[] material_ids = {
        4001124, // チエルの玉
        4010001 // 鋼鉄の原料
    };
    private final int[] material_quantities = {
        100,
        10
    };
    private final int[] donation_ids = {
        4000276, // ドングリ
        4000277, // 指貫
        4000278, // 針山
        4000279, // 赤花蛇の花
        4000280, // 青花蛇の花帽子
        4000291, // 壊れた壺のかけら
        4000292, // 人参煎液
        4000286, // 藁のかかし
        4000287, // 木のかかし
        4000293, // キキョウ
        4000294, // しなびたキキョウ
        4000298, // 古い紙
        4000284, // 腰帯(黄)
        4000288, // 折れた鹿の角
        4000285, // 腰帯(赤)
        4000282, // 桃の種
        4000295, // クロコファミリーの皮
        4000289, // 猫人形
        4000296, // 海賊の証
        4000297 // 船長の帽子 (元のスクリプトは選択時に 4031435 ダークワイバーンの霊魂 になっていた)
    };
    private final int donation_quantity = 100;
    private static final int MENU_MEDICINE = 0;
    private static final int MENU_SCROLL = 1;
    private static final int MENU_DONATION = 2;
    private static final int MENU_FORFEIT = 3;
    private int selected_type = -1;
    private int selected_item = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("私は多才多能な人なんだ。望むのが何なのか言ってみて。");
                nt.addMenu(MENU_MEDICINE, "薬を製造する");
                nt.addMenu(MENU_SCROLL, "呪文書を製造する");
                nt.addMenu(MENU_DONATION, "薬剤を寄付する");
                nt.addMenu(MENU_FORFEIT, "掛け軸復元をあきらめたいが...");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                this.selected_type = answer;
                switch (this.selected_type) {
                    case MENU_MEDICINE -> {
                        // TODO : 薬製造書を持っている場合の薬の製造
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("薬が作りたいならば、まず薬製造書から勉強してよ。 基本がない者がやたらに薬を使う事ほど危険なことってないからね。");
                        say(nt);
                        return false;
                    }
                    case MENU_SCROLL -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("どんな呪文書が作りたいのか？");
                        for (int i = 0; i < this.scroll_ids.length; i++) {
                            nt.addMenu(i, "#t" + this.scroll_ids[i] + "#");
                        }
                        askMenu(nt);
                        return true;
                    }
                    case MENU_DONATION -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("薬剤を寄付してくれるって？とても嬉しい話だね。アイテムの寄付は #b" + this.donation_quantity + "個#k 単位で受け取っているの。 寄付してくれた人には呪文書が作れる玉を渡しているよ。どんなアイテムを寄付してくれるの？ ");
                        for (int i = 0; i < this.donation_ids.length; i++) {
                            nt.addMenu(i, "#t" + this.donation_ids[i] + "#");
                        }
                        askMenu(nt);
                        return true;
                    }
                    default -> {
                        // TODO : 掛け軸復元をあきらめる
                        return false;
                    }
                }
            }
            case 2 -> {
                this.selected_item = answer;
                switch (this.selected_type) {
                    case MENU_SCROLL -> {
                        if (this.selected_item < 0 || this.scroll_ids.length <= this.selected_item) {
                            return false;
                        }
                        int scroll = this.scroll_ids[this.selected_item];
                        // No : 気が変わったようだね？ よし、もう一度考えさせるチャンスを与えるよ。
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("#b#t" + scroll + "##k?が作りたいのか？ #t" + scroll + "#を作ろうとしたら#b#t" + this.material_ids[0] + "# " + this.material_quantities[0] + "個#kと#b#t" + this.material_ids[1] + "# " + this.material_quantities[1] + "個#kが必要なの。 #k");
                        for (int i = 0; i < this.material_ids.length; i++) {
                            nt.add("#i" + this.material_ids[i] + "##t" + this.material_ids[i] + "# " + this.material_quantities[i] + "個");
                        }
                        nt.add("");
                        nt.add("どう？作ってみる？");
                        askYesNo(nt);
                        return true;
                    }
                    case MENU_DONATION -> {
                        if (this.selected_item < 0 || this.donation_ids.length <= this.selected_item) {
                            return false;
                        }
                        // No : なんだ。ああしたりこうしたり。なんでこう気まぐれなのか、まったく・・・
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("本当に#b#t" + this.donation_ids[this.selected_item] + "##k " + this.donation_quantity + "個を寄付してくれるの？");
                        askYesNo(nt);
                        return true;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case 3 -> {
                switch (this.selected_type) {
                    case MENU_SCROLL -> {
                        int scroll = this.scroll_ids[this.selected_item];
                        boolean complete = true;
                        for (int i = 0; i < this.material_ids.length; i++) {
                            if (!getPlayer().haveItem(this.material_ids[i], this.material_quantities[i])) {
                                complete = false;
                            }
                        }
                        if (!complete || !MapleInventoryManipulator.checkSpace(getPlayer(), scroll, 1, "")) {
                            say("材料が足りないか消費ウィンドウに空きがないのではないか確認してくれ。");
                            return false;
                        }
                        for (int i = 0; i < this.material_ids.length; i++) {
                            MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.material_ids[i], this.material_quantities[i], true, false);
                        }
                        // 元のスクリプトは材料を消費するのみで呪文書を渡していなかった
                        // TODO : 成功時の文章
                        MapleInventoryManipulator.addById(getPlayer(), scroll, 1); // TODO : item effect.
                        return false;
                    }
                    case MENU_DONATION -> {
                        // TODO : 寄付の処理 (薬剤の回収とチエルの玉の付与)
                        // 失敗 : 寄付する薬剤を本当に持っているのかETCウィンドウに空きがないのではないか確認してくれ。
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
