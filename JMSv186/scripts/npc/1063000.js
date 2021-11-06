// スリーピーウッド忍耐 1回目

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// cm.getQuestStatus(2052) == 1 && !cm.haveItem(4031025)
				cm.gainItem(4031025, 10);
				cm.warp(105040300, 0);
				// クエスト時以外は報酬適当に設定すると良さそう
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}