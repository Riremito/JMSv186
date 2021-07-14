// ヒナの巣の招待状
// 入場マップに戻す必要あり
function enter(pi) {
	pi.playPortalSE();
	var return_mapid = pi.getSavedLocation("MULUNG_TC");
	pi.clearSavedLocation("MULUNG_TC");
	pi.warp(return_mapid);
}