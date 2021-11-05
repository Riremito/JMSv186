// ジョン・バリケード
// iTCG

// 交換処理未実装

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
				var text = "おや、何を持っているんだ？おお！それはとてもレアな物だよ。どこで手に入れたのかな？\r\n";
				text += "#L" + 0 + "##bこれが何なのか教えて下さい。#k#l\r\n";
				text += "#L" + 1 + "##bこの古い物が？私が履きこんだ靴下よりは価値あるだろうけどね！#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				// iTCG系の処理
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}