// ガシャポン 兵法書

var rewards = new Array(
	2370000,
	2370001,
	2370002,
	2370003,
	2370004,
	2370005,
	2370006,
	2370007,
	2370008,
	2370009,
	2370010,
	2370011,
	2370012
);

function RandomRewards() {
	var target = Math.floor(Math.random() * rewards.length);

	cm.gainItem(rewards[target], 1);
	return true;
}

function ShowProb() {
	var text = "この「ガシャポン」を1回ご使用いただくと、次のレアアイテムを含むいずれか1つを獲得できます。\r\n";
	text += "※購入回数に応じて抽選の確率が変動するものではございません。\r\n";
	text += "※全てのアイテムは重複して入手可能です。\r\n";

	for (var i = 0; i < rewards.length; i++) {
		text += "#v"+ rewards[i] + "##t" + rewards[i] +  "# #b(" + Math.floor(1 / rewards.length * 100) + "%)#k\r\n";
	}

	cm.sendOk(text);
}

var status = -1;
function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	switch (status) {
		case 0:
			{
				var text = "何の用ですか。\r\n";
				text += "#L" + 1 + "##b" + "ガシャポン（兵法書）を利用したいです" + "#l#k\r\n";
				text += "#L" + 2 + "##b" + "兵法書の使用方法について知りたいです" + "#l#k\r\n";
				text += "#L" + 0 + "##b" + "排出率" + "#l#k\r\n";
				cm.sendSimple(text);
				return;
			}
		case 1:
			{
				switch (selection) {
					case 0:
						{
							ShowProb();
							break;
						}
					case 1:
						{
							if (cm.haveItem(5220000)) {
								if (RandomRewards()) {
									cm.gainItem(5220000, -1);
								}
							}
							break;
						}
					case 2:
						{
							var text = "#b■兵法書の使用方法について。\r\n";
							text += "兵法書アイテムをダブルクリックすると経験値を得る事ができます。\r\n\r\n";
							text += "■兵法書に設定されている経験値が、兵法書使用時のキャラクターレベルの必要経験値（※１）より多かった場合の動作について。\r\n";
							text += "上記の場合、レベルアップするまでは兵法書の経験値を使用しますが、兵法書の経験値が残った場合、画面下の経験値バーに兵法書の残経験値が記録されます。\r\n\r\n";
							text += "■兵法書の残経験値の確認方法について。\r\n";
							text += "経験値バーの通常の色はグリーン色ですが、残経験値はオレンジ...ここでテキストは途切れている\r\n";

							cm.sendOk(text);
							break;
						}
					default:
						break;
				}
				break;
			}
		default:
			break;
	}

	cm.dispose();
}