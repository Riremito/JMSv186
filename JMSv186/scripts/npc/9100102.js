// ガシャポン
// ペリオン

var rewards = new Array(
	1102040,
	1102041,
	1102042,
	1102084,
	1102085,
	1102086
);

function RandomRewards() {
	var target = Math.floor(Math.random() * rewards.length);
	return rewards[target];
}

var ticket_itemid = 5220000;

var npc_talk_status = 0;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;

	switch (npc_talk_status) {
		case 1:
			{
				// デバッグモード
				if (!cm.haveItem(ticket_itemid)) {
					cm.gainItem(ticket_itemid, 1);
				}
				if (!cm.haveItem(ticket_itemid)) {
					npc_talk_status = -1;
					// 原文ママ
					var text = "ガシャポンが置いてある…";
					return cm.sendSimple(text);
				}
				var text = "ガシャポンが置いてある。#b#z" + ticket_itemid + "##kを使いますか？";
				return cm.sendYesNo(text);
			}
		case 2:
			{
				if (cm.haveItem(ticket_itemid)) {
					var reward = RandomRewards();
					cm.gainItem(ticket_itemid, -1);
					cm.Gashapon(reward, 1);
					cm.sendOk("#b#t" + reward + "##k１個を獲得しました！");
				}
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}