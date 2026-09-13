package odin.server.quest;

import odin.client.Skill;
import java.util.Calendar;
import java.util.List;
import java.util.LinkedList;
import odin.client.inventory.Item;
import odin.client.SkillFactory;
import odin.constants.GameConstants;
import odin.client.MapleCharacter;
import odin.client.inventory.MaplePet;
import odin.client.inventory.MapleInventoryType;
import odin.client.MapleQuestStatus;
import java.util.AbstractMap.SimpleImmutableEntry;
import odin.provider.IMapleData;
import tacos.wz.WzDataTool;

public class MapleQuestRequirement {

    private MapleQuest quest;
    private MapleQuestRequirementType type;
    private int intStore;
    private String stringStore;
    private List<SimpleImmutableEntry<Integer, Integer>> dataStore;

    public MapleQuestRequirement(MapleQuest quest, MapleQuestRequirementType type, IMapleData data) {
        this.type = type;
        this.quest = quest;

        switch (type) {
            case job: {
                final List<IMapleData> child = data.getChildren();
                dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    dataStore.add(new SimpleImmutableEntry<>(i, WzDataTool.getInt(child.get(i), -1)));
                }
                break;
            }
            case skill: {
                final List<IMapleData> child = data.getChildren();
                dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    final IMapleData childdata = child.get(i);
                    dataStore.add(new SimpleImmutableEntry<>(WzDataTool.getInt(childdata.getChildByPath("id"), 0),
                            WzDataTool.getInt(childdata.getChildByPath("acquire"), 0)));
                }
                break;
            }
            case quest: {
                final List<IMapleData> child = data.getChildren();
                dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    final IMapleData childdata = child.get(i);
                    dataStore.add(new SimpleImmutableEntry<>(WzDataTool.getInt(childdata.getChildByPath("id")),
                            WzDataTool.getInt(childdata.getChildByPath("state"), 0)));
                }
                break;
            }
            case item: {
                final List<IMapleData> child = data.getChildren();
                dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    final IMapleData childdata = child.get(i);
                    dataStore.add(new SimpleImmutableEntry<>(WzDataTool.getInt(childdata.getChildByPath("id")),
                            WzDataTool.getInt(childdata.getChildByPath("count"), 0)));
                }
                break;
            }
            case pettamenessmin:
            case npc:
            case questComplete:
            case pop:
            case interval:
            case mbmin:
            case lvmax:
            case lvmin: {
                intStore = WzDataTool.getInt(data, -1);
                break;
            }
            case end: {
                stringStore = WzDataTool.getString(data, null);
                break;
            }
            case mob: {
                final List<IMapleData> child = data.getChildren();
                dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    final IMapleData childdata = child.get(i);
                    dataStore.add(new SimpleImmutableEntry<>(WzDataTool.getInt(childdata.getChildByPath("id"), 0),
                            WzDataTool.getInt(childdata.getChildByPath("count"), 0)));
                }
                break;
            }
            case fieldEnter: {
                final IMapleData zeroField = data.getChildByPath("0");
                if (zeroField != null) {
                    intStore = WzDataTool.getInt(zeroField);
                } else {
                    intStore = -1;
                }
                break;
            }
            case mbcard: {
                final List<IMapleData> child = data.getChildren();
                dataStore = new LinkedList<>();

                for (int i = 0; i < child.size(); i++) {
                    final IMapleData childdata = child.get(i);
                    dataStore.add(new SimpleImmutableEntry<>(WzDataTool.getInt(childdata.getChildByPath("id"), 0),
                            WzDataTool.getInt(childdata.getChildByPath("min"), 0)));
                }
                break;
            }
            case pet: {
                dataStore = new LinkedList<>();

                for (IMapleData child : data) {
                    dataStore.add(new SimpleImmutableEntry<>(-1, WzDataTool.getIntPath("id", child, 0)));
                }
                break;
            }
        }
    }

    public boolean check(MapleCharacter chr, Integer npc_id) {
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
                    final boolean acquire = a.getValue() > 0;
                    final int skill = a.getKey();
                    final Skill skil = SkillFactory.getSkill(skill);
                    if (acquire) {
                        if (skil.isFourthJob()) {
                            if (chr.getMasterLevel(skil) == 0) {
                                return false;
                            }
                        } else {
                            if (chr.getSkillLevel(skil) == 0) {
                                return false;
                            }
                        }
                    } else {
                        if (chr.getSkillLevel(skil) > 0 || chr.getMasterLevel(skil) > 0) {
                            return false;
                        }
                    }
                }
                return true;
            }
            case quest:
                for (SimpleImmutableEntry<Integer, Integer> a : dataStore) {
                    final MapleQuestStatus q = chr.getQuest(MapleQuest.getInstance(a.getKey()));
                    final int state = a.getValue();
                    if (state != 0) {
                        if (q == null && state == 0) {
                            continue;
                        }
                        if (q == null || q.getStatus() != state) {
                            return false;
                        }
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
                    final int count = a.getValue();
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
                final String timeStr = stringStore;
                final Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(timeStr.substring(0, 4)), Integer.parseInt(timeStr.substring(4, 6)), Integer.parseInt(timeStr.substring(6, 8)), Integer.parseInt(timeStr.substring(8, 10)), 0);
                return cal.getTimeInMillis() >= System.currentTimeMillis();
            case mob:
                for (SimpleImmutableEntry<Integer, Integer> a : dataStore) {
                    final int mobId = a.getKey();
                    final int killReq = a.getValue();
                    if (chr.getQuest(quest).getMobKills(mobId) < killReq) {
                        return false;
                    }
                }
                return true;
            case npc:
                return npc_id == null || npc_id == intStore;
            case fieldEnter:
                if (intStore != -1) {
                    return intStore == chr.getMapId();
                }
                return false;
            case mbmin:
                if (chr.getMonsterBook().getTotal() >= intStore) {
                    return true;
                }
                return false;
            case mbcard:
                for (SimpleImmutableEntry<Integer, Integer> a : dataStore) {
                    final int cardId = a.getKey();
                    final int killReq = a.getValue();
                    if (chr.getMonsterBook().getCardCount(cardId) < killReq) {
                        return false;
                    }
                }
                return true;
            case pop:
                return chr.getFame() <= intStore;
            case questComplete:
                return chr.getNumQuest() >= intStore;
            case interval:
                return chr.getQuest(quest).getStatus() != 2 || chr.getQuest(quest).getCompletionTime() <= System.currentTimeMillis() - intStore * 60 * 1000L;
            case pet:
                for (SimpleImmutableEntry<Integer, Integer> a : dataStore) {
                    if (chr.getPetById(a.getValue()) == -1) {
                        return false;
                    }
                }
                return true;
            case pettamenessmin:
                for (MaplePet pet : chr.getPets()) {
                    if (pet.getSummoned() && pet.getCloseness() >= intStore) {
                        return true;
                    }
                }
                return false;
            default:
                return true;
        }
    }

    public MapleQuestRequirementType getType() {
        return type;
    }
}
