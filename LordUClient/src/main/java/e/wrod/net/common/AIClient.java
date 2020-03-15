package e.wrod.net.common;

import e.wrod.net.component.JCard;
import e.wrod.net.model.User;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 机器人客户端
 */
public class AIClient {
    Logger logger = Logger.getLogger(AIClient.class);
    User user;
    int role;

    AIClient(User user, int role) {
        this.user = user;
        this.role = role;
    }

    public List<JCard> showCard() {
        Model model = Compute.getModel(user.getPlayers());
        // 待走的牌
        List<JCard> cards = new ArrayList<>();
        // 如果是主动出牌
        if (!user.isFollow()) {
            // 有单出单 (除开3带，飞机能带的单牌)
            if (model.getA1().size() > (model.getA111222().size() * 2 + model.getA3().size())) {
                logger.debug("有单牌就出单牌" + model.getA1().size());
                cards.addAll(model.getA1().get(model.getA1().size() - 1));
            }// 有对子出对子 (除开3带，飞机)
            else if (model.getA2().size() > (model.getA111222().size() * 2 + model.getA3().size())) {
                logger.debug("有对子就出对子" + model.getA2().size());
                cards.addAll(model.getA2().get(model.getA2().size() - 1));
            }// 有顺子出顺子
            else if (model.getA123().size() > 0) {
                logger.debug("有顺子就出顺子" + model.getA123().size());
                cards.addAll(model.getA123().get(model.getA123().size() - 1));
            }// 有3带就出3带，没有就出光3
            else if (model.getA3().size() > 0) {
                logger.debug("有三个就出三个" + model.getA3().size());
                // 3带单,且非关键时刻不能带王，2
                if (model.getA1().size() > 0) {
                    cards.addAll(model.getA1().get(model.getA1().size() - 1));
                }// 3带对
                else if (model.getA2().size() > 0) {
                    cards.addAll(model.getA2().get(model.getA2().size() - 1));
                }
                cards.addAll(model.getA3().get(model.getA3().size() - 1));
            }// 有双顺出双顺
            else if (model.getA112233().size() > 0) {
                logger.debug("有连队就出连队" + model.getA3().size());
                cards.addAll(model.getA112233().get(model.getA112233().size() - 1));
            }// 有飞机出飞机
            else if (model.getA111222().size() > 0) {
                logger.debug("有飞机就出飞机" + model.getA3().size());
                // 带单
                if (model.getA1().size() > 2) {
                    cards.addAll(model.getA111222().get(model.getA111222().size() - 1));
                    for (int i = 0; i < 2; i++)
                        cards.addAll(model.getA1().get(i));
                } else if (model.getA2().size() > 2)// 带双
                {
                    cards.addAll(model.getA111222().get(model.getA111222().size() - 1));
                    for (int i = 0; i < 2; i++)
                        cards.addAll(model.getA2().get(i));
                } else {
                    cards.addAll(model.getA111222().get(model.getA111222().size() - 1));
                }
                // 有炸弹出炸弹
            } else if (model.getA4().size() > 0) {
                logger.debug("有炸弹就出炸弹" + model.getA3().size());
                // 4带2,1
                int sizea1 = model.getA1().size();
                int sizea2 = model.getA2().size();
                if (sizea1 >= 2) {
                    cards.addAll(model.getA1().get(sizea1 - 1));
                    cards.addAll(model.getA1().get(sizea1 - 2));
                    cards.addAll(model.getA4().get(0));
                } else if (sizea2 >= 2) {
                    cards.addAll(model.getA2().get(sizea2 - 1));
                    cards.addAll(model.getA2().get(sizea2 - 2));
                    cards.addAll(model.getA4().get(0));
                } else {// 直接炸
                    cards.addAll(model.getA4().get(0));

                }
            }
        }// 如果是跟牌
        else {
            // TODO: 2020/3/15 判断之前用户的出牌
            List<JCard> shows = user.getShows();
            List<JCard> player = user.getPlayers();
            //获取地主
            int lordRole = user.getLordRole();
            CardType cType = Common.jugdeType(shows);
            logger.debug("判断出牌的类型为:" + cType);
            //如果是单牌
            if (cType == CardType.c1) {
                AI_1(model.a1, player, cards, lordRole, role);
            }//如果是对子
            else if (cType == CardType.c2) {
                AI_1(model.a2, player, cards, lordRole, role);
            }//3带
            else if (cType == CardType.c3) {
                AI_1(model.a3, player, cards, lordRole, role);
            }//炸弹
            else if (cType == CardType.c4) {
                AI_1(model.a4, player, cards, lordRole, role);
            }//如果是3带1
            else if (cType == CardType.c31) {
                //偏家 涉及到拆牌
                //if((role+1)%3==main.dizhuFlag)
                AI_2(model.a3, model.a1, player, cards, role);
            }//如果是3带2
            else if (cType == CardType.c32) {
                //偏家
                //if((role+1)%3==main.dizhuFlag)
                AI_2(model.a3, model.a2, player, cards, role);
            }//如果是4带11
            else if (cType == CardType.c411) {
                AI_5(model.a4, model.a1, player, cards, role);
            }
            //如果是4带22
            else if (cType == CardType.c422) {
                AI_5(model.a4, model.a2, player, cards, role);
            }
            //顺子
            else if (cType == CardType.c123) {
                AI_3(model.a123, player, cards, role);
            }
            //双顺
            else if (cType == CardType.c1122) {
                AI_3(model.a112233, player, cards, role);
            }
            //飞机带单
            else if (cType == CardType.c11122234) {
                AI_4(model.a111222, model.a1, player, cards, role);
            }
            //飞机带对
            else if (cType == CardType.c1112223344) {
                AI_4(model.a111222, model.a2, player, cards, role);
            }
            //炸弹
            if (cards.size() == 0) {
                int len4 = model.a4.size();
                if (len4 > 0)
                    cards.addAll(model.a4.get(len4 - 1));
            }
        }
        logger.debug("出牌的张数为:" + cards.size());
        return cards;
    }

