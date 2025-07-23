package server.network;

import constants.ServerConstants;
import client.MapleClient;
import config.Content;
import debug.Debug;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.handler.*;
import handling.login.LoginServer;
import server.Randomizer;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoSession;
import tools.FileoutputUtil;
import packet.ClientPacket;
import packet.request.AdminPacket;
import packet.request.ReqCUser_Dragon;
import packet.request.FriendRequest;
import packet.request.ItemRequest;
import packet.request.ReqCMobPool;
import packet.request.ReqCReactorPool;
import packet.request.ReqCSummonedPool;
import packet.request.ReqCUser;
import packet.request.ReqCUIItemUpgrade;
import packet.request.ReqCITC;
import packet.request.ReqCTownPortalPool;
import packet.request.ReqCUser_Pet;
import packet.request.ReqCCashShop;
import packet.request.ReqCClientSocket;
import packet.request.ReqCDropPool;
import packet.request.ReqCLogin;
import packet.request.Req_Farm;
import packet.response.ResCClientSocket;
import packet.response.ResCNpcPool;

public class MapleServerHandler extends IoHandlerAdapter {

    // サーバーの種類
    public enum ServerType {
        LoginServer,
        GameServer,
        PointShopServer,
        MapleTradeSpaceServer // PointShopと共通なので未使用
    }

    private int channel = -1;
    private ServerType server_type = ServerType.GameServer;

    public MapleServerHandler(final int channel, final ServerType st) {
        this.channel = channel;
        this.server_type = st;
    }

    @Override
    public void messageSent(final IoSession session, final Object message) throws Exception {
        final Runnable r = ((MaplePacket) message).getOnSend();
        if (r != null) {
            r.run();
        }
        super.messageSent(session, message);
    }

    // クライアントが接続
    @Override
    public void sessionOpened(final IoSession session) throws Exception {
        final String address = session.getRemoteAddress().toString();

        switch (server_type) {
            case LoginServer: {
                if (LoginServer.isShutdown()) {
                    session.close();
                    return;
                }
                Debug.InfoLog("[Login Server] Connected " + address);
                break;
            }
            case GameServer: {
                if (ChannelServer.getInstance(channel).isShutdown()) {
                    session.close();
                    return;
                }
                Debug.InfoLog("[Game Server " + String.format("%02d", channel) + "]  Connected " + address);
                break;
            }
            case PointShopServer: {
                if (CashShopServer.isShutdown()) {
                    session.close();
                    return;
                }
                Debug.InfoLog("[Cash Shop Server] Connected " + address);
                break;
            }
            case MapleTradeSpaceServer: {
                // ポイントショップとMTSを別のサーバーに分離した場合は修正が必要
                Debug.InfoLog("[Maple Trade Space Server] Connected " + address);
                break;
            }
            default: {
                Debug.ErrorLog("[Unknown Server] Connected " + address);
                break;
            }
        }

        final byte serverRecv[] = new byte[]{70, 114, 122, (byte) Randomizer.nextInt(255)};
        final byte serverSend[] = new byte[]{82, 48, 120, (byte) Randomizer.nextInt(255)};
        final byte ivRecv[] = ServerConstants.Use_Fixed_IV ? new byte[]{9, 0, 0x5, 0x5F} : serverRecv;
        final byte ivSend[] = ServerConstants.Use_Fixed_IV ? new byte[]{1, 0x5F, 4, 0x3F} : serverSend;

        final MapleClient client = new MapleClient(
                new MapleAESOFB(ivSend, server_type == ServerType.LoginServer, true), // Sent Cypher
                new MapleAESOFB(ivRecv, server_type == ServerType.LoginServer, false), // Recv Cypher
                session);
        client.setChannel(channel);

        MaplePacketDecoder.DecoderState decoderState = new MaplePacketDecoder.DecoderState();
        session.setAttribute(MaplePacketDecoder.DECODER_STATE_KEY, decoderState);

        session.write(ResCClientSocket.getHello(ServerConstants.Use_Fixed_IV ? serverSend : ivSend, ServerConstants.Use_Fixed_IV ? serverRecv : ivRecv));
        session.setAttribute(MapleClient.CLIENT_KEY, client);
        session.setIdleTime(IdleStatus.READER_IDLE, 60);
        session.setIdleTime(IdleStatus.WRITER_IDLE, 60);
    }

    // クライアントを切断
    @Override
    public void sessionClosed(final IoSession session) throws Exception {
        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

        if (client != null) {
            try {
                client.disconnect(true, server_type == ServerType.PointShopServer);
            } finally {
                session.close();
                session.removeAttribute(MapleClient.CLIENT_KEY);
            }
        }
        super.sessionClosed(session);
    }

