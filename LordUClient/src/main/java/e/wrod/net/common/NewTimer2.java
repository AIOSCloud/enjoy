package e.wrod.net.common;

import e.wrod.net.view.AIPage;

public class NewTimer2 implements Runnable {
    AIPage page;
    int mine = 0;
    int befor;
    int next;

    public NewTimer2(AIPage page, int mine, int befor, int next) {
        this.page = page;
        this.mine = mine;
        this.befor = befor;
        this.next = next;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        page.setTimer(new UTimer(page, mine, befor, next));
        page.getTimer().start();
    }
}
