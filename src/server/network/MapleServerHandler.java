package server.network;

import client.MapleClient;
import config.Content;
import debug.Debug;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import server.Randomizer;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoSession;
import tools.FileoutputUtil;
import packet.ClientPacket;
import packet.request.ReqCUser_Dragon;
import packet.request.ReqCMobPool;
import packet.request.ReqCReactorPool;
import packet.request.ReqCSummonedPool;
import packet.request.ReqCUser;
import packet.request.ReqCUIItemUpgrade;
import packet.request.ReqCITC;
import packet.request.ReqCUser_Pet;
import packet.request.ReqCCashShop;
import packet.request.ReqCClientSocket;
import packet.request.ReqCDropPool;
import packet.request.ReqCField;
import packet.request.ReqCField_Coconut;
import packet.request.ReqCField_MonsterCarnival;
import packet.request.ReqCField_SnowBall;
import packet.request.ReqCLogin;
import packet.request.ReqCNpcPool;
import packet.request.Req_MapleTV;
import packet.response.ResCClientSocket;

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
                    Debug.ErrorLog("sessionOpened dc 1.");
                    session.close();
                    return;
                }
                Debug.InfoLog("[Login Server] Connected " + address);
                break;
            }
            case GameServer: {
                if (ChannelServer.getInstance(channel).isShutdown()) {
                    Debug.ErrorLog("sessionOpened dc 2.");
                    session.close();
                    return;
                }
                Debug.InfoLog("[Game Server " + String.format("%02d", channel) + "]  Connected " + address);
                break;
            }
            case PointShopServer: {
                if (CashShopServer.isShutdown()) {
                    Debug.ErrorLog("sessionOpened dc 3.");
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

        final MapleClient client = new MapleClient(
                new MapleAESOFB(serverSend, server_type == ServerType.LoginServer, true), // Sent Cypher
                new MapleAESOFB(serverRecv, server_type == ServerType.LoginServer, false), // Recv Cypher
                session);
        client.setChannel(channel);

        session.write(ResCClientSocket.getHello(serverSend, serverRecv));
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
                //Debug.InfoLog("sessionClosed dc.");
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
            ClientPacket cp = new ClientPacket((byte[]) message);
            if (cp.getSize() < Content.PacketHeaderSize.getInt()) {
                return;
            }
            // client
            MapleClient c = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
            if (c.isOffline()) {
                Debug.ErrorLog("messageReceived : Migrating.");
                return;
            }

            short header_val = 0;
            if (Content.PacketHeaderSize.getInt() == 2) {
                header_val = cp.Decode2();
            } else {
                header_val = (short) (cp.Decode1() & 0xFF);
            }

            ClientPacket.Header header = ClientPacket.ToHeader(header_val);

            // not coded
            if (header == ClientPacket.Header.UNKNOWN) {
                Debug.CPLog(cp);
                return;
            }

            switch (server_type) {
                // ログインサーバー
                case LoginServer: {
                    if (!handleLoginPacket(c, header, cp)) {
                        Debug.CPLog(cp);
                    }
                    break;
                }
                // ゲームサーバー
                case GameServer: {
                    if (!handleGamePacket(c, header, cp)) {
                        Debug.CPLog(cp);
                    }
                    break;
                }
                case PointShopServer:
                case MapleTradeSpaceServer: {
                    // currently CS and ITC are same server.
                    if (!handlePointShopPacket(c, header, cp) && !handleMapleTradeSpacePacket(c, header, cp)) {
                        Debug.CPLog(cp);
                    }
                    break;
                }
                /*
                case PointShopServer: {
                    if (!handlePointShopPacket(c, header, cp)) {
                        Debug.CPLog(cp);
                    }
                    break;
                }
                case MapleTradeSpaceServer: {
                    if (!handleMapleTradeSpacePacket(c, header, cp)) {
                        Debug.CPLog(cp);
                    }
                    break;
                }
                 */
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
            client.SendPacket(ResCClientSocket.AliveReq());
        }

        super.sessionIdle(session, status);
    }

    public static final boolean handleLoginPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) throws Exception {
        if (header.between(ClientPacket.Header.CP_BEGIN_SOCKET, ClientPacket.Header.CP_END_SOCKET)) {
            if (ReqCClientSocket.OnPacket_Login(c, header, cp)) {
                return true;
            }
            return ReqCLogin.OnPacket(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_USER, ClientPacket.Header.CP_END_USER)) {
            return ReqCUser.OnPacket_Login(c, header, cp);
        }
        return false;
    }

    public static final boolean handlePointShopPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) throws Exception {
        if (header.between(ClientPacket.Header.CP_BEGIN_SOCKET, ClientPacket.Header.CP_END_SOCKET)) {
            return ReqCClientSocket.OnPacket_CS_ITC(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_USER, ClientPacket.Header.CP_END_USER)) {
            return ReqCUser.OnPacket_CS_ITC(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_CASHSHOP, ClientPacket.Header.CP_END_CASHSHOP)) {
            return ReqCCashShop.OnPacket(c, header, cp);
        }
        return false;
    }

    public static final boolean handleMapleTradeSpacePacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) throws Exception {
        if (header.between(ClientPacket.Header.CP_BEGIN_SOCKET, ClientPacket.Header.CP_END_SOCKET)) {
            return ReqCClientSocket.OnPacket_CS_ITC(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_USER, ClientPacket.Header.CP_END_USER)) {
            return ReqCUser.OnPacket_CS_ITC(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_ITC, ClientPacket.Header.CP_END_ITC)) {
            return ReqCITC.OnPacket(c, header, cp);
        }
        return false;
    }

    // Game Server
    // CClientSocket::ProcessPacket
    public static final boolean handleGamePacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) throws Exception {
        // socket
        if (header.between(ClientPacket.Header.CP_BEGIN_SOCKET, ClientPacket.Header.CP_END_SOCKET)) {
            return ReqCClientSocket.OnPacket(c, header, cp);
        }
        // user
        if (header.between(ClientPacket.Header.CP_BEGIN_USER, ClientPacket.Header.CP_END_USER)) {
            // family
            if (header.between(ClientPacket.Header.CP_FamilyChartRequest, ClientPacket.Header.CP_FamilySummonResult)) {
                return ReqCUser.OnFamilyPacket(c, header, cp);
            }
            // pet
            if (header.between(ClientPacket.Header.CP_BEGIN_PET, ClientPacket.Header.CP_END_PET)) {
                return ReqCUser_Pet.OnPetPacket(c, header, cp);
            }
            // summon
            if (header.between(ClientPacket.Header.CP_BEGIN_SUMMONED, ClientPacket.Header.CP_END_SUMMONED)) {
                return ReqCSummonedPool.OnPacket(c, header, cp);
            }
            // dragon
            if (header.between(ClientPacket.Header.CP_BEGIN_DRAGON, ClientPacket.Header.CP_END_DRAGON)) {
                return ReqCUser_Dragon.OnMove(c, header, cp);
            }
            return ReqCUser.OnPacket(c, header, cp);
        }
        // field
        if (header.between(ClientPacket.Header.CP_BEGIN_FIELD, ClientPacket.Header.CP_END_FIELD)) {
            // life
            if (header.between(ClientPacket.Header.CP_BEGIN_LIFEPOOL, ClientPacket.Header.CP_END_LIFEPOOL)) {
                // mob
                if (header.between(ClientPacket.Header.CP_BEGIN_MOB, ClientPacket.Header.CP_END_MOB)) {
                    return ReqCMobPool.OnPacket(c, header, cp);
                }
                // npc
                if (header.between(ClientPacket.Header.CP_BEGIN_NPC, ClientPacket.Header.CP_END_NPC)) {
                    ReqCNpcPool.OnPacket(c, header, cp);
                    return true;
                }
                return false;
            }
            // drop
            if (header.between(ClientPacket.Header.CP_BEGIN_DROPPOOL, ClientPacket.Header.CP_END_DROPPOOL)) {
                return ReqCDropPool.OnPacket(c, header, cp);
            }
            // reactor
            if (header.between(ClientPacket.Header.CP_BEGIN_REACTORPOOL, ClientPacket.Header.CP_END_REACTORPOOL)) {
                return ReqCReactorPool.OnPacket(c, header, cp);
            }
            // event field
            if (header.between(ClientPacket.Header.CP_BEGIN_EVENT_FIELD, ClientPacket.Header.CP_END_EVENT_FIELD)) {
                if (ReqCField_SnowBall.OnPacket(c, header, cp)) {
                    return true;
                }
                if (ReqCField_Coconut.OnPacket(c, header, cp)) {
                    return true;
                }
                return false;
            }
            // monster carnival field
            if (header.between(ClientPacket.Header.CP_BEGIN_MONSTER_CARNIVAL_FIELD, ClientPacket.Header.CP_END_MONSTER_CARNIVAL_FIELD)) {
                return ReqCField_MonsterCarnival.OnPacket(c, header, cp);
            }
            if (header.between(ClientPacket.Header.CP_BEGIN_PARTY_MATCH, ClientPacket.Header.CP_END_PARTY_MATCH)) {
                return true;
            }
            return ReqCField.OnPacket(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_RAISE, ClientPacket.Header.CP_END_RAISE)) {
            // 布製の人形などETCアイテムからUIを開くタイプの処理
            return true;
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_ITEMUPGRADE, ClientPacket.Header.CP_END_ITEMUPGRADE)) {
            ReqCUIItemUpgrade.Accept(c, cp);
            return true;
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_BATTLERECORD, ClientPacket.Header.CP_END_BATTLERECORD)) {
            return true;
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_MAPLETV, ClientPacket.Header.CP_END_MAPLETV)) {
            return Req_MapleTV.OnPacket(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_CHARACTERSALE, ClientPacket.Header.CP_END_CHARACTERSALE)) {
            return true;
        }

        return false;
    }

}
