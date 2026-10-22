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
package tacos.packet.ops;

/**
 *
 * @author Riremito
 */
// LP_JMS_PachinkoPlay type (JMS only, not in GMS95)
public enum OpsPachinkoPlay implements IPacketOps {
    // added for JMS
    PachinkoPlay_Start(0), // 开始打豆豆
    PachinkoPlay_HoleCount(3), // 颜色求进洞
    PachinkoPlay_Spin(4), // 进洞旋转
    PachinkoPlay_Reward(5), // 奖励豆豆效果
    PachinkoPlay_Unknown(6), // 未知效果
    PachinkoPlay_GoldenDog(7), // 黄金狗
    PachinkoPlay_RewardB(8), // 奖励豆豆效果B
    PachinkoPlay_RewardItem(9), // 领奖NPC
    UNKNOWN;

    private int value;

    OpsPachinkoPlay(int val) {
        this.value = val;
    }

    OpsPachinkoPlay() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int val) {
        this.value = val;
    }

    public static OpsPachinkoPlay find(int val) {
        for (OpsPachinkoPlay ops : OpsPachinkoPlay.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
    }
}
