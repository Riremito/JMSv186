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
package tacos.script.quest;

/**
 *
 * @author Riremito
 */
// Quest.wz endscript : scrollchange1, scrollchange2, scrollchange3, scrollchange4 (JMS186), lua の書交換イベント (07年3月5日から1か月間、毎週月曜日)
public class Quest_Event_ScrollChange extends QuestScriptMan {

    private final int quest_special_first = 4615; // カサンドラのスペシャル占い (4615 - 4618)
    private final int quest_daily_first = 9682; // 毎日毎日イベント3月 5日 (9682 - 9685)
    private final int quest_daily_count = 4;
    private final int quest_scroll_count = 7720; // 書を交換した回数 (各日1桁)
    private final int scroll_count_max = 5;
    // scrollchange1 - scrollchange4 の日付
    private final String[] event_dates = {"07/03/05", "07/03/12", "07/03/19", "07/03/26"};
    // 武器用の書
    private final int[][] weapon_pages = {
        {2043001, 2043002, 2043101, 2043102, 2043201, 2043202, 2043301, 2043302, 2043701, 2043702},
        {2043801, 2043802, 2044001, 2044002, 2044101, 2044102, 2044201, 2044202, 2044301, 2044302},
        {2044401, 2044402, 2044501, 2044502, 2044601, 2044602, 2044701, 2044702}
    };
    // 防具用の書
    private final int[][] armor_pages = {
        {2040001, 2040002, 2040004, 2040005, 2040301, 2040302, 2040401, 2040402, 2040501, 2040502},
        {2040504, 2040505, 2040513, 2040514, 2040516, 2040517, 2040601, 2040602, 2040701, 2040702},
        {2040704, 2040705, 2040707, 2040708, 2040801, 2040802, 2040804, 2040805, 2040901, 2040902},
        {2041001, 2041002, 2041004, 2041005, 2041007, 2041008, 2041010, 2041011, 2041013, 2041014},
        {2041016, 2041017, 2041019, 2041020, 2041022, 2041023}
    };
    // ペット装備用の書
    private final int[] pet_scrolls = {2048001, 2048002, 2048004, 2048005};
    // 交換前の書, 交換後の書 (成功確率が5%高い書)
    private final int[] scroll_change = {
        2043001, 2043011, 2043002, 2043012, 2043101, 2043106, 2043102, 2043107, 2043201, 2043206,
        2043202, 2043207, 2043301, 2043306, 2043302, 2043307, 2043701, 2043706, 2043702, 2043707,
        2043801, 2043806, 2043802, 2043807, 2044001, 2044006, 2044002, 2044007, 2044101, 2044106,
        2044102, 2044107, 2044201, 2044206, 2044202, 2044207, 2044301, 2044306, 2044302, 2044307,
        2044401, 2044406, 2044402, 2044407, 2044501, 2044506, 2044502, 2044507, 2044601, 2044606,
        2044602, 2044607, 2044701, 2044706, 2044702, 2044707,
        2048001, 2048006, 2048002, 2048007, 2048004, 2048008, 2048005, 2048009,
        2040001, 2040019, 2040002, 2040020, 2040004, 2040021, 2040005, 2040022, 2040301, 2040313,
        2040302, 2040314, 2040401, 2040415, 2040402, 2040416, 2040501, 2040522, 2040502, 2040523,
        2040504, 2040524, 2040505, 2040525, 2040513, 2040526, 2040514, 2040527, 2040516, 2040528,
        2040517, 2040529, 2040601, 2040615, 2040602, 2040616, 2040701, 2040718, 2040702, 2040719,
        2040704, 2040720, 2040705, 2040721, 2040707, 2040722, 2040708, 2040723, 2040801, 2040819,
        2040802, 2040820, 2040804, 2040821, 2040805, 2040822, 2040901, 2040910, 2040902, 2040911,
        2041001, 2041042, 2041002, 2041043, 2041004, 2041044, 2041005, 2041045, 2041007, 2041046,
        2041008, 2041047, 2041010, 2041048, 2041011, 2041049, 2041013, 2041050, 2041014, 2041051,
        2041016, 2041052, 2041017, 2041053, 2041019, 2041054, 2041020, 2041055, 2041022, 2041056,
        2041023, 2041057
    };
    private final int step_ask = 1; // 書に魔法をかけるか
    private final int step_category = 2; // 書の種類
    private final int step_page = 3; // 交換する書
    private final int step_confirm = 4; // 交換の確認
    private int step = 0;
    private int today_index = -1; // lua の scrollchange の result
    private int category = 0; // 0 = 武器, 1 = 防具, 2 = ペット装備
    private int page = 0;
    private int scroll = 0;

