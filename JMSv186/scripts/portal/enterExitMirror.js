// ‹¾‚Ì’†‚Ì‹à”“Ž›
function enter(pi) {
	var mapid = pi.getMapId();

	pi.playPortalSE();

	switch (mapid) {
		case 950100000:
			{
				pi.warp(809060000, "mirrorExit");
				break;
			}
		case 809060000:
			{
				pi.warp(950100000, "mirrorEnter");
				break;
			}
		default:
			break;
	}
}