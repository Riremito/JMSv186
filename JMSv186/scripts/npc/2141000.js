// ピンクビーン召喚
// キルストン

function start() {
	var text = "女神の鏡さえあれば…もう一度暗黒の魔法使いを呼び出すことができる！　…\r\n"
	text += "お、おかしい…どうして暗黒の魔法使いを呼び出さないんだ？　この気はなんだ？　暗黒の魔法使いとは全く違う…ウワアアアッ！\r\n";
	text += "#b(キルストンの肩に手をかける)\r\n";
	cm.askAcceptDecline(text);
}

function action(mode, type, selection) {
	if (mode == 1) {
		cm.removeNpc(270050100, 2141000);
		cm.forceStartReactor(270050100, 2709000);
	}

	cm.dispose();
}