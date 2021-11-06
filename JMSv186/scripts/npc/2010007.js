// ヘラクル
// ギルド

var status = -1;
var sel;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0 && status == 0) {
		cm.dispose();
		return;
	}
	if (mode == 1)
		status++;
	else
		status--;

	if (status == 0) {
		// BB後
		var text = "さあ、何か手伝おうか？\r\n";
		/*
		text += "#b#L0#ギルド人数を増やしたいです。#l\r\n"
		text += "#L1#ギルドを解散したいです。#l\r\n"
		text += "#L3#ギルドマスターを変更したいです。#l\r\n"
		text += "#L2#ギルドシステムについてもっと詳しく知りたいです。#l\r\n"
		text += "#L4#<シャレニアンの地下水路>について教えてください。#l\r\n"
		*/

		text += "#b#L0#ギルドを作成したいです。#l\r\n"; //?
		text += "#b#L2#ギルド人数を増やしたいです。#l\r\n";
		text += "#L1#ギルドを解散したいです。#l\r\n";


		cm.sendSimple(text);
	}
	else if (status == 1) {
		sel = selection;
		if (selection == 0) {
			if (cm.getPlayerStat("GID") > 0) {
				cm.sendOk("You may not create a new Guild while you are in one.");
				cm.dispose();
			} else
				cm.sendYesNo("Creating a Guild costs #b5,000,000 mesos#k, are you sure you want to continue?");
		} else if (selection == 1) {
			if (cm.getPlayerStat("GID") <= 0 || cm.getPlayerStat("GRANK") != 1) {
				cm.sendOk("You can only disband a Guild if you are the leader of that Guild.");
				cm.dispose();
			} else {
				// No そうか～よく決めてくれたな。今まで頑張ってきたギルドをなくしてはいけないだろう。
				cm.sendYesNo("ギルドを本当に解散したいのか。これはこれは、困ったな。一度解散しちゃうと、キミのギルドは永遠に削除されてしまうんだぜ。色んなギルド特権ももちろん一緒に消えるんだ。それでも、行うかい？");
			}
		} else if (selection == 2) {
			if (cm.getPlayerStat("GID") <= 0 || cm.getPlayerStat("GRANK") != 1) {
				cm.sendOk("You can only increase your Guild's capacity if you are the leader.");
				cm.dispose();
			} else {
				// ギルドメンバー数を増やしたくて来たのか？うむ、ギルドがずいぶん成長したようだな～既に知っていると思うが、ギルドメンバー数を増やすには我々ギルド本部にまた登録をしなければならない。もちろん手数料もGPを使用する必要があるんだ。ちなみに、ギルドメンバー数は最大200名まで増やすことができる。
				cm.sendYesNo("現在ギルドの最大人数は#b20人#kで、#b10人#k増やすために必要な手数料は#bGP 10000#kだ。ちなみに、君のギルドは現在#bGP 14363#kのGPを所持している。どう、ギルドメンバーを増やしたいか？");
				// No 手数料が負担になっているのか？時間が経てばGPくらいはすぐに貯まるから、そんなに心配することはない。それでは、また来てくれ。
			}
		}
	} else if (status == 2) {
		if (sel == 0 && cm.getPlayerStat("GID") <= 0) {
			cm.genericGuildMessage(1);
			cm.dispose();
		} else if (sel == 1 && cm.getPlayerStat("GID") > 0 && cm.getPlayerStat("GRANK") == 1) {
			cm.disbandGuild();
			cm.dispose();
		} else if (sel == 2 && cm.getPlayerStat("GID") > 0 && cm.getPlayerStat("GRANK") == 1) {
			cm.increaseGuildCapacity();
			cm.dispose();
		}
	}
}