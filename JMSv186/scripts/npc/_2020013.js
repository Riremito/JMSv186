// ペドロ
// 海賊3次

var status = 0;
var job;

function start() {
	status = -1;
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
		if (!(cm.getJob() == 510 || cm.getJob() == 520)) {
			cm.sendOk("君ならジャクムに挑戦するにもう十分だ。あの閉鉱の奥の#b#p2030008##kに行ってみてくれ。私が承諾する。そのダンジョンを探索してくれ。");
			// よし、私が#b#p2030008##kのいる#bジャクムへの門#kへ送ってやろう。
			cm.dispose();
			return;
		}
		if ((cm.getJob() == 510 || cm.getJob() == 520) && cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
			cm.sendNext("You are indeed a strong one.");
		} else {
			cm.sendOk("Please make sure that you have used all your 2nd job skill point before proceeding.");
			cm.safeDispose();
		}
	} else if (status == 1) {
		if (cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
			if (cm.getJob() == 510) {
				cm.changeJob(511);
				cm.gainAp(5);
				cm.sendOk("You are now a #bBuccaneer#k. May #rOdin#k be with you!");
				cm.dispose();
			} else if (cm.getJob() == 520) {
				cm.changeJob(521);
				cm.gainAp(5);
				cm.sendOk("You are now a #bValkyrie#k. May #rOdin#k be with you!");
				cm.dispose();
			}
		} else {
			cm.sendOk("Come back when you are level 70 and used SP.");
			cm.dispose();
		}
	}
}