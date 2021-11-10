// 戦士転職教官
// 試験中

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				if (cm.haveItem(4031013, 30)) {
					// 魔法使い
					// var text = "なんと…#b#z4031013##kを30個集めたか！並の試験ではなかったのに、よくぞやりとげた。うむ。君は合格だ。その証拠に#b#z4031012##kを渡そう。このアイテムを持ってエリニアに帰ってみなさい。";
					// 原文ママ
					var text = "お！#b#z4031013##k30個を全部集めたのか！よくやった！よし、君は試験に合格だ。その証拠に#b#z4031012##kをあげるよ。このアイテムを持ってペリオンに帰ってみなさい。";
					return cm.sendSimple(text);
				}
			}
		case 1:
			{
				cm.removeAll(4031013);
				cm.gainItem(4031012, 1);
				cm.warp(102020300, 0);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}

/*
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
		if (cm.haveItem(4031013, 30)) {
			cm.removeAll(4031013);
			cm.completeQuest(100004);
			cm.startQuest(100005);
			cm.sendOk("You're a true hero! Take this and Dances with Balrog will acknowledge you.");
		} else {
			cm.sendOk("You will have to collect me #b30 #t4031013##k. Good luck.")
			cm.dispose();
		}
	} else if (status == 1) {
		cm.gainItem(4031012, 1);
		cm.warp(102020300, 0);
		cm.dispose();
	}
}
*/