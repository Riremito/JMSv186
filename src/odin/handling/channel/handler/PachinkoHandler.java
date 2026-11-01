/*
 * Copyright (C) 2025 Riremito
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
package odin.handling.channel.handler;

import java.util.ArrayList;
import java.util.List;
import odin.server.Randomizer;
import tacos.client.TacosCharacter;
import tacos.packet.ClientPacket;
import tacos.packet.response.Res_JMS_CField_Pachinko;
import tacos.property.Property_Pachinko;
import tacos.shared.TacosSharedExpTable;

/**
 * JMS pachinko (パチンコ), based on the CMS v72 source:
 * https://github.com/huangshushu/Maplrstory072-cn-/blob/master/src/handling/channel/handler/BeanGame.java
 *
 * @author Riremito
 */
public class PachinkoHandler {

    private static final int MAX_HOLE_COUNT = 7;
    private static final int OCEAN_HAT_ITEM_ID = 1002743; // 海の帽子
    private static final int PACHINKO_MAP_ID = 809030000;
    private static final int QUEEN = 9; // 女王

    // the symbols of one line: row1, row3 and the row2 symbol of a hit
    private static final class Symbol {

        public final int row1;
        public final int row3;
        public final int row2;

        public Symbol(int row1, int row3, int row2) {
            this.row1 = row1;
            this.row3 = row3;
            this.row2 = row2;
        }

        public boolean matches(State state) {
            return state.row1 == row1 && state.row3 == row3 && state.row2 == row2;
        }

        // rates : [0], [1] = chance to show this symbol, [2], [3] = chance of a hit
        public String[] rates() {
            switch (row2) {
                case 0:
                    return Property_Pachinko.big_white_mob_rates;
                case 1:
                    return Property_Pachinko.purple_mob_rates;
                case 3:
                    return Property_Pachinko.pink_mob_rates;
                case 5:
                    return Property_Pachinko.small_white_mob_rates;
                case 2:
                    return Property_Pachinko.thief_rates;
                case 4:
                    return Property_Pachinko.pirate_rates;
                case 6:
                    return Property_Pachinko.magician_rates;
                case 7:
                    return Property_Pachinko.warrior_rates;
                case 8:
                    return Property_Pachinko.archer_rates;
                default:
                    return Property_Pachinko.queen_rates;
            }
        }
    }

    // checked in this order, the queen last
    private static final Symbol[] SYMBOLS = {
        new Symbol(0, 0, 0), // 白いモンスター (大)
        new Symbol(9, 7, 1), // 紫のモンスター
        new Symbol(1, 2, 3), // ピンクのモンスター
        new Symbol(5, 1, 5), // 白いモンスター (小)
        new Symbol(4, 9, 2), // 盗賊
        new Symbol(7, 5, 4), // 海賊
        new Symbol(2, 4, 6), // 魔法使い
        new Symbol(8, 8, 7), // 戦士
        new Symbol(6, 3, 8), // 弓使い
        new Symbol(3, 6, QUEEN), // 女王
    };

    // per character state
    public static class State {

        private int holeCount = 0; // 入賞回数 (最大 7)
        private int row1 = 0;
        private int row2 = 0;
        private int row3 = 0;
        private int mobEffect = 0; // モンスターの演出
        private int winRate = 0; // 当選率
        private int fastSpin = 0; // 高速回転
        private boolean hideEffectA = false; // false = 演出アニメーションを表示
        private boolean hideEffectB = false;
        private int blue = 0;
        private int green = 0;
        private int red = 0;
        private boolean goldenDog = false; // 黄金の犬
        private int power = 0; // 発射の強さ
        private int beanNumber = 0; // 玉の番号
        private boolean canSetBeanNumber = false;
    }

    public static class Bean {

        private final int number;
        private final int type;
        private final int pos;

        public Bean(int pos, int type, int number) {
            this.pos = pos;
            this.number = number;
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public int getNumber() {
            return number;
        }

        public int getPos() {
            return pos;
        }
    }

    private static int rate(String[] rates, int index) {
        return Integer.parseInt(rates[index]);
    }

