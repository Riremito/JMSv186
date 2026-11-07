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

import java.util.ArrayList;

/**
 *
 * @author Riremito
 */
public class Npc_Ariant_Mazra extends NpcScriptMan {

    private final int item_hair_coupon = 5150027; // 調髪特別券(アリアント)
    private final int item_hair_color_coupon = 5151022; // 染毛特別券(アリアント)
    private final int[] male_hairs = {
        30030, // 丸坊主(黒)
        30020, // 長髪(黒)
        30000, // かりあげ(黒)
        30130, // パーマ(黒)
        30350, // ワイルド(黒)
        30190, // パンプキン(黒)
        30110, // とんがり(黒)
        30180, // ライオンA(黒)
        30050, // プリンス(黒)
        30040, // 短髪(灰)
        30160 // サンダー(黒)
    };
    private final int[] female_hairs = {
        31050, // ショート(黒)
        31040, // パンク(黒)
        31000, // キューティー(黒)
        31060, // シック(黒)
        31090, // ポニーテールA(黒)
        31020, // ウェーブ(黒)
        31130, // 両編み(黒)
        31120, // 片編み(黒)
        31140, // チャイナA(黒)
        31330, // マジック(黒)
        31010 // ウインド(黒)
    };
    private final int hair_color_count = 8;
    private static final int MENU_HAIR = 0;
    private static final int MENU_HAIR_COLOR = 1;
    private static final int MENU_IDOL_STAR_HAIR = 2;
    private final ArrayList<Integer> styles = new ArrayList<>();
    private int selected = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ふふふ…熱い砂漠でもヘアスタイルを管理するあなたは本当のオシャレ屋ですね。#b調髪特別券(アリアント)#kや#b染毛特別券(アリアント)#kを持っていればヘアスタイルを変えてみませんか。");
                nt.addMenu(MENU_HAIR, "ヘアスタイルの変更(調髪特別券使用)");
                nt.addMenu(MENU_HAIR_COLOR, "ヘアカラーの変更(染毛特別券使用)");
                nt.addMenu(MENU_IDOL_STAR_HAIR, "アイドルスターヘアに変更");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                this.selected = answer;
                this.styles.clear();
                switch (this.selected) {
                    case MENU_HAIR -> {
                        int color = getPlayer().getHair() % 10;
                        for (int hair : (getPlayer().getGender() == 0) ? this.male_hairs : this.female_hairs) {
                            this.styles.add(hair + color);
                        }
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("ふふふ…あなたのヘアスタイルを新しいスタイルに変えてあげましょう。今のヘアスタイルにうんざりしていませんか？#b調髪特別券(アリアント)#kを持っているのでしたら、お任せください。さあ変えたいヘアスタイルをゆっくり選んでください。");
                        askAvatar(nt, this.styles);
                        return true;
                    }
                    case MENU_HAIR_COLOR -> {
                        int base = getPlayer().getHair() / 10 * 10;
                        for (int i = 0; i < this.hair_color_count; i++) {
                            this.styles.add(base + i);
                        }
                        // TODO : 英語の文章
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("Every once in a while, it doesn't hurt to change up your hair color... it's fun. Allow me, the great Mazra, to dye your hair, so you just bring me #bAriant hair color coupon(VIP)#k, and choose your new hair color.");
                        askAvatar(nt, this.styles);
                        return true;
                    }
                    case MENU_IDOL_STAR_HAIR -> {
                        // TODO : アイドルスターヘアに変更
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case 2 -> {
                if (answer < 0 || this.styles.size() <= answer) {
                    return false;
                }
                int coupon = (this.selected == MENU_HAIR) ? this.item_hair_coupon : this.item_hair_color_coupon;
                // TODO : 英語の文章
                if (!checkItem(coupon)) {
                    say("I thought I told you, you need the coupon in order for me to work magic on your hair check again.");
                    return false;
                }
                removeItem(coupon, 1);
                getPlayer().setHair(this.styles.get(answer));
                getPlayer().sendStatChanged();
                say((this.selected == MENU_HAIR) ? "Enjoy your new and improved hairstyle!" : "Enjoy your new and improved haircolor!");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
