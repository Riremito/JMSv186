// map カニングシティ 103000000
// npc モンロン

function start() {
	cm.sendYesNo("もしかしてネットカフェから接続してるんじゃないのか?ならさ、中に入ってきなよ。君のよく知った場所にいけるはずさ。どうだ、ちょっくら入ってみないか?");
}

function action(mode, type, selection) {
	if (mode == 0) {
		cm.sendNext("今は忙しそうだな?でもな?せっかくネットカフェから接続してるんなら、中に入ってみればいいのに。不思議な場所に行けるかもしれないよ。");
	} else {
		if (1/*cm.haveItem(5420007)*/) {
			cm.warp(193000000, 0);
		} else {
			cm.sendNext("あれ、君はネットカフェから接続してないみたいだね?もし、そうなら中には絶対入れないよ…。");
		}
	}
	cm.dispose();
}