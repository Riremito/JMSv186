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
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.DeveloperMode;
import config.Region;
import config.ServerConfig;
import config.Version;
import constants.GameConstants;
import debug.Debug;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.handler.AttackInfo;
import handling.channel.handler.ChatHandler;
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
import packet.ClientPacket;
import packet.ops.OpsChangeStat;
import packet.ops.OpsMapTransfer;
import packet.ops.Ops_Whisper;
import packet.request.parse.ParseCMovePath;
import packet.response.ResCField;
import packet.response.ResCUserLocal;
import packet.response.ResCUserRemote;
import packet.response.ResCWvsContext;
import packet.response.wrapper.ResWrapper;
import packet.response.wrapper.WrapCUserLocal;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
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
                if (!PortalPacket.OnPacket(cp, header, c)) {
                    Debug.ErrorLog("chage map not coded yet");
                }
                if (c.getPlayer().GetInformation()) {
                    c.getPlayer().DebugMsg("MapID = " + c.getPlayer().getMapId());
                }
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
                ChatHandler.GeneralChat(cp, c);
                return true;
            }
            case CP_UserADBoardClose: {
                c.getPlayer().setChalkboard(null);
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
                PlayerInteractionHandler.RemoteStore(c);
                return true;
            }
            case CP_UserScriptMessageAnswer: {
                ReqCScriptMan.OnScriptMessageAnswer(cp, c);
                return true;
            }
            case CP_UserShopRequest: {
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
                // @003B 05
                // クライアントが不思議なフクロウのUIを開くときにパケットが送信されているが、UIはクライアント側で開くのでサーバーからは何も出来ない
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
            case CP_UserSortItemRequest: {
                int timestamp = cp.Decode4();
                byte slot_type = cp.Decode1();

                chr.updateTick(timestamp);
                InventoryHandler.ItemSort(c, slot_type);
                return true;
            }
            case CP_UserGatherItemRequest: {
                int timestamp = cp.Decode4();
                byte slot_type = cp.Decode1();

                chr.updateTick(timestamp);
                InventoryHandler.ItemGather(c, slot_type);
                return true;
            }
            case CP_UserChangeSlotPositionRequest: {
                int timestamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
                byte slot_type = cp.Decode1();
                short slot_from = cp.Decode2();
                short slot_to = cp.Decode2();
                short quantity = cp.Decode2();

                chr.updateTick(timestamp);
                InventoryHandler.ItemMove(c, slot_type, slot_from, slot_to, quantity);
                return true;
            }
            case CP_UserStatChangeItemUseRequest: {
                ItemRequest.OnPacket(header, cp, c);
                return true;
            }
            case CP_UserStatChangeItemCancelRequest: {
                int item_id = cp.Decode4();
                PlayerHandler.CancelItemEffect(item_id, chr);
                return true;
            }
            case CP_UserMobSummonItemUseRequest: {
                int timestamp = cp.Decode4();
                short slot = cp.Decode2();
                int item_id = cp.Decode4();
                boolean ret = OnUserMobSummonItemUseRequest(chr, slot, item_id);
                chr.SendPacket(ResCField.MobSummonItemUseResult(ret));
                chr.UpdateStat(true); // unlock is needed.
                return true;
            }
            case CP_UserPetFoodItemUseRequest: {
                ReqCUser_Pet.OnPetPacket(header, cp, c);
                return true;
            }
            case CP_UserTamingMobFoodItemUseRequest: {
                //ItemRequest.UseMountFood(p, c, c.getPlayer());
                return true;
            }
            case CP_UserScriptItemUseRequest: {
                //InventoryHandler.UseScriptedNPCItem(p, c, c.getPlayer());
                return true;
            }
            case CP_UserConsumeCashItemUseRequest: {
                if (!ItemRequest.OnPacket(header, cp, c)) {
                    //InventoryHandler.UseCashItem(p, c, cp); // to do remove
                }
                return true;
            }
            case CP_UserDestroyPetItemRequest: {
                ReqCUser_Pet.OnPetPacket(header, cp, c);
                return true;
            }
            case CP_UserBridleItemUseRequest: {
                //ItemRequest.UseCatchItem(p, c, c.getPlayer());
                return true;
            }
            case CP_UserSkillLearnItemUseRequest: {
                int time_stamp = cp.Decode4();
                short slot = cp.Decode2();
                int item_id = cp.Decode4();
                chr.updateTick(time_stamp);
                if (ItemRequest.UseSkillBook(slot, item_id, c, c.getPlayer())) {
                    c.getPlayer().saveToDB(false, false);
                }
                return true;
            }
            case CP_UserShopScannerItemUseRequest: {
                // 消費アイテム版の不思議なフクロウが存在し、専用のパケットが送信される
                short slot = cp.Decode2();
                int owl_item_id = cp.Decode4();
                int target_id = cp.Decode4();
                InventoryHandler.OwlMinerva(c, slot, owl_item_id, target_id);
                return true;
            }
            case CP_UserMapTransferItemUseRequest: {
                // 消費アイテムのテレポストーン
                OnUserMapTransferItemUseRequest(chr, cp);
                return true;
            }
            case CP_UserPortalScrollUseRequest: {
                int time_stamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
                short slot = cp.Decode2();
                int item_id = cp.Decode4();
                chr.updateTick(time_stamp);
                ItemRequest.UseReturnScroll(c, chr, slot, item_id);
                return true;
            }
            case CP_UserUpgradeItemUseRequest:
            case CP_UserHyperUpgradeItemUseRequest:
            case CP_UserItemOptionUpgradeItemUseRequest: {
                int time_stamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
                short scroll_slot = cp.Decode2();
                short equip_slot = cp.Decode2();
                chr.updateTick(time_stamp);
                if (ItemRequest.UseUpgradeScroll(scroll_slot, equip_slot, (byte) 0, c, chr)) {
                    c.getPlayer().saveToDB(false, false);
                }
                return true;
            }
            case CP_UserItemReleaseRequest: {
                ItemRequest.UseMagnify(cp, c);
                return true;
            }
            // AP使用
            case CP_UserAbilityUpRequest: {
                OnAbilityUpRequest(cp, chr);
                return true;
            }
            case CP_UserAbilityMassUpRequest: {
                OnAbilityMassUpRequest(cp, chr);
                return true;
            }
            // 自動回復類
            case CP_UserChangeStatRequest: {
                OnChangeStatRequest(cp, chr);
                return true;
            }
            // SP使用
            case CP_UserSkillUpRequest: {
                OnSkillUpRequest(cp, chr);
                return true;
            }
            case CP_UserSkillUseRequest: {
                OnSkillUseRequest(cp, chr);
                return true;
            }
            // buff
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
                ReqCUser_Pet.OnPetPacket(header, cp, c);
                return true;
            }
            case CP_UserTemporaryStatUpdateRequest: {
                return true;
            }
            case CP_UserPortalScriptRequest: {
                PlayerHandler.ChangeMapSpecial(cp, c);
                return true;
            }
            case CP_UserPortalTeleportRequest: {
                // @0063 [13] [04 00 75 70 30 30] [9F 01] [04 00] [C9 01] [F4 FE]
                // ポータルカウント, ポータル名, 元のX座標, 元のY座標, 移動先のX座標, 移動先のY座標
                // ポータル利用時のスクリプト実行用だがJMSとEMS以外では利用されておらず意味がない
                // サーバー側で特にみる必要もないが、マップ内ポータルを利用した時にサーバー側でスクリプトを実行したい場合は必要になる
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
                return ReqCMiniRoomBaseDlg.OnPacket(c, header, cp);
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
            case CP_JMS_JUKEBOX:
            case CP_JMS_InstancePortalCreate:
            case CP_JMS_InstancePortalEnter: {
                return ItemRequest.OnPacket(header, cp, c);
            }
            case CP_UserMigrateToITCRequest: {
                ReqCClientSocket.EnterCS(c, chr, true);
                return true;
            }
            // 兵法書
            case CP_UserExpUpItemUseRequest:
            case CP_UserTempExpUseRequest: {
                GashaEXPPacket.OnPacket(cp, header, c);
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
        chr.SendPacket(ResWrapper.StatChanged(chr)); // ?_?
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
            chr.SendPacket(ResWrapper.StatChanged(chr));
            return false;
        }

        chr.SendPacket(ResCWvsContext.CharacterInfo(player, chr.getId() == m_dwCharacterId));
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

    public static boolean OnUserMobSummonItemUseRequest(MapleCharacter chr, short slot, int item_id) {
        IItem item_used = chr.getInventory(MapleInventoryType.USE).getItem(slot);
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
        MapleInventoryManipulator.removeFromSlot(chr.getClient(), MapleInventoryType.USE, slot, (short) 1, false);
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

    public static boolean OnGroupMessage(MapleCharacter chr, ClientPacket cp) {
        int type = cp.Decode1();
        byte numRecipients = cp.Decode1();
        int recipients[] = new int[numRecipients];

        for (byte i = 0; i < numRecipients; i++) {
            recipients[i] = cp.Decode4();
        }

        String chattext = cp.DecodeStr();

        switch (type) {
            case 0: {
                World.Buddy.buddyChat(recipients, chr.getId(), chr.getName(), chattext);
                return true;
            }
            case 1: {
                MapleParty party = chr.getParty();
                if (party != null) {
                    return true;
                }
                World.Party.partyChat(party.getId(), chattext, chr.getName());
                return true;
            }
            case 2: {
                int guild_id = chr.getGuildId();
                if (guild_id <= 0) {
                    return true;
                }
                World.Guild.guildChat(guild_id, chr.getName(), chr.getId(), chattext);
                return true;
            }
            case 3: {
                int guild_id = chr.getGuildId();
                if (guild_id <= 0) {
                    return true;
                }
                World.Alliance.allianceChat(guild_id, chr.getName(), chr.getId(), chattext);
                return true;
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
}
