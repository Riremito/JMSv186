// 造形物@ ビシャス

function action(mode, type, selection) {
	cm.gainItem(4031179, 1);
	var papuMap = cm.getMap(220080001);
	if (cm.getPlayerCount(220080001) <= 0) { // Papu Map
		papuMap.resetFully();
	}
	cm.warp(220080001, "st00");
	cm.dispose();
}