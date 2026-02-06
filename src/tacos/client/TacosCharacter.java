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
package tacos.client;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import odin.client.BuddyList;
import odin.client.BuddylistEntry;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.MonsterBook;
import odin.client.PlayerStats;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventory;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MapleMount;
import odin.client.inventory.MaplePet;
import odin.constants.GameConstants;
import odin.handling.world.MapleMessenger;
import odin.handling.world.OdinWorld;
import odin.handling.world.family.MapleFamilyCharacter;
import odin.handling.world.guild.MapleGuildCharacter;
import odin.server.maps.AbstractAnimatedMapleMapObject;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapFactory;
import odin.server.maps.MapleMapObjectType;
import tacos.config.Region;
import tacos.config.Version;
import tacos.constants.TacosConstants;
import tacos.database.LazyData;
import tacos.debug.DebugLogger;
import tacos.network.MaplePacket;
import tacos.packet.ops.OpsMovePathAttr;
import tacos.packet.response.ResCClientSocket;
import tacos.packet.response.ResCStage;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.script.portal.ArdentmillPortal;
import tacos.script.portal.FreeMarketPortal;
import tacos.server.TacosChannel;
import tacos.server.TacosServer;
import tacos.server.TacosServerType;
import tacos.server.TacosWorld;
import tacos.server.map.TacosPortal;

/**
 *
 * @author Riremito
 */
public class TacosCharacter extends AbstractAnimatedMapleMapObject {

    protected MapleClient client;
    protected int id;
    protected int world_id = 0;
    protected int channel_id = 0;
    protected MapleMap map;
    protected int dwPosMap;
    protected int nPortal;
    private TacosLastStat laststat = null;
    private int viewRange = 1600;
    private int viewRangeSq = 1600 * 1600;
    private TacosForcedStat forcedStat = new TacosForcedStat();
    protected TacosKeyLayout keylayout = new TacosKeyLayout();
    protected MonsterBook monsterbook = null;
    private FreeMarketPortal portal_fm = new FreeMarketPortal();
    private ArdentmillPortal portal_ardentmill = new ArdentmillPortal();
    private ArrayList<LazyData> lazy_data_list = new ArrayList<>();

    public void SendPacket(MaplePacket packet) {
        this.client.SendPacket(packet);
    }

    public void sendMigrateCommand(TacosServer server) {
        // send next server ip and port.
        SendPacket(ResCClientSocket.MigrateCommand(server));
        // stop sending/receiving packets.
        this.client.closeSession();
    }

    public int getId() {
        return this.id;
    }

    public TacosWorld getWorld() {
        return this.client.getWorld();
    }

    public TacosServerType getServerType() {
        return this.client.getServer().getType();
    }

    public TacosChannel getChannelServer() {
        return this.client.getChannelServer();
    }

    public int getWorldId() {
        return this.world_id;
    }

    public void setWorldId(int world_id) {
        this.world_id = world_id;
    }

    public int getChannelId() {
        return this.channel_id;
    }

    public void setChannelId(int channel) {
        this.channel_id = channel;
    }

    public int getViewRange() {
        return this.viewRange;
    }

    public void setViewRange(int viewRange) {
        this.viewRange = viewRange;
        this.viewRangeSq = viewRange * viewRange;
    }

    public int getViewRangeSq() {
        return this.viewRangeSq;
    }

    public TacosForcedStat getForcedStat() {
        return this.forcedStat;
    }

    public void setForcedStatBalorg() {
        int offset = 1 + (this.level - 90) / 20;
        this.forcedStat.setSTR(this.stats.getTotalStr() / offset);
        this.forcedStat.setDEX(this.stats.getTotalDex() / offset);
        this.forcedStat.setINT(this.stats.getTotalInt() / offset);
        this.forcedStat.setLUK(this.stats.getTotalLuk() / offset);
        this.forcedStat.setPAD(this.stats.getTotalWatk() / offset);
        this.forcedStat.setMAD(this.stats.getTotalMagic() / offset);
    }

    public void setForcedStatAran() {
        this.forcedStat.setSTR(999);
        this.forcedStat.setDEX(999);
        this.forcedStat.setINT(999);
        this.forcedStat.setLUK(999);
        this.forcedStat.setPAD(255);
        this.forcedStat.setACC(999);
        this.forcedStat.setEVA(999);
        this.forcedStat.setSpeed(140);
        this.forcedStat.setJump(120);
    }

