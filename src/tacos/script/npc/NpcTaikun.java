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

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : moonrabbit_tiger (JMS131-302), 会話文は lua_script_jp (moonrabbit_tiger) を利用
// 期間限定の追加の経験値は削除
public class NpcTaikun extends NpcScriptMan {

    private static final int NODE_START = 0;
    private static final int NODE_MENU = 1;
    private static final int NODE_SAYS = 2;
    private static final int NODE_CAKE = 3;
    private static final int NODE_OUT_ASK = 4;
    private static final int NODE_OUT = 5;
    private final String field_set_name = "MoonRabbit";
    private final int map_shortcut = 910010100; // 近道
    private final int map_return = 910010300; // 村への帰り道
    private final int item_cake = 4001101; // 月兎の餅
    private final int cake_count = 10;
    private final int clear_exp = 1600;
    private int node = NODE_START;
    private int last_status = 0;
    private int says_index = 0;
    // 翻訳
    private final String[] says = {
        "ここは、満月の夜に月うさぎが作るおいしい餅を最初に味わえる場所だ。",
        "このあたりの落ち葉からプリムラの種を集めて、三日月の植木鉢に植えると、プリムラの花が咲く。プリムラは6種類あって、それぞれ違う植木鉢が必要だ。種に合った植木鉢を選ぶことが欠かせない…",
        "プリムラの花が咲くと満月が昇り、月うさぎが現れて臼をつき始める。お前の仕事は、月うさぎが最高の餅作りに集中できるように、モンスターを倒すことだ。",
        "お前とパーティーで協力して、餅を10個持ってきてほしい。決められた時間内に餅を手に入れることを強くすすめる。"
    };

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        // 翻訳
        switch (this.node) {
            case NODE_START -> {
                if (field_set.getVar("clear").equals("1")) {
                    if (!isPartyBoss()) {
                        say("パーティーリーダーと一緒に進んでね。");
                        return false;
                    }
                    say("また#b#t" + this.item_cake + "##kを持ってきてね！いい旅を！", false, true);
                    this.node = NODE_OUT;
                    return true;
                }
                NpcScriptTag nt = new NpcScriptTag();
                if (!isPartyBoss()) {
                    nt.add("ガルル！俺はグローリー、いつでもこの場所を守る準備はできている。何の用だ？\r\n#b#L0# この場所について教えて。#l\r\n#L2# ここから出たい。#l", false);
                } else {
                    nt.add("ガルル！俺はグローリー、いつでもこの場所を守る準備はできている。何の用だ？\r\n#b#L0# この場所について教えて。#l\r\n#L1# #t" + this.item_cake + "#を持ってきたよ。 #l\r\n#L2# ここから出たい。#l", false);
                }
                askMenu(nt);
                this.node = NODE_MENU;
                return true;
            }
            case NODE_MENU -> {
                switch (answer) {
                    case 0 -> {
                        this.says_index = 0;
                        say(this.says[this.says_index], false, true);
                        this.node = NODE_SAYS;
                        return true;
                    }
                    case 1 -> {
                        if (getItemCount(this.item_cake) < this.cake_count) {
                            say("#b#t" + this.item_cake + "#を10個#k集めたか確認することをすすめる。");
                            return false;
                        }
                        say("おお…これは月うさぎが作った餅じゃないか？その餅をくれ。", false, true);
                        this.node = NODE_CAKE;
                        return true;
                    }
                    case 2 -> {
                        askYesNo("今やめたら、クエストは完了しないぞ。  本当にやめるのか？");
                        this.node = NODE_OUT_ASK;
                        return true;
                    }
                    default -> {
                        return false;
                    }
                }
            }
            case NODE_SAYS -> {
                if (!ok) {
                    this.says_index--;
                } else {
                    this.says_index++;
                }
                if (this.says_index < 0 || this.says.length <= this.says_index) {
                    return false;
                }
                say(this.says[this.says_index], 0 < this.says_index, this.says_index < this.says.length - 1);
                return this.says_index < this.says.length - 1;
            }
            case NODE_CAKE -> {
                // TODO : lua の field.setProtectMobDamagedByMob (月うさぎの保護) は未実装
                if (!exchange(0, this.item_cake, -this.cake_count)) {
                    say("もしかして餅をなくしたのか？");
                    return false;
                }
                if (!field_set.getVar("clear").equals("1")) {
                    effectScreen("quest/party/clear");
                    effectSound("Grupo1/Completado");
                    setMobGen(getMapId(), false, 0);
                    // TODO : lua の removeAllMob は MobDamagedByMob (月うさぎ) を除く
                    removeAllMob(getMapId());
                    field_set.incExpAll(this.clear_exp);
                    field_set.setVar("clear", "1");
                }
                say("うーん…うまい。また#b#t" + this.item_cake + "##kを持ってきてくれ！いい旅を！", false, true);
                this.node = NODE_OUT;
                return true;
            }
            case NODE_OUT_ASK -> {
                if (!ok) {
                    say("よし。頑張り続けろ。");
                    return false;
                }
                say("わかった。じゃあ、またな。", false, true);
                this.node = NODE_OUT;
                return true;
            }
            case NODE_OUT -> {
                if (field_set.getVar("clear").equals("1")) {
                    field_set.transferFieldAll(this.map_shortcut, "st00");
                } else {
                    warp(this.map_return, "st00");
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }
}
