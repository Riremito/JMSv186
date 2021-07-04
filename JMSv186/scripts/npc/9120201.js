// コンペイ
// ボディーガード
// Reactor Script 8001000

function GetSpawnItem(itemid) {
	if (cm.haveItem(itemid)) {
		return false;
	}

	cm.gainItem(itemid, 1);
	return true;
}

function EnterBossMap(mapid) {
	if (cm.getPlayerCount(mapid) == 0) {
		cm.resetMap(mapid);
	}
	cm.warp(mapid);
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
				// 姐御の櫛
				GetSpawnItem(4000138);
				cm.sendYesNo("ややっ！お前さんが持っているのはここの姐御の櫛じゃないか！？やっぱりオイラが見込んだとおりだった！お前さんなら大親分のやつを倒せるかもしれねぇ。本丸に攻め込むのか？");
				return;
			}
		case 1:
			{
				cm.sendYesNo("本当に入室するんだな？この部屋に長居はできないぜ！それとやつらは部屋の奥の宝箱の上に姐御の櫛を置くとすっ飛んで来るから気をつけろ。");
				return;
			}
		case 2:
			{
				// 悪夢の果て
				EnterBossMap(801040100);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}