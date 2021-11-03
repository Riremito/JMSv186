// リプレ - 時間の神殿
// コルバ

var warp_map = Array(
	// ポータル番号が見れないので適当です
	[200090500, 0, "#bドラゴンに変身したいです。#k"]
);

var npc_talk_status = -1;
function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				var mapid = cm.getMapId();
				var text = "羽があればそこに行けるじゃろう。じゃが、それだけでは足りんのじゃ。剣より鋭い風の狭間を飛ぶためには、丈夫な鱗も必要なんじゃ。戻ってくる方法まで知っておる半獣族は、もうワシ一人しかおらん…もしあそこに行きたいんじゃったら、ワシが変身させてやろう。お前の今の姿が何であれ、今、この瞬間だけは　#bドラゴン#kになるのじゃ…。\r\n";
				for (var i = 0; i < warp_map.length; i++) {
					text += "#L" + warp_map[i][0] + "#" + warp_map[i][2] + "#l\r\n";
				}
				return cm.sendSimple(text);
			}
		case 1:
			{
				var mapid = selection;
				for (var i = 0; i < warp_map.length; i++) {
					if (warp_map[i][0] == mapid) {
						cm.useItem(2210016);
						cm.warp(warp_map[i][0], warp_map[i][1]);
						return cm.dispose();
					}
				}
				break;
			}
		default:
			break;
	}

	return cm.dispose();
}