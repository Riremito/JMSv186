/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tools;

import java.awt.Point;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import client.BuddylistEntry;
import client.inventory.IItem;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MapleInventoryType;
import client.MapleKeyLayout;
import client.MapleStat;
import client.inventory.IEquip.ScrollResult;
import client.MapleDisease;
import client.inventory.MapleRing;
import client.SkillMacro;
import handling.MaplePacket;
import constants.ServerConstants;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildCharacter;
import handling.channel.MapleGuildRanking.GuildRankingInfo;
import handling.world.guild.MapleBBSThread;
import handling.world.guild.MapleGuildAlliance;
import java.net.UnknownHostException;
import server.MapleStatEffect;
import server.MapleTrade;
import server.life.MapleNPC;
import server.life.PlayerNPC;
import server.maps.MapleMap;
import server.maps.MapleReactor;
import server.maps.MapleMist;
import server.events.MapleSnowball.MapleSnowballs;
import server.life.MapleMonster;
import server.maps.MapleDragon;
import server.movement.LifeMovementFragment;
import server.shops.HiredMerchant;
import tools.data.output.LittleEndianWriter;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PacketHelper;
import client.MapleBeans;
import config.ServerConfig;
import constants.GameConstants;
import handling.channel.handler.AttackInfo;
import handling.channel.handler.BeanGame;
import handling.channel.handler.InventoryHandler;
import handling.world.World;
import handling.world.guild.MapleBBSThread.MapleBBSReply;
import java.util.ArrayList;
import java.util.Comparator;
import packet.ServerPacket;
import packet.content.ContextPacket;
import packet.content.ContextPacket.DropPickUpMessageType;
import packet.struct.CClientOptMan;
import packet.struct.CWvsContext;
import packet.struct.CharacterData;
import server.maps.MapleNodes.MapleNodeInfo;
import server.maps.MapleNodes.MaplePlatform;
import server.shops.MaplePlayerShopItem;

public class MaplePacketCreator {

    public final static List<Pair<MapleStat, Integer>> EMPTY_STATUPDATE = Collections.emptyList();

    // ゲームサーバーへ接続
    public static final MaplePacket getServerIP(final int port, final int clientId) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_SelectCharacterResult);
        p.Encode1(0);
        p.Encode1(0);
        // ゲームサーバーのIP
        try {
            p.EncodeBuffer(InetAddress.getByName(ServerConstants.Gateway_IP_String).getAddress()); // DWORD
        } catch (UnknownHostException e) {
            p.EncodeBuffer(ServerConstants.Gateway_IP); // DWORD
        }
        // ゲームサーバーのPort
        p.Encode2(port);
        // キャラクターID?
        p.Encode4(clientId);
        p.Encode1(0);
        p.Encode4(0);
        return p.Get();
    }

    // プレイヤー情報の初期化
    public static final MaplePacket getCharInfo(final MapleCharacter chr) {
        return SetField(chr, true, null, 0);
    }

    // マップ移動
    public static final MaplePacket getWarpToMap(final MapleMap to, final int spawnPoint, final MapleCharacter chr) {
        return SetField(chr, false, to, spawnPoint);
    }

    // CStage::OnSetField
    public static final MaplePacket SetField(MapleCharacter chr, boolean loggedin, MapleMap to, int spawnPoint) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_SetField);
        if ((ServerConfig.IsJMS() && 184 <= ServerConfig.GetVersion())
                || ServerConfig.IsCMS()) {
            p.EncodeBuffer(CClientOptMan.EncodeOpt());
        }
        // チャンネル
        p.Encode4(chr.getClient().getChannel() - 1);

        if (ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion()) {
            p.Encode1(0);
        }

        if ((ServerConfig.IsJMS() && 180 <= ServerConfig.GetVersion())
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            p.Encode4(0);
        }

        p.Encode1(chr.getPortalCount());

        if (ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion()) {
            p.Encode4(0);
        }

        if (ServerConfig.IsCMS()) {
            p.Encode1(0);
        }

        p.Encode1(loggedin ? 1 : 0); // 1 = all data, 0 = map change
        if ((ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion())
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            p.Encode2(0);
        }

        if (loggedin) {
            // [chr.CRand().connectData(mplew);]
            {
                p.Encode4(0);
                p.Encode4(0);
                p.Encode4(0);
            }
            // キャラクター情報
            p.EncodeBuffer(CharacterData.Encode(chr));

            if ((ServerConfig.IsJMS() && 184 <= ServerConfig.GetVersion())
                    || ServerConfig.IsTWMS()
                    || ServerConfig.IsCMS()) {
                // ログアウトギフト
                p.EncodeBuffer(CWvsContext.LogoutGiftConfig());
            }
        } else {
            if ((ServerConfig.IsJMS() && 180 <= ServerConfig.GetVersion())
                    || ServerConfig.IsTWMS()
                    || ServerConfig.IsCMS()) {
                p.Encode1(0);
            }

            p.Encode4(to.getId());
            p.Encode1(spawnPoint);

            if (ServerConfig.IsPreBB()) {
                p.Encode2(chr.getStat().getHp());
            } else {
                p.Encode4(chr.getStat().getHp());
            }
        }

        // サーバーの時間?
        p.Encode8(PacketHelper.getTime(System.currentTimeMillis()));

        if (ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion()) {
            p.Encode4(0);
            p.Encode4(0);
        }

        return p.Get();
    }

    public static final MaplePacket enableActions() {
        return ContextPacket.StatChanged(null, 1, 0);
    }

    public static final MaplePacket instantMapWarp(final byte portal) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserTeleport.Get());
        mplew.write(0);
        mplew.write(portal); // 6

        return mplew.getPacket();
    }

    public static final MaplePacket spawnPortal(final int townId, final int targetId, final int skillId, final Point pos) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_TownPortal.Get());
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        mplew.writeInt(skillId);
        if (pos != null) {
            mplew.writePos(pos);
        }

        return mplew.getPacket();
    }

    public static final MaplePacket spawnDoor(final int oid, final Point pos, final boolean town) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_TownPortalCreated.Get());
        mplew.write(town ? 1 : 0);
        mplew.writeInt(oid);
        mplew.writePos(pos);

        return mplew.getPacket();
    }

    public static MaplePacket removeDoor(int oid, boolean town) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (town) {
            mplew.writeShort(ServerPacket.Header.LP_TownPortal.Get());
            mplew.writeInt(999999999);
            mplew.writeLong(999999999);
        } else {
            mplew.writeShort(ServerPacket.Header.LP_TownPortalRemoved.Get());
            mplew.write(/*town ? 1 : */0);
            mplew.writeLong(oid);
        }

        return mplew.getPacket();
    }

    public static MaplePacket getRelogResponse() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        mplew.writeShort(ServerPacket.Header.RELOG_RESPONSE.Get());
        mplew.write(1);

        return mplew.getPacket();
    }

    /**
     * Possible values for <code>type</code>:<br>
     * 1: You cannot move that channel. Please try again later.<br>
     * 2: You cannot go into the cash shop. Please try again later.<br>
     * 3: The Item-Trading shop is currently unavailable, please try again
     * later.<br>
     * 4: You cannot go into the trade shop, due to the limitation of user
     * count.<br>
     * 5: You do not meet the minimum level requirement to access the Trade
     * Shop.<br>
     *
     * @param type The type
     * @return The "block" packet.
     */
    public static MaplePacket serverBlocked(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_TransferChannelReqIgnored.Get());
        mplew.write(type);

        return mplew.getPacket();
    }

    public static MaplePacket serverMessage(String message) {
        return serverMessage(4, 0, message, false);
    }

    public static MaplePacket serverNotice(int type, String message) {
        return serverMessage(type, 0, message, false);
    }

    public static MaplePacket serverNotice(int type, int channel, String message) {
        return serverMessage(type, channel, message, false);
    }

    public static MaplePacket serverNotice(int type, int channel, String message, boolean smegaEar) {
        return serverMessage(type, channel, message, smegaEar);
    }

    private static MaplePacket serverMessage(int type, int channel, String message, boolean megaEar) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        /*
        0x00    [告知事項]青文字
        0x01    ダイアログ
        0x02    メガホン
        0x03    拡声器
        0x04    画面上部のメッセージ
        0x05    ピンク文字
        0x06    青文字
        0x07    ??? 直接関数ポインタへ処理が移る 0x00B93F3F[0x07] = 00B93E27
        0x08    アイテム拡声器
        0x09    ワールド拡声器 (テスト)
        0x0A    三連拡声器
        0x0B    不明 0x00B93F3F[0x0B] = 00B93ECA
        0x0C    ハート拡声器
        0x0D    ドクロ拡声器
        0x0E    ガシャポン 0x00B93F3F[0x0E] = 00B93779
        0x0F    青文字 名前:アイテム名(xxxx個))
        0x10    体験用アバター獲得 0x00B93F3F[0x10] = 00B93950
        0x11    青文字 アイテム表示 0x00B93F3F[0x11] = 00B93DA1
         */
        mplew.writeShort(ServerPacket.Header.LP_BroadcastMsg.Get());
        mplew.write(type);

        if (type == 4) {
            mplew.write(1);
        }

        // 0x10 = 名前
        mplew.writeMapleAsciiString(message);

        switch (type) {
            case 0x0A:
                mplew.write(0x03);
                mplew.writeMapleAsciiString(message);
                mplew.writeMapleAsciiString(message);
                mplew.write(channel - 1);
                mplew.write(megaEar ? 1 : 0);
                break;
            // 拡声器, ハート拡声器, ドクロ拡声器
            case 0x03:
            case 0x0C:
            case 0x0D:
                mplew.write(channel - 1); // channel
                mplew.write(megaEar ? 1 : 0);
                break;
            case 0x06:
                mplew.writeInt(channel >= 1000000 && channel < 6000000 ? channel : 0); //cash itemID, displayed in yellow by the {name}
                break;
            case 0x07:
                mplew.writeInt(0);
                break;
            // ワールド拡声器
            case 0x09:
                // ワールド番号
                mplew.write(0);
                break;
            // 不明
            case 0x0B:
                mplew.writeInt(0); // 不明
                break;
            // アイテム情報 個数付き
            case 0x0F:
                mplew.writeInt(0); // 不明
                mplew.writeInt(1472117); // アイテムID
                mplew.writeInt(256); // 個数
                break;
            // お勧め体験用アバター
            case 0x10:
                // 必要なメッセージはキャラ名のみとなる
                break;
            // アイテム情報
            case 0x11:
                mplew.writeInt(1472117); // アイテムID
                break;
        }
        return mplew.getPacket();
    }

    public static MaplePacket getGachaponMega(final String name, final String message, final IItem item, final byte rareness) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_BroadcastMsg.Get());
        //mplew.write(rareness == 2 ? 15 : 14);
        mplew.write(0x0E);
        mplew.writeMapleAsciiString(name + " : " + message);
        mplew.writeInt(0x01010000);
        //mplew.writeMapleAsciiString(name);
        PacketHelper.addItemInfo(mplew, item, true, true);
        mplew.writeZeroBytes(10);
        return mplew.getPacket();
    }

    public static MaplePacket getAvatarMega(MapleCharacter chr, int channel, int itemId, String message, boolean ear) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AvatarMegaphoneRes.Get());
        mplew.writeInt(itemId);
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeMapleAsciiString(message);
        mplew.writeInt(channel - 1); // channel
        mplew.write(ear ? 1 : 0);
        PacketHelper.addCharLook(mplew, chr, true);

        return mplew.getPacket();
    }

    public static MaplePacket spawnNPCRequestController(MapleNPC life, boolean MiniMap) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_NpcChangeController.Get());
        mplew.write(1);
        mplew.writeInt(life.getObjectId());
        // フォーマット不明
        mplew.writeInt(life.getId());
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getCy());
        mplew.write(life.getF() == 1 ? 0 : 1);
        mplew.writeShort(life.getFh());
        mplew.writeShort(life.getRx0());
        mplew.writeShort(life.getRx1());
        mplew.write(MiniMap ? 1 : 0);

        return mplew.getPacket();
    }

    public static MaplePacket spawnPlayerNPC(PlayerNPC npc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ImitatedNPCData.Get());
        mplew.write(npc.getF() == 1 ? 0 : 1);
        mplew.writeInt(npc.getId());
        mplew.writeMapleAsciiString(npc.getName());
        mplew.write(npc.getGender());
        mplew.write(npc.getSkin());
        mplew.writeInt(npc.getFace());
        mplew.write(0);
        mplew.writeInt(npc.getHair());
        Map<Byte, Integer> equip = npc.getEquips();
        Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        for (Entry<Byte, Integer> position : equip.entrySet()) {
            byte pos = (byte) (position.getKey() * -1);
            if (pos < 100 && myEquip.get(pos) == null) {
                myEquip.put(pos, position.getValue());
            } else if ((pos > 100 || pos == -128) && pos != 111) { // don't ask. o.o
                pos = (byte) (pos == -128 ? 28 : pos - 100);
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, position.getValue());
            } else if (myEquip.get(pos) != null) {
                maskedEquip.put(pos, position.getValue());
            }
        }
        for (Entry<Byte, Integer> entry : myEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(0xFF);
        for (Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(0xFF);
        Integer cWeapon = equip.get((byte) -111);
        if (cWeapon != null) {
            mplew.writeInt(cWeapon);
        } else {
            mplew.writeInt(0);
        }
        for (int i = 0; i < 3; i++) {
            mplew.writeInt(npc.getPet(i));
        }

        return mplew.getPacket();
    }

    public static MaplePacket getChatText(int cidfrom, String text, boolean whiteBG, int show) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserChat.Get());
        mplew.writeInt(cidfrom);
        mplew.write(whiteBG ? 1 : 0);
        mplew.writeMapleAsciiString(text);

        if (ServerConfig.version > 131) {
            mplew.write(show);
        }

        return mplew.getPacket();
    }

    public static MaplePacket GameMaster_Func(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AdminResult.Get());
        mplew.write(value);
        mplew.writeZeroBytes(17);

        return mplew.getPacket();
    }

    public static MaplePacket testCombo(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_IncCombo.Get());
        mplew.writeInt(value);

        return mplew.getPacket();
    }

    public static MaplePacket getShowItemGain(int itemId, short quantity, boolean inChat) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(3);
        mplew.write(1); // item count
        mplew.writeInt(itemId);
        mplew.writeInt(quantity);

        return mplew.getPacket();
    }

    public static MaplePacket showRewardItemAnimation(int itemId, String effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(0x0F);
        mplew.writeInt(itemId);
        mplew.write(effect != null && effect.length() > 0 ? 1 : 0);
        if (effect != null && effect.length() > 0) {
            mplew.writeMapleAsciiString(effect);
        }

        return mplew.getPacket();
    }

    public static MaplePacket showRewardItemAnimation(int itemId, String effect, int from_playerid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.Get());
        mplew.writeInt(from_playerid);
        mplew.write(0x0F);
        mplew.writeInt(itemId);
        mplew.write(effect != null && effect.length() > 0 ? 1 : 0);
        if (effect != null && effect.length() > 0) {
            mplew.writeMapleAsciiString(effect);
        }

        return mplew.getPacket();
    }

    public static MaplePacket facialExpression(MapleCharacter from, int expression) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEmotion.Get());
        mplew.writeInt(from.getId());
        mplew.writeInt(expression);
        mplew.writeInt(-1); //itemid of expression use
        mplew.write(0);

        return mplew.getPacket();
    }

    // CLifePool::OnUserAttack
    public static MaplePacket UserAttack(AttackInfo attack) {
        ServerPacket p = new ServerPacket(attack.GetHeader());

        p.Encode4(attack.CharacterId);
        p.Encode1(attack.HitKey);

        if (ServerConfig.version > 131) {
            p.Encode1(attack.m_nLevel);
        }

        p.Encode1(attack.SkillLevel); // nPassiveSLV
        if (0 < attack.nSkillID) {
            p.Encode4(attack.nSkillID); // nSkillID
        }

        if (ServerConfig.version > 131) {
            p.Encode1(attack.BuffKey); // bSerialAttack
        }

        if (ServerConfig.version <= 131) {
            p.Encode1(attack.AttackActionKey);
        } else {
            p.Encode2(attack.AttackActionKey);
        }

        p.Encode1(attack.nAttackSpeed); // nActionSpeed
        p.Encode1(attack.nMastery); // nMastery
        p.Encode4(attack.nBulletItemID); // nBulletItemID

        for (AttackPair oned : attack.allDamage) {
            if (oned.attack != null) {
                p.Encode4(oned.objectid);
                p.Encode1(0x07);

                if (attack.IsMesoExplosion()) {
                    p.Encode1(oned.attack.size());
                }

                for (Pair<Integer, Boolean> eachd : oned.attack) {
                    if (ServerConfig.version <= 131) {
                        p.Encode4(eachd.left.intValue() | ((eachd.right ? 1 : 0) << 31));
                    } else {
                        p.Encode1(eachd.right ? 1 : 0);
                        p.Encode4(eachd.left.intValue());
                    }
                }
            }
        }

        if (attack.IsQuantumExplosion()) {
            p.Encode4(attack.tKeyDown);
        }

        if (131 < ServerConfig.version) {
            if (attack.GetHeader() == ServerPacket.Header.LP_UserShootAttack) {
                p.Encode2(attack.X);
                p.Encode2(attack.Y);
            }
        }

        return p.Get();
    }

    public static MaplePacket updateInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(fromDrop ? 1 : 0);
