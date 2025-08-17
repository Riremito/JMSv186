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

import packet.request.sub.ReqSub_FriendRequest;
import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.PlayerStats;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.IEquip;
import client.inventory.IItem;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MapleMount;
import client.messages.CommandProcessor;
import config.DeveloperMode;
import config.Region;
import config.ServerConfig;
import config.Version;
import constants.GameConstants;
import constants.ServerConstants;
import data.client.DC_Exp;
import debug.Debug;
import debug.DebugMan;
import debug.DebugShop;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.handler.AttackInfo;
import handling.channel.handler.HiredMerchantHandler;
import handling.channel.handler.InventoryHandler;
import handling.channel.handler.ItemMakerHandler;
import handling.channel.handler.NPCHandler;
import handling.channel.handler.PlayerHandler;
import handling.channel.handler.PlayerInteractionHandler;
import handling.channel.handler.PlayersHandler;
import handling.channel.handler.UserInterfaceHandler;
import handling.world.MapleParty;
import handling.world.World;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import packet.ClientPacket;
import packet.ops.OpsChangeStat;
import packet.ops.OpsChatGroup;
import packet.ops.OpsMapTransfer;
import packet.ops.OpsShopScanner;
import packet.ops.OpsTransferField;
import packet.ops.Ops_Whisper;
import packet.request.parse.ParseCMovePath;
import packet.request.sub.ReqSub_Admin;
import packet.request.sub.ReqSub_UserConsumeCashItemUseRequest;
import packet.response.ResCField;
import packet.response.ResCMobPool;
import packet.response.ResCUIVega;
import packet.response.ResCUser;
import packet.response.ResCUserLocal;
import packet.response.ResCUserRemote;
import packet.response.ResCWvsContext;
import packet.response.Res_JMS_CInstancePortalPool;
import packet.response.wrapper.ResWrapper;
import packet.response.wrapper.WrapCUserLocal;
import packet.response.wrapper.WrapCWvsContext;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.FieldLimitType;
import server.maps.MapleDynamicPortal;
import server.maps.MapleMap;
import server.shops.HiredMerchant;
import tools.AttackPair;
import tools.Pair;

/**
 *
 * @author Riremito
 */
public class ReqCUser {

    public static boolean OnPacket_Login(MapleClient c, ClientPacket.Header header, ClientPacket cp) {
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

    public static boolean OnPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return true;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return true;
        }

        switch (header) {
            case CP_UserTransferFieldRequest: {
                OnUserTransferFieldRequest(chr, cp);
                return true;
            }
            case CP_UserTransferChannelRequest: {
                OnTransferChannelRequest(cp, chr);
                return true;
            }
            case CP_UserMigrateToCashShopRequest: {
                ReqCClientSocket.EnterCS(c, chr, false);
                return true;
            }
            case CP_UserMove: {
                OnMove(cp, map, chr);
                return true;
            }
            case CP_UserSitRequest: {
                OnSitRequest(cp, chr);
                return true;
            }
            case CP_UserPortableChairSitRequest: {
                OnPortableChairSitRequest(cp, chr);
                return true;
            }
            case CP_UserMeleeAttack: {
                AttackInfo attack = OnAttack(cp, header, chr);
                PlayerHandler.closeRangeAttack(c, attack, false);
                return true;
            }
            case CP_UserShootAttack: {
                AttackInfo attack = OnAttack(cp, header, chr);
                PlayerHandler.rangedAttack(c, attack);
                return true;
            }
            case CP_UserMagicAttack: {
                AttackInfo attack = OnAttack(cp, header, chr);
                PlayerHandler.MagicDamage(c, attack);
                return true;
            }
            case CP_UserBodyAttack: {
                AttackInfo attack = OnAttack(cp, header, chr);
                PlayerHandler.closeRangeAttack(c, attack, true);
                return true;
            }
            case CP_UserHit: {
                PlayerHandler.TakeDamage(cp, c, chr);
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
                int emotion_id = cp.Decode4();
                PlayerHandler.ChangeEmotion(emotion_id, chr);
                return true;
            }
            case CP_UserActivateEffectItem: {
                int item_id = cp.Decode4();
                // pc
                PlayerHandler.UseItemEffect(item_id, c, chr);
                return true;
            }
            case CP_UserMonsterBookSetCover: {
                int unk = cp.Decode4();
                PlayerHandler.ChangeMonsterBookCover(unk, c, chr);
                return true;
            }
            case CP_UserSelectNpc: {
                int npc_oid = cp.Decode4();
                NPCHandler.NPCTalk(c, chr, npc_oid);
                return true;
            }
            case CP_UserRemoteShopOpenRequest: {
                short item_slot = cp.Decode2();
                ReqCMiniRoomBaseDlg.RemoteStore(chr, item_slot);
                return true;
            }
            case CP_UserScriptMessageAnswer: {
                if (chr.getDebugMan() != null) {
                    DebugMan.OnScriptMessageAnswerHook(chr, cp);
                    return true;
                }
                ReqCScriptMan.OnScriptMessageAnswer(cp, c);
                return true;
            }
            case CP_UserShopRequest: {
                if (chr.getDebugShop() != null) {
                    DebugShop.OnUserShopRequestHook(chr, cp);
                    return true;
                }
                ReqCShopDlg.OnPacket(cp, c);
                return true;
            }
            case CP_UserTrunkRequest: {
                ReqCTrunkDlg.OnPacket(cp, c);
                return true;
            }
            case CP_UserEntrustedShopRequest: {
                HiredMerchantHandler.UseHiredMerchant(c);
                return true;
            }
            case CP_UserStoreBankRequest: {
                return true;
            }
            case CP_UserEffectLocal: { // merchant?
                byte unk = cp.Decode1();
                HiredMerchantHandler.MerchantItemStore(c, unk, null); // not tested
                return true;
            }
            case CP_UserParcelRequest: {
                return ReqCParcelDlg.Accept(c, cp);
            }
            case CP_ShopScannerRequest: {
                OnShopScannerRequest(chr, cp);
                return true;
            }
            case CP_ShopLinkRequest: {
                int shop_id = cp.Decode4();
                int map_id = cp.Decode4();
                InventoryHandler.OwlWarp(c, shop_id, map_id);
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
                int item_id = cp.Decode4();
                PlayerHandler.CancelItemEffect(item_id, chr);
                return true;
            }
            case CP_UserMobSummonItemUseRequest: {
                int timestamp = cp.Decode4();
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4();
                boolean ret = OnUserMobSummonItemUseRequest(chr, item_slot, item_id);
                chr.SendPacket(ResCField.MobSummonItemUseResult(ret));
                chr.UpdateStat(true); // unlock is needed.
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
                //InventoryHandler.UseScriptedNPCItem(p, c, c.getPlayer());
                return true;
            }
            case CP_UserConsumeCashItemUseRequest: {
                if (!OnUserConsumeCashItemUseRequest(map, chr, cp)) {
                    chr.SendPacket(WrapCWvsContext.updateInv());
                }
                return true;
            }
            case CP_UserDestroyPetItemRequest: {
                // // 期限切れデンデン使用時のステータス更新とPointShopへ入場準備
                chr.UpdateStat(true); // OK, CANCEL 有効化
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
                chr.UpdateStat(true);
                return true;
            }
            case CP_JMS_MONSTERBOOK_SET: {
                int timestamp = cp.Decode4(); // 2114843894
                int item_slot = cp.Decode4();
                int song_time = cp.Decode4(); // 2560000

                // not coded.
                chr.UpdateStat(true);
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
                OnAbilityUpRequest(cp, chr);
                return true;
            }
            case CP_UserAbilityMassUpRequest: {
                OnAbilityMassUpRequest(cp, chr);
                return true;
            }
            case CP_UserChangeStatRequest: {
                OnChangeStatRequest(cp, chr);
                return true;
            }
            case CP_UserSkillUpRequest: {
                OnSkillUpRequest(cp, chr);
                return true;
            }
            case CP_UserSkillUseRequest: {
                OnSkillUseRequest(cp, chr);
                return true;
            }
            case CP_UserSkillCancelRequest: {
                OnSkillCancelRequest(cp, chr);
                return true;
            }
            case CP_UserSkillPrepareRequest: {
                OnSkillPrepareRequest(cp, chr);
                return true;
            }
            case CP_UserDropMoneyRequest: {
                int time_stamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
                int mesos = cp.Decode4();
                PlayerHandler.DropMeso(mesos, chr);
                return true;
            }
            case CP_UserGivePopularityRequest: {
                int target_id = cp.Decode4();
                byte mode = cp.Decode1();
                PlayersHandler.GiveFame(c, chr, target_id, mode);
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
                return true;
            }
            case CP_UserPortalScriptRequest: {
                OnUserPortalScriptRequest(chr, cp);
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
                NPCHandler.QuestAction(cp, c);
                return true;
            }
            case CP_UserCalcDamageStatSetRequest: {
                // @006A
                // バフを獲得するアイテムを使用した際に送信されている
                // 利用用途が不明だが、アイテム利用時ではなくてこちらが送信されたときにバフを有効にすべきなのかもしれない
                return true;
            }
            case CP_UserMacroSysDataModified: {
                return ReqCFuncKeyMappedMan.OnPacket(header, cp, c);
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
                    chr.UpdateStat(true);
                }
                return true;
            }
            case CP_UserRepairDurabilityAll: {
                NPCHandler.repairAll(c);
                return true;
            }
            case CP_UserRepairDurability: {
                //NPCHandler.repair(p, c);
                return true;
            }

