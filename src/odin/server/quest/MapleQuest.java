package odin.server.quest;

import odin.constants.GameConstants;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import odin.client.MapleCharacter;
import odin.client.MapleQuestStatus;
import tacos.config.Region;
import tacos.config.Version;
import tacos.wz.QuestWz;
import java.util.ArrayList;
import tacos.packet.ops.OpsUserEffect;
import tacos.packet.response.wrapper.WrapCUserLocal;
import tacos.packet.response.wrapper.WrapCUserRemote;
import odin.tools.FileoutputUtil;
import tacos.odin.OdinPair;
import odin.provider.IMapleData;
import tacos.script.TacosScriptQuest;
import tacos.wz.WzDataTool;
import tacos.wz.WzXML;

public class MapleQuest {

    private static Map<Integer, MapleQuest> quests = new LinkedHashMap<>();
    protected int id;
    protected List<MapleQuestRequirement> startReqs;
    protected List<MapleQuestRequirement> completeReqs;
    protected List<MapleQuestAction> startActs;
    protected List<MapleQuestAction> completeActs;
    protected Map<String, List<OdinPair<String, OdinPair<String, Integer>>>> partyQuestInfo; //[rank, [more/less/equal, [property, value]]]
    protected Map<Integer, Integer> relevantMobs;
    private boolean autoStart = false;
    private boolean autoPreComplete = false;
    private boolean repeatable = false, customend = false;
    private int viewMedalItem = 0, selectedSkillID = 0;
    protected String name = "";

    protected MapleQuest(final int id) {
        relevantMobs = new LinkedHashMap<>();
        startReqs = new LinkedList<>();
        completeReqs = new LinkedList<>();
        startActs = new LinkedList<>();
        completeActs = new LinkedList<>();
        partyQuestInfo = new LinkedHashMap<>();
        this.id = id;
    }

    /**
     * Creates a new instance of MapleQuest
     */
    private static boolean loadQuest(MapleQuest ret, int id) throws NullPointerException {
        // read reqs
        final IMapleData basedata1 = WzXML.QUEST.getCheck().getChildByPath(String.valueOf(id));
        final IMapleData basedata2 = WzXML.QUEST.getAct().getChildByPath(String.valueOf(id));

        if (basedata1 == null || basedata2 == null) {
            return false;
        }
        //-------------------------------------------------
        final IMapleData startReqData = basedata1.getChildByPath("0");
        if (startReqData != null) {
            final List<IMapleData> startC = startReqData.getChildren();
            if (startC != null && startC.size() > 0) {
                for (IMapleData startReq : startC) {
                    final MapleQuestRequirementType type = MapleQuestRequirementType.getByWZName(startReq.getName());
                    if (type.equals(MapleQuestRequirementType.interval)) {
                        ret.repeatable = true;
                    }
                    final MapleQuestRequirement req = new MapleQuestRequirement(ret, type, startReq);
                    if (req.getType().equals(MapleQuestRequirementType.mob)) {
                        for (IMapleData mob : startReq.getChildren()) {
                            ret.relevantMobs.put(WzDataTool.getInt(mob.getChildByPath("id")),
                                    WzDataTool.getInt(mob.getChildByPath("count"), 0));
                        }
                    }
                    ret.startReqs.add(req);
                }
            }
        }
        //-------------------------------------------------
        final IMapleData completeReqData = basedata1.getChildByPath("1");
        if (completeReqData != null) {
            final List<IMapleData> completeC = completeReqData.getChildren();
            if (completeC != null && completeC.size() > 0) {
                for (IMapleData completeReq : completeC) {
                    MapleQuestRequirement req = new MapleQuestRequirement(ret, MapleQuestRequirementType.getByWZName(completeReq.getName()), completeReq);
                    if (req.getType().equals(MapleQuestRequirementType.mob)) {
                        for (IMapleData mob : completeReq.getChildren()) {
                            ret.relevantMobs.put(WzDataTool.getInt(mob.getChildByPath("id")),
                                    WzDataTool.getInt(mob.getChildByPath("count"), 0));
                        }
                    } else if (req.getType().equals(MapleQuestRequirementType.endscript)) {
                        ret.customend = true;
                    }
                    ret.completeReqs.add(req);
                }
            }
        }
        // read acts
        final IMapleData startActData = basedata2.getChildByPath("0");
        if (startActData != null) {
            final List<IMapleData> startC = startActData.getChildren();
            for (IMapleData startAct : startC) {
                ret.startActs.add(new MapleQuestAction(MapleQuestActionType.getByWZName(startAct.getName()), startAct, ret));
            }
        }
        final IMapleData completeActData = basedata2.getChildByPath("1");

        if (completeActData != null) {
            final List<IMapleData> completeC = completeActData.getChildren();
            for (IMapleData completeAct : completeC) {
                ret.completeActs.add(new MapleQuestAction(MapleQuestActionType.getByWZName(completeAct.getName()), completeAct, ret));
            }
        }

        final IMapleData questInfo = WzXML.QUEST.getQuestInfo().getChildByPath(String.valueOf(id));
        if (questInfo != null) {
            ret.name = WzDataTool.getStringPath("name", questInfo, "");
            ret.autoStart = WzDataTool.getIntPath("autoStart", questInfo, 0) == 1;
            ret.autoPreComplete = WzDataTool.getIntPath("autoPreComplete", questInfo, 0) == 1;
            ret.viewMedalItem = WzDataTool.getIntPath("viewMedalItem", questInfo, 0);
            ret.selectedSkillID = WzDataTool.getIntPath("selectedSkillID", questInfo, 0);
        }

        // not in KMS55
        if (Version.GreaterOrEqual(Region.KMS, 65)) {
            final IMapleData pquestInfo = WzXML.QUEST.getPQuest().getChildByPath(String.valueOf(id));
            if (pquestInfo != null) {
                for (IMapleData d : pquestInfo.getChildByPath("rank")) {
                    List<OdinPair<String, OdinPair<String, Integer>>> pInfo = new ArrayList<>();
                    //LinkedHashMap<String, List<Pair<String, Pair<String, Integer>>>>
                    for (IMapleData c : d) {
                        for (IMapleData b : c) {
                            pInfo.add(new OdinPair<>(c.getName(), new OdinPair<>(b.getName(), WzDataTool.getInt(b, 0))));
                        }
                    }
                    ret.partyQuestInfo.put(d.getName(), pInfo);
                }
            }
        }

        return true;
    }

