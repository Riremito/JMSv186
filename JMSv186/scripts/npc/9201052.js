// フォックスウィット
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
				var text = "どうしたんじゃ？\r\n";
				text += "#L" + 0 + "##bこんなものを見つけたからちょっと見てほしくて、あなたが適任者だと思ったの。#k#l\r\n";
				text += "#L" + 1 + "##bいいえ、なんでもないです。#k#l\r\n";
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