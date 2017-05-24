package bishe;

import ipt.sortMethod.BubbleSort;

import javax.print.DocFlavor;
import java.util.*;

/**
 * Created by fenglin on 2017/3/15.
 */
public class anotherCase {


    public static ArrayList<ArrayList<String>> selectRuleUsingMyMethod(
            int[] rule_weight, int[] rule_cost, int switch_volume) {
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<Integer> sort_rule = new ArrayList<Integer>();
        HashMap<Integer, ArrayList<Integer>> dependency =initParameter.initDependency(rule_weight.length);// initDependency();
//        System.out.println(dependency);
        int cover_cost = 2;
        int method_number = 2;
        int rule_total_number = rule_weight.length;
        int[] rule_dependent_ratio = new int[rule_total_number];
        int[] rule_new_cost = new int[rule_total_number];
        int[] rule_cover_ratio = new int[rule_total_number];
        int[][] metric = new int[rule_total_number][method_number];
        boolean[] rule_deal = new boolean[rule_total_number];

        for (int i = 0; i < rule_total_number; i++) {
            rule_new_cost[i] = cover_cost;
            rule_dependent_ratio[i] = rule_weight[i] / rule_cost[i];
            rule_cover_ratio[i] = rule_weight[i] / rule_new_cost[i];
            metric[i][0] = rule_dependent_ratio[i];
            metric[i][1] = rule_cover_ratio[i];
            rule_deal[i] = false;
        }

        HashMap<Integer, Integer> rule = new HashMap<Integer, Integer>();
        for (int i = 0; i < rule_weight.length; i++) {
            rule.put(i, rule_weight[i]);
        }

        //按照权重规则排序，降序
        List<Map.Entry<Integer, Integer>> list =
                new ArrayList<Map.Entry<Integer, Integer>>(rule.entrySet());


        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1,
                               Map.Entry<Integer, Integer> o2) {
                return (o2.getValue() - o1.getValue());
            }
        });
//        System.out.println(list);

        //从排序的规则中选取topN条规则
        ArrayList<Integer> topN = new ArrayList<Integer>();
        for (int i = 0; i < switch_volume; i++) {
            topN.add(list.get(i).getKey());
        }

//        System.out.println("topN: "+topN);
        //topN规则编号按逆序排列，用于分组
        int[] sort_topN = new int[switch_volume];
        int j = 0;
        for (int i : topN) {
            sort_topN[j] = i;
            j++;
        }
//        System.out.println(topN);
        sort_topN = BubbleSort(sort_topN);
//        for (int i = 0; i < sort_topN.length; i++) {
//            System.out.print(sort_topN[i] + " ");
//        }
//        System.out.println();

        //分组
        HashMap<Integer, Boolean> isDeal = new HashMap<Integer, Boolean>();
        HashMap<Integer, ArrayList<Integer>> rule_relation = new HashMap<Integer, ArrayList<Integer>>();

        for (int i : topN) {
            isDeal.put(i, false);
        }


        for (int i = 0; i < sort_topN.length; i++) {

            if (!isDeal.get(sort_topN[i])) {
                isDeal.put(sort_topN[i], true);

                ArrayList<Integer> rule_relation_list = new ArrayList<Integer>();

                for (int k = i + 1; k < sort_topN.length; k++) {
                    if (!isDeal.get(sort_topN[k]) && dependency.get(sort_topN[i]).contains(sort_topN[k])) {
                        rule_relation_list.add(sort_topN[k]);
                        isDeal.put(sort_topN[k], true);
                    }

                }
                rule_relation.put(sort_topN[i], rule_relation_list);
            }
        }

