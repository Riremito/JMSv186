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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;
import odin.server.quest.MapleQuest;
import tacos.server.TacosFieldSet;
import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
public class NpcKonpeiBodyguard extends NpcScriptMan {

    private final int item_comb = 4000138; // 姐御の櫛
    private final int item_lantern = 4000141; // 大親分の提灯
    private final int map_boss = 801040100; // ジパング : 悪夢の果て
    private final int max_users = 20;
    private final int quest_boss = 8001; // 大親分への挑戦 (前回の入場時刻)
    private final int daily_minutes = 1440;
    private final DateTimeFormatter time_format = DateTimeFormatter.ofPattern("yy/MM/dd/HH/mm");

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1, 0 -> {
                if (status == -1 || answer < 0) {
                    // 翻訳
                    say("そうか？じゃあ、気が変わったら教えてくれ。");
                    return false;
                }
                // 大親分の提灯の回収
                int lantern = getPlayer().getItemQuantity(this.item_lantern, false);
                if (0 < lantern) {
                    MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_lantern, lantern, true, false);
                }
                if (!checkItem(this.item_comb)) {
                    // 翻訳
                    say("ここまで来たか。悪くない。これからボスに挑んでもらう！お前の力で強大なボスに立ち向かえるのか心配だが…誤解しないでくれ、うちのボスも彼女には歯が立たなかったんだ。もしボスを倒して彼女の櫛を持ち帰ってくれたら、次のステージへ連れて行ってやる。");
                    return false;
                }
                LocalDateTime time_now = LocalDateTime.now();
                String time_last = getPlayer().getQuest(MapleQuest.getInstance(this.quest_boss)).getCustomData();
                if (time_last != null && !time_last.isEmpty()) {
                    try {
                        if (Duration.between(LocalDateTime.parse(time_last, this.time_format), time_now).toMinutes() < this.daily_minutes) {
                            // 翻訳
                            say("敵が外にいる！ここで待とう！");
                            return false;
                        }
                    } catch (DateTimeParseException e) {
                    }
                }
                TacosFieldSet field_set = getFieldSet("shouwaBoss");
                // TODO : FieldSet の変数 shouwaBoss はリアクターのスクリプトで設定される (未実装)
                if (field_set != null && "yes".equals(field_set.getVar("shouwaBoss"))) {
                    // 翻訳
                    say("悪いが、戦いはもう始まっている。安全のため、今は外で待っていてもらわないと。");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ややっ！お前さんが持っているのはここの姐御の櫛じゃないか！？やっぱりオイラが見込んだとおりだった！お前さんなら大親分のやつを倒せるかもしれねぇ。本丸に攻め込むのか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("本当に入室するんだな？この部屋に長居はできないぜ！それとやつらは部屋の奥の宝箱の上に姐御の櫛を置くとすっ飛んで来るから気をつけろ。");
                askYesNo(nt);
                return true;
            }
            case 2 -> {
                TacosMap map = getPlayer().findMap(this.map_boss);
                TacosFieldSet field_set = getFieldSet("shouwaBoss");
                int users = field_set != null ? field_set.getUserCount() : (map != null ? map.getCharactersSize() : 0);
                if (this.max_users <= users) {
                    // 翻訳
                    say("今、勇敢な戦士たちがたくさん中で、俺たちのボスに取りついた悪霊と戦っている。部屋には一度に" + this.max_users + "人しか入れないんだ。今は順番を待ってくれ。");
                    return false;
                }
                if (map != null && map.getCharactersSize() == 0) {
                    map.resetFully();
                }
                MapleQuest.getInstance(this.quest_boss).forceStart(getPlayer(), getNpcId(), LocalDateTime.now().format(this.time_format));
                warp(this.map_boss);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
