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
package packet.request;

import client.ISkill;
import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.IEquip;
import client.inventory.IEquip.ScrollResult;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MapleMount;
import client.inventory.MaplePet;
import config.Region;
import config.ServerConfig;
import config.Version;
import constants.GameConstants;
import debug.Debug;
import handling.world.World;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import packet.ClientPacket;
import packet.ops.OpsBodyPart;
import packet.ops.OpsBroadcastMsg;
import packet.ops.arg.ArgBroadcastMsg;
import packet.response.ResCMobPool;
import packet.response.ResCParcelDlg;
import packet.response.ResCUser_Pet;
import packet.response.ResCUIVega;
import packet.response.ResCUser;
import packet.response.ResCUserLocal;
import packet.response.ResCWvsContext;
import packet.response.Res_JMS_CInstancePortalPool;
import packet.response.wrapper.ResWrapper;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleMonster;
import server.maps.FieldLimitType;
import server.maps.MapleDynamicPortal;
import server.maps.MapleMap;
import tools.data.input.SeekableLittleEndianAccessor;

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
            // 回復薬
            case CP_UserStatChangeItemUseRequest: {
                OnStatChangeItemUse(cp, chr);
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
            case CP_UserSkillLearnItemUseRequest: {
                return true;
            }
            case CP_UserBridleItemUseRequest: {
                return true;
            }
            case CP_UserTamingMobFoodItemUseRequest: {
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
            case CP_JMS_InstancePortalEnter: {
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
            case CP_JMS_InstancePortalCreate: {
                // v194
                cp.Decode4(); // -2145728229, 00A6618A
                short item_slot = cp.Decode2(); // 50, 00A66198
                int item_id = cp.Decode4(); // 2420004, 00A661A6
                short x = cp.Decode2(); // -1776, 00A661C1
                short y = cp.Decode2(); // 213, 00A661DD
                MapleDynamicPortal dynamic_portal = new MapleDynamicPortal(item_id, 749050200, x, y);
                c.getPlayer().getMap().addMapObject(dynamic_portal);
                c.getPlayer().getMap().broadcastMessage(Res_JMS_CInstancePortalPool.CreatePinkBeanEventPortal(dynamic_portal));
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

    public static boolean UseItem(MapleCharacter chr, short item_slot, int item_id) {
        return UseItem(chr, item_slot, item_id, 0);
    }

    public static boolean UseItem(MapleCharacter chr, short item_slot, int item_id, int timestamp) {
        IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(item_slot);

        if (toUse == null || toUse.getItemId() != item_id || toUse.getQuantity() < 1) {
            chr.SendPacket(ResWrapper.StatChanged(chr));
            return false;
        }

        // ?
        final long time = System.currentTimeMillis();
        if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "You may not use this item yet.");
            chr.SendPacket(ResWrapper.StatChanged(chr));
            return false;
        }

        MapleMap map = chr.getMap();

        if (FieldLimitType.PotionUse.check(map.getFieldLimit())) {
            chr.SendPacket(ResWrapper.StatChanged(chr));
            return false;
        }

        if (!MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
            chr.SendPacket(ResWrapper.StatChanged(chr));
            return false;
        }

        MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.USE, item_slot, (short) 1, false);
        if (chr.getMap().getConsumeItemCoolTime() > 0) {
            chr.setNextConsume(time + (chr.getMap().getConsumeItemCoolTime() * 1000));
        }

        if (timestamp != 0) {
            chr.updateTick(timestamp);
        }
        return true;
    }

    public static boolean OnStatChangeItemUse(ClientPacket cp, MapleCharacter chr) {
        int timestamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
        short item_slot = cp.Decode2();
        int item_id = cp.Decode4();
        return UseItem(chr, item_slot, item_id, timestamp);
    }

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

    // 勲章の名前を付けたキャラクター名
    public static String MegaphoneGetSenderName(MapleCharacter chr) {
        IItem equipped_medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem(OpsBodyPart.BP_MEDAL.getSlot());
        // "キャラクター名"
        if (equipped_medal == null) {
            return chr.getName();
        }
        String medal_name = MapleItemInformationProvider.getInstance().getName(equipped_medal.getItemId());

        //Debug.DebugLog("medal = " + equipped_medal.getItemId());
        if (medal_name == null) {
            return chr.getName();
        }

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
                String message = cp.DecodeStr();

                ArgBroadcastMsg bma = new ArgBroadcastMsg();
                bma.bm = OpsBroadcastMsg.BM_SPEAKERCHANNEL;
                bma.chr = chr;
                bma.message = message;
                chr.getClient().getChannelServer().broadcastSmega(ResCWvsContext.BroadcastMsg(bma).getBytes());
                return true;
            }
            // 拡声器
            case 5071000: {
                String message = cp.DecodeStr();
                byte ear = Version.LessOrEqual(Region.KMS, 31) ? 1 : cp.Decode1();

                ArgBroadcastMsg bma = new ArgBroadcastMsg();
                bma.bm = OpsBroadcastMsg.BM_SPEAKERWORLD;
                bma.chr = chr;
                bma.message = message;
                bma.ear = ear;
                World.Broadcast.broadcastSmega(ResCWvsContext.BroadcastMsg(bma).getBytes());
                return true;
            }
            // 高機能拡声器 (使えない)
            case 5072000: {
                break;
            }
            // ハート拡声器
            case 5073000: {
                String message = cp.DecodeStr();
                byte ear = cp.Decode1();

                ArgBroadcastMsg bma = new ArgBroadcastMsg();
                bma.bm = OpsBroadcastMsg.BM_HEARTSPEAKER;
                bma.chr = chr;
                bma.message = message;
                bma.ear = ear;
                World.Broadcast.broadcastSmega(ResCWvsContext.BroadcastMsg(bma).getBytes());
                return true;
            }
            // ドクロ拡声器
            case 5074000: {
                String message = cp.DecodeStr();
                byte ear = cp.Decode1();

                ArgBroadcastMsg bma = new ArgBroadcastMsg();
                bma.bm = OpsBroadcastMsg.BM_SKULLSPEAKER;
                bma.chr = chr;
                bma.message = message;
                bma.ear = ear;
                World.Broadcast.broadcastSmega(ResCWvsContext.BroadcastMsg(bma).getBytes());
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
                String message = cp.DecodeStr();
                byte ear = cp.Decode1();
                byte showitem = cp.Decode1();
                IItem item = null;
                if (showitem == 1) {
                    // アイテム情報
                    int type = cp.Decode4();
                    int slot = cp.Decode4();
                    item = chr.getInventory(MapleInventoryType.getByType((byte) type)).getItem((short) slot);
                }

                ArgBroadcastMsg bma = new ArgBroadcastMsg();
                bma.bm = OpsBroadcastMsg.BM_ITEMSPEAKER;
                bma.chr = chr;
                bma.message = message;
                bma.ear = ear;
                bma.item = item;
                World.Broadcast.broadcastSmega(ResCWvsContext.BroadcastMsg(bma).getBytes());
                return true;
            }
            // 三連拡声器
            case 5077000: {
                List<String> messages = new LinkedList<>();
                // メッセージの行数
                byte line = cp.Decode1();
                if (3 < line) {
                    Debug.ErrorLog("三連拡声器 - lines");
                    return false;
                }
                for (int i = 0; i < line; i++) {
                    String message = cp.DecodeStr();
                    if (message.length() > 65) {
                        Debug.ErrorLog("三連拡声器 - letters");
                        return false;
                    }
                    messages.add(message);
                }
                byte ear = cp.Decode1();

                ArgBroadcastMsg bma = new ArgBroadcastMsg();
                bma.bm = OpsBroadcastMsg.MEGAPHONE_TRIPLE;
                bma.chr = chr;
                bma.ear = ear;
                bma.multi_line = true;
                bma.messages = messages;

                World.Broadcast.broadcastSmega(ResCWvsContext.BroadcastMsg(bma).getBytes());
                return true;
            }
            default: {
                break;
            }
        }

        chr.SendPacket(ResWrapper.enableActions());
        return false;
    }

    public static final void UseCatchItem(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt();
        final MapleMonster mob = chr.getMap().getMonsterByOid(slea.readInt());
        final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mob != null) {
            switch (itemid) {
                case 2270004: {
                    //Purification Marble
                    final MapleMap map = chr.getMap();
                    if (mob.getHp() <= mob.getMobMaxHp() / 2) {
                        map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), itemid, (byte) 1));
                        map.killMonster(mob, chr, true, false, (byte) 0);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, false, false);
                        MapleInventoryManipulator.addById(c, 4001169, (short) 1);
                    } else {
                        map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), itemid, (byte) 0));
                        chr.dropMessage(5, "The monster has too much physical strength, so you cannot catch it.");
                    }
                    break;
                }
                case 2270002: {
                    // Characteristic Stone
                    final MapleMap map = chr.getMap();
                    if (mob.getHp() <= mob.getMobMaxHp() / 2) {
                        map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), itemid, (byte) 1));
                        map.killMonster(mob, chr, true, false, (byte) 0);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, false, false);
                    } else {
                        map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), itemid, (byte) 0));
                        chr.dropMessage(5, "The monster has too much physical strength, so you cannot catch it.");
                    }
                    break;
                }
                case 2270000: {
                    // Pheromone Perfume
                    if (mob.getId() != 9300101) {
                        break;
                    }
                    final MapleMap map = c.getPlayer().getMap();
                    map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), itemid, (byte) 1));
                    map.killMonster(mob, chr, true, false, (byte) 0);
                    MapleInventoryManipulator.addById(c, 1902000, (short) 1, null);
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, false, false);
                    break;
                }
                case 2270003: {
                    // Cliff's Magic Cane
                    if (mob.getId() != 9500320) {
                        break;
                    }
                    final MapleMap map = c.getPlayer().getMap();
                    if (mob.getHp() <= mob.getMobMaxHp() / 2) {
                        map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), itemid, (byte) 1));
                        map.killMonster(mob, chr, true, false, (byte) 0);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, itemid, 1, false, false);
                    } else {
                        map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), itemid, (byte) 0));
                        chr.dropMessage(5, "The monster has too much physical strength, so you cannot catch it.");
                    }
                    break;
                }
            }
        }
        c.getSession().write(ResWrapper.enableActions());
    }

    public static final void UseMagnify(ClientPacket cp, final MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        int time_stamp = cp.Decode4(); // time
        short slot_use_item = cp.Decode2();
        short slot_equip_item = cp.Decode2();
        final IItem magnify = chr.getInventory(MapleInventoryType.USE).getItem(slot_use_item);
        IItem toReveal = (slot_equip_item < 0) ? chr.getInventory(MapleInventoryType.EQUIPPED).getItem(slot_equip_item) : chr.getInventory(MapleInventoryType.EQUIP).getItem(slot_equip_item);

        if (magnify == null || toReveal == null) {
            chr.SendPacket(ResWrapper.getInventoryFull());
            Debug.ErrorLog("potential err 1");
            return;
        }

        final Equip eqq = (Equip) toReveal;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(eqq.getItemId()) / 10;

        Debug.DebugLog("eqq.getState =  " + eqq.getHidden() + ", magnify.getItemId = " + magnify.getItemId() + ", reqLevel = " + reqLevel);
        Debug.DebugLog("" + eqq.getPotential1() + ", " + eqq.getPotential2() + ", " + eqq.getPotential3());
        if (eqq.getHidden() == 1
                && (magnify.getItemId() == 2460003 || (magnify.getItemId() == 2460002 && reqLevel <= 12) || (magnify.getItemId() == 2460001 && reqLevel <= 7) || (magnify.getItemId() == 2460000 && reqLevel <= 3))) {
            eqq.setHidden(0); // 未確認状態へ変更
            chr.SendPacket(ResWrapper.scrolledItem(magnify, toReveal, false, true));
            chr.getMap().broadcastMessage(ResCUser.getPotentialReset(chr.getId(), eqq.getPosition()));
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, magnify.getPosition(), (short) 1, false);
            Debug.DebugLog("potential updated");
        } else {
            chr.SendPacket(ResWrapper.getInventoryFull());
            Debug.ErrorLog("potential err 2");
            return;
        }
    }

    public static final boolean UseSkillBook(short slot, final int itemId, final MapleClient c, final MapleCharacter chr) {
        final IItem toUse = chr.getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            return false;
        }
        final Map<String, Integer> skilldata = MapleItemInformationProvider.getInstance().getSkillStats(toUse.getItemId());
        if (skilldata == null) {
            // Hacking or used an unknown item
            return false;
        }
        boolean canuse = false;
        boolean success = false;
        int skill = 0;
        int maxlevel = 0;
        final int SuccessRate = skilldata.get("success");
        final int ReqSkillLevel = skilldata.get("reqSkillLevel");
        final int MasterLevel = skilldata.get("masterLevel");
        byte i = 0;
        Integer CurrentLoopedSkillId;
        while (true) {
            CurrentLoopedSkillId = skilldata.get("skillid" + i);
            i++;
            if (CurrentLoopedSkillId == null) {
                break; // End of data
            }
            final ISkill CurrSkillData = SkillFactory.getSkill(CurrentLoopedSkillId);
            if (CurrSkillData != null && CurrSkillData.canBeLearnedBy(chr.getJob()) && chr.getSkillLevel(CurrSkillData) >= ReqSkillLevel && chr.getMasterLevel(CurrSkillData) < MasterLevel) {
                canuse = true;
                if (Randomizer.nextInt(100) <= SuccessRate && SuccessRate != 0) {
                    success = true;
                    chr.changeSkillLevel(CurrSkillData, chr.getSkillLevel(CurrSkillData), (byte) MasterLevel);
                } else {
                    success = false;
                }
                MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(itemId), slot, (short) 1, false);
                break;
            }
        }
        c.getPlayer().getMap().broadcastMessage(ResCWvsContext.useSkillBook(chr, skill, maxlevel, canuse, success));
        c.getSession().write(ResWrapper.enableActions());
        return canuse;
    }

    public static final boolean UseUpgradeScroll(short slot, short dst, final byte ws, final MapleClient c, final MapleCharacter chr) {
        return UseUpgradeScroll(slot, dst, ws, c, chr, 0);
    }

    public static final boolean UseUpgradeScroll(short slot, short dst, final byte ws, final MapleClient c, final MapleCharacter chr, final int vegas) {
        boolean whiteScroll = true;
        boolean legendarySpirit = false; // legendary spirit skill
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        /*
        if ((ws & 2) == 2) {
        whiteScroll = true;
        }
         */
        IEquip toScroll;
        if (dst < 0) {
            toScroll = (IEquip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);
        } else {
            // legendary spirit
            legendarySpirit = true;
            toScroll = (IEquip) chr.getInventory(MapleInventoryType.EQUIP).getItem(dst);
        }
        if (toScroll == null) {
            return false;
        }
        final byte oldLevel = (byte) toScroll.getLevel();
        final byte oldEnhance = (byte) toScroll.getEnhance();
        final byte oldState = (byte) toScroll.getHidden();
        final byte oldFlag = (byte) toScroll.getFlag();
        final byte oldSlots = (byte) toScroll.getUpgradeSlots();
        IItem scroll = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (scroll == null) {
            c.getSession().write(ResWrapper.getInventoryFull());
            return false;
        }
        // 黄金つち (ビシャスのハンマー)
        if (scroll.getItemId() == 2470000) {
            final Equip toHammer = (Equip) toScroll;
            if (toHammer.getViciousHammer() >= 2 || toHammer.getUpgradeSlots() > 120) {
                c.getSession().write(ResWrapper.getInventoryFull());
                return false;
            }
            toHammer.setViciousHammer((byte) (toHammer.getViciousHammer() + 1));
            toHammer.setUpgradeSlots((byte) (toHammer.getUpgradeSlots() + 1));
            c.SendPacket(ResWrapper.scrolledItem(scroll, toHammer, false, false));
            chr.getInventory(MapleInventoryType.USE).removeItem(scroll.getPosition(), (short) 1, false);
            chr.getMap().broadcastMessage(chr, ResCUser.getScrollEffect(c.getPlayer().getId(), IEquip.ScrollResult.SUCCESS, legendarySpirit), vegas == 0);
            return true;
        }
        if (!GameConstants.isSpecialScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isPotentialScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() < 1) {
                c.getSession().write(ResWrapper.getInventoryFull());
                return false;
            }
        } else if (GameConstants.isEquipScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() >= 1 || toScroll.getEnhance() >= 100 || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                c.getSession().write(ResWrapper.getInventoryFull());
                return false;
            }
        } else if (GameConstants.isPotentialScroll(scroll.getItemId())) {
            if (toScroll.getHidden() >= 1 || (toScroll.getLevel() == 0 && toScroll.getUpgradeSlots() == 0) || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                c.getSession().write(ResWrapper.getInventoryFull());
                return false;
            }
        }
        if (!GameConstants.canScroll(toScroll.getItemId()) && !GameConstants.isChaosScroll(toScroll.getItemId())) {
            c.getSession().write(ResWrapper.getInventoryFull());
            return false;
        }
        if ((GameConstants.isCleanSlate(scroll.getItemId()) || GameConstants.isTablet(scroll.getItemId()) || GameConstants.isChaosScroll(scroll.getItemId())) && (vegas > 0 || ii.isCash(toScroll.getItemId()))) {
            c.getSession().write(ResWrapper.getInventoryFull());
            return false;
        }
        if (GameConstants.isTablet(scroll.getItemId()) && toScroll.getDurability() < 0) {
            //not a durability item
            c.getSession().write(ResWrapper.getInventoryFull());
            return false;
        } else if (!GameConstants.isTablet(scroll.getItemId()) && toScroll.getDurability() >= 0) {
            c.getSession().write(ResWrapper.getInventoryFull());
            return false;
        }
        IItem wscroll = null;
        // Anti cheat and validation
        List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
        if (scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
            c.getSession().write(ResWrapper.getInventoryFull());
            return false;
        }
        if (whiteScroll) {
            wscroll = chr.getInventory(MapleInventoryType.USE).findById(2340000);
            if (wscroll == null) {
                whiteScroll = false;
            }
        }
        if (scroll.getItemId() == 2049115 && toScroll.getItemId() != 1003068) {
            //ravana
            return false;
        }
        if (GameConstants.isTablet(scroll.getItemId())) {
            switch (scroll.getItemId() % 1000 / 100) {
                case 0:
                    //1h
                    if (GameConstants.isTwoHanded(toScroll.getItemId()) || !GameConstants.isWeapon(toScroll.getItemId())) {
                        return false;
                    }
                    break;
                case 1:
                    //2h
                    if (!GameConstants.isTwoHanded(toScroll.getItemId()) || !GameConstants.isWeapon(toScroll.getItemId())) {
                        return false;
                    }
                    break;
                case 2:
                    //armor
                    if (GameConstants.isAccessory(toScroll.getItemId()) || GameConstants.isWeapon(toScroll.getItemId())) {
                        return false;
                    }
                    break;
                case 3:
                    //accessory
                    if (!GameConstants.isAccessory(toScroll.getItemId()) || GameConstants.isWeapon(toScroll.getItemId())) {
                        return false;
                    }
                    break;
            }
        } else if (!GameConstants.isAccessoryScroll(scroll.getItemId()) && !GameConstants.isChaosScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isPotentialScroll(scroll.getItemId())) {
            if (!ii.canScroll(scroll.getItemId(), toScroll.getItemId())) {
                return false;
            }
        }
        if (GameConstants.isAccessoryScroll(scroll.getItemId()) && !GameConstants.isAccessory(toScroll.getItemId())) {
            return false;
        }
        if (scroll.getQuantity() <= 0) {
            return false;
        }
        if (legendarySpirit && vegas == 0) {
            if (chr.getSkillLevel(SkillFactory.getSkill(1003)) <= 0 && chr.getSkillLevel(SkillFactory.getSkill(10001003)) <= 0 && chr.getSkillLevel(SkillFactory.getSkill(20001003)) <= 0 && chr.getSkillLevel(SkillFactory.getSkill(20011003)) <= 0 && chr.getSkillLevel(SkillFactory.getSkill(30001003)) <= 0) {
                return false;
            }
        }
        // Scroll Success/ Failure/ Curse
        final IEquip scrolled = (IEquip) ii.scrollEquipWithId(toScroll, scroll, whiteScroll, chr, vegas);
        IEquip.ScrollResult scrollSuccess;
        if (scrolled == null) {
            scrollSuccess = IEquip.ScrollResult.CURSE;
        } else if (scrolled.getLevel() > oldLevel || scrolled.getEnhance() > oldEnhance || scrolled.getHidden() > oldState || scrolled.getFlag() > oldFlag) {
            scrollSuccess = IEquip.ScrollResult.SUCCESS;
        } else if (GameConstants.isCleanSlate(scroll.getItemId()) && scrolled.getUpgradeSlots() > oldSlots) {
            scrollSuccess = IEquip.ScrollResult.SUCCESS;
        } else {
            scrollSuccess = IEquip.ScrollResult.FAIL;
        }
        // Update
        chr.getInventory(MapleInventoryType.USE).removeItem(scroll.getPosition(), (short) 1, false);
        if (whiteScroll) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, wscroll.getPosition(), (short) 1, false, false);
        }
        if (scrollSuccess == IEquip.ScrollResult.CURSE) {
            c.SendPacket(ResWrapper.scrolledItem(scroll, toScroll, true, false));
            if (dst < 0) {
                chr.getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            } else {
                chr.getInventory(MapleInventoryType.EQUIP).removeItem(toScroll.getPosition());
            }
        } else if (vegas == 0) {
            c.SendPacket(ResWrapper.scrolledItem(scroll, scrolled, false, false));
        }
        chr.getMap().broadcastMessage(chr, ResCUser.getScrollEffect(c.getPlayer().getId(), scrollSuccess, legendarySpirit), vegas == 0);
        // equipped item was scrolled and changed
        if (dst < 0 && (scrollSuccess == IEquip.ScrollResult.SUCCESS || scrollSuccess == IEquip.ScrollResult.CURSE) && vegas == 0) {
            chr.equipChanged();
        }
        // ベガの呪文書
        if (vegas != 0) {
            c.getPlayer().forceReAddItem(toScroll, MapleInventoryType.EQUIP);
            c.ProcessPacket(ResCUIVega.Start());
            c.ProcessPacket(ResCUIVega.Result(scrollSuccess == IEquip.ScrollResult.SUCCESS));
        }
        return true;
    }

    public static final void UseReturnScroll(MapleClient c, MapleCharacter chr, short slot, int itemId) {
        if (!chr.isAlive()) {
            c.getSession().write(ResWrapper.enableActions());
            return;
        }
        final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId) {
            c.getSession().write(ResWrapper.enableActions());
            return;
        }
        if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyReturnScroll(chr)) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        } else {
            c.getSession().write(ResWrapper.enableActions());
        }
    }

    public static final void UseMountFood(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        c.getPlayer().updateTick(slea.readInt());
        final byte slot = (byte) slea.readShort();
        final int itemid = slea.readInt(); //2260000 usually
        final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        final MapleMount mount = chr.getMount();
        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemid && mount != null) {
            final int fatigue = mount.getFatigue();
            boolean levelup = false;
            mount.setFatigue((byte) -30);
            if (fatigue > 0) {
                mount.increaseExp();
                final int level = mount.getLevel();
                if (mount.getExp() >= GameConstants.getMountExpNeededForLevel(level + 1) && level < 31) {
                    mount.setLevel((byte) (level + 1));
                    levelup = true;
                }
            }
            chr.getMap().broadcastMessage(ResCWvsContext.updateMount(chr, levelup));
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        }
        c.getSession().write(ResWrapper.enableActions());
    }

}
