// 警察官ハーク
// 抜け道 途中退出

var status = -1;
function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	var mapid = cm.getMapId();

	switch (status) {
		case 0:
			{
				var text = "こんにちは。僕はハークと申します。何の用でしょうか。\r\n";
				text += "#L" + 0 + "##bここから出たいです。#k#l";
				cm.sendSimple(text);
				return;
			}
		case 1:
			{
				cm.warp(701010320, 0);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}