// 婦人警官ポリン
// 前提クエスト 実力の証明
// アイテム 資格証明書

var status = -1;
function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		status--;
	}

	var mapid = cm.getMapId();

	switch (status) {
		case 0:
			{
				// 前提クエストアイテムのチェック処理が必要
				cm.sendNext("こんにちは。私はポリンと申します。この中は誰もが入れるわけではありませんが、あたなは秘密任務の遂行中のようですね。中に送らせますので#b警察官ミャオ#kに会ってみてください。");
				return;
			}
		case 1:
			{
				// ブラックシープンの領域, 黒羊毛出ない
				cm.warp(701010321, 0);
				break;
			}
		default:
			break;
	}

	cm.dispose();
}