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

/**
 *
 * @author Riremito
 */
public class NpcCrane extends NpcScriptMan {

    private static final int MAP_ORBIS_TICKETING = 200000100; // オルビス : オルビスチケット売場
    private static final int MAP_ORBIS = 200000141; // オルビス : ステーション通路<武陵行き>
    private static final int MAP_MU_LUNG = 250000000; // 武陵 : 武陵
    private static final int MAP_MU_LUNG_TEMPLE = 250000100; // 武陵 : 武陵神社
    private static final int MAP_HERB_TOWN = 251000000; // 白草村 : 白草村
    private static final int MENU_ORBIS_TICKETING = 0;
    private static final int MENU_HERB_TOWN = 1;
    private static final int MENU_MU_LUNG_TEMPLE = 2;
    // 武陵神社からの料金 (初心者は 1/10)
    private final int cost_orbis = 6000;
    private final int cost_herb_town = 1500;
    // 白草村からの料金
    private final int cost_mu_lung_temple = 500;
    // オルビスからの料金
    private final int cost_mu_lung = 1500;
    private int menu = -1;
    private int cost = 0;

    @Override
    public boolean action(int status, int answer) {
        boolean beginner = getPlayer().getJob() == 0;
        switch (status) {
            case -1 -> {
                // 白草村
                // 翻訳
                say("OKです。気が変わったら、いつでもお声がけください。");
                return false;
            }
            case 0 -> {
                if (this.menu == MENU_HERB_TOWN) {
                    // 翻訳
                    say("OKです。気が変わったら、いつでもお声がけください。");
                    return false;
                }
                switch (getMapId()) {
                    case MAP_MU_LUNG_TEMPLE -> {
                        int cost_orbis_now = beginner ? this.cost_orbis / 10 : this.cost_orbis;
                        int cost_herb_town_now = beginner ? this.cost_herb_town / 10 : this.cost_herb_town;
                        // 翻訳
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("やあ！私は#b武陵#kと#bオルビス#kの間を行き来しているツルさ。いつも飛んでいるから、ついでに君みたいな旅人を安く運んでお金を稼ごうと思ってね。私にとってもいい商売なんだ。どうだい？");
                        nt.addMenu(MENU_ORBIS_TICKETING, "#m" + MAP_ORBIS_TICKETING + "#(" + cost_orbis_now + " メル)");
                        nt.addMenu(MENU_HERB_TOWN, "#m" + MAP_HERB_TOWN + "#(" + cost_herb_town_now + " メル)");
                        askMenu(nt);
                        return true;
                    }
                    case MAP_HERB_TOWN -> {
                        this.menu = MENU_MU_LUNG_TEMPLE;
                        this.cost = this.cost_mu_lung_temple;
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("良く来た！　我輩は#b#m" + MAP_MU_LUNG + "##kへ往復している孤高の鶴であーる！今#m" + MAP_MU_LUNG + "#に出発したいであーるか？#b" + this.cost + " メル#kでいいあーる。");
                        askYesNo(nt);
                        return true;
                    }
                    default -> {
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("良く来た！　我輩は#b#m" + MAP_MU_LUNG + "##kへ往復している孤高の鶴であーる！今#m" + MAP_MU_LUNG + "#に出発したいであーるか？");
                        nt.addMenu(MENU_MU_LUNG_TEMPLE, "#m" + MAP_MU_LUNG + "#(" + this.cost_mu_lung + " メル)");
                        askMenu(nt);
                        return true;
                    }
                }
            }
            case 1 -> {
                // 白草村
                if (this.menu == MENU_MU_LUNG_TEMPLE) {
                    if (!payMoney(this.cost)) {
                        // 翻訳
                        say("本当にメルは足りているかい？");
                        return false;
                    }
                    warp(MAP_MU_LUNG_TEMPLE);
                    return false;
                }
                this.menu = answer;
                switch (this.menu) {
                    case MENU_ORBIS_TICKETING -> {
                        // TODO : FieldSet (Crane_SS) で移動する, 他の人が移動中の場合は "今ちょうど別の人がオルビスに向かっているところなんだ。少ししてからまた話しかけてくれ。" (翻訳)
                        if (!payMoney(beginner ? this.cost_orbis / 10 : this.cost_orbis)) {
                            // 翻訳
                            say("本当にメルは足りているかい？");
                            return false;
                        }
                        warp(MAP_ORBIS_TICKETING);
                        return false;
                    }
                    case MENU_HERB_TOWN -> {
                        this.cost = beginner ? this.cost_herb_town / 10 : this.cost_herb_town;
                        // 翻訳
                        askYesNo("今すぐ#b#m" + MAP_HERB_TOWN + "##kへ移動するかい？#b" + this.cost + "メル#kを持っているなら、すぐに連れて行ってあげるよ。");
                        return true;
                    }
                    case MENU_MU_LUNG_TEMPLE -> {
                        // TODO : FieldSet (Crane_MR) で移動する, 他の人が移動中の場合は "今ちょうど別の人が武陵に向かっているところなんだ。少ししてからまた話しかけてくれ。" (翻訳)
                        if (!payMoney(this.cost_mu_lung)) {
                            // 翻訳
                            say("本当にメルは足りているかい？");
                            return false;
                        }
                        warp(MAP_MU_LUNG_TEMPLE);
                        return false;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case 2 -> {
                if (this.menu != MENU_HERB_TOWN) {
                    return false;
                }
                if (!payMoney(this.cost)) {
                    // 翻訳
                    say("本当にメルは足りているかい？");
                    return false;
                }
                warp(MAP_HERB_TOWN);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
