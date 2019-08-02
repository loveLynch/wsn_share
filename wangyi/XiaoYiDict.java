package wangyi;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

/**
 * @author Lynch
 * @date 2019/8/2 17:48
 * 小易的字典
 * 时间限制：1秒
 * <p>
 * 空间限制：262144K
 * <p>
 * 小易在学校中学习了关于字符串的理论, 于是他基于此完成了一个字典的项目。
 * <p>
 * 小易的这个字典很奇特, 字典内的每个单词都包含n个'a'和m个'z', 并且所有单词按照字典序排列。
 * <p>
 * 小易现在希望你能帮他找出第k个单词是什么。
 * <p>
 * <p>
 * 输入描述:
 * 输入包括一行三个整数n, m, k(1 <= n, m <= 100, 1 <= k <= 109), 以空格分割。
 * <p>
 * <p>
 * 输出描述:
 * 输出第k个字典中的字符串，如果无解，输出-1。
 * <p>
 * 输入例子1:
 * 2 2 6
 * <p>
 * 输出例子1:
 * zzaa
 */
public class XiaoYiDict {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String [] NMK = input.nextLine().split(" ");
        int N = Integer.parseInt(NMK[0]);
        int M = Integer.parseInt(NMK[1]);
        int K = Integer.parseInt(NMK[2]);
        System.out.println(getTheKDict(N, M, K));
    }

    private static String getTheKDict(int n, int m, int k) {
   


        return null;
    }
}
