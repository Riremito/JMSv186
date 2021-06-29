// アドビス@ジャクム

function action(mode, type, selection) {
	cm.gainItem(4001017, 1);

	var papuMap = cm.getMap(280030000);

	if (cm.getPlayerCount(280030000) <= 0) { // Papu Map
		papuMap.resetFully();
	}

	cm.warp(280030000, 0);
	cm.dispose();
}