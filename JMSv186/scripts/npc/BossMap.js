export function GetSpawnItem(itemid) {
	if (cm.haveItem(itemid)) {
		return false;
	}

	cm.gainItem(itemid, 1);
	return true;
}

export function EnterBossMap(mapid) {
	if (cm.getPlayerCount(mapid) == 0) {
		cm.resetMap(mapid);
	}
	cm.warp(mapid);
}