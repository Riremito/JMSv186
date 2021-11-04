// エレベーター
// ヘリオス塔

// エレベータースキップ
function enter(pi) {
	var mapid = pi.getMapId();

	switch (mapid) {
		case 222020100:
			{
				pi.playPortalSE();
				//pi.warp(222020110, "sp");
				pi.warp(222020200, "in00");
				return;
			}
		case 222020200:
			{
				pi.playPortalSE();
				//pi.warp(222020210, "sp");
				pi.warp(222020100, "in00");
				return;
			}
		default:
			break;
	}
}
