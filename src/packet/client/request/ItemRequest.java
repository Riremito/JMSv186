/*
 * Copyright (C) 2024 Riremito
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
package packet.client.request;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import config.ServerConfig;
import debug.Debug;
import handling.world.World;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import packet.client.ClientPacket;
import packet.server.response.DueyResponse;
import packet.server.response.ItemResponse;
import packet.server.response.MegaphoneResponse;
import packet.server.response.PachinkoResponse;
import packet.server.response.PetResponse;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.MapleDynamicPortal;
import tools.MaplePacketCreator;

/**
 *
 * @author Riremito
 */
public class ItemRequest {

    // 勲章が装備されるスロットのID
    private static final short SLOT_EQUIPPED_MEDAL = (short) -21;

    public static boolean OnPacket(ClientPacket.Header header, ClientPacket cp, MapleClient c) {
        MapleCharacter chr = c.getPlayer();

        if (chr == null || chr.getMap() == null) {
            return false;
        }

        switch (header) {
            case CP_UserConsumeCashItemUseRequest: {
                return ConsumeCashItem(cp, chr);
            }
            case CP_UserStatChangeItemUseRequest: {
                return true;
            }
            case CP_UserItemReleaseRequest: {
                return true;
            }
            case CP_UserScriptItemUseRequest: {
                return true;
            }
            case CP_UserPortalScrollUseRequest: {
                return true;
            }
            case CP_UserUpgradeItemUseRequest: {
                return true;
            }
            case CP_UserItemOptionUpgradeItemUseRequest: {
                return true;
            }
            case CP_UserHyperUpgradeItemUseRequest: {
                return true;
            }
            case CP_UserMobSummonItemUseRequest: {
                return true;
            }
            case USE_TREASUER_CHEST: {
                return true;
            }
            case CP_UserSkillLearnItemUseRequest: {
                return true;
            }
            case CP_UserBridleItemUseRequest: {
                return true;
            }
            case CP_UserTamingMobFoodItemUseRequest: {
                return true;
            }
            case CP_UserUseGachaponBoxRequest: {
                return true;
            }
            case CP_ShopLinkRequest: {
                return true;
            }
            case CP_UserShopScannerItemUseRequest: {
                return true;
            }
            case CP_UserSkillResetItemUseRequest: {
                // v194
                cp.Decode4(); // 2114524514, 00A67BE0
                short item_slot = cp.Decode2(); // 60, 00A67BEE
                int item_id = cp.Decode4(); // 2500000, 00A67BFC
                return true;
            }
            case CP_JMS_MONSTERBOOK_SET: {
                // v194
                cp.Decode4(); // 2114843894, 00A60ACB
                int item_slot = cp.Decode4(); // 64, 00A60AD9 4 bytes
                int song_time = cp.Decode4(); // 2560000, 00A60AE3
                return true;
            }
            case CP_JMS_JUKEBOX: {
                // v194
                cp.Decode4(); // 2113673714, 00A70E25
                short item_slot = cp.Decode2(); // 43, 00A70E36
                int item_id = cp.Decode4(); // 2150001, 00A70E40
                int song_time = cp.Decode4(); // 113788, 00A70E4A
                c.getPlayer().UpdateStat(true);
                return true;
            }
            case CP_JMS_PINKBEAN_PORTAL_ENTER: {
                int portal_id = cp.Decode4();
                byte flag = cp.Decode1();
                //MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(749050200);
                //c.getPlayer().changeMap(to, to.getPortal(0));
                MapleDynamicPortal dynamic_portal = chr.getMap().findDynamicPortal(portal_id);
                if (dynamic_portal == null) {
                    c.getPlayer().UpdateStat(true);
                    return true;
                }
                dynamic_portal.warp(chr);
                return true;
            }
            case CP_JMS_PINKBEAN_PORTAL_CREATE: {
                // v194
                cp.Decode4(); // -2145728229, 00A6618A
                short item_slot = cp.Decode2(); // 50, 00A66198
                int item_id = cp.Decode4(); // 2420004, 00A661A6
                short x = cp.Decode2(); // -1776, 00A661C1
                short y = cp.Decode2(); // 213, 00A661DD
                MapleDynamicPortal dynamic_portal = new MapleDynamicPortal(item_id, 749050200, x, y);
                c.getPlayer().getMap().addMapObject(dynamic_portal);
                c.getPlayer().getMap().broadcastMessage(ItemResponse.CreatePinkBeanEventPortal(dynamic_portal));
                c.getPlayer().UpdateStat(true);
                return true;
            }
            default: {
                break;
            }
        }
        c.getPlayer().UpdateStat(true);
        return false;
    }

