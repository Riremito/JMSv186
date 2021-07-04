// キノコの像
// https://www.nicovideo.jp/watch/sm5929362

function ItemInfo(itemid) {
	return "#v" + itemid + "##t" + itemid + "#";
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
				cm.sendYesNo("スノーボードを持ってきたのか？");
				return;
			}
		case 1:
			{
				var text = "渡したいスノーボードはどれだ？同じものが2つ以上ある場合は1番左上にあるものになるから、気をつけろよ。\r\n";
				text += "#L" + 5 + "##b" + ItemInfo(2031007) + "#l#k\r\n";
				text += "#L" + 6 + "##b" + "呪われた強化書(70%)" + "#l#k\r\n";
				text += "#L" + 7 + "##b" + "呪われた強化書(30%)" + "#l#k\r\n";
				cm.sendSimple(text);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}