            case CP_UserFollowCharacterRequest: {
                //PlayersHandler.FollowRequest(p, c);
                return true;
            }
            case CP_UserFollowCharacterWithdraw: {
                //PlayersHandler.FollowReply(p, c);
                return true;
            }
            case CP_GroupMessage: {
                OnGroupMessage(chr, cp);
                return true;
            }
            case CP_Whisper: {
                // 内緒話, 探す
                OnWhisper(chr, cp);
                return true;
            }
            case CP_Messenger: {
                return ReqCUIMessenger.OnPacket(c, header, cp);
            }
            case CP_MiniRoom: {
                return ReqCMiniRoomBaseDlg.OnMiniRoom(map, chr, cp);
            }
            case CP_PartyRequest: {
                //PartyHandler.PartyOperatopn(p, c);
                return true;
            }
            case CP_PartyResult: {
                //PartyHandler.DenyPartyRequest(p, c);
                return true;
            }
            case CP_GuildRequest: {
                //GuildHandler.Guild(p, c);
                return true;
            }
            case CP_GuildResult: {
                //p.skip(1);
                //GuildHandler.DenyGuildRequest(p.readMapleAsciiString(), c);
                return true;
            }
            case CP_Admin: {
                ReqSub_Admin.OnAdmin(chr, cp);
                return true;
            }
            case CP_Log: {
                String text = cp.DecodeStr();
                Debug.AdminLog("[OnLog] " + text);
                return true;
            }
            case CP_FriendRequest: {
                ReqSub_FriendRequest.OnFriendRequest(chr, cp);
                return true;
            }
            case CP_MemoRequest: {
                // c
                //PlayersHandler.Note(p, c.getPlayer());
                return true;
            }
            case CP_EnterTownPortalRequest: {
                ReqCTownPortalPool.TryEnterTownPortal(cp, c);
                return true;
            }
            case CP_FuncKeyMappedModified: {
                return ReqCFuncKeyMappedMan.OnPacket(header, cp, c);
            }
            case CP_RPSGame: {
                return ReqCRPSGameDlg.OnPacket(c, header, cp);
            }
            case CP_MarriageRequest: {
                //PlayersHandler.RingAction(p, c);
                return true;
            }

