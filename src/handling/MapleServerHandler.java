package handling;

import constants.ServerConstants;
import client.MapleClient;
import config.*;
import debug.Debug;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.handler.*;
import handling.login.LoginServer;
import handling.mina.MaplePacketDecoder;
import server.Randomizer;
import tools.MapleAESOFB;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoSession;
import tools.FileoutputUtil;
import packet.ClientPacket;
import packet.request.AdminPacket;
import packet.request.ReqCParcelDlg;
import packet.request.ReqCUser_Dragon;
import packet.request.FriendRequest;
import packet.request.GashaEXPPacket;
import packet.request.ItemRequest;
import packet.request.ReqCFuncKeyMappedMan;
import packet.request.ReqCMobPool;
import packet.request.ReqCNpcPool;
import packet.request.PortalPacket;
import packet.request.ReqCReactorPool;
import packet.request.ReqCSummonedPool;
import packet.request.ReqCTrunkDlg;
import packet.request.ReqCUserPool;
import packet.request.ReqCUIItemUpgrade;
import packet.request.ReqCITC;
import packet.request.ReqCTownPortalPool;
import packet.request.ReqCUser_Pet;
import packet.request.ReqCCashShop;
import packet.request.ReqCLogin;
import packet.response.ResCClientSocket;
import packet.response.ResCLogin;

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
            if (slea.available() < 2) {
                return;
            }
            // client
            MapleClient c = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
            if (!c.isReceiving()) {
                return;
            }
            slea.readShort(); // read header

            // client packet
            ClientPacket cp = new ClientPacket((byte[]) message);
            short header_val = cp.Decode2();
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
            case CP_AliveAck: {
                return true;
            }
            case CP_UserParcelRequest: {
                return ReqCParcelDlg.Accept(c, cp);
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
            //
            case CP_UserTransferChannelRequest: {
                // c
                InterServerHandler.ChangeChannel(cp, c, c.getPlayer());
                return true;
            }
            case CP_MigrateIn: {
                // +p
                final int playerid = p.readInt();
                InterServerHandler.Loggedin(playerid, c);
                if (!InterServerHandler.GetLogin()) {
                    InterServerHandler.SetLogin(true);
                    Debug.UserInfoLog(c, "Enter Game");
                    c.SendPacket(ResCClientSocket.AuthenCodeChanged()); // internet cafe
                    //Map<Integer, Integer> connected = World.getConnected();
                    //c.getPlayer().Notify(c.getPlayer().getName() + " がログインしました（CH " + (c.getChannel()) + "） 現在の接続人数は" + connected.get(0) + "人です");
                } else {
                    Debug.UserInfoLog(c, "Change Channel");
                }
                return true;
            }
            case CP_UserCharacterInfoRequest:
            case CP_UserSkillPrepareRequest:
            case CP_UserChangeStatRequest:
            case CP_UserMove:
            case CP_UserMeleeAttack:
            case CP_UserShootAttack:
            case CP_UserMagicAttack:
            case CP_UserBodyAttack:
            case CP_UserHit:
            case CP_UserSkillCancelRequest: {
                ReqCUserPool.OnPacket(cp, header, c);
                return true;
            }
            case CP_UserSkillUseRequest: {
                // c
                PlayerHandler.SpecialMove(p, c, c.getPlayer());
                return true;
            }
            case CP_UserEmotion: {
                // pc
                PlayerHandler.ChangeEmotion(p.readInt(), c.getPlayer());
                return true;
            }
            case CP_UserStatChangeItemCancelRequest: {
                // pc
                PlayerHandler.CancelItemEffect(p.readInt(), c.getPlayer());
                return true;
            }
            case CP_UserPortableChairSitRequest: {
                // pc
                PlayerHandler.UseChair(p.readInt(), c, c.getPlayer());
                return true;
            }
            case CP_UserSitRequest: {
                // pc
                PlayerHandler.CancelChair(p.readShort(), c, c.getPlayer());
                return true;
            }
            case CP_UserActivateEffectItem:
            case WHEEL_OF_FORTUNE: {
                // pc
                PlayerHandler.UseItemEffect(p.readInt(), c, c.getPlayer());
                return true;
            }
            case CP_UserDropMoneyRequest: {
                // 実装が悪い
                p.readInt();
                PlayerHandler.DropMeso(p.readInt(), c.getPlayer());
                return true;
            }
            case CP_UserMonsterBookSetCover: {
                // pc
                PlayerHandler.ChangeMonsterBookCover(p.readInt(), c, c.getPlayer());
                return true;
            }
            case CP_UserTransferFieldRequest: {
                // c
                if (!PortalPacket.OnPacket(cp, header, c)) {
                    PlayerHandler.ChangeMap(p, c, c.getPlayer());
                }
                if (c.getPlayer().GetInformation()) {
                    c.getPlayer().Info("MapID = " + c.getPlayer().getMapId());
                }
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
            case CP_UserCalcDamageStatSetRequest: {
                // @006A
                // バフを獲得するアイテムを使用した際に送信されている
                // 利用用途が不明だが、アイテム利用時ではなくてこちらが送信されたときにバフを有効にすべきなのかもしれない
                return true;
            }
            case CP_UserMapTransferRequest: {
                // c
                PlayerHandler.TrockAddMap(p, c, c.getPlayer());
                return true;
            }
            case CP_RequestIncCombo: {
                // pc
                PlayerHandler.AranCombo(c, c.getPlayer());
                return true;
            }
            case CP_UserGivePopularityRequest: {
                // c
                PlayersHandler.GiveFame(p, c, c.getPlayer());
                return true;
            }
            /*
            case TRANSFORM_PLAYER: {
                // c
                PlayersHandler.TransformPlayer(p, c, c.getPlayer());
                return true;
            }
             */
            case CP_MemoRequest: {
                // c
                PlayersHandler.Note(p, c.getPlayer());
                return true;
            }
            case CP_ReactorHit:
            case CP_ReactorTouch: {
                ReqCReactorPool.OnPacket(cp, header, c);
                return true;
            }
            case CP_UserADBoardClose: {
                // 実装が悪い
                c.getPlayer().setChalkboard(null);
                return true;
            }
            case CP_UserItemMakeRequest: {
                ItemMakerHandler.ItemMaker(p, c);
                return true;
            }
            case CP_UserSortItemRequest: {
                InventoryHandler.ItemSort(p, c);
                return true;
            }
            case CP_UserGatherItemRequest: {
                InventoryHandler.ItemGather(p, c);
                return true;
            }
            case CP_UserChangeSlotPositionRequest: {
                InventoryHandler.ItemMove(p, c);
                return true;
            }
            case CP_DropPickUpRequest: {
                // c
                InventoryHandler.Pickup_Player(p, c, c.getPlayer());
                return true;
            }
            case CP_UserConsumeCashItemUseRequest: {
                if (!ItemRequest.OnPacket(header, cp, c)) {
                    InventoryHandler.UseCashItem(p, c, cp); // to do remove
                }
                return true;
            }
            case CP_UserItemReleaseRequest: {
                ItemRequest.UseMagnify(cp, c);
                return true;
            }
            case CP_UserScriptItemUseRequest: {
                // c
                InventoryHandler.UseScriptedNPCItem(p, c, c.getPlayer());
                return true;
            }
            case CP_UserPortalScrollUseRequest: {
                // c
                ItemRequest.UseReturnScroll(p, c, c.getPlayer());
                return true;
            }
            case CP_UserUpgradeItemUseRequest: {
                // 実装が悪い
                p.readInt();
                if (ItemRequest.UseUpgradeScroll((byte) p.readShort(), (byte) p.readShort(), (ServerConfig.version > 131) ? (byte) p.readShort() : (byte) p.readByte(), c, c.getPlayer())) {
                    c.getPlayer().saveToDB(false, false);
                }
                return true;
            }
            case CP_UserItemOptionUpgradeItemUseRequest: {
                // 実装が悪い
                p.readInt();
                if (ItemRequest.UseUpgradeScroll((byte) p.readShort(), (byte) p.readShort(), (byte) 0, c, c.getPlayer())) {
                    c.getPlayer().saveToDB(false, false);
                }
                return true;
            }
            case CP_UserHyperUpgradeItemUseRequest: {
                // 実装が悪い
                p.readInt();
                if (ItemRequest.UseUpgradeScroll((byte) p.readShort(), (byte) p.readShort(), (byte) 0, c, c.getPlayer())) {
                    c.getPlayer().saveToDB(false, false);
                }
                return true;
            }
            case CP_UserMobSummonItemUseRequest: {
                // c
                ItemRequest.UseSummonBag(p, c, c.getPlayer());
                return true;
            }
            case USE_TREASUER_CHEST: {
                // c
                InventoryHandler.UseTreasureChest(p, c, c.getPlayer());
                return true;
            }
            case CP_UserSkillLearnItemUseRequest: {
                // 実装が悪い
                p.readInt();
                if (ItemRequest.UseSkillBook((byte) p.readShort(), p.readInt(), c, c.getPlayer())) {
                    c.getPlayer().saveToDB(false, false);
                }
                return true;
            }
            case CP_UserBridleItemUseRequest: {
                // c
                ItemRequest.UseCatchItem(p, c, c.getPlayer());
                return true;
            }
            case CP_UserTamingMobFoodItemUseRequest: {
                // c
                ItemRequest.UseMountFood(p, c, c.getPlayer());
                return true;
            }
            case CP_UserUseGachaponBoxRequest: {
                // c
                InventoryHandler.UseRewardItem((byte) p.readShort(), p.readInt(), c, c.getPlayer());
                return true;
            }
            case CP_MobAttackMob:
            case CP_MobEscortCollision:
            case CP_MobRequestEscortInfo:
            case CP_MobMove:
            case CP_MobApplyCtrl:
            case CP_MobHitByMob:
            case CP_MobSelfDestruct: {
                ReqCMobPool.OnPacket(cp, header, c);
                return true;
            }
            case CP_UserShopRequest: {
                ReqCNpcPool.OnShopPacket(cp, c);
                return true;
            }
            case CP_UserSelectNpc: {
                // c
                NPCHandler.NPCTalk(p, c, c.getPlayer());
                return true;
            }
            // 雇用商店遠隔管理機
            case CP_UserRemoteShopOpenRequest: {
                // @0033 [02 00]
                // アイテムのスロット指定されているだけ
                PlayerInteractionHandler.RemoteStore(p, c);
                return true;
            }
            case CP_UserScriptMessageAnswer: {
                NPCHandler.NPCMoreTalk(p, c);
                return true;
            }
            case CP_NpcMove: {
                NPCHandler.NPCAnimation(p, c);
                return true;
            }
            case CP_UserQuestRequest: {
                // c
                NPCHandler.QuestAction(p, c, c.getPlayer());
                return true;
            }
            case CP_UserTrunkRequest: {
                // 倉庫
                ReqCTrunkDlg.OnPacket(cp, c);
                return true;
            }
            case CP_UserChat: {
                ChatHandler.GeneralChat(cp, c);
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
            case CP_UserAbilityMassUpRequest: {
                // c
                StatsHandling.AutoAssignAP(p, c, c.getPlayer());
                return true;
            }
            case CP_UserAbilityUpRequest: {
                // c
                StatsHandling.DistributeAP(p, c, c.getPlayer());
                return true;
            }
            case CP_UserSkillUpRequest: {
                // 実装が悪い
                p.readInt();
                StatsHandling.DistributeSP(p.readInt(), c, c.getPlayer());
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
            case CYGNUS_SUMMON: {
                // 実装が悪い
                UserInterfaceHandler.CygnusSummon_NPCRequest(c);
                return true;
            }
            case CP_CONTISTATE: {
                // p
                UserInterfaceHandler.ShipObjectRequest(p.readInt(), c);
                return true;
            }
            // CUser::OnSummonedPacket
            case CP_SummonedMove:
            case CP_SummonedAttack:
            case CP_SummonedHit:
            case CP_SummonedSkill:
            case CP_Remove: {
                ReqCSummonedPool.OnPacket(cp, header, c);
                return true;
            }
            case CP_DragonMove: {
                //EvanDragonRequest.MoveDragon(p, c.getPlayer());
                ReqCUser_Dragon.OnMove(cp, c);
                return true;
            }
            case CP_MCarnivalRequest: {
                MonsterCarnivalHandler.MonsterCarnival(p, c);
                return true;
            }
            case CP_UserEntrustedShopRequest: {
                HiredMerchantHandler.UseHiredMerchant(p, c);
                return true;
            }
            case CP_UserEffectLocal: {
                HiredMerchantHandler.MerchantItemStore(p, c);
                return true;
            }
            case CP_UserTemporaryStatUpdateRequest: {
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
            case CP_UserRepairDurability: {
                NPCHandler.repair(p, c);
                return true;
            }
            case CP_UserRepairDurabilityAll: {
                // p
                NPCHandler.repairAll(c);
                return true;
            }
            /*
            case GAME_POLL: {
                UserInterfaceHandler.InGame_Poll(p, c);
                return true;
            }
             */
            case CP_ShopScannerRequest: {
                // @003B 05
                // クライアントが不思議なフクロウのUIを開くときにパケットが送信されているが、UIはクライアント側で開くのでサーバーからは何も出来ない
                return true;
            }
            case CP_ShopLinkRequest: {
                // @003C B3 86 01 00 87 7F 3D 36
                InventoryHandler.OwlWarp(p, c);
                return true;
            }
            case CP_UserShopScannerItemUseRequest: {
                // @004C 0A 00 70 3F 23 00 85 84 1E 00 00 8C 4E 34 1A
                // 消費アイテム版の不思議なフクロウが存在し、専用のパケットが送信される
                InventoryHandler.OwlMinerva(p, c);
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
            case CP_MarriageRequest: {
                PlayersHandler.RingAction(p, c);
                return true;
            }
            case CP_FamilyChartRequest: {
                FamilyHandler.RequestFamily(p, c);
                return true;
            }
            case CP_FamilyInfoRequest: {
                FamilyHandler.OpenFamily(p, c);
                return true;
            }
            case CP_FamilyRegisterJunior: {
                FamilyHandler.FamilyOperation(p, c);
                return true;
            }
            case CP_FamilyUnregisterJunior: {
                FamilyHandler.DeleteJunior(p, c);
                return true;
            }
            case CP_FamilyUnregisterParent: {
                FamilyHandler.DeleteSenior(p, c);
                return true;
            }
            case CP_FamilyUsePrivilege: {
                FamilyHandler.UseFamily(p, c);
                return true;
            }
            case CP_FamilySetPrecept: {
                FamilyHandler.FamilyPrecept(p, c);
                return true;
            }
            case CP_FamilySummonResult: {
                FamilyHandler.FamilySummon(p, c);
                return true;
            }
            case CP_FamilyJoinResult: {
                FamilyHandler.AcceptFamily(p, c);
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
            // 兵法書
            case CP_UserExpUpItemUseRequest:
            case CP_UserTempExpUseRequest: {
                GashaEXPPacket.OnPacket(cp, header, c);
                return true;
            }
            case CP_UserStatChangeItemUseRequest:
            case CP_JMS_JUKEBOX:
            case CP_JMS_PINKBEAN_PORTAL_CREATE:
            case CP_JMS_PINKBEAN_PORTAL_ENTER: {
                ItemRequest.OnPacket(header, cp, c);
                return true;
            }
            // Pet
            case CP_UserDestroyPetItemRequest:
            case CP_UserActivatePetRequest:
            case CP_UserPetFoodItemUseRequest:
            case CP_PetMove:
            case CP_PetAction:
            case CP_PetInteractionRequest:
            case CP_PetDropPickUpRequest:
            case CP_PetStatChangeItemUseRequest:
            case CP_PetUpdateExceptionListRequest: {
                ReqCUser_Pet.OnPetPacket(header, cp, c);
                return true;
            }
            // KeyMap
            case CP_UserMacroSysDataModified:
            case CP_FuncKeyMappedModified:
            case CP_QuickslotKeyMappedModified: {
                ReqCFuncKeyMappedMan.OnPacket(header, cp, c);
                return true;
            }
            // ミスティックドア
            case CP_EnterTownPortalRequest: {
                ReqCTownPortalPool.TryEnterTownPortal(cp, c);
                return true;
            }
            // ポイントショップ
            case CP_UserMigrateToCashShopRequest: {
                return ReqCCashShop.OnPacket(header, cp, c);
            }
            // MTS
            case CP_UserMigrateToITCRequest: {
                return ReqCITC.OnPacket(header, cp, c);
            }
            default: {
                break;
            }
        }
        return false;
    }

}
