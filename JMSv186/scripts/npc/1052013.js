// map ネットカフェ 193000000
// npc パソコン

function start() {
	var text = "接続する場所を選んでください。\r\n";

	text += "#b#L" + 190000000 + "#草原フィールド#l#k\r\n";
	text += "#b#L" + 191000000 + "#明るい森フィールド#l#k\r\n";
	text += "#b#L" + 192000000 + "#高原フィールド#l#k\r\n";
	text += "#b#L" + 195000000 + "#アリの巣ダンジョン#l#k\r\n";
	text += "#b#L" + 197000000 + "#ルディブリアム#l#k\r\n";
	text += "#b#L" + 196000000 + "#エルナス#l#k\r\n";
	text += "#b#L" + 880000000 + "#ワンワンマントル#l#k\r\n";
	text += "#b#L" + 881000000 + "#死霊の集う海岸#l#k\r\n";
	//text += "#b#L" + 260010100 + "#サボテンの砂漠#l#k\r\n";
	cm.sendSimple(text);
}

function action(mode, type, selection) {
	if (mode == 1) {
		if (selection == 197000000) {
			cm.warp(selection, "west00");
		}
		else {
			cm.warp(selection, "out00");
		}
	}
	cm.dispose();
}