            case CP_AllianceRequest: {
                //AllianceHandler.HandleAlliance(p, c, false);
                return true;
            }
            case CP_AllianceResult: {
                //AllianceHandler.HandleAlliance(p, c, true);
                return true;
            }
            case CP_GuildBBS: {
                //BBSHandler.BBSOperatopn(p, c);
                return true;
            }
            case CP_JMS_InstancePortalEnter: {
                int portal_id = cp.Decode4();
                byte flag = cp.Decode1();
                // 749050200
                MapleDynamicPortal dynamic_portal = chr.getMap().findDynamicPortal(portal_id);
                if (dynamic_portal == null) {
                    chr.UpdateStat(true);
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
                map.broadcastMessage(Res_JMS_CInstancePortalPool.CreatePinkBeanEventPortal(dynamic_portal));
                chr.UpdateStat(true);
                return true;
            }
            case CP_UserMigrateToITCRequest: {
                ReqCClientSocket.EnterCS(c, chr, true);
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
                chr.UpdateStat(true);
                return true;
            }
            case CP_TalkToTutor: {
                UserInterfaceHandler.CygnusSummon_NPCRequest(c);
                return true;
            }
            case CP_RequestIncCombo: {
                PlayerHandler.AranCombo(c, chr);
                return true;
            }
            case CP_QuickslotKeyMappedModified: {
                return ReqCFuncKeyMappedMan.OnPacket(header, cp, c);
            }
            case CP_UpdateScreenSetting: // 解像度変更
            {
                byte screen = cp.Decode1(); // 00 = 800x600, 01 = 1024x768
                byte unk2 = cp.Decode1();
                byte unk3 = cp.Decode1();
                byte unk4 = cp.Decode1();
                return true;
            }
            case CP_JMS_FarmEnter:
            case CP_JMS_FarmLeave: {
                Req_Farm.OnPacket(header, cp, c);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnPacket_CS_ITC(MapleClient c, ClientPacket.Header header, ClientPacket cp) {
        switch (header) {
            case CP_UpdateScreenSetting: {
                return true;
            }
            default: {
                break;
            }
        }
        MapleCharacter chr = c.getPlayer();

        if (chr == null) {
            Debug.ErrorLog("character is not online.");
            return false;
        }

        switch (header) {
            case CP_UserTransferFieldRequest: {
                ReqCCashShop.LeaveCS(c, chr);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnFamilyPacket(ClientPacket cp, ClientPacket.Header header, MapleClient c) {
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
                //FamilyHandler.RequestFamily(p, c);
                return true;
            }
            case CP_FamilyInfoRequest: {
                //FamilyHandler.OpenFamily(p, c);
                return true;
            }
            case CP_FamilyRegisterJunior: {
                //FamilyHandler.FamilyOperation(p, c);
                return true;
            }
            case CP_FamilyUnregisterJunior: {
                //FamilyHandler.DeleteJunior(p, c);
                return true;
            }
            case CP_FamilyUnregisterParent: {
                //FamilyHandler.DeleteSenior(p, c);
                return true;
            }
            case CP_FamilyJoinResult: {
                //FamilyHandler.AcceptFamily(p, c);
                return true;
            }
            case CP_FamilyUsePrivilege: {
                //FamilyHandler.UseFamily(p, c);
                return true;
            }
            case CP_FamilySetPrecept: {
                //FamilyHandler.FamilyPrecept(p, c);
                return true;
            }
            case CP_FamilySummonResult: {
                //FamilyHandler.FamilySummon(p, c);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnUserTransferFieldRequest(MapleCharacter chr, ClientPacket cp) {
        boolean isKMS95orLater = Version.GreaterOrEqual(Region.KMS, 95) || Region.check(Region.IMS) || Region.check(Region.MSEA); // not in KMST391
        short unk1 = isKMS95orLater ? cp.Decode2() : 0; // ?_?
        int unk2 = isKMS95orLater ? cp.Decode4() : 0; // 0
        byte portal_count = cp.Decode1();
        int map_id = cp.Decode4(); // -1 = use portal, 0 = revivie, id = /map command.
        String portal_name = cp.DecodeStr();
        boolean isPortal = !portal_name.equals("");
        short x = isPortal ? cp.Decode2() : 0;
        short y = isPortal ? cp.Decode2() : 0;
        byte unk3 = cp.Decode1();
        byte unk4 = cp.Decode1(); // revive_type -> JMS302 = 4 bytes

        if (isPortal) {
            // map_id is -1. (in JMS.)
            if (chr.mapChangePortal(map_id, portal_name)) {
                return true;
            }
        } else {
            if (!chr.isAlive()) {
                // revive
                if (map_id == 0) {
                    final MapleMap to = (unk4 > 0) ? chr.getMap() : chr.getMap().getReturnMap();
                    chr.changeMap(to, to.getPortal(0));
                    chr.getStat().setHp(chr.getStat().getMaxHp());
                    chr.UpdateStat(true);
                    return true;
                }
                // hack?
            } else {
                if (chr.mapChangeDirect(map_id)) {
                    return true;
                }
                // error?
            }
        }

        Debug.ErrorLog("OnUserTransferFieldRequest : map_to = " + map_id + ", portal = \"" + portal_name + "\"");
        chr.SendPacket(ResCField.TransferFieldReqIgnored(OpsTransferField.TF_DISABLED_PORTAL));
        return false;
    }

    public static void OnTransferChannelRequest(ClientPacket cp, MapleCharacter chr) {
        int channel = cp.Decode1();
        if (!ReqCClientSocket.ChangeChannel(chr, channel)) {
            chr.UpdateStat(true);
        }
    }

    // BMS CUser::OnAttack
    public static final AttackInfo OnAttack(ClientPacket cp, ClientPacket.Header header, MapleCharacter chr) {
        final AttackInfo attack = new AttackInfo();

        // attack type
        attack.AttackHeader = header;
        // attacker data
        attack.CharacterId = chr.getId();
        attack.m_nLevel = chr.getLevel();
        attack.nSkillID = 0;
        attack.SkillLevel = 0;
        attack.nMastery = chr.getStat().passive_mastery();
        attack.nBulletItemID = 0;
        attack.X = chr.getPosition().x;
        attack.Y = chr.getPosition().y;

        attack.FieldKey = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode1();

        // DR_Check
        if (Version.LessOrEqual(Region.KMS, 114)) {
            // ?
        } else if (ServerConfig.JMS180orLater()) {
            cp.Decode4(); // pDrInfo.dr0
            cp.Decode4(); // pDrInfo.dr1
        }

        attack.HitKey = cp.Decode1(); // nDamagePerMob | (16 * nCount)

        // DR_Check
        if (Version.LessOrEqual(Region.KMS, 114)) {
            // ?
        } else if (ServerConfig.JMS180orLater()) {
            cp.Decode4(); // pDrInfo.dr2
            cp.Decode4(); // pDrInfo.dr3
        }

        attack.nSkillID = cp.Decode4();
        attack.skill = attack.nSkillID; // old

        if (0 < attack.nSkillID) {
            // skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            attack.SkillLevel = chr.getSkillLevel(attack.nSkillID);
        }

        // v95 1 byte cd->nCombatOrders
        if (Version.LessOrEqual(Region.KMS, 114)) {
            // ?
        } else if (ServerConfig.JMS180orLater()) {
            cp.Decode4(); // get_rand of DR_Check
            cp.Decode4(); // Crc32 of DR_Check
            // v95 4 bytes SKILLLEVELDATA::GetCrc
        }

        if (Version.PostBB()) {
            cp.Decode1();
        }

        if (Version.LessOrEqual(Region.KMS, 95)) {
            // ?
        } else if (ServerConfig.JMS164orLater()) {
            cp.Decode4(); // Crc
        }

        attack.tKeyDown = 0;
        if (attack.is_keydown_skill()) {
            attack.tKeyDown = cp.Decode4();
        }

        if (Version.GreaterOrEqual(Region.KMS, 114) || ServerConfig.JMS194orLater()) {
            if (attack.AttackHeader == ClientPacket.Header.CP_UserShootAttack) {
                cp.Decode1();
            }
        }

        attack.BuffKey = cp.Decode1();

        if (Version.LessOrEqual(Region.KMS, 65) || Version.LessOrEqual(Region.JMS, 165)) {
            attack.AttackActionKey = cp.Decode1();
        } else {
            attack.AttackActionKey = cp.Decode2(); // nAttackAction & 0x7FFF | (bLeft << 15)
        }

        if (Version.PostBB()) {
            cp.Decode4();
        }

        // v95 4 bytes crc
        attack.nAttackActionType = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode1();
        attack.nAttackSpeed = cp.Decode1();
        attack.tAttackTime = Version.LessOrEqual(Region.KMS, 1) ? 0 : cp.Decode4();

        if (Version.GreaterOrEqual(Region.KMS, 95) || ServerConfig.JMS186orLater()) {
            cp.Decode4(); // dwID
        }

        if (attack.AttackHeader == ClientPacket.Header.CP_UserShootAttack) {
            attack.ProperBulletPosition = cp.Decode2();
            attack.pnCashItemPos = cp.Decode2();
            attack.nShootRange0a = cp.Decode1(); // nShootRange0a, GetShootRange0 func, is AOE or not, TT/ Avenger = 41, Showdown = 0

            if (0 < attack.nShootRange0a && !attack.IsShadowMeso() && chr.getBuffedValue(MapleBuffStat.SOULARROW) == null) {
                IItem BulletItem;
                if (0 < attack.pnCashItemPos) {
                    BulletItem = chr.getInventory(MapleInventoryType.CASH).getItem(attack.pnCashItemPos);
                } else {
                    BulletItem = chr.getInventory(MapleInventoryType.USE).getItem(attack.ProperBulletPosition);
                }
                if (BulletItem != null) {
                    attack.nBulletItemID = BulletItem.getItemId();
                }
            }
        }

        int damage;
        List<Pair<Integer, Boolean>> allDamageNumbers = null;
        attack.allDamage = new ArrayList<AttackPair>();

        if (attack.IsMesoExplosion()) { // Meso Explosion
            return parseMesoExplosion(cp, attack);
        }

        for (int i = 0; i < attack.GetMobCount(); i++) {
            int nTargetID = cp.Decode4();

            // v131 to v186 OK
            cp.Decode1(); // v366->nHitAction
            cp.Decode1(); // v366->nForeAction & 0x7F | (v156 << 7)
            cp.Decode1(); // v366->nFrameIdx
            cp.Decode1(); // CalcDamageStatIndex & 0x7F | v158
            cp.Decode2(); // Mob Something
            cp.Decode2(); // Mob Something
            cp.Decode2(); // Mob Something
            cp.Decode2(); // Mob Something
            cp.Decode2(); // v366->tDelay

            allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();

            for (int j = 0; j < attack.GetDamagePerMob(); j++) {
                damage = cp.Decode4(); // 366->aDamage[i]

                allDamageNumbers.add(new Pair<Integer, Boolean>(Integer.valueOf(damage), false));
            }

            if (Version.LessOrEqual(Region.KMS, 65) || Version.Equal(Region.THMS, 87)) {
                // nothing
            } else if (ServerConfig.JMS164orLater()) {
                cp.Decode4(); // CMob::GetCrc(v366->pMob)
            }

            attack.allDamage.add(new AttackPair(Integer.valueOf(nTargetID), allDamageNumbers));
        }

        if (Version.GreaterOrEqual(Region.KMS, 65) || ServerConfig.JMS180orLater()) {
            if (attack.AttackHeader == ClientPacket.Header.CP_UserShootAttack) {
                cp.Decode4(); // v292->CUser::CLife::IVecCtrlOwner::vfptr->GetPos?
            }
        }
        // is_wildhunter_job
        // byte 2, m_ptBodyRelMove.y

        attack.position = new Point();
        attack.position.x = cp.Decode2();
        attack.position.y = cp.Decode2();

        if (DeveloperMode.DM_CHECK_DAMAGE.get()) {
            if (allDamageNumbers != null) {
                Debug.DebugLog(cp.GetOpcodeName() + ": damage = " + allDamageNumbers);
            }
        }

        return attack;
    }

    public static final AttackInfo parseMesoExplosion(ClientPacket cp, final AttackInfo ret) {
        //System.out.println(lea.toString(true));
        byte bullets;
        int damage;
        if (ret.GetDamagePerMob() == 0) {
            cp.Decode4();
            bullets = cp.Decode1();
            for (int j = 0; j < bullets; j++) {
                damage = cp.Decode4();

                if (DeveloperMode.DM_CHECK_DAMAGE.get()) {
                    Debug.DebugLog(cp.GetOpcodeName() + ": damage = " + damage);
                }
                ret.allDamage.add(new AttackPair(Integer.valueOf(damage), null));
                cp.Decode1();
            }
            cp.Decode2(); // 8F 02
            return ret;
        }

        int oid;
        List<Pair<Integer, Boolean>> allDamageNumbers;

        for (int i = 0; i < ret.GetMobCount(); i++) {
            oid = cp.Decode4();
            // ?
            cp.Decode4();
            cp.Decode4();
            cp.Decode4();
            bullets = cp.Decode1();
            allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();
            for (int j = 0; j < bullets; j++) {
                damage = cp.Decode4();

                if (DeveloperMode.DM_CHECK_DAMAGE.get()) {
                    Debug.DebugLog(cp.GetOpcodeName() + ": damage = " + damage);
                }
                allDamageNumbers.add(new Pair<Integer, Boolean>(Integer.valueOf(damage), false)); //m.e. never crits
            }

            if (ServerConfig.JMS186orLater()) {
                cp.Decode4(); // CRC of monster [Wz Editing]
            }

            ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers));
        }
        cp.Decode4();
        bullets = cp.Decode1();

        for (int j = 0; j < bullets; j++) {
            damage = cp.Decode4();

            if (DeveloperMode.DM_CHECK_DAMAGE.get()) {
                Debug.DebugLog(cp.GetOpcodeName() + ": damage = " + damage);
            }
            ret.allDamage.add(new AttackPair(Integer.valueOf(damage), null));
            cp.Decode2();
        }

        cp.Decode2(); // 8F 02/ 63 02
        return ret;
    }

    public static boolean OnMove(ClientPacket cp, MapleMap map, MapleCharacter chr) {
        if (chr.isHidden()) {
            return false;
        }

        // not in TWMS148, CMS104, but in TWMS125
        if (Version.GreaterOrEqual(Region.JMS, 186) || Version.Between(Region.TWMS, 121, 125) || Version.Between(Region.CMS, 85, 88)) {
            cp.Decode4(); // -1
            cp.Decode4(); // -1
        }

        cp.Decode1(); // unk

        // not in TWMS148, CMS104, but in TWMS125
        if (Version.GreaterOrEqual(Region.JMS, 186) || Version.Between(Region.TWMS, 121, 125) || Version.Between(Region.CMS, 85, 88)) {
            cp.Decode4(); // -1
            cp.Decode4(); // -1
            cp.Decode4();
            cp.Decode4();
        }

        if (Version.LessOrEqual(Region.KMS, 65)) {
            // nothing
        } else {
            // not in JMS147
            if (ServerConfig.JMS164orLater()) {
                cp.Decode4();
            }
        }

        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
            cp.Decode4();
        }

        ParseCMovePath move_path = new ParseCMovePath();
        if (move_path.Decode(cp)) {
            move_path.update(chr);
        }

        map.movePlayer(chr, chr.getPosition());
        map.broadcastMessage(chr, ResCUserRemote.Move(chr, move_path), false);

        // クローン : 移動
        if (chr.isCloning()) {
            MapleCharacter chr_clone = chr.getClone();
            move_path.update(chr_clone);
            map.movePlayer(chr_clone, chr_clone.getPosition());
            map.broadcastMessageClone(chr_clone, ResCUserRemote.Move(chr_clone, move_path));
        }
        return true;
    }

    public static boolean OnSitRequest(ClientPacket cp, MapleCharacter chr) {
        short map_chair_id = cp.Decode2();

        boolean is_cancel = (map_chair_id == -1);

        if (is_cancel) {
            // 釣り
            if (chr.getChair() == 3011000) {
                chr.cancelFishingTask();
            }
            chr.getMap().broadcastMessage(chr, ResCUserRemote.SetActivePortableChair(chr.getId(), 0), false);
        }

        chr.setChair(is_cancel ? 0 : map_chair_id);
        chr.SendPacket(ResCUserLocal.SitResult(map_chair_id));
        return true;
    }

    public static boolean OnPortableChairSitRequest(ClientPacket cp, MapleCharacter chr) {
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
        chr.getMap().broadcastMessage(chr, ResCUserRemote.SetActivePortableChair(chr.getId(), item_id), false);
        chr.SendPacket(WrapCWvsContext.updateInv()); // ?_?
        return true;
    }

    public static boolean OnUserChat(MapleCharacter chr, MapleMap map, ClientPacket cp) {
        int timestamp = (ServerConfig.JMS180orLater() || Region.IsBMS()) ? cp.Decode4() : 0;
        String message = cp.DecodeStr();
        boolean bOnlyBalloon = (ServerConfig.JMS147orLater() || Region.IsBMS()) ? (cp.Decode1() != 0) : false; // skill macro

        // command
        if (CommandProcessor.processCommand(chr.getClient(), message, ServerConstants.CommandType.NORMAL)) {
            return true;
        }

        map.broadcastMessage(ResCUser.UserChat(chr, message, bOnlyBalloon), chr.getPosition());
        return true;
    }

    // CUser::OnCharacterInfoRequest
    // CharInfoRequest
    public static final boolean OnCharacterInfoRequest(ClientPacket cp, MapleCharacter chr, MapleMap map) {
        // CCheatInspector::InspectExclRequestTime
        final int update_time = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
        final int m_dwCharacterId = cp.Decode4();
        final MapleCharacter player = map.getCharacterById(m_dwCharacterId); // CUser::FindUser

        if (player == null) {
            chr.SendPacket(WrapCWvsContext.updateStat());
            return false;
        }

        chr.SendPacket(ResCWvsContext.CharacterInfo(player, chr.getId() == m_dwCharacterId));
        return true;
    }

    public static final boolean OnUserActivatePetRequest(MapleCharacter chr, ClientPacket cp) {
        int timestamp = cp.Decode4();
        short item_slot = cp.Decode2();
        byte flag = (Version.LessOrEqual(Region.JMS, 131) || Version.PostBB()) ? 1 : cp.Decode1();

        chr.spawnPet(item_slot, flag > 0 ? true : false);
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

        Debug.ErrorLog("OnShopScannerRequest : not coded " + req);
        return false;
    }

    public static boolean OnUserShopScannerItemUseRequest(MapleCharacter chr, ClientPacket cp) {
        short owl_slot = cp.Decode2(); // inlined
        int owl_item_id = cp.Decode4(); // inlined
        int target_item_id = cp.Decode4();
        int timestamp = cp.Decode4();

        IItem item_used = chr.getInventory(MapleInventoryType.USE).getItem(owl_slot);
        if (item_used == null || item_used.getItemId() != 2310000) {
            Debug.ErrorLog("OnUserShopScannerItemUseRequest : invalid owl.");
            return false;
        }
        MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, owl_item_id, 1, true, false);
        final List<HiredMerchant> hms = chr.getClient().getChannelServer().searchMerchant(target_item_id);
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
                        target_map = chr.getClient().getChannelServer().getMapFactory().getMap(target_map_id);
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
                Debug.ErrorLog("OnUserMapTransferItemUseRequest : not coded " + cmd);
                break;
            }
        }
        int timestamp = cp.Decode4();

