package e.wrod.net.component;

import e.wrod.net.model.Card;
import e.wrod.net.view.RoomPage;
import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@Data
public class JCard extends JLabel implements MouseListener {
    RoomPage room;
    Card card;
    boolean up; //是否为正反面
    boolean canClick;//是否可被点击
    boolean clicked;//是否已经被点击

    public JCard(Card card, boolean up) {
        this.card = card;
        this.up = up;
        if (this.up) {
            this.turnFront();
        } else {
            this.turnRear();
        }
        this.setSize(71, 96);
        this.setVisible(true);
        this.addMouseListener(this);
    }

    //正面
    public void turnFront() {
        this.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/card/" + card.getColor() + "-" + card.getNumber() + ".gif")));
        this.up = true;
    }

    //反面
    public void turnRear() {
        this.setIcon(new ImageIcon(ClassLoader.getSystemResource("images/card/rear.gif")));
        this.up = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (canClick) {
            Point from = this.getLocation();
            int step;//移动距离
            if (clicked) {
                step = -20;
            } else {
                step = 20;
            }
            clicked = !clicked;//反向
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
