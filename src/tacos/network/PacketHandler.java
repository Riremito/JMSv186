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
package tacos.network;

import odin.client.MapleClient;
import tacos.config.Content;
import tacos.config.Region;
import tacos.debug.DebugLogger;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import tacos.packet.ClientPacket;
import tacos.packet.response.ResCClientSocket;
import odin.server.Randomizer;
import odin.tools.FileoutputUtil;
import tacos.packet.ClientPacketHeader;

/**
 *
 * @author Riremito
 */
public class PacketHandler extends IoHandlerAdapter {

    private static SocketAcceptorConfig cfg = null;
    private static MapleCodecFactory mcf = null;
    private static ProtocolCodecFilter pcf = null;
    private static ProtocolEncoder encoder = null;
    private static ProtocolDecoder decoder = null;

    public static SocketAcceptorConfig getSocketAcceptorConfig() {
        if (cfg != null) {
            return cfg;
        }

        switch (Region.getRegion()) {
            case KMSB: {
                encoder = new PacketEncoder_KMSB();
                decoder = new PacketDecoder_KMSB();
                break;
            }
            case KMS:
            case KMST:
            case IMS: {
                encoder = new PacketEncoder_KMS();
                decoder = new PacketDecoder_KMS();
                break;
            }
            default: {
                encoder = new PacketEncoder();
                decoder = new PacketDecoder();
                break;
            }
        }

        mcf = new MapleCodecFactory(encoder, decoder);
        pcf = new ProtocolCodecFilter(mcf);
        cfg = new SocketAcceptorConfig();
        cfg.getSessionConfig().setTcpNoDelay(true);
        cfg.setDisconnectOnUnbind(true);
        cfg.getFilterChain().addLast("codec", pcf);
        return cfg;
    }

    protected String server_name;
    protected int channel = -1;

    public PacketHandler(int channel) {
        this.channel = channel;
    }

    public PacketHandler() {
        this.channel = -1;
    }

    protected void log(IoSession session, String text) {
        String client_ip = session.getRemoteAddress().toString();
        DebugLogger.NetworkLog("[Server_" + this.server_name + "][" + client_ip + "]" + " " + text);
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        log(session, "sessionCreated.");
        super.sessionCreated(session);
    }

    @Override
    public void sessionOpened(final IoSession session) throws Exception {
        log(session, "sessionOpened.");
        if (((IPacketHandler) this).isShutdown()) {
            session.close();
            return;
        }

        final byte serverRecv[] = new byte[]{70, 114, 122, (byte) Randomizer.nextInt(255)};
        final byte serverSend[] = new byte[]{82, 48, 120, (byte) Randomizer.nextInt(255)};

        MapleAESOFB aes_enc = new MapleAESOFB(serverSend, true, true);
        MapleAESOFB aes_dec = new MapleAESOFB(serverRecv, true, false);
        final MapleClient client = new MapleClient(session);
        client.setChannel(this.channel);

        session.setAttribute(MapleAESOFB.AES_ENC_KEY, null);
        session.write(ResCClientSocket.getHello(serverSend, serverRecv)); // send raw packet before server starts packet encryption.
        session.setAttribute(MapleClient.CLIENT_KEY, client);
        session.setAttribute(MapleAESOFB.AES_ENC_KEY, aes_enc);
        session.setAttribute(MapleAESOFB.AES_DEC_KEY, aes_dec);
        session.setIdleTime(IdleStatus.READER_IDLE, 10);
        //session.setIdleTime(IdleStatus.WRITER_IDLE, 5);
    }

    @Override
    public void sessionClosed(final IoSession session) throws Exception {
        log(session, "sessionClosed.");
        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

        if (client != null) {
            try {
                client.disconnect(true, false);
            } finally {
                session.close();
                session.removeAttribute(MapleClient.CLIENT_KEY);
            }
        }
        super.sessionClosed(session);
    }

    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status) throws Exception {
        log(session, "sessionIdle.");
        final MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);

        if (client != null) {
            client.sendPing();
            client.SendPacket(ResCClientSocket.AliveReq());
        }

        super.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log(session, "exceptionCaught.");
        super.exceptionCaught(session, cause);
    }

    @Override
    public void messageReceived(final IoSession session, final Object message) {
        log(session, "messageReceived.");
        try {
            ClientPacket cp = new ClientPacket((byte[]) message);
            if (cp.getSize() < Content.PacketHeaderSize.getInt()) {
                return;
            }
            // client
            MapleClient c = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
            if (c.isMigrating()) {
                log(session, "messageReceived : Migrating.");
                return;
            }

            short header_val = 0;
            if (Content.PacketHeaderSize.getInt() == 2) {
                header_val = cp.Decode2();
            } else {
                header_val = (short) (cp.Decode1() & 0xFF);
            }

            ClientPacketHeader header = ClientPacket.ToHeader(header_val);

            // not coded
            if (header == ClientPacketHeader.UNKNOWN) {
                DebugLogger.CPLog(cp);
                return;
            }

            if (!((IPacketHandler) this).OnPacket(c, header, cp)) {
                DebugLogger.CPLog(cp);
            }
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
            e.printStackTrace();
        }
    }

    @Override
    public void messageSent(final IoSession session, final Object message) throws Exception {
        log(session, "messageSent.");
        //log(session, (new ClientPacket(((MaplePacket) message).getBytes())).get()); // server packet
        super.messageSent(session, message);
    }
}
