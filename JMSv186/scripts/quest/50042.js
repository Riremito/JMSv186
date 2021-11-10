// パチンコデビュー

var npc_talk_status = -1;

function start(mode, type, selection) {
	if (mode != 1) {
		return qm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// BB後
				//qm.forceCompleteQuest();
				//qm.forceStartQuest();
				return qm.sendSimple("ごきげんよう。今日はどんより曇り空ね。雨でも降るのかしら。");
			}
		case 1:
			{
				// BB後
				return qm.sendSimple("こんな日には冒険するよりも、快適な室内で気分転換もいいんじゃないかしら。");
			}
		case 2:
			{
				// BB後
				return qm.sendSimple("気分転換といえばパチンコね。早速だけど、パチンコはやったことあるかしら？気軽に楽しめるわよ。丁度今回新台も入荷したところだから、一度やってみるといいわ。");
				// prev next
			}
		case 3:
			{
				// prev next
				return qm.sendSimple("パチンコ屋まで来てくださればパチンコ玉を少しあげるわ。近くの町からすぐにでも来れるはずだわ。");
			}
		case 4:
			{
				qm.forceStartQuest();
				return qm.dispose();
			}
		default:
			break;
	}
	return qm.dispose();
}

function end(mode, type, selection) {
	if (mode != 1) {
		return qm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// BB後
				return qm.sendSimple("あら、来てくれたのね。それじゃあパチンコ玉をちょっとあげるから、これでパチンコデビューしてごらんなさい。");
			}
		case 1:
			{
				// BB後
				return qm.sendSimple("今日の結果がイマイチでも、明日またちょっと玉を分けてあげるあｋら心配なく楽しんで来てね。");
			}
		case 2:
			{
				// BB後
				return qm.sendSimple("もし玉が足りなければ、そっちにある#bパチンコ玉交換機を利用するといいわよ。#k");
			}
		case 3:
			{
				// パチンコ玉を100玉追加する処理
				qm.forceCompleteQuest();
				return qm.dispose();
			}
		default:
			break;
	}
	return qm.dispose();
}