//	mplew.write((slot2 > 0 ? 1 : 0) + 1);
        mplew.write(1);
        mplew.write(1);
        mplew.write(type.getType()); // iv type
        mplew.writeShort(item.getPosition()); // slot id
        mplew.writeShort(item.getQuantity());
        /*	if (slot2 > 0) {
        mplew.write(1);
        mplew.write(type.getType());
        mplew.writeShort(slot2);
        mplew.writeShort(amt2);
        }*/
        return mplew.getPacket();
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, short src, short dst) {
        return moveInventoryItem(type, src, dst, (byte) -1);
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, short src, short dst, short equipIndicator) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 02"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(dst);
        if (equipIndicator != -1) {
            mplew.write(equipIndicator);
        }
        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeInventoryItem(MapleInventoryType type, short src, short dst, short total) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 03"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.write(1); // merge mode?
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(total);

        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeWithRestInventoryItem(MapleInventoryType type, short src, short dst, short srcQ, short dstQ) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 01"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(srcQ);
        mplew.write(HexTool.getByteArrayFromHexString("01"));
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(dstQ);

        return mplew.getPacket();
    }

    public static MaplePacket clearInventoryItem(MapleInventoryType type, short slot, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(fromDrop ? 1 : 0);
        mplew.write(HexTool.getByteArrayFromHexString("01 03"));
        mplew.write(type.getType());
        mplew.writeShort(slot);

        return mplew.getPacket();
    }

    public static MaplePacket updateSpecialItemUse(IItem item, byte invType) {
        return updateSpecialItemUse(item, invType, item.getPosition());
    }

    public static MaplePacket updateSpecialItemUse(IItem item, byte invType, short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(0); // could be from drop
        mplew.write(2); // always 2
        mplew.write(3); // quantity > 0 (?)
        mplew.write(invType); // Inventory type
        mplew.writeShort(pos); // item slot
        mplew.write(0);
        mplew.write(invType);
        if (item.getType() == 1) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
        PacketHelper.addItemInfo(mplew, item, true, true);
        if (item.getPosition() < 0) {
            mplew.write(2); //?
        }

        return mplew.getPacket();
    }

    public static MaplePacket updateSpecialItemUse_(IItem item, byte invType) {
        return updateSpecialItemUse_(item, invType, item.getPosition());
    }

    public static MaplePacket updateSpecialItemUse_(IItem item, byte invType, short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(0); // could be from drop
        mplew.write(1); // always 2
        mplew.write(0); // quantity > 0 (?)
        mplew.write(invType); // Inventory type
        if (item.getType() == 1) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
        PacketHelper.addItemInfo(mplew, item, true, true);
        if (item.getPosition() < 0) {
            mplew.write(1); //?
        }

        return mplew.getPacket();
    }

    public static MaplePacket getScrollEffect(int chr, ScrollResult scrollSuccess, boolean legendarySpirit) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserItemUpgradeEffect.Get());
        mplew.writeInt(chr);

        switch (scrollSuccess) {
            case SUCCESS:
                mplew.writeShort(1);
                mplew.writeShort(legendarySpirit ? 1 : 0);
                break;
            case FAIL:
                mplew.writeShort(0);
                mplew.writeShort(legendarySpirit ? 1 : 0);
                break;
            case CURSE:
                mplew.write(0);
                mplew.write(1);
                mplew.writeShort(legendarySpirit ? 1 : 0);
                break;
        }
        mplew.write(0); //? pam's song?

        // テスト
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    //miracle cube?
    public static MaplePacket getPotentialEffect(final int chr, final int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserItemUnreleaseEffect.Get());
        mplew.writeInt(chr);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }

    //magnify glass
    public static MaplePacket getPotentialReset(final int chr, final short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserItemReleaseEffect.Get());
        mplew.writeInt(chr);
        mplew.writeShort(pos);
        return mplew.getPacket();
    }

    public static final MaplePacket ItemMakerResult(boolean is_success) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(0x11);
        mplew.writeInt(is_success ? 0 : 1);
        return mplew.getPacket();
    }

    public static final MaplePacket ItemMakerResultTo(MapleCharacter chr, boolean is_success) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.Get());
        mplew.writeInt(chr.getId());
        mplew.write(0x11);
        mplew.writeInt(is_success ? 0 : 1);
        return mplew.getPacket();
    }

    public static MaplePacket updateCharLook(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserAvatarModified.Get());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        PacketHelper.addCharLook(mplew, chr, false);
        Pair<List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        addRingInfo(mplew, rings.getLeft());
        addRingInfo(mplew, rings.getRight());
        mplew.writeZeroBytes(5); //probably marriage ring (1) -> charid to follow (4)
        return mplew.getPacket();
    }

    public static void addRingInfo(MaplePacketLittleEndianWriter mplew, List<MapleRing> rings) {
        mplew.write(rings.size());
        for (MapleRing ring : rings) {
            mplew.writeInt(1);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
            mplew.writeInt(ring.getItemId());
        }
    }

    public static MaplePacket dropInventoryItem(MapleInventoryType type, short src) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 03"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        if (src < 0) {
            mplew.write(1);
        }
        return mplew.getPacket();
    }

    public static MaplePacket dropInventoryItemUpdate(MapleInventoryType type, IItem item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01"));
        mplew.write(type.getType());
        mplew.writeShort(item.getPosition());
        mplew.writeShort(item.getQuantity());

        return mplew.getPacket();
    }

    public static MaplePacket damagePlayer(int skill, int monsteridfrom, int cid, int damage, int fake, byte direction, int reflect, boolean is_pg, int oid, int pos_x, int pos_y) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserHit.Get());
        mplew.writeInt(cid);
        mplew.write(skill);
        mplew.writeInt(damage);
        mplew.writeInt(monsteridfrom);
        mplew.write(direction);

        if (reflect > 0) {
            mplew.write(reflect);
            mplew.write(is_pg ? 1 : 0);
            mplew.writeInt(oid);
            mplew.write(6);
            mplew.writeShort(pos_x);
            mplew.writeShort(pos_y);
            mplew.write(0);
        } else {
            mplew.writeShort(0);
        }
        mplew.writeInt(damage);
        if (fake > 0) {
            mplew.writeInt(fake);
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateQuestInfo(MapleCharacter c, int quest, int npc, byte progress) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserQuestResult.Get());
        mplew.write(progress);
        mplew.writeShort(quest);
        mplew.writeInt(npc);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static MaplePacket updateQuestFinish(int quest, int npc, int nextquest) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserQuestResult.Get());
        mplew.write(8);
        mplew.writeShort(quest);
        mplew.writeInt(npc);
        mplew.writeInt(nextquest);
        return mplew.getPacket();
    }

    private static void writeLongMask(MaplePacketLittleEndianWriter mplew, List<Pair<MapleBuffStat, Integer>> statups) {
        long firstmask = 0;
        long secondmask = 0;
        for (Pair<MapleBuffStat, Integer> statup : statups) {
            if (statup.getLeft().isFirst()) {
                firstmask |= statup.getLeft().getValue();
            } else {
                secondmask |= statup.getLeft().getValue();
            }
        }

        if (194 <= ServerConfig.version) {
            mplew.writeZeroBytes(4);
        }

        if (ServerConfig.version > 131) {
            mplew.writeLong(firstmask);
        }

        mplew.writeLong(secondmask);
    }

    // List<Pair<MapleDisease, Integer>>
    private static void writeLongDiseaseMask(MaplePacketLittleEndianWriter mplew, List<Pair<MapleDisease, Integer>> statups) {
        long firstmask = 0;
        long secondmask = 0;
        for (Pair<MapleDisease, Integer> statup : statups) {
            if (statup.getLeft().isFirst()) {
                firstmask |= statup.getLeft().getValue();
            } else {
                secondmask |= statup.getLeft().getValue();
            }
        }

        if (194 <= ServerConfig.version) {
            mplew.writeZeroBytes(4);
        }

        if (ServerConfig.version > 131) {
            mplew.writeLong(firstmask);
        }

        mplew.writeLong(secondmask);
    }

    private static void writeLongMaskFromList(MaplePacketLittleEndianWriter mplew, List<MapleBuffStat> statups) {
        long firstmask = 0;
        long secondmask = 0;
        for (MapleBuffStat statup : statups) {
            if (statup.isFirst()) {
                firstmask |= statup.getValue();
            } else {
                secondmask |= statup.getValue();
            }
        }

        if (194 <= ServerConfig.version) {
            mplew.writeZeroBytes(4);
        }

        if (ServerConfig.version > 131) {
            mplew.writeLong(firstmask);
        }

        mplew.writeLong(secondmask);
    }

    public static MaplePacket giveMount(int buffid, int skillid, List<Pair<MapleBuffStat, Integer>> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatSet.Get());

        writeLongMask(mplew, statups);

        mplew.writeShort(0);
        mplew.writeInt(buffid); // 1902000 saddle
        mplew.writeInt(skillid); // skillid
        mplew.writeInt(0); // Server tick value
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(2); // Total buffed times

        return mplew.getPacket();
    }

    public static MaplePacket givePirate(List<Pair<MapleBuffStat, Integer>> statups, int duration, int skillid) {
        final boolean infusion = skillid == 5121009 || skillid == 15111005;
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatSet.Get());
        writeLongMask(mplew, statups);

        mplew.writeShort(0);
        for (Pair<MapleBuffStat, Integer> stat : statups) {
            mplew.writeInt(stat.getRight().intValue());
            mplew.writeLong(skillid);
            mplew.writeZeroBytes(infusion ? 6 : 1);
            mplew.writeShort(duration);
        }
        mplew.writeShort(infusion ? 600 : 0);
        if (!infusion) {
            mplew.write(1); //does this only come in dash?
        }
        return mplew.getPacket();
    }

    public static MaplePacket giveForeignPirate(List<Pair<MapleBuffStat, Integer>> statups, int duration, int cid, int skillid) {
        final boolean infusion = skillid == 5121009 || skillid == 15111005;
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatSet.Get());
        mplew.writeInt(cid);
        writeLongMask(mplew, statups);
        mplew.writeShort(0);
        for (Pair<MapleBuffStat, Integer> stat : statups) {
            mplew.writeInt(stat.getRight().intValue());
            mplew.writeLong(skillid);
            mplew.writeZeroBytes(infusion ? 7 : 1);
            mplew.writeShort(duration);//duration... seconds
        }
        mplew.writeShort(infusion ? 600 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket giveHoming(int skillid, int mobid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatSet.Get());

        if (194 <= ServerConfig.version) {
            mplew.writeZeroBytes(4);
        }

        mplew.writeLong(MapleBuffStat.HOMING_BEACON.getValue());
        mplew.writeLong(0);

        mplew.writeShort(0);
        mplew.writeInt(1);
        mplew.writeLong(skillid);
        mplew.write(0);
        mplew.writeInt(mobid);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static MaplePacket giveEnergyChargeTest(int bar, int bufflength) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatSet.Get());
        mplew.writeLong(MapleBuffStat.ENERGY_CHARGE.getValue());
        mplew.writeLong(0);
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.writeInt(1555445060); //?
        mplew.writeShort(0);
        mplew.writeInt(Math.min(bar, 10000)); // 0 = no bar, 10000 = full bar
        mplew.writeLong(0); //skillid, but its 0 here
        mplew.write(0);
        mplew.writeInt(bar >= 10000 ? bufflength : 0);//short - bufflength...50
        return mplew.getPacket();
    }

    public static MaplePacket giveEnergyChargeTest(int cid, int bar, int bufflength) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatSet.Get());
        mplew.writeInt(cid);
        mplew.writeLong(MapleBuffStat.ENERGY_CHARGE.getValue());
        mplew.writeLong(0);
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.writeInt(1555445060); //?
        mplew.writeShort(0);
        mplew.writeInt(Math.min(bar, 10000)); // 0 = no bar, 10000 = full bar
        mplew.writeLong(0); //skillid, but its 0 here
        mplew.write(0);
        mplew.writeInt(bar >= 10000 ? bufflength : 0);//short - bufflength...50
        return mplew.getPacket();
    }

    public static MaplePacket giveBuff(int buffid, int bufflength, List<Pair<MapleBuffStat, Integer>> statups, MapleStatEffect effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatSet.Get());
        // 17 00 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 07 00 AE E1 3E 00 68 B9 01 00 00 00 00 00

        //lhc patch adds an extra int here
        writeLongMask(mplew, statups);

        for (Pair<MapleBuffStat, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
            mplew.writeInt(buffid);
            if (ServerConfig.version <= 131) {
                mplew.writeShort(bufflength);
            } else {
                mplew.writeInt(bufflength);
                if (buffid == 4331003) {
                    mplew.writeZeroBytes(10);
                }
            }
        }
        mplew.writeShort(0); // delay,  wk charges have 600 here o.o
        mplew.writeShort(0); // combo 600, too
        if (effect == null || (!effect.isCombo() && !effect.isFinalAttack())) {
            mplew.write(0); // Test
        }

        return mplew.getPacket();
    }

    public static MaplePacket giveDebuff(final List<Pair<MapleDisease, Integer>> statups, int skillid, int level, int duration) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatSet.Get());

        writeLongDiseaseMask(mplew, statups);

        for (Pair<MapleDisease, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
            mplew.writeShort(skillid);
            mplew.writeShort(level);
            mplew.writeInt(duration);
        }
        mplew.writeShort(0); // ??? wk charges have 600 here o.o
        mplew.writeShort(900); //Delay
        mplew.write(1);

        return mplew.getPacket();
    }

    public static MaplePacket giveForeignDebuff(int cid, final List<Pair<MapleDisease, Integer>> statups, int skillid, int level) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatSet.Get());
        mplew.writeInt(cid);

        writeLongDiseaseMask(mplew, statups);

        if (skillid == 125) {
            mplew.writeShort(0);
        }
        mplew.writeShort(skillid);
        mplew.writeShort(level);
        mplew.writeShort(0); // same as give_buff
        mplew.writeShort(900); //Delay

        return mplew.getPacket();
    }

    public static MaplePacket cancelForeignDebuff(int cid, long mask, boolean first) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatReset.Get());
        mplew.writeInt(cid);
        mplew.writeLong(first ? mask : 0);
        mplew.writeLong(first ? 0 : mask);

        return mplew.getPacket();
    }

    public static MaplePacket showMonsterRiding(int cid, List<Pair<MapleBuffStat, Integer>> statups, int itemId, int skillId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatSet.Get());
        mplew.writeInt(cid);

        writeLongMask(mplew, statups);

        mplew.writeShort(0);
        mplew.writeInt(itemId);
        mplew.writeInt(skillId);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket giveForeignBuff(int cid, List<Pair<MapleBuffStat, Integer>> statups, MapleStatEffect effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatSet.Get());
        mplew.writeInt(cid);

        writeLongMask(mplew, statups);

        for (Pair<MapleBuffStat, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
        }
        mplew.writeShort(0); // same as give_buff
        if (effect.isMorph()) {
            mplew.write(0);
        }
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket cancelForeignBuff(int cid, List<MapleBuffStat> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatReset.Get());
        mplew.writeInt(cid);

        writeLongMaskFromList(mplew, statups);

        return mplew.getPacket();
    }

    public static MaplePacket cancelBuff(List<MapleBuffStat> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatReset.Get());

        if (statups != null) {
            writeLongMaskFromList(mplew, statups);
            // 上のフラグの有無で以下のバイトが必要になる
            mplew.write(3);
        } else {
            mplew.writeLong(0);
            mplew.writeInt(0x40);
            mplew.writeInt(0x1000);
        }

        return mplew.getPacket();
    }

    public static MaplePacket cancelHoming() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatReset.Get());

        if (194 <= ServerConfig.version) {
            mplew.writeZeroBytes(4);
        }

        mplew.writeLong(MapleBuffStat.HOMING_BEACON.getValue());
        mplew.writeLong(0);

        return mplew.getPacket();
    }

    public static MaplePacket cancelDebuff(long mask, boolean first) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatReset.Get());

        if (194 <= ServerConfig.version) {
            mplew.writeZeroBytes(4);
        }

        mplew.writeLong(first ? mask : 0);
        mplew.writeLong(first ? 0 : mask);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static MaplePacket updateMount(MapleCharacter chr, boolean levelup) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_SetTamingMobInfo.Get());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());
        mplew.write(levelup ? 1 : 0);

        return mplew.getPacket();
    }

    public static MaplePacket mountInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_SetTamingMobInfo.Get());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());

        return mplew.getPacket();
    }

    public static MaplePacket getPlayerShopNewVisitor(MapleCharacter c, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(HexTool.getByteArrayFromHexString("04 0" + slot));
        PacketHelper.addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeShort(c.getJob());

        return mplew.getPacket();
    }

    public static MaplePacket getPlayerShopRemoveVisitor(int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(HexTool.getByteArrayFromHexString("0A 0" + slot));

        return mplew.getPacket();
    }

    public static MaplePacket getTradePartnerAdd(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(4);
        mplew.write(1);
        PacketHelper.addCharLook(mplew, c, false);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeShort(c.getJob());

        return mplew.getPacket();
    }

    public static MaplePacket getTradeInvite(MapleCharacter c, boolean isPointTrade) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(2);
        mplew.write(isPointTrade ? 6 : 3);
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeInt(0); // Trade ID

        return mplew.getPacket();
    }

    public static MaplePacket getTradeMesoSet(byte number, int meso) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        //mplew.write(0xF);
        mplew.write(0xE);
        mplew.write(number);
        mplew.writeInt(meso);

        return mplew.getPacket();
    }

    public static MaplePacket getTradeItemAdd(byte number, IItem item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        //mplew.write(0xE);
        mplew.write(0xD);
        mplew.write(number);
        PacketHelper.addItemInfo(mplew, item, false, false, true);

        return mplew.getPacket();
    }

    public static MaplePacket getTradeStart(MapleClient c, MapleTrade trade, byte number, boolean isPointTrade) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(5);
        mplew.write(isPointTrade ? 6 : 3);
        mplew.write(2);
        mplew.write(number);

        if (number == 1) {
            mplew.write(0);
            PacketHelper.addCharLook(mplew, trade.getPartner().getChr(), false);
            mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
            mplew.writeShort(trade.getPartner().getChr().getJob());
        }
        mplew.write(number);
        PacketHelper.addCharLook(mplew, c.getPlayer(), false);
        mplew.writeMapleAsciiString(c.getPlayer().getName());
        mplew.writeShort(c.getPlayer().getJob());
        mplew.write(0xFF);

        return mplew.getPacket();
    }

    public static MaplePacket getTradeConfirmation() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        //mplew.write(0x10); //or 7? what
        mplew.write(0x0F);

        return mplew.getPacket();
    }

    public static MaplePacket TradeMessage(final byte UserSlot, final byte message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(0xA);
        mplew.write(UserSlot);
        mplew.write(message);
        //0x02 = cancelled
        //0x07 = success [tax is automated]
        //0x08 = unsuccessful
        //0x09 = "You cannot make the trade because there are some items which you cannot carry more than one."
        //0x0A = "You cannot make the trade because the other person's on a different map."

        return mplew.getPacket();
    }

    public static MaplePacket getTradeCancel(final byte UserSlot, final int unsuccessful) { //0 = canceled 1 = invent space 2 = pickuprestricted
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_MiniRoom.Get());
        mplew.write(0xA);
        mplew.write(UserSlot);
        mplew.write(unsuccessful == 0 ? 2 : (unsuccessful == 1 ? 8 : 9));

        return mplew.getPacket();
    }

    public static MaplePacket getNPCTalk(int npc, byte msgType, String talk, String endBytes, byte type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_ScriptMessage.Get());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.write(msgType);
        // v164に存在しない
        if (186 <= ServerConfig.GetVersion()) {
            mplew.write(type); // 1 = No ESC, 3 = show character + no sec
        }
        mplew.writeMapleAsciiString(talk);
        mplew.write(HexTool.getByteArrayFromHexString(endBytes));

        return mplew.getPacket();
    }

    public static final MaplePacket getMapSelection(final int npcid, final String sel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_ScriptMessage.Get());
        mplew.write(4);
        mplew.writeInt(npcid);
        mplew.writeShort(0xE);
        mplew.writeInt(0);
        mplew.writeInt(5);
        mplew.writeMapleAsciiString(sel);

        return mplew.getPacket();
    }

    public static MaplePacket getNPCTalkStyle(int npc, String talk, int... args) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_ScriptMessage.Get());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.writeShort(8);
        mplew.writeMapleAsciiString(talk);
        mplew.write(args.length);

        for (int i = 0; i < args.length; i++) {
            mplew.writeInt(args[i]);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getNPCTalkNum(int npc, String talk, int def, int min, int max) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_ScriptMessage.Get());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.writeShort(4);
        mplew.writeMapleAsciiString(talk);
        mplew.writeInt(def);
        mplew.writeInt(min);
        mplew.writeInt(max);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static MaplePacket getNPCTalkText(int npc, String talk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_ScriptMessage.Get());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.writeShort(3);
        mplew.writeMapleAsciiString(talk);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static MaplePacket showForeignEffect(int cid, int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.Get());
        mplew.writeInt(cid);
        mplew.write(effect); // 0 = Level up, 8 = job change

        return mplew.getPacket();
    }

    public static MaplePacket showBuffeffect(int cid, int skillid, int effectid) {
        return showBuffeffect(cid, skillid, effectid, (byte) 3);
    }

    public static MaplePacket showBuffeffect(int cid, int skillid, int effectid, byte direction) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.Get());
        mplew.writeInt(cid);
        mplew.write(effectid); //ehh?
        mplew.writeInt(skillid);
        mplew.write(1); //skill level = 1 for the lulz
        mplew.write(1); //actually skill level ? 0 = dosnt show
        if (direction != (byte) 3) {
            mplew.write(direction);
        }
        return mplew.getPacket();
    }

    public static MaplePacket showOwnBuffEffect(int skillid, int effectid) {
        return showOwnBuffEffect(skillid, effectid, (byte) 3);
    }

    public static MaplePacket showOwnBuffEffect(int skillid, int effectid, byte direction) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(effectid);
        mplew.writeInt(skillid);
        mplew.write(1); //skill level = 1 for the lulz
        mplew.write(1); //0 = doesnt show? or is this even here
        if (direction != (byte) 3) {
            mplew.write(direction);
        }

        return mplew.getPacket();
    }

    public static MaplePacket showItemLevelupEffect() {
        return showSpecialEffect(17);
    }

    public static MaplePacket showForeignItemLevelupEffect(int cid) {
        return showSpecialEffect(cid, 17);
    }

    public static MaplePacket showSpecialEffect(int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(effect);

        return mplew.getPacket();
    }

    public static MaplePacket showSpecialEffect(int cid, int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.Get());
        mplew.writeInt(cid);
        mplew.write(effect);

        return mplew.getPacket();
    }

    public static MaplePacket updateSkill(int skillid, int level, int masterlevel, long expiration) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_ChangeSkillRecordResult.Get());
        mplew.write(1);
        mplew.writeShort(1);
        mplew.writeInt(skillid);
        mplew.writeInt(level);
        mplew.writeInt(masterlevel);

        if (ServerConfig.version > 131) {
            PacketHelper.addExpirationTime(mplew, expiration);
        }
        mplew.write(4);

        return mplew.getPacket();
    }

    public static MaplePacket getShowQuestCompletion(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_QuestClear.Get());
        mplew.writeShort(id);

        return mplew.getPacket();
    }

    public static MaplePacket getKeymap(MapleKeyLayout layout) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_FuncKeyMappedInit.Get());
        mplew.write(0);

        layout.writeData(mplew);

        return mplew.getPacket();
    }

    public static MaplePacket getWhisper(String sender, int channel, String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Whisper.Get());
        mplew.write(0x12);
        mplew.writeMapleAsciiString(sender);
        mplew.writeShort(channel - 1);
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static MaplePacket getWhisperReply(String target, byte reply) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Whisper.Get());
        mplew.write(0x0A); // whisper?
        mplew.writeMapleAsciiString(target);
        mplew.write(reply);//  0x0 = cannot find char, 0x1 = success

        return mplew.getPacket();
    }

    public static MaplePacket getFindReplyWithMap(String target, int mapid, final boolean buddy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Whisper.Get());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(1);
        mplew.writeInt(mapid);
        mplew.writeZeroBytes(8); // ?? official doesn't send zeros here but whatever

        return mplew.getPacket();
    }

    public static MaplePacket getFindReply(String target, int channel, final boolean buddy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Whisper.Get());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(3);
        mplew.writeInt(channel - 1);

        return mplew.getPacket();
    }

    public static MaplePacket getInventoryFull() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
        mplew.write(1);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket fairyPendantMessage(int type, int percent) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.FAIRY_PEND_MSG.Get());
        mplew.writeShort(21); // 0x15
        mplew.writeInt(0); // idk
        mplew.writeShort(0); // idk
        mplew.writeShort(percent); // percent
        mplew.writeShort(0); // idk

        return mplew.getPacket();
    }

    public static MaplePacket giveFameResponse(int mode, String charname, int newfame) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GivePopularityResult.Get());
        mplew.write(0);
        mplew.writeMapleAsciiString(charname);
        mplew.write(mode);
        mplew.writeShort(newfame);
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static MaplePacket giveFameErrorResponse(int status) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        /*	* 0: ok, use giveFameResponse<br>
         * 1: the username is incorrectly entered<br>
         * 2: users under level 15 are unable to toggle with fame.<br>
         * 3: can't raise or drop fame anymore today.<br>
         * 4: can't raise or drop fame for this character for this month anymore.<br>
         * 5: received fame, use receiveFame()<br>
         * 6: level of fame neither has been raised nor dropped due to an unexpected error*/
        mplew.writeShort(ServerPacket.Header.LP_GivePopularityResult.Get());
        mplew.write(status);

        return mplew.getPacket();
    }

    public static MaplePacket receiveFame(int mode, String charnameFrom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GivePopularityResult.Get());
        mplew.write(5);
        mplew.writeMapleAsciiString(charnameFrom);
        mplew.write(mode);

        return mplew.getPacket();
    }

    public static MaplePacket partyCreated(int partyid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_PartyResult.Get());
        mplew.write(8);
        mplew.writeInt(partyid);
        mplew.writeInt(999999999);
        mplew.writeInt(999999999);
        mplew.writeLong(0);

        return mplew.getPacket();
    }

    public static MaplePacket partyInvite(MapleCharacter from) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_PartyResult.Get());
        mplew.write(4);
        mplew.writeInt(from.getParty().getId());
        mplew.writeMapleAsciiString(from.getName());
        mplew.writeInt(from.getLevel());
        mplew.writeInt(from.getJob());
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket partyStatusMessage(int message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        /*	* 10: A beginner can't create a party.
         * 1/11/14/19: Your request for a party didn't work due to an unexpected error.
         * 13: You have yet to join a party.
         * 16: Already have joined a party.
         * 17: The party you're trying to join is already in full capacity.
         * 19: Unable to find the requested character in this channel.*/
        mplew.writeShort(ServerPacket.Header.LP_PartyResult.Get());
        mplew.write(message);

        return mplew.getPacket();
    }

    public static MaplePacket partyStatusMessage(int message, String charname) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_PartyResult.Get());
        mplew.write(message); // 23: 'Char' have denied request to the party.
        mplew.writeMapleAsciiString(charname);

        return mplew.getPacket();
    }

    private static void addPartyStatus(int forchannel, MapleParty party, LittleEndianWriter lew, boolean leaving) {
        List<MaplePartyCharacter> partymembers = new ArrayList<MaplePartyCharacter>(party.getMembers());
        while (partymembers.size() < 6) {
            partymembers.add(new MaplePartyCharacter());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeAsciiString(partychar.getName(), 13);
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getJobId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getLevel());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.isOnline()) {
                lew.writeInt(partychar.getChannel() - 1);
            } else {
                lew.writeInt(-2);
            }
        }
        lew.writeInt(party.getLeader().getId());
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel) {
                lew.writeInt(partychar.getMapid());
            } else {
                lew.writeInt(0);
            }
        }
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel && !leaving) {
                lew.writeInt(partychar.getDoorTown());
                lew.writeInt(partychar.getDoorTarget());
                lew.writeInt(partychar.getDoorSkill());
                lew.writeInt(partychar.getDoorPosition().x);
                lew.writeInt(partychar.getDoorPosition().y);
            } else {
                lew.writeInt(leaving ? 999999999 : 0);
                lew.writeLong(leaving ? 999999999 : 0);
                lew.writeLong(leaving ? -1 : 0);
            }
        }
    }

    public static MaplePacket updateParty(int forChannel, MapleParty party, PartyOperation op, MaplePartyCharacter target) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_PartyResult.Get());
        switch (op) {
            case DISBAND:
            case EXPEL:
            case LEAVE:
                mplew.write(0xC);
                mplew.writeInt(party.getId());
                mplew.writeInt(target.getId());
                mplew.write(op == PartyOperation.DISBAND ? 0 : 1);
                if (op == PartyOperation.DISBAND) {
                    mplew.writeInt(target.getId());
                } else {
                    mplew.write(op == PartyOperation.EXPEL ? 1 : 0);
                    mplew.writeMapleAsciiString(target.getName());
                    addPartyStatus(forChannel, party, mplew, op == PartyOperation.LEAVE);
                }
                break;
            case JOIN:
                mplew.write(0xF);
                mplew.writeInt(party.getId());
                mplew.writeMapleAsciiString(target.getName());
                addPartyStatus(forChannel, party, mplew, false);
                break;
            case SILENT_UPDATE:
            case LOG_ONOFF:
                mplew.write(0x7);
                mplew.writeInt(party.getId());
                addPartyStatus(forChannel, party, mplew, op == PartyOperation.LOG_ONOFF);
                break;
            case CHANGE_LEADER:
            case CHANGE_LEADER_DC:
                mplew.write(0x1F); //test
                mplew.writeInt(target.getId());
                mplew.write(op == PartyOperation.CHANGE_LEADER_DC ? 1 : 0);
                break;
            //1D = expel function not available in this map.
        }
        return mplew.getPacket();
    }

    public static MaplePacket partyPortal(int townId, int targetId, int skillId, Point position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_PartyResult.Get());
        mplew.writeShort(0x28);
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        mplew.writeInt(skillId);
        mplew.writePos(position);

        return mplew.getPacket();
    }

    public static MaplePacket updatePartyMemberHP(int cid, int curhp, int maxhp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserHP.Get());
        mplew.writeInt(cid);
        mplew.writeInt(curhp);
        mplew.writeInt(maxhp);

        return mplew.getPacket();
    }

    public static MaplePacket multiChat(String name, String chattext, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GroupMessage.Get());
        mplew.write(mode); //  0 buddychat; 1 partychat; 2 guildchat
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(chattext);

        return mplew.getPacket();
    }

    public static MaplePacket getClock(int time) { // time in seconds
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Clock.Get());
        mplew.write(2); // clock type. if you send 3 here you have to send another byte (which does not matter at all) before the timestamp
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static MaplePacket getClockTime(int hour, int min, int sec) { // Current Time
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Clock.Get());
        mplew.write(1); //Clock-Type
        mplew.write(hour);
        mplew.write(min);
        mplew.write(sec);

        return mplew.getPacket();
    }

    public static MaplePacket spawnMist(final MapleMist mist) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AffectedAreaCreated.Get());
        mplew.writeInt(mist.getObjectId());
        mplew.writeInt(mist.isMobMist() ? 0 : (mist.isPoisonMist() != 0 ? 1 : 2)); //2 = invincible, so put 1 for recovery aura
        mplew.writeInt(mist.getOwnerId());
        if (mist.getMobSkill() == null) {
            mplew.writeInt(mist.getSourceSkill().getId());
        } else {
            mplew.writeInt(mist.getMobSkill().getSkillId());
        }
        mplew.write(mist.getSkillLevel());
        mplew.writeShort(mist.getSkillDelay());
        mplew.writeInt(mist.getBox().x);
        mplew.writeInt(mist.getBox().y);
        mplew.writeInt(mist.getBox().x + mist.getBox().width);
        mplew.writeInt(mist.getBox().y + mist.getBox().height);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static MaplePacket removeMist(final int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AffectedAreaRemoved.Get());
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static MaplePacket buddylistMessage(byte message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_FriendResult.Get());
        mplew.write(message);

        return mplew.getPacket();
    }

    public static MaplePacket updateBuddylist(Collection<BuddylistEntry> buddylist) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_FriendResult.Get());
        mplew.write(7);
        mplew.write(buddylist.size());

        for (BuddylistEntry buddy : buddylist) {
            if (buddy.isVisible()) {
                mplew.writeInt(buddy.getCharacterId());
                mplew.writeAsciiString(StringUtil.getRightPaddedStr(buddy.getName(), '\0', 13));
                mplew.write(0);
                mplew.writeInt(buddy.getChannel() == -1 ? -1 : buddy.getChannel() - 1);
                mplew.writeAsciiString(StringUtil.getRightPaddedStr(buddy.getGroup(), '\0', 17));
            }
        }
        for (int x = 0; x < buddylist.size(); x++) {
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }

    public static MaplePacket requestBuddylistAdd(int cidFrom, String nameFrom, int levelFrom, int jobFrom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_FriendResult.Get());
        mplew.write(9);
        mplew.writeInt(cidFrom);
        mplew.writeMapleAsciiString(nameFrom);
        mplew.writeInt(levelFrom);
        mplew.writeInt(jobFrom);
        mplew.writeInt(cidFrom);
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(nameFrom, '\0', 13));
        mplew.write(1);
        mplew.writeInt(0);
        mplew.writeAsciiString(StringUtil.getRightPaddedStr("ETC", '\0', 16));
        mplew.writeShort(1);

        return mplew.getPacket();
    }

    public static MaplePacket updateBuddyChannel(int characterid, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_FriendResult.Get());
        mplew.write(0x14);
        mplew.writeInt(characterid);
        mplew.write(0);
        mplew.writeInt(channel);

        return mplew.getPacket();
    }

    public static MaplePacket itemEffect(int characterid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserSetActiveEffectItem.Get());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static MaplePacket updateBuddyCapacity(int capacity) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_FriendResult.Get());
        mplew.write(0x15);
        mplew.write(capacity);

        return mplew.getPacket();
    }

    public static MaplePacket showChair(int characterid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserSetActivePortableChair.Get());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static MaplePacket cancelChair(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserSitResult.Get());
        if (id == -1) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeShort(id);
        }
        return mplew.getPacket();
    }

    public static MaplePacket destroyReactor(MapleReactor reactor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_ReactorLeaveField.Get());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getPosition());

        return mplew.getPacket();
    }

    public static MaplePacket musicChange(String song) {
        return environmentChange(song, 6);
    }

    public static MaplePacket showEffect(String effect) {
        return environmentChange(effect, 3);
    }

    public static MaplePacket playSound(String sound) {
        return environmentChange(sound, 4);
    }

    public static MaplePacket environmentChange(String env, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_FieldEffect.Get());
        mplew.write(mode);
        mplew.writeMapleAsciiString(env);

        return mplew.getPacket();
    }

    public static MaplePacket environmentMove(String env, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_FieldObstacleOnOff.Get());
        mplew.writeMapleAsciiString(env);
        mplew.writeInt(mode);

        return mplew.getPacket();
    }

    public static MaplePacket startMapEffect(String msg, int itemid, boolean active) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_BlowWeather.Get());
