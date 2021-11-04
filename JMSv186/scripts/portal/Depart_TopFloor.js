// ジェラシーロッカー
// カニングスクエア

function enter(pi) {
	pi.playPortalSE();
	var mapid = 103040410;
	if (pi.getPlayerCount(mapid) == 0) {
		pi.resetMap(mapid);
	}
	pi.warp(mapid, "right01");
}