//        System.out.println("rule_relation :" +rule_relation);

        ArrayList<ArrayList<String>> candidate_list = new ArrayList<ArrayList<String>>();

        //容量为1，直接从叶节点中选一个最大的

        ArrayList<String> tplist = new ArrayList<String>();
        if (switch_volume == 1) {
            for (int i : dependency.keySet()) {
                if (dependency.get(i).size() == 0)
                    tplist.add(i + "");
            }

            int max = -1;
            int ree = -1;

            for (String i : tplist) {
                int n = Integer.parseInt(i);
                if (rule_weight[n] > max) {
                    max = rule_weight[n];
                    ree = n;
                }
            }

            ArrayList<String> tpresult = new ArrayList<String>();
            tpresult.add(ree + "");
//            System.out.println(tplist+" "+tplist.size()+" "+ree);
            candidate_list.add(tpresult);
            return candidate_list;


        }


        for (Integer ru : rule_relation.keySet()) {
            ArrayList<String> tmp_list = new ArrayList<String>();
            tmp_list.add(ru + "");
//            System.out.println("rule: "+ru);
            if (rule_relation.get(ru).size() == 0) {

                if (dependency.get(ru).size() == 0) {

                    rule_deal = new boolean[rule_total_number];
                    for (int i = 0; i < rule_total_number; i++)
                        rule_deal[i] = false;

                    rule_deal[ru] = true;
                    ArrayList<String> re
                            = selectRuleUsingMixedSetMyMethodProvedVersion(rule_weight, rule_cost, switch_volume - 1, rule_deal);
                    for (String s : re) {
                        tmp_list.add(s);
                    }
                    candidate_list.add(tmp_list);

                } else {
                    if (metric[ru][0] >= metric[ru][1]) {

                        rule_deal = new boolean[rule_total_number];
                        for (int i = 0; i < rule_total_number; i++)
                            rule_deal[i] = false;
                        rule_deal[ru] = true;
                        for (int in : dependency.get(ru)) {
                            tmp_list.add(in + "");
                            rule_deal[in] = true;

                        }
                        ArrayList<String> re
                                = selectRuleUsingMixedSetMyMethodProvedVersion(rule_weight, rule_cost, switch_volume - 1 - dependency.get(ru).size(), rule_deal);
                        for (String s : re) {
                            tmp_list.add(s);
                        }
                        candidate_list.add(tmp_list);

                    } else {
                        tmp_list.add(ru + "*");
                        rule_deal = new boolean[rule_total_number];
                        for (int i = 0; i < rule_total_number; i++)
                            rule_deal[i] = false;
                        rule_deal[ru] = true;
                        //
                        int[] cost=rule_cost.clone();
                        for(int i:dependency.get(ru)){
                            cost[i]-=1;
                        }
                        //
                        ArrayList<String> re
                                = selectRuleUsingMixedSetMyMethodProvedVersion(rule_weight, rule_cost, switch_volume - 2, rule_deal);
                        for (String s : re) {
                            tmp_list.add(s);
                        }
                        candidate_list.add(tmp_list);
                    }
                }

            } else {
                if (rule_relation.get(ru).size() + 1 > switch_volume) {
                    for (int i = 0; i < switch_volume - 2; i++) {
                        tmp_list.add(rule_relation.get(ru).get(i) + "");
                    }
                    tmp_list.add(rule_relation.get(ru).get(switch_volume - 3) + "*");
                    candidate_list.add(tmp_list);
                } else {

                    int last_index = rule_relation.get(ru).size() - 1;
                    int last_element = rule_relation.get(ru).get(last_index);
                    int position = -1;
                    for (int i = 0; i < dependency.get(ru).size(); i++) {
                        if (dependency.get(ru).get(i) == last_element) {
                            position = i;
                            break;
                        }
                    }

                    if (rule_relation.get(ru).size() + 1 == switch_volume) {

                        if (position == dependency.get(ru).size() - 1) {
                            for (int i = 0; i < rule_relation.size(); i++) {
                                tmp_list.add(rule_relation.get(ru).get(i) + "");
                            }
                            candidate_list.add(tmp_list);
                        } else {
                            for (int i = 0; i < rule_relation.get(ru).size() - 2; i++) {
                                tmp_list.add(rule_relation.get(ru).get(i) + "");
                            }
                            tmp_list.add(rule_relation.get(ru).get(rule_relation.size() - 2) + "*");
                            candidate_list.add(tmp_list);

                        }
                    } else {
                        System.out.println("positon : "+position);
                        if (position == dependency.get(ru).size() - 1) {
                            rule_deal = new boolean[rule_total_number];
                            for (int i = 0; i < rule_total_number; i++)
                                rule_deal[i] = false;
                            rule_deal[ru] = true;
                            for (int i = 0; i < rule_relation.get(ru).size(); i++) {
                                tmp_list.add(rule_relation.get(ru).get(i) + "");
                                rule_deal[rule_relation.get(ru).get(i)] = true;
                            }
                            ArrayList<String> re
                                    = selectRuleUsingMixedSetMyMethodProvedVersion(rule_weight, rule_cost, switch_volume - rule_relation.get(ru).size()-1, rule_deal);
                            for (String s : re) {
                                tmp_list.add(s);
                            }
                            candidate_list.add(tmp_list);
                        } else {
                            rule_deal = new boolean[rule_total_number];
                            for (int i = 0; i < rule_total_number; i++)
                                rule_deal[i] = false;
                            rule_deal[ru] = true;
                            for (int i = 0; i < rule_relation.get(ru).size() - 2; i++) {
                                tmp_list.add(rule_relation.get(ru).get(i) + "");
                                rule_deal[rule_relation.get(ru).get(i)] = true;
                            }
                            int extra = rule_relation.get(ru).get(rule_relation.get(ru).size() - 1);


                            if (metric[extra][0] >= metric[extra][1]) {


                                rule_deal[extra] = true;
                                for (int in : dependency.get(extra)) {
                                    tmp_list.add(in + "");
                                    rule_deal[in] = true;

                                }
                                //待检查
                                ArrayList<String> re
                                        = selectRuleUsingMixedSetMyMethodProvedVersion(rule_weight, rule_cost, switch_volume - 1 - rule_relation.get(ru).size() - dependency.get(extra).size(), rule_deal);
                                for (String s : re) {
                                    tmp_list.add(s);
                                }
                                candidate_list.add(tmp_list);

                            } else {
                                tmp_list.add(extra + "");
                                tmp_list.add(extra + "*");

                                rule_deal[extra] = true;

                                ArrayList<String> re
                                        = selectRuleUsingMixedSetMyMethodProvedVersion(rule_weight, rule_cost, switch_volume - 1 - rule_relation.get(ru).size() - 1, rule_deal);
                                for (String s : re) {
                                    tmp_list.add(s);
                                }
                                candidate_list.add(tmp_list);
                            }


                        }


                    }
                }

            }

        }


