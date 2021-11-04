// 二番目のエオス石

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// デバッグモード
				if (!cm.haveItem(4001020)) {
					cm.gainItem(4001020, 1);
				}
				var text = "#bエオス石の書#kを使って#b二番目のエオス石#kを活性化できます。どの石へ移動しますか？\r\n";
				text += "#L" + 221024400 + "##b一番目のエオス石(100階)#k#l\r\n";
				text += "#L" + 221021700 + "##b三番目のエオス石(41階)#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				var mapid = selection;
				if (cm.haveItem(4001020)) {
					cm.gainItem(4001020, -1);
					cm.warp(mapid, 3);
				}
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}
