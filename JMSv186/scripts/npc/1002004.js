// アリの巣
// リス港口高級タクシー

var npc_talk_status = -1;
function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var text = "初心者ではない方には決められた料金が請求されます。アリの巣広場はビクトリアアイランドの中央にあるダンジョンの奥の、24時間屋台が棲んでいるところです。#b10,000メル#kでアリの巣広場まで如何でしょうか？\r\n";
				return cm.sendYesNo(text);
			}
		case 1:
			{
				cm.warp(105070001, 0);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}