// ガードマン
// ジェラシーロッカー

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
				var text = "大変申し訳ございません。ここから先、関係者以外立ち入り禁止です。入場は制限させていただいております。\r\n";
				text += "#L" + 0 + "##b私は#e#p" + 9120056 + "##nさんを手伝っているところなんだ。#k#l\r\n"
				// 洞窟の入口
				return cm.sendSimple(text);
			}
		case 1:
			{
				// 原文ママ
				var text = "#b#p" + 9120056 + "#さん#kを手伝っている#b#h #さん#kですね。お待たせいたしました。7、8階の#b一般ゾーン#kへご入場ください。";
				return cm.sendSimple(text);
			}
		case 2:
			{
				// ジェラシーロッカー
				cm.playPortalSE();
				var mapid = 103040410;
				if (cm.getPlayerCount(mapid) == 0) {
					cm.resetMap(mapid);
				}
				cm.warp(mapid, "right01");
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}