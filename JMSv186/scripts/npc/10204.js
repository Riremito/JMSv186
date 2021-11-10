// コブシを開いて立て
// 選択の分かれ道

var npc_talk_status = 0;

function action(mode, type, selection) {
	if (mode != 1) {
		if (npc_talk_status == 2) {
			// BB後
			var text = "海賊を体験してみたかったらもう一度私に声をかけて。";
			cm.sendSimple(text);
		}
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 1:
			{
				// BB後
				var text = "海賊は高いDEX…もしくはSTRが重要になる職業で、銃か拳で戦うんだ。銃…ガンスリンガーは属性別の弾丸を使って効率的に攻撃したり、バトルシップって言う船に乗って強力な攻撃をしかける事ができる。拳…インファイターは、変身する事で強力なスキルを使えるようになるよ。";
				return cm.sendSimple(text);
			}
		case 2:
			{
				// BB後
				var text = "どう？海賊を体験してみる？";
				return cm.sendYesNo(text);
			}
		case 3:
			{
				// 職業体験カットシーン
				cm.MovieClipIntroUI(true);
				cm.warp(1020500, 0);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}