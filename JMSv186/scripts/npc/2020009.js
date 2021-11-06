// ロベイラ
// 魔法使い3次

var status = -1;
var job;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0 && status == 1) {
		cm.sendOk("Make up your mind and visit me again.");
		cm.dispose();
		return;
	}
	if (mode == 1)
		status++;
	else
		status--;

	if (status == 0) {
		if (!(cm.getJob() == 210 || cm.getJob() == 220 || cm.getJob() == 230)) { // CLERIC
			cm.sendOk("ジャクムダンジョンを探索しようとしてるのか？魔法使いじゃない者の能力を魔法使いが判断するわけにはいかない。君の職業の長老を訪ねてくれ。");
			cm.dispose();
			return;
		}
		if ((cm.getJob() == 210 || cm.getJob() == 220 || cm.getJob() == 230) && cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
			cm.sendNext("You are indeed a strong one.");
		} else {
			cm.sendOk("Please use all your SP from level 70 and under, and be level 70+...");
			cm.safeDispose();
		}
	} else if (status == 1) {
		if (cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
			if (cm.getJob() == 210) { // FP
				cm.changeJob(211); // FP MAGE
				cm.gainAp(5);
				cm.sendOk("You are now a #bFire/Poison Mage#k. May #rOdin#k be with you!");
				cm.dispose();
			} else if (cm.getJob() == 220) { // IL
				cm.changeJob(221); // IL MAGE
				cm.gainAp(5);
				cm.sendOk("You are now an #bIce/Lightning Mage#k. May #rOdin#k be with you!");
				cm.dispose();
			} else if (cm.getJob() == 230) { // CLERIC
				cm.changeJob(231); // PRIEST
				cm.gainAp(5);
				cm.sendOk("You are now a #bPriest#k. May #rOdin#k be with you!");
				cm.dispose();
			}
		} else {
			cm.sendOk("Come back when you are level 70 and used SP.");
			cm.dispose();
		}
	}
}