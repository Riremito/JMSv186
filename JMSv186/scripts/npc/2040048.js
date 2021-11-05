// ナナ
// フロリナビーチ

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// デバッグモード
				if (!cm.haveItem(4031134)) {
					cm.gainItem(4031134, 1);
				}

				// 適当
				var text = "港口から少し離れたところに#bビーチ#kという幻想的な海岸があると聞いたことはあるか？#b2000メル#kを払うか#bフリーパスカード#kを持っているなら、いつでも俺がそこに運んでやるぜ。\r\n";
				text += "#L" + 0 + "##b2000メル#k払う#l\r\n";
				text += "#L" + 1 + "##bフリーパスカード#kを持っている。#l\r\n";
				text += "#L" + 2 + "##bフリーパスカード#kとは？#l\r\n";
				return cm.sendSimple(text);
			}
		case 1:
			{
				if (selection != 0) {
					return cm.dispose();
				}
				// 適当
				var text = "#b2000メル#kを払ってビーチに行くんだな？でも、そこにもモンスターがいるらしいから油断するよ！では、早速出向準備をするが…今すぐビーチに行くのか？";
				return cm.sendYesNo(text);
			}
		case 2:
			{
				if (cm.haveItem(4031134)) {
					cm.gainItem(4031134, -1);
					cm.saveLocation("FLORINA");
					cm.warp(110000000, 0);
				}
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}