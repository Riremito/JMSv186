// ハインズ
// 選択の分かれ道

var npc_talk_status = 0;

function action(mode, type, selection) {
	if (mode != 1) {
		if (npc_talk_status == 2) {
			// BB後
			var text = "魔法使いを体験してみたかったらもう一度私に声をかけておくれ。";
			cm.sendSimple(text);
		}
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 1:
			{
				// BB後
				var text = "魔法使いは、華麗な属性魔法とグループでの戦闘時に有効な様々な補助魔法を持っておる。更に、2次転職以後に覚える属性魔法は、反対属性の敵に致命的なダメージを与えられるのだ。";
				return cm.sendSimple(text);
			}
		case 2:
			{
				// BB後
				var text = "どうじゃ？魔法使いを体験してみないか？";
				return cm.sendYesNo(text);
			}
		case 3:
			{
				// 職業体験カットシーン
				cm.MovieClipIntroUI(true);
				cm.warp(1020200, 0);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}