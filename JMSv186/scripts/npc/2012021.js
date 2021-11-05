// ラミニ
// オルビスからリプレ

var npc_talk_status = -1;
var ticket_itemid = 4031331;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// デバッグモード
				if (!cm.haveItem(ticket_itemid)) {
					cm.gainItem(ticket_itemid, 1);
				}
				// 適当
				var text = "いったん船に乗ると長旅になりますので急な用があれば先に解決してください。いかがですか？船に乗りますか？";
				return cm.sendYesNo(text);
			}
		case 1:
			{
				if (cm.haveItem(ticket_itemid)) {
					cm.gainItem(ticket_itemid, -1);
				}

				// リプレ チケット売場
				cm.warp(240000100);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}