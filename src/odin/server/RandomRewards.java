package odin.server;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import odin.constants.GameConstants;

public class RandomRewards {

    private final static RandomRewards instance = new RandomRewards();
    private List<Integer> compiledGold = null;
    private List<Integer> compiledSilver = null;
    private List<Integer> compiledFishing = null;

    public static RandomRewards getInstance() {
        return instance;
    }

    private RandomRewards() {
        // Gold Box
        List<Integer> returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.goldrewards);

        compiledGold = returnArray;

        // Silver Box
        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.silverrewards);

        compiledSilver = returnArray;

        // Fishing Rewards
        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.fishingReward);

        compiledFishing = returnArray;

        // Event Rewards
        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.eventCommonReward);

        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.eventUncommonReward);

        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.eventRareReward);

        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.eventSuperReward);
    }

    private void processRewards(List<Integer> returnArray, int[] list) {
        int lastitem = 0;
        for (int i = 0; i < list.length; i++) {
            if (i % 2 == 0) { // Even
                lastitem = list[i];
            } else { // Odd
                for (int j = 0; j < list[i]; j++) {
                    returnArray.add(lastitem);
                }
            }
        }
        Collections.shuffle(returnArray);
    }

    public int getGoldBoxReward() {
        return compiledGold.get(Randomizer.nextInt(compiledGold.size()));
    }

    public int getSilverBoxReward() {
        return compiledSilver.get(Randomizer.nextInt(compiledSilver.size()));
    }

    public int getFishingReward() {
        return compiledFishing.get(Randomizer.nextInt(compiledFishing.size()));
    }
}
