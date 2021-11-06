// マチルダ

// ネットカフェ用の分岐が必要

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// BB後
				var text = "#bネットカフェご利用のあなたへのサービス！#k\r\n";
				text += "現在ご使用中の武器に不満はありませんか？攻撃力が足りないと思っていませんか？#bネクソン公認ネットカフェ#kでのみ楽しめる武器貸与サービスを開始しました！";
				return cm.sendSimple(text);
			}
		case 1:
			{
				var text = "普通の武器であるとがっかりするにはまだ早いです！装備制限がぐっと下がっているのでワンランク上の強さが体験できます！#bネットカフェ#kでのみ楽しめる驚きの特典です！ \r\n";
				text += "#r但し、ログアウトと同時に貸与した武器は自動に消えますのでご参考ください。#k";
				return cm.sendSimple(text);
			}
		default:
			break;
	}

	return cm.dispose();
}