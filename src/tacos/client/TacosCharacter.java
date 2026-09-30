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

import tacos.server.map.object.TacosDragon;
import tacos.server.map.object.TacosSkillPet;
import java.awt.Point;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import odin.client.BuddyList;
import odin.client.BuddylistEntry;
import odin.client.Skill;
import odin.client.MapleCharacter;
import odin.client.PlayerStats;
import odin.client.SkillEntry;
import odin.client.SkillFactory;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventory;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MapleMount;
import odin.client.inventory.MaplePet;
import odin.constants.GameConstants;
import odin.handling.world.family.MapleFamilyCharacter;
import odin.handling.world.guild.MapleGuildCharacter;
import odin.server.MapleItemInformationProvider;
import odin.server.maps.MapleMap;
import tacos.config.Config;
import tacos.config.Region;
import tacos.constants.TacosConstants;
import tacos.database.LazyData;
import tacos.database.InvTypeDB;
import tacos.database.query.DQ_Monsterbook;
import tacos.database.query.DQ_Buddies;
import tacos.database.query.DQ_Characters;
import tacos.database.query.DQ_Inventoryitems;
import tacos.database.query.DQ_Inventoryslot;
import tacos.database.query.DQ_KeyMap;
import tacos.database.query.DQ_Mountdata;
import tacos.debug.DebugLogger;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Calendar;
import java.util.List;
import odin.client.MapleQuestStatus;
import odin.client.SkillMacro;
import odin.handling.world.MapleParty;
import odin.server.CashShop;
import odin.server.MapleInventoryManipulator;
import odin.server.RandomRewards;
import odin.server.Randomizer;
import odin.server.maps.Event_PyramidSubway;
import odin.server.quest.MapleQuest;
import tacos.database.DatabaseConnection;
import tacos.database.DatabaseException;
import tacos.database.query.DQ_Accounts;
import tacos.database.query.DQ_Achievements;
import tacos.database.query.DQ_Famelog;
import tacos.database.query.DQ_Questinfo;
import tacos.database.query.DQ_Queststatus;
import tacos.database.query.DQ_Regrocklocations;
import tacos.database.query.DQ_Savedlocations;
import tacos.database.query.DQ_Skillmacros;
import tacos.database.query.DQ_Skills;
import tacos.database.query.DQ_Trocklocations;
import tacos.database.query.DQ_Wishlist;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsBodyPart;
import tacos.packet.ops.OpsMovePathAttr;
import tacos.packet.ops.OpsSkill;
import tacos.packet.ops.OpsTransferField;
import tacos.packet.request.ReqCUser;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.ResCClientSocket;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCStage;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.ResCUser_Dragon;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.response.builder.PB_BroadcastMsg;
import tacos.packet.ops.OpsFriend;
import tacos.packet.response.ResCTownPortalPool;
import tacos.packet.response.ResCUser;
import tacos.packet.response.ResCUser_Pet;
import tacos.packet.response.builder.PB_Friend;
import tacos.packet.response.builder.PB_InvOp;
import tacos.script.portal.ArdentmillPortal;
import tacos.script.portal.FreeMarketPortal;
import tacos.server.TacosChannel;
import tacos.server.TacosServer;
import tacos.server.TacosServerType;
import tacos.server.TacosWorld;
import tacos.server.map.TacosPortal;
import tacos.server.TacosTask;
import tacos.server.map.object.TacosMysticDoor;
import tacos.server.map.object.TacosPet;
import tacos.server.map.object.TacosPlayer;
import tacos.server.map.object.TacosSummon;
import tacos.shared.TacosSharedExpTable;
import tacos.unofficial.PetCharacter;
import tacos.unofficial.PetMob;
import tacos.unofficial.PetNPC;
import tacos.wz.ids.DWI_Dafault;
import tacos.wz.WzDataStorage;

/**
 *
 * @author Riremito
 */
public class TacosCharacter extends TacosPlayer {

    public boolean isFacingLeft() {
        return getMoveAction() % 2 != 0;
    }

    protected TacosClient client;
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
    private FreeMarketPortal portal_fm = new FreeMarketPortal();
    private ArdentmillPortal portal_ardentmill = new ArdentmillPortal();
    private ArrayList<LazyData> lazy_data_list = new ArrayList<>();

    public void SendPacket(ServerPacket packet) {
        this.client.SendPacket(packet);
    }

    public void sendMigrateCommand(TacosServer server) {
        // send next server ip and port.
        SendPacket(ResCClientSocket.MigrateCommand(server));
        // stop sending/receiving packets.
        this.client.closeSession();
    }

    public TacosClient getClient() {
        return this.client;
    }