    public List<OdinPair<String, OdinPair<String, Integer>>> getInfoByRank(final String rank) {
        return partyQuestInfo.get(rank);
    }

    public final int getSkillID() {
        return selectedSkillID;
    }

    public final String getName() {
        return name;
    }

    public static void clearQuests() {
        quests.clear();
    }

    public static MapleQuest getInstance(int quest_id) {
        MapleQuest ret = quests.get(quest_id);
        if (ret == null) {
            ret = new MapleQuest(quest_id);
            try {
                if (GameConstants.isCustomQuest(quest_id) || !loadQuest(ret, quest_id)) {
                    ret = new MapleCustomQuest(quest_id);
                }
                quests.put(quest_id, ret);
            } catch (Exception ex) {
                ex.printStackTrace();
                FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
                FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Caused by questID " + quest_id);
                System.out.println("Caused by questID " + quest_id);
                return new MapleCustomQuest(quest_id);
            }
        }
        return ret;
    }

    public boolean canStart(MapleCharacter chr, Integer npcid) {
        if (chr.getQuest(this).getStatus() != 0 && !(chr.getQuest(this).getStatus() == 2 && repeatable)) {
            return false;
        }
        for (MapleQuestRequirement r : startReqs) {
            if (!r.check(chr, npcid)) {
                return false;
            }
        }
        return true;
    }

    public boolean canComplete(MapleCharacter chr, Integer npcid) {
        if (chr.getQuest(this).getStatus() != 1) {
            return false;
        }
        for (MapleQuestRequirement r : completeReqs) {
            if (!r.check(chr, npcid)) {
                return false;
            }
        }
        return true;
    }

    public void RestoreLostItem(MapleCharacter chr, int item_id) {
        for (MapleQuestAction a : startActs) {
            if (a.RestoreLostItem(chr, item_id)) {
                break;
            }
        }
    }

    public void start(MapleCharacter chr, int npc_id) {
        if ((autoStart || checkNPCOnMap(chr, npc_id)) && canStart(chr, npc_id)) {
            for (MapleQuestAction a : startActs) {
                if (!a.checkEnd(chr, null)) { //just in case
                    return;
                }
            }
            for (MapleQuestAction a : startActs) {
                a.runStart(chr, null);
            }
            if (!customend) {
                forceStart(chr, npc_id, null);
            } else {
                TacosScriptQuest.getInstance().endQuest(chr.getClient(), npc_id, getId(), true);
            }
        }
    }