    // Packet受信時の動作
    @Override
    public void messageReceived(final IoSession session, final Object message) {
        try {
            // please remove slea!
            SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream((byte[]) message));
            if (slea.available() < Content.PacketHeaderSize.getInt()) {
                return;
            }
            // client
            MapleClient c = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
            if (!c.isReceiving()) {
                return;
            }

            // TODO : remove
            if (Content.PacketHeaderSize.getInt() == 2) {
                slea.readShort(); // read header
            } else {
                slea.readByte();
            }

            // client packet
            ClientPacket cp = new ClientPacket((byte[]) message);
            short header_val = 0;
            if (Content.PacketHeaderSize.getInt() == 2) {
                header_val = cp.Decode2();
            } else {
                header_val = (short) (cp.Decode1() & 0xFF);
            }
            //Debug.DebugLog("DD = " + String.format("%02X", header_val));
            ClientPacket.Header header = ClientPacket.ToHeader(header_val);

            // not coded
            if (header == ClientPacket.Header.UNKNOWN) {
                Debug.CPLog(cp);
                return;
            }

            switch (server_type) {
                // ログインサーバー
                case LoginServer: {
                    if (!handleLoginPacket(header, cp, c)) {
                        Debug.CPLog(cp);
                    }
                    break;
                }
                // ゲームサーバー
                case GameServer: {
                    if (!handleGamePacket(slea, header, cp, c)) {
                        Debug.CPLog(cp);
                    }
                    break;
                }
                // ポイントショップとMTS (共通)
                case PointShopServer:
                case MapleTradeSpaceServer: {
                    if (!handlePointShopPacket(header, cp, c) && !handleMapleTradeSpacePacket(header, cp, c)) {
                        Debug.CPLog(cp);
                    }
                    break;
                }
                default: {
                    break;
                }
            }

        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
            e.printStackTrace();
        }
    }

    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status) throws Exception {
        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

        if (client != null) {
            client.sendPing();
        }

        super.sessionIdle(session, status);
    }

    // Login Server
    public static final boolean handleLoginPacket(ClientPacket.Header header, ClientPacket cp, MapleClient c) throws Exception {
        return ReqCLogin.OnPacket(header, cp, c);
    }

    // Point Shop (Cash Shop)
    public static final boolean handlePointShopPacket(ClientPacket.Header header, ClientPacket cp, MapleClient c) throws Exception {
        return ReqCCashShop.OnPacket(header, cp, c);
    }

    // Maple Trade Space (MTS)
    public static final boolean handleMapleTradeSpacePacket(ClientPacket.Header header, ClientPacket cp, MapleClient c) throws Exception {
        return ReqCITC.OnPacket(header, cp, c);
    }

    // Game Server
    // CClientSocket::ProcessPacket
    public static final boolean handleGamePacket(SeekableLittleEndianAccessor p, ClientPacket.Header header, ClientPacket cp, MapleClient c) throws Exception {
        // CClientSocket::ProcessUserPacket
        // CUser::OnPacket
        // CUser::OnPetPacket
        // CUser::OnFieldPacket
        // CUser::OnSummonedPacket
        switch (header) {
            case CP_MigrateIn:
            case CP_AliveAck:
            case CP_SecurityPacket: {
                return ReqCClientSocket.OnPacket(header, cp, c);
            }
            case CP_GoldHammerRequest: {
                return ReqCUIItemUpgrade.Accept(c, cp);
            }
            // サーバーメッセージ
            case CP_BroadcastMsg: {
                return true;
            }
            // GMコマンド
            case CP_Admin:
            // GMコマンドの文字列
            case CP_Log: {
                AdminPacket.OnPacket(cp, header, c);
                return true;
            }
            // 雪玉専用？
            case CP_EventStart: {
                return true;
            }
            // MapleTV
            case GM_COMMAND_MAPLETV: {
                return true;
            }
            // 未実装的な奴
            case CP_INVITE_PARTY_MATCH: {
                // @00EE Data: 91 00 00 00 A5 00 00 00 05 00 00 00 FF FF EF 0F
                return true;
            }
            case CP_CANCEL_INVITE_PARTY_MATCH: {
                // @00EF
                return true;
            }
            case CP_RaiseUIState: {
                // @0105 EC 1D 00 00 01
                // @0105 EC 1D 00 00 00
                // 布製の人形などETCアイテムからUIを開くタイプの処理
                // 最後の末尾のフラグが01なら開いて、00なら閉じる
                return true;
            }
            case CP_RaiseRefesh: {
                // @0104 EC 1D
                // ETCアイテムのUIの更新処理だと思われる
                return true;
            }
            case CP_RaiseIncExp: {
                // @0106 60 00 5E 85 3D 00 0D C4 00 00 64 00 00 00
                // ETCアイテムのUIにアイテムをドロップした際の処理
                return true;
            }
            // ウェディング系の謎UI
            case CP_WeddingWishListRequest: {
                // @0091 06 01 00 FA DD 13 00 01 00
                // アイテムを選択して送る
                // @0091 08
                // 出る
                return true;
            }
            // グループクエスト or 遠征隊検索
            case CP_PartyAdverRequest: {
                return true;
            }
            // CUser
            case CP_UserTransferFieldRequest:
            case CP_UserTransferChannelRequest:
            case CP_UserMigrateToCashShopRequest:
            case CP_UserMove:
            case CP_UserSitRequest:
            case CP_UserPortableChairSitRequest:
            case CP_UserMeleeAttack:
            case CP_UserShootAttack:
            case CP_UserMagicAttack:
            case CP_UserBodyAttack:
            case CP_UserHit:
            case CP_UserChat:
            case CP_UserADBoardClose:
            case CP_UserEmotion:
            case CP_UserActivateEffectItem:
            case CP_UserMonsterBookSetCover:
            case CP_UserSelectNpc:
            case CP_UserRemoteShopOpenRequest:
            case CP_UserScriptMessageAnswer:
            case CP_UserShopRequest:
            case CP_UserTrunkRequest:
            case CP_UserEntrustedShopRequest:
            case CP_UserStoreBankRequest:
            case CP_UserParcelRequest:
            case CP_UserEffectLocal:
            case CP_ShopScannerRequest:
            case CP_ShopLinkRequest:
            case CP_AdminShopRequest:
            case CP_UserSortItemRequest:
            case CP_UserGatherItemRequest:
            case CP_UserChangeSlotPositionRequest:
            case CP_UserStatChangeItemUseRequest:
            case CP_UserStatChangeItemCancelRequest:
            case CP_UserMobSummonItemUseRequest:
            case CP_UserPetFoodItemUseRequest:
            case CP_UserTamingMobFoodItemUseRequest:
            case CP_UserScriptItemUseRequest:
            case CP_UserConsumeCashItemUseRequest:
            case CP_UserDestroyPetItemRequest:
            case CP_UserBridleItemUseRequest:
            case CP_UserSkillLearnItemUseRequest:
            case CP_UserShopScannerItemUseRequest:
            case CP_UserPortalScrollUseRequest:
            case CP_UserUpgradeItemUseRequest:
            case CP_UserHyperUpgradeItemUseRequest:
            case CP_UserItemOptionUpgradeItemUseRequest:
            case CP_UserItemReleaseRequest:
            case CP_UserAbilityUpRequest:
            case CP_UserAbilityMassUpRequest:
            case CP_UserChangeStatRequest:
            case CP_UserSkillUpRequest:
            case CP_UserSkillUseRequest:
            case CP_UserSkillCancelRequest:
            case CP_UserSkillPrepareRequest:
            case CP_UserDropMoneyRequest:
            case CP_UserGivePopularityRequest:
            case CP_UserCharacterInfoRequest:
            case CP_UserActivatePetRequest:
            case CP_UserTemporaryStatUpdateRequest:
            case CP_UserPortalScriptRequest:
            case CP_UserPortalTeleportRequest:
            case CP_UserQuestRequest:
            case CP_UserCalcDamageStatSetRequest:
            case CP_UserMacroSysDataModified:
            case CP_UserUseGachaponBoxRequest:
            case CP_UserRepairDurabilityAll:
            case CP_UserRepairDurability:
            case CP_FuncKeyMappedModified:
            case CP_UserMigrateToITCRequest:
            case CP_UserExpUpItemUseRequest:
            case CP_UserTempExpUseRequest:
            case CP_TalkToTutor:
            case CP_RequestIncCombo:
            case CP_QuickslotKeyMappedModified:
            case CP_UpdateScreenSetting: {
                return ReqCUser.OnPacket(cp, header, c);
            }
            case CP_FamilyChartRequest:
            case CP_FamilyInfoRequest:
            case CP_FamilyRegisterJunior:
            case CP_FamilyUnregisterJunior:
            case CP_FamilyUnregisterParent:
            case CP_FamilyUsePrivilege:
            case CP_FamilySetPrecept:
            case CP_FamilySummonResult:
            case CP_FamilyJoinResult: {
                return ReqCUser.OnFamilyPacket(cp, header, c);
            }
            case CP_JMS_JUKEBOX:
            case CP_JMS_InstancePortalCreate:
            case CP_JMS_InstancePortalEnter: {
                return ItemRequest.OnPacket(header, cp, c);
            }
            // Pet
            case CP_PetMove:
            case CP_PetAction:
            case CP_PetInteractionRequest:
            case CP_PetDropPickUpRequest:
            case CP_PetStatChangeItemUseRequest:
            case CP_PetUpdateExceptionListRequest: {
                return ReqCUser_Pet.OnPetPacket(header, cp, c);
            }
            // CUser::OnSummonedPacket
            case CP_SummonedMove:
            case CP_SummonedAttack:
            case CP_SummonedHit:
            case CP_SummonedSkill:
            case CP_Remove: {
                return ReqCSummonedPool.OnPacket(cp, header, c);
            }
            case CP_DragonMove: {
                return ReqCUser_Dragon.OnMove(cp, c);
            }
            case CP_MobAttackMob:
            case CP_MobEscortCollision:
            case CP_MobRequestEscortInfo:
            case CP_MobMove:
            case CP_MobApplyCtrl:
            case CP_MobHitByMob:
            case CP_MobSelfDestruct: {
                return ReqCMobPool.OnPacket(cp, header, c);
            }
            case CP_NpcMove: {
                ResCNpcPool.NPCAnimation(p, c);
                return true;
            }
            case CP_DropPickUpRequest: {
                return ReqCDropPool.OnPacket(cp, header, c);
            }
            case CP_ReactorHit:
            case CP_ReactorTouch: {
                return ReqCReactorPool.OnPacket(cp, header, c);
            }
            case CP_MarriageRequest: {
                PlayersHandler.RingAction(p, c);
                return true;

            }
            case CP_UserMapTransferRequest: {
                // c
                PlayerHandler.TrockAddMap(p, c, c.getPlayer());
                return true;
            }
            case CP_MemoRequest: {
                // c
                PlayersHandler.Note(p, c.getPlayer());
                return true;
            }
            case CP_UserItemMakeRequest: {
                ItemMakerHandler.ItemMaker(p, c);
                return true;
            }
            case USE_TREASUER_CHEST: {
                // c
                InventoryHandler.UseTreasureChest(p, c, c.getPlayer());
                return true;
            }
            case CP_GroupMessage: {
                // c
                ChatHandler.Others(p, c, c.getPlayer());
                return true;
            }
            case CP_Whisper: {
                ChatHandler.Whisper_Find(p, c);
                return true;
            }
            case CP_Messenger: {
                ChatHandler.Messenger(p, c);
                return true;
            }
            case CP_MiniRoom: {
                // c
                PlayerInteractionHandler.PlayerInteraction(p, c, c.getPlayer());
                return true;
            }
            case CP_GuildRequest: {
                GuildHandler.Guild(p, c);
                return true;
            }
            case CP_GuildResult: {
                // 実装が悪い
                p.skip(1);
                GuildHandler.DenyGuildRequest(p.readMapleAsciiString(), c);
                return true;
            }
            case CP_AllianceRequest: {
                AllianceHandler.HandleAlliance(p, c, false);
                return true;
            }
            case CP_AllianceResult: {
                AllianceHandler.HandleAlliance(p, c, true);
                return true;
            }
            case CP_GuildBBS: {
                BBSHandler.BBSOperatopn(p, c);
                return true;
            }
            case CP_PartyRequest: {
                PartyHandler.PartyOperatopn(p, c);
                return true;
            }
            case CP_PartyResult: {
                PartyHandler.DenyPartyRequest(p, c);
                return true;
            }
            case CP_FriendRequest: {
                FriendRequest.OnPacket(cp, c);
                return true;
            }
            case CP_CONTISTATE: {
                // p
                UserInterfaceHandler.ShipObjectRequest(p.readInt(), c);
                return true;
            }

            case CP_MCarnivalRequest: {
                MonsterCarnivalHandler.MonsterCarnival(p, c);
                return true;
            }
            case LEFT_KNOCK_BACK: {
                PlayerHandler.leftKnockBack(p, c);
                return true;
            }
            case CP_SnowBallTouch: {
                PlayerHandler.snowBall(p, c);
                return true;
            }
            case CP_CoconutHit: {
                PlayersHandler.hitCoconut(p, c);
                return true;
            }
            case CP_RPSGame: {
                NPCHandler.RPSGame(p, c);
                return true;
            }
            case UPDATE_QUEST: {
                NPCHandler.UpdateQuest(p, c);
                return true;
            }
            case USE_ITEM_QUEST: {
                NPCHandler.UseItemQuest(p, c);
                return true;
            }
            case CP_UserFollowCharacterRequest: {
                PlayersHandler.FollowRequest(p, c);
                return true;
            }
            case CP_UserFollowCharacterWithdraw: {
                PlayersHandler.FollowReply(p, c);
                return true;
            }
            // パチンコ
            case BEANS_OPERATION: {
                BeanGame.BeanGame1(p, c);
                return true;
            }
            case BEANS_UPDATE: {
                BeanGame.BeanGame2(p, c);
                return true;
            }
            // ミスティックドア
            case CP_EnterTownPortalRequest: {
                ReqCTownPortalPool.TryEnterTownPortal(cp, c);
                return true;
            }
            // 農場
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

}
