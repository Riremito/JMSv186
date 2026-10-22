package odin.server.quest;

import odin.constants.GameConstants;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import odin.client.MapleQuestStatus;
import tacos.packet.ops.OpsUserEffect;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.wz.QuestWz;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import tacos.packet.response.builder.PB_UserEffect;
import tacos.script.TacosScriptQuest;
import tacos.wz.WzXML;

public class MapleQuest {

    private static final Map<Integer, MapleQuest> quests = new LinkedHashMap<>();
    private int id;
    private List<MapleQuestRequirement> startReqs;
    private List<MapleQuestRequirement> completeReqs;
    private List<MapleQuestAction> startActs;
    private List<MapleQuestAction> completeActs;
    private Map<Integer, Integer> relevantMobs;
    private String name = "";
    private boolean autoStart = false;
    private boolean autoPreComplete = false;
    private boolean repeatable = false;
    private boolean customend = false;
    private int viewMedalItem = 0;
    private int selectedSkillID = 0;
    private Map<String, List<SimpleImmutableEntry<String, SimpleImmutableEntry<String, Integer>>>> partyQuestInfo; //[rank, [more/less/equal, [property, value]]]

    private MapleQuest(int id) {
        relevantMobs = new LinkedHashMap<>();
        startReqs = new LinkedList<>();
        completeReqs = new LinkedList<>();
        startActs = new LinkedList<>();
        completeActs = new LinkedList<>();
        partyQuestInfo = new LinkedHashMap<>();
        this.id = id;
    }

    private static boolean loadQuest(MapleQuest ret, int id) {
        QuestWz.QuestInfo info = WzXML.QUEST.getQuestInfo(id);
        if (info == null) {
            return false;
        }

        for (QuestWz.QuestRequirementInfo req : info.getStartReqs()) {
            ret.startReqs.add(new MapleQuestRequirement(ret, req));
        }
        for (QuestWz.QuestRequirementInfo req : info.getCompleteReqs()) {
            ret.completeReqs.add(new MapleQuestRequirement(ret, req));
        }
        for (QuestWz.QuestActionInfo act : info.getStartActs()) {
            ret.startActs.add(new MapleQuestAction(act, ret));
        }
        for (QuestWz.QuestActionInfo act : info.getCompleteActs()) {
            ret.completeActs.add(new MapleQuestAction(act, ret));
        }
        ret.relevantMobs.putAll(info.getRelevantMobs());
        ret.repeatable = info.isRepeatable();
        ret.customend = info.isCustomend();
        ret.name = info.getName();
        ret.autoStart = info.isAutoStart();
        ret.autoPreComplete = info.isAutoPreComplete();
        ret.viewMedalItem = info.getViewMedalItem();
        ret.selectedSkillID = info.getSelectedSkillID();
        ret.partyQuestInfo.putAll(info.getPartyQuestInfo());
        return true;
    }

    public List<SimpleImmutableEntry<String, SimpleImmutableEntry<String, Integer>>> getInfoByRank(String rank) {
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

    public boolean canStart(TacosCharacter chr, Integer npcid) {
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

    public boolean canComplete(TacosCharacter chr, Integer npcid) {
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

    public void RestoreLostItem(TacosCharacter chr, int item_id) {
        for (MapleQuestAction a : startActs) {
            if (a.RestoreLostItem(chr, item_id)) {
                break;
            }
        }
    }

    public void start(TacosCharacter chr, int npc_id) {
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

    public void complete(TacosCharacter chr, int npc_id) {
        complete(chr, npc_id, null);
    }

    public void complete(TacosCharacter chr, int npc_id, Integer selection) {
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
            chr.getMap().splitSendPacket(chr, ResCUserRemote.UserEffectRemote(OpsUserEffect.UserEffect_QuestComplete, pb), chr.getId());
        }
    }

    public void forfeit(TacosCharacter chr) {
        if (chr.getQuest(this).getStatus() != (byte) 1) {
            return;
        }
        MapleQuestStatus oldStatus = chr.getQuest(this);
        MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte) 0);
        newStatus.setForfeited(oldStatus.getForfeited() + 1);
        newStatus.setCompletionTime(oldStatus.getCompletionTime());
        chr.updateQuest(newStatus);
    }

    public void forceStart(TacosCharacter chr, int npc, String customData) {
        MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte) 1, npc);
        newStatus.setForfeited(chr.getQuest(this).getForfeited());
        newStatus.setCompletionTime(chr.getQuest(this).getCompletionTime());
        newStatus.setCustomData(customData);
        chr.updateQuest(newStatus);
    }

    public void forceComplete(TacosCharacter chr, int npc) {
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

    private boolean checkNPCOnMap(TacosCharacter player, int npcid) {
        //mir = 1013000
        TacosMap map = player.getMap();
        return (GameConstants.isEvan(player.getJob()) && npcid == 1013000) || (map != null && map.containsNPC(npcid));
    }

    public int getMedalItem() {
        return viewMedalItem;
    }
}
