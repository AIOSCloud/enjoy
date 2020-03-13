package e.wrod.net.common;

import e.wrod.net.component.JCard;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Model {
    //一组牌
    int value; //权值
    int num;// 手数 (几次能够走完，没有挡的情况下)
    List<List<JCard>> a1 = new ArrayList<List<JCard>>(); //单张
    List<List<JCard>> a2 = new ArrayList<List<JCard>>(); //对子
    List<List<JCard>> a3 = new ArrayList<List<JCard>>(); //3带
    List<List<JCard>> a123 = new ArrayList<List<JCard>>(); //连子
    List<List<JCard>> a112233 = new ArrayList<List<JCard>>(); //连牌
    List<List<JCard>> a111222 = new ArrayList<List<JCard>>(); //飞机
    List<List<JCard>> a4 = new ArrayList<List<JCard>>(); //炸弹
}