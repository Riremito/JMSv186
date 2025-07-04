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

import debug.Debug;

/**
 *
 * @author Riremito
 */
public enum Content {
    Packet_Encryption_Custom(false),
    Packet_HeaderSize(2),
    Packet_Character_NameSize(13),
    Packet_Pet_NameSize(13),
    Job_Pirate, // JMS147
    Job_KnightsOfCygnus, // JMS165
    Job_Aran, // JMS173
    Job_Evan, // JMS183
    Job_DualBlade, // JMS183
    Job_Resistance, // JMS187
    Update_BigBang, // JMS187
    Update_Renaissance, // JMS200
    Update_Sengoku, // JMS302 (JMS300)
    Update_Tempest, // JMS308 (JMS307)
    // internal data
    PacketData_Inventory_SlotSize_Equip(2),
    PacketData_Inventory_SlotSize(2),
    PacketData_Equip_ViciousHammer,
    PacketData_Equip_Duability,
    PacketData_Equip_Star,
    PacketData_Equip_Potential,
    PacketData_Equip_SoulBall,
    PacketData_Equip_Additional_Potential,
    PacketData_Equip_Anvil,
    // bad version
    PacketData_Pre_Potential(false), // JMS184-185, KMS95
    UNKNOWN;

    int value;

    Content(boolean val) {
        set(val);
    }

    Content(int val) {
        setInt(val);
    }

    Content() {
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

    public static void check() {
        Debug.InfoLog("----- Content List -----");
        for (final Content content : Content.values()) {
            Debug.InfoLog(content.toString() + " : " + content.get());
        }
        Debug.InfoLog("------------------------");
    }

    public static void init() {
        boolean bOK = false;
        // JMS307
        if (bOK
                || Version.GreaterOrEqual(Region.JMS, 308)) {
            Update_Tempest.set(true);
            bOK = true;
        }
        // JMS300
        if (bOK
                || Version.GreaterOrEqual(Region.JMS, 302)) {
            Update_Sengoku.set(true);
            bOK = true;
        }
        // JMS200
        if (bOK
                || Version.GreaterOrEqual(Region.JMS, 200)
                || Version.GreaterOrEqual(Region.JMST, 110)) {
            Update_Renaissance.set(true);
            bOK = true;
        }
        // VMS, BMS are closed before Bigbang, IMS are lanched after Bigbang.
        if (bOK
                || Version.GreaterOrEqual(Region.KMS, 101)
                || Version.GreaterOrEqual(Region.JMS, 187)
                || Version.GreaterOrEqual(Region.CMS, 87)
                || Version.GreaterOrEqual(Region.TWMS, 123)
                || Version.GreaterOrEqual(Region.MSEA, 105)
                || Version.GreaterOrEqual(Region.THMS, 90)
                || Version.GreaterOrEqual(Region.GMS, 93)
                || Version.GreaterOrEqual(Region.EMS, 73)
                || Version.GreaterOrEqual(Region.IMS, 1)) {
            Update_BigBang.set(true);
            bOK = true;
        }
        // JMS187
        if (bOK
                || Version.GreaterOrEqual(Region.JMS, 187)) {
            Job_Resistance.set(true); // Wild Hunter
            bOK = true;
        }
        // Pre-BB
        // JMS183 (JMS180)
        if (bOK
                || Version.GreaterOrEqual(Region.JMS, 183)) {
            Job_DualBlade.set(true);
            bOK = true;
        }
        if (bOK
                || Version.GreaterOrEqual(Region.JMS, 183)) {
            Job_Evan.set(true);
            bOK = true;
        }
        if (bOK
                || Version.GreaterOrEqual(Region.JMS, 173)) {
            Job_Aran.set(true);
            bOK = true;
        }
        // JMS165
        if (bOK
                || Version.GreaterOrEqual(Region.JMS, 165)) {
            Job_KnightsOfCygnus.set(true);
            bOK = true;
        }
        // JMS147
        if (bOK
                || Version.GreaterOrEqual(Region.JMS, 147)) {
            Job_Pirate.set(true);
            bOK = true;
        }

    }
}
