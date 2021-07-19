function enter(pi) {
	var mapid = pi.getMapId();

	switch (mapid) {
		case 240060000:
			{
				mapid += 100;
				var reset = pi.getPlayerCount(mapid) ? false : true;
				if (reset) {
					var map = pi.getMap(mapid);
					pi.resetMap(mapid);
					map.resetFully();
				}
				pi.warp(mapid);
				if (reset) {
					pi.spawnMonster(8810025, -360, 230);
				}
				break;
			}
		case 240060100:
			{
				mapid += 100;
				var reset = pi.getPlayerCount(mapid) ? false : true;
				if (reset) {
					var map = pi.getMap(mapid);
					//pi.resetMap(mapid);
					map.resetFully();
				}
				pi.warp(mapid);
				break;
			}
		default:
			break;
	}
}