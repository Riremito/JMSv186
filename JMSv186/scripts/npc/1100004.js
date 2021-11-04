// キル
// エレヴからオルビス

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// 原文ママ
				var text = "オシリア大陸の#bオルビス#kまでかかる時間は約 #b8分#kだ。料金は#b1000#kメル。1000メル出して船に乗るか？";
				return cm.sendYesNo(text);
			}
		case 1:
			{
				cm.warp(200000161);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}