    public TacosKeyLayout getKeyLayout() {
        return this.keylayout;
    }

    public void setKeyLayout(TacosKeyLayout keylayout) {
        this.keylayout = keylayout;
    }

    public void changeKeybinding(int key, byte type, int action) {
        if (type != 0) {
            this.keylayout.put(key, type, action);
        } else {
            this.keylayout.remove(key);
        }
    }

    public MonsterBook getMonsterBook() {
        return this.monsterbook;
    }

    public void setMonsterBook(MonsterBook monsterbook) {
        this.monsterbook = monsterbook;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.PLAYER;
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // enter game server.
    protected void sendSetField(MapleCharacter mchr, boolean bCharacterData) {
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            SendPacket(ResCStage.SetField_JMS_302(mchr, 1, bCharacterData, 0));
            SendPacket(ResCStage.SetField_JMS_302(mchr, 2, bCharacterData, -1));
            return;
        }

        SendPacket(ResCStage.SetField(mchr, bCharacterData));
    }

    public MapleMap getMap() {
        return this.map;
    }

    public void setMap(MapleMap map) {
        this.map = map;
    }

    public int getPosMap() {
        return this.dwPosMap;
    }

    public void setPosMapAndPortal(int dwPosMap, int nPortal) {
        setPosMap(dwPosMap);
        setPortal(nPortal);
    }

    private void setPosMap(int dwPosMap) {
        this.dwPosMap = dwPosMap;
    }

    public int getPortal() {
        return this.nPortal;
    }

    private void setPortal(int nPortal) {
        this.nPortal = nPortal;
    }

    public void updateMap(MapleMap map_to, TacosPortal portal_to) {
        setMap(map_to);
        setPosMap(map_to.getId());
        setPortal(portal_to.getId()); // spawn point
        setPosition(portal_to.getPosition()); // spawn point xy (server side), some version could not control spawn xy by packet.
        setFH(0); // foothold id is 0 while character is in the air.
        setStance(OpsMovePathAttr.MPA_NORMAL.get()); // default state (?)
    }

    public void updateMapById(int map_id, int portal_id) {
        MapleMapFactory mapFactory = getChannelServer().getMapFactory();
        MapleMap map_to = mapFactory.getMap(map_id);

        if (map_to != null) {
            int forced_return_map_id = map_to.getForcedReturnId();
            if (forced_return_map_id != TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID) {
                map_to = map_to.getForcedReturnMap();
            }
        }
        if (map_to == null) {
            map_to = mapFactory.getMap(TacosConstants.DEFAULT_RETURN_MAP_ID); // return to default map.
            DebugLogger.ErrorLog("updateMapById : invalid map = " + map_id);
        }
        TacosPortal portal_to = map_to.getPortal(portal_id);
        if (portal_to == null) {
            portal_to = map_to.getPortal(0);
            DebugLogger.ErrorLog("updateMapById : invalid portal = " + portal_id);
        }

        updateMap(map_to, portal_to);
    }

    public boolean usePortal(boolean isPortal, int map_id_to, String portal_name, int revive_type) {
        return mapChangePortal(isPortal, map_id_to, portal_name, revive_type);
    }

    public boolean usePortalScript(String portal_name) {
        return mapChangePortal(true, -1, portal_name, 0);
    }

    public boolean usePortalTeleport(String portal_name) {
        // not coded.
        return true;
    }

    public boolean mapChangePortal(boolean isPortal, int map_id_to, String portal_name, int revive_type) {
        if (map == null) {
            return false;
        }
        // use normal portal.
        if (isPortal) {
            TacosPortal portal = map.getPortal(portal_name);
            if (portal == null) {
                return false;
            }
            DebugMsg("mapChangePortal : map = " + map.getId() + ", portal = \"" + portal_name + "\"" + " -> " + portal.getTargetMapId());
            if (!portal.enterPortal(client)) {
                return false;
            }
            return true;
        }
        if (map_id_to == 0) {
            if (!isAlive()) {

            }
        }
        MapleMap map_to = null;
        if (!isAlive()) {
            // revive
            if (map_id_to == 0) {
                map_to = (revive_type > 0) ? getMap() : getMap().getReturnMap();
                changeMap(map_to, map_to.getPortal(0));
                getStat().setHp(getStat().getMaxHp());
                getStat().setMp(getStat().getMaxMp());
                sendStatChanged(true);
                return true;
            }
            // hack?
            return false;
        }
        // direct map change.
        map_to = getChannelServer().getMapFactory().getMap(map_id_to);
        changeMap(map_to, map_to.getPortal(0));
        return true;
    }

