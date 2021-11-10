// ペドロ
// 海賊3次

var map_job_town = 120000000;
var npc_job = 1072008;
// 強靭のネックレス
var quest_item_1 = 4031057; // or 4032785
var quest_item_2 = 4031058; // or 4032789
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
				var text = "私に何の用かね？\r\n";
				text += "#L" + 0 + "##b3次転職がしたいです#k#l\r\n";
				text += "#L" + 1 + "##bジャクムダンジョンクエストを許可してください。#k#l\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				// 原文ママ
				if (selection == 0) {
					if (cm.haveItem(quest_item_2)) {
						// 原文ママ
						var text = "ほう…無事に聖地を見つけ、問いに正しい答えを返せたようだな。これで晴れて2つのテストはクリアだ。おめでとう。では、まず先にネックレスを預かろう。";
						return cm.sendSimple(text);
					}
					if (cm.haveItem(quest_item_1)) {
						// 原文ママ
						var text = "ほほう…#b#p" + npc_job + "##kからの任務を完遂したようだな。君ならできると思っていたよ。だが、まだ気を抜くのは早い。2つ目のテストが残っているからな。じゃあ、まずはテストの前にネックレスを預かろうか。";
						return cm.sendSimple(text);
					}
					var text = "ん？　何用だね？　ほう……3次転職をして、更に強い海賊になりたいと？　無論、私の力で君を更に強くさせることはできるが、その前に君がどれだけ熱心に修練に励んだかを見せて欲しい。これまで強くなりたいと願って多くの者が私の元を訪れてきたが、実際に己の強さを署名できた者は数少ない。どうかね？　そうそう簡単ではないが、やってみるかね？";
					return cm.sendYesNo(text);
				}
			}
		case 2:
			{

				if (cm.haveItem(quest_item_2)) {
					// 原文ママ
					var text = "よし…これで君はもっと強い海賊になれるはずだ。だが、その前にSPを全て消費したかどうか、確認させて欲しい。レベル70までに得た SPを全て消費しないと、3次転職はできないからね。あぁ、それと、3次転職ではどの職業にするかは特に悩む必要はない。そのために、2次転職の時に数々悩んでもらったのだ。さて、今すぐ転職するかね？";
					return cm.sendYesNo(text);
				}
				if (cm.haveItem(quest_item_1)) {
					// 原文ママ
					var text = "うむ…確かに。では、これで残りは知恵のテストのみだな。このテストをクリアできれば、君はより一層強い海賊になるだろう。2つ目のテストは、オシリアの雪原のどこかにある、モンスターの近寄れない小さな聖地で行われる。見た目は平凡な聖地だが、特別なアイテムを捧げると、その者の知恵を試す試練が訪れるだろう。";
					// prevnext
					return cm.sendSimple(text);
				}
				// 原文ママ
				var text = "よし！　では早速テストをしよう。君に証明して欲しいのは2つ。君の持つ力と知恵を示すのだ。まずは力のテストから説明しよう。君が、1次、2次転職をした際に世話になったであろう、#m" + map_job_town + "#の#b#p" + npc_job + "##kがいるだろう？　彼女を訪ねれば、君に1つ任務を与えてくれるだろう。その任務を完遂し、#p" + npc_job + "#から #b#z" + quest_item_1 + "##kを受け取ってくるのだ。";
				return cm.sendYesNo(text);
			}
		case 3:
			{
				if (cm.haveItem(quest_item_2)) {
					// 3次転職処理
					// 原文ママ
					var text = "おお…君はこの間、3次転職の難関をクリアした者だね。#bヴァイキング#kとしての生活はどうかね？　これから、更なる広い世界を旅しようと思っているなら、より一層強くならねばならないだろう。この大陸には、私も知らないような凶悪なモンスターがまだまだたくさんいるからな。何か気になることがあれば、いつでも訪ねてくるといい。頑張るのだぞ。";
					return cm.sendSimple(text);
				}
				if (cm.haveItem(quest_item_1)) {
					// 原文ママ
					var text = "まずは聖地を訪ね、特別なアイテムを捧げるのだ。そして、知恵の試練が始まったら、与えられた質問に正しい答えを返すのだ。君の答えに迷いがなければ、聖地は君を認め、#b#z" + quest_item_2 + "##kを与えるだろう。その#b#z" + quest_item_2 + "##kを私の元まで持ってくることができれば、私も君を認め、更に強い海賊へと転身させよう。では、幸運を祈る。";
					// prev next
					return cm.sendSimple(text);
				}
				// 原文ママ
				var text = "2つ目は知恵のテストだが、これは力のテストを無事クリアできたら行う。まずは、力のテストをクリアし、#b#z" + quest_item_1 + "##kを持ってくるのだ。じゃあ、#b#p" + npc_job + "##kには私から話を通しておくから、この後すぐに訪ねてみるといい。決して簡単ではないだろうが、きっと君ならできると信じているよ。";
				// prev next
				return cm.sendSimple(text);
			}
		case 4:
			{
				if (cm.haveItem(quest_item_2)) {
					// 原文ママ
					var text = "さて。少しだが、君に SPと APを与えたから確認してみて欲しい。これで君は十分強い海賊になれただろう。だが、これから先はまだまだ辛く厳しい冒険が君を待ち受けている。もし君が己の成長に限界を感じたら、もう一度私を訪ねて欲しい。その時きっとまた、君の力になれるだろう。";
					// prev next
					return cm.sendSimple(text);
				}
			}
		default:
			break;
	}

	return cm.dispose();
}


/*
var status = 0;
var job;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0 && status == 1) {
		cm.sendOk("Make up your mind and visit me again.");
		cm.dispose();
		return;
	}
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
		if (!(cm.getJob() == 510 || cm.getJob() == 520)) {
			cm.sendOk("君ならジャクムに挑戦するにもう十分だ。あの閉鉱の奥の#b#p2030008##kに行ってみてくれ。私が承諾する。そのダンジョンを探索してくれ。");
			// よし、私が#b#p2030008##kのいる#bジャクムへの門#kへ送ってやろう。
			cm.dispose();
			return;
		}
		if ((cm.getJob() == 510 || cm.getJob() == 520) && cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
			cm.sendNext("You are indeed a strong one.");
		} else {
			cm.sendOk("Please make sure that you have used all your 2nd job skill point before proceeding.");
			cm.safeDispose();
		}
	} else if (status == 1) {
		if (cm.getPlayerStat("LVL") >= 70 && cm.getPlayerStat("RSP") <= (cm.getPlayerStat("LVL") - 70) * 3) {
			if (cm.getJob() == 510) {
				cm.changeJob(511);
				cm.gainAp(5);
				cm.sendOk("You are now a #bBuccaneer#k. May #rOdin#k be with you!");
				cm.dispose();
			} else if (cm.getJob() == 520) {
				cm.changeJob(521);
				cm.gainAp(5);
				cm.sendOk("You are now a #bValkyrie#k. May #rOdin#k be with you!");
				cm.dispose();
			}
		} else {
			cm.sendOk("Come back when you are level 70 and used SP.");
			cm.dispose();
		}
	}
}
*/