    public void complete(MapleCharacter chr, int npc_id) {
        complete(chr, npc_id, null);
    }

    public void complete(MapleCharacter chr, int npc_id, Integer selection) {
        if ((autoPreComplete || checkNPCOnMap(chr, npc_id)) && canComplete(chr, npc_id)) {
            for (MapleQuestAction a : completeActs) {
                if (!a.checkEnd(chr, selection)) {
                    return;
                }
            }
            forceComplete(chr, npc_id);
            for (MapleQuestAction a : completeActs) {
                a.runEnd(chr, selection);
            }

            chr.SendPacket(WrapCUserLocal.EffectLocal(OpsUserEffect.UserEffect_QuestComplete));
            chr.getMap().broadcastMessage(chr, WrapCUserRemote.EffectRemote(OpsUserEffect.UserEffect_QuestComplete, chr), false);
        }
    }

    public void forfeit(MapleCharacter chr) {
        if (chr.getQuest(this).getStatus() != (byte) 1) {
            return;
        }
        final MapleQuestStatus oldStatus = chr.getQuest(this);
        final MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte) 0);
        newStatus.setForfeited(oldStatus.getForfeited() + 1);
        newStatus.setCompletionTime(oldStatus.getCompletionTime());
        chr.updateQuest(newStatus);
    }

    public void forceStart(MapleCharacter c, int npc, String customData) {
        final MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte) 1, npc);
        newStatus.setForfeited(c.getQuest(this).getForfeited());
        newStatus.setCompletionTime(c.getQuest(this).getCompletionTime());
        newStatus.setCustomData(customData);
        c.updateQuest(newStatus);
    }

    public void forceComplete(MapleCharacter c, int npc) {
        final MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte) 2, npc);
        newStatus.setForfeited(c.getQuest(this).getForfeited());
        c.updateQuest(newStatus);
    }

    public int getId() {
        return id;
    }

    public Map<Integer, Integer> getRelevantMobs() {
        return relevantMobs;
    }

    private boolean checkNPCOnMap(MapleCharacter player, int npcid) {
        //mir = 1013000
        return (GameConstants.isEvan(player.getJob()) && npcid == 1013000) || (player.getMap() != null && player.getMap().containsNPC(npcid));
    }

    public int getMedalItem() {
        return viewMedalItem;
    }

    public static enum MedalQuest {

        Beginner(29005, 29015, 15, new int[]{104000000, 104010001, 100000006, 104020000, 100000000, 100010000, 100040000, 100040100, 101010103, 101020000, 101000000, 102000000, 101030104, 101030406, 102020300, 103000000, 102050000, 103010001, 103030200, 110000000}),
        ElNath(29006, 29012, 50, new int[]{200000000, 200010100, 200010300, 200080000, 200080100, 211000000, 211030000, 211040300, 211041200, 211041800}),
        LudusLake(29007, 29012, 40, new int[]{222000000, 222010400, 222020000, 220000000, 220020300, 220040200, 221020701, 221000000, 221030600, 221040400}),
        Underwater(29008, 29012, 40, new int[]{230000000, 230010400, 230010200, 230010201, 230020000, 230020201, 230030100, 230040000, 230040200, 230040400}),
        MuLung(29009, 29012, 50, new int[]{251000000, 251010200, 251010402, 251010500, 250010500, 250010504, 250000000, 250010300, 250010304, 250020300}),
        NihalDesert(29010, 29012, 70, new int[]{261030000, 261020401, 261020000, 261010100, 261000000, 260020700, 260020300, 260000000, 260010600, 260010300}),
        MinarForest(29011, 29012, 70, new int[]{240000000, 240010200, 240010800, 240020401, 240020101, 240030000, 240040400, 240040511, 240040521, 240050000}),
        Sleepywood(29014, 29015, 50, new int[]{105040300, 105070001, 105040305, 105090200, 105090300, 105090301, 105090312, 105090500, 105090900, 105080000});
        public int questid, level, lquestid;
        public int[] maps;

        private MedalQuest(int questid, int lquestid, int level, int[] maps) {
            this.questid = questid; //infoquest = questid -2005, customdata = questid -1995
            this.level = level;
            this.lquestid = lquestid;
            this.maps = maps; //note # of maps
        }
    }
}
