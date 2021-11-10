// コブシを開いて立て
// 選択の分かれ道

var npc_talk_status = 0;

function action(mode, type, selection) {
	if (mode != 1) {
		if (npc_talk_status == 2) {
			// BB後
			var text = "盗賊を体験してみたかったらもう一度俺に声をかけてくれよ。";
			cm.sendSimple(text);
		}
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 1:
			{
				// BB後
				var text = "盗賊はLUKと、ある程度のDEXとSTRが重要になる職業で、戦闘では、相手を奇襲したり、姿を隠したり…特殊なスキルを使えるんだ。高い機動力と回避率を持つ盗賊は、様々なスキルを駆使して戦うことができる。";
				return cm.sendSimple(text);
			}
		case 2:
			{
				// BB後
				var text = "どうだい？盗賊を体験してみないか？";
				return cm.sendYesNo(text);
			}
		case 3:
			{
				// 職業体験カットシーン
				cm.MovieClipIntroUI(true);
				cm.warp(1020400, 0);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}