    public void changeMap(MapleMap to, TacosPortal pto) {
        ((MapleCharacter) this).changeMapInternal(to, pto.getPosition(), pto);
    }

    // unlock 1
    public void updateInv() {
        SendPacket(ResCWvsContext.InventoryOperation(true, null));
    }

    // unlock 2
    public void updateStat() {
        SendPacket(ResCWvsContext.StatChanged(null, true, 0));
    }

    public void sendStatChanged() {
        sendStatChanged(false);
    }

    // stat
    public void sendStatChanged(boolean unlock) {
        if (this.laststat == null) {
            this.laststat = new TacosLastStat(this);
            return;
        }

        this.laststat.update(this);

        SendPacket(ResCWvsContext.StatChanged(this, unlock, this.laststat.getStatMask()));
        if (this.laststat.getStatMask() != 0) {
            equipChanged();
        }

        this.laststat.clearStatMask();
    }

    protected int gender;
    protected int skinColor;
    protected int face;
    protected int hair;
    protected int level;
    protected int job;
    protected PlayerStats stats;
    protected int remainingAp;
    protected int[] remainingSp = new int[10];
    protected int exp;
    protected int fame;
    protected int meso;
    protected int gashaEXP = 0;
    protected List<MaplePet> pets;

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getSkinColor() {
        return this.skinColor;
    }

    public int getFace() {
        return this.face;
    }

    public int getHair() {
        return this.hair;
    }

    public int getLevel() {
        return this.level;
    }

    public int getJob() {
        return this.job;
    }

    public PlayerStats getStat() {
        return this.stats;
    }

    public boolean isAlive() {
        return this.stats.getHp() > 0;
    }

    public int getRemainingAp() {
        return this.remainingAp;
    }

    public void setRemainingAp(int remainingAp) {
        this.remainingAp = remainingAp;
    }

    public int getRemainingSp() {
        // default
        return this.remainingSp[GameConstants.getSkillBook(this.job)];
    }

    public int getRemainingSp(int skillbook) {
        return this.remainingSp[skillbook];
    }

    public int[] getRemainingSps() {
        return this.remainingSp;
    }

