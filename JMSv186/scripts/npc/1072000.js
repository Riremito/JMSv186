// 戦士転職教官

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				if (cm.haveItem(4031008)) {
					// 原文ママ, 魔法使いも同じ
					var text = "うん…これは間違いなく#b#p1022000##k様の手紙だな…君、戦士の2次転職試験を受けるため、ここまで私を尋ねて来たようだね。よし…それでは戦士の2次転職試験について簡単に説明しよう。あまり複雑ではないから心配しないでいいよ。";
					return cm.sendSimple(text);
				}
			}
		case 1:
			{
				// 原文ママ, 魔法使いも同じ
				var text = "私が君をある秘密のマップに送ってあげよう。そこには普通のフィールドには見られないかわったモンスターが現われる。もちろん姿は同じだが性格は全然違う。やつらからは経験値も一般アイテムも貰えない。";
				// prev next
				return cm.sendSimple(text);
			}
		case 2:
			{
				// 魔法使い
				// var text = "その中でやつらを倒すと#b#z4031013##kというアイテムを得ることができる。それはやつらの醜くて汚い心が集まって作られた特別な玉だ。この玉を30個集めて中にいる私の仲間に声を掛ければ合格だ。";
				// 原文ママ
				var text = "そいつらを倒すと#b#z4031013##kというアイテムを得ることができる。それはやつらの醜くて汚い心が集まって作られた特別な玉だ。この玉を30個集めて中にいる私の仲間に声を掛ければ合格だ。";
				// prev next
				return cm.sendSimple(text);
			}
		case 3:
			{
				// 原文ママ, 魔法使いも同じ
				var text = "一度中に入ったら任務を果すまで外に出ることはできない。そして、その中で死んでもやっぱり経験値は落ちるから気を付けた方が良いよ。ちゃんと準備をしなければならないということ。さあ…それじゃ今すぐ試験を受けるか？";
				return cm.sendYesNo(text);
			}
		case 4:
			{
				// 原文ママ , 魔法使いも同じ, アイテム名だけ青く変更
				var text = "よし！それじゃあ中に入れてやろう。中でモンスターを倒して#b#z4031013##kを30個集めた後、中にいる私の仲間に声を掛ければ試験合格の証拠品である#b#z4031012##kを受け取ることができる。それじゃ検討を祈る。";
				return cm.sendSimple(text);
			}
		case 5:
			{
				// 同一マップが3つ存在する
				cm.warp(108000300, 0);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}

/*
var status = -1;

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}
	if (status == 0) {
		if (cm.getQuestStatus(100004) == 1) {
			cm.sendOk("You will have to collect me #b30 #t4031013##k. Good luck.");
			status = 3;
		} else {
			if (cm.getQuestStatus(100004) == 2) {
				cm.sendOk("You're truly a hero!");
				cm.safeDispose();
			} else if (cm.getQuestStatus(100003) >= 1) {
				cm.completeQuest(100003);
				if (cm.getQuestStatus(100003) == 2) {
					cm.sendNext("Oh, isn't this a letter from #bDances with Balrog#k?");
				}
			} else {
				cm.sendOk("I can show you the way once your ready for it.");
				cm.safeDispose();
			}
		}
	} else if (status == 1) {
		cm.sendNextPrev("So you want to prove your skills? Very well...")
	} else if (status == 2) {
		cm.askAcceptDecline("I will give you a chance if you're ready.");
	} else if (status == 3) {
		cm.startQuest(100004);
		cm.sendOk("You will have to collect me #b30 #t4031013##k. Good luck.")
	} else if (status == 4) {
		//	    cm.gainItem(4031008, -1);
		cm.warp(108000300, 0);
		cm.dispose();
	}
}
*/