    public void setClient(TacosClient client) {
        this.client = client;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
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

    public MapleMap findMap(int map_id) {
        return getChannelServer().findMap(map_id);
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

    // enter game server.
    public void sendSetField(boolean bCharacterData) {
        if (bCharacterData) {
            getCalcDamage().setSeed(getCalcDamage().random(), getCalcDamage().random(), getCalcDamage().random());
        }
        if (Config.GreaterOrEqual(Region.JMS, 302)) {
            SendPacket(ResCStage.SetField_JMS_302((MapleCharacter) this, 1, bCharacterData, 0));
            SendPacket(ResCStage.SetField_JMS_302((MapleCharacter) this, 2, bCharacterData, -1));
            return;
        }

        SendPacket(ResCStage.SetField((MapleCharacter) this, bCharacterData));
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
        setFootholdId(0); // foothold id is 0 while character is in the air.
        setMoveAction(OpsMovePathAttr.MPA_NORMAL.get()); // default state (?)
    }

    public void updateMapById(int map_id, int portal_id) {
        MapleMap map_to = findMap(map_id);

        if (map_to != null) {
            int forced_return_map_id = map_to.getForcedReturnId();
            if (forced_return_map_id != TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID) {
                map_to = map_to.getForcedReturnMap();
            }
        }
        if (map_to == null) {
            map_to = findMap(TacosConstants.DEFAULT_RETURN_MAP_ID); // return to default map.
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
                changeMapPortal(map_to, map_to.getPortal(0));
                getStat().setHp(getStat().getMaxHp());
                getStat().setMp(getStat().getMaxMp());
                sendStatChanged(true);
                return true;
            }
            // hack?
            return false;
        }
        // direct map change.
        map_to = findMap(map_id_to);
        changeMapPortal(map_to, map_to.getPortal(0));
        return true;
    }

    public boolean changeMap(int map_id) {
        MapleMap map_to = findMap(map_id);
        if (map_to != null) {
            TacosPortal portal_to = map_to.getPortal(0);
            if (portal_to != null) {
                changeMapPortal(map_to, portal_to);
                return true;
            }
        }
        SendPacket(ResCField.TransferFieldReqIgnored(OpsTransferField.TF_DISABLED_PORTAL));
        return false;
    }

    public boolean changeMapWithCoordinate(int map_id, int x, int y) {
        MapleMap map_to = findMap(map_id);
        if (map_to != null) {
            TacosPortal portal_to = map_to.findClosestSpawnpoint(new Point(x, y));
            if (portal_to != null) {
                changeMapPortal(map_to, portal_to);
                return true;
            }
        }
        SendPacket(ResCField.TransferFieldReqIgnored(OpsTransferField.TF_DISABLED_PORTAL));
        return false;
    }

    // unofficial usage.
    public void changeMapDynamicPortal(MapleMap to, Point pos) {
        changeMapPortal(to, null);
    }

    public void changeMapBanish(int mapid, String portal, String msg) {
        DebugMsg(msg);
        MapleMap map_to = findMap(mapid);
        changeMapPortal(map_to, map_to.getPortal(portal));
    }

    public boolean changeMapPortal(MapleMap map_to, TacosPortal portal_to) {
        if (map_to == null) {
            return false;
        }

        int map_id_prev = this.map.getId();
        boolean map_id_check = this.map.getId() == map_id_prev;

        if (map_id_check) {
            MapleMap map_from = this.map;
            map_from.userLeaveField((MapleCharacter) this);
            updateMap(map_to, (portal_to != null) ? portal_to : this.map.getPortal(0)); // for dynamic portal
            sendSetField(false);
            updatePets();
            updateSummons();
            map_to.userEnterField((MapleCharacter) this);
            map_to.linkedObjectEnterField(this);
            stats.relocHeal();
        }

        if (getPyramidSubway() != null) {
            if (getPyramidSubway() != null) {
                getPyramidSubway().onChangeMap((MapleCharacter) this, map_to.getId());
            }
        }

        // マップ移動時にDBへ反映する
        saveToDB(false);
        return true;
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
    protected int tama = 0;

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

    public void setJob(int job) {
        if (!WzDataStorage.JOB.check(job)) {
            DebugLogger.ErrorLog("Invalid job id : " + job);
            this.job = DWI_Dafault.JOB;
            return;
        }
        this.job = job;
        setSkillPet();
        setDragon();
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

    public int getTama() {
        return this.tama;
    }

    public void setTama(int tama) {
        this.tama = tama;
    }

    // inventory.
    protected MapleInventory[] inventory;

    private MapleInventory[] getInventorys() {
        return this.inventory;
    }

    public MapleInventory getInventory(MapleInventoryType type) {
        return this.inventory[type.ordinal()];
    }

    private ArrayList<Item> getAllItems() {
        ArrayList<Item> ret = new ArrayList<>();
        for (MapleInventory iv : getInventorys()) {
            ret.addAll(iv.list());
        }
        return ret;
    }

    public Runnable checkItemSlot(short item_slot, int item_id) {
        return checkItemSlot(item_slot, item_id, (short) 1);
    }

    private Runnable checkItemSlot(short item_slot, int item_id, short item_quantity) {
        MapleInventoryType type = GameConstants.getInventoryType(item_id);
        Item item_used = getInventory(type).getItem(item_slot);

        if (item_used == null) {
            return null;
        }
        if (item_used.getItemId() != item_id) {
            return null;
        }
        if (item_used.getQuantity() < item_quantity) {
            return null;
        }

        Runnable use_item = () -> useItemDone(type, item_used, item_quantity);
        return use_item;
    }

    private boolean useItemDone(MapleInventoryType type, Item item_used, short item_quantity) {
        boolean isRecharge = GameConstants.isRechargable(item_used.getItemId());

        getInventory(type).removeItem(item_used.getPosition(), item_quantity, isRecharge);

        if (item_used.getQuantity() == 0 && !isRecharge) {
            SendPacket(ResCWvsContext.InventoryOperation(true, PB_InvOp.builder().remove(type, item_used.getPosition()).build()));
        } else {
            SendPacket(ResCWvsContext.InventoryOperation(true, PB_InvOp.builder().update(type, (Item) item_used).build()));
        }

        return true;
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

    protected Map<Skill, SkillEntry> skills = new LinkedHashMap<>();

    public int getSkillLevel(OpsSkill ops) {
        Skill skill = SkillFactory.getSkill(ops.get());
        if (skill == null) {
            return 0;
        }
        SkillEntry ret = this.skills.get(skill);

        int skill_level = Math.min(skill.getMaxLevel(), ret.skillevel + (skill.isBeginnerSkill() ? 0 : stats.incAllskill));
        return skill_level;
    }

    public OpsSkill getFakeSkill() {
        if (0 < getSkillLevel(OpsSkill.NIGHTLORD_FAKE)) {
            return OpsSkill.NIGHTLORD_FAKE;
        }
        if (0 < getSkillLevel(OpsSkill.SHADOWER_FAKE)) {
            return OpsSkill.SHADOWER_FAKE;
        }
        return OpsSkill.UNKNOWN;
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

    public void equipChanged() {
        this.map.broadcastMessage(this, ResCUserRemote.UserAvatarModified(this, 1), false);
        getWorld().avatarMessenger(this);
        this.stats.recalcLocalStats();
    }

    protected int accountid;
    protected String name;
    protected int gmLevel;

    public int getAccountId() {
        return this.accountid;
    }

    public void setAccountId(int accountid) {
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

    // debug
    // 青文字
    public void DebugMsg(String text) {
        SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, PB_BroadcastMsg.builder().message(text).build()));
    }

    // 青文字 & アイテム表示
    public void DebugMsgItem(String text, int item_id) {
        SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, PB_BroadcastMsg.builder().message(text).item_id(item_id).build()));
    }

    // ピンク
    public void DebugMsg2(String text) {
        SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message(text).build()));
    }