//        mplew.write(active ? 0 : 1);
        mplew.writeInt(itemid);
        if (active) {
            mplew.writeMapleAsciiString(msg);
        }
        return mplew.getPacket();
    }

    public static MaplePacket removeMapEffect() {
        return startMapEffect(null, 0, false);
    }

    public static MaplePacket showGuildInfo(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x1A); //signature for showing guild info

        if (c == null || c.getMGC() == null) { //show empty guild (used for leaving, expelled)
            mplew.write(0);
            return mplew.getPacket();
        }
        MapleGuild g = World.Guild.getGuild(c.getGuildId());
        if (g == null) { //failed to read from DB - don't show a guild
            mplew.write(0);
            return mplew.getPacket();
        }
        mplew.write(1); //bInGuild
        getGuildInfo(mplew, g);

        return mplew.getPacket();
    }

    private static void getGuildInfo(MaplePacketLittleEndianWriter mplew, MapleGuild guild) {
        mplew.writeInt(guild.getId());
        mplew.writeMapleAsciiString(guild.getName());
        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(guild.getRankTitle(i));
        }
        guild.addMemberData(mplew);
        mplew.writeInt(guild.getCapacity());
        mplew.writeShort(guild.getLogoBG());
        mplew.write(guild.getLogoBGColor());
        mplew.writeShort(guild.getLogo());
        mplew.write(guild.getLogoColor());
        mplew.writeMapleAsciiString(guild.getNotice());
        mplew.writeInt(guild.getGP());
        mplew.writeInt(guild.getAllianceId() > 0 ? guild.getAllianceId() : 0);
    }

    public static MaplePacket guildMemberOnline(int gid, int cid, boolean bOnline) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x3d);
        mplew.writeInt(gid);
        mplew.writeInt(cid);
        mplew.write(bOnline ? 1 : 0);

        return mplew.getPacket();
    }

    public static MaplePacket guildInvite(int gid, String charName, int levelFrom, int jobFrom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x05);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(charName);
        mplew.writeInt(levelFrom);
        mplew.writeInt(jobFrom);

        return mplew.getPacket();
    }

    public static MaplePacket denyGuildInvitation(String charname) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x37);
        mplew.writeMapleAsciiString(charname);

        return mplew.getPacket();
    }

    public static MaplePacket genericGuildMessage(byte code) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(code);

        return mplew.getPacket();
    }

    public static MaplePacket newGuildMember(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x27);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
        mplew.writeInt(mgc.getJobId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getGuildRank()); //should be always 5 but whatevs
        mplew.writeInt(mgc.isOnline() ? 1 : 0); //should always be 1 too
        mplew.writeInt(1); //? could be guild signature, but doesn't seem to matter
        mplew.writeInt(mgc.getAllianceRank()); //should always 3

        return mplew.getPacket();
    }

    //someone leaving, mode == 0x2c for leaving, 0x2f for expelled
    public static MaplePacket memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(bExpelled ? 0x2f : 0x2c);

        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeMapleAsciiString(mgc.getName());

        return mplew.getPacket();
    }

    public static MaplePacket changeRank(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x40);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.write(mgc.getGuildRank());

        return mplew.getPacket();
    }

    public static MaplePacket guildNotice(int gid, String notice) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x44);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(notice);

        return mplew.getPacket();
    }

    public static MaplePacket guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x3C);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getJobId());

        return mplew.getPacket();
    }

    public static MaplePacket rankTitleChange(int gid, String[] ranks) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x3e);
        mplew.writeInt(gid);

        for (String r : ranks) {
            mplew.writeMapleAsciiString(r);
        }
        return mplew.getPacket();
    }

    public static MaplePacket guildDisband(int gid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x32);
        mplew.writeInt(gid);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static MaplePacket guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x42);
        mplew.writeInt(gid);
        mplew.writeShort(bg);
        mplew.write(bgcolor);
        mplew.writeShort(logo);
        mplew.write(logocolor);

        return mplew.getPacket();
    }

    public static MaplePacket guildCapacityChange(int gid, int capacity) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x3a);
        mplew.writeInt(gid);
        mplew.write(capacity);

        return mplew.getPacket();
    }

    public static MaplePacket removeGuildFromAlliance(MapleGuildAlliance alliance, MapleGuild expelledGuild, boolean expelled) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x10);
        addAllianceInfo(mplew, alliance);
        getGuildInfo(mplew, expelledGuild);
        mplew.write(expelled ? 1 : 0); //1 = expelled, 0 = left
        return mplew.getPacket();
    }

    public static MaplePacket changeAlliance(MapleGuildAlliance alliance, final boolean in) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x01);
        mplew.write(in ? 1 : 0);
        mplew.writeInt(in ? alliance.getId() : 0);
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < noGuilds; i++) {
            g[i] = World.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return enableActions();
            }
        }
        mplew.write(noGuilds);
        for (int i = 0; i < noGuilds; i++) {
            mplew.writeInt(g[i].getId());
            //must be world
            Collection<MapleGuildCharacter> members = g[i].getMembers();
            mplew.writeInt(members.size());
            for (MapleGuildCharacter mgc : members) {
                mplew.writeInt(mgc.getId());
                mplew.write(in ? mgc.getAllianceRank() : 0);
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket changeAllianceLeader(int allianceid, int newLeader, int oldLeader) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x02);
        mplew.writeInt(allianceid);
        mplew.writeInt(oldLeader);
        mplew.writeInt(newLeader);
        return mplew.getPacket();
    }

    public static MaplePacket updateAllianceLeader(int allianceid, int newLeader, int oldLeader) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x19);
        mplew.writeInt(allianceid);
        mplew.writeInt(oldLeader);
        mplew.writeInt(newLeader);
        return mplew.getPacket();
    }

    public static MaplePacket sendAllianceInvite(String allianceName, MapleCharacter inviter) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x03);
        mplew.writeInt(inviter.getGuildId());
        mplew.writeMapleAsciiString(inviter.getName());
        //alliance invite did NOT change
        mplew.writeMapleAsciiString(allianceName);
        return mplew.getPacket();
    }

    public static MaplePacket changeGuildInAlliance(MapleGuildAlliance alliance, MapleGuild guild, final boolean add) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x04);
        mplew.writeInt(add ? alliance.getId() : 0);
        mplew.writeInt(guild.getId());
        Collection<MapleGuildCharacter> members = guild.getMembers();
        mplew.writeInt(members.size());
        for (MapleGuildCharacter mgc : members) {
            mplew.writeInt(mgc.getId());
            mplew.write(add ? mgc.getAllianceRank() : 0);
        }
        return mplew.getPacket();
    }

    public static MaplePacket changeAllianceRank(int allianceid, MapleGuildCharacter player) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x05);
        mplew.writeInt(allianceid);
        mplew.writeInt(player.getId());
        mplew.writeInt(player.getAllianceRank());
        return mplew.getPacket();
    }

    public static MaplePacket createGuildAlliance(MapleGuildAlliance alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x0F);
        addAllianceInfo(mplew, alliance);
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            g[i] = World.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return enableActions();
            }
        }
        for (MapleGuild gg : g) {
            getGuildInfo(mplew, gg);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getAllianceInfo(MapleGuildAlliance alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x0C);
        mplew.write(alliance == null ? 0 : 1); //in an alliance
        if (alliance != null) {
            addAllianceInfo(mplew, alliance);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getAllianceUpdate(MapleGuildAlliance alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x17);
        addAllianceInfo(mplew, alliance);
        return mplew.getPacket();
    }

    public static MaplePacket getGuildAlliance(MapleGuildAlliance alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x0D);
        if (alliance == null) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            g[i] = World.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return enableActions();
            }
        }
        mplew.writeInt(noGuilds);
        for (MapleGuild gg : g) {
            getGuildInfo(mplew, gg);
        }
        return mplew.getPacket();
    }

    public static MaplePacket addGuildToAlliance(MapleGuildAlliance alliance, MapleGuild newGuild) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x12);
        addAllianceInfo(mplew, alliance);
        mplew.writeInt(newGuild.getId()); //???
        getGuildInfo(mplew, newGuild);
        mplew.write(0); //???
        return mplew.getPacket();
    }

    private static void addAllianceInfo(MaplePacketLittleEndianWriter mplew, MapleGuildAlliance alliance) {
        mplew.writeInt(alliance.getId());
        mplew.writeMapleAsciiString(alliance.getName());
        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(alliance.getRank(i));
        }
        mplew.write(alliance.getNoGuilds());
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            mplew.writeInt(alliance.getGuildId(i));
        }
        mplew.writeInt(alliance.getCapacity()); // ????
        mplew.writeMapleAsciiString(alliance.getNotice());
    }

    public static MaplePacket allianceMemberOnline(int alliance, int gid, int id, boolean online) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x0E);
        mplew.writeInt(alliance);
        mplew.writeInt(gid);
        mplew.writeInt(id);
        mplew.write(online ? 1 : 0);

        return mplew.getPacket();
    }

    public static MaplePacket updateAlliance(MapleGuildCharacter mgc, int allianceid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x18);
        mplew.writeInt(allianceid);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getJobId());

        return mplew.getPacket();
    }

    public static MaplePacket updateAllianceRank(int allianceid, MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x1B);
        mplew.writeInt(allianceid);
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getAllianceRank());

        return mplew.getPacket();
    }

    public static MaplePacket disbandAlliance(int alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(0x1D);
        mplew.writeInt(alliance);

        return mplew.getPacket();
    }

    public static MaplePacket BBSThreadList(final List<MapleBBSThread> bbs, int start) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildBBS.Get());
        mplew.write(6);

        if (bbs == null) {
            mplew.write(0);
            mplew.writeLong(0);
            return mplew.getPacket();
        }
        int threadCount = bbs.size();
        MapleBBSThread notice = null;
        for (MapleBBSThread b : bbs) {
            if (b.isNotice()) { //notice
                notice = b;
                break;
            }
        }
        final int ret = (notice == null ? 0 : 1);
        mplew.write(ret);
        if (notice != null) { //has a notice
            addThread(mplew, notice);
            threadCount--; //one thread didn't count (because it's a notice)
        }
        if (threadCount < start) { //seek to the thread before where we start
            //uh, we're trying to start at a place past possible
            start = 0;
        }
        //each page has 10 threads, start = page # in packet but not here
        mplew.writeInt(threadCount);
        final int pages = Math.min(10, threadCount - start);
        mplew.writeInt(pages);

        for (int i = 0; i < pages; i++) {
            addThread(mplew, bbs.get(start + i + ret)); //because 0 = notice
        }
        return mplew.getPacket();
    }

    private static void addThread(MaplePacketLittleEndianWriter mplew, MapleBBSThread rs) {
        mplew.writeInt(rs.localthreadID);
        mplew.writeInt(rs.ownerID);
        mplew.writeMapleAsciiString(rs.name);
        mplew.writeLong(PacketHelper.getKoreanTimestamp(rs.timestamp));
        mplew.writeInt(rs.icon);
        mplew.writeInt(rs.getReplyCount());
    }

    public static MaplePacket showThread(MapleBBSThread thread) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildBBS.Get());
        mplew.write(7);

        mplew.writeInt(thread.localthreadID);
        mplew.writeInt(thread.ownerID);
        mplew.writeLong(PacketHelper.getKoreanTimestamp(thread.timestamp));
        mplew.writeMapleAsciiString(thread.name);
        mplew.writeMapleAsciiString(thread.text);
        mplew.writeInt(thread.icon);
        mplew.writeInt(thread.getReplyCount());
        for (MapleBBSReply reply : thread.replies.values()) {
            mplew.writeInt(reply.replyid);
            mplew.writeInt(reply.ownerID);
            mplew.writeLong(PacketHelper.getKoreanTimestamp(reply.timestamp));
            mplew.writeMapleAsciiString(reply.content);
        }
        return mplew.getPacket();
    }

    public static MaplePacket showGuildRanks(int npcid, List<GuildRankingInfo> all) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x49);
        mplew.writeInt(npcid);
        mplew.writeInt(all.size());

        for (GuildRankingInfo info : all) {
            mplew.writeMapleAsciiString(info.getName());
            mplew.writeInt(info.getGP());
            mplew.writeInt(info.getLogo());
            mplew.writeInt(info.getLogoColor());
            mplew.writeInt(info.getLogoBg());
            mplew.writeInt(info.getLogoBgColor());
        }

        return mplew.getPacket();
    }

    public static MaplePacket updateGP(int gid, int GP) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(0x48);
        mplew.writeInt(gid);
        mplew.writeInt(GP);

        return mplew.getPacket();
    }

    public static MaplePacket skillEffect(MapleCharacter from, int skillId, byte level, byte flags, byte speed, byte unk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserSkillPrepare.Get());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        mplew.write(level);
        mplew.write(flags);
        mplew.write(speed);
        mplew.write(unk); // Direction ??

        return mplew.getPacket();
    }

    public static MaplePacket skillCancel(MapleCharacter from, int skillId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserSkillCancel.Get());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);

        return mplew.getPacket();
    }

    public static MaplePacket showMagnet(int mobid, byte success) { // Monster Magnet
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.SHOW_MAGNET.Get());
        mplew.writeInt(mobid);
        mplew.write(success);

        return mplew.getPacket();
    }

    public static MaplePacket sendHint(String hint, int width, int height) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (width < 1) {
            width = hint.length() * 10;
            if (width < 40) {
                width = 40;
            }
        }
        if (height < 5) {
            height = 5;
        }
        mplew.writeShort(ServerPacket.Header.PLAYER_HINT.Get());
        mplew.writeMapleAsciiString(hint);
        mplew.writeShort(width);
        mplew.writeShort(height);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static MaplePacket messengerInvite(String from, int messengerid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Messenger.Get());
        mplew.write(0x03);
        mplew.writeMapleAsciiString(from);
        mplew.write(0x00);
        mplew.writeInt(messengerid);
        mplew.write(0x00);

        return mplew.getPacket();
    }

    public static MaplePacket addMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Messenger.Get());
        mplew.write(0x00);
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.writeShort(channel);

        return mplew.getPacket();
    }

    public static MaplePacket removeMessengerPlayer(int position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Messenger.Get());
        mplew.write(0x02);
        mplew.write(position);

        return mplew.getPacket();
    }

    public static MaplePacket updateMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Messenger.Get());
        mplew.write(0x07);
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.writeShort(channel);

        return mplew.getPacket();
    }

    public static MaplePacket joinMessenger(int position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Messenger.Get());
        mplew.write(0x01);
        mplew.write(position);

        return mplew.getPacket();
    }

    public static MaplePacket messengerChat(String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Messenger.Get());
        mplew.write(0x06);
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static MaplePacket messengerNote(String text, int mode, int mode2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Messenger.Get());
        mplew.write(mode);
        mplew.writeMapleAsciiString(text);
        mplew.write(mode2);

        return mplew.getPacket();
    }

    public static MaplePacket getFindReplyWithCS(String target, final boolean buddy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Whisper.Get());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(2);
        mplew.writeInt(-1);

        return mplew.getPacket();
    }

    public static MaplePacket getFindReplyWithMTS(String target, final boolean buddy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_Whisper.Get());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(0);
        mplew.writeInt(-1);

        return mplew.getPacket();
    }

    public static MaplePacket showEquipEffect() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_FieldSpecificData.Get());

        return mplew.getPacket();
    }

    public static MaplePacket showEquipEffect(int team) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_FieldSpecificData.Get());
        mplew.writeShort(team);
        return mplew.getPacket();
    }

    public static MaplePacket summonSkill(int cid, int summonSkillId, int newStance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_SummonedSkill.Get());
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(newStance);

        return mplew.getPacket();
    }

    public static MaplePacket skillCooldown(int sid, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_SkillCooltimeSet.Get());
        mplew.writeInt(sid);

        if (ServerConfig.version <= 186) {
            mplew.writeShort(time);
        } else {
            mplew.writeInt(time);
        }

        return mplew.getPacket();
    }

    public static MaplePacket useSkillBook(MapleCharacter chr, int skillid, int maxlevel, boolean canuse, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_SkillLearnItemResult.Get());
        mplew.write(0); //?
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.writeInt(skillid);
        mplew.writeInt(maxlevel);
        mplew.write(canuse ? 1 : 0);
        mplew.write(success ? 1 : 0);

        return mplew.getPacket();
    }

    public static MaplePacket getMacros(SkillMacro[] macros) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_MacroSysDataInit.Get());
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (macros[i] != null) {
                count++;
            }
        }
        mplew.write(count); // number of macros
        for (int i = 0; i < 5; i++) {
            SkillMacro macro = macros[i];
            if (macro != null) {
                mplew.writeMapleAsciiString(macro.getName());
                mplew.write(macro.getShout());
                mplew.writeInt(macro.getSkill1());
                mplew.writeInt(macro.getSkill2());
                mplew.writeInt(macro.getSkill3());
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateAriantPQRanking(String name, int score, boolean empty) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.ARIANT_PQ_START.Get());
        mplew.write(empty ? 0 : 1);
        if (!empty) {
            mplew.writeMapleAsciiString(name);
            mplew.writeInt(score);
        }
        return mplew.getPacket();
    }

    public static MaplePacket catchMonster(int mobid, int itemid, byte success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_MobCatchEffect.Get());
        mplew.writeInt(mobid);
        mplew.writeInt(itemid);
        mplew.write(success);

        return mplew.getPacket();
    }

    public static MaplePacket showAriantScoreBoard() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.ARIANT_SCOREBOARD.Get());

        return mplew.getPacket();
    }

    public static MaplePacket boatPacket(int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        // 1034: balrog boat comes, 1548: boat comes, 3: boat leaves
        mplew.writeShort(ServerPacket.Header.LP_CONTISTATE.Get());
        mplew.writeShort(effect); // 0A 04 balrog
        //this packet had 3: boat leaves

        return mplew.getPacket();
    }

    public static MaplePacket boatEffect(int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        // 1034: balrog boat comes, 1548: boat comes, 3: boat leaves
        mplew.writeShort(ServerPacket.Header.LP_CONTIMOVE.Get());
        mplew.writeShort(effect); // 0A 04 balrog
        //this packet had the other ones o.o

        return mplew.getPacket();
    }

    public static MaplePacket Mulung_DojoUp2() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(8); // portal sound

        return mplew.getPacket();
    }

    public static MaplePacket showOXQuiz(int questionSet, int questionId, boolean askQuestion) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_Quiz.Get());
        mplew.write(askQuestion ? 1 : 0);
        mplew.write(questionSet);
        mplew.writeShort(questionId);
        return mplew.getPacket();
    }

    public static MaplePacket leftKnockBack() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SnowBallTouch.Get());
        return mplew.getPacket();
    }

    public static MaplePacket rollSnowball(int type, MapleSnowballs ball1, MapleSnowballs ball2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SnowBallState.Get());
        mplew.write(type); // 0 = normal, 1 = rolls from start to end, 2 = down disappear, 3 = up disappear, 4 = move
        mplew.writeInt(ball1 == null ? 0 : (ball1.getSnowmanHP() / 75));
        mplew.writeInt(ball2 == null ? 0 : (ball2.getSnowmanHP() / 75));
        mplew.writeShort(ball1 == null ? 0 : ball1.getPosition());
        mplew.write(0);
        mplew.writeShort(ball2 == null ? 0 : ball2.getPosition());
        mplew.writeZeroBytes(11);
        return mplew.getPacket();
    }

    public static MaplePacket enterSnowBall() {
        return rollSnowball(0, null, null);
    }

    public static MaplePacket hitSnowBall(int team, int damage, int distance, int delay) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SnowBallHit.Get());
        mplew.write(team);// 0 is down, 1 is up
        mplew.writeShort(damage);
        mplew.write(distance);
        mplew.write(delay);
        return mplew.getPacket();
    }

    public static MaplePacket snowballMessage(int team, int message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SnowBallMsg.Get());
        mplew.write(team);// 0 is down, 1 is up
        mplew.writeInt(message);
        return mplew.getPacket();
    }

    public static MaplePacket finishedSort(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SortItemResult.Get());
        mplew.write(1);
        mplew.write(type);
        return mplew.getPacket();
    }

    // 00 01 00 00 00 00
    public static MaplePacket coconutScore(int[] coconutscore) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CoconutScore.Get());
        mplew.writeShort(coconutscore[0]);
        mplew.writeShort(coconutscore[1]);
        return mplew.getPacket();
    }

    public static MaplePacket hitCoconut(boolean spawn, int id, int type) {
        // FF 00 00 00 00 00 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CoconutHit.Get());
        if (spawn) {
            mplew.write(0);
            mplew.writeInt(0x80);
        } else {
            mplew.writeInt(id);
            mplew.write(type); // What action to do for the coconut.
        }
        return mplew.getPacket();
    }

    public static MaplePacket finishedGather(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GatherItemResult.Get());
        mplew.write(1);
        mplew.write(type);
        return mplew.getPacket();
    }

    public static MaplePacket yellowChat(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SetWeekEventMessage.Get());
        mplew.write(-1); //could be something like mob displaying message.
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static MaplePacket getPeanutResult(int itemId, short quantity, int itemId2, short quantity2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_IncubatorResult.Get());
        mplew.writeInt(itemId);
        mplew.writeShort(quantity);
        mplew.writeInt(5060003);
        mplew.writeInt(itemId2);
        mplew.writeInt(quantity2);

        return mplew.getPacket();
    }

    public static MaplePacket sendLevelup(boolean family, int level, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_NotifyLevelUp.Get());
        mplew.write(family ? 1 : 2);
        mplew.writeInt(level);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static MaplePacket sendMarriage(boolean family, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_NotifyWedding.Get());
        mplew.write(family ? 1 : 0);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static MaplePacket sendJobup(boolean family, int jobid, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_NotifyJobChange.Get());
        mplew.write(family ? 1 : 0);
        mplew.writeInt(jobid); //or is this a short
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static MaplePacket showZakumShrine(boolean spawned, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ZakumTimer.Get());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static MaplePacket showHorntailShrine(boolean spawned, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_HontailTimer.Get());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static MaplePacket showChaosZakumShrine(boolean spawned, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ChaosZakumTimer.Get());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static MaplePacket showChaosHorntailShrine(boolean spawned, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_HontaleTimer.Get());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static MaplePacket stopClock() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_DestroyClock.Get());
        return mplew.getPacket();
    }

    public static MaplePacket spawnDragon(MapleDragon d) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_DragonEnterField.Get());
        mplew.writeInt(d.getOwner());
        mplew.writeInt(d.getPosition().x);
        mplew.writeInt(d.getPosition().y);
        mplew.write(d.getStance()); //stance?
        mplew.writeShort(0);
        mplew.writeShort(d.getJobId());
        return mplew.getPacket();
    }

    public static MaplePacket removeDragon(int chrid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_DragonLeaveField.Get());
        mplew.writeInt(chrid);
        return mplew.getPacket();
    }

    public static MaplePacket moveDragon(MapleDragon d, Point startPos, List<LifeMovementFragment> moves) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_DragonMove.Get()); //not sure
        mplew.writeInt(d.getOwner());
        mplew.writePos(startPos);
        mplew.writeInt(0);

        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static final MaplePacket temporaryStats_Aran() {
        final List<Pair<MapleStat.Temp, Integer>> stats = new ArrayList<Pair<MapleStat.Temp, Integer>>();
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.STR, 999));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.DEX, 999));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.INT, 999));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.LUK, 999));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.WATK, 255));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.ACC, 999));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.AVOID, 999));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.SPEED, 140));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.JUMP, 120));
        return temporaryStats(stats);
    }

    public static final MaplePacket temporaryStats_Balrog(final MapleCharacter chr) {
        final List<Pair<MapleStat.Temp, Integer>> stats = new ArrayList<Pair<MapleStat.Temp, Integer>>();
        int offset = 1 + (chr.getLevel() - 90) / 20;
        //every 20 levels above 90, +1
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.STR, chr.getStat().getTotalStr() / offset));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.DEX, chr.getStat().getTotalDex() / offset));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.INT, chr.getStat().getTotalInt() / offset));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.LUK, chr.getStat().getTotalLuk() / offset));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.WATK, chr.getStat().getTotalWatk() / offset));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.MATK, chr.getStat().getTotalMagic() / offset));
        return temporaryStats(stats);
    }

    public static final MaplePacket temporaryStats(final List<Pair<MapleStat.Temp, Integer>> stats) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ForcedStatSet.Get());
        //str 0x1, dex 0x2, int 0x4, luk 0x8
        //level 0x10 = 255
        //0x100 = 999
        //0x200 = 999
        //0x400 = 120
        //0x800 = 140
        int updateMask = 0;
        for (final Pair<MapleStat.Temp, Integer> statupdate : stats) {
            updateMask |= statupdate.getLeft().getValue();
        }
        List<Pair<MapleStat.Temp, Integer>> mystats = stats;
        if (mystats.size() > 1) {
            Collections.sort(mystats, new Comparator<Pair<MapleStat.Temp, Integer>>() {

                @Override
                public int compare(final Pair<MapleStat.Temp, Integer> o1, final Pair<MapleStat.Temp, Integer> o2) {
                    int val1 = o1.getLeft().getValue();
                    int val2 = o2.getLeft().getValue();
                    return (val1 < val2 ? -1 : (val1 == val2 ? 0 : 1));
                }
            });
        }
        mplew.writeInt(updateMask);
        Integer value;

        for (final Pair<MapleStat.Temp, Integer> statupdate : mystats) {
            value = statupdate.getLeft().getValue();

            if (value >= 1) {
                if (value <= 0x200) { //level 0x10 - is this really short or some other? (FF 00)
                    mplew.writeShort(statupdate.getRight().shortValue());
                } else {
                    mplew.write(statupdate.getRight().byteValue());
                }
            }
        }
        return mplew.getPacket();
    }

    public static final MaplePacket temporaryStats_Reset() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ForcedStatReset.Get());
        return mplew.getPacket();
    }

    //its likely that durability items use this
    public static final MaplePacket showHpHealed(final int cid, final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.Get());
        mplew.writeInt(cid);
        mplew.write(0x0A); //Type 
        mplew.writeInt(amount);
        return mplew.getPacket();
    }

    public static final MaplePacket showOwnHpHealed(final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectLocal.Get());
        mplew.write(0x0A); //Type 
        mplew.writeInt(amount);
        return mplew.getPacket();
    }

    public static final MaplePacket sendRepairWindow(int npc) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserOpenUIWithOption.Get());
        mplew.writeInt(0x22); //sending 0x21 here opens evan skill window o.o
        mplew.writeInt(npc);
        return mplew.getPacket();
    }

    public static final MaplePacket sendPyramidUpdate(final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.PYRAMID_UPDATE.Get());
        mplew.writeInt(amount); //1-132 ?
        return mplew.getPacket();
    }

    public static final MaplePacket sendPyramidResult(final byte rank, final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.PYRAMID_RESULT.Get());
        mplew.write(rank);
        mplew.writeInt(amount); //1-132 ?
        return mplew.getPacket();
    }

    //show_status_info - 01 53 1E 01
    //10/08/14/19/11
    //update_quest_info - 08 53 1E 00 00 00 00 00 00 00 00
    //show_status_info - 01 51 1E 01 01 00 30
    //update_quest_info - 08 51 1E 00 00 00 00 00 00 00 00
    public static final MaplePacket sendPyramidEnergy(final String type, final String amount) {
        return sendString(1, type, amount);
    }

    public static final MaplePacket sendString(final int type, final String object, final String amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        switch (type) {
            case 1:
                mplew.writeShort(ServerPacket.Header.ENERGY.Get());
                break;
            case 2:
                mplew.writeShort(ServerPacket.Header.GHOST_POINT.Get());
                break;
            case 3:
                mplew.writeShort(ServerPacket.Header.GHOST_STATUS.Get());
                break;
        }
        mplew.writeMapleAsciiString(object); //massacre_hit, massacre_cool, massacre_miss, massacre_party, massacre_laststage, massacre_skill
        mplew.writeMapleAsciiString(amount);
        return mplew.getPacket();
    }

    public static final MaplePacket sendGhostPoint(final String type, final String amount) {
        return sendString(2, type, amount); //PRaid_Point (0-1500???)
    }

    public static final MaplePacket sendGhostStatus(final String type, final String amount) {
        return sendString(3, type, amount); //Red_Stage(1-5), Blue_Stage, blueTeamDamage, redTeamDamage
    }

    public static MaplePacket MulungEnergy(int energy) {
        return sendPyramidEnergy("energy", String.valueOf(energy));
    }

    public static MaplePacket getPollQuestion() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.GAME_POLL_QUESTION.Get());
        mplew.writeInt(1);
        mplew.writeInt(14);
        mplew.writeMapleAsciiString(ServerConstants.Poll_Question);
        mplew.writeInt(ServerConstants.Poll_Answers.length); // pollcount
        for (byte i = 0; i < ServerConstants.Poll_Answers.length; i++) {
            mplew.writeMapleAsciiString(ServerConstants.Poll_Answers[i]);
        }

        return mplew.getPacket();
    }

    public static MaplePacket getPollReply(String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserNoticeMsg.Get());
        mplew.writeMapleAsciiString(message);

        return mplew.getPacket();
    }

    public static MaplePacket getEvanTutorial(String data) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_ScriptMessage.Get());

        mplew.writeInt(8);
        mplew.write(0);
        mplew.write(1);
        mplew.write(1);
        mplew.write(1);
        mplew.writeMapleAsciiString(data);

        return mplew.getPacket();
    }

    public static MaplePacket showEventInstructions() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_Desc.Get());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket getOwlOpen() { //best items! hardcoded
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ShopScannerResult.Get());
        mplew.write(7);
        mplew.write(GameConstants.owlItems.length);
        for (int i : GameConstants.owlItems) {
            mplew.writeInt(i);
        } //these are the most searched items. too lazy to actually make
        return mplew.getPacket();
    }

    public static MaplePacket getOwlSearched(final int itemSearch, final List<HiredMerchant> hms) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ShopScannerResult.Get());
        mplew.write(6);
        mplew.writeInt(0);
        mplew.writeInt(itemSearch);
        int size = 0;

        for (HiredMerchant hm : hms) {
            size += hm.searchItem(itemSearch).size();
        }
        mplew.writeInt(size);
        for (HiredMerchant hm : hms) {
            final List<MaplePlayerShopItem> items = hm.searchItem(itemSearch);
            for (MaplePlayerShopItem item : items) {
                mplew.writeMapleAsciiString(hm.getOwnerName());
                mplew.writeInt(hm.getMap().getId());
                mplew.writeMapleAsciiString(hm.getDescription());
                mplew.writeInt(item.item.getQuantity()); //I THINK.
                mplew.writeInt(item.bundles); //I THINK.
                mplew.writeInt(item.price);
                switch (InventoryHandler.OWL_ID) {
                    case 0:
                        mplew.writeInt(hm.getOwnerId()); //store ID
                        break;
                    case 1:
                        mplew.writeInt(hm.getStoreId());
                        break;
                    default:
                        mplew.writeInt(hm.getObjectId());
                        break;
                }
                mplew.write(hm.getFreeSlot() == -1 ? 1 : 0);
                mplew.write(GameConstants.getInventoryType(itemSearch).getType()); //position?
                if (GameConstants.getInventoryType(itemSearch) == MapleInventoryType.EQUIP) {
                    PacketHelper.addItemInfo(mplew, item.item, true, true);
                }
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket getRPSMode(byte mode, int mesos, int selection, int answer) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_RPSGame.Get());
        mplew.write(mode);
        switch (mode) {
            case 6: { //not enough mesos
                if (mesos != -1) {
                    mplew.writeInt(mesos);
                }
                break;
            }
            case 8: { //open (npc)
                mplew.writeInt(9000019);
                break;
            }
            case 11: { //selection vs answer
                mplew.write(selection);
                mplew.write(answer); // FF = lose, or if selection = answer then lose ???
                break;
            }
        }
        return mplew.getPacket();
    }

    public static final MaplePacket getSlotUpdate(byte invType, byte newSlots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_InventoryGrow.Get());
        mplew.write(invType);
        mplew.write(newSlots);
        return mplew.getPacket();
    }

    public static MaplePacket followRequest(int chrid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SetPassenserRequest.Get());
        mplew.writeInt(chrid);
        return mplew.getPacket();
    }

    public static MaplePacket followEffect(int initiator, int replier, Point toMap) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserFollowCharacter.Get());
        mplew.writeInt(initiator);
        mplew.writeInt(replier);
        if (replier == 0) { //cancel
            mplew.write(toMap == null ? 0 : 1); //1 -> x (int) y (int) to change map
            if (toMap != null) {
                mplew.writeInt(toMap.x);
                mplew.writeInt(toMap.y);
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket getFollowMsg(int opcode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserFollowCharacterFailed.Get());
        /*
            0x00    原因不明の理由で自動追尾を申請できませんでした。
            0x01    相手が自動追尾できない位置にいるか距離が遠すぎて自動追尾できません。
            0x02    相手が自動追尾できない位置にいるか距離が遠すぎて自動追尾できません。
            0x03    相手は現在自動追尾申請できない状態です。
            0x04    自動追尾中のキャラクターがいると自動追尾申請できません。
            0x05    相手が自動追尾を許可しませんでした。
            0x06    離れているようです。
            0x07    以降0x00と同じ
         */
        mplew.writeLong(opcode); //5 = canceled request.
        return mplew.getPacket();
    }

    public static MaplePacket moveFollow(Point otherStart, Point myStart, Point otherEnd, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserPassiveMove.Get());
        mplew.writePos(otherStart);
        mplew.writePos(myStart);
        PacketHelper.serializeMovementList(mplew, moves);
        mplew.write(0x11); //what? could relate to movePlayer
        for (int i = 0; i < 8; i++) {
            mplew.write(0x88); //?? sometimes 44
        }
        mplew.write(8); //?
        mplew.writePos(otherEnd);
        mplew.writePos(otherStart);

        return mplew.getPacket();
    }

    // チャット欄へのテキスト表示
    public static final MaplePacket getFollowMessage(final String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_UserChatMsg.Get());
        /*
            // どのような内容のテキストでも問題なし
            0x0000  全体チャット
            0x0001  内緒
            0x0002  ピンク
            0x0003  友達
            0x0004  ギルド
            0x0005  連合
            0x0006  灰色
            0x0007  黄色
            0x0008  薄い黄色
            0x0009  水色
            0x000A  GM
            0x000B  薄いピンク
            0x000C  メガホン
            0x0011  濃い紫
            0x0017  黄色
            0x0018  薄い水色
            0x0019  GM
            0x001A  体験用アバター
            // "名前 : メッセージ" 形式のテキストでないとクライアントがクラッシュする
            0x000D  拡声器
            0x000E  体験用アバター
            0x000F  アバターランダムボックス
            0x0010  アイテム拡声器
            0x0012  ワールド拡声器
            0x0013  3連拡声器のプレビューと同等
            0x0014  ハート拡声器
            0x0015  ドクロ拡声器
            0x0016  ハートバルーン拡声器
         */
        mplew.writeShort(0x0B);
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static final MaplePacket getNodeProperties(final MapleMonster objectid, final MapleMap map) {
        //idk.
        if (objectid.getNodePacket() != null) {
            return objectid.getNodePacket();
        }
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.MONSTER_PROPERTIES.Get());
        mplew.writeInt(objectid.getObjectId()); //?
        mplew.writeInt(map.getNodes().size());
        mplew.writeInt(objectid.getPosition().x);
        mplew.writeInt(objectid.getPosition().y);
        for (MapleNodeInfo mni : map.getNodes()) {
            mplew.writeInt(mni.x);
            mplew.writeInt(mni.y);
            mplew.writeInt(mni.attr);
            if (mni.attr == 2) { //msg
                mplew.writeInt(500); //? talkMonster
            }
        }
        mplew.writeZeroBytes(6);
        objectid.setNodePacket(mplew.getPacket());
        return objectid.getNodePacket();
    }

    public static final MaplePacket getMovingPlatforms(final MapleMap map) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_FootHoldInfo.Get());
        mplew.writeInt(map.getPlatforms().size());
        for (MaplePlatform mp : map.getPlatforms()) {
            mplew.writeMapleAsciiString(mp.name);
            mplew.writeInt(mp.start);
            mplew.writeInt(mp.SN.size());
            for (int x = 0; x < mp.SN.size(); x++) {
                mplew.writeInt(mp.SN.get(x));
            }
            mplew.writeInt(mp.speed);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.x2);
            mplew.writeInt(mp.y1);
            mplew.writeInt(mp.y2);
            mplew.writeInt(mp.x1);//?
            mplew.writeInt(mp.y1);
            mplew.writeShort(mp.r);
        }
        return mplew.getPacket();
    }

    public static final MaplePacket getUpdateEnvironment(final MapleMap map) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.LP_FieldObstacleOnOffStatus.Get());
        mplew.writeInt(map.getEnvironment().size());
        for (Entry<String, Integer> mp : map.getEnvironment().entrySet()) {
            mplew.writeMapleAsciiString(mp.getKey());
            mplew.writeInt(mp.getValue());
        }
        return mplew.getPacket();
    }

    public static MaplePacket sendEngagementRequest(String name, int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MarriageRequest.Get());
        mplew.write(0); //mode, 0 = engage, 1 = cancel, 2 = answer.. etc
        mplew.writeMapleAsciiString(name); // name
        mplew.writeInt(cid); // playerid
        return mplew.getPacket();
    }

    /**
     *
     * @param type - (0:Light&Long 1:Heavy&Short)
     * @param delay - seconds
     * @return
     */
    public static MaplePacket trembleEffect(int type, int delay) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FieldEffect.Get());
        mplew.write(1);
        mplew.write(type);
        mplew.writeInt(delay);
        return mplew.getPacket();
    }

    public static MaplePacket sendEngagement(final byte msg, final int item, final MapleCharacter male, final MapleCharacter female) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 0B = Engagement has been concluded.
        // 0D = The engagement is cancelled.
        // 0E = The divorce is concluded.
        // 10 = The marriage reservation has been successsfully made.
        // 12 = Wrong character name
        // 13 = The party in not in the same map.
        // 14 = Your inventory is full. Please empty your E.T.C window.
        // 15 = The person's inventory is full.
        // 16 = The person cannot be of the same gender.
        // 17 = You are already engaged.
        // 18 = The person is already engaged.
        // 19 = You are already married.
        // 1A = The person is already married.
        // 1B = You are not allowed to propose.
        // 1C = The person is not allowed to be proposed to.
        // 1D = Unfortunately, the one who proposed to you has cancelled his proprosal.
        // 1E = The person had declined the proposal with thanks.
        // 1F = The reservation has been cancelled. Try again later.
        // 20 = You cannot cancel the wedding after reservation.
        // 22 = The invitation card is ineffective.
        mplew.writeShort(ServerPacket.Header.LP_MarriageResult.Get());
        mplew.write(msg); // 1103 custom quest
        switch (msg) {
            case 11: {
                mplew.writeInt(0); // ringid or uniqueid
                mplew.writeInt(male.getId());
                mplew.writeInt(female.getId());
                mplew.writeShort(1); //always
                mplew.writeInt(item);
                mplew.writeInt(item); // wtf?repeat?
                mplew.writeAsciiString(male.getName(), 13);
                mplew.writeAsciiString(female.getName(), 13);
                break;
            }
        }
        return mplew.getPacket();
    }

    // パチンコ関連
    // CMS v72から流用
    public static MaplePacket BeansGameMessage(int cid, int x, String laba) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.TIP_BEANS.Get());
        mplew.writeInt(cid);
        // JMS v186.1 fix
        mplew.write(x);
        mplew.writeMapleAsciiString(laba);
        return mplew.getPacket();
    }

    public static MaplePacket updateBeans(int beansCount) {
        return updateBeans(0, beansCount);
    }

    public static MaplePacket updateBeans(int cid, int beansCount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.UPDATE_BEANS.Get());
        mplew.writeInt(beansCount);
        return mplew.getPacket();
    }

    public static MaplePacket 能量储存器(int beansCount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.UPDATE_BEANS.Get());//0x253
        mplew.writeInt(beansCount);
        return mplew.getPacket();
    }

    public static MaplePacket openBeans(MapleCharacter c, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.OPEN_BEANS.Get());
        mplew.writeInt(c.getTama());
        mplew.write(type);

        return mplew.getPacket();
    }

    public static MaplePacket BeansZJgeidd(boolean type, int a) {//豆豆进洞后奖励的
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.SHOOT_BEANS.Get());
        mplew.write(type ? BeanGame.BeansType.奖励豆豆效果.getType() : BeanGame.BeansType.奖励豆豆效果B.getType());//类型 05   08  都是加豆豆···
        mplew.writeInt(a);//奖励豆豆的数量
        mplew.write(5);
        return mplew.getPacket();
    }

    public static MaplePacket BeansZJgeiddB(int a) {//豆豆进洞后奖励的
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.SHOOT_BEANS.Get());
        mplew.write(BeanGame.BeansType.奖励豆豆效果B.getType());//类型 05   08  都是加豆豆···
        mplew.writeInt(a);//奖励豆豆的数量
        mplew.write(0);//未知效果
        return mplew.getPacket();
    }

    public static MaplePacket BeansHJG(byte type) {//黄金狗
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.SHOOT_BEANS.Get());
        mplew.write(BeanGame.BeansType.黄金狗.getType());//类型
        mplew.write(type);//改变模式
        return mplew.getPacket();
    }

    public static MaplePacket BeansJDCS(int a, int 加速旋转, int 蓝, int 绿, int 红) {//进洞次数 最多有7个
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.SHOOT_BEANS.Get());
        mplew.write(BeanGame.BeansType.颜色求进洞.getType());
        mplew.write(a);//
        mplew.write(加速旋转);//快速转动
        mplew.write(蓝);// 蓝？
        mplew.write(绿);// 绿？
        mplew.write(红);// 红？
        return mplew.getPacket();
    }

    public static MaplePacket BeansJDXZ(int a, int 第一排, int 第三排, int 第二排, int 启动打怪效果, int 中奖率, int 加速旋转, boolean 关闭打击效果A, boolean 关闭打击效果B) {//进洞后开始旋转图片
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.SHOOT_BEANS.Get());
        mplew.write(BeanGame.BeansType.进洞旋转.getType());//类型
        mplew.write(a);
        mplew.write(第一排);//第一排 
        mplew.write(第三排);//第三排
        mplew.write(第二排);//第二排
        mplew.write(启动打怪效果);//开启情况下出现怪物打框
        if (启动打怪效果 > 0) {
            mplew.write(中奖率);//中奖率？？%
            mplew.writeInt(0);//未知
        }
        mplew.write(加速旋转);//加速旋转
        mplew.writeBoolean(关闭打击效果A);//boolean
        mplew.writeBoolean(关闭打击效果B);//boolean
        return mplew.getPacket();
    }

    public static MaplePacket Beans_why() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.SHOOT_BEANS.Get());
        mplew.write(BeanGame.BeansType.未知效果.getType());//类型
        return mplew.getPacket();
    }

    public static MaplePacket BeansUP(int ITEM) {//%s。请拿到凯瑟琳处确认。
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.SHOOT_BEANS.Get());
        mplew.write(BeanGame.BeansType.领奖NPC.getType());//类型
        mplew.writeInt(ITEM);
        return mplew.getPacket();
    }

    public static MaplePacket showBeans(List<MapleBeans> beansInfo) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(ServerPacket.Header.SHOOT_BEANS.Get());
        mplew.write(BeanGame.BeansType.开始打豆豆.getType());
        mplew.write(beansInfo.size());
        for (MapleBeans bean : beansInfo) {
            mplew.writeShort(bean.getPos());
            mplew.write(bean.getType());
            mplew.writeInt(bean.getNumber());
        }
        return mplew.getPacket();
    }

    // context packet
    public static MaplePacket getShowInventoryFull() {
        return ContextPacket.getShowInventoryStatus(DropPickUpMessageType.PICKUP_INVENTORY_FULL);
    }

    public static MaplePacket showItemUnavailable() {
        return ContextPacket.getShowInventoryStatus(DropPickUpMessageType.PICKUP_UNAVAILABLE);
    }
}
