// レイン

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// BB後
				var text = "ここはメイプルアイランドの玄関口#bアムホスト#kっていう小さな町よ。";
				return cm.sendSimple(text);
			}
		case 1:
			{
				// BB後
				var text = "キミ、いつまでここにいるの？　あたしも行けるものならここから船で#bビクトリアアイランド#kに行きたいんだけどさ……色々とあるのよ。";
				return cm.sendSimple(text);
			}
		case 2:
			{
				// BB後
				var text = "ビクトリアアイランドでは職業に就けるそうよ。戦士になるには#bペリオン#kだったかな？　その村は遺跡とかあって、原住民が歩きまわってるんだって。ま、あたしもサウスペリ原住民みたいなもんだけどさ。あーあ、なんか、たるぅ。";
				return cm.sendSimple(text);
			}
		default:
			break;
	}

	return cm.dispose();
}