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
 * JMS pachinko (豆豆机), based on the CMS v72 source:
 * https://github.com/huangshushu/Maplrstory072-cn-/blob/master/src/handling/channel/handler/BeanGame.java
 *
 * @author Riremito
 */
public class PachinkoHandler {

    private static final int MAX_HOLE_COUNT = 7;
    private static final int OCEAN_HAT_ITEM_ID = 1002743; // 海洋帽子
    private static final int PACHINKO_MAP_ID = 809030000;
    private static final int QUEEN = 9; // 女皇

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
                    return Property_Pachinko.大白怪;
                case 1:
                    return Property_Pachinko.紫色怪;
                case 3:
                    return Property_Pachinko.粉色怪;
                case 5:
                    return Property_Pachinko.小白怪;
                case 2:
                    return Property_Pachinko.飞侠;
                case 4:
                    return Property_Pachinko.海盗;
                case 6:
                    return Property_Pachinko.法师;
                case 7:
                    return Property_Pachinko.战士;
                case 8:
                    return Property_Pachinko.弓箭手;
                default:
                    return Property_Pachinko.女皇;
            }
        }
    }

    // checked in this order, the queen last
    private static final Symbol[] SYMBOLS = {
        new Symbol(0, 0, 0), // 大白怪
        new Symbol(9, 7, 1), // 紫色怪
        new Symbol(1, 2, 3), // 粉色怪
        new Symbol(5, 1, 5), // 小白怪
        new Symbol(4, 9, 2), // 飞侠
        new Symbol(7, 5, 4), // 海盗
        new Symbol(2, 4, 6), // 法师
        new Symbol(8, 8, 7), // 战士
        new Symbol(6, 3, 8), // 弓箭手
        new Symbol(3, 6, QUEEN), // 女皇
    };

    // per character state
    public static class State {

        private int holeCount = 0; // 进洞次数
        private int row1 = 0;
        private int row2 = 0;
        private int row3 = 0;
        private int mobEffect = 0; // 启动打怪效果
        private int winRate = 0; // 中奖率
        private int fastSpin = 0; // 加速旋转
        private boolean hideEffectA = false; // false = 启动动画效果
        private boolean hideEffectB = false;
        private int blue = 0;
        private int green = 0;
        private int red = 0;
        private boolean goldenDog = false; // 黄金狗
        private int power = 0; // 力度
        private int beanNumber = 0; // 豆豆序号
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
        return (Property_Pachinko.力度搞假 > 0) ? Randomizer.nextInt(Property_Pachinko.力度搞假) : 0;
    }

    // BEANS_OPERATION
    public static void OnPachinko(TacosCharacter chr, ClientPacket cp) {
        State state = chr.getPachinko();
        int type = cp.Decode1();
        switch (type) {
            // 0 : 开始打豆豆, 1 : 点开始的时候 确认打豆豆的力度
            case 0:
            case 1: {
                state.power = cp.Decode2() + randomPower();
                chr.updateStat();
                break;
            }
            // 2 : 没存在的必要 (02 1B 00 00 00)
            case 2: {
                break;
            }
            // 打豆豆进洞以后的数据
            case 3: {
                state.holeCount = Math.min(state.holeCount + 1, MAX_HOLE_COUNT);
                chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_HoleCount(state.holeCount, state.fastSpin, state.blue, state.green, state.red));
                break;
            }
            // 记录进洞次数的黄色豆豆。最多只有7个。
            case 4: {
                state.holeCount--;
                spin(state);
                chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_Spin(state.holeCount, state.row1, state.row3, state.row2, state.mobEffect, state.winRate, state.fastSpin, state.hideEffectA, state.hideEffectB));
                if (state.row2 != QUEEN) {
                    state.goldenDog = !state.goldenDog && Randomizer.nextInt(rate(Property_Pachinko.黄金狗几率, 0)) == rate(Property_Pachinko.黄金狗几率, 1);
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_GoldenDog((byte) (state.goldenDog ? 1 : 0)));
                }
                break;
            }
            // 移动以后三排一样的
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
            // 点start/stop的时候获得start/stop时豆豆的力度和序号
            case 11: {
                state.power = cp.Decode2() + randomPower();
                state.beanNumber = cp.Decode4() + 1; // 最后一个豆豆的序号
                if (state.beanNumber == 1) {
                    state.canSetBeanNumber = false;
                }
                sendBeans(chr, state);
                break;
            }
            // 点暂停或者满5个豆豆后客户端发送的豆豆信息 最多5个豆豆
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
            // 小白怪, 大白怪
            case 5:
            case 0: {
                String[] r = Property_Pachinko.白怪奖励;
                if (roll(r, 0, 1)) {
                    gainExp(chr, exp);
                } else if (roll(r, 2, 3)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.消耗品)));
                }
                break;
            }
            // 紫色怪, 粉色怪
            case 1:
            case 3: {
                String[] r = Property_Pachinko.色怪奖励;
                if (roll(r, 0, 1)) {
                    gainExp(chr, exp);
                } else if (roll(r, 4, 5)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.豆豆装备)));
                } else if (roll(r, 2, 3)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.消耗品)));
                }
                break;
            }
            // 飞侠, 海盗, 法师, 战士, 弓箭手
            case 2:
            case 4:
            case 6:
            case 7:
            case 8: {
                String[] r = Property_Pachinko.五职业奖励;
                if (roll(r, 0, 1)) {
                    gainExp(chr, exp);
                } else if (roll(r, 4, 5)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.豆豆装备)));
                } else if (roll(r, 6, 7)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.豆豆坐骑)));
                } else if (roll(r, 2, 3)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.消耗品)));
                }
                break;
            }
            // 女皇
            case QUEEN: {
                String[] r = Property_Pachinko.女皇奖励;
                if (roll(r, 4, 5)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.豆豆装备)));
                } else if (Randomizer.nextInt(100) + 1 == Property_Pachinko.海洋帽子几率) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(OCEAN_HAT_ITEM_ID));
                } else if (roll(r, 6, 7)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.豆豆坐骑)));
                } else if (roll(r, 2, 3)) {
                    chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_RewardItem(pick(Property_Pachinko.消耗品)));
                }
                break;
            }
            default: {
                break;
            }
        }
        int tama = (state.row2 + 1) * Property_Pachinko.豆豆奖励范围;
        chr.gainTama(tama);
        chr.Notice("在豆豆机中获得: " + tama + "个豆豆！");
        if (chr.getMapId() == PACHINKO_MAP_ID) {
            chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoMessage(1, 1, "恭喜你打豆豆成功中奖！当前中奖获得豆豆：" + tama + "个！"));
        }
        if (state.goldenDog && state.row2 == QUEEN) {
            state.goldenDog = false;
            chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_GoldenDog((byte) 0));
        }
        chr.SendPacket(Res_JMS_CField_Pachinko.PachinkoPlay_Reward(true, tama));
    }

    private static void gainExp(TacosCharacter chr, int exp) {
        chr.gainExp(exp, false, false, false);
        chr.Notice("在豆豆机中获得：" + exp + "经验值！");
    }
}
