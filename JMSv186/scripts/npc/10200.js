// ヘレナ
// 選択の分かれ道

var npc_talk_status = 0;

function action(mode, type, selection) {
	if (mode != 1) {
		if (npc_talk_status == 2) {
			// BB後
			var text = "弓使いを体験してみたかったらもう一度私に声をかけてください。";
			cm.sendSimple(text);
		}
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 1:
			{
				// BB後
				var text = "弓使いはDEXとSTRが重要な職業で、戦場の後列から遠距離攻撃を担当しています。地形を用いた狩りにもすごく強いのが特徴です。";
				return cm.sendSimple(text);
			}
		case 2:
			{
				// BB後
				var text = "どうですか？弓使いを体験してみませんか？";
				return cm.sendYesNo(text);
			}
		case 3:
			{
				// 職業体験カットシーン
				cm.MovieClipIntroUI(true);
				cm.warp(1020300, 0);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}