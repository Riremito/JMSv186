// 警察官ミャオ
// 大王ムカデへ

var status = -1;
function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	var mapid = cm.getMapId();

	switch (status) {
		case 0:
			{
				// 前提クエストのチェック処理が必要
				cm.sendNext("秘密任務遂行のため、通路にお送りします。");
				return;
			}
		case 1:
			{
				// 抜け道
				// 10分の制限時間必要
				cm.warp(701010322, 0);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}