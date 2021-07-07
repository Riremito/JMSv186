// 103050101
function enter(pi) {
	var mapid = pi.getMapId();

	pi.playPortalSE();

	switch (mapid) {
		case 103050101:
			{
				pi.warp(103050100, "in00");
				break;
			}
		default:
			break;
	}
}