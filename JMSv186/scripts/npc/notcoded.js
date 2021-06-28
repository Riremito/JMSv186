function action(mode, type, selection) {
	var id = cm.getNpc();
	var mapid = cm.getMapId();
	var text = "デバッグ情報\r\n";
	text += "#p" + id + "#\r\n";
	text += "NPC ID = #b" + id + "#k\r\n";
	text += "#m" + mapid + "#\r\n";
	text += "Map ID = #b" + mapid + "#k\r\n";

	cm.sendOk(text);
	cm.safeDispose();
}