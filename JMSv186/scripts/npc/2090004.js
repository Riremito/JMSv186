/* Author: aaroncsn(MapleSea Like)(Incomplete)
	NPC Name: 		Mr. Do
	Map(s): 		Mu Lung: Mu Lung(2500000000)
	Description: 		Potion Creator
*/
//importPackage(Packages.client);

var status = 0;
var selectedType = -1;
var selectedItem = -1;
var item;
var mats;
var matQty;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1)
		status++;
	else
		cm.dispose();
	if (status == 0 && mode == 1) {
		// BB後
		var selStr = "私は多才多能な人なんだ。望むのが何なのか言ってみて。#b"
		var options = new Array("薬を製造する", "呪文書を製造する", "薬剤を寄付する", "掛け軸復元をあきらめたいが...");
		for (var i = 0; i < options.length; i++) {
			selStr += "\r\n#L" + i + "# " + options[i] + "#l";
		}

		cm.sendSimple(selStr);
	} else if (status == 1 && mode == 1) {
		selectedType = selection;
		var selStr;
		var items;
		if (selectedType == 0) { //Make a medicine
			cm.sendNext("薬が作りたいならば、まず薬製造書から勉強してよ。 基本がない者がやたらに薬を使う事ほど危険なことってないからね。");
			cm.dispose();
			return;
		}
		else if (selectedType == 1) {//Make a scroll
			selStr = "どんな呪文書が作りたいのか？#b";
			items = new Array("#t2043000#", "#t2043100#", "#t2043200#",
				"#t2043300#", "#t2043700#", "#t2043800#",
				"#t2044000#", "#t2044100#", "#t2044200#",
				"#t2044300#", "#t2044400#", "#t2044500#", "#t2044600#",
				"#t2044700#", "#t2044800#", "#t2044900##k");
		}
		else if (selectedType == 2) {//Donate medicine ingredients
			selStr = "薬剤を寄付してくれるって？とても嬉しい話だね。アイテムの寄付は #b100個#k 単位で受け取っているの。 寄付してくれた人には呪文書が作れる玉を渡しているよ。どんなアイテムを寄付してくれるの？ #b";
			items = new Array("#t4000276#", "#t4000277#", "#t4000278#", "#t4000279#", "#t4000280#", "B#t4000291#", "#t4000292#", "#t4000286#", "#t4000287#", "#t4000293#", "#t4000294#",
				"#t4000298#", "#t4000284#", "#t4000288#", "#t4000285#", "#t4000282#", "#t4000295#", "#t4000289#", "#t4000296#", "#t4000297##k");
		}
		else {//I want to forfeit the restoration of Portrait Scroll...
			cm.dispose();
			return;
		}
		for (var i = 0; i < items.length; i++) {
			selStr += "\r\n#L" + i + "# " + items[i] + "#l";
		}
		cm.sendSimple(selStr);
	}
	else if (status == 2 && mode == 1) {
		selectedItem = selection;
		if (selectedType == 1) { //Scrolls
			var itemSet = new Array(2043000, 2043100, 2043200, 2043300, 2043700, 2043800, 2044000, 2044100, 2044200, 2044300, 2044400, 2044500, 2044600, 2044700, 2044800, 2044900);
			var matSet = new Array(new Array(4001124, 4010001), new Array(4001124, 4010001), new Array(4001124, 4010001), new Array(4001124, 4010001), new Array(4001124, 4010001),
				new Array(4001124, 4010001), new Array(4001124, 4010001), new Array(4001124, 4010001), new Array(4001124, 4010001), new Array(4001124, 4010001), new Array(4001124, 4010001),
				new Array(4001124, 4010001), new Array(4001124, 4010001), new Array(4001124, 4010001), new Array(4001124, 4010001), new Array(4001124, 4010001));
			var matQtySet = new Array(new Array(100, 10), new Array(100, 10), new Array(100, 10), new Array(100, 10), new Array(100, 10), new Array(100, 10), new Array(100, 10),
				new Array(100, 10), new Array(100, 10), new Array(100, 10), new Array(100, 10), new Array(100, 10), new Array(100, 10), new Array(100, 10), new Array(100, 10),
				new Array(100, 10));
			item = itemSet[selectedItem];
			mats = matSet[selectedItem];
			matQty = matQtySet[selectedItem];
			var prompt = "#b#t" + item + "##k?が作りたいのか？ #t" + item + "#を作ろうとしたら#b#t4001124# 100個#kと#b#t4010001# 10個#kが必要なの。 #k";
			if (mats instanceof Array) {
				for (var i = 0; i < mats.length; i++) {
					//prompt += "\r\n#i" + mats[i] + "# " + matQty[i] + " #t" + mats[i] + "#";
					prompt += "\r\n#i" + mats[i] + "##t" + mats[i] + "# " + matQty[i] + "個";
				}
			}
			else {
				prompt += "\r\n#i" + mats + "# " + matQty + " #t" + mats + "#What do you think? Would you like to make on right now?";
			}
			prompt += "\r\n\r\nどう？作ってみる？";
			// no 気が変わったようだね？ よし、もう一度考えさせるチャンスを与えるよ。
			cm.sendYesNo(prompt);
		}
		else if (selectedType == 2) {
			status = 3;
			var itemSet = new Array(4000276, 4000277, 4000278, 4000279, 4000280, 4000291, 4000292, 4000286, 4000287, 4000293, 4000294, 4000298, 4000284, 4000288, 4000285, 4000282, 4000295, 4000289, 4000296, 4031435);
			item = itemSet[selectedItem];
			var prompt = "本当に#b#t" + item + "##k 100個を寄付してくれるの？";
			// no なんだ。ああしたりこうしたり。なんでこう気まぐれなのか、まったく・・・
			// yes err 寄付する薬剤を本当に持っているのかETCウィンドウに空きがないのではないか確認してくれ。
			cm.sendYesNo(prompt);
		}
	} else if (status == 3 && mode == 1) {
		var complete = true;
		if (mats instanceof Array) {
			for (var i = 0; complete && i < mats.length; i++) {
				if (matQty[i] == 1) {
					if (!cm.haveItem(mats[i])) {
						complete = false;
					}
				}
				else {
					var count = 0;
					var iter = cm.getInventory(4).listById(mats[i]).iterator();
					while (iter.hasNext()) {
						count += iter.next().getQuantity();
					}
					if (count < matQty[i])
						complete = false;
				}
			}
		}
		else {
			var count = 0;
			var iter = cm.getInventory(4).listById(mats).iterator();
			while (iter.hasNext()) {
				count += iter.next().getQuantity();
			}
			if (count < matQty)
				complete = false;
		}

		if (!complete || !cm.canHold(2044900))
			cm.sendOk("材料が足りないか消費ウィンドウに空きがないのではないか確認してくれ。");
		else {
			if (mats instanceof Array) {
				for (var i = 0; i < mats.length; i++) {
					cm.gainItem(mats[i], -matQty[i]);
				}
			}
			else
				cm.gainItem(mats, -matQty);
		}
	}
}

