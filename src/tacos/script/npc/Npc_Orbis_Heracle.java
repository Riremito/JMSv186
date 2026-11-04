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

import odin.handling.world.Guild;
import odin.handling.world.guild.MapleGuild;
import tacos.packet.ops.OpsGuild;
import tacos.packet.response.ResCWvsContext;

/**
 *
 * @author Riremito
 */
public class Npc_Orbis_Heracle extends NpcScriptMan {

    private static final int MENU_CREATE = 0;
    private static final int MENU_DISBAND = 1;
    private static final int MENU_INCREASE = 2;
    private final int guild_master_rank = 1;
    private final int create_cost = 5000000; // ReqCUser_Guild の CREATE_GUILD_COST と同じ
    private final int increase_count = 10;
    private final int increase_gp = 10000;
    private int selected = -1;
    private int no_status = -2;

    @Override
    public boolean action(int status, int answer) {
        boolean in_guild = 0 < getPlayer().getGuildId();
        boolean guild_master = in_guild && getPlayer().getGuildRank() == this.guild_master_rank;
        // いいえを選択した
        if (status == this.no_status) {
            switch (this.selected) {
                case MENU_DISBAND -> {
                    // 原文ママ
                    say("そうか～よく決めてくれたな。今まで頑張ってきたギルドをなくしてはいけないだろう。");
                }
                case MENU_INCREASE -> {
                    // 原文ママ
                    say("手数料が負担になっているのか？時間が経てばGPくらいはすぐに貯まるから、そんなに心配することはない。それでは、また来てくれ。");
                }
                default -> {
                }
            }
            return false;
        }
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
                switch (this.selected) {
                    case MENU_CREATE -> {
                        if (in_guild) {
                            // 翻訳
                            say("ギルドに入っている間は、新しいギルドを作ることはできないぞ。");
                            return false;
                        }
                        // 翻訳
                        askYesNo("ギルドを作るには#b" + String.format("%,d", this.create_cost) + "メル#kが必要だ。本当にギルドを作るかい？");
                        return true;
                    }
                    case MENU_DISBAND -> {
                        if (!guild_master) {
                            // 翻訳
                            say("おい、キミはギルドマスターじゃないだろう！これはギルドマスターにしか決められないんだ。");
                            return false;
                        }
                        this.no_status = status - 1;
                        // 原文ママ
                        askYesNo("ギルドを本当に解散したいのか。これはこれは、困ったな。一度解散しちゃうと、キミのギルドは永遠に削除されてしまうんだぜ。色んなギルド特権ももちろん一緒に消えるんだ。それでも、行うかい？");
                        return true;
                    }
                    case MENU_INCREASE -> {
                        if (!guild_master) {
                            // 翻訳
                            say("おい、キミはギルドマスターじゃないだろう！これはギルドマスターにしか決められないんだ。");
                            return false;
                        }
                        MapleGuild guild = getPlayer().getWorld().getGuild().getGuild(getPlayer().getGuildId());
                        if (guild == null) {
                            return false;
                        }
                        // 原文ママ
                        say("ギルドメンバー数を増やしたくて来たのか？うむ、ギルドがずいぶん成長したようだな～既に知っていると思うが、ギルドメンバー数を増やすには我々ギルド本部にまた登録をしなければならない。もちろん手数料もGPを使用する必要があるんだ。ちなみに、ギルドメンバー数は最大200名まで増やすことができる。", false, true);
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
                        // ギルド名の入力 (ReqCUser_Guild で作成される)
                        getPlayer().SendPacket(ResCWvsContext.GuildResult(OpsGuild.GuildReq_InputGuildName));
                        return false;
                    }
                    case MENU_DISBAND -> {
                        if (!guild_master) {
                            return false;
                        }
                        getPlayer().getWorld().getGuild().disbandGuild(getPlayer().getGuildId());
                        return false;
                    }
                    case MENU_INCREASE -> {
                        MapleGuild guild = getPlayer().getWorld().getGuild().getGuild(getPlayer().getGuildId());
                        if (guild == null) {
                            return false;
                        }
                        this.no_status = status - 1;
                        // 原文ママ, 数値のみ変更
                        askYesNo("現在ギルドの最大人数は#b" + guild.getCapacity() + "人#kで、#b" + this.increase_count + "人#k増やすために必要な手数料は#bGP " + this.increase_gp + "#kだ。ちなみに、君のギルドは現在#bGP " + guild.getGP() + "#kのGPを所持している。どう、ギルドメンバーを増やしたいか？");
                        return true;
                    }
                    default -> {
                    }
                }
                return false;
            }
            case 3 -> {
                if (this.selected != MENU_INCREASE || !guild_master) {
                    return false;
                }
                Guild world_guild = getPlayer().getWorld().getGuild();
                int guild_id = getPlayer().getGuildId();
                if (world_guild.getGP(guild_id) < this.increase_gp) {
                    // 原文ママ
                    say("手数料が負担になっているのか？時間が経てばGPくらいはすぐに貯まるから、そんなに心配することはない。それでは、また来てくれ。");
                    return false;
                }
                // TODO : サーバー側は5人ずつ最大100人まで (JMSは最大200人)
                boolean increased = false;
                for (int i = 0; i < this.increase_count / 5; i++) {
                    if (world_guild.increaseGuildCapacity(guild_id)) {
                        increased = true;
                    }
                }
                if (!increased) {
                    // 翻訳
                    say("キミのギルドはずいぶん大きくなったようだな。これ以上ギルドメンバー数を増やすことはできないぞ。");
                    return false;
                }
                world_guild.gainGP(guild_id, -this.increase_gp);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
