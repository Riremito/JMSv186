// チェリ
// エリニアからオルビス

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
				if (!cm.haveItem(4031045)) {
					cm.gainItem(4031045, 1);
				}
				// 原文ママ
				var text = "いったん船に乗ると長旅になりますので急な用があれば先に解決してください。いかがですか？船に乗りますか？";
				return cm.sendYesNo(text);
			}
		case 1:
			{
				if (cm.haveItem(4031045)) {
					cm.gainItem(4031045, -1);
				}

				//cm.warp(101000301, 0);
				// オルビスチケット売場
				cm.warp(200000100);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}