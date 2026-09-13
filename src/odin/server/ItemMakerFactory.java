package odin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tacos.odin.OdinPair;
import odin.provider.IMapleData;
import tacos.wz.WzDataTool;
import tacos.wz.WzXML;

public class ItemMakerFactory {

    private final static ItemMakerFactory instance = new ItemMakerFactory();
    protected Map<Integer, ItemMakerCreateEntry> createCache = new HashMap<>();
    protected Map<Integer, GemCreateEntry> gemCache = new HashMap<>();

    public static ItemMakerFactory getInstance() {
        // DO ItemMakerFactory.getInstance() on ChannelServer startup.
        return instance;
    }

    protected ItemMakerFactory() {
        //System.out.println("Loading ItemMakerFactory :::");
        // 0 = Item upgrade crystals
        // 1 / 2/ 4/ 8 = Item creation

        if (WzXML.ETC.getItemMake() == null) {
            return;
        }

        byte totalupgrades, reqMakerLevel;
        int reqLevel, cost, quantity, stimulator;
        GemCreateEntry ret;
        ItemMakerCreateEntry imt;

        for (IMapleData dataType : WzXML.ETC.getItemMake().getChildren()) {
            int type = Integer.parseInt(dataType.getName());
            switch (type) {
                case 0: { // Caching of gem
                    for (IMapleData itemFolder : dataType.getChildren()) {
                        reqLevel = WzDataTool.getIntPath("reqLevel", itemFolder, 0);
                        reqMakerLevel = (byte) WzDataTool.getIntPath("reqSkillLevel", itemFolder, 0);
                        cost = WzDataTool.getIntPath("meso", itemFolder, 0);
                        quantity = WzDataTool.getIntPath("itemNum", itemFolder, 0);
//			totalupgrades = MapleDataTool.getInt("tuc", itemFolder, 0); // Gem is always 0

                        ret = new GemCreateEntry(cost, reqLevel, reqMakerLevel, quantity);

                        for (IMapleData rewardNRecipe : itemFolder.getChildren()) {
                            for (IMapleData ind : rewardNRecipe.getChildren()) {
                                if (rewardNRecipe.getName().equals("randomReward")) {
                                    ret.addRandomReward(WzDataTool.getIntPath("item", ind, 0), WzDataTool.getIntPath("prob", ind, 0));
// MapleDataTool.getInt("itemNum", ind, 0)
                                } else if (rewardNRecipe.getName().equals("recipe")) {
                                    ret.addReqRecipe(WzDataTool.getIntPath("item", ind, 0), WzDataTool.getIntPath("count", ind, 0));
                                }
                            }
                        }
                        gemCache.put(Integer.parseInt(itemFolder.getName()), ret);
                    }
                    break;
                }
                case 1: // Warrior
                case 2: // Magician
                case 4: // Bowman
                case 8: // Thief
                case 16: { // Pirate
                    for (IMapleData itemFolder : dataType.getChildren()) {
                        reqLevel = WzDataTool.getIntPath("reqLevel", itemFolder, 0);
                        reqMakerLevel = (byte) WzDataTool.getIntPath("reqSkillLevel", itemFolder, 0);
                        cost = WzDataTool.getIntPath("meso", itemFolder, 0);
                        quantity = WzDataTool.getIntPath("itemNum", itemFolder, 0);
                        totalupgrades = (byte) WzDataTool.getIntPath("tuc", itemFolder, 0);
                        stimulator = WzDataTool.getIntPath("catalyst", itemFolder, 0);

                        imt = new ItemMakerCreateEntry(cost, reqLevel, reqMakerLevel, quantity, totalupgrades, stimulator);

                        for (IMapleData Recipe : itemFolder.getChildren()) {
                            for (IMapleData ind : Recipe.getChildren()) {
                                if (Recipe.getName().equals("recipe")) {
                                    imt.addReqItem(WzDataTool.getIntPath("item", ind, 0), WzDataTool.getIntPath("count", ind, 0));
                                }
                            }
                        }
                        createCache.put(Integer.parseInt(itemFolder.getName()), imt);
                    }
                    break;
                }
            }
        }
    }

    public GemCreateEntry getGemInfo(int itemid) {
        return gemCache.get(itemid);
    }

    public ItemMakerCreateEntry getCreateInfo(int itemid) {
        return createCache.get(itemid);
    }

    public static class GemCreateEntry {

        private int reqLevel, reqMakerLevel;
        private int cost, quantity;
        private List<OdinPair<Integer, Integer>> randomReward = new ArrayList<>();
        private List<OdinPair<Integer, Integer>> reqRecipe = new ArrayList<>();

        public GemCreateEntry(int cost, int reqLevel, int reqMakerLevel, int quantity) {
            this.cost = cost;
            this.reqLevel = reqLevel;
            this.reqMakerLevel = reqMakerLevel;
            this.quantity = quantity;
        }

        public List<OdinPair<Integer, Integer>> getRandomReward() {
            return randomReward;
        }

        public List<OdinPair<Integer, Integer>> getReqRecipes() {
            return reqRecipe;
        }

        public int getReqLevel() {
            return reqLevel;
        }

        public int getReqSkillLevel() {
            return reqMakerLevel;
        }

        public int getCost() {
            return cost;
        }

        protected void addRandomReward(int itemId, int prob) {
            randomReward.add(new OdinPair<Integer, Integer>(itemId, prob));
        }

        protected void addReqRecipe(int itemId, int count) {
            reqRecipe.add(new OdinPair<Integer, Integer>(itemId, count));
        }
    }

    public static class ItemMakerCreateEntry {

        private int reqLevel;
        private int cost, quantity, stimulator;
        private byte tuc, reqMakerLevel;
        private List<OdinPair<Integer, Integer>> reqItems = new ArrayList<>(); // itemId / amount
        public ItemMakerCreateEntry(int cost, int reqLevel, byte reqMakerLevel, int quantity, byte tuc, int stimulator) {
            this.cost = cost;
            this.tuc = tuc;
            this.reqLevel = reqLevel;
            this.reqMakerLevel = reqMakerLevel;
            this.quantity = quantity;
            this.stimulator = stimulator;
        }

        public byte getTUC() {
            return tuc;
        }

        public List<OdinPair<Integer, Integer>> getReqItems() {
            return reqItems;
        }

        public int getReqLevel() {
            return reqLevel;
        }

        public byte getReqSkillLevel() {
            return reqMakerLevel;
        }

        public int getCost() {
            return cost;
        }

        public int getStimulator() {
            return stimulator;
        }

        protected void addReqItem(int itemId, int amount) {
            reqItems.add(new OdinPair<Integer, Integer>(itemId, amount));
        }
    }
}
