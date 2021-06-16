var Message = new Array(
    "You can earn A Cash by killing monsters, achievements and Mu Lung Dojo",
    "Use @help command for the list of player command you can use.",
    "MapleLeafs can be used to make Maple Items (lv35,43,64,77) or traded for Experience at Vavaan NPC in FreeMarket.",
    "Please do not use foul language, harass or scam other players. We would like to keep this community clean & friendly.",
    "Exclusive Warpback system for all bosses including BossPartyQuest!",
    "1 billion mesos can be traded for Golden Leaf from @joyce.",
    "Vote at our website and accumulate points to get special items!",
    "Gather your friends and enjoy the fun of our Party Quests !",
    "Look for RockPaperScissors Admin to play RockPaperScissors!",
    "Please report any bugs/glitches at our forum.",
    "Use @ea if you cant speak to a NPC.",
    "Follow system added! You can follow other players around in the map.",
    "Purchase a VIP Fishing Rod from the Cash Shop and collect 500 Golden Fish Eggs to trade for a Fishing King Medal.",
    "Type @mumu to purchase summoning rocks/magic rocks/all cure potions and elixirs, magnifying glasses.",
    "Make a party with your friends and conquer Mulung Dojo! Take down the bosses and receive points to exchange for belts",
    "Use @check to check for points, cash, and voting points!",
    "Check out @joyce to get to meso-map/cash-map and many more!",
    "We have full cash shop working! Purchase cash items to create your unique character look!",
    "Click on our PlayerNPCs to view your speedrun at bosses!",
    "Peanut Machines available when you join in our events!",
    "Events like Olaola/Maple Fitness/Snowball/Coconut Harvest/OX Quiz will be hosted by GMs.",
    "Fairy pendant lasts 24hrs, when equipped = 10%, 1hr = 20%, 2hr = 30% bonus exp available through BossPartyQuest.",
    "Trade rare item for cash at the rare item seller in Free Market .",
    "Try out Richie Gold Compass and recieve random rewards!",
    "Cassandra rewards 1-5 available through BossPartyQuest.",
    "There will be Channel limit for certain bosses. You can only fight the bosses in the stated channel.",
    "Now, there will be a random gain of a-cash when you kill a monster!",
    "Friendship rings/friendship shirt are working! ",
    "GuildPoint trader gives buff 2x meso/exp/drop/acash for 2hours.",
    "Gather your guildmates and try out the GuildPartyQuest!",
    "Look for Mar the Fairy at Ellinia with the rock of evolution to evolve your pet dragon or pet robo.",
    "You can purchase your respective mounts at Aqua Road Weapon Shop!",
    //"Look for Mar the Fairy to get a pet Snail Roon that auto loots meso/drops for you.",
    "Please report any bugs you are facing immediately in the forums!",
    "15% scrolls can be created by Tia NPC.");

var setupTask;

function init() {
    scheduleNew();
}

function scheduleNew() {
    setupTask = em.schedule("start", 900000);
}

function cancelSchedule() {
	setupTask.cancel(false);
}

function start() {
    scheduleNew();
    em.broadcastYellowMsg("[AuraTip] " + Message[Math.floor(Math.random() * Message.length)]);
}