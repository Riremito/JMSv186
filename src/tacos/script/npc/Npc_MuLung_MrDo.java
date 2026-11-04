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
import odin.constants.GameConstants;
import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class Npc_MuLung_MrDo extends NpcScriptMan {

    private static final int MENU_MEDICINE = 0;
    private static final int MENU_SCROLL = 1;
    private static final int MENU_DONATION = 2;
    private static final int MENU_FORFEIT = 3;
    private final int item_recipe_book = 4161030; // 薬材調合書
    // {薬, メル, 材料, 個数, ...}
    private final int[][] medicines = {
        {2022145, 0, 2022116, 3}, // 桃缶詰 : 桃
        {2022146, 0, 2022116, 3}, // モモジュース : 桃
        {2022147, 910, 4000281, 10, 4000293, 10}, // キキョウ蛇湯 : 蛇皮, キキョウ
        {2022148, 950, 4000276, 20, 2002005, 1}, // 上名丸 : ドングリ, 名手の薬
        {2022149, 1940, 4000288, 20, 4000292, 20}, // 聡明丸 : 折れた鹿の角, 人参煎液
        {2022150, 600, 4000295, 10}, // 酢名膏 : クロコファミリーの皮
        {2050004, 0, 2022131, 1, 2022132, 1}, // 万病治療薬 : 人参濃縮液, キキョウ濃縮液
        {4031554, 0, 4000286, 20, 4000287, 20, 4000293, 20} // 桃木用名薬 : 藁のかかし, 木のかかし, キキョウ
    };
    // {呪文書 (99%), 呪文書 (1%), 材料}
    private final int[][] scrolls = {
        {2043000, 2043001, 4010001}, // 攻撃の書(片手剣), 鋼鉄の原料
        {2043100, 2043101, 4010001}, // 攻撃の書(片手斧), 鋼鉄の原料
        {2043200, 2043201, 4010001}, // 攻撃の書(片手鈍器), 鋼鉄の原料
        {2043300, 2043301, 4020000}, // 攻撃の書(短剣), ガーネットの原石
        {2043700, 2043701, 4020005}, // 魔力の書(杖), サファイアの原石
        {2043800, 2043801, 4020005}, // 魔力の書(棒), サファイアの原石
        {2044000, 2044001, 4010001}, // 攻撃の書(両手剣), 鋼鉄の原料
        {2044100, 2044101, 4010001}, // 攻撃の書(両手斧), 鋼鉄の原料
        {2044200, 2044201, 4010001}, // 攻撃の書(両手鈍器), 鋼鉄の原料
        {2044300, 2044301, 4010001}, // 攻撃の書(槍), 鋼鉄の原料
        {2044400, 2044401, 4010001}, // 攻撃の書(鉾), 鋼鉄の原料
        {2044500, 2044501, 4010002}, // 攻撃の書(弓), ミスリルの原料
        {2044600, 2044601, 4010002}, // 攻撃の書(弩), ミスリルの原料
        {2044700, 2044701, 4020000}, // 攻撃の書(投擲), ガーネットの原石
        {2044800, 2044801, 4010001}, // 攻撃の書（ナックル）, TODO : 材料が不明 (lua に存在しない)
        {2044900, 2044901, 4010001} // 攻撃の書（銃）, TODO : 材料が不明 (lua に存在しない)
    };
    private final int item_orb = 4001124; // チエルの玉
    private final int orb_quantity = 100;
    private final int scroll_material_quantity = 10;
    // {寄付するアイテム, 乱数の範囲, しきい値, しきい値以下の個数, しきい値より上の個数}
    private final int[][] donations = {
        {4000276, 2, 1, 7, 6}, // ドングリ
        {4000277, 6, 5, 7, 6}, // 指貫
        {4000278, 6, 5, 7, 8}, // 針山
        {4000279, 1, 0, 7, 8}, // 赤花蛇の花
        {4000280, 1, 0, 7, 8}, // 青花蛇の花帽子
        {4000291, 6, 5, 8, 7}, // 壊れた壺のかけら
        {4000292, 0, 0, 8, 8}, // 人参煎液
        {4000286, 2, 1, 8, 9}, // 藁のかかし
        {4000287, 6, 5, 9, 8}, // 木のかかし
        {4000293, 1, 0, 9, 10}, // キキョウ
        {4000294, 0, 0, 10, 10}, // しなびたキキョウ
        {4000298, 1, 0, 10, 11}, // 古い紙
        {4000284, 6, 5, 11, 10}, // 腰帯(黄)
        {4000288, 2, 1, 12, 11}, // 折れた鹿の角
        {4000285, 1, 0, 12, 13}, // 腰帯(赤)
        {4000282, 2, 1, 13, 14}, // 桃の種
        {4000295, 5, 4, 14, 15}, // クロコファミリーの皮
        {4000289, 0, 0, 15, 15}, // 猫人形
        {4000296, 6, 5, 16, 15}, // 海賊の証
        {4000297, 2, 1, 17, 16} // 船長の帽子
    };
    private final int donation_quantity = 100;
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
                // 確認でいいえを選択した
                if (0 <= this.selected_item) {
                    switch (this.selected_type) {
                        case MENU_MEDICINE -> {
                            // 翻訳
                            say("わかった。決める前にゆっくり考えてくれ。");
                        }
                        case MENU_SCROLL -> {
                            say("気が変わったようだね？ よし、もう一度考えさせるチャンスを与えるよ。");
                        }
                        case MENU_DONATION -> {
                            say("なんだ。ああしたりこうしたり。なんでこう気まぐれなのか、まったく・・・");
                        }
                        default -> {
                        }
                    }
                    return false;
                }
                this.selected_type = answer;
                switch (this.selected_type) {
                    case MENU_MEDICINE -> {
                        if (!checkItem(this.item_recipe_book)) {
                            NpcScriptTag nt = new NpcScriptTag();
                            nt.add("薬が作りたいならば、まず薬製造書から勉強してよ。 基本がない者がやたらに薬を使う事ほど危険なことってないからね。");
                            say(nt);
                            return false;
                        }
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("どんな薬を作りたい？");
                        for (int i = 0; i < this.medicines.length; i++) {
                            nt.addMenu(i, "#i" + this.medicines[i][0] + ":# #t" + this.medicines[i][0] + "#");
                        }
                        askMenu(nt);
                        return true;
                    }
                    case MENU_SCROLL -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("どんな呪文書が作りたいのか？");
                        for (int i = 0; i < this.scrolls.length; i++) {
                            nt.addMenu(i, "#t" + this.scrolls[i][0] + "#");
                        }
                        askMenu(nt);
                        return true;
                    }
                    case MENU_DONATION -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("薬剤を寄付してくれるって？とても嬉しい話だね。アイテムの寄付は #b" + this.donation_quantity + "個#k 単位で受け取っているの。 寄付してくれた人には呪文書が作れる玉を渡しているよ。どんなアイテムを寄付してくれるの？ ");
                        for (int i = 0; i < this.donations.length; i++) {
                            nt.addMenu(i, "#t" + this.donations[i][0] + "#");
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
                switch (this.selected_type) {
                    case MENU_MEDICINE -> {
                        if (answer < 0 || this.medicines.length <= answer) {
                            return false;
                        }
                        this.selected_item = answer;
                        int[] medicine = this.medicines[this.selected_item];
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        String materials = "";
                        for (int i = 2; i < medicine.length; i += 2) {
                            materials += (i == 2 ? "" : "、") + "#b#t" + medicine[i] + "# " + medicine[i + 1] + "個#k";
                        }
                        if (0 < medicine[1]) {
                            materials += "、#b" + medicine[1] + "メル#k";
                        }
                        nt.add("#b#t" + medicine[0] + "##kを作りたいのかい？#t" + medicine[0] + "#を作るには、次のものが必要だ：" + materials + "。");
                        nt.add("");
                        for (int i = 2; i < medicine.length; i += 2) {
                            nt.add("#i" + medicine[i] + "# #t" + medicine[i] + "# " + medicine[i + 1] + "個");
                        }
                        if (0 < medicine[1]) {
                            nt.add("#i4031138# " + medicine[1] + "メル");
                        }
                        nt.add("");
                        nt.add("本当に作るかい？");
                        askYesNo(nt);
                        return true;
                    }
                    case MENU_SCROLL -> {
                        if (answer < 0 || this.scrolls.length <= answer) {
                            return false;
                        }
                        this.selected_item = answer;
                        int scroll = this.scrolls[this.selected_item][0];
                        int material = this.scrolls[this.selected_item][2];
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("#b#t" + scroll + "##k?が作りたいのか？ #t" + scroll + "#を作ろうとしたら#b#t" + this.item_orb + "# " + this.orb_quantity + "個#kと#b#t" + material + "# " + this.scroll_material_quantity + "個#kが必要なの。 #k");
                        nt.add("#i" + this.item_orb + "##t" + this.item_orb + "# " + this.orb_quantity + "個");
                        nt.add("#i" + material + "##t" + material + "# " + this.scroll_material_quantity + "個");
                        nt.add("");
                        nt.add("どう？作ってみる？");
                        askYesNo(nt);
                        return true;
                    }
                    case MENU_DONATION -> {
                        if (answer < 0 || this.donations.length <= answer) {
                            return false;
                        }
                        this.selected_item = answer;
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("本当に#b#t" + this.donations[this.selected_item][0] + "##k " + this.donation_quantity + "個を寄付してくれるの？");
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
                    case MENU_MEDICINE -> {
                        int[] medicine = this.medicines[this.selected_item];
                        boolean complete = medicine[1] <= getPlayer().getMeso() && MapleInventoryManipulator.checkSpace(getPlayer(), medicine[0], 1, "");
                        for (int i = 2; i < medicine.length; i += 2) {
                            if (getPlayer().getItemQuantity(medicine[i], false) < medicine[i + 1]) {
                                complete = false;
                            }
                        }
                        if (!complete) {
                            // 翻訳
                            say("正しい薬草をそろえて持ってきたか確認してくれ。何か足りないようだ。");
                            return false;
                        }
                        if (0 < medicine[1]) {
                            payMoney(medicine[1]);
                        }
                        for (int i = 2; i < medicine.length; i += 2) {
                            MapleInventoryManipulator.removeById(getPlayer(), GameConstants.getInventoryType(medicine[i]), medicine[i], medicine[i + 1], true, false);
                        }
                        MapleInventoryManipulator.addById(getPlayer(), medicine[0], (short) 1); // TODO : item effect.
                        return false;
                    }
                    case MENU_SCROLL -> {
                        int[] scroll = this.scrolls[this.selected_item];
                        // 1% の確率で別の呪文書になる
                        int scroll_id = (int) (Math.random() * 100) <= 98 ? scroll[0] : scroll[1];
                        if (getPlayer().getItemQuantity(this.item_orb, false) < this.orb_quantity || getPlayer().getItemQuantity(scroll[2], false) < this.scroll_material_quantity || !MapleInventoryManipulator.checkSpace(getPlayer(), scroll_id, 1, "")) {
                            say("材料が足りないか消費ウィンドウに空きがないのではないか確認してくれ。");
                            return false;
                        }
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_orb, this.orb_quantity, true, false);
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, scroll[2], this.scroll_material_quantity, true, false);
                        MapleInventoryManipulator.addById(getPlayer(), scroll_id, (short) 1); // TODO : item effect.
                        return false;
                    }
                    case MENU_DONATION -> {
                        int[] donation = this.donations[this.selected_item];
                        int orb_count = (int) (Math.random() * (donation[1] + 1)) <= donation[2] ? donation[3] : donation[4];
                        if (getPlayer().getItemQuantity(donation[0], false) < this.donation_quantity || !MapleInventoryManipulator.checkSpace(getPlayer(), this.item_orb, orb_count, "")) {
                            say("寄付する薬剤を本当に持っているのかETCウィンドウに空きがないのではないか確認してくれ。");
                            return false;
                        }
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, donation[0], this.donation_quantity, true, false);
                        MapleInventoryManipulator.addById(getPlayer(), this.item_orb, (short) orb_count);
                        // 翻訳
                        say("寄付してくれて本当にありがとう。必ず役立てるよ。これが最後の寄付にならないといいんだがな。");
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
