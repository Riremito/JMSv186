// パイソン
// フロリナビーチ

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var mapid = cm.getSavedLocation("FLORINA");
				// テキスト適当
				var text = "#b#m" + mapid + "##kに戻りますか。#k\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				var mapid = cm.getSavedLocation("FLORINA");
				cm.warp(mapid);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}