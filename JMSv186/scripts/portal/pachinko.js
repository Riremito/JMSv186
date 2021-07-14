function enter(pi) {
	pi.playPortalSE();
	var return_mapid = pi.getSavedLocation("MULUNG_TC");
	pi.clearSavedLocation("MULUNG_TC");

	switch (return_mapid) {
		// ルディブリアム パチンコ屋入口
		case 810000000:
			pi.warp(return_mapid, "in00");
			break;
		// ショーワ町通り
		case 801000300:
			pi.warp(return_mapid, "in00");
			break;
		default:
			pi.warp(return_mapid);
			break;
	}

}