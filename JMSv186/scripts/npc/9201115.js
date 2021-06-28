var status = -1;
var selected = 0;

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	switch (status) {
		case 0:
			{
				var text = "あなたは5つの恐怖を打ち破ることができると、本当に思いますか？　彼らはこの古代王国の中で、最も強い戦士たちなのです。\r\n";
				text += "#L" + 1 + "##b" + "はい！　打ち果たしてみせます！" + "#l#k\r\n";
				text += "#L" + 2 + "##b" + "いいえ…ここから出たいです。" + "#l#k\r\n";
				cm.sendSimple(text);
				return;
			}
		case 1:
			{
				selected = selection;
				if (selection == 1) {
					cm.sendNext("解りました…では、聖なる戦いをここに開始します…！");
					return;
				}
				else if (selection == 2) {
					cm.sendNext("解りました。あなたを外へとお送りしましょう。");
					return;
				}
				break;
			}
		case 2:
			{
				if (selected == 1) {
					// マーガナから順番に自動召喚される
					cm.spawnMonster(9400433, 124, 144);
				}
				else if (selected == 2) {
					cm.warp(803000505, "st00");
				}
				break;
			}
		default:
			break;
	}
	cm.dispose();
}