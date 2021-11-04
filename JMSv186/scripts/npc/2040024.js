// 一番目のエオス石

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
				var text = "#bエオス石の書#kを使って#b一番目のエオス石#kを活性化できます。#b二番目のエオス石#kがあるところへ移動しますか？";
				return cm.sendYesNo(text);
			}
		case 1:
			{
				if (cm.haveItem(4001020)) {
					cm.gainItem(4001020, -1);
					cm.warp(221022900, 3);
				}
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}