    // Randomizer.nextInt(rates[i]) > Randomizer.nextInt(rates[j])
    private static boolean roll(String[] rates, int i, int j) {
        return Randomizer.nextInt(rate(rates, i)) > Randomizer.nextInt(rate(rates, j));
    }

    private static int pick(String[] item_ids) {
        return Integer.parseInt(item_ids[Randomizer.nextInt(item_ids.length)]);
    }

    private static int randomPower() {
        return (Property_Pachinko.power_random > 0) ? Randomizer.nextInt(Property_Pachinko.power_random) : 0;
    }

    // BEANS_OPERATION
    public static void OnPachinko(TacosCharacter chr, ClientPacket cp) {
        State state = chr.getPachinko();
        int type = cp.Decode1();
        switch (type) {
            // 0 : 開始, 1 : 開始時の発射の強さ
            case 0:
            case 1: {
                state.power = cp.Decode2() + randomPower();
                chr.updateStat();
                break;
            }
            // 2 : 処理不要 (02 1B 00 00 00)
            case 2: {
                break;
            }
            // 玉が入賞口に入った
            case 3: {
                state.holeCount = Math.min(state.holeCount + 1, MAX_HOLE_COUNT);
                chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_HoleCount(state.holeCount, state.fastSpin, state.blue, state.green, state.red));
                break;
            }
            // 入賞回数を表す黄色の玉 (最大 7 個) を使って回転する
            case 4: {
                state.holeCount--;
                spin(state);
                chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_Spin(state.holeCount, state.row1, state.row3, state.row2, state.mobEffect, state.winRate, state.fastSpin, state.hideEffectA, state.hideEffectB));
                if (state.row2 != QUEEN) {
                    state.goldenDog = !state.goldenDog && Randomizer.nextInt(rate(Property_Pachinko.golden_dog_rates, 0)) == rate(Property_Pachinko.golden_dog_rates, 1);
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_GoldenDog((byte) (state.goldenDog ? 1 : 0)));
                }
                break;
            }
            // 回転後に 3 列が揃った
            case 5: {
                reward(chr, state);
                break;
            }
            case 7: {
                if (state.goldenDog) {
                    state.goldenDog = false;
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_GoldenDog((byte) 0));
                }
                chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_Reward(true, 0));
                break;
            }
            // start/stop を押したときの発射の強さと玉の番号
            case 11: {
                state.power = cp.Decode2() + randomPower();
                state.beanNumber = cp.Decode4() + 1; // 最後の玉の番号
                if (state.beanNumber == 1) {
                    state.canSetBeanNumber = false;
                }
                sendBeans(chr, state);
                break;
            }
            // 一時停止、または玉が 5 個たまったときにクライアントが送る玉の情報 (最大 5 個)
            case 6: {
                cp.Decode1();
                int count = cp.Decode1();
                if (count == 0) {
                    return;
                }
                if (state.canSetBeanNumber) {
                    state.beanNumber += count;
                }
                chr.gainTama(-count);
                state.canSetBeanNumber = true;
                sendBeans(chr, state);
                break;
            }
            default: {
                break;
            }
        }
    }

    private static void sendBeans(TacosCharacter chr, State state) {
        List<Bean> beans = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            beans.add(new Bean(state.power + Randomizer.rand(-100, 100), 0, state.beanNumber + i));
        }
        chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_Start(beans));
    }

    private static void spin(State state) {
        boolean forced_queen = state.goldenDog;
        for (Symbol s : SYMBOLS) {
            String[] rates = s.rates();
            boolean show = (s.row2 == QUEEN) ? (forced_queen || Randomizer.nextInt(rate(rates, 0)) > rate(rates, 1)) : (!forced_queen && Randomizer.nextInt(rate(rates, 0)) > rate(rates, 1));
            if (!show) {
                continue;
            }
            state.row1 = s.row1;
            state.row3 = s.row3;
            boolean hit;
            if (s.row2 == QUEEN && state.goldenDog) {
                hit = true;
                state.goldenDog = false;
            } else {
                hit = Randomizer.nextInt(rate(rates, 2)) > rate(rates, 3);
            }
            if (hit) {
                state.row2 = s.row2;
            }
            state.winRate = hit ? 100 : 0;
            state.mobEffect = 1;
            state.hideEffectA = (s.row2 != QUEEN);
            state.hideEffectB = (s.row2 != QUEEN);
            return;
        }
        // no symbol, random rows that do not line up
        state.row1 = Randomizer.nextInt(10);
        switch (state.row1) {
            case 0:
                state.row3 = Randomizer.nextInt(9) + 1;
                break;
            case 1:
                state.row3 = Randomizer.nextInt(7) + 3;
                break;
            case 2:
                state.row3 = Randomizer.nextInt(5) + 5;
                break;
            case 3:
                state.row3 = Randomizer.nextInt(3) + 7;
                break;
            case 4:
                state.row3 = Randomizer.nextInt(9);
                break;
            case 5:
                state.row3 = Randomizer.nextInt(8) + 2;
                break;
            case 6:
                state.row3 = Randomizer.nextInt(6) + 4;
                break;
            case 7:
                state.row3 = Randomizer.nextInt(4) + 6;
                break;
            case 8:
                state.row3 = Randomizer.nextInt(8);
                break;
            default:
                state.row3 = Randomizer.nextInt(7);
                break;
        }
        state.row2 = Randomizer.nextInt(10);
        state.mobEffect = 0;
        state.winRate = 0;
        state.fastSpin = 0;
        state.hideEffectA = false;
        state.hideEffectB = false;
    }

    private static void reward(TacosCharacter chr, State state) {
        boolean hit = false;
        for (Symbol s : SYMBOLS) {
            hit |= s.matches(state);
        }
        if (!hit) {
            return;
        }
        int exp = TacosSharedExpTable.getExpNeededForLevel(chr.getLevel() + 1) / 200;
        switch (state.row2) {
            // 白いモンスター (小, 大)
            case 5:
            case 0: {
                String[] r = Property_Pachinko.white_mob_reward_rates;
                if (roll(r, 0, 1)) {
                    gainExp(chr, exp);
                } else if (roll(r, 2, 3)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.consume_items)));
                }
                break;
            }
            // 紫, ピンクのモンスター
            case 1:
            case 3: {
                String[] r = Property_Pachinko.color_mob_reward_rates;
                if (roll(r, 0, 1)) {
                    gainExp(chr, exp);
                } else if (roll(r, 4, 5)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.equip_items)));
                } else if (roll(r, 2, 3)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.consume_items)));
                }
                break;
            }
            // 盗賊, 海賊, 魔法使い, 戦士, 弓使い
            case 2:
            case 4:
            case 6:
            case 7:
            case 8: {
                String[] r = Property_Pachinko.job_reward_rates;
                if (roll(r, 0, 1)) {
                    gainExp(chr, exp);
                } else if (roll(r, 4, 5)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.equip_items)));
                } else if (roll(r, 6, 7)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.mount_items)));
                } else if (roll(r, 2, 3)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.consume_items)));
                }
                break;
            }
            // 女王
            case QUEEN: {
                String[] r = Property_Pachinko.queen_reward_rates;
                if (roll(r, 4, 5)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.equip_items)));
                } else if (Randomizer.nextInt(100) + 1 == Property_Pachinko.ocean_hat_rate) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(OCEAN_HAT_ITEM_ID));
                } else if (roll(r, 6, 7)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.mount_items)));
                } else if (roll(r, 2, 3)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.consume_items)));
                }
                break;
            }
            default: {
                break;
            }
        }
        int tama = (state.row2 + 1) * Property_Pachinko.tama_reward_unit;
        chr.gainTama(tama);
        chr.Notice("パチンコで玉を" + tama + "個獲得しました！");
        if (chr.getMapId() == PACHINKO_MAP_ID) {
            chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoMessage(1, 1, "おめでとうございます！当たりで玉を" + tama + "個獲得しました！"));
        }
        if (state.goldenDog && state.row2 == QUEEN) {
            state.goldenDog = false;
            chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_GoldenDog((byte) 0));
        }
        chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_Reward(true, tama));
    }

    private static void gainExp(TacosCharacter chr, int exp) {
        chr.gainExp(exp, false, false, false);
        chr.Notice("パチンコで経験値を" + exp + "獲得しました！");
    }
}
