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
package tacos.packet.request;

import java.awt.Point;
import odin.client.ISkill;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.PlayerStats;
import odin.client.SkillFactory;
import odin.client.inventory.Equip;
import odin.client.inventory.IEquip;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventory;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MapleMount;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import odin.constants.GameConstants;
import tacos.shared.SharedExpTable;
import tacos.debug.DebugLogger;
import odin.handling.world.MapleParty;
import odin.handling.world.OdinWorld;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import odin.handling.channel.handler.AllianceHandler;
import odin.handling.channel.handler.BBSHandler;
import odin.handling.channel.handler.FamilyHandler;
import odin.handling.channel.handler.GuildHandler;
import odin.handling.channel.handler.InventoryHandler;
import odin.handling.channel.handler.ItemMakerHandler;
import odin.handling.world.MaplePartyCharacter;
import odin.handling.world.PartyOperation;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsChangeStat;
import tacos.packet.ops.OpsChatGroup;
import tacos.packet.ops.OpsEntrustedShop;
import tacos.packet.ops.OpsMapTransfer;
import tacos.packet.ops.OpsShopScanner;
import tacos.packet.ops.Ops_Whisper;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.request.sub.ReqSub_UserConsumeCashItemUseRequest;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCMobPool;
import tacos.packet.response.ResCUIVega;
import tacos.packet.response.ResCUser;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleStatEffect;
import odin.server.Randomizer;
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleMonster;
import odin.server.life.MapleNPC;
import odin.server.life.MobAttackInfo;
import odin.server.life.MobSkill;
import odin.server.maps.MapleDynamicPortal;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapItem;
import odin.server.maps.MapleMapObjectType;
import odin.server.quest.MapleQuest;
import odin.server.shops.HiredMerchant;
import tacos.config.ContentState;
import tacos.database.LazyDatabase;
import tacos.debug.DebugCommand;
import tacos.debug.DebugShop;
import tacos.odin.OdinPair;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ops.OpsAttackIndex;
import tacos.packet.ops.OpsCashItem;
import tacos.packet.ops.OpsGivePopularity;
import tacos.packet.ops.OpsMarriage;
import tacos.packet.ops.OpsMemo;
import tacos.packet.ops.OpsMobLeaveField;
import tacos.packet.ops.OpsParty;
import tacos.packet.ops.OpsQuest;
import tacos.packet.ops.OpsSkill;
import tacos.packet.ops.OpsTransferChannel;
import tacos.packet.ops.OpsTransferField;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.request.parse.ParseCUser_Attack;
import tacos.packet.request.sub.ReqSub_Admin;
import tacos.packet.request.sub.ReqSub_FriendRequest;
import tacos.packet.response.ResCDropPool;
import tacos.packet.response.Res_JMS_CInstancePortalPool;
import tacos.packet.response.wrapper.WrapCUserLocal;
import tacos.packet.response.wrapper.WrapCUserRemote;
import tacos.script.TacosScriptNPC;
import tacos.script.TacosScriptQuest;
import tacos.server.TacosWorld;
import tacos.server.map.TacosNpcShop;
import tacos.server.map.TacosTask;
import tacos.shared.TacosShared;
import tacos.wz.WzXML;
import tacos.wz.opt.FieldOpt;

/**
 *
 * @author Riremito
 */
public class ReqCUser {

