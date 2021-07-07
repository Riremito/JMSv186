// メロノン
// 夜市場分かれ道2 から 屋台 へ
// !npc 9330028
// https://www.youtube.com/watch?v=KcDHzsC1gsk

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

	var mapid = cm.getMapId();

	switch (status) {
		case 0:
			{
				// 前提クエスト 資格のテスト
				// クエスト未実施?
				//cm.sendOk("本当に屋台を倒せる勇敢なものがいないか…");
				cm.sendNext("君なら屋台を倒せるだろう。では、屋台があるところに送ってあげよう。気を付けてね！");
				return;
			}
		case 1:
			{
				// 741020101 ランダムに飛ぶ仕様?
				EnterBossMap(741020101);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}