    public static void RemoveCashItem(MapleCharacter chr, short item_slot) {
        MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.CASH, item_slot, (short) 1, false, true);
        chr.enableActions(); // 多分 remove時にどうにかできる
    }

    public static boolean ConsumeCashItem(ClientPacket cp, MapleCharacter chr) {
        // v131 does not have timestamp
        if (!(ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 131)) {
            int timestamp = cp.Decode4();
            chr.updateTick(timestamp);
        }

        short item_slot = cp.Decode2();
        int item_id = cp.Decode4();

        final IItem toUse = chr.getInventory(MapleInventoryType.CASH).getItem(item_slot);
        if (toUse == null || toUse.getItemId() != item_id || toUse.getQuantity() < 1) {
            chr.SendPacket(MaplePacketCreator.enableActions());
            Debug.ErrorLog("ConsumeCashItem : " + chr.getName() + " " + item_id);
            return false;
        }

        int item_type = item_id / 10000;

        switch (item_type) {
            case 507: {
                if (UseMegaphone(cp, chr, item_id)) {
                    RemoveCashItem(chr, item_slot);
                }
                return true;
            }
            default: {
                break;
            }
        }

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
                    chr.enableActions();
                    return true;
                }

                // new name
                pet.setName(pet_name);
                // remove item
                RemoveCashItem(chr, item_slot);
                chr.SendPacket(PetResponse.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition())));
                chr.getMap().broadcastMessage(PetResponse.changePetName(chr, pet_index, pet_name));
                return true;
            }
            // パチンコ玉
            case 5201000:
            case 5201001:
            case 5201002: {
                final int tama = MapleItemInformationProvider.getInstance().getInt(item_id, "info/dama");
                if (chr.gainTama(tama, true)) {
                    chr.SendPacket(PachinkoResponse.TamaBoxSuccess(tama));
                    RemoveCashItem(chr, item_slot);
                } else {
                    chr.SendPacket(PachinkoResponse.TamaBoxFailure());
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
                    chr.SendPacket(ItemResponse.RandomMesoBagSuccess((byte) (r + 1), randommeso));
                    RemoveCashItem(chr, item_slot);
                } else {
                    chr.SendPacket(ItemResponse.RandomMesoBagFailed());
                }
                return true;
            }
            // 速達
            case 5330000: {
                chr.SendPacket(DueyResponse.Open(true, false));
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // 勲章の名前を付けたキャラクター名
    private static String MegaphoneGetSenderName(MapleCharacter chr) {
        IItem equipped_medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(SLOT_EQUIPPED_MEDAL);
        // "キャラクター名"
        if (equipped_medal == null) {
            return chr.getName();
        }
        String medal_name = MapleItemInformationProvider.getInstance().getName(equipped_medal.getItemId());
        int padding = medal_name.indexOf("の勲章");
        if (padding > 0) {
            medal_name = medal_name.substring(0, padding);
        }
        // "<勲章> キャラクター名"
        return "<" + medal_name + "> " + chr.getName();
    }

    public static boolean UseMegaphone(ClientPacket cp, MapleCharacter chr, int item_id) {
        byte channel = (byte) chr.getClient().getChannel();
        switch (item_id) {
            // メガホン
            case 5070000: {
                String message = new String(cp.DecodeBuffer());
                chr.getMap().broadcastMessage(MegaphoneResponse.MegaphoneBlue(MegaphoneGetSenderName(chr) + " : " + message));
                return true;
            }
            // 拡声器
            case 5071000: {
                String message = new String(cp.DecodeBuffer());
                byte ear = cp.Decode1();
                World.Broadcast.broadcastSmega(MegaphoneResponse.Megaphone(MegaphoneGetSenderName(chr) + " : " + message, channel, ear).getBytes());
                return true;
            }
            // 高機能拡声器 (使えない)
            case 5072000: {
                break;
            }
            // ハート拡声器
            case 5073000: {
                String message = new String(cp.DecodeBuffer());
                byte ear = cp.Decode1();
                World.Broadcast.broadcastSmega(MegaphoneResponse.MegaphoneHeart(MegaphoneGetSenderName(chr) + " : " + message, channel, ear).getBytes());
                return true;
            }
            // ドクロ拡声器
            case 5074000: {
                String message = new String(cp.DecodeBuffer());
                byte ear = cp.Decode1();
                World.Broadcast.broadcastSmega(MegaphoneResponse.MegaphoneSkull(MegaphoneGetSenderName(chr) + " : " + message, channel, ear).getBytes());
                return true;
            }
            // MapleTV
            case 5075000:
            case 5075001:
            case 5075002:
            case 5075003:
            case 5075004:
            case 5075005: {
                break;
            }
            // アイテム拡声器
            case 5076000: {
                String message = new String(cp.DecodeBuffer());
                byte ear = cp.Decode1();
                byte showitem = cp.Decode1();
                IItem item = null;
                if (showitem == 1) {
                    // アイテム情報
                    int type = cp.Decode4();
                    int slot = cp.Decode4();
                    item = chr.getInventory(MapleInventoryType.getByType((byte) type)).getItem((short) slot);
                }
                World.Broadcast.broadcastSmega(MegaphoneResponse.MegaphoneItem(MegaphoneGetSenderName(chr) + " : " + message, channel, ear, showitem, item).getBytes());
                return true;
            }
            // 三連拡声器
            case 5077000: {
                List<String> messages = new LinkedList<>();
                String sender = MegaphoneGetSenderName(chr);
                // メッセージの行数
                byte line = cp.Decode1();
                for (int i = 0; i < line; i++) {
                    String message = new String(cp.DecodeBuffer());
                    if (message.length() > 65) {
                        break;
                    }
                    messages.add(sender + " : " + message);
                }
                byte ear = cp.Decode1();
                World.Broadcast.broadcastSmega(MegaphoneResponse.MegaphoneTriple(messages, (byte) channel, ear).getBytes());
                return true;
            }
            default: {
                break;
            }
        }

        chr.SendPacket(MaplePacketCreator.enableActions());
        return false;
    }

}
