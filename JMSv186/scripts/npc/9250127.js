// OS4ƒVƒƒƒgƒ‹ @502010000
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
				// VisitorleaveDirectionMode?
				var text = "‚Ç‚±‚Öo”­‚µ‚Ü‚·‚©H\r\n";
				// ‘Â—‚µ‚½‰F’ˆ‘D‚Ì[ŠC
				text += "#L" + 502010200 + "##b[ŠC‚ÉˆÚ“®‚·‚é#k#l\r\n";
				// ‰»Îz•¨‚ÌzR
				text += "#L" + 502010300 + "##bzR‚ÉˆÚ“®‚·‚é#k#l\r\n";
				// ‰ÁHHê—A‘—˜H
				text += "#L" + 502010400 + "##b‰ÁHHê‚É—A‘—‚·‚é#k#l\r\n";
				// ‰ÁHHê–hŒä‘D
				text += "#L" + 502010500 + "##b‰ÁHHê‚Ì–hŒä‘D‚ÉˆÚ“®‚·‚é#k#l\r\n";
				cm.sendSimple(text);
				return;
			}
		case 1:
			{
				if (selection == 502010500) {
					cm.sendOk("“üê‹Ö~");
					break;
				}
				cm.warp(selection, "sp");
				break;
			}
		default:
			break;
	}

	cm.dispose();
}