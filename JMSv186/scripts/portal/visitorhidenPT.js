function enter(pi) {
	pi.playPortalSE();
	pi.warp(pi.getSavedLocation("MULUNG_TC"), "visitor00");
	pi.clearSavedLocation("MULUNG_TC");
	return true;
}