    public static boolean OnPacket_Login(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_UpdateScreenSetting: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean OnPacket(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = client.getPlayer();
        if (chr == null) {
            return true;
        }
        MapleMap map = chr.getMap();
        if (map == null) {
            return true;
        }
        switch (header) {
            case CP_UserTransferFieldRequest: {
                if (!OnUserTransferFieldRequest(cp, chr)) {
                    chr.SendPacket(ResCField.TransferFieldReqIgnored(OpsTransferField.TF_DISABLED_PORTAL));
                }
                return true;
            }
            case CP_UserTransferChannelRequest: {
                if (!OnUserTransferChannelRequest(cp, chr)) {
                    chr.SendPacket(ResCField.TransferChannelReqIgnored(OpsTransferChannel.TC_GAMESVR_DISCONNECTED));
                }
                return true;
            }
            case CP_UserMigrateToCashShopRequest: {
                if (!OnUserMigrateToCashShopRequest(client, chr)) {
                    chr.SendPacket(ResCField.TransferChannelReqIgnored(OpsTransferChannel.TC_SHOPSVR_DISCONNECTED));
                }
                return true;
            }
            case CP_UserMove: {
                OnUserMove(cp, map, chr);
                return true;
            }
            case CP_UserSitRequest: {
                OnUserSitRequest(cp, chr);
                return true;
            }
            case CP_UserPortableChairSitRequest: {
                OnUserPortableChairSitRequest(cp, chr);
                return true;
            }
            case CP_UserMeleeAttack:
            case CP_UserShootAttack:
            case CP_UserMagicAttack:
            case CP_UserBodyAttack: {
                OnUserAttack(chr, header, cp);
                return true;
            }
            case CP_UserHit: {
                OnUserHit(chr, cp);
                return true;
            }
            case CP_UserChat: {
                OnUserChat(chr, map, cp);
                return true;
            }
            case CP_UserADBoardClose: {
                chr.setADBoard(null);
                map.broadcastMessage(ResCUser.UserADBoard(chr));
                return true;
            }
            case CP_UserEmotion: {
                OnUserEmotion(chr, cp);
                return true;
            }
            case CP_UserActivateEffectItem: {
                OnUserActivateEffectItem(chr, cp);
                return true;
            }
            case CP_UserMonsterBookSetCover: {
                OnUserMonsterBookSetCover(chr, cp);
                return true;
            }
            case CP_UserSelectNpc: {
                OnUserSelectNpc(chr, cp);
                return true;
            }
            case CP_UserRemoteShopOpenRequest: {
                short item_slot = cp.Decode2();
                ReqCMiniRoomBaseDlg.RemoteStore(chr, item_slot);
                return true;
            }
            case CP_UserScriptMessageAnswer: {
                ReqCScriptMan.OnScriptMessageAnswer(chr, cp);
                return true;
            }
            case CP_UserShopRequest: {
                if (chr.getDebugShop() != null) {
                    DebugShop.OnUserShopRequestHook(chr, cp);
                    return true;
                }
                ReqCShopDlg.OnPacket(cp, client);
                return true;
            }
            case CP_UserTrunkRequest: {
                ReqCTrunkDlg.OnPacket(cp, client);
                return true;
            }
            case CP_UserEntrustedShopRequest: {
                byte es_req = cp.Decode1();
                long cash_item_uid = cp.Decode8();
                OnUserEntrustedShopRequest(map, chr, es_req, cash_item_uid);
                return true;
            }
            case CP_UserStoreBankRequest: {
                return true;
            }
            case CP_UserEffectLocal: {
                // merchant?
                byte unk = cp.Decode1();
                return true;
            }
            case CP_UserParcelRequest: {
                return ReqCParcelDlg.Accept(client, cp);
            }
            case CP_ShopScannerRequest: {
                OnShopScannerRequest(chr, cp);
                return true;
            }
            case CP_ShopLinkRequest: {
                int shop_id = cp.Decode4();
                int map_id = cp.Decode4();
                InventoryHandler.OwlWarp(client, shop_id, map_id);
                return true;
            }
            case CP_AdminShopRequest: {
                return true;
            }
            case CP_UserGatherItemRequest: {
                int timestamp = cp.Decode4();
                byte slot_type = cp.Decode1();
                OnUserGatherItemRequest(chr, slot_type);
                return true;
            }
            case CP_UserSortItemRequest: {
                int timestamp = cp.Decode4();
                byte slot_type = cp.Decode1();
                OnUserSortItemRequest(chr, slot_type);
                return true;
            }
            case CP_UserChangeSlotPositionRequest: {
                int timestamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
                byte slot_type = cp.Decode1();
                short slot_from = cp.Decode2();
                short slot_to = cp.Decode2();
                short quantity = cp.Decode2();
                OnUserChangeSlotPositionRequest(chr, slot_type, slot_from, slot_to, quantity);
                return true;
            }
            case CP_UserStatChangeItemUseRequest: {
                OnUserStatChangeItemUseRequest(chr, cp);
                return true;
            }
            case CP_UserStatChangeItemCancelRequest: {
                OnUserStatChangeItemCancelRequest(chr, cp);
                return true;
            }
            case CP_UserMobSummonItemUseRequest: {
                int timestamp = cp.Decode4();
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4();
                boolean ret = OnUserMobSummonItemUseRequest(chr, item_slot, item_id);
                chr.SendPacket(ResCField.MobSummonItemUseResult(ret));
                chr.sendStatChanged(true); // unlock is needed.
                return true;
            }
            case CP_UserPetFoodItemUseRequest: {
                int timestamp = cp.Decode4();
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4();
                OnUserPetFoodItemUseRequest(chr, item_slot, item_id);
                return true;
            }
            case CP_UserTamingMobFoodItemUseRequest: {
                int timestamp = cp.Decode4();
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4();
                OnUserTamingMobFoodItemUseRequest(map, chr, item_slot, item_id);
                return true;
            }
            case CP_UserScriptItemUseRequest: {
                InventoryHandler.UseScriptedNPCItem(cp, client, chr);
                return true;
            }
            case CP_UserConsumeCashItemUseRequest: {
                if (!OnUserConsumeCashItemUseRequest(map, chr, cp)) {
                    chr.updateInv();
                }
                return true;
            }
            case CP_UserDestroyPetItemRequest: {
                // // 期限切れデンデン使用時のステータス更新とPointShopへ入場準備
                chr.sendStatChanged(true); // OK, CANCEL 有効化
                return true;
            }
            case CP_UserBridleItemUseRequest: {
                int timestamp = cp.Decode4();
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4();
                int mob_oid = cp.Decode4();
                OnUserBridleItemUseRequest(map, chr, item_slot, item_id, mob_oid);
                return true;
            }
            case CP_UserSkillLearnItemUseRequest: {
                int time_stamp = cp.Decode4();
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4();
                OnUserSkillLearnItemUseRequest(map, chr, item_slot, item_id);
                //chr.saveToDB(false, false);
                return true;
            }
            case CP_UserSkillResetItemUseRequest: {
                int timestamp = cp.Decode4();
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4(); // 2500000
                // not coded.
                chr.sendStatChanged(true);
                return true;
            }
            case CP_JMS_MONSTERBOOK_SET: {
                int timestamp = cp.Decode4(); // 2114843894
                int item_slot = cp.Decode4();
                int song_time = cp.Decode4(); // 2560000
                // not coded.
                chr.sendStatChanged(true);
                return true;
            }
            case CP_UserShopScannerItemUseRequest: {
                OnUserShopScannerItemUseRequest(chr, cp);
                return true;
            }
            case CP_UserMapTransferItemUseRequest: {
                OnUserMapTransferItemUseRequest(chr, cp);
                return true;
            }
            case CP_UserPortalScrollUseRequest: {
                int time_stamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4();
                OnUserPortalScrollUseRequest(chr, item_slot, item_id);
                return true;
            }
            case CP_UserUpgradeItemUseRequest:
            case CP_UserHyperUpgradeItemUseRequest:
            case CP_UserItemOptionUpgradeItemUseRequest: {
                int timestamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
                short item_slot = cp.Decode2();
                short equip_slot = cp.Decode2();
                OnUserUpgradeItemUseRequest(map, chr, item_slot, equip_slot, 0);
                return true;
            }
            case CP_UserItemReleaseRequest: {
                int timestamp = cp.Decode4();
                short item_slot = cp.Decode2();
                short equip_slot = cp.Decode2();
                OnUserItemReleaseRequest(map, chr, item_slot, equip_slot);
                return true;
            }
            case CP_UserAbilityUpRequest: {
                OnUserAbilityUpRequest(chr, cp);
                return true;
            }
            case CP_UserAbilityMassUpRequest: {
                OnUserAbilityMassUpRequest(chr, cp);
                return true;
            }
            case CP_UserChangeStatRequest: {
                OnUserChangeStatRequest(chr, cp);
                return true;
            }
            case CP_UserSkillUpRequest: {
                OnUserSkillUpRequest(chr, cp);
                return true;
            }
            case CP_UserSkillUseRequest: {
                OnUserSkillUseRequest(chr, cp);
                return true;
            }
            case CP_UserSkillCancelRequest: {
                OnUserSkillCancelRequest(chr, cp);
                return true;
            }
            case CP_UserSkillPrepareRequest: {
                OnUserSkillPrepareRequest(chr, cp);
                return true;
            }
            case CP_UserDropMoneyRequest: {
                OnUserDropMoneyRequest(chr, cp);
                return true;
            }
            case CP_UserGivePopularityRequest: {
                OnUserGivePopularityRequest(chr, cp);
                return true;
            }
            case CP_UserCharacterInfoRequest: {
                OnCharacterInfoRequest(cp, chr, map);
                return true;
            }
            case CP_UserActivatePetRequest: {
                OnUserActivatePetRequest(chr, cp);
                return true;
            }
            case CP_UserTemporaryStatUpdateRequest: {
                TacosTask.doCharacterTask_Buff(chr, System.currentTimeMillis());
                return true;
            }
            case CP_UserPortalScriptRequest: {
                // play portal SE before character tries entering portal.
                chr.SendPacket(WrapCUserLocal.EffectLocal(OpsUserEffect.UserEffect_PlayPortalSE));
                if (!OnUserPortalScriptRequest(chr, cp)) {
                    chr.SendPacket(ResCField.TransferFieldReqIgnored(OpsTransferField.TF_DISABLED_PORTAL));
                }
                return true;
            }
            case CP_UserPortalTeleportRequest: {
                OnUserPortalTeleportRequest(chr, cp);
                return true;
            }
            case CP_UserMapTransferRequest: {
                OnUserMapTransferRequest(chr, cp);
                return true;
            }
            case CP_UserQuestRequest: {
                OnUserQuestRequest(chr, cp);
                return true;
            }
            case CP_UserCalcDamageStatSetRequest: {
                // @006A
                // バフを獲得するアイテムを使用した際に送信されている
                // 利用用途が不明だが、アイテム利用時ではなくてこちらが送信されたときにバフを有効にすべきなのかもしれない
                return true;
            }
            case CP_UserMacroSysDataModified: {
                return ReqCFuncKeyMappedMan.OnPacket(header, cp, client);
            }
            case CP_UserItemMakeRequest: {
                ItemMakerHandler.OnItemMakeRequest(cp, chr);
                return true;
            }
            case CP_UserUseGachaponBoxRequest: {
                short slot = cp.Decode2();
                int item_id = cp.Decode4();
                int reward = InventoryHandler.UseTreasureChest(chr, slot, item_id);
                if (reward != 0) {
                    chr.SendPacket(ResCWvsContext.SuccessInUseGachaponBox(item_id));
                    chr.SendPacket(WrapCUserLocal.getShowItemGain(reward, (short) 1, true));
                } else {
                    chr.sendStatChanged(true);
                }
                return true;
            }
            case CP_UserRepairDurabilityAll: {
                OnUserRepairDurabilityAll(chr, cp);
                return true;
            }
            case CP_UserRepairDurability: {
                OnUserRepairDurability(chr, cp);
                return true;
            }
            case CP_UserFollowCharacterRequest: {
                OnUserFollowCharacterRequest(chr, cp);
                return true;
            }
            case CP_SetPassenserResult: {
                OnSetPassenserResult(chr, cp);
                return true;
            }
            case CP_GroupMessage: {
                OnGroupMessage(chr, cp);
                return true;
            }
            case CP_Whisper: {
                OnWhisper(chr, cp);
                return true;
            }
            case CP_Messenger: {
                return ReqCUIMessenger.OnPacket(chr, header, cp);
            }
            case CP_MiniRoom: {
                return ReqCMiniRoomBaseDlg.OnMiniRoom(map, chr, cp);
            }
            case CP_PartyRequest: {
                OnPartyRequest(chr, cp);
                return true;
            }
            case CP_PartyResult: {
                OnPartyResult(chr, cp);
                return true;
            }
            case CP_GuildRequest: {
                GuildHandler.Guild(cp, client);
                return true;
            }
            case CP_GuildResult: {
                GuildHandler.DenyGuildRequest(cp, client);
                return true;
            }
            case CP_Admin: {
                ReqSub_Admin.OnAdmin(chr, cp);
                return true;
            }
            case CP_Log: {
                String text = cp.DecodeStr();
                DebugLogger.AdminLog("[OnLog] " + text);
                return true;
            }
            case CP_FriendRequest: {
                ReqSub_FriendRequest.OnFriendRequest(cp, chr);
                return true;
            }
            case CP_MemoRequest: {
                OnMemoRequest(chr, cp);
                return true;
            }
            case CP_EnterTownPortalRequest: {
                ReqCTownPortalPool.TryEnterTownPortal(cp, client);
                return true;
            }
            case CP_FuncKeyMappedModified: {
                return ReqCFuncKeyMappedMan.OnPacket(header, cp, client);
            }
            case CP_RPSGame: {
                return ReqCRPSGameDlg.OnPacket(client, header, cp);
            }
            case CP_MarriageRequest: {
                OnMarriageRequest(chr, cp);
                return true;
            }
            case CP_AllianceRequest: {
                AllianceHandler.HandleAlliance(cp, client, false);
                return true;
            }
            case CP_AllianceResult: {
                AllianceHandler.HandleAlliance(cp, client, true);
                return true;
            }
            case CP_GuildBBS: {
                BBSHandler.BBSOperation(cp, client);
                return true;
            }
            case CP_JMS_InstancePortalEnter: {
                int portal_id = cp.Decode4();
                byte flag = cp.Decode1();
                // 749050200
                MapleDynamicPortal dynamic_portal = chr.getMap().findDynamicPortal(portal_id);
                if (dynamic_portal == null) {
                    chr.sendStatChanged(true);
                    return true;
                }
                dynamic_portal.warp(chr);
                return true;
            }
            case CP_JMS_InstancePortalCreate: {
                int timestamp = cp.Decode4();
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4(); // 2420004
                short x = cp.Decode2();
                short y = cp.Decode2();
                MapleDynamicPortal dynamic_portal = new MapleDynamicPortal(item_id, 749050200, x, y);
                map.addMapObject(dynamic_portal);
                map.broadcastMessage(Res_JMS_CInstancePortalPool.InstancePortalCreated(dynamic_portal));
                chr.sendStatChanged(true);
                return true;
            }
            case CP_UserMigrateToITCRequest: {
                if (!OnUserMigrateToITCRequest(client, chr)) {
                    chr.SendPacket(ResCField.TransferChannelReqIgnored(OpsTransferChannel.TC_ITCSVR_DISCONNECTED));
                }
                return true;
            }
            case CP_UserExpUpItemUseRequest: {
                int timestamp = cp.Decode4();
                short nPOS = cp.Decode2();
                int nItemID = cp.Decode4();
                OnUserExpUpItemUseRequest(chr, nPOS, nItemID);
                return true;
            }
            case CP_UserTempExpUseRequest: {
                int timestamp = cp.Decode4();
                OnUserTempExpUseRequest(chr);
                return true;
            }
            case CP_JMS_JUKEBOX: {
                int timestamp = cp.Decode4();
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4(); // 2150001
                int song_time = cp.Decode4(); // 113788
                map.startJukebox(chr.getName(), item_id);
                chr.sendStatChanged(true);
                return true;
            }
            case CP_TalkToTutor: {
                OnTalkToTutor(chr);
                return true;
            }
            case CP_RequestIncCombo: {
                chr.sendIncCombo();
                return true;
            }
            case CP_JMS_Poll_Answer: {
                int question_id = cp.Decode4();
                int answer_id = cp.Decode4();
                chr.DebugMsg("Poll : " + question_id + ", " + answer_id);
                return true;
            }
            case CP_QuickslotKeyMappedModified: {
                return ReqCFuncKeyMappedMan.OnPacket(header, cp, client);
            }
            case CP_UpdateScreenSetting: // 解像度変更
            {
                byte screen = cp.Decode1(); // 00 = 800x600, 01 = 1024x768
                byte unk2 = cp.Decode1();
                if (Region.check(Region.GMS)) {
                    return true;
                }
                byte unk3 = cp.Decode1();
                byte unk4 = cp.Decode1();
                return true;
            }
            case CP_JMS_FarmEnter:
            case CP_JMS_FarmLeave: {
                Req_Farm.OnPacket(header, cp, client);
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean OnPacket_ITC(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_UpdateScreenSetting: {
                return true;
            }
            default: {
                break;
            }
        }
        MapleCharacter chr = client.getPlayer();

        if (chr == null) {
            DebugLogger.ErrorLog("character is not online.");
            return false;
        }

        switch (header) {
            case CP_UserTransferFieldRequest: {
                OnUserTransferFieldRequest_ITC(chr);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnPacket_CS(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        switch (header) {
            case CP_UpdateScreenSetting: {
                return true;
            }
            default: {
                break;
            }
        }
        MapleCharacter chr = client.getPlayer();

        if (chr == null) {
            DebugLogger.ErrorLog("character is not online.");
            return false;
        }

        switch (header) {
            case CP_UserTransferFieldRequest: {
                OnUserTransferFieldRequest_CS(chr);
                return true;
            }
            // アバターランダムボックスのオープン処理
            case CP_CashGachaponOpenRequest: {
                long box_SN = cp.Decode8();
                ReqCCashShop.OnGachaponOpen(client, box_SN);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnFamilyPacket(MapleClient c, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return true;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return true;
        }

        switch (header) {
            case CP_FamilyChartRequest: {
                FamilyHandler.RequestFamily(cp, c);
                return true;
            }
            case CP_FamilyInfoRequest: {
                FamilyHandler.OpenFamily(cp, c);
                return true;
            }
            case CP_FamilyRegisterJunior: {
                FamilyHandler.FamilyOperation(cp, c);
                return true;
            }
            case CP_FamilyUnregisterJunior: {
                FamilyHandler.DeleteJunior(cp, c);
                return true;
            }
            case CP_FamilyUnregisterParent: {
                FamilyHandler.DeleteSenior(cp, c);
                return true;
            }
            case CP_FamilyJoinResult: {
                FamilyHandler.AcceptFamily(cp, c);
                return true;
            }
            case CP_FamilyUsePrivilege: {
                FamilyHandler.UseFamily(cp, c);
                return true;
            }
            case CP_FamilySetPrecept: {
                FamilyHandler.FamilyPrecept(cp, c);
                return true;
            }
            case CP_FamilySummonResult: {
                FamilyHandler.FamilySummon(cp, c);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnUserTransferFieldRequest(ClientPacket cp, MapleCharacter chr) {
        boolean isKMS95orLater = Version.GreaterOrEqual(Region.KMS, 95) || Version.GreaterOrEqual(Region.KMST, 330) || Region.check(Region.IMS) || Region.check(Region.MSEA); // not in KMST391
        short unk1 = isKMS95orLater ? cp.Decode2() : 0; // ?_?
        int unk2 = isKMS95orLater ? cp.Decode4() : 0; // 0
        byte portal_count = cp.Decode1();
        int map_id_to = cp.Decode4(); // -1 = use portal, 0 = revivie, id = /map command.
        int gms111_checksum = Version.GreaterOrEqual(Region.GMS, 111) ? cp.Decode4() : 0;
        String portal_name = cp.DecodeStr();
        boolean isPortal = !portal_name.equals("");
        short x = isPortal ? cp.Decode2() : 0;
        short y = isPortal ? cp.Decode2() : 0;
        byte unk3 = cp.Decode1();
        byte revive_type = cp.Decode1(); // revive_type -> JMS302 = 4 bytes

        // map_id is -1. (in JMS.)
        return chr.usePortal(isPortal, map_id_to, portal_name, revive_type);
    }

    public static void OnUserTransferFieldRequest_ITC(MapleCharacter chr) {
        chr.getWorld().addMigratingPlayer(chr);
        chr.getWorld().getITC().getOnlinePlayers().remove(chr);
        try {
            chr.sendMigrateCommand(chr.getWorld().getChannelServer(chr.getChannelId()));
        } finally {
            chr.saveToDB(false, true);
        }
    }

    public static void OnUserTransferFieldRequest_CS(MapleCharacter chr) {
        chr.getWorld().addMigratingPlayer(chr);
        chr.getWorld().getCashShop().getOnlinePlayers().remove(chr);
        try {
            chr.sendMigrateCommand(chr.getWorld().getChannelServer(chr.getChannelId()));
        } finally {
            chr.saveToDB(false, true);
        }
    }

    public static boolean OnUserTransferChannelRequest(ClientPacket cp, MapleCharacter chr) {
        int channel = cp.Decode1(); // from 0.

        if (!chr.isAlive() || FieldOpt.FIELDOPT_MIGRATELIMIT.check(chr.getMap().getFieldLimit())) {
            return false;
        }

        LazyDatabase.saveData(chr);
        return chr.changeChannel(channel + 1);
    }

    public static boolean OnUserMigrateToCashShopRequest(MapleClient c, MapleCharacter chr) {
        // temporary off
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            return false;
        }
        if (!chr.isAlive()) {
            return false;
        }

        DebugLogger.DebugLog("OnUserMigrateToCashShopRequest : " + chr.getWorldId() + ", " + chr.getChannelId());

        chr.changeRemoval();
        chr.getWorld().addMigratingPlayer(chr);
        chr.getChannelServer().getOnlinePlayers().remove(chr);
        chr.sendMigrateCommand(chr.getWorld().getCashShop());
        chr.saveToDB(false, false);
        LazyDatabase.saveData(chr);
        chr.getMap().userLeaveField(chr);
        return true;
    }

    public static boolean OnUserMove(ClientPacket cp, MapleMap map, MapleCharacter chr) {
        if (chr.isHidden()) {
            return false;
        }

        // not in TWMS148, CMS104, but in TWMS125
        if (Version.GreaterOrEqual(Region.JMS, 186) || Version.Between(Region.TWMS, 121, 125) || Version.Between(Region.CMS, 85, 88) || Version.GreaterOrEqual(Region.GMS, 95) || Version.Equal(Region.BMS, 24)) {
            cp.Decode4(); // -1
            cp.Decode4(); // -1
        }

        cp.Decode1(); // unk

        // not in TWMS148, CMS104, but in TWMS125
        if (Version.GreaterOrEqual(Region.JMS, 186) || Version.Between(Region.TWMS, 121, 125) || Version.Between(Region.CMS, 85, 88) || Version.GreaterOrEqual(Region.GMS, 95) || Version.Equal(Region.BMS, 24)) {
            cp.Decode4(); // -1
            cp.Decode4(); // -1
            cp.Decode4();
            cp.Decode4();
        }

        if (Version.LessOrEqual(Region.KMS, 65)) {
            // nothing
        } else {
            // not in JMS147
            if (ServerConfig.JMS164orLater() || Version.Equal(Region.BMS, 24)) {
                cp.Decode4();
            }
        }

        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
            cp.Decode4();
        }

        ParseCMovePath move_path = new ParseCMovePath();
        if (move_path.Decode(cp)) {
            map.userMove(chr, move_path);
            move_path.update(chr);
        }

        // follow.
        if (chr.getPassenger() != 0) {
            MapleCharacter passenger = map.getCharacterById(chr.getPassenger());
            if (passenger != null) {
                map.userMove(passenger, move_path); // test
                move_path.update(passenger); // for when passenger cancels follow.
                passenger.SendPacket(ResCUserLocal.UserPassiveMove(move_path));
                // to keep correct passenger coordinate for remote users requires calculation of actual passenger move path.
                //map.broadcastMessage(ResCUser.UserFollowCharacter(passenger, false));
            } else {
                chr.setPassenger(0);
            }
        }

        // unofficial.
        chr.movePetEx(move_path);
        return true;
    }

    public static boolean OnUserSitRequest(ClientPacket cp, MapleCharacter chr) {
        short map_chair_id = cp.Decode2();

        boolean is_cancel = (map_chair_id == -1);

        if (is_cancel) {
            // 釣り
            if (chr.getChair() == 3011000) {
                chr.cancelFishingTask();
            }
            chr.getMap().broadcastMessage(chr, ResCUserRemote.UserSetActivePortableChair(chr.getId(), 0), false);
        }

        chr.setChair(is_cancel ? 0 : map_chair_id);
        chr.SendPacket(ResCUserLocal.UserSitResult(map_chair_id));
        return true;
    }

    public static boolean OnUserPortableChairSitRequest(ClientPacket cp, MapleCharacter chr) {
        int item_id = cp.Decode4();

        IItem toUse = chr.getInventory(MapleInventoryType.SETUP).findById(item_id);
        if (toUse == null) {
            return false;
        }

        // 釣り
        if (item_id == 3011000) {
            int fishing_level = 0;
            for (IItem item : chr.getInventory(MapleInventoryType.CASH).list()) {
                if (fishing_level <= 1 && item.getItemId() == 5340000) {
                    fishing_level = 1;
                }
                if (item.getItemId() == 5340001) {
                    fishing_level = 2;
                    break;
                }
            }
            if (fishing_level > 0) {
                chr.startFishingTask(fishing_level == 2);
            }
        }

        chr.setChair(item_id);
        chr.getMap().broadcastMessage(chr, ResCUserRemote.UserSetActivePortableChair(chr.getId(), item_id), false);
        chr.updateInv();
        return true;
    }

    // BMS, CUser::OnAttack
    public static boolean OnUserAttack(MapleCharacter chr, ClientPacketHeader header, ClientPacket cp) {
        ParseCUser_Attack attack = ParseCUser_Attack.parse(chr, header, cp);
        attack.setCritical(chr);

        MapleMap map = chr.getMap();
        boolean is_skill_attack = attack.skill != 0;
        if (is_skill_attack) {
            ISkill skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            int skillLevel = chr.getSkillLevel(skill);
            MapleStatEffect skill_effect = attack.getAttackEffect(chr, skillLevel, skill);
            if (skill_effect == null) {
                DebugLogger.ErrorLog("attack : err 1.");
                return false;
            }
            if (0 < skill_effect.getCooldown()) {
                OpsSkill ops_skill = OpsSkill.find(attack.skill);
                if (chr.getCoolTime().check(ops_skill)) {
                    return false;
                }
                chr.getCoolTime().add(ops_skill, skill_effect.getCooldown());
            }
            if (attack.skill != OpsSkill.CLERIC_HEAL.get()) {
                skill_effect.applyTo(chr);
            }
        }
        if (!ContentState.CS_LOCK_LOSING_THRWOING.get()) {
            // consume star code.
        }
        ISkill eaterSkill = SkillFactory.getSkill(GameConstants.getMPEaterForJob(chr.getJob()));
        int eaterLevel = chr.getSkillLevel(eaterSkill);
        boolean is_meso_explosion = attack.skill == OpsSkill.THIEFMASTER_MESO_EXPLOSION.get();
        boolean is_pick_pocket = false;
        ISkill skill_pick_pocket = null;
        MapleStatEffect skill_effect_pick_pocket = null;
        if (is_pick_pocket) {
            skill_pick_pocket = SkillFactory.getSkill(OpsSkill.THIEFMASTER_PICKPOCKET.get());
            skill_effect_pick_pocket = skill_pick_pocket.getEffect(30); // level.
        }

        // for remote users.
        map.broadcastMessageTo(chr, ResCUserRemote.UserAttack(chr, attack), chr.getPosition());
        boolean is_steal = attack.skill == OpsSkill.THIEF_STEAL.get();
        for (Map.Entry<Integer, ArrayList<Integer>> entry : attack.damages.entrySet()) {
            MapleMonster monster = map.getMonsterByOid(entry.getKey());
            if (monster == null) {
                DebugLogger.ErrorLog("attack : err 3.");
                continue;
            }
            int total_damage = 0;
            for (Integer damage : entry.getValue()) {
                total_damage += damage & 0x7FFFFFFF;
                // pick pocket.
                if (is_pick_pocket && skill_effect_pick_pocket != null && !is_meso_explosion) {
                    if (skill_effect_pick_pocket.makeChanceResult()) {
                        int maxmeso = skill_effect_pick_pocket.getX();
                        map.spawnMesoDrop(Math.min((int) Math.max(((double) (damage & 0x7FFFFFFF) / (double) 20000) * (double) maxmeso, (double) 1), maxmeso), new Point((int) (monster.getPosition().getX() + Randomizer.nextInt(100) - 50), (int) (monster.getPosition().getY())), monster, chr, true, (byte) 0);
                    }
                }
            }
            if (total_damage < 0) {
                DebugLogger.ErrorLog("attack : err 4.");
                continue;
            }
            monster.damage(chr, total_damage, true, attack.skill);
            chr.checkMonsterAggro(monster);
            if (eaterSkill != null && 0 < eaterLevel) {
                eaterSkill.getEffect(eaterLevel).applyPassive(chr, monster);
            }
            if (is_steal) {
                monster.handleSteal(chr);
            }
        }
        // meso explosion.
        if (is_meso_explosion) {
            for (int drop_id : attack.allMeso) {
                MapleMapItem mmi = (MapleMapItem) map.getMapObject(drop_id, MapleMapObjectType.ITEM);
                if (mmi == null || mmi.getMeso() <= 0) {
                    DebugLogger.ErrorLog("attack : err meso explosion.");
                    continue;
                }
                map.removeMapObject(mmi);
                map.broadcastMessage(ResCDropPool.DropLeaveField(mmi, ResCDropPool.LeaveType.MESO_EXPLOSION));
            }
        }
        return true;
    }

    public static boolean OnUserHit(MapleCharacter chr, ClientPacket cp) {
        MapleMap map = chr.getMap();
        ResCUserRemote.UserHitData uhd = new ResCUserRemote.UserHitData();

        uhd.dwCharacterID = chr.getId();

        int unk1 = Version.GreaterOrEqual(Region.JMS, 302) ? cp.Decode4() : 0;
        int time = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
        uhd.nAttackIdx = cp.Decode1();
        byte nMagicElemAttr = Version.LessOrEqual(Region.KMS, 43) ? 0 : cp.Decode1();
        uhd.nDamage = cp.Decode4();
        byte unk3 = Version.GreaterOrEqual(Region.JMS, 302) ? cp.Decode1() : 0;
        byte unk4 = Version.GreaterOrEqual(Region.JMS, 302) ? cp.Decode1() : 0;

        boolean is_mob_attack = false;
        int mpattack = 0;
        boolean is_pg = false;
        boolean isDeadlyAttack = false;
        PlayerStats stats = chr.getStat();
        OpsAttackIndex ops = OpsAttackIndex.find(uhd.nAttackIdx);
        int m_dwMobID = 0;

        switch (ops) {
            case AttackIndex_Counter:
            case AttackIndex_Obstacle:
            case AttackIndex_Stat: {
                // no mob.
                short dwObstacleData = cp.Decode2();
                break;
            }
            default: {
                if (uhd.nAttackIdx < 0) {
                    // not coded.
                    chr.DebugMsg("OnUserHit : not coded, nAttackIdx =" + uhd.nAttackIdx + ", nDamage = " + uhd.nDamage);
                    return true;
                }
                // mob attack.
            }
            case AttackIndex_Mob_Physical:
            case AttackIndex_Mob_Magic: {
                // mob attack.
                is_mob_attack = true;
                uhd.dwTemplateID = cp.Decode4(); // mob wz id.
                m_dwMobID = cp.Decode4(); // mob object id.
                uhd.nLeft = cp.Decode1();
                uhd.nReflect = cp.Decode1();
                byte unk7 = cp.Decode1();
                //
                if (uhd.nReflect != 0 || unk7 == 2) {
                    // 1-4-1-2-2-2-2
                    uhd.bPowerGuard = cp.Decode1();
                    uhd.m_dwMobID = cp.Decode4(); // mob object id.
                    uhd.nHitAction = cp.Decode1();
                    uhd.ptHit_x = cp.Decode2();
                    uhd.ptHit_y = cp.Decode2();
                    short chr_x = cp.Decode2();
                    short chr_y = cp.Decode2();
                }
                break;
            }
        }

        short unk8 = Version.GreaterOrEqual(Region.JMS, 187) ? cp.Decode1() : 0;

        uhd.nDelta = uhd.nDamage;
        if (!is_mob_attack) {
            if (uhd.nDamage < 0) {
                // hack.
                return true;
            }
            map.broadcastMessage(chr, ResCUserRemote.UserHit(uhd), false);
            chr.getStat().setHp(chr.getStat().getHp() - uhd.nDamage);
            chr.sendStatChanged();
            return true;
        }

        MapleMonster monster = map.getMonsterByOid(m_dwMobID);
        if (monster == null || monster.getId() != uhd.dwTemplateID) {
            return true;
        }
        // fake skill.
        if (uhd.nDamage == -1) {
            OpsSkill fake_skill = chr.getFakeSkill();
            if (fake_skill == OpsSkill.UNKNOWN) {
                // hack.
                return true;
            }
            uhd.nSkillID = fake_skill.get();
            map.broadcastMessage(chr, ResCUserRemote.UserHit(uhd), false);
            return true;
        }
        if (uhd.nDamage < 0) {
            return true;
        }
        // MISS
        if (uhd.nDamage == 0) {
            map.broadcastMessage(chr, ResCUserRemote.UserHit(uhd), false);
            return true;
        }
        MobAttackInfo attackInfo = WzXML.MOB.getMobAttackInfo(monster, uhd.nAttackIdx);
        if (attackInfo != null) {
            // deadlyAttack, 1:1
            if (attackInfo.isDeadlyAttack()) {
                uhd.nDelta = chr.getStat().getHp() - 1;
                if (uhd.nDelta == 0) {
                    uhd.nDelta = 1;
                }
                map.broadcastMessage(chr, ResCUserRemote.UserHit(uhd), false);
                chr.getStat().setHp(1);
                chr.getStat().setMp(1);
                chr.sendStatChanged();
                chr.DebugMsg("deadlyAttack : " + uhd.nDamage + " -> " + uhd.nDelta);
                return true;
            }
            // mpBurn
            int mp_burn = (short) attackInfo.getMpBurn(); // 9400113, BodyGuard B meme.
            if (mp_burn != 0) {
                map.broadcastMessage(chr, ResCUserRemote.UserHit(uhd), false);
                int nMP = chr.getStat().getMp() - mp_burn;
                if (chr.getStat().getMaxMp() < nMP) {
                    nMP = chr.getStat().getMaxMp();
                }
                if (nMP < 0) {
                    nMP = 0;
                }
                chr.getStat().setMp(nMP);
                chr.sendStatChanged();
                chr.DebugMsg("mpBurn : " + uhd.nDamage + " -> " + uhd.nDelta + ", MP = " + mp_burn);
                return true;
            }
            // mob skill.
            MobSkill mob_skill = WzXML.SKILL.getMobSkillData(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
            if (mob_skill != null) {
                if (uhd.nDamage != 0) {
                    mob_skill.applyEffect(chr, monster, false);
                }
            }
            monster.setMp(monster.getMp() - attackInfo.getMpCon());
        }
        if (0 < uhd.nReflect) {
            MobSkill skill = WzXML.SKILL.getMobSkillData(0, uhd.nReflect);
            if (skill != null) {
                skill.applyEffect(chr, monster, false);
            }
        }
        if (uhd.nReflect != 0) {
            if (uhd.bPowerGuard != 0) {
                Integer rate = 0; // PG SKILL.
                int reflect_damage = (int) (uhd.nDamage / 100.0 * rate);
                uhd.nDelta = uhd.nDamage - reflect_damage;
                monster.damage(chr, reflect_damage, true);
                chr.getStat().setHp(chr.getStat().getHp() - uhd.nDelta);
                map.broadcastMessage(chr, ResCUserRemote.UserHit(uhd), false);
                chr.sendStatChanged();
                chr.DebugMsg("PowerGuard : " + uhd.nDamage + " -> " + uhd.nDelta + ", " + reflect_damage);
                return true;
            }
        }
        Integer magic_guard_rate = 0; // MG SKILL
        if (magic_guard_rate != 0) {
            int mp_damage = (int) (uhd.nDamage / 100.0 * magic_guard_rate);
            if (chr.getStat().getMp() < mp_damage) {
                mp_damage = chr.getStat().getMp();
            }
            int hp_damage = uhd.nDamage - mp_damage;
            map.broadcastMessage(chr, ResCUserRemote.UserHit(uhd), false);
            chr.getStat().setHp(chr.getStat().getHp() - hp_damage);
            chr.getStat().setMp(chr.getStat().getMp() - mp_damage);
            chr.sendStatChanged();
            chr.DebugMsg("MagicGuard : " + uhd.nDamage + " -> " + hp_damage + ", " + mp_damage);
            return true;
        }
        Integer meso_guard_rate = 0; // MESO GUARD SKILL.
        if (meso_guard_rate != 0) {
            int meso_damage = (int) (uhd.nDamage / 100.0 * meso_guard_rate);
            if (chr.getMeso() < meso_damage) {
                meso_damage = chr.getMeso();
            }
            int hp_damage = uhd.nDamage - meso_damage;
            map.broadcastMessage(chr, ResCUserRemote.UserHit(uhd), false);
            chr.getStat().setHp(chr.getStat().getHp() - hp_damage);
            chr.setMeso(chr.getMeso() - meso_damage);
            chr.sendStatChanged();
            chr.DebugMsg("MesoGuard : " + uhd.nDamage + " -> " + hp_damage + ", " + meso_damage);
            return true;
        }

        chr.DebugMsg("OnUserHit : nAttackIdx =" + uhd.nAttackIdx + ", nDamage = " + uhd.nDamage);
        map.broadcastMessage(chr, ResCUserRemote.UserHit(uhd), false);
        chr.getStat().setHp(chr.getStat().getHp() - uhd.nDamage);
        chr.sendStatChanged();
        return true;
    }

    public static boolean OnUserChat(MapleCharacter chr, MapleMap map, ClientPacket cp) {
        int timestamp = (ServerConfig.JMS180orLater() || Region.IsBMS()) ? cp.Decode4() : 0;
        String message = cp.DecodeStr();
        boolean bOnlyBalloon = (ServerConfig.JMS147orLater() || Region.IsBMS()) ? (cp.Decode1() != 0) : false; // skill macro

        if (!bOnlyBalloon) {
            // command.
            if (DebugCommand.checkCommand(chr, message)) {
                return true;
            }
        }

        map.broadcastMessage(ResCUser.UserChat(chr, message, bOnlyBalloon), chr.getPosition());
        return true;
    }

    public static boolean OnUserEmotion(MapleCharacter chr, ClientPacket cp) {
        int emotion_id = cp.Decode4();
        if (7 < emotion_id) {
            int item_id = 5160000 + emotion_id - 8;
            MapleInventoryType type = GameConstants.getInventoryType(item_id);
            if (chr.getInventory(type).findById(item_id) == null) {
                return false;
            }
        }
        if (emotion_id <= 0) {
            return false;
        }
        MapleMap map = chr.getMap();
        map.broadcastMessage(chr, ResCUserRemote.UserEmotion(chr, emotion_id), false);
        return true;
    }

    // CWvsContext::SendActiveEffectItemChange
    public static boolean OnUserActivateEffectItem(MapleCharacter chr, ClientPacket cp) {
        int nEffectItemID = cp.Decode4();
        int type = nEffectItemID / 10000;

        if (nEffectItemID != 0) {
            switch (type) {
                case 429: {
                    // is_non_cash_effect_item
                    if (chr.getInventory(MapleInventoryType.ETC).findById(nEffectItemID) == null) {
                        return false;
                    }
                    break;
                }
                case 501: {
                    if (chr.getInventory(MapleInventoryType.CASH).findById(nEffectItemID) == null) {
                        return false;
                    }
                    break;
                }
                default: {
                    return false;
                }
            }
        }

        chr.setActiveEffectItem(nEffectItemID);
        chr.getMap().broadcastMessage(chr, ResCUserRemote.UserSetActiveEffectItem(chr), false);
        return true;
    }

    public static boolean OnUserMonsterBookSetCover(MapleCharacter chr, ClientPacket cp) {
        int nMonsterBookCoverID = cp.Decode4();

        chr.setMonsterBookCover(nMonsterBookCoverID);
        chr.SendPacket(ResCWvsContext.MonsterBookSetCover(chr));
        return true;
    }

    public static boolean OnUserSelectNpc(MapleCharacter chr, ClientPacket cp) {
        int m_dwNpcId = cp.Decode4();
        short x = Version.LessOrEqual(Region.KMS, 1) ? 0 : cp.Decode2();
        short y = Version.LessOrEqual(Region.KMS, 1) ? 0 : cp.Decode2();

        MapleClient client = chr.getClient();
        MapleMap map = chr.getMap();
        MapleNPC npc = map.getNPCByOid(m_dwNpcId);

        if (npc == null) {
            DebugLogger.ErrorLog("OnUserSelectNpc : npc");
            return false;
        }
        if (chr.getConversation() != 0) {
            chr.DebugMsg("OnUserSelectNpc : getConversation = " + chr.getConversation());
            return false;
        }
        if (TacosNpcShop.checkNpcShop(chr, npc.getId())) {
            return true;
        }
        if (npc.hasShop()) {
            chr.DebugMsg("OnUserSelectNpc : " + npc.getId() + ", shop");
            chr.setConversation(1);
            npc.sendShop(client);
            return true;
        }

        chr.DebugMsg("OnUserSelectNpc : " + npc.getId());
        return TacosScriptNPC.getInstance().start(client, npc.getId());
    }

    public static boolean OnUserGivePopularityRequest(MapleCharacter chr, ClientPacket cp) {
        int target_id = cp.Decode4();
        byte mode = cp.Decode1();
        boolean is_up = mode != 0;

        if (chr.getLevel() < 15) {
            chr.SendPacket(ResCWvsContext.GivePopularityResult(OpsGivePopularity.GivePopularityRes_LevelLow, null, is_up, null));
            return false;
        }
        if (chr.getId() == target_id) {
            chr.SendPacket(ResCWvsContext.GivePopularityResult(OpsGivePopularity.GivePopularityRes_InvalidCharacterID, null, is_up, null));
            return false;
        }

        int famechange = mode == 0 ? -1 : 1;
        MapleCharacter target = (MapleCharacter) chr.getMap().getMapObject(target_id, MapleMapObjectType.PLAYER);
        switch (chr.canGiveFame(target)) {
            case OK:
                if (Math.abs(target.getFame() + famechange) <= 30000) {
                    target.addFame(famechange);
                    target.sendStatChanged();
                }
                chr.hasGivenFame(target);
                chr.SendPacket(ResCWvsContext.GivePopularityResult(OpsGivePopularity.GivePopularityRes_Success, chr, is_up, target));
                target.SendPacket(ResCWvsContext.GivePopularityResult(OpsGivePopularity.GivePopularityRes_Notify, chr, is_up, target));
                break;
            case NOT_TODAY:
                chr.SendPacket(ResCWvsContext.GivePopularityResult(OpsGivePopularity.GivePopularityRes_AlreadyDoneToday, null, is_up, null));
                break;
            case NOT_THIS_MONTH:
                chr.SendPacket(ResCWvsContext.GivePopularityResult(OpsGivePopularity.GivePopularityRes_AlreadyDoneTarget, null, is_up, null));
                break;
            default: {
                chr.SendPacket(ResCWvsContext.GivePopularityResult(OpsGivePopularity.GivePopularityRes_UnknownError, null, is_up, null));
                break;
            }
        }

        return true;
    }

    // CUser::OnCharacterInfoRequest
    public static final boolean OnCharacterInfoRequest(ClientPacket cp, MapleCharacter chr, MapleMap map) {
        // CCheatInspector::InspectExclRequestTime
        final int update_time = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
        final int m_dwCharacterId = cp.Decode4();
        final MapleCharacter player = map.getCharacterById(m_dwCharacterId); // CUser::FindUser

        if (player == null) {
            chr.updateStat();
            return false;
        }

        chr.SendPacket(ResCWvsContext.CharacterInfo(player, chr.getId() == m_dwCharacterId));
        return true;
    }

    public static final boolean OnUserActivatePetRequest(MapleCharacter chr, ClientPacket cp) {
        int timestamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
        short item_slot = cp.Decode2();
        byte flag = (Version.LessOrEqual(Region.KMS, 31) || Version.LessOrEqual(Region.JMS, 131) || Version.PostBB()) ? 1 : cp.Decode1();

        chr.spawnPet(item_slot, flag > 0 ? true : false);
        return true;
    }

    public static boolean OnUserEntrustedShopRequest(MapleMap map, MapleCharacter chr, byte es_req, long cash_item_uid) {
        chr.DebugMsg("OnUserEntrustedShopRequest : " + es_req + "," + cash_item_uid);
        // HiredMerchantHandler.UseHiredMerchant(c);
        if (OpsEntrustedShop.find(es_req) != OpsEntrustedShop.EntrustedShopReq_CheckOpenPossible) {
            return false;
        }

        chr.SendPacket(ResCWvsContext.EntrustedShopCheckResult(OpsEntrustedShop.EntrustedShopRes_OpenPossible));
        return true;
    }

    public static boolean OnShopScannerRequest(MapleCharacter chr, ClientPacket cp) {
        byte req = cp.Decode1();

        switch (OpsShopScanner.find(req)) {
            case ShopScannerReq_LoadHotList: {
                chr.SendPacket(ResCWvsContext.ShopScannerResult(OpsShopScanner.ShopScannerRes_LoadHotListResult));
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnShopScannerRequest : not coded " + req);
        return false;
    }

    public static boolean OnUserShopScannerItemUseRequest(MapleCharacter chr, ClientPacket cp) {
        short owl_slot = cp.Decode2(); // inlined
        int owl_item_id = cp.Decode4(); // inlined
        int target_item_id = cp.Decode4();
        int timestamp = cp.Decode4();

        IItem item_used = chr.getInventory(MapleInventoryType.USE).getItem(owl_slot);
        if (item_used == null || item_used.getItemId() != 2310000) {
            DebugLogger.ErrorLog("OnUserShopScannerItemUseRequest : invalid owl.");
            return false;
        }
        MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, owl_item_id, 1, true, false);
        final List<HiredMerchant> hms = chr.getChannelServer().searchMerchant(target_item_id);
        // not coded.
        chr.SendPacket(ResCWvsContext.ShopScannerResult(OpsShopScanner.ShopScannerRes_SearchResult));
        return true;
    }

    public static boolean OnUserMapTransferItemUseRequest(MapleCharacter chr, ClientPacket cp) {
        short slot = cp.Decode2();
        int item_id = cp.Decode4();
        byte cmd = cp.Decode1();

        MapleMap target_map = null;
        OpsMapTransfer ops_res = OpsMapTransfer.MapTransferRes_Unknown;
        // shared with cash item teleport rock, CWvsContext::RunMapTransferItem
        switch (cmd) {
            case 0: {
                int target_map_id = cp.Decode4();
                for (int map_id : chr.getRegRocks()) {
                    if (map_id == target_map_id) {
                        target_map = chr.findMap(target_map_id);
                        if (target_map != null) {
                            ops_res = OpsMapTransfer.MapTransferRes_Use;
                        }
                        break;
                    }
                }
                break;
            }
            case 1: {
                String target_name = cp.DecodeStr();
                ops_res = OpsMapTransfer.MapTransferRes_TargetNotExist;
                // not coded.
                break;
            }
            default: {
                DebugLogger.ErrorLog("OnUserMapTransferItemUseRequest : not coded " + cmd);
                break;
            }
        }
        int timestamp = cp.Decode4();

        chr.SendPacket(ResCWvsContext.MapTransferResult(chr, ops_res, false));
        if (ops_res == OpsMapTransfer.MapTransferRes_Use) {
            chr.changeMap(target_map, target_map.getPortal(0));
            return true;
        }
        chr.sendStatChanged(true);
        return false;
    }

    public static boolean OnUserPortalScrollUseRequest(MapleCharacter chr, short item_slot, int item_id) {
        final IItem item_used = chr.getInventory(MapleInventoryType.USE).getItem(item_slot);
        if (item_used == null || item_used.getQuantity() < 1 || item_used.getItemId() != item_id) {
            return false;
        }
        if (MapleItemInformationProvider.getInstance().getItemEffect(item_used.getItemId()).applyReturnScroll(chr)) {
            MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.USE, item_slot, (short) 1, false);
        } else {
            chr.updateInv();
        }

        return true;
    }

    public static boolean OnUserUpgradeItemUseRequest(MapleMap map, MapleCharacter chr, short item_slot, short equip_slot, int vegas) {
        boolean whiteScroll = true;
        boolean legendarySpirit = false; // legendary spirit skill
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        IEquip toScroll;
        if (equip_slot < 0) {
            toScroll = (IEquip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(equip_slot);
        } else {
            // legendary spirit
            legendarySpirit = true;
            toScroll = (IEquip) chr.getInventory(MapleInventoryType.EQUIP).getItem(equip_slot);
        }
        if (toScroll == null) {
            return false;
        }
        final byte oldLevel = (byte) toScroll.getLevel();
        final byte oldEnhance = (byte) toScroll.getEnhance();
        final byte oldState = (byte) toScroll.getHidden();
        final byte oldFlag = (byte) toScroll.getFlag();
        final byte oldSlots = (byte) toScroll.getUpgradeSlots();
        IItem scroll = chr.getInventory(MapleInventoryType.USE).getItem(item_slot);
        if (scroll == null) {
            chr.updateInv();
            return false;
        }
        // 黄金つち (ビシャスのハンマー)
        if (scroll.getItemId() == 2470000) {
            final Equip toHammer = (Equip) toScroll;
            if (toHammer.getViciousHammer() >= 2 || toHammer.getUpgradeSlots() > 120) {
                chr.updateInv();
                return false;
            }
            toHammer.setViciousHammer((byte) (toHammer.getViciousHammer() + 1));
            toHammer.setUpgradeSlots((byte) (toHammer.getUpgradeSlots() + 1));
            chr.SendPacket(ResWrapper.scrolledItem(scroll, toHammer, false, false));
            chr.getInventory(MapleInventoryType.USE).removeItem(scroll.getPosition(), (short) 1, false);
            chr.getMap().broadcastMessage(chr, ResCUser.getScrollEffect(chr.getId(), IEquip.ScrollResult.SUCCESS, legendarySpirit), vegas == 0);
            return true;
        }
        if (!GameConstants.isSpecialScroll(scroll.getItemId()) && !GameConstants.isCleanSlate(scroll.getItemId()) && !GameConstants.isEquipScroll(scroll.getItemId()) && !GameConstants.isPotentialScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() < 1) {
                chr.updateInv();
                return false;
            }
        } else if (GameConstants.isEquipScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() >= 1 || toScroll.getEnhance() >= 100 || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                chr.updateInv();
                return false;
            }
        } else if (GameConstants.isPotentialScroll(scroll.getItemId())) {
            if (toScroll.getHidden() >= 1 || (toScroll.getLevel() == 0 && toScroll.getUpgradeSlots() == 0) || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                chr.updateInv();
                return false;
            }
        }
        if (!GameConstants.canScroll(toScroll.getItemId()) && !GameConstants.isChaosScroll(toScroll.getItemId())) {
            chr.updateInv();
            return false;
        }
        if ((GameConstants.isCleanSlate(scroll.getItemId()) || GameConstants.isTablet(scroll.getItemId()) || GameConstants.isChaosScroll(scroll.getItemId())) && (vegas > 0 || ii.isCash(toScroll.getItemId()))) {
            chr.updateInv();
            return false;
        }
        if (GameConstants.isTablet(scroll.getItemId()) && toScroll.getDurability() < 0) {
            //not a durability item
            chr.updateInv();
            return false;
        } else if (!GameConstants.isTablet(scroll.getItemId()) && toScroll.getDurability() >= 0) {
            chr.updateInv();
            return false;
        }
        IItem wscroll = null;
        // Anti cheat and validation
        List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
        if (scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
            chr.updateInv();
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
            MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.USE, wscroll.getPosition(), (short) 1, false, false);
        }
        if (scrollSuccess == IEquip.ScrollResult.CURSE) {
            chr.SendPacket(ResWrapper.scrolledItem(scroll, toScroll, true, false));
            if (equip_slot < 0) {
                chr.getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            } else {
                chr.getInventory(MapleInventoryType.EQUIP).removeItem(toScroll.getPosition());
            }
        } else if (vegas == 0) {
            chr.SendPacket(ResWrapper.scrolledItem(scroll, scrolled, false, false));
        }
        chr.getMap().broadcastMessage(chr, ResCUser.getScrollEffect(chr.getId(), scrollSuccess, legendarySpirit), vegas == 0);
        // equipped item was scrolled and changed
        if (equip_slot < 0 && (scrollSuccess == IEquip.ScrollResult.SUCCESS || scrollSuccess == IEquip.ScrollResult.CURSE) && vegas == 0) {
            chr.equipChanged();
        }
        // ベガの呪文書
        if (vegas != 0) {
            chr.SendPacket(ResWrapper.addInventorySlot(MapleInventoryType.EQUIP, toScroll));
            chr.SendPacket(ResCUIVega.VegaResult(OpsCashItem.CashItemRes_VegaSuccess1));
            chr.SendPacket(ResCUIVega.VegaResult(scrollSuccess == IEquip.ScrollResult.SUCCESS ? OpsCashItem.CashItemRes_VegaSuccess2 : OpsCashItem.CashItemRes_VegaErr2));
        }
        return true;
    }

    public static boolean OnUserHyperUpgradeItemUseRequest(MapleMap map, MapleCharacter chr, short item_slot, short equip_slot) {

        return true;
    }

    public static boolean OnUserItemOptionUpgradeItemUseRequest(MapleMap map, MapleCharacter chr, short item_slot, short equip_slot) {

        return true;
    }

    public static boolean OnUserItemReleaseRequest(MapleMap map, MapleCharacter chr, short item_slot, short equip_slot) {
        final IItem magnify = chr.getInventory(MapleInventoryType.USE).getItem(item_slot);
        IItem toReveal = (equip_slot < 0) ? chr.getInventory(MapleInventoryType.EQUIPPED).getItem(equip_slot) : chr.getInventory(MapleInventoryType.EQUIP).getItem(equip_slot);

        if (magnify == null || toReveal == null) {
            chr.updateInv();
            return false;
        }

        final Equip eqq = (Equip) toReveal;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(eqq.getItemId()) / 10;

        //Debug.DebugLog("eqq.getState =  " + eqq.getHidden() + ", magnify.getItemId = " + magnify.getItemId() + ", reqLevel = " + reqLevel);
        //Debug.DebugLog("" + eqq.getPotential1() + ", " + eqq.getPotential2() + ", " + eqq.getPotential3());
        if (eqq.getHidden() == 1
                && (magnify.getItemId() == 2460003 || (magnify.getItemId() == 2460002 && reqLevel <= 12) || (magnify.getItemId() == 2460001 && reqLevel <= 7) || (magnify.getItemId() == 2460000 && reqLevel <= 3))) {
            eqq.setHidden(0); // 未確認状態へ変更
            chr.SendPacket(ResWrapper.scrolledItem(magnify, toReveal, false, true));
            map.broadcastMessage(ResCUser.UserItemReleaseEffect(chr, eqq.getPosition()));
            MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.USE, magnify.getPosition(), (short) 1, false);
            //Debug.DebugLog("potential updated");
        } else {
            chr.updateInv();
            //Debug.ErrorLog("potential err 2");
            return false;
        }

        return true;
    }

    public static boolean OnUserAbilityUpRequest(MapleCharacter chr, ClientPacket cp) {

        int time_stamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
        long flag = 0;

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            flag = cp.Decode8();
        } else {
            flag = cp.Decode4();
        }

        chr.updateTick(time_stamp);
        return OnAbilityUpRequestInternal(chr, flag);
    }

    public static boolean OnAbilityUpRequestInternal(MapleCharacter chr, long flag) {
        final PlayerStats stat = chr.getStat();
        final int job = chr.getJob();
        if (chr.getRemainingAp() > 0) {
            switch (OpsChangeStat.find((int) flag)) { // need to fix
                case CS_STR:
                    // Str
                    if (stat.getStr() >= 999) {
                        return false;
                    }
                    stat.setStr((short) (stat.getStr() + 1));
                    break;
                case CS_DEX:
                    // Dex
                    if (stat.getDex() >= 999) {
                        return false;
                    }
                    stat.setDex((short) (stat.getDex() + 1));
                    break;
                case CS_INT:
                    // Int
                    if (stat.getInt() >= 999) {
                        return false;
                    }
                    stat.setInt((short) (stat.getInt() + 1));
                    break;
                case CS_LUK:
                    // Luk
                    if (stat.getLuk() >= 999) {
                        return false;
                    }
                    stat.setLuk((short) (stat.getLuk() + 1));
                    break;
                case CS_MHP:
                    // HP
                    int maxhp = stat.getMaxHp();
                    if (chr.getHpApUsed() >= 10000 || maxhp >= 30000) {
                        return false;
                    }
                    if (job == 0) {
                        // Beginner
                        maxhp += Randomizer.rand(8, 12);
                    } else if ((job >= 100 && job <= 132) || (job >= 3200 && job <= 3212)) {
                        // Warrior
                        ISkill improvingMaxHP = SkillFactory.getSkill(1000001);
                        int improvingMaxHPLevel = chr.getSkillLevel(improvingMaxHP);
                        maxhp += Randomizer.rand(20, 25);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getX();
                        }
                    } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job))) {
                        // Magician
                        maxhp += Randomizer.rand(10, 20);
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 3300 && job <= 3312)) {
                        // Bowman
                        maxhp += Randomizer.rand(16, 20);
                    } else if ((job >= 500 && job <= 522) || (job >= 3500 && job <= 3512)) {
                        // Pirate
                        ISkill improvingMaxHP = SkillFactory.getSkill(5100000);
                        int improvingMaxHPLevel = chr.getSkillLevel(improvingMaxHP);
                        maxhp += Randomizer.rand(18, 22);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        }
                    } else if (job >= 1500 && job <= 1512) {
                        // Pirate
                        ISkill improvingMaxHP = SkillFactory.getSkill(15100000);
                        int improvingMaxHPLevel = chr.getSkillLevel(improvingMaxHP);
                        maxhp += Randomizer.rand(18, 22);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        }
                    } else if (job >= 1100 && job <= 1112) {
                        // Soul Master
                        ISkill improvingMaxHP = SkillFactory.getSkill(11000000);
                        int improvingMaxHPLevel = chr.getSkillLevel(improvingMaxHP);
                        maxhp += Randomizer.rand(36, 42);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        }
                    } else if (job >= 1200 && job <= 1212) {
                        // Flame Wizard
                        maxhp += Randomizer.rand(15, 21);
                    } else if (job >= 2000 && job <= 2112) {
                        // Aran
                        maxhp += Randomizer.rand(40, 50);
                    } else {
                        // GameMaster
                        maxhp += Randomizer.rand(50, 100);
                    }
                    maxhp = (short) Math.min(30000, Math.abs(maxhp));
                    chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                    stat.setMaxHp(maxhp);
                    break;
                case CS_MMP:
                    // MP
                    int maxmp = stat.getMaxMp();
                    if (chr.getHpApUsed() >= 10000 || stat.getMaxMp() >= 30000) {
                        return false;
                    }
                    if (job == 0) {
                        // Beginner
                        maxmp += Randomizer.rand(6, 8);
                    } else if (job >= 100 && job <= 132) {
                        // Warrior
                        maxmp += Randomizer.rand(2, 4);
                    } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job)) || (job >= 3200 && job <= 3212)) {
                        // Magician
                        ISkill improvingMaxMP = SkillFactory.getSkill(2000001);
                        int improvingMaxMPLevel = chr.getSkillLevel(improvingMaxMP);
                        maxmp += Randomizer.rand(18, 20);
                        if (improvingMaxMPLevel >= 1) {
                            maxmp += improvingMaxMP.getEffect(improvingMaxMPLevel).getY() * 2;
                        }
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 500 && job <= 522) || (job >= 3200 && job <= 3212) || (job >= 3500 && job <= 3512) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 1500 && job <= 1512)) {
                        // Bowman
                        maxmp += Randomizer.rand(10, 12);
                    } else if (job >= 1100 && job <= 1112) {
                        // Soul Master
                        maxmp += Randomizer.rand(6, 9);
                    } else if (job >= 1200 && job <= 1212) {
                        // Flame Wizard
                        ISkill improvingMaxMP = SkillFactory.getSkill(12000000);
                        int improvingMaxMPLevel = chr.getSkillLevel(improvingMaxMP);
                        maxmp += Randomizer.rand(18, 20);
                        if (improvingMaxMPLevel >= 1) {
                            maxmp += improvingMaxMP.getEffect(improvingMaxMPLevel).getY() * 2;
                        }
                    } else if (job >= 2000 && job <= 2112) {
                        // Aran
                        maxmp += Randomizer.rand(6, 9);
                    } else {
                        // GameMaster
                        maxmp += Randomizer.rand(50, 100);
                    }
                    maxmp = (short) Math.min(30000, Math.abs(maxmp));
                    chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                    stat.setMaxMp(maxmp);
                    break;
                default: {
                    chr.sendStatChanged(true);
                    return false;
                }
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - 1));
        }
        chr.sendStatChanged(true);

        return true;
    }

    public static boolean OnUserAbilityMassUpRequest(MapleCharacter chr, ClientPacket cp) {
        int time_stamp = cp.Decode4();
        int count = cp.Decode4(); // ループ数

        ArrayList<OpsChangeStat> stats = new ArrayList<>();
        ArrayList<Integer> points = new ArrayList<>();
        if (count != 2) {
            return false;
        }

        for (int i = 0; i < count; i++) {
            long stat = 0;
            int point = 0;
            if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
                stat = cp.Decode8();
            } else {
                stat = cp.Decode4();
            }
            point = cp.Decode4();
            // check.
            OpsChangeStat ops = OpsChangeStat.find((int) stat);
            if (ops.ordinal() < OpsChangeStat.CS_STR.ordinal() || OpsChangeStat.CS_LUK.ordinal() < ops.ordinal()) {
                return false;
            }
            if (point < 0 || 999 < point) {
                return false;
            }
            stats.add(ops);
            points.add(point);
        }

        int total_point = 0;
        for (int point : points) {
            total_point += point;
        }
        if (chr.getRemainingAp() != total_point) {
            return false;
        }

        chr.setRemainingAp(0);
        for (int i = 0; i < stats.size(); i++) {
            switch (stats.get(i)) {
                case CS_STR: {
                    chr.getStat().setStr(chr.getStat().getStr() + points.get(i));
                    break;
                }
                case CS_DEX: {
                    chr.getStat().setDex(chr.getStat().getDex() + points.get(i));
                    break;
                }
                case CS_INT: {
                    chr.getStat().setInt(chr.getStat().getInt() + points.get(i));
                    break;
                }
                case CS_LUK: {
                    chr.getStat().setLuk(chr.getStat().getLuk() + points.get(i));
                    break;
                }
                default: {
                    break;
                }
            }
        }

        chr.sendStatChanged(true);
        chr.updateTick(time_stamp);
        return true;
    }

    public static boolean OnUserChangeStatRequest(MapleCharacter chr, ClientPacket cp) {
        int time_stamp_1 = 0;

        if (ServerConfig.JMS180orLater()) {
            time_stamp_1 = cp.Decode4();
        }

        int update_mask[] = {0, 0};
        int heal_hp = 0;
        int heal_mp = 0;

        update_mask[0] = cp.Decode4();

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            update_mask[1] = cp.Decode4();
        }

        if ((update_mask[0] & OpsChangeStat.CS_HP.get()) != 0) {
            heal_hp = cp.Decode2();
        }
        if ((update_mask[0] & OpsChangeStat.CS_MP.get()) != 0) {
            heal_mp = cp.Decode2();
        }

        byte unk = cp.Decode1();

        if (Version.LessOrEqual(Region.KMS, 65) || Version.Equal(Region.KMST, 330) || Version.GreaterOrEqual(Region.GMS, 95)) {
        } else {
            int time_stamp_2 = cp.Decode4();
            chr.updateTick(time_stamp_2);
        }

        if (chr.getStat().getHp() <= 0) {
            return false;
        }

        if (0 < heal_hp) {
            chr.addHP(heal_hp);
        }
        if (0 < heal_mp) {
            chr.addMP(heal_mp);
        }
        return true;
    }

    public static boolean OnUserSkillUpRequest(MapleCharacter chr, ClientPacket cp) {
        int time_stamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
        int skill_id = cp.Decode4();

        chr.updateTick(time_stamp);
        return OnSkillUpRequestInternal(chr, skill_id);
    }

    public static boolean OnSkillUpRequestInternal(MapleCharacter chr, int skill_id) {
        boolean isBeginnerSkill = false;
        final int remainingSp;
        chr.setLastSkillUp(skill_id);
        switch (skill_id) {
            case 1000:
            case 1001:
            case 1002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(1000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(1001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(1002));
                remainingSp = Math.min(chr.getLevel() - 1, 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true;
                break;
            }
            case 10001000:
            case 10001001:
            case 10001002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(10001000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(10001001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(10001002));
                remainingSp = Math.min(chr.getLevel() - 1, 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true;
                break;
            }
            case 20001000:
            case 20001001:
            case 20001002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(20001000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(20001001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(20001002));
                remainingSp = Math.min(chr.getLevel() - 1, 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true;
                break;
            }
            case 20011000:
            case 20011001:
            case 20011002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(20011000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(20011001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(20011002));
                remainingSp = Math.min(chr.getLevel() - 1, 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true;
                break;
            }
            case 30001000:
            case 30001001:
            case 30000002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(30001000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(30001001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(30000002));
                remainingSp = Math.min(chr.getLevel() - 1, 9) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true; //resist can max ALL THREE
                break;
            }
            default: {
                remainingSp = chr.getRemainingSp(GameConstants.getSkillBookForSkill(skill_id));
                break;
            }
        }
        final ISkill skill = SkillFactory.getSkill(skill_id);
        if (skill.hasRequiredSkill()) {
            if (chr.getSkillLevel(SkillFactory.getSkill(skill.getRequiredSkillId())) < skill.getRequiredSkillLevel()) {
                DebugLogger.ErrorLog("Use SP 1 = " + skill_id);
                return false;
            }
        }
        final int maxlevel = skill.isFourthJob() ? chr.getMasterLevel(skill) : skill.getMaxLevel();
        final int curLevel = chr.getSkillLevel(skill);
        if (skill.isInvisible() && chr.getSkillLevel(skill) == 0) {
            if ((skill.isFourthJob() && chr.getMasterLevel(skill) == 0) || (!skill.isFourthJob() && maxlevel < 10 && !isBeginnerSkill)) {
                DebugLogger.ErrorLog("Use SP 2 = " + skill_id);
                return false;
            }
        }

        if ((remainingSp > 0 && curLevel + 1 <= maxlevel) && skill.canBeLearnedBy(chr.getJob())) {
            if (!isBeginnerSkill) {
                final int skillbook = GameConstants.getSkillBookForSkill(skill_id);
                chr.setRemainingSp(chr.getRemainingSp(skillbook) - 1, skillbook);
            }
            chr.sendStatChanged(false);
            chr.changeSkillLevel(skill, (byte) (curLevel + 1), chr.getMasterLevel(skill));
            return true;
        }
        if ((remainingSp > 0 && curLevel + 1 <= maxlevel) && isBeginnerSkill) {
            chr.sendStatChanged(false);
            chr.changeSkillLevel(skill, (byte) (curLevel + 1), chr.getMasterLevel(skill));
            return true;
        }
        DebugLogger.ErrorLog("Use SP 4 = " + skill_id);
        return false;
    }

    // CUserLocal::SendSkillUseRequest
    public static boolean OnUserSkillUseRequest(MapleCharacter chr, ClientPacket cp) {
        MapleMap map = chr.getMap();
        int update_time = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
        int nSkillID = cp.Decode4();
        byte nSLV = cp.Decode1();

        chr.SendPacket(ResCWvsContext.SkillUseResult());
        if (chr.getBuff().update(nSkillID)) {
            chr.SendPacket(ResCWvsContext.TemporaryStatSet(chr, nSkillID));
            return true;
        }

        OpsSkill ops_skill = OpsSkill.find(nSkillID);
        switch (ops_skill) {
            case HERO_MONSTER_MAGNET:
            case DARKKNIGHT_MONSTER_MAGNET: {
                List<Integer> monster_ids = new ArrayList<>();
                List<Byte> magnets = new ArrayList<>();
                int nMobCount = cp.Decode4();
                if (nMobCount < 0) {
                    return false;
                }
                for (int i = 0; i < nMobCount; i++) {
                    int dwMobID = cp.Decode4();
                    byte bSuccess = cp.Decode1(); // 01

                    monster_ids.add(dwMobID);
                    magnets.add(bSuccess);
                }
                if (Version.PostBB()) {
                    short unk = cp.Decode2();
                }
                byte tDelay = cp.Decode1(); // Left

                for (int i = 0; i < nMobCount; i++) {
                    MapleMonster monster = map.getMonsterByOid(monster_ids.get(i));
                    if (monster == null) {
                        continue;
                    }
                    map.broadcastMessage(chr, ResCMobPool.MobCatchEffect(monster, magnets.get(i) != 0), false);
                }
                // magnet effect for remote?
                //map.broadcastMessage(chr, ResCUserRemote.UserEffectRemote(chr.getId(), nSkillID, 1, slea.readByte()), chr.getPosition());
                return true;
            }
            default: {
                break;
            }
        }

        chr.sendStatChanged(true);
        DebugLogger.ErrorLog("OnUserSkillUseRequest : not coded, " + nSkillID + ", " + ops_skill);
        return false;
    }

    // CancelBuffHandler
    public static boolean OnUserSkillCancelRequest(MapleCharacter chr, ClientPacket cp) {
        MapleMap map = chr.getMap();
        int buff_id = cp.Decode4();

        chr.SendPacket(ResCWvsContext.TemporaryStatReset(chr, buff_id));
        if (!chr.getBuff().remove(buff_id)) {
            return false;
        }

        map.broadcastMessage(chr, ResCUserRemote.UserSkillCancel(chr, buff_id), false);
        return true;
    }

    // CUserLocal::DoActiveSkill_Prepare
    public static boolean OnUserSkillPrepareRequest(MapleCharacter chr, ClientPacket cp) {
        int nSkillID = cp.Decode4();
        byte nSLV = cp.Decode1();
        short action = 0; // m_nOneTimeAction & 0x7FFF | (m_nMoveAction << 15)
        if (Version.PostBB() || Version.GreaterOrEqual(Region.JMS, 186)) {
            action = cp.Decode2();
        } else {
            action = cp.Decode1();
        }
        byte attack_speed_degree = cp.Decode1();

        ISkill skill = SkillFactory.getSkill(nSkillID);
        if (chr == null) {
            return false;
        }
        int skilllevel_serv = chr.getSkillLevel(skill);

        if (skilllevel_serv > 0 && skilllevel_serv == nSLV && skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(System.currentTimeMillis());
            chr.getMap().broadcastMessage(chr, ResCUserRemote.UserSkillPrepare(chr, nSkillID, nSLV, action, attack_speed_degree), false);
        }

        return true;
    }

    public static boolean OnUserDropMoneyRequest(MapleCharacter chr, ClientPacket cp) {
        int time_stamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
        int mesos = cp.Decode4();

        if (!chr.isAlive() || (mesos < 10 || 50000 < mesos) || chr.getMeso() < mesos) {
            chr.updateStat();
            return false;
        }

        chr.gainMeso(-mesos, false, true);
        chr.getMap().spawnMesoDrop(mesos, chr.getPosition(), chr, chr, true, (byte) 0);
        return true;
    }

    public static boolean OnUserPortalScriptRequest(MapleCharacter chr, ClientPacket cp) {
        byte portal_count = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode1();
        String portal_name = cp.DecodeStr();
        short chr_x = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode2();
        short chr_y = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode2();

        return chr.usePortalScript(portal_name);
    }

    public static boolean OnUserPortalTeleportRequest(MapleCharacter chr, ClientPacket cp) {
        byte portal_count = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode1();
        String portal_name = cp.DecodeStr();
        short chr_x = cp.Decode2();
        short chr_y = cp.Decode2();
        short portal_to_x = cp.Decode2();
        short portal_to_y = cp.Decode2();

        return chr.usePortalTeleport(portal_name);
    }

    public static boolean OnUserMapTransferRequest(MapleCharacter chr, ClientPacket cp) {
        byte cmd = cp.Decode1();
        byte rock_type = cp.Decode1();
        boolean is_vip = rock_type == 1;

        OpsMapTransfer ops_req = OpsMapTransfer.find(cmd);
        switch (ops_req) {
            case MapTransferReq_DeleteList: {
                int target_map_id = cp.Decode4();
                if (rock_type == 0) {
                    chr.deleteFromRegRocks(target_map_id);
                    chr.SendPacket(ResCWvsContext.MapTransferResult(chr, OpsMapTransfer.MapTransferRes_DeleteList, is_vip));
                    return true;
                }
                if (rock_type == 1) {
                    chr.deleteFromRocks(target_map_id);
                    chr.SendPacket(ResCWvsContext.MapTransferResult(chr, OpsMapTransfer.MapTransferRes_DeleteList, is_vip));
                    return true;
                }
                break;
            }
            case MapTransferReq_RegisterList: {
                if (FieldOpt.FIELDOPT_TELEPORTITEMLIMIT.check(chr.getMap().getFieldLimit())) {
                    chr.SendPacket(ResCWvsContext.MapTransferResult(chr, OpsMapTransfer.MapTransferRes_NotAllowed, is_vip));
                    return true;
                }
                if (rock_type == 0) {
                    chr.addRegRockMap();
                    chr.SendPacket(ResCWvsContext.MapTransferResult(chr, OpsMapTransfer.MapTransferRes_RegisterList, is_vip));
                    return true;
                }
                if (rock_type == 1) {
                    chr.addRockMap();
                    chr.SendPacket(ResCWvsContext.MapTransferResult(chr, OpsMapTransfer.MapTransferRes_RegisterList, is_vip));
                    return true;
                }
                break;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnUserMapTransferRequest : not coded " + ops_req + ", rock_type = " + rock_type);
        chr.SendPacket(ResCWvsContext.MapTransferResult(chr, OpsMapTransfer.MapTransferRes_Unknown, is_vip));
        return true;
    }

    // CQuest::StartQuest
    public static boolean OnUserQuestRequest(MapleCharacter chr, ClientPacket cp) {
        MapleClient client = chr.getClient();
        MapleMap map = chr.getMap();

        byte action = cp.Decode1();
        short m_usQuestID = cp.Decode2();

        int uQuestID = Short.toUnsignedInt(m_usQuestID);
        MapleQuest quest = MapleQuest.getInstance(uQuestID);

        switch (OpsQuest.find(action)) {
            case QuestReq_LostItem: {
                int time = cp.Decode4();
                int item_id = cp.Decode4();

                quest.RestoreLostItem(chr, item_id);
                return true;
            }
            case QuestReq_AcceptQuest: {
                int m_dwNpcTemplateID = cp.Decode4();

                quest.start(chr, m_dwNpcTemplateID);
                return true;
            }
            case QuestReq_CompleteQuest: {
                int m_dwNpcTemplateID = cp.Decode4();
                int selection = cp.Decode4();

                if (selection != -1) {
                    quest.complete(chr, m_dwNpcTemplateID, selection);
                } else {
                    quest.complete(chr, m_dwNpcTemplateID);
                }

                return true;
            }
            case QuestReq_ResignQuest: {
                quest.forfeit(chr);
                return true;
            }
            case QuestReq_OpeningScript: {
                int m_dwNpcTemplateID = cp.Decode4();
                short pos_x = cp.Decode2();
                short pos_y = cp.Decode2();

                TacosScriptQuest.getInstance().startQuest(client, m_dwNpcTemplateID, uQuestID);
                return true;
            }
            case QuestReq_CompleteScript: {
                int m_dwNpcTemplateID = cp.Decode4();

                TacosScriptQuest.getInstance().endQuest(client, m_dwNpcTemplateID, uQuestID, false);
                chr.SendPacket(WrapCUserLocal.EffectLocal(OpsUserEffect.UserEffect_QuestComplete));
                map.broadcastMessage(chr, WrapCUserRemote.EffectRemote(OpsUserEffect.UserEffect_QuestComplete, chr), false);
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnUserQuestRequest : action = " + action);
        return false;
    }

    public static boolean OnUserGatherItemRequest(MapleCharacter chr, byte slot_type) {
        MapleInventoryType mit = MapleInventoryType.getByType(slot_type);

        if (mit == MapleInventoryType.UNDEFINED || mit == MapleInventoryType.EQUIPPED) {
            return false;
        }

        MapleInventory mi = chr.getInventory(mit);

        // 1. 最初の空きスロットを探す
        // 2. 空きスロット以降に存在するアイテムを探す
        // 3. アイテムを空きスロットに移動する
        for (short slot_to = mi.getNextFreeSlot(); slot_to <= mi.getSlotLimit(); slot_to++) {
            short slot_from = mi.getNextItem(slot_to);
            if (slot_from == 0) {
                break;
            }
            // 多分1回のpacketで送信するようにしたほうが良い
            OnUserChangeSlotPositionRequest(chr, slot_type, slot_from, slot_to, (short) -1);
        }

        chr.SendPacket(ResCWvsContext.GatherItemResult(slot_type));
        chr.updateInv();
        return true;
    }

    public static boolean OnUserSortItemRequest(MapleCharacter chr, byte slot_type) {
        MapleInventoryType mit = MapleInventoryType.getByType(slot_type);

        if (mit == MapleInventoryType.UNDEFINED || mit == MapleInventoryType.EQUIPPED) {
            return false;
        }

        MapleInventory mi = chr.getInventory(mit);
        short slot_limit = (short) mi.getSlotLimit();
        for (short slot_to = 1; slot_to <= slot_limit; slot_to++) {
            IItem item_to = mi.getItem(slot_to);
            if (item_to == null) {
                break;
            }

            int item_id = item_to.getItemId();
            short slot_from = slot_to;

            for (short slot = slot_to; slot <= slot_limit; slot++) {
                IItem item = mi.getItem(slot);
                if (item == null) {
                    break;
                }
                if (item.getItemId() < item_id) {
                    slot_from = slot;
                    item_id = item.getItemId();
                }
            }

            if (slot_from != slot_to) {
                OnUserChangeSlotPositionRequest(chr, slot_type, slot_from, slot_to, (short) -1);
            }
        }

        chr.SendPacket(ResCWvsContext.SortItemResult(slot_type));
        chr.updateInv();
        return true;
    }

    public static boolean OnUserChangeSlotPositionRequest(MapleCharacter chr, byte slot_type, short slot_from, short slot_to, short quantity) {
        if (chr.getPlayerShop() != null || chr.getTrade() != null) {
            return false;
        }

        MapleInventoryType type = MapleInventoryType.getByType(slot_type);

        // equipped
        if (slot_from <= -1 || slot_to <= -1) {
            chr.DebugMsg("Equipped : " + slot_from + " -> " + slot_to);
        }

        // drop
        if (slot_to == 0) {
            MapleInventoryManipulator.drop(chr.getClient(), type, slot_from, quantity);
            return true;
        }

        if (type == MapleInventoryType.EQUIP) {
            if (1 <= slot_from && slot_to <= -1) {
                MapleInventoryManipulator.equip(chr.getClient(), slot_from, slot_to);
                return true;
            }
            if (slot_from <= -1 && 1 <= slot_to) {
                MapleInventoryManipulator.unequip(chr.getClient(), slot_from, slot_to);
                return true;
            }
            if (slot_from <= -1 && slot_to <= -1) {
                DebugLogger.ErrorLog("OnUserChangeSlotPositionRequest : user tried moving equipped slot " + slot_from + " -> " + slot_to);
                return false;
            }
        }

        MapleInventoryManipulator.move(chr.getClient(), type, slot_from, slot_to);
        return false;
    }

    public static boolean OnUserStatChangeItemUseRequest(MapleCharacter chr, ClientPacket cp) {
        int timestamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
        short item_slot = cp.Decode2();
        int item_id = cp.Decode4();

        if (chr.useItem(item_slot, item_id)) {
            if (chr.getBuff().update(-item_id)) {
                chr.SendPacket(ResCWvsContext.TemporaryStatSet(chr, -item_id));
                return true;
            }
        }
        return true;
    }

    public static boolean OnUserStatChangeItemCancelRequest(MapleCharacter chr, ClientPacket cp) {
        int buff_id = cp.Decode4(); // negative item id.

        chr.SendPacket(ResCWvsContext.TemporaryStatReset(chr, buff_id));
        if (!chr.getBuff().remove(buff_id)) {
            return false;
        }

        return true;
    }

    public static boolean OnUserMobSummonItemUseRequest(MapleCharacter chr, short item_slot, int item_id) {
        IItem item_used = chr.getInventory(MapleInventoryType.USE).getItem(item_slot);
        if (item_used == null) {
            return false;
        }
        if (item_used.getItemId() != item_id) {
            return false;
        }
        if (item_used.getQuantity() < 1) {
            return false;
        }
        MapleMap map = chr.getMap();
        if (FieldOpt.FIELDOPT_SUMMONLIMIT.check(map.getFieldLimit())) {
            return false;
        }
        // used
        MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.USE, item_slot, (short) 1, false);
        // spawn mobs
        List<OdinPair<Integer, Integer>> summon_info = MapleItemInformationProvider.getInstance().getSummonMobs(item_id);
        if (summon_info == null) {
            return true;
        }
        for (OdinPair<Integer, Integer> summon_data : summon_info) {
            if (Randomizer.nextInt(100) < summon_data.getRight()) {
                MapleMonster monster = MapleLifeFactory.getMonster(summon_data.getLeft());
                chr.getMap().spawnMonster_sSack(monster, chr.getPosition(), 0);
            }
        }
        return true;
    }

    public static boolean OnUserPetFoodItemUseRequest(MapleCharacter chr, short item_slot, int item_id) {
        return ReqCUser_Pet.OnPetFood(chr, MapleInventoryType.USE, item_slot, item_id);
    }

    public static boolean OnUserTamingMobFoodItemUseRequest(MapleMap map, MapleCharacter chr, short item_slot, int item_id) {
        final IItem item_used = chr.getInventory(MapleInventoryType.USE).getItem(item_slot);
        final MapleMount mount = chr.getMount();

        if (item_used != null && item_used.getQuantity() > 0 && item_used.getItemId() == item_id && mount != null) {
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
            map.broadcastMessage(ResCWvsContext.SetTamingMobInfo(chr, levelup));
            MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.USE, item_slot, (short) 1, false);
        }
        chr.updateInv();
        return true;
    }

    public static boolean OnUserConsumeCashItemUseRequest(MapleMap map, MapleCharacter chr, ClientPacket cp) {
        return ReqSub_UserConsumeCashItemUseRequest.OnUserConsumeCashItemUseRequestInternal(map, chr, cp);
    }

    public static boolean OnUserBridleItemUseRequest(MapleMap map, MapleCharacter chr, short item_slot, int item_id, int mob_oid) {
        MapleMonster mob = map.getMonsterByOid(mob_oid);

        if (mob == null) {
            return false;
        }

        final IItem toUse = chr.getInventory(MapleInventoryType.USE).getItem(item_slot);
        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == item_id && mob != null) {
            switch (item_id) {
                case 2270004: {
                    if (mob.getHp() <= mob.getMobMaxHp() / 2) {
                        map.broadcastMessage(ResCMobPool.MobEffectByItem(mob, item_id, true));
                        map.killMonster(mob, chr, true, false, OpsMobLeaveField.MOBLEAVEFIELD_REMAINHP);
                        MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, item_id, 1, false, false);
                        MapleInventoryManipulator.addById(chr.getClient(), 4001169, (short) 1);
                    } else {
                        map.broadcastMessage(ResCMobPool.MobEffectByItem(mob, item_id, false));
                        chr.SendPacket(ResWrapper.BroadCastMsgEvent("The monster has too much physical strength, so you cannot catch it."));
                    }
                    break;
                }
                case 2270002: {
                    if (mob.getHp() <= mob.getMobMaxHp() / 2) {
                        map.broadcastMessage(ResCMobPool.MobEffectByItem(mob, item_id, true));
                        map.killMonster(mob, chr, true, false, OpsMobLeaveField.MOBLEAVEFIELD_REMAINHP);
                        MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, item_id, 1, false, false);
                    } else {
                        map.broadcastMessage(ResCMobPool.MobEffectByItem(mob, item_id, false));
                        chr.SendPacket(ResWrapper.BroadCastMsgEvent("The monster has too much physical strength, so you cannot catch it."));
                    }
                    break;
                }
                case 2270000: {
                    // Pheromone Perfume
                    if (mob.getId() != 9300101) {
                        break;
                    }
                    map.broadcastMessage(ResCMobPool.MobEffectByItem(mob, item_id, true));
                    map.killMonster(mob, chr, true, false, OpsMobLeaveField.MOBLEAVEFIELD_REMAINHP);
                    MapleInventoryManipulator.addById(chr.getClient(), 1902000, (short) 1, null);
                    MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, item_id, 1, false, false);
                    break;
                }
                case 2270003: {
                    // Cliff's Magic Cane
                    if (mob.getId() != 9500320) {
                        break;
                    }
                    if (mob.getHp() <= mob.getMobMaxHp() / 2) {
                        map.broadcastMessage(ResCMobPool.MobEffectByItem(mob, item_id, true));
                        map.killMonster(mob, chr, true, false, OpsMobLeaveField.MOBLEAVEFIELD_REMAINHP);
                        MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, item_id, 1, false, false);
                    } else {
                        map.broadcastMessage(ResCMobPool.MobEffectByItem(mob, item_id, false));
                        chr.SendPacket(ResWrapper.BroadCastMsgEvent("The monster has too much physical strength, so you cannot catch it."));
                    }
                    break;
                }
            }
        }

        chr.updateInv();
        return true;
    }

    public static boolean OnUserSkillLearnItemUseRequest(MapleMap map, MapleCharacter chr, short item_slot, final int item_id) {
        int item_type = item_id / 10000;
        boolean bIsMaterbook = (item_type == 229 || item_type == 562);
        boolean bUsed = false;
        boolean bSucceed = false;

        final IItem item_used = chr.getInventory(GameConstants.getInventoryType(item_id)).getItem(item_slot);
        if (item_used == null || item_used.getQuantity() < 1 || item_used.getItemId() != item_id) {
            chr.SendPacket(ResCWvsContext.SkillLearnItemResult(chr, bIsMaterbook, bUsed, bSucceed));
            return false;
        }

        final Map<String, Integer> skilldata = MapleItemInformationProvider.getInstance().getSkillStats(item_used.getItemId());
        if (skilldata == null) {
            chr.SendPacket(ResCWvsContext.SkillLearnItemResult(chr, bIsMaterbook, bUsed, bSucceed));
            return false;
        }

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
                bUsed = true;
                if (Randomizer.nextInt(100) <= SuccessRate && SuccessRate != 0) {
                    bSucceed = true;
                    chr.changeSkillLevel(CurrSkillData, chr.getSkillLevel(CurrSkillData), (byte) MasterLevel);
                } else {
                    bSucceed = false;
                }
                MapleInventoryManipulator.removeFromSlot(chr.getClient(), GameConstants.getInventoryType(item_id), item_slot, (short) 1, false);
                break;
            }
        }

        map.broadcastMessage(ResCWvsContext.SkillLearnItemResult(chr, bIsMaterbook, bUsed, bSucceed));
        chr.updateInv();
        return true;
    }

    public static boolean OnUserRepairDurabilityAll(MapleCharacter chr, ClientPacket cp) {
        int npc_id = Region.IsJMS() ? cp.Decode4() : 0;

        List<Equip> equips = new ArrayList<>();
        List<Equip> equippeds = new ArrayList<>();
        int total_price = 0;
        for (IItem item : chr.getInventory(MapleInventoryType.EQUIPPED)) {
            Equip equip = (Equip) item;
            if (0 <= equip.getDurability()) {
                int price = TacosShared.getRepairPrice(equip);
                int durability_max = TacosShared.getDurabilityMax(equip);
                if (0 < price && 0 <= durability_max) {
                    total_price += price;
                    equippeds.add(equip);
                }
            }
        }
        for (IItem item : chr.getInventory(MapleInventoryType.EQUIP)) {
            Equip equip = (Equip) item;
            if (0 <= equip.getDurability()) {
                int price = TacosShared.getRepairPrice(equip);
                int durability_max = TacosShared.getDurabilityMax(equip);
                if (0 < price && 0 <= durability_max) {
                    total_price += price;
                    equips.add(equip);
                }
            }
        }
        chr.DebugMsg("OnUserRepairDurabilityAll : total_price = " + total_price);

        if (chr.getMeso() < total_price) {
            return false;
        }

        chr.gainMeso(-total_price, false);

        for (Equip equip : equippeds) {
            equip.setDurability(TacosShared.getDurabilityMax(equip));
            chr.SendPacket(ResWrapper.addInventorySlot(MapleInventoryType.EQUIPPED, equip));
        }
        for (Equip equip : equips) {
            equip.setDurability(TacosShared.getDurabilityMax(equip));
            chr.SendPacket(ResWrapper.addInventorySlot(MapleInventoryType.EQUIP, equip));
        }

        return true;
    }

    public static boolean OnUserRepairDurability(MapleCharacter chr, ClientPacket cp) {
        int nPOS = cp.Decode4();
        int npc_id = Region.IsJMS() ? cp.Decode4() : 0;

        MapleInventoryType type = nPOS < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP;
        Equip equip = (Equip) chr.getInventory(type).getItem((short) nPOS);
        if (equip == null) {
            return false;
        }

        int price = TacosShared.getRepairPrice(equip);
        int durability_max = TacosShared.getDurabilityMax(equip);

        chr.DebugMsg("OnUserRepairDurability : price = " + price);

        if (chr.getMeso() < price || durability_max < 0) {
            return false;
        }

        chr.gainMeso(-price, false);
        equip.setDurability(durability_max);
        chr.SendPacket(ResWrapper.addInventorySlot(type, equip));
        return true;
    }

    // CWvsContext::SendFollowCharacterRequest
    public static boolean OnUserFollowCharacterRequest(MapleCharacter chr, ClientPacket cp) {
        int dwDriverID = cp.Decode4();
        byte bAutoReq = cp.Decode1();
        byte bKeyInput = cp.Decode1();

        MapleMap map = chr.getMap();

        if (bKeyInput != 0) {
            MapleCharacter driver = map.getCharacterById(chr.getDriver());
            if (driver != null) {
                driver.setPassenger(0);
            }
            chr.setDriver(0);
            map.broadcastMessage(ResCUser.UserFollowCharacter(chr, true));
            return true;
        }

        MapleCharacter driver = map.getCharacterById(dwDriverID);
        if (driver == null) {
            return false;
        }

        if (bAutoReq != 0) {
            return false;
        }

        driver.SendPacket(ResCWvsContext.SetPassenserRequest(chr));
        return true;
    }

    // CWvsContext::SendFollowRequestApply
    public static boolean OnSetPassenserResult(MapleCharacter chr, ClientPacket cp) {
        MapleMap map = chr.getMap();
        int error = 0;

        int m_dwFollowRequesterID = cp.Decode4();
        byte bApply = cp.Decode1();

        if (bApply == 0) {
            error = cp.Decode4(); // always 5.
        }

        MapleCharacter passenger = map.getCharacterById(m_dwFollowRequesterID);
        if (passenger == null) {
            return false;
        }

        if (bApply == 0) {
            passenger.SendPacket(ResCUserLocal.UserFollowCharacterFailed(error));
            return false;
        }

        passenger.setDriver(chr.getId());
        chr.setPassenger(passenger.getId());
        map.broadcastMessage(ResCUser.UserFollowCharacter(passenger, false));
        return true;
    }

    public static boolean OnGroupMessage(MapleCharacter chr, ClientPacket cp) {
        TacosWorld world = chr.getWorld();
        ArrayList<MapleCharacter> players = new ArrayList<>();
        // update_time
        int nChatTarget = cp.Decode1(); // nChatTarget
        byte nMemberCnt = cp.Decode1(); // nMemberCnt
        int[] adwGroupMemberID = new int[nMemberCnt]; // adwGroupMemberID
        for (byte i = 0; i < nMemberCnt; i++) {
            adwGroupMemberID[i] = cp.Decode4();
        }
        String sText = cp.DecodeStr(); // sText

        for (int player_id : adwGroupMemberID) {
            MapleCharacter player = world.findOnlinePlayerById(player_id);
            if (player == null) {
                continue;
            }
            players.add(player);
        }

        switch (OpsChatGroup.find(nChatTarget)) {
            case CG_Friend: {
                for (MapleCharacter friend : players) {
                    if (!friend.getBuddylist().containsVisible(chr.getId())) {
                        DebugLogger.ErrorLog("OnGroupMessage : CG_Friend");
                        continue;
                    }
                    friend.SendPacket(ResCField.GroupMessage(OpsChatGroup.CG_Friend, chr.getName(), sText));
                }
                return true;
            }
            case CG_Party: {
                MapleParty party = chr.getParty();
                if (party != null) {
                    return true;
                }
                OdinWorld.Party.partyChat(party.getId(), sText, chr.getName());
                return true;
            }
            case CG_Guild: {
                int guild_id = chr.getGuildId();
                if (guild_id <= 0) {
                    return true;
                }
                OdinWorld.Guild.guildChat(guild_id, chr.getName(), chr.getId(), sText);
                return true;
            }
            case CG_Alliance: {
                int guild_id = chr.getGuildId();
                if (guild_id <= 0) {
                    return true;
                }
                OdinWorld.Alliance.allianceChat(guild_id, chr.getName(), chr.getId(), sText);
                return true;
            }
            case CG_Couple: {
                break;
            }
            case CG_ToCouple: {
                break;
            }
            case CG_Expedition: {
                break;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnGroupMessage : not coded = " + nChatTarget);
        return false;
    }

    private static boolean OnWhisper(MapleCharacter chr, ClientPacket cp) {
        int operation = cp.Decode1();
        Ops_Whisper loc_whis = Ops_Whisper.find(operation & ~Ops_Whisper.WP_Request.get());

        switch (loc_whis) {
            case WP_Location: {
                String player_name = cp.DecodeStr();
                MapleCharacter chr_to = chr.getWorld().findOnlinePlayer(player_name);
                chr.SendPacket(ResCField.Whisper(Ops_Whisper.WP_Result, Ops_Whisper.WP_Location, chr, player_name, null, chr_to));
                return true;
            }
            case WP_Whisper: {
                String name_to = cp.DecodeStr();
                String message = cp.DecodeStr();
                MapleCharacter chr_to = chr.getWorld().findOnlinePlayer(name_to);;
                if (chr_to == null) {
                    chr.SendPacket(ResCField.Whisper(Ops_Whisper.WP_Result, Ops_Whisper.WP_Whisper, chr, name_to, message, null));
                    return false;
                }
                chr.SendPacket(ResCField.Whisper(Ops_Whisper.WP_Result, Ops_Whisper.WP_Whisper, chr, name_to, message, chr_to));
                chr_to.SendPacket(ResCField.Whisper(Ops_Whisper.WP_Receive, Ops_Whisper.WP_Whisper, chr, name_to, message, chr_to));
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnWhisper : not coded " + operation);
        return false;
    }

    public static boolean OnPartyRequest(MapleCharacter chr, ClientPacket cp) {
        int type = cp.Decode1();
        MapleParty party = chr.getParty();
        MaplePartyCharacter partyplayer = new MaplePartyCharacter(chr);

        OpsParty ops = OpsParty.find(type);
        boolean is_leader = false;

        if (party != null) {
            if (party.getLeader().getId() == chr.getId()) {
                is_leader = true;
            }
        }

        switch (ops) {
            case PartyReq_CreateNewParty: {
                if (party != null) {
                    chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_CreateNewParty_AlreayJoined));
                    return false;
                }

                party = OdinWorld.Party.createParty(partyplayer);
                chr.setParty(party);
                chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_CreateNewParty_Done, chr));
                return true;
            }
            case PartyReq_WithdrawParty: {
                if (party == null) {
                    chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_WithdrawParty_Unknown));
                    return false;
                }

                chr.setParty(null);

                if (is_leader) {
                    OdinWorld.Party.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
                    return true;
                }

                OdinWorld.Party.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
                return true;
            }
            case PartyReq_JoinParty: {
                int party_id = cp.Decode4();
                if (party != null) {
                    chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_JoinParty_AlreadyJoined));
                    return false;
                }

                party = OdinWorld.Party.getParty(party_id);
                if (party == null) {
                    chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_JoinParty_Unknown));
                    return false;
                }

                if (6 <= party.getMembers().size()) {
                    chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_JoinParty_AlreadyFull));
                    return false;
                }

                OdinWorld.Party.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
                chr.receivePartyMemberHP();
                chr.updatePartyMemberHP();
                return true;
            }
            case PartyReq_InviteParty: {
                String character_name = cp.DecodeStr();

                if (party == null || 6 <= party.getMembers().size()) {
                    chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_JoinParty_AlreadyFull));
                    return false;
                }

                MapleCharacter invited = chr.getWorld().findOnlinePlayer(character_name, false);
                if (invited == null) {
                    chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_JoinParty_UnknownUser));
                    return false;
                }
                if (invited.getLevel() < 10) {
                    chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_CreateNewParty_Beginner));
                    return false;
                }
                if (invited.getParty() != null) {
                    chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_JoinParty_AlreadyJoined));
                    return false;
                }

                chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_InviteParty_Sent, invited));
                invited.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyReq_InviteParty, chr));
                return true;
            }
            case PartyReq_KickParty: {
                int character_id = cp.Decode4();

                if (party == null || !is_leader) {
                    chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_KickParty_Unknown));
                    return false;
                }

                MaplePartyCharacter member = party.getMemberById(character_id);
                OdinWorld.Party.updateParty(party.getId(), PartyOperation.EXPEL, member);
                return true;
            }
            case PartyReq_ChangePartyBoss: {
                int character_id = cp.Decode4();

                if (party == null || !is_leader) {
                    chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_ChangePartyBoss_Unknown));
                    return false;
                }

                MaplePartyCharacter member = party.getMemberById(character_id);
                OdinWorld.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, member);
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnPartyRequest : not coded, type = " + type);
        return false;
    }

    public static boolean OnPartyResult(MapleCharacter chr, ClientPacket cp) {
        int type = cp.Decode1();
        int party_id = cp.Decode4();

        if (chr.getParty() != null) {
            chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_JoinParty_AlreadyJoined));
            return false;
        }

        MapleParty party = OdinWorld.Party.getParty(party_id);
        if (party == null) {
            chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_JoinParty_Unknown));
            return false;
        }

        switch (OpsParty.find(type)) {
            case PartyRes_InviteParty_Sent: {
                return true;
            }
            case PartyRes_InviteParty_BlockedUser: {
                MapleCharacter leader = chr.getWorld().findOnlinePlayerById(party.getLeader().getId());
                if (leader != null) {
                    leader.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_InviteParty_BlockedUser, chr));
                }
                return true;
            }
            case PartyRes_InviteParty_AlreadyInvited: {
                return true;
            }
            case PartyRes_InviteParty_AlreadyInvitedByInviter: {
                return true;
            }
            case PartyRes_InviteParty_Rejected: {
                return true;
            }
            case PartyRes_InviteParty_Accepted: {
                if (6 <= party.getMembers().size()) {
                    chr.SendPacket(ResCWvsContext.PartyResult(OpsParty.PartyRes_JoinParty_AlreadyFull));
                    return true;
                }

                OdinWorld.Party.updateParty(party_id, PartyOperation.JOIN, new MaplePartyCharacter(chr));
                chr.receivePartyMemberHP();
                chr.updatePartyMemberHP();
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnPartyResult : not coded, type = " + type);
        return false;
    }

    public static boolean OnMemoRequest(MapleCharacter chr, ClientPacket cp) {
        byte type = cp.Decode1();

        switch (OpsMemo.find(type)) {
            case MemoReq_Send: {
                String name = cp.DecodeStr();
                String msg = cp.DecodeStr();
                boolean fame = cp.Decode1() > 0;
                int unk = cp.Decode4();
                IItem itemz = chr.getCashInventory().findByCashId(cp.Decode8());
                if (itemz == null || !itemz.getGiftFrom().equalsIgnoreCase(name) || !chr.getCashInventory().canSendNote(itemz.getUniqueId())) {
                    return false;
                }
                try {
                    chr.sendNote(name, msg, fame ? 1 : 0);
                    chr.getCashInventory().sendedNote(itemz.getUniqueId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            case MemoReq_Delete: {
                byte num = cp.Decode1();
                short unk = cp.Decode2();

                for (int i = 0; i < num; i++) {
                    final int id = cp.Decode4();
                    chr.deleteNote(id, cp.Decode1() > 0 ? 1 : 0);
                }
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnMemoRequest : not coded " + type);
        return true;
    }

    // CWvsContext::SendSendInvitaionRequest
    // CWvsContext::SendInvitationQuery
    public static boolean OnMarriageRequest(MapleCharacter chr, ClientPacket cp) {
        MapleClient client = chr.getClient();
        byte mode = cp.Decode1();

        switch (OpsMarriage.find(mode)) {
            case MarriageReq_Propose: // CWvsContext::SendEngagementRequest
            {
                String name = cp.DecodeStr();
                int item_id = cp.Decode4();
                int ring_id = 1112300 + (item_id - 2240004);
                MapleCharacter player = chr.getChannelServer().getOnlinePlayers().findByName(name);

                if (0 < chr.getMarriageId()) {
                    chr.SendPacket(ResCWvsContext.MarriageResult(OpsMarriage.MarriageRes_RequesterAlreadyEngaged, 0, null, null));
                    return true;
                }
                if (player == null) {
                    chr.SendPacket(ResCWvsContext.MarriageResult(OpsMarriage.MarriageRes_WrongName, 0, null, null));
                    return true;
                }
                if (player.getMapId() != chr.getMapId()) {
                    chr.SendPacket(ResCWvsContext.MarriageResult(OpsMarriage.MarriageRes_NotSameMap, 0, null, null));
                    return true;
                }
                if (!chr.haveItem(item_id, 1)) {
                    chr.SendPacket(ResCWvsContext.MarriageResult(OpsMarriage.MarriageRes_BrokeUp, 0, null, null));
                    return true;
                }
                if (0 < player.getMarriageId() || 0 < player.getMarriageItemId()) {
                    chr.SendPacket(ResCWvsContext.MarriageResult(OpsMarriage.MarriageRes_TargetAlreadyEngaged, 0, null, null));
                    return true;
                }
                if (!MapleInventoryManipulator.checkSpace(client, ring_id, 1, "")) {
                    chr.SendPacket(ResCWvsContext.MarriageResult(OpsMarriage.MarriageRes_RequesterNoEmptySlot, 0, null, null));
                    return true;
                }
                if (!MapleInventoryManipulator.checkSpace(player.getClient(), ring_id, 1, "")) {
                    chr.SendPacket(ResCWvsContext.MarriageResult(OpsMarriage.MarriageRes_TargetNoEmptySlot, 0, null, null));
                    return true;
                }

                chr.setMarriageItemId(item_id);
                player.SendPacket(ResCWvsContext.MarriageRequest(chr.getName(), chr.getId()));
                return true;
            }
            case MarriageReq_CancelPropose: {
                chr.setMarriageItemId(0);
                // send cancel to player.
                return true;
            }
            case MarriageReq_Accept: {
                boolean accepted = cp.Decode1() != 0;
                String name = cp.DecodeStr();
                int character_id = cp.Decode4();
                MapleCharacter player = chr.getChannelServer().getOnlinePlayers().findByName(name);
                if (chr.getMarriageId() > 0 || player == null || player.getId() != character_id || player.getMarriageItemId() <= 0 || !player.haveItem(player.getMarriageItemId(), 1) || player.getMarriageId() > 0) {
                    chr.SendPacket(ResCWvsContext.MarriageResult(OpsMarriage.MarriageRes_RequesterCanceled, 0, null, null));
                    return true;
                }
                if (!accepted) {
                    player.setMarriageItemId(0);
                    player.SendPacket(ResCWvsContext.MarriageResult(OpsMarriage.MarriageRes_TargetRefused, 0, null, null));
                    return true;
                }
                int ring_id = 1112300 + (player.getMarriageItemId() - 2240004);
                if (!MapleInventoryManipulator.checkSpace(client, ring_id, 1, "") || !MapleInventoryManipulator.checkSpace(player.getClient(), ring_id, 1, "")) {
                    chr.SendPacket(ResCWvsContext.MarriageResult(OpsMarriage.MarriageRes_TargetNoEmptySlot, 0, null, null));
                    return true;
                }
                MapleInventoryManipulator.addById(client, ring_id, (short) 1);
                MapleInventoryManipulator.removeById(player.getClient(), MapleInventoryType.USE, player.getMarriageItemId(), 1, false, false);
                MapleInventoryManipulator.addById(player.getClient(), ring_id, (short) 1);
                player.setMarriageId(chr.getId());
                chr.setMarriageId(player.getId());
                player.SendPacket(ResCWvsContext.MarriageResult(OpsMarriage.MarriageRes_ReservationDone, ring_id, player, chr));
                return true;
            }
            case MarriageReq_BreakUp: {
                int item_id = cp.Decode4();
                MapleInventoryType type = GameConstants.getInventoryType(item_id);
                IItem item = chr.getInventory(type).findById(item_id);
                if (item != null && type == MapleInventoryType.ETC && item_id / 10000 == 421) {
                    MapleInventoryManipulator.drop(client, type, item.getPosition(), item.getQuantity());
                }
                return true;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnMarriageRequest not coded, " + mode);
        return false;
    }

    public static boolean OnUserMigrateToITCRequest(MapleClient c, MapleCharacter chr) {
        // temporary off
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            return false;
        }
        if (!chr.isAlive()) {
            return false;
        }

        DebugLogger.DebugLog("OnUserMigrateToITCRequest : " + chr.getWorldId() + ", " + chr.getChannelId());

        chr.changeRemoval();
        chr.getWorld().addMigratingPlayer(chr);
        chr.getChannelServer().getOnlinePlayers().remove(chr);
        chr.sendMigrateCommand(chr.getWorld().getITC());
        chr.saveToDB(false, false);
        LazyDatabase.saveData(chr);
        chr.getMap().userLeaveField(chr);
        return true;
    }

    private static boolean OnUserExpUpItemUseRequest(MapleCharacter chr, short nPOS, int nItemID) {
        IItem item = chr.getInventory(MapleInventoryType.USE).getItem(nPOS);
        if (item == null || chr.getGashaEXP() > 0 || item.getItemId() != nItemID || (nItemID / 10000) != 237) {
            chr.sendStatChanged(true);
            return false;
        }
        // TODO : level check and save to DB.

        int exp_gasha = MapleItemInformationProvider.getInstance().getItemEffect(item.getItemId()).getExp();
        chr.setGashaEXP(exp_gasha);

        OnUserTempExpUseRequest(chr);

        // 兵法書実装前
        if (Version.LessOrEqual(Region.JMS, 131)) {
            while (OnUserTempExpUseRequest(chr)) {
                // loop
            }
        }

        MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, nItemID, 1, true, false);
        return true;
    }

    private static boolean OnUserTempExpUseRequest(MapleCharacter chr) {
        int exp_table = SharedExpTable.getExpNeededForLevel(chr.getLevel());
        int exp_current = chr.getExp();
        int exp_temp = chr.getGashaEXP();

        if (exp_temp <= 0) {
            chr.sendStatChanged(true);
            return false;
        }

        if (exp_table - exp_current - exp_temp > 0) {
            chr.setGashaEXP(0);
            chr.gainExp(exp_temp, true, true, false);
        } else {
            chr.setGashaEXP(exp_temp - (exp_table - exp_current));
            chr.gainExp(exp_table - exp_current, true, true, false);
        }

        chr.sendStatChanged(true);
        return true;
    }

    private static boolean OnTalkToTutor(MapleCharacter chr) {

        int job_id = chr.getJob();

        switch (job_id) {
            case 1000: {
                TacosScriptNPC.getInstance().start(chr.getClient(), 1101008);
                return true;
            }
            case 2000: {
                TacosScriptNPC.getInstance().start(chr.getClient(), 1202000);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }
}
