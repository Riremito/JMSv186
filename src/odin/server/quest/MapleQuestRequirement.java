package odin.server.quest;

import odin.client.Skill;
import tacos.wz.WzXML;
import tacos.client.TacosCharacter;
import java.util.Calendar;
import java.util.List;
import odin.client.inventory.Item;
import odin.constants.GameConstants;
import odin.client.inventory.MapleInventoryType;
import odin.client.MapleQuestStatus;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.wz.QuestWz;

public class MapleQuestRequirement {

    private final MapleQuest quest;
    private final MapleQuestRequirementType type;
    private final int intStore;
    private final String stringStore;
    private final List<SimpleImmutableEntry<Integer, Integer>> dataStore;

    public MapleQuestRequirement(MapleQuest quest, QuestWz.QuestRequirementInfo info) {
        this.type = info.getType();
        this.quest = quest;
        this.intStore = info.getIntStore();
        this.stringStore = info.getStringStore();
        this.dataStore = info.getDataStore();
    }

    public boolean check(TacosCharacter chr, Integer npc_id) {
        switch (type) {
            case job:
                for (SimpleImmutableEntry<Integer, Integer> a : dataStore) {
                    if (a.getValue() == chr.getJob() || chr.isGM()) {
                        return true;
                    }
                }
                return false;
            case skill: {
                for (SimpleImmutableEntry<Integer, Integer> a : dataStore) {
                    boolean acquire = a.getValue() > 0;
                    Skill skil = WzXML.SKILL.getSkill().get(a.getKey());
                    if (acquire) {
                        // 4th job skills are checked by master level.
                        int level = skil.isFourthJob() ? chr.getMasterLevel(skil) : chr.getSkillLevel(skil);
                        if (level == 0) {
                            return false;
                        }
                    } else if (chr.getSkillLevel(skil) > 0 || chr.getMasterLevel(skil) > 0) {
                        return false;
                    }
                }
                return true;
            }
            case quest:
                for (SimpleImmutableEntry<Integer, Integer> a : dataStore) {
                    MapleQuestStatus q = chr.getQuest(MapleQuest.getInstance(a.getKey()));
                    int state = a.getValue();
                    if (state != 0 && (q == null || q.getStatus() != state)) {
                        return false;
                    }
                }
                return true;
            case item:
                MapleInventoryType iType;
                int itemId;
                short quantity;

                for (SimpleImmutableEntry<Integer, Integer> a : dataStore) {
                    itemId = a.getKey();
                    quantity = 0;
                    iType = GameConstants.getInventoryType(itemId);
                    for (Item item : chr.getInventory(iType).listById(itemId)) {
                        quantity += item.getQuantity();
                    }
                    int count = a.getValue();
                    if (quantity < count || count <= 0 && quantity > 0) {
                        return false;
                    }
                }
                return true;
            case lvmin:
                return chr.getLevel() >= intStore;
            case lvmax:
                return chr.getLevel() <= intStore;
            case end:
                // yyyyMMddHH, Calendar months start at 0
                String timeStr = stringStore;
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(timeStr.substring(0, 4)), Integer.parseInt(timeStr.substring(4, 6)) - 1, Integer.parseInt(timeStr.substring(6, 8)), Integer.parseInt(timeStr.substring(8, 10)), 0);
                return cal.getTimeInMillis() >= System.currentTimeMillis();
            case mob:
                for (SimpleImmutableEntry<Integer, Integer> a : dataStore) {
                    int mobId = a.getKey();
                    int killReq = a.getValue();
                    if (chr.getQuest(quest).getMobKills(mobId) < killReq) {
                        return false;
                    }
                }
                return true;
            case npc:
                return npc_id == null || npc_id == intStore;
            case fieldEnter:
                return intStore != -1 && intStore == chr.getMapId();
            case mbmin:
                return chr.getMonsterBook().getTotal() >= intStore;
            case mbcard:
                for (SimpleImmutableEntry<Integer, Integer> a : dataStore) {
                    int cardId = a.getKey();
                    int killReq = a.getValue();
                    if (chr.getMonsterBook().getCardCount(cardId) < killReq) {
                        return false;
                    }
                }
                return true;
            case pop:
                return chr.getFame() >= intStore; // minimum fame
            case questComplete:
                return chr.getNumQuest() >= intStore;
            case interval:
                return chr.getQuest(quest).getStatus() != 2 || chr.getQuest(quest).getCompletionTime() <= System.currentTimeMillis() - intStore * 60 * 1000L;
            case pet:
                return true;
            case pettamenessmin:
                return false;
            default:
                return true;
        }
    }

    public MapleQuestRequirementType getType() {
        return type;
    }

    public enum MapleQuestRequirementType {
        UNDEFINED,
        job,
        item,
        quest,
        lvmin,
        lvmax,
        end,
        mob,
        npc,
        fieldEnter,
        interval,
        startscript,
        endscript,
        pet,
        pettamenessmin,
        mbmin,
        questComplete,
        pop,
        skill,
        mbcard;

        public static MapleQuestRequirementType getByWZName(String name) {
            try {
                return valueOf(name);
            } catch (IllegalArgumentException ex) {
                return UNDEFINED;
            }
        }
    }
}
