package e.wrod.net.common;

import e.wrod.net.view.OffLinePage;

import javax.swing.*;

public class NewTimer implements Runnable {
    OffLinePage main;
    int i;

    public NewTimer(OffLinePage m, int i) {
        this.main = m;
        this.i = i;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        main.setT(new Time(main, 10));
        main.getT().start();
    }
}
