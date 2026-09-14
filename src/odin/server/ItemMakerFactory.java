package odin.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleImmutableEntry;
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
        WzXML.ETC.loadItemMake(gemCache, createCache);
    }

    public GemCreateEntry getGemInfo(int itemid) {
        return gemCache.get(itemid);
    }

    public ItemMakerCreateEntry getCreateInfo(int itemid) {
        return createCache.get(itemid);
    }

    public static class GemCreateEntry {

        private int reqLevel;
        private int reqMakerLevel;
        private int cost;
        private int quantity;
        private List<SimpleImmutableEntry<Integer, Integer>> randomReward = new ArrayList<>();
        private List<SimpleImmutableEntry<Integer, Integer>> reqRecipe = new ArrayList<>();

        public GemCreateEntry(int cost, int reqLevel, int reqMakerLevel, int quantity) {
            this.cost = cost;
            this.reqLevel = reqLevel;
            this.reqMakerLevel = reqMakerLevel;
            this.quantity = quantity;
        }

        public List<SimpleImmutableEntry<Integer, Integer>> getRandomReward() {
            return randomReward;
        }

        public List<SimpleImmutableEntry<Integer, Integer>> getReqRecipes() {
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

        public void addRandomReward(int itemId, int prob) {
            randomReward.add(new SimpleImmutableEntry<>(itemId, prob));
        }

        public void addReqRecipe(int itemId, int count) {
            reqRecipe.add(new SimpleImmutableEntry<>(itemId, count));
        }
    }

    public static class ItemMakerCreateEntry {

        private int reqLevel;
        private int cost;
        private int quantity;
        private int stimulator;
        private byte tuc;
        private byte reqMakerLevel;
        private List<SimpleImmutableEntry<Integer, Integer>> reqItems = new ArrayList<>(); // itemId / amount

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

        public List<SimpleImmutableEntry<Integer, Integer>> getReqItems() {
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

        public void addReqItem(int itemId, int amount) {
            reqItems.add(new SimpleImmutableEntry<>(itemId, amount));
        }
    }
}
