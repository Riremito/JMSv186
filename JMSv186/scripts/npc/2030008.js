// ジャクム前提クエスト
// アドビス

var npc_talk_status = -1;
function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var mapid = cm.getMapId();
				var text = "よし、君たちは十分にできそうだな。どの段階に挑戦してみるんだ？\r\n";
				text += "#L" + 0 + "##b閉鉱洞窟の調査に行く。(1段階)#k#l\r\n";
				text += "#L" + 1 + "##bジャクムダンジョンを探査する。(2段階)#k#l\r\n";
				text += "#L" + 2 + "##b製錬を要請する。(3段階)#k#l\r\n";
				text += "#L" + 3 + "##bクエストに対する説明を聞く。#k#l\r\n";
				text += "#L" + 4 + "##bデバッグモード。#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}

/*
function action(mode, type, selection) {
	cm.gainItem(4001017, 1);

	var papuMap = cm.getMap(280030000);

	if (cm.getPlayerCount(280030000) <= 0) { // Papu Map
		papuMap.resetFully();
	}

	cm.warp(280030000, 0);
	cm.dispose();
}
*/