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
package tacos.config;

import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public enum Content {
    Wz_SingleFile(false),
    Job_Pirate, // JMS147
    Job_KnightsOfCygnus, // JMS165
    Job_Aran, // JMS173
    Job_Evan, // JMS183
    Job_DualBlade, // JMS183
    Job_Resistance, // JMS187
    BIGBANG(false), // JMS187
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
    // settings for specific versions
    CharacterNameLength(13),
    PacketHeaderSize(2),
    OldIV(false),
    CustomEncryption(false),
    PrePotential(false), // JMS184-185, KMS95
    PetNameLength(13),
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

    public static void showContentList() {
        for (final Content content : Content.values()) {
            DebugLogger.InfoLog(content.toString() + " : " + content.get());
        }
    }

    public static void init() {
        boolean bOK = false;
        // JMS307
        if (bOK
                || Config.GreaterOrEqual(Region.JMS, 308)) {
            Update_Tempest.set(true);
            bOK = true;
        }
        // JMS300
        if (bOK
                || Config.GreaterOrEqual(Region.JMS, 302)) {
            Update_Sengoku.set(true);
            bOK = true;
        }
        // JMS200
        if (bOK
                || Config.GreaterOrEqual(Region.JMS, 200)
                || Config.GreaterOrEqual(Region.JMST, 110)) {
            Update_Renaissance.set(true);
            bOK = true;
        }
        // JMS187
        bOK = checkBigBang();
        Wz_SingleFile.set(checkWzSingleFile());
        BIGBANG.set(bOK);
        OldIV.set(checkOldIV());
        PacketHeaderSize.setInt(checkPacketHeaderSize());
        CustomEncryption.set(checkCustomEncryption());
        PrePotential.set(checkPrePotential());
        CharacterNameLength.setInt(checkCharacterNameLength());
        // Pre-BB
        // JMS183 (JMS180)
        if (bOK
                || Config.GreaterOrEqual(Region.JMS, 183)) {
            Job_DualBlade.set(true);
            bOK = true;
        }
        if (bOK
                || Config.GreaterOrEqual(Region.JMS, 183)) {
            Job_Evan.set(true);
            bOK = true;
        }
        if (bOK
                || Config.GreaterOrEqual(Region.JMS, 173)) {
            Job_Aran.set(true);
            bOK = true;
        }
        // JMS165
        if (bOK
                || Config.GreaterOrEqual(Region.JMS, 165)) {
            Job_KnightsOfCygnus.set(true);
            bOK = true;
        }
        // JMS147
        if (bOK
                || Config.GreaterOrEqual(Region.JMS, 147)) {
            Job_Pirate.set(true);
            bOK = true;
        }

    }

    private static boolean checkWzSingleFile() {
        if (Region.KMSB.check()) {
            return true;
        }
        if (Config.LessOrEqual(Region.KMS, 3)) {
            return true;
        }
        return false;
    }

    // BIGBANG
    private static boolean checkBigBang() {
        if (Config.GreaterOrEqual(Region.KMS, 101)) {
            return true;
        }
        if (Config.GreaterOrEqual(Region.JMS, 187)) {
            return true;
        }
        if (Config.GreaterOrEqual(Region.CMS, 87)) {
            return true;
        }
        if (Config.GreaterOrEqual(Region.TWMS, 123)) {
            return true;
        }
        if (Config.GreaterOrEqual(Region.THMS, 90)) {
            return true;
        }
        if (Config.GreaterOrEqual(Region.MSEA, 105)) {
            return true;
        }
        if (Config.GreaterOrEqual(Region.GMS, 93)) {
            return true;
        }
        if (Config.GreaterOrEqual(Region.EMS, 73)) {
            return true;
        }
        if (Region.IMS.check()) {
            return true;
        }
        // Test Server
        if (Config.GreaterOrEqual(Region.KMST, 317)) {
            return true;
        }
        if (Config.GreaterOrEqual(Region.JMST, 110)) {
            return true;
        }
        if (Config.GreaterOrEqual(Region.GMST, 2)) {
            return true;
        }
        // no BB
        if (Region.BMS.check()) {
            return false;
        }
        if (Region.VMS.check()) {
            return false;
        }
        return false;
    }

    private static int checkCharacterNameLength() {
        if (Config.GreaterOrEqual(Region.TWMS, 94)) {
            return 15;
        }
        if (Region.VMS.check()) {
            return 16;
        }
        return 13;
    }

    private static int checkPacketHeaderSize() {
        if (Region.KMSB.check()) {
            return 1;
        }
        if (Config.LessOrEqual(Region.KMS, 55)) {
            return 1;
        }
        return 2;
    }

    private static boolean checkOldIV() {
        if (Config.LessOrEqual(Region.JMS, 141)) {
            return true;
        }
        return false;
    }

    private static boolean checkCustomEncryption() {
        if (Config.LessOrEqual(Region.CMS, 85)) { // not checked
            return true;
        }
        if (Region.THMS.check()) {
            return true;
        }
        if (Region.MSEA.check()) {
            return true;
        }
        if (Region.VMS.check()) {
            return true;
        }
        if (Region.GMS.check() || Region.GMST.check()) {
            return true;
        }
        if (Region.EMS.check()) {
            return true;
        }
        if (Region.BMS.check()) {
            return true;
        }
        return false;
    }

    private static boolean checkPrePotential() {
        // JMS186 or later has different equip data format
        if (Config.Between(Region.JMS, 184, 185)) {
            return true;
        }
        if (Config.Equal(Region.KMS, 95)) {
            return true;
        }

        return false;
    }

}
