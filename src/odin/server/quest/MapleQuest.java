package odin.server.quest;

import odin.constants.GameConstants;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import odin.client.MapleCharacter;
import odin.client.MapleQuestStatus;
import tacos.config.Region;
import java.util.ArrayList;
import tacos.packet.ops.OpsUserEffect;
import tacos.odin.OdinPair;
import odin.provider.IMapleData;
import tacos.config.Config;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.builder.PB_UserEffect;
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
    protected Map<Integer, Integer> relevantMobs;
    protected String name = "";
    private boolean autoStart = false;
    private boolean autoPreComplete = false;
    private boolean repeatable = false;
    private boolean customend = false;
    private int viewMedalItem = 0;
    private int selectedSkillID = 0;
    protected Map<String, List<OdinPair<String, OdinPair<String, Integer>>>> partyQuestInfo; //[rank, [more/less/equal, [property, value]]]

    protected MapleQuest(final int id) {
        relevantMobs = new LinkedHashMap<>();
        startReqs = new LinkedList<>();
        completeReqs = new LinkedList<>();
        startActs = new LinkedList<>();
        completeActs = new LinkedList<>();
        partyQuestInfo = new LinkedHashMap<>();
        this.id = id;
    }

    private static boolean loadQuest(MapleQuest ret, int id) throws NullPointerException {
        IMapleData check_img = WzXML.QUEST.getCheck();
        IMapleData act_img = WzXML.QUEST.getAct();
        // KMS1
        if (check_img == null || act_img == null) {
            return false;
        }

        IMapleData basedata1 = check_img.getChildByPath(String.valueOf(id));
        IMapleData basedata2 = act_img.getChildByPath(String.valueOf(id));

        if (basedata1 == null || basedata2 == null) {
            return false;
        }
        //-------------------------------------------------
        final IMapleData startReqData = basedata1.getChildByPath("0");
        if (startReqData != null) {
            final List<IMapleData> startC = startReqData.getChildren();
            if (startC != null && !startC.isEmpty()) {
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
            if (completeC != null && !completeC.isEmpty()) {
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
        if (Config.GreaterOrEqual(Region.KMS, 65)) {
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

    public List<OdinPair<String, OdinPair<String, Integer>>> getInfoByRank(String rank) {
        return partyQuestInfo.get(rank);
    }

    public int getSkillID() {
        return selectedSkillID;
    }

    public static MapleQuest getInstance(int quest_id) {
        MapleQuest ret = quests.get(quest_id);
        if (ret == null) {
            ret = new MapleQuest(quest_id);
            if (loadQuest(ret, quest_id)) {
                quests.put(quest_id, ret);
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

            PB_UserEffect pb = PB_UserEffect.builder()
                    .player(chr)
                    .build();
            chr.SendPacket(ResCUserLocal.UserEffectLocal(OpsUserEffect.UserEffect_QuestComplete));
            chr.getMap().broadcastMessage(chr, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_QuestComplete, pb), false);
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

    public void forceStart(MapleCharacter chr, int npc, String customData) {
        MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte) 1, npc);
        newStatus.setForfeited(chr.getQuest(this).getForfeited());
        newStatus.setCompletionTime(chr.getQuest(this).getCompletionTime());
        newStatus.setCustomData(customData);
        chr.updateQuest(newStatus);
    }

    public void forceComplete(MapleCharacter chr, int npc) {
        MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte) 2, npc);
        newStatus.setForfeited(chr.getQuest(this).getForfeited());
        chr.updateQuest(newStatus);
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
}
