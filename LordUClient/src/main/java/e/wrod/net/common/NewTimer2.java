package e.wrod.net.common;

import e.wrod.net.view.AIPage;

public class NewTimer2 implements Runnable {
    AIPage page;
    int position;

    public NewTimer2(AIPage page, int position) {
        this.page = page;
        this.position = position;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        page.setTimer(new UTimer(page, position));
        page.getTimer().start();
    }
}
