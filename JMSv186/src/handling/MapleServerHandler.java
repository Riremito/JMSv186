package handling;

import constants.ServerConstants;
import client.MapleClient;
import command.GMCommand;
import debug.Debug;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.cashshop.handler.*;
import handling.channel.handler.*;
import handling.login.LoginServer;
import handling.login.handler.*;
import handling.mina.MaplePacketDecoder;
import server.Randomizer;
import tools.MapleAESOFB;
import tools.packet.LoginPacket;
import tools.data.input.ByteArrayByteStream;
import tools.data.input.GenericSeekableLittleEndianAccessor;
import tools.data.input.SeekableLittleEndianAccessor;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoSession;
import server.MTSStorage;
import tools.FileoutputUtil;
import handling.world.World;
import java.util.Map;
import packet.OutPacket;
import packet.SendPacket;
import server.Start;

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
                Debug.InfoLog("[LoginServer] " + address);
                break;
            }
            case GameServer: {
                if (ChannelServer.getInstance(channel).isShutdown()) {
                    session.close();
                    return;
                }
                Debug.InfoLog("[GameServer " + String.format("%02d", channel) + "] " + address);
                break;
            }
            case PointShopServer: {
                if (CashShopServer.isShutdown()) {
                    session.close();
                    return;
                }
                Debug.InfoLog("[PointShopServer] " + address);
                break;
            }
            case MapleTradeSpaceServer: {
                // ポイントショップとMTSを別のサーバーに分離した場合は修正が必要
                Debug.InfoLog("[MapleTradeSpaceServer] " + address);
                break;
            }
            default: {
                Debug.InfoLog("[UnknownServer] " + address);
                break;
            }
        }

        final byte serverRecv[] = new byte[]{70, 114, 122, (byte) Randomizer.nextInt(255)};
        final byte serverSend[] = new byte[]{82, 48, 120, (byte) Randomizer.nextInt(255)};
        final byte ivRecv[] = ServerConstants.Use_Fixed_IV ? new byte[]{9, 0, 0x5, 0x5F} : serverRecv;
        final byte ivSend[] = ServerConstants.Use_Fixed_IV ? new byte[]{1, 0x5F, 4, 0x3F} : serverSend;

        final MapleClient client = new MapleClient(
                new MapleAESOFB(ivSend, (short) (0xFFFF - Start.getMainVersion())), // Sent Cypher
                new MapleAESOFB(ivRecv, Start.getMainVersion()), // Recv Cypher
                session);
        client.setChannel(channel);

        MaplePacketDecoder.DecoderState decoderState = new MaplePacketDecoder.DecoderState();
        session.setAttribute(MaplePacketDecoder.DECODER_STATE_KEY, decoderState);

        session.write(LoginPacket.getHello(ServerConstants.Use_Fixed_IV ? serverSend : ivSend, ServerConstants.Use_Fixed_IV ? serverRecv : ivRecv));
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

    // ログをとる必要がないPacket
    private boolean IsAnnoyingPacket(OutPacket.Header r) {
        switch (r) {
            case CP_NpcMove:
            case CP_UserSkillUseRequest:
            case CP_UserShootAttack:
            case CP_UserMagicAttack:
            case CP_UserMeleeAttack:
            case CP_MobMove:
            case CP_UserChangeStatRequest:
            case CP_UserMove: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // ログをとる必要がないPacket (Headerが未定義)
    private boolean IsAnnoyingPacket(short r) {
        switch (r) {
            case 0x0010:
            case 0x00BF:
            case 0x000E:
            case 0x00DF: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // Packet受信時の動作
    @Override
    public void messageReceived(final IoSession session, final Object message) {
        try {
            final SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream((byte[]) message));
            if (slea.available() < 2) {
                return;
            }
            final short header_num = slea.readShort();

            for (final OutPacket.Header recv : OutPacket.Header.values()) {
                if (recv.Get() == header_num) {
                    final MapleClient c = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
                    if (!c.isReceiving()) {
                        return;
                    }

                    if (Start.getDebug()) {
                        Debug.DebugSendPacket((byte[]) message);
                    }

                    if (c.getPlayer() != null && c.getPlayer().GetDebugger() && !IsAnnoyingPacket(recv)) {
                        //Debug.DebugLog("[Packet] @" + String.format("%04X", header_num) + " " + slea.toString());
                    }

                    // ログインサーバー
                    if (server_type == ServerType.LoginServer) {
                        OutPacket op = new OutPacket((byte[]) message);
                        if (!handleLoginPacket(op, c)) {
                            //Debug.InfoLog("[ParseError] @" + String.format("%04X", header_num) + " " + slea.toString());
                        }
                        return;
                    }

                    // ポイントショップとMTSが共通のサーバーのため、ポイントショップで処理されなかったパケットはMTSのパケット扱いにする
                    if (server_type == ServerType.PointShopServer) {
                        if (!handlePointShopPacket(recv, slea, c)) {
                            if (!handleMapleTradeSpacePacket(recv, slea, c)) {
                                //Debug.InfoLog("[ParseError] @" + String.format("%04X", header_num) + " " + slea.toString());
                            }
                        }
                        return;
                    }

                    // ゲームサーバー
                    if (server_type == ServerType.GameServer) {
                        OutPacket op = new OutPacket((byte[]) message);
                        if (!handleGamePacket(slea, c, op)) {
                            //Debug.InfoLog("[ParseError] @" + String.format("%04X", header_num) + " " + slea.toString());
                        }
                        return;
                    }

                    return;
                }
            }
            final MapleClient c = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
            if ((server_type != ServerType.GameServer || (c.getPlayer() != null && c.getPlayer().GetDebugger())) && !IsAnnoyingPacket(header_num)) {
                //Debug.InfoLog("[UnknownPacket] @" + String.format("%04X", header_num) + " " + slea.toString());
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
    public static final boolean handleLoginPacket(OutPacket p, MapleClient c) throws Exception {
        short header = p.Decode2();
        OutPacket.Header type = OutPacket.ToHeader(header);

        if (Start.getMainVersion() >= 187) {
            Debug.DebugPacket(p);
        }

        switch (type) {
            // ログイン画面
            case CP_CreateSecurityHandle: {
                // +p
                c.getSession().write(LoginPacket.LoginAUTH(p, c));
                return true;
            }
            // ログイン
            case CP_CheckPassword: {
                if (CharLoginHandler.login(p, c)) {
                    InterServerHandler.SetLogin(false);
                    Debug.DebugLog("Login MapleID = " + c.getAccountName());
                }
                return true;
            }
            // サーバー一覧
            case CP_WorldInfoRequest: {
                // +p
                CharLoginHandler.ServerListRequest(c);
                return true;
            }
            // サーバーの状態
            case CP_CheckUserLimit: {
                // +p
                CharLoginHandler.ServerStatusRequest(c);
                return true;
            }
            // キャラクター一覧
            case CP_SelectWorld: {
                CharLoginHandler.CharlistRequest(p, c);
                return true;
            }
            // キャラクター作成時の名前重複確認
            case CP_CheckDuplicatedID: {
                // p
                CharLoginHandler.CheckCharName(p, c);
                return true;
            }
            // キャラクター作成
            case CP_CreateNewCharacter: {
                CharLoginHandler.CreateChar(p, c);
                return true;
            }
            // キャラクター削除
            case CP_DeleteCharacter: {
                CharLoginHandler.DeleteChar(p, c);
                return true;
            }
            // クラッシュデータ
            case CP_ExceptionLog: {
                // @000F EncodeBuffer(CrashDumpLog)
                // 起動時に何らかの条件で前回のクラッシュの詳細のテキストが送信される
                // 文字列で送信されているがnullで終わっていないので注意
                Debug.DebugPacket(p);
                return true;
            }
            // キャラクター選択
            case CP_SelectCharacter:
            case CP_CheckPinCode: {
                if (CharLoginHandler.Character_WithSecondPassword(p, c)) {
                    InterServerHandler.SetLogin(false);
                }
                return true;
            }
            // ログイン画面に到達
            case REACHED_LOGIN_SCREEN: {
                // @0018
                // ログイン画面に到達した場合に送信される
                Debug.DebugPacket(p);
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // Point Shop
    public static final boolean handlePointShopPacket(final OutPacket.Header header, final SeekableLittleEndianAccessor p, final MapleClient c) throws Exception {
        switch (header) {
            case CP_MigrateIn: {
                // +p
                final int playerid = p.readInt();
                CashShopOperation.EnterCS(playerid, c);
                Debug.DebugLog(c.getPlayer().getName() + " Enter PointShop");
                return true;
            }
            case CP_UserTransferFieldRequest: {
                // c
                CashShopOperation.LeaveCS(p, c, c.getPlayer());
                return true;
            }
            case CP_CashShopCashItemRequest: {
                // c
                CashShopOperation.BuyCashItem(p, c, c.getPlayer());
                return true;
            }
            case CP_CashShopCheckCouponRequest: {
                // 実装が悪い
                // 受け取りキャラクター指定した際にエラーしてる
                //FileoutputUtil.log(FileoutputUtil.PacketEx_Log, "Coupon : \n" + p.toString(true));
                //System.out.println(p.toString());
                p.skip(2);
                CashShopOperation.CouponCode(p.readMapleAsciiString(), c);
                return true;
            }
            case CP_CashShopChargeParamRequest: {
                // p
                // 充填ボタンをクリックした場合の処理
                // 公式サイトが開くような処理だったと思うが、特に何もしない
                CashShopOperation.CSUpdate(c);
                return true;
            }
            case CP_CashShopQueryCashRequest: {
                // p
                CashShopOperation.CSUpdate(c);
                return true;
            }
            case RECOMMENDED_AVATAR: {
                // @00FE
                // オススメアバターを選択した時の処理
                return true;
            }
            // アバターランダムボックスのオープン処理
            case CP_CashGachaponOpenRequest: {
                // @00AB [B0 58] 00 00 00 00 00 00
                // アイテムスロットが指定されている
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // MTS
    public static final boolean handleMapleTradeSpacePacket(final OutPacket.Header header, final SeekableLittleEndianAccessor p, final MapleClient c) throws Exception {
        switch (header) {
            case CP_MigrateIn: {
                // +p
                final int playerid = p.readInt();
                CashShopOperation.EnterCS(playerid, c);
                Debug.DebugLog(c.getPlayer().getName() + " Enter MTS");
                return true;
            }
            case CP_UserTransferFieldRequest: {
                // c
                CashShopOperation.LeaveCS(p, c, c.getPlayer());
                return true;
            }
            case TOUCHING_MTS: {
                // p
                MTSOperation.MTSUpdate(MTSStorage.getInstance().getCart(c.getPlayer().getId()), c);
                return true;
            }
            case MTS_TAB: {
                MTSOperation.MTSOperation(p, c);
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // Game Server
    public static final boolean handleGamePacket(final SeekableLittleEndianAccessor p, final MapleClient c, OutPacket op) throws Exception {
        short header = op.Decode2();
        OutPacket.Header type = OutPacket.ToHeader(header);

        switch (type) {
            case CP_UserParcelRequest: {
                return SendPacket.HomeDelivery.Accept(c, op);
            }
            case CP_GoldHammerRequest: {
                return SendPacket.ViciousHammer.Accept(c, op);
            }
            // サーバーメッセージ
            case CP_BroadcastMsg: {
                Debug.DebugPacket(op);
                return true;
            }
            // GMコマンド
            case CP_Admin: {
                Debug.DebugPacket(op);
                return GMCommand.Accept(p, c);
            }
            // GMコマンドの文字列
            case CP_Log: {
                Debug.DebugPacket(op);
                return true;
            }
            // 雪玉専用？
            case CP_EventStart: {
                Debug.DebugPacket(op);
                return true;
            }
            // MapleTV
            case GM_COMMAND_MAPLETV: {
                Debug.DebugPacket(op);
                return true;
            }
            // 未実装的な奴
            case CP_INVITE_PARTY_MATCH: {
                // @00EE Data: 91 00 00 00 A5 00 00 00 05 00 00 00 FF FF EF 0F
                Debug.DebugPacket(op);
                return true;
            }
            case CP_CANCEL_INVITE_PARTY_MATCH: {
                // @00EF
                Debug.DebugPacket(op);
                return true;
            }
            case CP_RaiseUIState: {
                // @0105 EC 1D 00 00 01
                // @0105 EC 1D 00 00 00
                // 布製の人形などETCアイテムからUIを開くタイプの処理
                // 最後の末尾のフラグが01なら開いて、00なら閉じる
                Debug.DebugPacket(op);
                return true;
            }
            case CP_RaiseRefesh: {
                // @0104 EC 1D
                // ETCアイテムのUIの更新処理だと思われる
                Debug.DebugPacket(op);
                return true;
            }
            case CP_RaiseIncExp: {
                // @0106 60 00 5E 85 3D 00 0D C4 00 00 64 00 00 00
                // ETCアイテムのUIにアイテムをドロップした際の処理
                Debug.DebugPacket(op);
                return true;
            }
            // ウェディング系の謎UI
            case CP_WeddingWishListRequest: {
                // @0091 06 01 00 FA DD 13 00 01 00
                // アイテムを選択して送る
                // @0091 08
                // 出る
                Debug.DebugPacket(op);
                return true;
            }
            // グループクエスト or 遠征隊検索
            case CP_PartyAdverRequest: {
                Debug.DebugPacket(op);
                return true;
            }
            //
            case CP_UserTransferChannelRequest: {
                // c
                InterServerHandler.ChangeChannel(p, c, c.getPlayer());
                return true;
            }
            case CP_MigrateIn: {
                // +p
                final int playerid = p.readInt();
                InterServerHandler.Loggedin(playerid, c);
                if (!InterServerHandler.GetLogin()) {
                    InterServerHandler.SetLogin(true);
                    Debug.DebugLog(c.getPlayer().getName() + " Login, MapID = " + c.getPlayer().getMapId());
                    Map<Integer, Integer> connected = World.getConnected();
                    c.getPlayer().Notify(c.getPlayer().getName() + " がログインしました（CH " + (c.getChannel()) + "） 現在の接続人数は" + connected.get(0) + "人です");
                } else {
                    Debug.DebugLog(c.getPlayer().getName() + " CC, MapID = " + c.getPlayer().getMapId());
                }
                return true;
            }
            case CP_UserMigrateToCashShopRequest: {
                // pc
                InterServerHandler.EnterCS(c, c.getPlayer(), false);
                return true;
            }
            case CP_UserMigrateToITCRequest: {
                // pc
                InterServerHandler.EnterCS(c, c.getPlayer(), true);
                return true;
            }
            case CP_UserMove: {
                // pc
                PlayerHandler.MovePlayer(p, c, c.getPlayer());
                return true;
            }
            case CP_UserCharacterInfoRequest: {
                // 実装が悪い
                p.readInt();
                PlayerHandler.CharInfoRequest(p.readInt(), c, c.getPlayer());
                return true;
            }
            case CP_UserMeleeAttack: {
                // c
                PlayerHandler.closeRangeAttack(p, c, c.getPlayer(), false);
                return true;
            }
            case CP_UserShootAttack: {
                // c
                PlayerHandler.rangedAttack(p, c, c.getPlayer());
                return true;
            }
            case CP_UserMagicAttack: {
                // c
                PlayerHandler.MagicDamage(p, c, c.getPlayer());
                return true;
            }
            case CP_UserSkillUseRequest: {
                // c
                PlayerHandler.SpecialMove(p, c, c.getPlayer());
                return true;
            }
            case CP_UserBodyAttack: {
                // c
                PlayerHandler.closeRangeAttack(p, c, c.getPlayer(), true);
                return true;
            }
            case CP_UserEmotion: {
                // pc
                PlayerHandler.ChangeEmotion(p.readInt(), c.getPlayer());
                return true;
            }
            case CP_UserHit: {
                // c
                PlayerHandler.TakeDamage(p, c, c.getPlayer());
                return true;
            }
            case CP_UserChangeStatRequest: {
                PlayerHandler.Heal(p, c.getPlayer());
                return true;
            }
            case CP_UserSkillCancelRequest: {
                // pc
                PlayerHandler.CancelBuffHandler(p.readInt(), c.getPlayer());
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
            case CP_UserSkillPrepareRequest: {
                // c
                PlayerHandler.SkillEffect(p, c.getPlayer());
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
            case CP_FuncKeyMappedModified: {
                // c
                PlayerHandler.ChangeKeymap(p, c.getPlayer());
                return true;
            }
            case CP_UserTransferFieldRequest: {
                // c
                PlayerHandler.ChangeMap(p, c, c.getPlayer());
                if (c.getPlayer().GetInformation()) {
                    c.getPlayer().Info("MapID = " + c.getPlayer().getMapId());
                }
                return true;
            }
            case CP_UserPortalScriptRequest: {
                // 実装が悪い
                p.skip(1);
                PlayerHandler.ChangeMapSpecial(p.readMapleAsciiString(), c, c.getPlayer());
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
            case CP_UserMacroSysDataModified: {
                // c
                PlayerHandler.ChangeSkillMacro(p, c.getPlayer());
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
            case CP_EnterTownPortalRequest: {
                // c
                PlayersHandler.UseDoor(p, c.getPlayer());
                return true;
            }
            case CP_ReactorHit: {
                PlayersHandler.HitReactor(p, c);
                return true;
            }
            case CP_ReactorTouch: {
                PlayersHandler.TouchReactor(p, c);
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
                InventoryHandler.UseCashItem(p, c, op);
                return true;
            }
            case CP_UserStatChangeItemUseRequest: {
                // c
                InventoryHandler.UseItem(p, c, c.getPlayer());
                return true;
            }
            case CP_UserItemReleaseRequest: {
                InventoryHandler.UseMagnify(p, c);
                return true;
            }
            case CP_UserScriptItemUseRequest: {
                // c
                InventoryHandler.UseScriptedNPCItem(p, c, c.getPlayer());
                return true;
            }
            case CP_UserPortalScrollUseRequest: {
                // c
                InventoryHandler.UseReturnScroll(p, c, c.getPlayer());
                return true;
            }
            case CP_UserUpgradeItemUseRequest: {
                // 実装が悪い
                p.readInt();
                if (InventoryHandler.UseUpgradeScroll((byte) p.readShort(), (byte) p.readShort(), (byte) p.readShort(), c, c.getPlayer())) {
                    c.getPlayer().saveToDB(false, false);
                }
                return true;
            }
            case CP_UserItemOptionUpgradeItemUseRequest: {
                // 実装が悪い
                p.readInt();
                if (InventoryHandler.UseUpgradeScroll((byte) p.readShort(), (byte) p.readShort(), (byte) 0, c, c.getPlayer())) {
                    c.getPlayer().saveToDB(false, false);
                }
                return true;
            }
            case CP_UserHyperUpgradeItemUseRequest: {
                // 実装が悪い
                p.readInt();
                if (InventoryHandler.UseUpgradeScroll((byte) p.readShort(), (byte) p.readShort(), (byte) 0, c, c.getPlayer())) {
                    c.getPlayer().saveToDB(false, false);
                }
                return true;
            }
            case CP_UserMobSummonItemUseRequest: {
                // c
                InventoryHandler.UseSummonBag(p, c, c.getPlayer());
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
                if (InventoryHandler.UseSkillBook((byte) p.readShort(), p.readInt(), c, c.getPlayer())) {
                    c.getPlayer().saveToDB(false, false);
                }
                return true;
            }
            case CP_UserBridleItemUseRequest: {
                // c
                InventoryHandler.UseCatchItem(p, c, c.getPlayer());
                return true;
            }
            case CP_UserTamingMobFoodItemUseRequest: {
                // c
                InventoryHandler.UseMountFood(p, c, c.getPlayer());
                return true;
            }
            case CP_UserUseGachaponBoxRequest: {
                // c
                InventoryHandler.UseRewardItem((byte) p.readShort(), p.readInt(), c, c.getPlayer());
                return true;
            }
            case CP_MobAttackMob: {
                // c
                MobHandler.HypnotizeDmg(p, c.getPlayer());
                return true;
            }
            case CP_MobEscortCollision: {
                // c
                MobHandler.MobNode(p, c.getPlayer());
                return true;
            }
            case CP_MobRequestEscortInfo: {
                // c
                MobHandler.DisplayNode(p, c.getPlayer());
                return true;
            }
            case CP_MobMove: {
                // c
                MobHandler.MoveMonster(p, c, c.getPlayer());
                return true;
            }
            case CP_MobApplyCtrl: {
                // c
                MobHandler.AutoAggro(p.readInt(), c.getPlayer());
                return true;
            }
            case CP_MobHitByMob: {
                // c
                MobHandler.FriendlyDamage(p, c.getPlayer());
                return true;
            }
            case CP_MobSelfDestruct: {
                // pc
                MobHandler.MonsterBomb(p.readInt(), c.getPlayer());
                return true;
            }
            case CP_UserShopRequest: {
                // c
                NPCHandler.NPCShop(p, c, c.getPlayer());
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
                // c
                NPCHandler.Storage(p, c, c.getPlayer());
                return true;
            }
            case CP_UserChat: {
                Debug.DebugPacket(op);
                // 実装が悪い
                if (Start.getMainVersion() > 164) {
                    p.readInt();
                }
                ChatHandler.GeneralChat(p.readMapleAsciiString(), p.readByte(), c, c.getPlayer());
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
                BuddyListHandler.BuddyOperation(p, c);
                return true;
            }
            case CYGNUS_SUMMON: {
                // 実装が悪い
                UserInterfaceHandler.CygnusSummon_NPCRequest(c);
                return true;
            }
            case SHIP_OBJECT: {
                // p
                UserInterfaceHandler.ShipObjectRequest(p.readInt(), c);
                return true;
            }
            case CP_SummonedHit: {
                // 実装が悪い
                p.skip(4);
                SummonHandler.DamageSummon(p, c.getPlayer());
                return true;
            }
            case CP_SummonedMove: {
                // c
                SummonHandler.MoveSummon(p, c.getPlayer());
                return true;
            }
            case CP_SummonedAttack: {
                // c
                SummonHandler.SummonAttack(p, c, c.getPlayer());
                return true;
            }
            case CP_DragonMove: {
                // c
                SummonHandler.MoveDragon(p, c.getPlayer());
                return true;
            }
            case CP_UserActivatePetRequest: {
                // c
                PetHandler.SpawnPet(p, c, c.getPlayer());
                return true;
            }
            case CP_PetMove: {
                // c
                PetHandler.MovePet(p, c.getPlayer());
                return true;
            }
            case CP_PetAction: {
                if (p.available() < 12) {
                    return false;
                }
                // p.readShort()
                // nullついてない文字数
                // p.readShort()
                // p.readMapleAsciiString()
                //PetHandler.PetChat((int) p.readLong(), p.readShort(), p.readMapleAsciiString(), c.getPlayer());
                PetHandler.PetChat(p, c.getPlayer());
                return true;
            }
            case CP_PetInteractionRequest: {
                // c
                PetHandler.PetCommand(p, c, c.getPlayer());
                return true;
            }
            case CP_UserPetFoodItemUseRequest: {
                // c
                PetHandler.PetFood(p, c, c.getPlayer());
                return true;
            }
            case CP_PetDropPickUpRequest: {
                // c
                InventoryHandler.Pickup_Pet(p, c, c.getPlayer());
                return true;
            }
            case CP_PetStatChangeItemUseRequest: {
                // c
                PetHandler.Pet_AutoPotion(p, c, c.getPlayer());
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
                //BeanGame.BeanGame1(p, c);
                return true;
            }
            case BEANS_UPDATE: {
                //BeanGame.BeanGame2(p, c);
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

}