    @Override
    public boolean questEnd(int status, int answer) {
        // lua の scroll_today
        String today = currentTime().substring(0, 8);
        int active = 0;
        int scroll_today = -1;
        for (int i = 0; i < this.quest_daily_count; i++) {
            if (getQuestStatus(this.quest_daily_first + i) == 1) {
                active++;
            }
        }
        for (int i = 0; i < this.quest_daily_count; i++) {
            if (!today.equals(this.event_dates[i])) {
                continue;
            }
            boolean only = true;
            for (int j = 0; j < this.quest_daily_count; j++) {
                if ((getQuestStatus(this.quest_daily_first + j) == 1) != (i == j)) {
                    only = false;
                }
            }
            if (only) {
                scroll_today = i;
            }
        }
        // 翻訳
        String text_active = "3月5日、12日、19日、26日の毎日イベントは一度に1つしか進行できないから、古いクエストを放棄してね。";
        // 翻訳
        String text_schedule = "書交換イベントのスケジュールをホームページで確認してね。";

        if (status == 0 && this.step == 0) {
            // lua の scrollchange1 - scrollchange4
            int script_index = (getQuestId() < this.quest_daily_first) ? (getQuestId() - this.quest_special_first) : (getQuestId() - this.quest_daily_first);
            if (script_index < 0 || this.event_dates.length <= script_index || !today.equals(this.event_dates[script_index])) {
                // 翻訳
                say("クエストを受けていないか、イベント期間中ではないわ。");
                return false;
            }
            // lua の scrollchange
            if (getQuestData(this.quest_scroll_count).isEmpty()) {
                setQuestData(this.quest_scroll_count, "0000");
            }
            if (1 < active) {
                say(text_active);
                return false;
            }
            this.today_index = scroll_today;
            if (this.today_index == -1) {
                // 翻訳
                say("クエストを受けていないか、イベント期間中ではないわ。");
                return false;
            }
            int count = integer(getQuestData(this.quest_scroll_count).substring(this.today_index, this.today_index + 1));
            if (this.scroll_count_max <= count) {
                // 翻訳
                say("今日はもう書をすべて交換したわ。");
                return false;
            }
            this.step = this.step_ask;
            // 翻訳
            askYesNo("#b毎日イベント！#k 3月の1か月間、毎週月曜日はこのカサンドラ様が書に魔法をかけて、成功確率が5%高い書にしてあげているの。ただし、魔法をかけた書は交換できなくなるわ。どう、やってみる？");
            return true;
        }

        if (this.step == this.step_ask) {
            if (getLastAction() == 0) {
                // 翻訳
                say("書に魔法をかけるのは思ったより大変なの。だから3月の毎週月曜日にしかできないのよ。気が変わったら、スケジュールに合わせて私のところに来てね。");
                return false;
            }
            this.step = this.step_category;
            // 翻訳
            askMenu("どの種類の書を交換する？\r\n#b#L0# 武器用の書を交換#l\r\n#L1# 防具用の書を交換#l\r\n#L2# ペット装備用の書を交換#l");
            return true;
        }

        if (this.step == this.step_category || this.step == this.step_page) {
            if (this.step == this.step_category) {
                if (answer < 0 || 2 < answer) {
                    return false;
                }
                this.category = answer;
                this.page = 0;
            } else {
                int[] scrolls = (this.category == 2) ? this.pet_scrolls : ((this.category == 0) ? this.weapon_pages[this.page] : this.armor_pages[this.page]);
                // 最初のページ (ペット装備用の書を含む) の選択後に日付を再確認する
                if (this.page == 0) {
                    if (1 < active) {
                        say(text_active);
                        return false;
                    }
                    if (scroll_today != this.today_index) {
                        // 翻訳
                        say("もう時間が過ぎたから、書を交換してあげられないわ。");
                        return false;
                    }
                }
                if (0 <= answer && answer < scrolls.length) {
                    // lua の scroll
                    this.scroll = scrolls[answer];
                    if (1 < active) {
                        say(text_active);
                        return false;
                    }
                    if (scroll_today == -1) {
                        say(text_schedule);
                        return false;
                    }
                    this.step = this.step_confirm;
                    // 翻訳
                    askYesNo("本当に#b#z" + this.scroll + "##kを交換する？");
                    return true;
                }
                if (this.category == 2) {
                    return false;
                }
                int pages = (this.category == 0) ? this.weapon_pages.length : this.armor_pages.length;
                if (answer == scrolls.length) {
                    // 次のページ, 最後のページは最初のページへ
                    this.page = (this.page + 1) % pages;
                } else {
                    this.page = 0;
                }
            }
            // 交換する書のメニュー
            this.step = this.step_page;
            int[] scrolls = (this.category == 2) ? this.pet_scrolls : ((this.category == 0) ? this.weapon_pages[this.page] : this.armor_pages[this.page]);
            StringBuilder sb = new StringBuilder();
            // 翻訳
            sb.append("交換する書を選んでね。\r\b#b");
            for (int i = 0; i < scrolls.length; i++) {
                if (0 < i) {
                    sb.append("\r\n");
                }
                sb.append("#L").append(i).append("# #z").append(scrolls[i]).append("##l");
            }
            if (this.category != 2) {
                int pages = (this.category == 0) ? this.weapon_pages.length : this.armor_pages.length;
                if (this.page + 1 < pages) {
                    // 翻訳
                    sb.append("\r\n#k#L").append(scrolls.length).append("# 次のページへ(").append(this.page + 1).append("/").append(pages).append(")#l");
                } else {
                    // 翻訳
                    sb.append("\r\n#k#L").append(scrolls.length).append("# 最初のページへ(").append(pages).append("/").append(pages).append(")#l");
                }
            }
            askMenu(sb.toString());
            return true;
        }

        if (this.step == this.step_confirm) {
            if (1 < active) {
                say(text_active);
                return false;
            }
            if (scroll_today == -1) {
                say(text_schedule);
                return false;
            }
            if (getLastAction() == 0) {
                // 翻訳
                say("もう一度よく考えてから話しかけてね。");
                return false;
            }
            int scroll_changed = 0;
            for (int i = 0; i < this.scroll_change.length; i += 2) {
                if (this.scroll_change[i] == this.scroll) {
                    scroll_changed = this.scroll_change[i + 1];
                }
            }
            if (!exchange(0, this.scroll, -1, scroll_changed, 1)) {
                // 翻訳
                say("交換する書を持っているか、消費インベントリに空きがあるか確認してね。");
                return false;
            }
            String data = getQuestData(this.quest_scroll_count);
            int count = integer(data.substring(scroll_today, scroll_today + 1)) + 1;
            setQuestData(this.quest_scroll_count, data.substring(0, scroll_today) + count + data.substring(scroll_today + 1));
            if (count < this.scroll_count_max) {
                // 翻訳
                say("じゃーん。どう？書に魔法がかかって、成功確率が5%アップしたでしょ？今日のうちにあと#b" + (this.scroll_count_max - count) + " 回#k魔法をかけてあげられるわ。また必要になったら来てね。");
                return false;
            }
            if (getQuestStatus(this.quest_daily_first + scroll_today) == 1) {
                forceCompleteQuest(this.quest_daily_first + scroll_today);
                questEndEffect();
            }
            // lua では最終日 (scroll_today が 4 以上) の場合に別の文章になるが、scroll_today は 0 - 3 のため常にこちらの文章になる
            // 翻訳
            say("じゃーん。どう？書に魔法がかかって、#b成功確率が5%#kアップしたでしょ？疲れちゃったから、もうこれ以上魔法はかけられないわ。次の月曜日にまた来てね。");
            return false;
        }
        return false;
    }
}