    public int getRemainingSpSize() {
        int ret = 0;
        for (int i = 0; i < this.remainingSp.length; i++) {
            if (this.remainingSp[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public void setRemainingSps(String remainingSp) {
        String sps[] = remainingSp.split(",");
        for (int i = 0; i < this.remainingSp.length; i++) {
            this.remainingSp[i] = Integer.parseInt(sps[i]);
        }
    }

    public int getExp() {
        return this.exp;
    }

    public int getFame() {
        return this.fame;
    }

    public int getMeso() {
        return this.meso;
    }

    public int getGashaEXP() {
        return this.gashaEXP;
    }

    // guild
    protected MapleGuildCharacter mgc;
    protected int guildid = 0;
    protected int guildrank = 5;
    protected int allianceRank = 5;

    public int getGuildId() {
        return guildid;
    }

    public void setGuildId(int guildid) {
        this.guildid = guildid;
        if (this.guildid > 0) {
            if (this.mgc == null) {
                this.mgc = new MapleGuildCharacter(this);

            } else {
                this.mgc.setGuildId(guildid);
            }
        } else {
            this.mgc = null;
        }
    }

    public int getGuildRank() {
        return this.guildrank;
    }

    public void setGuildRank(int guildrank) {
        this.guildrank = guildrank;
        if (this.mgc != null) {
            this.mgc.setGuildRank(guildrank);
        }
    }

    public int getAllianceRank() {
        return this.allianceRank;
    }

    public void setAllianceRank(int allianceRank) {
        this.allianceRank = allianceRank;
        if (this.mgc != null) {
            this.mgc.setAllianceRank(allianceRank);
        }
    }

    // family
    protected MapleFamilyCharacter mfc;
    protected int currentrep;
    protected int totalrep;

    public int getCurrentRep() {
        return this.currentrep;
    }

    public int getTotalRep() {
        return this.totalrep;
    }

    public void setCurrentRep(int currentrep) {
        this.currentrep = currentrep;
        if (this.mfc != null) {
            this.mfc.setCurrentRep(currentrep);
        }
    }

    public void setTotalRep(int totalrep) {
        this.totalrep = totalrep;
        if (this.mfc != null) {
            this.mfc.setTotalRep(totalrep);
        }
    }

    protected int subcategory = 0;

    public int getSubcategory() {
        if (this.job >= 430 && this.job <= 434) {
            return 1;
        }
        return this.subcategory;
    }

    public void setSubcategory(int subcategory) {
        this.subcategory = subcategory;
    }

    protected MapleMount mount = null;

    public MapleMount getMount() {
        return this.mount;
    }

    public boolean setMount() {
        int mount_id = 1004;
        switch (this.job / 1000) {
            case 0: {
                mount_id = 1004;
                break;
            }
            case 1: {
                mount_id = 10001004;
                break;
            }
            case 2: {
                if (GameConstants.isAran(this.job)) {
                    mount_id = 20001004;
                }
                if (GameConstants.isEvan(this.job)) {
                    mount_id = 20011004;
                }
                break;
            }
            case 3: {
                mount_id = 30001004;
                break;
            }
            default: {
                break;
            }
        }

        this.mount = new MapleMount(this, 0, mount_id, 0, 1, 0);
        return true;
    }

    // ranking
    protected int rank = 1;
    protected int rankMove = 0;
    protected int jobRank = 1;
    protected int jobRankMove = 0;

    public void setRank(int rank, int rank_move, int rank_job, int rank_job_move) {
        this.rank = rank;
        this.rankMove = rank_move;
        this.jobRank = rank_job;
        this.jobRankMove = rank_job_move;
    }

    public int getRank() {
        return this.rank;
    }

    public int getRankMove() {
        return this.rankMove;
    }

    public int getJobRank() {
        return this.jobRank;
    }

    public int getJobRankMove() {
        return this.jobRankMove;
    }

    protected int marriageId = 0;
    protected int marriageItemId = 0;

    public int getMarriageId() {
        return this.marriageId;
    }

    public void setMarriageId(int marriageId) {
        this.marriageId = marriageId;
    }

    public int getMarriageItemId() {
        return marriageItemId;
    }

    public void setMarriageItemId(int marriageItemId) {
        this.marriageItemId = marriageItemId;
    }

    public MaplePet getPet(int index) {
        if (3 <= index) {
            return null;
        }
        byte count = 0;
        for (MaplePet pet : this.pets) {
            if (pet.getSummoned()) {
                if (count == index) {
                    return pet;
                }
                count++;
            }
        }
        return null;
    }

    protected MapleInventory[] inventory;

    public MapleInventory getInventory(MapleInventoryType type) {
        return this.inventory[type.ordinal()];
    }

    public void equipChanged() {
        this.map.broadcastMessage(this, ResCUserRemote.AvatarModified(this, 1), false);

        this.stats.recalcLocalStats();
        if (getMessenger() != null) {
            OdinWorld.Messenger.updateMessenger(getMessenger().getId(), getName(), this.client.getChannelId());
        }

        if (isCloning()) {
            cloneUpdate();
            this.map.broadcastMessageClone(getClone(), ResCUserRemote.AvatarModified(getClone(), 1));
        }
    }

    protected MapleMessenger messenger;

    public MapleMessenger getMessenger() {
        return this.messenger;
    }

    protected int accountid;
    protected String name;
    protected int gmLevel;

    public int getAccountID() {
        return this.accountid;
    }

    public void setAccountID(int accountid) {
        this.accountid = accountid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGM() {
        return this.gmLevel > 0;
    }

    public void setGM(int gmLevel) {
        this.gmLevel = gmLevel;
    }

    public boolean isAdmin() {
        return gmLevel >= 5;
    }

    public void setMessenger(MapleMessenger messenger) {
        this.messenger = messenger;
    }

    // debug
    // 青文字
    public void DebugMsg(String text) {
        SendPacket(ResWrapper.BroadCastMsgNotice(text));
    }

    // 青文字 & アイテム表示
    public void DebugMsgItem(String text, int item_id) {
        SendPacket(ResWrapper.BroadCastMsgNoticeItem(text, item_id));
    }

    // ピンク
    public void DebugMsg2(String text) {
        SendPacket(ResWrapper.BroadCastMsgEvent(text));
    }

    // 黄色
    public void DebugMsg3(String text) {
        SendPacket(ResCWvsContext.SetWeekEventMessage(text));
    }

    public void Notice(String text) {
        SendPacket(ResWrapper.BroadCastMsgEvent(text));
    }

    public FreeMarketPortal getFreeMarketPortal() {
        return this.portal_fm;
    }

    public ArdentmillPortal getArdentmillPortal() {
        return this.portal_ardentmill;
    }

    public ArrayList<LazyData> getLazyDataList() {
        return this.lazy_data_list;
    }

    // friend
    protected BuddyList buddylist = null;

    public BuddyList getBuddylist() {
        return this.buddylist;
    }

    public void setBuddylist(int capacity) {
        this.buddylist = new BuddyList(capacity);
    }

    public int getBuddyCapacity() {
        return this.buddylist.getCapacity();
    }

    public void setBuddyCapacity(int capacity) {
        this.buddylist.setCapacity(capacity);
        SendPacket(ResWrapper.updateBuddyCapacity(capacity));
    }

    // 相互にフレンド登録されているフレンドにチャンネル情報を通知
    public boolean updateOnlineFriend(TacosCharacter friend, boolean isOnline) {
        // 相互にフレンド登録されているか確認
        BuddylistEntry ble = this.buddylist.get(friend.getId());
        if (ble == null) {
            return false;
        }
        if (!ble.isVisible()) {
            return false;
        }
        // フレンドのチャンネル情報を更新
        ble.setChannel(isOnline ? friend.getChannelId() : -1);
        this.buddylist.put(ble);
        SendPacket(ResWrapper.updateBuddyChannel(ble.getCharacterId(), isOnline ? (ble.getChannel() - 1) : -1)); // from 0.
        return true;
    }

    // フレンドへチャンネルを通知
    public void notityOnlineToFriends(boolean isOnline) {
        TacosWorld world = getWorld();
        for (int friend_id : this.buddylist.getBuddyIds()) {
            TacosCharacter friend = world.findOnlinePlayerById(friend_id);
            if (friend == null) {
                continue;
            }
            friend.updateOnlineFriend(this, isOnline);
        }
    }

    // フレンドのチャンネル情報を取得
    public void setOnlineFriends() {
        TacosWorld world = getWorld();
        for (int friend_id : this.buddylist.getBuddyIds()) {
            TacosCharacter friend = world.findOnlinePlayerById(friend_id);
            if (friend == null) {
                continue;
            }
            // オンラインのフレンドのチャンネル情報を更新
            BuddylistEntry ble = this.buddylist.get(friend.getId());
            ble.setChannel(friend.getChannelId());
            this.buddylist.put(ble);
        }
    }

    // clone
    protected boolean clone = false;
    protected boolean cloning = false;
    protected transient WeakReference<MapleCharacter>[] clones;
    protected MapleCharacter clone_parent = null;

    public boolean isClone() {
        return this.clone;
    }

    public void setClone(boolean c) {
        this.clone = c;
    }

    public boolean isCloning() {
        return this.cloning;
    }

    public WeakReference<MapleCharacter>[] getClones() {
        return this.clones;
    }

    public MapleCharacter getClone() {
        return this.clones[0].get();
    }

    public boolean cloneUpdate() {
        if (this.clone) {
            return false;
        }

        this.clones[0].get().getInventory(MapleInventoryType.EQUIPPED).resetForClone();
        for (IItem equip : getInventory(MapleInventoryType.EQUIPPED)) {
            this.clones[0].get().getInventory(MapleInventoryType.EQUIPPED).addFromDB(equip);
        }
        return true;
    }

    // old code.
    public int getMapId() {
        if (this.map != null) {
            return this.map.getId();
        }
        return this.dwPosMap;
    }
}