    // 黄色
    public void DebugMsg3(String text) {
        SendPacket(ResCWvsContext.SetWeekEventMessage(text));
    }

    public void Notice(String text) {
        SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message(text).build()));
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
        SendPacket(ResCWvsContext.FriendResult(OpsFriend.FriendRes_IncMaxCount_Done, PB_Friend.builder().nFriendMax(capacity).build()));
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
        SendPacket(ResCWvsContext.FriendResult(OpsFriend.FriendRes_Notify, PB_Friend.builder().friend_id(ble.getCharacterId()).friend_channel(isOnline ? (ble.getChannel() - 1) : -1).build())); // from 0.
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

    // pet.
    private final ArrayList<MaplePet> pets = new ArrayList<>();

    public ArrayList<MaplePet> getPets() {
        return this.pets;
    }

    public MaplePet getPetByIndex(int index) {
        if (this.pets.size() <= index) {
            return null;
        }
        return this.pets.get(index);
    }

    public int getPetIndex(TacosPet pet) {
        for (int index = 0; index < this.pets.size(); index++) {
            if (this.pets.get(index).getObjectId() == pet.getObjectId()) {
                return index;
            }
        }
        return -1;
    }

    public void removePet(MaplePet pet) {
        this.map.removePet(pet);
        this.map.broadcastMessage(ResCUser_Pet.Deactivated(this, pet, ResCUser_Pet.DeActivatedMsg.PET_NO_MSG)); // index is used inside thisi packet.
        int index = getPetIndex(pet);
        if (index != -1) {
            this.pets.remove(index);
        }
    }

    public void addPet(MaplePet pet) {
        if (getPetByUniqueId(pet.getUniqueId()) != null) {
            return;
        }
        if (1 <= pets.size()) {
            // TODO : multi pets.
            return;
        }
        this.pets.add(pet);
        pet.reset(this);
        this.map.addPet(pet);
        this.map.broadcastMessage(ResCUser_Pet.Activated(this, pet));
    }

    public void updatePets() {
        for (TacosPet pet : getPets()) {
            pet.reset(this);
            pet.setObjectId(); // update to new object id.
            this.map.addPet(pet);
        }
    }

    public MaplePet getPetByUniqueId(long pet_uid) {
        for (MaplePet pet : this.pets) {
            if (pet.getUniqueId() == pet_uid) {
                return pet;
            }
        }
        return null;
    }

    // summon.
    // skill_id
    private final ArrayList<TacosSummon> summons = new ArrayList<>();

    public ArrayList<TacosSummon> getSummons() {
        return this.summons;
    }

    public int findSummonByOid(TacosSummon summon) {
        for (int index = 0; index < this.summons.size(); index++) {
            if (this.summons.get(index).getObjectId() == summon.getObjectId()) {
                return index;
            }
        }
        return -1;
    }

    public TacosSummon findSummonBySkill(int skill_id) {
        for (int index = 0; index < this.summons.size(); index++) {
            if (this.summons.get(index).getSkillID() == skill_id) {
                return this.summons.get(index);
            }
        }
        return null;
    }

    public void removeSummon(TacosSummon summon) {
        int index = findSummonByOid(summon);
        if (index != -1) {
            this.summons.remove(index);
        }
        this.map.removeSummon(summon);
    }

    public boolean addSummon(TacosSummonSkill tss) {
        TacosSummon summon = findSummonBySkill(tss.getId());
        if (summon != null) {
            removeSummon(summon); // remove this line to allow multiple summoning.
        }
        summon = new TacosSummon(this, tss);
        summon.reset(this);
        this.summons.add(summon);
        this.map.addSummon(summon);
        return true;
    }

    public void updateSummons() {
        for (TacosSummon summon : getSummons()) {
            summon.reset(this);
            summon.setObjectId(); // update to new object id.
            this.map.addSummon(summon);
        }
    }

    // mystic door.
    private TacosMysticDoor door_field = null;
    private TacosMysticDoor door_town = null;

    public TacosMysticDoor getDoorField() {
        return this.door_field;
    }

    public void setDoorField(TacosMysticDoor door) {
        this.door_field = door;
    }

    public TacosMysticDoor getDoorTown() {
        return this.door_town;
    }

    public void setDoorTown(TacosMysticDoor door) {
        this.door_town = door;
    }

    public boolean addDoor(TacosDoorSkill tds) {
        if (getFootholdId() == 0) {
            return false;
        }

        removeDoor();
        TacosMysticDoor door_field_ = new TacosMysticDoor(this.map, this, tds); // field door.
        door_field_.reset(this);
        MapleMap map_town = getChannelServer().findMap(this.map.getReturnMapId());
        if (map_town == null) {
            SendPacket(ResCTownPortalPool.TownPortal(null));
            DebugLogger.ErrorLog("addDoor : map_town is null.");
            return false;
        }
        door_field_.setTownPortal(this.map.getPortal(0));

        TacosPortal portal_town = door_field_.getFreePortal(map_town);
        if (portal_town == null) {
            SendPacket(ResCTownPortalPool.TownPortal(null));
            DebugLogger.ErrorLog("addDoor : portal_town is null.");
            return false;
        }
        door_field_.setTownPortal(portal_town);
        setDoorField(door_field_);
        this.map.addDoor(door_field_);
        door_field_.setState(1);
        SendPacket(ResCTownPortalPool.TownPortal(door_field_));

        TacosMysticDoor door_town_ = new TacosMysticDoor(map_town, this, tds);
        Point pos_door_town = new Point(portal_town.getPosition());
        pos_door_town.y -= 1;
        pos_door_town = map_town.calcPointBelow(pos_door_town);
        door_town_.setPosition(pos_door_town);
        door_town_.setTownPortal(portal_town);
        setDoorTown(door_town_);
        map_town.addDoor(door_town_);
        door_town_.setState(1);
        return true;
    }

    public void removeDoor() {
        if (getDoorField() != null) {
            getDoorField().getMap().removeDoor(getDoorField());
            setDoorField(null);
        }
        if (getDoorTown() != null) {
            getDoorTown().getMap().removeDoor(getDoorTown());
            setDoorTown(null);
        }
    }
    // skill pet.
    protected TacosSkillPet skill_pet = null;

    public TacosSkillPet getSkillPet() {
        return this.skill_pet;
    }

    public boolean setSkillPet() {
        if (this.skill_pet != null) {
            return false;
        }
        if (TacosConstants.is_kanna(getJob())) {
            this.skill_pet = new TacosSkillPet(this, TacosConstants.KANNA_SKILL_PET_ID);
            this.skill_pet.setObjectId();
            return true;
        }
        return false;
    }

    protected TacosDragon dragon = null;

    public TacosDragon getDragon() {
        return this.dragon;
    }

    public boolean setDragon() {
        if (this.dragon != null) {
            if (TacosConstants.is_evan(getJob(), true)) {
                this.dragon.setJobCode(this);
                this.map.broadcastMessage(ResCUser_Dragon.DragonEnterField(this.dragon));
                return true;
            }
            this.dragon = null;
            return false;
        }
        if (TacosConstants.is_evan(getJob(), true)) {
            this.dragon = new TacosDragon(this);
            return true;
        }
        return false;
    }

    // coconut
    private int coconutteam = 0;

    public int getCoconutTeam() {
        return this.coconutteam;
    }

    public void setCoconutTeam(int coconutteam) {
        this.coconutteam = coconutteam;
    }

    // fishing.
    private final TacosTask task_fishing = new TacosTask();
    private int fishing_rod_id = 0;
    private int fishing_chair_id = 0;
    private int fishing_hit_interval = 30000;

    public boolean fishingHit() {
        if (((MapleCharacter) this).getChair() != 3011000) {
            return false;
        }
        int bait_level = 0;
        // 高級餌を消費
        if (bait_level < 2 && ((MapleCharacter) this).haveItem(2300001, 1, false, true)) {
            bait_level = 2;
            MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, 2300001, 1, false, false);
        }
        // 餌を消費
        if (bait_level < 2 && ((MapleCharacter) this).haveItem(2300000, 1, false, true)) {
            bait_level = 1;
            MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, 2300000, 1, false, false);
        }
        // 釣り終了
        if (bait_level <= 0) {
            return false;
        }
        int randval = RandomRewards.getInstance().getFishingReward();

        switch (randval) {
            case 0 -> // Meso
            {
                int caught_meso = Randomizer.rand(bait_level * 10000, bait_level * 100000);
                ((MapleCharacter) this).gainMeso(caught_meso, true);
                SendPacket(ResCWvsContext.fishingUpdate((byte) 1, caught_meso));
            }
            case 1 -> // EXP
            {
                int required_exp = TacosSharedExpTable.getExpNeededForLevel(level);
                int caught_exp = Randomizer.rand(required_exp / ((3 - bait_level) * 100), required_exp / ((3 - bait_level) * 10));
                if (caught_exp == 0) {
                    caught_exp += 1;
                }
                ((MapleCharacter) this).gainExp(caught_exp, true, false, true);
                SendPacket(ResCWvsContext.fishingUpdate((byte) 2, caught_exp));
            }
            default -> {
                if (!WzDataStorage.ITEM.check(randval)) {
                    DebugMsg("Fishing : invalid reward, " + randval);
                    return false;
                }
                MapleInventoryManipulator.addById(client, randval, (short) 1);
                SendPacket(ResCWvsContext.fishingUpdate((byte) 0, randval));
            }
        }

        this.map.splitSendPacket(this, ResCUser.UserFishingSuccess(this));
        return true;
    }

    public boolean startFishing() {
        for (Item item : getInventory(MapleInventoryType.CASH).list()) {
            int item_id = item.getItemId();
            if (item_id == 0 && item_id == 5340000) {
                this.fishing_rod_id = item_id;
            }
            if (item_id == 5340001) {
                this.fishing_rod_id = item_id;
                break;
            }
        }

        this.fishing_hit_interval = (fishing_rod_id == 5340001) ? 10000 : 30000;
        this.fishing_chair_id = ((MapleCharacter) this).getChair();
        this.task_fishing.reset();
        return true;
    }

    public void stopFishing() {
        this.fishing_rod_id = 0;
        this.fishing_chair_id = 0;
    }

    // aran
    private int m_nCombo = 0;
    private long m_tLastSetCombo = 0;

    public boolean sendIncCombo() {
        long time = System.currentTimeMillis();
        if (this.m_tLastSetCombo == 0 || (this.m_tLastSetCombo + 3500) < time) {
            this.m_nCombo = 0;
        }
        this.m_tLastSetCombo = System.currentTimeMillis();
        this.m_nCombo++;

        SendPacket(ResCUserLocal.IncCombo(this));

        int combo_level = this.m_nCombo / 10;
        if (this.m_nCombo % 10 == 0 && 1 <= combo_level && combo_level <= 10) {
            if (combo_level <= ((MapleCharacter) this).getSkillLevel(OpsSkill.ARAN_COMBO_ABILITY.get())) {
                // buff.
            }
        }
        return true;
    }

    public int getCombo() {
        return this.m_nCombo;
    }

    // effect item.
    protected int nEffectItemID = 0;

    public int getActiveEffectItem() {
        return this.nEffectItemID;
    }

    public void setActiveEffectItem(int nEffectItemID) {
        this.nEffectItemID = nEffectItemID;
    }

    // follow system.
    private int m_dwDriverID = 0;
    private int m_dwPassenserID = 0; // typo?

    public int getDriver() {
        return this.m_dwDriverID;
    }

    public void setDriver(int m_dwDriverID) {
        this.m_dwDriverID = m_dwDriverID;
    }

    public int getPassenger() {
        return this.m_dwPassenserID;
    }

    public void setPassenger(int m_dwPassenserID) {
        this.m_dwPassenserID = m_dwPassenserID;
    }

    // RPS game.
    private int m_nCntStraightVictories = 0;

    public int getCntStraightVictories() {
        return this.m_nCntStraightVictories;
    }

    public void setCntStraightVictories(int m_nCntStraightVictories) {
        this.m_nCntStraightVictories = m_nCntStraightVictories;
    }

    // buff.
    private final TacosBuff buffs = new TacosBuff(this);

    public TacosBuff getBuff() {
        return this.buffs;
    }

    // cool time.
    private final TacosCoolTime skill_ct = new TacosCoolTime(this);

    public TacosCoolTime getCoolTime() {
        return this.skill_ct;
    }

    // rand
    private final TacosCalcDamage calc_damage = new TacosCalcDamage();

    public TacosCalcDamage getCalcDamage() {
        return this.calc_damage;
    }

    // critical
    private final TacosCriticalRate critical_rate = new TacosCriticalRate(this);

    public TacosCriticalRate getCriticalRate() {
        return this.critical_rate;
    }

    // maple gift.
    private final TacosMapleGift maple_gift = new TacosMapleGift();

    public TacosMapleGift getMapleGift() {
        return this.maple_gift;
    }

    // monster book.
    private final TacosMonsterBook monster_book = new TacosMonsterBook();

    public TacosMonsterBook getMonsterBook() {
        return this.monster_book;
    }

    // storage.
    protected TacosStorage storage = null;

    public TacosStorage getStorage() {
        if (this.storage == null) {
            this.storage = new TacosStorage(this);
            this.storage.load();
        }
        return this.storage;
    }

    // useful.
    public String getPlayerNameWithMedal() {
        Item equipped_medal = getInventory(MapleInventoryType.EQUIPPED).getItem(OpsBodyPart.BP_MEDAL.getSlot());
        if (equipped_medal == null) {
            return getName();
        }
        String medal_name = MapleItemInformationProvider.getInstance().getName(equipped_medal.getItemId());

        if (medal_name == null) {
            return getName();
        }
        int padding = medal_name.indexOf("の勲章");
        if (padding > 0) {
            medal_name = medal_name.substring(0, padding);
        }
        return "<" + medal_name + "> " + getName();
    }

    // unofficial.
    private PetCharacter pet_player = new PetCharacter(this);
    private PetMob pet_mob = new PetMob(this);
    private PetNPC pet_npc = new PetNPC(this);

    public PetCharacter getPetCharacter() {
        return this.pet_player;
    }

    public PetMob getPetMob() {
        return this.pet_mob;
    }

    public PetNPC getPetNPC() {
        return this.pet_npc;
    }

    public void movePetEx(ParseCMovePath move_path) {
        this.pet_player.move(move_path);
        this.pet_mob.move(move_path);
        this.pet_npc.move(move_path);
    }

    private TacosValue sp_used = new TacosValue();

    public TacosValue getSpUsed() {
        return this.sp_used;
    }

    // old code.
    public int getMapId() {
        if (this.map != null) {
            return this.map.getId();
        }
        return this.dwPosMap;
    }

    // script.
    public boolean EnterPointShop() {
        ReqCUser.OnUserMigrateToCashShopRequest(client, (MapleCharacter) this);
        return true;
    }

    // update task.
    private final TacosTask task_player = new TacosTask();

    public boolean update(long time_current) {
        // player update.
        if (!this.task_player.check(time_current, 3000)) {
            return false;
        }

        // skill cool time.
        getCoolTime().update(time_current);
        // buff.
        updateBuffs(time_current);
        // pet.
        for (MaplePet pet : new ArrayList<>(getPets())) {
            //SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.CASH, getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition())).build()));
        }
        // summon.
        for (TacosSummon summon : new ArrayList<>(getSummons())) {
            if ((summon.getTimeCreated() + summon.getTime()) <= time_current) {
                removeSummon(summon);
            }
        }
        // door.
        if (getDoorField() != null) {
            if (getDoorField().getTimeCreated() + getDoorField().getTime() <= time_current) {
                removeDoor();
            }
        }
        // fishing.
        if (this.task_fishing.check(time_current, this.fishing_hit_interval)) {
            fishingHit();
        }
        return true;
    }

    public void updateBuffs(long time_current) {
        for (TacosBuff.Buff buff : getBuff().getCTSTimeout(time_current)) {
            SendPacket(ResCWvsContext.TemporaryStatReset(this, buff.buff_id));
        }
        getBuff().removeTimeout(time_current);
    }

    // not checked yet.
    private int fairyExp = 0;

    public int getFairyExp() {
        return this.fairyExp;
    }

    public void setFairyExp(int rate) {
        this.fairyExp = rate;
    }

    private Event_PyramidSubway pyramidSubway = null;

    public Event_PyramidSubway getPyramidSubway() {
        return this.pyramidSubway;
    }

    public void setPyramidSubway(Event_PyramidSubway ps) {
        this.pyramidSubway = ps;
    }

    // database.
    // database.
    public boolean saveNewCharToDB() {
        if (!addNewCharacterData()) {
            return false;
        }
        if (!DQ_Queststatus.add((MapleCharacter) this)) {
            return false;
        }
        return true;
    }

    public boolean addNewCharacterData() {
        if (!DQ_Characters.add(this)) {
            return false;
        }
        if (!DQ_Inventoryslot.add(this)) {
            return false;
        }
        if (!DQ_Inventoryitems.add(InvTypeDB.Inventory, this.id, getAllItems())) {
            return false;
        }
        if (!DQ_Mountdata.add(this)) {
            return false;
        }
        if (!DQ_KeyMap.add(this)) {
            return false;
        }
        return true;
    }

    public boolean loadCharacterData(boolean is_channel_server) {
        // all server.
        // login server.
        if (!is_channel_server) {
            // avatar look.
            for (SimpleImmutableEntry<Item, MapleInventoryType> mit : DQ_Inventoryitems.load(InvTypeDB.Inventory, this.id, true).values()) {
                if (mit.getValue() == MapleInventoryType.EQUIPPED) {
                    getInventory(MapleInventoryType.EQUIPPED).addFromDB(mit.getKey());
                }
            }
            return true;
        }
        // channel server.
        // inventory.
        DQ_Inventoryslot.load(this);
        for (SimpleImmutableEntry<Item, MapleInventoryType> mit : DQ_Inventoryitems.load(InvTypeDB.Inventory, this.id).values()) {
            if (!WzDataStorage.ITEM.check(mit.getKey().getItemId())) {
                DebugLogger.ErrorLog("Invalid item id : " + mit.getKey().getItemId());
                continue;
            }
            getInventory(mit.getValue()).addFromDB(mit.getKey());
        }
        DQ_KeyMap.loadKeyMap(this);
        DQ_Monsterbook.load(this);
        for (BuddylistEntry ble : DQ_Buddies.load(this)) {
            this.buddylist.put(ble);
        }
        return true;
    }

    public boolean saveCharacterData(boolean is_channel_server) {
        // all server.
        DQ_Inventoryslot.save(this);
        DQ_Inventoryitems.add(InvTypeDB.Inventory, this.id, getAllItems());
        // cash shop or itc server.
        if (!is_channel_server) {
            return true;
        }
        // channel server.
        if (storage != null) {
            storage.update();
        }

        DQ_KeyMap.saveKeys(this);
        DQ_Monsterbook.save(this);
        DQ_Buddies.removePending(this);
        DQ_Buddies.update(this);
        return true;
    }

    protected int hpApUsed;
    protected MapleParty party;
    protected int dojo;
    protected byte dojoRecord;
    protected SkillMacro[] skillMacros = new SkillMacro[5];
    protected Map<Integer, String> questinfo;
    protected Map<MapleQuest, MapleQuestStatus> quests;
    protected int[] savedLocations;
    protected int nexonPoint;
    protected int maplePoint;
    protected int points;
    protected int vpoints;
    protected int[] wishlist;
    protected int[] rocks;
    protected int[] regrocks;
    protected CashShop cs;
    protected byte[] petStore;
    protected String BlessOfFairy_Origin;
    protected long lastfametime;
    protected List<Integer> lastmonthfameids;

    public int getWishlistSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (wishlist[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public static MapleCharacter loadCharFromDB(int character_id, TacosClient client, boolean channelserver) {
        MapleCharacter ret = new MapleCharacter();
        ret.init_step1();

        if (channelserver) {
            ret.init_step2();
        }

        ret.client = client;
        ret.id = character_id;
        ret.loadCharacterData(channelserver);

        try {
            DQ_Characters.loadStat(ret);
            DQ_Characters.ExtrasRow extras = DQ_Characters.loadExtras(character_id);

            if (channelserver) {
                ret.updateMapById(ret.dwPosMap, ret.nPortal);

                int partyid = extras.party;
                if (partyid >= 0) {
                    MapleParty party = client.getWorld().getParty().getParty(partyid);
                    if (party != null && party.getMemberById(ret.id) != null) {
                        ret.party = party;
                    }
                }

                int cover = extras.monsterbookcover;
                ret.getMonsterBook().setCover(cover);

                ret.dojo = extras.dojo;
                ret.dojoRecord = extras.dojoRecord;
                final String[] pets = extras.pets.split(",");
                for (int i = 0; i < ret.petStore.length; i++) {
                    ret.petStore[i] = Byte.parseByte(pets[i]);
                }
            }

            boolean compensate_previousEvans = false;
            ret.quests.putAll(DQ_Queststatus.loadAll(character_id));
            for (final MapleQuestStatus loadedStatus : ret.quests.values()) {
                if (loadedStatus.getQuest().getId() == 170000) {
                    compensate_previousEvans = true;
                    break;
                }
            }

            if (channelserver) {
                DQ_Accounts.AccountLoginRow acc = DQ_Accounts.loadForCharacterLogin(ret.accountid);
                if (acc != null) {
                    ret.getClient().setMapleId(acc.name);
                    ret.nexonPoint = acc.acash;
                    ret.maplePoint = acc.mpoints;
                    ret.points = acc.points;
                    ret.vpoints = acc.vpoints;

                    if (acc.lastlogon != null) {
                        final Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(acc.lastlogon.getTime());
                        if (cal.get(Calendar.DAY_OF_WEEK) + 1 == Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                            ret.nexonPoint += 500;
                        }
                    }

                    DQ_Accounts.updateLastLogon(ret.accountid);
                }

                ret.questinfo.putAll(DQ_Questinfo.loadAll(character_id));

                Skill skil;
                for (DQ_Skills.SkillRow row : DQ_Skills.loadAll(character_id)) {
                    skil = SkillFactory.getSkill(row.skillId);
                    if (skil != null && GameConstants.isApplicableSkill(row.skillId)) {
                        ret.skills.put(skil, new SkillEntry(row.skillLevel, row.masterLevel, row.expiration));
                    } else if (skil == null) { //doesnt. exist. e.g. bb
                        ret.remainingSp[GameConstants.getSkillBookForSkill(row.skillId)] += row.skillLevel;
                    }
                }

                // Bless of Fairy handling
                byte maxlevel_ = 0;
                for (DQ_Characters.BlessOfFairyRow row : DQ_Characters.loadOtherCharactersForBlessOfFairy(ret.accountid)) {
                    if (row.id != character_id) { // Not this character
                        byte maxlevel = (byte) (row.level / 10);

                        if (maxlevel > 20) {
                            maxlevel = 20;
                        }
                        if (maxlevel > maxlevel_) {
                            maxlevel_ = maxlevel;
                            ret.BlessOfFairy_Origin = row.name;
                        }

                    } else if (character_id < 17000 && !compensate_previousEvans && ret.job >= 2200 && ret.job <= 2218) { //compensate, watch max charid
                        for (int i = 0; i <= GameConstants.getSkillBook(ret.job); i++) {
                            ret.remainingSp[i] += 2; //2 that they missed. gg
                        }
                        ret.setQuestAdd(MapleQuest.getInstance(170000), (byte) 0, null); //set it so never again
                    }
                }

                // 精霊の祝福
                final Skill bofskill = SkillFactory.getSkill(GameConstants.getBOF_ForJob(ret.job));

                if (bofskill != null) {
                    ret.skills.put(bofskill, new SkillEntry(maxlevel_, (byte) 0, -1));
                }
                // END

                ret.skillMacros = DQ_Skillmacros.loadAll(character_id);

                for (final Map.Entry<Integer, Integer> e : DQ_Savedlocations.loadAll(character_id).entrySet()) {
                    ret.savedLocations[e.getKey()] = e.getValue();
                }

                final DQ_Famelog.RecentFame recentFame = DQ_Famelog.loadRecent(character_id);
                ret.lastfametime = recentFame.lastFameTime;
                ret.lastmonthfameids = recentFame.lastMonthFameIds;

                ret.cs = new CashShop(ret.accountid, character_id, ret.getJob());

                int i = 0;
                for (final int sn : DQ_Wishlist.loadAll(character_id)) {
                    ret.wishlist[i] = sn;
                    i++;
                }
                while (i < 10) {
                    ret.wishlist[i] = 0;
                    i++;
                }

                int r = 0;
                for (final int mapid : DQ_Trocklocations.loadAll(character_id)) {
                    ret.rocks[r] = mapid;
                    r++;
                }
                while (r < 10) {
                    ret.rocks[r] = 999999999;
                    r++;
                }

                r = 0;
                for (final int mapid : DQ_Regrocklocations.loadAll(character_id)) {
                    ret.regrocks[r] = mapid;
                    r++;
                }
                while (r < 5) {
                    ret.regrocks[r] = 999999999;
                    r++;
                }

                final DQ_Mountdata.Row mountRow = DQ_Mountdata.load(character_id);
                final Item mount = ret.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18/*-22*/);
                ret.mount = new MapleMount(ret, mount != null ? mount.getItemId() : 0, ret.job > 1000 && ret.job < 2000 ? 10001004 : (ret.job >= 2000 ? (ret.job == 2001 || ret.job >= 2200 ? 20011004 : (ret.job >= 3000 ? 30001004 : 20001004)) : 1004), mountRow.fatigue, mountRow.level, mountRow.exp);

                ret.stats.recalcLocalStats(true);
            }
        } catch (SQLException ess) {
            ess.printStackTrace();
            System.out.println("Failed to load character..");
        }
        return ret;
    }

    public void saveToDB(boolean fromcs) {
        saveCharacterData(!fromcs);

        Connection con = DatabaseConnection.getConnection();
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);

            final StringBuilder sps = new StringBuilder();
            for (int i = 0; i < remainingSp.length; i++) {
                sps.append(remainingSp[i]);
                sps.append(",");
            }
            final String sp = sps.toString();

            final int mapToSave;
            if (!fromcs && map != null) {
                if (map.getForcedReturnId() != 999999999) {
                    mapToSave = map.getForcedReturnId();
                } else {
                    mapToSave = stats.getHp() < 1 ? map.getReturnMapId() : map.getId();
                }
            } else {
                mapToSave = dwPosMap;
            }

            final byte spawnpointToSave;
            if (map == null) {
                spawnpointToSave = (byte) 0;
            } else {
                final TacosPortal closest = map.findClosestSpawnpoint(getPosition());
                spawnpointToSave = (byte) (closest != null ? closest.getId() : 0);
            }

            int[] pet_slots = {-1, -1, -1};
            int pet_index = 0;

            for (MaplePet pet : getPets()) {
                pet.saveToDb();
                pet_slots[pet_index++] = pet.getInventoryPosition();
                if (3 <= pet_index) {
                    break;
                }
            }

            String petstring = String.format("%d,%d,%d", pet_slots[0], pet_slots[1], pet_slots[2]);

            DQ_Characters.CharacterSaveRow saveRow = new DQ_Characters.CharacterSaveRow(id, level, fame,
                    stats.getStr(), stats.getDex(), stats.getLuk(), stats.getInt(), exp,
                    stats.getHp() < 1 ? 50 : stats.getHp(), stats.getMp(), stats.getMaxHp(), stats.getMaxMp(),
                    sp.substring(0, sp.length() - 1), remainingAp, (byte) gmLevel, (byte) skinColor, (byte) gender,
                    job, hair, face, mapToSave, meso, hpApUsed, spawnpointToSave, party != null ? party.getId() : -1,
                    (short) (byte) buddylist.getCapacity(), getMonsterBook().getCover(), dojo, dojoRecord,
                    petstring, subcategory, marriageId, currentrep, totalrep,
                    name, tama);

            if (!DQ_Characters.updateStat(con, saveRow)) {
                throw new DatabaseException("Character not in database (" + id + ")");
            }

            DQ_Skillmacros.deleteAndSaveAll(con, id, skillMacros);
            DQ_Questinfo.deleteAndSaveAll(con, id, questinfo);
            DQ_Queststatus.deleteAndSaveAll(con, id, quests.values());
            DQ_Skills.deleteAndSaveAll(con, id, skills);
            DQ_Savedlocations.deleteAndSaveAll(con, id, savedLocations);
            DQ_Achievements.deleteByAccountId(con, accountid);
            DQ_Accounts.updatePoints(con, client.getId(), nexonPoint, maplePoint, points, vpoints);

            if (cs != null) {
                cs.save();
            }
            mount.saveMount(id);

            DQ_Wishlist.deleteAndSaveAll(con, id, wishlist, getWishlistSize());
            DQ_Trocklocations.deleteAndSaveAll(con, id, rocks);
            DQ_Regrocklocations.deleteAndSaveAll(con, id, regrocks);

            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
            DebugLogger.ExceptionLog("saveToDB : saving character data.");
            try {
                con.rollback();
            } catch (SQLException ex) {
                DebugLogger.ExceptionLog("saveToDB : Rolling Back.");
            }
        } finally {
            try {
                con.setAutoCommit(true);
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (SQLException e) {
                DebugLogger.ExceptionLog("saveToDB : going back to autocommit mode.");
            }
        }
    }
}
