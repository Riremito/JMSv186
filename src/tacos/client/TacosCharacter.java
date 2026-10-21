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
import tacos.wz.WzXML;
import tacos.server.map.object.TacosMerchant;
import tacos.server.map.TacosMap;
import tacos.server.map.object.TacosSkillPet;
import java.awt.Point;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import tacos.client.TacosCharacter.BuddyList.BuddylistEntry;
import odin.client.Skill;
import odin.client.Skill.SkillEntry;
import odin.client.inventory.Item;
import odin.client.inventory.Equip;
import odin.client.inventory.MapleInventory;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.handling.world.family.MapleFamilyCharacter;
import odin.handling.world.guild.MapleGuildCharacter;
import odin.server.MapleItemInformationProvider;
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
import java.util.TreeMap;
import odin.client.MapleQuestStatus;
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
import tacos.packet.ops.OpsSkill_BB;
import tacos.packet.ops.OpsTransferField;
import tacos.packet.request.ReqCUser;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.ResCClientSocket;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCStage;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.data.RD_CUser;
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
import odin.client.inventory.MapleRing;
import java.sql.PreparedStatement;
import java.util.Deque;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import odin.handling.world.MapleParty.MaplePartyCharacter;
import odin.handling.world.MapleParty.PartyOperation;
import odin.handling.world.family.MapleFamily;
import odin.handling.world.family.MapleFamilyBuff;
import odin.handling.world.family.MapleFamilyBuff.MapleFamilyBuffEntry;
import odin.handling.world.guild.MapleGuild;
import tacos.packet.ops.OpsQuest;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.Res_JMS_CField_Pachinko;
import tacos.packet.response.ResCField_MonsterCarnival;
import tacos.packet.response.ResCScriptMan;
import tacos.packet.ops.OpsMessage;
import tacos.packet.ops.OpsDropPickUpMessage;
import tacos.packet.ops.OpsQuestRecordMessage;
import tacos.packet.response.builder.PB_Message;
import odin.server.MapleShop;
import odin.server.MapleStatEffect;
import odin.server.MapleTrade;
import odin.server.MapleCarnivalParty;
import odin.server.maps.SavedLocationType;
import odin.server.shops.ShopDispatch;
import odin.server.MapleCarnivalChallenge;
import tacos.network.MockIOSession;
import tacos.database.query.DQ_Notes;
import tacos.debug.DebugShop;
import tacos.debug.IDebugMan;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import tacos.packet.response.builder.PB_UserEffect;
import tacos.script.TacosScriptNPC;
import tacos.script.TacosScriptQuest;
import tacos.shared.TacosSharedDate;
import tacos.wz.opt.FieldOpt;

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
    protected TacosMap map;
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
        // the client has no world on the login server (character list).
        TacosWorld world = (this.client != null) ? this.client.getWorld() : null;
        return (world != null) ? world : TacosWorld.find(this.world_id);
    }

    public TacosServerType getServerType() {
        return this.client.getServer().getType();
    }

    public TacosChannel getChannelServer() {
        return this.client.getChannelServer();
    }

    public TacosMap findMap(int map_id) {
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
            SendPacket(ResCStage.SetField_JMS302((TacosCharacter) this, 1, bCharacterData, 0));
            SendPacket(ResCStage.SetField_JMS302((TacosCharacter) this, 2, bCharacterData, -1));
            return;
        }

        SendPacket(ResCStage.SetField((TacosCharacter) this, bCharacterData));
    }

    public TacosMap getMap() {
        return this.map;
    }

    public void setMap(TacosMap map) {
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

    public void updateMap(TacosMap map_to, TacosPortal portal_to) {
        setMap(map_to);
        setPosMap(map_to.getId());
        setPortal(portal_to.getId()); // spawn point
        setPosition(portal_to.getPosition()); // spawn point xy (server side), some version could not control spawn xy by packet.
        setFootholdId(0); // foothold id is 0 while character is in the air.
        setMoveAction(OpsMovePathAttr.MPA_NORMAL.get()); // default state (?)
    }

    public void updateMapById(int map_id, int portal_id) {
        TacosMap map_to = findMap(map_id);

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
            if (!portal.enterPortal(this)) {
                return false;
            }
            return true;
        }
        if (map_id_to == 0) {
            if (!isAlive()) {

            }
        }
        TacosMap map_to = null;
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

    public boolean changeMapById(int map_id) {
        if (WzDataStorage.MAP.check(map_id)) {
            TacosMap map_to = findMap(map_id);
            if (map_to != null) {
                TacosPortal portal_to = map_to.getPortal(0);
                if (portal_to != null) {
                    changeMapPortal(map_to, portal_to);
                    return true;
                }
            }
        }
        SendPacket(ResCField.TransferFieldReqIgnored(OpsTransferField.TF_DISABLED_PORTAL));
        return false;
    }

    public boolean changeMapWithCoordinate(int map_id, int x, int y) {
        TacosMap map_to = findMap(map_id);
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
    public void changeMapDynamicPortal(TacosMap to, Point pos) {
        changeMapPortal(to, null);
    }

    public void changeMapBanish(int mapid, String portal, String msg) {
        DebugMsg(msg);
        TacosMap map_to = findMap(mapid);
        changeMapPortal(map_to, map_to.getPortal(portal));
    }

    public boolean changeMapPortal(TacosMap map_to, TacosPortal portal_to) {
        if (map_to == null) {
            return false;
        }

        int map_id_prev = this.map.getId();
        boolean map_id_check = this.map.getId() == map_id_prev;

        if (map_id_check) {
            TacosMap map_from = this.map;
            map_from.userLeaveField((TacosCharacter) this);
            updateMap(map_to, (portal_to != null) ? portal_to : this.map.getPortal(0)); // for dynamic portal
            sendSetField(false);
            map_to.userEnterField((TacosCharacter) this);
        }

        if (getPyramidSubway() != null) {
            if (getPyramidSubway() != null) {
                getPyramidSubway().onChangeMap((TacosCharacter) this, map_to.getId());
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

    public int getSkillLevel(OpsSkill_BB ops) {
        Skill skill = WzXML.SKILL.getSkill().get(ops.get());
        if (skill == null) {
            return 0;
        }
        SkillEntry ret = this.skills.get(skill);

        int skill_level = Math.min(skill.getMaxLevel(), ret.skillevel);
        return skill_level;
    }

    public OpsSkill_BB getFakeSkill() {
        if (0 < getSkillLevel(OpsSkill_BB.NIGHTLORD_FAKE)) {
            return OpsSkill_BB.NIGHTLORD_FAKE;
        }
        if (0 < getSkillLevel(OpsSkill_BB.SHADOWER_FAKE)) {
            return OpsSkill_BB.SHADOWER_FAKE;
        }
        return OpsSkill_BB.UNKNOWN;
    }

    // taming mob (monster riding).
    public static class MapleMount {

        private int itemid;
        private int exp;
        private int fatigue;
        private int level;
        private boolean changed = false;

        public MapleMount(int itemid, int fatigue, int level, int exp) {
            this.itemid = itemid;
            this.fatigue = fatigue;
            this.level = level;
            this.exp = exp;
        }

        public void saveMount(int charid) {
            if (changed) {
                DQ_Mountdata.update(this, charid);
            }
        }

        public int getItemId() {
            return itemid;
        }

        public int getFatigue() {
            return fatigue;
        }

        public int getExp() {
            return exp;
        }

        public int getLevel() {
            return level;
        }

        public void addFatigue(int amount) {
            changed = true;
            fatigue = Math.max(0, fatigue + amount);
        }

        public void setLevel(int level) {
            changed = true;
            this.level = level;
        }

        public void increaseExp() {
            int gain;
            if (1 <= level && level <= 7) {
                gain = Randomizer.nextInt(10) + 15;
            } else if (8 <= level && level <= 15) {
                gain = Randomizer.nextInt(13) + 7;
            } else if (16 <= level && level <= 24) {
                gain = Randomizer.nextInt(23) + 9;
            } else {
                gain = Randomizer.nextInt(28) + 12;
            }
            changed = true;
            exp += gain;
        }
    }

    protected MapleMount mount = null;

    public MapleMount getMount() {
        return this.mount;
    }

    public boolean setMount() {
        this.mount = new MapleMount(0, 0, 1, 0);
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
        this.map.splitSendPacket(this, ResCUserRemote.UserAvatarModified(this, 1), getId());
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
        Notice(text);
    }

    // 黄色
    public void DebugMsg3(String text) {
        SendPacket(ResCWvsContext.SetWeekEventMessage(text));
    }

    public void Notice(String text) {
        SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message(text).build()));
    }

    // alert dialog
    public void Alert(String text) {
        SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_ALERT, PB_BroadcastMsg.builder().message(text).build()));
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

    // friend list.
    public static class BuddyList {

        private final Map<Integer, BuddylistEntry> buddies = new LinkedHashMap<>();
        private int capacity;

        public BuddyList(int capacity) {
            this.capacity = capacity;
        }

        public boolean containsVisible(int characterId) {
            BuddylistEntry ble = buddies.get(characterId);
            return ble != null && ble.isVisible();
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public BuddylistEntry get(int characterId) {
            return buddies.get(characterId);
        }

        public BuddylistEntry get(String characterName) {
            for (BuddylistEntry ble : buddies.values()) {
                if (ble.getName().equalsIgnoreCase(characterName)) {
                    return ble;
                }
            }
            return null;
        }

        public void put(BuddylistEntry entry) {
            buddies.put(entry.getCharacterId(), entry);
        }

        public void remove(int characterId) {
            buddies.remove(characterId);
        }

        public Collection<BuddylistEntry> getBuddies() {
            return buddies.values();
        }

        public boolean isFull() {
            return buddies.size() >= capacity;
        }

        public int[] getBuddyIds() {
            return buddies.keySet().stream().mapToInt(Integer::intValue).toArray();
        }

        public static class BuddylistEntry {

            private final String name;
            private final int cid;
            private final int level;
            private final int job;
            private String group;
            private int channel;
            private boolean visible;
            private boolean hidden = false;

            public BuddylistEntry(String name, int characterId, String group, int channel, boolean visible, int level, int job) {
                this.name = name;
                this.cid = characterId;
                this.group = group;
                this.channel = channel;
                this.visible = visible;
                this.level = level;
                this.job = job;
            }

            public boolean getHidden() {
                return hidden;
            }

            public void setHidden(boolean hidden) {
                this.hidden = hidden;
            }

            public int getChannel() {
                return channel;
            }

            public void setChannel(int channel) {
                this.channel = channel;
            }

            public String getName() {
                return name;
            }

            public int getCharacterId() {
                return cid;
            }

            public void setVisible(boolean visible) {
                this.visible = visible;
            }

            public boolean isVisible() {
                return visible;
            }

            public String getGroup() {
                return group;
            }

            public void setGroup(String group) {
                this.group = group;
            }

            public int getLevel() {
                return level;
            }

            public int getJob() {
                return job;
            }
        }
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
        }
    }

    // pet.
    private final ArrayList<TacosPet> pets = new ArrayList<>();

    public ArrayList<TacosPet> getPets() {
        return this.pets;
    }

    public TacosPet getPetByIndex(int index) {
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

    public void removePet(TacosPet pet) {
        this.map.removePet(pet);
        this.map.broadcastPacket(ResCUser_Pet.Deactivated(this, pet, ResCUser_Pet.DeActivatedMsg.PET_NO_MSG)); // index is used inside thisi packet.
        int index = getPetIndex(pet);
        if (index != -1) {
            this.pets.remove(index);
        }
    }

    public void addPet(TacosPet pet) {
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
        this.map.broadcastPacket(ResCUser_Pet.Activated(this, pet));
    }

    public void updatePets() {
        for (TacosPet pet : getPets()) {
            pet.reset(this);
            pet.setObjectId(); // update to new object id.
            this.map.addPet(pet);
        }
    }

    public TacosPet getPetByUniqueId(long pet_uid) {
        for (TacosPet pet : this.pets) {
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
        TacosMap map_town = getChannelServer().findMap(this.map.getReturnMapId());
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

    public void updateSkillPet() {
        if (this.skill_pet != null) {
            this.skill_pet.reset(this);
            this.skill_pet.setObjectId();
        }
    }

    protected TacosDragon dragon = null;

    public TacosDragon getDragon() {
        return this.dragon;
    }

    public boolean setDragon() {
        if (this.dragon != null) {
            if (TacosConstants.is_evan(getJob(), true)) {
                this.dragon.setJobCode(this);
                this.map.broadcastPacket(ResCUser_Dragon.DragonEnterField(this.dragon));
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

    public void updateDragon() {
        if (this.dragon != null) {
            this.dragon.reset(this);
            this.dragon.setObjectId();
        }
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
        if (((TacosCharacter) this).getChair() != 3011000) {
            return false;
        }
        int bait_level = 0;
        // 高級餌を消費
        if (bait_level < 2 && ((TacosCharacter) this).haveItem(2300001, 1, false, true)) {
            bait_level = 2;
            MapleInventoryManipulator.removeById(client.getPlayer(), MapleInventoryType.USE, 2300001, 1, false, false);
        }
        // 餌を消費
        if (bait_level < 2 && ((TacosCharacter) this).haveItem(2300000, 1, false, true)) {
            bait_level = 1;
            MapleInventoryManipulator.removeById(client.getPlayer(), MapleInventoryType.USE, 2300000, 1, false, false);
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
                ((TacosCharacter) this).gainMeso(caught_meso, true);
                SendPacket(ResCWvsContext.fishingUpdate((byte) 1, caught_meso));
            }
            case 1 -> // EXP
            {
                int required_exp = TacosSharedExpTable.getExpNeededForLevel(level);
                int caught_exp = Randomizer.rand(required_exp / ((3 - bait_level) * 100), required_exp / ((3 - bait_level) * 10));
                if (caught_exp == 0) {
                    caught_exp += 1;
                }
                ((TacosCharacter) this).gainExp(caught_exp, true, false, true);
                SendPacket(ResCWvsContext.fishingUpdate((byte) 2, caught_exp));
            }
            default -> {
                if (!WzDataStorage.ITEM.check(randval)) {
                    DebugMsg("Fishing : invalid reward, " + randval);
                    return false;
                }
                MapleInventoryManipulator.addById(client.getPlayer(), randval, (short) 1);
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
        this.fishing_chair_id = ((TacosCharacter) this).getChair();
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
            if (combo_level <= ((TacosCharacter) this).getSkillLevel(OpsSkill_BB.ARAN_COMBO_ABILITY.get())) {
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

    // macro.
    private final TreeMap<Integer, TacosSingleMacro> macros = new TreeMap<>();

    public TreeMap<Integer, TacosSingleMacro> getMacros() {
        return this.macros;
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

    private int conv_state = 0;

    public int getConversation() {
        return this.conv_state;
    }

    public void setConversation(int conv_state) {
        this.conv_state = conv_state;
    }

    // script.
    public boolean EnterPointShop() {
        ReqCUser.OnUserMigrateToCashShopRequest(client, (TacosCharacter) this);
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
        for (TacosPet pet : new ArrayList<>(getPets())) {
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

    // show buffs to other users.
    public void sendRemoteTemporaryStatSet(int buff_id) {
        // SecondaryStat::DecodeForRemote is not coded.
        if (Config.GreaterOrEqual(Region.JMS, 302)) {
            return;
        }
        if (RD_CUser.getRemoteBuffs(getBuff().getCTS(buff_id)).isEmpty()) {
            return;
        }
        getMap().splitSendPacket(this, ResCUserRemote.UserTemporaryStatSet(this, buff_id), getId());
    }

    public void sendRemoteTemporaryStatReset(int buff_id) {
        if (Config.GreaterOrEqual(Region.JMS, 302)) {
            return;
        }
        if (RD_CUser.getRemoteBuffs(getBuff().getCTS(buff_id)).isEmpty()) {
            return;
        }
        getMap().splitSendPacket(this, ResCUserRemote.UserTemporaryStatReset(this, buff_id), getId());
    }

    public void updateBuffs(long time_current) {
        for (TacosBuff.Buff buff : getBuff().getCTSTimeout(time_current)) {
            SendPacket(ResCWvsContext.TemporaryStatReset(this, buff.buff_id));
            sendRemoteTemporaryStatReset(buff.buff_id);
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
    public boolean saveNewCharToDB() {
        if (!addNewCharacterData()) {
            return false;
        }
        if (!DQ_Queststatus.add((TacosCharacter) this)) {
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

    public static TacosCharacter loadCharFromDB(int character_id, TacosClient client, boolean channelserver) {
        TacosCharacter ret = new TacosCharacter();
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
                String[] pets = extras.pets.split(",");
                for (int i = 0; i < ret.petStore.length; i++) {
                    ret.petStore[i] = Byte.parseByte(pets[i]);
                }
            }

            boolean compensate_previousEvans = false;
            ret.quests.putAll(DQ_Queststatus.loadAll(character_id));
            for (MapleQuestStatus loadedStatus : ret.quests.values()) {
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
                        Calendar cal = Calendar.getInstance();
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
                    skil = WzXML.SKILL.getSkill().get(row.skillId);
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
                Skill bofskill = WzXML.SKILL.getSkill().get(GameConstants.getBOF_ForJob(ret.job));

                if (bofskill != null) {
                    ret.skills.put(bofskill, new SkillEntry(maxlevel_, (byte) 0, -1));
                }
                // END

                DQ_Skillmacros.loadAll(ret);

                for (Map.Entry<Integer, Integer> e : DQ_Savedlocations.loadAll(character_id).entrySet()) {
                    ret.savedLocations[e.getKey()] = e.getValue();
                }

                DQ_Famelog.RecentFame recentFame = DQ_Famelog.loadRecent(character_id);
                ret.lastfametime = recentFame.lastFameTime;
                ret.lastmonthfameids = recentFame.lastMonthFameIds;

                ret.cs = new CashShop(ret.accountid, character_id, ret.getJob());

                int i = 0;
                for (int sn : DQ_Wishlist.loadAll(character_id)) {
                    ret.wishlist[i] = sn;
                    i++;
                }
                while (i < 10) {
                    ret.wishlist[i] = 0;
                    i++;
                }

                int r = 0;
                for (int mapid : DQ_Trocklocations.loadAll(character_id)) {
                    ret.rocks[r] = mapid;
                    r++;
                }
                while (r < 10) {
                    ret.rocks[r] = 999999999;
                    r++;
                }

                r = 0;
                for (int mapid : DQ_Regrocklocations.loadAll(character_id)) {
                    ret.regrocks[r] = mapid;
                    r++;
                }
                while (r < 5) {
                    ret.regrocks[r] = 999999999;
                    r++;
                }

                DQ_Mountdata.Row mountRow = DQ_Mountdata.load(character_id);
                Item mount = ret.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18/*-22*/);
                ret.mount = new MapleMount(mount != null ? mount.getItemId() : 0, mountRow.fatigue, mountRow.level, mountRow.exp);

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

            StringBuilder sps = new StringBuilder();
            for (int i = 0; i < remainingSp.length; i++) {
                sps.append(remainingSp[i]);
                sps.append(",");
            }
            String sp = sps.toString();

            int mapToSave;
            if (!fromcs && map != null) {
                if (map.getForcedReturnId() != 999999999) {
                    mapToSave = map.getForcedReturnId();
                } else {
                    mapToSave = stats.getHp() < 1 ? map.getReturnMapId() : map.getId();
                }
            } else {
                mapToSave = dwPosMap;
            }

            byte spawnpointToSave;
            if (map == null) {
                spawnpointToSave = (byte) 0;
            } else {
                TacosPortal closest = map.findClosestSpawnpoint(getPosition());
                spawnpointToSave = (byte) (closest != null ? closest.getId() : 0);
            }

            int[] pet_slots = {-1, -1, -1};
            int pet_index = 0;

            for (TacosPet pet : getPets()) {
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

            DQ_Skillmacros.deleteAndSaveAll(con, this);
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

    private String chalktext;
    private String teleportname = "";
    private long nextConsume = 0;
    private long pqStartTime = 0;
    private int mulung_energy;
    private int availableCP;
    private int totalCP;
    private int chair;
    // パチンコ
    private int beansRange;
    private int beansNum;
    // ポータルカウント
    private int portal_count = 1;
    // ペット回復薬
    private int pet_auto_hp_item_id = 0;
    private int pet_auto_mp_item_id = 0;
    private int pet_auto_cure_item_id = 0;
    private boolean smega;
    private boolean hasTutorialSummon = false;
    private boolean canSetBeansNum;
    private transient Deque<MapleCarnivalChallenge> pendingCarnivalRequests;
    private transient MapleCarnivalParty carnivalParty;
    private transient MapleShop shop;
    private transient MapleTrade trade;
    private transient Object playerShop;
    // 雇用商人
    private Object remoteStore = null;
    private IDebugMan debugMan = null;
    private DebugShop debugShop = null;

    public int getPortalCount() {
        portal_count += 1;
        return portal_count;
    }

    // all
    public void init_step1() {
        inventory = new MapleInventory[MapleInventoryType.values().length];
        for (MapleInventoryType type : MapleInventoryType.values()) {
            inventory[type.ordinal()] = new MapleInventory(type);
        }
        quests = new LinkedHashMap<>(); // Stupid erev quest.
        stats = new PlayerStats();
        for (int i = 0; i < remainingSp.length; i++) {
            remainingSp[i] = 0;
        }
    }

    // channel
    @SuppressWarnings("unchecked")
    public void init_step2() {
        mulung_energy = 0;
        smega = true;
        petStore = new byte[3];
        for (int i = 0; i < petStore.length; i++) {
            petStore[i] = (byte) -1;
        }
        wishlist = new int[10];
        rocks = new int[10];
        regrocks = new int[5];
        pendingCarnivalRequests = new LinkedList<>();
        savedLocations = new int[SavedLocationType.values().length];
        for (int i = 0; i < SavedLocationType.values().length; i++) {
            savedLocations[i] = -1;
        }
        questinfo = new LinkedHashMap<>();
    }

    private void deleteWhereCharacterId(Connection con, String sql) throws SQLException {
        deleteWhereCharacterId(con, sql, id);
    }

    public static void deleteWhereCharacterId(Connection con, String sql, int id) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    public MapleQuestStatus getQuest(MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            return new MapleQuestStatus(quest, (byte) 0);
        }
        return quests.get(quest);
    }

    public void setQuestAdd(MapleQuest quest, byte status, String customData) {
        if (!quests.containsKey(quest)) {
            MapleQuestStatus stat = new MapleQuestStatus(quest, status);
            stat.setCustomData(customData);
            quests.put(quest, stat);
        }
    }

    public MapleQuestStatus getQuestNAdd(MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            MapleQuestStatus status = new MapleQuestStatus(quest, (byte) 0);
            quests.put(quest, status);
            return status;
        }
        return quests.get(quest);
    }

    public MapleQuestStatus getQuestNoAdd(MapleQuest quest) {
        return quests.get(quest);
    }

    public void updateQuest(MapleQuestStatus quest) {
        updateQuest(quest, false);
    }

    public void updateQuest(MapleQuestStatus quest, boolean update) {
        quests.put(quest.getQuest(), quest);
        if (!(quest.isCustom())) {
            SendPacket(ResCWvsContext.Message(OpsMessage.MS_QuestRecordMessage, PB_Message.builder().QuestID((short) quest.getQuest().getId()).qt(OpsQuestRecordMessage.get(quest.getStatus())).str(quest.getCustomData() != null ? quest.getCustomData() : "").build()));
            if (quest.getStatus() == 1 && !update) {
                SendPacket(ResCUserLocal.UserQuestResult(this, quest.getQuest().getId(), quest.getNpc(), OpsQuest.QuestRes_Act_Success));
            }
        }
    }

    public Map<Integer, String> getInfoQuest_Map() {
        return questinfo;
    }

    public Map<MapleQuest, MapleQuestStatus> getQuest_Map() {
        return quests;
    }

    public int getItemQuantity(int itemid, boolean checkEquipped) {
        int possesed = inventory[GameConstants.getInventoryType(itemid).ordinal()].countById(itemid);
        if (checkEquipped) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        return possesed;
    }

    public void silentEnforceMaxHpMp() {
        stats.setMp(stats.getMp());
        stats.setHp(stats.getHp(), true);
    }

    public void enforceMaxHpMp() {
        if (stats.getMp() > stats.getCurrentMaxMp()) {
            stats.setMp(stats.getMp());
        }
        if (stats.getHp() > stats.getCurrentMaxHp()) {
            stats.setHp(stats.getHp());
        }
        sendStatChanged();
    }

    public String getBlessOfFairyOrigin() {
        return this.BlessOfFairy_Origin;
    }

    public int getDojo() {
        return dojo;
    }

    public int getHpApUsed() {
        return hpApUsed;
    }

    public void setHpApUsed(int hpApUsed) {
        this.hpApUsed = hpApUsed;
    }

    public void setSkinColor(int skinColor) {
        if (!WzDataStorage.SKIN.check(skinColor)) {
            DebugLogger.ErrorLog("Invalid skin id : " + skinColor);
            this.skinColor = DWI_Dafault.SKIN;
            return;
        }

        this.skinColor = skinColor;
    }

    public void setGashaEXP(int gashaexp) {
        this.gashaEXP = gashaexp;
    }

    public void setFame(int fame) {
        this.fame = fame;
    }

    public void setDojo(int dojo) {
        this.dojo = dojo;
    }

    public void setRemainingSp(int remainingSp) {
        this.remainingSp[GameConstants.getSkillBook(job)] = remainingSp; //default
    }

    public void setRemainingSp(int remainingSp, int skillbook) {
        this.remainingSp[skillbook] = remainingSp;
    }

    public void baseSkills() {
        if (GameConstants.getJobNumber(job) >= 3) { //third job.
            List<Integer> skills = WzXML.SKILL.getSkillsByJob().get(job);
            if (skills != null) {
                for (int i : skills) {
                    Skill skil = WzXML.SKILL.getSkill().get(i);
                    if (skil != null && !skil.isInvisible() && skil.isFourthJob() && getSkillLevel(skil) <= 0 && getMasterLevel(skil) <= 0 && skil.getMasterLevel() > 0) {
                        changeSkillLevel(skil, (byte) 0, (byte) skil.getMasterLevel()); //usually 10 master
                    }
                }
            }
        }
    }

    public void changeSkillLevel(Skill skill, byte newLevel, byte newMasterlevel) { //1 month
        if (skill == null) {
            return;
        }
        changeSkillLevel(skill, newLevel, newMasterlevel, skill.isTimeLimited() ? (System.currentTimeMillis() + (long) (30L * 24L * 60L * 60L * 1000L)) : -1);
    }

    public void changeSkillLevel(Skill skill, byte newLevel, byte newMasterlevel, long expiration) {
        if (skill == null || (!GameConstants.isApplicableSkill(skill.getId()) && !GameConstants.isApplicableSkill_(skill.getId()))) {
            DebugLogger.ErrorLog("changeSkillLevel : error = " + skill.getId());
            return;
        }
        SendPacket(ResCWvsContext.ChangeSkillRecordResult(skill.getId(), newLevel, newMasterlevel, expiration));
        if (newLevel == 0 && newMasterlevel == 0) {
            if (skills.containsKey(skill)) {
                skills.remove(skill);
            } else {
                return;
            }
        } else {
            skills.put(skill, new SkillEntry(newLevel, newMasterlevel, expiration));
        }
        if (GameConstants.isElementAmp_Skill(skill.getId())) {
            stats.recalcLocalStats();
        }

    }

    public void playerDead() {
        if (job != 0 && job != 1000 && job != 2000 && job != 2001 && job != 3000) {
            int charms = getItemQuantity(5130000, false);
            if (charms > 0) {
                MapleInventoryManipulator.removeById(client.getPlayer(), MapleInventoryType.CASH, 5130000, 1, true, false);

                charms--;
                if (charms > 0xFF) {
                    charms = 0xFF;
                }
                SendPacket(ResCUserLocal.useCharm((byte) charms, (byte) 0));
            } else {
                float diepercentage = 0.0f;
                int expforlevel = TacosSharedExpTable.getExpNeededForLevel(level);
                if (map.isTown() || FieldOpt.FIELDOPT_PORTALSCROLLLIMIT.check(map.getFieldLimit())) {
                    diepercentage = 0.01f;
                } else {
                    float v8 = 0.0f;
                    if (this.job / 100 == 3) {
                        v8 = 0.08f;
                    } else {
                        v8 = 0.2f;
                    }
                    diepercentage = (float) (v8 / this.stats.getLuk() + 0.05);
                }
                int v10 = (int) (exp - (long) ((double) expforlevel * diepercentage));
                if (v10 < 0) {
                    v10 = 0;
                }
                this.exp = v10;
            }
        }

        sendStatChanged();

        if (!stats.checkEquipDurabilitys(-100)) { //i guess this is how it works ?
            Notice("An item has run out of durability but has no inventory room to go to.");
        } //lol
        if (getPyramidSubway() != null) {
            stats.setHp((short) 50);
            getPyramidSubway().fail(this);
        }
    }

    public void updatePartyMemberHP() {
        if (party != null) {
            int channel = client.getChannelId();
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                    TacosCharacter other = client.getChannelServer().getOnlinePlayers().findByName(partychar.getName());
                    if (other != null) {
                        other.SendPacket(ResCUserRemote.UserHP(getId(), stats.getHp(), stats.getCurrentMaxHp()));
                    }
                }
            }
        }
    }

    public void receivePartyMemberHP() {
        if (party == null) {
            return;
        }
        int channel = client.getChannelId();
        for (MaplePartyCharacter partychar : party.getMembers()) {
            if (partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                TacosCharacter other = client.getChannelServer().getOnlinePlayers().findByName(partychar.getName());
                if (other != null) {
                    SendPacket(ResCUserRemote.UserHP(other.getId(), other.getStat().getHp(), other.getStat().getCurrentMaxHp()));
                }
            }
        }
    }

    public void addMP(int delta) {
        if (stats.setMp(stats.getMp() + delta)) {
            sendStatChanged();
        }
    }

    public void familyRep(int prevexp, int needed, boolean leveled) {
        if (mfc != null) {
            int onepercent = needed / 100;
            int percentrep = (prevexp / onepercent + getExp() / onepercent);
            if (leveled) {
                percentrep = 100 - percentrep + (level / 2);
            }
            if (percentrep > 0) {
                int sensen = getWorld().getFamily().setRep(mfc.getFamilyId(), mfc.getSeniorId(), percentrep, level, getName());
                if (sensen > 0) {
                    getWorld().getFamily().setRep(mfc.getFamilyId(), sensen, percentrep / 2, level, getName()); //and we stop here
                }
            }
        }
    }

    public void gainExpMonster(int gain, boolean show, boolean white, byte pty, int Class_Bonus_EXP, int Equipment_Bonus_EXP, int Premium_Bonus_EXP) {
        int total = gain + Class_Bonus_EXP + Equipment_Bonus_EXP + Premium_Bonus_EXP;
        int partyinc = 0;
        int prevexp = getExp();
        if (pty > 1) {
            partyinc = (int) (((float) (gain / 20.0)) * (pty + 1));
            total += partyinc;
        }

        if (gain > 0 && total < gain) { //just in case
            total = Integer.MAX_VALUE;
        }
        int needed = TacosSharedExpTable.getExpNeededForLevel(level);
        if (level >= 200 || (GameConstants.isKOC(job) && level >= 120)) {
            if (exp + total > needed) {
                setExp(needed);
            } else {
                exp += total;
            }
        } else {
            boolean leveled = false;
            if (exp + total >= needed) {
                exp += total;
                levelUp();
                leveled = true;
                needed = TacosSharedExpTable.getExpNeededForLevel(level);
                if (exp > needed) {
                    setExp(needed);
                }
            } else {
                exp += total;
            }
            if (total > 0) {
                familyRep(prevexp, needed, leveled);
            }
        }
        if (gain != 0) {
            if (exp < 0) { // After adding, and negative
                if (gain > 0) {
                    setExp(TacosSharedExpTable.getExpNeededForLevel(level));
                } else if (gain < 0) {
                    setExp(0);
                }
            }
            sendStatChanged();
            if (show) { // still show the expgain even if it's not there
                SendPacket(ResCWvsContext.Message(OpsMessage.MS_IncEXPMessage, PB_Message.builder().Inc_EXP_TextColor(white ? 1 : 0).Inc_EXP(gain).Inc_EXP_PartyBonus(partyinc).Inc_EXP_EquipmentBonus(Equipment_Bonus_EXP).Inc_EXP_PremiumBonus(Premium_Bonus_EXP).Inc_EXP_ClassBonus(Class_Bonus_EXP).build()));
            }
            stats.checkEquipLevels(total);
        }
    }

    public void forceReAddItem_NoUpdate(Item item, MapleInventoryType type) {
        getInventory(type).removeSlot(item.getPosition());
        getInventory(type).addFromDB(item);
    }

    public void silentPartyUpdate() {
        if (party != null) {
            getWorld().getParty().updateParty(party.getId(), PartyOperation.SILENT_UPDATE, new MaplePartyCharacter(this));
        }
    }

    public MapleShop getShop() {
        return shop;
    }

    public void setShop(MapleShop shop) {
        this.shop = shop;
    }

    // 初期化用
    public void setMeso(int val) {
        meso = val;
    }

    public void saveLocation(SavedLocationType type, int mapz) {
        savedLocations[type.getValue()] = mapz;
    }

    public boolean gainMeso(int gain, boolean show) {
        return gainMeso(gain, show, false, false);
    }

    public void gainMeso(int gain, boolean show, boolean enableActions) {
        gainMeso(gain, show, enableActions, false);
    }

    public boolean gainTama(int gain, boolean show) {
        if (tama + gain < 0) {
            updateStat();
            return false;
        }
        gainTama(gain);
        if (show) {
            SendPacket(ResCWvsContext.Message(OpsMessage.MS_JMS_Pachinko, PB_Message.builder().Inc_Tama(gain).build()));
        }
        return true;
    }

    public void mobKilled(int id, int skillID) {
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() != 1 || !q.hasMobKills()) {
                continue;
            }
            if (q.mobKilled(id, skillID)) {
                {
                    StringBuilder sb = new StringBuilder();
                    for (int kills : q.getMobKills().values()) {
                        sb.append(String.format("%03d", kills));
                    }
                    SendPacket(ResCWvsContext.Message(OpsMessage.MS_QuestRecordMessage, PB_Message.builder().QuestID((short) q.getQuest().getId()).qt(OpsQuestRecordMessage.QUEST_UPDATE).str(sb.toString()).build()));
                }
                if (q.getQuest().canComplete(this, null)) {
                    SendPacket(ResCWvsContext.QuestClear(q.getQuest().getId()));
                }
            }
        }
    }

    public List<MapleQuestStatus> getStartedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 1 && !(q.isCustom())) {
                ret.add(q);
            }
        }
        return ret;
    }

    public List<MapleQuestStatus> getCompletedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 2 && !(q.isCustom())) {
                ret.add(q);
            }
        }
        return ret;
    }

    public Map<Skill, SkillEntry> getSkills() {
        return Collections.unmodifiableMap(skills);
    }

    public void levelUp() {
        if (GameConstants.isKOC(job)) {
            if (level <= 70) {
                remainingAp += 6;
            } else {
                remainingAp += 5;
            }
        } else {
            remainingAp += 5;
        }
        int maxhp = stats.getMaxHp();
        int maxmp = stats.getMaxMp();

        if (job == 0 || job == 1000 || job == 2000 || job == 2001 || job == 3000) { // Beginner
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(10, 12);
        } else if (job >= 100 && job <= 132) { // Warrior
            Skill improvingMaxHP = WzXML.SKILL.getSkill().get(1000001);
            int slevel = getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(24, 28);
            maxmp += Randomizer.rand(4, 6);
        } else if (job >= 200 && job <= 232) { // Magician
            Skill improvingMaxMP = WzXML.SKILL.getSkill().get(2000001);
            int slevel = getSkillLevel(improvingMaxMP);
            if (slevel > 0) {
                maxmp += improvingMaxMP.getEffect(slevel).getX() * 2;
            }
            maxhp += Randomizer.rand(10, 14);
            maxmp += Randomizer.rand(22, 24);
        } else if (job >= 3200 && job <= 3212) { //battle mages get their own little neat thing
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(42, 44);
        } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1311) || (job >= 1400 && job <= 1411) || (job >= 3300 && job <= 3312)) { // Bowman, Thief, Wind Breaker and Night Walker
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(14, 16);
        } else if ((job >= 500 && job <= 522) || (job >= 3500 && job <= 3512)) { // Pirate
            Skill improvingMaxHP = WzXML.SKILL.getSkill().get(5100000);
            int slevel = getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(22, 26);
            maxmp += Randomizer.rand(18, 22);
        } else if (job >= 1100 && job <= 1111) { // Soul Master
            Skill improvingMaxHP = WzXML.SKILL.getSkill().get(11000000);
            int slevel = getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(24, 28);
            maxmp += Randomizer.rand(4, 6);
        } else if (job >= 1200 && job <= 1211) { // Flame Wizard
            Skill improvingMaxMP = WzXML.SKILL.getSkill().get(12000000);
            int slevel = getSkillLevel(improvingMaxMP);
            if (slevel > 0) {
                maxmp += improvingMaxMP.getEffect(slevel).getX() * 2;
            }
            maxhp += Randomizer.rand(10, 14);
            maxmp += Randomizer.rand(22, 24);
        } else if (job >= 1500 && job <= 1512) { // Pirate
            Skill improvingMaxHP = WzXML.SKILL.getSkill().get(15100000);
            int slevel = getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(22, 26);
            maxmp += Randomizer.rand(18, 22);
        } else if (job >= 2100 && job <= 2112) { // Aran
            maxhp += Randomizer.rand(50, 52);
            maxmp += Randomizer.rand(4, 6);
        } else if (job >= 2200 && job <= 2218) { // Evan
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(50, 52);
        } else { // GameMaster
            maxhp += Randomizer.rand(50, 100);
            maxmp += Randomizer.rand(50, 100);
        }
        maxmp += stats.getTotalInt() / 10;
        exp -= TacosSharedExpTable.getExpNeededForLevel(level);
        if (TacosSharedExpTable.getExpNeededForLevel(level + 1) < exp) {
            exp = TacosSharedExpTable.getExpNeededForLevel(level + 1) - 1;
        }
        if (exp < 0) {
            exp = 0;
        }
        level += 1;

        int level = getLevel();
        getWorld().reachedMaxLevel(this);
        maxhp = (short) Math.min(30000, Math.abs(maxhp));
        maxmp = (short) Math.min(30000, Math.abs(maxmp));

        if (isGM() || (job != 0 && job != 1000 && job != 2000 && job != 2001 && job != 3000)) { // Not Beginner, Nobless and Legend
            remainingSp[GameConstants.getSkillBook(this.job)] += 3;
            sendStatChanged(false);
        } else {
            if (level <= 10) {
                stats.setStr((short) (stats.getStr() + remainingAp));
                remainingAp = 0;
            }
        }

        stats.setMaxHp((short) maxhp);
        stats.setMaxMp((short) maxmp);
        stats.setHp((short) maxhp);
        stats.setMp((short) maxmp);
        sendStatChanged();

        PB_UserEffect pb = PB_UserEffect.builder()
                .player(this)
                .build();
        map.splitSendPacket(this, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_LevelUp, pb), getId());
        stats.recalcLocalStats();
        silentPartyUpdate();
        guildUpdate();
        familyUpdate();

        if (GameConstants.isEvan(job)) {
            switch (level) {
                case 10:
                case 20:
                case 30:
                case 40:
                case 50:
                case 60:
                case 80:
                case 100:
                case 120:
                case 160:
                    if (job < 2218) {
                        changeJob(job == 2001 ? 2200 : (job == 2200 ? 2210 : (job + 1))); //automatic
                    }
                    break;
            }
        }

        // レベルアップ時にDBへ反映する
        saveToDB(false);
    }

    public FameStatus canGiveFame(TacosCharacter from) {
        if (lastfametime >= System.currentTimeMillis() - 60 * 60 * 24 * 1000) {
            return FameStatus.NOT_TODAY;
        } else if (from == null || lastmonthfameids == null || lastmonthfameids.contains(from.getId())) {
            return FameStatus.NOT_THIS_MONTH;
        }
        return FameStatus.OK;
    }

    public void hasGivenFame(TacosCharacter to) {
        lastfametime = System.currentTimeMillis();
        lastmonthfameids.add(to.getId());
        DQ_Famelog.insert(getId(), to.getId());
    }

    public void setParty(MapleParty party) {
        this.party = party;
    }

    public MapleTrade getTrade() {
        return trade;
    }

    public void setTrade(MapleTrade trade) {
        this.trade = trade;
    }

    public boolean getSmega() {
        return smega;
    }

    public int getChair() {
        return chair;
    }

    public void setChair(int chair) {
        this.chair = chair;
    }

    public int getFamilyId() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getFamilyId();
    }

    public int getSeniorId() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getSeniorId();
    }

    public int getJunior1() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getJunior1();
    }

    public int getJunior2() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getJunior2();
    }

    public MapleGuildCharacter getMGC() {
        return mgc;
    }

    public void guildUpdate() {
        if (guildid <= 0) {
            return;
        }
        mgc.setLevel((short) level);
        mgc.setJobId(job);
        getWorld().getGuild().memberLevelJobUpdate(mgc);
    }

    public void saveGuildStatus() {
        MapleGuild.setOfflineGuildStatus(guildid, guildrank, allianceRank, id);
    }

    public void familyUpdate() {
        if (mfc == null) {
            return;
        }
        getWorld().getFamily().memberFamilyUpdate(mfc, this);
    }

    public void saveFamilyStatus() {
        if (mfc == null) {
            DQ_Characters.updateFamilyStatus(id, 0, 0, 0, 0);
        } else {
            DQ_Characters.updateFamilyStatus(id, mfc.getFamilyId(), mfc.getSeniorId(), mfc.getJunior1(), mfc.getJunior2());
        }
        //MapleFamily.setOfflineFamilyStatus(familyid, seniorid, junior1, junior2, currentrep, totalrep, id);
    }

    public void modifyCSPoints(int type, int quantity) {
        modifyCSPoints(type, quantity, false);
    }

    public int getNexonPoint() {
        return nexonPoint;
    }

    public int getMaplePoint() {
        return maplePoint;
    }

    public boolean checkNexonPoint(int value) {
        // 購入不可
        if (nexonPoint < value) {
            return false;
        }
        return true;
    }

    public boolean checkMaplePoint(int value) {
        if (maplePoint < value) {
            return false;
        }
        return true;
    }

    public boolean useNexonPoint(int value) {
        // マイナス値不可
        if (value < 0 || nexonPoint < value) {
            return false;
        }
        nexonPoint -= value;
        return true;
    }

    public boolean useMaplePoint(int value) {
        if (value < 0 || maplePoint < value) {
            return false;
        }
        maplePoint -= value;
        return true;
    }

    public boolean addMaplePoint(int value) {
        if (value < 0) {
            return false;
        }
        maplePoint += value;
        return true;
    }

    public boolean addMeso(int value) {
        if (value < 0) {
            return false;
        }
        meso += value;
        return true;
    }

    public static enum FameStatus {

        OK, NOT_TODAY, NOT_THIS_MONTH
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void sendNote(String to, String msg) {
        sendNote(to, msg, 0);
    }

    public void sendNote(String to, String msg, int fame) {
        DQ_Notes.sendNote(to, getName(), msg, fame);
    }

    /*
    // TODO : remove last, and first.
    public void showNote() {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM notes WHERE `to`=?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                ps.setString(1, getName());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.last();
                    int count = rs.getRow();
                    rs.first();
                    client.SendPacket(ResCWvsContext.MemoResult(rs, count));
                }
            }
        } catch (SQLException e) {
            System.err.println("Unable to show note" + e);
        }
    }
     */
    public void deleteNote(int id, int fame) {
        Integer gift = DQ_Notes.getGift(id);
        if (gift != null && gift == fame && fame > 0) { //not exploited! hurray
            addFame(fame);
            sendStatChanged();
            SendPacket(ResCWvsContext.Message(OpsMessage.MS_IncPOPMessage, PB_Message.builder().Inc_Fame(fame).build()));
        }
        DQ_Notes.deleteById(id);
    }

    public void mulung_EnergyModify(boolean inc) {
        if (inc) {
            if (mulung_energy + 100 > 10000) {
                mulung_energy = 10000;
            } else {
                mulung_energy += 100;
            }
        } else {
            mulung_energy = 0;
        }
        SendPacket(ResCWvsContext.sendString(1, "energy", String.valueOf(mulung_energy)));
    }

    public void writeMulungEnergy() {
        SendPacket(ResCWvsContext.sendString(1, "energy", String.valueOf(mulung_energy)));
    }

    public void writeStatus(String type, String inc) {
        SendPacket(ResCWvsContext.sendString(3, type, inc));
    }

    public void writePoint(String type, String inc) {
        SendPacket(ResCWvsContext.sendString(2, type, inc));
    }

    public boolean IsBerserk() {
        Skill BerserkX = WzXML.SKILL.getSkill().get(1320006);
        int skilllevel = getSkillLevel(BerserkX);

        if (skilllevel < 1) {
            return false;
        }

        MapleStatEffect ampStat = BerserkX.getEffect(skilllevel);
        if (stats.getHp() * 100 / stats.getMaxHp() <= ampStat.getX()) {
            return true;
        }

        return false;
    }

    public byte getEffectMask() {
        byte mask = 0;

        // CUser::LoadDarkForceEffect
        if (IsBerserk()) {
            mask |= 1;
        }

        // CDragon::CreateEffect
        // mask |= 2;
        // CUser::LoadSwallowingEffect
        // mask |= 4;
        return mask;
    }

    public void setADBoard(String text) {
        this.chalktext = text;
    }

    public String getADBoard() {
        return chalktext;
    }

    public int[] getWishlist() {
        return wishlist;
    }

    public int[] getRocks() {
        return rocks;
    }

    public int getRockSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (rocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromRocks(int map) {
        for (int i = 0; i < 10; i++) {
            if (rocks[i] == map) {
                rocks[i] = 999999999;
                break;
            }
        }
    }

    public void addRockMap() {
        if (getRockSize() >= 10) {
            return;
        }
        rocks[getRockSize()] = getMapId();
    }

    public int[] getRegRocks() {
        return regrocks;
    }

    public int getRegRockSize() {
        int ret = 0;
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromRegRocks(int map) {
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] == map) {
                regrocks[i] = 999999999;
                break;
            }
        }
    }

    public void addRegRockMap() {
        if (getRegRockSize() >= 5) {
            return;
        }
        regrocks[getRegRockSize()] = getMapId();
    }

    public Object getPlayerShop() {
        return playerShop;
    }

    public TacosMerchant getMyHiredMerchant() {
        if (!(playerShop instanceof TacosMerchant)) {
            return null;
        }
        TacosMerchant merchant = (TacosMerchant) playerShop;
        if (!merchant.isOwner(this)) {
            return null;
        }

        return merchant;
    }

    public void setPlayerShop(Object playerShop) {
        this.playerShop = playerShop;
    }

    public void setCarnivalParty(MapleCarnivalParty party) {
        carnivalParty = party;
    }

    public void addCP(int ammount) {
        totalCP += ammount;
        availableCP += ammount;
    }

    public void useCP(int ammount) {
        availableCP -= ammount;
    }

    public int getAvailableCP() {
        return availableCP;
    }

    public void CPUpdate(boolean party, int available, int total, int team) {
        SendPacket(ResCField_MonsterCarnival.CPUpdate(party, available, total, team));
    }

    public int getEXPMod() {
        return stats.expMod;
    }

    public int getDropMod() {
        return stats.dropMod;
    }

    public CashShop getCashInventory() {
        return cs;
    }

    //TODO: more than one crush/friendship ring at a time
    public SimpleImmutableEntry<List<MapleRing>, List<MapleRing>> getRings(boolean equip) {
        MapleInventory iv = getInventory(MapleInventoryType.EQUIPPED);
        Collection<Item> equippedC = iv.list();
        List<Item> equipped = new ArrayList<>(equippedC.size());
        for (Item item : equippedC) {
            equipped.add((Item) item);
        }
        Collections.sort(equipped, (item1, item2) -> Item.comparePosition(item1, item2));
        List<MapleRing> crings = new ArrayList<>();
        List<MapleRing> frings = new ArrayList<>();
        MapleRing ring;
        for (Item item : equipped) {
            if (item.getRing() != null) {
                ring = item.getRing();
                ring.setEquipped(true);
                if (GameConstants.isFriendshipRing(item.getItemId()) || GameConstants.isCrushRing(item.getItemId())) {
                    if (equip) {
                        if (GameConstants.isCrushRing(item.getItemId())) {
                            crings.add(ring);
                        } else if (GameConstants.isFriendshipRing(item.getItemId())) {
                            frings.add(ring);
                        }
                    } else {
                        if (crings.size() == 0 && GameConstants.isCrushRing(item.getItemId())) {
                            crings.add(ring);
                        } else if (frings.size() == 0 && GameConstants.isFriendshipRing(item.getItemId())) {
                            frings.add(ring);
                        } //for 3rd person the actual slot doesnt matter, so we'll use this to have both shirt/ring same?
                        //however there seems to be something else behind this, will have to sniff someone with shirt and ring, or more conveniently 3-4 of those
                    }
                }
            }
        }
        if (equip) {
            iv = getInventory(MapleInventoryType.EQUIP);
            for (Item item : iv.list()) {
                if (item.getRing() != null && GameConstants.isCrushRing(item.getItemId())) {
                    ring = item.getRing();
                    ring.setEquipped(false);
                    if (GameConstants.isFriendshipRing(item.getItemId())) {
                        frings.add(ring);
                    } else if (GameConstants.isCrushRing(item.getItemId())) {
                        crings.add(ring);
                    }
                }
            }
        }
        Collections.sort(frings, new MapleRing.RingComparator());
        Collections.sort(crings, new MapleRing.RingComparator());
        return new SimpleImmutableEntry<>(crings, frings);
    }

    public long getNextConsume() {
        return nextConsume;
    }

    public void setNextConsume(long nc) {
        this.nextConsume = nc;
    }

    public boolean changeChannel(int channel) {
        return changeChannel(channel, false);
    }

    public boolean changeChannel(int channel, boolean fake_relog) {
        TacosChannel ch_server = getWorld().getChannelServer(channel);
        if (ch_server == null || (channel == client.getChannelId() && !fake_relog)) {
            return false;
        }

        changeRemoval();

        getWorld().addMigratingPlayer(this);
        getChannelServer().getOnlinePlayers().remove(this);
        sendMigrateCommand(ch_server);
        saveToDB(false);
        getMap().userLeaveField(this);
        return true;
    }

    // TODO: gvup, vic, lose, draw, VR
    public boolean startPartyQuest(int questid) {
        boolean ret = false;
        if (!quests.containsKey(MapleQuest.getInstance(questid)) || !questinfo.containsKey(questid)) {
            MapleQuestStatus status = getQuestNAdd(MapleQuest.getInstance(questid));
            status.setStatus((byte) 1);
            updateQuest(status);
            switch (questid) {
                case 1300:
                case 1301:
                case 1302: //carnival, ariants.
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0;gvup=0;vic=0;lose=0;draw=0");
                    break;
                case 1204: //herb town pq
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;have2=0;have3=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                case 1206: //ellin pq
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                default:
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
            }
            ret = true;
        } //started the quest.
        return ret;
    }

    public void recalcPartyQuestRank(int questid) {
        if (!startPartyQuest(questid)) {
            String oldRank = getOneInfo(questid, "rank");
            if (oldRank == null || oldRank.equals("S")) {
                return;
            }
            questinfo.get(questid).split(";");
            String newRank = null;
            if (oldRank.equals("A")) {
                newRank = "S";
            } else if (oldRank.equals("B")) {
                newRank = "A";
            } else if (oldRank.equals("C")) {
                newRank = "B";
            } else if (oldRank.equals("D")) {
                newRank = "C";
            } else if (oldRank.equals("F")) {
                newRank = "D";
            } else {
                return;
            }
            List<SimpleImmutableEntry<String, SimpleImmutableEntry<String, Integer>>> questInfo = MapleQuest.getInstance(questid).getInfoByRank(newRank);
            for (SimpleImmutableEntry<String, SimpleImmutableEntry<String, Integer>> q : questInfo) {
                boolean found = false;
                String val = getOneInfo(questid, q.getValue().getKey());
                if (val == null) {
                    return;
                }
                int vall = 0;
                try {
                    vall = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    return;
                }
                if (q.getKey().equals("less")) {
                    found = vall < q.getValue().getValue();
                } else if (q.getKey().equals("more")) {
                    found = vall > q.getValue().getValue();
                } else if (q.getKey().equals("equal")) {
                    found = vall == q.getValue().getValue();
                }
                if (!found) {
                    return;
                }
            }
            //perfectly safe
            updateOneInfo(questid, "rank", newRank);
        }
    }

    public void havePartyQuest(int itemId) {
        int questid = 0, index = -1;
        switch (itemId) {
            case 1002798:
                questid = 1200; //henesys
                break;
            case 1072369:
                questid = 1201; //kerning
                break;
            case 1022073:
                questid = 1202; //ludi
                break;
            case 1082232:
                questid = 1203; //orbis
                break;
            case 1002571:
            case 1002572:
            case 1002573:
            case 1002574:
                questid = 1204; //herbtown
                index = itemId - 1002571;
                break;
            case 1122010:
                questid = 1205; //magatia
                break;
            case 1032061:
            case 1032060:
                questid = 1206; //ellin
                index = itemId - 1032060;
                break;
            case 3010018:
                questid = 1300; //ariant
                break;
            case 1122007:
                questid = 1301; //carnival
                break;
            case 1122058:
                questid = 1302; //carnival2
                break;
            default:
                return;
        }
        startPartyQuest(questid);
        updateOneInfo(questid, "have" + (index == -1 ? "" : index), "1");
    }

    public boolean hasTutorialSummon() {
        return hasTutorialSummon;
    }

    public void setTutorialSummon(boolean summ) {
        this.hasTutorialSummon = summ;
    }

    public void changeRemoval() {
        changeRemoval(false);
    }

    public void changeRemoval(boolean dc) {
        if (getTrade() != null) {
            MapleTrade.cancelTrade(getTrade(), client);
        }
        if (getPyramidSubway() != null) {
            getPyramidSubway().dispose(this);
        }
        if (playerShop != null && !dc) {
            ShopDispatch.removeVisitor(playerShop, this);
            if (ShopDispatch.isOwner(playerShop, this)) {
                ShopDispatch.setOpen(playerShop, true);
            }
        }
        TacosScriptNPC.getInstance().dispose(client);
        TacosScriptQuest.getInstance().dispose(client);
    }

    public boolean canUseFamilyBuff(MapleFamilyBuffEntry buff) {
        MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(buff.questID));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Long.parseLong(stat.getCustomData()) + (24 * 3600000) < System.currentTimeMillis();
    }

    public void useFamilyBuff(MapleFamilyBuffEntry buff) {
        MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(buff.questID));
        stat.setCustomData(String.valueOf(System.currentTimeMillis()));
    }

    public List<SimpleImmutableEntry<Integer, Integer>> usedBuffs() {
        //assume count = 1
        List<SimpleImmutableEntry<Integer, Integer>> used = new ArrayList<>();
        for (MapleFamilyBuffEntry buff : MapleFamilyBuff.getBuffEntry()) {
            if (!canUseFamilyBuff(buff)) {
                used.add(new SimpleImmutableEntry<>(buff.index, buff.count));
            }
        }
        return used;
    }

    public String getTeleportName() {
        return teleportname;
    }

    public void setTeleportName(String tname) {
        teleportname = tname;
    }

    public int getNoJuniors() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getNoJuniors();
    }

    public MapleFamilyCharacter getMFC() {
        return mfc;
    }

    public void makeMFC(int familyid, int seniorid, int junior1, int junior2) {
        if (familyid > 0) {
            MapleFamily f = (getWorld() != null) ? getWorld().getFamily().getFamily(familyid) : null;
            if (f == null) {
                mfc = null;
            } else {
                mfc = f.getMFC(id);
                if (mfc == null) {
                    mfc = f.addFamilyMemberInfo(this, seniorid, junior1, junior2);
                }
                if (mfc.getSeniorId() != seniorid) {
                    mfc.setSeniorId(seniorid);
                }
                if (mfc.getJunior1() != junior1) {
                    mfc.setJunior1(junior1);
                }
                if (mfc.getJunior2() != junior2) {
                    mfc.setJunior2(junior2);
                }
            }
        } else {
            mfc = null;
        }
    }

    public void setFamily(int newf, int news, int newj1, int newj2) {
        if (mfc == null || newf != mfc.getFamilyId() || news != mfc.getSeniorId() || newj1 != mfc.getJunior1() || newj2 != mfc.getJunior2()) {
            makeMFC(newf, news, newj1, newj2);
        }
    }

    // パチンコ
    // CMS v72から流用
    public void gainTama(int s) {
        this.tama += s;
        SendPacket(ResCWvsContext.PachinkoResult(this));
    }

    public int getBeansRange() {
        return beansRange;
    }

    public void setBeansRange(int beansRange) {
        this.beansRange = beansRange;
    }

    public int getBeansNum() {
        return beansNum;
    }

    public void setBeansNum(int beansNum) {
        this.beansNum = beansNum;
    }

    public boolean isCanSetBeansNum() {
        return canSetBeansNum;
    }

    public void setCanSetBeansNum(boolean canSetBeansNum) {
        this.canSetBeansNum = canSetBeansNum;
    }

    // 雇用商人
    public void setRemoteStore(Object playerShop) {
        this.remoteStore = playerShop;
    }

    public Object getRemoteStore() {
        return this.remoteStore;
    }

    public void setPetAutoHPItem(int item_id) {
        if (item_id == 0 || WzDataStorage.ITEM.check(item_id)) {
            this.pet_auto_hp_item_id = item_id;
        }
    }

    public void setPetAutoMPItem(int item_id) {
        if (item_id == 0 || WzDataStorage.ITEM.check(item_id)) {
            this.pet_auto_mp_item_id = item_id;
        }
    }

    public void setPetAutoCureItem(int item_id) {
        if (item_id == 0 || WzDataStorage.ITEM.check(item_id)) {
            this.pet_auto_cure_item_id = item_id;
        }
    }

    public int getPetAutoHPItem() {
        return this.pet_auto_hp_item_id;
    }

    public int getPetAutoMPItem() {
        return this.pet_auto_mp_item_id;
    }

    public int getPetAutoCureItem() {
        return this.pet_auto_cure_item_id;
    }

    // クローン
    public TacosCharacter cloneCopy() {
        TacosClient client_clone = new TacosClient(new MockIOSession());

        int minus = (getId() + Randomizer.nextInt(getId())); // really randomize it, dont want it to fail

        TacosCharacter ret = new TacosCharacter();
        ret.init_step1();
        ret.init_step2();
        ret.id = minus;
        ret.client = client_clone;
        ret.exp = 0;
        ret.meso = 0;
        ret.remainingAp = 0;
        ret.fame = 0;
        ret.accountid = client.getId();
        ret.name = name;
        ret.level = level;
        ret.fame = fame;
        ret.job = job;
        ret.hair = hair;
        ret.face = face;
        ret.skinColor = skinColor;
        ret.mount = mount;
        ret.gmLevel = gmLevel;
        ret.gender = gender;
        ret.dwPosMap = map.getId();
        ret.map = map;
        ret.setMoveAction(getMoveAction());
        ret.chair = chair;
        ret.nEffectItemID = nEffectItemID;
        ret.guildid = guildid;
        ret.currentrep = currentrep;
        ret.totalrep = totalrep;
        ret.stats = stats;
        //ret.effects.putAll(effects);
        ret.guildrank = guildrank;
        ret.allianceRank = allianceRank;
        ret.setPosition(new Point(getPosition()));
        for (Item equip : getInventory(MapleInventoryType.EQUIPPED)) {
            ret.getInventory(MapleInventoryType.EQUIPPED).addFromDB(equip);
        }
        ret.keylayout = keylayout;
        ret.questinfo = questinfo;
        ret.savedLocations = savedLocations;
        ret.wishlist = wishlist;
        ret.rocks = rocks;
        ret.regrocks = regrocks;
        ret.buddylist = buddylist;
        ret.lastmonthfameids = lastmonthfameids;
        ret.lastfametime = lastfametime;
        ret.cs = this.cs;
        ret.client.setMapleId(client.getMapleId());
        ret.nexonPoint = nexonPoint;
        ret.maplePoint = maplePoint;
        while (map.getPlayerById(ret.id) != null || client.getChannelServer().getOnlinePlayers().findById(ret.id) != null) {
            ret.id++;
        }
        ret.client.setPlayer(ret);
        return ret;
    }

    public IDebugMan getDebugMan() {
        return this.debugMan;
    }

    public boolean setDebugMan(IDebugMan debugMan) {
        if (debugMan != null && this.debugMan != null) {
            return false;
        }
        this.debugMan = debugMan;
        return true;
    }

    public DebugShop getDebugShop() {
        return this.debugShop;
    }

    public boolean setDebugShop(DebugShop debugShop) {
        if (debugShop != null && this.debugShop != null) {
            return false;
        }
        this.debugShop = debugShop;
        return true;
    }

    public boolean useItem(short item_slot, int item_id) {
        Item toUse = this.getInventory(MapleInventoryType.USE).getItem(item_slot);

        if (toUse == null || toUse.getItemId() != item_id || toUse.getQuantity() < 1) {
            updateInv();
            return false;
        }

        long time = System.currentTimeMillis();
        if (this.getNextConsume() > time) {
            this.DebugMsg2("You may not use this item yet.");
            updateInv();
            return false;
        }

        if (FieldOpt.FIELDOPT_NOMOBCAPACITYLIMIT.check(map.getFieldLimit())) {
            updateInv();
            return false;
        }

        MapleStatEffect effect = MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId());
        if (!effect.applyTo(this)) {
            updateInv();
            return false;
        }

        MapleInventoryManipulator.removeFromSlot(client.getPlayer(), MapleInventoryType.USE, item_slot, (short) 1, false);
        if (map.getConsumeItemCoolTime() > 0) {
            this.setNextConsume(time + (this.map.getConsumeItemCoolTime() * 1000));
        }

        return true;
    }

    public void removalTask() {
        TacosMap map = this.getMap();
        try {
            if (this.getMarriageId() > 0) {
                MapleQuestStatus stat1 = this.getQuestNAdd(MapleQuest.getInstance(160001));
                MapleQuestStatus stat2 = this.getQuestNAdd(MapleQuest.getInstance(160002));
                if (stat1.getCustomData() != null && (stat1.getCustomData().equals("2_") || stat1.getCustomData().equals("2"))) {
                    //dc in process of marriage
                    if (stat2.getCustomData() != null) {
                        stat2.setCustomData("0");
                    }
                    stat1.setCustomData("3");
                }
            }
            this.changeRemoval(true);
            if (map != null) {
                map.userLeaveField(this);
            }

            Object shop = this.getPlayerShop();
            if (shop != null) {
                ShopDispatch.removeVisitor(shop, this);
                if (ShopDispatch.isOwner(shop, this)) {
                    if (ShopDispatch.getShopType(shop) == 1 && ShopDispatch.isAvailable(shop)) {
                        ShopDispatch.setOpen(shop, true);
                    } else {
                        ShopDispatch.closeShop(shop, true, true, 6);
                    }
                }
            }
        } catch (Throwable e) {
            DebugLogger.ErrorLog("removalTask");
        }
    }

    public boolean disconnect(boolean RemoveInChannelServer, boolean fromCS) {
        this.getName();
        int idz = this.getId(), gid = this.getGuildId(), fid = this.getFamilyId();
        this.getBuddylist();
        MaplePartyCharacter chrp = new MaplePartyCharacter(this);
        MapleGuildCharacter chrg = this.getMGC();
        MapleFamilyCharacter chrf = this.getMFC();

        // fix---
        if (!fromCS) {
            TacosChannel srv_ch = getChannelServer();

            try {
                if (srv_ch == null || srv_ch.isShutdown()) {
                    return false;
                }
                if (party != null) {
                    chrp.setOnline(false);
                    getWorld().getParty().updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                    if (map != null && party.getLeader().getId() == idz) {
                        MaplePartyCharacter lchr = null;
                        for (MaplePartyCharacter pchr : party.getMembers()) {
                            if (pchr != null && map.getPlayerById(pchr.getId()) != null && (lchr == null || lchr.getLevel() < pchr.getLevel())) {
                                lchr = pchr;
                            }
                        }
                        if (lchr != null) {
                            getWorld().getParty().updateParty(party.getId(), PartyOperation.CHANGE_LEADER_DC, lchr);
                        }
                    }
                }
                if (gid > 0) {
                    getWorld().getGuild().setGuildMemberOnline(chrg, false, -1);
                }
                if (fid > 0) {
                    getWorld().getFamily().setFamilyMemberOnline(chrf, false, -1);
                }
            } catch (Exception e) {
            } finally {
                if (RemoveInChannelServer && srv_ch != null) {
                    srv_ch.getOnlinePlayers().remove(this);
                }
            }
        } else {
            if (getWorld().findOnlinePlayerById(idz, false) != null) {
                disconnect(RemoveInChannelServer, false);//u lie
                return false;
            }
            try {
                if (party != null) {
                    chrp.setOnline(false);
                    getWorld().getParty().updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                }
                if (gid > 0) {
                    getWorld().getGuild().setGuildMemberOnline(chrg, false, -1);
                }
            } catch (Exception e) {
            }
        }

        return true;
    }

    // used by script
    public void updateInfoQuest(int questid, String data) {
        questinfo.put(questid, data);
        SendPacket(ResCWvsContext.Message(OpsMessage.MS_QuestRecordExMessage, PB_Message.builder().QuestID((short) questid).str(data).build()));
    }

    // used by script
    public String getInfoQuest(int questid) {
        if (questinfo.containsKey(questid)) {
            return questinfo.get(questid);
        }
        return "";
    }

    // used by script
    public int getNumQuest() {
        int i = 0;
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 2 && !(q.isCustom())) {
                i++;
            }
        }
        return i;
    }

    // used by script
    public byte getQuestStatus(int quest_id) {
        return getQuest(MapleQuest.getInstance(quest_id)).getStatus();
    }

    // used by script
    public int getSkillLevel(int skillid) {
        return getSkillLevel(WzXML.SKILL.getSkill().get(skillid));
    }

    // used by script
    public int getDojoRecord() {
        return dojoRecord;
    }

    // used by script
    public void setExp(int exp) {
        this.exp = exp;
    }

    // used by script
    public void setHair(int hair) {
        if (!WzDataStorage.HAIR.check(hair)) {
            DebugLogger.ErrorLog("Invalid hair id : " + hair);
            this.hair = DWI_Dafault.HAIR;
            return;
        }
        this.hair = hair;
    }

    // used by script
    public void setFace(int face) {
        if (!WzDataStorage.FACE.check(face)) {
            DebugLogger.ErrorLog("Invalid face id : " + face);
            this.face = DWI_Dafault.FACE;
            return;
        }
        this.face = face;
    }

    // used by script
    public void setDojoRecord(boolean reset) {
        if (reset) {
            dojo = 0;
            dojoRecord = 0;
        } else {
            dojoRecord++;
        }
    }

    // used by script
    public void addFame(int famechange) {
        this.fame += famechange;
    }

    // used by script
    public void changeJob(int newJob) {
        try {
            boolean isEv = GameConstants.isEvan(job) || GameConstants.isResist(job);
            this.job = (short) newJob;
            if (newJob != 0 && newJob != 1000 && newJob != 2000 && newJob != 2001 && newJob != 3000) {
                if (isEv) {
                    remainingSp[GameConstants.getSkillBook(newJob)] += 5;
                    SendPacket(ResCWvsContext.Message(OpsMessage.MS_IncSPMessage, PB_Message.builder().JobID((short) newJob).Inc_SP((byte) 5).build()));
                } else {
                    remainingSp[GameConstants.getSkillBook(newJob)]++;
                    if (newJob % 10 >= 2) {
                        remainingSp[GameConstants.getSkillBook(newJob)] += 2;
                    }
                }
            }
            if (newJob > 0 && !isGM()) {
                resetStatsByJob(true);
                if (!GameConstants.isEvan(newJob)) {
                    if (getLevel() > (newJob == 200 ? 8 : 10) && newJob % 100 == 0 && (newJob % 1000) / 100 > 0) { //first job
                        remainingSp[GameConstants.getSkillBook(newJob)] += 3 * (getLevel() - (newJob == 200 ? 8 : 10));
                    }
                } else if (newJob == 2200) {
                    MapleQuest.getInstance(22100).forceStart(this, 0, null);
                    MapleQuest.getInstance(22100).forceComplete(this, 0);
                    expandInventory((byte) 1, 4);
                    expandInventory((byte) 2, 4);
                    expandInventory((byte) 3, 4);
                    expandInventory((byte) 4, 4);
                    SendPacket(ResCScriptMan.getEvanTutorial("UI/tutorial/evan/14/0"));
                    Notice("The baby Dragon hatched and appears to have something to tell you. Click the baby Dragon to start a conversation.");
                }
            }
            sendStatChanged();

            int maxhp = stats.getMaxHp(), maxmp = stats.getMaxMp();

            switch (job) {
                case 100: // Warrior
                case 1100: // Soul Master
                case 2100: // Aran
                case 3200:
                    maxhp += Randomizer.rand(200, 250);
                    break;
                case 200: // Magician
                case 2200: //evan
                case 2210: //evan
                    maxmp += Randomizer.rand(100, 150);
                    break;
                case 300: // Bowman
                case 400: // Thief
                case 500: // Pirate
                case 3300:
                case 3500:
                    maxhp += Randomizer.rand(100, 150);
                    maxmp += Randomizer.rand(25, 50);
                    break;
                case 110: // Fighter
                    maxhp += Randomizer.rand(300, 350);
                    break;
                case 120: // Page
                case 130: // Spearman
                case 1110: // Soul Master
                case 2110: // Aran
                case 3210:
                    maxhp += Randomizer.rand(300, 350);
                    break;
                case 210: // FP
                case 220: // IL
                case 230: // Cleric
                    maxmp += Randomizer.rand(400, 450);
                    break;
                case 310: // Bowman
                case 320: // Crossbowman
                case 410: // Assasin
                case 420: // Bandit
                case 430: // Semi Dualer
                case 1310: // Wind Breaker
                case 1410: // Night Walker
                case 3310:
                case 3510:
                    maxhp += Randomizer.rand(300, 350);
                    maxhp += Randomizer.rand(150, 200);
                    break;
                case 900: // GM
                case 800: // Manager
                    maxhp += 30000;
                    maxhp += 30000;
                    break;
            }
            if (maxhp >= 30000) {
                maxhp = 30000;
            }
            if (maxmp >= 30000) {
                maxmp = 30000;
            }
            stats.setMaxHp((short) maxhp);
            stats.setMaxMp((short) maxmp);
            stats.setHp((short) maxhp);
            stats.setMp((short) maxmp);
            stats.recalcLocalStats();
            sendStatChanged();

            PB_UserEffect pb = PB_UserEffect.builder()
                    .player(this)
                    .build();
            map.splitSendPacket(this, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_JobChanged, pb), getId());
            silentPartyUpdate();
            guildUpdate();
            familyUpdate();
            baseSkills();

            // 転職時にDBへ反映する
            saveToDB(false);
        } catch (Exception e) {
        }
    }

    // used by script
    public void gainAp(short ap) {
        this.remainingAp += ap;
        sendStatChanged();
    }

    // used by script
    public void gainSP(int sp) {
        this.remainingSp[GameConstants.getSkillBook(job)] += sp; //default
        sendStatChanged(false);
        SendPacket(ResCWvsContext.Message(OpsMessage.MS_IncSPMessage, PB_Message.builder().JobID((short) job).Inc_SP((byte) sp).build()));
    }

    // used by script
    public void gainSP(int sp, int skillbook) {
        this.remainingSp[skillbook] += sp; //default
        sendStatChanged(false);
        SendPacket(ResCWvsContext.Message(OpsMessage.MS_IncSPMessage, PB_Message.builder().JobID((short) job).Inc_SP((byte) sp).build()));
    }

    // used by script
    public void resetAPSP() {
        for (int i = 0; i < remainingSp.length; i++) {
            this.remainingSp[i] = 0;
        }
        gainAp((short) -this.remainingAp);
        sendStatChanged();
    }

    // used by script
    public void addHP(int delta) {
        if (stats.setHp(stats.getHp() + delta)) {
            sendStatChanged();
        }
    }

    // used by script
    public void gainExp(int total, boolean show, boolean inChat, boolean white) {
        try {
            int prevexp = getExp();
            int needed = TacosSharedExpTable.getExpNeededForLevel(level);
            if (level >= 200 || (GameConstants.isKOC(job) && level >= 120)) {
                if (exp + total > needed) {
                    setExp(needed);
                } else {
                    exp += total;
                }
            } else {
                boolean leveled = false;
                if (exp + total >= needed) {
                    exp += total;
                    levelUp();
                    leveled = true;
                    needed = TacosSharedExpTable.getExpNeededForLevel(level);
                    if (exp > needed) {
                        setExp(needed);
                    }
                } else {
                    exp += total;
                }
                if (total > 0) {
                    familyRep(prevexp, needed, leveled);
                }
            }
            if (total != 0) {
                if (exp < 0) { // After adding, and negative
                    if (total > 0) {
                        setExp(needed);
                    } else if (total < 0) {
                        setExp(0);
                    }
                }
                sendStatChanged();
                if (show) { // still show the expgain even if it's not there
                    SendPacket(ResCWvsContext.Message(OpsMessage.MS_IncEXPMessage, PB_Message.builder().Inc_EXP_TextColor(white ? 1 : 0).Inc_EXP(total).InChat(inChat ? 1 : 0).build()));
                }
                if (total > 0) {
                    stats.checkEquipLevels(total); //gms like
                }
            }
        } catch (Exception e) {
        }
    }

    // used by script
    public int getSavedLocation(SavedLocationType type) {
        return savedLocations[type.getValue()];
    }

    // used by script
    public void saveLocation(SavedLocationType type) {
        savedLocations[type.getValue()] = getMapId();
    }

    // used by script
    public void clearSavedLocation(SavedLocationType type) {
        savedLocations[type.getValue()] = -1;
    }

    // used by script
    public boolean gainMeso(int gain, boolean show, boolean enableActions, boolean inChat) {
        if (meso + gain < 0) {
            updateStat();
            return false;
        }
        meso += gain;
        sendStatChanged(enableActions);
        if (show) {
            SendPacket((!inChat
                    ? ResCWvsContext.Message(OpsMessage.MS_DropPickUpMessage, PB_Message.builder().dt(OpsDropPickUpMessage.PICKUP_MESO).Inc_Meso(gain).build())
                    : ResCWvsContext.Message(OpsMessage.MS_IncMoneyMessage, PB_Message.builder().Inc_Meso(gain).build())));
        }
        return true;
    }

    // used by script
    public byte getSkillLevel(Skill skill) {
        // 存在しないスキルID
        if (skill == null) {
            return 0;
        }

        SkillEntry ret = skills.get(skill);
        if (ret == null || ret.skillevel <= 0) {
            return 0;
        }
        return (byte) Math.min(skill.getMaxLevel(), ret.skillevel);
    }

    // used by script
    public byte getMasterLevel(int skill) {
        return getMasterLevel(WzXML.SKILL.getSkill().get(skill));
    }

    // used by script
    public byte getMasterLevel(Skill skill) {
        // 存在しないスキルID
        if (skill == null) {
            return 0;
        }
        SkillEntry ret = skills.get(skill);
        if (ret == null) {
            return 0;
        }
        return ret.masterlevel;
    }

    // used by script
    @Override
    public int getObjectId() {
        return getId();
    }

    // used by script
    public MapleParty getParty() {
        return party;
    }

    // used by script
    public MapleGuild getGuild() {
        if (getGuildId() <= 0) {
            return null;
        }
        return getWorld().getGuild().getGuild(getGuildId());
    }

    // used by script
    public void modifyCSPoints(int type, int quantity, boolean show) {

        switch (type) {
            case 1:
                if (nexonPoint + quantity < 0) {
                    if (show) {
                        dropMessage(-1, "You have gained the max cash. No cash will be awarded.");
                    }
                    return;
                }
                nexonPoint += quantity;
                break;
            case 2:
                if (maplePoint + quantity < 0) {
                    if (show) {
                        dropMessage(-1, "You have gained the max maple points. No cash will be awarded.");
                    }
                    return;
                }
                maplePoint += quantity;
                break;
            default:
                break;
        }
        if (show && quantity != 0) {
            dropMessage(-1, "You have " + (quantity > 0 ? "gained " : "lost ") + quantity + (type == 1 ? " cash." : " maple points."));
            //SendPacket(MaplePacketCreator.showSpecialEffect(19));
        }
    }

    // used by script
    public boolean hasEquipped(int item_id) {
        return inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(item_id) >= 1;
    }

    // used by script
    public boolean haveItem(int item_id, int quantity) {
        return haveItem(item_id, quantity, true, true);
    }

    // used by script
    public boolean haveItem(int item_id) {
        return haveItem(item_id, 1, true, true);
    }

    // used by script
    public boolean haveItem(int item_id, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        MapleInventoryType type = GameConstants.getInventoryType(item_id);
        int possesed = inventory[type.ordinal()].countById(item_id);
        if (checkEquipped && type == MapleInventoryType.EQUIP) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(item_id);
        }
        if (greaterOrEquals) {
            return possesed >= quantity;
        } else {
            return possesed == quantity;
        }
    }

    // used by script
    public void dropMessage(int type, String message) {
        switch (type) {
            case -1 -> {
                SendPacket(ResCWvsContext.ScriptProgressMessage(message));
            }
            case -2 -> {
                SendPacket(ResCMiniRoomBaseDlg.shopChat(message, 0)); //0 or what
            }
            default -> {
                SendPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.find((byte) type), PB_BroadcastMsg.builder().message(message).build()));
            }
        }
    }

    // used by script
    public MapleCarnivalParty getCarnivalParty() {
        return carnivalParty;
    }

    // used by script
    public int getTotalCP() {
        return totalCP;
    }

    // used by script
    public void addCarnivalRequest(MapleCarnivalChallenge request) {
        pendingCarnivalRequests.add(request);
    }

    // used by script
    public MapleCarnivalChallenge getNextCarnivalRequest() {
        return pendingCarnivalRequests.pollLast();
    }

    // used by script
    public void setPoints(int p) {
        this.points = p;
    }

    // used by script
    public int getPoints() {
        return points;
    }

    // used by script
    public void setVPoints(int p) {
        this.vpoints = p;
    }

    // used by script
    public int getVPoints() {
        return vpoints;
    }

    // used by script
    public void removeAll(int id) {
        MapleInventoryType type = GameConstants.getInventoryType(id);
        int possessed = getInventory(type).countById(id);

        if (possessed > 0) {
            MapleInventoryManipulator.removeById(this, type, id, possessed, true, false);

            PB_UserEffect pb = PB_UserEffect.builder()
                    .item_id(id)
                    .item_quantity(-possessed)
                    .build();
            SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_Quest, pb));
        }
    }

    // used by script
    public void resetStats(int str, int dex, int int_, int luk) {
        int total = stats.getStr() + stats.getDex() + stats.getLuk() + stats.getInt() + getRemainingAp();

        total -= str;
        stats.setStr((short) str);

        total -= dex;
        stats.setDex((short) dex);

        total -= int_;
        stats.setInt((short) int_);

        total -= luk;
        stats.setLuk((short) luk);

        setRemainingAp((short) total);
        sendStatChanged();
    }

    // used by script
    public int itemQuantity(int itemid) {
        return getInventory(GameConstants.getInventoryType(itemid)).countById(itemid);
    }

    // used by script
    public void expandInventory(byte type, int amount) {
        MapleInventory inv = getInventory(MapleInventoryType.getByType(type));
        inv.addSlot((byte) amount);
        SendPacket(ResCWvsContext.InventoryGrow(type, (byte) inv.getSlotLimit()));
    }

    // used by script
    public String getOneInfo(int questid, String key) {
        if (!questinfo.containsKey(questid) || key == null) {
            return null;
        }
        String[] split = questinfo.get(questid).split(";");
        for (String x : split) {
            String[] split2 = x.split("="); //should be only 2
            if (split2.length == 2 && split2[0].equals(key)) {
                return split2[1];
            }
        }
        return null;
    }

    // used by script
    public void updateOneInfo(int questid, String key, String value) {
        if (!questinfo.containsKey(questid) || key == null || value == null) {
            return;
        }
        String[] split = questinfo.get(questid).split(";");
        boolean changed = false;
        StringBuilder newQuest = new StringBuilder();
        for (String x : split) {
            String[] split2 = x.split("="); //should be only 2
            if (split2.length != 2) {
                continue;
            }
            if (split2[0].equals(key)) {
                newQuest.append(key).append("=").append(value);
            } else {
                newQuest.append(x);
            }
            newQuest.append(";");
            changed = true;
        }

        updateInfoQuest(questid, changed ? newQuest.toString().substring(0, newQuest.toString().length() - 1) : newQuest.toString());
    }

    // used by script
    public void tryPartyQuest(int questid) {
        try {
            startPartyQuest(questid);
            pqStartTime = System.currentTimeMillis();
            updateOneInfo(questid, "try", String.valueOf(Integer.parseInt(getOneInfo(questid, "try")) + 1));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("tryPartyQuest error");
        }
    }

    // used by script
    public void endPartyQuest(int questid) {
        try {
            startPartyQuest(questid);
            if (pqStartTime > 0) {
                long changeTime = System.currentTimeMillis() - pqStartTime;
                int mins = (int) (changeTime / 1000 / 60), secs = (int) (changeTime / 1000 % 60);
                int mins2 = Integer.parseInt(getOneInfo(questid, "min"));
                Integer.parseInt(getOneInfo(questid, "sec"));
                if (mins2 <= 0 || mins < mins2) {
                    updateOneInfo(questid, "min", String.valueOf(mins));
                    updateOneInfo(questid, "sec", String.valueOf(secs));
                    updateOneInfo(questid, "date", TacosSharedDate.getDateString());
                }
                int newCmp = Integer.parseInt(getOneInfo(questid, "cmp")) + 1;
                updateOneInfo(questid, "cmp", String.valueOf(newCmp));
                updateOneInfo(questid, "CR", String.valueOf((int) Math.ceil((newCmp * 100.0) / Integer.parseInt(getOneInfo(questid, "try")))));
                recalcPartyQuestRank(questid);
                pqStartTime = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("endPartyQuest error");
        }

    }

    // used by script
    public void resetStatsByJob(boolean beginnerJob) {
        int baseJob = (beginnerJob ? (job % 1000) : (job % 1000 / 100 * 100)); //1112 -> 112 -> 1 -> 100
        if (baseJob == 100) { //first job = warrior
            resetStats(25, 4, 4, 4);
        } else if (baseJob == 200) {
            resetStats(4, 4, 20, 4);
        } else if (baseJob == 300 || baseJob == 400) {
            resetStats(4, 25, 4, 4);
        } else if (baseJob == 500) {
            resetStats(4, 20, 4, 4);
        }
    }

    // used by script
    public boolean StartPachinko(int type) {
        SendPacket(Res_JMS_CField_Pachinko.openBeans(this, type));
        return true;
    }

    public class PlayerStats {

        public int str;
        public int dex;
        public int luk;
        public int int_;
        public int hp;
        public int maxhp;
        public int mp;
        public int maxmp;
        public int equipmentBonusExp;
        public int expMod;
        public int dropMod;
        public int levelBonus;
        public boolean equippedWelcomeBackRing;
        public boolean hasPartyBonus;
        private final List<Equip> durabilityHandling = new ArrayList<>();
        private final List<Equip> equipLevelHandling = new ArrayList<>();
        private boolean isRecalc = false;
        private short localmaxhp;
        private short localmaxmp;
        private int localstr;
        private int localdex;
        private int localluk;
        private int localint_;
        private int magic;
        private int watk;

        public int getStr() {
            return str;
        }

        public int getDex() {
            return dex;
        }

        public int getLuk() {
            return luk;
        }

        public int getInt() {
            return int_;
        }

        public void setStr(int str) {
            this.str = str;
            recalcLocalStats();
        }

        public void setDex(int dex) {
            this.dex = dex;
            recalcLocalStats();
        }

        public void setLuk(int luk) {
            this.luk = luk;
            recalcLocalStats();
        }

        public void setInt(int int_) {
            this.int_ = int_;
            recalcLocalStats();
        }

        public boolean setHp(int newhp) {
            return setHp(newhp, false);
        }

        public boolean setHp(int newhp, boolean silent) {
            int oldHp = hp;
            hp = Math.max(0, Math.min(newhp, localmaxhp));
            if (!silent) {
                updatePartyMemberHP();
            }
            if (oldHp > hp && !isAlive()) {
                playerDead();
            }
            return hp != oldHp;
        }

        public boolean setMp(int newmp) {
            int oldMp = mp;
            mp = Math.max(0, Math.min(newmp, localmaxmp));
            return mp != oldMp;
        }

        public void setMaxHp(int hp) {
            this.maxhp = hp;
            recalcLocalStats();
        }

        public void setMaxMp(int mp) {
            this.maxmp = mp;
            recalcLocalStats();
        }

        public int getHp() {
            return hp;
        }

        public int getMaxHp() {
            return maxhp;
        }

        public int getMp() {
            return mp;
        }

        public int getMaxMp() {
            return maxmp;
        }

        public int getTotalDex() {
            return localdex;
        }

        public int getTotalInt() {
            return localint_;
        }

        public int getTotalStr() {
            return localstr;
        }

        public int getTotalLuk() {
            return localluk;
        }

        public int getTotalMagic() {
            return magic;
        }

        public int getTotalWatk() {
            return watk;
        }

        public short getCurrentMaxHp() {
            return localmaxhp;
        }

        public short getCurrentMaxMp() {
            return localmaxmp;
        }

        public void recalcLocalStats() {
            recalcLocalStats(false);
        }

        public void recalcLocalStats(boolean first_login) {
            if (isRecalc) {
                return;
            }
            isRecalc = true;
            int oldmaxhp = localmaxhp;
            int localmaxhp_ = maxhp;
            int localmaxmp_ = maxmp;
            int percent_hp = 0;
            int percent_mp = 0;
            localdex = dex;
            localint_ = int_;
            localstr = str;
            localluk = luk;
            magic = localint_;
            watk = 0;
            if (job == 500 || (520 <= job && job <= 522)) {
                watk = 20; // bullet
            } else if (job == 400 || (410 <= job && job <= 412) || (1400 <= job && job <= 1412)) {
                watk = 30; // stars
            }
            equippedWelcomeBackRing = false;
            hasPartyBonus = false;
            boolean canEquipLevel = level >= 120 && !GameConstants.isKOC(job);
            equipmentBonusExp = 0;
            dropMod = 1;
            expMod = 1;
            levelBonus = 0;
            durabilityHandling.clear();
            equipLevelHandling.clear();

            for (Item item : getInventory(MapleInventoryType.EQUIPPED)) {
                Equip equip = (Equip) item;
                localmaxhp_ += equip.getHp();
                localmaxmp_ += equip.getMp();
                localdex += equip.getDex();
                localint_ += equip.getInt();
                localstr += equip.getStr();
                localluk += equip.getLuk();
                magic += equip.getMatk() + equip.getInt();
                watk += equip.getWatk();
                if (equip.getItemId() == 1112127) {
                    equippedWelcomeBackRing = true;
                } else {
                    for (int eb_bonus : GameConstants.Equipments_Bonus) {
                        if (equip.getItemId() == eb_bonus) {
                            equipmentBonusExp += GameConstants.Equipment_Bonus_EXP(eb_bonus);
                            break;
                        }
                    }
                }
                percent_hp += equip.getHpR();
                percent_mp += equip.getMpR();
                if (equip.getHidden() > 1) {
                    if (equip.getDurability() > 0) {
                        durabilityHandling.add(equip);
                    }
                    int max_level = GameConstants.getMaxLevel(equip.getItemId());
                    if (canEquipLevel && max_level > 0 && (GameConstants.getStatFromWeapon(equip.getItemId()) == null ? equip.getEquipLevel() <= max_level : equip.getEquipLevel() < max_level)) {
                        equipLevelHandling.add(equip);
                    }
                }
            }
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            for (Item item : getInventory(MapleInventoryType.CASH)) {
                int item_id = item.getItemId();
                if (expMod < 3 && (item_id == 5211060 || item_id == 5211050 || item_id == 5211051 || item_id == 5211052 || item_id == 5211053 || item_id == 5211054)) {
                    expMod = 3; // overwrite
                } else if (expMod == 1 && (item_id == 5210000 || item_id == 5210001 || item_id == 5210002 || item_id == 5210003 || item_id == 5210004 || item_id == 5210005 || item_id == 5211061 || item_id == 5211000 || item_id == 5211001 || item_id == 5211002 || item_id == 5211003 || item_id == 5211046 || item_id == 5211047 || item_id == 5211048 || item_id == 5211049)) {
                    expMod = 2;
                } else if (expMod == 1 && item_id == 5210006 && (hour >= 22 || hour <= 2)) {
                    expMod = 2;
                } else if (expMod == 1 && item_id == 5210007 && hour >= 2 && hour <= 6) {
                    expMod = 2;
                } else if (expMod == 1 && item_id == 5210008 && hour >= 6 && hour <= 10) {
                    expMod = 2;
                } else if (expMod == 1 && item_id == 5210009 && hour >= 10 && hour <= 14) {
                    expMod = 2;
                } else if (expMod == 1 && item_id == 5210010 && hour >= 14 && hour <= 18) {
                    expMod = 2;
                } else if (expMod == 1 && item_id == 5210011 && hour >= 18 && hour <= 22) {
                    expMod = 2;
                }
                if (dropMod == 1) {
                    if (item_id == 5360009 || item_id == 5360010 || item_id == 5360011 || item_id == 5360012 || item_id == 5360013 || item_id == 5360014 || item_id == 5360017 || item_id == 5360050 || item_id == 5360053 || item_id == 5360042 || item_id == 5360052) {
                        dropMod = 2;
                    } else if (item_id == 5360000 && hour >= 0 && hour <= 6) {
                        dropMod = 2;
                    } else if (item_id == 5360001 && hour >= 6 && hour <= 12) {
                        dropMod = 2;
                    } else if (item_id == 5360002 && hour >= 12 && hour <= 18) {
                        dropMod = 2;
                    } else if (item_id == 5360003 && hour >= 18 && hour <= 24) {
                        dropMod = 2;
                    }
                }
                if (item_id == 5650000) {
                    hasPartyBonus = true;
                } else if (item_id == 5590001) {
                    levelBonus = 10;
                } else if (levelBonus == 0 && item_id == 5590000) {
                    levelBonus = 5;
                }
            }
            magic += getSkillLevel(WzXML.SKILL.getSkill().get(22000000));
            localmaxhp_ += (percent_hp * localmaxhp_) / 100f;
            localmaxmp_ += (percent_mp * localmaxmp_) / 100f;
            magic = Math.min(magic, 1999); // buffs can make it higher

            switch (job) {
                case 322: { // Crossbowman
                    watk += getExpertWatk(3220004);
                    break;
                }
                case 312: { // Bowmaster
                    watk += getExpertWatk(3120005);
                    break;
                }
                case 2112: { // Aran
                    watk += getExpertWatk(21120001);
                    break;
                }
                default: {
                    break;
                }
            }
            Skill blessoffairy = WzXML.SKILL.getSkill().get(GameConstants.getBOF_ForJob(job));
            int boflevel = getSkillLevel(blessoffairy);
            if (boflevel > 0) {
                watk += blessoffairy.getEffect(boflevel).getX();
                magic += blessoffairy.getEffect(boflevel).getY();
            }

            localmaxhp = (short) Math.min(30000, Math.abs(Math.max(-30000, localmaxhp_)));
            localmaxmp = (short) Math.min(30000, Math.abs(Math.max(-30000, localmaxmp_)));

            if (first_login) {
                silentEnforceMaxHpMp();
            } else {
                enforceMaxHpMp();
            }

            if (oldmaxhp != 0 && oldmaxhp != localmaxhp) {
                updatePartyMemberHP();
            }
            isRecalc = false;
        }

        private int getExpertWatk(int skill_id) {
            Skill expert = WzXML.SKILL.getSkill().get(skill_id);
            int boostLevel = getSkillLevel(expert);
            if (boostLevel <= 0) {
                return 0;
            }
            return expert.getEffect(boostLevel).getX();
        }

        public boolean checkEquipLevels(int gain) {
            boolean changed = false;
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            for (Equip eq : new ArrayList<>(equipLevelHandling)) {
                int lvlz = eq.getEquipLevel();
                eq.setItemEXP(eq.getItemEXP() + gain);

                if (eq.getEquipLevel() > lvlz) { // lvlup
                    for (int i = eq.getEquipLevel() - lvlz; i > 0; i--) {
                        int equip_level = lvlz + i;
                        // now for the equipment increments...
                        Map<Integer, Map<String, Integer>> inc = ii.getEquipIncrements(eq.getItemId());
                        if (inc != null && inc.containsKey(equip_level)) { // flair = 1
                            eq = ii.levelUpEquip(eq, inc.get(equip_level));
                        }
                        if (GameConstants.getStatFromWeapon(eq.getItemId()) == null) {
                            learnEquipSkills(ii.getEquipSkills(eq.getItemId()), equip_level);
                        }
                    }
                    changed = true;
                }
                SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.EQUIPPED, eq.copy()).build()));
            }
            if (changed) {
                equipChanged();

                PB_UserEffect pb = PB_UserEffect.builder()
                        .player(TacosCharacter.this)
                        .build();
                SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_ItemLevelUp));
                getMap().splitSendPacket(TacosCharacter.this, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_ItemLevelUp, pb), getId());
            }
            return changed;
        }

        // skills of the equip level : 10% chance to learn each skill
        private void learnEquipSkills(Map<Integer, List<Integer>> ins, int equip_level) {
            if (ins == null || !ins.containsKey(equip_level)) {
                return;
            }
            for (Integer z : ins.get(equip_level)) {
                if (Math.random() >= 0.1) { // 10% chance
                    continue;
                }
                Skill skil = WzXML.SKILL.getSkill().get(z);
                if (skil != null && skil.canBeLearnedBy(job) && getSkillLevel(skil) < getMasterLevel(skil)) { // dont go over masterlevel
                    changeSkillLevel(skil, (byte) (getSkillLevel(skil) + 1), getMasterLevel(skil));
                }
            }
        }

        public boolean checkEquipDurabilitys(int gain) {
            for (Equip item : durabilityHandling) {
                item.setDurability(Math.max(0, item.getDurability() + gain));
            }
            for (Equip eqq : new ArrayList<>(durabilityHandling)) {
                if (eqq.getDurability() != 0) {
                    SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.EQUIPPED, eqq.copy()).build()));
                    continue;
                }
                if (getInventory(MapleInventoryType.EQUIP).isFull()) {
                    updateInv();
                    SendPacket(ResCWvsContext.Message(OpsMessage.MS_DropPickUpMessage, PB_Message.builder().dt(OpsDropPickUpMessage.PICKUP_INVENTORY_FULL).build()));
                    return false;
                }
                durabilityHandling.remove(eqq);
                short pos = getInventory(MapleInventoryType.EQUIP).getNextFreeSlot();
                MapleInventoryManipulator.unequip(TacosCharacter.this, eqq.getPosition(), pos);
                SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(MapleInventoryType.EQUIP, eqq).build()));
            }
            return true;
        }
    }
}