//        System.out.println(dependency);
//        System.out.println(rule_relation);


        return candidate_list;
    }


    public static ArrayList<String> selectRuleUsingMixedSetMyMethodProvedVersion(
            int[] rule_weight, int[] rule_cost, int switch_volume, boolean[] rule_selected) {

        ArrayList<String> result = new ArrayList<String>();

        int rule_total_number = rule_weight.length;
        int method_number = 2;
        int cover_cost = 2;
        int[] rule_dependent_ratio = new int[rule_total_number];
        boolean[] rule_deal = new boolean[rule_total_number];
        int[] rule_new_cost = new int[rule_total_number];
        int[] rule_cover_ratio = new int[rule_total_number];
        int[][] metric = new int[rule_total_number][method_number];
        HashMap<Integer, ArrayList<Integer>> dependency = initParameter.initDependency(rule_weight.length);// initDependency();

        for (int i = 0; i < rule_total_number; i++) {
            rule_new_cost[i] = cover_cost;
            rule_dependent_ratio[i] = rule_weight[i] / rule_cost[i];
            rule_cover_ratio[i] = rule_weight[i] / rule_new_cost[i];
            metric[i][0] = rule_dependent_ratio[i];
            metric[i][1] = rule_cover_ratio[i];

        }
        rule_deal = rule_selected;

//        for(int i=0;i<rule_total_number;i++)
//            System.out.println(metric[i][0]+"  "+metric[i][1]);


        int max = -1;
        int pos_x = -1;
        int pos_y = -1;
        int volume = switch_volume;

        while (result.size() < switch_volume) {
            max = -1;
            for (int i = 0; i < rule_total_number; i++) {
                if (rule_deal[i] == false) {
                    if (metric[i][0] >= metric[i][1]) {
                        if (metric[i][0] > max) {
                            pos_x = i;
                            pos_y = 0;
                            max = metric[i][0];
                        }
                    } else {
                        if (metric[i][1] > max) {
                            pos_x = i;
                            pos_y = 1;
                            max = metric[i][1];
                        }
                    }
                }

            }

//            System.out.println("this turn "+pos_x+" "+pos_y);

            if (pos_y == 0) {

                if (rule_cost[pos_x] <= volume) {
                    result.add(pos_x + "");
                    rule_deal[pos_x] = true;
                    int nu = 0;
                    for (Integer i : dependency.get(pos_x)) {
                        if (!result.contains(i)) {
                            result.add(i + "");
                            rule_deal[i] = true;
                            nu++;
                        }

                    }
                    volume = volume - nu - 1;
                } else
                    rule_deal[pos_x] = true;

            } else if (pos_y == 1) {
                if (rule_new_cost[pos_x] <= volume) {


                    boolean flag = false;
                    int distance = 0;
                    int nn = 0;
                    for (String str : result) {
                        if (!str.contains("*")) {
                            if (dependency.get(Integer.parseInt(str)).contains(pos_x)) {
                                distance = dependency.get(Integer.parseInt(str)).indexOf(pos_x);
                                if (distance == 1) {
                                    result.add(pos_x + "");
                                    result.add(dependency.get(Integer.parseInt(str)).get(0) + "");
                                    rule_deal[pos_x] = true;
                                    rule_deal[dependency.get(Integer.parseInt(str)).get(0)] = true;
                                    if (dependency.get(Integer.parseInt(str)).size() == 2) {
                                        result.remove(Integer.parseInt(str) + "*");
                                        volume++;
                                    }
                                    flag = true;
                                    break;
                                }
                            }
                        }

                    }

                    for (String str : result) {
                        if (!str.contains("*")) {
                            if (dependency.get(pos_x).contains(Integer.parseInt(str))) {
                                distance = dependency.get(pos_x).indexOf(Integer.parseInt(str));
                                if (distance == 1) {
                                    result.add(pos_x + "");
                                    result.add(dependency.get(pos_x).get(0) + "");
                                    rule_deal[pos_x] = true;
                                    rule_deal[dependency.get(pos_x).get(0)] = true;
                                    flag = true;
                                    break;
                                }
                            }
                        }

                    }


                    if (!flag) {
                        result.add(pos_x + "");
                        result.add(pos_x + "*");
                        rule_deal[pos_x] = true;

                    }

                    volume = volume - rule_new_cost[pos_x];

                } else
                    rule_deal[pos_x] = true;

            }

        }

        return result;
    }


    public static ArrayList<String> selectRuleUsingMixedSetMethod(
            int[] rule_weight, int[] rule_cost, int switch_volume) {

        ArrayList<String> result = new ArrayList<String>();

        int rule_total_number = rule_weight.length;
        int method_number = 2;
        int cover_cost = 2;
        int[] rule_dependent_ratio = new int[rule_total_number];
        boolean[] rule_deal = new boolean[rule_total_number];
        int[] rule_new_cost = new int[rule_total_number];
        int[] rule_cover_ratio = new int[rule_total_number];
        int[][] metric = new int[rule_total_number][method_number];
        HashMap<Integer, ArrayList<Integer>> dependency = initParameter.initDependency(rule_weight.length);// initDependency();

        for (int i = 0; i < rule_total_number; i++) {
            rule_new_cost[i] = cover_cost;
            rule_dependent_ratio[i] = rule_weight[i] / rule_cost[i];
            rule_cover_ratio[i] = rule_weight[i] / rule_new_cost[i];
            metric[i][0] = rule_dependent_ratio[i];
            metric[i][1] = rule_cover_ratio[i];
            rule_deal[i] = false;
        }

//        for(int i=0;i<rule_total_number;i++)
//            System.out.println(metric[i][0]+"  "+metric[i][1]);


        int max = -1;
        int pos_x = -1;
        int pos_y = -1;
        int volume = switch_volume;

        while (result.size() < switch_volume) {
            max = -1;
            for (int i = 0; i < rule_total_number; i++) {
                if (rule_deal[i] == false) {
                    if (metric[i][0] >= metric[i][1]) {
                        if (metric[i][0] > max) {
                            pos_x = i;
                            pos_y = 0;
                            max = metric[i][0];
                        }
                    } else {
                        if (metric[i][1] > max) {
                            pos_x = i;
                            pos_y = 1;
                            max = metric[i][1];
                        }
                    }
                }

            }

//            System.out.println("this turn "+pos_x+" "+pos_y);

            if (pos_y == 0) {

                if (rule_cost[pos_x] <= volume) {
                    result.add(pos_x + "");
                    rule_deal[pos_x] = true;
                    int nu = 0;
                    for (Integer i : dependency.get(pos_x)) {
                        if (!result.contains(i)) {
                            result.add(i + "");
                            rule_deal[i] = true;
                            nu++;
                        }

                    }
                    volume = volume - nu - 1;
                } else
                    rule_deal[pos_x] = true;

            } else if (pos_y == 1) {
                if (rule_new_cost[pos_x] <= volume) {


                    result.add(pos_x + "");
                    result.add(pos_x + "*");
                    rule_deal[pos_x] = true;


                    volume = volume - rule_new_cost[pos_x];

                } else
                    rule_deal[pos_x] = true;

            }

        }

        return result;
    }


    public static ArrayList<String> selectRulesUsingCoverSetMethod(
            int[] rule_weight, int[] rule_cost, int switch_volume) {
        ArrayList<String> result = new ArrayList<String>();
        if (switch_volume <= 1) return result;

        int rule_total_number = rule_cost.length;
        int[] rule_new_cost = new int[rule_total_number];
        int[] rule_ratio = new int[rule_total_number];
        boolean[] rule_deal = new boolean[rule_total_number];

        HashMap<Integer, ArrayList<Integer>> dependency = initParameter.initDependency(rule_weight.length);// initDependency();

        for (int i = 0; i < rule_total_number; i++) {
            rule_new_cost[i] = 2;
            rule_ratio[i] = rule_weight[i] / rule_new_cost[i];
            rule_deal[i] = false;
        }

//        for (int i = 0; i < rule_total_number; i++) {
//            System.out.println(i + " " + rule_ratio[i]);
//        }


        int volume = switch_volume;
        int max = rule_ratio[0];
        int pos = -1;
        while (result.size() < switch_volume) {
            max = -1;
            for (int i = 0; i < rule_total_number; i++) {
                if (rule_deal[i] == false && rule_ratio[i] > max) {
                    max = rule_ratio[i];
                    pos = i;
                }
            }
//            System.out.println("this turn choose: "+pos+" "+volume);
            if (rule_new_cost[pos] <= volume) {

                result.add(pos + "");
                result.add(pos + "*");
                rule_deal[pos] = true;


                volume = volume - rule_new_cost[pos];
                if (volume == 1) break;
//                volume+=nn;
//                System.out.println(volume+ " ----");


            } else {
                rule_deal[pos] = true;
            }

//            for(String s:result){
//                System.out.print(s + " ");
//            }
//            System.out.println();

        }


        return result;

    }


    public static ArrayList<String> selectRuleUsingMixedSetMethodProvedVersion(
            int[] rule_weight, int[] rule_cost, int switch_volume) {

        ArrayList<String> result = new ArrayList<String>();

        int rule_total_number = rule_weight.length;
        int method_number = 2;
        int cover_cost = 2;
        int[] rule_dependent_ratio = new int[rule_total_number];
        boolean[] rule_deal = new boolean[rule_total_number];
        int[] rule_new_cost = new int[rule_total_number];
        int[] rule_cover_ratio = new int[rule_total_number];
        int[][] metric = new int[rule_total_number][method_number];
        HashMap<Integer, ArrayList<Integer>> dependency =initParameter.initDependency(rule_weight.length);// initDependency();

        for (int i = 0; i < rule_total_number; i++) {
            rule_new_cost[i] = cover_cost;
            rule_dependent_ratio[i] = rule_weight[i] / rule_cost[i];
            rule_cover_ratio[i] = rule_weight[i] / rule_new_cost[i];
            metric[i][0] = rule_dependent_ratio[i];
            metric[i][1] = rule_cover_ratio[i];
            rule_deal[i] = false;
        }

//        for(int i=0;i<rule_total_number;i++)
//            System.out.println(metric[i][0]+"  "+metric[i][1]);


        int max = -1;
        int pos_x = -1;
        int pos_y = -1;
        int volume = switch_volume;

        while (result.size() < switch_volume) {
            max = -1;
            for (int i = 0; i < rule_total_number; i++) {
                if (rule_deal[i] == false) {
                    if (metric[i][0] >= metric[i][1]) {
                        if (metric[i][0] > max) {
                            pos_x = i;
                            pos_y = 0;
                            max = metric[i][0];
                        }
                    } else {
                        if (metric[i][1] > max) {
                            pos_x = i;
                            pos_y = 1;
                            max = metric[i][1];
                        }
                    }
                }

            }

//            System.out.println("this turn "+pos_x+" "+pos_y);

            if (pos_y == 0) {

                if (rule_cost[pos_x] <= volume) {
                    result.add(pos_x + "");
                    rule_deal[pos_x] = true;
                    int nu = 0;
                    for (Integer i : dependency.get(pos_x)) {
                        if (!result.contains(i)) {
                            result.add(i + "");
                            rule_deal[i] = true;
                            nu++;
                        }

                    }
                    volume = volume - nu - 1;
                } else
                    rule_deal[pos_x] = true;

            } else if (pos_y == 1) {
                if (rule_new_cost[pos_x] <= volume) {


                    boolean flag = false;
                    int distance = 0;
                    int nn = 0;
                    for (String str : result) {
                        if (!str.contains("*")) {
                            if (dependency.get(Integer.parseInt(str)).contains(pos_x)) {
                                distance = dependency.get(Integer.parseInt(str)).indexOf(pos_x);
                                if (distance == 1) {
                                    result.add(pos_x + "");
                                    result.add(dependency.get(Integer.parseInt(str)).get(0) + "");
                                    rule_deal[pos_x] = true;
                                    rule_deal[dependency.get(Integer.parseInt(str)).get(0)] = true;
                                    if (dependency.get(Integer.parseInt(str)).size() == 2) {
                                        result.remove(Integer.parseInt(str) + "*");
                                        volume++;
                                    }
                                    flag = true;
                                    break;
                                }
                            }
                        }

                    }

                    for (String str : result) {
                        if (!str.contains("*")) {
                            if (dependency.get(pos_x).contains(Integer.parseInt(str))) {
                                distance = dependency.get(pos_x).indexOf(Integer.parseInt(str));
                                if (distance == 1) {
                                    result.add(pos_x + "");
                                    result.add(dependency.get(pos_x).get(0) + "");
                                    rule_deal[pos_x] = true;
                                    rule_deal[dependency.get(pos_x).get(0)] = true;
                                    flag = true;
                                    break;
                                }
                            }
                        }

                    }


                    if (!flag) {
                        result.add(pos_x + "");
                        result.add(pos_x + "*");
                        rule_deal[pos_x] = true;

                    }

                    volume = volume - rule_new_cost[pos_x];

                } else
                    rule_deal[pos_x] = true;

            }

        }

        return result;
    }


    public static ArrayList<String> selectRulesUsingCoverSetMethodProvedVersion(
            int[] rule_weight, int[] rule_cost, int switch_volume) {
        ArrayList<String> result = new ArrayList<String>();

        int rule_total_number = rule_cost.length;
        int[] rule_new_cost = new int[rule_total_number];
        int[] rule_ratio = new int[rule_total_number];
        boolean[] rule_deal = new boolean[rule_total_number];

        HashMap<Integer, ArrayList<Integer>> dependency = initParameter.initDependency(rule_weight.length);// initDependency();

        for (int i = 0; i < rule_total_number; i++) {
            rule_new_cost[i] = 2;
            rule_ratio[i] = rule_weight[i] / rule_new_cost[i];
            rule_deal[i] = false;
        }

//        for (int i = 0; i < rule_total_number; i++) {
//            System.out.println(i + " " + rule_ratio[i]);
//        }


        int volume = switch_volume;
        int max = rule_ratio[0];
        int pos = -1;
        while (result.size() < switch_volume) {
            max = -1;
            for (int i = 0; i < rule_total_number; i++) {
                if (rule_deal[i] == false && rule_ratio[i] > max) {
                    max = rule_ratio[i];
                    pos = i;
                }
            }
//            System.out.println("this turn choose: "+pos+" "+volume);
            if (rule_new_cost[pos] <= volume) {
//                System.out.println("///  "+pos);

                boolean flag = false;
                int distance = 0;
                int nn = 0;
                for (String str : result) {
                    if (!str.contains("*")) {
                        if (dependency.get(Integer.parseInt(str)).contains(pos)) {
                            distance = dependency.get(Integer.parseInt(str)).indexOf(pos);
                            if (distance == 1) {
                                result.add(pos + "");
                                result.add(dependency.get(Integer.parseInt(str)).get(0) + "");
                                rule_deal[pos] = true;
                                rule_deal[dependency.get(Integer.parseInt(str)).get(0)] = true;
                                if (dependency.get(Integer.parseInt(str)).size() == 2) {
                                    result.remove(Integer.parseInt(str) + "*");
                                    volume++;
                                }
                                flag = true;
                                break;
                            }
                        }
                    }

                }

                for (String str : result) {
                    if (!str.contains("*")) {
                        if (dependency.get(pos).contains(Integer.parseInt(str))) {
                            distance = dependency.get(pos).indexOf(Integer.parseInt(str));
                            if (distance == 1) {
                                result.add(pos + "");
                                result.add(dependency.get(pos).get(0) + "");
                                rule_deal[pos] = true;
                                rule_deal[dependency.get(pos).get(0)] = true;
//                                if(dependency.get(Integer.parseInt(str)).size()==2){
//                                    result.remove(Integer.parseInt(str)+"*");
//                                    volume++;
//                                }
                                flag = true;
                                break;
                            }
                        }
                    }

                }


                if (!flag) {
                    result.add(pos + "");
                    result.add(pos + "*");
                    rule_deal[pos] = true;

                }

                volume = volume - rule_new_cost[pos];
                if (volume == 1) break;
//                volume+=nn;
//                System.out.println(volume+ " ----");


            } else {
                rule_deal[pos] = true;
            }

//            for(String s:result){
//                System.out.print(s + " ");
//            }
//            System.out.println();

        }


        return result;

    }


    public static ArrayList<Integer> selectRulesUsingDependentSetMethod(
            int[] rule_weight, int[] rule_cost, int switch_volume) {

        ArrayList<Integer> result = new ArrayList<Integer>();

        int rule_total_number = rule_weight.length;
        int[] rule_ratio = new int[rule_total_number];
        boolean[] rule_deal = new boolean[rule_total_number];
        HashMap<Integer, ArrayList<Integer>> dependency = initParameter.initDependency(rule_weight.length);// initDependency();


        for (int i = 0; i < rule_total_number; i++) {
            rule_ratio[i] = rule_weight[i] / rule_cost[i];
            rule_deal[i] = false;
        }

//        for(int i=0;i<rule_total_number;i++){
//            System.out.println(i+" "+rule_ratio[i]);
//        }

        int volume = switch_volume;
        int max = rule_ratio[0];
        int pos = -1;
        while (result.size() < switch_volume) {
            System.out.println(result + " " + volume + " " + (result.size() < volume));
            max = -1;
            for (int i = 0; i < rule_total_number; i++) {
                if (rule_deal[i] == false && rule_ratio[i] > max) {
                    max = rule_ratio[i];
                    pos = i;
                }
            }

            if (dependency.get(pos).size() + 1 <= volume) {
                result.add(pos);
                rule_deal[pos] = true;
                int nu = 0;
                for (Integer i : dependency.get(pos)) {
                    boolean flag = result.contains(i);
                    if (!result.contains(i)) {
                        result.add(i);
                        rule_deal[i] = true;
                        nu++;
                    }

                }

                volume = volume - nu - 1;


            } else {
                rule_deal[pos] = true;
            }

            boolean still_have_element=false;
            for(int i=0;i<rule_deal.length;i++){
                if(rule_deal[i]==true) {
                    still_have_element=true;
                    break;
                }
            }
            if(!still_have_element) return result;
        }

        return result;
    }


    public static int[] BubbleSort(int[] rule) {

        int temp = 0;
        int[] rule_weight = rule;
        for (int i = 0; i < rule_weight.length - 1; i++) {
            for (int j = 0; j < rule_weight.length - i - 1; j++) {

                if (rule_weight[j] < rule_weight[j + 1]) {
                    temp = rule_weight[j];
                    rule_weight[j] = rule_weight[j + 1];
                    rule_weight[j + 1] = temp;
                }

            }
        }
        return rule_weight;
    }

    public static HashMap<Integer, ArrayList<Integer>> initDependency() {


        HashMap<Integer, ArrayList<Integer>> dependency = new HashMap<Integer, ArrayList<Integer>>();

        ArrayList<Integer> arr0 = new ArrayList<Integer>();
        dependency.put(0, arr0);

        ArrayList<Integer> arr1 = new ArrayList<Integer>();
        arr1.add(0);
        dependency.put(1, arr1);

        ArrayList<Integer> arr2 = new ArrayList<Integer>();
        arr2.add(1);
        arr2.add(0);
        dependency.put(2, arr2);

        ArrayList<Integer> arr3 = new ArrayList<Integer>();
        arr3.add(1);
        arr3.add(0);
        dependency.put(3, arr3);

        ArrayList<Integer> arr4 = new ArrayList<Integer>();
        arr4.add(2);
        arr4.add(1);
        arr4.add(0);

        dependency.put(4, arr4);

        ArrayList<Integer> arr5 = new ArrayList<Integer>();
        arr5.add(3);
        arr5.add(1);
        arr5.add(0);

        dependency.put(5, arr5);

//        System.out.println(dependency);
        return dependency;

    }


    public static void main(String[] args) {


//        System.out.println(Integer.parseInt("123a"));
//        System.out.println(initDependency());
//        double m=(int)100;
        int N=65930;
        int[] rule_weight = initParameter.initData(N);//{5, 10, 35, 10, 35, 40};
        int[] rule_cost = initParameter.initCost(N);//{1, 2, 3, 3, 4, 4};
        int switch_volume = 2;

        int sum = 0;
/*
        ArrayList<Integer> rule1 = selectRulesUsingDependentSetMethod(rule_weight, rule_cost, switch_volume);


        for (int i : rule1) {
            System.out.print(i + " ");
            sum += rule_weight[i];
        }

        System.out.println("Dependecy Set : " + sum);

        sum = 0;

        ArrayList<String> rule2 = selectRulesUsingCoverSetMethod(rule_weight, rule_cost, switch_volume);

        for (String i : rule2) {
            System.out.print(i + " ");
            if (!i.contains("*")) {
                sum += rule_weight[Integer.parseInt(i)];
//                System.out.println(sum);

            }
        }

        System.out.println("Cover Set : " + sum);

        ArrayList<String> rule3 = selectRuleUsingMixedSetMethod(rule_weight, rule_cost, switch_volume);
        sum = 0;
        for (String i : rule3) {
            System.out.print(i + " ");
            if (!i.contains("*")) {
                sum += rule_weight[Integer.parseInt(i)];
            }
        }

        System.out.println("Mixed Set : " + sum);

*/
//        System.out.println(selectRuleUsingMyMethod(rule_weight, rule_cost, switch_volume));

        ArrayList<ArrayList<String>> rule4 = selectRuleUsingMyMethod(rule_weight, rule_cost, switch_volume);

//        System.out.println("rule4: "+rule4);
        int max = 0;
        ArrayList<String> tp = new ArrayList<String>();
        ArrayList<String> tp1 = new ArrayList<String>();

        HashSet<String> set=new HashSet<String>();
        for (ArrayList<String> rulelist : rule4) {
            sum = 0;
            tp1.clear();
//            System.out.println(tp+" "+max);
            for (String str : rulelist) {
                tp1.add(str);
//                set.add(str);
                if (!str.contains("*")) {
                    sum += rule_weight[Integer.parseInt(str)];
                }
            }
            if (sum > max) {
                max = sum;
                tp = (ArrayList<String>) tp1.clone();
//                System.out.println(tp+" "+max+"000");
            }
        }

        System.out.println(tp+" "+max);
        for (String s : tp) {
            System.out.print(s + " ");
            set.add(s);
        }
        System.out.println("My Set :" + max);







        int sum2 = 0;
        for (String i : set) {
//            System.out.print(i + " ");
            set.add(i);
            if (!i.contains("*")) {
                sum2 += rule_weight[Integer.parseInt(i)];

            }
        }
        System.out.println(set);
        System.out.println("set weight:"+sum2+" "+set.size());

    }
}
