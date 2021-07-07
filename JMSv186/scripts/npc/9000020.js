// スピネル
// https://www.nicovideo.jp/watch/sm12852798

var status = -1;
function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	switch (status) {
		case 0:
			{
				var text = "#b国内のキノコ神社を含め、中国の上海、台湾の西門町#kに続き、#bタイランドの水上市場#kへのコースが用意出来ています。各旅行地でも私が皆様の楽しい旅行のために頑張ります。では、どこから行ってみたいですか？\r\n";
				text += "#b#L" + 1 + "##b" + "西門街(台湾)" + "#l#k\r\n";
				text += "#b#L" + 2 + "##b" + "上海(中国)" + "#l#k\r\n";
				text += "#b#L" + 3 + "##b" + "キノコ神社(日本)" + "#l#k\r\n";
				text += "#b#L" + 4 + "##b" + "水上市場(タイランド)" + "#l#k\r\n";
				text += "#b#L" + 5 + "##b" + "宋山里(中国)" + "#l#k\r\n";
				cm.sendSimple(text);
				return;
			}
		case 1:
			{
				switch (selection) {
					case 1:
						{
							cm.warp(740000000, 0);
							break;
						}
					case 2:
						{
							cm.warp(701000000, 0);
							break;
						}
					case 3:
						{
							cm.warp(800000000, 0);
							break;
						}
					case 4:
						{
							cm.warp(500000000, 0);
							break;
						}
					case 5:
						{
							cm.warp(702000000, 0);
							break;
						}
					default:
						break;
				}
				break;
			}
		default:
			break;
	}

	cm.dispose();
}