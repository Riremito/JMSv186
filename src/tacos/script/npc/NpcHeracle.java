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
public class NpcHeracle extends NpcScriptMan {

    private static final int MENU_CREATE = 0;
    private static final int MENU_DISBAND = 1;
    private static final int MENU_INCREASE = 2;
    private final int guild_master_rank = 1;
    private int selected = -1;

    @Override
    public boolean action(int status, int answer) {
        boolean in_guild = 0 < getPlayer().getGuildId();
        boolean guild_master = in_guild && getPlayer().getGuildRank() == this.guild_master_rank;
        switch (status) {
            case 0 -> {
                // TODO : ギルドマスターを変更したいです。, ギルドシステムについてもっと詳しく知りたいです。, <シャレニアンの地下水路>について教えてください。
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("さあ、何か手伝おうか？");
                nt.addMenu(MENU_CREATE, "ギルドを作成したいです。");
                nt.addMenu(MENU_INCREASE, "ギルド人数を増やしたいです。");
                nt.addMenu(MENU_DISBAND, "ギルドを解散したいです。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                this.selected = answer;
                // TODO : 英語の文章
                switch (this.selected) {
                    case MENU_CREATE -> {
                        if (in_guild) {
                            say("You may not create a new Guild while you are in one.");
                            return false;
                        }
                        askYesNo("Creating a Guild costs #b5,000,000 mesos#k, are you sure you want to continue?");
                        return true;
                    }
                    case MENU_DISBAND -> {
                        if (!guild_master) {
                            say("You can only disband a Guild if you are the leader of that Guild.");
                            return false;
                        }
                        // No : そうか～よく決めてくれたな。今まで頑張ってきたギルドをなくしてはいけないだろう。
                        askYesNo("ギルドを本当に解散したいのか。これはこれは、困ったな。一度解散しちゃうと、キミのギルドは永遠に削除されてしまうんだぜ。色んなギルド特権ももちろん一緒に消えるんだ。それでも、行うかい？");
                        return true;
                    }
                    case MENU_INCREASE -> {
                        if (!guild_master) {
                            say("You can only increase your Guild's capacity if you are the leader.");
                            return false;
                        }
                        // TODO : 現在のギルドの最大人数, 所持GP, 手数料
                        // ギルドメンバー数を増やしたくて来たのか？うむ、ギルドがずいぶん成長したようだな～既に知っていると思うが、ギルドメンバー数を増やすには我々ギルド本部にまた登録をしなければならない。もちろん手数料もGPを使用する必要があるんだ。ちなみに、ギルドメンバー数は最大200名まで増やすことができる。
                        // No : 手数料が負担になっているのか？時間が経てばGPくらいはすぐに貯まるから、そんなに心配することはない。それでは、また来てくれ。
                        askYesNo("現在ギルドの最大人数は#b20人#kで、#b10人#k増やすために必要な手数料は#bGP 10000#kだ。ちなみに、君のギルドは現在#bGP 14363#kのGPを所持している。どう、ギルドメンバーを増やしたいか？");
                        return true;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case 2 -> {
                switch (this.selected) {
                    case MENU_CREATE -> {
                        // TODO : ギルド作成 (genericGuildMessage(1))
                    }
                    case MENU_DISBAND -> {
                        // TODO : ギルド解散 (disbandGuild)
                    }
                    case MENU_INCREASE -> {
                        // TODO : ギルド人数の増加 (increaseGuildCapacity)
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
