// クリスタル 未来東京

var warp_map = Array(
	[802000200, "st00", "#b未来 - 2100年 トウキョウお台場#k"],
	[802000300, "st00", "#b未来 - 2095年 トウキョウ公園#k"],
	[802000500, "st00", "#b未来 - 2102年 トウキョウ秋葉原#k"],
	[802000600, "st00", "#b未来 - 2102年 トウキョウ上空#k"],
	[802000700, "st00", "#b未来 - 2102年 トウキョウ渋谷#k"],
	[802000800, "st00", "#b未来 - 2102年 トウキョウ六本木モール最上層#k"],
	[800040000, "st00", "#b過去 - 楓城　天下泰平#k"],
	[910000000, "out00", "#bデバッグ - #m910000000##k"]
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
				var text = "私はアーシア様の力を増幅させる者。タイムワープの許可がアーシア様より下りていれば、過去や未来のジパングに飛ばしてあげよう。\r\n";
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
						// debug
						if (!cm.haveItem(5252002)) {
							// 質屋にあった大ガマガエルの財布
							cm.gainItem(5252002, 1);
						}
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