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
package packet.request;

import client.MapleCharacter;
import client.inventory.Equip;
import client.inventory.IEquip.ScrollResult;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import config.ServerConfig;
import debug.Debug;
import java.util.Random;
import packet.ClientPacket;
import packet.response.ResCParcelDlg;
import packet.response.ResCUser_Pet;
import packet.response.ResCUser;
import packet.response.ResCUserLocal;
import packet.response.wrapper.ResWrapper;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;

/**
 *
 * @author Riremito
 */
public class ItemRequest {

    public static void RemoveCashItem(MapleCharacter chr, short item_slot) {
        MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.CASH, item_slot, (short) 1, false, true);
        chr.SendPacket(ResWrapper.StatChanged(chr)); // 多分 remove時にどうにかできる
    }

    public static boolean ConsumeCashItemUse(ClientPacket cp, MapleCharacter chr) {
        if (ServerConfig.JMS180orLater()) {
            int timestamp = cp.Decode4(); // v164は何故か末尾にあるので注意
            chr.updateTick(timestamp);
        }

        short item_slot = cp.Decode2();
        int item_id = cp.Decode4();

        final IItem toUse = chr.getInventory(MapleInventoryType.CASH).getItem(item_slot);
        if (toUse == null || toUse.getItemId() != item_id || toUse.getQuantity() < 1) {
            chr.SendPacket(ResWrapper.enableActions());
            Debug.ErrorLog("ConsumeCashItem : " + chr.getName() + " " + item_id);
            return false;
        }

        int item_type = item_id / 10000;

        switch (item_id) {
            case 5170000: {
                long unique_id = cp.Decode8();
                String pet_name = cp.DecodeStr();

                MaplePet pet = null;
                int pet_index = 0;
                for (int i = 0; i < 3; i++) {
                    pet = chr.getPet(pet_index);
                    if (pet != null) {
                        if (pet.getUniqueId() == unique_id) {
                            break;
                        }
                        pet = null;
                    }
                }

                if (pet == null) {
                    chr.SendPacket(ResWrapper.StatChanged(chr));
                    return true;
                }

                // new name
                pet.setName(pet_name);
                // remove item
                RemoveCashItem(chr, item_slot);
                chr.SendPacket(ResWrapper.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition())));
                chr.getMap().broadcastMessage(ResCUser_Pet.changePetName(chr, pet_index, pet_name));
                return true;
            }
            // パチンコ玉
            case 5201000:
            case 5201001:
            case 5201002: {
                final int tama = MapleItemInformationProvider.getInstance().getInt(item_id, "info/dama");
                if (chr.gainTama(tama, true)) {
                    chr.SendPacket(ResCUserLocal.PachinkoBoxSuccess(tama));
                    RemoveCashItem(chr, item_slot);
                } else {
                    chr.SendPacket(ResCUserLocal.PachinkoBoxFailure());
                }
                return true;
            }
            // ランダムメル袋 (未実装アイテム)
            case 5202000: {
                int randommeso = 0;
                final int meso = MapleItemInformationProvider.getInstance().getInt(item_id, "info/meso");
                final int mesomax = MapleItemInformationProvider.getInstance().getInt(item_id, "info/mesomax");
                final int mesomin = MapleItemInformationProvider.getInstance().getInt(item_id, "info/mesomin");
                final int mesostdev = MapleItemInformationProvider.getInstance().getInt(item_id, "info/mesostdev");

                Random random = new Random();
                int r = random.nextInt(4);

                switch (r) {
                    case 0:
                        randommeso = mesomin;
                        break;
                    case 1:
                        randommeso = mesostdev;
                        break;
                    case 2:
                        randommeso = meso;
                        break;
                    case 3:
                        randommeso = mesomax;
                        break;
                    default:
                        randommeso = mesomin;
                        break;
                }

                if (chr.gainMeso(randommeso, false)) {
                    chr.SendPacket(ResCUserLocal.RandomMesoBagSuccess((byte) (r + 1), randommeso));
                    RemoveCashItem(chr, item_slot);
                } else {
                    chr.SendPacket(ResCUserLocal.RandomMesoBagFailed());
                }
                return true;
            }
            // 速達
            case 5330000: {
                chr.SendPacket(ResCParcelDlg.Open(true, false));
                return true;
            }
            // ミラクルキューブ
            case 5062000:
            case 5062001:
            case 5062002:
            case 5062003: {
                int equip_slot = cp.Decode4();
                IItem item = chr.getInventory(MapleInventoryType.EQUIP).getItem((short) equip_slot);
                if (item != null) {
                    final Equip equip = (Equip) item;
                    equip.resetPotential(item_id == 5062001 || item_id == 5062003, item_id == 5062002 || item_id == 5062003);

                    chr.SendPacket(ResCUser.getPotentialEffect(chr.getId(), equip.getPosition()));
                    chr.getMap().broadcastMessage(chr, ResCUser.getScrollEffect(chr.getId(), ScrollResult.SUCCESS, false), false);
                    chr.SendPacket(ResWrapper.scrolledItem(toUse, item, false, true));
                    RemoveCashItem(chr, item_slot);
                    chr.forceReAddItem_NoUpdate(item, MapleInventoryType.EQUIP);
                    chr.saveToDB(false, false);
                    //MapleInventoryManipulator.addById(chr.getClient(), 2430112, (short) 1);
                }
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

}
