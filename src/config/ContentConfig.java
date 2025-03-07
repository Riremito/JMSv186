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
public enum ContentConfig {
    CC_Start,
    // region
    CC_KMS, // 1
    CC_KMST, // 2
    CC_JMS(true), // 3
    CC_CMS, // 4
    CC_TWMS, // 6
    CC_MSEA, // 7
    CC_THMS, // 7
    CC_VMS, // 7
    CC_GMS, // 8
    CC_EMS, // 9
    CC_BMS, // 9
    // packet
    CC_Packet_HeaderSize(2),
    CC_Packet_Encryption(true),
    CC_Packet_Encryption_Custom(false),
    // localized
    CC_Character_NameSize(13),
    CC_Pet_NameSize(13),
    // update
    CC_Update_BigBang(false),
    CC_Update_Renaissance(false),
    CC_Update_Sengoku(false),
    // internal data
    CC_Inventory_SlotSize_Equip(2),
    CC_Inventory_SlotSize_Others(2),
    CC_Inventory_Equip_PrePotential(false),
    CC_Inventory_Pet_Test(false),
    // job
    CC_Job_Pirate(true),
    CC_Job_KnightsOfCygnus(true),
    CC_Job_DualBlade(true),
    CC_Job_WildHunter(false),
    CC_End;

    int value;

    ContentConfig(boolean val) {
        set(val);
    }

    ContentConfig(int val) {
        setInt(val);
    }

    ContentConfig() {
        value = 0;
    }

    public boolean get() {
        return (value != 0);
    }

    private void set(boolean val) {
        this.value = val ? 1 : 0;
    }

    public int getInt() {
        return value;
    }

    private void setInt(int val) {
        this.value = val;
    }

    public static void initContentConfig() {
        CC_Packet_Encryption.set(true);
    }
}
