package e.word.net.common;

import e.word.net.model.Card;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Model {
    //一组牌
    int value; //权值
    int num;// 手数 (几次能够走完，没有挡的情况下)
    List<List<Card>> a1 = new ArrayList<List<Card>>(); //单张
    List<List<Card>> a2 = new ArrayList<List<Card>>(); //对子
    List<List<Card>> a3 = new ArrayList<List<Card>>(); //3带
    List<List<Card>> a123 = new ArrayList<List<Card>>(); //连子
    List<List<Card>> a112233 = new ArrayList<List<Card>>(); //连牌
    List<List<Card>> a111222 = new ArrayList<List<Card>>(); //飞机
    List<List<Card>> a4 = new ArrayList<List<Card>>(); //炸弹
}