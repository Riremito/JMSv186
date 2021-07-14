// OS3Aマシーン 地下道入口, 502010010
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
				var text = "行き先を選択してください（テスト）\r\n";
				// 秘密基地地下道
				text += "#L" + 502010100 + "##b#m502010100##l\r\n";
				// OSSS秘密基地格納庫
				text += "#L" + 502010000 + "##b#m502010000##l\r\n";
				cm.sendSimple(text);
				return;
			}
		case 1:
			{
				cm.warp(selection, "sp");
				break;
			}
		default:
			break;
	}

	cm.dispose();
}