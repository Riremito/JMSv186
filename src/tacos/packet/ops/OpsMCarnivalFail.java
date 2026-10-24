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
package tacos.packet.ops;

/**
 *
 * @author Riremito
 */
public enum OpsMCarnivalFail implements IPacketOps {
    // GMS v95 and JMS v186, CField_MonsterCarnival::OnRequestResult
    MCarnivalFail_NoCP(1), // CPが足りないため、実行できません。
    MCarnivalFail_MobLimit(2), // これ以上モンスターの召喚ができません。
    MCarnivalFail_GuardianLimit(3), // これ以上の守護物の召喚ができません。
    MCarnivalFail_AlreadyGuardian(4), // 既に召喚されている守護物です。
    MCarnivalFail_Unknown(5), // 原因不明のエラーで要請に失敗しました。
    UNKNOWN;

    private int value;

    OpsMCarnivalFail(int val) {
        this.value = val;
    }

    OpsMCarnivalFail() {
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

    public static OpsMCarnivalFail find(int val) {
        for (OpsMCarnivalFail ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
