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
package config;

/**
 *
 * @author Riremito
 */
public enum ContentState {
    CS_NETCAFE(false), // ネットカフェ
    CS_HIDE_EVENT_MOB(true),
    CS_HIDE_EVENT_NPC(true),
    CS_HIDE_MAPLE_TV(true),
    CS_LOCK_HP_WASH, // HP振り替え
    CS_LOCK_HAMMER, // ビシャスのハンマー
    CS_LOCK_EE_SCROLL, // 装備強化
    CS_LOCK_POTENTIAL, // 潜在能力
    CS_LOCK_BOOM, // 書失敗時の装備破壊
    CS_LOCK_LOSING_UPGRADE_SLOT, // 書失敗時のUG数減少
    CS_LOCK_LOSING_THRWOING, // 手裏剣消費
    CS_LOCK_LOSING_STONE, // 召喚石消費
    UNKNOWN;

    int value;

    ContentState(boolean val) {
        set(val);
    }

    ContentState(int val) {
        setInt(val);
    }

    ContentState() {
        this.value = 0;
    }

    public boolean get() {
        return (this.value != 0);
    }

    public void set(boolean val) {
        this.value = val ? 1 : 0;
    }

    public int getInt() {
        return this.value;
    }

    public void setInt(int val) {
        this.value = val;
    }
}
