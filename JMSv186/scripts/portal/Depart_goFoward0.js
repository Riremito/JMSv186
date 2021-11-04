// カニングスクエア

function enter(pi) {
	pi.playPortalSE();
	var mapid = pi.getPlayer().getMapId() + 10;
	if (pi.getPlayerCount(mapid) == 0) {
		pi.resetMap(mapid);
	}
	pi.warp(mapid, "right00");
}