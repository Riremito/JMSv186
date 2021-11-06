// 風来坊錬金術師

var npc_talk_status = 0;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 1:
			{
				// BB後
				var text = "モグラの舌とフクロウの嘴を1:1の比率で混ぜ合わせて…あれ！ほのかな光の粉を入れることを忘れてた。大変～！あれ？いつからそこにいました？あ…僕があまりにも仕事に夢中になっていて気づいていなかったのか。これは失敬。\r\n";
				return cm.sendSimple(text);
			}
		case 2:
			{
				// BB後
				var text = "ご覧の通り、僕は風来坊の錬金術師です。まだ未熟者ですが、あなたが必要なものを作れるかもしれません。一度見てみますか？\r\n";
				text += "#L" + 4006000 + "##b魔法の石作り#k#l\r\n";
				text += "#L" + 4006001 + "##b召喚の石作り#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 3:
			{
				// デバッグモード
				var itemid = selection;
				cm.gainItem(itemid, 100);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}