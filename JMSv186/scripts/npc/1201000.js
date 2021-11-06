var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// BB後
				var text = "どうしましたか？\r\n";
				text += "#L" + 0 + "##bあなたと話したいです。#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				var text = "私があなたと話す理由がありますか？そんなにやすやす話はしません。\r\n";
				return cm.sendSimple(text);
			}
		default:
			break;
	}

	return cm.dispose();
}