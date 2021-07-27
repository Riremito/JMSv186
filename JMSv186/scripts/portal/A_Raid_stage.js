// 海外ボスモンスターレイド v180
// 889200000, 889210100
// 889212400
// NG 4,11,18,21,22,23 fix
function GetStage(mapid) {

	if (mapid < 889210100 || 889222517 < mapid) {
		return 0;
	}

	var stage = Math.floor((mapid % 10000) / 100);

	return stage;
}


function enter(pi) {
	var mapid = pi.getMapId() + 100;
	var stage = GetStage(mapid);

	// ステージスキップ処理
	switch (stage) {
		/*
		case 4:
			mapid += 100;
			break;
		case 11:
			mapid += 100;
			break;
		case 18:
			mapid += 100;
			break;
		case 21:
			mapid += 300;
			break;
		case 22:
			mapid += 200;
			break;
		case 23:
			mapid += 100;
			break;
		*/
		// 最深部へ到達
		case 26:
			mapid = 889200020;
			break;
		default:
			break;
	}

	if (pi.getPlayerCount(mapid) == 0) {
		pi.resetMap(mapid);
	}

	pi.warp(mapid, "sp");
}