    //顺子
    public void AI_3(List<List<JCard>> model, List<JCard> player, List<JCard> list, int role) {
        for (int i = 0, len = model.size(); i < len; i++) {
            List<JCard> flow = model.get(i);
            if (flow.size() == player.size() && Common.getValue(model.get(i).get(0)) > Common.getValue(player.get(0))) {
                list.addAll(model.get(i));
                return;
            }
        }
    }

    //飞机带单，双
    public void AI_4(List<List<JCard>> model1, List<List<JCard>> model2, List<JCard> player, List<JCard> list, int role) {
        //排序按重复数
        Common.order(player);
        int len1 = model1.size();
        int len2 = model2.size();

        if (len1 < 1 || len2 < 1)
            return;
        for (int i = 0; i < len1; i++) {
            List<JCard> plan = model1.get(i);
            List<JCard> twos = model2.get(0);
            if ((plan.size() / 3 <= len2) && (plan.size() * (3 + twos.size()) == player.size()) && Common.getValue(model1.get(i).get(0)) > Common.getValue(player.get(0))) {
                list.addAll(model1.get(i));
                for (int j = 1; j <= plan.size(); j++)
                    list.addAll(model2.get(len2 - j));
            }
        }
    }

    //4带1，2
    public void AI_5(List<List<JCard>> model1, List<List<JCard>> model2, List<JCard> player, List<JCard> list, int role) {
        //排序按重复数
        Common.order(player);
        int len1 = model1.size();
        int len2 = model2.size();

        if (len1 < 1 || len2 < 2)
            return;
        for (int i = 0; i < len1; i++) {
            if (Common.getValue(model1.get(i).get(0)) > Common.getValue(player.get(0))) {
                list.addAll(model1.get(i));
                for (int j = 1; j <= 2; j++)
                    list.addAll(model2.get(len2 - j));
            }
        }
    }

    //单牌，对子，3个，4个,通用
    public void AI_1(List<List<JCard>> model, List<JCard> players, List<JCard> cards, int lordRole, int role) {
        //顶家
        if ((role + 1) % 3 == lordRole) {
            for (int i = 0, len = model.size(); i < len; i++) {
                // TODO: 2020/3/15 出小的
                if (Common.getValue(model.get(i).get(0)) > Common.getValue(players.get(0))) {
                    cards.addAll(model.get(i));
                    break;
                }
            }
        } else {//偏家
            for (int len = model.size(), i = len - 1; i >= 0; i--) {
                // TODO: 2020/3/15 出小的
                if (Common.getValue(model.get(i).get(0)) > Common.getValue(players.get(0))) {
                    cards.addAll(model.get(i));
                    break;
                }
            }
        }
    }

    //3带1,2,4带1,2
    public void AI_2(List<List<JCard>> model1, List<List<JCard>> model2, List<JCard> player, List<JCard> list, int role) {
        //model1是主牌,model2是带牌,player是玩家出的牌,list是准备回的牌
        //排序按重复数
        Common.order(player);
        int len1 = model1.size();
        int len2 = model2.size();
        //如果有王直接炸了
        if (len1 > 0 && model1.get(0).size() < 10) {
            list.addAll(model1.get(0));
            System.out.println("王炸");
            return;
        }
        if (len1 < 1 || len2 < 1)
            return;
        for (int len = len1, i = len - 1; i >= 0; i--) {
            if (Common.getValue(model1.get(i).get(0)) > Common.getValue(player.get(0))) {
                list.addAll(model1.get(i));
                break;
            }
        }
        list.addAll(model2.get(len2 - 1));
        if (list.size() < 2)
            list.clear();
    }
}
