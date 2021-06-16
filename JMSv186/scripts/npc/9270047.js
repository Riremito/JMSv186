/* 
 * NPC   : Aldol
 * Map   : Malaysia - Spooky world
 */

function start() {
    cm.sendYesNo("Do you want to get out now?");
}

function action(mode, type, selection) {
    if (mode == 1) {
	cm.warp(551030100, 0);
    }
    cm.dispose();
}