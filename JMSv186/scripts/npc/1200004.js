// プロ
// リエンへの船

var npc_talk_status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		return cm.dispose();
	}

	npc_talk_status++;
	switch (npc_talk_status) {
		case 0:
			{
				// BB後のテキスト
				var text = "もしやビクトリアアイランドを離れ、我々の村に行くつもりか？この船に乗ると#bリエン#kまで乗せていってやれるが…#b料金800#kメル必要だ。リエンに行くかい？行くのにかかる時間は、訳1分だ。";
				return cm.sendYesNo(text);
			}
		case 1:
			{
				cm.warp(140020300);
				return cm.dispose();
			}
		default:
			break;
	}

	return cm.dispose();
}
