// コブシを開いて立て
// 選択の分かれ道

var npc_talk_status = 0;

function action(mode, type, selection) {
	if (mode != 1) {
		if (npc_talk_status == 2) {
			// BB後
			var text = "戦士を体験してみたかったらもう一度俺に声をかけてくれよ。";
			cm.sendSimple(text);
		}
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 1:
			{
				// BB後
				var text = "戦士は、凄まじい攻撃力と体力を持つ職業で、戦場の最前線でその真価を発揮するんだ。基本攻撃力がとても強い職業で、高レベルのスキルを覚えればもっと強い力を発揮する事ができるよ。";
				return cm.sendSimple(text);
			}
		case 2:
			{
				// BB後
				var text = "どうだい？戦士を体験してみないか？";
				return cm.sendYesNo(text);
			}
		case 3:
			{
				// 職業体験カットシーン
				cm.MovieClipIntroUI(true);
				cm.warp(1020100, 0);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}