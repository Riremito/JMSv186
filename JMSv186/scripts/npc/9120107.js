// キャサリン
var status = -1;
var last_selection = 0;

function ItemInfo(itemid) {
	return "#v" + itemid + "##t" + itemid + "#";
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	switch (status) {
		case 0:
			{
				switch (last_selection) {
					case 0:
						{
							var text = "いらっしゃいませ。何かご用ですか？\r\n";
							text += "#L" + 1 + "#パチンコ景品を貰いたい" + "#l\r\n";
							text += "#L" + 2 + "#パチンコダンジョンに行きたい" + "#l\r\n";
							text += "#L" + 3 + "#パチバルログの魂と景品を交換したい" + "#l\r\n";
							text += "#L" + 4 + "#パチンコの帝王の称号について聞く" + "#l\r\n";
							cm.sendSimple(text);
							return;
						}
					case 2:
						{
							cm.sendNext("わかったわ。またね。");
							break;
						}
					default:
						break;
				}
			}
		case -1:
			{
				cm.sendNext("XXXX");
				break;
			}
		case 1:
			{
				last_selection = selection;
				switch (selection) {
					case 1:
						{
							cm.sendOk("景品交換UI");
							break;
						}
					case 2:
						{
							var level = cm.getPlayer().getLevel();
							var text = "あなたのレベルは" + level + "ね。\r\n";
							text += Math.floor(level / 10) + "ダンジョンに入りたいのかしら？\r\n";
							cm.sendYesNo(text);
							return;
						}
					case 3:
						{
							var text = "えーっと…はい、確かに#b" + ItemInfo(4001315) + "#kを持っているわね。どちらのアイテムを賞品と交換するのかしら？\r\n";
							text += "#L" + 5 + "##b" + ItemInfo(2031007) + "#l#k\r\n";
							text += "#L" + 6 + "##b" + "呪われた強化書(70%)" + "#l#k\r\n";
							text += "#L" + 7 + "##b" + "呪われた強化書(30%)" + "#l#k\r\n";

							cm.sendNext(text);
							break;
						}
					case 4:
						{
							var text = "パチンコの帝王、ね。なんて素敵な響きかしら。\r\n";
							text += "#L" + 5 + "#パチンコの帝王とは?" + "#l\r\n";
							text += "#L" + 6 + "#当たりランクを確認する" + "#l\r\n";
							text += "#L" + 7 + "#パチンコの帝王の座に着く" + "#l\r\n";

							cm.sendNext(text);
							break;
						}
					default:
						break;
				}
				break;
			}
		case 2:
			{
				if (last_selection == 2) {
					var text = "パチンコダンジョンに入るには、#b" + ItemInfo(4001312) + "#kが必要よ。";
					cm.sendNext(text);
				}
				break;
			}
		default:
			break;
	}

	cm.dispose();
}