// ホーンテイル
// !npc 2083004
function EnterBossMap(mapid) {
	var reset = cm.getPlayerCount(mapid) ? false : true;
	if (reset) {
		var map = cm.getMap(mapid);
		cm.resetMap(mapid);
		map.resetFully();
	}
	cm.warp(mapid);
	if (reset) {
		cm.spawnMonster(8810024, 890, 230);
	}
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
				cm.sendNext("ホーンテイルテスト");
				return;
			}
		case 1:
			{
				EnterBossMap(240060000);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}