        chr.SendPacket(ResCWvsContext.MapTransferResult(chr, ops_res, false));
        if (ops_res == OpsMapTransfer.MapTransferRes_Use) {
            chr.changeMap(target_map, target_map.getPortal(0));
            return true;
        }
        chr.UpdateStat(true);
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
            chr.SendPacket(WrapCWvsContext.updateStat());
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
            chr.SendPacket(WrapCWvsContext.updateInv());
            return false;
        }
        // 黄金つち (ビシャスのハンマー)
        if (scroll.getItemId() == 2470000) {
            final Equip toHammer = (Equip) toScroll;
            if (toHammer.getViciousHammer() >= 2 || toHammer.getUpgradeSlots() > 120) {
                chr.SendPacket(WrapCWvsContext.updateInv());
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
                chr.SendPacket(WrapCWvsContext.updateInv());
                return false;
            }
        } else if (GameConstants.isEquipScroll(scroll.getItemId())) {
            if (toScroll.getUpgradeSlots() >= 1 || toScroll.getEnhance() >= 100 || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                chr.SendPacket(WrapCWvsContext.updateInv());
                return false;
            }
        } else if (GameConstants.isPotentialScroll(scroll.getItemId())) {
            if (toScroll.getHidden() >= 1 || (toScroll.getLevel() == 0 && toScroll.getUpgradeSlots() == 0) || vegas > 0 || ii.isCash(toScroll.getItemId())) {
                chr.SendPacket(WrapCWvsContext.updateInv());
                return false;
            }
        }
        if (!GameConstants.canScroll(toScroll.getItemId()) && !GameConstants.isChaosScroll(toScroll.getItemId())) {
            chr.SendPacket(WrapCWvsContext.updateInv());
            return false;
        }
        if ((GameConstants.isCleanSlate(scroll.getItemId()) || GameConstants.isTablet(scroll.getItemId()) || GameConstants.isChaosScroll(scroll.getItemId())) && (vegas > 0 || ii.isCash(toScroll.getItemId()))) {
            chr.SendPacket(WrapCWvsContext.updateInv());
            return false;
        }
        if (GameConstants.isTablet(scroll.getItemId()) && toScroll.getDurability() < 0) {
            //not a durability item
            chr.SendPacket(WrapCWvsContext.updateInv());
            return false;
        } else if (!GameConstants.isTablet(scroll.getItemId()) && toScroll.getDurability() >= 0) {
            chr.SendPacket(WrapCWvsContext.updateInv());
            return false;
        }
        IItem wscroll = null;
        // Anti cheat and validation
        List<Integer> scrollReqs = ii.getScrollReqs(scroll.getItemId());
        if (scrollReqs.size() > 0 && !scrollReqs.contains(toScroll.getItemId())) {
            chr.SendPacket(WrapCWvsContext.updateInv());
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
            chr.forceReAddItem(toScroll, MapleInventoryType.EQUIP);
            chr.SendPacket(ResCUIVega.Start());
            chr.SendPacket(ResCUIVega.Result(scrollSuccess == IEquip.ScrollResult.SUCCESS));
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
            chr.SendPacket(WrapCWvsContext.updateInv());
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
            chr.SendPacket(WrapCWvsContext.updateInv());
            //Debug.ErrorLog("potential err 2");
            return false;
        }

        return true;
    }

    public static boolean OnAbilityUpRequest(ClientPacket cp, MapleCharacter chr) {

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
                    chr.UpdateStat(true);
                    return false;
                }
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - 1));
        }
        chr.UpdateStat(true);

        return true;
    }

    public static boolean OnAbilityMassUpRequest(ClientPacket cp, MapleCharacter chr) {
        int time_stamp = cp.Decode4();
        int count = cp.Decode4(); // ループ数

        int PrimaryStat = 0;
        int amount = 0;
        int SecondaryStat = 0;
        int amount2 = 0;

        for (int i = 0; i < count; i++) {
            long stat = 0;

            if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
                stat = cp.Decode8();
            } else {
                stat = cp.Decode4();
            }

            int point = cp.Decode4();

            // test
            if (i == 0) {
                PrimaryStat = (int) stat;
                amount = point;
            } else if (i == 1) {
                SecondaryStat = (int) stat;
                amount2 = point;
            }
        }

        if (amount < 0 || amount2 < 0) {
            return false;
        }

        chr.updateTick(time_stamp);

        final PlayerStats playerst = chr.getStat();
        if (chr.getRemainingAp() == amount + amount2) {
            switch (PrimaryStat) {
                case 64:
                    // Str
                    if (playerst.getStr() + amount > 999) {
                        return false;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount));
                    break;
                case 128:
                    // Dex
                    if (playerst.getDex() + amount > 999) {
                        return false;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount));
                    break;
                case 256:
                    // Int
                    if (playerst.getInt() + amount > 999) {
                        return false;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount));
                    break;
                case 512:
                    // Luk
                    if (playerst.getLuk() + amount > 999) {
                        return false;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount));
                    break;
                default:
                    chr.UpdateStat(true);
                    return false;
            }
            switch (SecondaryStat) {
                case 64:
                    // Str
                    if (playerst.getStr() + amount2 > 999) {
                        return false;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount2));
                    break;
                case 128:
                    // Dex
                    if (playerst.getDex() + amount2 > 999) {
                        return false;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount2));
                    break;
                case 256:
                    // Int
                    if (playerst.getInt() + amount2 > 999) {
                        return false;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount2));
                    break;
                case 512:
                    // Luk
                    if (playerst.getLuk() + amount2 > 999) {
                        return false;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount2));
                    break;
                default:
                    chr.UpdateStat(true);
                    return false;
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - (amount + amount2)));
            chr.UpdateStat(true);
        }

        return true;
    }

    public static boolean OnChangeStatRequest(ClientPacket cp, MapleCharacter chr) {
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

        if ((update_mask[0] & OpsChangeStat.CS_HP.get()) > 0) {
            heal_hp = cp.Decode2();
        }
        if ((update_mask[0] & OpsChangeStat.CS_MP.get()) > 0) {
            heal_mp = cp.Decode2();
        }

        byte unk = cp.Decode1();

        if (Version.LessOrEqual(Region.KMS, 65)) {
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

    public static boolean OnSkillUpRequest(ClientPacket cp, MapleCharacter chr) {
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
                Debug.ErrorLog("Use SP 1 = " + skill_id);
                return false;
            }
        }
        final int maxlevel = skill.isFourthJob() ? chr.getMasterLevel(skill) : skill.getMaxLevel();
        final int curLevel = chr.getSkillLevel(skill);
        if (skill.isInvisible() && chr.getSkillLevel(skill) == 0) {
            if ((skill.isFourthJob() && chr.getMasterLevel(skill) == 0) || (!skill.isFourthJob() && maxlevel < 10 && !isBeginnerSkill)) {
                Debug.ErrorLog("Use SP 2 = " + skill_id);
                return false;
            }
        }
        for (int i : GameConstants.blockedSkills) {
            if (skill.getId() == i) {
                chr.dropMessage(1, "You may not add this skill.");
                Debug.ErrorLog("Use SP 3 = " + skill_id);
                return false;
            }
        }
        if ((remainingSp > 0 && curLevel + 1 <= maxlevel) && skill.canBeLearnedBy(chr.getJob())) {
            if (!isBeginnerSkill) {
                final int skillbook = GameConstants.getSkillBookForSkill(skill_id);
                chr.setRemainingSp(chr.getRemainingSp(skillbook) - 1, skillbook);
            }
            chr.UpdateStat(false);
            chr.changeSkillLevel(skill, (byte) (curLevel + 1), chr.getMasterLevel(skill));
            return true;
        }
        if ((remainingSp > 0 && curLevel + 1 <= maxlevel) && isBeginnerSkill) {
            chr.UpdateStat(false);
            chr.changeSkillLevel(skill, (byte) (curLevel + 1), chr.getMasterLevel(skill));
            return true;
        }
        Debug.ErrorLog("Use SP 4 = " + skill_id);
        return false;
    }

    public static boolean OnSkillUseRequest(ClientPacket cp, MapleCharacter chr) {
        int time_stamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
        int skill_id = cp.Decode4();
        byte skill_level = cp.Decode1();

        //Debug.DebugLog("OnSkillUseRequest :  " + skill_id);
        chr.updateTick(time_stamp);
        PlayerHandler.SpecialMove(chr, cp, skill_id, skill_level, null);
        return true;
    }

    // CancelBuffHandler
    public static boolean OnSkillCancelRequest(ClientPacket cp, MapleCharacter chr) {
        int skill_id = cp.Decode4();
        ISkill skill = SkillFactory.getSkill(skill_id);

        if (skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0);
            chr.getMap().broadcastMessage(chr, ResCUserRemote.SkillCancel(chr, skill_id), false);
        } else {
            chr.cancelEffect(skill.getEffect(1), false, -1);
        }

        // クローン : 暴風停止
        if (chr.isCloning()) {
            MapleCharacter chr_clone = chr.getClone();
            chr.getMap().broadcastMessageClone(chr_clone, ResCUserRemote.SkillCancel(chr_clone, skill_id));
        }

        return true;
    }

    public static boolean OnSkillPrepareRequest(ClientPacket cp, MapleCharacter chr) {
        int skill_id = cp.Decode4();
        byte skill_level = cp.Decode1();
        short action = 0;
        if (Version.GreaterOrEqual(Region.JMS, 186)) {
            action = cp.Decode2();
        } else {
            action = cp.Decode1();
        }
        byte m_nPrepareSkillActionSpeed = cp.Decode1();
        PlayerHandler.SkillEffect(chr, skill_id, skill_level, action, m_nPrepareSkillActionSpeed);
        return true;
    }

    public static boolean OnUserPortalScriptRequest(MapleCharacter chr, ClientPacket cp) {
        byte portal_count = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode1();
        String portal_name = cp.DecodeStr();
        short chr_x = cp.Decode2();
        short chr_y = cp.Decode2();
        //chr.DebugMsg("OnUserPortalScriptRequest : map = " + chr.getMap().getId() + ", portal = \"" + portal_name + "\"");
        if (!chr.mapChangePortal(chr.getMap().getId(), portal_name)) {
            //chr.SendPacket(ResCField.TransferFieldReqIgnored(OpsTransferField.TF_DISABLED_PORTAL));
            return false;
        }
        return true;
    }

    public static boolean OnUserPortalTeleportRequest(MapleCharacter chr, ClientPacket cp) {
        byte portal_count = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode1();
        String portal_name = cp.DecodeStr();
        short chr_x = cp.Decode2();
        short chr_y = cp.Decode2();
        short portal_to_x = cp.Decode2();
        short portal_to_y = cp.Decode2();

        //chr.DebugMsg("OnUserPortalTeleportRequest : map = " + chr.getMap().getId() + ", portal = \"" + portal_name + "\"");
        return true;
    }

    public static boolean OnUserMapTransferRequest(MapleCharacter chr, ClientPacket cp) {
        byte cmd = cp.Decode1();
        byte rock_type = cp.Decode1();
        OpsMapTransfer ops_req = OpsMapTransfer.find(cmd);
        int target_map_id = 999999999;

        if (ops_req == OpsMapTransfer.MapTransferReq_DeleteList) {
            target_map_id = cp.Decode4();
        }

        OpsMapTransfer ops_res = PlayerHandler.TrockAddMap(chr, ops_req, rock_type, target_map_id);
        chr.SendPacket(ResCWvsContext.MapTransferResult(chr, ops_res, rock_type != 0));
        return true;
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
        chr.SendPacket(WrapCWvsContext.updateStat()); // 必要
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
        chr.SendPacket(WrapCWvsContext.updateStat());
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
                Debug.ErrorLog("OnUserChangeSlotPositionRequest : user tried moving equipped slot " + slot_from + " -> " + slot_to);
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

        chr.useItem(item_slot, item_id);
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
        if (FieldLimitType.SummoningBag.check(map.getFieldLimit())) {
            return false;
        }
        // used
        MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.USE, item_slot, (short) 1, false);
        // spawn mobs
        List<Pair<Integer, Integer>> summon_info = MapleItemInformationProvider.getInstance().getSummonMobs(item_id);
        if (summon_info == null) {
            return true;
        }
        for (Pair<Integer, Integer> summon_data : summon_info) {
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
            map.broadcastMessage(ResCWvsContext.updateMount(chr, levelup));
            MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.USE, item_slot, (short) 1, false);
        }

        chr.SendPacket(WrapCWvsContext.updateStat());
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
                        map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), item_id, (byte) 1));
                        map.killMonster(mob, chr, true, false, (byte) 0);
                        MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, item_id, 1, false, false);
                        MapleInventoryManipulator.addById(chr.getClient(), 4001169, (short) 1);
                    } else {
                        map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), item_id, (byte) 0));
                        chr.SendPacket(ResWrapper.BroadCastMsgEvent("The monster has too much physical strength, so you cannot catch it."));
                    }
                    break;
                }
                case 2270002: {
                    if (mob.getHp() <= mob.getMobMaxHp() / 2) {
                        map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), item_id, (byte) 1));
                        map.killMonster(mob, chr, true, false, (byte) 0);
                        MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, item_id, 1, false, false);
                    } else {
                        map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), item_id, (byte) 0));
                        chr.SendPacket(ResWrapper.BroadCastMsgEvent("The monster has too much physical strength, so you cannot catch it."));
                    }
                    break;
                }
                case 2270000: {
                    // Pheromone Perfume
                    if (mob.getId() != 9300101) {
                        break;
                    }
                    map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), item_id, (byte) 1));
                    map.killMonster(mob, chr, true, false, (byte) 0);
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
                        map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), item_id, (byte) 1));
                        map.killMonster(mob, chr, true, false, (byte) 0);
                        MapleInventoryManipulator.removeById(chr.getClient(), MapleInventoryType.USE, item_id, 1, false, false);
                    } else {
                        map.broadcastMessage(ResCMobPool.catchMonster(mob.getId(), item_id, (byte) 0));
                        chr.SendPacket(ResWrapper.BroadCastMsgEvent("The monster has too much physical strength, so you cannot catch it."));
                    }
                    break;
                }
            }
        }

        chr.SendPacket(WrapCWvsContext.updateStat());
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
        chr.SendPacket(WrapCWvsContext.updateStat());
        return true;
    }

    public static boolean OnGroupMessage(MapleCharacter chr, ClientPacket cp) {
        int type = cp.Decode1();
        byte numRecipients = cp.Decode1();
        int recipients[] = new int[numRecipients];

        for (byte i = 0; i < numRecipients; i++) {
            recipients[i] = cp.Decode4();
        }

        String chattext = cp.DecodeStr();

        switch (OpsChatGroup.find(type)) {
            case CG_Friend: {
                World.Buddy.buddyChat(recipients, chr.getId(), chr.getName(), chattext);
                return true;
            }
            case CG_Party: {
                MapleParty party = chr.getParty();
                if (party != null) {
                    return true;
                }
                World.Party.partyChat(party.getId(), chattext, chr.getName());
                return true;
            }
            case CG_Guild: {
                int guild_id = chr.getGuildId();
                if (guild_id <= 0) {
                    return true;
                }
                World.Guild.guildChat(guild_id, chr.getName(), chr.getId(), chattext);
                return true;
            }
            case CG_Alliance: {
                int guild_id = chr.getGuildId();
                if (guild_id <= 0) {
                    return true;
                }
                World.Alliance.allianceChat(guild_id, chr.getName(), chr.getId(), chattext);
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

        Debug.ErrorLog("OnGroupMessage : not coded = " + type);
        return false;
    }

    private static boolean OnWhisper(MapleCharacter chr, ClientPacket cp) {
        int operation = cp.Decode1();
        Ops_Whisper loc_whis = Ops_Whisper.find(operation & ~Ops_Whisper.WP_Request.get());

        switch (loc_whis) {
            case WP_Location: {
                String player_name = cp.DecodeStr();

                int ch = World.Find.findChannel(player_name);
                Debug.DebugLog("CH = " + ch);
                MapleCharacter chr_to = null;
                // something wrong for cs
                if (0 < ch) {
                    chr_to = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(player_name);
                } else if (ch == -10) {
                    chr_to = CashShopServer.getPlayerStorage().getCharacterByName(player_name);
                } else if (ch == -20) {
                    chr_to = CashShopServer.getPlayerStorageMTS().getCharacterByName(player_name);
                }
                chr.SendPacket(ResCField.Whisper(Ops_Whisper.WP_Result, Ops_Whisper.WP_Location, chr, player_name, null, chr_to));
                return true;
            }
            case WP_Whisper: {
                String name_to = cp.DecodeStr();
                String message = cp.DecodeStr();
                int ch = World.Find.findChannel(name_to);
                if (ch < 0) {
                    chr.SendPacket(ResCField.Whisper(Ops_Whisper.WP_Result, Ops_Whisper.WP_Whisper, chr, name_to, message, null));
                    return false;
                }
                MapleCharacter chr_to = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(name_to);
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

        Debug.ErrorLog("OnWhisper : not coded " + operation);
        return false;
    }

    private static boolean OnUserExpUpItemUseRequest(MapleCharacter chr, short nPOS, int nItemID) {
        IItem item = chr.getInventory(MapleInventoryType.USE).getItem(nPOS);
        if (item == null || chr.getGashaEXP() > 0 || item.getItemId() != nItemID || (nItemID / 10000) != 237) {
            chr.UpdateStat(true);
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
        int exp_table = DC_Exp.getExpNeededForLevel(chr.getLevel());
        int exp_current = chr.getExp();
        int exp_temp = chr.getGashaEXP();

        if (exp_temp <= 0) {
            chr.UpdateStat(true);
            return false;
        }

        if (exp_table - exp_current - exp_temp > 0) {
            chr.setGashaEXP(0);
            chr.gainExp(exp_temp, true, true, false);
        } else {
            chr.setGashaEXP(exp_temp - (exp_table - exp_current));
            chr.gainExp(exp_table - exp_current, true, true, false);
        }

        chr.UpdateStat(true);
        return true;
    }

}
