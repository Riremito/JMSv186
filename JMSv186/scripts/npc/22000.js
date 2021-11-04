// シャンクス
// 港口からメイプルアイランドへ

function ToVictoria(npc_talk_status, selection) {
	switch (npc_talk_status) {
		case 0:
			{
				// デバッグモード
				if (!cm.haveItem(4031801)) {
					cm.gainItem(4031801, 1);
				}
				// 原文ママ
				var text = "この船に乗れば、広大なフィールドが広がる#bビクトリアアイランド#kに行くことができる。#e150 メル#n必要だけどね。ただ、一度ビクトリアアイランドに渡ってしまうと、修行をつんで一人前にならなければ、このメイプルアイランドには戻ってこれなくなるんだ。ここに比べて危険の多い島でもあるから、レベルを5ぐらいにあげてから旅立つとよいだろうね。";
				return cm.sendYesNo(text);

			}
		case 1:
			{
				if (cm.haveItem(4031801)) {
					// 原文ママ
					var text = "それはアムホストの長老ルーカス様の推薦書じゃないか!これがあるなら早く行ってくれ、金はいらんぞ。";
					return cm.sendSimple(text);
				}
				if (cm.getMeso() < 150) {
					// 適当
					cm.sendOk("メルが不足しています。");
					return cm.dispose();
				}
				// 適当
				return cm.sendSimple("150メル受け取りました。");
			}
		case 2:
			{
				if (cm.haveItem(4031801)) {
					// 原文ママ
					var text = "推薦書を持っているから、特別に料金は免除しよう。さあ！ビクトリアアイランドに出発するぞ！揺れるかもしれないから何かに捕まってくれ！";
					// sendNextPrevが正常に動作しない
					return cm.sendSimple(text);
				}
				cm.gainMeso(-150);
				cm.warp(104000000);
				return cm.dispose();
			}
		case 3:
			{
				cm.warp(104000000);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}

function FromVictoria(npc_talk_status, selection) {
	var fame = cm.getPlayer().getFame();
	switch (npc_talk_status) {
		case 0:
			{
				// 人気度制限
				if (fame < 300) {
					// 原文ママ
					var text = "人気度が300以上ないとメイプルアイランドに行くことはできない。";
					return cm.sendSimple(text);
				}
				// 原文ママ
				var text = "特別にサウスペリに移動させてあげよう。どうだい #bメイプルアイランドのサウスペリ#kに行きたいかい？";
				return cm.sendYesNo(text);

			}
		case 1:
			{
				if (fame < 300) {
					return cm.dispose();
				}
				cm.warp(2000000);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}
	npc_talk_status++;

	var mapid = cm.getMapId();

	if (mapid == 104000000) {
		return FromVictoria(npc_talk_status, selection);
	}

	return ToVictoria(npc_talk_status, selection);
}
