// 警察官ミカーファイ
// 抜け道 から 大王ムカデへ
// !npc 9310006

function EnterAreaBossMap(mapid, mobid, x, y) {
	var first_user = cm.getPlayerCount(mapid) ? false : true;
	cm.warp(mapid);
	if (first_user) {
		cm.resetMap(mapid);
		//cm.spawnMonster(mobid, x, y);
	}
}

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
				// もともと誤字？
				cm.sendNext("大王ムカデを倒しに来た方ですね。では、任務遂行にため、大王ムカデがいるところに行ってみましょう。");
				return;
			}
		case 1:
			{
				// 大王ムカデが召喚されない...?
				// 制限時間10分
				//EnterAreaBossMap(701010323, 5220004, 2160, 823);
				EnterBossMap(701010323);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}