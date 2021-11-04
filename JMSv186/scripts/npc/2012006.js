// オルビス 船
// map 200000100

var ship_platform = Array(
	[200000110, "エリニア行きの船に乗る昇降場"],
	[200000120, "ルディブリアム行きの船に乗る昇降場"],
	[200000130, "リプレ行きの船に乗る昇降場"],
	[200000140, "武陵行きのツルに乗る昇降場"],
	[200000150, "アリアント行きのジニに乗る昇降場"],
	[200000160, "エレヴ行きの船に乗る昇降場"]
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
				var text = "オルビスステーションには多くの昇降場があります。目的地によって正しい昇降場へ行かなければなりません。どの方面に向かう船の昇降場に行くんですか？\r\n";
				for (var i = 0; i < ship_platform.length; i++) {
					text += "#L" + ship_platform[i][0] + "##b" + ship_platform[i][1] + "#k#l\r\n";
				}
				return cm.sendSimple(text);
			}
		case 1:
			{
				var mapid = selection;
				cm.warp(mapid, "west00");
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}