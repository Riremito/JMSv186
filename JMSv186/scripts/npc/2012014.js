// オルビス魔法石
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
				if (!cm.haveItem(4001019)) {
					cm.gainItem(4001019, 1);
				}
				var text = "#b魔法石の書#kを使って#bオルビス魔法石#kを活性化できます。#bエルナス魔法石#kがあるところへ移動しますか？";
				return cm.sendYesNo(text);
			}
		case 1:
			{
				if (cm.haveItem(4001019)) {
					cm.gainItem(4001019, -1);
					cm.warp(200082100, 0);
				}
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}