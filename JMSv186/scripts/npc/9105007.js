function EnterBossMap(mapid) {
	if (cm.getPlayerCount(mapid) == 0) {
		cm.resetMap(mapid);
	}
	cm.warp(mapid);
}

var status = -1;
function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	var mapid = cm.getMapId();

	switch (status) {
		case 0:
			{
				cm.sendYesNo("ボスモンスターレイド開始");
				return;
			}
		case 1:
			{
				EnterBossMap(889210100);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}