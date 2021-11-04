// 闘技場
// セザール

var npc_talk_status = -1;
function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var mapid = cm.getMapId();
				var text = "何のようだ？\r\n";
				text += "#L" + 0 + "##b一番目闘技場(空室)#k#l\r\n";
				text += "#L" + 1 + "##b二番目闘技場(空室)#k#l\r\n";
				text += "#L" + 2 + "##b三番目闘技場(空室)#k#l\r\n";
				text += "#L" + 3 + "##b闘技大会について知りたいです。#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}