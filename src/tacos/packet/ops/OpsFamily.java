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
public enum OpsFamily implements IPacketOps {
    // GMS v95, same values in JMS v186 (CWvsContext::OnFamilyResult).
    FamilyRes_Success(0x0),
    FamilyRes_Success_Unregister(0x1), // You have severed ties with %s.
    FamilyRes_Fail_CanNotRegister(0x40), // You cannot add this character as a Junior.
    FamilyRes_Fail_WrongName(0x41), // The name you entered is incorrect or he/she is currently not logged in.
    FamilyRes_Fail_SameFamily(0x42), // You belong to the same family.
    FamilyRes_Fail_NotSameFamily(0x43), // You do not belong to the same family.
    FamilyRes_Fail_DifferentFamily(0x44),
    FamilyRes_Fail_NotSameMap(0x45), // The character you wish to add as a Junior must be in the same map.
    FamilyRes_Fail_AleadyHasParent(0x46), // This character is already a Junior of another character.
    FamilyRes_Fail_OverLevel(0x47), // The Junior you wish to add must be at a lower rank.
    FamilyRes_Fail_UnderLevel(0x48), // The gap between you and your junior must be within 20 levels.
    FamilyRes_Fail_AlreadyJoining(0x49), // Another character has requested to add this character.
    FamilyRes_Fail_AlreadySummon(0x4A), // Another character has requested a summon.
    FamilyRes_Fail_Summon(0x4B), // The summons has failed. Your current location or state does not allow a summons.
    FamilyRes_Fail_MaxDepth(0x4C), // The family cannot extend more than 1000 generations from above and below.
    FamilyRes_Fail_MinLevel(0x4D), // The Junior you wish to add must be over Level 10.
    FamilyRes_Fail_ChildReqWorldTransferUser(0x4E),
    FamilyRes_Fail_SelfReqWorldTransferUser(0x4F),
    FamilyRes_Fail_NotEnoughMoney(0x50),
    FamilyRes_Fail_NotEnoughParentMoney(0x51),
    FamilyRes_Fail_CanNotTeleport_ByLevellimited(0x52),
    UNKNOWN;

    private int value;

    OpsFamily(int val) {
        this.value = val;
    }

    OpsFamily() {
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

    public static OpsFamily find(int val) {
        for (OpsFamily ops : values()) {
            if (ops.get() == val && val != UNKNOWN.get()) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
